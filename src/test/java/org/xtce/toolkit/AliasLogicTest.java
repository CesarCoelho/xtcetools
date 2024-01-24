/* Copyright 2015 David Overeem (dovereem@startmail.com)
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
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author dovereem
 */

public class AliasLogicTest {
    
    public AliasLogicTest() {

        String file = "src/test/resources/org/xtce/toolkit/test/UnitTests.xml";

        try {
           db_ = new XTCEDatabase( new File( file ), false, false, true );
        } catch ( Exception ex ) {
            ex.printStackTrace();
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
    public void testSingleAliasOnParameter() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_WITH_ALIAS" );

            List<XTCEAlias> aliases = parameter.getAliasSet();

            Assert.assertTrue( aliases.size() == 1 );

            XTCEAlias alias = aliases.get( 0 );

            Assert.assertTrue( alias.getAliasName().equals( "INT_WITH_ALIAS_2" ) == true );
            Assert.assertTrue( alias.getNameSpace().equals( "foo" ) == true );
            
        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testAliasesOnParameterType() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_WITHOUT_ALIAS" );

            List<XTCEAlias> aliases = parameter.getAliasSet();

            Assert.assertTrue( aliases.size() == 2 );

            String alias1 = parameter.getAlias( "foo" );
            String alias2 = parameter.getAlias( "bar" );

            Assert.assertTrue( alias1.equals( "INTTYPE_WITH_ALIAS" ) == true );
            Assert.assertTrue( alias2.equals( "INTTYPE_WITH_SECOND_ALIAS" ) == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testMixedAliases() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_WITH_MIXED_ALIAS" );

            List<XTCEAlias> aliases = parameter.getAliasSet();

            Assert.assertTrue( aliases.size() == 2 );

            String alias1 = parameter.getAlias( "foo" );
            String alias2 = parameter.getAlias( "bar" );

            Assert.assertTrue( alias1.equals( "INT_WITH_ALIAS_2" ) == true );
            Assert.assertTrue( alias2.equals( "INTTYPE_WITH_SECOND_ALIAS" ) == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testNoAliasParameter() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_NO_ALIAS" );

            List<XTCEAlias> aliases = parameter.getAliasSet();

            Assert.assertTrue( "Expected 0 aliases",
                               aliases.isEmpty() == true );

            String alias = parameter.getAlias( "nobody" );

            Assert.assertTrue( "Expected an empty string but got " + alias,
                               alias.isEmpty() == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testNoAliasSpaceSystem() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCESpaceSystem ss = db_.getRootSpaceSystem();

            List<XTCEAlias> aliases = ss.getAliasSet();

            Assert.assertTrue( "Expected 0 aliases",
                               aliases.isEmpty() == true );

            String alias = ss.getAlias( "nobody" );

            Assert.assertTrue( "Expected 0 aliases for pattern",
                               alias.isEmpty() == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testAliasComparison() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter1 = db_.
                                       getRootSpaceSystem().
                                       getTelemetryParameter( "INT_WITH_MIXED_ALIAS" );

            List<XTCEAlias> aliases1 = parameter1.getAliasSet();

            XTCEParameter parameter2 = db_.
                                       getRootSpaceSystem().
                                       getTelemetryParameter( "INT_WITH_ALIAS" );

            List<XTCEAlias> aliases2 = parameter2.getAliasSet();

            System.out.println( "Alias 1a: " + aliases1.get( 0 ).getFullAliasName() );
            System.out.println( "Alias 1b: " + aliases1.get( 1 ).getFullAliasName() );
            System.out.println( "Alias 2a: " + aliases2.get( 0 ).getFullAliasName() );

            Assert.assertFalse( "Aliases 1a and 2a should NOT be equal",
                                aliases1.get( 1 ).equals( aliases2.get( 0 ) ) );

            Assert.assertTrue( "Aliases 1a and 2a should be equal",
                               aliases1.get( 0 ).equals( aliases2.get( 0 ) ) );

            int hash1 = aliases1.get( 0 ).hashCode();
            int hash2 = aliases2.get( 0 ).hashCode();
            int hash3 = aliases1.get( 1 ).hashCode();

            System.out.println( "Hash of 1a: " + Integer.toString( hash1 ) );
            System.out.println( "Hash of 2a: " + Integer.toString( hash2 ) );
            System.out.println( "Hash of 1b: " + Integer.toString( hash3 ) );

            Assert.assertTrue( "Hashes for 1a and 2a should be equal",
                               hash1 == hash2 );

            Assert.assertFalse( "Hashes for 1b and 2a should NOT be equal",
                                hash3 == hash2 );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testAliasEqualityForOtherObjects() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter1 = db_.
                                       getRootSpaceSystem().
                                       getTelemetryParameter( "INT_WITH_MIXED_ALIAS" );

            List<XTCEAlias> aliases1 = parameter1.getAliasSet();

            Assert.assertTrue( "Alias object equals self is true",
                               aliases1.get( 1 ).equals( aliases1.get( 1 ) ) );

            Long myNumber = Long.valueOf("1" );

            Assert.assertFalse( "Alias object equals another object is false",
                                aliases1.get( 1 ).equals( myNumber ) );

            Assert.assertFalse( "Alias object equals null is false",
                                aliases1.get( 1 ).equals( null ) );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    private XTCEDatabase db_ = null;

}
