/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.omg.space.xtce.database.AbsoluteTimeDataType;
import org.omg.space.xtce.database.AggregateDataType;
import org.omg.space.xtce.database.AliasSetType;
import org.omg.space.xtce.database.ArrayDataTypeType;
import org.omg.space.xtce.database.BaseDataType;
import org.omg.space.xtce.database.BaseTimeDataType;
import org.omg.space.xtce.database.BinaryDataType;
import org.omg.space.xtce.database.BooleanDataType;
import org.omg.space.xtce.database.CalibratorType;
import org.omg.space.xtce.database.CalibratorType.SplineCalibrator;
import org.omg.space.xtce.database.DescriptionType.AncillaryDataSet;
import org.omg.space.xtce.database.DescriptionType.AncillaryDataSet.AncillaryData;
import org.omg.space.xtce.database.EnumeratedDataType;
import org.omg.space.xtce.database.FloatDataType;
import org.omg.space.xtce.database.IntegerDataType;
import org.omg.space.xtce.database.IntegerValueType;
import org.omg.space.xtce.database.NameDescriptionType;
import org.omg.space.xtce.database.PolynomialType;
import org.omg.space.xtce.database.PolynomialType.Term;
import org.omg.space.xtce.database.RelativeTimeDataType;
import org.omg.space.xtce.database.SplinePointType;
import org.omg.space.xtce.database.StringDataEncodingType;
import org.omg.space.xtce.database.StringDataType;
import org.omg.space.xtce.database.UnitType;
import org.omg.space.xtce.database.ValueEnumerationType;

/** The XTCETypesObject class extends the basic XTCENamedObject class to serve
 * by capturing the named objects that have specific type element associated
 * with them, such as Parameter, Arguments, and Members.
 *
 * @author David Overeem
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
                     AncillaryDataSet    anc,
                     NameDescriptionType typeObj ) {

        super( name, spaceSystemPath, obj, anc );

        typeObj_ = typeObj;

        // this overloads the previously defined alias list that was determined
        // from the named object with additions from the typed object.

        if ( typeObj_ != null ) {
            populateAliasListFromReference( typeObj_.getAliasSet() );
        }

        // if the AncillaryDataSet is not null, then XTCE requires there to be
        // content from getAncillaryData(), so no second null check needed.

        if ( typeObj.getAncillaryDataSet() != null ) {
           typeAncDataList_ = typeObj.getAncillaryDataSet().getAncillaryData();
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

    /** Accessor to retrieve the list of AncillaryData elements from the XTCE
     * model that apply to this typed object, but it a Parameter, Argument, or
     * Member.
     *
     * This accessor filters duplicates in preference for those that are
     * defined on the named object.  Duplicate is determined when both the
     * name attribute and the value are the same.
     *
     * @return List of AncillaryData elements that apply.  The caller cannot
     * differentiate from this function if those came from the named object or
     * the typed object.
     *
     */

    @Override
    public List<AncillaryData> getAncillaryData() {

        List<AncillaryData> namedObjAncData = super.getAncillaryData();
        List<AncillaryData> retList         = namedObjAncData;

        if ( typeAncDataList_ == null ) {
            return retList;
        }

        for ( AncillaryData ancData : typeAncDataList_ ) {

            String  nameAttr = ancData.getName();
            String  value    = ancData.getValue();
            boolean found    = false;

            for ( AncillaryData namedAncData : namedObjAncData ) {
                String namedNameAttr = namedAncData.getName();
                String namedValue    = namedAncData.getValue();
                if ( nameAttr.equals( namedNameAttr ) == true ) {
                    if ( value != null && namedValue != null ) {
                        if ( value.equals( namedValue ) == true ) {
                            found = true;
                        }
                    } else {
                        found = true;
                    }
                }
            }

            if ( found == false ) {
                retList.add( ancData );
            }

        }

        return retList;

    }

    /** Accessor to retrieve only a partial list of AncillaryData elements from
     * the XTCE data for this typed object where the name attribute of the
     * AncillaryData element must either exactly match a string or a glob style
     * pattern.
     *
     * Since the name attribute of an AncillaryData element does not qualify
     * uniqueness, multiple results can still be retrieved when doing an exact
     * match.
     *
     * @param nameGlob String containing a precise name or a glob style pattern
     * to match against the name attribute of the AncillaryData elements.
     *
     * @return List of AncillaryData elements that match.  The list can be
     * empty but it will never be null.
     *
     */

    @Override
    public List<AncillaryData> getAncillaryData( String nameGlob ) {

        ArrayList<AncillaryData> list = new ArrayList<>();

        for ( AncillaryData element : getAncillaryData() ) {
            if ( XTCEFunctions.matchesUsingGlob( element.getName(), nameGlob) == true ) {
                list.add( element );
            }
        }

        return list;

    }

    /** Retrieve the units of measure and the nature/description for this
     * named and typed object in the XTCE data model.
     *
     * Not all types have an associated unit.  Arrays, Structures/Aggregates,
     * do not.
     *
     * TODO: Add Absolute/Relative time unit support.
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
            if ( typeObj_ instanceof EnumeratedDataType ) {
                EnumeratedDataType tRef = (EnumeratedDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            } else if ( typeObj_ instanceof IntegerDataType ) {
                IntegerDataType tRef = (IntegerDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            } else if ( typeObj_ instanceof FloatDataType ) {
                FloatDataType tRef = (FloatDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue().toString();
                }
            } else if ( typeObj_ instanceof BinaryDataType ) {
                BinaryDataType tRef = (BinaryDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    byte[] bytes = tRef.getInitialValue();
                    StringBuilder out = new StringBuilder( "0x" );
                    for ( byte singleByte : bytes ) {
                        out.append( String.format( "%02x", singleByte ) );
                    }
                    return out.toString();
                }
            } else if ( typeObj_ instanceof BooleanDataType ) {
                BooleanDataType tRef = (BooleanDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            } else if ( typeObj_ instanceof StringDataType ) {
                StringDataType tRef = (StringDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
            } else if ( typeObj_ instanceof AbsoluteTimeDataType ) {
                AbsoluteTimeDataType tRef = (AbsoluteTimeDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue().toString();
                }
            } else if ( typeObj_ instanceof RelativeTimeDataType ) {
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
                    if ( pTypeRef.getIntegerDataEncoding().getChangeThreshold() != null ) {
                        return pTypeRef.getIntegerDataEncoding().
                                        getChangeThreshold().
                                        toString();
                    } else {
                        return "";
                    }
                } else if ( pTypeRef.getFloatDataEncoding() != null ) {
                    if ( pTypeRef.getFloatDataEncoding().getChangeThreshold() != null ) {
                        return pTypeRef.getFloatDataEncoding().
                                        getChangeThreshold().
                                        toString();
                    } else {
                        return "";
                    }
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
                        if ( pTypeRef.getEncoding().getIntegerDataEncoding().getChangeThreshold() != null ) {
                            return pTypeRef.getEncoding().
                                            getIntegerDataEncoding().
                                            getChangeThreshold().
                                            toString();
                        } else {
                            return "";
                        }
                    } else if ( pTypeRef.getEncoding().getFloatDataEncoding() != null ) {
                        if ( pTypeRef.getEncoding().getFloatDataEncoding().getChangeThreshold() != null ) {
                            return pTypeRef.getEncoding().
                                            getFloatDataEncoding().
                                            getChangeThreshold().
                                            toString();
                        } else {
                            return "";
                        }
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
            if ( typeObj_ instanceof EnumeratedDataType ) {
                return "ENUMERATED";
            } else if ( typeObj_ instanceof IntegerDataType ) {
                IntegerDataType tRef = (IntegerDataType)typeObj_;
                return ( tRef.isSigned() == true ? "SIGNED" : "UNSIGNED" );
            } else if ( typeObj_ instanceof FloatDataType ) {
                FloatDataType tRef = (FloatDataType)typeObj_;
                return "FLOAT" + tRef.getSizeInBits().toString();
            } else if ( typeObj_.getClass() == AggregateDataType.class ) {
                return "STRUCTURE";
            } else if ( typeObj_ instanceof BinaryDataType ) {
                return "BINARY";
            } else if ( typeObj_ instanceof BooleanDataType ) {
                return "BOOLEAN";
            } else if ( typeObj_ instanceof StringDataType ) {
                return "STRING";
            } else if ( typeObj_ instanceof AbsoluteTimeDataType ) {
                return "TIME";
            } else if ( typeObj_ instanceof RelativeTimeDataType ) {
                return "DURATION";
            } else if ( typeObj_ instanceof ArrayDataTypeType ) {
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
        } else if ( typeObj_ instanceof EnumeratedDataType ) {
            return ((EnumeratedDataType)typeObj_).getEnumerationList()
                                                 .getEnumeration();
        }

        return new ArrayList<>();

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

        if ( typeObj_ == null ) {
            return false;
        }

        return ( ( typeObj_ instanceof IntegerDataType    ) ||
                 ( typeObj_ instanceof EnumeratedDataType ) ||
                 ( typeObj_ instanceof FloatDataType      ) ||
                 ( typeObj_ instanceof BinaryDataType     ) ||
                 ( typeObj_ instanceof BooleanDataType    ) ||
                 ( typeObj_ instanceof StringDataType     ) );

    }

    /** Private method to determine if the type is a BaseTimeDataType in the
     * XTCE data model.
     *
     * @return boolean indicating if it is safe to cast this type to an XTCE
     * BaseTimeDataType from the JAXB generated classes.
     *
     */

    private boolean isBaseTimeDataType() {

        if ( typeObj_ == null ) {
            return false;
        }

        return ( ( typeObj_ instanceof AbsoluteTimeDataType ) ||
                 ( typeObj_ instanceof RelativeTimeDataType ) );

    }

    // Private Data Members

    private NameDescriptionType typeObj_         = null;
    private List<AncillaryData> typeAncDataList_ = null;

}
