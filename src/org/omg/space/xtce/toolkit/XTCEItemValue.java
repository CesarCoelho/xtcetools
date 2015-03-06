/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.omg.space.xtce.database.ArgumentTypeSetType.FloatArgumentType;
import org.omg.space.xtce.database.ArgumentTypeSetType.IntegerArgumentType;
import org.omg.space.xtce.database.BooleanDataType;
import org.omg.space.xtce.database.CalibratorType;
import org.omg.space.xtce.database.CalibratorType.MathOperationCalibrator;
import org.omg.space.xtce.database.CalibratorType.SplineCalibrator;
import org.omg.space.xtce.database.EnumeratedDataType;
import org.omg.space.xtce.database.FloatDataType;
import org.omg.space.xtce.database.IntegerDataType;
import org.omg.space.xtce.database.NameDescriptionType;
import org.omg.space.xtce.database.ParameterTypeSetType.BooleanParameterType;
import org.omg.space.xtce.database.ParameterTypeSetType.EnumeratedParameterType;
import org.omg.space.xtce.database.ParameterTypeSetType.FloatParameterType;
import org.omg.space.xtce.database.ParameterTypeSetType.IntegerParameterType;
import org.omg.space.xtce.database.PolynomialType;
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
 * @author b1053583
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

        // gather the raw encoding size, which may not be appropriate
        try {
            rawSizeInBits_ = Integer.parseInt( item.getRawSizeInBits() );
            validObject_   = true;
        } catch ( NumberFormatException ex ) {
            warnings_.add( "Cannot encode/decode item " +
                           itemName_ +
                           " without a numeric raw size in bits.  Valud provided: " +
                           rawSizeInBits_ );
            validObject_ = false;
        }

        // gather the Type Reference, for which we cannot proceed further
        // unless exists
        NameDescriptionType typeObj = item.getTypeReference();
        if ( typeObj == null ) {
            warnings_.add( "No type defined for " + itemName_ );
            validObject_ = false;
            return;
        }

        // gather the extended attributes that are needed for specific types
        setValidRangeAttributes( typeObj );
        setEnumerationList( item );
        setDefaultCalibrator( item );

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
     * @return ArrayList of String containing the warning messages.
     *
     */

    public ArrayList<String> getWarnings() {
        return warnings_;
    }

    /** Clears the list of warning messages for this object, which can be
     * useful if multiple raw or engineering values need to be generated, so
     * that the list does not include previous issues.
     *
     */

    public void clearWarnings() {
        warnings_ = new ArrayList<String>();
    }

    public String decode( BitSet rawValue ) {
        return "";
    }

    public BitSet encode( String euValue ) {

        BigInteger calNumericValue   = null;
        BigInteger uncalNumericValue = null;
        BitSet     rawValue          = new BitSet( rawSizeInBits_ );

        if ( euTypeName_.equals( "BOOLEAN" ) == true ) {
            calNumericValue   = numberFromBoolean( euValue );
            uncalNumericValue = encodeNumber( calNumericValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "ENUMERATED" ) == true ) {
            calNumericValue   = numberFromEnumeration( euValue );
            uncalNumericValue = encodeNumber( calNumericValue );
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
            // TODO Add SIGNED Type
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

    }

    public BitSet encode( long euValue ) {

        BigInteger calNumericValue   = null;
        BigInteger uncalNumericValue = null;
        BitSet     rawValue          = null;
        if ( euTypeName_.equals( "BOOLEAN" ) == true ) {
            calNumericValue   = numberFromBoolean( Long.toString( euValue ) );
            uncalNumericValue = encodeNumber( calNumericValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "ENUMERATED" ) == true ) {
            calNumericValue   = numberFromEnumeration( Long.toString( euValue ) );
            uncalNumericValue = encodeNumber( calNumericValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "STRING" ) == true ) {
            uncalNumericValue = encodeNumber( BigInteger.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "BINARY" ) == true ) {
            uncalNumericValue = encodeNumber( BigInteger.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.startsWith( "FLOAT" ) == true ) {
            uncalNumericValue = encodeNumber( BigInteger.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "UNSIGNED" ) == true ) {
            uncalNumericValue = encodeNumber( BigInteger.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        }

        return rawValue;

    }

    public BitSet encode( double euValue ) {

        BigInteger calNumericValue   = null;
        BigInteger uncalNumericValue = null;
        BitSet     rawValue          = null;
        if ( euTypeName_.equals( "BOOLEAN" ) == true ) {
            long numericValue = Double.valueOf( euValue ).longValue();
            calNumericValue   = numberFromBoolean( Long.toString( numericValue ) );
            uncalNumericValue = encodeNumber( calNumericValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "ENUMERATED" ) == true ) {
            calNumericValue   = numberFromEnumeration( Double.toString( euValue ) );
            uncalNumericValue = encodeNumber( calNumericValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "STRING" ) == true ) {
            uncalNumericValue = encodeDecimal( BigDecimal.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "BINARY" ) == true ) {
            warnings_.add( "Invalid binary value for " +
                           itemName_ +
                           " Value Provided: " +
                           Double.toString( euValue ) );
            rawValue = new BitSet( rawSizeInBits_ );
        } else if ( euTypeName_.startsWith( "FLOAT" ) == true ) {
            uncalNumericValue = encodeDecimal( BigDecimal.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "UNSIGNED" ) == true ) {
            if ( ( euValue % 1 ) != 0 ) {
                warnings_.add( "Invalid unsigned integer value for " +
                               itemName_ +
                               " Value Provided: " +
                               Double.toString( euValue ) );
                rawValue = new BitSet( rawSizeInBits_ );
                return rawValue;
            }
            long number = Double.valueOf( euValue ).longValue();
            uncalNumericValue = encodeNumber( BigInteger.valueOf( number ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        }

        return rawValue;

    }

    public BitSet encode( float euValue ) {

        BigInteger calNumericValue   = null;
        BigInteger uncalNumericValue = null;
        BitSet     rawValue          = null;
        if ( euTypeName_.equals( "BOOLEAN" ) == true ) {
            long numericValue = Float.valueOf( euValue ).longValue();
            calNumericValue   = numberFromBoolean( Long.toString( numericValue ) );
            uncalNumericValue = encodeNumber( calNumericValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "ENUMERATED" ) == true ) {
            calNumericValue   = numberFromEnumeration( Float.toString( euValue ) );
            uncalNumericValue = encodeNumber( calNumericValue );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "STRING" ) == true ) {
            uncalNumericValue = encodeDecimal( BigDecimal.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "BINARY" ) == true ) {
            warnings_.add( "Invalid binary value for " +
                           itemName_ +
                           " Value Provided: " +
                           Float.toString( euValue ) );
            rawValue = new BitSet( rawSizeInBits_ );
        } else if ( euTypeName_.startsWith( "FLOAT" ) == true ) {
            uncalNumericValue = encodeDecimal( BigDecimal.valueOf( euValue ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        } else if ( euTypeName_.equals( "UNSIGNED" ) == true ) {
            if ( ( euValue % 1 ) != 0 ) {
                warnings_.add( "Invalid unsigned integer value for " +
                               itemName_ +
                               " Value Provided: " +
                               Float.toString( euValue ) );
                rawValue = new BitSet( rawSizeInBits_ );
                return rawValue;
            }
            long number = Float.valueOf( euValue ).longValue();
            uncalNumericValue = encodeNumber( BigInteger.valueOf( number ) );
            rawValue          = encodeRawBits( uncalNumericValue );
        }

        return rawValue;

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

    private BigInteger numberFromBoolean( String euValue ) {

        if ( euValue.equals( booleanZeroString_ ) == true ) {
            return BigInteger.ZERO;
        } else if ( euValue.equals( booleanOneString_ ) == true ) {
            return BigInteger.ONE;
        } else {
            warnings_.add( "Invalid Boolean value for " +
                           itemName_ +
                           " Value Provided: " +
                           euValue );
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

    private BigInteger numberFromEnumeration( String euValue ) {

        for ( ValueEnumerationType enumItem : enums_ ) {

            if ( enumItem.getLabel().equals( euValue ) == false ) {
                continue;
            }

            if ( enumItem.getMaxValue() != null ) {
                warnings_.add( "Enumeration label " +
                               enumItem.getLabel() +
                               " for " +
                               itemName_ +
                               " has values " +
                               enumItem.getValue().toString() +
                               " through " +
                               enumItem.getMaxValue().toString() +
                               ", using lowest possible value" );
            }

            return enumItem.getValue();

        }

        warnings_.add( "No enumeration value found for " +
                       itemName_ +
                       " label: " +
                       euValue );

        return BigInteger.ZERO;

    }

    private String enumerationFromNumber( BigInteger euValue ) {

        for ( ValueEnumerationType enumItem : enums_ ) {
            BigInteger value    = enumItem.getValue();
            BigInteger maxValue = value;
            if ( enumItem.getMaxValue() != null ) {
                maxValue = enumItem.getMaxValue();
            }
            if ( ( euValue.compareTo( value )    > -1 ) &&
                 ( euValue.compareTo( maxValue ) < 1  ) ) {
                return enumItem.getLabel();
            }
        }

        warnings_.add( "No enumeration label found for " +
                       itemName_ +
                       " value: " +
                       euValue.toString() );
        return "";

    }

    private BigInteger encodeNumber( BigInteger calValue ) {

        if ( rawTypeName_.equals( "unsigned" ) == true ) {
            if ( calValue.max( BigInteger.ZERO ) == BigInteger.ZERO ) {
                warnings_.add( "Unsigned value for item " +
                               itemName_ +
                               " is " +
                               calValue.toString() +
                               " which cannot be negative" );
                return BigInteger.ZERO;
            }
            // TODO uncalibrate
            BigInteger maxValue = new BigInteger( "2" ).pow( rawSizeInBits_ ).subtract( new BigInteger( "1" ) );
            if ( calValue.max( maxValue ) == calValue ) {
                warnings_.add( "Unsigned value for encoding item " +
                               itemName_ +
                               " is " +
                               calValue.toString() +
                               " which exceeds fixed length max of " +
                               maxValue.toString() +
                               " using " +
                               Long.toString( rawSizeInBits_ ) +
                               " bits" );
            }
            return calValue;
        } else if ( rawTypeName_.equals( "signMagnitude" ) == true ) {
            // TODO uncalibrate
            // TODO handle BigInteger twosComplement with signMagnitude
            return calValue;
        } else if ( rawTypeName_.equals( "twosComplement" ) == true ) {
            // TODO uncalibrate
            return calValue;
        } else if ( rawTypeName_.equals( "onesComplement" ) == true ) {
            // TODO uncalibrate
            // TODO handle BigInteger twosComplement with onesComplement
            return calValue;
        } else if ( rawTypeName_.equals( "binary" ) == true ) {
            return calValue;
        } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {
            if ( rawSizeInBits_ == 32 ) {
                return BigInteger.valueOf( Float.floatToRawIntBits( calValue.floatValue() ) );
            } else if ( rawSizeInBits_ == 64 ) {
                return BigInteger.valueOf( Double.doubleToRawLongBits( calValue.doubleValue() ) );
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
            return retValue;
        } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {
            String chars = calValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_16 ) );
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
            return retValue;
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
            
        } else if ( rawTypeName_.equals( "signMagnitude" ) == true ) {
            
        } else if ( rawTypeName_.equals( "twosComplement" ) == true ) {
            
        } else if ( rawTypeName_.equals( "onesComplement" ) == true ) {
            
        } else if ( rawTypeName_.equals( "binary" ) == true ) {
            warnings_.add( "Unsupported encoding type for " +
                           itemName_ +
                           " Encoding: " +
                           rawTypeName_ );
        } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {
            if ( rawSizeInBits_ == 32 ) {
                return BigInteger.valueOf( Float.floatToRawIntBits( calValue.floatValue() ) );
            } else if ( rawSizeInBits_ == 64 ) {
                return BigInteger.valueOf( Double.doubleToRawLongBits( calValue.doubleValue() ) );
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
            return retValue;
        } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {
            String chars = calValue.toString();
            BigInteger retValue = new BigInteger( chars.getBytes( StandardCharsets.UTF_16 ) );
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
            return retValue;
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
                calValue = calValue.toLowerCase();
                if ( calValue.startsWith( "0x" ) == true ) {
                    BigInteger retValue = new BigInteger( calValue.replaceFirst( "0x", "" ), 16 );
                    BigInteger maxValue = new BigInteger( "2" ).pow( rawSizeInBits_ ).subtract( new BigInteger( "1" ) );
                    if ( retValue.max( BigInteger.ZERO ) == BigInteger.ZERO ) {
                        warnings_.add( "Unsigned value for item " +
                                       itemName_ +
                                       " is " +
                                       retValue.toString() +
                                       " which cannot be negative" );
                        return BigInteger.ZERO;
                    }
                    if ( retValue.max( maxValue ) == retValue ) {
                        warnings_.add( "Unsigned value for encoding item " +
                                       itemName_ +
                                       " is " +
                                       retValue.toString() +
                                       " which exceeds fixed length max of " +
                                       maxValue.toString() +
                                       " using " +
                                       Long.toString( rawSizeInBits_ ) +
                                       " bits" );
                    }
                    return retValue;
                } else {
                    BigInteger retValue = new BigInteger( calValue );
                    BigInteger maxValue = new BigInteger( "2" ).pow( rawSizeInBits_ ).subtract( new BigInteger( "1" ) );
                    if ( retValue.max( BigInteger.ZERO ) == BigInteger.ZERO ) {
                        warnings_.add( "Unsigned value for item " +
                                       itemName_ +
                                       " is " +
                                       retValue.toString() +
                                       " which cannot be negative" );
                        return BigInteger.ZERO;
                    }
                    if ( retValue.max( maxValue ) == retValue ) {
                        warnings_.add( "Unsigned value for encoding item " +
                                       itemName_ +
                                       " is " +
                                       retValue.toString() +
                                       " which exceeds fixed length max of " +
                                       maxValue.toString() +
                                       " using " +
                                       Long.toString( rawSizeInBits_ ) +
                                       " bits" );
                    }
                    return retValue;
                }
            } else if ( rawTypeName_.equals( "signMagnitude" ) == true ) {
            
            } else if ( rawTypeName_.equals( "twosComplement" ) == true ) {
            
            } else if ( rawTypeName_.equals( "onesComplement" ) == true ) {
            
            } else if ( rawTypeName_.equals( "binary" ) == true ) {
                calValue = calValue.toLowerCase();
                if ( calValue.startsWith( "0x" ) == true ) {
                    return new BigInteger( calValue.replaceFirst( "0x", "" ), 16 );
                } else {
                    return new BigInteger( calValue );
                }
            } else if ( rawTypeName_.equals( "IEEE754_1985" ) == true ) {
                if ( rawSizeInBits_ == 32 ) {
                    return BigInteger.valueOf( Float.floatToRawIntBits( Float.parseFloat( calValue ) ) );
                } else if ( rawSizeInBits_ == 64 ) {
                    return BigInteger.valueOf( Double.doubleToRawLongBits( Double.parseDouble( calValue ) ) );
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
                BigInteger retValue = new BigInteger( calValue.getBytes( StandardCharsets.UTF_8 ) );
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
                return retValue;
            } else if ( rawTypeName_.equals( "UTF-16" ) == true ) {
                BigInteger retValue = new BigInteger( calValue.getBytes( StandardCharsets.UTF_16 ) );
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
                return retValue;
            } else {
                warnings_.add( "Unrecognized encoding type for " +
                               itemName_ +
                               " Encoding: " +
                               rawTypeName_ );
            }

        } catch ( NumberFormatException ex ) {
            warnings_.add( "Invalid value for " +
                           itemName_ +
                           " encoding: " +
                           rawTypeName_ +
                           " value: " +
                           calValue );
        }

        return BigInteger.ZERO;

    }

    /** Function to create the raw binary bits for populating a container
     * binary based on the encoded value of this named and typed item from the
     * XTCE data model.
     *
     * The caller provides the uncalibrated value in the form of a BigInteger
     * and this function walks through the bits of that value, ensuring to
     * use all the bits that are in the raw encoded size.  It sets the BitSet
     * such that bit 0 of the BitSet is the least significant bit and the
     * highest (rawSizeInBits_) is the most significant bit.  If the order is
     * reversed by the encoding attribute @bitOrder, then the reverse happens.
     *
     * @param uncalValue BigInteger containing the value to encode into a raw
     * BitSet for inclusion into a container object, either Telemetry or
     * Telecommand.
     *
     * @return BitSet suitable for inclusion into a Telemetry or Telecommand
     * container by simply walking the length and placing the bits into the
     * container.  All ordering has already been handled.
     *
     */

    private BitSet encodeRawBits( BigInteger uncalValue ) {

        // TODO Handle Byte order element ByteOrderList in the encoding

        BitSet rawBits = new BitSet( rawSizeInBits_ );
        if ( rawBitOrder_.equals( "mostSignificantBitFirst" ) == true ) {
            for ( int iii = rawSizeInBits_ - 1; iii >= 0; --iii ) {
                rawBits.set( iii, uncalValue.testBit( iii ) );
            }
        } else {
            for ( int iii = rawSizeInBits_ - 1; iii >= 0; --iii ) {
                rawBits.set( rawSizeInBits_ - iii - 1, uncalValue.testBit( iii ) );
            }
        }

        return rawBits;

    }

    /** Sets ranging information for item value validity.
     *
     * Ranging information is optional and will be ignored if it has not been
     * setup by this method.  This is generally invoked from the function
     * setValidRangeAttributes().  The reason it is separated is so that some
     * platform specific versions of this class can be generated, although they
     * will not be a part of this toolkit.
     *
     * @param lowValidValue String containing the minimum valid value.  See the
     * other arguments for modifiers of this.
     *
     * @param highValidValue String containing the maximum valid value.  See
     * the other arguments for modifiers of this.
     *
     * @param lowValidValueInclusive boolean indicating if the minimum valid
     * value should be inclusive (if true) or exclusive (if false).  This
     * changes the behavior to &gt; or equal to in the case of true and just
     * &gt; in the event of false.
     *
     * @param highValidValueInclusive boolean indicating if the minimum valid
     * value should be inclusive (if true) or exclusive (if false).  This
     * changes the behavior to &lt; or equal to in the case of true and just
     * &lt; in the event of false.
     *
     * @param lowAppliesToCalibrated boolean indicating of the valid values are
     * to be applied to the raw/uncalibrated or EU/calibrated value when doing
     * the encode/decode operations.
     *
     * @param highAppliesToCalibrated boolean indicating of the valid values are
     * to be applied to the raw/uncalibrated or EU/calibrated value when doing
     * the encode/decode operations.
     *
     */

    protected void setValidRange( String  lowValidValue,
                                  String  highValidValue,
                                  boolean lowValidValueInclusive,
                                  boolean highValidValueInclusive,
                                  boolean lowAppliesToCalibrated,
                                  boolean highAppliesToCalibrated ) {

        lowValidValue_           = lowValidValue;
        highValidValue_          = highValidValue;
        lowValidValueInclusive_  = lowValidValueInclusive;
        highValidValueInclusive_ = highValidValueInclusive;
        lowAppliesToCalibrated_  = lowAppliesToCalibrated;
        highAppliesToCalibrated_ = highAppliesToCalibrated;
        validRangeExists_        = true;

    }

    /** Sets the Valid Range attributes of this object based on a general
     * type specification from the XTCE data model, which all inherit from the
     * NameDescriptionType.
     *
     * @param typeObj NameDescriptionType containing the type information for
     * this named and typed Parameter/Argument.
     *
     */

    private void setValidRangeAttributes( NameDescriptionType typeObj ) {

        if ( typeObj.getClass() == BooleanParameterType.class ) {
            setValidRange( "0", "1", true, true, false, false );
            booleanZeroString_ = ((BooleanParameterType)typeObj).getZeroStringValue();
            booleanOneString_  = ((BooleanParameterType)typeObj).getOneStringValue();
        } else if ( typeObj.getClass() == BooleanDataType.class ) {
            booleanZeroString_ = ((BooleanDataType)typeObj).getZeroStringValue();
            booleanOneString_  = ((BooleanDataType)typeObj).getOneStringValue();
        } else if ( typeObj.getClass() == IntegerParameterType.class ) {
            IntegerDataType.ValidRange rangeElement = ((IntegerParameterType)typeObj).getValidRange();
            if ( rangeElement != null ) {
                setValidRange( rangeElement.getMinInclusive(),
                               rangeElement.getMaxInclusive(),
                               true,
                               true,
                               rangeElement.isValidRangeAppliesToCalibrated(),
                               rangeElement.isValidRangeAppliesToCalibrated() );
            }
        } else if ( typeObj.getClass() == IntegerArgumentType.class ) {
            IntegerDataType.ValidRange rangeElement = ((IntegerArgumentType)typeObj).getValidRange();
            if ( rangeElement != null ) {
                setValidRange( rangeElement.getMinInclusive(),
                               rangeElement.getMaxInclusive(),
                               true,
                               true,
                               rangeElement.isValidRangeAppliesToCalibrated(),
                               rangeElement.isValidRangeAppliesToCalibrated() );
            }
        } else if ( typeObj.getClass() == FloatParameterType.class ) {
            FloatDataType.ValidRange rangeElement = ((FloatParameterType)typeObj).getValidRange();
            if ( rangeElement != null ) {
                setFloatValidRange( rangeElement );
            }
        } else if ( typeObj.getClass() == FloatArgumentType.class ) {
            FloatDataType.ValidRange rangeElement = ((FloatArgumentType)typeObj).getValidRange();
            if ( rangeElement != null ) {
                setFloatValidRange( rangeElement );
            }
        }

    }

    /** Sets the Valid Range attributes of this object based on those that are
     * provided with a Floating Point Encoding value.
     *
     * @param rangeElement A FloatDataType.ValidRange element from the XTCE
     * data model.
     *
     */

    private void setFloatValidRange( FloatDataType.ValidRange rangeElement ) {

        // first evaluate the minimum value, if it exists, and determine if
        // it is inclusive or exclusive.  We leave it an empty string if it is
        // not applicable to this item.
        boolean minInclusive = true;
        String  minValue     = "";
        Double  min          = rangeElement.getMinInclusive();
        if ( min == null ) {
            min = rangeElement.getMinExclusive();
            if ( min != null ) {
                minInclusive = false;
            }
        }
        if ( min != null ) {
            minValue = min.toString();
        }

        // next evaluated the maximum value, if it exists, and determine if
        // it is inclusive or exclusive.  We leave it an empty string if it is
        // not applicable to this item.
        boolean maxInclusive = true;
        String  maxValue     = "";
        Double  max          = rangeElement.getMaxInclusive();
        if ( max == null ) {
            max = rangeElement.getMinExclusive();
            if ( max != null ) {
                maxInclusive = false;
            }
        }
        if ( max != null ) {
            maxValue = max.toString();
        }

        // set the valid range information from the more generic setter method
        setValidRange( minValue,
                       maxValue,
                       minInclusive,
                       maxInclusive,
                       rangeElement.isValidRangeAppliesToCalibrated(),
                       rangeElement.isValidRangeAppliesToCalibrated() );

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

    private BigDecimal floatEncodingUncalibrate( BigDecimal calValue ) {

        if ( defCal_ == null ) {
            return calValue;
        }

        return calValue;

    }

    private BigDecimal floatEncodingUncalibrate( BigInteger calValue ) {

        if ( defCal_ == null ) {
            return new BigDecimal( calValue );
        }

        return new BigDecimal( calValue );

    }

    private BigInteger integerEncodingUncalibrate( BigDecimal calValue ) {

        if ( defCal_ == null ) {
            long value = calValue.longValue();
            return new BigInteger( Long.toString( value ) );
        }

        PolynomialType polyCal = defCal_.getPolynomialCalibrator();
        if ( polyCal != null ) {
            //BigInteger retValue = BigInteger.ZERO;
            //List<PolynomialType.Term> terms = polyCal.getTerm();
            //for ( PolynomialType.Term term : terms ) {
            //    
            //}
        }

        SplineCalibrator splineCal = defCal_.getSplineCalibrator();
        if ( splineCal != null ) {
            
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

        return calValue;

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

    /** Main function to facilitate quick testing of this class without the
     * overhead of creating another tool or using the graphical interface.
     *
     * @param args the command line arguments
     *
     */

    public static void main( String args[] ) {

        System.out.println( "Testing XTCEItemValue Class" );

        System.out.println( "Loading the BogusSat-1.xml demo database" );

        String file = "src/org/omg/space/xtce/database/BogusSat-1.xml";

        try {

            XTCEDatabase db =
                new XTCEDatabase( new File( file ), false, null );

            System.out.println( "Testing boolean/unsigned no Calibrator" );

            XTCEParameter p1 = db.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" )
                                 .getTelemetryParameter( "Battery_Charge_Mode" );

            XTCEItemValue v1 = new XTCEItemValue( p1 );
            if ( v1.isValid() == false ) {
                throw new XTCEDatabaseException( "Parameter " +
                                                 p1.getName() +
                                                 " missing encoding information" );
            }

            BitSet r1 = v1.encode( "CHARGE" );
            System.out.println( "CHARGE = " + v1.bitSetToHex( r1 ) + " (" + v1.bitSetToBinary( r1 ) + ")" );
            BitSet r2 = v1.encode( "DISCHARGE" );
            System.out.println( "DISCHARGE = " + v1.bitSetToHex( r2 ) + " (" + v1.bitSetToBinary( r2 ) + ")" );
            BitSet r3 = v1.encode( "FOOBAR" );
            System.out.println( "FOOBAR = " + v1.bitSetToHex( r3 ) + " (" + v1.bitSetToBinary( r3 ) + ")" );
            BitSet r4 = null;
            BitSet r5 = null;
            BitSet r6 = null;
            BitSet r7 = null;
            BitSet r8 = null;
            BitSet r9 = null;
            BitSet r10 = null;
            for ( String warn : v1.getWarnings() ) {
                System.out.println( warn );
            }
            v1.clearWarnings();
            System.out.println( "" );

            p1 = db.getSpaceSystem( "/BogusSAT/SC001/Payload1" )
                   .getTelemetryParameter( "Payload_1_Phase" );

            v1 = new XTCEItemValue( p1 );
            if ( v1.isValid() == false ) {
                throw new XTCEDatabaseException( "Parameter " +
                                                 p1.getName() +
                                                 " missing encoding information" );
            }

            r1 = v1.encode( "STANDBY" );
            System.out.println( "STANDBY = " + v1.bitSetToHex( r1 ) + " (" + v1.bitSetToBinary( r1 ) + ")" );
            r2 = v1.encode( "FAILED" );
            System.out.println( "FAILED = " + v1.bitSetToHex( r2 ) + " (" + v1.bitSetToBinary( r2 ) + ")" );
            r3 = v1.encode( "FOOBAR" );
            System.out.println( "FOOBAR = " + v1.bitSetToHex( r3 ) + " (" + v1.bitSetToBinary( r3 ) + ")" );
            for ( String warn : v1.getWarnings() ) {
                System.out.println( warn );
            }
            v1.clearWarnings();
            System.out.println( "" );

            p1 = db.getSpaceSystem( "/BogusSAT/SC001/Payload1" )
                   .getTelemetryParameter( "Payload_MD5" );

            v1 = new XTCEItemValue( p1 );
            if ( v1.isValid() == false ) {
                throw new XTCEDatabaseException( "Parameter " +
                                                 p1.getName() +
                                                 " missing encoding information" );
            }

            r1 = v1.encode( "0xa567e0660841dc13346047aa5ac2b5c7" );
            System.out.println( "0xa567e0660841dc13346047aa5ac2b5c7 = " + v1.bitSetToHex( r1 ) + " (" + v1.bitSetToBinary( r1 ) + ")" );
            r2 = v1.encode( "0xaaaabbbb" );
            System.out.println( "aaaabbbb = " + v1.bitSetToHex( r2 ) + " (" + v1.bitSetToBinary( r2 ) + ")" );
            r3 = v1.encode( "12345678" );
            System.out.println( "12345678 = " + v1.bitSetToHex( r3 ) + " (" + v1.bitSetToBinary( r3 ) + ")" );
            r4 = v1.encode( 12345678 );
            System.out.println( "12345678 = " + v1.bitSetToHex( r4 ) + " (" + v1.bitSetToBinary( r4 ) + ")" );
            r5 = v1.encode( 12345678.0 );
            System.out.println( "12345678.0 = " + v1.bitSetToHex( r5 ) + " (" + v1.bitSetToBinary( r5 ) + ")" );
            r6 = v1.encode( "FOOBAR" );
            System.out.println( "FOOBAR = " + v1.bitSetToHex( r6 ) + " (" + v1.bitSetToBinary( r6 ) + ")" );
            for ( String warn : v1.getWarnings() ) {
                System.out.println( warn );
            }
            v1.clearWarnings();
            System.out.println( "" );

            p1 = db.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" )
                   .getTelemetryParameter( "Battery_Voltage" );

            v1 = new XTCEItemValue( p1 );
            if ( v1.isValid() == false ) {
                throw new XTCEDatabaseException( "Parameter " +
                                                 p1.getName() +
                                                 " missing encoding information" );
            }

            r1 = v1.encode( "1.25" );
            System.out.println( "1.25 = " + v1.bitSetToHex( r1 ) + " (" + v1.bitSetToBinary( r1 ) + ")" );
            r2 = v1.encode( "99.5" );
            System.out.println( "99.5 = " + v1.bitSetToHex( r2 ) + " (" + v1.bitSetToBinary( r2 ) + ")" );
            r3 = v1.encode( "12.1" );
            System.out.println( "12.1 = " + v1.bitSetToHex( r3 ) + " (" + v1.bitSetToBinary( r3 ) + ")" );
            r4 = v1.encode( 5 );
            System.out.println( "5 = " + v1.bitSetToHex( r4 ) + " (" + v1.bitSetToBinary( r4 ) + ")" );
            r5 = v1.encode( 1.25 );
            System.out.println( "1.25 = " + v1.bitSetToHex( r5 ) + " (" + v1.bitSetToBinary( r5 ) + ")" );
            r6 = v1.encode( "FOOBAR" );
            System.out.println( "FOOBAR = " + v1.bitSetToHex( r6 ) + " (" + v1.bitSetToBinary( r6 ) + ")" );
            for ( String warn : v1.getWarnings() ) {
                System.out.println( warn );
            }
            v1.clearWarnings();
            System.out.println( "" );

            p1 = db.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" )
                   .getTelemetryParameter( "Bus_Fault_Message" );

            v1 = new XTCEItemValue( p1 );
            if ( v1.isValid() == false ) {
                throw new XTCEDatabaseException( "Parameter " +
                                                 p1.getName() +
                                                 " missing encoding information" );
            }

            r1 = v1.encode( "This is a test" );
            System.out.println( "This is a test = " + v1.bitSetToHex( r1 ) + " (" + v1.bitSetToBinary( r1 ) + ")" );
            r2 = v1.encode( "" );
            System.out.println( "[empty string] = " + v1.bitSetToHex( r2 ) + " (" + v1.bitSetToBinary( r2 ) + ")" );
            r3 = v1.encode( "12345678901234567890" );
            System.out.println( "12345678901234567890 = " + v1.bitSetToHex( r3 ) + " (" + v1.bitSetToBinary( r3 ) + ")" );
            r4 = v1.encode( 5 );
            System.out.println( "5 = " + v1.bitSetToHex( r4 ) + " (" + v1.bitSetToBinary( r4 ) + ")" );
            r5 = v1.encode( 1.25 );
            System.out.println( "1.25 = " + v1.bitSetToHex( r5 ) + " (" + v1.bitSetToBinary( r5 ) + ")" );
            r6 = v1.encode( "FOOBAR" );
            System.out.println( "FOOBAR = " + v1.bitSetToHex( r6 ) + " (" + v1.bitSetToBinary( r6 ) + ")" );
            for ( String warn : v1.getWarnings() ) {
                System.out.println( warn );
            }
            v1.clearWarnings();
            System.out.println( "" );

            p1 = db.getSpaceSystem( "/BogusSAT" )
                   .getTelemetryParameter( "CCSDSAPID" );

            v1 = new XTCEItemValue( p1 );
            if ( v1.isValid() == false ) {
                throw new XTCEDatabaseException( "Parameter " +
                                                 p1.getName() +
                                                 " missing encoding information" );
            }

            r1 = v1.encode( "15" );
            System.out.println( "15 = " + v1.bitSetToHex( r1 ) + " (" + v1.bitSetToBinary( r1 ) + ")" );
            r2 = v1.encode( "1" );
            System.out.println( "1 = " + v1.bitSetToHex( r2 ) + " (" + v1.bitSetToBinary( r2 ) + ")" );
            r3 = v1.encode( "2047" );
            System.out.println( "2047 = " + v1.bitSetToHex( r3 ) + " (" + v1.bitSetToBinary( r3 ) + ")" );
            r4 = v1.encode( 5 );
            System.out.println( "5 = " + v1.bitSetToHex( r4 ) + " (" + v1.bitSetToBinary( r4 ) + ")" );
            r5 = v1.encode( 1.25 );
            System.out.println( "1.25 = " + v1.bitSetToHex( r5 ) + " (" + v1.bitSetToBinary( r5 ) + ")" );
            r6 = v1.encode( "FOOBAR" );
            System.out.println( "FOOBAR = " + v1.bitSetToHex( r6 ) + " (" + v1.bitSetToBinary( r6 ) + ")" );
            r7 = v1.encode( "0xff" );
            System.out.println( "0xff = " + v1.bitSetToHex( r7 ) + " (" + v1.bitSetToBinary( r7 ) + ")" );
            r8 = v1.encode( "0xffff" );
            System.out.println( "0xffff = " + v1.bitSetToHex( r8 ) + " (" + v1.bitSetToBinary( r8 ) + ")" );
            r9 = v1.encode( -2 );
            System.out.println( "-2 = " + v1.bitSetToHex( r9 ) + " (" + v1.bitSetToBinary( r9 ) + ")" );
            r10 = v1.encode( "-1" );
            System.out.println( "-1 = " + v1.bitSetToHex( r10 ) + " (" + v1.bitSetToBinary( r10 ) + ")" );
            for ( String warn : v1.getWarnings() ) {
                System.out.println( warn );
            }
            v1.clearWarnings();
            System.out.println( "" );

            System.out.println( "Done" );

        } catch ( Exception ex ) {

            System.out.println( "Exception: " + ex.getLocalizedMessage() );
            ex.printStackTrace();

        }

    }



    // Private Data Members

    private String  itemName_;
    private String  euTypeName_;
    private String  rawTypeName_;
    private int     rawSizeInBits_;
    private String  rawBitOrder_;
    private String  lowValidValue_;
    private String  highValidValue_;
    private boolean lowValidValueInclusive_;
    private boolean highValidValueInclusive_;
    private boolean lowAppliesToCalibrated_;
    private boolean highAppliesToCalibrated_;
    private boolean validRangeExists_;
    private boolean validObject_;

    private String  booleanZeroString_;
    private String  booleanOneString_;

    private ArrayList<String>          warnings_ = new ArrayList<String>();
    private List<ValueEnumerationType> enums_    = null;
    private CalibratorType             defCal_   = null;

}
