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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.omg.space.xtce.database.AggregateDataType;
import org.omg.space.xtce.database.AggregateDataType.MemberList.Member;
import org.omg.space.xtce.database.CommandMetaDataType.MetaCommandSet.BlockMetaCommand;
import org.omg.space.xtce.database.HeaderType;
import org.omg.space.xtce.database.MetaCommandType;
import org.omg.space.xtce.database.NameDescriptionType;
import org.omg.space.xtce.database.PCMStreamType;
import org.omg.space.xtce.database.ParameterSetType.Parameter;
import org.omg.space.xtce.database.SequenceContainerType;
import org.omg.space.xtce.database.SpaceSystemType;

/** The XTCESpaceSystem class represents the SpaceSystems elements in the XTCE
 * data model and provides convenient accessors that abstract some of the XTCE
 * processing rules from the application developer.
 *
 * @author David Overeem
 *
 */

public class XTCESpaceSystem extends XTCENamedObject {

    /** Constructor
     *
     * The XTCESpaceSystem constructor is currently private to the toolkit
     * because it is instantiated by the XTCEDatabase object.  To create a new
     * SpaceSystem element, use the addSpaceSystem() method on the XTCEDatabase
     * object.
     *
     * @param path String containing the fully qualified path to the
     * SpaceSystem element within the XTCE document.
     *
     * @param ssRef The JAXB SpaceSystemType element that represents this
     * SpaceSystem element in the XTCE data model.
     *
     * @param dbRef The XTCEDatabase object that represents the XTCE data model
     * for the current XTCE document.
     *
     */

    XTCESpaceSystem( String path, SpaceSystemType ssRef, XTCEDatabase dbRef ) {

        super( ssRef.getName(),
               XTCEFunctions.getPathNameFromReferenceString( path ),
               ssRef.getAliasSet(),
               ssRef.getAncillaryDataSet() );

        reference_         = ssRef;
        databaseReference_ = dbRef;
        warnings_          = new ArrayList<>();

    }

    /** Retrieve a reference to the JAXB object that represents this
     * SpaceSystem element in the XTCE data model.
     *
     * @return SpaceSystemType object for this Space System.
     *
     */

    public SpaceSystemType getReference() {
        return reference_;
    }

    /** Retrieve the warnings created from the last operation invoked.
     *
     * @return List of Strings containing the warnings.
     *
     */

    public List<String> getWarningsFromLastOperation() {
        return warnings_;
    }

    /** Retrieve the short description attribute of this Space System.
     *
     * @return String containing the short description or an empty string.
     * This accessor, unlike the underlying JAXB reference, can never be null.
     *
     */

    public String getShortDescription() {
        return getPrimaryShortDescription( reference_ );
    }

    /** Sets the short description attribute of this Space System.
     *
     * Changing the short description of the Space System should be done using
     * this setter instead of the underlying JAXB object so that the null can
     * be handled automatically and the changed flag can be set for user
     * applications to know whether the underlying model has changed.  This is
     * used for later saving the changes.
     *
     * @param text String containing the short description text, which can be
     * empty or null, in which case it nulls the underlying short description
     * attribute of the JAXB object.
     *
     */

    public void setShortDescription( String text ) {
        setPrimaryShortDescription( reference_, text );
    }

    /** Retrieve the long description element content of this Space System.
     *
     * @return String containing the long description or an empty string.
     * This value, unlike the underlying JAXB reference, can never be null.
     *
     */

    public String getLongDescription() {
        return getPrimaryLongDescription( reference_ );
    }

    /** Sets the long description attribute of this Space System.
     *
     * Changing the long description of the Space System should be done using
     * this setter instead of the underlying JAXB object so that the null can
     * be handled automatically and the changed flag can be set for user
     * applications to know whether the underlying model has changed.  This is
     * used for later saving the changes.
     *
     * @param text String containing the long description text, which can be
     * empty or null, in which case it nulls the underlying long description
     * element of the JAXB object.
     *
     */

    public void setLongDescription( String text ) {
        setPrimaryLongDescription( reference_, text );
    }

    /** Retrieve the operationalStatus attribute from the SpaceSystem element
     * attributes list.
     *
     * @return String containing the attribute text or an empty string.  The
     * value will never be null.
     *
     */

    public String getOperationalStatus() {
        if ( getReference().getOperationalStatus() == null ) {
            return ""; // NOI18N
        }
        return getReference().getOperationalStatus();
    }

    /** Sets the operationalStatus attribute on the SpaceSystem element or
     * removes it if the content is empty.
     *
     * @param text String containing the operational status or empty in which
     * case the attribute will be removed for brevity on the XML document.
     *
     */

    public void setOperationalStatus( String text ) {
        if ( ( text == null ) || ( text.isEmpty() == true ) ) {
            getReference().setOperationalStatus( null );
        } else {
            getReference().setOperationalStatus( text );
        }
    }

    /** Retrieve the metrics for the Space System represented by this object.
     *
     * The metrics returned are shallow to this Space System in the XTCE data
     * model.  Metrics for an entire XTCE document can be obtained using the
     * getMetrics() method on the XTCEDatabase class.
     *
     * @return XTCESpaceSystemMetrics object containing a variety of counts.
     *
     */

    public XTCESpaceSystemMetrics getMetrics() {
        return new XTCESpaceSystemMetrics( this );
    }

    /** Sets the basic Header element attributes for this Space System element.
     *
     * Attributes on the Header element are generally associated with the
     * document itself when it is a Space System root element.  Otherwise they
     * are associated with the child subsystem that the Space System element
     * represents.
     *
     * Arguments passed to this functions may be null or empty strings, in
     * which case the function will remove an existing attribute and if all of
     * them are empty, then it will clear the Header element from the Space
     * System.
     *
     * Attributes which are optional may be passed null or an empty string.
     * When this state exists, this function will remove the attribute from
     * the document instead of leaving an empty value.  This aids in document
     * compression.  If a default value exists, the value will be checked
     * against the passed value and if they match, the attribute will also be
     * removed as the default will be retrieved by the getter in the event that
     * the attribute is not present.
     *
     * @param version String containing the document or Space System element
     * version.  XTCE models this field as a plain string, so no special
     * restrictions exist.
     *
     * @param date String containing the date to associate with the document or
     * Space System element.  XTCE models this field as a plain string, so any
     * date format is accepted.
     *
     * @param classification String containing a classification description.
     * The XTCE data model supplies a default of "NotClassified" here if the
     * user provides no value.
     *
     * @param instructions String containing a description of the
     * classification instructions.
     *
     * @param validation String containing the validation status, which is a
     * mandatory attribute if the Header element does exist.  If not supplied
     * here, the value "Unknown" will be set, but only if the Header element
     * needs to exist.
     *
     */

    public void setHeaderAttributes( String version,
                                     String date,
                                     String classification,
                                     String instructions,
                                     String validation ) {

        // first check for the case where we clear the Header element
        if ( ( ( version        == null ) || ( version.isEmpty()        == true ) ) &&
             ( ( date           == null ) || ( date.isEmpty()           == true ) ) &&
             ( ( classification == null ) || ( classification.isEmpty() == true ) ) &&
             ( ( instructions   == null ) || ( instructions.isEmpty()   == true ) ) &&
             ( ( validation     == null ) || ( validation.isEmpty()     == true ) ) ) {
            getReference().setHeader( null );
            return;
        }

        // we must have something to set, so make sure we have a Header element
        HeaderType header = getReference().getHeader();
        if ( header == null ) {
            header = new HeaderType();
            getReference().setHeader( header );
        }

        if ( version != null && version.isEmpty() == false ) {
            header.setVersion( version );
        } else {
            header.setVersion( null );
        }

        if ( date != null && date.isEmpty() == false ) {
            header.setDate( date );
        } else {
            header.setDate( null );
        }

        if ( classification == null || classification.isEmpty() == true ) {
            header.setClassification( null );
        } else if ( classification.equals( "NotClassified") == true ) { // NOI18N
            header.setClassification( null );
        } else {
            header.setClassification( classification );
        }

        if ( instructions != null && instructions.isEmpty() == false ) {
            header.setClassificationInstructions( instructions );
        } else {
            header.setClassificationInstructions( null );
        }

        if ( validation != null && validation.isEmpty() == false ) {
            header.setValidationStatus( validation );
        } else {
            header.setValidationStatus( "Unknown" ); // NOI18N
        }

    }

    /** Check whether or not a named TM Parameter exists in this Space System.
     *
     * This method first checks a hash table of parameter names in the Space
     * System for high speed access.  This hash table is "lazy loaded", so the
     * very first request to any of the parameter checks/gets on this Space
     * System will be slower than subsequent calls to the same functions.
     *
     * @see #getTelemetryParameter
     * @see #getParameters()
     *
     * @param name String containing the Parameter element name attribute.
     *
     * @return boolean indicating if a Parameter by the requested name is
     * contained in this Space System.
     *
     */

    public boolean isTelemetryParameter( String name ) {

        if ( tmParameterHashTable_ != null ) {
            XTCEParameter parameter = tmParameterHashTable_.get( name );
            return ( parameter != null );
        }

        try {
            List<Object> xmlParameters = getReference().
                                         getTelemetryMetaData().
                                         getParameterSet().
                                         getParameterOrParameterRef();
            //ArrayList<XTCEParameter> parameters =
            //    addParameters( xmlParameters );
            tmParameterHashTable_ =
                ensureHashTable( addParameters( xmlParameters ) );
            return isTelemetryParameter( name );
            //for ( XTCEParameter parameter : parameters ) {
            //    if ( parameter.getName().equals( name ) == true ) {
            //        return true;
            //    }
            //}
        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any TM parameters
        }

        return false;

    }

    /** Check whether or not a named TC Parameter exists in this Space System.
     *
     * This method first checks a hash table of parameter names in the Space
     * System for high speed access.  This hash table is "lazy loaded", so the
     * very first request to any of the parameter checks/gets on this Space
     * System will be slower than subsequent calls to the same functions.
     *
     * @see #getTelecommandParameter
     * @see #getParameters()
     *
     * @param name String containing the Parameter element name attribute.
     *
     * @return boolean indicating if a Parameter by the requested name is
     * contained in this Space System.
     *
     */

    public boolean isTelecommandParameter( String name ) {

        if ( tcParameterHashTable_ != null ) {
            XTCEParameter parameter = tcParameterHashTable_.get( name );
            return ( parameter != null );
        }

        try {
            List<Object> xmlParameters = getReference().
                                         getCommandMetaData().
                                         getParameterSet().
                                         getParameterOrParameterRef();
            //ArrayList<XTCEParameter> parameters =
            //    addParameters( xmlParameters );
            tcParameterHashTable_ =
                ensureHashTable( addParameters( xmlParameters ) );
            return isTelecommandParameter( name );
            //for ( XTCEParameter parameter : parameters ) {
            //    if ( parameter.getName().equals( name ) == true ) {
            //        return true;
            //    }
            //}
        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any TM parameters
        }

        return false;

    }

    /** Retrieves a named TM Parameter from this Space System.
     *
     * This method first checks a hash table of parameter names in the Space
     * System for high speed access.  This hash table is "lazy loaded", so the
     * very first request to any of the parameter checks/gets on this Space
     * System will be slower than subsequent calls to the same functions.
     *
     * @see #isTelemetryParameter
     * @see #getParameters()
     *
     * @param name String containing the Parameter element name attribute.
     *
     * @return XTCEParameter object that contains the toolkit model for the TM
     * Parameter and related elements in the XTCE data model.
     *
     * @throws XTCEDatabaseException when the TM Parameter does not exist in
     * this Space System.  The exception can be avoided by using the method
     * isTelemetryParameter() when the presence is in question.
     *
     */

    public XTCEParameter getTelemetryParameter( String name ) throws XTCEDatabaseException {

        if ( tmParameterHashTable_ != null ) {
            XTCEParameter parameter = tmParameterHashTable_.get( name );
            if ( parameter != null ) {
                return parameter;
            } else {
                throw new XTCEDatabaseException( XTCEFunctions.getText( "error_tmp" ) + // NOI18N
                                                 " '" + // NOI18N
                                                 name +
                                                 "' " + // NOI18N
                                                 XTCEFunctions.getText( "error_not_in_ss" ) + // NOI18N
                                                 " '" + // NOI18N
                                                 getFullPath() +
                                                 "'" ); // NOI18N
            }
        }

        try {
            List<Object> xmlParameters = getReference().
                                         getTelemetryMetaData().
                                         getParameterSet().
                                         getParameterOrParameterRef();
            //ArrayList<XTCEParameter> parameters =
            //    addParameters( xmlParameters );
            tmParameterHashTable_ =
                ensureHashTable( addParameters( xmlParameters ) );
            return getTelemetryParameter( name );
            //for ( XTCEParameter parameter : parameters ) {
            //    if ( parameter.getName().equals( name ) == true ) {
            //        return parameter;
            //    }
            //}
        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any TM parameters
        }

        throw new XTCEDatabaseException( XTCEFunctions.getText( "error_tmp" ) + // NOI18N
                                         " '" + // NOI18N
                                         name +
                                         "' " + // NOI18N
                                         XTCEFunctions.getText( "error_not_in_ss" ) + // NOI18N
                                         " '" + // NOI18N
                                         getFullPath() +
                                         "'" ); // NOI18N

    }

    /** Retrieves a named TC Parameter from this Space System.
     *
     * This method first checks a hash table of parameter names in the Space
     * System for high speed access.  This hash table is "lazy loaded", so the
     * very first request to any of the parameter checks/gets on this Space
     * System will be slower than subsequent calls to the same functions.
     *
     * @see #isTelecommandParameter
     * @see #getParameters()
     *
     * @param name String containing the Parameter element name attribute.
     *
     * @return XTCEParameter object that contains the toolkit model for the TC
     * Parameter and related elements in the XTCE data model.
     *
     * @throws XTCEDatabaseException when the TC Parameter does not exist in
     * this Space System.  The exception can be avoided by using the method
     * isTelecommandParameter() when the presence is in question.
     *
     */

    public XTCEParameter getTelecommandParameter( String name ) throws XTCEDatabaseException {


        if ( tcParameterHashTable_ != null ) {
            XTCEParameter parameter = tcParameterHashTable_.get( name );
            if ( parameter != null ) {
                return parameter;
            } else {
                throw new XTCEDatabaseException( XTCEFunctions.getText( "error_tcp" ) + // NOI18N
                                                 " '" + // NOI18N
                                                 name +
                                                 "' " + // NOI18N
                                                 XTCEFunctions.getText( "error_not_in_ss" ) + // NOI18N
                                                 " '" + // NOI18N
                                                 getFullPath() +
                                                 "'" ); // NOI18N
            }
        }

        try {
            List<Object> xmlParameters = getReference().
                                         getCommandMetaData().
                                         getParameterSet().
                                         getParameterOrParameterRef();
            //ArrayList<XTCEParameter> parameters =
            //    addParameters( xmlParameters );
            tcParameterHashTable_ =
                ensureHashTable( addParameters( xmlParameters ) );
            return getTelecommandParameter( name );
            //for ( XTCEParameter parameter : parameters ) {
            //    if ( parameter.getName().equals( name ) == true ) {
            //        return parameter;
            //    }
            //}
        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any TM parameters
        }

        throw new XTCEDatabaseException( XTCEFunctions.getText( "error_tcp" ) + // NOI18N
                                         " '" + // NOI18N
                                         name +
                                         "' " + // NOI18N
                                         XTCEFunctions.getText( "error_not_in_ss" ) + // NOI18N
                                         " '" + // NOI18N
                                         getFullPath() +
                                         "'" ); // NOI18N

    }

    /** Retrieves a List of all TM Parameters modeled by this Space
     * System object.
     *
     * This method does not use the hash lookup because it is a linear grab of
     * all the Parameters in order.  Aggregate Parameters have Members modeled
     * using C style notation, so XTCEParameter objects will be returned for
     * Aggregate Members where their name is in the form A.B, where A is the
     * Parameter Aggregate and B is the name attribute of the Aggregate Member
     * element.  This makes it very easy for the caller to iterate through the
     * resulting List without needing to resolve the internal
     * relationships of the XTCE data model.
     *
     * @return List of XTCEParameter objects or an empty list if there are
     * no Parameters modeled within this particular Space System.
     *
     */

    public List<XTCEParameter> getTelemetryParameters() {

        try {
           List<Object> parameters = getReference().
                                     getTelemetryMetaData().
                                     getParameterSet().
                                     getParameterOrParameterRef();
           return addParameters( parameters );
        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any TM parameters
        }

        return new ArrayList<>();

    }

    /** Retrieves a List of all TC Parameters modeled by this Space
     * System object.
     *
     * This method does not use the hash lookup because it is a linear grab of
     * all the Parameters in order.  Aggregate Parameters have Members modeled
     * using C style notation, so XTCEParameter objects will be returned for
     * Aggregate Members where their name is in the form A.B, where A is the
     * Parameter Aggregate and B is the name attribute of the Aggregate Member
     * element.  This makes it very easy for the caller to iterate through the
     * resulting List without needing to resolve the internal
     * relationships of the XTCE data model.
     *
     * @return List of XTCEParameter objects or an empty list if there are
     * no Parameters modeled within this particular Space System.
     *
     */

    public List<XTCEParameter> getTelecommandParameters() {

        try {
           List<Object> parameters = getReference().
                                     getCommandMetaData().
                                     getParameterSet().
                                     getParameterOrParameterRef();
           return addParameters( parameters );
        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any TC parameters
        }

        return new ArrayList<>();

    }

    /** Retrieves a List of all Parameters modeled by this Space
     * System object.
     *
     * This method does not use the hash lookup because it is a linear grab of
     * all the Parameters in order.  Aggregate Parameters have Members modeled
     * using C style notation, so XTCEParameter objects will be returned for
     * Aggregate Members where their name is in the form A.B, where A is the
     * Parameter Aggregate and B is the name attribute of the Aggregate Member
     * element.  This makes it very easy for the caller to iterate through the
     * resulting List without needing to resolve the internal
     * relationships of the XTCE data model.
     *
     * @return List of XTCEParameter objects or an empty list if there are
     * no Parameters modeled within this particular Space System.
     *
     */

    public List<XTCEParameter> getParameters() {

        ArrayList<XTCEParameter> list = new ArrayList<>();
        list.addAll( getTelemetryParameters() );
        list.addAll( getTelecommandParameters() );
        return list;

    }

    /** Retrieve a named SequenceContainer as a toolkit modeled XTCETMContainer
     * object from this Space System object.
     *
     * @param nameOrPath String containing the local name or a path to the
     * name.  The path will be ignored as this method is intended to only find
     * in the local Space System element.
     *
     * @return XTCETMContainer object representing the named SequenceContainer
     * element.
     *
     * @throws XTCEDatabaseException in the event that the named
     * SequenceContainer does not exist in this Space System element.
     *
     */

    public XTCETMContainer getContainer( String nameOrPath ) throws XTCEDatabaseException {

        try {

            String name =
                XTCEFunctions.getNameFromPathReferenceString( nameOrPath );

            List<SequenceContainerType> containers = getReference().
                                                     getTelemetryMetaData().
                                                     getContainerSet().
                                                     getSequenceContainer();

            for ( SequenceContainerType container : containers ) {
                if ( container.getName().equals( name ) == true ) {
                    return new XTCETMContainer( getFullPath(),
                                                makeContainerInheritanceString( container ),
                                                container );
                }
            }

        } catch ( NullPointerException ex ) {
            // do nothing as we throw next
        }

        throw new XTCEDatabaseException( XTCEFunctions.getText( "error_tmc" ) + // NOI18N
                                         " '" + // NOI18N
                                         nameOrPath +
                                         "' " + // NOI18N
                                         XTCEFunctions.getText( "error_not_in_ss" ) + // NOI18N
                                         " '" + // NOI18N
                                         getFullPath() +
                                         "'" ); // NOI18N

    }

    /** Retrieve a List of SequenceContainers that match a user provided string
     * glob, modeled as XTCETMContainer objects.
     *
     * @param nameGlob String containing a glob style matching pattern to match
     * against the container names.
     *
     * @return List of XTCETMContainer objects representing the containers
     * that match the provided glob or an empty list if there are no matches.
     *
     */

    public List<XTCETMContainer> getContainers( String nameGlob ) {

        warnings_ = new ArrayList<>();

        ArrayList<XTCETMContainer> list = new ArrayList<>();

        try {

            List<SequenceContainerType> containers = getReference().
                                                     getTelemetryMetaData().
                                                     getContainerSet().
                                                     getSequenceContainer();

            for ( SequenceContainerType container : containers ) {

                if ( XTCEFunctions.matchesUsingGlob( container.getName(), nameGlob ) == true ) {

                    try {
                        list.add( new XTCETMContainer( getFullPath(),
                                                       makeContainerInheritanceString( container ),
                                                       container ) );
                    } catch ( XTCEDatabaseException ex ) {
                        warnings_.add( ex.getLocalizedMessage() );
                    }

                }

            }

        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any TM containers
        }

        return list;

    }

    /** Retrieve all containers that inherit from the container inheritance
     * path provided.
     *
     * Note to the implementer to be sure that it is understood that a
     * container inheritance path is not the same as the Space System path
     * used by XTCE documents.  This is a path that is defined by the base
     * containers in a container inheritance structure.
     *
     * @param parentPath String containing a base inheritance to search for
     * children of.
     *
     * @return List of XTCETMContainer objects in this SpaceSystem that inherit
     * from the parent path provided.
     *
     */

    public List<XTCETMContainer> getInheritingContainers( String parentPath ) {

        warnings_ = new ArrayList<>();

        ArrayList<XTCETMContainer> list = new ArrayList<>();

        // this algorithm is a little more complicated than is necessary
        // because it attempts to avoid constructing every XTCETMContainer
        // object by looking at the inheritance path first.

        try {

            List<SequenceContainerType> containers = getReference().
                                                     getTelemetryMetaData().
                                                     getContainerSet().
                                                     getSequenceContainer();

            for ( SequenceContainerType container : containers ) {

                try {

                    String childPath =
                        makeContainerInheritanceString( container );

                    if ( childPath.startsWith( parentPath ) == true ) {
                        list.add( new XTCETMContainer( getFullPath(),
                                                       childPath,
                                                       container ) );
                    }

                } catch ( XTCEDatabaseException ex ) {
                    warnings_.add( ex.getLocalizedMessage() );
                }

            }

        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any TM containers
        }

        return list;

    }

    /** Retrieve a List of SequenceContainers that are locally defined
     * in this Space System, modeled as XTCETMContainer objects.
     *
     * @return List of XTCETMContainer objects representing the containers
     * that are modeled by this Space System or an empty list if the Space
     * System does not locally define any SequenceContainer elements.
     *
     */

    public List<XTCETMContainer> getContainers() {

        warnings_ = new ArrayList<>();

        ArrayList<XTCETMContainer> list = new ArrayList<>();

        try {

            List<SequenceContainerType> containers = getReference().
                                                     getTelemetryMetaData().
                                                     getContainerSet().
                                                     getSequenceContainer();

            for ( SequenceContainerType container : containers ) {
                try {
                    list.add( new XTCETMContainer( getFullPath(),
                                                   makeContainerInheritanceString( container ),
                                                   container ) );
                } catch ( XTCEDatabaseException ex ) {
                    warnings_.add( ex.getLocalizedMessage() );
                }
            }

        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any TM containers
        }

        return list;

    }

    /** Retrieve a List of Streams that are locally defined in this
     * Space System, modeled as XTCETMStream objects.
     *
     * Note that Custom Stream elements are not yet supported and will not be
     * included in the final list.
     *
     * @return List of XTCETMStream objects representing the streams
     * that are modeled by this Space System or an empty list if the Space
     * System does not locally define any elements in a StreamSet.
     *
     */

    public List<XTCETMStream> getStreams() {

        warnings_ = new ArrayList<>();

        ArrayList<XTCETMStream> list = new ArrayList<>();

        try {

            List<PCMStreamType> streams =
                getReference().
                getTelemetryMetaData().
                getStreamSet().
                getFixedFrameStreamOrVariableFrameStreamOrCustomStream();

            for ( PCMStreamType stream : streams ) {
                try {
                    list.add( new XTCETMStream( stream,
                                                this,
                                                databaseReference_,
                                                stream.getAliasSet(),
                                                stream.getAncillaryDataSet() ) );
                } catch ( XTCEDatabaseException ex ) {
                    warnings_.add( ex.getLocalizedMessage() );
                }
            }

        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any TM streams
        }

        return list;

    }

    /** Retrieves a named Telecommand from this Space System as an
     * XTCETelecommand object.
     *
     * At present, the element must be a MetaCommand or a BlockMetaCommand.
     * The MetaCommandRef element is not supported in the MetaCommandSet
     * element and this class behaves as if it does not exist, so requests for
     * the name will result in the name being reported as not being found in
     * the subsequent exception described for this method.
     *
     * @see #getTelecommands()
     *
     * @param nameOrPath String containing the MetaCommand or BlockMetaCommand
     * name attribute from the XTCE data model.  Similar to the getContainer()
     * method, this accepts a sloppy name or path.  It ignores the path and
     * extracts only the name for searching in the local Space System.
     *
     * @return XTCETelecommand object that contains the toolkit model for the
     * MetaCommand or BlockMetaCommand and related elements in the XTCE data
     * model.
     *
     * @throws XTCEDatabaseException when the Telecommand does not exist in
     * this Space System.
     *
     */

    public XTCETelecommand getTelecommand( String nameOrPath ) throws XTCEDatabaseException {

        String name = XTCEFunctions.getNameFromPathReferenceString( nameOrPath );

        List<Object> metacommands =
            getReference().
            getCommandMetaData().
            getMetaCommandSet().
            getMetaCommandOrMetaCommandRefOrBlockMetaCommand();

        for ( Object metacommand : metacommands ) {
            String foundName = null;
            if ( metacommand.getClass() == MetaCommandType.class ) {
                foundName = ((NameDescriptionType)metacommand).getName();
            } else if ( metacommand.getClass() == BlockMetaCommand.class ) {
                foundName = ((NameDescriptionType)metacommand).getName();
            } else {
                continue;
            }
            //System.out.println( "Search Space System " +
            //                    getFullPath() +
            //                    " for " +
            //                    nameOrPath +
            //                    " with " +
            //                    name );
            if ( foundName.equals( name ) == true ) {
                return new XTCETelecommand( getFullPath(),
                                            makeTelecommandInheritanceString( metacommand ),
                                            metacommand,
                                            databaseReference_ );
            }
        }

        throw new XTCEDatabaseException( XTCEFunctions.getText( "general_telecommand" ) + // NOI18N
                                         " '" + // NOI18N
                                         nameOrPath +
                                         "' " + // NOI18N
                                         XTCEFunctions.getText( "error_not_in_ss" ) + // NOI18N
                                         " '" + // NOI18N
                                         getFullPath() +
                                         "'" ); // NOI18N

    }

    /** Retrieve a List of MetaCommand or BlockMetaCommand elements that
     * are locally defined in this Space System, modeled as XTCETelecommand
     * objects.
     *
     * TODO: Figure out the rest of the error handling model for this.
     *
     * @return List of XTCETelecommand objects representing the commands
     * that are modeled by this Space System or an empty list if the Space
     * System does not locally define any MetaCommand or BlockMetaCommand
     * elements.
     *
     */

    public List<XTCETelecommand> getTelecommands() {

        ArrayList<XTCETelecommand> list = new ArrayList<>();

        try {

            List<Object> metacommands =
                getReference().
                getCommandMetaData().
                getMetaCommandSet().
                getMetaCommandOrMetaCommandRefOrBlockMetaCommand();

            for ( Object metacommand : metacommands ) {
                try {
                    if ( ( metacommand.getClass() == MetaCommandType.class  ) ||
                         ( metacommand.getClass() == BlockMetaCommand.class ) ) {
                        //System.out.println( "Loading " + ((NameDescriptionType)metacommand).getName() );
                        list.add( new XTCETelecommand( getFullPath(),
                                                       makeTelecommandInheritanceString( metacommand ),
                                                       metacommand,
                                                       databaseReference_ ) );
                    }
                } catch ( XTCEDatabaseException ex ) {
                    System.out.println( ex.getLocalizedMessage() );
                    // need to make an error message
                }
            }

        } catch ( NullPointerException ex ) {
            // this is okay, the SpaceSystem may not have any Telecommands
        }

        return list;

    }

    /** Private method to initialize the hash table of TM or TM parameters.
     * 
     * @param list List of XTCEParameters that have been located/modeled.
     *
     * @return HashMap of String names to XTCEParameter objects for setting the
     * private data member of this class.
     *
     */

    private HashMap<String, XTCEParameter> ensureHashTable( List<XTCEParameter> list ) {

        //System.out.println( "Creating Hash Table in " + getFullPath() );
        HashMap<String, XTCEParameter> table = new HashMap<>( list.size() );
        for ( XTCEParameter parameter : list ) {
            table.put( parameter.getName(), parameter );
        }
        return table;

    }

    private String makeContainerInheritanceString( SequenceContainerType container ) throws XTCEDatabaseException {

        // optimization would be good to have here.

        LinkedList<String> cpathList = new LinkedList<>();
        SequenceContainerType currentContainer = container;
        cpathList.addFirst( container.getName() );
        String currentSpaceSystemPath = getFullPath();

        while ( currentContainer.getBaseContainer() != null ) {
            String path = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                              currentContainer.getBaseContainer().getContainerRef() );
            currentContainer = getContainerElement( path );
            currentSpaceSystemPath = path.substring( 0, path.lastIndexOf( '/' ) );
            cpathList.addFirst( currentContainer.getName() );
        }

        StringBuilder inheritancePathBuilder = new StringBuilder();
        for ( String containerName : cpathList ) {
            inheritancePathBuilder.append( "/" ); // NOI18N
            inheritancePathBuilder.append( containerName );
        }

        return inheritancePathBuilder.toString();

    }

    private String makeTelecommandInheritanceString( Object metacommand ) throws XTCEDatabaseException {

        if ( metacommand.getClass() != MetaCommandType.class ) {
            /// @todo BlockMetaCommand
            return ""; // NOI18N
        }

        LinkedList<String> cpathList = new LinkedList<>();
        MetaCommandType currentMetaCommand = (MetaCommandType)metacommand;
        cpathList.addFirst( currentMetaCommand.getName() );
        String currentSpaceSystemPath = getFullPath();

        while ( currentMetaCommand.getBaseMetaCommand() != null ) {
            String path = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                              currentMetaCommand.getBaseMetaCommand().getMetaCommandRef() );
            currentMetaCommand = getMetaCommandElement( path );
            currentSpaceSystemPath = XTCEFunctions.getPathNameFromReferenceString( path );
            cpathList.addFirst( currentMetaCommand.getName() );
        }

        StringBuilder inheritancePathBuilder = new StringBuilder();
        for ( String metaCommandName : cpathList ) {
            inheritancePathBuilder.append( "/" ); // NOI18N
            inheritancePathBuilder.append( metaCommandName );
        }

        //System.out.println( "Set Inheritance Path For TC: " + inheritancePathBuilder.toString() );
        return inheritancePathBuilder.toString();

    }

    private SequenceContainerType getContainerElement( String ssPath ) throws XTCEDatabaseException {

        String name = XTCEFunctions.getNameFromPathReferenceString( ssPath );
        String path = XTCEFunctions.getPathNameFromReferenceString( ssPath );

        List<XTCESpaceSystem> spaceSystems =
            databaseReference_.getSpaceSystemTree();
        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            if ( spaceSystem.getFullPath().equals( path ) == true ) {
                List<SequenceContainerType> containers =
                    spaceSystem.getReference()
                               .getTelemetryMetaData()
                               .getContainerSet()
                               .getSequenceContainer();
                for ( SequenceContainerType container : containers ) {
                    if ( container.getName().equals( name ) == true ) {
                        return container;
                    }
                }
            }
        }

        throw new XTCEDatabaseException( XTCEFunctions.getText( "error_tmc" ) + // NOI18N
                                         " '" + // NOI18N
                                         name +
                                         "' " + // NOI18N
                                         XTCEFunctions.getText( "error_not_in_ss" ) + // NOI18N
                                         " '" + // NOI18N
                                         path +
                                         "'" ); // NOI18N

    }

    private MetaCommandType getMetaCommandElement( String ssPath ) throws XTCEDatabaseException {

        String name = XTCEFunctions.getNameFromPathReferenceString( ssPath );
        String path = XTCEFunctions.getPathNameFromReferenceString( ssPath );
        List<XTCESpaceSystem> spaceSystems =
            databaseReference_.getSpaceSystemTree();
        //System.out.println( "Searching for MetaCommand " + name + " at " + path );
        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            //System.out.println( "Checking in Space System " + spaceSystem.getFullPath() );
            if ( spaceSystem.getFullPath().equals( path ) == true ) {
                List<Object> metacommands =
                    spaceSystem.getReference()
                               .getCommandMetaData()
                               .getMetaCommandSet()
                               .getMetaCommandOrMetaCommandRefOrBlockMetaCommand();
                //System.out.println( "Found " + Integer.toString( metacommands.size() ) + " MetaCommands" );
                for ( Object metacommand : metacommands ) {
                    if ( metacommand.getClass() == MetaCommandType.class ) {
                        MetaCommandType found = (MetaCommandType)metacommand;
                        //System.out.println( "Found MetaCommand " + found.getName() );
                        if ( found.getName().equals( name ) == true ) {
                            return found;
                        }
                    }
                }
            }
        }

        throw new XTCEDatabaseException( XTCEFunctions.getText( "general_telecommand" ) + // NOI18N
                                         " '" + // NOI18N
                                         name +
                                         "' " + // NOI18N
                                         XTCEFunctions.getText( "error_not_in_ss" ) + // NOI18N
                                         " '" + // NOI18N
                                         path +
                                         "'" ); // NOI18N

    }

    private List<XTCEParameter> addParameters( List<Object> parameters ) {

        warnings_ = new ArrayList<>();

        ArrayList<XTCEParameter> list = new ArrayList<>();

        for ( int iii = 0; iii < parameters.size(); ++iii ) {

            if ( parameters.get( iii ).getClass() == Parameter.class ) {

                Parameter parameter = (Parameter)parameters.get( iii );

                try {

                    String path =
                        XTCEFunctions.resolvePathReference( getFullPath(),
                                                            parameter.getParameterTypeRef() );

                    NameDescriptionType type =
                        databaseReference_.getParameterTypeReference( path );

                    list.add( new XTCEParameter( parameter.getName(),
                                                 getFullPath(),
                                                 parameter,
                                                 type ) );

                    if ( ( type            != null                    ) &&
                         ( type.getClass() == AggregateDataType.class ) ) {

                        addMembers( parameter.getName(),
                                    (AggregateDataType)type,
                                    list );

                    }


                } catch ( NullPointerException ex ) {
                    warnings_.add( parameter.getName() + " " +  // NOI18N
                        XTCEFunctions.getText( "error_param_invalid_type" ) ); // NOI18N
                }

            }

        }

        return list;

    }

    private void addMembers( String              basename,
                             AggregateDataType   type,
                             List<XTCEParameter> list ) {

        // this function need not clear the warnings because it is only called
        // from the addParameters method.

        List<Member> members = type.getMemberList().getMember();

        for ( Member member : members ) {

            try {

                String mpath =
                    XTCEFunctions.resolvePathReference( getFullPath(),
                                                        member.getTypeRef() );

                String newbasename = basename + "." + member.getName(); // NOI18N
                NameDescriptionType mtype =
                    databaseReference_.getParameterTypeReference( mpath );
            
                list.add( new XTCEParameter( newbasename,
                                             getFullPath(),
                                             member,
                                             mtype ) );

                if ( ( mtype            != null                    ) &&
                     ( mtype.getClass() == AggregateDataType.class ) ) {

                    addMembers( newbasename, (AggregateDataType)mtype, list );

                }

            } catch ( NullPointerException ex ) {
                warnings_.add( member.getName() + " " +  // NOI18N
                    XTCEFunctions.getText( "error_parammember_invalid_type" ) ); // NOI18N
            }

        }

    }

    // Private Data Members

    private SpaceSystemType                reference_            = null;
    private XTCEDatabase                   databaseReference_    = null;
    private HashMap<String, XTCEParameter> tmParameterHashTable_ = null;
    private HashMap<String, XTCEParameter> tcParameterHashTable_ = null;
    private List<String>                   warnings_             = null;

}
