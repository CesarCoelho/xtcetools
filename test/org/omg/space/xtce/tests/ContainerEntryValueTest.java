/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.tests;

import java.io.File;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.space.xtce.toolkit.XTCEContainerEntryValue;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEDatabaseException;
import org.omg.space.xtce.toolkit.XTCEParameter;

/**
 *
 * @author dovereem
 */

public class ContainerEntryValueTest {

    public ContainerEntryValueTest() {

        try {
            loadDocument();
        } catch ( Throwable ex ) {
            Assert.fail( "Cannot start test: " + ex.getLocalizedMessage() );
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
    public void checkFixedValue() {

        try {

            XTCEContainerEntryValue valueObj =
                new XTCEContainerEntryValue( "6" );

            Assert.assertTrue( "Fixed value name should be empty",
                               valueObj.getName().isEmpty() == true );

            Assert.assertTrue( "Fixed value full path name should be empty",
                               valueObj.getItemFullPath().isEmpty() == true );

            Assert.assertTrue( "Fixed value should be 6",
                               valueObj.getAssignedValue().equals( "6" ) == true );

            Assert.assertTrue( "Fixed value Operator should be ==",
                               valueObj.getOperator().equals( "==" ) == true );

            Assert.assertTrue( "Fixed value Form should be Calibrated",
                               valueObj.getComparisonForm().equals( "Calibrated" ) == true );

            Assert.assertTrue( "Fixed value display string should be ==6{cal}",
                               valueObj.toStringWithoutParameter().equals( "==6{cal}" ) == true );

            Assert.assertTrue( "Fixed value full string should be ==6{cal}",
                               valueObj.toString().equals( "==6{cal}" ) == true );

        } catch ( Exception ex ) {
            Assert.fail( "Exception: " + ex.getLocalizedMessage() );
        }

    }

    @Test
    public void checkCalibratedEqualityValue() {

        String paramName = "Payload_1_Phase";

        try {

            List<XTCEParameter> parameters =
                db_.getTelemetryParameters( paramName );

            if ( parameters.size() != 1 ) {
                Assert.fail( "Test not well posed, there should be 1 " +
                    "TM Parameter of '" + paramName + "', but found " +
                    Long.toString( parameters.size() ) );
            }

            XTCEParameter parameter = parameters.get( 0 );

            XTCEContainerEntryValue valueObj =
                new XTCEContainerEntryValue( parameter,
                                             "ACTIVE",
                                             "==",
                                             "Calibrated" );

            Assert.assertTrue( paramName + " name should be " + paramName,
                               valueObj.getName().equals( paramName ) == true );

            Assert.assertTrue( paramName + " name should be /BogusSAT/SC001/Payload1/" + paramName,
                               valueObj.getItemFullPath().equals( "/BogusSAT/SC001/Payload1/" + paramName ) == true );

            Assert.assertTrue( paramName + " EU/Cal value should be ACTIVE",
                               valueObj.getAssignedValue().equals( "ACTIVE" ) == true );

            Assert.assertTrue( paramName + " Operator should be ==",
                               valueObj.getOperator().equals( "==" ) == true );

            Assert.assertTrue( paramName + " Form should be Calibrated",
                               valueObj.getComparisonForm().equals( "Calibrated" ) == true );

            Assert.assertTrue( paramName + " display string should be ==ACTIVE{cal}",
                               valueObj.toStringWithoutParameter().equals( "==ACTIVE{cal}" ) == true );

            Assert.assertTrue( paramName + " full string should be " + paramName + "==ACTIVE{cal}",
                               valueObj.toString().equals( paramName + "==ACTIVE{cal}" ) == true );

        } catch ( Exception ex ) {
            Assert.fail( "Exception: " + ex.getLocalizedMessage() );
        }

    }

    @Test
    public void checkCalibratedInequalityValue() {

        String paramName = "Payload_1_Phase";

        try {

            List<XTCEParameter> parameters =
                db_.getTelemetryParameters( paramName );

            if ( parameters.size() != 1 ) {
                Assert.fail( "Test not well posed, there should be 1 " +
                    "TM Parameter of '" + paramName + "', but found " +
                    Long.toString( parameters.size() ) );
            }

            XTCEParameter parameter = parameters.get( 0 );

            XTCEContainerEntryValue valueObj =
                new XTCEContainerEntryValue( parameter,
                                             "STANDBY",
                                             "!=",
                                             "Calibrated" );

            Assert.assertTrue( paramName + " name should be " + paramName,
                               valueObj.getName().equals( paramName ) == true );

            Assert.assertTrue( paramName + " name should be /BogusSAT/SC001/Payload1/" + paramName,
                               valueObj.getItemFullPath().equals( "/BogusSAT/SC001/Payload1/" + paramName ) == true );

            Assert.assertTrue( paramName + " EU/Cal value should be STANDBY",
                               valueObj.getAssignedValue().equals( "STANDBY" ) == true );

            Assert.assertTrue( paramName + " Operator should be !=",
                               valueObj.getOperator().equals( "!=" ) == true );

            Assert.assertTrue( paramName + " Form should be Calibrated",
                               valueObj.getComparisonForm().equals( "Calibrated" ) == true );

            Assert.assertTrue( paramName + " display string should be !=STANDBY{cal}",
                               valueObj.toStringWithoutParameter().equals( "!=STANDBY{cal}" ) == true );

            Assert.assertTrue( paramName + " full string should be " + paramName + "!=STANDBY{cal}",
                               valueObj.toString().equals( paramName + "!=STANDBY{cal}" ) == true );

        } catch ( Exception ex ) {
            Assert.fail( "Exception: " + ex.getLocalizedMessage() );
        }

    }

    @Test
    public void checkUncalibratedEqualityValue() {

        String paramName = "Payload_1_Phase";

        try {

            List<XTCEParameter> parameters =
                db_.getTelemetryParameters( paramName );

            if ( parameters.size() != 1 ) {
                Assert.fail( "Test not well posed, there should be 1 " +
                    "TM Parameter of '" + paramName + "', but found " +
                    Long.toString( parameters.size() ) );
            }

            XTCEParameter parameter = parameters.get( 0 );

            XTCEContainerEntryValue valueObj =
                new XTCEContainerEntryValue( parameter,
                                             "4",
                                             "==",
                                             "Uncalibrated" );

            Assert.assertTrue( paramName + " name should be " + paramName,
                               valueObj.getName().equals( paramName ) == true );

            Assert.assertTrue( paramName + " name should be /BogusSAT/SC001/Payload1/" + paramName,
                               valueObj.getItemFullPath().equals( "/BogusSAT/SC001/Payload1/" + paramName ) == true );

            Assert.assertTrue( paramName + " EU/Cal value should be 4",
                               valueObj.getAssignedValue().equals( "4" ) == true );

            Assert.assertTrue( paramName + " Operator should be ==",
                               valueObj.getOperator().equals( "==" ) == true );

            Assert.assertTrue( paramName + " Form should be Uncalibrated",
                               valueObj.getComparisonForm().equals( "Uncalibrated" ) == true );

            Assert.assertTrue( paramName + " display string should be ==4{uncal}",
                               valueObj.toStringWithoutParameter().equals( "==4{uncal}" ) == true );

            Assert.assertTrue( paramName + " full string should be " + paramName + "==4{uncal}",
                               valueObj.toString().equals( paramName + "==4{uncal}" ) == true );

        } catch ( Exception ex ) {
            Assert.fail( "Exception: " + ex.getLocalizedMessage() );
        }

    }

    @Test
    public void checkUncalibratedInequalityValue() {

        String paramName = "Payload_1_Phase";

        try {

            List<XTCEParameter> parameters =
                db_.getTelemetryParameters( paramName );

            if ( parameters.size() != 1 ) {
                Assert.fail( "Test not well posed, there should be 1 " +
                    "TM Parameter of '" + paramName + "', but found " +
                    Long.toString( parameters.size() ) );
            }

            XTCEParameter parameter = parameters.get( 0 );

            XTCEContainerEntryValue valueObj =
                new XTCEContainerEntryValue( parameter,
                                             "3",
                                             "!=",
                                             "Uncalibrated" );

            Assert.assertTrue( paramName + " name should be " + paramName,
                               valueObj.getName().equals( paramName ) == true );

            Assert.assertTrue( paramName + " name should be /BogusSAT/SC001/Payload1/" + paramName,
                               valueObj.getItemFullPath().equals( "/BogusSAT/SC001/Payload1/" + paramName ) == true );

            Assert.assertTrue( paramName + " EU/Cal value should be 3",
                               valueObj.getAssignedValue().equals( "3" ) == true );

            Assert.assertTrue( paramName + " Operator should be !=",
                               valueObj.getOperator().equals( "!=" ) == true );

            Assert.assertTrue( paramName + " Form should be Uncalibrated",
                               valueObj.getComparisonForm().equals( "Uncalibrated" ) == true );

            Assert.assertTrue( paramName + " display string should be !=3{uncal}",
                               valueObj.toStringWithoutParameter().equals( "!=3{uncal}" ) == true );

            Assert.assertTrue( paramName + " full string should be " + paramName + "!=3{uncal}",
                               valueObj.toString().equals( paramName + "!=3{uncal}" ) == true );

        } catch ( Exception ex ) {
            Assert.fail( "Exception: " + ex.getLocalizedMessage() );
        }

    }

    @Test
    public void checkEquality() {

        String paramName1 = "Payload_1_Phase";
        String paramName2 = "Payload_1_State";

        List<XTCEParameter> parameters1;
        List<XTCEParameter> parameters2;

        try {

            parameters1 = db_.getTelemetryParameters( paramName1 );
            parameters2 = db_.getTelemetryParameters( paramName2 );

            if ( parameters1.size() != 1 ) {
                Assert.fail( "Test not well posed, there should be 1 " +
                    "TM Parameter of '" + paramName1 + "', but found " +
                    Long.toString( parameters1.size() ) );
            }

            if ( parameters2.size() != 1 ) {
                Assert.fail( "Test not well posed, there should be 1 " +
                    "TM Parameter of '" + paramName2 + "', but found " +
                    Long.toString( parameters2.size() ) );
            }

            XTCEParameter parameter1 = parameters1.get( 0 );
            XTCEParameter parameter2 = parameters2.get( 0 );

            XTCEContainerEntryValue valueObj1 =
                new XTCEContainerEntryValue( parameter1,
                                             "ACTIVE",
                                             "==",
                                             "Calibrated" );

            Assert.assertTrue( "Equality of same object should be true",
                               valueObj1.equals( valueObj1 ) == true );

            XTCEContainerEntryValue valueObj2 =
                new XTCEContainerEntryValue( parameter1,
                                             "STANDBY",
                                             "==",
                                             "Calibrated" );

            Assert.assertTrue( "Equality of same parameter different value objects should be false",
                               valueObj1.equals( valueObj2 ) == false );

            XTCEContainerEntryValue valueObj3 =
                new XTCEContainerEntryValue( parameter2,
                                             "ACTIVE",
                                             "==",
                                             "Calibrated" );

            Assert.assertTrue( "Equality of different parameter same value objects should be false",
                               valueObj1.equals( valueObj3 ) == false );

            XTCEContainerEntryValue valueObj5 =
                new XTCEContainerEntryValue( parameter1,
                                             "ACTIVE",
                                             "!=",
                                             "Calibrated" );

            Assert.assertTrue( "Equality of same parameter different compare objects should be false",
                               valueObj1.equals( valueObj5 ) == false );

            XTCEContainerEntryValue valueObj7 =
                new XTCEContainerEntryValue( parameter1,
                                             "ACTIVE",
                                             "==",
                                             "Uncalibrated" );

            Assert.assertTrue( "Equality of same parameter different form objects should be false",
                               valueObj1.equals( valueObj7 ) == false );

        } catch ( Exception ex ) {
            Assert.fail( "Exception: " + ex.getLocalizedMessage() );
        }

    }

    @Test
    public void checkCompatibility() {

        String paramName1 = "Payload_1_Phase";
        String paramName2 = "Payload_1_State";

        List<XTCEParameter> parameters1;
        List<XTCEParameter> parameters2;

        try {

            parameters1 = db_.getTelemetryParameters( paramName1 );
            parameters2 = db_.getTelemetryParameters( paramName2 );

            if ( parameters1.size() != 1 ) {
                Assert.fail( "Test not well posed, there should be 1 " +
                    "TM Parameter of '" + paramName1 + "', but found " +
                    Long.toString( parameters1.size() ) );
            }

            if ( parameters2.size() != 1 ) {
                Assert.fail( "Test not well posed, there should be 1 " +
                    "TM Parameter of '" + paramName2 + "', but found " +
                    Long.toString( parameters2.size() ) );
            }

            XTCEParameter parameter1 = parameters1.get( 0 );
            XTCEParameter parameter2 = parameters2.get( 0 );

            XTCEContainerEntryValue valueObj1 =
                new XTCEContainerEntryValue( parameter1,
                                             "ACTIVE",
                                             "==",
                                             "Calibrated" );

            Assert.assertTrue( "Compatibility of same object should be true",
                               valueObj1.isCompatibleWith( valueObj1 ) == true );

            XTCEContainerEntryValue valueObj2 =
                new XTCEContainerEntryValue( parameter1,
                                             "STANDBY",
                                             "==",
                                             "Calibrated" );

            Assert.assertTrue( "Compatibility of same parameter different value objects should be false",
                               valueObj1.isCompatibleWith( valueObj2 ) == false );

            XTCEContainerEntryValue valueObj3 =
                new XTCEContainerEntryValue( parameter2,
                                             "ACTIVE",
                                             "==",
                                             "Calibrated" );

            Assert.assertTrue( "Compatibility of different parameter same value objects should be false",
                               valueObj1.isCompatibleWith( valueObj3 ) == false );

            XTCEContainerEntryValue valueObj5 =
                new XTCEContainerEntryValue( parameter1,
                                             "ACTIVE",
                                             "!=",
                                             "Calibrated" );

            Assert.assertTrue( "Compatibility of same parameter different compare objects should be false",
                               valueObj1.isCompatibleWith( valueObj5 ) == false );

            XTCEContainerEntryValue valueObj7 =
                new XTCEContainerEntryValue( parameter1,
                                             "4",
                                             "==",
                                             "Uncalibrated" );

            Assert.assertTrue( "Compatibility of same parameter different form objects should be true (1)",
                               valueObj1.isCompatibleWith( valueObj7 ) == true );

            Assert.assertTrue( "Compatibility of same parameter different form objects should be true (2)",
                               valueObj7.isCompatibleWith( valueObj1 ) == true );

            XTCEContainerEntryValue valueObj8 =
                new XTCEContainerEntryValue( parameter1,
                                             "STANDBY",
                                             "!=",
                                             "Calibrated" );


            Assert.assertTrue( "Compatibility of same parameter different value objects with != should be true (1)",
                               valueObj1.isCompatibleWith( valueObj7 ) == true );

            Assert.assertTrue( "Compatibility of same parameter different value objects with != should be true (2)",
                               valueObj7.isCompatibleWith( valueObj1 ) == true );

        } catch ( Exception ex ) {
            Assert.fail( "Exception: " + ex.getLocalizedMessage() );
        }

    }

    private void loadDocument() throws XTCEDatabaseException {

        //System.out.println( "Loading the BogusSat-2.xml demo database" );

        String file = "src/org/omg/space/xtce/database/BogusSat-2.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;

}
