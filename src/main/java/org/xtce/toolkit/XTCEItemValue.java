/* Copyright 2015 David Overeem (dovereem@cox.net)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.xtce.toolkit;

import org.xtce.math.MilStd1750A;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.omg.space.xtce.AbsoluteTimeDataType;
import org.omg.space.xtce.BooleanDataType;
import org.omg.space.xtce.CalibratorType;
import org.omg.space.xtce.NameDescriptionType;
import org.omg.space.xtce.ValueEnumerationType;
import org.xtce.math.Calibration;
import org.xtce.math.MathOperationCalibration;
import org.xtce.math.PolynomialCalibration;
import org.xtce.math.SplineCalibration;
import org.xtce.toolkit.XTCETypedObject.EngineeringType;
import org.xtce.toolkit.XTCETypedObject.RawType;

/** This class captures the attributes needed to encode or decode a raw value
 * to or from an engineering value and provides public methods to do these
 * tasks from the caller's application.
 *
 * The encode method is for converting an EU value provided to a raw value.
 * The decode method does the opposite, which converts a raw value back to an
 * EU value.  Telemetry typically uses decode on the ground and Telecommanding
 * typically uses encode on the ground, with the reverse of both happening on
 * the satellite side.
 *
 * The implementation of this class might seem a little strange or less than
 * efficient because it is extended by platform specific classes for other use
 * cases that overload the population of the attributes such that it can be
 * used for specific cots products.  Those extended implementations are not
 * included in this toolkit.
 *
 * @author David Overeem
 *
 */

public class XTCEItemValue {

    /** Constructor
     *
     * @param item XTCETypedObject containing the Parameter or Argument
     * that is being encoded/decoded.
     *
     */

    public XTCEItemValue( final XTCETypedObject item ) {

        // first gather the general attributes that are common
        itemName_    = item.getName();
        euTypeName_  = item.getEngineeringType();
        rawTypeName_ = item.getRawType();
        rawBitOrder_ = item.getRawBitOrder();
        itemObj_     = item;

        // gather the Type Reference, for which we cannot proceed further
        // unless exists
        NameDescriptionType typeObj = item.getTypeReference();
        if ( typeObj == null ) {
            warn( itemName_ + " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_notype" ) ); // NOI18N
            validObject_ = false;
            return;
        }

        // gather the raw encoding size, which may not be appropriate
        try {
            rawSizeInBits_ = Integer.parseInt( item.getRawSizeInBits() );
            validObject_   = true;
        } catch ( NumberFormatException ex ) {
            warn( itemName_ + " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_norawsize" ) + // NOI18N
                  " '" + rawSizeInBits_ + "'" ); // NOI18N
            validObject_ = false;
        }

        // get the registered time handler if one exists
        if ( euTypeName_ == EngineeringType.TIME ) {
            try {
                AbsoluteTimeDataType timeXml =
                    (AbsoluteTimeDataType)itemObj_.getTypeReference();
                timeHandler_ =
                    XTCEFunctions.getAbsoluteTimeHandler( timeXml );
            } catch ( XTCEDatabaseException ex ) {
                warn( itemName_ + " " + ex.getLocalizedMessage() ); // NOI18N
                validObject_ = false;
                return;
            }
        }

        // gather the extended attributes that are needed for specific types
        setValidRangeAttributes( item );
        setEnumerationList( item );
        setDefaultCalibrator( item );

    }

    /** Constructor
     *
     * @param item XTCETypedObject containing the Parameter or Argument
     * that is being encoded/decoded.
     *
     * @param calibrator CalibratorType containing the contextually accurate
     * calibrator for this item value evaluation.
     *
     */

    public XTCEItemValue( final XTCETypedObject item,
                          final CalibratorType  calibrator ) {

        this( item );

        defCal_ = calibrator;

    }

    /** Retrieves the item name of the Parameter or Argument that is the basis
     * for this item value encode()/decode() object.
     *
     * @return String containing the name used when this object was
     * constructed.
     *
     */

    public final String getItemName() {
        return itemName_;
    }

    /** Retrieves the validity flag from this item value encode/decode.
     *
     * @return boolean indicating if the encode/decode can be performed.  If
     * this is false, the encode/decode functions will throw an exception.
     *
     */

    public final boolean isValid() {
        return validObject_;
    }

    /** Retrieve the list of warnings that have accumulated since this object
     * was created or since the caller last cleared the warnings list.
     *
     * @see #clearWarnings()
     *
     * @return List of String containing the warning messages.
     *
     */

    public final List<String> getWarnings() {
        if ( warnings_ == null ) {
            return new ArrayList<>();
        }
        return warnings_;
    }

    /** Clears the list of warning messages for this object, which can be
     * useful if multiple raw or engineering values need to be generated, so
     * that the list does not include previous issues.
     *
     */

    public final void clearWarnings() {
        if ( warnings_ != null ) {
            warnings_.clear();
        }
    }

    /** Retrieve the Calibrated/Engineering value of this typed object when
     * given the raw binary value.
     *
     * This method is a shortcut to calling both the getUncalibratedFromRaw()
     * and getCalibratedFromUncalibrated() functions.  Those functions contain
     * additional details for the reader concerning the nature of the data
     * and what is being performed.
     * 
     * The user must interrogate the getWarnings() method to ensure that this
     * function did not encounter any problems during conversion.  In the
     * event that warnings happened, then the return value cannot be used.
     *
     * @param rawValue BitSet containing the raw binary value that would
     * be encoded on the wire or bitfield.  The raw binary is always expected
     * to be in the order read from the stream.
     *
     * @return String containing the proper Calibrated/Engineering
     * representation of the raw encoded value provided by the caller.
     * 
     */

    public String decode( final BitSet rawValue ) {

        String uncalValue = getUncalibratedFromRaw( rawValue );
        if ( warnings_ != null && warnings_.isEmpty() == false ) {
            return "";
        } else {
            return getCalibratedFromUncalibrated( uncalValue );
        }

    }


    /** Retrieve the uncalibrated value of this typed object item when given
     * the raw binary value.
     *
     * The raw value is provided as a Java BitSet to account for an
     * arbitrary size of the raw value binary.  The output of this function
     * takes into account the encoding type to interpret the raw binary in the
     * proper type and alignment.
     *
     * @param rawValue BitSet containing the raw binary value that would
     * be encoded on the wire or bitfield.  The raw binary is always expected
     * to be in the order read from the stream.
     *
     * @return String containing the proper uncalibrated representation of the
     * raw encoded value provided by the caller.
     *
     */

    public String getUncalibratedFromRaw( final BitSet rawValue ) {

        clearWarnings();

        if ( rawBitOrder_.equals( "mostSignificantBitFirst" ) == false ) { // NOI18N
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_rawbitorder" ) + // NOI18N
                  ": '" + // NOI18N
                  rawBitOrder_ +
                  "'" ); // NOI18N
            return ""; // NOI18N
        }

        if ( euTypeName_ == EngineeringType.TIME ) {
            if ( timeHandler_ != null ) {
                return timeHandler_.getUncalibratedFromRaw( rawValue );
            } else {
                return "0x00"; // NOI18N
            }
        }

        BigInteger numericValue = bitSetToNumber( rawValue );

        switch ( rawTypeName_ ) {

            case unsigned:
                isIntegerRawValueReasonable( numericValue );
                return numericValue.toString();

            case signMagnitude: {
                BigInteger halfValue = BigInteger.valueOf( 2 ).pow( rawSizeInBits_ - 1 );
                if ( numericValue.compareTo( halfValue ) >= 0 ) {
                    numericValue = numericValue.subtract( halfValue ).negate();
                }
                isIntegerRawValueReasonable( numericValue );
                return numericValue.toString();
            }

            case twosComplement: {
                BigInteger halfValue = BigInteger.valueOf( 2 ).pow( rawSizeInBits_ - 1 );
                BigInteger fullValue = BigInteger.valueOf( 2 ).pow( rawSizeInBits_ );
                if ( numericValue.compareTo( halfValue ) >= 0 ) {
                    numericValue = numericValue.subtract( fullValue );
                }
                isIntegerRawValueReasonable( numericValue );
                return numericValue.toString();
            }

            case onesComplement: {
                BigInteger halfValue = BigInteger.valueOf( 2 ).pow( rawSizeInBits_ - 1 );
                BigInteger fullValue = BigInteger.valueOf( 2 ).pow( rawSizeInBits_ );
                if ( numericValue.compareTo( halfValue ) >= 0 ) {
                    numericValue = numericValue.subtract( fullValue ).add( BigInteger.ONE );
                }
                isIntegerRawValueReasonable( numericValue );
                return numericValue.toString();
            }

            case IEEE754_1985:
                if ( rawSizeInBits_ == 32 ) {
                    Float floatValue = Float.intBitsToFloat( numericValue.intValue() );
                    isFloatRawValueReasonable( floatValue );
                    return floatValue.toString();
                } else if ( rawSizeInBits_ == 64 ) {
                    Double doubleValue = Double.longBitsToDouble( numericValue.longValue() );
                    isFloatRawValueReasonable( doubleValue );
                    return doubleValue.toString();
                } else if ( rawSizeInBits_ == 128 ) {
                    warn( itemName_ +
                          " " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_rawtypenotsupported" ) + // NOI18N
                          ": " + // NOI18N
                          rawTypeName_ +
                          " (" + // NOI18N
                          Integer.toString( rawSizeInBits_ ) +
                          " " + // NOI18N
                          XTCEFunctions.getText( "general_bits" ) + // NOI18N
                          ")" ); // NOI18N
                } else {
                    warn( itemName_ +
                          " " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_rawenctype" ) + // NOI18N
                          ": " + // NOI18N
                          rawTypeName_ +
                          " (" + // NOI18N
                          Integer.toString( rawSizeInBits_ ) +
                          " " + // NOI18N
                          XTCEFunctions.getText( "general_bits" ) + // NOI18N
                          ")" ); // NOI18N
                }
                break;

            case MILSTD_1750A:
                if ( ( rawSizeInBits_ == 16 ) ||
                     ( rawSizeInBits_ == 32 ) ||
                     ( rawSizeInBits_ == 48 ) ) {
                    MilStd1750A floatValue = new MilStd1750A( numericValue,
                                                              rawSizeInBits_ );
                    isFloatRawValueReasonable( floatValue.toIeeeDouble() );
                    return floatValue.toString();
                } else {
                    warn( itemName_ +
                          " " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_rawenctype" ) + // NOI18N
                          ": " + // NOI18N
                          rawTypeName_ +
                          " (" + // NOI18N
                          Integer.toString( rawSizeInBits_ ) +
                          " " + // NOI18N
                          XTCEFunctions.getText( "general_bits" ) + // NOI18N
                          ")" ); // NOI18N
                }
                break;

            case binary:
                return "0x" + numericValue.toString( 16 ); // NOI18N

            case UTF8:
                return getUncalibratedFromRawString( "UTF-8", numericValue ); // NOI18N

            case UTF16:
                return getUncalibratedFromRawString( "UTF-16", numericValue ); // NOI18N

            default:
                // not supported BCD, packedBCD
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_rawtypenotsupported" ) + // NOI18N
                      ": " + // NOI18N
                      rawTypeName_ );

        }

        return ""; // NOI18N

    }

    /** Retrieve the EU calibrated value of this typed object item when given
     * the uncalibrated value.
     *
     * @param uncalValue String containing the uncalibrated value that is
     * derived from the encoded value on the wire or bitfield.
     *
     * @return String containing the proper EU calibrated representation of the
     * uncalibrated value provided by the caller.
     *
     */

    public String getCalibratedFromUncalibrated( final String uncalValue ) {

        clearWarnings();

        String calValue = applyCalibrator( uncalValue );

        switch ( euTypeName_ ) {

            case UNSIGNED:
            case SIGNED:
                return getCalibratedFromIntegerString( calValue );

            case FLOAT32:
            case FLOAT64:
            case FLOAT128:
                return getCalibratedFromFloatString( calValue );

            case BOOLEAN:
                return getCalibratedFromBooleanNumericString( calValue );

            case ENUMERATED:
                return getCalibratedValueFromEnumeratedNumericString( calValue );

            case STRING:
                return calValue;

            case BINARY:
                // warnings for binary transformations?
                // might need 0x protection over entire function
                if ( calValue.startsWith( "0x" ) == true ) { // NOI18N
                    BigInteger intValue =
                        new BigInteger( calValue.replaceFirst( "0x", "" ), 16 ); // NOI18N
                    return "0x" + intValue.toString( 16 ); // NOI18N
                } else {
                    BigInteger intValue = new BigInteger( calValue );
                    return "0x" + intValue.toString( 16 ); // NOI18N
                }

            case TIME:
                try {
                    if ( timeHandler_ != null ) {
                        return timeHandler_.getCalibratedFromUncalibrated( uncalValue );
                    }
                } catch ( Exception ex ) {
                    warn( itemName_ + " " + ex.getLocalizedMessage() ); // NOI18N
                }
                break;

            default:
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                      ": " + // NOI18N
                      euTypeName_ );
                break;

        }

        return ""; // NOI18N

    }

    /** Retrieve the Raw value of this typed object when given the EU
     * calibrated value.
     *
     * This method is a shortcut to calling both the
     * getUncalibratedFromCalibrated() and getRawFromUncalibrated() functions.
     * Those functions contain additional details for the reader concerning the
     * nature of the data and what is being performed.
     * 
     * The user must interrogate the getWarnings() method to ensure that this
     * function did not encounter any problems during conversion.  In the
     * event that warnings happened, then the return value cannot be used.
     *
     * @param euValue String containing the EU calibrated value that would
     * be encoded on the wire or bitfield.
     *
     * @return String containing the encoded BitSet suitable for encoding a
     * stream with this item value.
     * 
     */

    public BitSet encode( final String euValue ) {

        String uncalValue = getUncalibratedFromCalibrated( euValue );
        return getRawFromUncalibrated( uncalValue );

    }

    /** Retrieve the Raw value of this typed object when given the EU
     * calibrated value.
     *
     * This method is a shortcut to calling both the
     * getUncalibratedFromCalibrated() and getRawFromUncalibrated() functions.
     * Those functions contain additional details for the reader concerning the
     * nature of the data and what is being performed.
     * 
     * The user must interrogate the getWarnings() method to ensure that this
     * function did not encounter any problems during conversion.  In the
     * event that warnings happened, then the return value cannot be used.
     *
     * @param euValue long containing the EU calibrated value that would
     * be encoded on the wire or bitfield.
     *
     * @return String containing the encoded BitSet suitable for encoding a
     * stream with this item value.
     * 
     */

    public BitSet encode( final long euValue ) {

        String uncalValue = getUncalibratedFromCalibrated( euValue );
        return getRawFromUncalibrated( uncalValue );

    }

    /** Retrieve the Raw value of this typed object when given the EU
     * calibrated value.
     *
     * This method is a shortcut to calling both the
     * getUncalibratedFromCalibrated() and getRawFromUncalibrated() functions.
     * Those functions contain additional details for the reader concerning the
     * nature of the data and what is being performed.
     * 
     * The user must interrogate the getWarnings() method to ensure that this
     * function did not encounter any problems during conversion.  In the
     * event that warnings happened, then the return value cannot be used.
     *
     * @param euValue double containing the EU calibrated value that would
     * be encoded on the wire or bitfield.
     *
     * @return String containing the encoded BitSet suitable for encoding a
     * stream with this item value.
     * 
     */

    public BitSet encode( final double euValue ) {

        String uncalValue = getUncalibratedFromCalibrated( euValue );
        return getRawFromUncalibrated( uncalValue );

    }

    /** Retrieve the Raw value of this typed object when given the EU
     * calibrated value.
     *
     * This method is a shortcut to calling both the
     * getUncalibratedFromCalibrated() and getRawFromUncalibrated() functions.
     * Those functions contain additional details for the reader concerning the
     * nature of the data and what is being performed.
     * 
     * The user must interrogate the getWarnings() method to ensure that this
     * function did not encounter any problems during conversion.  In the
     * event that warnings happened, then the return value cannot be used.
     *
     * @param euValue float containing the EU calibrated value that would
     * be encoded on the wire or bitfield.
     *
     * @return String containing the encoded BitSet suitable for encoding a
     * stream with this item value.
     * 
     */

    public BitSet encode( final float euValue ) {

        String uncalValue = getUncalibratedFromCalibrated( euValue );
        return getRawFromUncalibrated( uncalValue );

    }

    /** Retrieve the raw binary bits for encoding of this typed object when
     * given the uncalibrated value.
     *
     * @param uncalValue String containing the uncalibrated representation of
     * the value.
     *
     * @return BitSet containing the raw bits.
     *
     */

    public BitSet getRawFromUncalibrated( final String uncalValue ) {

        // TODO Handle Byte order element ByteOrderList in the encoding

        BigInteger intValue = BigInteger.ZERO;

        try {

            if ( euTypeName_ == EngineeringType.TIME ) {
                if ( timeHandler_ != null ) {
                    return timeHandler_.getRawFromUncalibrated( uncalValue );
                } else {
                    return new BitSet( rawSizeInBits_ );
                }
            } else if ( ( rawTypeName_ == RawType.unsigned       ) ||
                        ( rawTypeName_ == RawType.signMagnitude  ) ||
                        ( rawTypeName_ == RawType.twosComplement ) ||
                        ( rawTypeName_ == RawType.onesComplement ) ) {
                String lowerCalValue = uncalValue.toLowerCase();
                if ( lowerCalValue.startsWith( "0x" ) == true ) { // NOI18N
                    intValue = new BigInteger( lowerCalValue.replaceFirst( "0x", "" ), 16 ); // NOI18N
                    return getRawFromUncalibrated( intValue );
                } else {
                    intValue = new BigDecimal( lowerCalValue ).toBigIntegerExact();
                    return getRawFromUncalibrated( intValue );
                }
            } else if ( rawTypeName_ == RawType.binary ) {
                // need to know EU type here!
                if ( uncalValue.contains( "-" ) == true ) { // NOI18N
                    warn( itemName_ +
                          " " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_binaryinvalid" ) + // NOI18N
                          " '" + // NOI18N
                          uncalValue +
                          "'.  " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_binarynegative" ) ); // NOI18N
                } else {
                    String lowerCalValue = uncalValue.toLowerCase();
                    intValue = integerStringToBigInteger( lowerCalValue );
                }
            } else if ( ( rawTypeName_ == RawType.IEEE754_1985 ) ||
                        ( rawTypeName_ == RawType.MILSTD_1750A ) ) {
                String reasCalValue = uncalValue.toLowerCase();
                if ( reasCalValue.startsWith( "0x" ) == true ) { // NOI18N
                    BigInteger temp = new BigInteger( reasCalValue.replaceFirst( "0x", "" ), 16 ); // NOI18N
                    return getRawFromUncalibrated( new BigDecimal( temp ) );
                } else {
                    return getRawFromUncalibrated( new BigDecimal( reasCalValue ) );
                }
            } else if ( rawTypeName_ == RawType.UTF8 ) {
                BigInteger retValue = BigInteger.ZERO;
                if ( uncalValue.isEmpty() == false ) {
                    retValue = new BigInteger( uncalValue.getBytes( StandardCharsets.UTF_8 ) );
                }
                intValue = encodeUtfString( retValue );
            } else if ( rawTypeName_ == RawType.UTF16 ) {
                BigInteger retValue = BigInteger.ZERO;
                if ( uncalValue.isEmpty() == false ) {
                    retValue = new BigInteger( uncalValue.getBytes( StandardCharsets.UTF_16 ) );
                }
                if ( retValue.equals( BigInteger.ZERO ) == true ) {
                    retValue = new BigInteger( utf16_ );
                }
                intValue = encodeUtfString( retValue );
            } else if ( euTypeName_ == EngineeringType.DURATION ) {
                // TODO Add RelativeTime (DURATION) Type
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                      ": " + // NOI18N
                      euTypeName_ );
            } else if ( euTypeName_ == EngineeringType.STRUCTURE ) {
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_aggregate" ) ); // NOI18N
            } else if ( euTypeName_ == EngineeringType.ARRAY ) {
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_array" ) ); // NOI18N
            } else {
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                      ": " + // NOI18N
                      euTypeName_ );
            }

        } catch ( NumberFormatException ex ) {
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText ( "error_encdec_stringinvalid" ) + // NOI18N
                  " " + // NOI18N
                  rawTypeName_ +
                  ": '" + // NOI18N
                  uncalValue +
                  "'" ); // NOI18N
        } catch ( ArithmeticException ex ) {
            warn( itemName_ +
                  " " +  // NOI18N
                  XTCEFunctions.getText ( "error_encdec_decimalinvalid" ) + // NOI18N
                  " " + // NOI18N
                  rawTypeName_ +
                  ": '" + // NOI18N
                  uncalValue +
                  "'" ); // NOI18N
        }

        return makeBitSetFromBigInteger( intValue );

    }

    /** Retrieve the raw binary bits for encoding of this typed object when
     * given the uncalibrated value.
     *
     * @param uncalValue BigInteger containing the uncalibrated representation
     * of the value.
     *
     * @return BitSet containing the raw bits.
     *
     */

    public BitSet getRawFromUncalibrated( BigInteger uncalValue ) {

        // TODO Handle Byte order element ByteOrderList in the encoding

        if ( euTypeName_ == EngineeringType.TIME ) {
            if ( timeHandler_ != null ) {
                return timeHandler_.getRawFromUncalibrated( uncalValue.toString() );
            } else {
                return new BitSet( rawSizeInBits_ );
            }
        }

        if ( rawTypeName_ == RawType.unsigned ) {
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ ); // NOI18N
            if ( uncalValue.compareTo( max ) == 1 ) {
                warn( itemName_ + " overflow value '" + uncalValue +
                      "', larger than available encoding bits." );
                uncalValue = BigInteger.ZERO;
            } else if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                uncalValue = BigInteger.ZERO;
            }
        } else if ( rawTypeName_ == RawType.signMagnitude ) {
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ - 1 ); // NOI18N
            BigInteger min = max.negate();
            if ( uncalValue.compareTo( max ) == 1 ) {
                warn( itemName_ + " overflow value '" + uncalValue +
                      "', larger than maximum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( uncalValue.compareTo (min ) == -1 ) {
                warn( itemName_ + " overflow value '" + uncalValue +
                      "', smaller than minimum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                uncalValue = BigInteger.ZERO;
            }
            if ( uncalValue.compareTo( BigInteger.ZERO ) < 0 ) {
                uncalValue = uncalValue.negate();
                uncalValue = uncalValue.setBit( rawSizeInBits_ - 1 );
            }
        } else if ( rawTypeName_ == RawType.twosComplement ) {
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ - 1 ); // NOI18N
            BigInteger min = max.negate();
            if ( uncalValue.compareTo( max ) == 1 ) {
                warn( itemName_ + " overflow value '" + uncalValue +
                      "', larger than maximum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( uncalValue.compareTo (min ) == -1 ) {
                warn( itemName_ + " overflow value '" + uncalValue +
                      "', smaller than minimum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                uncalValue = BigInteger.ZERO;
            }
        } else if ( rawTypeName_ == RawType.onesComplement ) {
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ - 1 ); // NOI18N
            BigInteger min = max.negate();
            if ( uncalValue.compareTo( max ) == 1 ) {
                warn( itemName_ + " overflow value '" + uncalValue +
                      "', larger than maximum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( uncalValue.compareTo (min ) == -1 ) {
                warn( itemName_ + " overflow value '" + uncalValue +
                      "', smaller than minimum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                uncalValue = BigInteger.ZERO;
            }
            if ( uncalValue.compareTo( BigInteger.ZERO ) < 0 ) {
                uncalValue = uncalValue.subtract( BigInteger.ONE );
            }
        } else if ( rawTypeName_ == RawType.binary ) {
            // do nothing
        } else if ( rawTypeName_ == RawType.IEEE754_1985 ) {
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                uncalValue = BigInteger.ZERO;
            } else {
                if ( rawSizeInBits_ == 32 ) {
                    uncalValue = BigInteger.valueOf( Float.floatToRawIntBits( uncalValue.floatValue() ) );
                } else if ( rawSizeInBits_ == 64 ) {
                    uncalValue = BigInteger.valueOf( Double.doubleToRawLongBits( uncalValue.doubleValue() ) );
                } else if ( rawSizeInBits_ == 128 ) {
                    warn( itemName_ +
                          " " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_rawtypenotsupported" ) + // NOI18N
                          ": " + // NOI18N
                          rawTypeName_ +
                          " (" + // NOI18N
                          Integer.toString( rawSizeInBits_ ) +
                          " " + // NOI18N
                          XTCEFunctions.getText( "general_bits" ) + // NOI18N
                          ")" ); // NOI18N
                } else {
                    warn( itemName_ +
                          " " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_rawenctype" ) + // NOI18N
                          ": " + // NOI18N
                          rawTypeName_ +
                          " (" + // NOI18N
                          Integer.toString( rawSizeInBits_ ) +
                          " " + // NOI18N
                          XTCEFunctions.getText( "general_bits" ) + // NOI18N
                          ")" ); // NOI18N
                }
            }
        } else if ( rawTypeName_ == RawType.MILSTD_1750A ) {
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                uncalValue = BigInteger.ZERO;
            } else {
                if ( ( rawSizeInBits_ == 16 ) ||
                     ( rawSizeInBits_ == 32 ) ||
                     ( rawSizeInBits_ == 48 ) ) {
                    MilStd1750A milValue = new MilStd1750A( uncalValue.doubleValue(),
                                                            rawSizeInBits_ );
                    uncalValue = BigInteger.valueOf( milValue.toRawBits() );
                } else {
                    warn( itemName_ +
                          " " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_rawenctype" ) + // NOI18N
                          ": " + // NOI18N
                          rawTypeName_ +
                          " (" + // NOI18N
                          Integer.toString( rawSizeInBits_ ) +
                          " " + // NOI18N
                          XTCEFunctions.getText( "general_bits" ) + // NOI18N
                          ")" ); // NOI18N
                }
            }
        } else if ( rawTypeName_ == RawType.UTF8 ) {
            String chars = uncalValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_8 ) );
            uncalValue = encodeUtfString( retValue );
        } else if ( rawTypeName_ == RawType.UTF16 ) {
            String chars = uncalValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_16 ) );
            if ( retValue.equals( BigInteger.ZERO ) == true ) {
                retValue = new BigInteger( utf16_ );
            }
            uncalValue = encodeUtfString( retValue );
        } else if ( euTypeName_ == EngineeringType.TIME ) {
            // TODO Add AbsoluteTime (TIME) Type
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                  ": " + // NOI18N
                  euTypeName_ );
            uncalValue = BigInteger.ZERO;
        } else if ( euTypeName_ == EngineeringType.DURATION ) {
            // TODO Add RelativeTime (DURATION) Type
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                  ": " + // NOI18N
                  euTypeName_ );
            uncalValue = BigInteger.ZERO;
        } else if ( euTypeName_ == EngineeringType.STRUCTURE ) {
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_aggregate" ) ); // NOI18N
            uncalValue = BigInteger.ZERO;
        } else if ( euTypeName_ == EngineeringType.ARRAY ) {
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_array" ) ); // NOI18N
            uncalValue = BigInteger.ZERO;
        } else {
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                  ": " + // NOI18N
                  euTypeName_ );
            uncalValue = BigInteger.ZERO;
        }

        return makeBitSetFromBigInteger( uncalValue );

    }

    /** Retrieve the raw binary bits for encoding of this typed object when
     * given the uncalibrated value.
     *
     * @param uncalValue long containing the uncalibrated representation of
     * the value.
     *
     * @return BitSet containing the raw bits.
     *
     */

    public BitSet getRawFromUncalibrated( final long uncalValue ) {

        BigInteger intValue = BigInteger.valueOf( uncalValue );
        return getRawFromUncalibrated( intValue );

    }

    /** Retrieve the raw binary bits for encoding of this typed object when
     * given the uncalibrated value.
     *
     * @param uncalValue BigDecimal containing the uncalibrated representation
     * of the value.
     *
     * @return BitSet containing the raw bits.
     *
     */

    public BitSet getRawFromUncalibrated( final BigDecimal uncalValue ) {

        // TODO Handle Byte order element ByteOrderList in the encoding

        BigInteger intValue = BigInteger.ZERO;

        if ( ( rawTypeName_ == RawType.unsigned       ) ||
             ( rawTypeName_ == RawType.signMagnitude  ) ||
             ( rawTypeName_ == RawType.twosComplement ) ||
             ( rawTypeName_ == RawType.onesComplement ) )   {
            try {
                return getRawFromUncalibrated( uncalValue.toBigIntegerExact() );
            } catch ( NumberFormatException ex ) {
                warn( itemName_ +
                      " " +  // NOI18N
                      XTCEFunctions.getText ( "error_encdec_decimalinvalid" ) + // NOI18N
                      " " + // NOI18N
                      rawTypeName_ +
                      ": '" + // NOI18N
                      uncalValue +
                      "'" ); // NOI18N
            }
        } else if ( rawTypeName_ == RawType.binary ) {
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_rawtypenotsupported" ) + // NOI18N
                  ": " + // NOI18N
                  rawTypeName_ +
                  " (" + // NOI18N
                  Integer.toString( rawSizeInBits_ ) +
                  " " + // NOI18N
                  XTCEFunctions.getText( "general_bits" ) + // NOI18N
                  ")" ); // NOI18N
        } else if ( rawTypeName_ == RawType.IEEE754_1985 ) {
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                intValue = BigInteger.ZERO;
            } else {
                if ( rawSizeInBits_ == 32 ) {
                    intValue = BigInteger.valueOf( Float.floatToRawIntBits( uncalValue.floatValue() ) );
                } else if ( rawSizeInBits_ == 64 ) {
                    intValue = BigInteger.valueOf( Double.doubleToRawLongBits( uncalValue.doubleValue() ) );
                } else if ( rawSizeInBits_ == 128 ) {
                    warn( itemName_ +
                          " " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_rawtypenotsupported" ) + // NOI18N
                          ": " + // NOI18N
                          rawTypeName_ +
                          " (" + // NOI18N
                          Integer.toString( rawSizeInBits_ ) +
                          " " + // NOI18N
                          XTCEFunctions.getText( "general_bits" ) + // NOI18N
                          ")" ); // NOI18N
                } else {
                    warn( itemName_ +
                          " " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_rawenctype" ) + // NOI18N
                          ": " + // NOI18N
                          rawTypeName_ +
                          " (" + // NOI18N
                          Integer.toString( rawSizeInBits_ ) +
                          " " + // NOI18N
                          XTCEFunctions.getText( "general_bits" ) + // NOI18N
                          ")" ); // NOI18N
                }
            }
        } else if ( rawTypeName_ == RawType.MILSTD_1750A ) {
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                intValue = BigInteger.ZERO;
            } else {
                if ( ( rawSizeInBits_ == 16 ) ||
                     ( rawSizeInBits_ == 32 ) ||
                     ( rawSizeInBits_ == 48 ) ) {
                    MilStd1750A milValue = new MilStd1750A( uncalValue.doubleValue(),
                                                            rawSizeInBits_ );
                    intValue = BigInteger.valueOf( milValue.toRawBits() );
                } else {
                    warn( itemName_ +
                          " " + // NOI18N
                          XTCEFunctions.getText( "error_encdec_rawenctype" ) + // NOI18N
                          ": " + // NOI18N
                          rawTypeName_ +
                          " (" + // NOI18N
                          Integer.toString( rawSizeInBits_ ) +
                          " " + // NOI18N
                          XTCEFunctions.getText( "general_bits" ) + // NOI18N
                          ")" ); // NOI18N
                }
            }
        } else if ( rawTypeName_ == RawType.UTF8 ) {
            String chars = uncalValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_8 ) );
            intValue = encodeUtfString( retValue );
        } else if ( rawTypeName_ == RawType.UTF16 ) {
            String chars = uncalValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_16 ) );
            if ( retValue.equals( BigInteger.ZERO ) == true ) {
                retValue = new BigInteger( utf16_ );
            }
            intValue = encodeUtfString( retValue );
        } else if ( euTypeName_ == EngineeringType.TIME ) {
            // TODO Add AbsoluteTime (TIME) Type
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                  ": " + // NOI18N
                  euTypeName_ );
        } else if ( euTypeName_ == EngineeringType.DURATION ) {
            // TODO Add RelativeTime (DURATION) Type
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                  ": " + // NOI18N
                  euTypeName_ );
        } else if ( euTypeName_ == EngineeringType.STRUCTURE ) {
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_aggregate" ) ); // NOI18N
        } else if ( euTypeName_ == EngineeringType.ARRAY ) {
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_array" ) ); // NOI18N
        } else {
            warn( itemName_ +
                  " " + // NOI18N
                  XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                  ": " + // NOI18N
                  euTypeName_ );
        }

        return makeBitSetFromBigInteger( intValue );

    }

    /** Retrieve the raw binary bits for encoding of this typed object when
     * given the uncalibrated value.
     *
     * @param uncalValue double containing the uncalibrated representation of
     * the value.
     *
     * @return BitSet containing the raw bits.
     *
     */

    public BitSet getRawFromUncalibrated( final double uncalValue ) {

        BigDecimal decimalValue = new BigDecimal( uncalValue );
        return getRawFromUncalibrated( decimalValue );

    }

    /** Retrieve the raw binary bits for encoding of this typed object when
     * given the uncalibrated value.
     *
     * @param uncalValue float containing the uncalibrated representation of
     * the value.
     *
     * @return BitSet containing the raw bits.
     *
     */

    public BitSet getRawFromUncalibrated( final float uncalValue ) {

        BigDecimal decimalValue = new BigDecimal( uncalValue );
        return getRawFromUncalibrated( decimalValue );

    }

    /** Retrieve the uncalibrated value of an EU calibrated value for this
     * typed object.
     *
     * @param euValue String containing a value of this item represented in
     * EU/calibrated form.
     *
     * @return String containing the uncalibrated value.
     *
     */

    public String getUncalibratedFromCalibrated( final String euValue ) {

        switch ( euTypeName_ ) {

            case BOOLEAN: {
                BigInteger integerValue = integerFromBooleanType( euValue );
                isIntegerRawValueReasonable( integerValue );
                return uncalibrateIntegerType( integerValue );
            }

            case ENUMERATED: {
                BigInteger integerValue = integerFromEnumerationType( euValue );
                return uncalibrateIntegerType( integerValue );
            }

            case STRING:
                return euValue;

            case BINARY:
                return "0x" + // NOI18N
                       integerStringToBigInteger( euValue ).toString( 16 );

            case FLOAT32:
            case FLOAT64:
            case FLOAT128:
                try {
                    if ( euValue.startsWith( "0x" ) == true ) { // NOI18N
                        BigInteger intValue = new BigInteger( euValue.replaceFirst( "0x", "" ), 16 ); // NOI18N
                        BigDecimal calValue = new BigDecimal( intValue );
                        //isFloatRawValueReasonable( calValue.doubleValue() );
                        return uncalibrateFloatType( calValue );
                    } else if ( euValue.startsWith( "0X" ) == true ) { // NOI18N
                        BigInteger intValue = new BigInteger( euValue.replaceFirst( "0X", "" ), 16 ); // NOI18N
                        BigDecimal calValue = new BigDecimal( intValue );
                        //isFloatRawValueReasonable( calValue.doubleValue() );
                        return uncalibrateFloatType( calValue );
                    }
                    BigDecimal calValue = new BigDecimal( euValue );
                    //isFloatRawValueReasonable( calValue.doubleValue() );
                    return uncalibrateFloatType( calValue );
                } catch ( NumberFormatException ex ) {
                    warn( itemName_ +
                          " Invalid String value for uncalibrate " +
                          rawTypeName_ +
                          " of '" +
                          euValue +
                          "'" );
                }
                break;

            case UNSIGNED:
            case SIGNED:
                try {
                    if ( euValue.startsWith( "0x" ) == true ) { // NOI18N
                        return uncalibrateIntegerType( new BigInteger( euValue.replaceFirst( "0x", "" ), 16 ) ); // NOI18N
                    } else if ( euValue.startsWith( "0X" ) == true ) { // NOI18N
                        return uncalibrateIntegerType( new BigInteger( euValue.replaceFirst( "0X", "" ), 16 ) ); // NOI18N
                    }
                    BigDecimal testValue = new BigDecimal( euValue );
                    if ( ( testValue.doubleValue() % 1 ) != 0 ) {
                        warn( itemName_ +
                              " Invalid Integer value for uncalibrate of '" +
                              euValue + "'" );
                    } else {
                        BigInteger intValue = testValue.toBigInteger();
                        return uncalibrateIntegerType( intValue );
                    }
                } catch ( NumberFormatException ex ) {
                    warn( itemName_ +
                          " Invalid Integer value for uncalibrate of '" +
                          euValue +
                          "'" );
                }
                break;

            case TIME:
                try {
                    if ( timeHandler_ != null ) {
                        return timeHandler_.getUncalibratedFromCalibrated( euValue );
                    }
                } catch ( Exception ex ) {
                    warn( itemName_ + " " + ex.getLocalizedMessage() ); // NOI18N
                }
                break;

            case DURATION:
                // TODO Add RelativeTime (DURATION) Type
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                      ": " + // NOI18N
                      euTypeName_ );
                break;

            case STRUCTURE:
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_aggregate" ) ); // NOI18N
                break;

            case ARRAY:
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_array" ) ); // NOI18N
                break;

            default:
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_engtypenotsupported" ) + // NOI18N
                      ": " + // NOI18N
                      euTypeName_ );
                break;

        }

        return "";

    }

    /** Retrieve the uncalibrated value of an EU calibrated value for this
     * typed object.
     *
     * @param euValue BigInteger containing a value of this item represented in
     * EU/calibrated form.
     *
     * @return String containing the uncalibrated value.
     *
     */

    public String getUncalibratedFromCalibrated( final BigInteger euValue ) {

        return getUncalibratedFromCalibrated( euValue.toString() );

    }

    /** Retrieve the uncalibrated value of an EU calibrated value for this
     * typed object.
     *
     * @param euValue long containing a value of this item represented in
     * EU/calibrated form.
     *
     * @return String containing the uncalibrated value.
     *
     */

    public String getUncalibratedFromCalibrated( final long euValue ) {

        BigInteger intValue = BigInteger.valueOf( euValue );
        return getUncalibratedFromCalibrated( intValue );

    }

    /** Retrieve the uncalibrated value of an EU calibrated value for this
     * typed object.
     *
     * @param euValue BigDecimal containing a value of this item represented in
     * EU/calibrated form.
     *
     * @return String containing the uncalibrated value.
     *
     */

    public String getUncalibratedFromCalibrated( final BigDecimal euValue ) {

        return getUncalibratedFromCalibrated( euValue.doubleValue() );

    }

    /** Retrieve the uncalibrated value of an EU calibrated value for this
     * typed object.
     *
     * @param euValue double containing a value of this item represented in
     * EU/calibrated form.
     *
     * @return String containing the uncalibrated value.
     *
     */

    public String getUncalibratedFromCalibrated( final double euValue ) {

        return getUncalibratedFromCalibrated( Double.toString( euValue ) );

    }

    /** Retrieve the uncalibrated value of an EU calibrated value for this
     * typed object.
     *
     * @param euValue float containing a value of this item represented in
     * EU/calibrated form.
     *
     * @return String containing the uncalibrated value.
     *
     */

    public String getUncalibratedFromCalibrated( final float euValue ) {

        return getUncalibratedFromCalibrated( Float.toString( euValue ) );

    }

    /** Function to resolve a Boolean Engineering Type, which has a flexible
     * set of EU values, into the numeric calibrated value.
     *
     * This function could have been implemented by creating a bogus set of
     * enumerations, where just 0 and 1 are implememnted, using the attributes
     * of @zeroStringValue and @oneStringValue, but it was just as simple to
     * create this and it seemed like less overhead.  A warning message is
     * added to the warnings list in the event that the engineering value does
     * not match and the return value will be BigInteger.ZERO.
     *
     * @param euValue String containing the user provided engineering value for
     * this Boolean type Parameter/Argument.
     *
     * @return BigInteger containing either ZERO or ONE, which are constants
     * for the BigInteger class.
     *
     */

    private BigInteger integerFromBooleanType( final String euValue ) {

        if ( euValue.equals( booleanZeroString_ ) == true ) {
            return BigInteger.ZERO;
        } else if ( euValue.equals( booleanOneString_ ) == true ) {
            return BigInteger.ONE;
        } else {
            warn( itemName_ +
                  " Invalid EU Boolean value of '" + euValue + "'" );
        }

        return BigInteger.ZERO;

    }

    /** Function to resolve an Enumerated Engineering Type into the numeric
     * calibrated value.
     *
     * This function searches the labels of the EnumerationList in the XTCE
     * data model to find a match.  It takes into account that some
     * enumerations have ranges, and if there is an overlap, it will end up
     * returning the first match.  Since this function supports the encode()
     * method, enumeration labels that have ranges cannot be exactly
     * determined, so the raw value returned will always be the first valid
     * value for the label, if it is in a range.  A warning will be posted to
     * the warnings list, but it should be considered harmless in this case.
     *
     * @param euValue String containing the enumeration label provided by the
     * user that wishes to encode() this Parameter/Argument into a raw value.
     *
     * @return BigInteger containing the raw value associated with the provided
     * enumeration label, or BigInteger.ZERO if the label was not located.  A
     * warning will be posted to the warnings list if this occurs.
     *
     */

    private BigInteger integerFromEnumerationType( final String euValue ) {

        for ( ValueEnumerationType enumItem : enums_ ) {

            if ( enumItem.getLabel().equals( euValue ) == false ) {
                continue;
            }

            if ( enumItem.getMaxValue() != null ) {
                warn( "Enumeration label '" +
                      enumItem.getLabel() +
                      "' for " +
                      itemName_ +
                      " has values '" +
                      enumItem.getValue().toString() +
                      "' through '" +
                      enumItem.getMaxValue().toString() +
                      "', using lowest possible value" );
            }

            return enumItem.getValue();

        }

        warn( itemName_ +
              " Invalid EU Enumeration value of '" + euValue + "'" );

        return BigInteger.ZERO;

    }

    private String uncalibrateIntegerType( final BigInteger calValue ) {

        switch ( rawTypeName_ ) {

            case unsigned:
            case signMagnitude:
            case twosComplement:
            case onesComplement: {
                BigDecimal decValue   = floatEncodingUncalibrate( calValue );
                BigInteger uncalValue = decValue.toBigInteger();
                if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                    return "0"; // NOI18N
                }
                return uncalValue.toString();
            }

            case binary:
                return calValue.toString();

            case IEEE754_1985:
            case MILSTD_1750A: {
                BigDecimal uncalValue =
                    floatEncodingUncalibrate( new BigDecimal( calValue ) );
                if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                    return "0.0"; // NOI18N
                }
                return Double.toString( uncalValue.doubleValue() );
            }

            case UTF8:
            case UTF16:
                return calValue.toString();

            default:
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_rawtypenotsupported" ) + // NOI18N
                      ": " + // NOI18N
                      rawTypeName_ );

        }

        return "0"; // NOI18N

    }

    private String uncalibrateFloatType( final BigDecimal calValue ) {

        switch ( rawTypeName_ ) {

            case unsigned:
            case signMagnitude:
            case twosComplement:
            case onesComplement: {
                BigDecimal decValue   = floatEncodingUncalibrate( calValue );
                BigInteger uncalValue = decValue.toBigInteger();
                if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                    return "0"; // NOI18N
                }
                return uncalValue.toString();
            }

            case binary:
                return calValue.toString();

            case IEEE754_1985:
            case MILSTD_1750A: {
                BigDecimal uncalValue =
                    floatEncodingUncalibrate( calValue );
                if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                    return "0.0"; // NOI18N
                }
                return Double.toString( uncalValue.doubleValue() );
            }

            case UTF8:
            case UTF16:
                return calValue.toString();

            default:
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_rawtypenotsupported" ) + // NOI18N
                      ": " + // NOI18N
                      rawTypeName_ );

        }

        return "0";

    }

    private String getUncalibratedFromRawString( final String     encoding,
                                                 final BigInteger uncalValue ) {

        // we need to accomodate the TerminationChar and the LeadingSize here

        String retValue = new String( uncalValue.toByteArray(),
                                      Charset.forName( encoding ) );
        if ( retValue.length() == 0 ) {
            return ""; // NOI18N
        }
        int endIndex = retValue.length() - 1;
        while ( retValue.charAt( endIndex ) == '\0' ) { // NOI18N
            if ( endIndex == 0 ) {
                return ""; // NOI18N
            } else {
                retValue = retValue.substring( 0, endIndex );
            }
            endIndex = retValue.length() - 1;
        }
        return retValue;

    }

    private String getCalibratedFromIntegerString( final String calValue ) {

        try {

            // the idea here is to allow for a tolerance when doing the
            // conversion of string to integer taking into account that float
            // calculations can have noise.  if the decimal is substantive,
            // then an exception is thrown from toBigIntegerExact().

            BigInteger largerValue =
                new BigDecimal( calValue ).movePointRight( 6 ).toBigInteger();

            BigDecimal smallerValue =
                new BigDecimal( largerValue.divide( new BigInteger( "1000000" ) ) ); // NOI18N

            BigInteger retValue = smallerValue.toBigIntegerExact();

            if ( validRange_.isValidRangeApplied() == true ) {
                checkValidRangeCalibrated( new BigDecimal( retValue ), calValue );
            }

            return retValue.toString();

        } catch ( Exception ex ) {

            warn( itemName_ + " Calibrated value '" +
                  calValue + "' is not representative of an integer" );

        }

        return "";

    }

    private String getCalibratedFromFloatString( final String calValue ) {

        try {

            BigDecimal retValue = new BigDecimal( calValue );

            if ( validRange_.isValidRangeApplied() == true ) {
                checkValidRangeCalibrated( retValue, calValue );
            }

            return retValue.toString();

        } catch ( NumberFormatException ex ) {
            warn( itemName_ + " Calibrated value '" +
                  calValue + "' is not representative of a floating " +
                  "point number" );
        }

        return "";

    }

    private String getCalibratedFromBooleanNumericString( final String uncalValue ) {

        try {

            BigInteger intValue = new BigInteger( uncalValue );

            if ( intValue.compareTo( BigInteger.ZERO ) == 0 ) {
                return booleanZeroString_;
            } else if ( intValue.compareTo( BigInteger.ONE ) == 0 ) {
                return booleanOneString_;
            }

            warn( itemName_ + " Boolean undefined for " +
                  " uncalibrated value '" + uncalValue + "'" );

        } catch ( NumberFormatException ex ) {
            warn( itemName_ + " uncalibrated value '" +
                  uncalValue + "' is not an integer number needed for an " +
                  "boolean type label" );
        }

        return "";

    }

    private String getCalibratedValueFromEnumeratedNumericString( final String uncalValue ) {

        try {

            BigInteger intValue = new BigInteger( uncalValue );

            for ( ValueEnumerationType entry : enums_ ) {
                if ( entry.getValue().compareTo( intValue ) == 0 ) {
                    return entry.getLabel();
                } else if ( entry.getMaxValue() != null ) {
                    if ( ( entry.getValue().compareTo( intValue )    <= 0 ) &&
                         ( entry.getMaxValue().compareTo( intValue ) >= 0 ) ) {
                        return entry.getLabel();
                    }
                }
            }

            warn( itemName_ + " Enumeration undefined for " +
                  "uncalibrated value '" + uncalValue + "'" );

        } catch ( NumberFormatException ex ) {
            warn( itemName_ + " uncalibrated value '" +
                  uncalValue + "' is not an integer number needed for an " +
                  "enumerated type label" );
        }

        return "";

    }

    private void checkValidRangeCalibrated( BigDecimal retValue,
                                            String     calValue ) {

        try {

            if ( validRange_.isHighValueCalibrated() == true ) {
                BigDecimal high = new BigDecimal( validRange_.getHighValue() );
                if ( validRange_.isHighValueInclusive() == true ) {
                    if ( high.compareTo( retValue ) == -1 ) {
                        warn( itemName_ + " Calibrated value '" +
                              calValue + "' is too large for valid " +
                              "inclusive max value of '" +
                              validRange_.getHighValue() + "'" );
                    }
                } else {
                    if ( high.compareTo( retValue ) < 1 ) {
                        warn( itemName_ + " Calibrated value '" +
                              calValue + "' is too large for valid " +
                              "exclusive max value of '" +
                              validRange_.getHighValue() + "'" );
                    }
                }
            }

            if ( validRange_.isLowValueCalibrated() == true ) {
                BigDecimal low = new BigDecimal( validRange_.getLowValue() );
                if ( validRange_.isLowValueInclusive() == true ) {
                    if ( low.compareTo( retValue ) == 1 ) {
                        warn( itemName_ + " Calibrated value '" +
                              calValue + "' is too small for valid " +
                              "inclusive min value of '" +
                              validRange_.getLowValue() + "'" );
                    }
                } else {
                    if ( low.compareTo( retValue ) > -1 ) {
                        warn( itemName_ + " Calibrated value '" +
                              calValue + "' is too small for valid " +
                              "exclusive min value of '" +
                              validRange_.getLowValue() + "'" );
                    }
                }
            }

        } catch ( NumberFormatException ex ) {
            warn( itemName_ + " Valid Range contains value that " +
                  "is not representative of the numeric engineering type " +
                  euTypeName_ );
        }

    }

    /** Private method to calculate and apply the calibrator to the
     * uncalibrated value.
     *
     * This method does not support MathOperationCalibrator elements and will
     * record a warning.  MathOperationCalibrators are quietly ignored.
     *
     * @param uncalValue String containing the uncalibrated value derived from
     * the raw binary value in the stream.
     *
     * @return String containing the value as calibrated, or a quick return of
     * the original value if no calibrator exists to act on.
     *
     */

    private String applyCalibrator( final String uncalValue ) {

        if ( defCal_ == null ) {
            return uncalValue;
        }

        try {

            Calibration calObj;

            if ( defCal_.getPolynomialCalibrator() != null ) {
                calObj = new PolynomialCalibration( defCal_.getPolynomialCalibrator(), validRange_, rawSizeInBits_, rawTypeName_ );
            } else if ( defCal_.getSplineCalibrator() != null ) {
                calObj = new SplineCalibration( defCal_.getSplineCalibrator() );
            } else if ( defCal_.getMathOperationCalibrator() != null ) {
                calObj = new MathOperationCalibration( defCal_.getMathOperationCalibrator() );
            } else {
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_unknown_calibrator" ) ); // NOI18N
                return uncalValue;
            }

            return calObj.calibrate( Double.valueOf( uncalValue ) ).toString();

        } catch ( XTCEDatabaseException ex ) {

            warn( itemName_ + " " + ex.getLocalizedMessage() ); // NOI18N

        }

        return uncalValue;

    }

    private BigInteger encodeUtfString( BigInteger retValue ) {

        long bitLength = retValue.toByteArray().length * 8;
        if ( bitLength > rawSizeInBits_ ) {
            warn( "String length for encoding " +
                  rawTypeName_ +
                  " item " +
                  itemName_ +
                  " is " +
                  Long.toString( bitLength ) +
                  " which exceeds fixed length size of " +
                  Long.toString( rawSizeInBits_) );
        }
        if ( retValue.equals( BigInteger.ZERO ) == true ) {
            return retValue;
        }
        while ( bitLength < rawSizeInBits_ ) {
            retValue  = retValue.shiftLeft( 8 );
            bitLength = retValue.toByteArray().length * 8;
            //retValue = retValue.add(  ); for termination char
        }
        return retValue;

    }

    /** Function to create the raw binary bits for populating a container
     * binary based on the encoded value of this named and typed item from the
     * XTCE data model.
     *
     * The caller provides the raw value in the form of a BigInteger
     * and this function walks through the bits of that value, ensuring to
     * use all the bits that are in the raw encoded size.  It sets the BitSet
     * such that bit 0 of the BitSet is the least significant bit and the
     * highest (rawSizeInBits_) is the most significant bit.  If the order is
     * reversed by the encoding attribute @bitOrder, then the reverse happens.
     *
     * @param rawValue BigInteger containing the value to encode into a raw
     * BitSet for inclusion into a container object, either Telemetry or
     * Telecommand.
     *
     * @return BitSet suitable for inclusion into a Telemetry or Telecommand
     * container by simply walking the length and placing the bits into the
     * container.  All ordering has already been handled.
     *
     */

    public BitSet encodeRawBits( BigInteger rawValue ) {

        // TODO Handle Byte order element ByteOrderList in the encoding

        BitSet rawBits = new BitSet( rawSizeInBits_ );

        // this code is not a part of the encoding logic, rather it compensates
        // for the design of Java BigInteger, which internallys stores as a
        // twosComplement.  When creating from hex input, this would not need
        // to be invoked.  A string version of this function would not need to
        // do this, either.  It could operate on a byte[].

        if ( rawValue.compareTo( BigInteger.ZERO ) < 0 ) {
            if ( rawTypeName_ == RawType.signMagnitude ) {
                rawValue = rawValue.negate().setBit( rawSizeInBits_ - 1 );
            } else if ( rawTypeName_ == RawType.onesComplement ) {
                rawValue = rawValue.subtract( BigInteger.ONE );
            }
        }

        if ( rawBitOrder_.equals( "mostSignificantBitFirst" ) == true ) { // NOI18N
            for ( int iii = rawSizeInBits_ - 1; iii >= 0; --iii ) {
                rawBits.set( iii, rawValue.testBit( iii ) );
            }
        } else {
            for ( int iii = rawSizeInBits_ - 1; iii >= 0; --iii ) {
                rawBits.set( rawSizeInBits_ - iii - 1, rawValue.testBit( iii ) );
            }
        }

        return rawBits;

    }

    /** Sets the Valid Range attributes of this object based on a general
     * type specification from the XTCE data model, which all inherit from the
     * NameDescriptionType.
     *
     * @param typeObj NameDescriptionType containing the type information for
     * this named and typed Parameter/Argument.
     *
     */

    private void setValidRangeAttributes( final XTCETypedObject item ) {

        validRange_ = item.getValidRange();

        // these private members are probably not necessary.  might remove

        NameDescriptionType typeObj = item.getTypeReference();

        if ( typeObj instanceof BooleanDataType ) {
            booleanZeroString_ = ((BooleanDataType)typeObj).getZeroStringValue();
            booleanOneString_  = ((BooleanDataType)typeObj).getOneStringValue();
        }

    }

    private BitSet makeBitSetFromBigInteger( final BigInteger rawValue ) {

        BitSet rawBits = new BitSet( rawSizeInBits_ );

        if ( rawBitOrder_.equals( "mostSignificantBitFirst" ) == true ) { // NOI18N
            for ( int iii = rawSizeInBits_ - 1; iii >= 0; --iii ) {
                rawBits.set( iii, rawValue.testBit( iii ) );
            }
        } else {
            for ( int iii = rawSizeInBits_ - 1; iii >= 0; --iii ) {
                rawBits.set( rawSizeInBits_ - iii - 1, rawValue.testBit( iii ) );
            }
        }

        return rawBits;

    }

    /** Convert the BitSet object from the encode() method over to a hex byte
     * string, ordered from the most significant byte to the least significant
     * byte.
     *
     * The most significant bits are padded with zero in this case when the
     * raw size is not on an even 8 bit boundary.  This results in the function
     * never returning a hex string that is less than 2 characters for each
     * byte, with a minimum of 1 byte.  A "0x" is prepended.  If the exact
     * raw size is needed, call the rawSizeInBits() method to determine which
     * of the uppermost bits are extraneous.
     *
     * @param bits BitSet returned from the encode() function.
     *
     * @return String containing the hex of the raw value to be encoded,
     * subject to the explanation above associated with this function.
     *
     */

    public final String bitSetToHex( final BitSet bits ) {

        int bitCount = rawSizeInBits_;
        if ( ( rawSizeInBits_ % 8 ) != 0 ) {
            bitCount += 8 - ( rawSizeInBits_ % 8 );
        }

        int byteCount = bitCount / 8;

        StringBuilder sb = new StringBuilder( "0x" ); // NOI18N

        byte[] bytes = bits.toByteArray();
        for ( int iii = byteCount - 1; iii >= 0; --iii ) {
            if ( iii < bytes.length ) {
                sb.append( String.format( "%02x", bytes[iii] ) ); // NOI18N
            } else {
                sb.append( "00" ); // NOI18N
            }
        }

        return sb.toString();

    }

    /** Convert the BitSet object from the encode() method over to a integral
     * number, ordered from the most significant byte to the least significant
     * byte.
     *
     * @param bits BitSet returned from the encode() function.
     *
     * @return String containing the hex of the raw value to be encoded,
     * subject to the explanation above associated with this function.
     *
     */

    public final BigInteger bitSetToNumber( final BitSet bits ) {

        int bitCount = rawSizeInBits_;
        if ( ( rawSizeInBits_ % 8 ) != 0 ) {
            bitCount += 8 - ( rawSizeInBits_ % 8 );
        }

        int byteCount = bitCount / 8;
        StringBuilder sb = new StringBuilder();
        byte[] bytes = bits.toByteArray();

        for ( int iii = byteCount - 1; iii >= 0; --iii ) {
            if ( iii < bytes.length ) {
                sb.append( String.format( "%02x", bytes[iii] ) ); // NOI18N
            } else {
                sb.append( "00" ); // NOI18N
            }
        }

        if ( bits.length() > rawSizeInBits_ ) {
            warn( itemName_ + " raw binary length '" +
                  Integer.toString( bits.length() ) + "' overflows raw " +
                  "encoding length of '" + Integer.toString( rawSizeInBits_ ) +
                  "'" );
        }

        return new BigInteger( sb.toString(), 16 );

    }

    /** Convert the BitSet object from the encode() method over to a binary
     * string of zeros and ones, ordered from most significant bit to least
     * significant bit.
     *
     * @param bits BitSet containing the bits returned from the encode()
     * function.
     *
     * @return String containing the binary zeros and ones, with all bit
     * positions populated for the entire length of the raw size.  The number
     * of bits will always exactly equal the raw encoding size, with the upper
     * possible unused bits padded with zeros.
     *
     */

    public final String bitSetToBinary( final BitSet bits ) {

        StringBuilder sb = new StringBuilder();

        for ( int iii = rawSizeInBits_ - 1; iii >= 0; --iii ) {
            sb.append( bits.get( iii ) == true ? "1" : "0" ); // NOI18N
        }

        return sb.toString();

    }

    /** Convert a string representation of a Raw Value to a BigInteger.
     *
     * All raw values are represented in the form of hexadecimal or perhaps in
     * more rare occasions, a user may present a base 10 number.  The
     * BigInteger is a convenient container for an arbitrary length series of
     * bytes that is easy to work with in hex form.
     *
     * A warning is logged if the string representation cannot be converted to
     * numeric.
     *
     * @param rawValue String containing the candidate raw representation.
     *
     * @return BigInteger containing the raw value or zero if a warning was
     * logged.
     *
     */

    public BigInteger integerStringToBigInteger( final String rawValue ) {

        BigInteger rawInteger = BigInteger.ZERO;

        try {

            String reasValue = rawValue.toLowerCase();
            if ( reasValue.startsWith( "0x" ) == true ) { // NOI18N
                rawInteger =
                    new BigInteger( reasValue.replaceFirst( "0x", "" ), 16 ); // NOI18N
            } else {
                rawInteger = new BigInteger( reasValue );
            }

        } catch ( NumberFormatException ex ) {
            warn( itemName_ + " raw value provided '" + rawValue +
                  "' is not a properly formatted hex or integer." );
        }

        return rawInteger;

    }

    private BigDecimal floatEncodingUncalibrate( final Number calValue ) {

        try {

            Calibration calObj;

            if ( defCal_ == null ) {
                return new BigDecimal( calValue.toString() );
            } else if ( defCal_.getPolynomialCalibrator() != null ) {
                calObj = new PolynomialCalibration( defCal_.getPolynomialCalibrator(), validRange_, rawSizeInBits_, rawTypeName_ );
            } else if ( defCal_.getSplineCalibrator() != null ) {
                calObj = new SplineCalibration( defCal_.getSplineCalibrator() );
            } else if ( defCal_.getMathOperationCalibrator() != null ) {
                calObj = new MathOperationCalibration( defCal_.getMathOperationCalibrator() );
            } else {
                warn( itemName_ +
                      " " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_unknown_calibrator" ) ); // NOI18N
                return new BigDecimal( calValue.toString() );
            }

            return new BigDecimal( calObj.uncalibrate( calValue ).doubleValue() );

        } catch ( XTCEDatabaseException ex ) {
            warn( itemName_ + " " + ex.getLocalizedMessage() ); // NOI18N
        }

        return new BigDecimal( calValue.toString() );

    }

    /** Sets the internal enumeration list information in the event that this
     * is an Enumerated Type Parameter/Argument.
     *
     * @param typeObj XTCETypedObject base class for the Type information
     * about this Parameter/Argument.
     *
     */

    private void setEnumerationList( final XTCETypedObject item ) {
        enums_ = item.getEnumerations();
    }

    /** Sets the internal default calibrator list information in the event that
     * this Parameter/Argument has an encoding type that supports calibration.
     *
     * The types that support Calibrators are Integer and Float data encoding.
     *
     */

    private void setDefaultCalibrator( final XTCETypedObject item ) {
        defCal_ = item.getDefaultCalibrator();
    }

    /** Check for reasonableness of the raw value to encode to an integer type
     * encoding.
     *
     * This method first figures out the range of possible values based on the
     * size in bits and the signed state.  It then checks if the value should
     * be restricted further by a possibly present ValidRange element.  It
     * applies those limits if they are intended for the raw value.
     *
     * @param rawValue BigInteger containing the raw value that will be encoded
     * if it turns out to be reasonable.
     *
     * @return boolean indicating if this function thinks the value is
     * reasonable to fit in the allowable size and range.
     *
     */

    public boolean isIntegerRawValueReasonable( final BigInteger rawValue ) {

        // first find the general size applicable to the bit length
        boolean minInclusive = true;
        boolean maxInclusive = true;
        long    minValue     = 0;
        long    maxValue     = (long)Math.pow( 2, rawSizeInBits_ ) - 1;

        // if it is signed, then correct that range
        if ( ( rawTypeName_ == RawType.signMagnitude  ) ||
             ( rawTypeName_ == RawType.onesComplement ) ||
             ( rawTypeName_ == RawType.twosComplement ) ) {
            minValue = -1 * (long)Math.pow( 2, ( rawSizeInBits_ - 1 ) );
            maxValue = (long)Math.pow( 2, ( rawSizeInBits_ - 1 ) ) - 1;
        }

        // if a ValidRange element exists then we want to filter the min and
        // max through that, but only if it applies to RAW
        if ( validRange_.isValidRangeApplied() == true ) {
            if ( validRange_.isLowValueCalibrated() == false ) {
                minValue = Math.round( Double.parseDouble( validRange_.getLowValue() ) );
                if ( validRange_.isLowValueInclusive() == false ) {
                    minInclusive = false;
                }
            }
            if ( validRange_.isHighValueCalibrated() == false ) {
                maxValue = Math.round( Double.parseDouble( validRange_.getHighValue() ) );
                if ( validRange_.isHighValueInclusive() == false ) {
                    maxInclusive = false;
                }
            }
        }

        // check the special negative case first
        if ( rawTypeName_ == RawType.unsigned ) {
            if ( rawValue.signum() < 0 ) {
                warn( itemName_ + " Unsigned value is '" +
                      rawValue.toString() +
                      "', which cannot be negative for unsigned encoding" );
                return false;
            }
        }

        // TODO: BigIntegers screw this up on long overflow!

        // check for the boundary conditions with the minimum first
        if ( minInclusive == false ) {
            if ( minValue >= rawValue.longValue() ) {
                warn( itemName_ + " Encoded value '" +
                      rawValue.toString() +
                      "' is too small for valid " +
                      "exclusive min value of '" +
                      Long.toString( minValue ) + "' on encoding of " +
                      rawTypeName_ );
                return false;
            }
        } else {
            if ( minValue > rawValue.longValue() ) {
                warn( itemName_ + " Encoded value '" +
                      rawValue.toString() +
                      "' is too small for valid " +
                      "inclusive min value of '" +
                      Long.toString( minValue ) + "' on encoding of " +
                      rawTypeName_ );
                return false;
            }
        }

        // check for the boundary conditions with the maximum
        if ( maxInclusive == false ) {
            if ( maxValue <= rawValue.longValue() ) {
                warn( itemName_ + " Encoded value '" +
                      rawValue.toString() +
                      "' is too large for valid " +
                      "exclusive max value of '" +
                      Long.toString( maxValue ) + "' on encoding of " +
                      rawTypeName_ );
                return false;
            }
        } else {
            if ( maxValue < rawValue.longValue() ) {
                warn( itemName_ + " Encoded value '" +
                      rawValue.toString() +
                      "' is too large for valid " +
                      "inclusive max value of '" +
                      Long.toString( maxValue ) + "' on encoding of " +
                      rawTypeName_ );
                return false;
            }
        }

        return true;

    }

    /** Check for reasonableness of the raw value to encode to a float type
     * encoding.
     *
     * This method first figures out the range of possible values based on the
     * size in bits of the floating point range.  This is generally not very
     * useful because the range is very wide.  It then checks if the value
     * should be restricted further by a possibly present ValidRange element.
     * It applies those limits if they are intended for the raw value.
     *
     * @param rawValue double containing the raw value that will be encoded
     * if it turns out to be reasonable.
     *
     * @return boolean indicating if this function thinks the value is
     * reasonable to fit in the allowable size and range.
     *
     */

    public boolean isFloatRawValueReasonable( final double rawValue ) {

        // first find the general size applicable to the bit length
        boolean minInclusive = true;
        boolean maxInclusive = true;
        double  minValue     = -Double.MAX_VALUE;
        double  maxValue     = Double.MAX_VALUE;

        // if a ValidRange element exists then we want to filter the min and
        // max through that, but only if it applies to RAW
        if ( validRange_.isValidRangeApplied() == true ) {
            if ( validRange_.isLowValueCalibrated() == false ) {
                minValue = Math.round( Double.parseDouble( validRange_.getLowValue() ) );
                if ( validRange_.isLowValueInclusive() == false ) {
                    minInclusive = false;
                }
            }
            if ( validRange_.isHighValueCalibrated() == false ) {
                maxValue = Math.round( Double.parseDouble( validRange_.getHighValue() ) );
                if ( validRange_.isHighValueInclusive() == false ) {
                    maxInclusive = false;
                }
            }
        }

        // check for the boundary conditions with the minimum first
        if ( minInclusive == false ) {
            if ( minValue >= rawValue ) {
                warn( itemName_ + " Encoded value '" +
                      Double.toString( rawValue ) +
                      "' is too small for valid " +
                      "exclusive min value of '" +
                      Double.toString( minValue ) + "' on encoding of " +
                      rawTypeName_ );
                return false;
            }
        } else {
            if ( minValue > rawValue ) {
                warn( itemName_ + " Encoded value '" +
                      Double.toString( rawValue ) +
                      "' is too small for valid " +
                      "inclusive min value of '" +
                      Double.toString( minValue ) + "' on encoding of " +
                      rawTypeName_ );
                return false;
            }
        }

        // check for the boundary conditions with the maximum
        if ( maxInclusive == false ) {
            if ( maxValue <= rawValue ) {
                warn( itemName_ + " Encoded value '" +
                      Double.toString( rawValue ) +
                      "' is too large for valid " +
                      "exclusive max value of '" +
                      Double.toString( maxValue ) + "' on encoding of " +
                      rawTypeName_ );
                return false;
            }
        } else {
            if ( maxValue < rawValue ) {
                warn( itemName_ + " Encoded value '" +
                      Double.toString( rawValue ) +
                      "' is too large for valid " +
                      "inclusive max value of '" +
                      Double.toString( maxValue ) + "' on encoding of " +
                      rawTypeName_ );
                return false;
            }
        }

        return true;

    }

    private void warn( final String warning ) {

        if ( warnings_ == null ) {
            warnings_ = new ArrayList<>();
        }

        warnings_.add( warning );

    }

    // Private Data Members

    private String          itemName_;
    private EngineeringType euTypeName_;
    private RawType         rawTypeName_;
    private int             rawSizeInBits_;
    private String          rawBitOrder_;
    private boolean         validObject_;
    private String          booleanZeroString_;
    private String          booleanOneString_;

    private List<String>               warnings_ = null;
    private List<ValueEnumerationType> enums_;
    private CalibratorType             defCal_;
    private XTCEValidRange             validRange_;
    private XTCETypedObject            itemObj_;
    private XTCEAbsoluteTimeType       timeHandler_ = null;

    private static final byte[] utf16_ = new byte[] { (byte)0xfe, (byte)0xff };

}
