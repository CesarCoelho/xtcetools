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
import java.util.ArrayList;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCEFunctions;
import org.xtce.toolkit.XTCEParameter;
import org.xtce.toolkit.XTCETypedObject.EngineeringType;

/** This application emits Parameter default values from a specified XTCE
 * database file to the STDOUT stream for use by scripts or other tools.
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
 * public class ParameterDefaultValueDump {
 * 
 *     /** Create the ParameterDefaultValueDump application and initialize with
 *      * a satellite database and output file.
 *      *
 *      * @param xtceFile String containing a top-level XTCE file for a mission
 *      * to examine for the necessary parameters to satisfy the user request.
 *      *
 *      * @throws XTCEDatabaseException in the event that the file cannot load.
 *      *
 *      * /
 * 
 *     ParameterDefaultValueDump( final String xtceFile )
 *         throws XTCEDatabaseException {
 * 
 *         sdb_ = new XTCEDatabase( new File( xtceFile ),
 *                                            false,  // XSD validate flag
 *                                            true,   // XInclude flag
 *                                            true ); // ReadOnly flag
 * 
 *     }
 * 
 *     /** Method to display the found parameter default/initial values.
 *      * 
 *      * @param parameterGlobs List of strings containing the name glob list of
 *      * parameters to find and output data for the default/initial values.
 *      *
 *      * @param aliasNameSpace String containing the requested alias namespace
 *      * for the parameter to locate the Alias name from the XTCE document.
 *      *
 *      * /
 * 
 *     protected void showParameterDefaultValues( final List<String> parameterGlobs,
 *                                                final String       aliasNameSpace ) {
 * 
 *         for ( String parameterGlob : parameterGlobs ) {
 * 
 *             List<XTCEParameter> foundList;
 * 
 *             if ( aliasNameSpace.isEmpty() == true ) {
 *                 foundList = sdb_.getTelemetryParameters( parameterGlob );
 *             } else {
 *                 foundList = sdb_.getTelemetryParameters( parameterGlob,
 *                                                          aliasNameSpace );
 *             }
 * 
 *             if ( foundList.isEmpty() == true ) {
 * 
 *                System.err.println( "ERROR: '" +
 *                                    parameterGlob +
 *                                    "' not found in database file" );
 * 
 *             } else {
 * 
 *                for ( XTCEParameter parameter : foundList ) {
 *                    getValueDisplayString( parameter, aliasNameSpace );
 *                }
 * 
 *             }
 * 
 *         }
 * 
 *     }
 * 
 *     /** Method to build a the Java Properties output string for a specific
 *      * parameter.
 *      *
 *      * @param parameter XTCEParameter containing the parameter data.
 *      *
 *      * @param aliasNameSpace String containing the requested alias namespace
 *      * for the parameter to locate the Alias name from the XTCE document.
 *      *
 *      * /
 * 
 *     protected void getValueDisplayString( final XTCEParameter parameter,
 *                                           final String        aliasNameSpace ) {
 * 
 *         StringBuilder sb    = new StringBuilder();
 *         String        value = parameter.getInitialValue();
 * 
 * 
 *         if ( ( parameter.getEngineeringType() == EngineeringType.STRUCTURE ) ||
 *              ( parameter.getEngineeringType() == EngineeringType.ARRAY     ) ) {
 *             return;
 *         }
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
 *             sb.append( XTCEFunctions.makeAliasDisplayString( parameter,
 *                                                              false,
 *                                                              false,
 *                                                              aliasNameSpace ) );
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
 *     /** This application emits Parameter default values from an XTCE database
 *      * file provided as the first argument with the remaining arguments
 *      * containing the desired parameter names.
 *      *
 *      * @param args String array of the command line arguments passed by the
 *      * Java VM.
 *      *
 *      * <p>The command line arguments required are as follows:</p>
 *      *
 *      * <ul>
 *      * <li>XTCEFILE - A top-level XTCE satellite database file for a
 *      * mission.</li>
 *      * <li>PARAMETER(s) - The name of the parameter(s) to dump values for.
 *      * At least 1 must be provided and multiple are allowed.  The names
 *      * may be filesystem glob patterns for matching, although is the
 *      * user specifies a wildcard, it needs to be quoted so that the
 *      * shell will not try to expand it.</li>
 *      * <li>--aliasns=ALIAS_NAME_SPACE - Optional argument to specify which
 *      * Alias @nameSpace in the XTCE document to prefer for the parameter name
 *      * portion of the name=value pairs output text.  This affects both the
 *      * search for the parameter names as well as the output.
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
 *         // be sure the user has provided at least two command line arguments
 * 
 *         if ( args.length < 2 ) {
 *             System.err.println( "Usage: getparametervalues " + // NOI18N
 *                                 "XTCEFILE " + // NOI18N
 *                                 "PARAMETER [PARAMETER ...] " + // NOI18N
 *                                 "[--aliasns=ALIAS_NAME_SPACE]" ); // NOI18N
 *             System.exit( -1 );
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
 *         // gather the parameters into a list and check if the user has
 *         // specified an Alias Namespace to use instead of the parameter name.
 * 
 *         List<String> parameters = new ArrayList<>();
 *         String       aliasNS    = ""; // NOI18N
 * 
 *         for ( int iii = 1; iii < args.length; ++iii ) {
 * 
 *             if ( ( iii  == ( args.length - 1 )                  ) &&
 *                  ( true == args[iii].startsWith( "--aliasns=" ) ) ) {
 *                 aliasNS = args[iii].replaceFirst( "--aliasns=", "" ); // NOI18N
 *             } else {
 *                 parameters.add( args[iii] );
 *             }
 * 
 *         }
 * 
 *         // load the database file provided and show the parameter default
 *         // values to the STDOUT stream.
 * 
 *         try {
 * 
 *             ParameterDefaultValueDump app =
 *                 new ParameterDefaultValueDump( args[0] );
 * 
 *             app.showParameterDefaultValues( parameters, aliasNS );
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

public class ParameterDefaultValueDump {

    /** Create the ParameterDefaultValueDump application and initialize with
     * a satellite database and output file.
     *
     * @param xtceFile String containing a top-level XTCE file for a mission
     * to examine for the necessary parameters to satisfy the user request.
     *
     * @throws XTCEDatabaseException in the event that the file cannot load.
     *
     */

    ParameterDefaultValueDump( final String xtceFile )
        throws XTCEDatabaseException {

        sdb_ = new XTCEDatabase( new File( xtceFile ),
                                           false,  // XSD validate flag
                                           true,   // XInclude flag
                                           true ); // ReadOnly flag

    }

    /** Method to display the found parameter default/initial values.
     * 
     * @param parameterGlobs List of strings containing the name glob list of
     * parameters to find and output data for the default/initial values.
     *
     * @param aliasNameSpace String containing the requested alias namespace
     * for the parameter to locate the Alias name from the XTCE document.
     *
     */

    protected void showParameterDefaultValues( final List<String> parameterGlobs,
                                               final String       aliasNameSpace ) {

        for ( String parameterGlob : parameterGlobs ) {

            List<XTCEParameter> foundList;

            if ( aliasNameSpace.isEmpty() == true ) {
                foundList = sdb_.getTelemetryParameters( parameterGlob );
            } else {
                foundList = sdb_.getTelemetryParameters( parameterGlob,
                                                         aliasNameSpace );
            }

            if ( foundList.isEmpty() == true ) {

               System.err.println( "ERROR: '" +
                                   parameterGlob +
                                   "' not found in database file" );

            } else {

               for ( XTCEParameter parameter : foundList ) {
                   getValueDisplayString( parameter, aliasNameSpace );
               }

            }

        }

    }

    /** Method to build a the Java Properties output string for a specific
     * parameter.
     *
     * @param parameter XTCEParameter containing the parameter data.
     *
     * @param aliasNameSpace String containing the requested alias namespace
     * for the parameter to locate the Alias name from the XTCE document.
     *
     */

    protected void getValueDisplayString( final XTCEParameter parameter,
                                          final String        aliasNameSpace ) {

        StringBuilder sb    = new StringBuilder();
        String        value = parameter.getInitialValue();

        if ( ( parameter.getEngineeringType() == EngineeringType.STRUCTURE ) ||
             ( parameter.getEngineeringType() == EngineeringType.ARRAY     ) ) {
            return;
        }

        if ( value.isEmpty() == true ) {

            System.err.println( "ERROR: No default value defined for '" +
                                parameter.getName() +
                                "'" );
            return;

        }

        if ( aliasNameSpace.isEmpty() == true ) {

            sb.append( parameter.getName() );

        } else {

            sb.append( XTCEFunctions.makeAliasDisplayString( parameter,
                                                             false,
                                                             false,
                                                             aliasNameSpace ) );

        }

        sb.append( " = " );
        sb.append( value );

        System.out.println( sb.toString() );

    }

    /** This application emits Parameter default values from an XTCE database
     * file provided as the first argument with the remaining arguments
     * containing the desired parameter names.
     *
     * @param args String array of the command line arguments passed by the
     * Java VM.
     *
     * <p>The command line arguments required are as follows:</p>
     *
     * <ul>
     * <li>XTCEFILE - A top-level XTCE satellite database file for a
     * mission.</li>
     * <li>PARAMETER(s) - The name of the parameter(s) to dump values for.
     * At least 1 must be provided and multiple are allowed.  The names
     * may be filesystem glob patterns for matching, although is the
     * user specifies a wildcard, it needs to be quoted so that the
     * shell will not try to expand it.</li>
     * <li>--aliasns=ALIAS_NAME_SPACE - Optional argument to specify which
     * Alias @nameSpace in the XTCE document to prefer for the parameter name
     * portion of the name=value pairs output text.  This affects both the
     * search for the parameter names as well as the output.
     * </ul>
     *
     * <p>This tool will return a negative exit code value back to the calling
     * process when it is not successful.  When successful, the normal return
     * value from the JVM occurs, which should be 0 on most platforms.  Errors
     * will also print a string to the STDERR stream.</p>
     *
     */

    public static void main( String[] args ) {

        // be sure the user has provided at least two command line arguments

        if ( args.length < 2 ) {
            System.err.println( "Usage: getparametervalues " + // NOI18N
                                "XTCEFILE " + // NOI18N
                                "PARAMETER [PARAMETER ...] " + // NOI18N
                                "[--aliasns=ALIAS_NAME_SPACE]" ); // NOI18N
            System.exit( -1 );
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

        // gather the parameters into a list and check if the user has
        // specified an Alias Namespace to use instead of the parameter name.

        List<String> parameters = new ArrayList<>();
        String       aliasNS    = ""; // NOI18N

        for ( int iii = 1; iii < args.length; ++iii ) {

            if ( ( iii  == ( args.length - 1 )                  ) &&
                 ( true == args[iii].startsWith( "--aliasns=" ) ) ) {
                aliasNS = args[iii].replaceFirst( "--aliasns=", "" ); // NOI18N
            } else {
                parameters.add( args[iii] );
            }

        }

        // load the database file provided and show the parameter default
        // values to the STDOUT stream.

        try {

            ParameterDefaultValueDump app =
                new ParameterDefaultValueDump( args[0] );

            app.showParameterDefaultValues( parameters, aliasNS );

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
