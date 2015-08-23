/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omg.space.xtce.examples;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry;
import org.omg.space.xtce.toolkit.XTCEContainerContentModel;
import org.omg.space.xtce.toolkit.XTCEContainerEntryValue;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCETMContainer;
import org.omg.space.xtce.toolkit.XTCEParameter;
import org.omg.space.xtce.database.ValueEnumerationType;

/** This class is an executable main that loads an XTCE database file and dumps
 * the contents of all the ECSS PUS Housekeeping packets in Service 3,
 * Subservice 25 to STDOUT based on iterating through the "Structure IDs" where
 * a parameter SID_SWITCH_PARAMETER_NAME is an Enumerated Parameter containing
 * all the SIDs and a value of NONE.
 *
 * <P>The following is an example based on this demonstration class file.  The
 * class main() uses a built-in example database file in this larger package,
 * instead of the "myfile.xml" used in the example code below.</P>
 *
 * <pre>
 * {@code
 * try {
 *
 *     System.out.println( "Loading the database" );
 *
 *     // load the satellite database
 *
 *     XTCEDatabase db = new XTCEDatabase( new File( "myfile.xml" ),
 *                                         false,  // skip XSD validation
 *                                         true,   // apply the XIncludes
 *                                         null ); // no progess listener
 *
 *     System.out.println( "Done Loading" );
 *
 *     // replace the parameter name here with the ECCS "Structure ID"
 *     // parameter name.  It is assumed for this example that the SID is
 *     // used in the packet with conditional ContainerRefEntry elements.
 *
 *     List<XTCEParameter> sidParameters =
 *         db.getTelemetryParameters( "SID_SWITCH_PARAMETER_NAME" );
 *
 *     // only 1 SID parameter should have been returned.
 *
 *     if ( sidParameters.size() != 1 ) {
 *         System.out.println( "oops - size is " + sidParameters.size() );
 *         return;
 *     }
 *
 *     // get the list of all the SID (structure ID) conditional parameters
 *     // that can exist in the PUS(3,25) packet structure.
 *
 *     List<ValueEnumerationType> sidEnums =
 *         sidParameters.get( 0 ).getEnumerations();
 *
 *     // get the specific container in XTCE that represents the PUS(3,25)
 *
 *     XTCETMContainer container =
 *         db.getContainer( "/PROGRAM_NAME/PACKETS/SERVICE_003_025" );
 *
 *     // loop through the SID enumerations for SERVICE_003_025 and apply
 *     // the conditional for each evaluation to get the unique packet
 *     // structure, which ends up resulting in the size being available,
 *     // as well as the content.
 *
 *     for ( ValueEnumerationType sidEnum : sidEnums ) {
 *
 *         // skip the SID value equal to NONE because it doesn't exist
 *
 *         if ( sidEnum.getLabel().equals( "NONE" ) == true ) {
 *             continue;
 *         }
 *
 *         // make a message to indicate starting a specific packet
 *
 *         System.out.println( "SERVICE_003_025 with SID " +
 *                             sidEnum.getLabel() );
 *
 *         // make a list of 1 "user value" to pass to the processContainer
 *         // method in order to get a specific evaluation of the packet.
 *
 *         XTCEContainerEntryValue valueObj =
 *             new XTCEContainerEntryValue( sidParameters.get( 0 ),
 *                                          sidEnum.getLabel(),
 *                                          "==",
 *                                          "Calibrated" );
 *
 *         ArrayList<XTCEContainerEntryValue> values = new ArrayList<>();
 *         values.add( valueObj );
 *
 *         // evaluate the contents of the packet in this specific case.
 *
 *         XTCEContainerContentModel model =
 *             db.processContainer( container, values, false );
 *
 *         // get the size (very easy...)
 *
 *         long sizeInBytes = model.getTotalSize();
 *
 *         // for demonstration, get the parameters in the container and
 *         // all of their positional information like start bit, length,
 *         // etc.  see the Javadoc for all that is available.
 *
 *         List<XTCEContainerContentEntry> entries = model.getContentList();
 *
 *         // loop through all the parameters in the packet
 *
 *         for ( XTCEContainerContentEntry entry : entries ) {
 *
 *             // skip content entries that are not actually used because
 *             // the conditional include is false
 *
 *             if ( entry.isCurrentlyInUse() == false ) {
 *                 continue;
 *             }
 *
 *             // print a message based on the type of entry.  in reality,
 *             // TM packets can only have PARAMETER and CONTAINER, but the
 *             // XTCEContainerContentEntry supports TM containers and also TC
 *             // containers.
 *
 *             switch ( entry.getEntryType() ) {
 *
 *                case PARAMETER:
 *                   System.out.println( entry.getEntryTypeString() +
 *                                       ": " +
 *                                       entry.getParameter().getName() +
 *                                       " (" +
 *                                       entry.getParameter().getAlias( "SCS" ) +
 *                                       ")" );
 *                   break;
 *
 *                case ARGUMENT:
 *                   System.out.println( entry.getEntryTypeString() +
 *                                       ": " +
 *                                       entry.getArgument().getName() +
 *                                       " (" +
 *                                       entry.getArgument().getAlias( "SCS" ) +
 *                                       ")" );
 *                   break;
 *
 *                case CONSTANT:
 *                   System.out.println( entry.getEntryTypeString() +
 *                                       ": " +
 *                                       entry.getValue() );
 *                   break;
 *
 *                case CONTAINER:
 *                   // ignore for this purpose
 *                   break;
 *
 *                default:
 *                   break;
 *
 *             } // end of switch/case statement
 *
 *         } // end of container content entry loop
 *
 *         System.out.println( "Total Size: " +
 *                             Long.toString( sizeInBytes ) +
 *                             " bytes" );
 *
 *     } // end of SID (structure ID) loop
 *
 *     System.out.println( "Done" );
 *
 * } catch ( Exception ex ) {
 *
 *    System.out.println( "Exception: " + ex.getLocalizedMessage() );
 *    ex.printStackTrace();
 *
 * } // end of try block
 * }
 * </pre>
 *
 * @author b1053583
 *
 */

public class ProcessEcssHousekeepingExample {

   /** Everything is in main for this example.
    *
    * @param args the command line arguments.
    *
    */

   public static void main( String[] args ) {

      try {

         System.out.println( "Loading the database" );

         // load the satellite database

         XTCEDatabase db = new XTCEDatabase( new File( "myfile.xml" ),
                                             false,  // skip XSD validation
                                             true,   // apply the XIncludes
                                             null ); // no progess listener

         System.out.println( "Done Loading" );

         // replace the parameter name here with the ECCS "Structure ID"
         // parameter name.  It is assumed for this example that the SID is
         // used in the packet with conditional ContainerRefEntry elements.

         List<XTCEParameter> sidParameters =
            db.getTelemetryParameters( "SID_SWITCH_PARAMETER_NAME" );

         // only 1 SID parameter should have been returned.

         if ( sidParameters.size() != 1 ) {
            System.out.println( "oops - size is " + sidParameters.size() );
            return;
         }

         // get the list of all the SID (structure ID) conditional parameters
         // that can exist in the PUS(3,25) packet structure.

         List<ValueEnumerationType> sidEnums =
            sidParameters.get( 0 ).getEnumerations();

         // get the specific container in XTCE that represents the PUS(3,25)

         XTCETMContainer container =
            db.getContainer( "/PROGRAM_NAME/PACKETS/SERVICE_003_025" );

         // loop through the SID enumerations for SERVICE_003_025 and apply
         // the conditional for each evaluation to get the unique packet
         // structure, which ends up resulting in the size being available,
         // as well as the content.

         for ( ValueEnumerationType sidEnum : sidEnums ) {

            // skip the SID value equal to NONE because it doesn't exist

            if ( sidEnum.getLabel().equals( "NONE" ) == true ) {
               continue;
            }

            // make a message to indicate starting a specific packet

            System.out.println( "SERVICE_003_025 with SID " +
                                sidEnum.getLabel() );

            // make a list of 1 "user value" to pass to the processContainer
            // method in order to get a specific evaluation of the packet.

            XTCEContainerEntryValue valueObj =
               new XTCEContainerEntryValue( sidParameters.get( 0 ),
                                            sidEnum.getLabel(),
                                            "==",
                                            "Calibrated" );

            ArrayList<XTCEContainerEntryValue> values = new ArrayList<>();
            values.add( valueObj );

            // evaluate the contents of the packet in this specific case.

            XTCEContainerContentModel model =
               db.processContainer( container, values, false );

            // get the size (very easy...)

            long sizeInBytes = model.getTotalSize();

            // for demonstration, get the parameters in the container and
            // all of their positional information like start bit, length,
            // etc.  see the Javadoc for all that is available.

            List<XTCEContainerContentEntry> entries = model.getContentList();

            // loop through all the parameters in the packet

            for ( XTCEContainerContentEntry entry : entries ) {

               // skip content entries that are not actually used because
               // the conditional include is false

               if ( entry.isCurrentlyInUse() == false ) {
                  continue;
               }

               // print a message based on the type of entry.  in reality,
               // TM packets can only have PARAMETER and CONTAINER, but the
               // XTCEContainerContentEntry supports TM containers and also TC
               // containers.

               switch ( entry.getEntryType() ) {

                  case PARAMETER:
                     System.out.println( entry.getEntryTypeString() +
                                         ": " +
                                         entry.getParameter().getName() +
                                         " (" +
                                         entry.getParameter().getAlias( "SCS" ) +
                                         ")" );
                     break;

                  case ARGUMENT:
                     System.out.println( entry.getEntryTypeString() +
                                         ": " +
                                         entry.getArgument().getName() +
                                         " (" +
                                         entry.getArgument().getAlias( "SCS" ) +
                                         ")" );
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

               } // end of switch/case statement

            } // end of container content entry loop

            System.out.println( "Total Size: " +
                                Long.toString( sizeInBytes ) +
                                " bytes" );

         } // end of SID (structure ID) loop

         System.out.println( "Done" );

      } catch ( Exception ex ) {

         System.out.println( "Exception: " + ex.getLocalizedMessage() );
         ex.printStackTrace();

      } // end of try block

   } // end of main() function

}

