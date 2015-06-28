/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.tests;

import java.io.File;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEDatabaseException;

/**
 *
 * @author b1053583
 */

public class TestProcessContainer {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {

        System.out.println( "Running ProcessContainer For XTCEDatabase" );

        long errors = 0;

        try {

            TestProcessContainer test = new TestProcessContainer();

            test.loadDocument();

            //errors += test.

            System.out.println( "Errors: " + Long.toString( errors ) );
            System.out.println( "Done" );

        } catch ( Exception ex ) {

            System.out.println( "Exception: " + ex.getLocalizedMessage() );
            ex.printStackTrace();

        }

    }


    private void loadDocument() throws XTCEDatabaseException {

        System.out.println( "Loading the BogusSat-1.xml demo database" );

        String file = "src/org/omg/space/xtce/database/BogusSat-1.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, null );

    }

    // Private Data Members

    private XTCEDatabase db_ = null;

}
