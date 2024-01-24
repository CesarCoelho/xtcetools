/* Copyright 2015 David Overeem (dovereem@startmail.com)
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

package org.xtce.toolkit.examples;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import org.xtce.toolkit.XTCEContainerContentEntry;
import org.xtce.toolkit.XTCEContainerContentEntry.FieldType;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEContainerEntryValue;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEFunctions;
import org.xtce.toolkit.XTCETMContainer;

/** The EncodeContainerExample application provides an API example to
 * process a container specification from an XTCE document and encode values
 * to the container, enabling the user to extract the binary contents of that
 * container.
 *
 * <P>The following is an example based on this demonstration class file.
 * There are no command line arguments for this example as it is
 * completely self encapsulated.  It uses the built-in demonstration
 * database "BogusSAT-2.xml".</p>
 *
 * <pre>
 * {@code
 * File dbFile =
 *     new File( "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml" );
 *
 * String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket1";
 *
 * List<XTCEContainerEntryValue> values = new ArrayList<>();
 *
 * // The sequence to be performed.  the example handles errors only on
 * // a global catch and is long because the purpose of this is to
 * // demonstrate interface usage and not a well-factored class.
 *
 * try {
 *
 *     // Load the database file provided in read-only with XInclude.
 *
 *     XTCEDatabase db = new XTCEDatabase( dbFile,
 *                                         false,  // XSD validate flag
 *                                         true,   // XInclude flag
 *                                         true ); // ReadOnly flag
 *
 *     // Retrieve the container object from the database using the full
 *     // path, which is the quickest for this demo, but findContainers()
 *     // in the API is much more flexible.
 *
 *     XTCETMContainer container = db.getContainer( containerName );
 *
 *     // Values can be set for the parameters inside the container
 *     // either before or after the container is processed.  If the
 *     // values are set after, then the processContainer has be to run
 *     // again if there are an include conditions that might have been
 *     // changed by the values that were set.  Knowledge on the part of
 *     // the user about the container is helpful here.  The graphical
 *     // interface can assist with making a visual of the container
 *     // layout.
 *
 *     values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "CCSDS_Packet_Sequence.GroupFlags" ).get( 0 ),
 *                                              "3",
 *                                              "==",
 *                                              "Calibrated" ) );
 *
 *     values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "CCSDS_Packet_Sequence.Count" ).get( 0 ),
 *                                              "3783",
 *                                              "==",
 *                                              "Calibrated" ) );
 *
 *     values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "CCSDS_Packet_Length" ).get( 0 ),
 *                                              "11",
 *                                              "==",
 *                                              "Calibrated" ) );
 *
 *     // There is no need to set the values for the header parameters
 *     // that have restricted values in the container definition, so the
 *     // following are skipped in this example:
 *     //
 *     // CCSDS_Packet_ID.Version = 0
 *     // CCSDS_Packet_ID.Type = TM
 *     // CCSDS_Packet_ID.SecHdrFlag = NotPresent
 *     // CCSDS_Packet_ID.APID = 1
 *
 *     values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "Battery_Voltage" ).get( 0 ),
 *                                              "12.3",
 *                                              "==",
 *                                              "Calibrated" ) );
 *
 *     values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "Battery_Current" ).get( 0 ),
 *                                              "0.5",
 *                                              "==",
 *                                              "Calibrated" ) );
 *
 *     values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "Battery_Charge_Mode" ).get( 0 ),
 *                                              "CHARGE",
 *                                              "==",
 *                                              "Calibrated" ) );
 *
 *     values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "SomeParameter" ).get( 0 ),
 *                                              "5",
 *                                              "==",
 *                                              "Calibrated" ) );
 *
 *     values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "Solar_Array_Voltage_1" ).get( 0 ),
 *                                              "23.0",
 *                                              "==",
 *                                              "Calibrated" ) );
 *
 *     values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "Solar_Array_Voltage_2" ).get( 0 ),
 *                                              "1230", // same as 23.0 calibrated
 *                                              "==",
 *                                              "Uncalibrated" ) );
 *
 *     // This uses the option to process the container when the values
 *     // are provided in advance.
 *
 *     XTCEContainerContentModel model = db.processContainer( container,
 *                                                            values,
 *                                                            false );
 *
 *     BitSet rawBits = model.encodeContainer();
 *
 *     long sizeInBits = model.getTotalSize();
 *
 *     // Just some code to print binary contents
 *
 *     StringBuilder procBitString = new StringBuilder();
 *
 *     for ( int iii = 0; iii < sizeInBits; ++iii ) {
 *         if ( rawBits.get( iii ) == true ) {
 *             procBitString.append( "1" );
 *         } else {
 *             procBitString.append( "0" );
 *         }
 *     }
 *
 *     System.out.println( "Encoded(1): " + procBitString.toString() );
 *     System.out.println( "Encoded(1): 0x" + new BigInteger( XTCEFunctions.getStreamByteArrayFromBitSet( rawBits, (int)sizeInBits / 8 ) ).toString( 16 ) );
 *
 *     // It is also possible to set the values after the processing and
 *     // re-encode the binary of the container.  Here two different
 *     // values are changed and the container encoded a second time.  In
 *     // this case, no include conditions are changed, so the container
 *     // does not require the processContainer() method to be used again.
 *
 *     for ( XTCEContainerContentEntry entry : model.getContentList() ) {
 *
 *         if ( entry.getEntryType() != FieldType.PARAMETER ) {
 *             continue;
 *         }
 *
 *         if ( entry.getParameter().getName().equals( "Battery_Voltage" ) == true ) {
 *
 *             XTCEContainerEntryValue newValue =
 *                 new XTCEContainerEntryValue( entry.getParameter(),
 *                                              "14.0",
 *                                              "==",
 *                                              "Calibrated" );
 *
 *             entry.setValue( newValue );
 *
 *         } else if ( entry.getParameter().getName().equals( "Solar_Array_Voltage_1" ) == true ) {
 *
 *             XTCEContainerEntryValue newValue =
 *                 new XTCEContainerEntryValue( entry.getParameter(),
 *                                              "5.7",
 *                                              "==",
 *                                              "Calibrated" );
 *
 *             entry.setValue( newValue );
 *
 *         }
 *
 *     } // End of Parameter for loop
 *
 *     BitSet newRawBits = model.encodeContainer();
 *
 *     // Just some code to print binary contents
 *
 *     StringBuilder newProcBitString = new StringBuilder();
 *
 *     for ( int iii = 0; iii < sizeInBits; ++iii ) {
 *         if ( newRawBits.get( iii ) == true ) {
 *             newProcBitString.append( "1" );
 *         } else {
 *             newProcBitString.append( "0" );
 *         }
 *     }
 *
 *     System.out.println( "Encoded(2): " + newProcBitString.toString() );
 *     System.out.println( "Encoded(2): 0x" + new BigInteger( XTCEFunctions.getStreamByteArrayFromBitSet( newRawBits, (int)sizeInBits / 8 ) ).toString( 16 ) );
 *
 * } catch ( Exception ex ) {
 *     System.err.println( ex.getClass().getName() );
 *     System.err.println( "Exception: " + ex.getLocalizedMessage() );
 *     System.exit( -1 );
 * }
 *
 * }
 * </pre>
 *
 * @author dovereem
 *
 */

public class EncodeContainerExample {

    /** The EncodeContainerExample application provides an API example to
     * process a container specification from an XTCE document and encode values
     * to the container, enabling the user to extract the binary contents of that
     * container.
     *
     * @param args String array of the command line arguments passed by the
     * Java VM.
     *
     * <p>There are no command line arguments for this example as it is
     * completely self encapsulated.  It uses the built-in demonstration
     * database "BogusSAT-2.xml".</p>
     *
     * <p>This example returns -1 on exception and 0 on success.  Errors and
     * warnings are to STDERR and processed output is to STDOUT.</p>
     *
     */

    public static void main( String[] args ) {

        File dbFile =
            new File( "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket1";

        List<XTCEContainerEntryValue> values = new ArrayList<>();

        // The sequence to be performed.  the example handles errors only on
        // a global catch and is long because the purpose of this is to
        // demonstrate interface usage and not a well-factored class.

        try {

            // Load the database file provided in read-only with XInclude.

            XTCEDatabase db = new XTCEDatabase( dbFile,
                                                false,  // XSD validate flag
                                                true,   // XInclude flag
                                                true ); // ReadOnly flag

            // Retrieve the container object from the database using the full
            // path, which is the quickest for this demo, but findContainers()
            // in the API is much more flexible.

            XTCETMContainer container = db.getContainer( containerName );

            // Values can be set for the parameters inside the container
            // either before or after the container is processed.  If the
            // values are set after, then the processContainer has be to run
            // again if there are an include conditions that might have been
            // changed by the values that were set.  Knowledge on the part of
            // the user about the container is helpful here.  The graphical
            // interface can assist with making a visual of the container
            // layout.

            values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "CCSDS_Packet_Sequence.GroupFlags" ).get( 0 ),
                                                     "3",
                                                     "==",
                                                     "Calibrated" ) );

            values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "CCSDS_Packet_Sequence.Count" ).get( 0 ),
                                                     "3783",
                                                     "==",
                                                     "Calibrated" ) );

            values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "CCSDS_Packet_Length" ).get( 0 ),
                                                     "11",
                                                     "==",
                                                     "Calibrated" ) );

            // There is no need to set the values for the header parameters
            // that have restricted values in the container definition, so the
            // following are skipped in this example:
            //
            // CCSDS_Packet_ID.Version = 0
            // CCSDS_Packet_ID.Type = TM
            // CCSDS_Packet_ID.SecHdrFlag = NotPresent
            // CCSDS_Packet_ID.APID = 1

            values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "Battery_Voltage" ).get( 0 ),
                                                     "12.3",
                                                     "==",
                                                     "Calibrated" ) );

            values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "Battery_Current" ).get( 0 ),
                                                     "0.5",
                                                     "==",
                                                     "Calibrated" ) );

            values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "Battery_Charge_Mode" ).get( 0 ),
                                                     "CHARGE",
                                                     "==",
                                                     "Calibrated" ) );

            values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "SomeParameter" ).get( 0 ),
                                                     "5",
                                                     "==",
                                                     "Calibrated" ) );

            values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "Solar_Array_Voltage_1" ).get( 0 ),
                                                     "23.0",
                                                     "==",
                                                     "Calibrated" ) );

            values.add( new XTCEContainerEntryValue( db.getTelemetryParameters( "Solar_Array_Voltage_2" ).get( 0 ),
                                                     "1230", // same as 23.0 calibrated
                                                     "==",
                                                     "Uncalibrated" ) );

            // This uses the option to process the container when the values
            // are provided in advance.

            XTCEContainerContentModel model = db.processContainer( container,
                                                                   values,
                                                                   false );

            BitSet rawBits = model.encodeContainer();

            long sizeInBits = model.getTotalSize();

            // Just some code to print binary contents

            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            System.out.println( "Encoded(1): " + procBitString.toString() );
            System.out.println( "Encoded(1): 0x" + new BigInteger( XTCEFunctions.getStreamByteArrayFromBitSet( rawBits, (int)sizeInBits / 8 ) ).toString( 16 ) );

            // It is also possible to set the values after the processing and
            // re-encode the binary of the container.  Here two different
            // values are changed and the container encoded a second time.  In
            // this case, no include conditions are changed, so the container
            // does not require the processContainer() method to be used again.

            for ( XTCEContainerContentEntry entry : model.getContentList() ) {

                if ( entry.getEntryType() != FieldType.PARAMETER ) {
                    continue;
                }

                if ( entry.getParameter().getName().equals( "Battery_Voltage" ) == true ) {

                    XTCEContainerEntryValue newValue =
                        new XTCEContainerEntryValue( entry.getParameter(),
                                                     "14.0",
                                                     "==",
                                                     "Calibrated" );

                    entry.setValue( newValue );

                } else if ( entry.getParameter().getName().equals( "Solar_Array_Voltage_1" ) == true ) {

                    XTCEContainerEntryValue newValue =
                        new XTCEContainerEntryValue( entry.getParameter(),
                                                     "5.7",
                                                     "==",
                                                     "Calibrated" );

                    entry.setValue( newValue );

                }

            } // End of Parameter for loop

            BitSet newRawBits = model.encodeContainer();

            // Just some code to print binary contents

            StringBuilder newProcBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( newRawBits.get( iii ) == true ) {
                    newProcBitString.append( "1" );
                } else {
                    newProcBitString.append( "0" );
                }
            }

            System.out.println( "Encoded(2): " + newProcBitString.toString() );
            System.out.println( "Encoded(2): 0x" + new BigInteger( XTCEFunctions.getStreamByteArrayFromBitSet( newRawBits, (int)sizeInBits / 8 ) ).toString( 16 ) );

        } catch ( Exception ex ) {
            System.err.println( ex.getClass().getName() );
            System.err.println( "Exception: " + ex.getLocalizedMessage() );
            System.exit( -1 );
        }

    } // End of the example main()

}
