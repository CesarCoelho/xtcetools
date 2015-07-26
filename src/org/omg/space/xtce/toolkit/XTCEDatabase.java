/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.omg.space.xtce.database.NameDescriptionType;
import org.omg.space.xtce.database.ObjectFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.omg.space.xtce.database.SpaceSystemType;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** The XTCEDatabase class is the first core object to be used by a client
 * that is working with an XTCE Database File.
 *
 *
 * @author David Overeem
 *
 */

public final class XTCEDatabase {

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

        if ( dbFile.isFile() == false || dbFile.canRead() == false ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "file_chooser_noload_text" ) + // NOI18N
                " " +
                dbFile.toString() );
        }

        String currentDir = System.getProperty( "user.dir" ); // NOI18N

        try {
            updateLoadProgress( listener, 0, "Loading File" );
            System.setProperty( "user.dir", dbFile.getParent() );
            jaxbContext = JAXBContext.newInstance( XTCEConstants.XTCE_PACKAGE );
            if ( validateOnLoad == true ) {
                updateLoadProgress( listener, 5, "Validating File" ); 
            } else {
                updateLoadProgress( listener, 25, "Validation Disabled (WARNING: Viewer May Be De-Stabilized)" );
            }
            populateDataModel( dbFile,
                               listener,
                               validateOnLoad,
                               applyXIncludes );
            xtceFilename = dbFile;
            updateLoadProgress( listener, 100, "Completed" );
        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( ex );
        } finally {
            System.setProperty( "user.dir", currentDir ); // NOI18N
        }

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

        try {

            ObjectFactory factory = new ObjectFactory();
            jaxbContext         = JAXBContext.newInstance( XTCEConstants.XTCE_PACKAGE );
            topLevelSpaceSystem = ( factory.createSpaceSystemType() );
            jaxbElementRoot     = factory.createSpaceSystem( topLevelSpaceSystem );
            topLevelSpaceSystem.setName( topLevelSpaceSystemName );
            xtceFilename = new File( "" );
            setChanged( true );

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( ex );
        }

    }

    /** Retrieve the current filename, in fully qualified path form, for the
     * XTCE database file that is loaded.
     *
     * @return String containing the name, or an empty string if this is a new
     * database that has not yet been saved to a filesystem.
     *
     */

    public File getFilename( ) {
        return xtceFilename;
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

    /** Retrieve the XTCE schema document that is applicable for this loaded
     * file.
     *
     * TODO: This only returns the default right now, need to work that.
     *
     * @return String containing the file and path to the schema document.
     *
     */

    public String getSchemaFromDocument( ) {
        return schemaLocation;
    }

    /** Retrieve the XTCE namespace that is applicable to the document that is
     * loaded.
     *
     * TODO: This only returns the default right now, need to work that.
     *
     * @return String containing the namespace.
     *
     */

    public String getNamespaceFromDocument( ) {
        /// @todo work on this document namespace
        return XTCEConstants.XTCE_NAMESPACE;
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

        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( new XTCENamespaceContext() );

        try {

            XPathExpression expr = xpath.compile( query );
            NodeList nnn = (NodeList)expr.evaluate( domDocumentRoot.getDocumentElement(),
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

    public void saveDatabase( File dbFile ) throws XTCEDatabaseException {
        
        try {

            // thinking about this update, maybe it should be done when nodes
            // are edited to keep in sync for xpath

            if ( getChanged() == true ) {
                domBinder.updateXML( jaxbElementRoot );
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer        t  = tf.newTransformer();

            t.transform( new DOMSource( domDocumentRoot ),
                         new StreamResult( dbFile ) );

            //Marshaller mmm = jaxbContext.createMarshaller();

            //mmm.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            //mmm.setProperty( Marshaller.JAXB_SCHEMA_LOCATION,
            //                 XTCEConstants.XTCE_NAMESPACE +
            //                 " " +
            //                 XTCEConstants.DEFAULT_SCHEMA_FILE );

            //XMLOutputFactory xof = XMLOutputFactory.newInstance();
            //FileOutputStream stream = new FileOutputStream( dbFile );
            //XMLStreamWriter writer = xof.createXMLStreamWriter( stream );
            // TODO: not sure how to manipulate if xtce: prefix is present
            //writer.setDefaultNamespace( XTCEConstants.XTCE_NAMESPACE );
            //writer.setPrefix( "", XTCEConstants.XTCE_NAMESPACE );
            //writer.setPrefix( "xtce", XTCEConstants.XTCE_NAMESPACE );

            //mmm.marshal( jaxbElementRoot, stream );

            xtceFilename = dbFile;

            //stream.close();

            setChanged( false );

        } catch ( Exception ex ) {
            
            throw new XTCEDatabaseException( ex );
            
        }
        
    }

    /** Retrieve an arbitrary SpaceSystem wrapped element from the document.
     *
     * @param fullPath String containing the full path to the XTCE SpaceSystem
     * element using the fully qualified UNIX style path rules of an XTCE
     * reference.
     *
     * @return XTCESpaceSystem object containing the SpaceSystem and also some
     * helper functions.
     *
     */

    public XTCESpaceSystem getSpaceSystem( String fullPath ) {

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
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

        return new XTCESpaceSystem( "/" + topLevelSpaceSystem.getName(),
                                    topLevelSpaceSystem,
                                    this );

    }

    /** Retrieve a list of all the SpaceSystem elements in this XTCE document,
     * wrapped inside XTCESpaceSystem objects.
     *
     * @return ArrayList of XTCESpaceSystem objects that are created from the
     * structure of the XTCE document.
     *
     */

    public ArrayList<XTCESpaceSystem> getSpaceSystemTree( ) {

        /// @todo make this efficient with a cache or something

        ArrayList<XTCESpaceSystem> spaceSystems =
            new ArrayList<XTCESpaceSystem>();

        XTCESpaceSystem rootSpaceSystem =
            new XTCESpaceSystem( "/" + topLevelSpaceSystem.getName(),
                                 topLevelSpaceSystem,
                                 this );
        spaceSystems.add( rootSpaceSystem );
        
        recurseSpaceSystems( rootSpaceSystem, spaceSystems );

        return spaceSystems;
        
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

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

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

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

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
     * @return ArrayList of XTCEParameter objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no telemetry
     * parameters, which is likely only to happen on a newly created database
     * file.
     *
     */

    public ArrayList<XTCEParameter> getTelemetryParameters( ) {

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter> list = new ArrayList<>();
        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            list.addAll( spaceSystems.get( iii ).getTelemetryParameters() );
        }
        return list;

    }

    /** Function to retrieve all of the Telecommand Parameters that are defined
     * in the XTCE document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return ArrayList of XTCEParameter objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no telecommand
     * parameters, which is likely only to happen on a newly created database
     * file.
     *
     */

    public ArrayList<XTCEParameter> getTelecommandParameters( ) {

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter> list = new ArrayList<>();
        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            list.addAll( spaceSystems.get( iii ).getTelecommandParameters() );
        }
        return list;

    }

    /** Function to retrieve all of the Parameters that are defined in the XTCE
     * document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return ArrayList of XTCEParameter objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no
     * parameters, which is likely only to happen on a newly created database
     * file.
     *
     */

    public ArrayList<XTCEParameter> getParameters( ) {

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter> list = new ArrayList<>();
        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            list.addAll( spaceSystems.get( iii ).getParameters() );
        }
        return list;

    }

    /** Function to retrieve all of the Telemetry Containers that are defined
     * in the XTCEdocument.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return ArrayList of XTCETMContainer objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no
     * containers, which is likely only to happen on a newly created database
     * file.
     *
     */

    public ArrayList<XTCETMContainer> getContainers( ) {

       ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
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

    /** Function to retrieve all of the Telemetry Containers that are members
     * of a specified stream in the XTCE document.
     *
     * @param stream XTCETMStream representing the desired stream to find the
     * included containers.
     *
     * @return ArrayList of XTCETMContainers in the stream.
     *
     * @throws XTCEDatabaseException in the event that the stream root
     * container does not exist in the XTCE document data.
     *
     */

    public ArrayList<XTCETMContainer> getContainers( XTCETMStream stream ) throws XTCEDatabaseException {

        ArrayList<XTCETMContainer> containers = getContainers();
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
     * @return ArrayList of XTCETMStream objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no
     * containers, which is likely only to happen on a newly created database
     * file.
     *
     */

    public ArrayList<XTCETMStream> getStreams( ) {

       ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
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
     * @param userValues ArrayList of XTCEContainerEntryValue objects that
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

    public XTCEContainerContentModel processContainer( XTCETMContainer                    container,
                                                       ArrayList<XTCEContainerEntryValue> userValues,
                                                       boolean                            showAllConditions )
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
     * @param userValues ArrayList of XTCEContainerEntryValue objects that
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

    public XTCETelecommandContentModel processTelecommand( XTCETelecommand                    tcObject,
                                                           ArrayList<XTCEContainerEntryValue> userValues,
                                                           boolean                            showAllConditions )
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
     * @return ArrayList of XTCETMContainer objects found, or an empty list if
     * the no container references the parameter.
     *
     */

    public ArrayList<XTCETMContainer> findContainers( XTCEParameter parameter ) {

        ArrayList<XTCETMContainer> allContainers = getContainers();
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
     * @param spaceSystemList ArrayList of XTCESpaceSystem objects that will
     * be populated for the caller.
     *
     */

    private void recurseSpaceSystems( XTCESpaceSystem            currentSpaceSystem,
                                      ArrayList<XTCESpaceSystem> spaceSystemList ) {
        
        List<SpaceSystemType> ssTree =
            currentSpaceSystem.getReference().getSpaceSystem();

        for ( int iii = 0; iii < ssTree.size(); ++iii ) {
            if ( ssTree.get( iii ) == null ) {
               //System.out.println( "not sure why null" );
            } else {
               String newCurrent =
                    currentSpaceSystem.getFullPath() + "/" + ssTree.get( iii ).getName();
               XTCESpaceSystem childSpaceSystem =
                    new XTCESpaceSystem( newCurrent, ssTree.get( iii ), this );
               spaceSystemList.add( childSpaceSystem );
               recurseSpaceSystems( childSpaceSystem, spaceSystemList );
            }
        }
    }

    /** Private method to read an XTCE XML document and populate the internal
     * indexes for this object.
     *
     * @param dbFile File object containing the XTCE XML document to load.
     *
     * @param listener XTCEProgressListener currently unused as the progress
     * bar portion of the graphical user interface has not been completed or
     * even well conceived at this point.  A null object is currently used.
     *
     * @param validate boolean to indicate if the XML document should be
     * validated through the XML Schema Description (XSD) document that is
     * configured for the XTCEDatabase object.  In the future this will be more
     * flexible.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     * @throws XTCEDatabaseException thrown in the event that the document
     * cannot be read or is not sufficiently valid to complete construction of
     * the internal data model.
     *
     */

    private void populateDataModel( File                 dbFile,
                                    XTCEProgressListener listener,
                                    boolean              validate,
                                    boolean              applyXIncludes ) throws XTCEDatabaseException {
        
        try {

            updateLoadProgress( listener, 30, "Loading File" );

            domBinder = jaxbContext.createBinder();

            //Unmarshaller     um  = jaxbContext.createUnmarshaller();
            //SAXParserFactory spf = SAXParserFactory.newInstance();

            //spf.setXIncludeAware( applyXIncludes );
	    //spf.setNamespaceAware( true );
            //spf.setValidating( validate );

            //SAXParser parser = spf.newSAXParser();
            //parser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
            //                    "http://www.w3.org/2001/XMLSchema" );

            //XMLReader              reader  = parser.getXMLReader();
            XTCESchemaErrorHandler handler = new XTCESchemaErrorHandler();

            //reader.setErrorHandler( handler );

            //FileInputStream stream = new FileInputStream( dbFile );

            //SAXSource source = new SAXSource( reader, new InputSource( stream ) );

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setXIncludeAware( applyXIncludes );
            dbf.setNamespaceAware( true );
            dbf.setValidating( validate );

            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler( handler );

            domDocumentRoot = db.parse( dbFile );

            jaxbElementRoot = (JAXBElement)domBinder.unmarshal( domDocumentRoot );
            
            Object candidate = jaxbElementRoot.getValue();

            if ( candidate.getClass().equals( SpaceSystemType.class ) == false ) {
                throw new XTCEDatabaseException( XTCEFunctions.getText( "error_invalidxmlfile" ) ); // NOI18N
            }
            
            topLevelSpaceSystem = (SpaceSystemType)candidate;

            updateLoadProgress( listener, 70, "Caching Type Information" );

            cacheParameterTypes();
            cacheArgumentTypes();

        } catch ( Exception ex ) {
            
            throw new XTCEDatabaseException( ex );
            
        }
        
    }

    /** Private method to perform XML Schema Description (XSD) verification on
     * the XTCE document that has been loaded.
     *
     * At present, this function is somewhat inefficient because it needs to
     * do a separate loading and verification prior to the unmarshal operation
     * of the JAXB data model.  It would be nice if this could be combined.
     *
     * @param dbFile File object containing the fully qualified filesystem path
     * to the XTCE document to be loaded and verified.
     *
     * @throws XTCEDatabaseException thrown in the event that the document does
     * not pass validation.  The specific messages in the exception will
     * reflect the issues.  The application may de-stabilize if an document is
     * used that does not pass validation against the schema document.
     *
     */

    private void validateInputDocument( File dbFile ) throws XTCEDatabaseException {
        
        try {

            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware( true );
            spf.setXIncludeAware( true );
            spf.setValidating( true );
            
            SAXParser parser = spf.newSAXParser();
            parser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage", // NOI18N
                                "http://www.w3.org/2001/XMLSchema" ); // NOI18N

            FileInputStream stream = new FileInputStream( dbFile );
            
            XTCESchemaErrorHandler handler = new XTCESchemaErrorHandler();
            
            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler( handler );
            reader.parse( new InputSource( stream ) );

            if ( handler.errors > 0 ) {
                throw new XTCEDatabaseException( handler.messages );
            }
            
        } catch ( Exception ex ) {

            throw new XTCEDatabaseException( ex );
            
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

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

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
                                        "/" +
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
                                        "/" +
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

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {

            try {
               List<NameDescriptionType> argtypes =
                    spaceSystems.get( iii ).
                                 getReference().
                                 getCommandMetaData().
                                 getArgumentTypeSet().
                                 getStringArgumentTypeOrEnumeratedArgumentTypeOrIntegerArgumentType();
               for ( int jjj = 0; jjj < argtypes.size(); ++jjj ) {
                  argumentTypes.put( spaceSystems.get( iii ).getFullPath() + "/" + argtypes.get( jjj ).getName(), argtypes.get( jjj ) );
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

    private File            xtceFilename        = null;
    private JAXBContext     jaxbContext         = null;
    private SpaceSystemType topLevelSpaceSystem = null;
    private JAXBElement     jaxbElementRoot     = null;
    private boolean         databaseChanged     = false;
    private String          schemaLocation      = XTCEConstants.DEFAULT_SCHEMA_FILE;
    private Document        domDocumentRoot     = null;
    private Binder<Node>    domBinder           = null;

    private HashMap<String, NameDescriptionType> parameterTypes = null;
    private HashMap<String, NameDescriptionType> argumentTypes  = null;

}
