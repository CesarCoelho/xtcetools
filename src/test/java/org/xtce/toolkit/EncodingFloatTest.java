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
import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;
import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xtce.math.MilStd1750A;

/**
 *
 * @author dovereem
 */

public class EncodingFloatTest {
    
    public EncodingFloatTest() {

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
    public void testFloatParameterTypesRaw32() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float32 no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Battery_Voltage" );

            errors += check( "1.25",
                             "0x3fa00000",
                             "00111111101000000000000000000000" );

            errors += check( "99.50",
                             "Battery_Voltage Encoded value '99.5' is too large for valid inclusive max value of '15.0' on encoding of IEEE754_1985" );

            errors += check( "12.1",
                             "0x4141999a",
                             "01000001010000011001100110011010" );

            errors += check( 5,
                             "0x40a00000",
                             "01000000101000000000000000000000" );

            errors += check( (float)1.25,
                             "0x3fa00000",
                             "00111111101000000000000000000000" );

            errors += check( 10.0,
                             "0x41200000",
                             "01000001001000000000000000000000" );

            errors += check( -1024.5,
                             "Battery_Voltage Encoded value '-1024.5' is too small for valid inclusive min value of '0.0' on encoding of IEEE754_1985" );

            errors += check( "FOOBAR",
                             "Battery_Voltage Invalid String value for uncalibrate IEEE754_1985 of 'FOOBAR'" );

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

            errors += check( "1.25",
                             "0x3ff4000000000000",
                             "0011111111110100000000000000000000000000000000000000000000000000" );

            errors += check( "12.1",
                             "0x4028333333333333",
                             "0100000000101000001100110011001100110011001100110011001100110011" );

            errors += check( 5,
                             "0x4014000000000000",
                             "0100000000010100000000000000000000000000000000000000000000000000" );

            errors += check( (float)1.25,
                             "0x3ff4000000000000",
                             "0011111111110100000000000000000000000000000000000000000000000000" );

            errors += check( 10.0,
                             "0x4024000000000000",
                             "0100000000100100000000000000000000000000000000000000000000000000" );

            errors += check( -1024000.5,
                             "0xc12f400100000000",
                             "1100000100101111010000000000000100000000000000000000000000000000" );

            errors += check( "FOOBAR",
                             "Basic_Float64 Invalid String value for uncalibrate IEEE754_1985 of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesRaw128() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw ieee128 not yet supported" );

            getParameterItemValueObj2( "/UNIT_TEST",
                                       "FLOAT_IEEE_UNSUPPORTED_RAW" );

            errors += check( "0.0", "FLOAT_IEEE_UNSUPPORTED_RAW Raw encoding type not yet supported: IEEE754_1985 (128 bits)" );

            errors += check( (float)0.0, "FLOAT_IEEE_UNSUPPORTED_RAW Raw encoding type not yet supported: IEEE754_1985 (128 bits)" );

            errors += check( 0.0, "FLOAT_IEEE_UNSUPPORTED_RAW Raw encoding type not yet supported: IEEE754_1985 (128 bits)" );

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

            errors += check( "0.0", "FLOAT_IEEE_INVALID_RAW Raw encoding for type named: IEEE754_1985 (23 bits)" );

            errors += check( (float)0.0, "FLOAT_IEEE_INVALID_RAW Raw encoding for type named: IEEE754_1985 (23 bits)" );

            errors += check( 0.0, "FLOAT_IEEE_INVALID_RAW Raw encoding for type named: IEEE754_1985 (23 bits)" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testMilStdParameterTypesRaw16() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw milstd16 no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_MilFloat16" );

            errors += check( "0.0",
                             "0x0000",
                             "0000000000000000" );

            errors += check( (float)0.0,
                             "0x0000",
                             "0000000000000000" );

            errors += check( 0.0,
                             "0x0000",
                             "0000000000000000" );

            errors += check( "0.5",
                             "0x4000",
                             "0100000000000000" );

            errors += check( (float)0.5,
                             "0x4000",
                             "0100000000000000" );

            errors += check( 0.5,
                             "0x4000",
                             "0100000000000000" );

            errors += check( "1.0",
                             "0x4001",
                             "0100000000000001" );

            errors += check( (float)1.0,
                             "0x4001",
                             "0100000000000001" );

            errors += check( 1.0,
                             "0x4001",
                             "0100000000000001" );

            errors += check( "-1.0",
                             "0x8000",
                             "1000000000000000" );

            errors += check( (float)-1.0,
                             "0x8000",
                             "1000000000000000" );

            errors += check( -1.0,
                             "0x8000",
                             "1000000000000000" );

            errors += check( "-12.125",
                             "0x9f04",
                             "1001111100000100" );

            errors += check( (float)-12.125,
                             "0x9f04",
                             "1001111100000100" );

            errors += check( -12.125,
                             "0x9f04",
                             "1001111100000100" );

            errors += check( "12.40625",
                             "0x6344",
                             "0110001101000100" );

            errors += check( (float)12.40625,
                             "0x6344",
                             "0110001101000100" );

            errors += check( 12.40625,
                             "0x6344",
                             "0110001101000100" );

            errors += check( "FOOBAR",
                             "Basic_MilFloat16 Invalid String value for uncalibrate MILSTD_1750A of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testMilStdParameterTypesRaw32() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw milstd32 no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_MilFloat32" );

            errors += check( "0.0",
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( (float)0.0,
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 0.0,
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( "0.5",
                             "0x40000000",
                             "01000000000000000000000000000000" );

            errors += check( (float)0.5,
                             "0x40000000",
                             "01000000000000000000000000000000" );

            errors += check( 0.5,
                             "0x40000000",
                             "01000000000000000000000000000000" );

            errors += check( "1.0",
                             "0x40000001",
                             "01000000000000000000000000000001" );

            errors += check( (float)1.0,
                             "0x40000001",
                             "01000000000000000000000000000001" );

            errors += check( 1.0,
                             "0x40000001",
                             "01000000000000000000000000000001" );

            errors += check( "-1.0",
                             "0x80000000",
                             "10000000000000000000000000000000" );

            errors += check( (float)-1.0,
                             "0x80000000",
                             "10000000000000000000000000000000" );

            errors += check( -1.0,
                             "0x80000000",
                             "10000000000000000000000000000000" );

            errors += check( "0.25",
                             "0x400000ff",
                             "01000000000000000000000011111111" );

            errors += check( (float)0.25,
                             "0x400000ff",
                             "01000000000000000000000011111111" );

            errors += check( 0.25,
                             "0x400000ff",
                             "01000000000000000000000011111111" );

            errors += check( "-25.630001068115234",
                             "0x997ae105",
                             "10011001011110101110000100000101" );

            //errors += check( (float)-25.630001068115234,
            //                 "0x997ae105",
            //                 "10011001011110101110000100000101" );

            errors += check( -25.630001068115234,
                             "0x997ae105",
                             "10011001011110101110000100000101" );

            errors += check( "FOOBAR",
                             "Basic_MilFloat32 Invalid String value for uncalibrate MILSTD_1750A of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testMilStdParameterTypesRaw48() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw milstd48 no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_MilFloat48" );

            errors += check( "0.0",
                             "0x000000000000",
                             "000000000000000000000000000000000000000000000000" );

            errors += check( (float)0.0,
                             "0x000000000000",
                             "000000000000000000000000000000000000000000000000" );

            errors += check( 0.0,
                             "0x000000000000",
                             "000000000000000000000000000000000000000000000000" );

            errors += check( "0.5",
                             "0x400000000000",
                             "010000000000000000000000000000000000000000000000" );

            errors += check( (float)0.5,
                             "0x400000000000",
                             "010000000000000000000000000000000000000000000000" );

            errors += check( 0.5,
                             "0x400000000000",
                             "010000000000000000000000000000000000000000000000" );

            errors += check( "1.0",
                             "0x400000010000",
                             "010000000000000000000000000000010000000000000000" );

            errors += check( (float)1.0,
                             "0x400000010000",
                             "010000000000000000000000000000010000000000000000" );

            errors += check( 1.0,
                             "0x400000010000",
                             "010000000000000000000000000000010000000000000000" );

            errors += check( "-1.0",
                             "0x800000000000",
                             "100000000000000000000000000000000000000000000000" );

            errors += check( (float)-1.0,
                             "0x800000000000",
                             "100000000000000000000000000000000000000000000000" );

            errors += check( -1.0,
                             "0x800000000000",
                             "100000000000000000000000000000000000000000000000" );

            errors += check( "0.25",
                             "0x400000ff0000",
                             "010000000000000000000000111111110000000000000000" );

            errors += check( (float)0.25,
                             "0x400000ff0000",
                             "010000000000000000000000111111110000000000000000" );

            errors += check( 0.25,
                             "0x400000ff0000",
                             "010000000000000000000000111111110000000000000000" );

            errors += check( "105.639485637361",
                             "0x69a3b50754aa",
                             "011010011010001110110101000001110101010010101010" );

            //errors += check( (float)105.639485637361,
            //                 "0x69a3b50754ab",
            //                 "011010011010001110110101000001110101010010101011" );

            errors += check( 105.639485637361,
                             "0x69a3b50754aa",
                             "011010011010001110110101000001110101010010101010" );

            errors += check( "FOOBAR",
                             "Basic_MilFloat48 Invalid String value for uncalibrate MILSTD_1750A of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testMilStdParameterTypesRawInvalidSize() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw milstd39 (bogus)" );

            getParameterItemValueObj2( "/UNIT_TEST",
                                       "FLOAT_MILSTD_INVALID_RAW" );

            errors += check( "0.0", "FLOAT_MILSTD_INVALID_RAW Raw encoding for type named: MILSTD_1750A (39 bits)" );

            errors += check( (float)0.0, "FLOAT_MILSTD_INVALID_RAW Raw encoding for type named: MILSTD_1750A (39 bits)" );

            errors += check( 0.0, "FLOAT_MILSTD_INVALID_RAW Raw encoding for type named: MILSTD_1750A (39 bits)" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        try {
            MilStd1750A milStdObj = new MilStd1750A( BigInteger.ONE, 23 );
            System.out.println( "FAILED: Invalid size for MilStd1750A constructor did not throw" );
            errors++;
        } catch ( IllegalArgumentException ex ) {
            System.out.println( "PASSED: Invalid size for MilStd1750A constructor" );
        }

        try {
            MilStd1750A milStdObj = new MilStd1750A( 0.0, 23 );
            System.out.println( "FAILED: Invalid size for MilStd1750A constructor did not throw" );
            errors++;
        } catch ( IllegalArgumentException ex ) {
            System.out.println( "PASSED: Invalid size for MilStd1750A constructor" );
        }

        Assert.assertFalse( "FLOAT_MILSTD_INVALID_RAW should not report true from hasDefaultCalibrator()",
                            ppp_.hasDefaultCalibrator() );

        Assert.assertFalse( "FLOAT_MILSTD_INVALID_RAW should not report true from hasContextCalibrators()",
                            ppp_.hasContextCalibrators() );

        System.out.println( "" );

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawFloatQuadraticSingleRoot1Calibrator() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float Quadratic Single Root1 Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Quadratic_Demo_SingleRoot" );

            errors += check( "0",
                             "Quadratic_Demo_SingleRoot Polynomial calibrator has no real roots for calibrated value '0'" );

            errors += check( -1,
                             "Quadratic_Demo_SingleRoot Polynomial calibrator has no real roots for calibrated value '-1'" );

            errors += check( 0.0,
                             "Quadratic_Demo_SingleRoot Polynomial calibrator has no real roots for calibrated value '0.0'" );

            errors += check( (float)0.0,
                             "Quadratic_Demo_SingleRoot Polynomial calibrator has no real roots for calibrated value '0.0'" );

            errors += check( 1,
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( "2",
                             "0xbf800000",
                             "10111111100000000000000000000000" );

            errors += check( 3.0,
                             "0xbfb504f3",
                             "10111111101101010000010011110011" );

            errors += check( -5.0,
                             "Quadratic_Demo_SingleRoot Polynomial calibrator has no real roots for calibrated value '-5.0'" );

            errors += check( (float)5.0,
                             "0xc0000000",
                             "11000000000000000000000000000000" );

            errors += check( "0x0a",
                             "0xc0400000",
                             "11000000010000000000000000000000" );

            errors += check( "10.0",
                             "0xc0400000",
                             "11000000010000000000000000000000" );

            errors += check( "20.0",
                             "0xc08b7c1a",
                             "11000000100010110111110000011010" );

            errors += check( 102,
                             "Quadratic_Demo_SingleRoot Encoded value '-10.04987562112089' is too small for valid inclusive min value of '-10.0' on encoding of IEEE754_1985" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

        Assert.assertTrue( "Quadratic_Demo_SingleRoot should not report false from hasDefaultCalibrator()",
                            ppp_.hasDefaultCalibrator() );

        Assert.assertFalse( "Quadratic_Demo_SingleRoot should not report true from hasContextCalibrators()",
                            ppp_.hasContextCalibrators() );

        System.out.println( "" );

    }

    @Test
    public void testFloatParameterTypesWithRawFloatQuadraticSingleRoot2Calibrator() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float Quadratic Single Root2 Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Quadratic_Demo_SingleRoot2" );

            errors += check( "0",
                             "0x40000000",
                             "01000000000000000000000000000000" );

            errors += check( 0.0,
                             "0x40000000",
                             "01000000000000000000000000000000" );

            errors += check( (float)0.0,
                             "0x40000000",
                             "01000000000000000000000000000000" );

            errors += check( 16,
                             "0xc0000000",
                             "11000000000000000000000000000000" );

            errors += check( "16",
                             "0xc0000000",
                             "11000000000000000000000000000000" );

            errors += check( 16.0,
                             "0xc0000000",
                             "11000000000000000000000000000000" );

            errors += check( -2.0,
                             "Quadratic_Demo_SingleRoot2 Polynomial calibrator has no real roots for calibrated value '-2.0'" );

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

            System.out.println( "Testing EU Float Raw float Order 0 Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_Order0_PolyTerms" );

            errors += check( "0",
                             "0xc00ccccd",
                             "11000000000011001100110011001101" );

            errors += check( 0.0,
                             "0xc00ccccd",
                             "11000000000011001100110011001101" );

            errors += check( (float)0.0,
                             "0xc00ccccd",
                             "11000000000011001100110011001101" );

            //errors += check( 16,
            //                 "0xc00ccccd",
            //                 "11000000000011001100110011001101" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testFloatParameterTypesWithRawFloatOrder1Calibrator() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Float Raw float Order 1 (Linear) Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_Order1_PolyTerms" );

            errors += check( "0",
                             "0xbeaaaaab",
                             "10111110101010101010101010101011" );

            errors += check( 0.0,
                             "0xbeaaaaab",
                             "10111110101010101010101010101011" );

            errors += check( (float)0.0,
                             "0xbeaaaaab",
                             "10111110101010101010101010101011" );

            //errors += check( 16,
            //                 "0xc00ccccd",
            //                 "11000000000011001100110011001101" );

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

            errors += check( "0",
                             "Spline_Demo Spline calibrator does not extrapolate and does not bound calibrated value '0'" );

            errors += check( 0.0,
                             "Spline_Demo Spline calibrator does not extrapolate and does not bound calibrated value '0.0'" );

            errors += check( (float)0.0,
                             "Spline_Demo Spline calibrator does not extrapolate and does not bound calibrated value '0.0'" );

            errors += check( 2,
                             "0xc008000000000000",
                             "1100000000001000000000000000000000000000000000000000000000000000" );

            errors += check( "-2",
                             "Spline_Demo Spline calibrator does not extrapolate and does not bound calibrated value '-2'" );

            errors += check( 3.0,
                             "0xbff0000000000000",
                             "1011111111110000000000000000000000000000000000000000000000000000" );

            errors += check( 2.5,
                             "0xc000000000000000",
                             "1100000000000000000000000000000000000000000000000000000000000000" );

            errors += check( -4.0,
                             "Spline_Demo Spline calibrator does not extrapolate and does not bound calibrated value '-4.0'" );

            errors += check( 5.0,
                             "Spline_Demo Spline calibrator does not extrapolate and does not bound calibrated value '5.0'" );

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

            System.out.println( "Testing EU Float Raw float Bad Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_Missing_PolyTerms" );

            errors += check( 5.0,
                             "Float_Missing_PolyTerms No terms specified for the polynomial calibrator." );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_Order3_PolyTerms" );

            errors += check( 5.0,
                             "Float_Order3_PolyTerms Polynomial calibrator has an exponent greater than 2, which is the maximum uncalibration exponent supported. (3)" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_Missing_SplinePoints" );

            errors += check( 5.0,
                             "Float_Missing_SplinePoints Spline calibrator does not have at least 2 points to work from." );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Spline_Demo" );

            errors += check( 1024.0,
                             "Spline_Demo Spline calibrator does not extrapolate and does not bound calibrated value '1024.0'" );

            //getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
            //                          "Spline_Quad_Infinite_Slope" );

            //errors += check( 1.0, // not working and not sure why
            //                 "Spline_Quad_Infinite_Slope" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Float_MathOpCal_3" );

            errors += check( 5.0,
                             "Float_MathOpCal_3 MathOperationCalibrator is not supported.  Returning uncalibrated value." );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    //
    
    /* Test Function Template

    @Test
    public void testTemplate() {

        long errors = 0;

        try {
            
        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    */

    private long check( long eu, String warning ) {

        BitSet result = vvv_.encode( eu );
        return checkWarning( Long.toString( eu ), result, warning );
        
    }

    private long check( double eu, String warning ) {

        BitSet result = vvv_.encode( eu );
        return checkWarning( Double.toString( eu ), result, warning );

    }

    private long check( float eu, String warning ) {

        BitSet result = vvv_.encode( eu );
        return checkWarning( Float.toString( eu ), result, warning );

    }

    private long check( String eu, String warning ) {

        BitSet result = vvv_.encode( eu );
        return checkWarning( eu, result, warning );

    }

    private long check( double eu, String raw, String binary ) {

        BitSet result = vvv_.encode( eu );
        return checkExpected( Double.toString( eu ), raw, binary, result );

    }

    private long check( float eu, String raw, String binary ) {

        BitSet result = vvv_.encode( eu );
        return checkExpected( Float.toString( eu ), raw, binary, result );

    }

    private long check( long eu, String raw, String binary ) {

        BitSet result = vvv_.encode( eu );
        return checkExpected( Long.toString( eu ), raw, binary, result );

    }

    private long check( String eu, String raw, String binary ) {

        BitSet result = vvv_.encode( eu );
        return checkExpected( eu, raw, binary, result );

    }

    private long checkExpected( String eu, String raw, String binary, BitSet result ) {

        long errors = 0;

        StringBuilder msg = new StringBuilder();

        String rawHex = vvv_.bitSetToHex( result );
        String rawBin = vvv_.bitSetToBinary( result );

        if ( ( rawHex.equals( raw )    == false ) ||
             ( rawBin.equals( binary ) == false ) ) {
            ++errors;
            msg.append( "FAILED: " );
            msg.append( vvv_.getItemName() );
            msg.append( " EU/Cal Value '" );
            msg.append( eu );
            msg.append( "' Raw Hex '" );
            msg.append( rawHex );
            msg.append( "' Binary '" );
            msg.append( rawBin );
            msg.append( "' (Expected Raw Hex '" );
            msg.append( raw );
            msg.append( "' Binary '" );
            msg.append( binary );
            msg.append( "')" );
        } else {
            msg.append( "PASSED: " );
            msg.append( vvv_.getItemName() );
            msg.append( " EU/Cal Value '" );
            msg.append( eu );
            msg.append( "' Raw Hex '" );
            msg.append( rawHex );
            msg.append( "' Binary '" );
            msg.append( rawBin );
            msg.append( "'" );
        }

        printWarnings();
        System.out.println( msg.toString() );

        return errors;

    }

    private long checkWarning( String eu, BitSet result, String warning ) {

        long    errors = 0;
        boolean found  = false;

        StringBuilder msg = new StringBuilder();

        String rawHex         = vvv_.bitSetToHex( result );
        String rawBin         = vvv_.bitSetToBinary( result );
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
            msg.append( " Expected Warning " );
            msg.append( warning );
            msg.append( " EU/Cal Value '" );
            msg.append( eu );
            msg.append( "' Raw Hex '" );
            msg.append( rawHex );
            msg.append( "' Binary '" );
            msg.append( rawBin );
            msg.append( "'" );
            printWarnings();
        } else {
            msg.append( "PASSED: " );
            msg.append( vvv_.getItemName() );
            msg.append( " Received Warning " );
            msg.append( warning );
            msg.append( " EU/Cal Value '" );
            msg.append( eu );
            msg.append( "' Raw Hex '" );
            msg.append( rawHex );
            msg.append( "' Binary '" );
            msg.append( rawBin );
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

    private void getParameterItemValueObj( String path, String name )
        throws XTCEDatabaseException {

        ppp_ = db_.getSpaceSystem( path ).getTelemetryParameter( name );

        vvv_ = new XTCEItemValue( ppp_ );
        if ( vvv_.isValid() == false ) {
            throw new XTCEDatabaseException( "Parameter " +
                                             ppp_.getName() +
                                             " missing encoding information" );
        }

    }

    private void getParameterItemValueObj2( String path, String name )
        throws XTCEDatabaseException {

        ppp_ = db2_.getSpaceSystem( path ).getTelemetryParameter( name );

        vvv_ = new XTCEItemValue( ppp_ );
        if ( vvv_.isValid() == false ) {
            throw new XTCEDatabaseException( "Parameter " +
                                             ppp_.getName() +
                                             " missing encoding information" );
        }

    }

    private void loadDocuments() throws XTCEDatabaseException {

        String path = "src/test/resources/org/xtce/toolkit/test";

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
