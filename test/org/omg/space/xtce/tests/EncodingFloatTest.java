/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

public class EncodingFloatTest {
    
    public EncodingFloatTest() {

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
                             "IEEE754_1985 encoding value for item Battery_Voltage is 99.5, which is greater than the maximum value 15.0" );

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
                             "IEEE754_1985 encoding value for item Battery_Voltage is -1024.5, which is less than the minimum value 0.0" );

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
                             "Polynomial Calibrator for Quadratic_Demo_SingleRoot has no real roots for EU value 0" );

            errors += check( -1,
                             "Polynomial Calibrator for Quadratic_Demo_SingleRoot has no real roots for EU value -1" );

            errors += check( 0.0,
                             "Polynomial Calibrator for Quadratic_Demo_SingleRoot has no real roots for EU value 0.0" );

            errors += check( (float)0.0,
                             "Polynomial Calibrator for Quadratic_Demo_SingleRoot has no real roots for EU value 0.0" );

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
                             "Polynomial Calibrator for Quadratic_Demo_SingleRoot has no real roots for EU value -5.0" );

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
                             "IEEE754_1985 encoding value for item Quadratic_Demo_SingleRoot is -10.04987562112089, which is less than the minimum value -10.0" );

            System.out.println( "" );

        } catch ( Throwable ex ) {
            Assert.fail( ex.getLocalizedMessage() );
        }

        if ( errors != 0 ) {
            Assert.fail( "Not all checks passed" );
        }

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
                             "Polynomial Calibrator for Quadratic_Demo_SingleRoot2 has no real roots for EU value -2.0" );

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
                             "Spline Calibrator for Spline_Demo does not bound calibrated value 0 and extrapolate is false" );

            errors += check( 0.0,
                             "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            errors += check( (float)0.0,
                             "Spline Calibrator for Spline_Demo does not bound calibrated value 0.0 and extrapolate is false" );

            errors += check( 2,
                             "0xc008000000000000",
                             "1100000000001000000000000000000000000000000000000000000000000000" );

            errors += check( "-2",
                             "Spline Calibrator for Spline_Demo does not bound calibrated value -2 and extrapolate is false" );

            errors += check( 3.0,
                             "0xbff0000000000000",
                             "1011111111110000000000000000000000000000000000000000000000000000" );

            errors += check( 2.5,
                             "0xc000000000000000",
                             "1100000000000000000000000000000000000000000000000000000000000000" );

            errors += check( -4.0,
                             "Spline Calibrator for Spline_Demo does not bound calibrated value -4.0 and extrapolate is false" );

            errors += check( 5.0,
                             "Spline Calibrator for Spline_Demo does not bound calibrated value 5.0 and extrapolate is false" );

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
