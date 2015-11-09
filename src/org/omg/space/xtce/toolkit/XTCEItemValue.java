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

package org.omg.space.xtce.toolkit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.omg.space.xtce.database.AbsoluteTimeDataType;
import org.omg.space.xtce.database.BooleanDataType;
import org.omg.space.xtce.database.CalibratorType;
import org.omg.space.xtce.database.CalibratorType.MathOperationCalibrator;
import org.omg.space.xtce.database.CalibratorType.SplineCalibrator;
import org.omg.space.xtce.database.NameDescriptionType;
import org.omg.space.xtce.database.PolynomialType;
import org.omg.space.xtce.database.SplinePointType;
import org.omg.space.xtce.database.ValueEnumerationType;
import org.omg.space.xtce.toolkit.XTCETypedObject.EngineeringType;
import org.omg.space.xtce.toolkit.XTCETypedObject.RawType;

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
            warn( itemName_ + " " +
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
                warn( itemName_ + " " + ex.getLocalizedMessage() );
                validObject_ = false;
                return;
            }
        }

        // gather the extended attributes that are needed for specific types
        setValidRangeAttributes( item );
        setEnumerationList( item );
        setDefaultCalibrator( item );

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

        if ( rawBitOrder_.equals( "mostSignificantBitFirst" ) == false ) {
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
                return numericValue.toString();

            case signMagnitude: {
                int sizeInBits = rawSizeInBits_;
                BigInteger halfValue = BigInteger.valueOf( 2 ).pow( sizeInBits - 1 );
                if ( numericValue.compareTo( halfValue ) >= 0 ) {
                    numericValue = numericValue.subtract( halfValue ).negate();
                }
                return numericValue.toString();
            }

            case twosComplement: {
                int sizeInBits = rawSizeInBits_;
                BigInteger halfValue = BigInteger.valueOf( 2 ).pow( sizeInBits - 1 );
                BigInteger fullValue = BigInteger.valueOf( 2 ).pow( sizeInBits );
                if ( numericValue.compareTo( halfValue ) >= 0 ) {
                    numericValue = numericValue.subtract( fullValue );
                }
                return numericValue.toString();
            }

            case onesComplement: {
                int sizeInBits = rawSizeInBits_;
                BigInteger halfValue = BigInteger.valueOf( 2 ).pow( sizeInBits - 1 );
                BigInteger fullValue = BigInteger.valueOf( 2 ).pow( sizeInBits );
                if ( numericValue.compareTo( halfValue ) >= 0 ) {
                    numericValue = numericValue.subtract( fullValue ).add( BigInteger.ONE );
                }
                return numericValue.toString();
            }

            case IEEE754_1985: {
                int sizeInBits = rawSizeInBits_;
                if ( sizeInBits == 32 ) {
                    Float floatValue = Float.intBitsToFloat( numericValue.intValue() );
                    return floatValue.toString();
                } else if ( sizeInBits == 64 ) {
                    Double doubleValue = Double.longBitsToDouble( numericValue.longValue() );
                    return doubleValue.toString();
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
            }

            case binary:
                return "0x" + numericValue.toString( 16 ); // NOI18N

            case UTF8:
                return getUncalibratedFromRawString( "UTF-8", numericValue ); // NOI18N

            case UTF16:
                return getUncalibratedFromRawString( "UTF-16", numericValue ); // NOI18N

            default:
                // not supported MILSTD_1750A, BCD, packedBCD
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
                    warn( itemName_ + ex.getLocalizedMessage() );
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

        BitSet rawValue   = new BitSet( rawSizeInBits_ );
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

        BitSet rawValue   = new BitSet( rawSizeInBits_ );
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

        BitSet rawValue   = new BitSet( rawSizeInBits_ );
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

        BitSet rawValue   = new BitSet( rawSizeInBits_ );
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
                    warn( itemName_ + " Invalid value for binary " +
                          "encoding '" + uncalValue + "', negative has no " +
                          "meaning in a binary context." );
                } else {
                    String lowerCalValue = uncalValue.toLowerCase();
                    intValue = integerStringToBigInteger( lowerCalValue );
                }
            } else if ( rawTypeName_ == RawType.IEEE754_1985 ) {
                String reasCalValue = uncalValue.toLowerCase();
                if ( reasCalValue.startsWith( "0x" ) == true ) { // NOI18N
                    BigInteger temp = new BigInteger( reasCalValue.replaceFirst( "0x", "" ), 16 ); // NOI18N
                    return getRawFromUncalibrated( new BigDecimal( temp ) );
                } else {
                    return getRawFromUncalibrated( new BigDecimal( reasCalValue ) );
                }
            } else if ( rawTypeName_ == RawType.MILSTD_1750A ) {
                warn( "Unsupported encoding type for " + itemName_ +
                      " Encoding: " + rawTypeName_ );
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
                // TODO Add DURATION Type
                warn( "Relative Time Type Not Yet Supported for " + itemName_ );
            } else {
                warn( "AGGREGATE and ARRAY types for item " + itemName_ +
                      " cannot directly be encoded." + 
                      "  Use their children instead" );
            }

        } catch ( NumberFormatException ex ) {
            warn( itemName_ +
                  " Invalid String value for encoding " +
                  rawTypeName_ +
                  " of '" +
                  uncalValue +
                  "'" );
        } catch ( ArithmeticException ex ) {
            warn( itemName_ +
                  " Invalid Decimal value for encoding " +
                  rawTypeName_ +
                  " of '" +
                  uncalValue +
                  "'" );
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
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ );
            if ( uncalValue.compareTo( max ) == 1 ) {
                warn( itemName_ + " overflow value '" + uncalValue +
                      "', larger than available encoding bits." );
                uncalValue = BigInteger.ZERO;
            } else if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                uncalValue = BigInteger.ZERO;
            }
        } else if ( rawTypeName_ == RawType.signMagnitude ) {
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ - 1 );
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
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ - 1 );
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
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ - 1 );
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
                    warn( "Unsupported encoding type for " +
                          itemName_ +
                          " Encoding: " +
                          rawTypeName_ );
                }
            }
        } else if ( rawTypeName_ == RawType.MILSTD_1750A ) {
            warn( "Unsupported encoding type for " +
                  itemName_ +
                  " Encoding: " +
                  rawTypeName_ );
            uncalValue = BigInteger.ZERO;
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
            // TODO Add TIME Type
            warn( "Absolute Time Type Not Yet Supported for " + itemName_ );
            uncalValue = BigInteger.ZERO;
        } else if ( euTypeName_ == EngineeringType.DURATION ) {
            // TODO Add DURATION Type
            warn( "Relative Time Type Not Yet Supported for " + itemName_ );
            uncalValue = BigInteger.ZERO;
        } else {
            warn( "AGGREGATE and ARRAY types for item " +
                  itemName_ +
                  " cannot directly be encoded." + 
                  "  Use their children instead" );
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
                      " Invalid Decimal value for encoding " +
                      rawTypeName_ +
                      " of '" +
                      uncalValue +
                      "'" );
            }
        } else if ( rawTypeName_ == RawType.binary ) {
            warn( "Unsupported encoding type for " +
                  itemName_ +
                  " Encoding: " +
                  rawTypeName_ );
        } else if ( rawTypeName_ == RawType.IEEE754_1985 ) {
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                intValue = BigInteger.ZERO;
            } else {
                if ( rawSizeInBits_ == 32 ) {
                    intValue = BigInteger.valueOf( Float.floatToRawIntBits( uncalValue.floatValue() ) );
                } else if ( rawSizeInBits_ == 64 ) {
                    intValue = BigInteger.valueOf( Double.doubleToRawLongBits( uncalValue.doubleValue() ) );
                } else if ( rawSizeInBits_ == 128 ) {
                    warn( "Unsupported encoding type for " +
                          itemName_ +
                          " Encoding: " +
                          rawTypeName_ );
                }
            }
        } else if ( rawTypeName_ == RawType.MILSTD_1750A ) {
            warn( "Unsupported encoding type for " +
                  itemName_ +
                  " Encoding: " +
                  rawTypeName_ );
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
            // TODO Add TIME Type
            warn( "Absolute Time Type Not Yet Supported for " + itemName_ );
        } else if ( euTypeName_ == EngineeringType.DURATION ) {
            // TODO Add DURATION Type
            warn( "Relative Time Type Not Yet Supported for " + itemName_ );
        } else {
            warn( "AGGREGATE and ARRAY types for item " +
                  itemName_ +
                  " cannot directly be encoded." + 
                  "  Use their children instead" );
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
                return uncalibrateIntegerType( integerValue );
            }

            case ENUMERATED: {
                BigInteger integerValue = integerFromEnumerationType( euValue );
                return uncalibrateIntegerType( integerValue );
            }

            case STRING:
                return uncalibrateStringType( euValue );

            case BINARY:
                return "0x" +
                       integerStringToBigInteger( euValue ).toString( 16 );

            case FLOAT32:
            case FLOAT64:
            case FLOAT128:
                try {
                    if ( euValue.startsWith( "0x" ) == true ) {
                        BigInteger intValue = new BigInteger( euValue.replaceFirst( "0x", "" ), 16 );
                        return uncalibrateFloatType( new BigDecimal( intValue ) );
                    } else if ( euValue.startsWith( "0X" ) == true ) {
                        BigInteger intValue = new BigInteger( euValue.replaceFirst( "0X", "" ), 16 );
                        return uncalibrateFloatType( new BigDecimal( intValue ) );
                    }
                    return uncalibrateFloatType( new BigDecimal( euValue ) );
                } catch ( NumberFormatException ex ) {
                    warn( itemName_ +
                          " Invalid String value for uncalibrate " +
                          "IEEE754_1985 of '" +
                          euValue +
                          "'" );
                }
                break;

            case UNSIGNED:
            case SIGNED:
                try {
                    if ( euValue.startsWith( "0x" ) == true ) {
                        return uncalibrateIntegerType( new BigInteger( euValue.replaceFirst( "0x", "" ), 16 ) );
                    } else if ( euValue.startsWith( "0X" ) == true ) {
                        return uncalibrateIntegerType( new BigInteger( euValue.replaceFirst( "0X", "" ), 16 ) );
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
                    warn( itemName_ + ex.getLocalizedMessage() );
                }
                break;

            case DURATION:
                // TODO Add DURATION Type
                warn( "Relative Time Type Not Yet Supported for " + itemName_ );
                break;

            default:
                warn( "AGGREGATE and ARRAY types for item " +
                      itemName_ +
                      " cannot directly be encoded." + 
                      "  Use their children instead" );
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

    /** Function to resolve a numeric uncalibrated value to a Calibrated EU
     * Boolean string value.
     *
     * @param uncalValue double containing the raw value, which will result in
     * a warning if the value is not integral.
     *
     * @return String containing the Boolean EU type text.
     *
     */

    private String booleanTypeFromUncalibrated( final double uncalValue ) {

        if ( ( uncalValue % 1 ) != 0 ) {
            warn( itemName_ +
                  " Invalid raw value of '" +
                  Double.toString( uncalValue ) +
                  "' for Boolean EU type" );
            return booleanZeroString_;
        }

        return booleanTypeFromUncalibrated( Double.valueOf( uncalValue ).longValue() );

    }

    /** Function to resolve a numeric uncalibrated value to a Calibrated EU
     * Boolean string value.
     *
     * @param uncalValue long containing the raw value, which will result in
     * a warning if the value is not either 0 or 1.
     *
     * @return String containing the Boolean EU type text.
     *
     */

    private String booleanTypeFromUncalibrated( final long uncalValue ) {

        if ( uncalValue == 0 ) {
            return booleanZeroString_;
        } else if ( uncalValue == 1 ) {
            return booleanOneString_;
        } else {
            warn( itemName_ +
                  " Invalid raw value of '" +
                  Long.toString( uncalValue ) +
                  "' for Boolean EU type" );
            return booleanZeroString_;
        }

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

    /** Function to resolve a numeric uncalibrated value to a Calibrated EU
     *  Enumerated string value.
     *
     * @param uncalValue BigInteger containing the uncalibrated value.  A
     * warning will be recorded if the number does not resolve to a label.
     *
     * @return String containing the Enumerated EU type text, or an empty
     * string if no label was found for the numeric value.
     *
     */

    private String enumeratedTypeFromUncalibrated( final BigInteger uncalValue ) {

        for ( ValueEnumerationType enumItem : enums_ ) {
            BigInteger value    = enumItem.getValue();
            BigInteger maxValue = value;
            if ( enumItem.getMaxValue() != null ) {
                maxValue = enumItem.getMaxValue();
            }
            if ( ( uncalValue.compareTo( value )    > -1 ) &&
                 ( uncalValue.compareTo( maxValue ) < 1  ) ) {
                return enumItem.getLabel();
            }
        }

        warn( itemName_ + " No enumeration label found for value of '" +
              uncalValue.toString() + "'");

        return "";

    }

    private String uncalibrateIntegerType( final BigInteger calValue ) {

        switch ( rawTypeName_ ) {

            case unsigned:
            case signMagnitude:
            case twosComplement:
            case onesComplement: {
                BigInteger uncalValue = integerEncodingUncalibrate( calValue );
                if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                    return "0";
                }
                return uncalValue.toString();
            }

            case binary:
                return calValue.toString();

            case IEEE754_1985: {
                BigDecimal uncalValue =
                    floatEncodingUncalibrate( new BigDecimal( calValue ) );
                if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                    return "0.0";
                }
                return Double.toString( uncalValue.doubleValue() );
            }

            case MILSTD_1750A:
                warn( "Unsupported encoding type for " +
                      itemName_ +
                      " Encoding: " +
                      rawTypeName_ );
                break;

            case UTF8:
            case UTF16:
                return calValue.toString();

            default:
                warn( "Unrecognized encoding type for " +
                      itemName_ +
                      " Encoding: " +
                      rawTypeName_ );

        }

        return "0";

    }

    private String uncalibrateFloatType( final BigDecimal calValue ) {

        switch ( rawTypeName_ ) {

            case unsigned:
            case signMagnitude:
            case twosComplement:
            case onesComplement: {
                BigInteger uncalValue = integerEncodingUncalibrate( calValue );
                if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                    return "0";
                }
                return uncalValue.toString();
            }

            case binary:
                return calValue.toString();

            case IEEE754_1985: {
                BigDecimal uncalValue =
                    floatEncodingUncalibrate( calValue );
                if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                    return "0.0";
                }
                return Double.toString( uncalValue.doubleValue() );
            }

            case MILSTD_1750A:
                warn( "Unsupported encoding type for " +
                      itemName_ +
                      " Encoding: " +
                      rawTypeName_ );
                break;

            case UTF8:
            case UTF16:
                return calValue.toString();

            default:
                warn( "Unrecognized encoding type for " +
                      itemName_ +
                      " Encoding: " +
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
            return "";
        }
        int endIndex = retValue.length() - 1;
        while ( retValue.charAt( endIndex ) == '\0' ) {
            if ( endIndex == 0 ) {
                return "";
            } else {
                retValue = retValue.substring( 0, endIndex );
            }
            endIndex = retValue.length() - 1;
        }
        return retValue;

    }

    private String uncalibrateStringType( final String calValue ) {

        return calValue;

    }

    private String getCalibratedFromIntegerString( final String calValue ) {

        try {
            BigInteger retValue = new BigInteger( calValue );
            return retValue.toString();
        } catch ( NumberFormatException ex ) {
            warn( itemName_ + " Calibrated value '" +
                  calValue + "' is not representative of an integer" );
        }

        return "";

    }

    private String getCalibratedFromFloatString( final String calValue ) {

        try {
            BigDecimal retValue = new BigDecimal( calValue );
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

    /** Private method to calculate and apply the calibrator to the
     * uncalibrated value.
     *
     * This method does not support ContextCalibrator and
     * MathOperationCalibrator elements and will record a warning.
     * ContextCalibrators are quietly ignored.
     *
     * @param uncalValue String containing the uncalibrated value derived from
     * the raw binary value in the stream.
     *
     * @return String containing the value as calibrated, or a quick return of
     * the original value if no calibrator exists to act on.
     *
     */

    private String applyCalibrator( final String uncalValue ) {

        CalibratorType calNode = defCal_;
        if ( calNode == null ) {
            return uncalValue;
        }

        double xValue = Double.valueOf( uncalValue );

        if ( calNode.getPolynomialCalibrator() != null ) {
            PolynomialType polyCalNode = calNode.getPolynomialCalibrator();
            List<PolynomialType.Term> terms = polyCalNode.getTerm();
            double doubleValue = applyPolynomial( xValue, terms );
            return Double.toString( doubleValue );
        } else if ( calNode.getSplineCalibrator() != null ) {
            SplineCalibrator      splineNode  = calNode.getSplineCalibrator();
            BigInteger            order       = splineNode.getOrder();
            boolean               extrapolate = splineNode.isExtrapolate();
            List<SplinePointType> points      = splineNode.getSplinePoint();
            double doubleValue = applySpline( xValue, order, extrapolate, points );
            return Double.toString( doubleValue );
        } else {
            warn( itemName_ + " Unsupported calibrator form" );
        }

        return "";

    }

    /** Private method to apply a Polynomial Calibrator to an uncalibrated
     * value.
     *
     * This method doesn't care which order the coefficient/exponent terms
     * appears because of the commutative property of addition, so any
     * sequence of terms may be specified in XTCE and this function will apply
     * them as they are specified.
     *
     * This function is not concerned with the encoding type (integer or float)
     * when performing the calculation.  The calculation is always done in the
     * floating point space and if the engineering type is integer, then it
     * will be later rounded back.  This could be a point of controversy.
     *
     * @param xValue double containing the uncalibrated value.
     *
     * @param terms List of the Term elements in the XTCE Polynomial Calibrator
     * element.
     *
     * @return double containing the results.
     *
     */

    private double applyPolynomial( final double                    xValue,
                                    final List<PolynomialType.Term> terms ) {

        double yValue = 0.0;
        for ( PolynomialType.Term term : terms ) {
            double coeff    = term.getCoefficient();
            double exponent = term.getExponent().doubleValue();
            double powTerm  = Math.pow( xValue, exponent );
            yValue += coeff * powTerm;
        }
        return yValue;

    }

    /** Private method to apply a Spline Calibrator, otherwise known as a
     * "piecewise function", to an uncalibrated raw value.
     *
     * The Spline Point pairs in XTCE are expected to be in sequential order
     * from lowest to highest raw value.  The first two points are mandatory
     * and subsequent points are made by adding one new point and dropping the
     * previous low point.  This assures that the evaluation is continuous.  It
     * is also assumed that they are in order from lowest raw value to highest
     * raw value.
     *
     * @param xValue double containing the uncalibrated value.
     *
     * @param order BigInteger indicating the order of interpolation between
     * the points.  This can be 0, which is a flat line from the low point to
     * the high point, 1 for a linear interpolation, and 2 for a quadratic
     * interpolation.  In the event that the value is exactly at the high
     * point of the pair of points, then the value is evaluated when the next
     * pair occurs and the value is at the low point.
     *
     * @param extrapolate boolean indicating if the value should be
     * extrapolated when outside of the spline point pairs based on the curve
     * from the first or last spline point set, respectively.
     *
     * @param points List of SplinePointType objects from the SplinePoint
     * elements in the XTCE SplineCalibrator element.  Two more more of these
     * will always exist per the XTCE schema definition.
     *
     * @return BigDecimal containing the results.
     *
     */

    private double applySpline( final double                xValue,
                                final BigInteger            order,
                                final boolean               extrapolate,
                                final List<SplinePointType> points ) {

        // TODO: Support quadratics because I did it on the other side

        if ( order.intValue() > 1 ) {
            warn( itemName_ + " Unsupported Spline " +
                  "order of approximation " + order.toString() +
                  ", only flat, linear, and quadratic (0, 1, 2) are " +
                  "supported." );
            return 0.0;
        }

        double rawLow  = points.get( 0 ).getRaw();
        double calLow  = points.get( 0 ).getCalibrated();
        for ( int iii = 1; iii < points.size(); ++iii ) {
            double rawHigh = points.get( iii ).getRaw();
            double calHigh = points.get( iii ).getCalibrated();
            if ( ( xValue >= rawLow ) && ( xValue <= rawHigh ) ) {
                if ( order.intValue() == 0 ) {
                    // if it equals rawHigh, then take the next one as there is
                    // a discontinuity and this is how I handled it
                    if ( xValue < rawHigh ) {
                        return calLow;
                    }
                } else if ( order.intValue() == 1 ) {
                    double slope = ( calHigh - calLow ) / ( rawHigh - rawLow );
                    double intercept = calLow - ( slope * rawLow );
                    //double slope = ( rawHigh - rawLow ) / ( calHigh - calLow );
                    //System.out.println( "xvalue = " + new Double( xValue ).toString() +
                    //    " slope = " + new Double( slope ).toString() +
                    //    " calLow = " + new Double( calLow ).toString() +
                    //    " calHigh = " + new Double( calHigh ).toString() );
                    return ( slope * xValue ) + intercept;
                }
            }
            rawLow = rawHigh;
            calLow = calHigh;
        }

        // out of bounds case

        return xValue;
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

        if ( rawBitOrder_.equals( "mostSignificantBitFirst" ) == true ) {
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

        NameDescriptionType typeObj = item.getTypeReference();

        if ( typeObj instanceof BooleanDataType ) {
            booleanZeroString_ = ((BooleanDataType)typeObj).getZeroStringValue();
            booleanOneString_  = ((BooleanDataType)typeObj).getOneStringValue();
        }

        validRange_ = item.getValidRange();

    }

    private BitSet makeBitSetFromBigInteger( final BigInteger rawValue ) {

        BitSet rawBits = new BitSet( rawSizeInBits_ );

        if ( rawBitOrder_.equals( "mostSignificantBitFirst" ) == true ) {
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

        StringBuilder sb = new StringBuilder( "0x" );

        byte[] bytes = bits.toByteArray();
        for ( int iii = byteCount - 1; iii >= 0; --iii ) {
            if ( iii < bytes.length ) {
                sb.append( String.format( "%02x", bytes[iii] ) );
            } else {
                sb.append( "00" );
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
                sb.append( String.format( "%02x", bytes[iii] ) );
            } else {
                sb.append( "00" );
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
            sb.append( bits.get( iii ) == true ? "1" : "0" );
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
        String     reasValue  = rawValue;

        try {

            reasValue = rawValue.toLowerCase();
            if ( reasValue.startsWith( "0x" ) == true ) {
                rawInteger =
                    new BigInteger( reasValue.replaceFirst( "0x", "" ), 16 );
            } else {
                rawInteger = new BigInteger( reasValue );
            }

        } catch ( NumberFormatException ex ) {
            warn( itemName_ + " raw value provided '" + rawValue +
                  "' is not a properly formatted hex or integer." );
        }

        return rawInteger;

    }

    private BigDecimal floatEncodingUncalibrate( final BigDecimal calValue ) {

        if ( defCal_ == null ) {
            return calValue;
        }

        PolynomialType polyCal = defCal_.getPolynomialCalibrator();
        if ( polyCal != null ) {
            HashMap<BigInteger, BigDecimal> terms     = new HashMap<>();
            List<PolynomialType.Term>       xtceTerms = polyCal.getTerm();
            long maxExponent = 0;
            for ( PolynomialType.Term term : xtceTerms ) {
                if ( term.getCoefficient() != 0.0 ) {
                    terms.put( term.getExponent(), new BigDecimal( term.getCoefficient() ) );
                    if ( term.getExponent().longValue() > maxExponent ) {
                        maxExponent = term.getExponent().longValue();
                    }
                }
            }
            if ( maxExponent <= 1 ) {
                double value = calValue.doubleValue();
                if ( terms.containsKey( BigInteger.ZERO ) == true ) {
                    value -= terms.get( BigInteger.ZERO ).doubleValue();
                }
                if ( terms.containsKey( BigInteger.ONE ) == true ) {
                    value /= terms.get( BigInteger.ONE ).doubleValue();
                }
                return new BigDecimal( value );
            } else if ( maxExponent == 2 ) {
                final BigInteger inttwo = new BigInteger( "2" );
                // evaluate b^2 -4ac to determine if roots exist
                double aaa = 0.0;
                double bbb = 0.0;
                double ccc = -1.0 * calValue.doubleValue();
                if ( terms.containsKey( BigInteger.ZERO ) == true ) {
                    ccc += terms.get( BigInteger.ZERO ).doubleValue();
                }
                if ( terms.containsKey( BigInteger.ONE ) == true ) {
                    bbb = terms.get( BigInteger.ONE ).doubleValue();
                }
                if ( terms.containsKey( inttwo ) == true ) {
                    aaa = terms.get( inttwo ).doubleValue();
                }
                double discriminant = Math.pow( bbb, 2 ) - ( 4.0 * aaa * ccc );
                if ( discriminant < 0 ) {
                    warn( "Polynomial Calibrator for " +
                          itemName_ +
                          " has no real roots for EU value " +
                          calValue.toString() );
                    return BigDecimal.ZERO;
                }
                double posroot = Math.sqrt( discriminant );
                double root1   = ( ( bbb * -1.0 ) - posroot ) / ( 2.0 * aaa );
                double root2   = ( ( bbb * -1.0 ) + posroot ) / ( 2.0 * aaa );
                //System.out.println( "Root1 = " + Double.toString( root1 ) + " Root2 = " + Double.toString( root2 ) );
                double bestPick = findBestRoot( root1, root2 );
                return new BigDecimal( bestPick );
            } else {
                warn( "Polynomial Calibrator for " +
                      itemName_ +
                      " contains exponent power as high as " +
                      Long.toString( maxExponent ) +
                      ".  Not supported by this toolkit." );
                return BigDecimal.ZERO;
            }
        }

        SplineCalibrator splineCal = defCal_.getSplineCalibrator();
        if ( splineCal != null ) {
            long    interpolateOrder = splineCal.getOrder().longValue();
            boolean extrapolate      = splineCal.isExtrapolate();
            List<SplinePointType> points = splineCal.getSplinePoint();
            ArrayList<BigDecimal> calList = new ArrayList<>();
            ArrayList<BigDecimal> rawList = new ArrayList<>();
            for ( SplinePointType point : points ) {
                calList.add( new BigDecimal( point.getCalibrated() ) );
                rawList.add( new BigDecimal( point.getRaw() ) );
            }
            BigDecimal minCalValue = calList.get( 0 );
            BigDecimal maxCalValue = calList.get( calList.size() - 1);
            for ( BigDecimal cal : calList ) {
                if ( cal.min( minCalValue ) == cal ) {
                    minCalValue = cal;
                }
                if ( cal.max( maxCalValue ) == cal ) {
                    maxCalValue = cal;
                }
            }
            if ( extrapolate == false ) {
                if ( ( calValue.compareTo( minCalValue ) < 0 ) ||
                     ( calValue.compareTo( maxCalValue ) > 0 ) ) {
                    warn( "Spline Calibrator for " +
                          itemName_ +
                          " does not bound calibrated value " +
                          calValue.toString() +
                          " and extrapolate is false" );
                    return BigDecimal.ZERO;
                }
            }
            // shema requires two spline points minimum, so this should never
            // hit a null case where a spline point is not found.
            BigDecimal rawValue1 = null;
            BigDecimal rawValue2 = null;
            BigDecimal calValue1 = null;
            BigDecimal calValue2 = null;
            Iterator<BigDecimal> calitr = calList.iterator();
            Iterator<BigDecimal> rawitr = rawList.iterator();
            if ( calitr.hasNext() == true ) {
                calValue1 = calitr.next();
                rawValue1 = rawitr.next();
            }
            while ( calitr.hasNext() == true ) {
                if ( calValue2 != null ) {
                    calValue1 = calValue2;
                    rawValue1 = rawValue2;
                }
                calValue2 = calitr.next();
                rawValue2 = rawitr.next();
                //System.out.println( "Cals: cal1 = " + calValue1.toString() +
                //                    " cal2 = " + calValue2.toString() );
                if ( ( calValue1.compareTo( calValue ) <= 0 ) &&
                     ( calValue2.compareTo( calValue ) >= 0 ) ) {
                    if ( calValue.equals( calValue1 ) == true ) {
                        return rawValue1;
                    } else if ( calValue.equals( calValue2 ) == true ) {
                        return rawValue2;
                    }
                    break;
                }
            }
            if ( rawValue1 == null || rawValue2 == null ) {
                warn( "Spline Calibrator for " +
                      itemName_ +
                      " does not bound calibrated value " +
                      calValue.toString() );
                return BigDecimal.ZERO;
            }
            //System.out.println( calValue.toString() +
            //                    " Order = " + Long.toString( interpolateOrder ) +
            //                    " y2 = " + calValue2.toString() +
            //                    " y1 = " + calValue1.toString() +
            //                    " x2 = " + rawValue2.toString() +
            //                    " x1 = " + rawValue2.toString() );
            double y2 = calValue2.doubleValue();
            double y1 = calValue1.doubleValue();
            double x2 = rawValue2.doubleValue();
            double x1 = rawValue1.doubleValue();
            if ( interpolateOrder == 0 ) {
                return new BigDecimal( ( x1 + x2 ) / 2.0 );
            } else if ( interpolateOrder == 1 ) {
                double slope = ( y2 - y1 ) / ( x2 - x1 );
                //System.out.println( "Slope = " + Double.toString( slope ) );
                double rawValue = ( calValue.doubleValue() - y1 ) / slope + x1;
                //System.out.println( "Raw = " + Double.toString( rawValue ) );
                return new BigDecimal( rawValue );
            } else {
                warn( "Spline Calibrator for " +
                      itemName_ +
                      " contains interpolate order of " +
                      Long.toString( interpolateOrder ) +
                      ".  Not supported by this toolkit." );
                return BigDecimal.ZERO;
            }
        }

        MathOperationCalibrator mathCal = defCal_.getMathOperationCalibrator();
        if ( mathCal != null ) {
            warn( "MathOperationCalibrator for " +
                  itemName_ +
                  " not supported" );
            return BigDecimal.ZERO;
        }

        return calValue;

    }

    private BigDecimal floatEncodingUncalibrate( final BigInteger calValue ) {

        if ( defCal_ == null ) {
            return new BigDecimal( calValue );
        }

        return floatEncodingUncalibrate( new BigDecimal( calValue ) );

    }

    private BigInteger integerEncodingUncalibrate( final BigDecimal calValue ) {

        if ( defCal_ == null ) {
            long value = Math.round( calValue.doubleValue() );
            return new BigInteger( Long.toString( value ) );
        }

        PolynomialType polyCal = defCal_.getPolynomialCalibrator();
        if ( polyCal != null ) {
            HashMap<BigInteger, BigDecimal> terms       = new HashMap<>();
            List<PolynomialType.Term>       xtceTerms   = polyCal.getTerm();
            long                            maxExponent = 0;
            for ( PolynomialType.Term term : xtceTerms ) {
                if ( term.getCoefficient() != 0.0 ) {
                    terms.put( term.getExponent(), new BigDecimal( term.getCoefficient() ) );
                    if ( term.getExponent().longValue() > maxExponent ) {
                        maxExponent = term.getExponent().longValue();
                    }
                }
            }
            if ( maxExponent <= 1 ) {
                double value = calValue.doubleValue();
                if ( terms.containsKey( BigInteger.ZERO ) == true ) {
                    value -= terms.get( BigInteger.ZERO ).doubleValue();
                }
                if ( terms.containsKey( BigInteger.ONE ) == true ) {
                    value /= terms.get( BigInteger.ONE ).doubleValue();
                }
                return BigInteger.valueOf( Double.valueOf( value ).longValue() );
            } else if ( maxExponent == 2 ) {
                final BigInteger inttwo = new BigInteger( "2" );
                // evaluate b^2 -4ac to determine if roots exist
                double aaa = 0.0;
                double bbb = 0.0;
                double ccc = -1.0 * calValue.doubleValue();
                if ( terms.containsKey( BigInteger.ZERO ) == true ) {
                    ccc += terms.get( BigInteger.ZERO ).doubleValue();
                }
                if ( terms.containsKey( BigInteger.ONE ) == true ) {
                    bbb = terms.get( BigInteger.ONE ).doubleValue();
                }
                if ( terms.containsKey( inttwo ) == true ) {
                    aaa = terms.get( inttwo ).doubleValue();
                }
                double discriminant = Math.pow( bbb, 2 ) - ( 4.0 * aaa * ccc );
                if ( discriminant < 0 ) {
                    warn( "Polynomial Calibrator for " +
                          itemName_ +
                          " has no real roots for EU value " +
                          calValue.toString() );
                    return BigInteger.ZERO;
                }
                double posroot = Math.sqrt( discriminant );
                double root1   = ( ( bbb * -1.0 ) - posroot ) / ( 2.0 * aaa );
                double root2   = ( ( bbb * -1.0 ) + posroot ) / ( 2.0 * aaa );
                //System.out.println( "Root1 = " + Double.toString( root1 ) + " Root2 = " + Double.toString( root2 ) );
                double bestPick = findBestRoot( root1, root2 );
                return BigInteger.valueOf( Math.round( bestPick ) );
            } else {
                warn( "Polynomial Calibrator for " +
                      itemName_ +
                      " contains exponent power as high as " +
                      Long.toString( maxExponent ) +
                      ".  Not supported by this toolkit." );
                return BigInteger.ZERO;
            }
        }

        SplineCalibrator splineCal = defCal_.getSplineCalibrator();
        if ( splineCal != null ) {
            long    interpolateOrder = splineCal.getOrder().longValue();
            boolean extrapolate      = splineCal.isExtrapolate();
            List<SplinePointType> points = splineCal.getSplinePoint();
            ArrayList<BigDecimal> calList = new ArrayList<>();
            ArrayList<BigDecimal> rawList = new ArrayList<>();
            for ( SplinePointType point : points ) {
                calList.add( new BigDecimal( point.getCalibrated() ) );
                rawList.add( new BigDecimal( point.getRaw() ) );
            }
            BigDecimal minCalValue = calList.get( 0 );
            BigDecimal maxCalValue = calList.get( calList.size() - 1);
            for ( BigDecimal cal : calList ) {
                if ( cal.min( minCalValue ) == cal ) {
                    minCalValue = cal;
                }
                if ( cal.max( maxCalValue ) == cal ) {
                    maxCalValue = cal;
                }
            }
            if ( extrapolate == false ) {
                if ( ( calValue.compareTo( minCalValue ) < 0 ) ||
                     ( calValue.compareTo( maxCalValue ) > 0 ) ) {
                    warn( "Spline Calibrator for " +
                          itemName_ +
                          " does not bound calibrated value " +
                          calValue.toString() +
                          " and extrapolate is false" );
                    return BigInteger.ZERO;
                }
            }
            BigDecimal rawValue1 = null;
            BigDecimal rawValue2 = null;
            BigDecimal calValue1 = null;
            BigDecimal calValue2 = null;
            Iterator<BigDecimal> calitr = calList.iterator();
            Iterator<BigDecimal> rawitr = rawList.iterator();
            if ( calitr.hasNext() == true ) {
                calValue1 = calitr.next();
                rawValue1 = rawitr.next();
            }
            while ( calitr.hasNext() == true ) {
                if ( calValue2 != null ) {
                    calValue1 = calValue2;
                    rawValue1 = rawValue2;
                }
                calValue2 = calitr.next();
                rawValue2 = rawitr.next();
                //System.out.println( "Cals: cal1 = " + calValue1.toString() +
                //                    " cal2 = " + calValue2.toString() );
                if ( ( calValue1.compareTo( calValue ) <= 0 ) &&
                     ( calValue2.compareTo( calValue ) >= 0 ) ) {
                    if ( calValue.equals( calValue1 ) == true ) {
                        return rawValue1.toBigInteger();
                    } else if ( calValue.equals( calValue2 ) == true ) {
                        return rawValue2.toBigInteger();
                    }
                    break;
                }
            }
            if ( rawValue1 == null || rawValue2 == null ) {
                warn( "Spline Calibrator for " +
                      itemName_ +
                      " does not bound calibrated value " +
                      calValue.toString() );
                return BigInteger.ZERO;
            }
            //System.out.println( calValue.toString() +
            //                    " Order = " + Long.toString( interpolateOrder ) +
            //                    " y2 = " + calValue2.toString() +
            //                    " y1 = " + calValue1.toString() +
            //                    " x2 = " + rawValue2.toString() +
            //                    " x1 = " + rawValue2.toString() );
            double y2 = calValue2.doubleValue();
            double y1 = calValue1.doubleValue();
            double x2 = rawValue2.doubleValue();
            double x1 = rawValue1.doubleValue();
            if ( interpolateOrder == 0 ) {
                return new BigDecimal( ( x1 + x2 ) / 2.0 ).toBigInteger();
            } else if ( interpolateOrder == 1 ) {
                double slope = ( y2 - y1 ) / ( x2 - x1 );
                //System.out.println( "Slope = " + Double.toString( slope ) );
                double rawValue = ( calValue.doubleValue() - y1 ) / slope + x1;
                //System.out.println( "Raw = " + Double.toString( rawValue ) );
                return new BigDecimal( rawValue ).toBigInteger();
            } else {
                warn( "Spline Calibrator for " +
                      itemName_ +
                      " contains interpolate order of " +
                      Long.toString( interpolateOrder ) +
                      ".  Not supported by this toolkit." );
                return BigInteger.ZERO;
            }
        }

        MathOperationCalibrator mathCal = defCal_.getMathOperationCalibrator();
        if ( mathCal != null ) {
            warn( "MathOperationCalibrator for " +
                  itemName_ +
                  " not supported" );
            return BigInteger.ZERO;
        }

        long value = calValue.longValue();
        return new BigInteger( Long.toString( value ) );

    }

    private BigInteger integerEncodingUncalibrate( final BigInteger calValue ) {

        if ( defCal_ == null ) {
            return calValue;
        }

        // TODO customize this version of the function later
        return integerEncodingUncalibrate( new BigDecimal( calValue ) );

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
                warn( "Unsigned value for item " +
                      itemName_ +
                      " is " +
                      rawValue.toString() +
                      " which cannot be negative" );
                return false;
            }
        }

        // TODO: BigIntegers screw this up on long overflow!

        // check for the boundary conditions with the minimum first
        if ( minInclusive == false ) {
            if ( minValue >= rawValue.longValue() ) {
                warn( rawTypeName_ +
                      " encoding value for item " +
                      itemName_ +
                      " is " +
                      rawValue.toString() +
                      ", which is less than or equal to the minimum value " +
                      Long.toString( minValue ) );
                return false;
            }
        } else {
            if ( minValue > rawValue.longValue() ) {
                warn( rawTypeName_ +
                      " encoding value for item " +
                      itemName_ +
                      " is " +
                      rawValue.toString() +
                      ", which is less than the minimum value " +
                      Long.toString( minValue ) );
                return false;
            }
        }

        // check for the boundary conditions with the maximum
        if ( maxInclusive == false ) {
            if ( maxValue <= rawValue.longValue() ) {
                warn( rawTypeName_ +
                      " encoding value for item " +
                      itemName_ +
                      " is " +
                      rawValue.toString() +
                      ", which is greater than or equal to the maximum value " +
                      Long.toString( maxValue ) );
                return false;
            }
        } else {
            if ( maxValue < rawValue.longValue() ) {
                warn( rawTypeName_ +
                      " encoding value for item " +
                      itemName_ +
                      " is " +
                      rawValue.toString() +
                      ", which is greater than the maximum value " +
                      Long.toString( maxValue ) );
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
                warn( rawTypeName_ +
                      " encoding value for item " +
                      itemName_ +
                      " is " +
                      Double.toString( rawValue ) +
                      ", which is less than or equal to the minimum value " +
                      Double.toString( minValue ) );
                return false;
            }
        } else {
            if ( minValue > rawValue ) {
                warn( rawTypeName_ +
                      " encoding value for item " +
                      itemName_ +
                      " is " +
                      Double.toString( rawValue ) +
                      ", which is less than the minimum value " +
                      Double.toString( minValue ) );
                return false;
            }
        }

        // check for the boundary conditions with the maximum
        if ( maxInclusive == false ) {
            if ( maxValue <= rawValue ) {
                warn( rawTypeName_ +
                      " encoding value for item " +
                      itemName_ +
                      " is " +
                      Double.toString( rawValue ) +
                      ", which is greater than or equal to the maximum value " +
                      Double.toString( maxValue ) );
                return false;
            }
        } else {
            if ( maxValue < rawValue ) {
                warn( rawTypeName_ +
                      " encoding value for item " +
                      itemName_ +
                      " is " +
                      Double.toString( rawValue ) +
                      ", which is greater than the maximum value " +
                      Double.toString( maxValue ) );
                return false;
            }
        }

        return true;

    }

    private double findBestRoot( double root1, double root2 ) {

        // function is never called if BOTH are invalid since we checked the
        // discriminant earlier.
        if ( ( root1 == Double.NaN               ) ||
             ( root1 == Double.POSITIVE_INFINITY ) ||
             ( root1 == Double.NEGATIVE_INFINITY ) ) {
            root1 = root2;
        }
        if ( ( root2 == Double.NaN               ) ||
             ( root2 == Double.POSITIVE_INFINITY ) ||
             ( root2 == Double.NEGATIVE_INFINITY ) ) {
            root2 = root1;
        }

        if ( rawTypeName_ == RawType.unsigned ) {
            // TODO worry about inclusive versus exclusive
            long minValue = 0;
            long maxValue = (long)Math.pow( 2, rawSizeInBits_  ) - 1;
            if ( ( validRange_.isValidRangeApplied()  == true  ) &&
                 ( validRange_.isLowValueCalibrated() == false ) ) {
                minValue = Math.round( Double.parseDouble( validRange_.getLowValue() ) );
            }
            if ( ( validRange_.isValidRangeApplied()   == true  ) &&
                 ( validRange_.isHighValueCalibrated() == false ) ) {
                maxValue = Math.round( Double.parseDouble( validRange_.getHighValue() ) );
            }
            if ( ( root1 >= minValue ) && ( root1 <= maxValue ) ) {
                return root1;
            } else if ( ( root2 >= minValue ) && ( root2 <= maxValue ) ) {
                return root2;
            } else {
                warn( "Polynomial Calibrator for " +
                      itemName_ +
                      " contains roots of " +
                      Double.toString( root1 ) +
                      " and " +
                      Double.toString( root2 ) +
                      ", neither of which are in the range of " +
                      Long.toString( minValue ) +
                      " to " +
                      Long.toString( maxValue ) +
                      " for encoding " +
                      rawTypeName_ );
                return 0.0;
            }
        } else if ( ( rawTypeName_ == RawType.signMagnitude  ) ||
                    ( rawTypeName_ == RawType.twosComplement ) ||
                    ( rawTypeName_ == RawType.onesComplement ) ) {
            // TODO worry about inclusive versus exclusive
            long minValue = -1 * (long)Math.pow( 2, ( rawSizeInBits_ - 1 ) );
            long maxValue = (long)Math.pow( 2, ( rawSizeInBits_ - 1 ) ) - 1;
            if ( ( validRange_.isValidRangeApplied()  == true  ) &&
                 ( validRange_.isLowValueCalibrated() == false ) ) {
                minValue = Math.round( Double.parseDouble( validRange_.getLowValue() ) );
            }
            if ( ( validRange_.isValidRangeApplied()   == true  ) &&
                 ( validRange_.isHighValueCalibrated() == false ) ) {
                maxValue = Math.round( Double.parseDouble( validRange_.getHighValue() ) );
            }
            if ( ( root1 >= minValue ) && ( root1 <= maxValue ) ) {
                return root1;
            } else if ( ( root2 >= minValue ) && ( root2 <= maxValue ) ) {
                return root2;
            } else {
                warn( "Polynomial Calibrator for " +
                      itemName_ +
                      " contains roots of " +
                      Double.toString( root1 ) +
                      " and " +
                      Double.toString( root2 ) +
                      ", neither of which are in the range of " +
                      Long.toString( minValue ) +
                      " to " +
                      Long.toString( maxValue ) +
                      " for encoding " +
                      rawTypeName_ );
                return 0.0;
            }
        }
        return root1;

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
