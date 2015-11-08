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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/** The XTCEDatabaseExporter class is an abstract base class for providing an
 * interface to implement specific exporter classes.
 *
 */

public abstract class XTCEDatabaseExporter {

    /** Constructor
     *
     * The exporter object needs to be constructed with a list of properties.
     * These properties inform how the data is output.  The list of property
     * keys and their default values is:
     *
     * @li use_header_row = "true".  This property indicates if the column
     * headers should be included in the output.
     *
     * @li show_all_alias_namespaces = "true".  This property indicates if all
     * of the Aliases in the AliasSet should be output (space delimited)
     *
     * @li show_alias_namespaces = "true".  This property indicates if the
     * alias namespace should be shown in front of the alias, e.g NS::ALIAS.
     *
     * @li preferred_alias_namespace = "".  This property restricts the
     * alias output to a specific namespace.  This is ignored if the property
     * show_all_alias_namespaces is "true".
     *
     * @li show_all_conditions = "false".  This property restricts the
     * container conditional depth to stop at the first non-included container
     * when a container is conditionally included in another container.
     *
     * @param db XTCEDatabase object to export from.
     * 
     * @param properties Properties object containing potential modifiers for
     * use by this exporter.  The object may be null, in which case all default
     * properties are used.
     * 
     * @throws XTCEDatabaseException in the event that the database object is
     * null.
     *
     */

    XTCEDatabaseExporter( XTCEDatabase db, Properties properties )
        throws XTCEDatabaseException {

        if ( db == null ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "dialog_export_nulldb_message" ) ); // NOI18N
        }

        db_ = db;

        if ( properties == null ) {
            properties_ = new Properties();
        } else {
            properties_ = properties;
        }

        if ( properties_.getProperty( "use_header_row" ) == null ) { // NOI18N
            properties_.setProperty( "use_header_row", "true" ); // NOI18N
        }

        if ( properties_.getProperty( "show_all_alias_namespaces" ) == null ) { // NOI18N
            properties_.setProperty( "show_all_alias_namespaces", "true" ); // NOI18N
        }

        if ( properties_.getProperty( "show_alias_namespaces" ) == null ) { // NOI18N
            properties_.setProperty( "show_alias_namespaces", "true" ); // NOI18N
        }

        if ( properties_.getProperty( "preferred_alias_namespace" ) == null ) { // NOI18N
            properties_.setProperty( "preferred_alias_namespace", "" ); // NOI18N
        }

        if ( properties_.getProperty( "show_all_conditions" ) == null ) { // NOI18N
            properties_.setProperty( "show_all_conditions", "false" ); // NOI18N
        }

    }

    /** Export the parameters in the XTCE file represented by an XTCEDatabase
     * object.
     *
     * This class needs to be implemented for each specific exporter.  The
     * exporter should throw on critical filesystem errors, but any export
     * warnings/errors should be returned in the ArrayList of String so that
     * the caller can evaluate these independent of filesystem errors.
     * 
     * The XTCEDatabase object is provided to the constructor of this class,
     * along with a Properties object that contains modifier information.
     *
     * @param outFile File object to export to the data to.
     *
     * @return List containing 0 or more error/warning messages.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be opened/written/etc.
     *
     */

    public List<String> exportParameters( File outFile )
        throws XTCEDatabaseException {

        ArrayList<String> msg = new ArrayList<>();
        msg.add( XTCEFunctions.getText( "dialog_export_notyetimplemented_text" ) ); // NOI18N
        return msg;

    }

    /** Export the telemetry containers in the XTCE file represented by an
     * XTCEDatabase object.
     *
     * This class needs to be implemented for each specific exporter.  The
     * exporter should throw on critical filesystem errors, but any export
     * warnings/errors should be returned in the ArrayList of String so that
     * the caller can evaluate these independent of filesystem errors.
     * 
     * The XTCEDatabase object is provided to the constructor of this class,
     * along with a Properties object that contains modifier information.
     *
     * @param outFile File object to export to the data to.
     *
     * @return List containing 0 or more error/warning messages.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be opened/written/etc.
     *
     */

    public List<String> exportContainers( File outFile )
        throws XTCEDatabaseException {

        ArrayList<String> msg = new ArrayList<>();
        msg.add( XTCEFunctions.getText( "dialog_export_notyetimplemented_text" ) ); // NOI18N
        return msg;

    }

    /** Export the telecommands in the XTCE file represented by an XTCEDatabase
     * object.
     *
     * This class needs to be implemented for each specific exporter.  The
     * exporter should throw on critical filesystem errors, but any export
     * warnings/errors should be returned in the ArrayList of String so that
     * the caller can evaluate these independent of filesystem errors.
     * 
     * The XTCEDatabase object is provided to the constructor of this class,
     * along with a Properties object that contains modifier information.
     *
     * @param outFile File object to export to the data to.
     *
     * @return List containing 0 or more error/warning messages.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be opened/written/etc.
     *
     */

    public List<String> exportTelecommands( File outFile )
        throws XTCEDatabaseException {

        ArrayList<String> msg = new ArrayList<>();
        msg.add( XTCEFunctions.getText( "dialog_export_notyetimplemented_text" ) ); // NOI18N
        return msg;

    }

    /// The instance of the XTCE Database object that contains the full data
    /// model.

    protected final XTCEDatabase db_;

    /// A list of properties to affect the behavior of the export.

    protected final Properties properties_;

}
