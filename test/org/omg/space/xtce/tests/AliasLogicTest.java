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
import org.omg.space.xtce.toolkit.XTCEAlias;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEParameter;
import org.omg.space.xtce.toolkit.XTCESpaceSystem;

/**
 *
 * @author dovereem
 */

public class AliasLogicTest {
    
    public AliasLogicTest() {

        String file = "src/org/omg/space/xtce/database/UnitTests.xml";

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

    private XTCEDatabase db_ = null;

}
