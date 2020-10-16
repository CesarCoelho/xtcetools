/* Copyright 2015 David Overeem (dovereem@startmail.com)
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

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.omg.space.xtce.AggregateDataType.MemberList.Member;
import org.omg.space.xtce.NameDescriptionType;
import org.omg.space.xtce.ParameterSetType.Parameter;

/** This class serves as a convenient container for representing TM and TC
 * Parameters in the XTCE data model by abstracting the mechanics of assembling
 * Parameter attributes that are derived from XTCE related rules of processing,
 * which can include processing of Parameter, Member, and Type elements.
 *
 * This class endeavors to generate a minimum of additional data, storing
 * almost entirely references to objects already created in other parts of the
 * data model.  An exception to this is the list of Aliases, which requires
 * some logic to arrive at.  This one time creation of the alias information
 * is expected to increase performance by reducing redundant need to deduce the
 * information.  If aliases are not used on the project, then this will have
 * minimal to no impact on the processing times.
 *
 * @author David Overeem
 *
 */

public class XTCEParameter extends XTCETypedObject {

    /** Constructor
     *
     * This is a constructor for an XTCEParameter object when the parameter of
     * interest is a Parameter element in the XTCE data model.
     *
     * @param name The name attribute of the Parameter element, which is kept
     * internally as a reference to that object.
     *
     * @param pRef The object reference to the Parameter element, which is kept
     * internally as a reference to that object.
     *
     * @param ptRef The object reference to the Parameter Type that is
     * applicable for this parameter in the XTCE data model.
     *
     */

    XTCEParameter( final String              name,
                   final String              path,
                   final Parameter           pRef,
                   final NameDescriptionType ptRef ) {

        super( name,
               path,
               pRef.getAliasSet(),
               pRef.getAncillaryDataSet(),
               ptRef );

        reference_       = pRef;
        memberReference_ = null;

    }

    /** Constructor
     *
     * This is a constructor for an XTCEParameter object when the parameter of
     * interest is a Member element in the XTCE data model.
     *
     * @param name The name attribute of the Member element, which is kept
     * internally as a reference to that object.
     *
     * @param pRef The object reference to the Member element, which is kept
     * internally as a reference to that object.
     *
     * @param ptRef The object reference to the Parameter Type that is
     * applicable for this parameter in the XTCE data model.
     *
     */

    XTCEParameter( final String              name,
                   final String              path,
                   final Member              pRef,
                   final NameDescriptionType ptRef ) {

        super( name, path, null, null, ptRef );

        reference_       = null;
        memberReference_ = pRef;

    }

    /** Retrieves the fully qualified XTCE path reference to the type
     * that describes this parameter.
     *
     * @return String containing the path or an empty string if there is an
     * error case and there is no type reference.
     *
     */

    public final String getTypeReferenceFullPath() {

        if ( isValid() == false ) {
            return ""; // NOI18N
        }

        if ( reference_ != null ) {
            return XTCEFunctions.resolvePathReference( getSpaceSystemPath(),
                                                       reference_.getParameterTypeRef() );
        } else if ( memberReference_ != null ) {
            return XTCEFunctions.resolvePathReference( getSpaceSystemPath(),
                                                       memberReference_.getTypeRef() );
        }

        return ""; // NOI18N

    }

    /** Retrieves the "effective" Short Description of this
     * Parameter or Member in the XTCE data model.
     *
     * The "effective" Short Description is defined as the shortDescription
     * attribute that first appears on the Parameter element, and if not
     * present, then it can fall back to the shortDescription attribute that is
     * on the Parameter Type element, and eventually fall back to returning an
     * empty string if neither of these contains a shortDescription attribute.
     *
     * @return String containing the Short Description of this Parameter or
     * Member, or an empty string if there is none.  This function endeavors
     * to never return null.
     *
     */

    public final String getShortDescription() {

        String parameterDescription = ""; // NOI18N

        if ( ( isMember() == true ) && ( getTypeReference() != null ) ) {
            parameterDescription = getTypeReference().getShortDescription();
        } else if ( isParameter() == true ) {
            parameterDescription = getPrimaryShortDescription( reference_ );
            if ( parameterDescription.isEmpty() == true ) {
                if ( getTypeReference() != null ) {
                    parameterDescription = getTypeReference().getShortDescription();
                }
            }
        }

        if ( parameterDescription == null ) {
            parameterDescription = ""; // NOI18N
        }

        return parameterDescription;

    }

    /** Retrieves the "effective" Long Description of this
     * Parameter or Member in the XTCE data model.
     *
     * The "effective" Long Description is defined as the LongDescription
     * element that first appears on the Parameter element, and if not
     * present, then it can fall back to the LongDescription element that is
     * on the Parameter Type element, and eventually fall back to returning an
     * empty string if neither of these contains a LongDescription element.
     *
     * @return String containing the Long Description of this Parameter or
     * Member, or an empty string if there is none.  This function endeavors
     * to never return null.
     *
     */

    public final String getLongDescription() {

        String parameterDescription = ""; // NOI18N

        if ( ( isMember() == true ) && ( getTypeReference() != null ) ) {
            parameterDescription = getTypeReference().getLongDescription();
        } else if ( isParameter() == true ) {
            parameterDescription = getPrimaryLongDescription( reference_ );
            if ( parameterDescription.isEmpty() == true ) {
                if ( getTypeReference() != null ) {
                    parameterDescription = getTypeReference().getLongDescription();
                }
            }
        }

        if ( parameterDescription == null ) {
            parameterDescription = ""; // NOI18N
        }

        return parameterDescription;

    }

    /** Retrieves the preferred effective description of this Parameter or
     * Member in the XTCE data model.
     *
     * This method prefers the shortDescription attribute on the primary
     * element and then the Long Description element of the primary element.
     * In the event at that are not present or empty, it will attempt to get
     * the shortDescription atrribute from the type element and if that is not
     * present or empty, the Long Description element.  Members will also get
     * from the type element.
     *
     * @return String containing a single description text item for this
     * Parameter or Member, generally suitable for display tables.  The String
     * will never be null.
     *
     */

    public final String getDescription() {

        String parameterDescription;

        if ( ( isMember() == true ) && ( getTypeReference() != null ) ) {
            parameterDescription = getTypeReference().getShortDescription();
            if ( parameterDescription == null || parameterDescription.isEmpty() == true ) {
                parameterDescription = getTypeReference().getLongDescription();
            }
        } else {
            parameterDescription = getPrimaryShortDescription( reference_ );
            if ( parameterDescription.isEmpty() == true ) {
                parameterDescription = getPrimaryLongDescription( reference_ );
                if ( parameterDescription.isEmpty() == true ) {
                    if ( getTypeReference() != null ) {
                        parameterDescription = getTypeReference().getShortDescription();
                        if ( parameterDescription == null || parameterDescription.isEmpty() == true ) {
                            parameterDescription = getTypeReference().getLongDescription();
                        }
                    }
                }
            }
        }

        if ( parameterDescription == null ) {
            parameterDescription = ""; // NOI18N
        }
        return parameterDescription;

    }

    /** Accessor to retrieve the dataSource attribute from the
     * ParameterProperties element.
     * 
     * There is a special case in the XTCE schema of this toolkit.  The
     * ParameterProperties element is allowed to also appear on the Member
     * elements, although that is non-standard until perhaps XTCE version 1.2.
     *
     * @return String containing one of the valid values for the dataSource
     * attribute, which can be "telemetered", "local", "derived", and
     * "constant".  This function will never return null.  Although a default
     * is not declared in XTCE, this function forces the default to be
     * "telemetered".
     *
     */

    public final String getDataSource() {

        // we compensate here for the missing default value in the XTCE schema
        // for ParameterProperties/@dataSource

        String dataSource = "telemetered"; // NOI18N

        try {

            if ( isMember() == true ) {
                if ( memberReference_.getParameterProperties().getDataSource() != null ) {
                    dataSource = memberReference_.getParameterProperties().getDataSource();
                }
            } else if ( isParameter() == true ) {
                if ( reference_.getParameterProperties().getDataSource() != null ) {
                    dataSource = reference_.getParameterProperties().getDataSource();
                }
            }

        } catch ( NullPointerException ex ) {
            // this is okay when the Parameter is a Member or the dataSource
            // attribute is not present
        }

        return dataSource;

    }

    /** Accessor to retrieve the content of the SystemName element in the
     * XTCE Parameter element.
     * 
     * There is a special case in the XTCE schema of this toolkit.  The
     * ParameterProperties element is allowed to also appear on the Member
     * elements, although that is non-standard until perhaps XTCE version 1.2.
     * As a result, it is possible for there to be a SystemName element in this
     * toolkit for Member elements.
     *
     * @return String containing the value of the SystemName element, or an
     * empty string if that element is not present.  The value will never be
     * null.
     *
     */

    public final String getSystemName() {

        try {

            if ( isMember() == true ) {
                if ( memberReference_.getParameterProperties().getSystemName() == null ) {
                    return ""; // NOI18N
                }
                return memberReference_.getParameterProperties().getSystemName();
            } else if ( isParameter() == true ) {
                if ( reference_.getParameterProperties().getSystemName() == null ) {
                    return ""; // NOI18N
                }
                return reference_.getParameterProperties().getSystemName();
            }

        } catch ( NullPointerException ex ) {
            // this is okay when the SystemName element is not present
        }

        return ""; // NOI18N

    }

    /** Accessor to retrieve the "initial" or "default" value of the Parameter
     * or Member represented by this object, considering the XTCE data model
     * rules.
     *
     * This method first examines whether there is an initialValue attribute
     * on the Parameter element (cannot appear on the Member element) and if
     * not present, then it looks for this attribute on the Parameter Type
     * element.
     *
     * @return The initial or default value of the Parameter, or an empty
     * string.  This method endeavors to never return a null string.
     *
     */

    @Override
    public String getInitialValue() {

        if ( ( isParameter() == true ) && ( reference_.getInitialValue() != null ) ) {
            return reference_.getInitialValue();
        } else {
            return super.getInitialValue();
        }

    }

    /** Accessor to determine if this Parameter or Member represented by this
     * object is in a "Read Only" state, as defined by the readOnly attribute
     * of the ParameterProperties element.
     *
     * There is a special case in the XTCE schema of this toolkit.  The
     * ParameterProperties element is allowed to also appear on the Member
     * elements, although that is non-standard until perhaps XTCE version 1.2.
     * As a result, it is possible for there to be a ParameterProperties
     * element in this toolkit for Member elements.
     *
     * @return boolean indicating if this Parameter or Member is "read only".
     *
     */

    public final boolean isReadOnly() {

        try {

            if ( isMember() == true ) {
                return memberReference_.getParameterProperties().isReadOnly();
            } else if ( isParameter() == true ) {
                return reference_.getParameterProperties().isReadOnly();
            }

        } catch ( NullPointerException ex ) {
            // this is okay when the Parameter is a Member
        }

        return false;

    }

    /** Accessor to determine if this Parameter or Member represented by this
     * object is in a "settable" state, as defined by the readOnly attribute
     * of the ParameterProperties element.
     *
     * This method is the inverse of the isReadOnly() method.
     *
     * There is a special case in the XTCE schema of this toolkit.  The
     * ParameterProperties element is allowed to also appear on the Member
     * elements, although that is non-standard until perhaps XTCE version 1.2.
     * As a result, it is possible for there to be a ParameterProperties
     * element in this toolkit for Member elements.
     *
     * @return boolean indicating if this Parameter or Member is settable.
     *
     */

    public final boolean isSettable() {
        return ( isReadOnly() == false );
    }

    /** Accessor to determine if this XTCEParameter object represents a
     * Member element in the XTCE data model.
     *
     * @return boolean indicating if this is a Member element object in the
     * XTCE data model.
     *
     */

    public final boolean isMember() {
        return ( memberReference_ != null );
    }

    /** Retrieve the underlying Member element from the XTCE data model.
     *
     * Use the isMember() method to test whether this is a member before using
     * this method.
     *
     * @return Member element or a null in the event that this is not a
     * Member of an aggregate.
     *
     */

    public final Member getMemberReference() {
        return memberReference_;
    }

    /** Accessor to determine if this XTCEParameter object represents a
     * Parameter element in the XTCE data model.
     *
     * @return boolean indicating if this is a Parameter element object in the
     * XTCE data model.
     *
     */

    public final boolean isParameter() {
        return ( reference_ != null );
    }

    /** Retrieve the underlying Parameter element from the XTCE data model.
     *
     * Use the isParameter() method to test whether this is a member before
     * using this method.
     *
     * @return Parameter element or a null in the event that this is not a
     * Parameter.
     *
     */

    public final Parameter getParameterReference() {
        return reference_;
    }

    /** Retrieve an XML string that represents this Parameter or Member
     * element.
     *
     * @return String containing the XML fragment.
     *
     * @throws XTCEDatabaseException in the event that the elements being
     * marshaled from the JAXB internal classes cannot make a valid document.
     * Check the exception message for causality information.
     *
     */

    public String toXml() throws XTCEDatabaseException {

        try {

            if ( isMember() == true ) {

                JAXBElement<Member> xmlElement = new JAXBElement<>
                    ( new QName( Member.class.getSimpleName() ),
                      Member.class,
                      memberReference_ );

                XTCEDocumentMarshaller mmm =
                    new XTCEDocumentMarshaller( Member.class, true );

                return XTCEFunctions.xmlPrettyPrint( mmm.marshalToXml( xmlElement ) );

            } else {

                JAXBElement<Parameter> xmlElement = new JAXBElement<>
                    ( new QName( Parameter.class.getSimpleName() ),
                      Parameter.class,
                      reference_ );

                XTCEDocumentMarshaller mmm =
                    new XTCEDocumentMarshaller( Parameter.class, true );

                return XTCEFunctions.xmlPrettyPrint( mmm.marshalToXml( xmlElement ) );

            }

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException(
                getName() +
                ": " + // NOI18N
                XTCEFunctions.getText( "xml_marshal_error_parameter" ) + // NOI18N
                " '" + // NOI18N
                ex.getCause() +
                "'" ); // NOI18N
        }

    }

    /// Reference to the Parameter object in JAXB

    private final Parameter reference_;

    /// Reference to the Member object in JAXB

    private final Member memberReference_;

}
