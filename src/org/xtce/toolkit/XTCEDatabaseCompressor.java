/* Copyright 2017 David Overeem (dovereem@cox.net)
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Class to implement the capability to compress the XML in an XTCE document
 * considering the schema features to reduce document size.
 *
 * This compression is still under construction.
 *
 * Options that may be used in this class are provided in the constructor using
 * the Properties argument.  All keys are expected to have a boolean value.
 * Any missing keys are assumed to be false/unselected.  The list of keys is:
 *
 * BASIC_DEFAULTS
 * IDENTITY_CALIBRATORS
 * UNNEEDED_LISTS
 * ENUM_2_BOOL
 * RAW_VALID_RANGES
 * ADVANCED_DEFAULTS
 * DUPLICATE_TYPES
 * START_BITS
 * XTCE_PREFIX
 *
 * @author dovereem
 *
 */

public class XTCEDatabaseCompressor extends XTCEDatabaseParser {

    /** Constructor
     *
     * Create and initialize an instance of the XTCE Database Compressor for
     * XML document compression.  The provided file is loaded and if
     * successful, the user can begin the conversion with the compress()
     * method, or select specific step methods.
     *
     * @param file File containing the XTCE database XML file to load and use
     * for the conversion operation.
     *
     * @param options Properties containing the compression options.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be suitably loaded for conversion.
     *
     */

    public XTCEDatabaseCompressor( final File file, final Properties options )
        throws XTCEDatabaseException {

        messages_ = new ArrayList<>();
        options_  = options;

        setFilename( file );

        loadDatabase( getFilename(),
                      false, // do not validate on load
                      getOption( "XINCLUDE" ), // NOI18N
                      false ); // editable - loads the Document Object Model

    }

    /** Retrieve the messages that were generated, if any, during the file
     * compression activity.
     *
     * @return List of String objects containing the messages.  This return
     * will never be null, but it can be an empty list.
     *
     */

    public List<String> getMessages() {
        return messages_;
    }

    /** Perform all of the compression steps except saving the result.
     * 
     * @return long containing the number of elements/attributes that were
     * modified during the compression operation.  This is not particularly
     * useful to know, but might be of interest to user display messages.
     *
     */

    public long compress() {

        long numberConverted = 0;

        numberConverted += compressEncodingAttributes();
        numberConverted += compressCalibrationTestAttributes();
        numberConverted += compressParameterPropertiesAttributes();
        numberConverted += compressListsAndSets();
        numberConverted += compressIdentityCalibrators();
        numberConverted += compressSimpleEnumerations();
        numberConverted += compressValidRangeTruisms();
        numberConverted += compressDuplicateTypes();
        numberConverted += compressContainerStartBits();
        //numberConverted += compressEmptyUnitSets();
        numberConverted += compressAdditionalAttributes();

        messages_.add( Long.toString( numberConverted ) +
                       " " + // NOI18N
                       XTCEFunctions.getText( "file_upgrade_count" ) ); // NOI18N

        return numberConverted;

    }

    /** Compress Raw Data Encoding attributes.
     *
     * bitOrder="mostSignificantBitFirst"
     * abstract="false"
     *
     * @return long containing the number of encoding attributes that were
     * changed on DataEncoding elements.
     *
     */

    public long compressEncodingAttributes() {

        long numberConverted = 0;

        if ( getOption( "BASIC_DEFAULTS" ) == false ) { // NOI18N
            return numberConverted;
        }

        try {

            String[] queries = { "//*[@bitOrder = 'mostSignificantBitFirst']/@bitOrder", // NOI18N
                                 "//*[@abstract = 'false']/@abstract" }; // NOI18N

            for ( String query : queries ) {

                NodeList nodes = evaluateXPathQuery( query );

                for ( int iii = 0; iii < nodes.getLength(); ++iii ) {

                    Attr    attrNode = (Attr)( nodes.item( iii ) );
                    Element element  = attrNode.getOwnerElement();

                    element.removeAttributeNode( attrNode );
                    ++numberConverted;

                }

            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "enc count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Compress ParameterProperties attributes and remove the element if there
     * is no additional content.
     *
     * dataSource="telemetered"
     * readOnly="false"
     *
     * @return long containing the number of ParameterProperties elements that
     * were modified or removed entirely.
     *
     */

    public long compressParameterPropertiesAttributes() {

        long numberConverted = 0;

        if ( getOption( "BASIC_DEFAULTS" ) == false ) { // NOI18N
            return numberConverted;
        }

        try {

            NodeList nodes =
                evaluateXPathQuery( "//xtce:ParameterProperties" ); // NOI18N

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {

                Element element  = (Element)( nodes.item( iii ) );
                boolean modified = false;

                if ( removeDefaultAttribute( element, "dataSource", "telemetered" ) ) { // NOI18N
                    modified = true;
                }

                if ( removeDefaultAttribute( element, "readOnly", "false" ) ) { // NOI18N
                    modified = true;
                }

                if ( element.getChildNodes().getLength() == 0 ) {
                    element.getParentNode().removeChild( element );
                    modified = true;
                }

                if ( modified == true ) {
                    ++numberConverted;
                }

            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "pprop count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Compress calibration test attributes used in several elements.
     *
     * useCalibratedValue="true"
     * comparisonOperator="=="
     * instance="0"
     *
     * @return long containing the number of removed attributes.
     *
     */

    public long compressCalibrationTestAttributes() {

        long numberConverted = 0;

        if ( getOption( "BASIC_DEFAULTS" ) == false ) { // NOI18N
            return numberConverted;
        }

        try {

            String[] queries = { "//*[@useCalibratedValue = 'true']/@useCalibratedValue", // NOI18N
                                 "//*[@comparisonOperator = '==']/@comparisonOperator", // NOI18N
                                 "//*[@instance = '0']/@instance" }; // NOI18N

            for ( String query : queries ) {

                NodeList nodes = evaluateXPathQuery( query );

                for ( int iii = 0; iii < nodes.getLength(); ++iii ) {
                    Attr    attrNode = (Attr)( nodes.item( iii ) );
                    Element parent   = attrNode.getOwnerElement();
                    parent.removeAttributeNode( attrNode );
                    ++numberConverted;
                }

            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "caltest count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Remove List and Set element variants that are unnecessary because there
     * is only 1 child element and that element is valid outside the grouping.
     *
     * @return long containing the number of List or Set elements removed.
     *
     */

    public long compressListsAndSets() {

        long numberConverted = 0;

        if ( getOption( "UNNEEDED_LISTS" ) == false ) { // NOI18N
            return numberConverted;
        }

        NodeList nodes;

        try {

            nodes = evaluateXPathQuery( "//xtce:ComparisonList" ); // NOI18N

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {

                Node     compare  = null;
                int      count    = 0;
                NodeList children = nodes.item( iii ).getChildNodes();

                for ( int jjj = 0; jjj < children.getLength(); ++jjj ) {

                    if ( children.item( jjj ).getNodeType() == Node.ELEMENT_NODE ) {
                        if ( compare == null ) {
                            compare = children.item( jjj );
                        }
                        ++count;
                    }

                }

                if ( count == 1 ) {
                    nodes.item( iii )
                         .getParentNode()
                         .replaceChild( compare, nodes.item( iii ) );
                    ++numberConverted;
                }

            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "list count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Remove identity calibrators that have no effect on the calibrated
     * value.
     *
     * This does not remove identity calibrators that are in the context
     * matching feature because those may be used to undo another context.
     *
     * @return long containing the number of calibrator elements removed.
     *
     */

    public long compressIdentityCalibrators() {

        long numberConverted = 0;

        if ( getOption( "IDENTITY_CALIBRATORS" ) == false ) { // NOI18N
            return numberConverted;
        }

        NodeList nodes;

        try {

            nodes = evaluateXPathQuery( "//xtce:DefaultCalibrator/xtce:PolynomialCalibrator" ); // NOI18N

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {

                double   coeff0   = 0.0;
                double   coeff1   = 1.0;
                boolean  highterm = false;
                NodeList children = nodes.item( iii ).getChildNodes();

                for ( int jjj = 0; jjj < children.getLength(); ++jjj ) {

                    if ( children.item( jjj ).getNodeType() != Node.ELEMENT_NODE ) {
                        continue;
                    }

                    Element termElement = (Element)( children.item( jjj ) );

                    if ( termElement.getAttribute( "exponent" ).equals( "0" ) == true ) { // NOI18N
                        coeff0 = Double.parseDouble( termElement.getAttribute( "coefficient" ) ); // NOI18N
                    } else if ( termElement.getAttribute( "exponent" ).equals( "1" ) == true ) { // NOI18N
                        coeff1 = Double.parseDouble( termElement.getAttribute( "coefficient" ) ); // NOI18N
                    } else {
                        highterm = true;
                        break;
                    }

                }

                if ( ( coeff0 == 0.0 ) && ( coeff1 == 1.0 ) && ( highterm == false ) ) {

                    Node defCalNode   = nodes.item( iii ).getParentNode();
                    Node encodingNode = defCalNode.getParentNode();

                    encodingNode.removeChild( defCalNode );
                    ++numberConverted;

                }

            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "poly count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Compress simple enumeration types to boolean types.
     *
     * @return long containing the number of calibrator elements removed.
     *
     */

    public long compressSimpleEnumerations() {

        long numberConverted = 0;

        if ( getOption( "ENUM_2_BOOL" ) == false ) { // NOI18N
            return numberConverted;
        }

        NodeList nodes;

        try {

            nodes = evaluateXPathQuery( "//xtce:EnumerationList" ); // NOI18N

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {

                String   zeroString = ""; // NOI18N
                String   oneString  = ""; // NOI18N
                boolean  isSimple   = true;
                NodeList children   = nodes.item( iii ).getChildNodes();

                for ( int jjj = 0; jjj < children.getLength(); ++jjj ) {

                    if ( children.item( jjj ).getNodeType() != Node.ELEMENT_NODE ) {
                        continue;
                    }

                    NamedNodeMap attrs = children.item( jjj ).getAttributes();

                    String num = attrs.getNamedItem( "value" ).getNodeValue(); // NOI18N

                    if ( Double.parseDouble( num ) == 0.0 ) {
                        zeroString = attrs.getNamedItem( "label" ).getNodeValue(); // NOI18N
                    } else if ( Double.parseDouble( num ) == 1.0 ) {
                        oneString = attrs.getNamedItem( "label" ).getNodeValue(); // NOI18N
                    } else {
                        isSimple = false;
                    }

                }

                if ( ( isSimple             == false ) ||
                     ( zeroString.isEmpty() == true  ) ||
                     ( oneString.isEmpty()  == true  ) ) {

                    continue;

                }

                Element typeElement =
                    (Element)( nodes.item( iii ).getParentNode() );

                String elementName = typeElement.getNodeName();

                if ( elementName.contains( "ParameterType" ) == true ) { // NOI18N
                    renameElement( typeElement, "BooleanParameterType" ); // NOI18N
                } else if ( elementName.contains( "ArgumentType" ) == true ) { // NOI18N
                    renameElement( typeElement, "BooleanArgumentType" ); // NOI18N
                } else {
                    continue;
                }

                if ( zeroString.equals( "False" ) == false ) { // NOI18N
                    typeElement.setAttribute( "zeroStringValue", zeroString ); // NOI18N
                }

                if ( oneString.equals( "True" ) == false ) { // NOI18N
                    typeElement.setAttribute( "oneStringValue", oneString ); // NOI18N
                }

                typeElement.removeChild( nodes.item( iii ) );

                ++numberConverted;

            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "enum count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Compress ValidRange specifications that are nothing more than truisms
     * when taking into account the size of the encoding.
     *
     * @return long containing the number ValidRange elements modified or
     * removed.
     *
     */

    public long compressValidRangeTruisms() {

        long numberConverted = 0;

        if ( getOption( "RAW_VALID_RANGES" ) == false ) { // NOI18N
            return numberConverted;
        }

        try {

            messages_.add( XTCEFunctions.getText( "general_warning" ) + // NOI18N
                           ": compressValidRangeTruisms() " + // NOI18N
                           XTCEFunctions.getText( "general_not_implemented" ) ); // NOI18N

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "range count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Compress Type elements for Parameter and Argument that can be
     * considered duplicate when refactoring is possible.
     *
     * @return long containing the number Types elements removed.
     *
     */

    public long compressDuplicateTypes() {

        long numberConverted = 0;

        if ( getOption( "DUPLICATE_TYPES" ) == false ) { // NOI18N
            return numberConverted;
        }

        try {

            messages_.add( XTCEFunctions.getText( "general_warning" ) + // NOI18N
                           ": compressDuplicateTypes() " + // NOI18N
                           XTCEFunctions.getText( "general_not_implemented" ) ); // NOI18N

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "types count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Compress location elements in containers where the start bit provided
     * is the same as it would have been using the default of 0 past end of
     * previous item.
     *
     * @return long containing the number location elements removed.
     *
     */

    public long compressContainerStartBits() {

        long numberConverted = 0;

        if ( getOption( "START_BITS" ) == false ) { // NOI18N
            return numberConverted;
        }

        try {

            messages_.add( XTCEFunctions.getText( "general_warning" ) + // NOI18N
                           ": compressContainerStartBits() " + // NOI18N
                           XTCEFunctions.getText( "general_not_implemented" ) ); // NOI18N

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "bits count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Compress additional attributes that have default values.
     *
     * IntegerDataEncoding/@sizeInBits="8"
     * IntegerDataEncoding/@encoding="unsigned"
     * FloatDataEncoding/@sizeInBits="32"
     * FloatDataEncoding/@encoding="IEEE754_1985"
     * StringDataEncoding/@encoding="UTF-8"
     * IntegerParameterType/@signed="true"
     * IntegerParameterType/@sizeInBits="32"
     * FloatParameterType/@sizeInBits="32"
     * BooleanParameterType/@zeroStringValue="False"
     * BooleanParameterType/@oneStringValue="True"
     *
     * @return long containing the number of removed attributes.
     *
     */

    public long compressAdditionalAttributes() {

        long numberConverted = 0;

        if ( getOption( "ADVANCED_DEFAULTS" ) == false ) { // NOI18N
            return numberConverted;
        }

        try {

            String[] queries = { "//xtce:IntegerDataEncoding[@sizeInBits = '8']/@sizeInBits", // NOI18N
                                 "//xtce:IntegerDataEncoding[@encoding = 'unsigned']/@encoding", // NOI18N
                                 "//xtce:FloatDataEncoding[@sizeInBits = '32']/@sizeInBits", // NOI18N
                                 "//xtce:FloatDataEncoding[@encoding = 'IEEE754_1985']/@encoding", // NOI18N
                                 "//xtce:StringDataEncoding[@encoding = 'UTF-8']/@encoding", // NOI18N
                                 "//xtce:IntegerParameterType[@signed = 'true']/@signed", // NOI18N
                                 "//xtce:IntegerParameterType[@sizeInBits = '32']/@sizeInBits", // NOI18N
                                 "//xtce:FloatParameterType[@sizeInBits = '32']/@sizeInBits", // NOI18N
                                 "//xtce:BooleanParameterType[@zeroStringValue = 'False']/@zeroStringValue", // NOI18N
                                 "//xtce:BooleanParameterType[@oneStringValue = 'True']/@oneStringValue" }; // NOI18N

            for ( String query : queries ) {

                NodeList nodes = evaluateXPathQuery( query );

                for ( int iii = 0; iii < nodes.getLength(); ++iii ) {
                    Attr    attrNode = (Attr)( nodes.item( iii ) );
                    Element parent   = attrNode.getOwnerElement();
                    parent.removeAttributeNode( attrNode );
                    ++numberConverted;
                }

            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "addldef count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Compress the empty UnitSet elements by removing them to reduce size.
     *
     * This method would only be applicable after migration to XTCE 1.2, so for
     * now it is not active here, but instead on the conversion class.
     *
     * @return long containing the number of UnitSet elements that were
     * removed.
     *
     */

    public long compressEmptyUnitSets() {

        long numberConverted = 0;

        if ( getOption( "BASIC_DEFAULTS" ) == false ) {
            return numberConverted;
        }

        try {

            NodeList nodes =
                evaluateXPathQuery( "//xtce:UnitSet" ); // NOI18N

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {
                if ( nodes.item( iii ).hasChildNodes() == false ) {
                    Node parent = nodes.item( iii ).getParentNode();
                    parent.removeChild( nodes.item( iii ) );
                    // dead text node no longer needs to be removed because
                    // of the normalize method called before saving
                    ++numberConverted;
                }
            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "unit count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Conclude the compression by saving the XML content to an
     * automatically generated filename.
     *
     * The file will be overwritten if it already exists.  The old file name
     * will be used to create the new file name.  First it will try to replace
     * the ".xml" extension with "-NEW.xml".  If that does not succeed, then
     * the file name will receive "-NEW.xml" at the end to make the new file
     * name.
     *
     * @return boolean indicating if the save operation was successful.
     *
     */

    public boolean save() {

        String oldFile = getFilename().getAbsolutePath();
        String newFile = oldFile.replaceFirst( "\\.xml$", "-NEW.xml" ); // NOI18N

        if ( oldFile.equals( newFile ) == true ) {
            newFile = oldFile + "-NEW.xml"; // NOI18N
        }

        return save( new File( newFile ) );

    }

    /** Conclude the compression by saving the XML content to a provided
     * file.
     *
     * The file will be overwritten if it already exists.
     *
     * @param xmlFile File containing the path and name to the target filename
     * to save.
     *
     * @return boolean indicating if the save operation was successful.
     *
     */

    public boolean save( final File xmlFile ) {

        try {

            if ( messages_.isEmpty() == false ) {
                updateHistorySet( messages_ );
            }

            saveDatabase( xmlFile );

        } catch ( Exception ex ) {

            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );

            return false;

        }

        setFilename( xmlFile );

        return true;

    }

    /** Retrieve an option from the Properties list of choices.
     *
     * The option may or may not be set by the user.  All options are false by
     * default.
     *
     * @param name String containing the name of the option.
     *
     * @return boolean indicating if this option is selected.
     *
     */

    private boolean getOption( final String name ) {

        try {

            if ( options_.get( name ) == null ) {
                return false;
            } else {
                return (boolean)( options_.get( name ) );
            }

        } catch ( Exception ex ) {
            // do nothing and assume the default return of false
        }

        return false;

    }

    // Private Data Members

    private final List<String> messages_;
    private final Properties   options_;

}
