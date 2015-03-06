/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;
import org.omg.space.xtce.database.NameDescriptionType;
import org.omg.space.xtce.database.ObjectFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import org.omg.space.xtce.database.SpaceSystemType;

/** The XTCEDatabase class is the first core object to be used by a client
 * that is working with an XTCE Database File.
 *
 *
 * @author Melanie Laub
 *
 */

public class XTCEDatabase {

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
                         XTCEProgressListener listener ) throws XTCEDatabaseException {

        String currentDir = System.getProperty( "user.dir" );

        try {
            updateLoadProgress( listener, 0, "Loading File" );
            System.setProperty( "user.dir", dbFile.getParent() );
            jaxbContext = JAXBContext.newInstance( XTCEConstants.XTCE_PACKAGE );
            if ( validateOnLoad == true ) {
                updateLoadProgress( listener, 5, "Validating File" ); 
            } else {
                updateLoadProgress( listener, 25, "Validation Disabled (WARNING: Viewer May Be De-Stabilized)" );
            }
            populateDataModel( dbFile, listener, validateOnLoad );
            xtceFilename = dbFile;
            updateLoadProgress( listener, 100, "Completed" );
        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( ex );
        } finally {
            System.setProperty( "user.dir", currentDir );
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
            topLevelSpaceSystem = (SpaceSystemType)( factory.createSpaceSystemType() );
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

    public File getFilename() {
        return xtceFilename;
    }

    /** Retrieve the changed flag indicating if some part of the XTCE document
     * has changed since it has last been saved.
     *
     * @return boolean indicating if a change has been made that has not been
     * yet saved.
     *
     */

    public boolean getChanged() {
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

    public String getSchemaFromDocument() {
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

    public String getNamespaceFromDocument() {
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

    public XTCESpaceSystemMetrics getMetrics() {
        return new XTCESpaceSystemMetrics( this );
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

            Marshaller mmm = jaxbContext.createMarshaller();

            mmm.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            mmm.setProperty( Marshaller.JAXB_SCHEMA_LOCATION,
                             XTCEConstants.XTCE_NAMESPACE +
                             " " +
                             XTCEConstants.DEFAULT_SCHEMA_FILE );

            XMLOutputFactory xof = XMLOutputFactory.newInstance();
            FileOutputStream stream = new FileOutputStream( dbFile );
            XMLStreamWriter writer = xof.createXMLStreamWriter( stream );
            // TODO: not sure how to manipulate if xtce: prefix is present
            writer.setDefaultNamespace( XTCEConstants.XTCE_NAMESPACE );
            writer.setPrefix( "", XTCEConstants.XTCE_NAMESPACE );
            writer.setPrefix( "xtce", XTCEConstants.XTCE_NAMESPACE );

            mmm.marshal( jaxbElementRoot, stream );

            xtceFilename = dbFile;

            stream.close();

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

    public XTCESpaceSystem getRootSpaceSystem() {

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

    public ArrayList<XTCESpaceSystem> getSpaceSystemTree() {

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
            throw new XTCEDatabaseException( "Root SpaceSystem is made with Create Database Menu Item" );
        }

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            if ( spaceSystems.get( iii ).getFullPath().equals( path ) == true ) {
                XTCESpaceSystem parentSpaceSystem = spaceSystems.get( iii );
                List<SpaceSystemType> list = parentSpaceSystem.getReference().getSpaceSystem();
                for ( int jjj = 0; jjj < list.size(); ++jjj ) {
                    if ( list.get( jjj ).getName().equals( name ) == true ) {
                        throw new XTCEDatabaseException( "Space System named " + name + " already exist in " + path );
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

        int lastSlashIndex = ssPath.lastIndexOf( "/" );
        String pathOnly = ssPath.substring( 0, lastSlashIndex );
        String nameOnly = ssPath.substring( lastSlashIndex + 1 );

        if ( pathOnly == null || pathOnly.isEmpty() == true ) {
            throw new XTCEDatabaseException( "Root SpaceSystem cannot be deleted" );
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

        throw new XTCEDatabaseException( "Space System " + ssPath + " not found" );

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
        ArrayList<XTCEParameter> list = new ArrayList<XTCEParameter>();
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
        ArrayList<XTCEParameter> list = new ArrayList<XTCEParameter>();
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
        ArrayList<XTCEParameter> list = new ArrayList<XTCEParameter>();
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

    public ArrayList<XTCETMContainer> getContainers() {

       ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
       ArrayList<XTCETMContainer> containers   = new ArrayList<XTCETMContainer>();

       for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
           containers.addAll( spaceSystem.getContainers() );
       }
       
       return containers;

    }

    public XTCEContainerContentModel processContainer( XTCETMContainer                    container,
                                                       ArrayList<XTCEContainerEntryValue> userValues,
                                                       boolean                            showAllConditions )
        throws XTCEDatabaseException {

        return new XTCEContainerContentModel( container,
                                              getSpaceSystemTree(),
                                              userValues,
                                              showAllConditions );

    }

    public XTCETelecommandContentModel processTelecommand( XTCETelecommand                    tcObject,
                                                           ArrayList<XTCEContainerEntryValue> userValues,
                                                           boolean                            showAllConditions )
        throws XTCEDatabaseException {

        return new XTCETelecommandContentModel( tcObject,
                                                getSpaceSystemTree(),
                                                userValues,
                                                showAllConditions );

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
    
    private void populateDataModel( File                 dbFile,
                                    XTCEProgressListener listener,
                                    boolean              validate ) throws XTCEDatabaseException {
        
        try {

            updateLoadProgress( listener, 30, "Loading File" );

            Unmarshaller     um  = jaxbContext.createUnmarshaller();
            SAXParserFactory spf = SAXParserFactory.newInstance();

            spf.setXIncludeAware( true );
	    spf.setNamespaceAware( true );
            spf.setValidating( validate );

            SAXParser parser = spf.newSAXParser();
            parser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                                "http://www.w3.org/2001/XMLSchema" );

            XMLReader              reader  = parser.getXMLReader();
            XTCESchemaErrorHandler handler = new XTCESchemaErrorHandler();

            reader.setErrorHandler( handler );

            FileInputStream stream = new FileInputStream( dbFile );

            SAXSource source = new SAXSource( reader, new InputSource( stream ) );

            jaxbElementRoot = (JAXBElement)um.unmarshal( source );
            
            Object candidate = jaxbElementRoot.getValue();

            if ( candidate.getClass().equals( SpaceSystemType.class ) == false ) {
                throw new XTCEDatabaseException( "Requested XML Document is not an XTCE Document" );
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
            parser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                                "http://www.w3.org/2001/XMLSchema" );

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

        parameterTypes = new HashMap<String, NameDescriptionType>();

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

        argumentTypes = new HashMap<String, NameDescriptionType>();

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

    private HashMap<String, NameDescriptionType> parameterTypes = null;
    private HashMap<String, NameDescriptionType> argumentTypes  = null;

}
