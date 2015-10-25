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

package org.omg.space.xtce.tests;

import java.io.File;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.space.xtce.toolkit.XTCEConstants;
import org.omg.space.xtce.toolkit.XTCEDatabase;

/**
 *
 * @author dovereem
 */
public class ParsingTest {
    
    public ParsingTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testLoadingFileNotFound() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file = "foo-xtce.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                false,  // validate on load
                                                false,  // xinclude
                                                true ); // read only

            Assert.fail( "File " + file + " should have thrown not found" );

        } catch ( Exception ex ) {

            if ( ex.getLocalizedMessage().contains( file ) == false ) {
                Assert.fail( "Expected filename in throw, text: " +
                             ex.getLocalizedMessage() );
            }

        }

    }

    @Test
    public void testLoadingUnitTestFileReadOnly() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file = "src/org/omg/space/xtce/database/UnitTests.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                false,  // validate on load
                                                false,  // xinclude
                                                true ); // read only

            Assert.assertTrue( "Read Only Flag Not Set", db.isReadOnly() );

        } catch ( Exception ex ) {

            Assert.fail( "Unexpected exception: " + ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testLoadingUnitTestFileEditable() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file = "src/org/omg/space/xtce/database/UnitTests.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                false,   // validate on load
                                                false,   // xinclude
                                                false ); // read only

            Assert.assertFalse( "Read Only Flag Set", db.isReadOnly() );

        } catch ( Exception ex ) {

            Assert.fail( "Unexpected exception: " + ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testFileAccessorAttributes() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file = "src/org/omg/space/xtce/database/UnitTests.xml";

        try {

            File fileIn = new File( file );

            XTCEDatabase db = new XTCEDatabase( fileIn,
                                                false,  // validate on load
                                                false,  // xinclude
                                                true ); // read only

            File fileOut = db.getFilename();

            Assert.assertTrue( "File opened should equal file out",
                               fileIn.getAbsolutePath().equals( fileOut.getAbsolutePath() ) );

            Assert.assertTrue( "Namespace should be '" +
                               XTCEConstants.XTCE_NAMESPACE + "'",
                               db.getNamespaceFromDocument().equals( XTCEConstants.XTCE_NAMESPACE ) );

            Assert.assertTrue( "Default Schema should be '" +
                               XTCEConstants.DEFAULT_SCHEMA_FILE + "'",
                               db.getSchemaFromDocument().equals( XTCEConstants.DEFAULT_SCHEMA_FILE ) );

            Assert.assertTrue( "Loaded file should not have changed",
                               db.getChanged() == false );

        } catch ( Exception ex ) {

            Assert.fail( "Unexpected exception: " + ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testCreatingFile() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String ssName = "New_Space_System";

        try {

            XTCEDatabase db = new XTCEDatabase( ssName );

            Assert.assertFalse( "Read Only Flag Set", db.isReadOnly() );

            Assert.assertTrue( "Top level Space System Name is wrong",
                               db.getRootSpaceSystem().getName().equals( ssName ) );

        } catch ( Exception ex ) {

            Assert.fail( "Unexpected exception: " + ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testLoadingFileWithSyntaxErrorsReadOnlyWithValidation() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file =
            "src/org/omg/space/xtce/database/UnitTest-BadForm.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                true,   // validate on load
                                                false,  // xinclude
                                                true ); // read only

            Assert.fail( "Expected throw when loading " + file );

        } catch ( Exception ex ) {

            System.out.println( "Expected Errors:" +
                                System.getProperty( "line.separator" ) +
                                ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testLoadingFileWithSyntaxErrorsEditableWithValidation() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file =
            "src/org/omg/space/xtce/database/UnitTest-BadForm.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                true,    // validate on load
                                                false,   // xinclude
                                                false ); // read only

            Assert.fail( "Expected throw when loading " + file );

        } catch ( Exception ex ) {

            System.out.println( "Expected Errors:" +
                                System.getProperty( "line.separator" ) +
                                ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testLoadingFileWithErrorsReadOnlyWithValidation() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file =
            "src/org/omg/space/xtce/database/BogusSAT-1-Bad.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                true,   // validate on load
                                                false,  // xinclude
                                                true ); // read only

            if ( db.getErrorCount() == 0 ) {
                Assert.fail( "Expected errors loading " + file );
            } else {
                for ( String message : db.getDocumentWarnings() ) {
                    Assert.assertFalse( "Should not have gotten IO Fatal",
                                        message.startsWith( "IO Fatal:" ) );
                    System.out.println( "Expected Message: " + message );
                }
            }

        } catch ( Exception ex ) {

            System.out.println( "Expected Errors:" +
                                System.getProperty( "line.separator" ) +
                                ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testLoadingFileWithErrorsEditableWithValidation() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file =
            "src/org/omg/space/xtce/database/BogusSAT-1-Bad.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                true,    // validate on load
                                                false,   // xinclude
                                                false ); // read only

            if ( db.getErrorCount() == 0 ) {
                Assert.fail( "Expected errors loading " + file );
            } else {
                for ( String message : db.getDocumentWarnings() ) {
                    Assert.assertFalse( "Should not have gotten IO Fatal",
                                        message.startsWith( "IO Fatal:" ) );
                    System.out.println( "Expected Message: " + message );
                }
            }

        } catch ( Exception ex ) {

            System.out.println( "Expected Errors:" +
                                System.getProperty( "line.separator" ) +
                                ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testLoadingFileWithErrorsReadOnlyNoValidation() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file =
            "src/org/omg/space/xtce/database/BogusSAT-1-Bad.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                false,  // validate on load
                                                false,  // xinclude
                                                true ); // read only

        } catch ( Exception ex ) {

            Assert.fail( "Unexpected exception: " + ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testLoadingFileWithErrorsEditableNoValidation() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file =
            "src/org/omg/space/xtce/database/BogusSAT-1-Bad.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                false,   // validate on load
                                                false,   // xinclude
                                                false ); // read only

        } catch ( Exception ex ) {

            Assert.fail( "Unexpected exception: " + ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testStaticValidationFunctionBadlyFormed() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file =
            "src/org/omg/space/xtce/database/UnitTest-BadForm.xml";

        try {

            List<String> messages =
                XTCEDatabase.validateDocument( new File( file ), false );

            if ( messages.isEmpty() == true ) {
                Assert.fail( "Expected errors loading " + file );
            } else {
                for ( String message : messages ) {
                    System.out.println( "Message: " + message );
                    Assert.assertFalse( "Should not have gotten IO Fatal",
                                        message.startsWith( "IO Fatal:" ) );
                }
            }

        } catch ( Exception ex ) {

            Assert.fail( "Should not have thrown from " +
                "XTCEDatabase.validateDocument: " + ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testStaticValidationFunctionNotSchemaCompliant() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String file =
            "src/org/omg/space/xtce/database/BogusSAT-1-Bad.xml";

        try {

            List<String> messages =
                XTCEDatabase.validateDocument( new File( file ), false );

            if ( messages.isEmpty() == true ) {
                Assert.fail( "Expected errors loading " + file );
            } else {
                for ( String message : messages ) {
                    System.out.println( "Message: " + message );
                    Assert.assertFalse( "Should not have gotten IO Fatal",
                                        message.startsWith( "IO Fatal:" ) );
                }
            }

        } catch ( Exception ex ) {

            Assert.fail( "Should not have thrown from " +
                "XTCEDatabase.validateDocument: " + ex.getLocalizedMessage() );

        }

    }

}
