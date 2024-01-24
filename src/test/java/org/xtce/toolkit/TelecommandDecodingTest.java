/* Copyright 2017 David Overeem (dovereem@startmail.com)
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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

public class TelecommandDecodingTest {
    
    public TelecommandDecodingTest() {

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
    public void processSimpleBaseTelecommand() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/CCSDSDirectTelecommand";

        // 0x10c7c0030000
        byte[] bytes = new byte[] { (byte)0x10, (byte)0xc7, (byte)0xc0, (byte)0x03, (byte)0x00, (byte)0x00 };

        InputStream stream = new ByteArrayInputStream( bytes );

        try {

            XTCETelecommand tc = db_.getTelecommand( containerName );

            XTCETelecommandContentModel model =
                db_.processTelecommand( tc, stream );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 48 bits",
                sizeInBytes == 48 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "CCSDS_Packet_ID.Version":
                        ++items;
                        checkEntry( entry, "Parameter", "3", "0", "==0{cal}", "0", "", "" );
                        break;
                    case "CCSDS_Packet_ID.Type":
                        ++items;
                        checkEntry( entry, "Parameter", "1", "3", "==TC{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.SecHdrFlag":
                        ++items;
                        checkEntry( entry, "Parameter", "1", "4", "==NotPresent{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.APID":
                        ++items;
                        checkEntry( entry, "Parameter", "11", "5", "==199{cal}", "2047", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.GroupFlags":
                        ++items;
                        checkEntry( entry, "Parameter", "2", "16", "==Standalone{cal}", "Standalone", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.Count":
                        ++items;
                        checkEntry( entry, "Parameter", "14", "18", "==3{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_Length":
                        ++items;
                        checkEntry( entry, "Parameter", "16", "32", "==0{cal}", "", "", "" );
                        break;
                }

            }

            Assert.assertTrue( "Telecommand parameter/argument count of " +
                containerName +
                " is " +
                Long.toString( items ) + " but should be 7 items",
                items == 7 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processTelecommandWithInheritance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName =
            "/BogusSAT/SC001/BusElectronics/Reaction_Wheel_2_On";

        // 0x13ffc06300010201
        byte[] bytes = new byte[] { (byte)0x13, (byte)0xff, (byte)0xc0,
                                    (byte)0x63, (byte)0x00, (byte)0x01,
                                    (byte)0x02, (byte)0x01 };

        InputStream stream = new ByteArrayInputStream( bytes );

        try {

            XTCETelecommand tc = db_.getTelecommand( containerName );

            XTCETelecommandContentModel model =
                db_.processTelecommand( tc, stream );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 64 bits",
                sizeInBytes == 64 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "CCSDS_Packet_ID.Version":
                        ++items;
                        checkEntry( entry, "Parameter", "3", "0", "==0{cal}", "0", "", "" );
                        break;
                    case "CCSDS_Packet_ID.Type":
                        ++items;
                        checkEntry( entry, "Parameter", "1", "3", "==TC{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.SecHdrFlag":
                        ++items;
                        checkEntry( entry, "Parameter", "1", "4", "==NotPresent{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.APID":
                        ++items;
                        checkEntry( entry, "Parameter", "11", "5", "==1023{cal}", "2047", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.GroupFlags":
                        ++items;
                        checkEntry( entry, "Parameter", "2", "16", "==Standalone{cal}", "Standalone", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.Count":
                        ++items;
                        checkEntry( entry, "Parameter", "14", "18", "==99{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_Length":
                        ++items;
                        checkEntry( entry, "Parameter", "16", "32", "==1{cal}", "", "", "" );
                        break;
                    case "RW_UNIT_ID":
                        ++items;
                        checkEntry( entry, "Argument", "8", "48", "==2{cal}", "", "", "" );
                        break;
                    case "RW_PWR_STATE":
                        ++items;
                        checkEntry( entry, "Argument", "8", "56", "==ON{cal}", "", "", "" );
                        break;
                }

            }

            Assert.assertTrue( "Telecommand parameter/argument count of " +
                containerName +
                " is " +
                Long.toString( items ) + " but should be 9 items",
                items == 9 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processTelecommandWithFixedValue() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName =
            "/BogusSAT/SC001/Payload1/Payload_1_Control";

        // 0x1fff80410005810040200000
        byte[] bytes = new byte[] { (byte)0x1f, (byte)0xff, (byte)0x80,
                                    (byte)0x41, (byte)0x00, (byte)0x05,
                                    (byte)0x81, (byte)0x00, (byte)0x40,
                                    (byte)0x20, (byte)0x00, (byte)0x00 };

        InputStream stream = new ByteArrayInputStream( bytes );

        try {

            XTCETelecommand tc = db_.getTelecommand( containerName );

            XTCETelecommandContentModel model =
                db_.processTelecommand( tc, stream );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 96 bits",
                sizeInBytes == 96 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "CCSDS_Packet_ID.Version":
                        ++items;
                        checkEntry( entry, "Parameter", "3", "0", "==0{cal}", "0", "", "" );
                        break;
                    case "CCSDS_Packet_ID.Type":
                        ++items;
                        checkEntry( entry, "Parameter", "1", "3", "==TC{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.SecHdrFlag":
                        ++items;
                        checkEntry( entry, "Parameter", "1", "4", "==Present{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.APID":
                        ++items;
                        checkEntry( entry, "Parameter", "11", "5", "==2047{cal}", "2047", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.GroupFlags":
                        ++items;
                        checkEntry( entry, "Parameter", "2", "16", "==Last{cal}", "Standalone", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.Count":
                        ++items;
                        checkEntry( entry, "Parameter", "14", "18", "==65{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_Length":
                        ++items;
                        checkEntry( entry, "Parameter", "16", "32", "==5{cal}", "", "", "" );
                        break;
                    case "STATE":
                        ++items;
                        checkEntry( entry, "Argument", "8", "48", "==OFF{cal}", "", "", "" );
                        break;
                    case "":
                        if ( entry.getEntryTypeString().equals( "Constant" ) == true ) {
                            ++items;
                            checkEntry( entry, "Constant", "8", "56", "==240{cal}", "240", "", "" );
                        }
                        break;
                    case "OUTPUT_POWER":
                        ++items;
                        checkEntry( entry, "Argument", "32", "64", "==2.5{cal}", "", "", "" );
                        break;
                }

            }

            Assert.assertTrue( "Telecommand parameter/argument count of " +
                containerName +
                " is " +
                Long.toString( items ) + " but should be 10 items",
                items == 10 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    private void checkEntry( XTCEContainerContentEntry entry,
                             String                    entryType,
                             String                    sizeInBits,
                             String                    startBit,
                             String                    value,
                             String                    initialValue,
                             String                    condition,
                             String                    repeat ) {

        if ( entry.getEntryTypeString().equals( entryType ) == false ) {
            Assert.fail( "Telecommand parameter/argument " + entry.getName() +
                " should be entry type '" + entryType + "' but reports '" +
                entry.getEntryTypeString() + "' instead" );
        }
        if ( entry.getRawSizeInBits().equals( sizeInBits ) == false ) {
            Assert.fail( "Telecommand parameter/argument " + entry.getName() +
                " should be " + sizeInBits + " bits, but it is " +
                entry.getRawSizeInBits() + " bits instead" );
        }

        if ( entry.getStartBit().equals( startBit ) == false ) {
            Assert.fail( "Telecommand parameter/argument " + entry.getName() +
                " should start at bit " + startBit + " but it starts at bit " +
                entry.getStartBit() + " instead" );
            if ( startBit.isEmpty() == true ) {
                Assert.assertTrue( "Telecommand parameter/argument " + entry.getName() +
                    " should report 'not in use' if expected start bit is " +
                    "empty",
                    entry.isCurrentlyInUse() == false );
            }
        }

        String itemValue = "";
        if ( entry.getValue() != null ) {
            itemValue = entry.getValue().toStringWithoutParameter();
        }

        if ( itemValue.equals( value ) == false ) {
            Assert.fail( "Telecommand parameter/argument " + entry.getName() +
                " should have value of '" + value + "' but it is '" +
                itemValue + "' instead" );
        }

        if ( entry.getInitialValue().equals( initialValue ) == false ) {
            Assert.fail( "Telecommand parameter/argument " + entry.getName() +
                " should have initial value of '" + initialValue +
                "' but it is '" + entry.getInitialValue() + "' instead" );
        }

        if ( entry.getConditions().equals( condition ) == false ) {
            Assert.fail( "Telecommand parameter/argument " + entry.getName() +
                " should have conditions of '" + condition +
                "' but it is '" + entry.getConditions() + "' instead" );
        }

        if ( entry.getRepeatParameterInfo().equals( repeat ) == false ) {
            Assert.fail( "Telecommand parameter/argument " + entry.getName() +
                " should have repeat info of '" + repeat +
                "' but it is '" + entry.getRepeatParameterInfo() +
                "' instead" );
        }

        StringBuilder sb = new StringBuilder();
        sb.append( "PASSED: " );
        sb.append( entry.getName() );
        sb.append( " Size: '" );
        sb.append( sizeInBits );
        sb.append( "' StartBit: '" );
        sb.append( startBit );
        sb.append( "' Value: '" );
        sb.append( value );
        sb.append( "' Initial Value: '" );
        sb.append( initialValue );
        sb.append( "' Condition: '" );
        sb.append( condition );
        sb.append( "' Repeat: '" );
        sb.append( repeat );
        sb.append( "'" );

        System.out.println( sb.toString() );

    }

    private void assertOnWarnings( XTCETelecommandContentModel model ) {

        List<String> warnings = model.getWarnings();

        if ( warnings.isEmpty() == false ) {
            Assert.fail( "First warning: " + warnings.get( 0 ) );
        }

    }

    private BitSet readBytesFromFile( String binFilename ) {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {

            File binFile = new File( binFilename );

            InputStream stream = new FileInputStream( binFile );

            int byteValue;

            StringBuilder sb = new StringBuilder( "Read From File 0x" );

            while ( ( byteValue = stream.read() ) != -1 ) {
                buffer.write( byteValue );
                sb.append( String.format( "%02x", byteValue ) );
            }

            System.out.println( sb.toString() );

        } catch ( FileNotFoundException ex ) {
            Assert.fail( "Must be able to read sample binary file: " +
                         binFilename );
        } catch ( IOException ex ) {
            Assert.fail( "Trouble reading file: " + binFilename +
                         " because " + ex.getLocalizedMessage() );
        }

        byte[] bytes = buffer.toByteArray();
        BitSet bits  = XTCEFunctions.getBitSetFromStreamByteArray( bytes );

        //for ( int iii = 0; iii < bits.size(); ++iii ) {
        //    System.out.println( String.format( "Bit %03d %d", iii, bits.get( iii ) ? 1 : 0 ) );

        return bits;

    }

    private void loadDocument() throws XTCEDatabaseException {

        System.out.println( "Loading the BogusSAT-2.xml demo database" );

        String file = "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;

}
