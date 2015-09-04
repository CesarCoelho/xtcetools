/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omg.space.xtce.tests;

import java.io.File;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
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

        String file = "foo-xtce.xml";

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

        String file = "src/org/omg/space/xtce/database/UnitTests.xml";

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

        String file = "src/org/omg/space/xtce/database/UnitTests.xml";

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
    public void testCreatingFile() {

        String ssName = "New_Space_System";

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

        String file = "src/org/omg/space/xtce/database/UnitTest-BadForm.xml";

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

        String file = "src/org/omg/space/xtce/database/UnitTest-BadForm.xml";

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

        String file = "src/org/omg/space/xtce/database/BogusSAT-1-Bad.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                true,   // validate on load
                                                false,  // xinclude
                                                true ); // read only

            if ( db.getErrorCount() == 0 ) {
                Assert.fail( "Expected errors loading " + file );
            }

        } catch ( Exception ex ) {

            System.out.println( "Expected Errors:" +
                                System.getProperty( "line.separator" ) +
                                ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testLoadingFileWithErrorsEditableWithValidation() {

        String file = "src/org/omg/space/xtce/database/BogusSAT-1-Bad.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                true,    // validate on load
                                                false,   // xinclude
                                                false ); // read only

            Assert.fail( "Expected errors loading " + file );

        } catch ( Exception ex ) {

            System.out.println( "Expected Errors:" +
                                System.getProperty( "line.separator" ) +
                                ex.getLocalizedMessage() );

        }

    }

    @Test
    public void testLoadingFileWithErrorsReadOnlyNoValidation() {

        String file = "src/org/omg/space/xtce/database/BogusSAT-1-Bad.xml";

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

        String file = "src/org/omg/space/xtce/database/BogusSAT-1-Bad.xml";

        try {

            XTCEDatabase db = new XTCEDatabase( new File( file ),
                                                false,   // validate on load
                                                false,   // xinclude
                                                false ); // read only

        } catch ( Exception ex ) {

            Assert.fail( "Unexpected exception: " + ex.getLocalizedMessage() );

        }

    }

}
