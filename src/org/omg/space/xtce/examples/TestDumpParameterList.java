/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.examples;

import java.io.File;
import java.util.List;
import org.omg.space.xtce.toolkit.XTCEAlias;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEParameter;

/** This class is an executable main that loads an XTCE database file and dumps
 * the parameters to STDOUT.
 *
 * @author David Overeem
 *
 */

public class TestDumpParameterList {

    /** The main executable function
     *
     * @param args the command line arguments.  One argument is expected,
     * which is the name of an XTCE database file to load.
     *
     */

    public static void main(String[] args) {

        System.out.println( "Running TestDumpParameterList" );

        try {

            if ( args.length < 1 ) {
                System.out.println( "Expecting a database filename" );
                System.exit( -1 );
            }

            System.out.println( "Loading the " +
                                args[0] +
                                " database file" );

            XTCEDatabase db =
                new XTCEDatabase( new File( args[0] ), false, false, null );

            List<XTCEParameter> parameters = db.getTelemetryParameters();

            for ( XTCEParameter parameter : parameters ) {

                List<XTCEAlias> aliases = parameter.getAliasSet();

                StringBuilder builder = new StringBuilder();
                for ( XTCEAlias alias : aliases ) {
                    builder.append( alias.getFullAliasName() );
                    builder.append( " " );
                }

                System.out.println( parameter.getName() +
                                    " " +
                                    builder.toString() );

            }

            System.out.println( "Done" );

        } catch ( Exception ex ) {

            System.out.println( "Exception: " + ex.getLocalizedMessage() );
            ex.printStackTrace();

        }

    }
    
}
