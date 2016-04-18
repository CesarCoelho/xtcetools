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
import java.math.BigInteger;
import java.util.BitSet;
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

public class PosixTimeHandlerTest {
    
    public PosixTimeHandlerTest() {

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

    /**
     * Test of isApplicable method, of class XTCEPosixTimeHandler.
     */

    @Test
    public void testIsApplicable() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCEParameter        parameter;
        AbsoluteTimeDataType xml;

        try {

            XTCEAbsoluteTimeType instance = new XTCEPosixTimeHandler();

            parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "ODD_TIME" );

            xml = (AbsoluteTimeDataType)parameter.getTypeReference();

            Assert.assertTrue( "Handler should not be found for ODD_TIME",
                               instance.isApplicable( xml ) == false );

            parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "UNIX_TIME" );

            xml = (AbsoluteTimeDataType)parameter.getTypeReference();

            Assert.assertTrue( "Handler should be found for UNIX_TIME",
                               instance.isApplicable( xml ) == true );

            Assert.assertTrue( "Handler should not be found for NULL",
                               instance.isApplicable( null ) == false );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    /**
     * Test of getUncalibratedFromRaw method, of class XTCEPosixTimeHandler.
     */

    @Test
    public void testGetUncalibratedFromRaw() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String     uncalValue;
            BigInteger uncalTime;

            XTCEAbsoluteTimeType instance = new XTCEPosixTimeHandler();

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "UNIX_TIME" );

            System.out.println( "Testing Parameter UNIX_TIME" );

            uncalValue = instance.getUncalibratedFromRaw( new BitSet( 64 ) );
            uncalTime  = new BigInteger( uncalValue.replaceFirst( "0x", "" ), 16 );

            System.out.println( "Uncal Time: 0x" + uncalTime.toString( 16 ) );

            Assert.assertTrue( "Raw Bits of 0 should be 0 uncalibrated",
                               uncalTime.compareTo( BigInteger.ZERO ) == 0 );

            byte[] bytes1 = { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                              (byte)0xff, (byte)0xee, (byte)0x00, (byte)0x00 };

            BitSet aTime = XTCEFunctions.getBitSetFromByteArray( bytes1 );

            uncalValue = instance.getUncalibratedFromRaw( aTime );

            System.out.println( "BitSet: " + XTCEFunctions.bitSetToHex( aTime, 8 ) );
            System.out.println( "Uncal Time: " + uncalValue );

            Assert.assertTrue( "Time should be 0xe3eca7dc0",
                               uncalValue.equals( "0xe3eca7dc0" ) == true );

            byte[] bytes2 = { (byte)0xcc, (byte)0xdd, (byte)0x00, (byte)0x00,
                              (byte)0xff, (byte)0xee, (byte)0x00, (byte)0x00 };

            BitSet bTime = XTCEFunctions.getBitSetFromByteArray( bytes2 );

            uncalValue = instance.getUncalibratedFromRaw( bTime );

            System.out.println( "BitSet: " + XTCEFunctions.bitSetToHex( bTime, 8 ) );
            System.out.println( "Uncal Time: " + uncalValue );

            Assert.assertTrue( "Time should be 0xe3ecb5b8c",
                               uncalValue.equals( "0xe3ecb5b8c" ) == true );

            byte[] bytes3 = { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                              (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff };

            BitSet cTime = XTCEFunctions.getBitSetFromByteArray( bytes3 );

            uncalValue = instance.getUncalibratedFromRaw( cTime );

            System.out.println( "BitSet: " + XTCEFunctions.bitSetToHex( cTime, 8 ) );
            System.out.println( "Uncal Time: " + uncalValue );

            Assert.assertTrue( "Time should be 0xf423ffff0bdc0",
                               uncalValue.equals( "0xf423ffff0bdc0" ) == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getClass().getName() + ": " + ex.getLocalizedMessage() );
        }

    }

    /**
     * Test of getCalibratedFromUncalibrated method, of class XTCEPosixTimeHandler.
     */

    @Test
    public void testGetCalibratedFromUncalibrated() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String     uncalValue;
            String     calValue;
            BigInteger uncalTime;

            XTCEAbsoluteTimeType instance = new XTCEPosixTimeHandler();

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "UNIX_TIME" );

            System.out.println( "Testing Parameter UNIX_TIME" );

            uncalValue = "0x00";
            calValue = instance.getCalibratedFromUncalibrated( uncalValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Cal time should be 1970-01-01 00:00:00.000, but is " + calValue,
                               calValue.equals( "1970-01-01 00:00:00.000" ) == true );

            uncalValue = "0xe3eca7dc0";
            calValue = instance.getCalibratedFromUncalibrated( uncalValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Cal time should be 1970-01-01 16:59:43.000, but is " + calValue,
                               calValue.equals( "1970-01-01 16:59:43.000" ) == true );

            uncalValue = "0xe3ecb5b8c";
            calValue = instance.getCalibratedFromUncalibrated( uncalValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Cal time should be 1970-01-01 16:59:43.056, but is " + calValue,
                               calValue.equals( "1970-01-01 16:59:43.056" ) == true );

            uncalValue = "0x7a11ffff0bdc0";
            calValue = instance.getCalibratedFromUncalibrated( uncalValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Cal time should be 2038-01-19 03:14:07.000, but is " + calValue,
                               calValue.equals( "2038-01-19 03:14:07.000" ) == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getClass().getName() + ": " + ex.getLocalizedMessage() );
        }

    }

    /**
     * Test of getRawFromUncalibrated method, of class XTCEPosixTimeHandler.
     */

    @Test
    public void testGetRawFromUncalibrated() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCEAbsoluteTimeType instance = new XTCEPosixTimeHandler();

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "UNIX_TIME" );

            System.out.println( "Testing Parameter UNIX_TIME" );

            BitSet bits;
            String hex;
            String uncalValue;

            uncalValue = "0x00";
            bits = instance.getRawFromUncalibrated( uncalValue );
            hex = XTCEFunctions.bitSetToHex( bits, 8 );

            System.out.println( "Uncal Time: " + uncalValue );
            System.out.println( "BitSet: " + hex );

            Assert.assertTrue( "Uncal 0x00 should have raw 0x0000000000000000",
                               hex.equals( "0x0000000000000000" ) == true );

            uncalValue = "0xe3eca7dc0";
            bits = instance.getRawFromUncalibrated( uncalValue );
            hex = XTCEFunctions.bitSetToHex( bits, 8 );

            System.out.println( "Uncal Time: " + uncalValue );
            System.out.println( "BitSet: " + hex );

            Assert.assertTrue( "Uncal 0xe3eca7dc0 should have raw 0x0000eeff00000000",
                               hex.equals( "0x0000eeff00000000" ) == true );

            uncalValue = "0xe3ecb5b8c";
            bits = instance.getRawFromUncalibrated( uncalValue );
            hex = XTCEFunctions.bitSetToHex( bits, 8 );

            System.out.println( "Uncal Time: " + uncalValue );
            System.out.println( "BitSet: " + hex );

            Assert.assertTrue( "Uncal 0xe3eca7dc0 should have raw 0x0000eeff0000ddcc",
                               hex.equals( "0x0000eeff0000ddcc" ) == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getClass().getName() + ": " + ex.getLocalizedMessage() );
        }

    }

    /**
     * Test of getUncalibratedFromCalibrated method, of class XTCEPosixTimeHandler.
     */

    @Test
    public void testGetUncalibratedFromCalibrated() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String     uncalValue;
            String     calValue;
            BigInteger uncalTime;

            XTCEAbsoluteTimeType instance = new XTCEPosixTimeHandler();

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "UNIX_TIME" );

            System.out.println( "Testing Parameter UNIX_TIME" );

            calValue = "1970-01-01 00:00:00.000";
            uncalValue = instance.getUncalibratedFromCalibrated( calValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Uncal time should be 0x0, but is " + uncalValue,
                               uncalValue.equals( "0x0" ) == true );

            calValue = "1970-01-01 16:59:43.000";
            uncalValue = instance.getUncalibratedFromCalibrated( calValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Uncal time should be 0xe3eca7dc0, but is " + uncalValue,
                               uncalValue.equals( "0xe3eca7dc0" ) == true );

            calValue = "2038-01-19 03:14:07.000";
            uncalValue = instance.getUncalibratedFromCalibrated( calValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Uncal time should be 0x7a11ffff0bdc0, but is " + uncalValue,
                               uncalValue.equals( "0x7a11ffff0bdc0" ) == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getClass().getName() + ": " + ex.getLocalizedMessage() );
        }

    }

    private XTCEDatabase db_ = null;

}
