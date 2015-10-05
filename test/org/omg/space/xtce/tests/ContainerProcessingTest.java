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
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry;
import org.omg.space.xtce.toolkit.XTCEContainerContentModel;
import org.omg.space.xtce.toolkit.XTCEContainerEntryValue;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEDatabaseException;
import org.omg.space.xtce.toolkit.XTCEParameter;
import org.omg.space.xtce.toolkit.XTCESpaceSystem;
import org.omg.space.xtce.toolkit.XTCETMContainer;

/**
 *
 * @author dovereem
 */

public class ContainerProcessingTest {
    
    public ContainerProcessingTest() {

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
    public void lookupSingleContainerFromDatabase() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName;

        containerName = "/BogusSAT/SC001/CCSDS_SpacePacket1";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            Assert.assertTrue( "Container found '" + container.getFullPath() +
                "' should have been '" + containerName + "'",
                container.getFullPath().equals( containerName ) == true );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

        containerName = "/BogusSAT/SC001/CCSDS_SpacePacketX";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            Assert.fail( "Exception should have been thrown on '" +
                containerName + "', which does not exist" );

        } catch ( Exception ex ) {
            // expected
        }

    }

    @Test
    public void lookupSingleContainerFromSpaceSystem() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001" );

            String containerName;

            containerName = "CCSDS_SpacePacket2";

            try {

                XTCETMContainer container = ss.getContainer( containerName );

                Assert.assertTrue( "Container found '" + container.getName() +
                    "' should have been '" + containerName + "'",
                    container.getName().equals( containerName ) == true );

            } catch ( Exception ex ) {
                //ex.printStackTrace();
                Assert.fail( ex.getLocalizedMessage() );
            }

            containerName = "CCSDS_SpacePacketX";

            try {

                XTCETMContainer container = ss.getContainer( containerName );

                Assert.fail( "Exception should have been thrown on '" +
                    containerName + "', which does not exist" );

            } catch ( Exception ex ) {
                // expected
            }

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void lookupAllContainersFromDatabase() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            List<XTCETMContainer> containers = db_.getContainers();

            long expected = 17;

            Assert.assertTrue( "Should have found " +
                Long.toString( expected ) + " containers, but found instead " +
                Long.toString( containers.size() ),
                containers.size() == expected );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void lookupAllContainersFromSpaceSystem() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001" );

            List<XTCETMContainer> containers = ss.getContainers();

            long expected = 10;

            Assert.assertTrue( "Should have found " +
                Long.toString( expected ) + " containers, but found instead " +
                Long.toString( containers.size() ),
                containers.size() == expected );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void lookupMultipleContainersFromDatabase() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            List<XTCETMContainer> containers = db_.getContainers( "*ECSS*" );

            long expected = 5;

            Assert.assertTrue( "'*ECSS*' Should have found " +
                Long.toString( expected ) + " containers, but found instead " +
                Long.toString( containers.size() ),
                containers.size() == expected );

            expected = 0;

            containers = db_.getContainers( "*foo*" );

            Assert.assertTrue( "'*foo*' Should have found " +
                Long.toString( expected ) + " containers, but found instead " +
                Long.toString( containers.size() ),
                containers.size() == expected );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void lookupMultipleContainersFromSpaceSystem() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001" );

            List<XTCETMContainer> containers = ss.getContainers( "*Service*" );

            long expected = 3;

            Assert.assertTrue( "'*Service*' Should have found " +
                Long.toString( expected ) + " containers, but found instead " +
                Long.toString( containers.size() ),
                containers.size() == expected );

            expected = 0;

            containers = ss.getContainers( "*foo*" );

            Assert.assertTrue( "'*foo*' Should have found " +
                Long.toString( expected ) + " containers, but found instead " +
                Long.toString( containers.size() ),
                containers.size() == expected );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processSimpleContainer() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName =
            "/BogusSAT/SC001/Onboard_Tables/Calibration_Offsets";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, null, false );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 184 bits",
                sizeInBytes == 184 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "Battery_Voltage_Offset" ) ) {
                    ++items;
                    checkEntry( entry, "32", "0", "", "2.5", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_1_Offset" ) ) {
                    ++items;
                    checkEntry( entry, "32", "48", "", "2", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_2_Offset" ) ) {
                    ++items;
                    checkEntry( entry, "32", "80", "", "1", "", "" );
                } else if ( entry.getName().equals( "Battery_Current_Offset" ) ) {
                    ++items;
                    checkEntry( entry, "64", "112", "", "-1.5", "", "" );
                } else if ( entry.getName().equals( "Default_CPU_Start_Mode" ) ) {
                    ++items;
                    checkEntry( entry, "8", "176", "", "NORMAL", "", "" );
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
    public void processContainerWithInheritance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket1";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, null, false );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 144 bits",
                sizeInBytes == 144 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==1{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "Battery_Voltage" ) ) {
                    ++items;
                    checkEntry( entry, "32", "48", "", "", "", "" );
                } else if ( entry.getName().equals( "Battery_Current" ) ) {
                    ++items;
                    checkEntry( entry, "32", "80", "", "", "", "" );
                } else if ( entry.getName().equals( "Battery_Charge_Mode" ) ) {
                    ++items;
                    checkEntry( entry, "1", "112", "", "", "", "" );
                } else if ( entry.getName().equals( "SomeParameter" ) ) {
                    ++items;
                    checkEntry( entry, "7", "113", "", "", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_1" ) ) {
                    ++items;
                    checkEntry( entry, "12", "120", "", "", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_2" ) ) {
                    ++items;
                    checkEntry( entry, "12", "132", "", "", "", "" );
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
    public void processContainerWithInheritanceAndIncludesFalse() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_SpacePacket2";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, null, false );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 192 bits",
                sizeInBytes == 192 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==Present{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==100{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Spare1" ) ) {
                    ++items;
                    checkEntry( entry, "1", "48", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "49", "==1{cal}", "1", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Spare4" ) ) {
                    ++items;
                    checkEntry( entry, "4", "52", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Service" ) ) {
                    ++items;
                    checkEntry( entry, "8", "56", "==3{cal}", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Subservice" ) ) {
                    ++items;
                    checkEntry( entry, "8", "64", "==25{cal}", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.SeqCount" ) ) {
                    ++items;
                    checkEntry( entry, "8", "72", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Destination" ) ) {
                    ++items;
                    checkEntry( entry, "8", "80", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Time" ) ) {
                    ++items;
                    checkEntry( entry, "64", "", "", "", "APPL_TIME_CODE!=NotUsed{cal}", "" );
                } else if ( entry.getName().equals( "PUS_Error_Control_Field" ) ) {
                    ++items;
                    checkEntry( entry, "16", "", "", "", "TM_CHECKSUM_TYPE!=NotUsed{cal}", "" );
                } else if ( entry.getName().equals( "PUS_Structure_ID" ) ) {
                    ++items;
                    checkEntry( entry, "8", "88", "==ECSS_SpacePacket2{cal}", "", "", "" );
                } else if ( entry.getName().equals( "Battery_Voltage" ) ) {
                    ++items;
                    checkEntry( entry, "32", "96", "", "", "", "" );
                } else if ( entry.getName().equals( "Battery_Current" ) ) {
                    ++items;
                    checkEntry( entry, "32", "128", "", "", "", "" );
                } else if ( entry.getName().equals( "Flag_Parameter" ) ) {
                    ++items;
                    checkEntry( entry, "8", "160", "", "", "", "" );
                } else if ( entry.getName().equals( "Optional_Parameter" ) ) {
                    ++items;
                    checkEntry( entry, "16", "", "", "", "Flag_Parameter==1{cal}", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_1" ) ) {
                    ++items;
                    checkEntry( entry, "12", "168", "", "", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_2" ) ) {
                    ++items;
                    checkEntry( entry, "12", "180", "", "", "", "" );
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
    public void processContainerWithInheritanceAndIncludesTrue() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_SpacePacket2";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            List<XTCEParameter> swParameters =
                db_.getTelemetryParameters( "Flag_Parameter" );

            Assert.assertTrue( "Should find 1 parameter named 'Flag_Parameter'",
                               swParameters.size() == 1 );

            XTCEContainerEntryValue valueObj =
               new XTCEContainerEntryValue( swParameters.get( 0 ),
                                            "1",
                                            "==",
                                            "Calibrated" );

            ArrayList<XTCEContainerEntryValue> values = new ArrayList<>();
            values.add( valueObj );

            XTCEContainerContentModel model =
                db_.processContainer( container, values, false );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 208 bits",
                sizeInBytes == 208 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==Present{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==100{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Spare1" ) ) {
                    ++items;
                    checkEntry( entry, "1", "48", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "49", "==1{cal}", "1", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Spare4" ) ) {
                    ++items;
                    checkEntry( entry, "4", "52", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Service" ) ) {
                    ++items;
                    checkEntry( entry, "8", "56", "==3{cal}", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Subservice" ) ) {
                    ++items;
                    checkEntry( entry, "8", "64", "==25{cal}", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.SeqCount" ) ) {
                    ++items;
                    checkEntry( entry, "8", "72", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Destination" ) ) {
                    ++items;
                    checkEntry( entry, "8", "80", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Time" ) ) {
                    ++items;
                    checkEntry( entry, "64", "", "", "", "APPL_TIME_CODE!=NotUsed{cal}", "" );
                } else if ( entry.getName().equals( "PUS_Error_Control_Field" ) ) {
                    ++items;
                    checkEntry( entry, "16", "", "", "", "TM_CHECKSUM_TYPE!=NotUsed{cal}", "" );
                } else if ( entry.getName().equals( "PUS_Structure_ID" ) ) {
                    ++items;
                    checkEntry( entry, "8", "88", "==ECSS_SpacePacket2{cal}", "", "", "" );
                } else if ( entry.getName().equals( "Battery_Voltage" ) ) {
                    ++items;
                    checkEntry( entry, "32", "96", "", "", "", "" );
                } else if ( entry.getName().equals( "Battery_Current" ) ) {
                    ++items;
                    checkEntry( entry, "32", "128", "", "", "", "" );
                } else if ( entry.getName().equals( "Flag_Parameter" ) ) {
                    ++items;
                    checkEntry( entry, "8", "160", "==1{cal}", "", "", "" );
                } else if ( entry.getName().equals( "Optional_Parameter" ) ) {
                    ++items;
                    checkEntry( entry, "16", "168", "", "", "Flag_Parameter==1{cal}", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_1" ) ) {
                    ++items;
                    checkEntry( entry, "12", "184", "", "", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_2" ) ) {
                    ++items;
                    checkEntry( entry, "12", "196", "", "", "", "" );
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
    public void processContainerWithInheritanceFixedRepeatParameter() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket3";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, null, false );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 208 bits",
                sizeInBytes == 208 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==3{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_string_uint32" ) ) {
                    ++items;
                    if ( items == 8 ) {
                        checkEntry( entry, "32", "48", "", "", "", "Repeat 1 of 5" );
                    } else if ( items == 9 ) {
                        checkEntry( entry, "32", "80", "", "", "", "Repeat 2 of 5" );
                    } else if ( items == 10 ) {
                        checkEntry( entry, "32", "112", "", "", "", "Repeat 3 of 5" );
                    } else if ( items == 11 ) {
                        checkEntry( entry, "32", "144", "", "", "", "Repeat 4 of 5" );
                    } else if ( items == 12 ) {
                        checkEntry( entry, "32", "176", "", "", "", "Repeat 5 of 5" );
                    }
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

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            List<XTCEParameter> repParameters =
                db_.getTelemetryParameters( "TC_Parameter_Count" );

            Assert.assertTrue( "Should find 1 parameter named 'TC_Parameter_Count'",
                               repParameters.size() == 1 );

            XTCEContainerEntryValue valueObj =
               new XTCEContainerEntryValue( repParameters.get( 0 ),
                                            "3",
                                            "==",
                                            "Calibrated" );

            ArrayList<XTCEContainerEntryValue> values = new ArrayList<>();
            values.add( valueObj );

            XTCEContainerContentModel model =
                db_.processContainer( container, values, false );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 168 bits",
                sizeInBytes == 168 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==Present{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==100{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Spare1" ) ) {
                    ++items;
                    checkEntry( entry, "1", "48", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "49", "==1{cal}", "1", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Spare4" ) ) {
                    ++items;
                    checkEntry( entry, "4", "52", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Service" ) ) {
                    ++items;
                    checkEntry( entry, "8", "56", "==1{cal}", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Subservice" ) ) {
                    ++items;
                    checkEntry( entry, "8", "64", "==2{cal}", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.SeqCount" ) ) {
                    ++items;
                    checkEntry( entry, "8", "72", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Destination" ) ) {
                    ++items;
                    checkEntry( entry, "8", "80", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Time" ) ) {
                    ++items;
                    checkEntry( entry, "64", "", "", "", "APPL_TIME_CODE!=NotUsed{cal}", "" );
                } else if ( entry.getName().equals( "PUS_Error_Control_Field" ) ) {
                    ++items;
                    checkEntry( entry, "16", "", "", "", "TM_CHECKSUM_TYPE!=NotUsed{cal}", "" );
                } else if ( entry.getName().equals( "TC_Packet_ID" ) ) {
                    ++items;
                    checkEntry( entry, "16", "88", "", "", "", "" );
                } else if ( entry.getName().equals( "TC_Packet_Sequence_Control" ) ) {
                    ++items;
                    checkEntry( entry, "16", "104", "", "", "", "" );
                } else if ( entry.getName().equals( "TC_Accept_Failure_Code" ) ) {
                    ++items;
                    checkEntry( entry, "16", "120", "", "", "", "" );
                } else if ( entry.getName().equals( "TC_Parameter_Count" ) ) {
                    ++items;
                    checkEntry( entry, "8", "136", "==3{cal}", "", "", "" );
                } else if ( entry.getName().equals( "TC_Parameter_Data" ) ) {
                    ++items;
                    if ( items == 21 ) {
                        checkEntry( entry, "8", "144", "", "", "", "Repeat 1 of 3" );
                    } else if ( items == 22 ) {
                        checkEntry( entry, "8", "152", "", "", "", "Repeat 2 of 3" );
                    } else if ( items == 23 ) {
                        checkEntry( entry, "8", "160", "", "", "", "Repeat 3 of 3" );
                    }
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
    public void processContainerWithInheritanceDynamicRepeatNoCountParameter() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/ECSS_Service_1_Subservice_2";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, null, false );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 152 bits",
                sizeInBytes == 152 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==Present{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==100{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Spare1" ) ) {
                    ++items;
                    checkEntry( entry, "1", "48", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "49", "==1{cal}", "1", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Spare4" ) ) {
                    ++items;
                    checkEntry( entry, "4", "52", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Service" ) ) {
                    ++items;
                    checkEntry( entry, "8", "56", "==1{cal}", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Subservice" ) ) {
                    ++items;
                    checkEntry( entry, "8", "64", "==2{cal}", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.SeqCount" ) ) {
                    ++items;
                    checkEntry( entry, "8", "72", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Data_Field_Header.Destination" ) ) {
                    ++items;
                    checkEntry( entry, "8", "80", "", "", "", "" );
                } else if ( entry.getName().equals( "PUS_Time" ) ) {
                    ++items;
                    checkEntry( entry, "64", "", "", "", "APPL_TIME_CODE!=NotUsed{cal}", "" );
                } else if ( entry.getName().equals( "PUS_Error_Control_Field" ) ) {
                    ++items;
                    checkEntry( entry, "16", "", "", "", "TM_CHECKSUM_TYPE!=NotUsed{cal}", "" );
                } else if ( entry.getName().equals( "TC_Packet_ID" ) ) {
                    ++items;
                    checkEntry( entry, "16", "88", "", "", "", "" );
                } else if ( entry.getName().equals( "TC_Packet_Sequence_Control" ) ) {
                    ++items;
                    checkEntry( entry, "16", "104", "", "", "", "" );
                } else if ( entry.getName().equals( "TC_Accept_Failure_Code" ) ) {
                    ++items;
                    checkEntry( entry, "16", "120", "", "", "", "" );
                } else if ( entry.getName().equals( "TC_Parameter_Count" ) ) {
                    ++items;
                    checkEntry( entry, "8", "136", "", "", "", "" );
                } else if ( entry.getName().equals( "TC_Parameter_Data" ) ) {
                    ++items;
                    checkEntry( entry, "8", "144", "", "", "", "==TC_Parameter_Count{cal}" );
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 21 items",
                items == 21 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processContainerWithIncludesNotTrueNotExpanded() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket4";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, null, false );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 196 bits",
                sizeInBytes == 196 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==4{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "Payload_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "4", "48", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_Float32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "52", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_Float64" ) ) {
                    ++items;
                    checkEntry( entry, "64", "84", "", "", "", "" );
                } else if ( entry.getName().equals( "enum_int16_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "16", "148", "", "", "", "" );
                } else if ( entry.getName().equals( "enum_int16_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "164", "", "", "", "" );
                } else if ( entry.getName().equals( "enum_int16_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "180", "", "", "", "" );
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
    public void processContainerWithIncludesNotTrueExpanded() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket4";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            XTCEContainerContentModel model =
                db_.processContainer( container, null, true );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 196 bits",
                sizeInBytes == 196 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==4{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "Payload_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "4", "48", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_uint32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_int32_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "32", "", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_int32_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_int32_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_Float32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "52", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_Float64" ) ) {
                    ++items;
                    checkEntry( entry, "64", "84", "", "", "", "" );
                } else if ( entry.getName().equals( "enum_int16_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "16", "148", "", "", "", "" );
                } else if ( entry.getName().equals( "enum_int16_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "164", "", "", "", "" );
                } else if ( entry.getName().equals( "enum_int16_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "180", "", "", "", "" );
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 17 items",
                items == 17 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processContainerWithIncludesTrue() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket4";

        try {

            XTCETMContainer container = db_.getContainer( containerName );

            List<XTCEParameter> repParameters =
                db_.getTelemetryParameters( "Payload_1_State" );

            Assert.assertTrue( "Should find 1 parameter named 'Payload_1_State'",
                               repParameters.size() == 1 );

            XTCEContainerEntryValue valueObj =
               new XTCEContainerEntryValue( repParameters.get( 0 ),
                                            "ON",
                                            "==",
                                            "Calibrated" );

            ArrayList<XTCEContainerEntryValue> values = new ArrayList<>();
            values.add( valueObj );

            XTCEContainerContentModel model =
                db_.processContainer( container, values, true );

            long sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName + " is " +
                Long.toString( sizeInBytes ) + " but should be 324 bits",
                sizeInBytes == 324 );

            List<XTCEContainerContentEntry> entries = model.getContentList();

            long items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==4{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "Payload_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "4", "48", "==ON{cal}", "", "", "" );
                } else if ( entry.getName().equals( "Basic_uint32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "52", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_int32_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "32", "84", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_int32_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "116", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_int32_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "148", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_Float32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "180", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_Float64" ) ) {
                    ++items;
                    checkEntry( entry, "64", "212", "", "", "", "" );
                } else if ( entry.getName().equals( "enum_int16_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "16", "276", "", "", "", "" );
                } else if ( entry.getName().equals( "enum_int16_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "292", "", "", "", "" );
                } else if ( entry.getName().equals( "enum_int16_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "308", "", "", "", "" );
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 17 items",
                items == 17 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processMultiIncludesCase1() {

        // container with include, inside is parameters with includes of both
        // == and !=, here we set no user values but check both processing
        // the container with and without showing all conditionals

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket5";

        XTCETMContainer container;
        XTCEContainerContentModel model;
        List<XTCEContainerContentEntry> entries;
        long sizeInBytes;
        long items;

        try {

            container = db_.getContainer( containerName );

            model = db_.processContainer( container, null, true );

            sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Expanded Container size of " + containerName +
                " is " +
                Long.toString( sizeInBytes ) + " but should be 188 bits",
                sizeInBytes == 188 );

            entries = model.getContentList();

            items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==5{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "Payload_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "4", "48", "", "", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "8", "52", "", "OFF", "", "" );
                } else if ( entry.getName().equals( "enum_binary" ) ) {
                    ++items;
                    checkEntry( entry, "32", "", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_float32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_float64" ) ) {
                    ++items;
                    checkEntry( entry, "64", "", "", "", "Payload_1_State==ON{cal},Solar_Array_Voltage_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "16", "", "", "", "Payload_1_State==ON{cal},Solar_Array_Voltage_1_State!=ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_uint32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "60", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "32", "92", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "124", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "156", "", "", "", "" );
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 19 items",
                items == 19 );

            assertOnWarnings( model );

            container = db_.getContainer( containerName );

            model = db_.processContainer( container, null, false );

            sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Container size of " + containerName +
                " is " +
                Long.toString( sizeInBytes ) + " but should be 188 bits",
                sizeInBytes == 188 );

            entries = model.getContentList();

            items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==5{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "Payload_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "4", "48", "", "", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "8", "52", "", "OFF", "", "" );
                } else if ( entry.getName().equals( "Basic_uint32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "60", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "32", "92", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "124", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "156", "", "", "", "" );
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
    public void processMultiIncludesCase2() {

        // container with include, inside is parameters with includes of both
        // == and !=, here we set no user values but check both processing
        // the container with and without showing all conditionals

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket5";

        XTCETMContainer container;
        XTCEContainerContentModel model;
        List<XTCEContainerContentEntry> entries;
        long sizeInBytes;
        long items;

        try {

            container = db_.getContainer( containerName );

            List<XTCEParameter> repParameters =
                db_.getTelemetryParameters( "Payload_1_State" );

            Assert.assertTrue( "Should find 1 parameter named 'Payload_1_State'",
                               repParameters.size() == 1 );

            XTCEContainerEntryValue valueObj =
               new XTCEContainerEntryValue( repParameters.get( 0 ),
                                            "ON",
                                            "==",
                                            "Calibrated" );

            ArrayList<XTCEContainerEntryValue> values = new ArrayList<>();
            values.add( valueObj );

            model = db_.processContainer( container, values, true );

            sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Expanded Container size of " + containerName +
                " is " +
                Long.toString( sizeInBytes ) + " but should be 284 bits",
                sizeInBytes == 284 );

            entries = model.getContentList();

            items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==5{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "Payload_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "4", "48", "==ON{cal}", "", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "8", "52", "", "OFF", "", "" );
                } else if ( entry.getName().equals( "enum_binary" ) ) {
                    ++items;
                    checkEntry( entry, "32", "60", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_float32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "92", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_float64" ) ) {
                    ++items;
                    checkEntry( entry, "64", "", "", "", "Payload_1_State==ON{cal},Solar_Array_Voltage_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "16", "", "", "", "Payload_1_State==ON{cal},Solar_Array_Voltage_1_State!=ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "124", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "140", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_uint32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "156", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "32", "188", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "220", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "252", "", "", "", "" );
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 19 items",
                items == 19 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processMultiIncludesCase3() {

        // container with include, inside is parameters with includes of both
        // == and !=, here we set no user values but check both processing
        // the container with and without showing all conditionals

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket5";

        XTCETMContainer container;
        XTCEContainerContentModel model;
        List<XTCEContainerContentEntry> entries;
        long sizeInBytes;
        long items;

        try {

            container = db_.getContainer( containerName );

            List<XTCEParameter> repParameters =
                db_.getTelemetryParameters( "Payload_1_State" );

            Assert.assertTrue( "Should find 1 parameter named 'Payload_1_State'",
                               repParameters.size() == 1 );

            XTCEContainerEntryValue valueObj1 =
               new XTCEContainerEntryValue( repParameters.get( 0 ),
                                            "ON",
                                            "==",
                                            "Calibrated" );

            repParameters =
                db_.getTelemetryParameters( "Solar_Array_Voltage_1_State" );

            Assert.assertTrue( "Should find 1 parameter named 'Solar_Array_Voltage_1_State'",
                               repParameters.size() == 1 );

            XTCEContainerEntryValue valueObj2 =
               new XTCEContainerEntryValue( repParameters.get( 0 ),
                                            "ON",
                                            "==",
                                            "Calibrated" );

            ArrayList<XTCEContainerEntryValue> values = new ArrayList<>();
            values.add( valueObj1 );
            values.add( valueObj2 );

            model = db_.processContainer( container, values, true );

            sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Expanded Container size of " + containerName +
                " is " +
                Long.toString( sizeInBytes ) + " but should be 348 bits",
                sizeInBytes == 348 );

            entries = model.getContentList();

            items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==5{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "Payload_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "4", "48", "==ON{cal}", "", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "8", "52", "==ON{cal}", "OFF", "", "" );
                } else if ( entry.getName().equals( "enum_binary" ) ) {
                    ++items;
                    checkEntry( entry, "32", "60", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_float32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "92", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_float64" ) ) {
                    ++items;
                    checkEntry( entry, "64", "124", "", "", "Payload_1_State==ON{cal},Solar_Array_Voltage_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "16", "", "", "", "Payload_1_State==ON{cal},Solar_Array_Voltage_1_State!=ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "188", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "204", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_uint32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "220", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "32", "252", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "284", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "316", "", "", "", "" );
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 19 items",
                items == 19 );

            assertOnWarnings( model );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void processMultiIncludesCase4() {

        // container with include, inside is parameters with includes of both
        // == and !=, here we set no user values but check both processing
        // the container with and without showing all conditionals

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String containerName = "/BogusSAT/SC001/CCSDS_SpacePacket5";

        XTCETMContainer container;
        XTCEContainerContentModel model;
        List<XTCEContainerContentEntry> entries;
        long sizeInBytes;
        long items;

        try {

            container = db_.getContainer( containerName );

            List<XTCEParameter> repParameters =
                db_.getTelemetryParameters( "Payload_1_State" );

            Assert.assertTrue( "Should find 1 parameter named 'Payload_1_State'",
                               repParameters.size() == 1 );

            XTCEContainerEntryValue valueObj1 =
               new XTCEContainerEntryValue( repParameters.get( 0 ),
                                            "ON",
                                            "==",
                                            "Calibrated" );

            repParameters =
                db_.getTelemetryParameters( "Solar_Array_Voltage_1_State" );

            Assert.assertTrue( "Should find 1 parameter named 'Solar_Array_Voltage_1_State'",
                               repParameters.size() == 1 );

            XTCEContainerEntryValue valueObj2 =
               new XTCEContainerEntryValue( repParameters.get( 0 ),
                                            "OFF",
                                            "==",
                                            "Calibrated" );

            ArrayList<XTCEContainerEntryValue> values = new ArrayList<>();
            values.add( valueObj1 );
            values.add( valueObj2 );

            model = db_.processContainer( container, values, true );

            sizeInBytes = model.getTotalSize();

            Assert.assertTrue( "Expanded Container size of " + containerName +
                " is " +
                Long.toString( sizeInBytes ) + " but should be 300 bits",
                sizeInBytes == 300 );

            entries = model.getContentList();

            items = 0;

            for ( XTCEContainerContentEntry entry : entries ) {

                if ( entry.getName().equals( "CCSDS_Packet_ID.Version" ) ) {
                    ++items;
                    checkEntry( entry, "3", "0", "==0{cal}", "0", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.Type" ) ) {
                    ++items;
                    checkEntry( entry, "1", "3", "==TM{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.SecHdrFlag" ) ) {
                    ++items;
                    checkEntry( entry, "1", "4", "==NotPresent{cal}", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_ID.APID" ) ) {
                    ++items;
                    checkEntry( entry, "11", "5", "==5{cal}", "2047", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.GroupFlags" ) ) {
                    ++items;
                    checkEntry( entry, "2", "16", "", "3", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Sequence.Count" ) ) {
                    ++items;
                    checkEntry( entry, "14", "18", "", "", "", "" );
                } else if ( entry.getName().equals( "CCSDS_Packet_Length" ) ) {
                    ++items;
                    checkEntry( entry, "16", "32", "", "", "", "" );
                } else if ( entry.getName().equals( "Payload_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "4", "48", "==ON{cal}", "", "", "" );
                } else if ( entry.getName().equals( "Solar_Array_Voltage_1_State" ) ) {
                    ++items;
                    checkEntry( entry, "8", "52", "==OFF{cal}", "OFF", "", "" );
                } else if ( entry.getName().equals( "enum_binary" ) ) {
                    ++items;
                    checkEntry( entry, "32", "60", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_float32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "92", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_float64" ) ) {
                    ++items;
                    checkEntry( entry, "64", "", "", "", "Payload_1_State==ON{cal},Solar_Array_Voltage_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "16", "124", "", "", "Payload_1_State==ON{cal},Solar_Array_Voltage_1_State!=ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "140", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "enum_int16_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "16", "156", "", "", "Payload_1_State==ON{cal}", "" );
                } else if ( entry.getName().equals( "Basic_uint32" ) ) {
                    ++items;
                    checkEntry( entry, "32", "172", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_signmag" ) ) {
                    ++items;
                    checkEntry( entry, "32", "204", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_twoscomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "236", "", "", "", "" );
                } else if ( entry.getName().equals( "Basic_int32_onescomp" ) ) {
                    ++items;
                    checkEntry( entry, "32", "268", "", "", "", "" );
                }

            }

            Assert.assertTrue( "Container parameter count of " + containerName + " is " +
                Long.toString( items ) + " but should be 19 items",
                items == 19 );

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

    private void loadDocument() throws XTCEDatabaseException {

        System.out.println( "Loading the BogusSat-2.xml demo database" );

        String file = "src/org/omg/space/xtce/database/BogusSat-2.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;

}
