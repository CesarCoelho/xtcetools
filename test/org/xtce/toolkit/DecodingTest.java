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
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;
import org.omg.space.xtce.CalibratorType;
import org.omg.space.xtce.CalibratorType.MathOperationCalibrator;
import org.omg.space.xtce.ContextCalibratorType;
import org.omg.space.xtce.MatchCriteriaType;
import org.xtce.math.MathOperationCalibration;

/**
 *
 * @author dovereem
 */

public class DecodingTest {
    
    public DecodingTest() {

        try {
            loadDocuments();
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
    public void testRawOverflows() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing Raw Value Overflows" );

            BitSet overflow = null;

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Battery_Charge_Mode" );

            overflow = new BitSet( 2 );
            overflow.set( 1 );

            errors += checkFail( overflow,
                                 0x02,
                                 "Battery_Charge_Mode raw binary length '2' overflows raw encoding length of '1'" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Payload_1_Phase" );

            overflow = new BitSet( 5 );
            overflow.set( 4 );

            errors += checkFail( overflow,
                                 0x10,
                                 "Payload_1_Phase raw binary length '5' overflows raw encoding length of '4'" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Payload_MD5" );

            overflow = new BitSet( 140 );
            overflow.set( 139 );

            errors += checkFail( overflow, 0x00, "Payload_MD5 raw binary length '140' overflows raw encoding length of '128'" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Bus_Fault_Message" );

            overflow = new BitSet( 132 );
            overflow.set( 131 );

            errors += checkFail( overflow, 0x00, "Bus_Fault_Message raw binary length '132' overflows raw encoding length of '128'" );

            getParameterItemValueObj( "/BogusSAT",
                                      "CCSDSAPID" );

            overflow = new BitSet( 16 );
            overflow.set( 15 );

            errors += checkFail( overflow, 0x00, "CCSDSAPID raw binary length '16' overflows raw encoding length of '11'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testBooleanParameterTypes() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Boolean Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Battery_Charge_Mode" );

            errors += checkPass( "CHARGE", 0x01 );
            errors += checkPass( "DISCHARGE", 0x00 );
            errors += checkPass( "DISCHARGE", 0x02 ); // truncate raw value

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testEnumeratedParameterTypes() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Enumerated Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Payload_1_Phase" );

            errors += checkPass( "TEST", 0x01 );
            errors += checkPass( "STANDBY", 0x03 );
            errors += checkPass( "FAILED", 0x06 );
            errors += checkPass( "FAILED", 0x09 );
            errors += checkFail( 0x00, "Payload_1_Phase Enumeration undefined for uncalibrated value '0'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testBinaryParameterTypes() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Binary Raw binary" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Payload_MD5" );

            errors += checkPass( "0xa567e0660841dc13346047aa5ac2b5c7",
                                 "0xa567e0660841dc13346047aa5ac2b5c7" );
        
            errors += checkPass( "0xaaaabbbb",
                                 "0x000000000000000000000000aaaabbbb" );

            errors += checkPass( "0xbc614e",
                                 12345678 );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testStringParameterTypesForUTF8() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU String Raw UTF-8" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Bus_Fault_Message" );

            errors += checkPass( "This is a test",
                                 "0x5468697320697320612074657374" );

            errors += checkPass( "",
                                 "0x00000000000000000000000000000000" );

            errors += checkPass( "5678901234567890",  // leading 1234 is overflow
                                 "0x35363738393031323334353637383930" );

            errors += checkPass( "A",
                                 65 );

            //errors += check( 1.25,
            //                 "0x312e3235000000000000000000000000",
            //                 "00110001001011100011001000110101000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" );

            //errors += check( (float)99.0,
            //                 "0x39392e30000000000000000000000000",
            //                 "00111001001110010010111000110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" );


            errors += checkPass( "5678901234567890", // leading aaaa1234 is overflow
                                 "0x35363738393031323334353637383930" );

            errors += checkPass( "Z",
                                 "0x5a000000000000000000000000000000" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Ignore( "not sure what this should be yet" )
    @Test
    public void testStringParameterTypesForUTF16() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU String Raw UTF-16" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "CPU_Fault_Message" );

            errors += checkPass( "abc",
                                 "0xfeff006100620063" );

            errors += checkPass( "",
                                 "0xfeff000000000000" );

            errors += checkPass( "123",
                                 "0xfeff003100320033" );

            errors += checkPass( "0xfeff003500000000",
                                 5 );

            //errors += check( 1.2,
            //                 "0xfeff0031002e0032",
            //                 "1111111011111111000000000011000100000000001011100000000000110010" );

            //errors += check( (float)2.0,
            //                 "0xfeff0032002e0030",
            //                 "1111111011111111000000000011001000000000001011100000000000110000" );

            errors += checkPass( "Z",
                                 "0xfeff005a00000000" );

            // TODO: Fix the case where the UTF-16 header is lost on truncation of long string
            //errors += check( "aaaa1234",
            //                 "0x0031003200330034",
            //                 "0000000000110001000000000011001000000000001100110000000000110100" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testIntegerParameterTypesRawUnsigned() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU UnsignedInteger Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT",
                                      "CCSDSAPID" );

            errors += checkPass( "0",
                                 "0x00000000" );

            errors += checkPass( "0",
                                 0 );

            errors += checkPass( "15",
                                 0x000f );

            errors += checkPass( "1",
                                 0x0001 );

            errors += checkPass( "2047",
                                 "0x07ff" );

            errors += checkPass( "5",
                                 0x0005 );

            //errors += check( (float)1.25,
            //                 "CCSDSAPID Invalid EU unsigned integer value of '1.25'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testIntegerParameterTypesRawSignMagnitude() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU SignedInteger Raw signMagnitude no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_int32_signmag" );

            errors += checkPass( "0",
                                 "0x00000000" );

            errors += checkPass( "0",
                                 0 );

            errors += checkPass( "1",
                                 "0x00000001" );

            errors += checkPass( "1",
                                 1 );

            //errors += check( (float)1.0,
            //                 "0x00000001",
            //                 "00000000000000000000000000000001" );

            errors += checkPass( "2047",
                                 0x000007ff );

            errors += checkPass( "5",
                                 "0x00000005" );

            //errors += check( (float)1.25,
            //                 "Basic_int32_signmag Invalid EU signed integer value of '1.25'" );

            errors += checkPass( "-1",
                                 "0x80000001" );

            errors += checkPass( "-1",
                                 -1 );

            errors += checkPass( "-2",
                                 "0x80000002" );

            errors += checkPass( "-2",
                                 -2 );

            errors += checkPass( "-6",
                                 "0x80000006" );

            errors += checkPass( "-2147483647",
                                 "0xffffffff" );

            errors += checkPass( "0",
                                 "0x80000000" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testIntegerParameterTypesRawTwosComplement() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU SignedInteger Raw twosComplement no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_int32_twoscomp" );

            errors += checkPass( "0",
                                 "0x00000000" );

            errors += checkPass( "0",
                                 0 );

            errors += checkPass( "1",
                                 "0x00000001" );

            errors += checkPass( "1",
                                 1 );

            //errors += check( 1.0,
            //                 "0x00000001",
            //                 "00000000000000000000000000000001" );

            //errors += check( (float)1.0,
            //                 "0x00000001",
            //                 "00000000000000000000000000000001" );

            errors += checkPass( "2047",
                                 0x000007ff );

            errors += checkPass( "5",
                                 "0x00000005" );

            //errors += check( (float)1.25,
            //                 "Basic_int32_signmag Invalid EU signed integer value of '1.25'" );

            errors += checkPass( "-1",
                                 "0xffffffff" );

            errors += checkPass( "-1",
                                 -1 );

            errors += checkPass( "-2",
                                 "0xfffffffe" );

            errors += checkPass( "-2",
                                 -2 );

            errors += checkPass( "-6",
                                 "0xfffffffa" );

            errors += checkPass( "-2147483648",
                                 "0x80000000" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testIntegerParameterTypesRawOnesComplement() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU SignedInteger Raw onesComplement no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_int32_onescomp" );

            errors += checkPass( "0",
                                 "0x00000000" );

            errors += checkPass( "0",
                                 0 );

            errors += checkPass( "1",
                                 "0x00000001" );

            errors += checkPass( "1",
                                 1 );

            //errors += check( 1.0,
            //                 "0x00000001",
            //                 "00000000000000000000000000000001" );

            //errors += check( (float)1.0,
            //                 "0x00000001",
            //                 "00000000000000000000000000000001" );

            errors += checkPass( "2047",
                                 0x000007ff );

            errors += checkPass( "5",
                                 "0x00000005" );

            //errors += check( (float)1.25,
            //                 "Basic_int32_signmag Invalid EU signed integer value of '1.25'" );

            errors += checkPass( "-1",
                                 "0xfffffffe" );

            errors += checkPass( "-1",
                                 -1 );

            errors += checkPass( "-2",
                                 "0xfffffffd" );

            errors += checkPass( "-2",
                                 -2 );

            errors += checkPass( "-6",
                                 "0xfffffff9" );

            errors += checkPass( "-2147483647",
                                 "0x80000000" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesRaw32() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float32 no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Battery_Voltage" );

            errors += checkPass( "1.25",
                                 "0x3fa00000" );

            errors += checkPass( "1.25",
                                 (float)1.25 );

            errors += checkPass( "12.1",
                                 "0x4141999a" );

            errors += checkPass( "12.1",
                                 (float)12.1 );

            errors += checkPass( "5.0",
                                 "0x40a00000" );

            // Reasonableness is not yet making warnings

            //errors += checkFail( (float)99.50,
            //                     "IEEE754_1985 encoding value for item Battery_Voltage is 99.5, which is greater than the maximum value 15.0" );

            //errors += checkFail( (float)-1024.5,
            //                     "IEEE754_1985 encoding value for item Battery_Voltage is -1024.5, which is less than the minimum value 0.0" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesRaw64() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {
            
            System.out.println( "Testing EU Float Raw float64 no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_Float64" );

            errors += checkPass( "1.25",
                                 "0x3ff4000000000000" );

            errors += checkPass( "1.25",
                                 1.25 );

            errors += checkPass( "12.1",
                                 "0x4028333333333333" );

            errors += checkPass( "12.1",
                                 12.1 );

            errors += checkPass( "5.0",
                                 "0x4014000000000000" );

            // Reasonableness is not yet making warnings

            //errors += checkFail( (float)99.50,
            //                     "IEEE754_1985 encoding value for item Battery_Voltage is 99.5, which is greater than the maximum value 15.0" );

            //errors += checkFail( (float)-1024.5,
            //                     "IEEE754_1985 encoding value for item Battery_Voltage is -1024.5, which is less than the minimum value 0.0" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesRawInvalidSize() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw ieee23 bogus" );

            getParameterItemValueObj2( "/UNIT_TEST",
                                       "FLOAT_IEEE_INVALID_RAW" );

            errors += checkFail( "0x0000000000", "FLOAT_IEEE_INVALID_RAW Raw encoding for type named: IEEE754_1985 (23 bits)" );

            errors += checkFail( "0x4000000000", "FLOAT_IEEE_INVALID_RAW Raw encoding for type named: IEEE754_1985 (23 bits)" );

            errors += checkFail( "0x4000000100", "FLOAT_IEEE_INVALID_RAW Raw encoding for type named: IEEE754_1985 (23 bits)" );

            errors += checkFail( "0x69a3b50754", "FLOAT_IEEE_INVALID_RAW Raw encoding for type named: IEEE754_1985 (23 bits)" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesRawMilStd16() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw milstd16 no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_MilFloat16" );

            errors += checkPass( "0.0",
                                 "0x0000" );

            errors += checkPass( "0.5",
                                 "0x4000" );

            errors += checkPass( "1.0",
                                 "0x4001" );

            errors += checkPass( "0.25",
                                 "0x403f" );

            errors += checkPass( "12.40625", // -12.4
                                 "0x6344" );

            errors += checkPass( "12.40625", // -12.4
                                 25412 );

            errors += checkPass( "-12.125",
                                 "0x9f04" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesRawMilStd32() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw milstd32 no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_MilFloat32" );

            errors += checkPass( "0.0",
                                 "0x00000000" );

            errors += checkPass( "0.5",
                                 "0x40000000" );

            errors += checkPass( "1.0",
                                 "0x40000001" );

            errors += checkPass( "0.25",
                                 "0x400000ff" );

            errors += checkPass( "-25.630001068115234", // -25.63
                                 "0x997ae105" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesRawMilStd48() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw milstd48 no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_MilFloat48" );

            errors += checkPass( "0.0",
                                 "0x000000000000" );

            errors += checkPass( "0.5",
                                 "0x400000000000" );

            errors += checkPass( "1.0",
                                 "0x400000010000" );

            errors += checkPass( "105.63948563742451", // 105.639485637361
                                 "0x69a3b50754ab" );

            // need negative value

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesRawMilStdInvalidSize() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw milstd39 bogus" );

            getParameterItemValueObj2( "/UNIT_TEST",
                                       "FLOAT_MILSTD_INVALID_RAW" );

            errors += checkFail( "0x0000000000", "FLOAT_MILSTD_INVALID_RAW Raw encoding for type named: MILSTD_1750A (39 bits)" );

            errors += checkFail( "0x4000000000", "FLOAT_MILSTD_INVALID_RAW Raw encoding for type named: MILSTD_1750A (39 bits)" );

            errors += checkFail( "0x4000000100", "FLOAT_MILSTD_INVALID_RAW Raw encoding for type named: MILSTD_1750A (39 bits)" );

            errors += checkFail( "0x69a3b50754", "FLOAT_MILSTD_INVALID_RAW Raw encoding for type named: MILSTD_1750A (39 bits)" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawUnsignedLinearCalibrator() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw unsigned Linear Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Solar_Array_Voltage_1" );

            errors += checkPass( "-100.0",
                                 "0x0000" );

            errors += checkPass( "-100.0",
                                 (float)0.0 );

            errors += checkPass( "-50.0",
                                 "0x01f4" );

            errors += checkPass( "-50.0",
                                 0x01f4 );

            errors += checkPass( "0.0",
                                 "0x03e8" );

            errors += checkPass( "5.0",
                                 "0x041a" );

            errors += checkPass( "-1.0",
                                 "0x03de" );

            // Reasonableness is not yet making warnings

            //errors += check( 1024,
            //                 "unsigned encoding value for item Solar_Array_Voltage_1 is 11240, which is greater than the maximum value 4000" );

            //errors += check( -200.0,
            //                 "Unsigned value for item Solar_Array_Voltage_1 is -1000 which cannot be negative" );

            //errors += check( 50000.0,
            //                 "unsigned encoding value for item Solar_Array_Voltage_1 is 501000, which is greater than the maximum value 4000" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawUnsignedQuadraticCalibrator() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw unsigned Quadratic Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Quadratic_Demo" );

            errors += checkPass( "0.0",
                                 "0x0002" );

            errors += checkPass( "0.0",
                                 2 );

            errors += checkPass( "7.0",
                                 "0x0003" );

            errors += checkPass( "1015.0",
                                 "0x001f" );

            errors += checkPass( "16.0",
                                 "0x0004" );

            errors += checkPass( "50167.0",
                                 223 );

            errors += checkPass( "50167.0",
                                 "0x00df" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawFloatOrder0Calibrator() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw Float Bad Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_Order0_PolyTerms" );

            errors += checkPass( "2.2",
                                 "0x00df" );


            errors += checkPass( "2.2",
                                 "0x00af" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawFloatBadCalibrator() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw Float Bad Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_Missing_PolyTerms" );

            errors += checkFail( 7.0,
                                 "Float_Missing_PolyTerms No terms specified for the polynomial calibrator." );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_Missing_SplinePoints" );

            errors += checkFail( 7.0,
                                 "Float_Missing_SplinePoints Spline calibrator does not have at least 2 points to work from." );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Spline_Quad_Interpolate_Bad" );

            errors += checkFail( 1.0,
                                 "Spline_Quad_Interpolate_Bad Unsupported order of approximation for spline points of '2', Only flat and linear orders 0 and 1 are supported." );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_3" );

            errors += checkFail( 7.0,
                                 "Float_MathOpCal_3 Invalid postfix expression evaluation, stack size is 0" );

            try {
                CalibratorType defCalObj3 = ppp_.getDefaultCalibrator();
                MathOperationCalibrator mathCalObj3 = defCalObj3.getMathOperationCalibrator();
                MathOperationCalibration calObj3 = new MathOperationCalibration( mathCalObj3 );
                System.out.println( "FAILED: Expected exception from calObj3.toInfixString(): " + calObj3.toInfixString() );
                ++errors;
            } catch ( XTCEDatabaseException ex ) {
                System.out.println( "PASSED: Exception from calObj3.toInfixString(): " + ex.getLocalizedMessage() );
            }

            //getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
            //                          "Float_MathOpCal_2" );

            //errors += checkFail( 5.0,
            //                     "Float_MathOpCal_2 Non-terminating decimal expansion; no exact representable decimal result." );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_4" );

            errors += checkFail( 1.0,
                                 "Float_MathOpCal_4 Unsupported MathOperation element ParameterInstanceRefType" );

            CalibratorType defCalObj = ppp_.getDefaultCalibrator();
            MathOperationCalibrator mathCalObj = defCalObj.getMathOperationCalibrator();
            MathOperationCalibration calObj = new MathOperationCalibration( mathCalObj );

            Assert.assertEquals( "3.0 Float_MathOpCal_1.cal * Float_MathOpCal_2.uncal +",
                                 calObj.toString() );

            Assert.assertEquals( "((3.0 * Float_MathOpCal_1.cal) + Float_MathOpCal_2.uncal)",
                                 calObj.toInfixString() );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_5" );

            errors += checkFail( 1.0,
                                 "Float_MathOpCal_5 Invalid postfix expression evaluation, stack size is 0" );

            try {
                CalibratorType defCalObj5 = ppp_.getDefaultCalibrator();
                MathOperationCalibrator mathCalObj5 = defCalObj5.getMathOperationCalibrator();
                MathOperationCalibration calObj5 = new MathOperationCalibration( mathCalObj5 );
                System.out.println( "FAILED: Expected exception from calObj5.toInfixString(): " + calObj5.toInfixString() );
                ++errors;
            } catch ( XTCEDatabaseException ex ) {
                System.out.println( "PASSED: Exception from calObj5.toInfixString(): " + ex.getLocalizedMessage() );
            }

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_6" );

            errors += checkFail( 1.0,
                                 "Float_MathOpCal_6 Unsupported math operator 'foo'" );

            try {
                CalibratorType defCalObj6 = ppp_.getDefaultCalibrator();
                MathOperationCalibrator mathCalObj6 = defCalObj6.getMathOperationCalibrator();
                MathOperationCalibration calObj6 = new MathOperationCalibration( mathCalObj6 );
                System.out.println( "FAILED: Expected exception from calObj6.toInfixString(): " + calObj6.toInfixString() );
                ++errors;
            } catch ( XTCEDatabaseException ex ) {
                System.out.println( "PASSED: Exception from calObj6.toInfixString(): " + ex.getLocalizedMessage() );
            }

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_10" );

            errors += checkFail( 1.0,
                                 "Float_MathOpCal_10 Division by zero in expression" );

            try {
                CalibratorType defCalObj10 = ppp_.getDefaultCalibrator();
                MathOperationCalibrator mathCalObj10 = defCalObj10.getMathOperationCalibrator();
                MathOperationCalibration calObj10 = new MathOperationCalibration( mathCalObj10 );
                System.out.println( "PASSED: No exception from calObj10.toInfixString(): " + calObj10.toInfixString() );
            } catch ( XTCEDatabaseException ex ) {
                System.out.println( "FAILED: Exception from calObj10.toInfixString(): " + ex.getLocalizedMessage() );
                ++errors;
            }

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawFloatSplineCalibrator() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float Spline Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Spline_Demo" );

            //errors += check( "0",
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0 and extrapolate is false" );

            //errors += check( 0.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            //errors += check( (float)0.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            errors += checkPass( "2.0",
                                 "0xc008000000000000" );

            //errors += check( "-2",
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value -2 and extrapolate is false" );

            errors += checkPass( "3.0",
                                 "0xbff0000000000000" );

            errors += checkPass( "2.5",
                                 "0xc000000000000000" );

            //errors += check( -4.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value -4.0 and extrapolate is false" );

            //errors += check( 5.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 5.0 and extrapolate is false" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawFloatMathOperationCalibrator1() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float MathOperation Calibrator 1" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_1" );

            errors += checkPass( "3.25",
                                 "0x40800000" );

            CalibratorType defCalObj = ppp_.getDefaultCalibrator();
            MathOperationCalibrator mathCalObj = defCalObj.getMathOperationCalibrator();
            MathOperationCalibration calObj = new MathOperationCalibration( mathCalObj );

            Assert.assertEquals( "0.5 uncal * 1.25 +",
                                 calObj.toString() );

            Assert.assertEquals( "((0.5 * uncal) + 1.25)",
                                 calObj.toInfixString() );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawFloatMathOperationCalibrator2() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float MathOperation Calibrator 2" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_2" );

            errors += checkPass( "-129.189484825602022510793176479637622833251953125000000",
                                 "0x3fc999999999999a" ); // 0.2

            errors += checkPass( "168.01731131998036516961292363703250885009765625000000",
                                 "0x4014000000000000" ); // 5.0

            CalibratorType defCalObj = ppp_.getDefaultCalibrator();
            MathOperationCalibrator mathCalObj = defCalObj.getMathOperationCalibrator();
            MathOperationCalibration calObj = new MathOperationCalibration( mathCalObj );

            Assert.assertEquals( "1.234 uncal * ln 2.0 ln / 64.0 *",
                                 calObj.toString() );

            Assert.assertEquals( "((ln[(1.234 * uncal)] / ln[2.0] ) * 64.0)",
                                 calObj.toInfixString() );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawFloatMathOperationCalibrator7() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float MathOperation Calibrator 7" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_7" );

            errors += checkPass( "64",
                                 "0x3fc999999999999a" ); // 0.2

            errors += checkPass( "64",
                                 "0x4014000000000000" ); // 5.0

            CalibratorType defCalObj = ppp_.getDefaultCalibrator();
            MathOperationCalibrator mathCalObj = defCalObj.getMathOperationCalibrator();
            MathOperationCalibration calObj = new MathOperationCalibration( mathCalObj );

            Assert.assertEquals( "5.0 3.0 - 3.0 ^ 92.0 + log 1/x -4.0 * abs 5.0 swap - 2.0 - asin cos acos sin acos atan 19.0 + 8.0 % 4.0 y^x",
                                 calObj.toString() );

            Assert.assertEquals( "(4.0 ^ ((atan[acos[sin[acos[cos[asin[((abs[((1.0 / log[(((5.0 - 3.0) ^ 3.0) + 92.0)]) * -4.0)] - 5.0) - 2.0)]]]]]] + 19.0) % 8.0) )",
                                 calObj.toInfixString() );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawFloatMathOperationCalibrator8() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float MathOperation Calibrator 8" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_8" );

            errors += checkPass( "2.718281828459045090795598298427648842334747314453125",
                                 "0x3fc999999999999a" ); // 0.2

            errors += checkPass( "2.718281828459045090795598298427648842334747314453125",
                                 "0x4014000000000000" ); // 5.0

            CalibratorType defCalObj = ppp_.getDefaultCalibrator();
            MathOperationCalibrator mathCalObj = defCalObj.getMathOperationCalibrator();
            MathOperationCalibration calObj = new MathOperationCalibration( mathCalObj );

            Assert.assertEquals( "99.0 100.0 == 12.0 != 1.0 == 1.0 != 5.0 < 0.0 < 5.0 > -1.0 > 1.0 <= 2.0 <= 0.0 <= 4.0 >= 0.0 >= 0.0 >= e^x",
                                 calObj.toString() );

            Assert.assertEquals( "(e ^ ((((((((((((((99.0 == 100.0) != 12.0) == 1.0) != 1.0) < 5.0) < 0.0) > 5.0) > -1.0) <= 1.0) <= 2.0) <= 0.0) >= 4.0) >= 0.0) >= 0.0))",
                                 calObj.toInfixString() );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawFloatMathOperationCalibrator9() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float MathOperation Calibrator 9" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_9" );

            errors += checkPass( "1.707298654565544193673076733830384910106658935546875",
                                 "0x3fc999999999999a" ); // 0.2

            errors += checkPass( "1.707298654565544193673076733830384910106658935546875",
                                 "0x4014000000000000" ); // 5.0

            CalibratorType defCalObj = ppp_.getDefaultCalibrator();
            MathOperationCalibrator mathCalObj = defCalObj.getMathOperationCalibrator();
            MathOperationCalibration calObj = new MathOperationCalibration( mathCalObj );

            Assert.assertEquals( "90.0 1.0 - cos 89.0 + cos 45.0 + tan tanh cosh sinh",
                                 calObj.toString() );

            Assert.assertEquals( "sinh[cosh[tanh[tan[(cos[(cos[(90.0 - 1.0)] + 89.0)] + 45.0)]]]]",
                                 calObj.toInfixString() );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesDefaultWithPolynomialContextCalibrator() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Default with Polynomial Context Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "SunSensorLevel" );

            errors += checkPass( "-10.0",
                                 "0x0000" );

            errors += checkPass( "-5.0",
                                 1 );

            errors += checkPass( "0.0",
                                 "0x0002" );

            errors += checkPass( "5.0",
                                 "0x0003" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesDefaultWithSplineContextCalibrator() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Default with Spline Context Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "EarthSensorLevel" );

            //errors += check( "0",
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0 and extrapolate is false" );

            //errors += check( 0.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            //errors += check( (float)0.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            errors += checkPass( "0.0",
                                 "0x0000000000000000" );

            //errors += check( "-2",
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value -2 and extrapolate is false" );

            errors += checkPass( "5.0",
                                 10 );

            errors += checkPass( "10.0",
                                 "0x0000000000000014" );

            //errors += checkFail( "50",
            //                     "Spline Calibrator for EarthSensorLevel does not bound calibrated value -4.0 and extrapolate is false" );

            //errors += check( 5.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 5.0 and extrapolate is false" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithPolynomialContextCalibratorFalseMatch() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float with Polynomial Context Calibrator False Match" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "SunSensorLevel",
                                      "/BogusSAT/SC001/BusElectronics",
                                      "SunSensorMode",
                                      "OFF" );

            errors += checkPass( "-10.0",
                                 "0x0000" );

            errors += checkPass( "-5.0",
                                 1 );

            errors += checkPass( "0.0",
                                 "0x0002" );

            errors += checkPass( "5.0",
                                 "0x0003" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithPolynomialContextCalibratorTrueMatch1() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float with Polynomial Context Calibrator True Match = LOW" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "SunSensorLevel",
                                      "/BogusSAT/SC001/BusElectronics",
                                      "SunSensorMode",
                                      "LOW" );

            errors += checkPass( "0.0",
                                 "0x0000" );

            errors += checkPass( "5.0",
                                 1 );

            errors += checkPass( "10.0",
                                 "0x0002" );

            errors += checkPass( "15.0",
                                 "0x0003" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithPolynomialContextCalibratorTrueMatch2() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float with Polynomial Context Calibrator True Match = HIGH" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "SunSensorLevel",
                                      "/BogusSAT/SC001/BusElectronics",
                                      "SunSensorMode",
                                      "HIGH" );

            errors += checkPass( "0.0",
                                 "0x0000" );

            errors += checkPass( "15.0",
                                 1 );

            errors += checkPass( "30.0",
                                 "0x0002" );

            errors += checkPass( "45.0",
                                 "0x0003" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithSplineContextCalibratorFalseMatch() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float with Spline Context Calibrator False Match" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "EarthSensorLevel",
                                      "/BogusSAT/SC001/BusElectronics",
                                      "EarthSensorMode",
                                      "OFF" );

            //errors += check( "0",
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0 and extrapolate is false" );

            //errors += check( 0.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            //errors += check( (float)0.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            errors += checkPass( "0.0",
                                 "0x0000000000000000" );

            //errors += check( "-2",
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value -2 and extrapolate is false" );

            errors += checkPass( "5.0",
                                 10 );

            errors += checkPass( "10.0",
                                 "0x0000000000000014" );

            //errors += checkFail( "50",
            //                     "Spline Calibrator for EarthSensorLevel does not bound calibrated value -4.0 and extrapolate is false" );

            //errors += check( 5.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 5.0 and extrapolate is false" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithSplineContextCalibratorTrueMatch1() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float with Spline Context Calibrator True Match = LOW" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "EarthSensorLevel",
                                      "/BogusSAT/SC001/BusElectronics",
                                      "EarthSensorMode",
                                      "LOW" );

            //errors += check( "0",
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0 and extrapolate is false" );

            //errors += check( 0.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            //errors += check( (float)0.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            errors += checkPass( "5.0",
                                 "0x0000000000000000" );

            //errors += check( "-2",
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value -2 and extrapolate is false" );

            errors += checkPass( "7.5", // testing order=1
                                 5 );

            errors += checkPass( "10.0",
                                 10 );

            errors += checkPass( "15.0",
                                 "0x0000000000000014" );

            //errors += checkFail( "50",
            //                     "Spline Calibrator for EarthSensorLevel does not bound calibrated value -4.0 and extrapolate is false" );

            //errors += check( 5.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 5.0 and extrapolate is false" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithSplineContextCalibratorTrueMatch2() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float with Spline Context Calibrator True Match = HIGH" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "EarthSensorLevel",
                                      "/BogusSAT/SC001/BusElectronics",
                                      "EarthSensorMode",
                                      "HIGH" );

            //errors += check( "0",
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0 and extrapolate is false" );

            //errors += check( 0.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            //errors += check( (float)0.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            errors += checkPass( "10.0",
                                 "0x0000000000000000" );

            //errors += check( "-2",
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value -2 and extrapolate is false" );

            errors += checkPass( "10.0", // testing order=0
                                 5 );

            errors += checkPass( "15.0",
                                 10 );

            errors += checkPass( "20.0",
                                 "0x0000000000000014" );

            //errors += checkFail( "50",
            //                     "Spline Calibrator for EarthSensorLevel does not bound calibrated value -4.0 and extrapolate is false" );

            //errors += check( 5.0,
            //                 "Spline Calibrator for Spline_Demo does not bound calibrated value 5.0 and extrapolate is false" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    private long checkFail( BitSet bits, long raw, String warning ) {

        String result = vvv_.decode( bits );
        return checkWarning( Long.toString( raw ), result, warning );

    }

    private long checkFail( String raw, String warning ) {

        //BigInteger rawHex   = new BigInteger( raw.getBytes() );
        //BitSet     rawValue = vvv_.encodeRawBits( rawHex );
        //String     result   = vvv_.decode( rawValue );
        BigInteger rawIntegerValue = null;
        if ( raw.startsWith( "0x" ) == true ) {
            rawIntegerValue = new BigInteger( raw.replaceFirst( "0x", "" ), 16 );
        } else {
            rawIntegerValue = new BigInteger( raw, 16 );
        }

        BitSet rawValue = vvv_.encodeRawBits( rawIntegerValue );
        String result   = vvv_.decode( rawValue );
        return checkWarning( raw, result, warning );

    }

    private long checkFail( long raw, String warning ) {

        BitSet rawValue = vvv_.encodeRawBits( BigInteger.valueOf( raw ) );
        String result   = vvv_.decode( rawValue );
        return checkWarning( Long.toString( raw ), result, warning );
        
    }

    private long checkFail( double raw, String warning ) {

        BigInteger intValue =
            BigInteger.valueOf( Double.doubleToRawLongBits( raw ) );
        BitSet rawValue = vvv_.encodeRawBits( intValue );
        String result   = vvv_.decode( rawValue );
        return checkWarning( Double.toString( raw ), result, warning );

    }

    private long checkFail( float raw, String warning ) {

        BigInteger intValue =
            BigInteger.valueOf( Float.floatToRawIntBits( raw ) );
        BitSet rawValue = vvv_.encodeRawBits( intValue );
        String result   = vvv_.decode( rawValue );
        return checkWarning( Float.toString( raw ), result, warning );

    }

    private long checkPass( String eu, double raw ) {

        BigInteger intValue = BigInteger.valueOf( Double.doubleToRawLongBits( raw ) );
        BitSet     rawValue = vvv_.encodeRawBits( intValue );
        String result = vvv_.decode( rawValue );
        return checkExpected( eu, "0x" + intValue.toString( 16 ), result );

    }

    private long checkPass( String eu, float raw ) {

        BigInteger intValue = BigInteger.valueOf( Float.floatToIntBits( raw ) );
        BitSet     rawValue = vvv_.encodeRawBits( intValue );
        String result = vvv_.decode( rawValue );
        return checkExpected( eu, "0x" + intValue.toString( 16 ), result );

    }

    private long checkPass( String eu, long raw ) {

        BitSet rawValue = vvv_.encodeRawBits( new BigInteger( Long.toString( raw ) ) );
        String result   = vvv_.decode( rawValue );
        return checkExpected( eu, Long.toString( raw ), result );

    }

    private long checkPass( String eu, String raw ) {

        BigInteger rawIntegerValue = null;
        if ( raw.startsWith( "0x" ) == true ) {
            rawIntegerValue = new BigInteger( raw.replaceFirst( "0x", "" ), 16 );
        } else {
            rawIntegerValue = new BigInteger( raw, 16 );
        }

        BitSet rawValue = vvv_.encodeRawBits( rawIntegerValue );
        String result   = vvv_.decode( rawValue );
        return checkExpected( eu, raw, result );

    }

    private long checkExpected( String eu, String raw, String result ) {

        long errors = 0;

        StringBuilder msg = new StringBuilder();

        if ( result.equals( eu ) == false ) {
            ++errors;
            msg.append( "FAILED: " );
            msg.append( vvv_.getItemName() );
            msg.append( " EU/Cal Value '" );
            msg.append( result );
            msg.append( "' Raw '" );
            msg.append( raw );
            msg.append( "' (Expected EU '" );
            msg.append( eu );
            msg.append( "')" );
        } else {
            msg.append( "PASSED: " );
            msg.append( vvv_.getItemName() );
            msg.append( " EU/Cal Value '" );
            msg.append( eu );
            msg.append( "' Raw '" );
            msg.append( raw );
            msg.append( "'" );
        }

        printWarnings();
        System.out.println( msg.toString() );

        return errors;

    }

    private long checkWarning( String raw, String result, String warning ) {

        long    errors = 0;
        boolean found  = false;

        StringBuilder msg = new StringBuilder();

        List<String> warnings = vvv_.getWarnings();

        for ( String retWarning : warnings ) {
            if ( retWarning.equals( warning ) == true ) {
                found = true;
            }
        }

        if ( found == false ) {
            ++errors;
            msg.append( "FAILED: " );
            msg.append( vvv_.getItemName() );
            msg.append( " Expected Warning [" );
            msg.append( warning );
            msg.append( "] for EU/Cal Value '" );
            msg.append( result );
            msg.append( "' Raw Hex '" );
            msg.append( raw );
            msg.append( "'" );
            printWarnings();
        } else {
            msg.append( "PASSED: " );
            msg.append( vvv_.getItemName() );
            msg.append( " Received Warning [" );
            msg.append( warning );
            msg.append( "] for EU/Cal Value '" );
            msg.append( result );
            msg.append( "' Raw Hex '" );
            msg.append( raw );
            msg.append( "'" );
            vvv_.clearWarnings();
        }

        System.out.println( msg.toString() );

        return errors;

    }

    private void printWarnings() {

        for ( String warn : vvv_.getWarnings() ) {
            System.out.println( "WARNING: " + warn );
        }

        vvv_.clearWarnings();

    }

    private void getParameterItemValueObj( String path,
                                           String name )
        throws XTCEDatabaseException {

        ppp_ = db_.getSpaceSystem( path ).getTelemetryParameter( name );

        vvv_ = new XTCEItemValue( ppp_ );
        if ( vvv_.isValid() == false ) {
            throw new XTCEDatabaseException( "Parameter " +
                                             ppp_.getName() +
                                             " missing encoding information" );
        }

    }

    private void getParameterItemValueObj2( String path,
                                            String name )
        throws XTCEDatabaseException {

        ppp_ = db2_.getSpaceSystem( path ).getTelemetryParameter( name );

        vvv_ = new XTCEItemValue( ppp_ );
        if ( vvv_.isValid() == false ) {
            throw new XTCEDatabaseException( "Parameter " +
                                             ppp_.getName() +
                                             " missing encoding information" );
        }

    }

    private void getParameterItemValueObj( String path,
                                           String name,
                                           String pathContextMatcher,
                                           String nameContextMatcher,
                                           String calValueMatch )
        throws XTCEDatabaseException {

        // this should NOT serve as an example of how to do this because it is
        // not general, rather it is crafted for these test cases and nothing
        // more.

        ppp_ = db_.getSpaceSystem( path )
                  .getTelemetryParameter( name );

        XTCEParameter contextMatcher =
            db_.getSpaceSystem( pathContextMatcher )
               .getTelemetryParameter( nameContextMatcher );

        List<ContextCalibratorType> calibrators = ppp_.getContextCalibrators();

        if ( calibrators == null ) {
            throw new XTCEDatabaseException( "Parameter " +
                                             ppp_.getName() +
                                             " missing context calibrators" );
        }

        // this only supports single match cases, not the list of comparisons

        vvv_ = null;

        for ( ContextCalibratorType calibrator : calibrators ) {

            MatchCriteriaType matcher = calibrator.getContextMatch();
            String parameterRef = matcher.getComparison().getParameterRef();
            String matchParameterAbsPath = XTCEFunctions.resolvePathReference( path, parameterRef );
            String matchParameterPath = XTCEFunctions.getPathNameFromReferenceString( matchParameterAbsPath );
            String matchParameterName = XTCEFunctions.getNameFromPathReferenceString( matchParameterAbsPath );

            XTCEParameter matchParameter =
                db_.getSpaceSystem( matchParameterPath )
                   .getTelemetryParameter( matchParameterName );

            if ( matchParameter.equals( contextMatcher ) == true ) {
                String calValue = matcher.getComparison().getValue();
                if ( calValue.equals( calValueMatch ) == true ) {
                    vvv_ = new XTCEItemValue( ppp_, calibrator.getCalibrator() );
                    break;
                }
            }

        }

        // no context match uses the default calibrator

        if ( vvv_ == null ) {
            vvv_ = new XTCEItemValue( ppp_, ppp_.getDefaultCalibrator() );
        }

        if ( vvv_.isValid() == false ) {
            throw new XTCEDatabaseException( "Parameter " +
                                             ppp_.getName() +
                                             " missing encoding information" );
        }

    }

    private void getParameterItemValueObj2( String path,
                                            String name,
                                            String pathContextMatcher,
                                            String nameContextMatcher,
                                            String calValueMatch )
        throws XTCEDatabaseException {

        // this should NOT serve as an example of how to do this because it is
        // not general, rather it is crafted for these test cases and nothing
        // more.

        ppp_ = db2_.getSpaceSystem( path )
                   .getTelemetryParameter( name );

        XTCEParameter contextMatcher =
            db2_.getSpaceSystem( pathContextMatcher )
                .getTelemetryParameter( nameContextMatcher );

        List<ContextCalibratorType> calibrators = ppp_.getContextCalibrators();

        if ( calibrators == null ) {
            throw new XTCEDatabaseException( "Parameter " +
                                             ppp_.getName() +
                                             " missing context calibrators" );
        }

        // this only supports single match cases, not the list of comparisons

        vvv_ = null;

        for ( ContextCalibratorType calibrator : calibrators ) {

            MatchCriteriaType matcher = calibrator.getContextMatch();
            String parameterRef = matcher.getComparison().getParameterRef();
            String matchParameterAbsPath = XTCEFunctions.resolvePathReference( path, parameterRef );
            String matchParameterPath = XTCEFunctions.getPathNameFromReferenceString( matchParameterAbsPath );
            String matchParameterName = XTCEFunctions.getNameFromPathReferenceString( matchParameterAbsPath );

            XTCEParameter matchParameter =
                db2_.getSpaceSystem( matchParameterPath )
                    .getTelemetryParameter( matchParameterName );

            if ( matchParameter.equals( contextMatcher ) == true ) {
                String calValue = matcher.getComparison().getValue();
                if ( calValue.equals( calValueMatch ) == true ) {
                    vvv_ = new XTCEItemValue( ppp_, calibrator.getCalibrator() );
                    break;
                }
            }

        }

        // no context match uses the default calibrator

        if ( vvv_ == null ) {
            vvv_ = new XTCEItemValue( ppp_, ppp_.getDefaultCalibrator() );
        }

        if ( vvv_.isValid() == false ) {
            throw new XTCEDatabaseException( "Parameter " +
                                             ppp_.getName() +
                                             " missing encoding information" );
        }

    }

    private void loadDocuments() throws XTCEDatabaseException {

        String path = "test/org/xtce/toolkit/test";

        File file1 = new File ( path + "/BogusSAT-1.xml" );
        File file2 = new File ( path + "/UnitTests.xml" );

        System.out.println( "Loading the " + file1.getName() + " demo database" );

        db_  = new XTCEDatabase( file1, false, false, true );

        System.out.println( "Loading the " + file2.getName() + " demo database" );

        db2_ = new XTCEDatabase( file2, false, false, true );

    }

    // Private Data Members

    private XTCEDatabase db_;
    private XTCEDatabase db2_;
    private XTCEParameter ppp_ = null;
    private XTCEItemValue vvv_ = null;

}
