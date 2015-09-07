/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.omg.space.xtce.database.SpaceSystemType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/** Helper class to do the XTCE document parsing to support the XTCEDatabase
 * class.
 *
 * @author dovereem
 */

public abstract class XTCEDatabaseParser {

    /** Retrieve the current filename, in fully qualified path form, for the
     * XTCE database file that is loaded.
     *
     * @return String containing the name, or an empty string if this is a new
     * database that has not yet been saved to a filesystem.
     *
     */

    public File getFilename( ) {
        return xtceFilename_;
    }

    /** Protected method to set a new filename when the database is saved.
     *
     * @param dbFile File object containing the file that was saved.
     *
     */

    protected void setFilename( File dbFile ) {
        xtceFilename_ = dbFile.getAbsoluteFile();
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
        return schemaLocation_;
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

    /** Retrieve the list of warnings and errors that were collected during
     * parsing of the XTCE document by the SAX parser.
     *
     */

    public List<String> getDocumentWarnings( ) {
        return warnings_;
    }

    /** Retrieve the count of error messages from the parsing.
     *
     * @return long count of the number of messages logged as errors when
     * parsing a document.
     *
     */

    public long getErrorCount( ) {
        return errorCount_;
    }

    /** Retrieve the read only flag that was provided when the XTCE document
     * was loaded and parsed.
     *
     * When this flag is true, XPath queries and document saves cannot be used.
     *
     * @return boolean indicating if the document was opened read-only.
     *
     */

    public boolean isReadOnly( ) {
        return ( domLoaded_ == false );
    }

    /** Method to perform XML Schema Description (XSD) verification on an
     * arbitrary XTCE document.
     *
     * @param dbFile File object containing the fully qualified filesystem path
     * to the XTCE document to be verified.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     */

    public static List<String> validateDocument( File    dbFile,
                                                 boolean applyXIncludes ) {

        String currentDir = System.getProperty( "user.dir" ); // NOI18N
        String path       = dbFile.getAbsolutePath();

        path = XTCEFunctions.getPathNameFromReferenceString( path );

        System.out.println( "Path is: " + path );
        if ( path != null ) {
            System.setProperty( "user.dir", path ); // NOI18N
        }

        XTCESchemaErrorHandler handler = new XTCESchemaErrorHandler();

        try {

            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware( true );
            spf.setXIncludeAware( true );
            spf.setValidating( true );
            
            SAXParser parser = spf.newSAXParser();
            parser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage", // NOI18N
                                "http://www.w3.org/2001/XMLSchema" ); // NOI18N

            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler( handler );
            reader.parse( new InputSource( dbFile.getName() ) );

        } catch ( ParserConfigurationException ex ) {
            List<String> messages = new ArrayList<>();
            messages.add( "Bad Configuration: " + ex.getLocalizedMessage() );
            return messages;
        } catch ( SAXParseException ex ) {
            handler.fatalError( ex );
        } catch ( SAXException ex ) {
            handler.fatalError( ex );
        } catch ( IOException ex ) {
            handler.fatalError( ex );
        } finally {
            System.setProperty( "user.dir", currentDir ); // NOI18N
        }

        return handler.getMessages();

    }

    protected SpaceSystemType newDatabase( String spaceSystemName ) throws XTCEDatabaseException {

        try {

            jaxbContext_ =
                JAXBContext.newInstance( XTCEConstants.XTCE_PACKAGE );

            domBinder_ = jaxbContext_.createBinder();

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setXIncludeAware( true );
            dbf.setNamespaceAware( true );
            dbf.setValidating( false );

            DocumentBuilder db = dbf.newDocumentBuilder();

            domDocumentRoot_ = db.newDocument();
            domDocumentRoot_.setDocumentURI( XTCEConstants.XTCE_NAMESPACE );
            Element root =
                domDocumentRoot_.createElementNS( XTCEConstants.XTCE_NAMESPACE,
                                                  "SpaceSystem" );
            root.setAttribute( "name", spaceSystemName );
            domDocumentRoot_.appendChild( root );

            jaxbElementRoot_ =
                (JAXBElement)domBinder_.unmarshal( domDocumentRoot_ );

            Object candidate = jaxbElementRoot_.getValue();

            if ( candidate.getClass().equals( SpaceSystemType.class ) == false ) {
                String exceptionMessage =
                    XTCEFunctions.getText( "error_invalidxmlfile" ); // NOI18N
                throw new XTCEDatabaseException( exceptionMessage );
            }

            errorCount_ = 0;
            warnings_   = new ArrayList<>();
            domLoaded_  = true;

            return (SpaceSystemType)candidate;

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( ex );
        }

    }

    /** Protected method to read/parse an XTCE XML document and populate the
     * internal data members for this object.
     *
     * @param dbFile File object containing the XTCE XML document to load.
     *
     * @param validateOnLoad boolean to indicate if the XML document should be
     * validated through the XML Schema Description (XSD) document that is
     * configured for the XTCEDatabase object.  In the future this will be more
     * flexible.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     * @param readOnly boolean indicating if the Document Object Model should
     * be constructed.  This is needed for round trip editing, but it is slower
     * than just the pure JAXB implementation.
     *
     * @throws XTCEDatabaseException thrown in the event that the document
     * cannot be read or is not sufficiently valid to complete construction of
     * the internal data model.
     *
     */

    protected SpaceSystemType loadDatabase( File    dbFile,
                                            boolean validateOnLoad,
                                            boolean applyXIncludes,
                                            boolean readOnly ) throws XTCEDatabaseException {

        String path = dbFile.getAbsolutePath();
        path = XTCEFunctions.getPathNameFromReferenceString( path );

        try {

            InputStream istream = new FileInputStream( dbFile );

            return loadDatabase( istream,
                                 path,
                                 validateOnLoad,
                                 applyXIncludes,
                                 readOnly );
            
        } catch ( FileNotFoundException ex ) {

            String exceptionMessage =
                XTCEFunctions.getText( "file_chooser_noload_text" ) + // NOI18N
                " " + // NOI18N
                dbFile.toString();
            throw new XTCEDatabaseException( exceptionMessage );

        }

    }

    /** Protected method to read/parse an XTCE XML document and populate the
     * internal data members for this object.
     *
     * @param dbLocation URL containing a location to load the document from.
     *
     * @param validateOnLoad boolean to indicate if the XML document should be
     * validated through the XML Schema Description (XSD) document that is
     * configured for the XTCEDatabase object.  In the future this will be more
     * flexible.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     * @param readOnly boolean indicating if the Document Object Model should
     * be constructed.  This is needed for round trip editing, but it is slower
     * than just the pure JAXB implementation.
     *
     * @throws XTCEDatabaseException thrown in the event that the document
     * cannot be read or is not sufficiently valid to complete construction of
     * the internal data model.
     *
     */

    protected SpaceSystemType loadDatabase( URL     dbLocation,
                                            boolean validateOnLoad,
                                            boolean applyXIncludes,
                                            boolean readOnly ) throws XTCEDatabaseException {

        try {

            InputStream istream = dbLocation.openStream();

            return loadDatabase( istream,
                                 null,
                                 validateOnLoad,
                                 applyXIncludes,
                                 readOnly );
            
        } catch ( Exception ex ) {

            String msg = XTCEFunctions.getText( "error_urlnotfound" ) + // NOI18N
                         ": " +
                         ex.getLocalizedMessage();
            throw new XTCEDatabaseException( msg );

        }

    }

    /** Protected method to read/parse an XTCE XML document and populate the
     * internal data members for this object.
     *
     * @param dbStream InputStream object containing the XTCE XML document to
     * load.
     *
     * @param path String containing the current working directory for the
     * stream, which is needed for XInclude, or null if not possible to get.
     *
     * @param validateOnLoad boolean to indicate if the XML document should be
     * validated through the XML Schema Description (XSD) document that is
     * configured for the XTCEDatabase object.  In the future this will be more
     * flexible.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     * @param readOnly boolean indicating if the Document Object Model should
     * be constructed.  This is needed for round trip editing, but it is slower
     * than just the pure JAXB implementation.
     *
     * @throws XTCEDatabaseException thrown in the event that the document
     * cannot be read or is not sufficiently valid to complete construction of
     * the internal data model.
     *
     */

    protected SpaceSystemType loadDatabase( InputStream dbStream,
                                            String      path,
                                            boolean     validateOnLoad,
                                            boolean     applyXIncludes,
                                            boolean     readOnly ) throws XTCEDatabaseException {

        try {
            jaxbContext_ =
                JAXBContext.newInstance( XTCEConstants.XTCE_PACKAGE );
        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( ex );
        }

        if ( readOnly == true ) {
            return loadReadOnlyDatabase( dbStream,
                                         path,
                                         validateOnLoad,
                                         applyXIncludes );
        } else {
            return loadEditableDatabase( dbStream,
                                         path,
                                         validateOnLoad,
                                         applyXIncludes );
        }

    }

    private SpaceSystemType loadReadOnlyDatabase( InputStream dbStream,
                                                  String      path,
                                                  boolean     validateOnLoad,
                                                  boolean     applyXIncludes ) throws XTCEDatabaseException {

        String currentDir = System.getProperty( "user.dir" ); // NOI18N

        if ( path != null ) {
            System.setProperty( "user.dir", path ); // NOI18N
        }

        XTCESchemaErrorHandler handler = new XTCESchemaErrorHandler();

        try {

            Unmarshaller     um  = jaxbContext_.createUnmarshaller();
            SAXParserFactory spf = SAXParserFactory.newInstance();

            spf.setXIncludeAware( applyXIncludes );
	    spf.setNamespaceAware( true );
            spf.setValidating( validateOnLoad );

            SAXParser parser = spf.newSAXParser();
            parser.setProperty( "http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                                "http://www.w3.org/2001/XMLSchema" );

            XMLReader reader = parser.getXMLReader();
            reader.setErrorHandler( handler );
            um.setEventHandler( handler );

            SAXSource source =
                new SAXSource( reader, new InputSource( dbStream ) );

            jaxbElementRoot_ = (JAXBElement)um.unmarshal( source );

            errorCount_ = handler.getErrorCount();
            warnings_   = handler.getMessages();
            
            Object candidate = jaxbElementRoot_.getValue();

            if ( candidate.getClass().equals( SpaceSystemType.class ) == false ) {
                String exceptionMessage =
                    XTCEFunctions.getText( "error_invalidxmlfile" ); // NOI18N
                throw new XTCEDatabaseException( exceptionMessage );
            }

            domLoaded_       = false;
            domDocumentRoot_ = null;
            domBinder_       = null;

            return (SpaceSystemType)candidate;

        } catch ( UnmarshalException ex ) {
            throw new XTCEDatabaseException( handler.getMessages() );
        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( ex ); 
        } finally {
            System.setProperty( "user.dir", currentDir ); // NOI18N
        }

    }

    private SpaceSystemType loadEditableDatabase( InputStream dbStream,
                                                  String      path,
                                                  boolean     validateOnLoad,
                                                  boolean     applyXIncludes ) throws XTCEDatabaseException {

        String currentDir = System.getProperty( "user.dir" ); // NOI18N

        if ( path != null ) {
            System.setProperty( "user.dir", path ); // NOI18N
        }

        XTCESchemaErrorHandler handler = new XTCESchemaErrorHandler();

        try {

            domBinder_ = jaxbContext_.createBinder();
            domBinder_.setEventHandler( handler );

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            dbf.setXIncludeAware( applyXIncludes );
            dbf.setNamespaceAware( true );
            dbf.setValidating( false );

            DocumentBuilder db = dbf.newDocumentBuilder();
            db.setErrorHandler( handler );

            domDocumentRoot_ = db.parse( dbStream );

            jaxbElementRoot_ =
                (JAXBElement)domBinder_.unmarshal( domDocumentRoot_ );

            errorCount_ = handler.getErrorCount();
            warnings_   = handler.getMessages();

            Object candidate = jaxbElementRoot_.getValue();

            if ( candidate.getClass().equals( SpaceSystemType.class ) == false ) {
                String exceptionMessage =
                    XTCEFunctions.getText( "error_invalidxmlfile" ); // NOI18N
                throw new XTCEDatabaseException( exceptionMessage );
            }
            
            domLoaded_ = true;

            return (SpaceSystemType)candidate;

        } catch ( UnmarshalException ex ) {
            throw new XTCEDatabaseException( handler.getMessages() );
        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( ex );    
        } finally {
            System.setProperty( "user.dir", currentDir ); // NOI18N
        }

    }

    protected void saveDatabase( File dbFile ) throws XTCEDatabaseException {

        if ( isReadOnly() == true ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_isreadonly" ) ); // NOI18N
        }

        try {

            // thinking about this update, maybe it should be done when nodes
            // are edited to keep in sync for xpath

            domBinder_.updateXML( jaxbElementRoot_ );

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer        t  = tf.newTransformer();

            t.transform( new DOMSource( domDocumentRoot_ ),
                         new StreamResult( dbFile ) );

        } catch ( Exception ex ) {
            
            throw new XTCEDatabaseException( ex );
            
        }
        
    }

    protected Element getDocumentElement() throws XTCEDatabaseException {

        if ( isReadOnly() == true ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_isreadonly" ) ); // NOI18N
        }

        return domDocumentRoot_.getDocumentElement();

    }

    // Private Data Members

    private File         xtceFilename_    = null;
    private String       schemaLocation_  = XTCEConstants.DEFAULT_SCHEMA_FILE;
    private List<String> warnings_        = new ArrayList<>();
    private long         errorCount_      = 0;
    private boolean      domLoaded_       = true;
    private JAXBContext  jaxbContext_     = null;
    private JAXBElement  jaxbElementRoot_ = null;
    private Document     domDocumentRoot_ = null;
    private Binder<Node> domBinder_       = null;

}
