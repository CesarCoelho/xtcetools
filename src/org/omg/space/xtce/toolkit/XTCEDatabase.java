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

    public String getSchemaFromDocument() {
        return schemaLocation;
    }

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

    public XTCESpaceSystem getSpaceSystem( String fullPath ) {

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            if ( spaceSystem.getFullPath().equals( fullPath ) == true ) {
                return spaceSystem;
            }
        }

        return null;

    }

    public XTCESpaceSystem getRootSpaceSystem() {

        return new XTCESpaceSystem( "/" + topLevelSpaceSystem.getName(),
                                    topLevelSpaceSystem,
                                    this );

    }

    public ArrayList<XTCESpaceSystem> getSpaceSystemTree() {

        /// @todo make this efficient with a cache or something

        ArrayList<XTCESpaceSystem> spaceSystems = new ArrayList<XTCESpaceSystem>();

        XTCESpaceSystem rootSpaceSystem =
            new XTCESpaceSystem( "/" + topLevelSpaceSystem.getName(),
                                 topLevelSpaceSystem,
                                 this );
        spaceSystems.add( rootSpaceSystem );
        
        recurseSpaceSystems( rootSpaceSystem, spaceSystems );

        return spaceSystems;
        
    }

    public void addSpaceSystem( String name, String path ) throws XTCEDatabaseException {

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

    public ArrayList<XTCEParameter> getTelemetryParameters( ) {

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter> list = new ArrayList<XTCEParameter>();
        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            list.addAll( spaceSystems.get( iii ).getTelemetryParameters() );
        }
        return list;

    }

    public ArrayList<XTCEParameter> getTelecommandParameters( ) {

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter> list = new ArrayList<XTCEParameter>();
        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            list.addAll( spaceSystems.get( iii ).getTelecommandParameters() );
        }
        return list;

    }
    
    public ArrayList<XTCEParameter> getParameters( ) {

        ArrayList<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter> list = new ArrayList<XTCEParameter>();
        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            list.addAll( spaceSystems.get( iii ).getParameters() );
        }
        return list;

    }

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

    public NameDescriptionType getParameterTypeReference( String typePath ) {
        
        if ( parameterTypes.containsKey( typePath ) == true ) {
            return parameterTypes.get( typePath );
        } else {
            return null;
        }

    }

    public NameDescriptionType getArgumentTypeReference( String typePath ) {

        if ( argumentTypes.containsKey( typePath ) == true ) {
            return argumentTypes.get( typePath );
        } else {
            return null;
        }

    }

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
