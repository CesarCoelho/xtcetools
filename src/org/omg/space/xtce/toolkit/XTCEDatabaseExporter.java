/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

/** The XTCEDatabaseExporter class is an abstract base class for providing an
 * interface to implement specific exporter classes.
 *
 */

public abstract class XTCEDatabaseExporter {

    /** Constructor
     *
     * @param db XTCEDatabase object to export from.
     * 
     * @param properties Properties object containing potential modifiers for
     * use by this exporter.  See additional information about this class
     * regarding which modifiers are applicable.
     * 
     * @throws XTCEDatabaseException in the event that the database or the
     * properties objects are not usable (null in this case).
     *
     */

    XTCEDatabaseExporter( XTCEDatabase db, Properties properties ) throws XTCEDatabaseException {
        if ( db == null ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "dialog_export_nulldb_message" ) );
        }
        if ( properties == null ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "dialog_export_nullproperties_message" ) );
        }
        db_         = db;
        properties_ = properties;
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
     * @return ArrayList containing 0 or more error/warning messages.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be opened/written/etc.
     *
     */

    public ArrayList<String> exportParameters( File outFile ) throws XTCEDatabaseException {
        ArrayList<String> msg = new ArrayList<String>();
        msg.add( XTCEFunctions.getText( "dialog_export_notyetimplemented_text" ) );
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
     * @return ArrayList containing 0 or more error/warning messages.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be opened/written/etc.
     *
     */

    public ArrayList<String> exportContainers( File outFile ) throws XTCEDatabaseException {
        ArrayList<String> msg = new ArrayList<String>();
        msg.add( XTCEFunctions.getText( "dialog_export_notyetimplemented_text" ) );
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
     * @return ArrayList containing 0 or more error/warning messages.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be opened/written/etc.
     *
     */

    public ArrayList<String> exportTelecommands( File outFile ) throws XTCEDatabaseException {
        ArrayList<String> msg = new ArrayList<String>();
        msg.add( XTCEFunctions.getText( "dialog_export_notyetimplemented_text" ) );
        return msg;
    }

    /// The instance of the XTCE Database object that contains the full data
    /// model.

    protected XTCEDatabase db_         = null;

    /// A list of properties to affect the behavior of the export.

    protected Properties   properties_ = null;

}
