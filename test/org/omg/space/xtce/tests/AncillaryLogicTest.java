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
import org.omg.space.xtce.database.DescriptionType.AncillaryDataSet.AncillaryData;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEParameter;
import org.omg.space.xtce.toolkit.XTCESpaceSystem;

/**
 *
 * @author dovereem
 */
public class AncillaryLogicTest {
    
    public AncillaryLogicTest() {

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
    public void testNoAncDataOnParameter() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_NO_ANCDATA" );

            List<AncillaryData> ancData = parameter.getAncillaryData();

            Assert.assertTrue( "Expected 0 AncillaryData elements",
                               ancData.isEmpty() == true );

            List<AncillaryData> ancDataByName =
                parameter.getAncillaryData( "nobody" );

            Assert.assertTrue( "Expected 0 AncillaryData elements for pattern",
                               ancDataByName.isEmpty() == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testNoAncDataOnSpaceSystem() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCESpaceSystem ss = db_.getRootSpaceSystem();

            List<AncillaryData> ancData = ss.getAncillaryData();

            Assert.assertTrue( "Expected 0 AncillaryData elements",
                               ancData.isEmpty() == true );

            List<AncillaryData> ancDataByName =
                ss.getAncillaryData( "nobody" );

            Assert.assertTrue( "Expected 0 AncillaryData elements for pattern",
                               ancDataByName.isEmpty() == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParamOnlyAncData() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_PONLY_ANCDATA" );

            List<AncillaryData> ancData = parameter.getAncillaryData();

            Assert.assertTrue( "Expected 3 AncillaryData elements",
                               ancData.size() == 3 );

            List<AncillaryData> ancDataByName = null;
            String              name          = null;
            String              value         = null;

            ancDataByName = parameter.getAncillaryData( "nobody" );

            Assert.assertTrue( "Expected 0 AncillaryData elements for pattern 'nobody' in name",
                               ancDataByName.isEmpty() == true );

            ancDataByName = parameter.getAncillaryData( "test6" );

            Assert.assertTrue( "Expected 1 AncillaryData elements",
                               ancDataByName.size() == 1 );

            name  = ancDataByName.get( 0 ).getName();
            value = ancDataByName.get( 0 ).getValue();

            Assert.assertTrue( "Expected 'test6' for name attribute but got '" + name + "'",
                               name.equals( "test6" ) == true );
            Assert.assertTrue( "Expected 'value6' for value content but got '" + value + "'",
                               value.equals( "value6" ) == true );

            ancDataByName = parameter.getAncillaryData( "test5" );

            Assert.assertTrue( "Expected 1 AncillaryData elements",
                               ancDataByName.size() == 1 );

            name  = ancDataByName.get( 0 ).getName();
            value = ancDataByName.get( 0 ).getValue();

            Assert.assertTrue( "Expected 'test5' for name attribute but got '" + name + "'",
                               name.equals( "test5" ) == true );
            Assert.assertTrue( "Expected '' for value content but got '" + value + "'",
                               value.isEmpty() == true );

            ancDataByName = parameter.getAncillaryData( "test?" );

            Assert.assertTrue( "Expected 2 AncillaryData elements with 'test?'",
                               ancDataByName.size() == 2 );

            ancDataByName = parameter.getAncillaryData( "test*" );

            Assert.assertTrue( "Expected 2 AncillaryData elements with 'test*'",
                               ancDataByName.size() == 2 );

            ancDataByName = parameter.getAncillaryData( "*" );

            Assert.assertTrue( "Expected 3 AncillaryData elements with '*'",
                               ancDataByName.size() == 3 );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParamTypeOnlyAncData() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_TYPEONLY_ANCDATA" );

            List<AncillaryData> ancData = parameter.getAncillaryData();

            Assert.assertTrue( "Expected 3 AncillaryData elements",
                               ancData.size() == 3 );

            List<AncillaryData> ancDataByName = null;
            String              name          = null;
            String              value         = null;

            ancDataByName = parameter.getAncillaryData( "nobody" );

            Assert.assertTrue( "Expected 0 AncillaryData elements for pattern 'nobody' in name",
                               ancDataByName.isEmpty() == true );

            ancDataByName = parameter.getAncillaryData( "test2" );

            Assert.assertTrue( "Expected 1 AncillaryData elements",
                               ancDataByName.size() == 1 );

            name  = ancDataByName.get( 0 ).getName();
            value = ancDataByName.get( 0 ).getValue();

            Assert.assertTrue( "Expected 'test2' for name attribute but got '" + name + "'",
                               name.equals( "test2" ) == true );
            Assert.assertTrue( "Expected 'value2' for value content but got '" + value + "'",
                               value.equals( "value2" ) == true );

            ancDataByName = parameter.getAncillaryData( "test1" );

            Assert.assertTrue( "Expected 1 AncillaryData elements",
                               ancDataByName.size() == 1 );

            name  = ancDataByName.get( 0 ).getName();
            value = ancDataByName.get( 0 ).getValue();

            Assert.assertTrue( "Expected 'test1' for name attribute but got '" + name + "'",
                               name.equals( "test1" ) == true );
            Assert.assertTrue( "Expected '' for value content but got '" + value + "'",
                               value.isEmpty() == true );

            ancDataByName = parameter.getAncillaryData( "test?" );

            Assert.assertTrue( "Expected 2 AncillaryData elements with 'test?'",
                               ancDataByName.size() == 2 );

            ancDataByName = parameter.getAncillaryData( "test*" );

            Assert.assertTrue( "Expected 2 AncillaryData elements with 'test*'",
                               ancDataByName.size() == 2 );

            ancDataByName = parameter.getAncillaryData( "*" );

            Assert.assertTrue( "Expected 3 AncillaryData elements with '*'",
                               ancDataByName.size() == 3 );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParamMixedAncData() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( "INT_MIXED_ANCDATA" );

            List<AncillaryData> ancData = parameter.getAncillaryData();

            Assert.assertTrue( "Expected 6 AncillaryData elements",
                               ancData.size() == 6 );

            List<AncillaryData> ancDataByName = null;
            String              name          = null;
            String              value         = null;

            ancDataByName = parameter.getAncillaryData( "nobody" );

            Assert.assertTrue( "Expected 0 AncillaryData elements for pattern 'nobody' in name",
                               ancDataByName.isEmpty() == true );

            ancDataByName = parameter.getAncillaryData( "" );

            Assert.assertTrue( "Expected 2 AncillaryData elements for pattern ''",
                               ancDataByName.size() == 2 );

            boolean found_value1 = false;
            boolean found_value4 = false;

            for ( AncillaryData data : ancDataByName ) {
                if ( data.getValue().equals( "value1" ) == true ) {
                    found_value1 = true;
                } else if ( data.getValue().equals( "value4" ) == true ) {
                    found_value4 = true;
                }
            }

            Assert.assertTrue( "Did not find value1 and value4",
                               ( found_value1 && found_value4 ) == true );
            
            ancDataByName = parameter.getAncillaryData( "test2" );

            Assert.assertTrue( "Expected 1 AncillaryData elements",
                               ancDataByName.size() == 1 );

            name  = ancDataByName.get( 0 ).getName();
            value = ancDataByName.get( 0 ).getValue();

            Assert.assertTrue( "Expected 'test2' for name attribute but got '" + name + "'",
                               name.equals( "test2" ) == true );
            Assert.assertTrue( "Expected 'value2' for value content but got '" + value + "'",
                               value.equals( "value2" ) == true );

            ancDataByName = parameter.getAncillaryData( "test1" );

            Assert.assertTrue( "Expected 1 AncillaryData elements",
                               ancDataByName.size() == 1 );

            name  = ancDataByName.get( 0 ).getName();
            value = ancDataByName.get( 0 ).getValue();

            Assert.assertTrue( "Expected 'test1' for name attribute but got '" + name + "'",
                               name.equals( "test1" ) == true );
            Assert.assertTrue( "Expected '' for value content but got '" + value + "'",
                               value.isEmpty() == true );

            ancDataByName = parameter.getAncillaryData( "test?" );

            Assert.assertTrue( "Expected 4 AncillaryData elements with 'test?'",
                               ancDataByName.size() == 4 );

            ancDataByName = parameter.getAncillaryData( "test*" );

            Assert.assertTrue( "Expected 4 AncillaryData elements with 'test*'",
                               ancDataByName.size() == 4 );

            ancDataByName = parameter.getAncillaryData( "*" );

            Assert.assertTrue( "Expected 6 AncillaryData elements with '*'",
                               ancDataByName.size() == 6 );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testSpaceSystemAncData() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCESpaceSystem ss =
                db_.getSpaceSystem( "/UNIT_TEST/ANCDATA_SS_TEST" );

            List<AncillaryData> ancData = ss.getAncillaryData();

            Assert.assertTrue( "Expected 4 AncillaryData elements",
                               ancData.size() == 4 );

            List<AncillaryData> ancDataByName = null;
            String              name          = null;
            String              value         = null;

            ancDataByName = ss.getAncillaryData( "nobody" );

            Assert.assertTrue( "Expected 0 AncillaryData elements for pattern 'nobody'",
                               ancDataByName.isEmpty() == true );

            ancDataByName = ss.getAncillaryData( "bar" );

            Assert.assertTrue( "Expected 1 AncillaryData elements",
                               ancDataByName.size() == 1 );

            name  = ancDataByName.get( 0 ).getName();
            value = ancDataByName.get( 0 ).getValue();

            Assert.assertTrue( "Expected 'bar' for name attribute but got '" + name + "'",
                               name.equals( "bar" ) == true );
            Assert.assertTrue( "Expected 'value12' for value content but got '" + value + "'",
                               value.equals( "value12" ) == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    private XTCEDatabase db_ = null;

}
