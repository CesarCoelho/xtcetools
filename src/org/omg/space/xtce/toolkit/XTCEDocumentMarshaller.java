/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

//import com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper;
//import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import java.io.StringWriter;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
//import javax.xml.namespace.NamespaceContext;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NamedNodeMap;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

/**
 *
 * @author David Overeem
 *
 */

public class XTCEDocumentMarshaller {

    XTCEDocumentMarshaller( final Class className, final boolean fragment ) throws XTCEDatabaseException {

        try {

            jaxbContext = JAXBContext.newInstance( className );
            mmm = jaxbContext.createMarshaller();
            mmm.setProperty( Marshaller.JAXB_FRAGMENT, fragment );
            mmm.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
            //NamespacePrefixMapper prefixMapper = new XTCEViewerNamespacePrefixMapper();
            //mmm.setProperty( "com.sun.xml.internal.bind.namespacePrefixMapper", prefixMapper );
            XMLOutputFactory xof = XMLOutputFactory.newInstance();
            stream = new StringWriter();
            writer = xof.createXMLStreamWriter( stream );
            writer.setDefaultNamespace( XTCEConstants.XTCE_NAMESPACE );
            //writer.setPrefix( "", XTCEConstants.XTCE_NAMESPACE );
            //writer.setPrefix( "xtce", XTCEConstants.XTCE_NAMESPACE );

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( "Unable to create JAXB XML Context: " + ex.getCause() );
        }

    }

    public String marshalToXml( JAXBElement xmlElement ) throws XTCEDatabaseException {

        try {
            mmm.marshal( xmlElement, stream );
            //DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            //domFactory.setNamespaceAware(true); // never forget this!
            //DocumentBuilder builder = domFactory.newDocumentBuilder();
            //Document doc = builder.parse( stream.toString() );
            //Element element = doc.getDocumentElement();
            return stream.toString();
        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( "Failed to create XML string from element" );
        }

    }

    private JAXBContext     jaxbContext = null;
    private Marshaller      mmm         = null;
    private XMLStreamWriter writer      = null;
    private StringWriter    stream      = null;

    /*
    public class XTCEViewerNamespacePrefixMapper extends NamespacePrefixMapper implements NamespaceContext {

        private final String[] EMPTY_STRING = new String[0];

        private HashMap<String, String> prefixToUri = null;
        private HashMap<String, String> uriToPrefix = null;

        private void init(){
            prefixToUri = new HashMap<String, String>();
            prefixToUri.put( XMLConstants.DEFAULT_NS_PREFIX, XTCEConstants.XTCE_NAMESPACE );
            prefixToUri.put( "xtce", XTCEConstants.XTCE_NAMESPACE );
            prefixToUri.put( "xi", "http://www.w3.org/2001/XInclude" );
            prefixToUri.put( "xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI );
            prefixToUri.put( XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI  );
            prefixToUri.put( XMLConstants.XMLNS_ATTRIBUTE , XMLConstants.XMLNS_ATTRIBUTE_NS_URI );
            uriToPrefix = new HashMap<String, String>();
            for ( String prefix : prefixToUri.keySet() ) {
                uriToPrefix.put( prefixToUri.get( prefix ), prefix );
            }
        }

        @Override
        public String getPreferredPrefix( String namespaceUri, String suggestion, boolean requirePrefix ) {
            if ( uriToPrefix == null ) {
                init();
            }

            System.out.println( "Get Preferred '" + namespaceUri + "' '" + suggestion + "' " + requirePrefix );
            return "";

            if ( uriToPrefix.containsKey( namespaceUri ) == true ) {
                return uriToPrefix.get( namespaceUri );
            }

            return suggestion;

        }

        @Override
        public String[] getContextualNamespaceDecls() {
            // TODO Auto-generated method stub
            System.out.println( "Get Contextual " + EMPTY_STRING );
            return EMPTY_STRING;
        }

        @Override
        public String[] getPreDeclaredNamespaceUris() {
            // TODO Auto-generated method stub
            System.out.println( "Get PreDeclared " + EMPTY_STRING );
            return EMPTY_STRING;
        }

        @Override
        public String[] getPreDeclaredNamespaceUris2() {
            System.out.println( "Get 2 " + prefixToUri.get( "" ) );
            return EMPTY_STRING;
            //return new String [] { "", prefixToUri.get( "" ) };
        }

        public String getNamespaceURI( String prefix ) {

            if (prefixToUri == null) {
                init();
            }

            System.out.println( "Get NS URI '" + prefix + "'" );
            return null;

            if (prefixToUri.containsKey( prefix ) ) {
                return prefixToUri.get( prefix );
            } else {
                return XMLConstants.NULL_NS_URI;
            }

        }

        public String getPrefix( String namespaceURI ) {

            if ( uriToPrefix == null ) {
                init();
            }

            System.out.println( "Get Prefix '" + namespaceURI + "'" );
            return "";

            if ( uriToPrefix.containsKey( namespaceURI ) ){
                return uriToPrefix.get( namespaceURI );
            } else {
                return null;
            }

        }

        public Iterator getPrefixes( String namespaceURI ) {

            if (uriToPrefix == null) {
                init();
            }

            System.out.println( "Get Prefixes '" + namespaceURI + "'" );

            List prefixes = new LinkedList();

            if (uriToPrefix.containsKey(namespaceURI)){
                prefixes.add(uriToPrefix.get(namespaceURI));
            }

            //return prefixes.iterator();
            return null;

        }

    }
*/
}
    
