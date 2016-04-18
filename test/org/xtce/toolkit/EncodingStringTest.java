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
import java.util.BitSet;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

/**
 *
 * @author dovereem
 */

public class EncodingStringTest {
    
    public EncodingStringTest() {

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
    public void testStringParameterTypeUTF8Encoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU String Raw UTF-8" );

            getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                      "Bus_Fault_Message" );

            errors += check( "This is a test",
                             "0x54686973206973206120746573740000",
                             "01010100011010000110100101110011001000000110100101110011001000000110000100100000011101000110010101110011011101000000000000000000" );

            errors += check( "",
                             "0x00000000000000000000000000000000",
                             "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" );

            errors += check( "12345678901234567890",
                             "0x35363738393031323334353637383930",
                             "00110101001101100011011100111000001110010011000000110001001100100011001100110100001101010011011000110111001110000011100100110000" );

            errors += check( 5,
                             "0x35000000000000000000000000000000",
                             "00110101000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" );

            errors += check( 1.25,
                             "0x312e3235000000000000000000000000",
                             "00110001001011100011001000110101000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" );

            errors += check( (float)99.0,
                             "0x39392e30000000000000000000000000",
                             "00111001001110010010111000110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" );

            errors += check( "Z",
                             "0x5a000000000000000000000000000000",
                             "01011010000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" );

            errors += check( "aaaa12345678901234567890",
                             "0x35363738393031323334353637383930",
                             "00110101001101100011011100111000001110010011000000110001001100100011001100110100001101010011011000110111001110000011100100110000" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testStringParameterTypeUTF16Encoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU String Raw UTF-16" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "CPU_Fault_Message" );

            errors += check( "abc",
                             "0xfeff006100620063",
                             "1111111011111111000000000110000100000000011000100000000001100011" );

            errors += check( "",
                             "0xfeff000000000000",
                             "1111111011111111000000000000000000000000000000000000000000000000" );

            errors += check( "123",
                             "0xfeff003100320033",
                             "1111111011111111000000000011000100000000001100100000000000110011" );

            errors += check( 5,
                             "0xfeff003500000000",
                             "1111111011111111000000000011010100000000000000000000000000000000" );

            errors += check( 1.2,
                             "0xfeff0031002e0032",
                             "1111111011111111000000000011000100000000001011100000000000110010" );

            errors += check( (float)2.0,
                             "0xfeff0032002e0030",
                             "1111111011111111000000000011001000000000001011100000000000110000" );

            errors += check( "Z",
                             "0xfeff005a00000000",
                             "1111111011111111000000000101101000000000000000000000000000000000" );

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
    public void testStringParameterTypeUnsignedEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU String Raw Unsigned No Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_string_uint32" );

            errors += check( "This is a test",
                             "Basic_string_uint32 Invalid String value for encoding unsigned of 'This is a test'" );

            errors += check( "",
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 5,
                             "0x00000005",
                             "00000000000000000000000000000101" );

            errors += check( 1.25,
                             "Basic_string_uint32 Invalid Decimal value for encoding unsigned of '1.25'" );

            errors += check( (float)99.0,
                             "0x00000063",
                             "00000000000000000000000001100011" );

            errors += check( "99.0",
                             "0x00000063",
                             "00000000000000000000000001100011" );

            errors += check( "12345678901234567890",
                             "Basic_string_uint32 overflow value '12345678901234567890', larger than available encoding bits." );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testStringParameterTypeSignMagnitudeEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU String Raw Unsigned No Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_string_int32_signmag" );

            errors += check( "This is a test",
                             "Basic_string_int32_signmag Invalid String value for encoding signMagnitude of 'This is a test'" );

            errors += check( "",
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 5,
                             "0x00000005",
                             "00000000000000000000000000000101" );

            errors += check( -6,
                             "0x80000006",
                             "10000000000000000000000000000110" );

            errors += check( 1.25,
                             "Basic_string_int32_signmag Invalid Decimal value for encoding signMagnitude of '1.25'" );

            errors += check( (float)99.0,
                             "0x00000063",
                             "00000000000000000000000001100011" );

            errors += check( "99.0",
                             "0x00000063",
                             "00000000000000000000000001100011" );

            errors += check( "12345678901234567890",
                             "Basic_string_int32_signmag overflow value '12345678901234567890', larger than maximum value for encoding." );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testStringParameterTypetwosComplementEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU String Raw twosComplement No Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_string_int32_twoscomp" );

            errors += check( "This is a test",
                             "Basic_string_int32_twoscomp Invalid String value for encoding twosComplement of 'This is a test'" );

            errors += check( "",
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 5,
                             "0x00000005",
                             "00000000000000000000000000000101" );

            errors += check( -6,
                             "0xfffffffa",
                             "11111111111111111111111111111010" );

            errors += check( 1.25,
                             "Basic_string_int32_twoscomp Invalid Decimal value for encoding twosComplement of '1.25'" );

            errors += check( (float)99.0,
                             "0x00000063",
                             "00000000000000000000000001100011" );

            errors += check( "99.0",
                             "0x00000063",
                             "00000000000000000000000001100011" );

            errors += check( "12345678901234567890",
                             "Basic_string_int32_twoscomp overflow value '12345678901234567890', larger than maximum value for encoding." );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testStringParameterTypeonesComplementEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU String Raw onesComplement No Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_string_int32_onescomp" );

            errors += check( "This is a test",
                             "Basic_string_int32_onescomp Invalid String value for encoding onesComplement of 'This is a test'" );

            errors += check( "",
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 5,
                             "0x00000005",
                             "00000000000000000000000000000101" );

            errors += check( -6,
                             "0xfffffff9",
                             "11111111111111111111111111111001" );

            errors += check( 1.25,
                             "Basic_string_int32_onescomp Invalid Decimal value for encoding onesComplement of '1.25'" );

            errors += check( (float)99.0,
                             "0x00000063",
                             "00000000000000000000000001100011" );

            errors += check( "99.0",
                             "0x00000063",
                             "00000000000000000000000001100011" );

            errors += check( "12345678901234567890",
                             "Basic_string_int32_onescomp overflow value '12345678901234567890', larger than maximum value for encoding." );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Test
    public void testStringParameterTypeFloatEncoding() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU String Raw float No Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_string_float32" );

            errors += check( "This is a test",
                             "Basic_string_float32 Invalid String value for encoding IEEE754_1985 of 'This is a test'" );

            errors += check( "",
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 5,
                             "0x40a00000",
                             "01000000101000000000000000000000" );

            errors += check( 1.25,
                             "0x3fa00000",
                             "00111111101000000000000000000000" );

            errors += check( (float)99.0,
                             "0x42c60000",
                             "01000010110001100000000000000000" );


            errors += check( "12345678901234567890",
                             "0x5f2b54aa",
                             "01011111001010110101010010101010" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_string_float64" );

            errors += check( "This is a test",
                             "Basic_string_float64 Invalid String value for encoding IEEE754_1985 of 'This is a test'" );

            errors += check( "",
                             "0x0000000000000000",
                             "0000000000000000000000000000000000000000000000000000000000000000" );

            errors += check( 5,
                             "0x4014000000000000",
                             "0100000000010100000000000000000000000000000000000000000000000000" );

            errors += check( 1.25,
                             "0x3ff4000000000000",
                             "0011111111110100000000000000000000000000000000000000000000000000" );

            errors += check( (float)99.0,
                             "0x4058c00000000000",
                             "0100000001011000110000000000000000000000000000000000000000000000" );


            errors += check( "12345678901234567890",
                             "0x43e56a95319d63e1",
                             "0100001111100101011010101001010100110001100111010110001111100001" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

    }

    @Ignore( "not ready yet" )
    @Test
    public void testStringParameterTypeBinaryEncoding() {

        // this test is a placeholder until I can figure out what it should be

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU String Raw float No Calibrator" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_string_binary" );

            errors += check( "This is a test",
                             "Basic_string_binary Invalid String value for encoding binary of 'This is a test'" );

            errors += check( "",
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 5,
                             "0x40a00000",
                             "01000000101000000000000000000000" );

            errors += check( 1.25,
                             "0x3fa00000",
                             "00111111101000000000000000000000" );

            errors += check( (float)99.0,
                             "0x42c60000",
                             "01000010110001100000000000000000" );


            errors += check( "12345678901234567890",
                             "0x5f2b54aa",
                             "01011111001010110101010010101010" );

            getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                      "Basic_string_binary" );

            errors += check( "This is a test",
                             "Basic_string_binary Invalid String value for encoding binary of 'This is a test'" );

            errors += check( "",
                             "0x0000000000000000",
                             "0000000000000000000000000000000000000000000000000000000000000000" );

            errors += check( 5,
                             "0x4014000000000000",
                             "0100000000010100000000000000000000000000000000000000000000000000" );

            errors += check( 1.25,
                             "0x3ff4000000000000",
                             "0011111111110100000000000000000000000000000000000000000000000000" );

            errors += check( (float)99.0,
                             "0x4058c00000000000",
                             "0100000001011000110000000000000000000000000000000000000000000000" );


            errors += check( "12345678901234567890",
                             "0x43e56a95319d63e1",
                             "0100001111100101011010101001010100110001100111010110001111100001" );

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

        String file = "src/org/xtce/toolkit/database/examples/BogusSAT-2.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;
    private XTCEParameter ppp_ = null;
    private XTCEItemValue vvv_ = null;

}
