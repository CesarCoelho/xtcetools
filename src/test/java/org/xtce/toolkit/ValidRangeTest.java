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

public class ValidRangeTest {
    
    public ValidRangeTest() {

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
    public void testParameterRangeAllPositive() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "INT_ALLPOS_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEValidRange vrng = parameter.getValidRange();

            if ( vrng.isValidRangeApplied() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have valid range applied true" );
            }

            if ( vrng.isHighValueCalibrated() == true ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have uncalibrated high valid range" );
            }

            if ( vrng.isLowValueCalibrated() == true ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have uncalibrated low valid range" );
            }

            if ( vrng.isHighValueInclusive() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have inclusive high valid range" );
            }

            if ( vrng.isLowValueInclusive() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have inclusive low valid range" );
            }

            if ( vrng.getHighValue().equals( "99" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect high range value" );
            }

            if ( vrng.getLowValue().equals( "2" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect low range value" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterRangeAllNegative() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "INT_ALLNEG_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEValidRange vrng = parameter.getValidRange();

            if ( vrng.isValidRangeApplied() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have valid range applied true" );
            }

            if ( vrng.isHighValueCalibrated() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have calibrated high valid range" );
            }

            if ( vrng.isLowValueCalibrated() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have calibrated low valid range" );
            }

            if ( vrng.isHighValueInclusive() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have inclusive high valid range" );
            }

            if ( vrng.isLowValueInclusive() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have inclusive low valid range" );
            }

            if ( vrng.getHighValue().equals( "-10" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect high range value" );
            }

            if ( vrng.getLowValue().equals( "-25" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect low range value" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterRangeAllPositiveIncExc() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "FLOAT_ALLPOS_INCEXC_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEValidRange vrng = parameter.getValidRange();

            if ( vrng.isValidRangeApplied() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have valid range applied true" );
            }

            if ( vrng.isHighValueCalibrated() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have calibrated high valid range" );
            }

            if ( vrng.isLowValueCalibrated() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have calibrated low valid range" );
            }

            if ( vrng.isHighValueInclusive() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have inclusive high valid range" );
            }

            if ( vrng.isLowValueInclusive() == true ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have exclusive low valid range" );
            }

            if ( vrng.getHighValue().equals( "11.75" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect high range value" );
            }

            if ( vrng.getLowValue().equals( "2.5" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect low range value" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterRangeAllNegativeIncExc() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "FLOAT_ALLNEG_INCEXC_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEValidRange vrng = parameter.getValidRange();

            if ( vrng.isValidRangeApplied() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have valid range applied true" );
            }

            if ( vrng.isHighValueCalibrated() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have calibrated high valid range" );
            }

            if ( vrng.isLowValueCalibrated() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have calibrated low valid range" );
            }

            if ( vrng.isHighValueInclusive() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have inclusive high valid range" );
            }

            if ( vrng.isLowValueInclusive() == true ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have exclusive low valid range" );
            }

            if ( vrng.getHighValue().equals( "-10.0" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect high range value" );
            }

            if ( vrng.getLowValue().equals( "-25.0" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect low range value" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterRangeAllPositiveExcInc() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "FLOAT_ALLPOS_EXCINC_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEValidRange vrng = parameter.getValidRange();

            if ( vrng.isValidRangeApplied() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have valid range applied true" );
            }

            if ( vrng.isHighValueCalibrated() == true ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have uncalibrated high valid range" );
            }

            if ( vrng.isLowValueCalibrated() == true ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have uncalibrated low valid range" );
            }

            if ( vrng.isHighValueInclusive() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have inclusive high valid range" );
            }

            if ( vrng.isLowValueInclusive() == true ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have exclusive low valid range" );
            }

            if ( vrng.getHighValue().equals( "11.75" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect high range value" );
            }

            if ( vrng.getLowValue().equals( "2.5" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect low range value" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterRangeAllNegativeExcInc() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "FLOAT_ALLNEG_EXCINC_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEValidRange vrng = parameter.getValidRange();

            if ( vrng.isValidRangeApplied() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have valid range applied true" );
            }

            if ( vrng.isHighValueCalibrated() == true ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have uncalibrated high valid range" );
            }

            if ( vrng.isLowValueCalibrated() == true ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have uncalibrated low valid range" );
            }

            if ( vrng.isHighValueInclusive() == true ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have exclusive high valid range" );
            }

            if ( vrng.isLowValueInclusive() == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " should have inclusive low valid range" );
            }

            if ( vrng.getHighValue().equals( "-10.0" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect high range value" );
            }

            if ( vrng.getLowValue().equals( "-25.0" ) == false ) {
                Assert.fail( "Parameter " + parameterName +
                             " returns incorrect low range value" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterCalibrateWarningAllPositiveIncExc() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "FLOAT_ALLPOS_INCEXC_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEItemValue value = new XTCEItemValue( parameter );

            String calValue;
            List<String> warnings;

            calValue = value.getCalibratedFromUncalibrated( "5.0" );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                System.out.println( warnings.get( 0 ) );
                Assert.fail( parameterName + " value '5.0' should not warn" );
            }

            calValue = value.getCalibratedFromUncalibrated( "2.0" );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '2.0' should warn" );
            }

            calValue = value.getCalibratedFromUncalibrated( "2.5" );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '2.5' should warn" );
            }

            calValue = value.getCalibratedFromUncalibrated( "11.75" );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                System.out.println( warnings.get( 0 ) );
                Assert.fail( parameterName + " value '11.75' should not warn" );
            }

            calValue = value.getCalibratedFromUncalibrated( "20.0" );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '20.0' should warn" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterCalibrateWarningAllNegativeIncExc() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "FLOAT_ALLNEG_INCEXC_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEItemValue value = new XTCEItemValue( parameter );

            String calValue;
            List<String> warnings;

            calValue = value.getCalibratedFromUncalibrated( "-11.5" );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                System.out.println( warnings.get( 0 ) );
                Assert.fail( parameterName + " value '-11.5' should not warn" );
            }

            calValue = value.getCalibratedFromUncalibrated( "-44.5" );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '-44.5' should warn" );
            }

            calValue = value.getCalibratedFromUncalibrated( "-25.0" );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '-25.0' should warn" );
            }

            calValue = value.getCalibratedFromUncalibrated( "-10.0" );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                System.out.println( warnings.get( 0 ) );
                Assert.fail( parameterName + " value '-10.0' should not warn" );
            }

            calValue = value.getCalibratedFromUncalibrated( "3.2" );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '3.2' should warn" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterRawWarningAllPositive() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "INT_ALLPOS_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEItemValue value = new XTCEItemValue( parameter );

            String calValue;
            List<String> warnings;
            BitSet rawValue;

            rawValue = value.encodeRawBits( BigInteger.valueOf( 5 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                for ( String warning : warnings ) {
                    System.out.println( warning );
                }
                Assert.fail( parameterName + " value '5' should not warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 2 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                for ( String warning : warnings ) {
                    System.out.println( warning );
                }
                Assert.fail( parameterName + " value '2' should not warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 99 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                for ( String warning : warnings ) {
                    System.out.println( warning );
                }
                Assert.fail( parameterName + " value '99' should not warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 100 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '100' should warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 1 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '1' should warn" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterRawWarningAllNegative() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "INT_ALLNEG_RANGE_RAW";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEItemValue value = new XTCEItemValue( parameter );

            String calValue;
            List<String> warnings;
            BitSet rawValue;

            rawValue = value.encodeRawBits( BigInteger.valueOf( -15 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            //System.out.println( "bits " + XTCEFunctions.bitSetToHex( rawValue, 1 ) );
            //System.out.println( "uncal " + calValue );
            if ( warnings.isEmpty() == false ) {
                for ( String warning : warnings ) {
                    System.out.println( warning );
                }
                Assert.fail( parameterName + " value '-15' should not warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( -25 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            //System.out.println( "bits " + XTCEFunctions.bitSetToHex( rawValue, 1 ) );
            //System.out.println( "uncal " + calValue );
            if ( warnings.isEmpty() == false ) {
                for ( String warning : warnings ) {
                    System.out.println( warning );
                }
                Assert.fail( parameterName + " value '-25' should not warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( -10 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            //System.out.println( "bits " + XTCEFunctions.bitSetToHex( rawValue, 1 ) );
            //System.out.println( "uncal " + calValue );
            if ( warnings.isEmpty() == false ) {
                for ( String warning : warnings ) {
                    System.out.println( warning );
                }
                Assert.fail( parameterName + " value '-10' should not warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( -50 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            //System.out.println( "bits " + XTCEFunctions.bitSetToHex( rawValue, 1 ) );
            //System.out.println( "uncal " + calValue );
            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '-50' should warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( -2 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            //System.out.println( "bits " + XTCEFunctions.bitSetToHex( rawValue, 1 ) );
            //System.out.println( "uncal " + calValue );
            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '-2' should warn" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterRawWarningAllPositiveExcInc() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "FLOAT_ALLPOS_EXCINC_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEItemValue value = new XTCEItemValue( parameter );

            String calValue;
            List<String> warnings;
            BitSet rawValue;

            rawValue = value.encodeRawBits( BigInteger.valueOf( 0x40a00000 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                for ( String warning : warnings ) {
                    System.out.println( warning );
                }
                Assert.fail( parameterName + " value '5.0' should not warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 0x40000000 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '2.0' should warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 0x40200000 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '2.5' should warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 0x413c0000 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                for ( String warning : warnings ) {
                    System.out.println( warning );
                }
                Assert.fail( parameterName + " value '11.75' should not warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 0x41a00000 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '20.0' should warn" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterRawWarningAllNegativeExcInc() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String parameterName = "FLOAT_ALLNEG_EXCINC_RANGE";

        try {

            XTCEParameter parameter = db_.
                                      getRootSpaceSystem().
                                      getTelemetryParameter( parameterName );

            XTCEItemValue value = new XTCEItemValue( parameter );

            String calValue;
            List<String> warnings;
            BitSet rawValue;

            rawValue = value.encodeRawBits( BigInteger.valueOf( 0xc1700000 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                for ( String warning : warnings ) {
                    System.out.println( warning );
                }
                Assert.fail( parameterName + " value '-15.0' should not warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 0x40000000 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '2.0' should warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 0xc1200000 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '-10.0' should warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 0xc1c80000 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == false ) {
                for ( String warning : warnings ) {
                    System.out.println( warning );
                }
                Assert.fail( parameterName + " value '-25.0' should not warn" );
            }

            rawValue = value.encodeRawBits( BigInteger.valueOf( 0xbfc00000 ) );
            calValue = value.getUncalibratedFromRaw( rawValue );
            warnings = value.getWarnings();

            if ( warnings.isEmpty() == true ) {
                Assert.fail( parameterName + " value '-1.5' should warn" );
            }

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    private XTCEDatabase db_ = null;

}
