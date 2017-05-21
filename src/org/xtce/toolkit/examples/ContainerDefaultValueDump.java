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
import java.util.List;
import org.xtce.toolkit.XTCEContainerContentEntry;
import org.xtce.toolkit.XTCEContainerContentEntry.FieldType;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCEFunctions;
import org.xtce.toolkit.XTCEParameter;
import org.xtce.toolkit.XTCETMContainer;
import org.xtce.toolkit.XTCETypedObject.EngineeringType;

/** This application emits Parameter default values from a specified XTCE
 * database container to the STDOUT stream for use by scripts or other tools.
 *
 * <p>The output file is in Java Properties format and is an ordered list of
 * the parameters in a database file with their associated engineering values.
 * </P>
 *
 * <p>This demo application could be extended by inheritance where the user
 * would override the getValueDisplayString method to make any sort of printout
 * of parameter attributes needed.</p>
 *
 * <pre>
 * {@code
 * public class ContainerDefaultValueDump {
 * 
 *     /** Create the ContainerDefaultValueDump application and initialize with
 *      * a satellite database and output file.
 *      *
 *      * @param xtceFile String containing a top-level XTCE file for a mission
 *      * to examine for the necessary container.
 *      *
 *      * @throws XTCEDatabaseException in the event that the file cannot load.
 *      *
 *      * /
 * 
 *     ContainerDefaultValueDump( final String xtceFile )
 *         throws XTCEDatabaseException {
 * 
 *         sdb_ = new XTCEDatabase( new File( xtceFile ),
 *                                  false,  // XSD validate flag
 *                                  true,   // XInclude flag
 *                                  true ); // ReadOnly flag
 * 
 *     }
 * 
 *     /** Method to display the found parameter default/initial values in the
 *      * user provided database container.
 *      * 
 *      * @param container String containing the name of the container to find
 *      * and output data for the default/initial values.
 *      *
 *      * @param aliasNameSpace String containing the requested alias namespace
 *      * for the parameter to locate the Alias name from the XTCE document.
 *      *
 *      * @throws XTCEDatabaseException in the event that the chosen container
 *      * could not be processed.
 *      *
 *      * /
 * 
 *     protected void showContainerDefaultValues( final String container,
 *                                                final String aliasNameSpace )
 *         throws XTCEDatabaseException {
 * 
 *         // retrieve the container
 * 
 *         XTCEContainerContentModel model = decodeContainer( sdb_, container );
 * 
 *         // emit the contents of the container to STDOUT
 * 
 *         for ( XTCEContainerContentEntry entry : model.getContentList() ) {
 * 
 *            if ( ( entry.getEntryType()                      == FieldType.PARAMETER       ) &&
 *                 ( entry.getParameter().getEngineeringType() != EngineeringType.STRUCTURE ) &&
 *                 ( entry.getParameter().getEngineeringType() != EngineeringType.ARRAY     ) &&
 *                 ( entry.isCurrentlyInUse()                  == true                      ) ) {
 * 
 *               getValueDisplayString( entry, aliasNameSpace );
 * 
 *            }
 * 
 *         }
 * 
 *     }
 * 
 *     /** Method to build a the Java Properties output string for a specific
 *      * entry in the container.
 *      *
 *      * @param entry XTCEContainerContentEntry containing the parameter entry
 *      * in the container content.
 *      *
 *      * @param aliasNameSpace String containing the requested alias namespace
 *      * for the parameter to locate the Alias name from the XTCE document.
 *      *
 *      * /
 * 
 *     protected void getValueDisplayString( XTCEContainerContentEntry entry,
 *                                           String                    aliasNameSpace ) {
 * 
 *         StringBuilder sb        = new StringBuilder();
 *         XTCEParameter parameter = entry.getParameter();
 *         String        value     = parameter.getInitialValue();
 * 
 *         if ( value.isEmpty() == true ) {
 * 
 *             System.err.println( "ERROR: No default value defined for '" +
 *                                 parameter.getName() +
 *                                 "'" );
 *             return;
 * 
 *         }
 * 
 *         if ( aliasNameSpace.isEmpty() == true ) {
 * 
 *             sb.append( parameter.getName() );
 * 
 *         } else {
 * 
 *             String alias = XTCEFunctions.makeAliasDisplayString( parameter,
 *                                                                  false,
 *                                                                  false,
 *                                                                  aliasNameSpace );
 * 
 *             // fallback if the parameter does not have the requested namespace
 *             // in the alias set element.
 * 
 *             if ( alias.isEmpty() == true ) {
 *                 alias = parameter.getName();
 *             }
 * 
 *             sb.append( alias );
 * 
 *         }
 * 
 *         sb.append( " = " );
 *         sb.append( value );
 * 
 *         System.out.println( sb.toString() );
 * 
 *     }
 * 
 *     /** Method to process the specific container from the satellite database.
 *      *
 *      * @param sdb XTCEDatabase object with the database loaded in order to
 *      * search for the requested container.
 *      *
 *      * @param container String containing the container name to search for.
 *      *
 *      * @return XTCEContainerContentModel object that contains the
 *      * processed results for the container in the SDB.
 *      *
 *      * @throws XTCEDatabaseException in the event that the container cannot be
 *      * processed by the toolkit library.  A RuntimeExceptio is thrown in the
 *      * event that this application could not locate the precise container from
 *      * the loaded XTCE database object.
 *      *
 *      * /
 * 
 *     private XTCEContainerContentModel decodeContainer( XTCEDatabase sdb,
 *                                                        String       container )
 *         throws XTCEDatabaseException {
 * 
 *         List<XTCETMContainer> containers = sdb.getContainers( container );
 * 
 *         if ( containers.size() != 1 ) {
 *             throw new XTCEDatabaseException( "Unable to precisely locate the" +
 *                 " decom container '" + container +
 *                 "' from the database.  Found " +
 *                 Integer.toString( containers.size() ) + " matches." );
 *         }
 * 
 *         // process the container
 * 
 *         return sdb.processContainer( containers.get( 0 ), null, false );
 * 
 *     }
 * 
 *     /** This application emits Parameter default values from an XTCE database
 *      * file provided as the first argument with the remaining arguments
 *      * containing the desired container names.
 *      *
 *      * @param args String array of the command line arguments passed by the
 *      * Java VM.
 *      *
 *      * <p>The command line arguments required are as follows:</p>
 *      *
 *      * <ul>
 *      * <li>XTCEFILE - A top-level XTCE satellite database file for a
 *      * mission.</li>
 *      * <li>CONTAINER - The name of the TM container to dump values for.</li>
 *      * <li>--aliasns=ALIAS_NAME_SPACE - Optional argument to specify which
 *      * Alias @nameSpace in the XTCE document to prefer for the parameter name
 *      * portion of the name=value pairs output text.  This affects both the
 *      * search for the parameter names as well as the output.
 *      * </ul>
 *      * </ul>
 *      *
 *      * <p>This tool will return a negative exit code value back to the calling
 *      * process when it is not successful.  When successful, the normal return
 *      * value from the JVM occurs, which should be 0 on most platforms.  Errors
 *      * will also print a string to the STDERR stream.</p>
 *      *
 *      * /
 * 
 *     public static void main( String[] args ) {
 * 
 *         // be sure the user has provided the two command line arguments
 * 
 *         if ( args.length < 2 ) {
 *             System.err.println( "Usage: getcontainervalues " + // NOI18N
 *                                 "XTCEFILE " + // NOI18N
 *                                 "CONTAINER " + // NOI18N
 *                                 "[--aliasns=ALIAS_NAME_SPACE]" ); // NOI18N
 *             System.exit( -1 );
 *         }
 * 
 *         // grab the alias namespace option if the user provided it
 * 
 *         String aliasNameSpace = "";
 * 
 *         if ( args.length == 3 ) {
 *             aliasNameSpace = args[2].replaceFirst( "--aliasns=", "" );
 *         }
 * 
 *         // register any custom AbsoluteTime Handler classes here.  these are
 *         // specific to the users' program and examples are in the toolkit for
 *         // POSIX and CCSDS CUC.  the examples are always registered, so they
 *         // need not be added here.
 * 
 *         try {
 * 
 *             XTCEFunctions.registerAbsoluteTimeHandler( new MyProgramTimeHandler() );
 * 
 *         } catch ( Exception ex ) {
 * 
 *             System.err.println( "ERROR: Unable to register time handlers: " +
 *                                 ex.getLocalizedMessage() );
 *             System.exit( -2 );
 * 
 *         }
 * 
 *         // process the requested container and output the parameter values
 * 
 *         try {
 * 
 *             ContainerDefaultValueDump app =
 *                 new ContainerDefaultValueDump( args[0] );
 * 
 *             app.showContainerDefaultValues( args[1], aliasNameSpace );
 * 
 *         } catch ( XTCEDatabaseException ex ) {
 * 
 *             System.err.println( "ERROR: Cannot process file " +
 *                                 args[0] + " due to exception '" +
 *                                 ex.getLocalizedMessage() + "'" );
 * 
 *             System.exit( -3 );
 * 
 *         }
 * 
 *     } // end of main()
 * 
 *     /** Data Member to hold the XTCEDatabase file object from the library for
 *      * use by functions.
 *      *
 *      * /
 * 
 *     protected final XTCEDatabase sdb_;
 * 
 * }
 * }
 * </pre>
 *
 * @author David Overeem
 *
 */

public class ContainerDefaultValueDump {

    /** Create the ContainerDefaultValueDump application and initialize with
     * a satellite database and output file.
     *
     * @param xtceFile String containing a top-level XTCE file for a mission
     * to examine for the necessary container.
     *
     * @throws XTCEDatabaseException in the event that the file cannot load.
     *
     */

    ContainerDefaultValueDump( final String xtceFile )
        throws XTCEDatabaseException {

        sdb_ = new XTCEDatabase( new File( xtceFile ),
                                 false,  // XSD validate flag
                                 true,   // XInclude flag
                                 true ); // ReadOnly flag

    }

    /** Method to display the found parameter default/initial values in the
     * user provided database container.
     * 
     * @param container String containing the name of the container to find
     * and output data for the default/initial values.
     *
     * @param aliasNameSpace String containing the requested alias namespace
     * for the parameter to locate the Alias name from the XTCE document.
     *
     * @throws XTCEDatabaseException in the event that the chosen container
     * could not be processed.
     *
     */

    protected void showContainerDefaultValues( final String container,
                                               final String aliasNameSpace )
        throws XTCEDatabaseException {

        // retrieve the container

        XTCEContainerContentModel model = decodeContainer( sdb_, container );

        // emit the contents of the container to STDOUT

        for ( XTCEContainerContentEntry entry : model.getContentList() ) {

           if ( ( entry.getEntryType()                      == FieldType.PARAMETER       ) &&
                ( entry.getParameter().getEngineeringType() != EngineeringType.STRUCTURE ) &&
                ( entry.getParameter().getEngineeringType() != EngineeringType.ARRAY     ) &&
                ( entry.isCurrentlyInUse()                  == true                      ) ) {

              getValueDisplayString( entry, aliasNameSpace );

           }

        }

    }

    /** Method to build a the Java Properties output string for a specific
     * entry in the container.
     *
     * @param entry XTCEContainerContentEntry containing the parameter entry
     * in the container content.
     *
     * @param aliasNameSpace String containing the requested alias namespace
     * for the parameter to locate the Alias name from the XTCE document.
     *
     */

    protected void getValueDisplayString( XTCEContainerContentEntry entry,
                                          String                    aliasNameSpace ) {

        StringBuilder sb        = new StringBuilder();
        XTCEParameter parameter = entry.getParameter();
        String        value     = parameter.getInitialValue();

        if ( value.isEmpty() == true ) {

            System.err.println( "ERROR: No default value defined for '" +
                                parameter.getName() +
                                "'" );
            return;

        }

        if ( aliasNameSpace.isEmpty() == true ) {

            sb.append( parameter.getName() );

        } else {

            String alias = XTCEFunctions.makeAliasDisplayString( parameter,
                                                                 false,
                                                                 false,
                                                                 aliasNameSpace );

            // fallback if the parameter does not have the requested namespace
            // in the alias set element.

            if ( alias.isEmpty() == true ) {
                alias = parameter.getName();
            }

            sb.append( alias );

        }

        sb.append( " = " );
        sb.append( value );

        System.out.println( sb.toString() );

    }

    /** Method to process the specific container from the satellite database.
     *
     * @param sdb XTCEDatabase object with the database loaded in order to
     * search for the requested container.
     *
     * @param container String containing the container name to search for.
     *
     * @return XTCEContainerContentModel object that contains the
     * processed results for the container in the SDB.
     *
     * @throws XTCEDatabaseException in the event that the container cannot be
     * processed by the toolkit library.  A RuntimeExceptio is thrown in the
     * event that this application could not locate the precise container from
     * the loaded XTCE database object.
     *
     */

    private XTCEContainerContentModel decodeContainer( XTCEDatabase sdb,
                                                       String       container )
        throws XTCEDatabaseException {

        List<XTCETMContainer> containers = sdb.getContainers( container );

        if ( containers.size() != 1 ) {
            throw new XTCEDatabaseException( "Unable to precisely locate the" +
                " decom container '" + container +
                "' from the database.  Found " +
                Integer.toString( containers.size() ) + " matches." );
        }

        // process the container

        return sdb.processContainer( containers.get( 0 ), null, false );

    }

    /** This application emits Parameter default values from an XTCE database
     * file provided as the first argument with the remaining arguments
     * containing the desired container names.
     *
     * @param args String array of the command line arguments passed by the
     * Java VM.
     *
     * <p>The command line arguments required are as follows:</p>
     *
     * <ul>
     * <li>XTCEFILE - A top-level XTCE satellite database file for a
     * mission.</li>
     * <li>CONTAINER - The name of the TM container to dump values for.</li>
     * <li>--aliasns=ALIAS_NAME_SPACE - Optional argument to specify which
     * Alias @nameSpace in the XTCE document to prefer for the parameter name
     * portion of the name=value pairs output text.  This affects both the
     * search for the parameter names as well as the output.
     * </ul>
     * </ul>
     *
     * <p>This tool will return a negative exit code value back to the calling
     * process when it is not successful.  When successful, the normal return
     * value from the JVM occurs, which should be 0 on most platforms.  Errors
     * will also print a string to the STDERR stream.</p>
     *
     */

    public static void main( String[] args ) {

        // be sure the user has provided the two command line arguments

        if ( args.length < 2 ) {
            System.err.println( "Usage: getcontainervalues " + // NOI18N
                                "XTCEFILE " + // NOI18N
                                "CONTAINER " + // NOI18N
                                "[--aliasns=ALIAS_NAME_SPACE]" ); // NOI18N
            System.exit( -1 );
        }

        // grab the alias namespace option if the user provided it

        String aliasNameSpace = "";

        if ( args.length == 3 ) {
            aliasNameSpace = args[2].replaceFirst( "--aliasns=", "" );
        }

        // register any custom AbsoluteTime Handler classes here.  these are
        // specific to the users' program and examples are in the toolkit for
        // POSIX and CCSDS CUC.  the examples are always registered, so they
        // need not be added here.

        try {

            //XTCEFunctions.registerAbsoluteTimeHandler( new MyProgramTimeHandler() );

        } catch ( Exception ex ) {

            System.err.println( "ERROR: Unable to register time handlers: " +
                                ex.getLocalizedMessage() );
            System.exit( -2 );

        }

        // process the requested container and output the parameter values

        try {

            ContainerDefaultValueDump app =
                new ContainerDefaultValueDump( args[0] );

            app.showContainerDefaultValues( args[1], aliasNameSpace );

        } catch ( XTCEDatabaseException ex ) {

            System.err.println( "ERROR: Cannot process file " +
                                args[0] + " due to exception '" +
                                ex.getLocalizedMessage() + "'" );

            System.exit( -3 );

        }

    } // end of main()

    /** Data Member to hold the XTCEDatabase file object from the library for
     * use by functions.
     *
     */

    protected final XTCEDatabase sdb_;

}
