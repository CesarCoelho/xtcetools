/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.examples;

import java.io.File;
import java.util.List;
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry;
import org.omg.space.xtce.toolkit.XTCEContainerContentModel;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCETMContainer;

/** This class is an executable main that loads an XTCE database file and dumps
 * the contents of all the containers to STDOUT.
 *
 * <P>The following is an example based on this demonstration class file.  The
 * class main() uses a built-in example database file in this larger package,
 * instead of the "myfile.xml" used in the example code below.</P>
 *
 * <pre>
 * {@code
 * try {
 *
 *
 *     XTCEDatabase db = new XTCEDatabase( new File( "myfile.xml" ),
 *                                         false,  // skip XSD validation
 *                                         true,   // apply the XIncludes
 *                                         true ); // ReadOnly
 *
 *     List<XTCETMContainer> containers = db.getContainers();
 *
 *     for ( XTCETMContainer container : containers ) {
 *
 *         XTCEContainerContentModel model =
 *             new XTCEContainerContentModel( container,
 *                                            db.getSpaceSystemTree(),
 *                                            null,
 *                                            false );
 *
 *         // get the size
 *         long sizeInBytes = model.getTotalSize();
 *
 *         // for demonstration, get the parameters in the container
 *         List<XTCEContainerContentEntry> entries =
 *             model.getContentList();
 *
 *         for ( XTCEContainerContentEntry entry : entries ) {
 *
 *             // skip content entries that are not actually used because
 *             // the conditional include is false
 *             if ( entry.isCurrentlyInUse() == false ) {
 *                 continue;
 *             }
 *
 *             // print a message based on the type of entry
 *             switch ( entry.getEntryType() ) {
 *
 *                 case PARAMETER:
 *                     System.out.println( entry.getEntryTypeString() +
 *                                         ": " +
 *                                         entry.getParameter().getName() );
 *                     break;
 *
 *                 case ARGUMENT:
 *                     System.out.println( entry.getEntryTypeString() +
 *                                         ": " +
 *                                         entry.getArgument().getName() );
 *                     break;
 *
 *                 case CONSTANT:
 *                     System.out.println( entry.getEntryTypeString() +
 *                                         ": " +
 *                                         entry.getValue() );
 *                     break;
 *
 *                 case CONTAINER:
 *                     // ignore for this purpose
 *                     break;
 *
 *                 default:
 *                     break;
 *
 *             }
 *
 *         }
 *
 *         System.out.println( "Total Size: " +
 *                             Long.toString( sizeInBytes ) +
 *                             " bytes" );
 *
 *     }
 *
 *     System.out.println( "Done" );
 *
 * } catch ( Exception ex ) {
 *
 *     System.out.println( "Exception: " + ex.getLocalizedMessage() );
 *     ex.printStackTrace();
 *
 * }
 * }
 * </pre>
 *
 * @author David Overeem
 *
 */

public class ProcessContainerExample {

    /**
     * @param args the command line arguments
     */

    public static void main( String[] args ) {

        System.out.println( "Running ProcessContainer For XTCEDatabase" );

        try {

            if ( args.length < 1 ) {
                System.out.println( "Expecting a database filename" );
                System.exit( -1 );
            }

            System.out.println( "Loading the " +
                                args[0] +
                                " database file" );

            XTCEDatabase db = new XTCEDatabase( new File( args[0] ),
                                                false,
                                                true,
                                                true );

            List<XTCETMContainer> containers = db.getContainers();

            for ( XTCETMContainer container : containers ) {

                XTCEContainerContentModel model =
                    new XTCEContainerContentModel( container,
                                                   db.getSpaceSystemTree(),
                                                   null,
                                                   false );

                // get the size
                long sizeInBytes = model.getTotalSize();

                // for demonstration, get the parameters in the container
                List<XTCEContainerContentEntry> entries =
                    model.getContentList();

                for ( XTCEContainerContentEntry entry : entries ) {

                    // skip content entries that are not actually used because
                    // the conditional include is false
                    if ( entry.isCurrentlyInUse() == false ) {
                        continue;
                    }

                    // print a message based on the type of entry
                    switch ( entry.getEntryType() ) {

                        case PARAMETER:
                            System.out.println( entry.getEntryTypeString() +
                                                ": " +
                                                entry.getParameter().getName() );
                            break;

                        case ARGUMENT:
                            System.out.println( entry.getEntryTypeString() +
                                                ": " +
                                                entry.getArgument().getName() );
                            break;

                        case CONSTANT:
                            System.out.println( entry.getEntryTypeString() +
                                                ": " +
                                                entry.getValue() );
                            break;

                        case CONTAINER:
                            // ignore for this purpose
                            break;

                        default:
                            break;

                    }

                }

                System.out.println( "Total Size: " +
                                    Long.toString( sizeInBytes ) +
                                    " bytes" );

            }

            System.out.println( "Done" );

        } catch ( Exception ex ) {

            System.out.println( "Exception: " + ex.getLocalizedMessage() );
            ex.printStackTrace();
            System.exit( -1 );

        }

        System.exit( 0 );

    }

}
