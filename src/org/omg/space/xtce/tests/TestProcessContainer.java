/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.tests;

import java.io.File;
import java.util.ArrayList;
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry;
import org.omg.space.xtce.toolkit.XTCEContainerContentModel;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCETMContainer;

/**
 *
 * @author David Overeem
 *
 */

public class TestProcessContainer {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {

        System.out.println( "Running ProcessContainer For XTCEDatabase" );

        try {

            System.out.println( "Loading the BogusSat-1.xml demo database" );

            String file = "src/org/omg/space/xtce/database/BogusSat-1.xml";

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                false,
                                                false,
                                                null );

            ArrayList<XTCETMContainer> containers = db.getContainers();

            for ( XTCETMContainer container : containers ) {

                XTCEContainerContentModel model =
                    new XTCEContainerContentModel( container,
                                                   db.getSpaceSystemTree(),
                                                   null,
                                                   false );

                // get the size
                long sizeInBytes = model.getTotalSize();

                // for demonstration, get the parameters in the container
                ArrayList<XTCEContainerContentEntry> entries =
                    model.getContentList();

                for( XTCEContainerContentEntry entry : entries ) {

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

        }

    }

}
