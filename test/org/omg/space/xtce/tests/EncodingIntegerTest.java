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

package org.omg.space.xtce.tests;

import java.io.File;
import java.util.BitSet;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEDatabaseException;
import org.omg.space.xtce.toolkit.XTCEItemValue;
import org.omg.space.xtce.toolkit.XTCEParameter;

/**
 *
 * @author dovereem
 */

public class EncodingIntegerTest {
    
    public EncodingIntegerTest() {

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
    public void testIntegerParameterTypesRawUnsigned() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        long errors = 0;

        try {

            System.out.println( "Testing EU UnsignedInteger Raw unsigned no Calibrator" );

            getParameterItemValueObj( "/BogusSAT",
                                      "CCSDSAPID" );

            errors += check( "15",
                             "0x000f",
                             "00000001111" );

            errors += check( "1",
                             "0x0001",
                             "00000000001" );

            errors += check( "2047",
                             "0x07ff",
                             "11111111111" );

            errors += check( 5,
                             "0x0005",
                             "00000000101" );

            errors += check( (float)1.25,
                             "CCSDSAPID Invalid Integer value for uncalibrate of '1.25'" );

            errors += check( 10.0,
                             "0x000a",
                             "00000001010" );

            errors += check( -1024.5,
                             "CCSDSAPID Invalid Integer value for uncalibrate of '-1024.5'" );

            errors += check( "-9",
                             "Unsigned value for item CCSDSAPID is -9 which cannot be negative" );

            errors += check( "-1",
                             "Unsigned value for item CCSDSAPID is -1 which cannot be negative" );

            errors += check( 0xff,
                             "0x00ff",
                             "00011111111" );

            errors += check( 0xffff,
                             "unsigned encoding value for item CCSDSAPID is 65535, which is greater than the maximum value 2047" );

            errors += check( "FOOBAR",
                             "CCSDSAPID Invalid Integer value for uncalibrate of 'FOOBAR'" );

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

            errors += check( "0",
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 0,
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 0.0,
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( "1",
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( 1,
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( 1.0,
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( (float)1.0,
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( "2047",
                             "0x000007ff",
                             "00000000000000000000011111111111" );

            errors += check( 5,
                             "0x00000005",
                             "00000000000000000000000000000101" );

            errors += check( (float)1.25,
                             "Basic_int32_signmag Invalid Integer value for uncalibrate of '1.25'" );

            errors += check( "0x0a",
                             "0x0000000a",
                             "00000000000000000000000000001010" );

            errors += check( 10.0,
                             "0x0000000a",
                             "00000000000000000000000000001010" );

            errors += check( -1024.5,
                             "Basic_int32_signmag Invalid Integer value for uncalibrate of '-1024.5'" );

            errors += check( "-1",
                             "0x80000001",
                             "10000000000000000000000000000001" );

            errors += check( -1,
                             "0x80000001",
                             "10000000000000000000000000000001" );

            errors += check( -1.0,
                             "0x80000001",
                             "10000000000000000000000000000001" );

            errors += check( -6,
                             "0x80000006",
                             "10000000000000000000000000000110" );

            errors += check( -6.0,
                             "0x80000006",
                             "10000000000000000000000000000110" );

            errors += check( 0xff,
                             "0x000000ff",
                             "00000000000000000000000011111111" );

            errors += check( "FOOBAR",
                             "Basic_int32_signmag Invalid Integer value for uncalibrate of 'FOOBAR'" );

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

            errors += check( "0",
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 0,
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 0.0,
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( "1",
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( 1,
                            "0x00000001",
                            "00000000000000000000000000000001" );

            errors += check( 1.0,
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( (float)1.0,
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( "2047",
                             "0x000007ff",
                             "00000000000000000000011111111111" );

            errors += check( 5,
                             "0x00000005",
                             "00000000000000000000000000000101" );

            errors += check( (float)1.25,
                             "Basic_int32_twoscomp Invalid Integer value for uncalibrate of '1.25'" );

            errors += check( "0x0a",
                             "0x0000000a",
                             "00000000000000000000000000001010" );

            errors += check( 10.0,
                             "0x0000000a",
                             "00000000000000000000000000001010" );

            errors += check( -1024.5,
                             "Basic_int32_twoscomp Invalid Integer value for uncalibrate of '-1024.5'" );

            errors += check( "-1",
                             "0xffffffff",
                             "11111111111111111111111111111111" );

            errors += check( -1,
                             "0xffffffff",
                             "11111111111111111111111111111111" );

            errors += check( -1.0,
                             "0xffffffff",
                             "11111111111111111111111111111111" );

            errors += check( -6,
                             "0xfffffffa",
                             "11111111111111111111111111111010" );

            errors += check( -6.0,
                             "0xfffffffa",
                             "11111111111111111111111111111010" );

            errors += check( 0xff,
                             "0x000000ff",
                             "00000000000000000000000011111111" );

            errors += check( "FOOBAR",
                             "Basic_int32_twoscomp Invalid Integer value for uncalibrate of 'FOOBAR'" );

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

            errors += check( "0",
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 0,
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( 0.0,
                             "0x00000000",
                             "00000000000000000000000000000000" );

            errors += check( "1",
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( 1,
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( 1.0,
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( (float)1.0,
                             "0x00000001",
                             "00000000000000000000000000000001" );

            errors += check( "2047",
                             "0x000007ff",
                             "00000000000000000000011111111111" );

            errors += check( 5,
                             "0x00000005",
                             "00000000000000000000000000000101" );

            errors += check( (float)1.25,
                             "Basic_int32_onescomp Invalid Integer value for uncalibrate of '1.25'" );

            errors += check( "0x0a",
                             "0x0000000a",
                             "00000000000000000000000000001010" );

            errors += check( 10.0,
                             "0x0000000a",
                             "00000000000000000000000000001010" );

            errors += check( -1024.5,
                             "Basic_int32_onescomp Invalid Integer value for uncalibrate of '-1024.5'" );

            errors += check( "-1",
                             "0xfffffffe",
                             "11111111111111111111111111111110" );

            errors += check( -1,
                             "0xfffffffe",
                             "11111111111111111111111111111110" );

            errors += check( -1.0,
                             "0xfffffffe",
                             "11111111111111111111111111111110" );

            errors += check( -6,
                             "0xfffffff9",
                             "11111111111111111111111111111001" );

            errors += check( -6.0,
                             "0xfffffff9",
                             "11111111111111111111111111111001" );

            errors += check( 0xff,
                             "0x000000ff",
                             "00000000000000000000000011111111" );

            errors += check( "FOOBAR",
                             "Basic_int32_onescomp Invalid Integer value for uncalibrate of 'FOOBAR'" );

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

        //System.out.println( "Loading the BogusSat-1.xml demo database" );

        String file = "src/org/omg/space/xtce/database/BogusSat-1.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;
    private XTCEParameter ppp_ = null;
    private XTCEItemValue vvv_ = null;

}
