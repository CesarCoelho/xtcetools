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
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** Class to implement the capability to convert/upgrade an XTCE document from
 * the 1.1 version to the 1.2 version.
 *
 * This conversion is still under construction.  The XTCE 1.2 schema is only
 * a proposal at this stage and is expected to get some additional work before
 * ultimate release/voting in September of 2017.  As a result, this logic
 * should not be considered "ready".  It is only a first cut to get some of
 * the work out of the way.
 *
 * @author dovereem
 *
 */

public class XTCEDatabaseConverter extends XTCEDatabaseParser {

    /** Constructor
     *
     * Create and initialize an instance of the XTCE Database Converter for
     * schema 1.1 to 1.2.  The provided file is loaded and if successful, the
     * user can begin the conversion with the upgrade() method, or select
     * specific step methods.
     *
     * @param file File containing the XTCE database XML file to load and use
     * for the conversion operation.
     *
     * @param useXInclude boolean indicating if XInclude processing should be
     * enabled during load.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be suitably loaded for conversion.
     *
     */

    public XTCEDatabaseConverter( final File file, final boolean useXInclude )
        throws XTCEDatabaseException {

        messages_ = new ArrayList<>();

        setFilename( file );

        loadDatabase( getFilename(),
                      false, // do not validate on load
                      useXInclude,
                      false ); // editable - loads the Document Object Model

    }

    /** Retrieve the messages that were generated, if any, during the file
     * conversion/upgrade activity.
     *
     * @return List of String objects containing the messages.  This return
     * will never be null, but it can be an empty list.
     *
     */

    public List<String> getMessages() {
        return messages_;
    }

    /** Perform all of the conversion/upgrade steps except saving the result.
     * 
     * @return long containing the number of elements/attributes that were
     * modified during the conversion operation.  This is not particularly
     * useful to know, but might be of interest to user display messages.
     *
     */

    public long upgrade() {

        long numberConverted = 0;

        numberConverted += convertTwosComplement();
        numberConverted += convertEnumerationAlarmValue();
        numberConverted += convertMessageContainRef();
        numberConverted += convertEmptyUnitSets();
        numberConverted += convertValidRangeCalibratedAttribute();
        numberConverted += convertErrorDetectCRC();
        numberConverted += convertAlarmFormToRangeForm();
        numberConverted += convertBlockMetaCommandStepArguments();
        numberConverted += convertTimeOfDayEpoch();

        messages_.add( Long.toString( numberConverted ) +
                       " " + // NOI18N
                       XTCEFunctions.getText( "file_upgrade_count" ) ); // NOI18N

        return numberConverted;

    }

    /** Convert the twosComplement value change.
     *
     * Changing the value of an attribute
     *
     * @return long containing the number of encoding attributes that were
     * changed on IntegerDataEncoding elements.
     *
     */

    public long convertTwosComplement() {

        long numberConverted = 0;

        try {

            NodeList nodes =
                evaluateXPathQuery( "//xtce:IntegerDataEncoding[@encoding = 'twosCompliment']/@encoding" ); // NOI18N

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {
                nodes.item( iii ).setNodeValue( "twosComplement" ); // NOI18N
                ++numberConverted;
            }

        } catch ( Exception ex ) {
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Convert the EnumerationAlarm attribute name.
     *
     * Changing the name of an attribute
     *
     * @return long containing the number of enumerationValue attributes that
     * were changed to enumerationLabel.
     *
     */

    public long convertEnumerationAlarmValue() {

        long numberConverted = 0;

        try {

            NodeList nodes =
                evaluateXPathQuery( "//xtce:EnumerationAlarmList/xtce:EnumerationAlarm/@enumerationValue" ); // NOI18N

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {
                getDocument().renameNode( nodes.item( iii ),
                                          nodes.item( iii ).getNamespaceURI(),
                                          "enumerationLabel" ); // NOI18N
                ++numberConverted;
            }

        } catch ( Exception ex ) {
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Convert the MessageSet ContainerRef element to ContainerRef.
     *
     * Changing the name of an element
     *
     * @return long containing the number of ContainRef elements that were
     * renamed to ContainerRef in the MessageSet.
     *
     */

    public long convertMessageContainRef() {

        long numberConverted = 0;

        try {

            NodeList nodes =
                evaluateXPathQuery( "//xtce:TelemetryMetaData/xtce:MessageSet/xtce:Message/xtce:ContainRef" ); // NOI18N

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {
                renameElement( (Element)nodes.item( iii ), "ContainerRef" ); // NOI18N
                ++numberConverted;
            }

        } catch ( Exception ex ) {
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Convert the empty UnitSet elements by removing them to reduce size.
     *
     * Removing an element
     *
     * @return long containing the number of UnitSet elements that were
     * removed.
     *
     */

    public long convertEmptyUnitSets() {

        long numberConverted = 0;

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
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Convert the validRangeAppliesToCalibrated attribute by relocating it.
     *
     * Move an attribute from one element to another
     *
     * @return long containing the number of ContainRef elements that were
     * renamed to ContainerRef in the MessageSet.
     *
     */

    public long convertValidRangeCalibratedAttribute() {

        long numberConverted = 0;

        try {

            String[] queries = {
                "//xtce:ParameterTypeSet/*/@validRangeAppliesToCalibrated", // NOI18N
                "//xtce:ArgumentTypeSet/*/@validRangeAppliesToCalibrated" // NOI18N
            };

            for ( String query : queries ) {

                NodeList nodes = evaluateXPathQuery( query );

                for ( int iii = 0; iii < nodes.getLength(); ++iii ) {

                    Element  parent   = ((Attr)nodes.item( iii )).getOwnerElement();
                    String   value    = nodes.item( iii ).getNodeValue();
                    NodeList children = parent.getChildNodes();

                    for ( int jjj = 0; jjj < children.getLength(); ++jjj ) {
                        Node node = children.item( jjj );
                        if ( ( node.getNodeType()                          == Node.ELEMENT_NODE ) &&
                             ( node.getNodeName().endsWith( "ValidRange" ) == true              ) ) { // NOI18N
                            ((Element)node).setAttribute( "validRangeAppliesToCalibrated", value ); // NOI18N
                        }
                    }

                    parent.removeAttribute( "validRangeAppliesToCalibrated" );

                    ++numberConverted;

                }

            }

        } catch ( Exception ex ) {
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Convert the ErrorDetectCorrect CRC Polynomial element.
     *
     * Removing the contents of an element
     *
     * This conversion requires user interaction in that there is not enough
     * information in XTCE 1.1 to completely define the CRC algorithm
     * uniqueness to make the XTCE 1.2 representation complete.
     * 
     * I think I have enough information to perform an auto-convert entirely
     * if the polynomial resembles either CRC-CCITT, CRC-16-ANSI, and
     * CRC-32 POSIX/ISO 3309, but I need to make an assumption that someone is
     * not using an odd reflect/remainder/init/XOR setting(s).  Not sure if I
     * should do this or not.
     *
     * @return long containing the number of CRC Polynomial elements that
     * had their contents removed.
     *
     */

    public long convertErrorDetectCRC() {

        long numberConverted = 0;

        String query = "//xtce:ErrorDetectCorrect/xtce:CRC/xtce:Polynomial"; // NOI18N

        try {

            NodeList nodes = evaluateXPathQuery( query );

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {

                if ( nodes.item( iii ).hasChildNodes() == false ) {
                    continue;
                }

                NodeList children = nodes.item( iii ).getChildNodes();

                for ( int jjj = 0; jjj < children.getLength(); ++jjj ) {

                    if ( children.item( jjj ).getNodeType() != Node.ELEMENT_NODE ) {
                        continue;
                    }

                    Node typeNode = children.item( jjj )
                                            .getParentNode()
                                            .getParentNode()
                                            .getParentNode()
                                            .getParentNode()
                                            .getParentNode();

                    String typeName =
                        ((Element)typeNode).getAttribute( "name" ); // NOI18N

                    messages_.add( typeName +
                                   ": " + // NOI18N
                                   XTCEFunctions.getText( "file_upgrade_notice_crc" ) ); // NOI18N

                    nodes.item( iii ).setTextContent( "" ); // NOI18N

                    ++numberConverted;

                    break;

                }

            }

        } catch ( Exception ex ) {
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Convert the alarmForm attribute name.
     *
     * Changing the name of an attribute
     *
     * This only applies to a very small number of users who incorporated the
     * original XTCE 1.2 proposal with extensions provided by this toolkit.
     * Pure XTCE 1.1 users would not have any cases of this conversion.
     *
     * @return long containing the number of alarmForm attributes that
     * were changed to rangeForm.
     *
     */

    public long convertAlarmFormToRangeForm() {

        long numberConverted = 0;

        try {

            NodeList nodes =
                evaluateXPathQuery( "//xtce:StaticAlarmRanges/@alarmForm" ); // NOI18N

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {
                getDocument().renameNode( nodes.item( iii ),
                                          nodes.item( iii ).getNamespaceURI(),
                                          "rangeForm" ); // NOI18N
                ++numberConverted;
            }

        } catch ( Exception ex ) {
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Convert the ArgumentList to ArgumentAssignmentList in the
     * BlockMetaCommand elements.
     *
     * CHanging the name of an element and also the child elements and
     * attributes.
     *
     * @return long containing the number of ArgumentList elements that
     * were changed to ArgumentAssignmentList elements with modified child
     * elements and attributes.
     *
     */

    public long convertBlockMetaCommandStepArguments() {

        long numberConverted = 0;

        try {

            NodeList nodes =
                evaluateXPathQuery( "//xtce:CommandMetaData" + // NOI18N
                                    "/xtce:MetaCommandSet" + // NOI18N
                                    "/xtce:BlockMetaCommand" + // NOI18N
                                    "/xtce:MetaCommandStepList" + // NOI18N
                                    "/xtce:MetaCommandStep" + // NOI18N
                                    "/xtce:ArgumentList" ); // NOI18N

            for ( int iii = 0; iii < nodes.getLength(); ++iii ) {
                renameElement( (Element)nodes.item( iii ), "ArgumentAssignmentList" ); // NOI18N
                ++numberConverted;
                NodeList children = nodes.item( iii ).getChildNodes();
                for ( int jjj = 0; jjj < children.getLength(); ++jjj ) {
                    Node node = children.item( jjj );
                    if ( ( node.getNodeType()                        == Node.ELEMENT_NODE ) &&
                         ( node.getNodeName().endsWith( "Argument" ) == true              ) ) { // NOI18N
                        renameElement( (Element)node, "ArgumentAssignment" ); // NOI18N
                        NamedNodeMap attrs = node.getAttributes();
                        Node nameAttr = attrs.getNamedItem( "name" ); // NOI18N
                        getDocument().renameNode( nameAttr,
                                                  nameAttr.getNamespaceURI(),
                                                  "argumentName" ); // NOI18N
                        Node valueAttr = attrs.getNamedItem( "value" ); // NOI18N
                        getDocument().renameNode( valueAttr,
                                                  valueAttr.getNamespaceURI(),
                                                  "argumentValue" ); // NOI18N
                    }
                }
            }

        } catch ( Exception ex ) {
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Convert the timeOfDay attribute value to the Epoch text content.
     *
     * Removing an attribute and relocating data
     *
     * This only applies to a very small number of users who incorporated the
     * original XTCE 1.2 proposal with extensions provided by this toolkit.
     * Pure XTCE 1.1 users would not have any cases of this conversion.
     *
     * @return long containing the number of timeOfDay attributes that
     * were changed to Epoch element content.
     *
     */

    public long convertTimeOfDayEpoch() {

        long numberConverted = 0;

        try {

            String[] queries = {
                "//xtce:ParameterTypeSet/*/xtce:ReferenceTime/xtce:Epoch/@timeOfDay", // NOI18N
                "//xtce:ArgumentTypeSet/*/xtce:ReferenceTime/xtce:Epoch/@timeOfDay" // NOI18N
            };

            for ( String query : queries ) {

                NodeList nodes = evaluateXPathQuery( query );

                for ( int iii = 0; iii < nodes.getLength(); ++iii ) {

                    Element  parent     = ((Attr)nodes.item( iii )).getOwnerElement();
                    String   timeString = nodes.item( iii ).getNodeValue();
                    String   dateString = parent.getTextContent();

                    if ( dateString.matches( "^[0-9]{4}-[0-9]{2}-[0-9]{2}" ) == true ) { // NOI18N

                        dateString = dateString.substring( 0, 10 ) +
                                     "T" + // NOI18N
                                     timeString;
                        if ( dateString.matches( "[0-9]$" ) == true ) {
                            dateString += "Z"; // NOI18N
                        }
                        parent.setTextContent( dateString );
                        parent.removeAttribute( "timeOfDay" ); // NOI18N
                        ++numberConverted;

                    } else {

                        messages_.add( ( (Element)parent.getParentNode()
                                                        .getParentNode() )
                                                        .getAttribute( "name" ) +
                                       ": " +
                                       XTCEFunctions.getText( "file_upgrade_notice_epoch" ) );

                    }

                }

            }

        } catch ( Exception ex ) {
            messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                           " " + // NOI18N
                           ex.getLocalizedMessage() );
        }

        //System.out.println( "count = " + Long.toString( numberConverted ) );
        return numberConverted;

    }

    /** Conclude the conversion/upgrade by saving the XML content to an
     * automatically generated filename.
     *
     * The file will be overwritten if it already exists.  The old file name
     * will be used to create the new file name.  First it will try to replace
     * the ".xml" extension with "-1.2.xml".  If that does not succeed, then
     * the file name will receive "-1.2.xml" at the end to make the new file
     * name.
     *
     * @return boolean indicating if the save operation was successful.
     *
     */

    public boolean save() {

        String oldFile = getFilename().getAbsolutePath();
        String newFile = oldFile.replaceFirst( "\\.xml$", "-1.2.xml" ); // NOI18N

        if ( oldFile.equals( newFile ) == true ) {
            newFile = oldFile + "-1.2.xml"; // NOI18N
        }

        return save( new File( newFile ) );

    }

    /** Conclude the conversion/upgrade by saving the XML content to a provided
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

    // Private Data Members

    private final List<String> messages_;

}
