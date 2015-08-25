/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
     * @throws XTCEDatabaseException in the event that the database or the
     * properties objects are not usable (null in this case).
     *
     */

    public XTCEDatabaseExporterCsv( XTCEDatabase db, Properties properties ) throws XTCEDatabaseException {

        super( db, properties );
        headerFields_ = new ArrayList<String>();
        headerFields_.add( "SpaceSystem" );
        headerFields_.add( "Name" );
        headerFields_.add( "EngType" );
        headerFields_.add( "Unit" );
        headerFields_.add( "Size" );
        headerFields_.add( "EncodingType" );
        headerFields_.add( "BitOrder" );
        headerFields_.add( "Source" );
        headerFields_.add( "ReadOnly" );
        headerFields_.add( "DefaultValue" );
        headerFields_.add( "ChangeThreshold" );
        headerFields_.add( "Description" );

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
    public List<String> exportParameters( File outFile ) throws XTCEDatabaseException {

        try {

            FileOutputStream stream = new FileOutputStream( outFile );
            List<XTCESpaceSystem> spaceSystems = db_.getSpaceSystemTree();
            if ( properties_.getProperty( "use_header_row" ).equals( "true" ) == true ) {
                for ( int iii = 0; iii < headerFields_.size(); ++iii ) {
                    stream.write( headerFields_.get( iii ).getBytes() );
                    if ( iii == ( headerFields_.size() - 1) ) {
                        stream.write( ',' );
                    }
                }
                stream.write( '\n' );
            }
            for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
                List<XTCEParameter> parameters = spaceSystem.getParameters();
                for ( XTCEParameter parameter : parameters ) {
                    stream.write( spaceSystem.getFullPath().getBytes() );
                    stream.write( ',' );
                    stream.write( parameter.getName().getBytes() );
                    stream.write( ',' );
                    stream.write( parameter.getEngineeringType().getBytes() );
                    stream.write( ',' );
                    stream.write( parameter.getUnits().getBytes() );
                    stream.write( ',' );
                    stream.write( parameter.getRawSizeInBits().getBytes() );
                    stream.write( ',' );
                    stream.write( parameter.getRawType().getBytes() );
                    stream.write( ',' );
                    stream.write( parameter.getRawBitOrder().getBytes() );
                    stream.write( ',' );
                    stream.write( parameter.getDataSource().getBytes() );
                    stream.write( ',' );
                    stream.write( ( parameter.isSettable() == true ? "true" : "false" ).getBytes() );
                    stream.write( ',' );
                    stream.write( parameter.getInitialValue().getBytes() );
                    stream.write( ',' );
                    stream.write( parameter.getChangeThreshold().getBytes() );
                    stream.write( ',' );
                    stream.write( '"' );
                    stream.write( parameter.getDescription().getBytes() );
                    stream.write( '"' );
                    stream.write( '\n' );
                }
            }
            stream.close();

        } catch ( FileNotFoundException ex ) {
            throw new XTCEDatabaseException( "Unable to open file for writing: " + outFile.getAbsolutePath() );
        } catch ( IOException ex ) {
            throw new XTCEDatabaseException( "Unable to write to file: " + outFile.getAbsolutePath() );
        } catch ( NullPointerException ex ) {
            ex.printStackTrace();
        }

        return new ArrayList<String>();

    }

    /// Header row fields specific to the CSV output and intialized in the
    /// constructor of this class.

    private ArrayList<String> headerFields_ = null;

}
