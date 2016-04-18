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

public class CcsdsCucTimeHandlerTest {
    
    public CcsdsCucTimeHandlerTest() {

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
    public void testIsApplicableTAI() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCEParameter        parameter;
        AbsoluteTimeDataType xml;

        try {

            XTCEAbsoluteTimeType instance = new XTCECcsdsCucTimeHandler();

            parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "ODD_TIME" );

            xml = (AbsoluteTimeDataType)parameter.getTypeReference();

            Assert.assertTrue( "Handler should not be found for ODD_TIME",
                               instance.isApplicable( xml ) == false );

            parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "CUC_TAI_TIME" );

            xml = (AbsoluteTimeDataType)parameter.getTypeReference();

            Assert.assertTrue( "Handler should be found for CUC_TAI_TIME",
                               instance.isApplicable( xml ) == true );

            parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "CUC_GPS_TIME" );

            xml = (AbsoluteTimeDataType)parameter.getTypeReference();

            Assert.assertTrue( "Handler should not be found for CUC_GPS_TIME",
                               instance.isApplicable( xml ) == false );

            Assert.assertTrue( "Handler should not be found for NULL",
                               instance.isApplicable( null ) == false );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    /**
     * Test of isApplicable method, of class XTCEPosixTimeHandler.
     */

    @Test
    public void testIsApplicableGPS() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCEParameter        parameter;
        AbsoluteTimeDataType xml;

        try {

            XTCEAbsoluteTimeType instance = new XTCECcsdsCucTimeHandler( "yyyy-MM-dd HH:mm:ss.SSS",
                                                                         "GMT",
                                                                         "1980-01-06", 
                                                                         4,
                                                                         3 );

            parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "ODD_TIME" );

            xml = (AbsoluteTimeDataType)parameter.getTypeReference();

            Assert.assertTrue( "Handler should not be found for ODD_TIME",
                               instance.isApplicable( xml ) == false );

            parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "CUC_TAI_TIME" );

            xml = (AbsoluteTimeDataType)parameter.getTypeReference();

            Assert.assertTrue( "Handler should not be found for CUC_TAI_TIME",
                               instance.isApplicable( xml ) == false );

            parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "CUC_GPS_TIME" );

            xml = (AbsoluteTimeDataType)parameter.getTypeReference();

            Assert.assertTrue( "Handler should be found for CUC_GPS_TIME",
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
/*
    @Test
    public void testGetUncalibratedFromRawTAI() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String     uncalValue;
            BigInteger uncalTime;

            XTCEAbsoluteTimeType instance = new XTCECcsdsCucTimeHandler();

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "CUC_TAI_TIME" );

            System.out.println( "Testing Parameter CUC_TAI_TIME" );

            // 1 January 1990 is 0
            // UNIX time is 315964800 (0x12d53d80)
            // usecs 315964800000000 (0x11f5e49aa6000)

            uncalValue = instance.getUncalibratedFromRaw( new BitSet( 56 ) );
            uncalTime  = new BigInteger( uncalValue.replaceFirst( "0x", "" ), 16 );

            System.out.println( "Uncal Time: 0x" + uncalTime.toString( 16 ) );

            Assert.assertTrue( "Raw Bits of 0 should be 0x11f5e49aa6000 uncalibrated",
                               uncalValue.equals( "0x11f5e49aa6000" ) == true );

            // 1 June 1980 00:00:00 is  (0xc1cc80 0x000000)
            // UNIX time is 328665600 (0x13970a00)
            // usecs 328665600000000 (0x12aeb6c568000)
            // gps usecs offset 0x4990f83c00
            // uncal 0x12aeb6c568000

            byte[] bytes1 = { (byte)0x00, (byte)0x00, (byte)0x00,
                              (byte)0x80, (byte)0xcc, (byte)0xc1, (byte)0x00 };

            BitSet aTime = XTCEFunctions.getBitSetFromByteArray( bytes1 );

            uncalValue = instance.getUncalibratedFromRaw( aTime );

            System.out.println( "BitSet: " + XTCEFunctions.bitSetToHex( aTime, 7 ) );
            System.out.println( "Uncal Time: " + uncalValue );

            Assert.assertTrue( "Time should be 0x12aeb6c568000",
                               uncalValue.equals( "0x12aeb6c568000" ) == true );

            // 1 June 1980 00:00:00.937500 is  (0xc1cc80 0xf00000)
            // UNIX time is 328665600 (0x13970a00)
            // usecs 328665600000000 (0x12aeb6c568000)
            // gps usecs offset 0x4990f83c00
            // uncal 0x12aeb6c568000

            byte[] bytes2 = { (byte)0x00, (byte)0x00, (byte)0xf0,
                              (byte)0x80, (byte)0xcc, (byte)0xc1, (byte)0x00 };

            BitSet bTime = XTCEFunctions.getBitSetFromByteArray( bytes2 );

            uncalValue = instance.getUncalibratedFromRaw( bTime );

            System.out.println( "BitSet: " + XTCEFunctions.bitSetToHex( bTime, 7 ) );
            System.out.println( "Uncal Time: " + uncalValue );

            Assert.assertTrue( "Time should be 0x12aeb6c64ce1c",
                               uncalValue.equals( "0x12aeb6c64ce1c" ) == true );

            byte[] bytes3 = { (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                              (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff };

            BitSet cTime = XTCEFunctions.getBitSetFromByteArray( bytes3 );

            uncalValue = instance.getUncalibratedFromRaw( cTime );

            System.out.println( "BitSet: " + XTCEFunctions.bitSetToHex( cTime) );
            System.out.println( "Uncal Time: " + uncalValue );

            Assert.assertTrue( "Time should be 0xf423ffff0bdc0",
                               uncalValue.equals( "0xf423ffff0bdc0" ) == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getClass().getName() + ": " + ex.getLocalizedMessage() );
        }

    }
*/
    /**
     * Test of getUncalibratedFromRaw method, of class XTCEPosixTimeHandler.
     */

    @Test
    public void testGetUncalibratedFromRawGPS() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String     uncalValue;
            BigInteger uncalTime;

            XTCEAbsoluteTimeType instance = new XTCECcsdsCucTimeHandler( "yyyy-MM-dd HH:mm:ss.SSS",
                                                                         "GMT",
                                                                         "1980-01-06", 
                                                                         4,
                                                                         3 );

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "CUC_GPS_TIME" );

            System.out.println( "Testing Parameter CUC_GPS_TIME" );

            // 6 January 1980 is 0
            // UNIX time is 315964800 (0x12d53d80)
            // usecs 315964800000000 (0x11f5e49aa6000)

            uncalValue = instance.getUncalibratedFromRaw( new BitSet( 56 ) );
            uncalTime  = new BigInteger( uncalValue.replaceFirst( "0x", "" ), 16 );

            System.out.println( "Uncal Time: 0x" + uncalTime.toString( 16 ) );

            Assert.assertTrue( "Raw Bits of 0 should be 0x11f5e49aa6000 uncalibrated",
                               uncalValue.equals( "0x11f5e49aa6000" ) == true );

            // 1 June 1980 00:00:00 is  (0xc1cc80 0x000000)
            // UNIX time is 328665600 (0x13970a00)
            // usecs 328665600000000 (0x12aeb6c568000)
            // gps usecs offset 0x4990f83c00
            // uncal 0x12aeb6c568000

            byte[] bytes1 = { (byte)0x00, (byte)0x00, (byte)0x00,
                              (byte)0x80, (byte)0xcc, (byte)0xc1, (byte)0x00 };

            BitSet aTime = XTCEFunctions.getBitSetFromByteArray( bytes1 );

            uncalValue = instance.getUncalibratedFromRaw( aTime );

            System.out.println( "BitSet: " + XTCEFunctions.bitSetToHex( aTime, 7 ) );
            System.out.println( "Uncal Time: " + uncalValue );

            Assert.assertTrue( "Time should be 0x12aeb6c568000",
                               uncalValue.equals( "0x12aeb6c568000" ) == true );

            // 1 June 1980 00:00:00.937500 is  (0xc1cc80 0xf00000)
            // UNIX time is 328665600 (0x13970a00)
            // usecs 328665600000000 (0x12aeb6c568000)
            // gps usecs offset 0x4990f83c00
            // uncal 0x12aeb6c568000

            byte[] bytes2 = { (byte)0x00, (byte)0x00, (byte)0xf0,
                              (byte)0x80, (byte)0xcc, (byte)0xc1, (byte)0x00 };

            BitSet bTime = XTCEFunctions.getBitSetFromByteArray( bytes2 );

            uncalValue = instance.getUncalibratedFromRaw( bTime );

            System.out.println( "BitSet: " + XTCEFunctions.bitSetToHex( bTime, 7 ) );
            System.out.println( "Uncal Time: " + uncalValue );

            Assert.assertTrue( "Time should be 0x12aeb6c64ce1c",
                               uncalValue.equals( "0x12aeb6c64ce1c" ) == true );

            // 20 December 2012 00:00:30.500 is  (0x3DFD179E 0x800000)
            // CUC is 17448251464613888 (0x3DFD179E800000)
            // UNIX time is 1355961630 (0x50D2551E)
            // usecs 1355961630000000 (0x4D13D6B490380)
            // gps usecs offset 0x4990f83c00
            // uncal 0x4d13d6b50a4a0

            byte[] bytes3 = { (byte)0x00, (byte)0x00, (byte)0x80,
                              (byte)0x9e, (byte)0x17, (byte)0xfd, (byte)0x3d };

            BitSet cTime = XTCEFunctions.getBitSetFromByteArray( bytes3 );

            uncalValue = instance.getUncalibratedFromRaw( cTime );

            System.out.println( "BitSet: " + XTCEFunctions.bitSetToHex( cTime, 7 ) );
            System.out.println( "Uncal Time: " + uncalValue );

            Assert.assertTrue( "Time should be 0x4d13d6b50a4a0",
                               uncalValue.equals( "0x4d13d6b50a4a0" ) == true );

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

            XTCEAbsoluteTimeType instance = new XTCECcsdsCucTimeHandler( "yyyy-MM-dd HH:mm:ss.SSS",
                                                                         "GMT",
                                                                         "1980-01-06", 
                                                                         4,
                                                                         3 );

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "CUC_GPS_TIME" );

            System.out.println( "Testing Parameter CUC_GPS_TIME" );

            uncalValue = "0x11f5e49aa6000";
            calValue = instance.getCalibratedFromUncalibrated( uncalValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Cal time should be 1980-01-06 00:00:00.000, but is " + calValue,
                               calValue.equals( "1980-01-06 00:00:00.000" ) == true );

            uncalValue = "0x12aeb6c568000";
            calValue = instance.getCalibratedFromUncalibrated( uncalValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Cal time should be 1980-06-01 00:00:00.000, but is " + calValue,
                               calValue.equals( "1980-06-01 00:00:00.000" ) == true );

            uncalValue = "0x12aeb6c64ce1c";
            calValue = instance.getCalibratedFromUncalibrated( uncalValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Cal time should be 1980-06-01 00:00:00.937, but is " + calValue,
                               calValue.equals( "1980-06-01 00:00:00.937" ) == true );

            uncalValue = "0x4d13d6b50a4a0";
            calValue = instance.getCalibratedFromUncalibrated( uncalValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Cal time should be 2012-12-20 00:00:30.500, but is " + calValue,
                               calValue.equals( "2012-12-20 00:00:30.500" ) == true );

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

            XTCEAbsoluteTimeType instance = new XTCECcsdsCucTimeHandler( "yyyy-MM-dd HH:mm:ss.SSS",
                                                                         "GMT",
                                                                         "1980-01-06", 
                                                                         4,
                                                                         3 );

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "CUC_GPS_TIME" );

            System.out.println( "Testing Parameter CUC_GPS_TIME" );

            BitSet bits;
            String hex;
            String uncalValue;

            // 6 January 1980 is 0
            // UNIX time is 315964800 (0x12d53d80)
            // usecs 315964800000000 (0x11f5e49aa6000)

            uncalValue = "0x11f5e49aa6000";
            bits = instance.getRawFromUncalibrated( uncalValue );
            hex = XTCEFunctions.bitSetToHex( bits, 7 );

            System.out.println( "Uncal Time: " + uncalValue );
            System.out.println( "BitSet: " + hex );
            System.out.println( "Expect: 0x00000000000000" );

            Assert.assertTrue( "Uncal 0x11f5e49aa6000 should have raw 0x00000000000000",
                               hex.equals( "0x00000000000000" ) == true );

            // 1 June 1980 00:00:00 is  (0xc1cc80 0x000000)
            // UNIX time is 328665600 (0x13970a00)
            // usecs 328665600000000 (0x12aeb6c568000)
            // gps usecs offset 0x4990f83c00
            // uncal 0x12aeb6c568000

            uncalValue = "0x12aeb6c568000";
            bits = instance.getRawFromUncalibrated( uncalValue );
            hex = XTCEFunctions.bitSetToHex( bits, 7 );

            System.out.println( "Uncal Time: " + uncalValue );
            System.out.println( "BitSet: " + hex );
            System.out.println( "Expect: 0x00c1cc80000000" );

            Assert.assertTrue( "Uncal 0x12aeb6c568000 should have raw 0x00c1cc80000000",
                               hex.equals( "0x00c1cc80000000" ) == true );

            // 20 December 2012 00:00:30.500 is  (0x3DFD179E 0x800000)
            // CUC is 17448251464613888 (0x3DFD179E800000)
            // UNIX time is 1355961630 (0x50D2551E)
            // usecs 1355961630000000 (0x4D13D6B490380)
            // gps usecs offset 0x4990f83c00
            // uncal 0x4d13d6b50a4a0

            uncalValue = "0x4d13d6b50a4a0";
            bits = instance.getRawFromUncalibrated( uncalValue );
            hex = XTCEFunctions.bitSetToHex( bits, 7 );

            System.out.println( "Uncal Time: " + uncalValue );
            System.out.println( "BitSet: " + hex );
            System.out.println( "Expect: 0x3dfd179e800000" );

            Assert.assertTrue( "Uncal 0x4d13d6b50a4a0 should have raw 0x3dfd179e800000",
                               hex.equals( "0x3dfd179e800000" ) == true );

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

            XTCEAbsoluteTimeType instance = new XTCECcsdsCucTimeHandler( "yyyy-MM-dd HH:mm:ss.SSS",
                                                                         "GMT",
                                                                         "1980-01-06", 
                                                                         4,
                                                                         3 );

            XTCEParameter parameter =
                db_.getRootSpaceSystem().getTelemetryParameter( "CUC_GPS_TIME" );

            System.out.println( "Testing Parameter CUC_GPS_TIME" );

            calValue = "1980-01-06 00:00:00.000";
            uncalValue = instance.getUncalibratedFromCalibrated( calValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Uncal time should be 0x11f5e49aa6000, but is " + uncalValue,
                               uncalValue.equals( "0x11f5e49aa6000" ) == true );

            calValue = "1980-06-01 00:00:00.000";
            uncalValue = instance.getUncalibratedFromCalibrated( calValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Uncal time should be 0x12aeb6c568000, but is " + uncalValue,
                               uncalValue.equals( "0x12aeb6c568000" ) == true );

            // rounding? 0x12aeb6c64ce1c is 0x12aeb6c64cc28

            calValue = "1980-06-01 00:00:00.937";
            uncalValue = instance.getUncalibratedFromCalibrated( calValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Uncal time should be 0x12aeb6c64cc28, but is " + uncalValue,
                               uncalValue.equals( "0x12aeb6c64cc28" ) == true );

            calValue = "2012-12-20 00:00:30.500";
            uncalValue = instance.getUncalibratedFromCalibrated( calValue );

            System.out.println( "Cal Time: " + calValue + " for " + uncalValue );

            Assert.assertTrue( "Uncal time should be 0x4d13d6b50a4a0, but is " + uncalValue,
                               uncalValue.equals( "0x4d13d6b50a4a0" ) == true );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getClass().getName() + ": " + ex.getLocalizedMessage() );
        }

    }

    private XTCEDatabase db_ = null;

}
