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

package org.xtce.toolkit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.omg.space.xtce.DescriptionType.AncillaryDataSet.AncillaryData;
import org.xtce.toolkit.XTCEContainerContentEntry.FieldType;

/** The XTCEDatabaseExporterCsv class is a specific implementation of the
 * XTCEDatabaseExporter abstract base class for the purpose of exporting TM
 * parameters to a CSV file.
 *
 */

public class XTCEDatabaseExporterCsv extends XTCEDatabaseExporter {

    /** Constructor
     *
     * @param db XTCEDatabase object to export from.
     * 
     * @param properties Properties object containing potential modifiers for
     * use by this exporter.  See additional information about this class
     * regarding which modifiers are applicable.
     *
     * @param charEncoding Charset to encode the output text as.
     *
     * @throws XTCEDatabaseException in the event that the database or the
     * properties objects are not usable (null in this case).
     *
     */

    public XTCEDatabaseExporterCsv( final XTCEDatabase db,
                                    final Properties   properties,
                                    final Charset      charEncoding )
        throws XTCEDatabaseException {

        super( db, properties, charEncoding );

    }

    /** Export the parameters in the XTCE file represented by an XTCEDatabase
     * object.
     * 
     * The XTCEDatabase object is provided to the constructor of this class,
     * along with a Properties object that contains modifier information.
     *
     * @param outFile File object to export to the data to.
     *
     * @return List containing 0 or more error/warning messages.
     * For this CSV implementation, no messages are produced.  In the event
     * that there are filesystem errors, those will be thrown by exception.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be opened/written/etc.
     *
     */

    @Override
    public List<String> exportParameters( File outFile )
        throws XTCEDatabaseException {

        try ( OutputStream       stream = new FileOutputStream( outFile );
              OutputStreamWriter writer = new OutputStreamWriter( stream, charEncoding_ ) ) {

            List<XTCESpaceSystem> spaceSystems = db_.getSpaceSystemTree();

            if ( properties_.getProperty( "use_header_row" ).equals( "true" ) == true ) { // NOI18N
                writeHeaderFields( getParametersHeaderFields(), writer );
            }

            for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
                List<XTCEParameter> parameters = spaceSystem.getParameters();
                for ( XTCEParameter parameter : parameters ) {

                    String aliasText =
                        XTCEFunctions.makeAliasDisplayString( parameter,
                                                              properties_.getProperty( "show_all_alias_namespaces" ).equals( "true" ), // NOI18N
                                                              properties_.getProperty( "show_alias_namespaces" ).equals( "true" ), // NOI18N
                                                              properties_.getProperty( "preferred_alias_namespace" ) ); // NOI18N

                    XTCEValidRange rangeObj = parameter.getValidRange();
                    String lowValue    = ""; // NOI18N
                    String highValue   = ""; // NOI18N
                    String lowIncFlag  = ""; // NOI18N
                    String highIncFlag = ""; // NOI18N

                    if ( rangeObj.isValidRangeApplied() == true ) {
                        if ( ( rangeObj.getLowValue()           != null  ) &&
                             ( rangeObj.getLowValue().isEmpty() == false ) ) {
                            lowValue   = rangeObj.getLowValue();
                            lowIncFlag = ( rangeObj.isLowValueInclusive() == true ? "inc" : "exc" ); // NOI18N
                            if ( rangeObj.isLowValueCalibrated() == false ) {
                                XTCEItemValue valueObj =
                                    new XTCEItemValue( parameter );
                                lowValue =
                                    valueObj.getCalibratedFromUncalibrated( lowValue );
                            }
                        }
                        if ( ( rangeObj.getHighValue()           != null ) &&
                             ( rangeObj.getHighValue().isEmpty() == false ) ) {
                            highValue   = rangeObj.getHighValue();
                            highIncFlag = ( rangeObj.isHighValueInclusive() == true ? "inc" : "exc" ); // NOI18N
                            if ( rangeObj.isHighValueCalibrated() == false ) {
                                XTCEItemValue valueObj =
                                    new XTCEItemValue( parameter );
                                highValue =
                                    valueObj.getCalibratedFromUncalibrated( highValue );
                            }
                        }
                    }

                    writer.write( spaceSystem.getFullPath() );
                    writer.write( ',' ); // NOI18N
                    writer.write( parameter.getName() );
                    writer.write( ',' ); // NOI18N
                    writer.write( aliasText );
                    writer.write( ',' ); // NOI18N
                    writer.write( parameter.getEngineeringTypeString() );
                    writer.write( ',' ); // NOI18N
                    writer.write( parameter.getUnits() );
                    writer.write( ',' ); // NOI18N
                    writer.write( parameter.getRawSizeInBits() );
                    writer.write( ',' ); // NOI18N
                    writer.write( parameter.getRawTypeString() );
                    writer.write( ',' ); // NOI18N
                    writer.write( parameter.getRawBitOrder() );
                    writer.write( ',' ); // NOI18N
                    writer.write( parameter.getDataSource() );
                    writer.write( ',' ); // NOI18N
                    writer.write( ( parameter.isSettable() == true ? "true" : "false" ) ); // NOI18N
                    writer.write( ',' ); // NOI18N
                    writer.write( parameter.getInitialValue() );
                    writer.write( ',' ); // NOI18N
                    writer.write( parameter.getChangeThreshold() );
                    writer.write( ',' ); // NOI18N
                    writer.write( getSpecialData( parameter ) );
                    writer.write( ',' ); // NOI18N
                    writer.write( lowValue );
                    writer.write( ',' ); // NOI18N
                    writer.write( lowIncFlag );
                    writer.write( ',' ); // NOI18N
                    writer.write( highValue );
                    writer.write( ',' ); // NOI18N
                    writer.write( highIncFlag );
                    writer.write( ',' ); // NOI18N
                    writer.write( '"' ); // NOI18N
                    writer.write( parameter.getDescription() );
                    writer.write( '"' ); // NOI18N
                    writer.write( System.getProperty( "line.separator" ) ); // NOI18N

                }
            }

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "dialog_export_error_writing" ) + // NOI18N
                                             " (" + outFile.getAbsolutePath() + // NOI18N
                                             "): " + ex.getLocalizedMessage() ); // NOI18N
        }

        return new ArrayList<>();

    }

    /** Export the containers in the XTCE file represented by an XTCEDatabase
     * object.
     * 
     * The XTCEDatabase object is provided to the constructor of this class,
     * along with a Properties object that contains modifier information.
     *
     * @param outFile File object to export to the data to.
     *
     * @return List containing 0 or more error/warning messages.
     * For this CSV implementation, no messages are produced.  In the event
     * that there are filesystem errors, those will be thrown by exception.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be opened/written/etc.
     *
     */

    @Override
    public List<String> exportContainers( File outFile )
        throws XTCEDatabaseException {

        List<String> warnings = new ArrayList<>();

        try ( OutputStream       stream = new FileOutputStream( outFile );
              OutputStreamWriter writer = new OutputStreamWriter( stream, charEncoding_ ) ) {

            if ( properties_.getProperty( "use_header_row" ).equals( "true" ) == true ) { // NOI18N
                writeHeaderFields( getContainersHeaderFields(), writer );
            }

            List<XTCETMContainer> containers = db_.getContainers();

            for ( XTCETMContainer container : containers ) {

                XTCEContainerContentModel model =
                    db_.processContainer( container,
                                          null,
                                          properties_.getProperty( "show_all_conditions" ).equals( "true" ) ); // NOI18N

                warnings.addAll( model.getWarnings() );

                List<XTCEContainerContentEntry> entries =
                    model.getContentList();

                for ( XTCEContainerContentEntry entry : entries ) {

                    String aliasString = ""; // NOI18N
                    String name        = ""; // NOI18N
                    if ( entry.getEntryType() == FieldType.PARAMETER ) {
                        aliasString =
                            XTCEFunctions.makeAliasDisplayString( entry.getParameter(),
                                                                  properties_.getProperty( "show_all_alias_namespaces" ).equals( "true" ), // NOI18N
                                                                  properties_.getProperty( "show_alias_namespaces" ).equals( "true" ), // NOI18N
                                                                  properties_.getProperty( "preferred_alias_namespace" ) ); // NOI18N
                        name = entry.getParameter().getName();
                    }

                    String containerName = "UNDEFINED"; // NOI18N
                    String description   = ""; // NOI18N

                    if ( entry.getEntryType() == FieldType.PARAMETER ) {
                        containerName = entry.getHoldingContainer().getName();
                        description   = entry.getParameter().getDescription();
                    } else if ( entry.getEntryType() == FieldType.CONTAINER ) {
                        containerName = entry.getTelemetryContainer().getName();
                        description   = entry.getTelemetryContainer().getDescription();
                    }

                    String rValue = ""; // NOI18N
                    if ( entry.getValue() != null ) {
                        rValue = entry.getValue().toStringWithoutParameter();
                    }

                    writer.write( container.getName() );
                    writer.write( ',' ); // NOI18N
                    writer.write( containerName );
                    writer.write( ',' ); // NOI18N
                    writer.write( name );
                    writer.write( ',' ); // NOI18N
                    writer.write( aliasString );
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getRawSizeInBits() );
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getStartBit() );
                    writer.write( ',' ); // NOI18N
                    writer.write( rValue );
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getInitialValue() );
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getConditions() );
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getRepeatParameterInfo() );
                    writer.write( ',' ); // NOI18N
                    writer.write( '"' ); // NOI18N
                    writer.write( description );
                    writer.write( '"' ); // NOI18N
                    writer.write( System.getProperty( "line.separator" ) ); // NOI18N

                }

            }

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "dialog_export_error_writing" ) + // NOI18N
                                             " (" + outFile.getAbsolutePath() + // NOI18N
                                             "): " + ex.getLocalizedMessage() ); // NOI18N
        }

        return warnings;

    }

    /** Export the telecommands in the XTCE file represented by an XTCEDatabase
     * object.
     * 
     * The XTCEDatabase object is provided to the constructor of this class,
     * along with a Properties object that contains modifier information.
     *
     * @param outFile File object to export to the data to.
     *
     * @return List containing 0 or more error/warning messages.
     * For this CSV implementation, no messages are produced.  In the event
     * that there are filesystem errors, those will be thrown by exception.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be opened/written/etc.
     *
     */

    @Override
    public List<String> exportTelecommands( File outFile )
        throws XTCEDatabaseException {

        List<String> warnings = new ArrayList<>();

        try ( OutputStream       stream = new FileOutputStream( outFile );
              OutputStreamWriter writer = new OutputStreamWriter( stream, charEncoding_ ) ) {

            if ( properties_.getProperty( "use_header_row" ).equals( "true" ) == true ) { // NOI18N
                writeHeaderFields( getTelecommandsHeaderFields(), writer );
            }

            List<XTCETelecommand> telecommands = db_.getTelecommands();

            for ( XTCETelecommand tc : telecommands ) {

                XTCETelecommandContentModel model =
                    db_.processTelecommand( tc,
                                            null,
                                            properties_.getProperty( "show_all_conditions" ).equals( "true" ) ); // NOI18N

                warnings.addAll( model.getWarnings() );

                List<XTCEContainerContentEntry> entries =
                    model.getContentList();

                for ( XTCEContainerContentEntry entry : entries ) {

                    String aliasString = ""; // NOI18N
                    String name        = ""; // NOI18N
                    if ( entry.getEntryType() == FieldType.PARAMETER ) {
                        aliasString =
                            XTCEFunctions.makeAliasDisplayString( entry.getParameter(),
                                                                  properties_.getProperty( "show_all_alias_namespaces" ).equals( "true" ), // NOI18N
                                                                  properties_.getProperty( "show_alias_namespaces" ).equals( "true" ), // NOI18N
                                                                  properties_.getProperty( "preferred_alias_namespace" ) ); // NOI18N
                        name = entry.getParameter().getName();
                    }

                    String containerName = "UNDEFINED"; // NOI18N
                    String description   = ""; // NOI18N
                    String engType       = ""; // NOI18N
                    String units         = ""; // NOI18N
                    String encoding      = ""; // NOI18N
                    String bitOrder      = ""; // NOI18N

                    if ( entry.getEntryType() == FieldType.PARAMETER ) {
                        containerName = entry.getTelecommand().getName();
                        description   = entry.getParameter().getDescription();
                        engType       = entry.getParameter().getEngineeringType().toString();
                        units         = entry.getParameter().getUnits();
                        encoding      = entry.getParameter().getRawTypeString();
                        bitOrder      = entry.getParameter().getRawBitOrder();
                    } else if ( entry.getEntryType() == FieldType.CONTAINER ) {
                        containerName = entry.getTelecommandContainer().getName();
                        description   = entry.getTelecommandContainer().getDescription();
                    } else if ( entry.getEntryType() == FieldType.ARGUMENT ) {
                        containerName = entry.getTelecommand().getName();
                        description   = entry.getArgument().getDescription();
                        engType       = entry.getArgument().getEngineeringType().toString();
                        units         = entry.getArgument().getUnits();
                        encoding      = entry.getArgument().getRawTypeString();
                        bitOrder      = entry.getArgument().getRawBitOrder();
                    }

                    String rValue = ""; // NOI18N
                    if ( entry.getValue() != null ) {
                        rValue = entry.getValue().toStringWithoutParameter();
                    }

                    writer.write( containerName );
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getEntryTypeString() );
                    writer.write( ',' ); // NOI18N
                    writer.write( name );
                    writer.write( ',' ); // NOI18N
                    writer.write( aliasString );
                    writer.write( ',' ); // NOI18N
                    writer.write( engType );
                    writer.write( ',' ); // NOI18N
                    writer.write( units ); // NOI18N
                    writer.write( ',' ); // NOI18N
                    writer.write( encoding ); // NOI18N
                    writer.write( ',' ); // NOI18N
                    writer.write( bitOrder ); // NOI18N
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getRawSizeInBits() );
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getStartBit() );
                    writer.write( ',' ); // NOI18N
                    writer.write( rValue );
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getInitialValue() );
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getConditions() );
                    writer.write( ',' ); // NOI18N
                    writer.write( entry.getRepeatParameterInfo() );
                    writer.write( ',' ); // NOI18N
                    writer.write( '"' ); // NOI18N
                    writer.write( description );
                    writer.write( '"' ); // NOI18N
                    writer.write( System.getProperty( "line.separator" ) ); // NOI18N

                }

            }

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "dialog_export_error_writing" ) + // NOI18N
                                             " (" + outFile.getAbsolutePath() + // NOI18N
                                             "): " + ex.getLocalizedMessage() ); // NOI18N
        }

        return warnings;

    }

    /** Private method to assemble the list of header fields for the parameter
     * export output.
     *
     * @return List of String objects containing the names of the columns.
     *
     */

    private List<String> getParametersHeaderFields() {

        List<String> headerFields = new ArrayList<>();

        headerFields.add( XTCEFunctions.getText( "ss_name_text" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_name_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_aliases_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_type_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_unit_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_size_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_encoding_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_bitorder_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_source_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_readonly_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_defaultvalue_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_changethreshold_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_id_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_lowval_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_lowinc_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_highval_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_highinc_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_desc_col_label" ) ); // NOI18N

        return headerFields;

    }

    /** Private method to assemble the list of header fields for the container
     * export output.
     *
     * @return List of String objects containing the names of the columns.
     *
     */

    private List<String> getContainersHeaderFields() {

        List<String> headerFields = new ArrayList<>();

        headerFields.add( XTCEFunctions.getText( "table_containers_contname_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_containers_contname_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_containers_paramname_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_containers_paramaliases_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_containers_size_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_containers_startbit_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_containers_value_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_containers_defaultvalue_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_containers_condition_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_containers_repeat_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_desc_col_label" ) ); // NOI18N

        return headerFields;

    }

    /** Private method to assemble the list of header fields for the
     * telecommand export output.
     *
     * @return List of String objects containing the names of the columns.
     *
     */

    private List<String> getTelecommandsHeaderFields() {

        List<String> headerFields = new ArrayList<>();

        headerFields.add( XTCEFunctions.getText( "table_telecommands_contname_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_telecommands_fieldtype_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_telecommands_itemname_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_containers_paramaliases_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_type_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_unit_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_encoding_col_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_parameters_bitorder_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_telecommands_size_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_telecommands_startbit_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_telecommands_value_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_telecommands_defaultvalue_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_telecommands_condition_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_telecommands_repeat_label" ) ); // NOI18N
        headerFields.add( XTCEFunctions.getText( "table_telecommands_desc_label" ) ); // NOI18N

        return headerFields;

    }

    private void writeHeaderFields( final List<String>       headerFields,
                                    final OutputStreamWriter writer )
        throws IOException {

        for ( int iii = 0; iii < headerFields.size(); ++iii ) {

            writer.write( headerFields.get( iii ) );

            if ( iii != ( headerFields.size() - 1 ) ) {
                writer.write( ',' ); // NOI18N
            }

        }

        writer.write( System.getProperty( "line.separator" ) ); // NOI18N

    }

    private String getSpecialData( final XTCEParameter parameter ) {

        List<AncillaryData> list =
            parameter.getAncillaryData( "PARAMID = *" ); // NOI18N

        for ( AncillaryData data : list ) {
            return data.getName().replaceFirst( "PARAMID = ", "" ); // NOI18N
        }

        return ""; // NOI18N

    }

}
