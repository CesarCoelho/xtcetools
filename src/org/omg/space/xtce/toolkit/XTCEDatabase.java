/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.omg.space.xtce.database.NameDescriptionType;
import org.omg.space.xtce.database.SpaceSystemType;
import org.w3c.dom.NodeList;

/** The XTCEDatabase class is the first core object to be used by a client
 * that is working with an XTCE Database File.
 *
 *
 * @author David Overeem
 *
 */

public final class XTCEDatabase extends XTCEDatabaseParser {

    /** Constructor for use with an XTCE database file on the filesystem.
     *
     * Successfully constructing this object means that the XTCE database file
     * was successfully loaded and methods can be called on the contents.
     *
     * @param dbFile File object containing the name and path to the XTCE
     * database file to load and optionally validate.
     *
     * @param validateOnLoad boolean indicating if the XSD validation should be
     * performed during the loading.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     * @param listener Hold on to this.
     *
     * @throws XTCEDatabaseException in the event that the file could not be
     * successfully loaded in a valid state.  This can be partly bypassed by
     * not enabling the XSD validation, which is not recommended because it
     * may de-stabilize the application using this data file.
     *
     */

    public XTCEDatabase( File                 dbFile,
                         boolean              validateOnLoad,
                         boolean              applyXIncludes,
                         XTCEProgressListener listener ) throws XTCEDatabaseException {

        topLevelSpaceSystem = loadDatabase( dbFile,
                                            validateOnLoad,
                                            applyXIncludes,
                                            false );

        setFilename( dbFile );
        cacheParameterTypes();
        cacheArgumentTypes();

    }

    /** Constructor for use with an XTCE database file on the filesystem.
     *
     * Successfully constructing this object means that the XTCE database file
     * was successfully loaded and methods can be called on the contents.
     *
     * @param dbLocation URL object containing the location of the XTCE
     * document to load.
     *
     * @param validateOnLoad boolean indicating if the XSD validation should be
     * performed during the loading.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     * @param readOnly boolean indicating if the document should be opened in
     * a read-only context, which is faster because only the JAXB structure is
     * created, avoiding the need to build the Document Object Model that is
     * needed for round trip processing.
     *
     * @throws XTCEDatabaseException in the event that the file could not be
     * successfully loaded in a valid state.  This can be partly bypassed by
     * not enabling the XSD validation, which is not recommended because it
     * may de-stabilize the application using this data file.
     *
     */

    public XTCEDatabase( URL     dbLocation,
                         boolean validateOnLoad,
                         boolean applyXIncludes,
                         boolean readOnly ) throws XTCEDatabaseException {

        topLevelSpaceSystem = loadDatabase( dbLocation,
                                            validateOnLoad,
                                            applyXIncludes,
                                            readOnly );

        setFilename( new File( dbLocation.getPath() ) );
        cacheParameterTypes();
        cacheArgumentTypes();

    }

    /** Constructor for use with an XTCE database file on the filesystem.
     *
     * Successfully constructing this object means that the XTCE database file
     * was successfully loaded and methods can be called on the contents.
     *
     * @param dbFile File object containing the name and path to the XTCE
     * database file to load and optionally validate.
     *
     * @param validateOnLoad boolean indicating if the XSD validation should be
     * performed during the loading.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     * @param readOnly boolean indicating if the document should be opened in
     * a read-only context, which is faster because only the JAXB structure is
     * created, avoiding the need to build the Document Object Model that is
     * needed for round trip processing.
     *
     * @throws XTCEDatabaseException in the event that the file could not be
     * successfully loaded in a valid state.  This can be partly bypassed by
     * not enabling the XSD validation, which is not recommended because it
     * may de-stabilize the application using this data file.
     *
     */

    public XTCEDatabase( File    dbFile,
                         boolean validateOnLoad,
                         boolean applyXIncludes,
                         boolean readOnly ) throws XTCEDatabaseException {

        topLevelSpaceSystem = loadDatabase( dbFile,
                                            validateOnLoad,
                                            applyXIncludes,
                                            readOnly );

        setFilename( dbFile );
        cacheParameterTypes();
        cacheArgumentTypes();

    }

    /** Constructor for creating a new XTCE database object based on a top
     * level SpaceSystem element name.
     *
     * @param topLevelSpaceSystemName String containing the name of the top
     * level SpaceSystem element to create a new and empty XTCE database.
     *
     * @throws XTCEDatabaseException in the event that the name being used
     * cannot be a name for the top level SpaceSystem element.  Examine the
     * exception message for more details on the cause.
     *
     */

    public XTCEDatabase ( String topLevelSpaceSystemName ) throws XTCEDatabaseException {

        topLevelSpaceSystem = newDatabase( topLevelSpaceSystemName );

        setFilename( new File( "" ) );
        setChanged( true );

    }

    /** Retrieve the changed flag indicating if some part of the XTCE document
     * has changed since it has last been saved.
     *
     * @return boolean indicating if a change has been made that has not been
     * yet saved.
     *
     */

    public boolean getChanged( ) {
        return databaseChanged;
    }

    /** Set the change flag to indicate whether or not this XTCE document has
     * been changed since the last time it was saved.
     *
     * @param changedFlag boolean to use to set the changed flag.
     *
     */

    public void setChanged( boolean changedFlag ) {
        databaseChanged = changedFlag;
    }

    /** Retrieve the metrics for the XTCE document represented by this object.
     *
     * The metrics returned are inclusive and recursive to the top level Space
     * System in the XTCE data model.  Metrics for a singular Space System
     * element can be obtained using the getMetrics() method on the
     * XTCESpaceSystem class.
     *
     * @return XTCESpaceSystemMetrics object containing a variety of counts.
     *
     */

    public XTCESpaceSystemMetrics getMetrics( ) {
        return new XTCESpaceSystemMetrics( this );
    }

    /** Evaluate an arbitrary XPath Query Expression and return a generic
     * NodeList object back or an exception with an error message.
     *
     * @param query String containing the XPath expression.
     *
     * @return NodeList containing 0 or more nodes that were located by the
     * query.
     *
     * @throws XTCEDatabaseException in the event that the query contains an
     * error or cannot otherwise be evaluated against the DOM tree.
     *
     */

    public NodeList evaluateXPathQuery( String query ) throws XTCEDatabaseException {

        if ( isReadOnly() == true ) {
            String message = "XPath cannot be used when loading Read-Only";
            throw new XTCEDatabaseException( message );
        }

        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( new XTCENamespaceContext() );

        try {

            XPathExpression expr = xpath.compile( query );
            NodeList nnn = (NodeList)expr.evaluate( getDocumentElement(),
                                                    XPathConstants.NODESET );
            return nnn;

        } catch ( XPathException ex ) {
            throw new XTCEDatabaseException( ex );
        }

    }

    /** Function to save the currently loaded database file.
     *
     * @param dbFile File object containing the file and path for which to save
     * the file containing the XTCE document.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be saved.  The caller should inspect the message inside the exception
     * for more specific details on the cause of this error.
     *
     */

    public void save( File dbFile ) throws XTCEDatabaseException {

        if ( getChanged() == false ) {
            throw new XTCEDatabaseException( "No changes to save" );
        }

        saveDatabase( dbFile );
        setFilename( dbFile );
        setChanged( false );

    }

    /** Retrieve an arbitrary SpaceSystem wrapped element from the document.
     *
     * @param fullPath String containing the full path to the XTCE SpaceSystem
     * element using the fully qualified UNIX style path rules of an XTCE
     * reference.
     *
     * @return XTCESpaceSystem object containing the SpaceSystem and also some
     * helper functions.  If not found, the return can be null.
     *
     */

    public XTCESpaceSystem getSpaceSystem( String fullPath ) {

        for ( XTCESpaceSystem spaceSystem : getSpaceSystemTree() ) {
            if ( spaceSystem.getFullPath().equals( fullPath ) == true ) {
                return spaceSystem;
            }
        }

        return null;

    }

    /** Retrieve an XTCESpaceSystem object that represents the root SpaceSystem
     * element of this XTCE document.
     *
     * @return XTCESpaceSystem representing the root SpaceSystem, which for any
     * valid document can never be null.
     *
     */

    public XTCESpaceSystem getRootSpaceSystem( ) {

        return new XTCESpaceSystem( "/" + topLevelSpaceSystem.getName(), // NOI18N
                                    topLevelSpaceSystem,
                                    this );

    }

    /** Retrieve a list of all the SpaceSystem elements in this XTCE document,
     * wrapped inside XTCESpaceSystem objects.
     *
     * @return List of XTCESpaceSystem objects that are created from the
     * structure of the XTCE document.
     *
     */

    public List<XTCESpaceSystem> getSpaceSystemTree( ) {

        if ( spaceSystemCache != null ) {
            return spaceSystemCache;
        }

        spaceSystemCache = new ArrayList<>();

        XTCESpaceSystem rootSpaceSystem =
            new XTCESpaceSystem( "/" + topLevelSpaceSystem.getName(), // NOI18N
                                 topLevelSpaceSystem,
                                 this );
        spaceSystemCache.add( rootSpaceSystem );
        
        recurseSpaceSystems( rootSpaceSystem, spaceSystemCache );

        return spaceSystemCache;
        
    }

    /** Function to add a new Space System element to the XTCE document
     * structure.
     *
     * @param name String containing the name of the Space System to add.
     *
     * @param path String containing the fully qualified XTCE UNIX style path
     * to the new Space System in the hierarchy.
     *
     * @throws XTCEDatabaseException thrown in the event that this add method
     * is called for a root SpaceSystem element or the desired name conflicts
     * with another SpaceSystem element that already exists.
     *
     */

    public void addSpaceSystem( String name,
                                String path ) throws XTCEDatabaseException {

        List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

        if ( path == null || path.isEmpty() == true ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_addrootss" ) ); // NOI18N
        }

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            if ( spaceSystems.get( iii ).getFullPath().equals( path ) == true ) {
                XTCESpaceSystem parentSpaceSystem = spaceSystems.get( iii );
                List<SpaceSystemType> list = parentSpaceSystem.getReference().getSpaceSystem();
                for ( int jjj = 0; jjj < list.size(); ++jjj ) {
                    if ( list.get( jjj ).getName().equals( name ) == true ) {
                        throw new XTCEDatabaseException(
                            XTCEFunctions.getText( "ss_name_text" ) + // NOI18N
                            " " + // NOI18N
                            name +
                            " " + // NOI18N
                            XTCEFunctions.getText( "error_ssexists" ) + // NOI18N
                            " " + // NOI18N
                            path );
                    }
                }
                SpaceSystemType newSpaceSystem = new SpaceSystemType();
                newSpaceSystem.setName( name );
                list.add( newSpaceSystem );
            }
        }

        setChanged( true );
        spaceSystemCache = null;

    }

    /** Deletes a SpaceSystem element from the current XTCE document.
     *
     * @param ssPath String containing the fully qualified path to the XTCE
     * SpaceSystem element that should be removed from the data model
     * structure.
     *
     * @throws XTCEDatabaseException thrown in the event that this method is
     * called to remove the root SpaceSystem or the SpaceSystem element to be
     * removed cannot be located (as in, it does not exist).
     *
     */

    public void deleteSpaceSystem( String ssPath ) throws XTCEDatabaseException {

        List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

        int lastSlashIndex = ssPath.lastIndexOf('/');
        String pathOnly = ssPath.substring( 0, lastSlashIndex );
        String nameOnly = ssPath.substring( lastSlashIndex + 1 );

        if ( pathOnly == null || pathOnly.isEmpty() == true ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_rootssnodelete" ) ); // NOI18N
        }

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            if ( spaceSystems.get( iii ).getFullPath().equals( pathOnly ) == true ) {
                XTCESpaceSystem parentSpaceSystem = spaceSystems.get( iii );
                List<SpaceSystemType> list = parentSpaceSystem.getReference().getSpaceSystem();
                for ( int jjj = 0; jjj < list.size(); ++jjj ) {
                    if ( list.get( jjj ).getName().equals( nameOnly ) == true ) {
                        list.remove( jjj );
                        jjj = list.size();
                        setChanged( true );
                        spaceSystemCache = null;
                        return;
                    }
                }
            }
        }

        throw new XTCEDatabaseException( XTCEFunctions.getText( "error_ssnotfound" ) + ": " + ssPath ); // NOI18N

    }

    /** Function to retrieve all of the Telemetry Parameters that are defined
     * in the XTCE document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCEParameter objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no telemetry
     * parameters, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCEParameter> getTelemetryParameters( ) {

        List<XTCESpaceSystem>    spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter> list         = new ArrayList<>();

        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            list.addAll( spaceSystem.getTelemetryParameters() );
        }

        return list;

    }

    /** Function to retrieve all of the Telemetry Parameters that are defined
     * in the XTCE document that match a glob style name pattern.
     *
     * Since the parameter name in XTCE is unique by Space System, it is
     * possible for this method to return multiple results even for a name that
     * is exact.
     *
     * TODO: This function can be optimized for searches that do not include
     * glob matching.  Not sure if this is needed though.
     *
     * @param nameGlob String containing a precise name or a glob of potential
     * names.
     *
     * @return List of XTCEParameter objects found, which can be empty.
     *
     */

    public List<XTCEParameter> getTelemetryParameters( String nameGlob ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            List<XTCEParameter> parameters =
                spaceSystems.get( iii ).getTelemetryParameters();
            for ( XTCEParameter parameter : parameters ) {
                if ( XTCEFunctions.matchesUsingGlob( parameter.getName(), nameGlob ) == true ) {
                    list.add( parameter );
                }
            }
        }

        return list;

    }

    /** Function to retrieve all of the Telemetry Parameters that are defined
     * in the XTCE document that match a glob style alias pattern in a
     * specified namespace.
     *
     * @param aliasGlob String containing a precise alias or a glob of
     * potential alias strings.
     *
     * @param aliasNameSpace String containing the namespace of the alias in
     * the XTCE data model.
     *
     * @return List of XTCEParameter objects found, which can be empty.
     *
     */

    public List<XTCEParameter> getTelemetryParameters( String aliasGlob,
                                                       String aliasNameSpace ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            List<XTCEParameter> parameters =
                spaceSystems.get( iii ).getTelemetryParameters();
            for ( XTCEParameter parameter : parameters ) {
                String alias = parameter.getAlias( aliasNameSpace );
                if ( ( alias.isEmpty()                                    == false ) &&
                     ( XTCEFunctions.matchesUsingGlob( alias, aliasGlob ) == true  ) ) {
                    list.add( parameter );
                }
            }
        }

        return list;

    }
    
    /** Function to retrieve all of the Telecommand Parameters that are defined
     * in the XTCE document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCEParameter objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no telecommand
     * parameters, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCEParameter> getTelecommandParameters( ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            list.addAll( spaceSystem.getTelecommandParameters() );
        }

        return list;

    }

    /** Function to retrieve all of the Telecommand Parameters that are defined
     * in the XTCE document that match a glob style name pattern.
     *
     * Since the parameter name in XTCE is unique by Space System, it is
     * possible for this method to return multiple results even for a name that
     * is exact.
     *
     * TODO: This function can be optimized for searches that do not include
     * glob matching.  Not sure if this is needed though.
     *
     * @param nameGlob String containing a precise name or a glob of potential
     * names.
     *
     * @return List of XTCEParameter objects found, which can be empty.
     *
     */

    public List<XTCEParameter> getTelecommandParameters( String nameGlob ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            List<XTCEParameter> parameters =
                spaceSystems.get( iii ).getTelecommandParameters();
            for ( XTCEParameter parameter : parameters ) {
                if ( XTCEFunctions.matchesUsingGlob( parameter.getName(), nameGlob) == true ) {
                    list.add( parameter );
                }
            }
        }

        return list;

    }

    /** Function to retrieve all of the Telecommand Parameters that are defined
     * in the XTCE document that match a glob style alias pattern in a
     * specified namespace.
     *
     * @param aliasGlob String containing a precise alias or a glob of
     * potential alias strings.
     *
     * @param aliasNameSpace String containing the namespace of the alias in
     * the XTCE data model.
     *
     * @return List of XTCEParameter objects found, which can be empty.
     *
     */

    public List<XTCEParameter> getTelecommandParameters( String aliasGlob,
                                                         String aliasNameSpace ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            List<XTCEParameter> parameters =
                spaceSystems.get( iii ).getTelecommandParameters();
            for ( XTCEParameter parameter : parameters ) {
                String alias = parameter.getAlias( aliasNameSpace );
                if ( ( alias.isEmpty()                                    == false ) &&
                     ( XTCEFunctions.matchesUsingGlob( alias, aliasGlob ) == true  ) ) {
                    list.add( parameter );
                }
            }
        }

        return list;

    }

    /** Function to retrieve all of the Parameters that are defined in the XTCE
     * document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCEParameter objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no
     * parameters, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCEParameter> getParameters( ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            list.addAll( spaceSystem.getParameters() );
        }

        return list;

    }

    /** Function to retrieve all of the Telemetry Containers that are defined
     * in the XTCEdocument.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCETMContainer objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no
     * containers, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCETMContainer> getContainers( ) {

       List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
       ArrayList<XTCETMContainer> containers   = new ArrayList<>();

       for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
           containers.addAll( spaceSystem.getContainers() );
       }
       
       return containers;

    }

    /** Retrieve a specific container in the XTCE database by the fully
     * qualified path name to the container, using XTCE document path rules.
     *
     * @param contFullPath String containing the fully qualified path to the
     * container desired.
     *
     * @return XTCETMContainer representing the SequenceContainer element in
     * the XTCE data model.
     *
     * @throws XTCEDatabaseException thrown in the event that the container
     * cannot be located using the provided path.
     *
     */

    public XTCETMContainer getContainer( String contFullPath ) throws XTCEDatabaseException {

        String contPath =
            XTCEFunctions.getPathNameFromReferenceString( contFullPath );
        String contName =
            XTCEFunctions.getNameFromPathReferenceString( contFullPath );

        XTCESpaceSystem spaceSystem = getSpaceSystem( contPath );

        return spaceSystem.getContainer( contName );

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

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCETMContainer> list         = new ArrayList<>();

        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            list.addAll( spaceSystem.getContainers( nameGlob ) );
        }

        return list;

    }

    /** Function to retrieve all of the Telemetry Containers that are members
     * of a specified stream in the XTCE document.
     *
     * @param stream XTCETMStream representing the desired stream to find the
     * included containers.
     *
     * @return List of XTCETMContainers in the stream.
     *
     * @throws XTCEDatabaseException in the event that the stream root
     * container does not exist in the XTCE document data.
     *
     */

    public List<XTCETMContainer> getContainers( XTCETMStream stream ) throws XTCEDatabaseException {

        List<XTCETMContainer>      containers = getContainers();
        ArrayList<XTCETMContainer> retList    = new ArrayList<>();

        XTCETMContainer streamRootContainer =
            getContainer( stream.getStreamContainerPath() );

        String streamRootPath = streamRootContainer.getInheritancePath();

        for ( XTCETMContainer container : containers ) {
            String cPath = container.getInheritancePath();
            if ( cPath.startsWith( streamRootPath ) == true ) {
                retList.add( container );
            }
        }

        return retList;

    }

    /** Function to retrieve all of the Telemetry Streams that are defined
     * in the XTCEdocument.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCETMStream objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no
     * containers, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCETMStream> getStreams( ) {

       List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
       ArrayList<XTCETMStream>    streams      = new ArrayList<>();

       for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
           streams.addAll( spaceSystem.getStreams() );
       }
       
       return streams;

    }

    /** Function to decompose an XTCETMContainer object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * @param container XTCETMContainer object containing the container/packet
     * that the caller wishes to decompose.
     *
     * @param userValues List of XTCEContainerEntryValue objects that
     * represent desired setpoints for parameters in the container.  This
     * permits the caller to decompose a specific packet instance from a
     * container by specifying values for parameters that satisfy include
     * conditions for variable content.  Restriction values for Base Container
     * portions are automatically applied and do not need to be supplied by
     * the caller.
     *
     * @param showAllConditions boolean indicating if the returned content
     * model should provide an array of entry results that include information
     * only rows.  These information only rows consist of rows to announce the
     * start of a new Container or a new Aggregate.  If false, only those rows
     * will be returned for which a concrete start bit and length exist.
     *
     * @return XTCEContainerContentModel representing this XTCETMContainer.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the container completely due to bad references in
     * the XTCE document.
     *
     */

    public XTCEContainerContentModel processContainer( XTCETMContainer               container,
                                                       List<XTCEContainerEntryValue> userValues,
                                                       boolean                       showAllConditions )
        throws XTCEDatabaseException {

        return new XTCEContainerContentModel( container,
                                              getSpaceSystemTree(),
                                              userValues,
                                              showAllConditions );

    }

    /** Function to decompose an XTCETMContainer object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * @param container XTCETMContainer object containing the container/packet
     * that the caller wishes to decompose.
     *
     * @param binaryData byte[] containing the container binary encoded data
     * so that the output object contains entries with actual values from a
     * real binary image.
     *
     * @return XTCEContainerContentModel representing this XTCETMContainer.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the container completely due to bad references in
     * the XTCE document.
     *
     */

    public XTCEContainerContentModel processContainer( XTCETMContainer container,
                                                       byte[]          binaryData )
        throws XTCEDatabaseException {

        return new XTCEContainerContentModel( container,
                                              getSpaceSystemTree(),
                                              binaryData );

    }

    /** Function to decompose an XTCETelecommand object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * @param tcObject XTCETelecommand object containing the telecommand
     * that the caller wishes to decompose.
     *
     * @param userValues List of XTCEContainerEntryValue objects that
     * represent desired setpoints for arguments and/or parameters in the
     * telecommand container.  This permits the caller to decompose a specific
     * telecommand instance from a more general telecommand by specifying
     * values for parameters that satisfy include conditions for variable
     * content.  Restriction values for Base MetaComamand portions are
     * automatically applied and do not need to be supplied by the caller.
     *
     * @param showAllConditions boolean indicating if the returned content
     * model should provide an array of entry results that include information
     * only rows.  These information only rows consist of rows to announce the
     * start of a new Container or a new Aggregate.  If false, only those rows
     * will be returned for which a concrete start bit and length exist.
     *
     * @return XTCEContainerContentModel representing this XTCETelecommand.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the container completely due to bad references in
     * the XTCE document.
     *
     */

    public XTCETelecommandContentModel processTelecommand( XTCETelecommand               tcObject,
                                                           List<XTCEContainerEntryValue> userValues,
                                                           boolean                       showAllConditions )
        throws XTCEDatabaseException {

        return new XTCETelecommandContentModel( tcObject,
                                                getSpaceSystemTree(),
                                                userValues,
                                                showAllConditions );

    }

    /** Retrieve the containers in the XTCE document that directly reference
     * an entry in their manifest that includes the provided Parameter.
     *
     * @param parameter XTCEParameter object to find in the containers defined
     * in this XTCE database document.
     *
     * @return List of XTCETMContainer objects found, or an empty list if
     * the no container references the parameter.
     *
     */

    public List<XTCETMContainer> findContainers( XTCEParameter parameter ) {

        List<XTCETMContainer>      allContainers = getContainers();
        ArrayList<XTCETMContainer> containers    = new ArrayList<>();

        for ( XTCETMContainer container : allContainers ) {
            if ( container.contains( parameter ) == true ) {
                containers.add( container );
            }
        }

        return containers;

    }

    /** Retrieve the type reference from the JAXB generated objects for a
     * particular TM Parameter fully qualified type object path in the XTCE
     * data model.
     *
     * @param typePath String containing the UNIX style fully qualified path
     * to the TM Parameter type object.
     *
     * @return NameDescriptionType base class for the type object found, or a
     * null object reference in the event that it is not found.
     *
     */

    public NameDescriptionType getParameterTypeReference( String typePath ) {
        
        if ( parameterTypes.containsKey( typePath ) == true ) {
            return parameterTypes.get( typePath );
        } else {
            return null;
        }

    }

    /** Retrieve the type reference from the JAXB generated objects for a
     * particular TC Argument fully qualified type object path in the XTCE
     * data model.
     *
     * @param typePath String containing the UNIX style fully qualified path
     * to the TC Argument type object.
     *
     * @return NameDescriptionType base class for the type object found, or a
     * null object reference in the event that it is not found.
     *
     */

    public NameDescriptionType getArgumentTypeReference( String typePath ) {

        if ( argumentTypes.containsKey( typePath ) == true ) {
            return argumentTypes.get( typePath );
        } else {
            return null;
        }

    }

    /** Private function to recursively locate SpaceSystem elements from the
     * XTCE data model.
     *
     * @param currentSpaceSystem XTCESpaceSystem object to start the recursion
     * from.
     *
     * @param spaceSystemList List of XTCESpaceSystem objects that will
     * be populated for the caller.
     *
     */

    private void recurseSpaceSystems( XTCESpaceSystem       currentSpaceSystem,
                                      List<XTCESpaceSystem> spaceSystemList ) {
        
        List<SpaceSystemType> ssTree =
            currentSpaceSystem.getReference().getSpaceSystem();

        for ( int iii = 0; iii < ssTree.size(); ++iii ) {
            if ( ssTree.get( iii ) == null ) {
               //System.out.println( "not sure why null" );
            } else {
               String newCurrent =
                    currentSpaceSystem.getFullPath() + "/" + ssTree.get( iii ).getName(); // NOI18N
               XTCESpaceSystem childSpaceSystem =
                    new XTCESpaceSystem( newCurrent, ssTree.get( iii ), this );
               spaceSystemList.add( childSpaceSystem );
               recurseSpaceSystems( childSpaceSystem, spaceSystemList );
            }
        }
    }

    /** Private method to capture the Parameter Type paths with mapping to a
     * reference to the Argument Type itself, as generically represented by a
     * NameDescriptionType in the inheritance tree.
     *
     * The mapping is kept in a HashMap, which a private data member of this
     * object.
     *
     */

    private void cacheParameterTypes() {

        parameterTypes = new HashMap<>();

        List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {

            try {
                List<NameDescriptionType> tmtypes =
                    spaceSystems.get( iii ).
                                 getReference().
                                 getTelemetryMetaData().
                                 getParameterTypeSet().
                                 getStringParameterTypeOrEnumeratedParameterTypeOrIntegerParameterType();
                for ( int jjj = 0; jjj < tmtypes.size(); ++jjj ) {
                    parameterTypes.put( spaceSystems.get( iii ).getFullPath() +
                                        "/" + // NOI18N
                                        tmtypes.get( jjj ).getName(),
                                        tmtypes.get( jjj ) );
                }
            } catch ( NullPointerException ex ) {
                // this is okay, skip this SpaceSystem since it does not have types
            }

            try {
                List<NameDescriptionType> tctypes =
                    spaceSystems.get( iii ).
                                 getReference().
                                 getCommandMetaData().
                                 getParameterTypeSet().
                                 getStringParameterTypeOrEnumeratedParameterTypeOrIntegerParameterType();
                for ( int jjj = 0; jjj < tctypes.size(); ++jjj ) {
                    parameterTypes.put( spaceSystems.get( iii ).getFullPath() +
                                        "/" + // NOI18N
                                        tctypes.get( jjj ).getName(),
                                        tctypes.get( jjj ) );
                }
            } catch ( NullPointerException ex ) {
                // this is okay, skip this SpaceSystem since it does not have types
            }

        }

    }

    /** Private method to capture the Argument Type paths with mapping to a
     * reference to the Argument Type itself, as generically represented by a
     * NameDescriptionType in the inheritance tree.
     *
     * The mapping is kept in a HashMap, which a private data member of this
     * object.
     *
     */

    private void cacheArgumentTypes() {

        argumentTypes = new HashMap<>();

        List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {

            try {
               List<NameDescriptionType> argtypes =
                    spaceSystems.get( iii ).
                                 getReference().
                                 getCommandMetaData().
                                 getArgumentTypeSet().
                                 getStringArgumentTypeOrEnumeratedArgumentTypeOrIntegerArgumentType();
               for ( int jjj = 0; jjj < argtypes.size(); ++jjj ) {
                  argumentTypes.put( spaceSystems.get( iii ).getFullPath() + "/" + argtypes.get( jjj ).getName(), argtypes.get( jjj ) ); // NOI18N
               }
            } catch ( NullPointerException ex ) {
                // this is okay, skip this SpaceSystem since it does not have types
            }

        }

    }

    /** Private method to update a progress bar object.
     *
     * Currently not used for anything as the passed reference is always null.
     *
     * @param listener XTCEProgressListener object or a null reference if none
     * is being used by the calling application.
     *
     * @param percentComplete int containing the current estimation of percent
     * complete, from 0-100.
     *
     * @param currentStep String containing the name of the current step.
     *
     */

    private void updateLoadProgress( XTCEProgressListener listener,
                                     int                  percentComplete,
                                     String               currentStep ) {

        if ( listener != null ) {
            listener.updateProgress( percentComplete, currentStep );
            try {
                Thread.sleep( 500 );
            } catch ( InterruptedException ex ) {
                // do nothing
            }
        }

    }

    // Private Data Members

    private SpaceSystemType topLevelSpaceSystem = null;
    private boolean         databaseChanged     = false;

    private HashMap<String, NameDescriptionType> parameterTypes   = null;
    private HashMap<String, NameDescriptionType> argumentTypes    = null;
    private ArrayList<XTCESpaceSystem>           spaceSystemCache = null;

}
