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

package org.xtce.toolkit.examples;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.xtce.toolkit.XTCEContainerContentEntry;
import org.xtce.toolkit.XTCEContainerContentEntry.FieldType;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEFunctions;
import org.xtce.toolkit.XTCETMContainer;

/** The DecodeContainerExample application provides an API example to
 * process/decode a binary file that contains a container whose binary
 * contents are described in an XTCE satellite database.
 *
 * <P>The following is an example based on this demonstration class file.  The
 * class main() requires command line arguments to point to the database file
 * instead of the "myfile.xml" used in the example code below.</P>
 *
 * <pre>
 * {@code
 * // The sequence to be performed.  the example handles errors only on
 * // a global catch and is long because the purpose of this is to
 * // demonstrate interface usage and not a well-factored class.
 *
 * try {
 *
 *     // Load the database file provided in read-only with XInclude.
 *
 *     XTCEDatabase db = new XTCEDatabase( new File( "myfile.xml" ),
 *                                         false,  // XSD validate flag
 *                                         true,   // XInclude flag
 *                                         true ); // ReadOnly flag
 *
 *     // Retrieve the container object from the database using the full
 *     // path, which is the quickest for this demo, but findContainers()
 *     // in the API is much more flexible.
 *
 *     XTCETMContainer container = db.getContainer( "/MySAT/TM/Packet1" );
 *
 *     // Get a stream object for the provided bunary file contents.
 *
 *     InputStream fStream = new FileInputStream( args[2] );
 *
 *     // Decode/process the container in the stream using the container
 *     // returned from db.getContainer() above.
 *
 *     XTCEContainerContentModel model = db.processContainer( container,
 *                                                            fStream );
 *
 *     // Loop through the entries in the container content model
 *     // returned from the db.processContainer() above.
 *
 *     for ( XTCEContainerContentEntry entry : model.getContentList() ) {
 *
 *         // Skip entries that represent container transitions and
 *         // parameters shown as informative but do not exist in the
 *         // specific binary container processed content.  This can apply
 *         // to ARRAY and AGGREGATE parameters where the parent is in the
 *         // output but only the children have values to process.
 *
 *         if ( ( entry.getEntryType()     != FieldType.PARAMETER ) ||
 *              ( entry.getValue()         == null                ) ) {
 *             continue;
 *         }
 *
 *         // Some databases choose to use aliases for parameters.  This
 *         // example shows displaying all of them with their namespace
 *         // name prepended.  A program might more like use two false
 *         // arguments with the preferred namespace name string.
 *
 *         String alias =
 *             XTCEFunctions.makeAliasDisplayString( entry.getParameter(),
 *                                                   true,  // use all NS
 *                                                   false, // show NS
 *                                                   "" );  // preferred
 *
 *         // Make a one-liner with some basic information about the
 *         // parameter as processed.  This could be heavy on the STDOUT
 *         // flush operation for a very large file.
 *
 *         StringBuilder sb = new StringBuilder();
 *         sb.append( entry.getParameter().getName() );
 *         if ( alias.isEmpty() == false ) {
 *             sb.append( " (" );
 *             sb.append( alias );
 *             sb.append( ")" );
 *         }
 *         sb.append( " = " );
 *         sb.append( entry.getValue().getCalibratedValue() );
 *         sb.append( " [" );
 *         sb.append( entry.getValue().getRawValueHex() );
 *         sb.append( "]" );
 *         sb.append( " " );
 *         sb.append( entry.getParameter().getUnits() );
 *
 *         System.out.println( sb.toString() );
 *
 *     } // End of Parameter for loop
 *
 * } catch ( Exception ex ) {
 *     System.err.println( ex.getClass().getName() );
 *     System.err.println( "Exception: " + ex.getLocalizedMessage() );
 *     System.exit( -1 );
 * }
 * }
 * </pre>
 *
 * @author David Overeem
 *
 */

public class DecodeContainerExample {

    /** The DecodeContainerExample application provides an API example to
     * process/decode a binary file that contains a container whose binary
     * contents are described in an XTCE satellite database.
     *
     * @param args String array of the command line arguments passed by the
     * Java VM.
     *
     * <p>The command line arguments required are as follows:</p>
     *
     * <ul>
     * <li>XTCEFILE - The name of the of the XTCE file to use for the source of
     * container/packet decode specifications.</li>
     * <li>CONTAINER_NAME - The name of the SequenceContainer element in the
     * XTCE document that contains the decode description for the binary file
     * to be provided.  The container name should be the fully qualified path
     * to the container in the XTCE SpaceSystem model.</li>
     * <li>BINARY_FILE - The name of the binary file that contains the contents
     * of the container/packet to be decoded/processed.</li>
     * </ul>
     *
     * <p>This example returns -1 on exception and 0 on success.  Errors and
     * warnings are to STDERR and processed output is to STDOUT.</p>
     *
     */

    public static void main( String[] args ) {

        // Be sure the user has provided the arguments that are needed.

        if ( args.length < 3 ) {
            StringBuilder sb = new StringBuilder();
            sb.append( "Usage: DecodeContainerExample " );
            sb.append( "XTCEFILE " );
            sb.append( "CONTAINER_NAME " );
            sb.append( "BINARY_FILE " );
            System.err.println( sb.toString() );
            System.exit( -1 );
        }

        // The sequence to be performed.  the example handles errors only on
        // a global catch and is long because the purpose of this is to
        // demonstrate interface usage and not a well-factored class.

        try {

            // Load the database file provided in read-only with XInclude.

            XTCEDatabase db = new XTCEDatabase( new File( args[0] ),
                                                false,  // XSD validate flag
                                                true,   // XInclude flag
                                                true ); // ReadOnly flag

            // Retrieve the container object from the database using the full
            // path, which is the quickest for this demo, but findContainers()
            // in the API is much more flexible.

            XTCETMContainer container = db.getContainer( args[1] );

            // Get a stream object for the provided bunary file contents.

            InputStream fStream = new FileInputStream( args[2] );

            // Decode/process the container in the stream using the container
            // returned from db.getContainer() above.

            XTCEContainerContentModel model = db.processContainer( container,
                                                                   fStream );

            // Loop through the entries in the container content model
            // returned from the db.processContainer() above.

            for ( XTCEContainerContentEntry entry : model.getContentList() ) {

                // Skip entries that represent container transitions and
                // parameters shown as informative but do not exist in the
                // specific binary container processed content.  This can apply
                // to ARRAY and AGGREGATE parameters where the parent is in the
                // output but only the children have values to process.

                if ( ( entry.getEntryType()     != FieldType.PARAMETER ) ||
                     ( entry.getValue()         == null                ) ) {
                    continue;
                }

                // Some databases choose to use aliases for parameters.  This
                // example shows displaying all of them with their namespace
                // name prepended.  A program might more like use two false
                // arguments with the preferred namespace name string.

                String alias =
                    XTCEFunctions.makeAliasDisplayString( entry.getParameter(),
                                                          true,  // use all NS
                                                          false, // show NS
                                                          "" );  // preferred

                // Make a one-liner with some basic information about the
                // parameter as processed.  This could be heavy on the STDOUT
                // flush operation for a very large file.

                StringBuilder sb = new StringBuilder();
                sb.append( entry.getParameter().getName() );
                if ( alias.isEmpty() == false ) {
                    sb.append( " (" );
                    sb.append( alias );
                    sb.append( ")" );
                }
                sb.append( " = " );
                sb.append( entry.getValue().getCalibratedValue() );
                sb.append( " [" );
                sb.append( entry.getValue().getRawValueHex() );
                sb.append( "]" );
                sb.append( " " );
                sb.append( entry.getParameter().getUnits() );

                System.out.println( sb.toString() );

            } // End of Parameter for loop

        } catch ( Exception ex ) {
            System.err.println( ex.getClass().getName() );
            System.err.println( "Exception: " + ex.getLocalizedMessage() );
            System.exit( -1 );
        }

    } // End of the example main()

}
