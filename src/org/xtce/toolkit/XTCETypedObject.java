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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.omg.space.xtce.AbsoluteTimeDataType;
import org.omg.space.xtce.AggregateDataType;
import org.omg.space.xtce.AliasSetType;
import org.omg.space.xtce.ArrayDataTypeType;
import org.omg.space.xtce.BaseDataType;
import org.omg.space.xtce.BaseTimeDataType;
import org.omg.space.xtce.BinaryDataType;
import org.omg.space.xtce.BooleanDataType;
import org.omg.space.xtce.CalibratorType;
import org.omg.space.xtce.ContextCalibratorType;
import org.omg.space.xtce.DescriptionType.AncillaryDataSet;
import org.omg.space.xtce.DescriptionType.AncillaryDataSet.AncillaryData;
import org.omg.space.xtce.EnumeratedDataType;
import org.omg.space.xtce.FloatDataType;
import org.omg.space.xtce.IntegerDataType;
import org.omg.space.xtce.IntegerValueType;
import org.omg.space.xtce.NameDescriptionType;
import org.omg.space.xtce.RelativeTimeDataType;
import org.omg.space.xtce.StringDataEncodingType;
import org.omg.space.xtce.StringDataType;
import org.omg.space.xtce.UnitType;
import org.omg.space.xtce.ValueEnumerationType;

/** The XTCETypedObject class extends the basic XTCENamedObject class to serve
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

    XTCETypedObject( final String              name,
                     final String              spaceSystemPath,
                     final AliasSetType        obj,
                     final AncillaryDataSet    anc,
                     final NameDescriptionType typeObj ) {

        super( name, spaceSystemPath, obj, anc );

        typeObj_ = typeObj;

        if ( typeObj_ != null ) {
            if ( typeObj_ instanceof EnumeratedDataType ) {
                dataType_ = DataType.EnumeratedDataType;
            } else if ( typeObj_ instanceof IntegerDataType ) {
                dataType_ = DataType.IntegerDataType;
            } else if ( typeObj_ instanceof FloatDataType ) {
                dataType_ = DataType.FloatDataType;
            } else if ( typeObj_ instanceof BinaryDataType ) {
                dataType_ = DataType.BinaryDataType;
            } else if ( typeObj_ instanceof BooleanDataType ) {
                dataType_ = DataType.BooleanDataType;
            } else if ( typeObj_ instanceof StringDataType ) {
                dataType_ = DataType.StringDataType;
            } else if ( typeObj_ instanceof AggregateDataType ) {
                dataType_ = DataType.AggregateDataType;
            } else if ( typeObj_ instanceof AbsoluteTimeDataType ) {
                dataType_ = DataType.AbsoluteTimeDataType;
            } else if ( typeObj_ instanceof RelativeTimeDataType ) {
                dataType_ = DataType.RelativeTimeDataType;
            } else if ( typeObj_ instanceof ArrayDataTypeType ) {
                dataType_ = DataType.ArrayDataTypeType;
            } else {
                dataType_ = null;
            }
        } else {
            dataType_ = null;
        }

        // this overloads the previously defined alias list that was determined
        // from the named object with additions from the typed object.

        if ( typeObj_ != null ) {

            populateAliasListFromReference( typeObj_.getAliasSet() );

            // if the AncillaryDataSet is not null, then XTCE requires
            // content from getAncillaryData(), so no second null check needed.

            if ( typeObj_.getAncillaryDataSet() != null ) {
                typeAncDataList_ =
                    typeObj_.getAncillaryDataSet().getAncillaryData();
            } else {
                typeAncDataList_ = null;
            }

        } else {
            typeAncDataList_ = null;
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

    public final NameDescriptionType getTypeReference() {
        return typeObj_;
    }

    /** Accessor to determine if this named and types object is valid in the
     * XTCE document.
     *
     * @return boolean indicating if this is a valid named and typed object
     * based on whether the type is null.
     *
     */

    public final boolean isValid() {
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
    public List<AncillaryData> getAncillaryData( final String nameGlob ) {

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

    public final String getUnits() {

        if ( typeObj_ == null ) {
            return ""; // NOI18N
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
                units = units + ":" + unitList.get( 0 ).getDescription(); // NOI18N
            }
            return units;
        }

        return ""; // NOI18N

    }

    /** Accessor to retrieve the "initial" or "default" value of the type
     * represented by this object, considering the XTCE data model rules.
     *
     * @return The initial or default value of the type, or an empty
     * string.  This method endeavors to never return a null string.
     *
     */

    protected String getInitialValue() {

        if ( typeObj_ == null || dataType_ == null ) {
            return ""; // NOI18N
        }

        switch ( dataType_ ) {

            case IntegerDataType: {
                IntegerDataType tRef = (IntegerDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
                break;
            }

            case EnumeratedDataType: {
                EnumeratedDataType tRef = (EnumeratedDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
                break;
            }

            case FloatDataType: {
                FloatDataType tRef = (FloatDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue().toString();
                }
                break;
            }

            case BinaryDataType: {
                BinaryDataType tRef = (BinaryDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    byte[] bytes = tRef.getInitialValue();
                    StringBuilder out = new StringBuilder( "0x" ); // NOI18N
                    for ( byte singleByte : bytes ) {
                        out.append( String.format( "%02x", singleByte ) ); // NOI18N
                    }
                    return out.toString();
                }
                break;
            }

            case BooleanDataType: {
                BooleanDataType tRef = (BooleanDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
                break;
            }

            case StringDataType: {
                StringDataType tRef = (StringDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue();
                }
                break;
            }

            case AbsoluteTimeDataType: {
                AbsoluteTimeDataType tRef = (AbsoluteTimeDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue().toString();
                }
                break;
            }

            case RelativeTimeDataType: {
                RelativeTimeDataType tRef = (RelativeTimeDataType)typeObj_;
                if ( tRef.getInitialValue() != null ) {
                    return tRef.getInitialValue().toString();
                }
                break;
            }

        }

        return ""; // NOI18N

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
            return ""; // NOI18N
        }

        Method ctm;
        Object cth;

        if ( isBaseDataType() == true ) {

            BaseDataType pTypeRef = (BaseDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getIntegerDataEncoding() != null ) {
                    try {
                        ctm = pTypeRef.getIntegerDataEncoding().
                                       getClass().
                                       getDeclaredMethod( "getChangeThreshold" );
                        cth = ctm.invoke( pTypeRef.getIntegerDataEncoding(),
                                          (Object[])null );
                    } catch ( Exception ex ) {
                        // the @changeThreshold attribute does not exist in
                        // XTCE 1.1, although it will in 1.2 and later.
                        return "";
                    }
                    if ( cth != null ) {
                        return ((BigInteger)cth).toString();
                    } else {
                        return ""; // NOI18N
                    }
                } else if ( pTypeRef.getFloatDataEncoding() != null ) {
                    try {
                        ctm = pTypeRef.getFloatDataEncoding().
                                       getClass().
                                       getDeclaredMethod( "getChangeThreshold" );
                        cth = ctm.invoke( pTypeRef.getFloatDataEncoding(),
                                          (Object[])null );
                    } catch ( Exception ex ) {
                        // the @changeThreshold attribute does not exist in
                        // XTCE 1.1, although it will in 1.2 and later.
                        return "";
                    }
                    if ( cth != null ) {
                        return ((BigDecimal)cth).toString();
                    } else {
                        return ""; // NOI18N
                    }
                } else if ( pTypeRef.getBinaryDataEncoding() != null ) {
                    return ""; // NOI18N
                } else if ( pTypeRef.getStringDataEncoding() != null ) {
                    return ""; // NOI18N
                }
            }

        } else if ( isBaseTimeDataType() == true ) {

            BaseTimeDataType pTypeRef = (BaseTimeDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getEncoding() != null ) {
                    if ( pTypeRef.getEncoding().getIntegerDataEncoding() != null ) {
                        try {
                            ctm = pTypeRef.getEncoding().
                                           getIntegerDataEncoding().
                                           getClass().
                                           getDeclaredMethod( "getChangeThreshold" );
                            cth = ctm.invoke( pTypeRef.getEncoding().
                                                       getIntegerDataEncoding(),
                                              (Object[])null );
                        } catch ( Exception ex ) {
                            // the @changeThreshold attribute does not exist in
                            // XTCE 1.1, although it will in 1.2 and later.
                            return "";
                        }
                        if ( cth != null ) {
                            return ((BigInteger)cth).toString();
                        } else {
                            return ""; // NOI18N
                        }
                    } else if ( pTypeRef.getEncoding().getFloatDataEncoding() != null ) {
                        try {
                            ctm = pTypeRef.getEncoding().
                                           getFloatDataEncoding().
                                           getClass().
                                           getDeclaredMethod( "getChangeThreshold" );
                            cth = ctm.invoke( pTypeRef.getEncoding().
                                                       getFloatDataEncoding(),
                                              (Object[])null );
                        } catch ( Exception ex ) {
                            // the @changeThreshold attribute does not exist in
                            // XTCE 1.1, although it will in 1.2 and later.
                            return "";
                        }
                        if ( cth != null ) {
                            return ((BigDecimal)cth).toString();
                        } else {
                            return ""; // NOI18N
                        }
                    } else if ( pTypeRef.getEncoding().getBinaryDataEncoding() != null ) {
                        return ""; // NOI18N
                    } else if ( pTypeRef.getEncoding().getStringDataEncoding() != null ) {
                        return ""; // NOI18N
                    }
                }
            }

        }

        return ""; // NOI18N

    }

    /** Accessor to retrieve the ValidRange element information from this
     * named and typed object in the XTCE data model.
     *
     * @return XTCEValidRange containing the information gathered.
     *
     */

    public final XTCEValidRange getValidRange() {
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

    public final String getEngineeringTypeString() {

        if ( typeObj_ == null || dataType_ == null ) {
            return ""; // NOI18N
        }

        switch ( dataType_ ) {

            case IntegerDataType: {
                IntegerDataType tRef = (IntegerDataType)typeObj_;
                return ( tRef.isSigned() == true ? "SIGNED" : "UNSIGNED" ); // NOI18N
            }

            case EnumeratedDataType:
                return "ENUMERATED"; // NOI18N

            case FloatDataType: {
                FloatDataType tRef = (FloatDataType)typeObj_;
                return "FLOAT" + tRef.getSizeInBits().toString(); // NOI18N
            }

            case BinaryDataType:
                return "BINARY"; // NOI18N

            case AggregateDataType:
                return "STRUCTURE"; // NOI18N

            case BooleanDataType:
                return "BOOLEAN"; // NOI18N

            case StringDataType:
                return "STRING"; // NOI18N

            case AbsoluteTimeDataType:
                return "TIME"; // NOI18N

            case RelativeTimeDataType:
                return "DURATION"; // NOI18N

            case ArrayDataTypeType:
                return "ARRAY"; // NOI18N

        }

        return ""; // NOI18N

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
     * @return EngineeringType enumeration containing the abstracted name of
     * the Engineering or Operator type associated with this Parameter,
     * Argument, or Member.
     *
     */

    public final EngineeringType getEngineeringType() {

        String eType = getEngineeringTypeString();
        if ( eType.isEmpty() == true ) {
            return EngineeringType.UNKNOWN;
        }
        return EngineeringType.valueOf( eType );

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

    public final String getRawTypeString() {

        if ( typeObj_ == null ) {
            return ""; // NOI18N
        }

        if ( isBaseDataType() == true ) {

            BaseDataType pTypeRef = (BaseDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getIntegerDataEncoding() != null ) {
                    return pTypeRef.getIntegerDataEncoding().getEncoding();
                } else if ( pTypeRef.getFloatDataEncoding() != null ) {
                    return pTypeRef.getFloatDataEncoding().getEncoding();
                } else if ( pTypeRef.getBinaryDataEncoding() != null ) {
                    return "binary"; // NOI18N
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
                        return "binary"; // NOI18N
                    } else if ( pTypeRef.getEncoding().getStringDataEncoding() != null ) {
                        return pTypeRef.getEncoding().getStringDataEncoding().getEncoding();
                    }
                }
            }

        }

        return ""; // NOI18N

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
     * @return RawType enumeration containing the short type name.  The
     * function endeavors to never return null.
     *
     */

    public final RawType getRawType() {

        String rType = getRawTypeString();
        if ( rType.isEmpty() == true ) {
            return RawType.UNKNOWN;
        } else {
            rType = rType.replaceAll( "-", "" ); // NOI18N
        }
        return RawType.valueOf( rType );

    }

    /** Accessor to determine the bit ordering from the encoding node of the
     * Type that describes this Parameter, Argument, or Member element.
     *
     * @return String containing "mostSignificantBitFirst" or the opposite,
     * "leastSignificantBitFirst".  The returned string will never be null.
     *
     */

    public final String getRawBitOrder() {

        if ( typeObj_ == null ) {
            return ""; // NOI18N
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

        return "mostSignificantBitFirst"; // NOI18N

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

    public final String getRawSizeInBits() {

        if ( typeObj_ == null ) {
            return ""; // NOI18N
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
                            return "dynamic"; // NOI18N
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
                                return "dynamic"; // NOI18N
                            }
                        } else {
                            return "dynamic"; // NOI18N
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
                                return "dynamic"; // NOI18N
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
                                    return "dynamic"; // NOI18N
                                }
                            } else {
                                return "dynamic"; // NOI18N
                            }
                        }
                    }
                }
            }

        }

        return ""; // NOI18N

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

    public final List<ValueEnumerationType> getEnumerations() {

        if ( typeObj_ == null ) {
            return null;
        } else if ( dataType_ == DataType.EnumeratedDataType ) {
            return ((EnumeratedDataType)typeObj_).getEnumerationList()
                                                 .getEnumeration();
        }

        return new ArrayList<>();

    }

    /** Retrieves the default CalibratorType from the JAXB generated classes
     * that is attached to this type, or null if this type has no default
     * Calibrator.
     *
     * @return CalibratorType containing the Default Calibrator element for
     * this type, or a null pointer if there is not a default calibrator.
     *
     */

    public final CalibratorType getDefaultCalibrator() {

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

    /** Method to determine if this typed object has a default calibrator.
     *
     * @return boolean true if a default calibrator exists or false if not.
     *
     */

    public final boolean hasDefaultCalibrator() {
        return ( getDefaultCalibrator() != null );
    }

    /** Retrieves the List of ContextCalibratorType elements from the JAXB
     * generated classes that is attached to this type.
     *
     * @return List of ContextCalibratorType elements for this type, or a null
     * pointer if there aren't any context calibrators.
     *
     */

    public final List<ContextCalibratorType> getContextCalibrators() {

        if ( typeObj_ == null ) {
            return null;
        }

        if ( isBaseDataType() == true ) {

            BaseDataType pTypeRef = (BaseDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getIntegerDataEncoding() != null ) {
                    try {
                       return pTypeRef.getIntegerDataEncoding()
                                      .getContextCalibratorList()
                                      .getContextCalibrator();
                    } catch ( NullPointerException ex ) {
                        // do nothing
                    }
                } else if ( pTypeRef.getFloatDataEncoding() != null ) {
                    try {
                        return pTypeRef.getFloatDataEncoding()
                                       .getContextCalibratorList()
                                       .getContextCalibrator();
                    } catch ( NullPointerException ex ) {
                        // do nothing
                    }
                }
            }

        } else if ( isBaseTimeDataType() == true ) {

            BaseTimeDataType pTypeRef = (BaseTimeDataType)typeObj_;

            if ( pTypeRef != null ) {
                if ( pTypeRef.getEncoding() != null ) {
                    if ( pTypeRef.getEncoding().getIntegerDataEncoding() != null ) {
                        try {
                            return pTypeRef.getEncoding()
                                           .getIntegerDataEncoding()
                                           .getContextCalibratorList()
                                           .getContextCalibrator();
                        } catch ( NullPointerException ex ) {
                            // do nothing
                        }
                    } else if ( pTypeRef.getEncoding().getFloatDataEncoding() != null ) {
                        try {
                            return pTypeRef.getEncoding()
                                           .getFloatDataEncoding()
                                           .getContextCalibratorList()
                                           .getContextCalibrator();
                        } catch ( NullPointerException ex ) {
                            // do nothing
                        }
                    }
                }
            }

        }

        return null;

    }

    /** Method to determine if this typed object has a list of context
     * calibrators.
     *
     * @return boolean true if a list exists or false if not.
     *
     */

    public final boolean hasContextCalibrators() {
        return ( getContextCalibrators() != null );
    }

    /** Retrieve an XML string that represents this typed object's "type"
     * element.
     *
     * @return String containing the XML fragment.
     *
     * @throws XTCEDatabaseException in the event that the XML could not be
     * marshaled to the string.
     *
     */

    @SuppressWarnings("unchecked")
    public String typeToXml() throws XTCEDatabaseException {

        NameDescriptionType type = getTypeReference();

        if ( type == null ) {
            throw new XTCEDatabaseException(
                getName() +
                ": " + // NOI18N
                XTCEFunctions.getText( "xml_marshal_notype" ) ); // NOI18N
        }

        QName elementName;

        if ( type instanceof StringDataType ) {
            elementName = new QName( "StringParameterType" );
        } else if ( type instanceof EnumeratedDataType ) {
            elementName = new QName( "EnumeratedParameterType" );
        } else if ( type instanceof IntegerDataType ) {
            elementName = new QName( "IntegerParameterType" );
        } else if ( type instanceof BinaryDataType ) {
            elementName = new QName( "BinaryParameterType" );
        } else if ( type instanceof FloatDataType ) {
            elementName = new QName( "FloatParameterType" );
        } else if ( type instanceof BooleanDataType ) {
            elementName = new QName( "BooleanParameterType" );
        } else if ( type instanceof RelativeTimeDataType ) {
            elementName = new QName( "RelativeTimeParameterType" );
        } else if ( type instanceof AbsoluteTimeDataType ) {
            elementName = new QName( "AbsoluteTimeParameterType" );
        } else if ( type instanceof ArrayDataTypeType ) {
            elementName = new QName( "ArrayParameterType" );
        } else if ( type instanceof AggregateDataType ) {
            elementName = new QName( "AggregateParameterType" );
        } else {
            elementName = new QName( "UNDEFINED" );
        }

        try {

            // this constructor warns unchecked on the typeObj_.getClass() and
            // I am not sure how to fix that right now.

            JAXBElement<?> xmlElement =
                new JAXBElement( elementName,
                                 type.getClass(),
                                 type );

            XTCEDocumentMarshaller mmm =
                new XTCEDocumentMarshaller( type.getClass(), true );

            return
                XTCEFunctions.xmlPrettyPrint( mmm.marshalToXml( xmlElement ) );

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException(
                getName() +
                ": " + // NOI18N
                XTCEFunctions.getText( "xml_marshal_error_type" ) + // NOI18N
                " '" + // NOI18N
                ex.getCause() +
                "'" ); // NOI18N
        }

    }

    /** Private method to determine if the type is a BaseDataType in the XTCE
     * data model.
     *
     * @return boolean indicating if it is safe to cast this type to an XTCE
     * BaseDataType from the JAXB generated classes.
     *
     */

    private boolean isBaseDataType() {

        switch ( dataType_ ) {
            case IntegerDataType:
            case EnumeratedDataType:
            case FloatDataType:
            case BinaryDataType:
            case StringDataType:
            case BooleanDataType:
                return true;
            default:
                return false;
        }

    }

    /** Private method to determine if the type is a BaseTimeDataType in the
     * XTCE data model.
     *
     * @return boolean indicating if it is safe to cast this type to an XTCE
     * BaseTimeDataType from the JAXB generated classes.
     *
     */

    private boolean isBaseTimeDataType() {

        switch ( dataType_ ) {
            case AbsoluteTimeDataType:
            case RelativeTimeDataType:
                return true;
            default:
                return false;
        }

    }

    // Private Data Members

    private final NameDescriptionType typeObj_;
    private final List<AncillaryData> typeAncDataList_;
    private final DataType            dataType_;


    /** Enumeration representing the Calibrated/Engineering type of an item
     * in the XTCE data model.
     *
     */

    public enum EngineeringType {

        /** Calibrated/Engineering type could not be determined.
         *
         */

        UNKNOWN,

        /** Calibrated/Engineering type is an Enumerated String.
         *
         */

        ENUMERATED,

        /** Calibrated/Engineering type is a signed integer.
         *
         */

        SIGNED,

        /** Calibrated/Engineering type is an unsigned integer.
         *
         */

        UNSIGNED,

        /** Calibrated/Engineering type is a single IEEE754 precision Floating
         * Point number.
         *
         */

        FLOAT32,

        /** Calibrated/Engineering type is a double IEEE754 precision Floating
         * Point number.
         *
         */

        FLOAT64,

        /** Calibrated/Engineering type is a quad IEEE754 precision Floating
         * Point number.
         *
         */

        FLOAT128,

        /** Calibrated/Engineering type is an Aggregate or C style Structure.
         *
         */

        STRUCTURE,

        /** Calibrated/Engineering type is a simple binary value.
         *
         */

        BINARY,

        /** Calibrated/Engineering type is a Boolean String, which is a trivial
         * two value case of an Enumerated type.
         *
         */

        BOOLEAN,

        /** Calibrated/Engineering type is a String value.
         *
         */

        STRING,

        /** Calibrated/Engineering type is an Absolute Time value.
         *
         */

        TIME,

        /** Calibrated/Engineering type is a duration, or relative time value.
         *
         */

        DURATION,

        /** Calibrated/Engineering type is an Array of an item of another type.
         *
         */

        ARRAY;

    }

    /** Enumeration representing the Raw/Encoding type of an item in the
     * XTCE data model.
     *
     */

    public enum RawType {

        /** Raw/Encoding type could not be determined.
         *
         */

        UNKNOWN,

        /** Raw/Encoding type is an integer of unsigned values.
         *
         */

        unsigned,

        /** Raw/Encoding type is an integer of signed values differentiated
         * by the value of the most significant bit.
         *
         */

        signMagnitude,

        /** Raw/Encoding type is an integer of signed values differentiated
         * by ones complement representation.
         *
         */

        onesComplement,

        /** Raw/Encoding type is an integer of signed values differentiated
         * by twos complement representation.
         *
         */

        twosComplement,

        /** Raw/Encoding type is Binary Coded Data.
         *
         * NOTE: Not yet supported by this toolkit.
         *
         */

        BCD,

        /** Raw/Encoding type is Packed Binary Coded Data.
         *
         * NOTE: Not yet supported by this toolkit.
         *
         */

        packedBCD,

        /** Raw/Encoding type is simple binary data.
         *
         */

        binary,

        /** Raw/Encoding type is an IEEE 754 floating point value.
         *
         */

        IEEE754_1985,

        /** Raw/Encoding type is a MIL-STD 1750A floating point value.
         *
         * NOTE: Not yet supported by this toolkit.
         *
         */

        MILSTD_1750A,

        /** Raw/Encoding type is a UTF-8 encoding string value.
         *
         */

        UTF8,

        /** Raw/Encoding type is a UTF-16 encoding string value.
         *
         * NOTE: Only partially supported by this toolkit.
         *
         */

        UTF16;

    }

    /** Private enumeration to capture the actual XTCE element data type for
     * this typed object, so that reflection is only performed once in the
     * constructor instead of in several methods.
     *
     */

    private enum DataType {

        IntegerDataType,
        EnumeratedDataType,
        FloatDataType,
        BinaryDataType,
        BooleanDataType,
        StringDataType,
        AbsoluteTimeDataType,
        RelativeTimeDataType,
        AggregateDataType,
        ArrayDataTypeType;

    }

}
