/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.tests;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEDatabaseException;
import org.omg.space.xtce.toolkit.XTCEItemValue;
import org.omg.space.xtce.toolkit.XTCEParameter;

/** This class exercises the XTCEItemValue class to demonstrate the encode()
 * methods work with the sample BogusSAT-1.xml XTCE file.
 *
 * @author b1053583
 */

public class TestEncode {

    /** Main
     *
     * @param args the command line arguments
     *
     */

    public static void main( String[] args ) {

        System.out.println( "Running TestEncode For XTCEItemValue" );

        long errors = 0;

        try {

            TestEncode test = new TestEncode();

            test.loadDocument();

            errors += test.testBooleanParameterTypes();
            errors += test.testBinaryParameterTypes();
            errors += test.testEnumeratedParameterTypes();
            errors += test.testStringParameterTypes();
            errors += test.testFloatParameterTypes();
            errors += test.testIntegerParameterTypes();
            errors += test.testFloatParameterTypesWithCalibrators();

            System.out.println( "Errors: " + Long.toString( errors ) );
            System.out.println( "Done" );

        } catch ( Exception ex ) {

            System.out.println( "Exception: " + ex.getLocalizedMessage() );
            ex.printStackTrace();

        }

    }

    private long testBooleanParameterTypes() throws XTCEDatabaseException {

        long errors = 0;

        System.out.println( "Testing EU Boolean Raw unsigned no Calibrator" );

        getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                  "Battery_Charge_Mode" );

        errors += check( "CHARGE", "0x01", "1" );
        errors += check( "DISCHARGE", "0x00", "0" );
        errors += check( "FOOBAR", "Battery_Charge_Mode Invalid EU Boolean value of 'FOOBAR'" );

        System.out.println( "" );

        return errors;

    }

    private long testEnumeratedParameterTypes() throws XTCEDatabaseException {

        long errors = 0;

        System.out.println( "Testing EU Enumerated Raw unsigned no Calibrator" );

        getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                  "Payload_1_Phase" );

        errors += check( "TEST", "0x01", "0001" );
        errors += check( "STANDBY", "0x03", "0011" );
        errors += check( "FAILED", "0x06", "0110" );
        errors += check( "FOOBAR", "Payload_1_Phase Invalid EU Enumeration value of 'FOOBAR'" );

        System.out.println( "" );

        return errors;

    }

    private long testStringParameterTypes() throws XTCEDatabaseException {

        long errors = 0;

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

        return errors;

    }

    private long testBinaryParameterTypes() throws XTCEDatabaseException {

        long errors = 0;

        System.out.println( "Testing EU Binary Raw binary" );

        getParameterItemValueObj( "/BogusSAT/SC001/Payload1",
                                  "Payload_MD5" );

        errors += check( "0xa567e0660841dc13346047aa5ac2b5c7",
                         "0xa567e0660841dc13346047aa5ac2b5c7",
                         "10100101011001111110000001100110000010000100000111011100000100110011010001100000010001111010101001011010110000101011010111000111" );
        
        errors += check( "0xaaaabbbb",
                         "0x000000000000000000000000aaaabbbb",
                         "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010101010101010101011101110111011" );

        errors += check( 12345678,
                         "0x00000000000000000000000000bc614e",
                         "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000101111000110000101001110" );

        errors += check( 12345678.0, "Payload_MD5 Invalid EU Binary value of '1.2345678E7'" );

        errors += check( (float)1.0, "Payload_MD5 Invalid EU Binary value of '1.0'" );

        errors += check( "FOOBAR", "Payload_MD5 Invalid String value for encoding binary of 'FOOBAR'" );

        System.out.println( "" );

        return errors;

    }

    private long testFloatParameterTypes() throws XTCEDatabaseException {

        long errors = 0;

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
                         "Battery_Voltage Invalid String value for encoding IEEE754_1985 of 'FOOBAR'" );

        System.out.println( "" );

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
                         "Basic_Float64 Invalid String value for encoding IEEE754_1985 of 'FOOBAR'" );

        System.out.println( "" );

        return errors;

    }

    private long testIntegerParameterTypes() throws XTCEDatabaseException {

        long errors = 0;

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
                         "CCSDSAPID Invalid EU unsigned integer value of '1.25'" );

        errors += check( 10.0,
                         "0x000a",
                         "00000001010" );

        errors += check( -1024.5,
                         "CCSDSAPID Invalid EU unsigned integer value of '-1024.5'" );

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
                         "CCSDSAPID Invalid String value for encoding unsigned of 'FOOBAR'" );

        System.out.println( "" );

        return errors;

    }

    private long testFloatParameterTypesWithCalibrators() throws XTCEDatabaseException {

        long errors = 0;

        System.out.println( "Testing EU Float Raw unsigned Linear Calibrator" );

        getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                  "Solar_Array_Voltage_1" );

        errors += check( "-100",
                         "0x0000",
                         "000000000000" );

        errors += check( "-50",
                         "0x01f4",
                         "000111110100" );

        errors += check( "0",
                         "0x03e8",
                         "001111101000" );

        errors += check( 5,
                         "0x041a",
                         "010000011010" );

        errors += check( 1024,
                         "unsigned encoding value for item Solar_Array_Voltage_1 is 11240, which is greater than the maximum value 4000" );

        errors += check( 1.2,
                         "0x03f4",
                         "001111110100" );

        errors += check( -200.0,
                         "Unsigned value for item Solar_Array_Voltage_1 is -1000 which cannot be negative" );

        errors += check( 50000.0,
                         "unsigned encoding value for item Solar_Array_Voltage_1 is 501000, which is greater than the maximum value 4000" );

        errors += check( "0x2",
                         "0x03fc",
                         "001111111100" );

        errors += check( "-1.0",
                         "0x03de",
                         "001111011110" );

        System.out.println( "" );

        System.out.println( "Testing EU Float Raw unsigned Quadratic Calibrator" );

        getParameterItemValueObj( "/BogusSAT/SC001/BusElectronics",
                                  "Quadratic_Demo" );

        errors += check( "-100",
                         "Polynomial Calibrator for Quadratic_Demo has no real roots for EU value -100" );

        errors += check( "-50",
                         "Polynomial Calibrator for Quadratic_Demo has no real roots for EU value -50" );

        errors += check( "0",
                         "0x0002",
                         "0000000000000010" );

        errors += check( 5,
                         "0x0003",
                         "0000000000000011" );

        errors += check( 1024,
                         "0x001f",
                         "0000000000011111" );

        errors += check( 21.2,
                         "0x0004",
                         "0000000000000100" );

        errors += check( -200.0,
                         "Polynomial Calibrator for Quadratic_Demo has no real roots for EU value -200.0" );

        errors += check( 50000.0,
                         "0x00df",
                         "0000000011011111" );

        errors += check( "0x2",
                         "0x0002",
                         "0000000000000010" );

        errors += check( "-1.0",
                         "0x0002",
                         "0000000000000010" );

        System.out.println( "" );
        return errors;

    }

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

        String rawHex = vvv_.bitSetToHex( result );
        String rawBin = vvv_.bitSetToBinary( result );
        ArrayList<String> warnings = vvv_.getWarnings();

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

        System.out.println( "Loading the BogusSat-1.xml demo database" );

        String file = "src/org/omg/space/xtce/database/BogusSat-1.xml";

        db_ = new XTCEDatabase( new File( file ), false, null );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;
    private XTCEParameter ppp_ = null;
    private XTCEItemValue vvv_ = null;

}
