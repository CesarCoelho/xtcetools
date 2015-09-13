/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    public XTCEItemValue( XTCETypedObject item ) {

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
            warnings_.add( "No type defined for " + itemName_ +
                           ", uncalibration may be possible, but not raw." );
            validObject_ = false;
            return;
        }

        // gather the raw encoding size, which may not be appropriate
        try {
            rawSizeInBits_ = Integer.parseInt( item.getRawSizeInBits() );
            validObject_   = true;
        } catch ( NumberFormatException ex ) {
            warnings_.add( "Cannot encode/decode typed item " +
                           itemName_ +
                           " without a numeric raw size in bits.  Size is '" +
                           rawSizeInBits_ +
                           "'" );
            validObject_ = false;
        }

        // get the registered time handler if one exists
        if ( euTypeName_.equals( "TIME" ) == true ) {
            try {
                AbsoluteTimeDataType timeXml =
                    (AbsoluteTimeDataType)itemObj_.getTypeReference();
                timeHandler_ =
                    XTCEFunctions.getAbsoluteTimeHandler( timeXml );
            } catch ( XTCEDatabaseException ex ) {
                warnings_.add( itemName_ + " " + ex.getLocalizedMessage() );
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

    public String getItemName() {
        return itemName_;
    }

    /** Retrieves the validity flag from this item value encode/decode.
     *
     * @return boolean indicating if the encode/decode can be performed.  If
     * this is false, the encode/decode functions will throw an exception.
     *
     */

    public boolean isValid() {
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

    public List<String> getWarnings() {
        return warnings_;
    }

    /** Clears the list of warning messages for this object, which can be
     * useful if multiple raw or engineering values need to be generated, so
     * that the list does not include previous issues.
     *
     */

    public void clearWarnings() {
        warnings_ = new ArrayList<>();
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

    public String decode( BitSet rawValue ) {

        String uncalValue = getUncalibratedFromRaw( rawValue );
        if ( getWarnings().isEmpty() == false ) {
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

    public String getUncalibratedFromRaw( BitSet rawValue ) {

        clearWarnings();

        String rawTypeName = rawTypeName_;
        String rawBitOrder = rawBitOrder_;

        if ( rawBitOrder.equals( "mostSignificantBitFirst" ) == false ) {
            warnings_.add( itemName_ + " Raw encoding " + rawBitOrder +
                " not yet supported" );
            return "";
        }

        if ( euTypeName_.equals( "TIME" ) == true ) {
            if ( timeHandler_ != null ) {
                return timeHandler_.getUncalibratedFromRaw( rawValue );
            } else {
                return "0x00";
            }
        }

        BigInteger numericValue = bitSetToNumber( rawValue );

        if ( rawTypeName.equals( "unsigned" ) == true ) {
            return numericValue.toString();
        } else if ( rawTypeName.equals( "signMagnitude" ) == true ) {
            int sizeInBits = rawSizeInBits_;
            BigInteger halfValue = BigInteger.valueOf( 2 ).pow( sizeInBits - 1 );
            if ( numericValue.compareTo( halfValue ) >= 0 ) {
                numericValue = numericValue.subtract( halfValue ).negate();
            }
            return numericValue.toString();
        } else if ( rawTypeName.equals( "twosComplement" ) == true ) {
            int sizeInBits = rawSizeInBits_;
            BigInteger halfValue = BigInteger.valueOf( 2 ).pow( sizeInBits - 1 );
            BigInteger fullValue = BigInteger.valueOf( 2 ).pow( sizeInBits );
            if ( numericValue.compareTo( halfValue ) >= 0 ) {
                numericValue = numericValue.subtract( fullValue );
            }
            return numericValue.toString();
        } else if ( rawTypeName.equals( "onesComplement" ) == true ) {
            int sizeInBits = rawSizeInBits_;
            BigInteger halfValue = BigInteger.valueOf( 2 ).pow( sizeInBits - 1 );
            BigInteger fullValue = BigInteger.valueOf( 2 ).pow( sizeInBits );
            if ( numericValue.compareTo( halfValue ) >= 0 ) {
                numericValue = numericValue.subtract( fullValue ).add( BigInteger.ONE );
            }
            return numericValue.toString();
        } else if ( rawTypeName.equals( "IEEE754_1985" ) == true ) {
            int sizeInBits = rawSizeInBits_;
            if ( sizeInBits == 32 ) {
                Float floatValue = Float.intBitsToFloat( numericValue.intValue() );
                return floatValue.toString();
            } else if ( sizeInBits == 64 ) {
                Double doubleValue = Double.longBitsToDouble( numericValue.longValue() );
                return doubleValue.toString();
            } else {
                warnings_.add( itemName_ + " Raw encoding " +
                    rawTypeName + " using " +
                    Integer.toString( rawSizeInBits_ ) + " bits " +
                    "is not yet supported" );
                return "";
            }
        } else if ( rawTypeName.equals( "binary" ) == true ) {
            return "0x" + numericValue.toString( 16 );
        } else if ( rawTypeName.equals( "UTF-8" ) == true ) {
            String retValue = new String( numericValue.toByteArray(),
                                          Charset.forName( "UTF-8" ) );
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
        } else if ( rawTypeName.equals( "UTF-16" ) == true ) {
            String retValue = new String( numericValue.toByteArray(),
                                          Charset.forName( "UTF-16" ) );
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

        // not supported MILSTD_1750A, BCD, packedBCD
        warnings_.add( itemName_ + " Raw encoding type " + rawTypeName +
            " not yet supported" );

        return "";

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

    public String getCalibratedFromUncalibrated( String uncalValue ) {

        clearWarnings();

        String rawTypeName = rawTypeName_;
        String engTypeName = euTypeName_;
        String calValue    = applyCalibrator( uncalValue );

        if ( ( engTypeName.equals( "UNSIGNED" ) == true ) ||
             ( engTypeName.equals( "SIGNED" )   == true ) ) {
            return getCalibratedFromIntegerString( calValue );
        } else if ( ( engTypeName.equals( "FLOAT32" )  == true ) ||
                    ( engTypeName.equals( "FLOAT64" )  == true ) ||
                    ( engTypeName.equals( "FLOAT128" ) == true ) ) {
            return getCalibratedFromFloatString( calValue );
        } else if ( engTypeName.equals( "BOOLEAN" ) == true ) {
            return getCalibratedFromBooleanNumericString( calValue );
        } else if ( engTypeName.equals( "ENUMERATED" ) == true ) {
            return getCalibratedValueFromEnumeratedNumericString( calValue );
        } else if ( engTypeName.equals( "STRING" ) == true ) {
            return calValue;
        } else if ( engTypeName.equals( "BINARY" ) == true ) {
            // warnings for binary transformations?
            // might need 0x protection over entire function
            if ( calValue.startsWith( "0x" ) == true ) {
                BigInteger intValue = new BigInteger( calValue.replaceFirst( "0x", "" ), 16 );
                return "0x" + intValue.toString( 16 );
            } else {
                BigInteger intValue = new BigInteger( calValue );
                return "0x" + intValue.toString( 16 );
            }
        } else if ( engTypeName.equals( "TIME" ) == true ) {
            try {
                if ( timeHandler_ != null ) {
                    return timeHandler_.getCalibratedFromUncalibrated( uncalValue );
                }
            } catch ( Exception ex ) {
                warnings_.add( itemName_ + ex.getLocalizedMessage() );
            }
        } else if ( engTypeName.equals( "DURATION" ) == true ) {
            warnings_.add( itemName_ + " Relative Time Type is not " +
                "yet supported" );
        } else {
            warnings_.add( itemName_ + " Type '" + engTypeName +
                "' is not yet supported" );
        }

        return "";

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

    public BitSet encode( String euValue ) {

        BigInteger integerTempValue  = null;
        BigInteger uncalNumericValue = null;
        BitSet     rawValue          = new BitSet( rawSizeInBits_ );

        String uncalValue = getUncalibratedFromCalibrated( euValue );
        return getRawFromUncalibrated( uncalValue );

/*
        if ( euTypeName_.equals( "BOOLEAN" ) == true ) {
            // two steps, first get the integral value from the boolean type
            // EU value and then apply uncalibration from the encoding element
            integerTempValue  = integerFromBooleanType( euValue );
            uncalNumericValue = encodeInteger( integerTempValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "ENUMERATED" ) == true ) {
            // two steps, first get the integral value from the enumerated type
            // EU value and then apply uncalibration from the encoding element
            integerTempValue  = integerFromEnumerationType( euValue );
            uncalNumericValue = encodeInteger( integerTempValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "STRING" ) == true ) {
            uncalNumericValue = encodeString( euValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "BINARY" ) == true ) {
            uncalNumericValue = encodeString( euValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.startsWith( "FLOAT" ) == true ) {
            uncalNumericValue = encodeString( euValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "UNSIGNED" ) == true ) {
            uncalNumericValue = encodeString( euValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "SIGNED" ) == true ) {
            uncalNumericValue = encodeString( euValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "TIME" ) == true ) {
            // TODO Add TIME Type
            warnings_.add( "Absolute Time Type Not Yet Supported for " +
                           itemName_ );
        } else if ( euTypeName_.equals( "DURATION" ) == true ) {
            // TODO Add DURATION Type
            warnings_.add( "Relative Time Type Not Yet Supported for " +
                           itemName_ );
        } else {
            warnings_.add( "AGGREGATE and ARRAY types for item " +
                           itemName_ +
                           " cannot directly be encoded." + 
                           "  Use their children instead" );
        }

        return rawValue;
*/
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

    public BitSet encode( long euValue ) {

        BigInteger integerTempValue  = null;
        BigInteger uncalNumericValue = null;
        BitSet     rawValue          = new BitSet( rawSizeInBits_ );

        String uncalValue = getUncalibratedFromCalibrated( euValue );
        return getRawFromUncalibrated( uncalValue );

/*
        if ( euTypeName_.equals( "BOOLEAN" ) == true ) {
            // two steps, first get the integral value from the boolean type
            // EU value and then apply uncalibration from the encoding element
            integerTempValue  = integerFromBooleanType( Long.toString( euValue ) );
            uncalNumericValue = encodeInteger( integerTempValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "ENUMERATED" ) == true ) {
            // two steps, first get the integral value from the enumerated type
            // EU value and then apply uncalibration from the encoding element
            integerTempValue  = integerFromEnumerationType( Long.toString( euValue ) );
            uncalNumericValue = encodeInteger( integerTempValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "STRING" ) == true ) {
            uncalNumericValue = encodeInteger( BigInteger.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "BINARY" ) == true ) {
            uncalNumericValue = encodeInteger( BigInteger.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.startsWith( "FLOAT" ) == true ) {
            uncalNumericValue = encodeInteger( BigInteger.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "UNSIGNED" ) == true ) {
            uncalNumericValue = encodeInteger( BigInteger.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "SIGNED" ) == true ) {
            uncalNumericValue = encodeInteger( BigInteger.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "TIME" ) == true ) {
            // TODO Add TIME Type
            warnings_.add( "Absolute Time Type Not Yet Supported for " +
                           itemName_ );
        } else if ( euTypeName_.equals( "DURATION" ) == true ) {
            // TODO Add DURATION Type
            warnings_.add( "Relative Time Type Not Yet Supported for " +
                           itemName_ );
        } else {
            warnings_.add( "AGGREGATE and ARRAY types for item " +
                           itemName_ +
                           " cannot directly be encoded." + 
                           "  Use their children instead" );
        }

        return rawValue;
*/
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

    public BitSet encode( double euValue ) {

        BigInteger integerTempValue  = null;
        BigInteger uncalNumericValue = null;
        BitSet     rawValue          = new BitSet( rawSizeInBits_ );

        String uncalValue = getUncalibratedFromCalibrated( euValue );
        return getRawFromUncalibrated( uncalValue );

/*
        if ( euTypeName_.equals( "BOOLEAN" ) == true ) {
            // two steps, first get the integral value from the boolean type
            // EU value and then apply uncalibration from the encoding element
            // TODO: the boolean string values can be anything, so check this
            // for suitability.
            long numericValue = Double.valueOf( euValue ).longValue();
            integerTempValue  = integerFromBooleanType( Long.toString( numericValue ) );
            uncalNumericValue = encodeInteger( integerTempValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "ENUMERATED" ) == true ) {
            // two steps, first get the integral value from the enumerated type
            // EU value and then apply uncalibration from the encoding element
            // TODO: the boolean string values can be anything, so check this
            // for suitability.
            integerTempValue  = integerFromEnumerationType( Double.toString( euValue ) );
            uncalNumericValue = encodeInteger( integerTempValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "STRING" ) == true ) {
            uncalNumericValue = encodeDecimal( BigDecimal.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "BINARY" ) == true ) {
            warnings_.add( itemName_ +
                           " Invalid EU Binary value of '" +
                           Double.toString( euValue ) +
                           "'" );
            rawValue = new BitSet( rawSizeInBits_ );
        } else if ( euTypeName_.startsWith( "FLOAT" ) == true ) {
            uncalNumericValue = encodeDecimal( BigDecimal.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "UNSIGNED" ) == true ) {
            if ( ( euValue % 1 ) != 0 ) {
                warnings_.add( itemName_ +
                               " Invalid EU unsigned integer value of '" +
                               Double.toString( euValue ) +
                               "'" );
                rawValue = new BitSet( rawSizeInBits_ );
                return rawValue;
            }
            long number = Double.valueOf( euValue ).longValue();
            uncalNumericValue = encodeInteger( BigInteger.valueOf( number ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "SIGNED" ) == true ) {
            if ( ( euValue % 1 ) != 0 ) {
                warnings_.add( itemName_ +
                               " Invalid EU signed integer value of '" +
                               Double.toString( euValue ) +
                               "'" );
                rawValue = new BitSet( rawSizeInBits_ );
                return rawValue;
            }
            long number = Double.valueOf( euValue ).longValue();
            uncalNumericValue = encodeInteger( BigInteger.valueOf( number ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "TIME" ) == true ) {
            // TODO Add TIME Type
            warnings_.add( "Absolute Time Type Not Yet Supported for " +
                           itemName_ );
        } else if ( euTypeName_.equals( "DURATION" ) == true ) {
            // TODO Add DURATION Type
            warnings_.add( "Relative Time Type Not Yet Supported for " +
                           itemName_ );
        } else {
            warnings_.add( "AGGREGATE and ARRAY types for item " +
                           itemName_ +
                           " cannot directly be encoded." + 
                           "  Use their children instead" );
        }

        return rawValue;
*/
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

    public BitSet encode( float euValue ) {

        BigInteger integerTempValue  = null;
        BigInteger uncalNumericValue = null;
        BitSet     rawValue          = new BitSet( rawSizeInBits_ );

        String uncalValue = getUncalibratedFromCalibrated( euValue );
        return getRawFromUncalibrated( uncalValue );

/*
        if ( euTypeName_.equals( "BOOLEAN" ) == true ) {
            // two steps, first get the integral value from the boolean type
            // EU value and then apply uncalibration from the encoding element
            // TODO: the boolean string values can be anything, so check this
            // for suitability.
            long numericValue = Float.valueOf( euValue ).longValue();
            integerTempValue  = integerFromBooleanType( Long.toString( numericValue ) );
            uncalNumericValue = encodeInteger( integerTempValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "ENUMERATED" ) == true ) {
            // two steps, first get the integral value from the enumerated type
            // EU value and then apply uncalibration from the encoding element
            // TODO: the boolean string values can be anything, so check this
            // for suitability.
            integerTempValue  = integerFromEnumerationType( Float.toString( euValue ) );
            uncalNumericValue = encodeInteger( integerTempValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "STRING" ) == true ) {
            uncalNumericValue = encodeDecimal( BigDecimal.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "BINARY" ) == true ) {
            warnings_.add( itemName_ +
                           " Invalid EU Binary value of '" +
                           Double.toString( euValue ) +
                           "'" );
            rawValue = new BitSet( rawSizeInBits_ );
        } else if ( euTypeName_.startsWith( "FLOAT" ) == true ) {
            uncalNumericValue = encodeDecimal( BigDecimal.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "UNSIGNED" ) == true ) {
            if ( ( euValue % 1 ) != 0 ) {
                warnings_.add( itemName_ +
                               " Invalid EU unsigned integer value of '" +
                               Float.toString( euValue ) +
                               "'" );
                rawValue = new BitSet( rawSizeInBits_ );
                return rawValue;
            }
            long number = Float.valueOf( euValue ).longValue();
            uncalNumericValue = encodeInteger( BigInteger.valueOf( number ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "SIGNED" ) == true ) {
            if ( ( euValue % 1 ) != 0 ) {
                warnings_.add( itemName_ +
                               " Invalid EU signed integer value of '" +
                               Float.toString( euValue ) +
                               "'" );
                rawValue = new BitSet( rawSizeInBits_ );
                return rawValue;
            }
            long number = Float.valueOf( euValue ).longValue();
            uncalNumericValue = encodeInteger( BigInteger.valueOf( number ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "TIME" ) == true ) {
            // TODO Add TIME Type
            warnings_.add( "Absolute Time Type Not Yet Supported for " +
                           itemName_ );
        } else if ( euTypeName_.equals( "DURATION" ) == true ) {
            // TODO Add DURATION Type
            warnings_.add( "Relative Time Type Not Yet Supported for " +
                           itemName_ );
        } else {
            warnings_.add( "AGGREGATE and ARRAY types for item " +
                           itemName_ +
                           " cannot directly be encoded." + 
                           "  Use their children instead" );
        }

        return rawValue;
*/
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

    public BitSet getRawFromUncalibrated( String uncalValue ) {

        // TODO Handle Byte order element ByteOrderList in the encoding

        BigInteger intValue = BigInteger.ZERO;

        try {

            if ( euTypeName_.equals( "TIME" ) == true ) {
                if ( timeHandler_ != null ) {
                    return timeHandler_.getRawFromUncalibrated( uncalValue );
                } else {
                    return new BitSet( rawSizeInBits_ );
                }
            } else if ( ( rawTypeName_.equals( "unsigned" )       == true ) ||
                        ( rawTypeName_.equals( "signMagnitude" )  == true ) ||
                        ( rawTypeName_.equals( "twosComplement" ) == true ) ||
                        ( rawTypeName_.equals( "onesComplement" ) == true ) ) {
                String lowerCalValue = uncalValue.toLowerCase();
                if ( lowerCalValue.startsWith( "0x" ) == true ) {
                    intValue = new BigInteger( lowerCalValue.replaceFirst( "0x", "" ), 16 );
                    return getRawFromUncalibrated( intValue );
                } else {
                    intValue = new BigDecimal( lowerCalValue ).toBigIntegerExact();
                    return getRawFromUncalibrated( intValue );
                }
            } else if ( rawTypeName_.equals( "binary" ) == true ) {
                // need to know EU type here!
                if ( uncalValue.contains( "-" ) == true ) {
                    warnings_.add( itemName_ + " Invalid value for binary " +
                        "encoding '" + uncalValue + "', negative has no " +
                        "meaning in a binary context." );
                } else {
                    String lowerCalValue = uncalValue.toLowerCase();
                    intValue = integerStringToBigInteger( lowerCalValue );
                }
            } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {
                String reasCalValue = uncalValue.toLowerCase();
                if ( reasCalValue.startsWith( "0x" ) == true ) {
                    BigInteger temp = new BigInteger( reasCalValue.replaceFirst( "0x", "" ), 16 );
                    return getRawFromUncalibrated( new BigDecimal( temp ) );
                } else {
                    return getRawFromUncalibrated( new BigDecimal( reasCalValue ) );
                }
            } else if ( rawTypeName_.equals( "MILSTD_1750A" ) == true ) {
                warnings_.add( "Unsupported encoding type for " +
                               itemName_ +
                               " Encoding: " +
                               rawTypeName_ );
            } else if ( rawTypeName_.equals( "UTF-8" ) == true ) {
                BigInteger retValue = BigInteger.ZERO;
                if ( uncalValue.isEmpty() == false ) {
                    retValue = new BigInteger( uncalValue.getBytes( StandardCharsets.UTF_8 ) );
                }
                intValue = encodeUtfString( retValue );
            } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {
                BigInteger retValue = BigInteger.ZERO;
                if ( uncalValue.isEmpty() == false ) {
                    retValue = new BigInteger( uncalValue.getBytes( StandardCharsets.UTF_16 ) );
                }
                if ( retValue.equals( BigInteger.ZERO ) == true ) {
                    retValue = new BigInteger( utf16_ );
                }
                intValue = encodeUtfString( retValue );
            } else if ( euTypeName_.equals( "DURATION" ) == true ) {
                // TODO Add DURATION Type
                warnings_.add( "Relative Time Type Not Yet Supported for " +
                               itemName_ );
            } else {
                warnings_.add( "AGGREGATE and ARRAY types for item " +
                               itemName_ +
                               " cannot directly be encoded." + 
                               "  Use their children instead" );
            }

        } catch ( NumberFormatException ex ) {
            warnings_.add( itemName_ +
                           " Invalid String value for encoding " +
                           rawTypeName_ +
                           " of '" +
                           uncalValue +
                           "'" );
        } catch ( ArithmeticException ex ) {
            warnings_.add( itemName_ +
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

        if ( euTypeName_.equals( "TIME" ) == true ) {
            if ( timeHandler_ != null ) {
                return timeHandler_.getRawFromUncalibrated( uncalValue.toString() );
            } else {
                return new BitSet( rawSizeInBits_ );
            }
        }

        if ( rawTypeName_.equals( "unsigned" ) == true ) {
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ );
            if ( uncalValue.compareTo( max ) == 1 ) {
                warnings_.add( itemName_ + " overflow value '" + uncalValue +
                    "', larger than available encoding bits." );
                uncalValue = BigInteger.ZERO;
            } else if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                uncalValue = BigInteger.ZERO;
            }
        } else if ( rawTypeName_.equals( "signMagnitude" ) == true ) {
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ - 1 );
            BigInteger min = max.negate();
            if ( uncalValue.compareTo( max ) == 1 ) {
                warnings_.add( itemName_ + " overflow value '" + uncalValue +
                    "', larger than maximum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( uncalValue.compareTo (min ) == -1 ) {
                warnings_.add( itemName_ + " overflow value '" + uncalValue +
                    "', smaller than minimum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                uncalValue = BigInteger.ZERO;
            }
            if ( uncalValue.compareTo( BigInteger.ZERO ) < 0 ) {
                uncalValue = uncalValue.negate();
                uncalValue = uncalValue.setBit( rawSizeInBits_ - 1 );
            }
        } else if ( rawTypeName_.equals( "twosComplement" ) == true ) {
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ - 1 );
            BigInteger min = max.negate();
            if ( uncalValue.compareTo( max ) == 1 ) {
                warnings_.add( itemName_ + " overflow value '" + uncalValue +
                    "', larger than maximum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( uncalValue.compareTo (min ) == -1 ) {
                warnings_.add( itemName_ + " overflow value '" + uncalValue +
                    "', smaller than minimum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                uncalValue = BigInteger.ZERO;
            }
        } else if ( rawTypeName_.equals( "onesComplement" ) == true ) {
            BigInteger max = new BigInteger( "2" ).pow( rawSizeInBits_ - 1 );
            BigInteger min = max.negate();
            if ( uncalValue.compareTo( max ) == 1 ) {
                warnings_.add( itemName_ + " overflow value '" + uncalValue +
                    "', larger than maximum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( uncalValue.compareTo (min ) == -1 ) {
                warnings_.add( itemName_ + " overflow value '" + uncalValue +
                    "', smaller than minimum value for encoding." );
                uncalValue = BigInteger.ZERO;
            } else if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                uncalValue = BigInteger.ZERO;
            }
            if ( uncalValue.compareTo( BigInteger.ZERO ) < 0 ) {
                uncalValue = uncalValue.subtract( BigInteger.ONE );
            }
        } else if ( rawTypeName_.equals( "binary" ) == true ) {
            // do nothing
        } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                uncalValue = BigInteger.ZERO;
            } else {
                if ( rawSizeInBits_ == 32 ) {
                    uncalValue = BigInteger.valueOf( Float.floatToRawIntBits( uncalValue.floatValue() ) );
                } else if ( rawSizeInBits_ == 64 ) {
                    uncalValue = BigInteger.valueOf( Double.doubleToRawLongBits( uncalValue.doubleValue() ) );
                } else if ( rawSizeInBits_ == 128 ) {
                    warnings_.add( "Unsupported encoding type for " +
                                   itemName_ +
                                   " Encoding: " +
                                   rawTypeName_ );
                }
            }
        } else if ( rawTypeName_.equals( "MILSTD_1750A" ) == true ) {
            warnings_.add( "Unsupported encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );
            uncalValue = BigInteger.ZERO;
        } else if ( rawTypeName_.equals( "UTF-8" ) == true ) {
            String chars = uncalValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_8 ) );
            uncalValue = encodeUtfString( retValue );
        } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {
            String chars = uncalValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_16 ) );
            if ( retValue.equals( BigInteger.ZERO ) == true ) {
                retValue = new BigInteger( utf16_ );
            }
            uncalValue = encodeUtfString( retValue );
        } else if ( euTypeName_.equals( "TIME" ) == true ) {
            // TODO Add TIME Type
            warnings_.add( "Absolute Time Type Not Yet Supported for " +
                           itemName_ );
            uncalValue = BigInteger.ZERO;
        } else if ( euTypeName_.equals( "DURATION" ) == true ) {
            // TODO Add DURATION Type
            warnings_.add( "Relative Time Type Not Yet Supported for " +
                           itemName_ );
            uncalValue = BigInteger.ZERO;
        } else {
            warnings_.add( "AGGREGATE and ARRAY types for item " +
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

    public BitSet getRawFromUncalibrated( long uncalValue ) {

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

    public BitSet getRawFromUncalibrated( BigDecimal uncalValue ) {

        // TODO Handle Byte order element ByteOrderList in the encoding

        BigInteger intValue = BigInteger.ZERO;

        if ( ( rawTypeName_.equals( "unsigned" )       == true ) ||
             ( rawTypeName_.equals( "signMagnitude" )  == true ) ||
             ( rawTypeName_.equals( "twosComplement" ) == true ) ||
             ( rawTypeName_.equals( "onesComplement" ) == true ) )   {
            try {
                return getRawFromUncalibrated( uncalValue.toBigIntegerExact() );
            } catch ( NumberFormatException ex ) {
                warnings_.add( itemName_ +
                               " Invalid Decimal value for encoding " +
                               rawTypeName_ +
                               " of '" +
                               uncalValue +
                               "'" );
            }
        } else if ( rawTypeName_.equals( "binary" ) == true ) {
            warnings_.add( "Unsupported encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );
        } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                intValue = BigInteger.ZERO;
            } else {
                if ( rawSizeInBits_ == 32 ) {
                    intValue = BigInteger.valueOf( Float.floatToRawIntBits( uncalValue.floatValue() ) );
                } else if ( rawSizeInBits_ == 64 ) {
                    intValue = BigInteger.valueOf( Double.doubleToRawLongBits( uncalValue.doubleValue() ) );
                } else if ( rawSizeInBits_ == 128 ) {
                    warnings_.add( "Unsupported encoding type for " +
                                   itemName_ +
                                   " Encoding: " +
                                   rawTypeName_ );
                }
            }
        } else if ( rawTypeName_.equals( "MILSTD_1750A" ) == true ) {
            warnings_.add( "Unsupported encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );
        } else if ( rawTypeName_.equals( "UTF-8" ) == true ) {
            String chars = uncalValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_8 ) );
            intValue = encodeUtfString( retValue );
        } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {
            String chars = uncalValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_16 ) );
            if ( retValue.equals( BigInteger.ZERO ) == true ) {
                retValue = new BigInteger( utf16_ );
            }
            intValue = encodeUtfString( retValue );
        } else if ( euTypeName_.equals( "TIME" ) == true ) {
            // TODO Add TIME Type
            warnings_.add( "Absolute Time Type Not Yet Supported for " +
                           itemName_ );
        } else if ( euTypeName_.equals( "DURATION" ) == true ) {
            // TODO Add DURATION Type
            warnings_.add( "Relative Time Type Not Yet Supported for " +
                           itemName_ );
        } else {
            warnings_.add( "AGGREGATE and ARRAY types for item " +
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

    public BitSet getRawFromUncalibrated( double uncalValue ) {

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

    public BitSet getRawFromUncalibrated( float uncalValue ) {

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

    public String getUncalibratedFromCalibrated( String euValue ) {

        if ( euTypeName_.equals( "BOOLEAN" ) == true ) {
            BigInteger integerValue = integerFromBooleanType( euValue );
            return uncalibrateIntegerType( integerValue );
        } else if ( euTypeName_.equals( "ENUMERATED" ) == true ) {
            BigInteger integerValue = integerFromEnumerationType( euValue );
            return uncalibrateIntegerType( integerValue );
        } else if ( euTypeName_.equals( "STRING" ) == true ) {
            return uncalibrateStringType( euValue );
        } else if ( euTypeName_.equals( "BINARY" ) == true ) {
            return "0x" + integerStringToBigInteger( euValue ).toString( 16 );
        } else if ( euTypeName_.startsWith( "FLOAT" ) == true ) {
            try {
                if ( euValue.startsWith( "0x" ) == true ) {
                    BigInteger intValue = new BigInteger( euValue.replaceFirst( "0x", "" ), 16 );
                    return uncalibrateFloatType( new BigDecimal( intValue ) );
                }
                if ( euValue.startsWith( "0X" ) == true ) {
                    BigInteger intValue = new BigInteger( euValue.replaceFirst( "0X", "" ), 16 );
                    return uncalibrateFloatType( new BigDecimal( intValue ) );
                }
                return uncalibrateFloatType( new BigDecimal( euValue ) );
            } catch ( NumberFormatException ex ) {
                warnings_.add( itemName_ +
                               " Invalid String value for uncalibrate " +
                               "IEEE754_1985 of '" +
                               euValue +
                               "'" );
            }
        } else if ( ( euTypeName_.equals( "UNSIGNED" ) == true ) ||
                    ( euTypeName_.equals( "SIGNED" )   == true ) ) {
            try {
                if ( euValue.startsWith( "0x" ) == true ) {
                    return uncalibrateIntegerType( new BigInteger( euValue.replaceFirst( "0x", "" ), 16 ) );
                }
                if ( euValue.startsWith( "0X" ) == true ) {
                    return uncalibrateIntegerType( new BigInteger( euValue.replaceFirst( "0X", "" ), 16 ) );
                }
                BigDecimal testValue = new BigDecimal( euValue );
                if ( ( testValue.doubleValue() % 1 ) != 0 ) {
                    warnings_.add( itemName_ +
                                   " Invalid Integer value for uncalibrate of '" +
                                   euValue + "'" );
                } else {
                    BigInteger intValue = testValue.toBigInteger();
                    return uncalibrateIntegerType( intValue );
                }
            } catch ( NumberFormatException ex ) {
                warnings_.add( itemName_ +
                               " Invalid Integer value for uncalibrate of '" +
                               euValue +
                               "'" );
            }
        } else if ( euTypeName_.equals( "TIME" ) == true ) {
            try {
                if ( timeHandler_ != null ) {
                    return timeHandler_.getUncalibratedFromCalibrated( euValue );
                }
            } catch ( Exception ex ) {
                warnings_.add( itemName_ + ex.getLocalizedMessage() );
            }
        } else if ( euTypeName_.equals( "DURATION" ) == true ) {
            // TODO Add DURATION Type
            warnings_.add( "Relative Time Type Not Yet Supported for " +
                           itemName_ );
        } else {
            warnings_.add( "AGGREGATE and ARRAY types for item " +
                           itemName_ +
                           " cannot directly be encoded." + 
                           "  Use their children instead" );
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

    public String getUncalibratedFromCalibrated( BigInteger euValue ) {

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

    public String getUncalibratedFromCalibrated( long euValue ) {

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

    public String getUncalibratedFromCalibrated( BigDecimal euValue ) {

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

    public String getUncalibratedFromCalibrated( double euValue ) {

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

    public String getUncalibratedFromCalibrated( float euValue ) {

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

    private BigInteger integerFromBooleanType( String euValue ) {

        if ( euValue.equals( booleanZeroString_ ) == true ) {
            return BigInteger.ZERO;
        } else if ( euValue.equals( booleanOneString_ ) == true ) {
            return BigInteger.ONE;
        } else {
            warnings_.add( itemName_ +
                           " Invalid EU Boolean value of " +
                           "'" +
                           euValue +
                           "'" );
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

    private String booleanTypeFromUncalibrated( double uncalValue ) {

        if ( ( uncalValue % 1 ) != 0 ) {
            warnings_.add( itemName_ +
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

    private String booleanTypeFromUncalibrated( long uncalValue ) {

        if ( uncalValue == 0 ) {
            return booleanZeroString_;
        } else if ( uncalValue == 1 ) {
            return booleanOneString_;
        } else {
            warnings_.add( itemName_ +
                           " Invalid raw value of " +
                           "'" +
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

    private BigInteger integerFromEnumerationType( String euValue ) {

        for ( ValueEnumerationType enumItem : enums_ ) {

            if ( enumItem.getLabel().equals( euValue ) == false ) {
                continue;
            }

            if ( enumItem.getMaxValue() != null ) {
                warnings_.add( "Enumeration label '" +
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

        warnings_.add( itemName_ +
                       " Invalid EU Enumeration value of '" +
                       euValue +
                       "'" );

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

    private String enumeratedTypeFromUncalibrated( BigInteger uncalValue ) {

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

        warnings_.add( itemName_ + " No enumeration label found for " +
                       " value of '" +
                       uncalValue.toString() +
                       "'");

        return "";

    }

    private String uncalibrateIntegerType( BigInteger calValue ) {

        if ( ( rawTypeName_.equals( "unsigned" )       == true ) ||
             ( rawTypeName_.equals( "signMagnitude" )  == true ) ||
             ( rawTypeName_.equals( "twosComplement" ) == true ) ||
             ( rawTypeName_.equals( "onesComplement" ) == true ) ) {

            BigInteger uncalValue = integerEncodingUncalibrate( calValue );
            if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                return "0";
            }
            return uncalValue.toString();

        } else if ( rawTypeName_.equals( "binary" ) == true ) {

            return calValue.toString();

        } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {

            BigDecimal uncalValue =
                floatEncodingUncalibrate( new BigDecimal( calValue ) );
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                return "0.0";
            }
            return Double.toString( uncalValue.doubleValue() );

        } else if ( rawTypeName_.equals( "MILSTD_1750A" ) == true ) {

            warnings_.add( "Unsupported encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );

        } else if ( rawTypeName_.equals( "UTF-8" ) == true ) {

            return calValue.toString();

        } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {

            return calValue.toString();

        } else {

            warnings_.add( "Unrecognized encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );

        }

        return "0";

    }

    private String uncalibrateFloatType( BigDecimal calValue ) {

        if ( ( rawTypeName_.equals( "unsigned" )       == true ) ||
             ( rawTypeName_.equals( "signMagnitude" )  == true ) ||
             ( rawTypeName_.equals( "twosComplement" ) == true ) ||
             ( rawTypeName_.equals( "onesComplement" ) == true ) ) {

            BigInteger uncalValue = integerEncodingUncalibrate( calValue );
            if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                return "0";
            }
            return uncalValue.toString();

        } else if ( rawTypeName_.equals( "binary" ) == true ) {

            return calValue.toString();

        } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {

            BigDecimal uncalValue =
                floatEncodingUncalibrate( calValue );
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                return "0.0";
            }
            return Double.toString( uncalValue.doubleValue() );

        } else if ( rawTypeName_.equals( "MILSTD_1750A" ) == true ) {

            warnings_.add( "Unsupported encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );

        } else if ( rawTypeName_.equals( "UTF-8" ) == true ) {

            return calValue.toString();

        } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {

            return calValue.toString();

        } else {

            warnings_.add( "Unrecognized encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );

        }

        return "0";

    }

    private String uncalibrateStringType( String calValue ) {

        return calValue;

    }

    private BigInteger encodeInteger( BigInteger calValue ) {

        if ( rawTypeName_.equals( "unsigned" ) == true ) {
            BigInteger uncalValue = integerEncodingUncalibrate( calValue );
            if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                return BigInteger.ZERO;
            }
            return uncalValue;
        } else if ( rawTypeName_.equals( "signMagnitude" ) == true ) {
            BigInteger uncalValue = integerEncodingUncalibrate( calValue );
            if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                return BigInteger.ZERO;
            }
            return uncalValue;
        } else if ( rawTypeName_.equals( "twosComplement" ) == true ) {
            BigInteger uncalValue = integerEncodingUncalibrate( calValue );
            if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                return BigInteger.ZERO;
            }
            return uncalValue;
        } else if ( rawTypeName_.equals( "onesComplement" ) == true ) {
            BigInteger uncalValue = integerEncodingUncalibrate( calValue );
            if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                return BigInteger.ZERO;
            }
            return uncalValue;
        } else if ( rawTypeName_.equals( "binary" ) == true ) {
            return calValue;
        } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {
            BigDecimal uncalValue = floatEncodingUncalibrate( new BigDecimal( calValue ) );
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                uncalValue = BigDecimal.ZERO;
            }
            if ( rawSizeInBits_ == 32 ) {
                return BigInteger.valueOf( Float.floatToRawIntBits( uncalValue.floatValue() ) );
            } else if ( rawSizeInBits_ == 64 ) {
                return BigInteger.valueOf( Double.doubleToRawLongBits( uncalValue.doubleValue() ) );
            } else if ( rawSizeInBits_ == 128 ) {
                warnings_.add( "Unsupported encoding type for " +
                               itemName_ +
                               " Encoding: " +
                               rawTypeName_ );
            }
        } else if ( rawTypeName_.equals( "MILSTD_1750A" ) == true ) {
            warnings_.add( "Unsupported encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );
        } else if ( rawTypeName_.equals( "UTF-8" ) == true ) {
            String chars = calValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_8 ) );
            return encodeUtfString( retValue );
        } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {
            String chars = calValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_16 ) );
            if ( retValue.equals( BigInteger.ZERO ) == true ) {
                retValue = new BigInteger( utf16_ );
            }
            return encodeUtfString( retValue );
        } else {
            warnings_.add( "Unrecognized encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );
        }

        return BigInteger.ZERO;

    }

    private BigInteger encodeDecimal( BigDecimal calValue ) {

        if ( rawTypeName_.equals( "unsigned" ) == true ) {
            BigInteger uncalValue = integerEncodingUncalibrate( calValue );
            if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                return BigInteger.ZERO;
            }
            return uncalValue;
        } else if ( rawTypeName_.equals( "signMagnitude" ) == true ) {
            BigInteger uncalValue = integerEncodingUncalibrate( calValue );
            if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                return BigInteger.ZERO;
            }
            return uncalValue;
        } else if ( rawTypeName_.equals( "twosComplement" ) == true ) {
            BigInteger uncalValue = integerEncodingUncalibrate( calValue );
            if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                return BigInteger.ZERO;
            }
            return uncalValue;
        } else if ( rawTypeName_.equals( "onesComplement" ) == true ) {
            BigInteger uncalValue = integerEncodingUncalibrate( calValue );
            if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                return BigInteger.ZERO;
            }
            return uncalValue;
        } else if ( rawTypeName_.equals( "binary" ) == true ) {
            warnings_.add( "Unsupported encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );
        } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {
            BigDecimal uncalValue = floatEncodingUncalibrate( calValue );
            if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                uncalValue = BigDecimal.ZERO;
            }
            if ( rawSizeInBits_ == 32 ) {
                return BigInteger.valueOf( Float.floatToRawIntBits( uncalValue.floatValue() ) );
            } else if ( rawSizeInBits_ == 64 ) {
                return BigInteger.valueOf( Double.doubleToRawLongBits( uncalValue.doubleValue() ) );
            } else if ( rawSizeInBits_ == 128 ) {
                warnings_.add( "Unsupported encoding type for " +
                               itemName_ +
                               " Encoding: " +
                               rawTypeName_ );
            }
        } else if ( rawTypeName_.equals( "MILSTD_1750A" ) == true ) {
            warnings_.add( "Unsupported encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );
        } else if ( rawTypeName_.equals( "UTF-8" ) == true ) {
            String chars = calValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_8 ) );
            return encodeUtfString( retValue );
        } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {
            String chars = calValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_16 ) );
            if ( retValue.equals( BigInteger.ZERO ) == true ) {
                retValue = new BigInteger( utf16_ );
            }
            return encodeUtfString( retValue );
        } else {
            warnings_.add( "Unrecognized encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );
        }

        return BigInteger.ZERO;

    }

    private BigInteger encodeString( String calValue ) {

        try {

            if ( rawTypeName_.equals( "unsigned" ) == true ) {
                String lowerCalValue = calValue.toLowerCase();
                if ( lowerCalValue.startsWith( "0x" ) == true ) {
                    BigInteger retValue = new BigInteger( lowerCalValue.replaceFirst( "0x", "" ), 16 );
                    BigInteger uncalValue = integerEncodingUncalibrate( retValue );
                    if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                        return BigInteger.ZERO;
                    }
                    return uncalValue;
                } else {
                    BigDecimal retValue = new BigDecimal( lowerCalValue );
                    BigInteger uncalValue = integerEncodingUncalibrate( retValue );
                    if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                        return BigInteger.ZERO;
                    }
                    return uncalValue;
                }
            } else if ( rawTypeName_.equals( "signMagnitude" ) == true ) {
                String lowerCalValue = calValue.toLowerCase();
                if ( lowerCalValue.startsWith( "0x" ) == true ) {
                    BigInteger retValue = new BigInteger( lowerCalValue.replaceFirst( "0x", "" ), 16 );
                    BigInteger uncalValue = integerEncodingUncalibrate( retValue );
                    if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                        return BigInteger.ZERO;
                    }
                    return uncalValue;
                } else {
                    BigDecimal retValue = new BigDecimal( lowerCalValue );
                    BigInteger uncalValue = integerEncodingUncalibrate( retValue );
                    if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                        return BigInteger.ZERO;
                    }
                    return uncalValue;
                }
            } else if ( rawTypeName_.equals( "twosComplement" ) == true ) {
                String lowerCalValue = calValue.toLowerCase();
                if ( lowerCalValue.startsWith( "0x" ) == true ) {
                    BigInteger retValue = new BigInteger( lowerCalValue.replaceFirst( "0x", "" ), 16 );
                    BigInteger uncalValue = integerEncodingUncalibrate( retValue );
                    if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                        return BigInteger.ZERO;
                    }
                    return uncalValue;
                } else {
                    BigDecimal retValue = new BigDecimal( lowerCalValue );
                    BigInteger uncalValue = integerEncodingUncalibrate( retValue );
                    if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                        return BigInteger.ZERO;
                    }
                    return uncalValue;
                }
            } else if ( rawTypeName_.equals( "onesComplement" ) == true ) {
                String lowerCalValue = calValue.toLowerCase();
                if ( lowerCalValue.startsWith( "0x" ) == true ) {
                    BigInteger retValue = new BigInteger( lowerCalValue.replaceFirst( "0x", "" ), 16 );
                    BigInteger uncalValue = integerEncodingUncalibrate( retValue );
                    if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                        return BigInteger.ZERO;
                    }
                    return uncalValue;
                } else {
                    BigDecimal retValue = new BigDecimal( lowerCalValue );
                    BigInteger uncalValue = integerEncodingUncalibrate( retValue );
                    if ( isIntegerRawValueReasonable( uncalValue ) == false ) {
                        return BigInteger.ZERO;
                    }
                    return uncalValue;
                }
            } else if ( rawTypeName_.equals( "binary" ) == true ) {
                String lowerCalValue = calValue.toLowerCase();
                if ( lowerCalValue.startsWith( "0x" ) == true ) {
                    return new BigInteger( lowerCalValue.replaceFirst( "0x", "" ), 16 );
                } else {
                    return new BigInteger( lowerCalValue );
                }
            } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {
                String reasCalValue = calValue.toLowerCase();
                if ( reasCalValue.startsWith( "0x" ) == true ) {
                    BigInteger retValue = new BigInteger( reasCalValue.replaceFirst( "0x", "" ), 16 );
                    reasCalValue = retValue.toString();
                }
                BigDecimal uncalValue = floatEncodingUncalibrate( new BigDecimal( reasCalValue ) );
                if ( isFloatRawValueReasonable( uncalValue.doubleValue() ) == false ) {
                    uncalValue = BigDecimal.ZERO;
                }
                //System.out.println( "Raw Uncal " + uncalValue.toString() );
                if ( rawSizeInBits_ == 32 ) {
                    return BigInteger.valueOf( Float.floatToRawIntBits( uncalValue.floatValue() ) );
                } else if ( rawSizeInBits_ == 64 ) {
                    return BigInteger.valueOf( Double.doubleToRawLongBits( uncalValue.doubleValue() ) );
                } else if ( rawSizeInBits_ == 128 ) {
                    warnings_.add( "Unsupported encoding type for " +
                                   itemName_ +
                                   " Encoding: " +
                                   rawTypeName_ );
                }
            } else if ( rawTypeName_.equals( "MILSTD_1750A" ) == true ) {
                warnings_.add( "Unsupported encoding type for " +
                               itemName_ +
                               " Encoding: " +
                               rawTypeName_ );
            } else if ( rawTypeName_.equals( "UTF-8" ) == true ) {
                BigInteger retValue = BigInteger.ZERO;
                if ( calValue.isEmpty() == false ) {
                    retValue = new BigInteger( calValue.getBytes( StandardCharsets.UTF_8 ) );
                }
                return encodeUtfString( retValue );
            } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {
                BigInteger retValue = BigInteger.ZERO;
                if ( calValue.isEmpty() == false ) {
                    retValue = new BigInteger( calValue.getBytes( StandardCharsets.UTF_16 ) );
                }
                if ( retValue.equals( BigInteger.ZERO ) == true ) {
                    retValue = new BigInteger( utf16_ );
                }
                return encodeUtfString( retValue );
            } else {
                warnings_.add( "Unrecognized encoding type for " +
                               itemName_ +
                               " Encoding: " +
                               rawTypeName_ );
            }

        } catch ( NumberFormatException ex ) {
            warnings_.add( itemName_ +
                           " Invalid String value for encoding " +
                           rawTypeName_ +
                           " of '" +
                           calValue +
                           "'" );
        }

        return BigInteger.ZERO;

    }

    private String getCalibratedFromIntegerString( String calValue ) {

        try {
            BigInteger retValue = new BigInteger( calValue );
            return retValue.toString();
        } catch ( NumberFormatException ex ) {
            warnings_.add( itemName_ + " Calibrated value '" +
                calValue + "' is not representative of an integer" );
        }

        return "";

    }

    private String getCalibratedFromFloatString( String calValue ) {

        try {
            BigDecimal retValue = new BigDecimal( calValue );
            return retValue.toString();
        } catch ( NumberFormatException ex ) {
            warnings_.add( itemName_ + " Calibrated value '" +
                calValue + "' is not representative of a floating " +
                "point number" );
        }

        return "";

    }

    private String getCalibratedFromBooleanNumericString( String uncalValue ) {

        try {

            BigInteger intValue = new BigInteger( uncalValue );

            if ( intValue.compareTo( BigInteger.ZERO ) == 0 ) {
                return booleanZeroString_;
            } else if ( intValue.compareTo( BigInteger.ONE ) == 0 ) {
                return booleanOneString_;
            }

            warnings_.add( itemName_ + " Boolean undefined for " +
                " uncalibrated value '" + uncalValue + "'" );

        } catch ( NumberFormatException ex ) {
            warnings_.add( itemName_ + " uncalibrated value '" +
                uncalValue + "' is not an integer number needed for an " +
                "boolean type label" );
        }

        return "";

    }

    private String getCalibratedValueFromEnumeratedNumericString( String uncalValue ) {

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

            warnings_.add( itemName_ + " Enumeration undefined for " +
                "uncalibrated value '" + uncalValue + "'" );

        } catch ( NumberFormatException ex ) {
            warnings_.add( itemName_ + " uncalibrated value '" +
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

    private String applyCalibrator( String uncalValue ) {

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
            warnings_.add( itemName_ + " Unsupported calibrator form" );
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

    private double applyPolynomial( double                    xValue,
                                    List<PolynomialType.Term> terms ) {

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

    private double applySpline( double                xValue,
                                BigInteger            order,
                                boolean               extrapolate,
                                List<SplinePointType> points ) {

        // TODO: Support quadratics because I did it on the other side

        if ( order.intValue() > 1 ) {
            warnings_.add( itemName_ + " Unsupported Spline " +
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
            warnings_.add( "String length for encoding " +
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
            retValue = retValue.shiftLeft( 8 );
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
            if ( rawTypeName_.equals( "signMagnitude" ) == true ) {
                rawValue = rawValue.negate();
                rawValue = rawValue.setBit( rawSizeInBits_ - 1 );
            } else if ( rawTypeName_.equals( "onesComplement" ) == true ) {
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

    private void setValidRangeAttributes( XTCETypedObject item ) {

        NameDescriptionType typeObj = item.getTypeReference();

        if ( typeObj instanceof BooleanDataType ) {
            booleanZeroString_ = ((BooleanDataType)typeObj).getZeroStringValue();
            booleanOneString_  = ((BooleanDataType)typeObj).getOneStringValue();
        }

        validRange_ = item.getValidRange();

    }

    private BitSet makeBitSetFromBigInteger( BigInteger rawValue ) {

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

    public String bitSetToHex( BitSet bits ) {

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

    public BigInteger bitSetToNumber( BitSet bits ) {

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
            warnings_.add( itemName_ + " raw binary length '" +
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

    public String bitSetToBinary( BitSet bits ) {

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

    public BigInteger integerStringToBigInteger( String rawValue ) {

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
            warnings_.add( itemName_ + " raw value provided '" + rawValue +
                "' is not a properly formatted hex or integer." );
        }

        return rawInteger;

    }

    private BigDecimal floatEncodingUncalibrate( BigDecimal calValue ) {

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
                    warnings_.add( "Polynomial Calibrator for " +
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
                warnings_.add( "Polynomial Calibrator for " +
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
                    warnings_.add( "Spline Calibrator for " +
                                   itemName_ +
                                   " does not bound calibrated value " +
                                   calValue.toString() +
                                   " and extrapolate is false" );
                    return BigDecimal.ZERO;
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
                        return rawValue1;
                    } else if ( calValue.equals( calValue2 ) == true ) {
                        return rawValue2;
                    }
                    break;
                }
            }
            if ( rawValue1 == null || rawValue2 == null ) {
                warnings_.add( "Spline Calibrator for " +
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
                warnings_.add( "Spline Calibrator for " +
                               itemName_ +
                               " contains interpolate order of " +
                               Long.toString( interpolateOrder ) +
                               ".  Not supported by this toolkit." );
                return BigDecimal.ZERO;
            }
        }

        MathOperationCalibrator mathCal = defCal_.getMathOperationCalibrator();
        if ( mathCal != null ) {
            warnings_.add( "MathOperationCalibrator for " +
                           itemName_ +
                           " not supported" );
            return BigDecimal.ZERO;
        }

        return calValue;

    }

    private BigDecimal floatEncodingUncalibrate( BigInteger calValue ) {

        if ( defCal_ == null ) {
            return new BigDecimal( calValue );
        }

        return floatEncodingUncalibrate( new BigDecimal( calValue ) );

    }

    private BigInteger integerEncodingUncalibrate( BigDecimal calValue ) {

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
                    warnings_.add( "Polynomial Calibrator for " +
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
                warnings_.add( "Polynomial Calibrator for " +
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
                    warnings_.add( "Spline Calibrator for " +
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
                warnings_.add( "Spline Calibrator for " +
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
                warnings_.add( "Spline Calibrator for " +
                               itemName_ +
                               " contains interpolate order of " +
                               Long.toString( interpolateOrder ) +
                               ".  Not supported by this toolkit." );
                return BigInteger.ZERO;
            }
        }

        MathOperationCalibrator mathCal = defCal_.getMathOperationCalibrator();
        if ( mathCal != null ) {
            warnings_.add( "MathOperationCalibrator for " +
                           itemName_ +
                           " not supported" );
            return BigInteger.ZERO;
        }

        long value = calValue.longValue();
        return new BigInteger( Long.toString( value ) );

    }

    private BigInteger integerEncodingUncalibrate( BigInteger calValue ) {

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

    private void setEnumerationList( XTCETypedObject item ) {
        enums_ = item.getEnumerations();
    }

    /** Sets the internal default calibrator list information in the event that
     * this Parameter/Argument has an encoding type that supports calibration.
     *
     * The types that support Calibrators are Integer and Float data encoding.
     *
     */

    private void setDefaultCalibrator( XTCETypedObject item ) {
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

    public boolean isIntegerRawValueReasonable( BigInteger rawValue ) {

        // first find the general size applicable to the bit length
        boolean minInclusive = true;
        boolean maxInclusive = true;
        long    minValue     = 0;
        long    maxValue     = (long)Math.pow( 2, rawSizeInBits_ ) - 1;

        // if it is signed, then correct that range
        if ( ( rawTypeName_.equals( "signMagnitude" ) == true ) ||
             ( rawTypeName_.equals( "onesComplement" ) == true ) ||
             ( rawTypeName_.equals( "twosComplement" ) == true ) ) {
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
        if ( rawTypeName_.equals( "unsigned" ) == true ) {
            if ( rawValue.signum() < 0 ) {
                warnings_.add( "Unsigned value for item " +
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
                warnings_.add( rawTypeName_ +
                               " encoding value for item " +
                               itemName_ +
                               " is " +
                               rawValue.toString() +
                               ", which is less than or equal to the " +
                               "minimum value " +
                               Long.toString( minValue ) );
                return false;
            }
        } else {
            if ( minValue > rawValue.longValue() ) {
                warnings_.add( rawTypeName_ +
                               " encoding value for item " +
                               itemName_ +
                               " is " +
                               rawValue.toString() +
                               ", which is less than the " +
                               "minimum value " +
                               Long.toString( minValue ) );
                return false;
            }
        }

        // check for the boundary conditions with the maximum
        if ( maxInclusive == false ) {
            if ( maxValue <= rawValue.longValue() ) {
                warnings_.add( rawTypeName_ +
                               " encoding value for item " +
                               itemName_ +
                               " is " +
                               rawValue.toString() +
                               ", which is greater than or equal to the " +
                               "maximum value " +
                               Long.toString( maxValue ) );
                return false;
            }
        } else {
            if ( maxValue < rawValue.longValue() ) {
                warnings_.add( rawTypeName_ +
                               " encoding value for item " +
                               itemName_ +
                               " is " +
                               rawValue.toString() +
                               ", which is greater than the " +
                               "maximum value " +
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

    public boolean isFloatRawValueReasonable( double rawValue ) {

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
                warnings_.add( rawTypeName_ +
                               " encoding value for item " +
                               itemName_ +
                               " is " +
                               Double.toString( rawValue ) +
                               ", which is less than or equal to the " +
                               "minimum value " +
                               Double.toString( minValue ) );
                return false;
            }
        } else {
            if ( minValue > rawValue ) {
                warnings_.add( rawTypeName_ +
                               " encoding value for item " +
                               itemName_ +
                               " is " +
                               Double.toString( rawValue ) +
                               ", which is less than the " +
                               "minimum value " +
                               Double.toString( minValue ) );
                return false;
            }
        }

        // check for the boundary conditions with the maximum
        if ( maxInclusive == false ) {
            if ( maxValue <= rawValue ) {
                warnings_.add( rawTypeName_ +
                               " encoding value for item " +
                               itemName_ +
                               " is " +
                               Double.toString( rawValue ) +
                               ", which is greater than or equal to the " +
                               "maximum value " +
                               Double.toString( maxValue ) );
                return false;
            }
        } else {
            if ( maxValue < rawValue ) {
                warnings_.add( rawTypeName_ +
                               " encoding value for item " +
                               itemName_ +
                               " is " +
                               Double.toString( rawValue ) +
                               ", which is greater than the " +
                               "maximum value " +
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

        if ( rawTypeName_.equals( "unsigned" ) == true ) {
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
                warnings_.add( "Polynomial Calibrator for " +
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
        } else if ( ( rawTypeName_.equals( "signMagnitude" ) == true ) ||
                    ( rawTypeName_.equals( "onesComplement" ) == true ) ||
                    ( rawTypeName_.equals( "twosComplement" ) == true ) ) {
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
                warnings_.add( "Polynomial Calibrator for " +
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


    // Private Data Members

    private String  itemName_;
    private String  euTypeName_;
    private String  rawTypeName_;
    private int     rawSizeInBits_;
    private String  rawBitOrder_;
    private boolean validObject_;
    private String  booleanZeroString_;
    private String  booleanOneString_;

    private List<String>               warnings_    = new ArrayList<>();
    private List<ValueEnumerationType> enums_       = null;
    private CalibratorType             defCal_      = null;
    private XTCEValidRange             validRange_  = null;
    private XTCETypedObject            itemObj_     = null;
    private XTCEAbsoluteTimeType       timeHandler_ = null;

    private static final byte[] utf16_ = new byte[] { (byte)0xfe, (byte)0xff };

}
