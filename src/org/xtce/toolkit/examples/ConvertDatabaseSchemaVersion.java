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

package org.xtce.toolkit.examples;

import java.io.File;
import org.xtce.toolkit.XTCEDatabaseConverter;

/** This application emits a new XTCE 1.2 compliant XML document from an
 * existing XTCE 1.1 compliant XML document.
 *
 * <p>This demo application can be used directly or extended when other options
 * and/or features are desired.
 * </P>
 *
 * <pre>
 * {@code
 * public static void main( String[] args ) {
 * 
 *     // be sure the user has provided an XTCE database XML document
 * 
 *     if ( args.length < 1 ) {
 *         System.err.println( "Usage: convertxtcedatabase XTCEFILE" ); // NOI18N
 *         System.exit( -1 );
 *     }
 * 
 *     // convert the provided XTCE XML document from 1.1 schema compliance
 *     // to 1.2 schema compliance and if successful, save the file with a
 *     // new name using "-1.2.xml" at the end (default new name on save).
 *     // alternate names can be provided with the save( File ) method.
 * 
 *     try {
 * 
 *         // initialize the converter using XInclude = false
 * 
 *         XTCEDatabaseConverter converter =
 *             new XTCEDatabaseConverter( new File( args[0] ), false );
 * 
 *         // perform the modifications to the document structure
 * 
 *         converter.upgrade();
 * 
 *         // normalize the spacing for output on save
 * 
 *         converter.normalizeDocument();
 * 
 *         // save the new file with the default output filename
 * 
 *         boolean success = converter.save();
 * 
 *         // print any messages emitted from the operation
 * 
 *         for ( String message : converter.getMessages() ) {
 *             System.out.println( message );
 *         }
 * 
 *         if ( success == false ) {
 *             System.err.println( "Output file not emitted" );
 *             System.exit( -2 );
 *         }
 * 
 *     } catch ( Exception ex ) {
 * 
 *         System.err.println( "Unexpected exception: " +
 *                             ex.getLocalizedMessage() );
 * 
 *         System.exit( -3 );
 * 
 *     }
 * }
 * </pre>
 *
 * @author David Overeem
 *
 */

public class ConvertDatabaseSchemaVersion {

    /** This application emits a new XTCE 1.2 compliant XML document from an
     * existing XTCE 1.1 compliant XML document.
     *
     * @param args String array of the command line arguments passed by the
     * Java VM.  One argument is needed and that is the XML document file.
     *
     * <p>This tool will return a negative exit code value back to the calling
     * process when it is not successful.  When successful, the normal return
     * value from the JVM occurs, which should be 0 on most platforms.  Errors
     * will also print a string to the STDERR stream.</p>
     *
     */

    public static void main( String[] args ) {

        // be sure the user has provided an XTCE database XML document

        if ( args.length < 1 ) {
            System.err.println( "Usage: convertxtcedatabase XTCEFILE" ); // NOI18N
            System.exit( -1 );
        }

        // convert the provided XTCE XML document from 1.1 schema compliance
        // to 1.2 schema compliance and if successful, save the file with a
        // new name using "-1.2.xml" at the end (default new name on save).
        // alternate names can be provided with the save( File ) method.

        try {

            // initialize the converter using XInclude = false

            XTCEDatabaseConverter converter =
                new XTCEDatabaseConverter( new File( args[0] ), false );

            // perform the modifications to the document structure

            converter.upgrade();

            // normalize the spacing for output on save

            converter.normalizeDocument();

            // save the new file with the default output filename

            boolean success = converter.save();

            // print any messages emitted from the operation

            for ( String message : converter.getMessages() ) {
                System.out.println( message );
            }

            if ( success == false ) {
                System.err.println( "Output file not emitted" );
                System.exit( -2 );
            }

        } catch ( Exception ex ) {

            System.err.println( "Unexpected exception: " +
                                ex.getLocalizedMessage() );

            System.exit( -3 );

        }

    } // end of main()

}
