/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.util.ArrayList;
import java.util.List;
import org.omg.space.xtce.database.AbsoluteTimeDataType;
import org.omg.space.xtce.database.AggregateDataType;
import org.omg.space.xtce.database.AliasSetType;
import org.omg.space.xtce.database.ArgumentTypeSetType.FloatArgumentType;
import org.omg.space.xtce.database.ArgumentTypeSetType.IntegerArgumentType;
import org.omg.space.xtce.database.ArrayDataTypeType;
import org.omg.space.xtce.database.BaseDataType;
import org.omg.space.xtce.database.BaseTimeDataType;
import org.omg.space.xtce.database.BinaryDataType;
import org.omg.space.xtce.database.BooleanDataType;
import org.omg.space.xtce.database.CalibratorType;
import org.omg.space.xtce.database.EnumeratedDataType;
import org.omg.space.xtce.database.IntegerValueType;
import org.omg.space.xtce.database.NameDescriptionType;
import org.omg.space.xtce.database.ParameterTypeSetType.BinaryParameterType;
import org.omg.space.xtce.database.ParameterTypeSetType.BooleanParameterType;
import org.omg.space.xtce.database.ParameterTypeSetType.EnumeratedParameterType;
import org.omg.space.xtce.database.ParameterTypeSetType.FloatParameterType;
import org.omg.space.xtce.database.ParameterTypeSetType.IntegerParameterType;
import org.omg.space.xtce.database.ParameterTypeSetType.RelativeTimeParameterType;
import org.omg.space.xtce.database.ParameterTypeSetType.StringParameterType;
import org.omg.space.xtce.database.RelativeTimeDataType;
import org.omg.space.xtce.database.StringDataEncodingType;
import org.omg.space.xtce.database.StringDataType;
import org.omg.space.xtce.database.UnitType;
import org.omg.space.xtce.database.ValueEnumerationType;

/** The XTCETypesObject class extends the basic XTCENamedObject class to serve
 * by capturing the named objects that have specific type element associated
 * with them, such as Parameter, Arguments, and Members.
 *
 * @author b1053583
 *
 */

public abstract class XTCETypedObject extends XTCENamedObject {

    /** Constructor
     * 
     * Constructs a new instance of a named and typed object from the XTCE data
     * model.  These include Parameters, Arguments, and Members.
     *
     * @param name String containing the object name as referenced from the
     * getName() method on the JAXB generated object.
     *
     * @param spaceSystemPath String containing the Space System path leading
     * to this object in the XTCE data model, but without the object name.
     *
     * @param obj AliasSetType object from the JAXB generated classes.
     *
     * @param typeObj NameDescriptionType base class from the JAXB generated
     * classes that contains the specific type for this named and typed object.
     *
     */

    XTCETypedObject( String              name,
                     String              spaceSystemPath,
                     AliasSetType        obj,
                     NameDescriptionType typeObj ) {

        super( name, spaceSystemPath, obj );

        typeObj_ = typeObj;
        if ( typeObj_ != null ) {
            populateAliasListFromReference( typeObj_.getAliasSet() );
        }

    }

    /** Accessor to retrieve a reference to the Parameter or Argument Type
     * object that is kept as a reference for this typed and named object.
     *
     * @return NameDescriptionType object that serves as the base class for the
     * varies Parameter and Argument Types.  It will need to be downcast to
     * access certain attributes.
     *
     */

    public NameDescriptionType getTypeReference() {
        return typeObj_;
    }

    /** Accessor to determine if this named and types object is valid in the
     * XTCE document.
     *
     * @return boolean indicating if this is a valid named and typed object
     * based on whether the type is null.
     *
     */

    public boolean isValid() {
        return ( typeObj_ != null );
    }

    /** Retrieve the units of measure and the nature/description for this
     * named and typed object in the XTCE data model.
     *
     * Not all types have an associated unit.  Arrays, Structures/Aggregates,
     * do not.
     *
     * @todo Add Absolute/Relative time unit support.
     *
     * <b>
     * This function currently only supports 1 Unit element in the
     * UnitSet element of the XTCE data model.
     * </b>
     *
     * @return String containing the units:description, e.g. meters:distance
     * for this object, or an empty string in the event that there are no
     * Unit elements defined.
     *
     */

    public String getUnits() {

        if ( typeObj_ == null ) {
            return "";
        }

        List<UnitType> unitList = null;

        if ( isBaseDataType() == true ) {

            if ( ((BaseDataType)typeObj_).getUnitSet() != null ) {
                unitList = ((BaseDataType)typeObj_).getUnitSet().getUnit();
            }

        }

        // at present, only 1 supported
        if ( ( unitList != null ) && ( unitList.size() > 0 ) ) {
            String units = unitList.get( 0 ).getContent();
            if ( unitList.get( 0 ).getDescription() != null ) {
                units = units + ":" + unitList.get( 0 ).getDescription();
            }
            return units;
        }

        return "";

    }

    /** Accessor to retrieve the "initial" or "default" value of the type
     * represented by this object, considering the XTCE data model rules.
     *
     * @return The initial or default value of the type, or an empty
     * string.  This method endeavors to never return a null string.
     *
     */

    protected String getInitialValue() {

        if ( typeObj_ != null ) {
            if ( typeObj_.getClass() == EnumeratedParameterType.class ) {
                EnumeratedParameterType tRef = (EnumeratedParameterType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            } else if ( typeObj_.getClass() == IntegerParameterType.class ) {
                IntegerParameterType tRef = (IntegerParameterType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            } else if ( typeObj_.getClass() == FloatParameterType.class ) {
                FloatParameterType tRef = (FloatParameterType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue().toString();
                }
            } else if ( typeObj_.getClass() == BinaryParameterType.class ) {
                BinaryParameterType tRef = (BinaryParameterType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    byte[] bytes = tRef.getInitialValue();
                    StringBuilder out = new StringBuilder( "0x" );
                    for ( byte singleByte : bytes ) {
                        out.append( String.format( "%02x", singleByte ) );
                    }
                    return out.toString();
                }
            } else if ( typeObj_.getClass() == BooleanParameterType.class ) {
                BooleanParameterType tRef = (BooleanParameterType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            } else if ( typeObj_.getClass() == StringParameterType.class ) {
                StringParameterType tRef = (StringParameterType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            }  else if ( typeObj_.getClass() == RelativeTimeParameterType.class ) {
                RelativeTimeParameterType tRef = (RelativeTimeParameterType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue().toString();
                }

            } else if ( typeObj_.getClass() == EnumeratedDataType.class ) {
                EnumeratedDataType tRef = (EnumeratedDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            } else if ( typeObj_.getClass() == IntegerArgumentType.class ) {
                IntegerArgumentType tRef = (IntegerArgumentType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            } else if ( typeObj_.getClass() == FloatArgumentType.class ) {
                FloatArgumentType tRef = (FloatArgumentType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue().toString();
                }
            } else if ( typeObj_.getClass() == BinaryDataType.class ) {
                BinaryDataType tRef = (BinaryDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    byte[] bytes = tRef.getInitialValue();
                    StringBuilder out = new StringBuilder( "0x" );
                    for ( byte singleByte : bytes ) {
                        out.append( String.format( "%02x", singleByte ) );
                    }
                    return out.toString();
                }
            } else if ( typeObj_.getClass() == BooleanDataType.class ) {
                BooleanDataType tRef = (BooleanDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            } else if ( typeObj_.getClass() == StringDataType.class ) {
                StringDataType tRef = (StringDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }

            } else if ( typeObj_.getClass() == AbsoluteTimeDataType.class ) {
                AbsoluteTimeDataType tRef = (AbsoluteTimeDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue().toString();
                }
            } else if ( typeObj_.getClass() == RelativeTimeDataType.class ) {
                RelativeTimeDataType tRef = (RelativeTimeDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue().toString();
                }
            }
        }

        return "";

    }

    /** Accessor to retrieve the changeThreshold attribute from the encoding of
     * this Parameter, Argument, or Member, which is sometimes viewed as an
     * "aperture" or minimum "observability" for change.
     *
     * The changeThreshold attribute is only applicable for integer and float
     * encoding types.  Other types should generally be assumed that ANY change
     * is observable.
     *
     * @return String containing the value or an empty string if the value is
     * not set or is not applicable.  This function endeavors to never return
     * a null value.
     *
     */

    public String getChangeThreshold() {

        if ( typeObj_ == null ) {
            return "";
        }

        if ( isBaseDataType() == true ) {

            BaseDataType pTypeRef = (BaseDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getIntegerDataEncoding() != null ) {
                    return pTypeRef.getIntegerDataEncoding().getChangeThreshold().toString();
                } else if ( pTypeRef.getFloatDataEncoding() != null ) {
                    return pTypeRef.getFloatDataEncoding().getChangeThreshold().toString();
                } else if ( pTypeRef.getBinaryDataEncoding() != null ) {
                    return "";
                } else if ( pTypeRef.getStringDataEncoding() != null ) {
                    return "";
                }
            }

        } else if ( isBaseTimeDataType() == true ) {

            BaseTimeDataType pTypeRef = (BaseTimeDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getEncoding() != null ) {
                    if ( pTypeRef.getEncoding().getIntegerDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getIntegerDataEncoding().getChangeThreshold().toString();
                    } else if ( pTypeRef.getEncoding().getFloatDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getFloatDataEncoding().getChangeThreshold().toString();
                    } else if ( pTypeRef.getEncoding().getBinaryDataEncoding() != null ) {
                        return "";
                    } else if ( pTypeRef.getEncoding().getStringDataEncoding() != null ) {
                        return "";
                    }
                }
            }

        }

        return "";

    }

    /** Accessor to retrieve the ValidRange element information from this
     * named and typed object in the XTCE data model.
     *
     * @return XTCEValidRange containing the information gathered.
     *
     */

    public XTCEValidRange getValidRange() {
        return new XTCEValidRange( typeObj_ );
    }

    /** Accessor to return a descriptive name for the Engineering or Operator
     * type of this Parameter, Argument, or Member represented by this object.
     *
     * The Type element is not a perfect mapping to the return value of this
     * function.  The possible values are:
     *
     * <ul>
     * <li>
     * ENUMERATED - Represents Enumerated Type
     * </li>
     * <li>
     * SIGNED - Represents Integer Type with signed="true"
     * </li>
     * <li>
     * UNSIGNED - Represents Integer Type with signed="false"
     * </li>
     * <li>
     * FLOAT32 - Represents Float Type with sizeInBits="32"
     * </li>
     * <li>
     * FLOAT64 - Represents Float Type with sizeInBits="64"
     * </li>
     * <li>
     * FLOAT128 - Represents Float Type with sizeInBits="128"
     * </li>
     * <li>
     * STRUCTURE - Represents an Aggregate Type
     * </li>
     * <li>
     * BINARY - Represents a Binary Type
     * </li>
     * <li>
     * BOOLEAN - Represents a Boolean Type
     * </li>
     * <li>
     * STRING - Represents a String Type
     * </li>
     * <li>
     * TIME - Represents an Absolute Time Type
     * </li>
     * <li>
     * DURATION - Represents a Relative Time Type
     * </li>
     * <li>
     * ARRAY - Represents an Array Type
     * </li>
     * </ul>
     *
     * @return String containing the abstracted name of the Engineering or
     * Operator type associated with this Parameter, Argument, or Member, or an
     * empty string if that is not located.  This function endeavors to never
     * return a null value.
     *
     */

    public String getEngineeringType() {

        if ( typeObj_ != null ) {
            if ( ( typeObj_.getClass() == EnumeratedParameterType.class ) ||
                 ( typeObj_.getClass() == EnumeratedDataType.class )      ) {
                return "ENUMERATED";
            } else if ( typeObj_.getClass() == IntegerParameterType.class ) {
                IntegerParameterType tRef = (IntegerParameterType)typeObj_;
                return ( tRef.isSigned() == true ? "SIGNED" : "UNSIGNED" );
            } else if ( typeObj_.getClass() == IntegerArgumentType.class ) {
                IntegerArgumentType tRef = (IntegerArgumentType)typeObj_;
                return ( tRef.isSigned() == true ? "SIGNED" : "UNSIGNED" );
            } else if ( typeObj_.getClass() == FloatParameterType.class ) {
                FloatParameterType tRef = (FloatParameterType)typeObj_;
                return "FLOAT" + tRef.getSizeInBits().toString();
            } else if ( typeObj_.getClass() == FloatArgumentType.class ) {
                FloatArgumentType tRef = (FloatArgumentType)typeObj_;
                return "FLOAT" + tRef.getSizeInBits().toString();
            } else if ( typeObj_.getClass() == AggregateDataType.class ) {
                return "STRUCTURE";
            } else if ( ( typeObj_.getClass() == BinaryParameterType.class ) ||
                        ( typeObj_.getClass() == BinaryDataType.class )      ) {
                return "BINARY";
            } else if ( ( typeObj_.getClass() == BooleanParameterType.class ) ||
                        ( typeObj_.getClass() == BooleanDataType.class )      ) {
                return "BOOLEAN";
            } else if ( ( typeObj_.getClass() == StringParameterType.class ) ||
                        ( typeObj_.getClass() == StringDataType.class )      ) {
                return "STRING";
            } else if ( typeObj_.getClass() == AbsoluteTimeDataType.class ) {
                return "TIME";
            } else if ( ( typeObj_.getClass() == RelativeTimeParameterType.class ) ||
                        ( typeObj_.getClass() == RelativeTimeDataType.class )      ) {
                return "DURATION";
            } else if ( typeObj_.getClass() == ArrayDataTypeType.class ) {
                return "ARRAY";
            }
        }

        return "";

    }

    /** Retrieves a short name for the raw encoding type of this named and
     * typed object in the XTCE data model.
     *
     * Currently recognized raw encoding types consist of:
     *
     * <ul>
     * <li>
     * unsigned - Unsigned Integer
     * </li>
     * <li>
     * signMagnitude - Signed Integer using high bit to indicate sign
     * </li>
     * <li>
     * onesComplement - Signed Integer using ones complement formula
     * </li>
     * <li>
     * twosComplement - Signed Integer using twos complement formula
     * </li>
     * <li>
     * BCD - Binary Coded Data
     * </li>
     * <li>
     * packedBCD - Packed Binary Coded Data
     * </li>
     * <li>
     * binary - Plain binary data field
     * </li>
     * <li>
     * IEEE754_1985 - Most common floating point encoding approximation
     * </li>
     * <li>
     * MILSTD_1750A - MIL Standard floating point encoding approximation
     * </li>
     * <li>
     * UTF-8 - 8 Bit String Encoding
     * </li>
     * <li>
     * UTF-16 - 16 Bit String Encoding (wide characters)
     * </li>
     * </ul>
     *
     * @return String containing the short type name.  The function endeavors
     * to never return null.  If no type is recognized, it will return an
     * empty string.
     *
     */

    public String getRawType() {

        if ( typeObj_ == null ) {
            return "";
        }

        if ( isBaseDataType() == true ) {

            BaseDataType pTypeRef = (BaseDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getIntegerDataEncoding() != null ) {
                    return pTypeRef.getIntegerDataEncoding().getEncoding();
                } else if ( pTypeRef.getFloatDataEncoding() != null ) {
                    return pTypeRef.getFloatDataEncoding().getEncoding();
                } else if ( pTypeRef.getBinaryDataEncoding() != null ) {
                    return "binary";
                } else if ( pTypeRef.getStringDataEncoding() != null ) {
                    return pTypeRef.getStringDataEncoding().getEncoding();
                }
            }

        } else if ( isBaseTimeDataType() == true ) {

            BaseTimeDataType pTypeRef = (BaseTimeDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getEncoding() != null ) {
                    if ( pTypeRef.getEncoding().getIntegerDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getIntegerDataEncoding().getEncoding();
                    } else if ( pTypeRef.getEncoding().getFloatDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getFloatDataEncoding().getEncoding();
                    } else if ( pTypeRef.getEncoding().getBinaryDataEncoding() != null ) {
                        return "binary";
                    } else if ( pTypeRef.getEncoding().getStringDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getStringDataEncoding().getEncoding();
                    }
                }
            }

        }

        return "";

    }

    /** Accessor to determine the bit ordering from the encoding node of the
     * Type that describes this Parameter, Argument, or Member element.
     *
     * @return String containing "mostSignificantBitFirst" or the opposite,
     * "leastSignificantBitFirst".  The returned string will never be null.
     *
     */

    public String getRawBitOrder() {

        if ( typeObj_ == null ) {
            return "";
        }

        if ( isBaseDataType() == true ) {

            BaseDataType pTypeRef = (BaseDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getIntegerDataEncoding() != null ) {
                    return pTypeRef.getIntegerDataEncoding().getBitOrder();
                } else if ( pTypeRef.getFloatDataEncoding() != null ) {
                    return pTypeRef.getFloatDataEncoding().getBitOrder();
                } else if ( pTypeRef.getBinaryDataEncoding() != null ) {
                    return pTypeRef.getBinaryDataEncoding().getBitOrder();
                } else if ( pTypeRef.getStringDataEncoding() != null ) {
                    return pTypeRef.getStringDataEncoding().getBitOrder();
                }
            }

        } else if ( isBaseTimeDataType() == true ) {

            BaseTimeDataType pTypeRef = (BaseTimeDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getEncoding() != null ) {
                    if ( pTypeRef.getEncoding().getIntegerDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getIntegerDataEncoding().getBitOrder();
                    } else if ( pTypeRef.getEncoding().getFloatDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getFloatDataEncoding().getBitOrder();
                    } else if ( pTypeRef.getEncoding().getBinaryDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getBinaryDataEncoding().getBitOrder();
                    } else if ( pTypeRef.getEncoding().getStringDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getStringDataEncoding().getBitOrder();
                    }
                }
            }

        }

        return "mostSignificantBitFirst";

    }

    /** Retrieves the size in bits of the raw encoded value for this type of
     * Parameter, Argument, or Member.
     *
     * @return String containing the number of bits or the word "dynamic" if
     * the count is variable.  String can also be empty if this type is not
     * valid.  In that case, the user should test for validity of this type
     * using the isValid() method.
     *
     */

    public String getRawSizeInBits() {

        if ( typeObj_ == null ) {
            return "";
        }

        if ( isBaseDataType() == true ) {

            BaseDataType pTypeRef = (BaseDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getIntegerDataEncoding() != null ) {
                    return pTypeRef.getIntegerDataEncoding().getSizeInBits().toString();
                } else if ( pTypeRef.getFloatDataEncoding() != null ) {
                    return pTypeRef.getFloatDataEncoding().getSizeInBits().toString();
                } else if ( pTypeRef.getBinaryDataEncoding() != null ) {
                    IntegerValueType sizeDetail = pTypeRef.getBinaryDataEncoding().getSizeInBits();
                    if ( sizeDetail != null ) {
                        if ( sizeDetail.getFixedValue() != null ) {
                            return sizeDetail.getFixedValue();
                        } else {
                            return "dynamic";
                        }
                    }
                } else if ( pTypeRef.getStringDataEncoding() != null ) {
                    StringDataEncodingType.SizeInBits sizeElement = pTypeRef.getStringDataEncoding().getSizeInBits();
                    if ( sizeElement != null ) {
                        IntegerValueType sizeDetail = sizeElement.getFixed();
                        if ( sizeDetail != null ) {
                            if ( sizeDetail.getFixedValue() != null ) {
                                return sizeDetail.getFixedValue();
                            } else {
                                return "dynamic";
                            }
                        } else {
                            return "dynamic";
                        }
                    }
                }
            }

        } else if ( isBaseTimeDataType() == true ) {

            BaseTimeDataType pTypeRef = (BaseTimeDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getEncoding() != null ) {
                    if ( pTypeRef.getEncoding().getIntegerDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getIntegerDataEncoding().getSizeInBits().toString();
                    } else if ( pTypeRef.getEncoding().getFloatDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getFloatDataEncoding().getSizeInBits().toString();
                    } else if ( pTypeRef.getEncoding().getBinaryDataEncoding() != null ) {
                        IntegerValueType sizeDetail = pTypeRef.getEncoding().getBinaryDataEncoding().getSizeInBits();
                        if ( sizeDetail != null ) {
                            if ( sizeDetail.getFixedValue() != null ) {
                                return sizeDetail.getFixedValue();
                            } else {
                                return "dynamic";
                            }
                        }
                    } else if ( pTypeRef.getEncoding().getStringDataEncoding() != null ) {
                        StringDataEncodingType.SizeInBits sizeElement = pTypeRef.getEncoding().getStringDataEncoding().getSizeInBits();
                        if ( sizeElement != null ) {
                            IntegerValueType sizeDetail = sizeElement.getFixed();
                            if ( sizeDetail != null ) {
                                if ( sizeDetail.getFixedValue() != null ) {
                                    return sizeDetail.getFixedValue();
                                } else {
                                    return "dynamic";
                                }
                            } else {
                                return "dynamic";
                            }
                        }
                    }
                }
            }

        }

        return "";

    }

    /** Retrieves the enumerations for the Parameter, Argument, or Member
     * represented by this type.
     *
     * This method currently provides direct access to the JAXB List of
     * ValueEnumerationType objects because it is not apparent that wrapping
     * this with anything makes any added value.  There is no corresponding
     * setter method because the direct reference can be modified.
     *
     * @return List of ValueEnumerationType objects.  The return will be null
     * in the event that the underlying type is not an Enumerated Type, which
     * can be tested with the getEngineeringType() method.
     *
     */

    public List<ValueEnumerationType> getEnumerations() {

        if ( typeObj_ == null ) {
            return null;
        } else if ( typeObj_.getClass() == EnumeratedParameterType.class ) {
            return ((EnumeratedParameterType)typeObj_).getEnumerationList().getEnumeration();
        } else if ( typeObj_.getClass() == EnumeratedDataType.class ) {
            return ((EnumeratedDataType)typeObj_).getEnumerationList().getEnumeration();
        }
        return new ArrayList<ValueEnumerationType>();

    }

    /** Retrieves the CalibratorType from the JAXB generated classes that is
     * attached to this type, or null if this type has no Calibrators.
     *
     * @return CalibratorType containing the Default and Context Calibrator
     * elements for this type, or a null pointer if there aren't any.
     *
     */

    public CalibratorType getDefaultCalibrator() {

        if ( typeObj_ == null ) {
            return null;
        }

        if ( isBaseDataType() == true ) {

            BaseDataType pTypeRef = (BaseDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getIntegerDataEncoding() != null ) {
                    return pTypeRef.getIntegerDataEncoding().getDefaultCalibrator();
                } else if ( pTypeRef.getFloatDataEncoding() != null ) {
                    return pTypeRef.getFloatDataEncoding().getDefaultCalibrator();
                }
            }

        } else if ( isBaseTimeDataType() == true ) {

            BaseTimeDataType pTypeRef = (BaseTimeDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getEncoding() != null ) {
                    if ( pTypeRef.getEncoding().getIntegerDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getIntegerDataEncoding().getDefaultCalibrator();
                    } else if ( pTypeRef.getEncoding().getFloatDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getFloatDataEncoding().getDefaultCalibrator();
                    }
                }
            }

        }

        return null;

    }

    /** Private method to determine if the type is a BaseDataType in the XTCE
     * data model.
     *
     * @return boolean indicating if it is safe to cast this type to an XTCE
     * BaseDataType from the JAXB generated classes.
     *
     */

    private boolean isBaseDataType() {

        return ( ( typeObj_.getClass() == IntegerParameterType.class )    ||
                 ( typeObj_.getClass() == EnumeratedParameterType.class ) ||
                 ( typeObj_.getClass() == FloatParameterType.class )      ||
                 ( typeObj_.getClass() == BinaryParameterType.class )     ||
                 ( typeObj_.getClass() == BooleanParameterType.class )    ||
                 ( typeObj_.getClass() == StringParameterType.class )     ||
                 ( typeObj_.getClass() == IntegerArgumentType.class )     ||
                 ( typeObj_.getClass() == EnumeratedDataType.class )      ||
                 ( typeObj_.getClass() == FloatArgumentType.class )       ||
                 ( typeObj_.getClass() == BinaryDataType.class )          ||
                 ( typeObj_.getClass() == BooleanDataType.class )         ||
                 ( typeObj_.getClass() == StringDataType.class )          );

    }

    /** Private method to determine if the type is a BaseTimeDataType in the
     * XTCE data model.
     *
     * @return boolean indicating if it is safe to cast this type to an XTCE
     * BaseTimeDataType from the JAXB generated classes.
     *
     */

    private boolean isBaseTimeDataType() {

        return ( ( typeObj_.getClass() == AbsoluteTimeDataType.class )      ||
                 ( typeObj_.getClass() == RelativeTimeParameterType.class ) ||
                 ( typeObj_.getClass() == RelativeTimeDataType.class )      );

    }

    // Private Data Members

    private NameDescriptionType typeObj_ = null;

}
