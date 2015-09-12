/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.tests;

import java.io.File;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.space.xtce.toolkit.XTCEAlias;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEFunctions;
import org.omg.space.xtce.toolkit.XTCEParameter;

/**
 *
 * @author dovereem
 */

public class StaticFunctionsTest {
    
    public StaticFunctionsTest() {

        String file = "src/org/omg/space/xtce/database/UnitTests.xml";

        try {
           db_ = new XTCEDatabase( new File( file ), false, false, true );
        } catch ( Exception ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

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
    public void testGetBitSetFromByteArray() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        byte[] bytes = new byte[5];
        bytes[0] = (byte)0xff;
        bytes[1] = (byte)0x00;
        bytes[2] = (byte)0xf0;
        bytes[3] = (byte)0x0f;
        bytes[4] = (byte)0x00;

        BitSet bits = XTCEFunctions.getBitSetFromByteArray( bytes );

        for ( int iii = 0; iii < bits.size(); ++iii ) {
            if ( ( iii >= 0 ) && ( iii < 8 ) ) {
                Assert.assertTrue( "bits 0 to 7 should be 1",
                                   bits.get( iii ) == true );
            } else if ( ( iii >= 8 ) && ( iii < 16 ) ) {
                Assert.assertTrue( "bits 8 to 16 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 17 ) && ( iii < 20 ) ) {
                Assert.assertTrue( "bits 17 to 20 should be 1",
                                   bits.get( iii ) == true );
            } else if ( ( iii >= 20 ) && ( iii < 24 ) ) {
                Assert.assertTrue( "bits 20 to 24 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 24 ) && ( iii < 28 ) ) {
                Assert.assertTrue( "bits 24 to 28 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 28 ) && ( iii < 32 ) ) {
                Assert.assertTrue( "bits 28 to 32 should be 1",
                                   bits.get( iii ) == true );
            } else if ( ( iii >= 32 ) && ( iii < 40 ) ) {
                Assert.assertTrue( "bits 32 to 40 should be 0",
                                   bits.get( iii ) == false );
            }
        }
        
    }

    @Test
    public void resolvePathReference() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String currentPath   = null;
        String pathReference = null;
        String realPath      = null;

        // check if the root current path will result in /FOO
        currentPath   = "/";
        pathReference = "FOO";

        realPath = XTCEFunctions.resolvePathReference( currentPath,
                                                       pathReference );

        Assert.assertTrue( "Expected /FOO but got " + realPath,
                           realPath.equals( "/FOO" ) == true );

        // check if a non-root current path will result in /FOO
        currentPath   = "/BAR";
        pathReference = "FOO";

        realPath = XTCEFunctions.resolvePathReference( currentPath,
                                                       pathReference );

        Assert.assertTrue( "Expected /BAR/FOO but got " + realPath,
                           realPath.equals( "/BAR/FOO" ) == true );

        // check if a non-root current path will result in /FOO when there is
        // a trailing / on the current path
        currentPath   = "/BAR/";
        pathReference = "FOO";

        realPath = XTCEFunctions.resolvePathReference( currentPath,
                                                       pathReference );

        Assert.assertTrue( "Expected /BAR/FOO but got " + realPath,
                           realPath.equals( "/BAR/FOO" ) == true );

        // check if a non-root current path with a reference containing a .
        // will get watered out
        currentPath   = "/FOO/BAR";
        pathReference = "./NEW";

        realPath = XTCEFunctions.resolvePathReference( currentPath,
                                                       pathReference );

        Assert.assertTrue( "Expected /FOO/BAR/NEW but got " + realPath,
                           realPath.equals( "/FOO/BAR/NEW" ) == true );

        // check if a non-root current path with a reference containing a ..
        // will go back by one
        currentPath   = "/FOO/BAR";
        pathReference = "../NEW";

        realPath = XTCEFunctions.resolvePathReference( currentPath,
                                                       pathReference );

        Assert.assertTrue( "Expected /FOO/NEW but got " + realPath,
                           realPath.equals( "/FOO/NEW" ) == true );

        // check if a non-root current path with a reference containing a .
        // in the middle will get watered out
        currentPath   = "/FOO/BAR";
        pathReference = "OLD/./NEW";

        realPath = XTCEFunctions.resolvePathReference( currentPath,
                                                       pathReference );

        Assert.assertTrue( "Expected /FOO/BAR/OLD/NEW but got " + realPath,
                           realPath.equals( "/FOO/BAR/OLD/NEW" ) == true );

        // check if a non-root current path with a reference containing a ..
        // in the middle will go back by one
        currentPath   = "/FOO/BAR";
        pathReference = "OLD/../NEW";

        realPath = XTCEFunctions.resolvePathReference( currentPath,
                                                       pathReference );

        Assert.assertTrue( "Expected /FOO/BAR/NEW but got " + realPath,
                           realPath.equals( "/FOO/BAR/NEW" ) == true );

        // check if a non-root current path with a reference that is an
        // absolute path containing a . in the middle will get watered out
        currentPath   = "/FOO/BAR";
        pathReference = "/OLD/./NEW";

        realPath = XTCEFunctions.resolvePathReference( currentPath,
                                                       pathReference );

        Assert.assertTrue( "Expected /OLD/NEW but got " + realPath,
                           realPath.equals( "/OLD/NEW" ) == true );

        // check if a non-root current path with a reference that is an
        // absolute path containg a .. in the middle will go back by one
        currentPath   = "/FOO/BAR";
        pathReference = "/OLD/../NEW";

        realPath = XTCEFunctions.resolvePathReference( currentPath,
                                                       pathReference );

        Assert.assertTrue( "Expected /NEW but got " + realPath,
                           realPath.equals( "/NEW" ) == true );

    }

    @Test
    public void getPathNameFromReferenceString() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String filePath = null;
        String pathName = null;

        filePath = "FOO";

        pathName = XTCEFunctions.getPathNameFromReferenceString( filePath );

        Assert.assertTrue( "Expected empty string but got " + pathName,
                           pathName.isEmpty() == true );

        filePath = "FOO/BAR";

        pathName = XTCEFunctions.getPathNameFromReferenceString( filePath );

        Assert.assertTrue( "Expected FOO but got " + pathName,
                           pathName.equals( "FOO" ) == true );

        filePath = "/FOO/BAR";

        pathName = XTCEFunctions.getPathNameFromReferenceString( filePath );

        Assert.assertTrue( "Expected /FOO but got " + pathName,
                           pathName.equals( "/FOO" ) == true );

    }

    @Test
    public void getNameFromPathReferenceString() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String filePath = null;
        String pathName = null;

        filePath = "FOO";

        pathName = XTCEFunctions.getNameFromPathReferenceString( filePath );

        Assert.assertTrue( "Expected FOO but got " + pathName,
                           pathName.equals( "FOO" ) == true );

        filePath = "FOO/BAR";

        pathName = XTCEFunctions.getNameFromPathReferenceString( filePath );

        Assert.assertTrue( "Expected BAR but got " + pathName,
                           pathName.equals( "BAR" ) == true );

        filePath = "/FOO/BAR";

        pathName = XTCEFunctions.getNameFromPathReferenceString( filePath );

        Assert.assertTrue( "Expected BAR but got " + pathName,
                           pathName.equals( "BAR" ) == true );

    }

    @Test
    public void makeAliasDisplayString_ForNoAlias() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_NO_ALIAS" );

            String aliasDisplay =
                XTCEFunctions.makeAliasDisplayString( parameter,
                                                      true,
                                                      true,
                                                      "" );

            List<XTCEAlias> aliases = parameter.getAliasSet();

            Assert.assertTrue( "Expected empty string but got " + aliasDisplay,
                               aliasDisplay.isEmpty() == true );
            
        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void makeAliasDisplayString_ForPreferredNoNamespaceAlias() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_WITH_MIXED_ALIAS" );

            String aliasDisplay =
                XTCEFunctions.makeAliasDisplayString( parameter,
                                                      false, // show all NS
                                                      false, // show alias NS
                                                      "bar" );

            Assert.assertTrue( "Expected INTTYPE_WITH_SECOND_ALIAS but got " + aliasDisplay,
                               aliasDisplay.equals( "INTTYPE_WITH_SECOND_ALIAS" ) == true );
            
        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void makeAliasDisplayString_ForPreferredWithNamespaceAlias() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_WITH_MIXED_ALIAS" );

            String aliasDisplay =
                XTCEFunctions.makeAliasDisplayString( parameter,
                                                      false, // show all NS
                                                      true,  // show alias NS
                                                      "bar" );

            Assert.assertTrue( "Expected bar::INTTYPE_WITH_SECOND_ALIAS but got " + aliasDisplay,
                               aliasDisplay.equals( "bar::INTTYPE_WITH_SECOND_ALIAS" ) == true );
            
        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void makeAliasDisplayString_ForPreferredNoNamespaceAliasShowAll() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        // the preferred namespace is ignored in the show all case

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_WITH_MIXED_ALIAS" );

            String aliasDisplay =
                XTCEFunctions.makeAliasDisplayString( parameter,
                                                      true,  // show all NS
                                                      false, // show alias NS
                                                      "bar" );

            Assert.assertTrue( "Expected INT_WITH_ALIAS_2 INTTYPE_WITH_SECOND_ALIAS but got " + aliasDisplay,
                               aliasDisplay.equals( "INT_WITH_ALIAS_2 INTTYPE_WITH_SECOND_ALIAS" ) == true );
            
        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void makeAliasDisplayString_ForPreferredWithNamespaceAliasShowAll() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        // the preferred namespace is ignored in the show all case

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_WITH_MIXED_ALIAS" );

            String aliasDisplay =
                XTCEFunctions.makeAliasDisplayString( parameter,
                                                      true, // show all NS
                                                      true, // show alias NS
                                                      "bar" );

            Assert.assertTrue( "Expected foo::INT_WITH_ALIAS_2 bar::INTTYPE_WITH_SECOND_ALIAS but got " + aliasDisplay,
                               aliasDisplay.equals( "foo::INT_WITH_ALIAS_2 bar::INTTYPE_WITH_SECOND_ALIAS" ) == true );
            
        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void matchesUsingGlob() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String  text   = null;
        String  glob   = null;
        boolean result = false;

        text   = "abcd1234";
        glob   = "abcd1234";
        result = XTCEFunctions.matchesUsingGlob( text, glob );

        Assert.assertTrue( "no glob exact text should match",
                           result == true );

        text   = "abcd1234";
        glob   = "xxx";
        result = XTCEFunctions.matchesUsingGlob( text, glob );

        Assert.assertTrue( "no glob wrong text should not match",
                           result == false );

        text   = "abcd1234";
        glob   = "*bcd*";
        result = XTCEFunctions.matchesUsingGlob( text, glob );

        Assert.assertTrue( "wildcard glob should match when valid",
                           result == true );

        text   = "abcd1234";
        glob   = "*xxx*";
        result = XTCEFunctions.matchesUsingGlob( text, glob );

        Assert.assertTrue( "wildcard glob should not match when not valid",
                           result == false );

        text   = "abcd1234";
        glob   = "abcd?234";
        result = XTCEFunctions.matchesUsingGlob( text, glob );

        Assert.assertTrue( "wildcard glob should match when valid",
                           result == true );

        text   = "abcd1234";
        glob   = "abcd?555";
        result = XTCEFunctions.matchesUsingGlob( text, glob );

        Assert.assertTrue( "wildcard glob should not match when not valid",
                           result == false );

    }

    @Test
    public void getText() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        // a valid key in the resource bundle will return text but an invalid
        // key will throw

        try {

            XTCEFunctions.setLocalePreference( Locale.ENGLISH );

            String out = null;

            out = XTCEFunctions.getText( "general_telemetry" );

            Assert.assertTrue( "Expected 'Telemetry' but got '" + out + "'",
                               out.equals( "Telemetry" ) == true );

            try {

               out = XTCEFunctions.getText( "foobar" );

               Assert.fail( "Expected exception because 'foobar' is not in " +
                            "the I18N resource bundle" );

            } catch ( Throwable ex ) {
                // do nothing, this is expected
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void setLocalePreference() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        boolean result = false;

        try {

            result = XTCEFunctions.setLocalePreference( Locale.ENGLISH );

            Assert.assertTrue( "Should be able to set Locale.ENGLISH",
                               result == true );

            result = XTCEFunctions.setLocalePreference( Locale.FRENCH );

            Assert.assertTrue( "Should be able to set Locale.FRENCH",
                               result == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        } finally {
            XTCEFunctions.setLocalePreference( Locale.ENGLISH );
        }

    }

    @Test
    public void checkLocaleAvailable() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        boolean result = false;

        result = XTCEFunctions.checkLocaleAvailable( Locale.ENGLISH );

        Assert.assertTrue( "Locale.ENGLISH should be available",
                           result == true );

        result = XTCEFunctions.setLocalePreference( Locale.FRENCH );

        Assert.assertTrue( "Locale.FRENCH should be available",
                           result == true );

        result = XTCEFunctions.setLocalePreference( Locale.CHINESE );

        Assert.assertTrue( "Locale.CHINESE should be available, but useless",
                           result == true );

    }

    @Test
    public void generalErrorPrefix() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {
            XTCEFunctions.setLocalePreference( Locale.ENGLISH );
            String out = XTCEFunctions.generalErrorPrefix();
            Assert.assertTrue( "Expected 'ERROR: ' but got '" + out + "'",
                               out.equals( "ERROR: " ) == true );
        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void generalWarningPrefix() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {
            XTCEFunctions.setLocalePreference( Locale.ENGLISH );
            String out = XTCEFunctions.generalWarningPrefix();
            Assert.assertTrue( "Expected 'Warning: ' but got '" + out + "'",
                               out.equals( "Warning: " ) == true );
        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    // cannot easily do the xmlPrettyPrint and getMemoryUsageStatistics
    // functions right now.  The formatMemoryQuantity is not public.

    private XTCEDatabase db_ = null;

}
