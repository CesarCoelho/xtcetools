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

package org.xtce.toolkit;

import java.io.File;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Locale;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.space.xtce.AbsoluteTimeDataType;

/**
 *
 * @author dovereem
 */

public class StaticFunctionsTest {
    
    public StaticFunctionsTest() {

        String file = "test/org/xtce/toolkit/test/UnitTests.xml";

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
    public void testBitSetToHex() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        BitSet bits;
        String hex;

        bits = new BitSet( 2 );
        hex  = XTCEFunctions.bitSetToHex( bits, 8 );

        Assert.assertTrue( "Value should be 0x0000000000000000, but is " + hex,
                           hex.equals( "0x0000000000000000" ) == true );

        bits.set( 1, true );
        hex = XTCEFunctions.bitSetToHex( bits, 8 );

        Assert.assertTrue( "Value should be 0x0000000000000002, but is " + hex,
                           hex.equals( "0x0000000000000002" ) == true );

        bits = new BitSet( 70 );
        bits.set( 69 );
        hex = XTCEFunctions.bitSetToHex( bits, 9 );

        Assert.assertTrue( "Value should be 0x200000000000000000, but is " + hex,
                           hex.equals( "0x200000000000000000" ) == true );

        hex = XTCEFunctions.bitSetToHex( bits, 10 );

        Assert.assertTrue( "Value should be 0x00200000000000000000, but is " + hex,
                           hex.equals( "0x00200000000000000000" ) == true );

    }

    @Test
    public void testGetBitSetFromByteArray() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        // this is 0xee000ff000ff

        byte[] bytes = new byte[6];
        bytes[0] = (byte)0xff;
        bytes[1] = (byte)0x00;
        bytes[2] = (byte)0xf0;
        bytes[3] = (byte)0x0f;
        bytes[4] = (byte)0x00;
        bytes[5] = (byte)0xee;

        BitSet bits = XTCEFunctions.getBitSetFromByteArray( bytes );

        System.out.println( "Checking " + XTCEFunctions.bitSetToHex( bits, 6 ) );

        for ( int iii = 0; iii < bits.size(); ++iii ) {
            if ( ( iii >= 0 ) && ( iii < 8 ) ) {
                Assert.assertTrue( "bits 0 to 7 should be 1",
                                   bits.get( iii ) == true );
            } else if ( ( iii >= 8 ) && ( iii < 16 ) ) {
                Assert.assertTrue( "bits 8 to 15 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 16 ) && ( iii < 20 ) ) {
                Assert.assertTrue( "bits 16 to 19 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 20 ) && ( iii < 24 ) ) {
                Assert.assertTrue( "bits 20 to 23 should be 1",
                                   bits.get( iii ) == true );
            } else if ( ( iii >= 24 ) && ( iii < 28 ) ) {
                Assert.assertTrue( "bits 24 to 27 should be 1",
                                   bits.get( iii ) == true );
            } else if ( ( iii >= 28 ) && ( iii < 32 ) ) {
                Assert.assertTrue( "bits 28 to 31 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 32 ) && ( iii < 40 ) ) {
                Assert.assertTrue( "bits 32 to 39 should be 0",
                                   bits.get( iii ) == false );
            } else if ( iii == 40 ) {
                Assert.assertTrue( "bit 40 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 41 ) && ( iii < 44 ) ) {
                Assert.assertTrue( "bits 41 to 43 should be 1",
                                   bits.get( iii ) == true );
            } else if ( iii == 44 ) {
                Assert.assertTrue( "bit 44 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 45 ) && ( iii < 48 ) ) {
                Assert.assertTrue( "bits 45 to 47 should be 1",
                                   bits.get( iii ) == true );
            }
        }
        
    }

    @Test
    public void testGetStreamByteArrayFromBitSet() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        byte[] bytes = { (byte)0x40, (byte)0x20, (byte)0x00, (byte)0x00, // 2.5
                         (byte)0x00, (byte)0x00,                         // 2 byte gap
                         (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, // 2
                         (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, // 1
                         (byte)0xbf, (byte)0xf8, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // -1.5
                         (byte)0x01 }; // 1=NORMAL

        int minBytes = bytes.length;

        BitSet bits = XTCEFunctions.getBitSetFromStreamByteArray( bytes );
        byte[] bytesOutput = XTCEFunctions.getStreamByteArrayFromBitSet( bits, minBytes );

        if ( Arrays.equals( bytes, bytesOutput ) == false ) {
            Assert.fail( "Could not recover bytes" );
        }

    }

    @Test
    public void testGetBitSetFromStreamByteArray() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        // this is 0xff00f00f00ee (stream order)

        byte[] bytes = new byte[6];
        bytes[0] = (byte)0xff;
        bytes[1] = (byte)0x00;
        bytes[2] = (byte)0xf0;
        bytes[3] = (byte)0x0f;
        bytes[4] = (byte)0x00;
        bytes[5] = (byte)0xee;

        BitSet bits = XTCEFunctions.getBitSetFromStreamByteArray( bytes );

        System.out.println( "Checking " + XTCEFunctions.bitSetToHex( bits, 6 ) );

        for ( int iii = 0; iii < bits.size(); ++iii ) {
            if ( ( iii >= 0 ) && ( iii < 8 ) ) {
                Assert.assertTrue( "bits 0 to 7 should be 1",
                                   bits.get( iii ) == true );
            } else if ( ( iii >= 8 ) && ( iii < 16 ) ) {
                Assert.assertTrue( "bits 8 to 15 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 16 ) && ( iii < 20 ) ) {
                Assert.assertTrue( "bits 16 to 19 should be 1",
                                   bits.get( iii ) == true );
            } else if ( ( iii >= 20 ) && ( iii < 28 ) ) {
                Assert.assertTrue( "bits 20 to 27 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 28 ) && ( iii < 32 ) ) {
                Assert.assertTrue( "bits 28 to 31 should be 1",
                                   bits.get( iii ) == true );
            } else if ( ( iii >= 32 ) && ( iii < 40 ) ) {
                Assert.assertTrue( "bits 32 to 39 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 20 ) && ( iii < 28 ) ) {
                Assert.assertTrue( "bits 20 to 27 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 28 ) && ( iii < 32 ) ) {
                Assert.assertTrue( "bits 28 to 31 should be 1",
                                   bits.get( iii ) == true );
            } else if ( ( iii >= 32 ) && ( iii < 40 ) ) {
                Assert.assertTrue( "bits 32 to 39 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 40 ) && ( iii < 43 ) ) {
                Assert.assertTrue( "bits 40 to 42 should be 1",
                                   bits.get( iii ) == true );
            } else if ( iii == 43 ) {
                Assert.assertTrue( "bit 43 should be 0",
                                   bits.get( iii ) == false );
            } else if ( ( iii >= 44 ) && ( iii < 47 ) ) {
                Assert.assertTrue( "bits 44 to 46 should be 1",
                                   bits.get( iii ) == true );
            } else if ( iii == 47 ) {
                Assert.assertTrue( "bit 47 should be 0",
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
    public void makeAliasDisplayString_ForNoAliasNullPreferred() {

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
                                                      null );

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

    @Test
    public void getCleanHexString() {

        String hex = " \t    0x 12 34 \r 56 \r\n 78 \f 99aabbccdd";

        String fixed = XTCEFunctions.getCleanHexString( hex );

        String expect = "1234567899aabbccdd";

        Assert.assertTrue( "String should be '" + expect + "'",
                           fixed.equals( expect ) == true );

        hex = " \t  12 34 \r 56 \r\n 78 \f 99aabbccdd";

        fixed = XTCEFunctions.getCleanHexString( hex );

        Assert.assertTrue( "String should be '" + expect + "'",
                           fixed.equals( expect ) == true );

    }

    @Test
    public void getBytesFromHexString() {

        String hex   = "123";
        byte[] fixed = XTCEFunctions.getBytesFromHexString( hex );

        if ( fixed[0] != (byte)0x12 ) {
            Assert.fail( "First byte should be 0x12" );
        }
        if ( fixed[1] != (byte)0x30 ) {
            Assert.fail( "Second byte should be 0x30" );
        }

        hex = "1234";
        fixed = XTCEFunctions.getBytesFromHexString( hex );

        if ( fixed[0] != (byte)0x12 ) {
            Assert.fail( "First byte should be 0x12" );
        }
        if ( fixed[1] != (byte)0x34 ) {
            Assert.fail( "Second byte should be 0x34" );
        }

        hex = "1234aabbccdd";
        fixed = XTCEFunctions.getBytesFromHexString( hex );

        if ( fixed[0] != (byte)0x12 ) {
            Assert.fail( "First byte should be 0x12" );
        }
        if ( fixed[1] != (byte)0x34 ) {
            Assert.fail( "Second byte should be 0x34" );
        }
        if ( fixed[2] != (byte)0xaa ) {
            Assert.fail( "Third byte should be 0x12" );
        }
        if ( fixed[3] != (byte)0xbb ) {
            Assert.fail( "Fourth byte should be 0x34" );
        }
        if ( fixed[4] != (byte)0xcc ) {
            Assert.fail( "Fifth byte should be 0x12" );
        }
        if ( fixed[5] != (byte)0xdd ) {
            Assert.fail( "Sixth byte should be 0x34" );
        }

        hex = "1234aabbccdde";
        fixed = XTCEFunctions.getBytesFromHexString( hex );

        if ( fixed[0] != (byte)0x12 ) {
            Assert.fail( "First byte should be 0x12" );
        }
        if ( fixed[1] != (byte)0x34 ) {
            Assert.fail( "Second byte should be 0x34" );
        }
        if ( fixed[2] != (byte)0xaa ) {
            Assert.fail( "Third byte should be 0x12" );
        }
        if ( fixed[3] != (byte)0xbb ) {
            Assert.fail( "Fourth byte should be 0x34" );
        }
        if ( fixed[4] != (byte)0xcc ) {
            Assert.fail( "Fifth byte should be 0x12" );
        }
        if ( fixed[5] != (byte)0xdd ) {
            Assert.fail( "Sixth byte should be 0x34" );
        }
        if ( fixed[6] != (byte)0xe0 ) {
            Assert.fail( "Seventh byte should be 0xe0" );
        }

        try {
            hex          = " \t    0x 12 34 \r 56 \r\n 78 z\f 99aabbccdd";
            hex          = XTCEFunctions.getCleanHexString( hex );
            byte[] bytes = XTCEFunctions.getBytesFromHexString( hex );
        } catch ( NumberFormatException ex ) {
            // expected this, test passes
            return;
        }

        Assert.fail( "Should have gotten an exception with 'z'" );

        hex = "aabb";
        fixed = XTCEFunctions.getBytesFromHexString( hex );

        if ( fixed[0] != (byte)0xaa ) {
            Assert.fail( "First byte should be 0xaa" );
        }
        if ( fixed[1] != (byte)0xbb ) {
            Assert.fail( "Second byte should be 0xbb" );
        }

    }

    @Test
    public void verifyDefaultTimeHandlerExists() {

        try {

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "CUC_GPS_TIME" );

            AbsoluteTimeDataType xml =
                (AbsoluteTimeDataType)parameter.getTypeReference();

            XTCEAbsoluteTimeType handler =
                 XTCEFunctions.getAbsoluteTimeHandler( xml );

            Assert.assertTrue( "Handler should be found for CUC_GPS_TIME",
                               handler.isApplicable( xml ) == true );

        } catch ( Exception ex ) {
            Assert.fail( "Default CCSDS time handler not registered: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void verifyNoDefaultTimeHandlerExists() {

        try {

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "ODD_TIME" );

            AbsoluteTimeDataType xml =
                (AbsoluteTimeDataType)parameter.getTypeReference();

            XTCEAbsoluteTimeType handler =
                 XTCEFunctions.getAbsoluteTimeHandler( xml );

            Assert.fail( "Handler should NOT be found for ODD_TIME" );

        } catch ( Exception ex ) {
            // expect exception
        }

    }

    @Test
    public void verifyNewTimeHandlerRegistration() {

        try {

            XTCEAbsoluteTimeType instance = new XTCECcsdsCucTimeHandler();

            XTCEFunctions.registerAbsoluteTimeHandler( instance );

        } catch ( Exception ex ) {
            Assert.fail( "Handler registration should not throw: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void getMemoryUsageStatistics() {

        // not sure how to test this than to otherwise call it...
        String text = XTCEFunctions.getMemoryUsageStatistics();
        System.out.println( text );

    }
    
    // cannot easily do the xmlPrettyPrint and getMemoryUsageStatistics
    // functions right now.  The formatMemoryQuantity is not public.

    private XTCEDatabase db_ = null;

}
