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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

public class ContainerEncodingTest {
    
    public ContainerEncodingTest() {

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
    public void encodeSimpleContainer() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName =
            "/BogusSAT/SC001/Onboard_Tables/Calibration_Offsets";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-Calibration_Offsets.bin";

        try {

            BitSet fileRawBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, fileRawBits );

            long sizeInBits = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBits ) + " but should be 184 bits",
                sizeInBits == 184 );

            BitSet rawBits = model.encodeContainer();

            StringBuilder fileBitString = new StringBuilder();
            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( fileRawBits.get( iii ) == true ) {
                    fileBitString.append( "1" );
                } else {
                    fileBitString.append( "0" );
                }
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            String fileString = fileBitString.toString();
            String procString = procBitString.toString();

            System.out.println( "File: " + fileString );
            System.out.println( "Proc: " + procString );

            if ( fileString.equals( procString ) == false ) {
                Assert.fail( "File content should have been the same as processed content" );
            }

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void encodeSimpleContainerInitialValues() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName =
            "/BogusSAT/SC001/Onboard_Tables/Calibration_Offsets";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, null, false );

            long sizeInBits = model.getTotalSize();

            byte[] bytes = { (byte)0x40, (byte)0x20, (byte)0x00, (byte)0x00, // 2.5
                             (byte)0x00, (byte)0x00,                         // 2 byte gap
                             (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x02, // 2
                             (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, // 1
                             (byte)0xbf, (byte)0xf8, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // -1.5
                             (byte)0x01 }; // 1=NORMAL

            BitSet fileRawBits = XTCEFunctions.getBitSetFromStreamByteArray( bytes );

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBits ) + " but should be 184 bits",
                sizeInBits == 184 );

            BitSet rawBits = model.encodeContainer();

            StringBuilder fileBitString = new StringBuilder();
            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( fileRawBits.get( iii ) == true ) {
                    fileBitString.append( "1" );
                } else {
                    fileBitString.append( "0" );
                }
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            String fileString = fileBitString.toString();
            String procString = procBitString.toString();

            System.out.println( "Comp: " + fileString );
            System.out.println( "Proc: " + procString );

            if ( fileString.equals( procString ) == false ) {
                Assert.fail( "Compare content should have been the same as processed content" );
            }

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void encodeSimpleContainerAssignedValues() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName =
            "/BogusSAT/SC001/Onboard_Tables/Calibration_Offsets";

        try {

            List<XTCEContainerEntryValue> values = new ArrayList<>();

            values.add( new XTCEContainerEntryValue( db_.getTelemetryParameters( "Battery_Voltage_Offset" ).get( 0 ),
                                                     "90.25",
                                                     "==",
                                                     "Calibrated" ) );


            values.add( new XTCEContainerEntryValue( db_.getTelemetryParameters( "Solar_Array_Voltage_1_Offset" ).get( 0 ),
                                                     "-30",
                                                     "==",
                                                     "Calibrated" ) );

            values.add( new XTCEContainerEntryValue( db_.getTelemetryParameters( "Solar_Array_Voltage_2_Offset" ).get( 0 ),
                                                     "7",
                                                     "==",
                                                     "Calibrated" ) );

            values.add( new XTCEContainerEntryValue( db_.getTelemetryParameters( "Battery_Current_Offset" ).get( 0 ),
                                                     "20.75",
                                                     "==",
                                                     "Calibrated" ) );

            values.add( new XTCEContainerEntryValue( db_.getTelemetryParameters( "Default_CPU_Start_Mode" ).get( 0 ),
                                                     "SAFEHOLD",
                                                     "==",
                                                     "Calibrated" ) );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, values, false );

            long sizeInBits = model.getTotalSize();

            byte[] bytes = { (byte)0x42, (byte)0xb4, (byte)0x80, (byte)0x00, // 90.25 0x42B48000
                             (byte)0x00, (byte)0x00,                         // 2 byte gap
                             (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xe2, // -30 0xFFFFFFE2
                             (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x07, // 7
                             (byte)0x40, (byte)0x34, (byte)0xc0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, // 20.75 0x4034C00000000000
                             (byte)0x02 }; // 1=SAFEHOLD

            BitSet fileRawBits = XTCEFunctions.getBitSetFromStreamByteArray( bytes );

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBits ) + " but should be 184 bits",
                sizeInBits == 184 );

            BitSet rawBits = model.encodeContainer();

            StringBuilder fileBitString = new StringBuilder();
            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( fileRawBits.get( iii ) == true ) {
                    fileBitString.append( "1" );
                } else {
                    fileBitString.append( "0" );
                }
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            String fileString = fileBitString.toString();
            String procString = procBitString.toString();

            System.out.println( "Comp: " + fileString );
            System.out.println( "Proc: " + procString );

            if ( fileString.equals( procString ) == false ) {
                Assert.fail( "Compare content should have been the same as processed content" );
            }

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void encodeContainerWithInheritance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket1";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-CCSDS_SpacePacket1.bin";

        try {

            BitSet fileRawBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, fileRawBits );

            long sizeInBits = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBits ) + " but should be 144 bits",
                sizeInBits == 144 );

            BitSet rawBits = model.encodeContainer();

            StringBuilder fileBitString = new StringBuilder();
            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( fileRawBits.get( iii ) == true ) {
                    fileBitString.append( "1" );
                } else {
                    fileBitString.append( "0" );
                }
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            String fileString = fileBitString.toString();
            String procString = procBitString.toString();

            System.out.println( "File: " + fileString );
            System.out.println( "Proc: " + procString );

            if ( fileString.equals( procString ) == false ) {
                Assert.fail( "File content should have been the same as processed content" );
            }

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void encodeContainerWithFixedRepeat() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket3";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-CCSDS_SpacePacket3.bin";

        try {

            BitSet fileRawBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, fileRawBits );

            long sizeInBits = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBits ) + " but should be 208 bits",
                sizeInBits == 208 );

            BitSet rawBits = model.encodeContainer();

            StringBuilder fileBitString = new StringBuilder();
            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( fileRawBits.get( iii ) == true ) {
                    fileBitString.append( "1" );
                } else {
                    fileBitString.append( "0" );
                }
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            String fileString = fileBitString.toString();
            String procString = procBitString.toString();

            System.out.println( "File: " + fileString );
            System.out.println( "Proc: " + procString );

            if ( fileString.equals( procString ) == false ) {
                Assert.fail( "File content should have been the same as processed content" );
            }

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void encodeContainerWithDynamicRepeat() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_Service_1_Subservice_2";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-ECSS_Service_1_Subservice_2.bin";

        try {

            BitSet fileRawBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, fileRawBits );

            assertOnWarnings( model );

            long sizeInBits = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBits ) + " but should be 272 bits",
                sizeInBits == 272 );

            BitSet rawBits = model.encodeContainer();

            StringBuilder fileBitString = new StringBuilder();
            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( fileRawBits.get( iii ) == true ) {
                    fileBitString.append( "1" );
                } else {
                    fileBitString.append( "0" );
                }
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            String fileString = fileBitString.toString();
            String procString = procBitString.toString();

            System.out.println( "File: " + fileString );
            System.out.println( "Proc: " + procString );

            if ( fileString.equals( procString ) == false ) {
                Assert.fail( "File content should have been the same as processed content" );
            }

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void encodeContainerWithWrongInheritance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket1";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-CCSDS_SpacePacket1-Bad.bin";

        try {

            BitSet fileRawBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, fileRawBits );

            long sizeInBits = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBits ) + " but should be 144 bits",
                sizeInBits == 144 );

            BitSet rawBits = model.encodeContainer();

            StringBuilder fileBitString = new StringBuilder();
            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( fileRawBits.get( iii ) == true ) {
                    fileBitString.append( "1" );
                } else {
                    fileBitString.append( "0" );
                }
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            String fileString = fileBitString.toString();
            String procString = procBitString.toString();

            System.out.println( "File: " + fileString );
            System.out.println( "Proc: " + procString );

            if ( fileString.equals( procString ) == false ) {
                Assert.fail( "File content should have been the same as processed content" );
            }

            if ( model.getWarnings().isEmpty() == true ) {
                Assert.fail( "Expected warning on 'NotPresent' versus 'Present' due to container restriction" );
            } else {
                for ( String warning : model.getWarnings() ) {
                    System.out.println( "Expected Warning: " + warning );
                }
            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void encodeContainerWithInheritanceAndIncludesFalse() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_SpacePacket2";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-ECSS_3_25_HK-ECSS_SpacePacket2-NoInc.bin";

        try {

            BitSet fileRawBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, fileRawBits );

            long sizeInBits = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBits ) + " but should be 192 bits",
                sizeInBits == 192 );

            BitSet rawBits = model.encodeContainer();

            StringBuilder fileBitString = new StringBuilder();
            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( fileRawBits.get( iii ) == true ) {
                    fileBitString.append( "1" );
                } else {
                    fileBitString.append( "0" );
                }
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            String fileString = fileBitString.toString();
            String procString = procBitString.toString();

            System.out.println( "File: " + fileString );
            System.out.println( "Proc: " + procString );

            if ( fileString.equals( procString ) == false ) {
                Assert.fail( "File content should have been the same as processed content" );
            }

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void encodeContainerWithInheritanceAndIncludesTrue() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_SpacePacket2";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-ECSS_3_25_HK-ECSS_SpacePacket2-Inc.bin";

        try {

            BitSet fileRawBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, fileRawBits );

            long sizeInBits = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBits ) + " but should be 208 bits",
                sizeInBits == 208 );

            BitSet rawBits = model.encodeContainer();

            StringBuilder fileBitString = new StringBuilder();
            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( fileRawBits.get( iii ) == true ) {
                    fileBitString.append( "1" );
                } else {
                    fileBitString.append( "0" );
                }
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            String fileString = fileBitString.toString();
            String procString = procBitString.toString();

            System.out.println( "File: " + fileString );
            System.out.println( "Proc: " + procString );

            if ( fileString.equals( procString ) == false ) {
                Assert.fail( "File content should have been the same as processed content" );
            }

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void encodeContainerWithContextCalibrators() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/BusElectronics/SensorHistoryBuffer";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-SensorHistoryBuffer.bin";

        try {

            BitSet fileRawBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, fileRawBits );

            long sizeInBits = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBits ) + " but should be 448 bits",
                sizeInBits == 448 );

            BitSet rawBits = model.encodeContainer();

            StringBuilder fileBitString = new StringBuilder();
            StringBuilder procBitString = new StringBuilder();

            for ( int iii = 0; iii < sizeInBits; ++iii ) {
                if ( fileRawBits.get( iii ) == true ) {
                    fileBitString.append( "1" );
                } else {
                    fileBitString.append( "0" );
                }
                if ( rawBits.get( iii ) == true ) {
                    procBitString.append( "1" );
                } else {
                    procBitString.append( "0" );
                }
            }

            String fileString = fileBitString.toString();
            String procString = procBitString.toString();

            System.out.println( "File: " + fileString );
            System.out.println( "Proc: " + procString );

            if ( fileString.equals( procString ) == false ) {
                Assert.fail( "File content should have been the same as processed content" );
            }

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    private void checkEntry( XTCEContainerContentEntry entry,
                             String                    sizeInBits,
                             String                    startBit,
                             String                    value,
                             String                    initialValue,
                             String                    condition,
                             String                    repeat ) {

        if ( entry.getRawSizeInBits().equals( sizeInBits ) == false ) {
            Assert.fail( "Container parameter " + entry.getName() +
                " should be " + sizeInBits + " bits, but it is " +
                entry.getRawSizeInBits() + " bits instead" );
        }

        if ( entry.getStartBit().equals( startBit ) == false ) {
            Assert.fail( "Container parameter " + entry.getName() +
                " should start at bit " + startBit + " but it starts at bit " +
                entry.getStartBit() + " instead" );
            if ( startBit.isEmpty() == true ) {
                Assert.assertTrue( "Container parameter " + entry.getName() +
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
            Assert.fail( "Container parameter " + entry.getName() +
                " should have value of '" + value + "' but it is '" +
                itemValue + "' instead" );
        }

        if ( entry.getInitialValue().equals( initialValue ) == false ) {
            Assert.fail( "Container parameter " + entry.getName() +
                " should have initial value of '" + initialValue +
                "' but it is '" + entry.getInitialValue() + "' instead" );
        }

        if ( entry.getConditions().equals( condition ) == false ) {
            Assert.fail( "Container parameter " + entry.getName() +
                " should have conditions of '" + condition +
                "' but it is '" + entry.getConditions() + "' instead" );
        }

        if ( entry.getRepeatParameterInfo().equals( repeat ) == false ) {
            Assert.fail( "Container parameter " + entry.getName() +
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

    private void assertOnWarnings( XTCEContainerContentModel model ) {

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
