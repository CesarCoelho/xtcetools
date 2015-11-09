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

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.omg.space.xtce.database.AggregateDataType.MemberList.Member;
import org.omg.space.xtce.database.MetaCommandType.ArgumentList.Argument;
import org.omg.space.xtce.database.NameDescriptionType;

/** This class serves as a convenient container for representing Telecommand
 * Arguments in the XTCE data model by abstracting the mechanics of assembling
 * Argument attributes that are derived from XTCE related rules of processing,
 * which can include processing of Argument, Member, and Type elements.
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

public class XTCEArgument extends XTCETypedObject {

    /** Constructor
     *
     * This is a constructor for an XTCEArgument object when the argument of
     * interest is a Argument element in the XTCE data model.
     *
     * @param name The name attribute of the Argument element, which is kept
     * internally as a reference to that object.
     *
     * @param path The Space System path that contains the reference to this
     * Argument element.  It does not include the name.
     *
     * @param pRef The object reference to the Argument element, which is kept
     * internally as a reference to that object.
     *
     * @param ptRef The object reference to the Argument Type that is
     * applicable for this parameter in the XTCE data model.
     *
     */

    XTCEArgument( final String              name,
                  final String              path,
                  final Argument            pRef,
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
     * This is a constructor for an XTCEArgument object when the argument of
     * interest is a Member element in the XTCE data model.
     *
     * @param name The name attribute of the Member element, which is kept
     * internally as a reference to that object.
     *
     * @param path The Space System path that contains the reference to this
     * Argument/Member element.  It does not include the name.
     *
     * @param pRef The object reference to the Member element, which is kept
     * internally as a reference to that object.
     *
     * @param ptRef The object reference to the Argument Type that is
     * applicable for this argument in the XTCE data model.
     *
     */

    XTCEArgument( final String              name,
                  final String              path,
                  final Member              pRef,
                  final NameDescriptionType ptRef ) {

        super( name, path, null, null, ptRef );

        reference_       = null;
        memberReference_ = pRef;

    }

    /** Retrieves the fully qualified XTCE path reference to the type
     * that describes this argument.
     *
     * @return String containing the path or an empty string if there is an
     * error case and there is no type reference.
     *
     */

    public String getTypeReferenceFullPath() {

        if ( isValid() == false ) {
            return ""; // NOI18N
        }

        if ( reference_ != null ) {
            return XTCEFunctions.resolvePathReference( getSpaceSystemPath(),
                                                       reference_.getArgumentTypeRef() );
        } else if ( memberReference_ != null ) {
            return XTCEFunctions.resolvePathReference( getSpaceSystemPath(),
                                                       memberReference_.getTypeRef() );
        }

        return ""; // NOI18N

    }

    /** Retrieves the "effective" Short Description of this
     * Argument or Member in the XTCE data model.
     *
     * The "effective" Short Description is defined as the shortDescription
     * attribute that first appears on the Argument element, and if not
     * present, then it can fall back to the shortDescription attribute that is
     * on the Argument Type element, and eventually fall back to returning an
     * empty string if neither of these contains a shortDescription attribute.
     *
     * @return String containing the Short Description of this Argument or
     * Member, or an empty string if there is none.  This function endeavors
     * to never return null.
     *
     */

    public String getShortDescription() {

        String parameterDescription = ""; // NOI18N

        if ( ( isMember() == true ) && ( getTypeReference() != null ) ) {
            parameterDescription = getTypeReference().getShortDescription();
        } else if ( isArgument() == true ) {
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
     * Argument or Member in the XTCE data model.
     *
     * The "effective" Long Description is defined as the LongDescription
     * element that first appears on the Argument element, and if not
     * present, then it can fall back to the LongDescription element that is
     * on the Argument Type element, and eventually fall back to returning an
     * empty string if neither of these contains a LongDescription element.
     *
     * @return String containing the Long Description of this Argument or
     * Member, or an empty string if there is none.  This function endeavors
     * to never return null.
     *
     */

    public String getLongDescription() {

        String parameterDescription = ""; // NOI18N

        if ( ( isMember() == true ) && ( getTypeReference() != null ) ) {
            parameterDescription = getTypeReference().getLongDescription();
        } else if ( isArgument() == true ) {
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

    /** Retrieves the preferred effective description of this Argument or
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
     * Argument or Member, generally suitable for display tables.  The String
     * will never be null.
     *
     */

    public String getDescription() {

        String parameterDescription = ""; // NOI18N

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

    /** Accessor to retrieve the "initial" or "default" value of the Argument
     * or Member represented by this object, considering the XTCE data model
     * rules.
     *
     * This method first examines whether there is an initialValue attribute
     * on the Argument element (cannot appear on the Member element) and if
     * not present, then it looks for this attribute on the Argument Type
     * element.
     *
     * @return The initial or default value of the Argument, or an empty
     * string.  This method endeavors to never return a null string.
     *
     */

    @Override
    public String getInitialValue() {

        if ( ( isArgument() == true ) && ( reference_.getInitialValue() != null ) ) {
            return reference_.getInitialValue();
        } else {
            return super.getInitialValue();
        }

    }

    /** Accessor to determine if this XTCEArgument object represents a
     * Member element in the XTCE data model.
     *
     * @return boolean indicating if this is a Member element object in the
     * XTCE data model.
     *
     */

    public boolean isMember() {
        return ( memberReference_ != null );
    }

    /** Accessor to determine if this XTCEArgument object represents a
     * Argument element in the XTCE data model.
     *
     * @return boolean indicating if this is a Argument element object in the
     * XTCE data model.
     *
     */

    public boolean isArgument() {
        return ( reference_ != null );
    }

    /** Retrieve a string of the XML text for this Argument element.
     *
     * @return String containing the XML as formatted text.
     *
     * @throws XTCEDatabaseException in the event that the elements being
     * marshaled from the JAXB internal classes cannot make a valid document.
     * Check the exception message for causality information.
     *
     */

    public String toXml() throws XTCEDatabaseException {

        try {

            if ( isMember() == true ) {
                JAXBElement xmlElement = new JAXBElement( new QName(Member.class.getSimpleName()),
                                                          Member.class,
                                                          memberReference_ );
                XTCEDocumentMarshaller mmm = new XTCEDocumentMarshaller( Member.class, true );
                return XTCEFunctions.xmlPrettyPrint( mmm.marshalToXml( xmlElement ) );
            } else {
                JAXBElement xmlElement = new JAXBElement( new QName(Argument.class.getSimpleName()),
                                                          Argument.class,
                                                          reference_ );
                XTCEDocumentMarshaller mmm = new XTCEDocumentMarshaller( Argument.class, true );
                return XTCEFunctions.xmlPrettyPrint( mmm.marshalToXml( xmlElement ) );
            }

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException(
                getName() +
                ": " + // NOI18N
                XTCEFunctions.getText( "xml_marshal_error_argument" ) + // NOI18N
                " '" + // NOI18N
                ex.getCause() +
                "'" ); // NOI18N
        }

    }

    // Private Data Members

    private final Argument reference_;
    private final Member   memberReference_;

}
