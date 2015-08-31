/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omg.space.xtce.tests;

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
import static org.junit.Assert.*;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEDatabaseException;
import org.omg.space.xtce.toolkit.XTCEItemValue;
import org.omg.space.xtce.toolkit.XTCEParameter;

/**
 *
 * @author dovereem
 */
public class DecodingTest {
    
    public DecodingTest() {

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
    public void testRawOverflows() {

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

    @Test
    public void testStringParameterTypesForUTF16() {

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

    private long checkFail( BitSet bits, long raw, String warning ) {

        String result = vvv_.decode( bits );
        return checkWarning( Long.toString( raw ), result, warning );

    }

    private long checkFail( String raw, String warning ) {

        BigInteger rawHex   = new BigInteger( raw.getBytes() );
        BitSet     rawValue = vvv_.encodeRawBits( rawHex );
        String     result   = vvv_.decode( rawValue );
        return checkWarning( "0x" + rawHex.toString( 16 ), result, warning );

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
/*
    private long check( double eu, String raw, String binary ) {

        BitSet result = vvv_.encode( eu );
        return checkExpected( Double.toString( eu ), raw, binary, result );

    }

    private long check( float eu, String raw, String binary ) {

        BitSet result = vvv_.encode( eu );
        return checkExpected( Float.toString( eu ), raw, binary, result );

    }
*/
    private long checkPass( String eu, long raw ) {

        BitSet rawValue = vvv_.encodeRawBits( BigInteger.valueOf( raw ) );
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
            msg.append( "' Raw Hex '" );
            msg.append( raw );
            msg.append( "' (Expected EU '" );
            msg.append( eu );
            msg.append( "')" );
        } else {
            msg.append( "PASSED: " );
            msg.append( vvv_.getItemName() );
            msg.append( " EU/Cal Value '" );
            msg.append( eu );
            msg.append( "' Raw Hex '" );
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

        db_ = new XTCEDatabase( new File( file ), false, false, null );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;
    private XTCEParameter ppp_ = null;
    private XTCEItemValue vvv_ = null;

}
