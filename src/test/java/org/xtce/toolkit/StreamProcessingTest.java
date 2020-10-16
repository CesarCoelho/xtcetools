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

public class StreamProcessingTest {
    
    public StreamProcessingTest()  {

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
    public void lookupAllStreamsFromDatabase() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            List<XTCETMStream> streams = db_.getStreams();

            long expected = 7;

            Assert.assertTrue( "Should have found " +
                Long.toString( expected ) + " streams, but found instead " +
                Long.toString( streams.size() ),
                streams.size() == expected );

            long found = 0;

            String root1 =
                "/BogusSAT/SC001/Onboard_Tables/Calibration_Offsets";
            String root2 =
                "/BogusSAT/CCSDSTelemetryPacket";
            String root3 =
                "/BogusSAT/LOG_MSGS/LOG_DATA";
            String root4 =
                "/BogusSAT/LOG_MSGS/LOG_FOOTER";
            String root5 =
                "/BogusSAT/LOG_MSGS/LOG_DYNAMIC_MEMORY";
            String root6 =
                "/BogusSAT/LOG_MSGS/LOG_ALL";
            String root7 =
                "/BogusSAT/SC001/Onboard_Processor_Config/Processor_Config_Table";

            for ( XTCETMStream stream : streams ) {
                if ( stream.getName().equals( "CALTABLE" ) == true ) {
                    Assert.assertTrue( "Stream CALTABLE should have root " + root1,
                                       stream.getStreamRootContainer().getFullPath().equals( root1 ) );
                    ++found;
                } else if ( stream.getName().equals( "CCSDS-TM" ) == true ) {
                    Assert.assertTrue( "Stream CCSDS-TM should have root " + root2,
                                       stream.getStreamRootContainer().getFullPath().equals( root2 ) );
                    ++found;
                } else if ( stream.getName().equals( "LOG_DATA" ) == true ) {
                    Assert.assertTrue( "Stream LOG_DATA should have root " + root3,
                                       stream.getStreamRootContainer().getFullPath().equals( root3 ) );
                    ++found;
                } else if ( stream.getName().equals( "LOG_FOOTER" ) == true ) {
                    Assert.assertTrue( "Stream LOG_FOOTER should have root " + root4,
                                       stream.getStreamRootContainer().getFullPath().equals( root4 ) );
                    ++found;
                } else if ( stream.getName().equals( "LOG_DYNAMIC_MEMORY" ) == true ) {
                    Assert.assertTrue( "Stream LOG_DYNAMIC_MEMORY should have root " + root5,
                                       stream.getStreamRootContainer().getFullPath().equals( root5 ) );
                    ++found;
                } else if ( stream.getName().equals( "LOG_ALL" ) == true ) {
                    Assert.assertTrue( "Stream LOG_ALL should have root " + root6,
                                       stream.getStreamRootContainer().getFullPath().equals( root6 ) );
                    ++found;
                } else if ( stream.getName().equals( "CFGTABLE" ) == true ) {
                    Assert.assertTrue( "Stream CFGTABLE should have root " + root7,
                                       stream.getStreamRootContainer().getFullPath().equals( root7 ) );
                    ++found;
                }
            }

            Assert.assertTrue( "Verified " + Long.toString( found ) + " of " +
                               Long.toString( expected ) + " streams",
                               found == expected );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void lookupAllStreamsFromSpaceSystem() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/Onboard_Tables" );

            List<XTCETMStream> streams = ss.getStreams();

            long expected = 1;

            Assert.assertTrue( "Should have found " +
                Long.toString( expected ) + " streams in " + ss.getName() +
                ", but found instead " +
                Long.toString( streams.size() ),
                streams.size() == expected );

            long found = 0;

            String root1 =
                "/BogusSAT/SC001/Onboard_Tables/Calibration_Offsets";

            for ( XTCETMStream stream : streams ) {
                if ( stream.getName().equals( "CALTABLE" ) == true ) {
                    Assert.assertTrue( "Stream CALTABLE should have root " + root1,
                                       stream.getStreamRootContainer().getFullPath().equals( root1 ) );
                    ++found;
                }
            }

            Assert.assertTrue( "Verified " + Long.toString( found ) + " of " +
                               Long.toString( expected ) + " streams",
                               found == expected );

            expected = 0;

            ss = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" );

            streams = ss.getStreams();

            Assert.assertTrue( "Should have found " +
                Long.toString( expected ) + " streams in " + ss.getName() +
                ", but found instead " +
                Long.toString( streams.size() ),
                streams.size() == expected );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void checkInvalidStreamName() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCETMStream stream = db_.getStream( "FOOBAR" );

            Assert.fail( "Should have gotten an exception on stream lookup for FOOBAR" );

        } catch ( Exception ex ) {
            // do nothing, expect an exception
        }

    }

    @Test
    public void checkSimpleInheritancePath() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName =
            "/BogusSAT/SC001/Onboard_Tables/Calibration_Offsets";

        String expectedPath =
            "/Calibration_Offsets";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            String iPath = container.getInheritancePath();

            Assert.assertTrue( "Incorrect inheritance path '" + iPath + "' " +
                               "expected '" + expectedPath + "' for " +
                               containerName,
                               iPath.equals( expectedPath ) == true );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void checkLongerInheritancePath() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName =
            "/BogusSAT/SC001/ECSS_SpacePacket1";

        String expectedPath =
            "/CCSDSPacket/CCSDSTelemetryPacket/CCSDSPUSTelemetryPacket/" +
            "ECSS_Service_3_Subservice_25/ECSS_SpacePacket1";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            String iPath = container.getInheritancePath();

            Assert.assertTrue( "Incorrect inheritance path '" + iPath + "' " +
                               "expected '" + expectedPath + "' for " +
                               containerName,
                               iPath.equals( expectedPath ) == true );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void checkStreamContainers() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCETMStream stream = db_.getStream( "CCSDS-TM" );

            Assert.assertTrue( "Should have found CCSDS-TM stream",
                               stream != null );

            if ( stream.getReference() == null ) {
                Assert.fail( "Stream 'CCSDS-TM' should have a PCMStreamType internally" );
            }

            if ( stream.getStreamRootContainerPath().equals( "/BogusSAT/CCSDSTelemetryPacket" ) == false ) {
                Assert.fail( "Expected root path of '/BogusSAT/CCSDSTelemetryPacket' for 'CCSDS-TM' but got " + stream.getStreamRootContainerPath() );
            }

            long expected = 14;
            long found    = 0;

            List<XTCETMContainer> containers = stream.getContainers();
            for ( XTCETMContainer container : containers ) {
                System.out.println( "CCSDS-TM Contains: " +
                                    container.getInheritancePath() );
                ++found;
            }

            Assert.assertTrue( "Should have found " +
                Long.toString( expected ) + " containers in CCSDS-TM " +
                ", but found instead " + Long.toString( found ),
                found == expected );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processSimpleStream() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName =
            "/BogusSAT/SC001/Onboard_Tables/Calibration_Offsets";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-Calibration_Offsets.bin";

        try {

            XTCETMStream stream = db_.getStream( "CALTABLE" );

            BitSet rawBits = readBytesFromFile( binFilename );

            XTCEContainerContentModel model = stream.processStream( rawBits );

            Assert.assertTrue( "Processing CALTABLE should have been valid " +
                               "with file " + binFilename,
                               model.isValid() == true );

            Assert.assertTrue( "Stream should have found container " +
                               containerName + ", but it found instead " +
                               model.getContainerReference().getFullPath(),
                               model.getContainerReference().getFullPath().equals( containerName ) );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "Battery_Voltage_Offset":
                        ++items;
                        checkEntry( entry, "32", "0", "==3.5{cal}", "2.5", "", "" );
                        break;
                    case "Solar_Array_Voltage_1_Offset":
                        ++items;
                        checkEntry( entry, "32", "48", "==9{cal}", "2", "", "" );
                        break;
                    case "Solar_Array_Voltage_2_Offset":
                        ++items;
                        checkEntry( entry, "32", "80", "==-5{cal}", "1", "", "" );
                        break;
                    case "Battery_Current_Offset":
                        ++items;
                        checkEntry( entry, "64", "112", "==14.25{cal}", "-1.5", "", "" );
                        break;
                    case "Default_CPU_Start_Mode":
                        ++items;
                        checkEntry( entry, "8", "176", "==SAFEHOLD{cal}", "NORMAL", "", "" );
                        break;
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 5 items",
                items == 5 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void calculateCompatibilitySimpleStream() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName =
            "/BogusSAT/SC001/Onboard_Tables/Calibration_Offsets";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-Calibration_Offsets.bin";

        try {

            BitSet packetBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                new XTCEContainerContentModel( container,
                                               db_.getSpaceSystemTree(),
                                               null,
                                               false );

            Assert.assertTrue( "Processing " + containerName +
                               " should have been valid ",
                               model.isValid() == true );

            Assert.assertTrue( "Binary should have been compatible (true)",
                               model.isProcessingCompatible( packetBits ) );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processContainerWithInheritance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket1";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-CCSDS_SpacePacket1.bin";

        try {

            XTCETMStream stream = db_.getStream( "CCSDS-TM" );

            BitSet rawBits = readBytesFromFile( binFilename );

            XTCEContainerContentModel model = stream.processStream( rawBits );

            Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                               "with file " + binFilename,
                               model.isValid() == true );

            Assert.assertTrue( "Stream should have found container " +
                               containerName + ", but it found instead " +
                               model.getContainerReference().getFullPath(),
                               model.getContainerReference().getFullPath().equals( containerName ) );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "CCSDS_Packet_ID.Version":
                        ++items;
                        checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                        break;
                    case "CCSDS_Packet_ID.Type":
                        ++items;
                        checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.SecHdrFlag":
                        ++items;
                        checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.APID":
                        ++items;
                        checkEntry( entry, "11", "5", "==1{cal}", "2047", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.GroupFlags":
                        ++items;
                        checkEntry( entry, "2", "16", "==Standalone{cal}", "Standalone", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.Count":
                        ++items;
                        checkEntry( entry, "14", "18", "==3783{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_Length":
                        ++items;
                        checkEntry( entry, "16", "32", "==11{cal}", "", "", "" );
                        break;
                    case "Battery_Voltage":
                        ++items;
                        checkEntry( entry, "32", "48", "==12.3{cal}", "", "", "" );
                        break;
                    case "Battery_Current":
                        ++items;
                        checkEntry( entry, "32", "80", "==0.5{cal}", "", "", "" );
                        break;
                    case "Battery_Charge_Mode":
                        ++items;
                        checkEntry( entry, "1", "112", "==CHARGE{cal}", "", "", "" );
                        break;
                    case "SomeParameter":
                        ++items;
                        checkEntry( entry, "7", "113", "==5{cal}", "", "", "" );
                        break;
                    case "Solar_Array_Voltage_1":
                        ++items;
                        checkEntry( entry, "12", "120", "==23.0{cal}", "", "", "" );
                        break;
                    case "Solar_Array_Voltage_2":
                        ++items;
                        checkEntry( entry, "12", "132", "==23.0{cal}", "", "", "" );
                        break;
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 13 items",
                items == 13 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void calculateCompatibilityContainerWithInheritance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket1";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-CCSDS_SpacePacket1.bin";

        try {

            BitSet packetBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                new XTCEContainerContentModel( container,
                                               db_.getSpaceSystemTree(),
                                               null,
                                               false );

            Assert.assertTrue( "Processing " + containerName +
                               " should have been valid ",
                               model.isValid() == true );

            Assert.assertTrue( "Binary should have been compatible (true)",
                               model.isProcessingCompatible( packetBits ) );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processContainerWithWrongInheritance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket1";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-CCSDS_SpacePacket1-Bad.bin";

        try {

            XTCETMStream stream = db_.getStream( "CCSDS-TM" );

            BitSet rawBits = readBytesFromFile( binFilename );

            XTCEContainerContentModel model = stream.processStream( rawBits );

            Assert.fail( "Processing CCSDS-TM should have been invalid " +
                         "with file " + binFilename );

        } catch ( Exception ex ) {
            // expect to get an exception here
        }

    }

    @Test
    public void calculateCompatibilityContainerWithWrongInheritance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket1";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-CCSDS_SpacePacket1-Bad.bin";

        try {

            BitSet packetBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                new XTCEContainerContentModel( container,
                                               db_.getSpaceSystemTree(),
                                               null,
                                               false );

            Assert.assertTrue( "Processing " + containerName +
                               " should have been valid ",
                               model.isValid() == true );

            Assert.assertFalse( "Binary should NOT have been compatible (false)",
                                model.isProcessingCompatible( packetBits ) );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            // expect to get an exception here
        }

    }

    @Test
    public void processContainerWithInheritanceAndIncludesFalse() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_SpacePacket2";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-ECSS_3_25_HK-ECSS_SpacePacket2-NoInc.bin";

        try {

            XTCETMStream stream = db_.getStream( "CCSDS-TM" );

            BitSet rawBits = readBytesFromFile( binFilename );

            XTCEContainerContentModel model = stream.processStream( rawBits );

            Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                               "with file " + binFilename,
                               model.isValid() == true );

            Assert.assertTrue( "Stream should have found container " +
                               containerName + ", but it found instead " +
                               model.getContainerReference().getFullPath(),
                               model.getContainerReference().getFullPath().equals( containerName ) );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            for ( String warning : model.getWarnings() ) {
                System.out.println( "Container Model Warning: " + warning );
            }

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "CCSDS_Packet_ID.Version":
                        ++items;
                        checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                        break;
                    case "CCSDS_Packet_ID.Type":
                        ++items;
                        checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.SecHdrFlag":
                        ++items;
                        checkEntry( entry, "1", "4", "==Present{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.APID":
                        ++items;
                        checkEntry( entry, "11", "5", "==100{cal}", "2047", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.GroupFlags":
                        ++items;
                        checkEntry( entry, "2", "16", "==Standalone{cal}", "Standalone", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.Count":
                        ++items;
                        checkEntry( entry, "14", "18", "==3784{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_Length":
                        ++items;
                        checkEntry( entry, "16", "32", "==17{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Spare1":
                        ++items;
                        checkEntry( entry, "1", "48", "==0{cal}", "0", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Version":
                        ++items;
                        checkEntry( entry, "3", "49", "==1{cal}", "1", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Spare4":
                        ++items;
                        checkEntry( entry, "4", "52", "==0{cal}", "0", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Service":
                        ++items;
                        checkEntry( entry, "8", "56", "==3{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Subservice":
                        ++items;
                        checkEntry( entry, "8", "64", "==25{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.SeqCount":
                        ++items;
                        checkEntry( entry, "8", "72", "==241{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Destination":
                        ++items;
                        checkEntry( entry, "8", "80", "==3{cal}", "", "", "" );
                        break;
                    case "PUS_Time":
                        ++items;
                        checkEntry( entry, "64", "", "", "", "APPL_TIME_CODE!=NotUsed{cal}", "" );
                        break;
                    case "PUS_Error_Control_Field":
                        ++items;
                        checkEntry( entry, "16", "", "", "", "TM_CHECKSUM_TYPE!=NotUsed{cal}", "" );
                        break;
                    case "PUS_Structure_ID":
                        ++items;
                        checkEntry( entry, "8", "88", "==ECSS_SpacePacket2{cal}", "", "", "" );
                        break;
                    case "Battery_Voltage":
                        ++items;
                        checkEntry( entry, "32", "96", "==12.3{cal}", "", "", "" );
                        break;
                    case "Battery_Current":
                        ++items;
                        checkEntry( entry, "32", "128", "==0.5{cal}", "", "", "" );
                        break;
                    case "Flag_Parameter":
                        ++items;
                        checkEntry( entry, "8", "160", "==5{cal}", "", "", "" );
                        break;
                    case "Optional_Parameter":
                        ++items;
                        checkEntry( entry, "16", "", "", "", "Flag_Parameter==1{cal}", "" );
                        break;
                    case "Solar_Array_Voltage_1":
                        ++items;
                        checkEntry( entry, "12", "168", "==5.0{cal}", "", "", "" );
                        break;
                    case "Solar_Array_Voltage_2":
                        ++items;
                        checkEntry( entry, "12", "180", "==23.0{cal}", "", "", "" );
                        break;
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 23 items",
                items == 23 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void calculateCompatibilityContainerWithInheritanceAndIncludesFalse() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_SpacePacket2";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-ECSS_3_25_HK-ECSS_SpacePacket2-NoInc.bin";

        try {

            BitSet packetBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                new XTCEContainerContentModel( container,
                                               db_.getSpaceSystemTree(),
                                               null,
                                               false );

            Assert.assertTrue( "Processing " + containerName +
                               " should have been valid ",
                               model.isValid() == true );

            Assert.assertTrue( "Binary should have been compatible (true)",
                               model.isProcessingCompatible( packetBits ) );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processContainerWithInheritanceAndIncludesTrue() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_SpacePacket2";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-ECSS_3_25_HK-ECSS_SpacePacket2-Inc.bin";

        try {

            XTCETMStream stream = db_.getStream( "CCSDS-TM" );

            BitSet rawBits = readBytesFromFile( binFilename );

            XTCEContainerContentModel model = stream.processStream( rawBits );

            Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                               "with file " + binFilename,
                               model.isValid() == true );

            Assert.assertTrue( "Stream should have found container " +
                               containerName + ", but it found instead " +
                               model.getContainerReference().getFullPath(),
                               model.getContainerReference().getFullPath().equals( containerName ) );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            for ( String warning : model.getWarnings() ) {
                System.out.println( "Container Model Warning: " + warning );
            }

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "CCSDS_Packet_ID.Version":
                        ++items;
                        checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                        break;
                    case "CCSDS_Packet_ID.Type":
                        ++items;
                        checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.SecHdrFlag":
                        ++items;
                        checkEntry( entry, "1", "4", "==Present{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.APID":
                        ++items;
                        checkEntry( entry, "11", "5", "==100{cal}", "2047", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.GroupFlags":
                        ++items;
                        checkEntry( entry, "2", "16", "==Standalone{cal}", "Standalone", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.Count":
                        ++items;
                        checkEntry( entry, "14", "18", "==3784{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_Length":
                        ++items;
                        checkEntry( entry, "16", "32", "==19{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Spare1":
                        ++items;
                        checkEntry( entry, "1", "48", "==0{cal}", "0", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Version":
                        ++items;
                        checkEntry( entry, "3", "49", "==1{cal}", "1", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Spare4":
                        ++items;
                        checkEntry( entry, "4", "52", "==0{cal}", "0", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Service":
                        ++items;
                        checkEntry( entry, "8", "56", "==3{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Subservice":
                        ++items;
                        checkEntry( entry, "8", "64", "==25{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.SeqCount":
                        ++items;
                        checkEntry( entry, "8", "72", "==241{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Destination":
                        ++items;
                        checkEntry( entry, "8", "80", "==3{cal}", "", "", "" );
                        break;
                    case "PUS_Time":
                        ++items;
                        checkEntry( entry, "64", "", "", "", "APPL_TIME_CODE!=NotUsed{cal}", "" );
                        break;
                    case "PUS_Error_Control_Field":
                        ++items;
                        checkEntry( entry, "16", "", "", "", "TM_CHECKSUM_TYPE!=NotUsed{cal}", "" );
                        break;
                    case "PUS_Structure_ID":
                        ++items;
                        checkEntry( entry, "8", "88", "==ECSS_SpacePacket2{cal}", "", "", "" );
                        break;
                    case "Battery_Voltage":
                        ++items;
                        checkEntry( entry, "32", "96", "==12.3{cal}", "", "", "" );
                        break;
                    case "Battery_Current":
                        ++items;
                        checkEntry( entry, "32", "128", "==0.5{cal}", "", "", "" );
                        break;
                    case "Flag_Parameter":
                        ++items;
                        checkEntry( entry, "8", "160", "==1{cal}", "", "", "" );
                        break;
                    case "Optional_Parameter":
                        ++items;
                        checkEntry( entry, "16", "168", "==61455{cal}", "", "Flag_Parameter==1{cal}", "" );
                        break;
                    case "Solar_Array_Voltage_1":
                        ++items;
                        checkEntry( entry, "12", "184", "==5.0{cal}", "", "", "" );
                        break;
                    case "Solar_Array_Voltage_2":
                        ++items;
                        checkEntry( entry, "12", "196", "==23.0{cal}", "", "", "" );
                        break;
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 23 items",
                items == 23 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void calculateCompatibilityContainerWithInheritanceAndIncludesTrue() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_SpacePacket2";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-ECSS_3_25_HK-ECSS_SpacePacket2-Inc.bin";

        try {

            BitSet packetBits = readBytesFromFile( binFilename );

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                new XTCEContainerContentModel( container,
                                               db_.getSpaceSystemTree(),
                                               null,
                                               false );

            Assert.assertTrue( "Processing " + containerName +
                               " should have been valid ",
                               model.isValid() == true );

            Assert.assertTrue( "Binary should have been compatible (true)",
                               model.isProcessingCompatible( packetBits ) );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processContainerWithInheritanceFixedRepeatParameter() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket3";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-CCSDS_SpacePacket3.bin";

        try {

            XTCETMStream stream = db_.getStream( "CCSDS-TM" );

            BitSet rawBits = readBytesFromFile( binFilename );

            XTCEContainerContentModel model = stream.processStream( rawBits );

            Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                               "with file " + binFilename,
                               model.isValid() == true );

            Assert.assertTrue( "Stream should have found container " +
                               containerName + ", but it found instead " +
                               model.getContainerReference().getFullPath(),
                               model.getContainerReference().getFullPath().equals( containerName ) );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            for ( String warning : model.getWarnings() ) {
                System.out.println( "Container Model Warning: " + warning );
            }

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "CCSDS_Packet_ID.Version":
                        ++items;
                        checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                        break;
                    case "CCSDS_Packet_ID.Type":
                        ++items;
                        checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.SecHdrFlag":
                        ++items;
                        checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.APID":
                        ++items;
                        checkEntry( entry, "11", "5", "==3{cal}", "2047", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.GroupFlags":
                        ++items;
                        checkEntry( entry, "2", "16", "==Standalone{cal}", "Standalone", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.Count":
                        ++items;
                        checkEntry( entry, "14", "18", "==3786{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_Length":
                        ++items;
                        checkEntry( entry, "16", "32", "==19{cal}", "", "", "" );
                        break;
                    case "Basic_string_uint32":
                        ++items;
                        if ( items == 8 ) {
                            checkEntry( entry, "32", "48", "==1{cal}", "", "", XTCEFunctions.makeRepeatString( 1, 5 ) );
                        } else if ( items == 9 ) {
                            checkEntry( entry, "32", "80", "==0{cal}", "", "", XTCEFunctions.makeRepeatString( 2, 5 ) );
                        } else if ( items == 10 ) {
                            checkEntry( entry, "32", "112", "==65535{cal}", "", "", XTCEFunctions.makeRepeatString( 3, 5 ) );
                        } else if ( items == 11 ) {
                            checkEntry( entry, "32", "144", "==21845{cal}", "", "", XTCEFunctions.makeRepeatString( 4, 5 ) );
                        } else if ( items == 12 ) {
                            checkEntry( entry, "32", "176", "==4096{cal}", "", "", XTCEFunctions.makeRepeatString( 5, 5 ) );
                        }   break;
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 12 items",
                items == 12 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processContainerWithInheritanceDynamicRepeatParameter() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_Service_1_Subservice_2";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-ECSS_Service_1_Subservice_2.bin";

        try {

            XTCETMStream stream = db_.getStream( "CCSDS-TM" );

            BitSet rawBits = readBytesFromFile( binFilename );

            XTCEContainerContentModel model = stream.processStream( rawBits );

            Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                               "with file " + binFilename,
                               model.isValid() == true );

            Assert.assertTrue( "Stream should have found container " +
                               containerName + ", but it found instead " +
                               model.getContainerReference().getFullPath(),
                               model.getContainerReference().getFullPath().equals( containerName ) );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            for ( String warning : model.getWarnings() ) {
                System.out.println( "Container Model Warning: " + warning );
            }

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "CCSDS_Packet_ID.Version":
                        ++items;
                        checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                        break;
                    case "CCSDS_Packet_ID.Type":
                        ++items;
                        checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.SecHdrFlag":
                        ++items;
                        checkEntry( entry, "1", "4", "==Present{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_ID.APID":
                        ++items;
                        checkEntry( entry, "11", "5", "==100{cal}", "2047", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.GroupFlags":
                        ++items;
                        checkEntry( entry, "2", "16", "==Standalone{cal}", "Standalone", "", "" );
                        break;
                    case "CCSDS_Packet_Sequence.Count":
                        ++items;
                        checkEntry( entry, "14", "18", "==3785{cal}", "", "", "" );
                        break;
                    case "CCSDS_Packet_Length":
                        ++items;
                        checkEntry( entry, "16", "32", "==27{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Spare1":
                        ++items;
                        checkEntry( entry, "1", "48", "==0{cal}", "0", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Version":
                        ++items;
                        checkEntry( entry, "3", "49", "==1{cal}", "1", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Spare4":
                        ++items;
                        checkEntry( entry, "4", "52", "==0{cal}", "0", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Service":
                        ++items;
                        checkEntry( entry, "8", "56", "==1{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Subservice":
                        ++items;
                        checkEntry( entry, "8", "64", "==2{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.SeqCount":
                        ++items;
                        checkEntry( entry, "8", "72", "==254{cal}", "", "", "" );
                        break;
                    case "PUS_Data_Field_Header.Destination":
                        ++items;
                        checkEntry( entry, "8", "80", "==2{cal}", "", "", "" );
                        break;
                    case "PUS_Time":
                        ++items;
                        checkEntry( entry, "64", "", "", "", "APPL_TIME_CODE!=NotUsed{cal}", "" );
                        break;
                    case "PUS_Error_Control_Field":
                        ++items;
                        checkEntry( entry, "16", "", "", "", "TM_CHECKSUM_TYPE!=NotUsed{cal}", "" );
                        break;
                    case "TC_Packet_ID":
                        ++items;
                        checkEntry( entry, "16", "88", "==170{cal}", "", "", "" );
                        break;
                    case "TC_Packet_Sequence_Control":
                        ++items;
                        checkEntry( entry, "16", "104", "==257{cal}", "", "", "" );
                        break;
                    case "TC_Accept_Failure_Code":
                        ++items;
                        checkEntry( entry, "16", "120", "==INCONSISTENT_APPL_DATA{cal}", "", "", "" );
                        break;
                    case "TC_Parameter_Count":
                        ++items;
                        checkEntry( entry, "8", "136", "==16{cal}", "", "", "" );
                        break;
                    case "TC_Parameter_Data":
                        ++items;
                        if ( items == 21 ) {
                            checkEntry( entry, "8", "144", "==0{cal}", "", "", XTCEFunctions.makeRepeatString( 1, 16 ) );
                        } else if ( items == 22 ) {
                            checkEntry( entry, "8", "152", "==1{cal}", "", "", XTCEFunctions.makeRepeatString( 2, 16 ) );
                        } else if ( items == 23 ) {
                            checkEntry( entry, "8", "160", "==2{cal}", "", "", XTCEFunctions.makeRepeatString( 3, 16 ) );
                        } else if ( items == 24 ) {
                            checkEntry( entry, "8", "168", "==3{cal}", "", "", XTCEFunctions.makeRepeatString( 4, 16 ) );
                        } else if ( items == 25 ) {
                            checkEntry( entry, "8", "176", "==4{cal}", "", "", XTCEFunctions.makeRepeatString( 5, 16 ) );
                        } else if ( items == 26 ) {
                            checkEntry( entry, "8", "184", "==5{cal}", "", "", XTCEFunctions.makeRepeatString( 6, 16 ) );
                        } else if ( items == 27 ) {
                            checkEntry( entry, "8", "192", "==6{cal}", "", "", XTCEFunctions.makeRepeatString( 7, 16 ) );
                        } else if ( items == 28 ) {
                            checkEntry( entry, "8", "200", "==7{cal}", "", "", XTCEFunctions.makeRepeatString( 8, 16 ) );
                        } else if ( items == 29 ) {
                            checkEntry( entry, "8", "208", "==8{cal}", "", "", XTCEFunctions.makeRepeatString( 9, 16 ) );
                        } else if ( items == 30 ) {
                            checkEntry( entry, "8", "216", "==9{cal}", "", "", XTCEFunctions.makeRepeatString( 10, 16 ) );
                        } else if ( items == 31 ) {
                            checkEntry( entry, "8", "224", "==10{cal}", "", "", XTCEFunctions.makeRepeatString( 11, 16 ) );
                        } else if ( items == 32 ) {
                            checkEntry( entry, "8", "232", "==11{cal}", "", "", XTCEFunctions.makeRepeatString( 12, 16 ) );
                        } else if ( items == 33 ) {
                            checkEntry( entry, "8", "240", "==12{cal}", "", "", XTCEFunctions.makeRepeatString( 13, 16 ) );
                        } else if ( items == 34 ) {
                            checkEntry( entry, "8", "248", "==13{cal}", "", "", XTCEFunctions.makeRepeatString( 14, 16 ) );
                        } else if ( items == 35 ) {
                            checkEntry( entry, "8", "256", "==14{cal}", "", "", XTCEFunctions.makeRepeatString( 15, 16 ) );
                        } else if ( items == 36 ) {
                            checkEntry( entry, "8", "264", "==15{cal}", "", "", XTCEFunctions.makeRepeatString( 16, 16 ) );
                        }   break;
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 36 items",
                items == 36 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processContainerWithParameterAtEndInheritance1() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/Onboard_Processor_Config/Processor_1_Config_Table";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-Proc_1_Cfg_Table.bin";

        try {

            XTCETMStream stream = db_.getStream( "CFGTABLE" );

            BitSet rawBits = readBytesFromFile( binFilename );

            XTCEContainerContentModel model = stream.processStream( rawBits );

            Assert.assertTrue( "Processing CFGTABLE should have been valid " +
                               "with file " + binFilename,
                               model.isValid() == true );

            Assert.assertTrue( "Stream should have found container " +
                               containerName + ", but it found instead " +
                               model.getContainerReference().getFullPath(),
                               model.getContainerReference().getFullPath().equals( containerName ) );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            for ( String warning : model.getWarnings() ) {
                System.out.println( "Container Model Warning: " + warning );
            }

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "Table_Processor_ID":
                        ++items;
                        checkEntry( entry, "8", "0", "==PROCESSOR1{cal}", "NOT_YET_SET", "", "" );
                        break;
                    case "Config_Log_Levels":
                        ++items;
                        checkEntry( entry, "", "", "", "", "", "" );
                        break;
                    case "Config_Log_Levels.COREINF":
                        ++items;
                        checkEntry( entry, "4", "8", "==info{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.ANTCTRL":
                        ++items;
                        checkEntry( entry, "4", "12", "==debug{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.TCMGMNT":
                        ++items;
                        checkEntry( entry, "4", "16", "==info{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.TIME":
                        ++items;
                        checkEntry( entry, "4", "20", "==warning{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.TMMGMNT":
                        ++items;
                        checkEntry( entry, "4", "24", "==error{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.INTERFACE":
                        ++items;
                        checkEntry( entry, "4", "28", "==warning{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.MW1553":
                        ++items;
                        checkEntry( entry, "4", "32", "==info{cal}", "warning", "", "" );
                        break;
                    case "Config_Watchdog":
                        ++items;
                        checkEntry( entry, "16", "44", "==30{cal}", "5", "", "" );
                        break;
                    case "Config_PROM_Access":
                        ++items;
                        checkEntry( entry, "", "", "", "", "", "" );
                        break;
                    case "Config_PROM_Access.Read":
                        ++items;
                        checkEntry( entry, "16", "60", "==1000{cal}", "100", "", "" );
                        break;
                    case "Config_PROM_Access.Write":
                        ++items;
                        checkEntry( entry, "16", "76", "==99{cal}", "100", "", "" );
                        break;
                    case "Config_PROM_Access.Erase":
                        ++items;
                        checkEntry( entry, "16", "92", "==250{cal}", "100", "", "" );
                        break;
                    case "Config_PROM_Access.Lock":
                        ++items;
                        checkEntry( entry, "16", "108", "==31000{cal}", "100", "", "" );
                        break;
                    case "Config_Self_Test":
                        ++items;
                        checkEntry( entry, "", "", "", "", "", "" );
                        break;
                    case "Config_Self_Test.pad_2bits":
                        ++items;
                        checkEntry( entry, "2", "124", "==0x0{cal}", "0x00", "", "" );
                        break;
                    case "Config_Self_Test.Antenna":
                        ++items;
                        checkEntry( entry, "1", "126", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.CPU":
                        ++items;
                        checkEntry( entry, "1", "127", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.RAM":
                        ++items;
                        checkEntry( entry, "1", "128", "==DISABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.EEPROM":
                        ++items;
                        checkEntry( entry, "1", "129", "==DISABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.Flash":
                        ++items;
                        checkEntry( entry, "1", "130", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.Modem":
                        ++items;
                        checkEntry( entry, "1", "131", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Clock_ID":
                        ++items;
                        checkEntry( entry, "8", "132", "==PRIMARY{cal}", "PRIMARY", "", "" );
                        break;
                    case "Config_Ant_Power_Level":
                        ++items;
                        checkEntry( entry, "16", "140", "==5.8{cal}", "-0.6", "", "" );
                        break;
                    case "Config_Payload_IF_ID":
                        ++items;
                        checkEntry( entry, "4", "156", "==LINK1{cal}", "LINK1", "", "" );
                        break;
                    case "Config_Payload_Perf":
                        ++items;
                        checkEntry( entry, "4", "160", "==NORMAL{cal}", "NORMAL", "", "" );
                        break;
                    case "Table_CRC_Value":
                        ++items;
                        checkEntry( entry, "16", "164", "==0xabcd{cal}", "", "", "" );
                        break;
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 28 items",
                items == 28 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processContainerWithParameterAtEndInheritance2() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/Onboard_Processor_Config/Processor_2_Config_Table";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-Proc_2_Cfg_Table.bin";

        try {

            XTCETMStream stream = db_.getStream( "CFGTABLE" );

            BitSet rawBits = readBytesFromFile( binFilename );

            XTCEContainerContentModel model = stream.processStream( rawBits );

            Assert.assertTrue( "Processing CFGTABLE should have been valid " +
                               "with file " + binFilename,
                               model.isValid() == true );

            Assert.assertTrue( "Stream should have found container " +
                               containerName + ", but it found instead " +
                               model.getContainerReference().getFullPath(),
                               model.getContainerReference().getFullPath().equals( containerName ) );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            for ( String warning : model.getWarnings() ) {
                System.out.println( "Container Model Warning: " + warning );
            }

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "Table_Processor_ID":
                        ++items;
                        checkEntry( entry, "8", "0", "==PROCESSOR2{cal}", "NOT_YET_SET", "", "" );
                        break;
                    case "Config_Log_Levels":
                        ++items;
                        checkEntry( entry, "", "", "", "", "", "" );
                        break;
                    case "Config_Log_Levels.COREINF":
                        ++items;
                        checkEntry( entry, "4", "8", "==info{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.ANTCTRL":
                        ++items;
                        checkEntry( entry, "4", "12", "==debug{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.TCMGMNT":
                        ++items;
                        checkEntry( entry, "4", "16", "==info{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.TIME":
                        ++items;
                        checkEntry( entry, "4", "20", "==warning{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.TMMGMNT":
                        ++items;
                        checkEntry( entry, "4", "24", "==error{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.INTERFACE":
                        ++items;
                        checkEntry( entry, "4", "28", "==warning{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.MW1553":
                        ++items;
                        checkEntry( entry, "4", "32", "==info{cal}", "warning", "", "" );
                        break;
                    case "Config_Watchdog":
                        ++items;
                        checkEntry( entry, "16", "44", "==30{cal}", "5", "", "" );
                        break;
                    case "Config_PROM_Access":
                        ++items;
                        checkEntry( entry, "", "", "", "", "", "" );
                        break;
                    case "Config_PROM_Access.Read":
                        ++items;
                        checkEntry( entry, "16", "60", "==1000{cal}", "100", "", "" );
                        break;
                    case "Config_PROM_Access.Write":
                        ++items;
                        checkEntry( entry, "16", "76", "==99{cal}", "100", "", "" );
                        break;
                    case "Config_PROM_Access.Erase":
                        ++items;
                        checkEntry( entry, "16", "92", "==250{cal}", "100", "", "" );
                        break;
                    case "Config_PROM_Access.Lock":
                        ++items;
                        checkEntry( entry, "16", "108", "==31000{cal}", "100", "", "" );
                        break;
                    case "Config_Self_Test":
                        ++items;
                        checkEntry( entry, "", "", "", "", "", "" );
                        break;
                    case "Config_Self_Test.pad_2bits":
                        ++items;
                        checkEntry( entry, "2", "124", "==0x0{cal}", "0x00", "", "" );
                        break;
                    case "Config_Self_Test.Antenna":
                        ++items;
                        checkEntry( entry, "1", "126", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.CPU":
                        ++items;
                        checkEntry( entry, "1", "127", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.RAM":
                        ++items;
                        checkEntry( entry, "1", "128", "==DISABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.EEPROM":
                        ++items;
                        checkEntry( entry, "1", "129", "==DISABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.Flash":
                        ++items;
                        checkEntry( entry, "1", "130", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.Modem":
                        ++items;
                        checkEntry( entry, "1", "131", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Clock_ID":
                        ++items;
                        checkEntry( entry, "8", "132", "==PRIMARY{cal}", "PRIMARY", "", "" );
                        break;
                    case "Config_Ant_Power_Level":
                        ++items;
                        checkEntry( entry, "16", "140", "==5.0{cal}", "-0.6", "", "" );
                        break;
                    case "Table_CRC_Value":
                        ++items;
                        checkEntry( entry, "16", "156", "==0xabcd{cal}", "", "", "" );
                        break;
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 26 items",
                items == 26 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processContainerWithParameterAtEndInheritanceAndGapContainer() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/Onboard_Processor_Config/Processor_3_Config_Table";

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-Proc_3_Cfg_Table.bin";

        try {

            XTCETMStream stream = db_.getStream( "CFGTABLE" );

            BitSet rawBits = readBytesFromFile( binFilename );

            XTCEContainerContentModel model = stream.processStream( rawBits );

            Assert.assertTrue( "Processing CFGTABLE should have been valid " +
                               "with file " + binFilename,
                               model.isValid() == true );

            Assert.assertTrue( "Stream should have found container " +
                               containerName + ", but it found instead " +
                               model.getContainerReference().getFullPath(),
                               model.getContainerReference().getFullPath().equals( containerName ) );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            for ( String warning : model.getWarnings() ) {
                System.out.println( "Container Model Warning: " + warning );
            }

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                switch (entry.getName()) {
                    case "Table_Processor_ID":
                        ++items;
                        checkEntry( entry, "8", "0", "==PROCESSOR3{cal}", "NOT_YET_SET", "", "" );
                        break;
                    case "Config_Log_Levels":
                        ++items;
                        checkEntry( entry, "", "", "", "", "", "" );
                        break;
                    case "Config_Log_Levels.COREINF":
                        ++items;
                        checkEntry( entry, "4", "8", "==info{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.ANTCTRL":
                        ++items;
                        checkEntry( entry, "4", "12", "==debug{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.TCMGMNT":
                        ++items;
                        checkEntry( entry, "4", "16", "==info{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.TIME":
                        ++items;
                        checkEntry( entry, "4", "20", "==warning{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.TMMGMNT":
                        ++items;
                        checkEntry( entry, "4", "24", "==error{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.INTERFACE":
                        ++items;
                        checkEntry( entry, "4", "28", "==warning{cal}", "warning", "", "" );
                        break;
                    case "Config_Log_Levels.MW1553":
                        ++items;
                        checkEntry( entry, "4", "32", "==info{cal}", "warning", "", "" );
                        break;
                    case "Config_Watchdog":
                        ++items;
                        checkEntry( entry, "16", "44", "==30{cal}", "5", "", "" );
                        break;
                    case "Config_PROM_Access":
                        ++items;
                        checkEntry( entry, "", "", "", "", "", "" );
                        break;
                    case "Config_PROM_Access.Read":
                        ++items;
                        checkEntry( entry, "16", "60", "==1000{cal}", "100", "", "" );
                        break;
                    case "Config_PROM_Access.Write":
                        ++items;
                        checkEntry( entry, "16", "76", "==99{cal}", "100", "", "" );
                        break;
                    case "Config_PROM_Access.Erase":
                        ++items;
                        checkEntry( entry, "16", "92", "==250{cal}", "100", "", "" );
                        break;
                    case "Config_PROM_Access.Lock":
                        ++items;
                        checkEntry( entry, "16", "108", "==31000{cal}", "100", "", "" );
                        break;
                    case "Config_Self_Test":
                        ++items;
                        checkEntry( entry, "", "", "", "", "", "" );
                        break;
                    case "Config_Self_Test.pad_2bits":
                        ++items;
                        checkEntry( entry, "2", "124", "==0x0{cal}", "0x00", "", "" );
                        break;
                    case "Config_Self_Test.Antenna":
                        ++items;
                        checkEntry( entry, "1", "126", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.CPU":
                        ++items;
                        checkEntry( entry, "1", "127", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.RAM":
                        ++items;
                        checkEntry( entry, "1", "128", "==DISABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.EEPROM":
                        ++items;
                        checkEntry( entry, "1", "129", "==DISABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.Flash":
                        ++items;
                        checkEntry( entry, "1", "130", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Self_Test.Modem":
                        ++items;
                        checkEntry( entry, "1", "131", "==ENABLED{cal}", "ENABLED", "", "" );
                        break;
                    case "Config_Clock_ID":
                        ++items;
                        checkEntry( entry, "8", "132", "==PRIMARY{cal}", "PRIMARY", "", "" );
                        break;
                    case "Config_Ant_Power_Level":
                        ++items;
                        checkEntry( entry, "16", "140", "==7.5{cal}", "-0.6", "", "" );
                        break;
                    case "Config_Payload_IF_ID":
                        ++items;
                        checkEntry( entry, "4", "180", "==LINK1{cal}", "LINK1", "", "" );
                        break;
                    case "Config_Payload_Perf":
                        ++items;
                        checkEntry( entry, "4", "184", "==NORMAL{cal}", "NORMAL", "", "" );
                        break;
                    case "Table_CRC_Value":
                        ++items;
                        checkEntry( entry, "16", "188", "==0xabcd{cal}", "", "", "" );
                        break;
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 28 items",
                items == 28 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void process60000Packets() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-10000Packets.bin";

        long startTime = System.currentTimeMillis();

        try {

            XTCETMStream stream = db_.getStream( "CCSDS-TM" );

            File binFile = new File( binFilename );

            InputStream fstream = new FileInputStream( binFile );

            int byteValue;

            XTCEContainerContentModel model;

            for ( int iii = 0; iii < 10000; ++iii ) {

                ByteArrayOutputStream buffer1 = new ByteArrayOutputStream();
                ByteArrayOutputStream buffer2 = new ByteArrayOutputStream();
                ByteArrayOutputStream buffer3 = new ByteArrayOutputStream();
                ByteArrayOutputStream buffer4 = new ByteArrayOutputStream();
                ByteArrayOutputStream buffer5 = new ByteArrayOutputStream();
                ByteArrayOutputStream buffer6 = new ByteArrayOutputStream();

                for ( int bbb = 0; bbb < 18; ++bbb ) {
                    if ( ( byteValue = fstream.read() ) != -1 ) {
                        buffer1.write( byteValue );
                    }
                }

                for ( int bbb = 0; bbb < 26; ++bbb ) {
                    if ( ( byteValue = fstream.read() ) != -1 ) {
                        buffer2.write( byteValue );
                    }
                }

                for ( int bbb = 0; bbb < 15; ++bbb ) {
                    if ( ( byteValue = fstream.read() ) != -1 ) {
                        buffer3.write( byteValue );
                    }
                }

                for ( int bbb = 0; bbb < 34; ++bbb ) {
                    if ( ( byteValue = fstream.read() ) != -1 ) {
                        buffer4.write( byteValue );
                    }
                }

                for ( int bbb = 0; bbb < 24; ++bbb ) {
                    if ( ( byteValue = fstream.read() ) != -1 ) {
                        buffer5.write( byteValue );
                    }
                }

                for ( int bbb = 0; bbb < 26; ++bbb ) {
                    if ( ( byteValue = fstream.read() ) != -1 ) {
                        buffer6.write( byteValue );
                    }
                }

                model = stream.processStream( buffer1.toByteArray() );

                Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                                   "with file " + binFilename + " packet 1 " +
                                   " on iteration " + Integer.toString( iii ),
                                   model.isValid() == true );

                assertOnWarnings( model );

                model = stream.processStream( buffer2.toByteArray() );

                Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                                   "with file " + binFilename + " packet 2 " +
                                   " on iteration " + Integer.toString( iii ),
                                   model.isValid() == true );

                assertOnWarnings( model );

                model = stream.processStream( buffer3.toByteArray() );

                Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                                   "with file " + binFilename + " packet 3 " +
                                   " on iteration " + Integer.toString( iii ),
                                   model.isValid() == true );

                assertOnWarnings( model );

                model = stream.processStream( buffer4.toByteArray() );

                Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                                   "with file " + binFilename + " packet 4 " +
                                   " on iteration " + Integer.toString( iii ),
                                   model.isValid() == true );

                assertOnWarnings( model );

                model = stream.processStream( buffer5.toByteArray() );

                Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                                   "with file " + binFilename + " packet 5 " +
                                   " on iteration " + Integer.toString( iii ),
                                   model.isValid() == true );

                assertOnWarnings( model );

                model = stream.processStream( buffer6.toByteArray() );

                Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                                   "with file " + binFilename + " packet 6 " +
                                   " on iteration " + Integer.toString( iii ),
                                   model.isValid() == true );

                assertOnWarnings( model );

                if ( iii % 1000 == 0 ) {
                    System.out.println( String.format( "%06d", iii * 6 ) );
                }

            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

        long estimatedTime = System.currentTimeMillis() - startTime;

        System.out.println( "Elapsed Time " +
            Double.toString( estimatedTime / 1000.0 ) + " seconds" );

    }

    @Test
    public void processPacketsIncludeOnly() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-UniquePackets.bin";

        try {

            XTCETMStream stream = db_.getStream( "CCSDS-TM" );

            File binFile = new File( binFilename );

            InputStream fstream = new FileInputStream( binFile );

            int byteValue;

            ByteArrayOutputStream buffer1 = new ByteArrayOutputStream();
            ByteArrayOutputStream buffer2 = new ByteArrayOutputStream();
            ByteArrayOutputStream buffer3 = new ByteArrayOutputStream();
            ByteArrayOutputStream buffer4 = new ByteArrayOutputStream();
            ByteArrayOutputStream buffer5 = new ByteArrayOutputStream();
            ByteArrayOutputStream buffer6 = new ByteArrayOutputStream();

            for ( int bbb = 0; bbb < 18; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer1.write( byteValue );
                }
            }

            for ( int bbb = 0; bbb < 26; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer2.write( byteValue );
                }
            }

            for ( int bbb = 0; bbb < 15; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer3.write( byteValue );
                }
            }

            for ( int bbb = 0; bbb < 34; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer4.write( byteValue );
                }
            }

            for ( int bbb = 0; bbb < 24; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer5.write( byteValue );
                }
            }

            for ( int bbb = 0; bbb < 26; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer6.write( byteValue );
                }
            }

            List<XTCETMContainer> allList = db_.getContainers();
            List<XTCETMContainer> includeList = new ArrayList<>();
            for ( XTCETMContainer container : allList ) {
                if ( container.getName().equals( "CCSDS_SpacePacket1" ) == true ) {
                    includeList.add( container );
                } else if ( container.getName().equals( "ECSS_Service_1_Subservice_2" ) == true ) {
                    includeList.add( container );
                }
            }

            XTCEContainerContentModel model1 =
                stream.processStreamIncludeOnly( buffer1.toByteArray(), includeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                               "with file " + binFilename +
                               " on container CCSDS_SpacePacket1",
                               model1 != null );

            assertOnWarnings( model1 );

            XTCEContainerContentModel model2 =
                stream.processStreamIncludeOnly( buffer2.toByteArray(), includeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been invalid " +
                               "with file " + binFilename +
                               " on container CCSDS_SpacePacket3",
                               model2 == null );

            XTCEContainerContentModel model3 =
                stream.processStreamIncludeOnly( buffer3.toByteArray(), includeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been invalid " +
                               "with file " + binFilename +
                               " on container ECSS_Service_1_Subservice_1",
                               model3 == null );

            XTCEContainerContentModel model4 =
                stream.processStreamIncludeOnly( buffer4.toByteArray(), includeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                               "with file " + binFilename +
                               " on container ECSS_Service_1_Subservice_2",
                               model4 != null );

            assertOnWarnings( model4 );

            XTCEContainerContentModel model5 =
                stream.processStreamIncludeOnly( buffer5.toByteArray(), includeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been invalid " +
                               "with file " + binFilename +
                               " on container ECSS_SpacePacket2 (noinc)",
                               model5 == null );

            XTCEContainerContentModel model6 =
                stream.processStreamIncludeOnly( buffer6.toByteArray(), includeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been invalid " +
                               "with file " + binFilename +
                               " on container ECSS_SpacePacket2 (inc)",
                               model6 == null );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processPacketsWithExcludes() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String binFilename =
            "src/test/resources/org/xtce/toolkit/test/Container-UniquePackets.bin";

        try {

            XTCETMStream stream = db_.getStream( "CCSDS-TM" );

            File binFile = new File( binFilename );

            InputStream fstream = new FileInputStream( binFile );

            int byteValue;

            ByteArrayOutputStream buffer1 = new ByteArrayOutputStream();
            ByteArrayOutputStream buffer2 = new ByteArrayOutputStream();
            ByteArrayOutputStream buffer3 = new ByteArrayOutputStream();
            ByteArrayOutputStream buffer4 = new ByteArrayOutputStream();
            ByteArrayOutputStream buffer5 = new ByteArrayOutputStream();
            ByteArrayOutputStream buffer6 = new ByteArrayOutputStream();

            for ( int bbb = 0; bbb < 18; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer1.write( byteValue );
                }
            }

            for ( int bbb = 0; bbb < 26; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer2.write( byteValue );
                }
            }

            for ( int bbb = 0; bbb < 15; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer3.write( byteValue );
                }
            }

            for ( int bbb = 0; bbb < 34; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer4.write( byteValue );
                }
            }

            for ( int bbb = 0; bbb < 24; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer5.write( byteValue );
                }
            }

            for ( int bbb = 0; bbb < 26; ++bbb ) {
                if ( ( byteValue = fstream.read() ) != -1 ) {
                    buffer6.write( byteValue );
                }
            }

            List<XTCETMContainer> allList = db_.getContainers();
            List<XTCETMContainer> excludeList = new ArrayList<>();
            for ( XTCETMContainer container : allList ) {
                if ( container.getName().equals( "CCSDS_SpacePacket3" ) == true ) {
                    excludeList.add( container );
                } else if ( container.getName().equals( "ECSS_Service_1_Subservice_1" ) == true ) {
                    excludeList.add( container );
                } else if ( container.getName().equals( "ECSS_SpacePacket2" ) == true ) {
                    excludeList.add( container );
                }
            }

            XTCEContainerContentModel model1 =
                stream.processStreamWithExcludes( buffer1.toByteArray(), excludeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                               "with file " + binFilename +
                               " on container CCSDS_SpacePacket1",
                               model1 != null );

            assertOnWarnings( model1 );

            XTCEContainerContentModel model2 =
                stream.processStreamWithExcludes( buffer2.toByteArray(), excludeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been invalid " +
                               "with file " + binFilename +
                               " on container CCSDS_SpacePacket3",
                               model2 == null );

            XTCEContainerContentModel model3 =
                stream.processStreamWithExcludes( buffer3.toByteArray(), excludeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been invalid " +
                               "with file " + binFilename +
                               " on container ECSS_Service_1_Subservice_1",
                               model3 == null );

            XTCEContainerContentModel model4 =
                stream.processStreamWithExcludes( buffer4.toByteArray(), excludeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been valid " +
                               "with file " + binFilename +
                               " on container ECSS_Service_1_Subservice_2",
                               model4 != null );

            assertOnWarnings( model4 );

            XTCEContainerContentModel model5 =
                stream.processStreamWithExcludes( buffer5.toByteArray(), excludeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been invalid " +
                               "with file " + binFilename +
                               " on container ECSS_SpacePacket2 (noinc)",
                               model5 == null );

            XTCEContainerContentModel model6 =
                stream.processStreamWithExcludes( buffer6.toByteArray(), excludeList );

            Assert.assertTrue( "Processing CCSDS-TM should have been invalid " +
                               "with file " + binFilename +
                               " on container ECSS_SpacePacket2 (inc)",
                               model6 == null );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testXmlOutput() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {
            XTCETMStream stream = db_.getStream( "CCSDS-TM" );
            String streamXml = stream.toXml();
            System.out.println( streamXml );
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Got exception for TM stream XML generation on " +
                         "'CCSDS-TM' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
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

    private XTCEDatabase db_ = null;

}
