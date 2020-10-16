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
import java.util.BitSet;
import java.util.List;
import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author dovereem
 */

public class EncodingEnumeratedTest {
    
    public EncodingEnumeratedTest() {

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
    public void testEnumeratedParameterTypeUnsignedEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Enumerated Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Payload_1_Phase" );

            errors += check( "TEST", "0x01", "0001" );
            errors += check( "STANDBY", "0x03", "0011" );
            errors += check( "FAILED", "0x06", "0110" );
            errors += check( "FOOBAR", "Payload_1_Phase Invalid EU Enumeration value of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testEnumeratedParameterTypeSignMagnitudeEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Enumerated Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "enum_int16_signmag" );

            errors += check( "TEST",    "0x0001", "0000000000000001" );
            errors += check( "STANDBY", "0x0003", "0000000000000011" );
            errors += check( "FAILED",  "0x0006", "0000000000000110" );
            errors += check( "NEGNUM",  "0x8002", "1000000000000010" );
            errors += check( "FOOBAR",  "enum_int16_signmag Invalid EU Enumeration value of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testEnumeratedParameterTypeTwosComplementEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Enumerated Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "enum_int16_twoscomp" );

            errors += check( "TEST",    "0x0001", "0000000000000001" );
            errors += check( "STANDBY", "0x0003", "0000000000000011" );
            errors += check( "FAILED",  "0x0006", "0000000000000110" );
            errors += check( "NEGNUM",  "0xfffe", "1111111111111110" );
            errors += check( "FOOBAR",  "enum_int16_twoscomp Invalid EU Enumeration value of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testEnumeratedParameterTypeOnesComplementEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Enumerated Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "enum_int16_onescomp" );

            errors += check( "TEST",    "0x0001", "0000000000000001" );
            errors += check( "STANDBY", "0x0003", "0000000000000011" );
            errors += check( "FAILED",  "0x0006", "0000000000000110" );
            errors += check( "NEGNUM",  "0xfffd", "1111111111111101" );
            errors += check( "FOOBAR",  "enum_int16_onescomp Invalid EU Enumeration value of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testEnumeratedParameterTypeBinaryEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Enumerated Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "enum_binary" );

            errors += check( "TEST",    "0x00000001", "00000000000000000000000000000001" );
            errors += check( "STANDBY", "0x00000003", "00000000000000000000000000000011" );
            errors += check( "FAILED",  "0x00000006", "00000000000000000000000000000110" );
            errors += check( "NEGNUM",  "enum_binary Invalid value for binary encoding of '-2'.  Negative values do not have a meaning in a binary context." );
            errors += check( "FOOBAR",  "enum_binary Invalid EU Enumeration value of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testEnumeratedParameterTypeFloatEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Enumerated Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "enum_float32" );

            errors += check( "TEST",    "0x3f800000", "00111111100000000000000000000000" );
            errors += check( "STANDBY", "0x40400000", "01000000010000000000000000000000" );
            errors += check( "FAILED",  "0x40c00000", "01000000110000000000000000000000" );
            errors += check( "NEGNUM",  "0xc0000000", "11000000000000000000000000000000" );
            errors += check( "FOOBAR",  "enum_float32 Invalid EU Enumeration value of 'FOOBAR'" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "enum_float64" );

            errors += check( "TEST",    "0x3ff0000000000000", "0011111111110000000000000000000000000000000000000000000000000000" );
            errors += check( "STANDBY", "0x4008000000000000", "0100000000001000000000000000000000000000000000000000000000000000" );
            errors += check( "FAILED",  "0x4018000000000000", "0100000000011000000000000000000000000000000000000000000000000000" );
            errors += check( "NEGNUM",  "0xc000000000000000", "1100000000000000000000000000000000000000000000000000000000000000" );
            errors += check( "FOOBAR",  "enum_float64 Invalid EU Enumeration value of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testEnumeratedParameterTypeStringEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU Enumerated Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "enum_string" );

            errors += check( "TEST",    "0x31000000", "00110001000000000000000000000000" );
            errors += check( "STANDBY", "0x33000000", "00110011000000000000000000000000" );
            errors += check( "FAILED",  "0x36000000", "00110110000000000000000000000000" );
            errors += check( "NEGNUM",  "0x2d320000", "00101101001100100000000000000000" );
            errors += check( "FOOBAR",  "enum_string Invalid EU Enumeration value of 'FOOBAR'" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

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

    private void loadDocument() throws XTCEDatabaseException {

        System.out.println( "Loading the BogusSAT-2.xml demo database" );

        String file = "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;
    private XTCEParameter ppp_ = null;
    private XTCEItemValue vvv_ = null;

}
