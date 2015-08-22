/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

/**
 *
 * @author dovereem
 */

public class AliasLogicTest {
    
    public AliasLogicTest() {

        String file = "src/org/omg/space/xtce/database/UnitTests.xml";

        try {
           db_ = new XTCEDatabase( new File( file ), false, false, null );
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
    public void testSingleAliasOnParameter() {

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

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_NO_ALIAS" );

            List<XTCEAlias> aliases = parameter.getAliasSet();

            Assert.assertTrue( "Expected 0 aliases",
                               aliases.size() == 0 );

            String alias = parameter.getAlias( "nobody" );

            Assert.assertTrue( "Expected an empty string but got " + alias,
                               alias.isEmpty() == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    private XTCEDatabase db_ = null;

}
