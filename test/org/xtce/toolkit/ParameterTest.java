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

public class ParameterTest {
    
    public ParameterTest() {

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
    public void testBasicParameterRetrieval() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT" );

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "FOOBAR" );
            Assert.fail( "Should have gotten an exception for getting parameter named 'FOOBAR'" );
        } catch ( XTCEDatabaseException ex ) {
            // do nothing
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'FOOBAR' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        try {
            XTCEParameter ppp = ss.getTelecommandParameter( "FOOBAR" );
            Assert.fail( "Should have gotten an exception for getting parameter named 'FOOBAR'" );
        } catch ( XTCEDatabaseException ex ) {
            // do nothing
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'FOOBAR' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "CCSDS_Packet_Length" );
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'CCSDS_Packet_Length'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'CCSDS_Packet_Length' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        try {
            XTCEParameter ppp = ss.getTelecommandParameter( "TC_CHECKSUM" );
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'TC_CHECKSUM'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'TC_CHECKSUM' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "CCSDS_Packet_Length" );
            if ( ss.isTelemetryParameter( "CCSDS_Packet_Length" ) == false ) {
                Assert.fail( "'CCSDS_Packet_Length' should have isTelemetryParameter == true" );
            }
            if ( ss.isTelecommandParameter( "CCSDS_Packet_Length" ) == true ) {
                Assert.fail( "'CCSDS_Packet_Length' should have isTelecommandParameter == false" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'CCSDS_Packet_Length'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'CCSDS_Packet_Length' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        try {
            XTCEParameter ppp = ss.getTelecommandParameter( "TC_CHECKSUM" );
            if ( ss.isTelemetryParameter( "TC_CHECKSUM" ) == true ) {
                Assert.fail( "'TC_CHECKSUM' should have isTelemetryParameter == false" );
            }
            if ( ss.isTelecommandParameter( "TC_CHECKSUM" ) == false ) {
                Assert.fail( "'TC_CHECKSUM' should have isTelecommandParameter == true" );
            }
            if ( ppp.getSpaceSystemName().equals( "BogusSAT" ) == false ) {
                Assert.fail( "'TC_CHECKSUM' should have space system name 'BogusSAT'" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'TC_CHECKSUM'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'TC_CHECKSUM' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        try {
            XTCEParameter ppp1 = ss.getTelemetryParameter( "CCSDS_Packet_Length" );
            XTCEParameter ppp2 = ss.getTelemetryParameter( "PUS_Time" );
            XTCEParameter ppp3 = ss.getTelemetryParameter( "CCSDS_Packet_Length" );
            if ( ppp1.equals( "/BogusSAT/CCSDS_Packet_Length" ) == false ) {
                Assert.fail( "Parameter1 should equal string '/BogusSAT/CCSDS_Packet_Length'" );
            }
            if ( ppp1.equals( ppp3 ) == false ) {
                Assert.fail( "Parameter1 should equal parameter3" );
            }
            if ( ppp1.equals( ppp2 ) == true ) {
                Assert.fail( "Parameter1 should NOT equal parameter2" );
            }
            if ( ppp1.equals( ss ) == true ) {
                Assert.fail( "Parameter1 should never equal a SpaceSystem" );
            }
            if ( ppp1.compareTo( ppp3 ) != 0 ) {
                Assert.fail( "Parameter1 should compareTo parameter3 == 0" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'CCSDS_Packet_Length' or 'PUS_Time'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'CCSDS_Packet_Length' or 'PUS_Time' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

    }

    @Test
    public void testMemberParameterRetrieval() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT" );

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "CCSDS_Packet_ID.APID" );
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'CCSDS_Packet_ID.APID'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'CCSDS_Packet_ID.APID' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        // Dont have a Telecommand Parameter with members in this test db
        //try {
        //    XTCEParameter ppp = ss.getTelecommandParameter( "TC_CHECKSUM" );
        //} catch ( XTCEDatabaseException ex ) {
        //    Assert.fail( "Should not have gotten an exception for getting parameter named 'TC_CHECKSUM'" );
        //} catch ( Exception ex ) {
        //    Assert.fail( "Got unknown exception for getting parameter " +
        //                 "'TC_CHECKSUM' with '" +
        //                 ex.getLocalizedMessage() +
        //                 "'" );
        //}

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "CCSDS_Packet_ID.APID" );
            if ( ss.isTelemetryParameter( "CCSDS_Packet_ID.APID" ) == false ) {
                Assert.fail( "'CCSDS_Packet_ID.APID' should have isTelemetryParameter == true" );
            }
            if ( ss.isTelecommandParameter( "CCSDS_Packet_ID.APID" ) == true ) {
                Assert.fail( "'CCSDS_Packet_ID.APID' should have isTelecommandParameter == false" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'CCSDS_Packet_ID.APID'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'CCSDS_Packet_ID.APID' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        // Dont have a Telecommand Parameter with members in this test db
        //try {
        //    XTCEParameter ppp = ss.getTelecommandParameter( "TC_CHECKSUM" );
        //    if ( ss.isTelemetryParameter( "TC_CHECKSUM" ) == true ) {
        //        Assert.fail( "'TC_CHECKSUM' should have isTelemetryParameter == false" );
        //    }
        //    if ( ss.isTelecommandParameter( "TC_CHECKSUM" ) == false ) {
        //        Assert.fail( "'TC_CHECKSUM' should have isTelecommandParameter == true" );
        //    }
        //} catch ( XTCEDatabaseException ex ) {
        //    Assert.fail( "Should not have gotten an exception for getting parameter named 'TC_CHECKSUM'" );
        //} catch ( Exception ex ) {
        //    Assert.fail( "Got unknown exception for getting parameter " +
        //                 "'TC_CHECKSUM' with '" +
        //                 ex.getLocalizedMessage() +
        //                 "'" );
        //}

    }

    @Test
    public void testAttributeLogic() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" );

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "Battery_Voltage" );
            if ( ppp.getDataSource().equals( "telemetered" ) == false ) {
                Assert.fail( "Parameter 'Battery_Voltage' should be 'telemetered'" );
            }
            if ( ppp.getSystemName().equals( "EPS" ) == false ) {
                Assert.fail( "Parameter 'Battery_Voltage' should have SystemName 'EPS'" );
            }
            if ( ppp.isReadOnly() == false ) {
                Assert.fail( "Parameter 'Battery_Voltage' should be read only" );
            }
            if ( ppp.isSettable() == true ) {
                Assert.fail( "Parameter 'Battery_Voltage' should not be settable" );
            }
            if ( ppp.getTypeReferenceFullPath().equals( "/BogusSAT/SC001/BusElectronics/Battery_Voltage_Type" ) == false ) {
                Assert.fail( "Parameter 'Battery_Voltage' should have type reference of '/BogusSAT/SC001/BusElectronics/Battery_Voltage_Type'" );
            }
            if ( ppp.isMember() == true ) {
                Assert.fail( "Parameter 'Battery_Voltage' should not be a Member" );
            }
            if ( ppp.getMemberReference() != null ) {
                Assert.fail( "Parameter 'Battery_Voltage' should not have a Member Reference" );
            }
            if ( ppp.getChangeThreshold().equals( "0.01" ) == false ) {
                Assert.fail( "Parameter 'Battery_Voltage' should have a change threshold of '0.01'" );
            } 
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'Battery_Voltage'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'Battery_Voltage' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "Battery_State_Of_Charge" );
            if ( ppp.getDataSource().equals( "derived" ) == false ) {
                Assert.fail( "Parameter 'Battery_State_Of_Charge' should be 'derived'" );
            }
            if ( ppp.getSystemName().equals( "EPS" ) == false ) {
                Assert.fail( "Parameter 'Battery_State_Of_Charge' should have SystemName 'EPS'" );
            }
            if ( ppp.isReadOnly() == false ) {
                Assert.fail( "Parameter 'Battery_State_Of_Charge' should not be settable" );
            }
            if ( ppp.getTypeReferenceFullPath().equals( "/BogusSAT/SC001/BusElectronics/Battery_State_Of_Charge_Type" ) == false ) {
                Assert.fail( "Parameter 'Battery_Voltage' should have type reference of '/BogusSAT/SC001/BusElectronics/Battery_State_Of_Charge_Type'" );
            }
            if ( ppp.isMember() == true ) {
                Assert.fail( "Parameter 'Battery_State_Of_Charge' should not be a Member" );
            }
            if ( ppp.getMemberReference() != null ) {
                Assert.fail( "Parameter 'Battery_State_Of_Charge' should not have a Member Reference" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'Battery_State_Of_Charge'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'Battery_State_Of_Charge' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        ss = db_.getSpaceSystem( "/BogusSAT/SC001/Onboard_Tables" );

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "Solar_Array_Voltage_1_Offset" );
            if ( ppp.getDataSource().equals( "telemetered" ) == false ) {
                Assert.fail( "Parameter 'Solar_Array_Voltage_1_Offset' should be 'telemetered'" );
            }
            if ( ppp.getSystemName().isEmpty() == false ) {
                Assert.fail( "Parameter 'Solar_Array_Voltage_1_Offset' should have SystemName ''" );
            }
            if ( ppp.isReadOnly() == true ) {
                Assert.fail( "Parameter 'Solar_Array_Voltage_1_Offset' should not be read only" );
            }
            if ( ppp.isSettable() == false ) {
                Assert.fail( "Parameter 'Solar_Array_Voltage_1_Offset' should be settable" );
            }
            if ( ppp.getTypeReferenceFullPath().equals( "/BogusSAT/SC001/Onboard_Tables/Uint32_Offset" ) == false ) {
                Assert.fail( "Parameter 'Solar_Array_Voltage_1_Offset' should have type reference of '/BogusSAT/SC001/Onboard_Tables/Uint32_Offset'" );
            }
            if ( ppp.isMember() == true ) {
                Assert.fail( "Parameter 'Solar_Array_Voltage_1_Offset' should not be a Member" );
            }
            if ( ppp.getMemberReference() != null ) {
                Assert.fail( "Parameter 'Solar_Array_Voltage_1_Offset' should not have a Member Reference" );
            }
            if ( ppp.getChangeThreshold().isEmpty() == false ) {
                Assert.fail( "Parameter 'Solar_Array_Voltage_1_Offset' should have a change threshold of ''" );
            } 
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'Solar_Array_Voltage_1_Offset'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'Solar_Array_Voltage_1_Offset' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        ss = db_.getSpaceSystem( "/BogusSAT" );

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "CCSDS_Packet_ID.APID" );
            if ( ppp.getDataSource().equals( "telemetered" ) == false ) {
                Assert.fail( "Parameter 'CCSDS_Packet_ID.APID' should be 'telemetered'" );
            }
            if ( ppp.getSystemName().isEmpty() == false ) {
                Assert.fail( "Parameter 'CCSDS_Packet_ID.APID' should have SystemName ''" );
            }
            if ( ppp.isReadOnly() == true ) {
                Assert.fail( "Parameter 'CCSDS_Packet_ID.APID' should be settable" );
            }
            if ( ppp.getTypeReferenceFullPath().equals( "/BogusSAT/CCSDSAPIDType" ) == false ) {
                Assert.fail( "Parameter 'CCSDS_Packet_ID.APID' should have type reference of '/BogusSAT/CCSDSAPIDType'" );
            }
            if ( ppp.isMember() == false ) {
                Assert.fail( "Parameter 'CCSDS_Packet_ID.APID' should be a Member" );
            }
            if ( ppp.getMemberReference() == null ) {
                Assert.fail( "Parameter 'CCSDS_Packet_ID.APID' should have a Member Reference" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'CCSDS_Packet_ID.APID'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'CCSDS_Packet_ID.APID' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "PUS_Data_Field_Header.Service" );
            if ( ppp.getDataSource().equals( "telemetered" ) == false ) {
                Assert.fail( "Parameter 'PUS_Data_Field_Header.Service' should be 'telemetered'" );
            }
            if ( ppp.getSystemName().equals( "PUS" ) == false ) {
                Assert.fail( "Parameter 'PUS_Data_Field_Header.Service' should have SystemName 'PUS'" );
            }
            if ( ppp.isReadOnly() == false ) {
                Assert.fail( "Parameter 'PUS_Data_Field_Header.Service' should be read only" );
            }
            if ( ppp.getTypeReferenceFullPath().equals( "/BogusSAT/PUSServiceType" ) == false ) {
                Assert.fail( "Parameter 'PUS_Data_Field_Header.Service' should have type reference of '/BogusSAT/PUSServiceType'" );
            }
            if ( ppp.isMember() == false ) {
                Assert.fail( "Parameter 'PUS_Data_Field_Header.Service' should be a Member" );
            }
            if ( ppp.getMemberReference() == null ) {
                Assert.fail( "Parameter 'PUS_Data_Field_Header.Service' should have a Member Reference" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'PUS_Data_Field_Header.Service'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'PUS_Data_Field_Header.Service' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

    }

    @Test
    public void testDescriptionLogic() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" );

        // no description case

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "Flag_Parameter" );
            if ( ppp.getShortDescription().isEmpty() == false ) {
                Assert.fail( "Parameter 'Flag_Parameter' should have no Short Description" );
            }
            if ( ppp.getLongDescription().isEmpty() == false ) {
                Assert.fail( "Parameter 'Flag_Parameter' should have no Long Description" );
            }
            if ( ppp.getDescription().isEmpty() == false ) {
                Assert.fail( "Parameter 'Flag_Parameter' should have no Combined Description" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'Flag_Parameter'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'Flag_Parameter' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        // shortDescription on type, no shortDescription on Parameter

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "boolean_float64" );
            if ( ppp.getShortDescription().equals( "Generic boolean EU type with float64 data encoding" ) == false ) {
                Assert.fail( "Parameter 'boolean_float64' should have a Short Description" );
            }
            if ( ppp.getLongDescription().isEmpty() == false ) {
                Assert.fail( "Parameter 'boolean_float64' should have no Long Description" );
            }
            if ( ppp.getDescription().equals( "Generic boolean EU type with float64 data encoding" ) == false ) {
                Assert.fail( "Parameter 'boolean_float64' should have a Combined Description" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'boolean_float64'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'boolean_float64' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        // no shortDescription on type, shortDescription on Parameter

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "SunSensorMode" );
            if ( ppp.getShortDescription().equals( "Operational mode of the sun sensor" ) == false ) {
                Assert.fail( "Parameter 'SunSensorMode' should have a Short Description" );
            }
            if ( ppp.getLongDescription().isEmpty() == false ) {
                Assert.fail( "Parameter 'SunSensorMode' should have no Long Description" );
            }
            if ( ppp.getDescription().equals( "Operational mode of the sun sensor" ) == false ) {
                Assert.fail( "Parameter 'SunSensorMode' should have a Combined Description" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'SunSensorMode'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'SunSensorMode' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        // shortDescription on type, shortDescription on Parameter

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "SensorTime" );
            if ( ppp.getShortDescription().equals( "Measurement time of the sensor data" ) == false ) {
                Assert.fail( "Parameter 'SensorTime' should have a Short Description" );
            }
            if ( ppp.getLongDescription().isEmpty() == false ) {
                Assert.fail( "Parameter 'SensorTime' should have no Long Description" );
            }
            if ( ppp.getDescription().equals( "Measurement time of the sensor data" ) == false ) {
                Assert.fail( "Parameter 'SensorTime' should have a Combined Description" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'SensorTime'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'SensorTime' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        // shortDescription and long description for Member

        ss = db_.getSpaceSystem( "/BogusSAT" );

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "CCSDS_Packet_ID.Type" );
            if ( ppp.getShortDescription().equals( "CCSDS Packet Type" ) == false ) {
                Assert.fail( "Parameter 'CCSDS_Packet_ID.Type' should have a Short Description" );
            }
            if ( ppp.getLongDescription().startsWith( "This bit distinguishes " ) == false ) {
                Assert.fail( "Parameter 'CCSDS_Packet_ID.Type' should have a Long Description" );
            }
            if ( ppp.getDescription().equals( "CCSDS Packet Type" ) == false ) {
                Assert.fail( "Parameter 'CCSDS_Packet_ID.Type' should have a Combined Description" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'CCSDS_Packet_ID.Type'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'CCSDS_Packet_ID.Type' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        // long description on type, no long description on parameter

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "CCSDS_Packet_Length" );
            if ( ppp.getShortDescription().equals( "CCSDS Packet Length Field" ) == false ) {
                Assert.fail( "Parameter 'CCSDS_Packet_Length' should have a Short Description" );
            }
            if ( ppp.getLongDescription().startsWith( "The packet length field " ) == false ) {
                Assert.fail( "Parameter 'CCSDS_Packet_Length' should have a Long Description" );
            }
            if ( ppp.getDescription().equals( "CCSDS Packet Length Field" ) == false ) {
                Assert.fail( "Parameter 'CCSDS_Packet_Length' should have a Combined Description" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'CCSDS_Packet_Length'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'CCSDS_Packet_Length' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        // no long description on type, long description on parameter

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "APPL_TIME_CODE" );
            if ( ppp.getShortDescription().equals( "ECSS PUS Constant" ) == false ) {
                Assert.fail( "Parameter 'APPL_TIME_CODE' should have a Short Description" );
            }
            if ( ppp.getLongDescription().startsWith( "This mission constant " ) == false ) {
                Assert.fail( "Parameter 'APPL_TIME_CODE' should have a Long Description" );
            }
            if ( ppp.getDescription().equals( "ECSS PUS Constant" ) == false ) {
                Assert.fail( "Parameter 'APPL_TIME_CODE' should have a Combined Description" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'APPL_TIME_CODE'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'APPL_TIME_CODE' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        // long description on type, long description on parameter

        ss = db_.getSpaceSystem( "/BogusSAT/SC001/Onboard_Tables" );

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "Battery_Voltage_Offset" );
            if ( ppp.getShortDescription().equals( "Correction for battery voltage measurements" ) == false ) {
                Assert.fail( "Parameter 'Battery_Voltage_Offset' should have a Short Description" );
            }
            if ( ppp.getLongDescription().equals( "Extended discussion about the nature of the battery voltage offset" ) == false ) {
                Assert.fail( "Parameter 'Battery_Voltage_Offset' should have a Long Description" );
            }
            if ( ppp.getDescription().equals( "Correction for battery voltage measurements" ) == false ) {
                Assert.fail( "Parameter 'Battery_Voltage_Offset' should have a Combined Description" );
            }
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Should not have gotten an exception for getting parameter named 'Battery_Voltage_Offset'" );
        } catch ( Exception ex ) {
            Assert.fail( "Got unknown exception for getting parameter " +
                         "'Battery_Voltage_Offset' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

    }

    @Test
    public void testGettingAllParameters() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        List<XTCEParameter> params;

        int expected;

        params = db_.getTelemetryParameters();
        expected = 144;
        if ( params.size() != expected ) {
            Assert.fail( "Should have seen " +
                         Integer.toString( expected ) +
                         " telemetry parameters but received " +
                         Integer.toString( params.size() ) +
                         " instead" );
        }
        params = db_.getTelecommandParameters();
        expected = 1;
        if ( params.size() != expected ) {
            Assert.fail( "Should have seen " +
                         Integer.toString( expected ) +
                         " telecommand parameters but received " +
                         Integer.toString( params.size() ) +
                         " instead" );
        }
        params = db_.getParameters();
        expected = 145;
        if ( params.size() != expected ) {
            Assert.fail( "Should have seen " +
                         Integer.toString( expected ) +
                         " total parameters but received " +
                         Integer.toString( params.size() ) +
                         " instead" );
        }

    }

    @Test
    public void testGettingAllParametersByAlias() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        List<XTCEParameter> params;

        int expected;

        params = db_.getTelemetryParameters( "100*", "ID" );
        expected = 3;
        if ( params.size() != expected ) {
            Assert.fail( "Should have seen " +
                         Integer.toString( expected ) +
                         " telemetry parameters but received " +
                         Integer.toString( params.size() ) +
                         " instead" );
        }
        params = db_.getTelecommandParameters( "100*", "ID" );
        expected = 0;
        if ( params.size() != expected ) {
            Assert.fail( "Should have seen " +
                         Integer.toString( expected ) +
                         " telecommand parameters but received " +
                         Integer.toString( params.size() ) +
                         " instead" );
        }

    }

    @Test
    public void testXmlOutput() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT" );

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "PUS_Time" );
            String parameterXml     = ppp.toXml();
            String parameterTypeXml = ppp.typeToXml();
            System.out.println( parameterXml );
            System.out.println( parameterTypeXml );
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Got exception for parameter XML generation on " +
                         "'PUS_Time' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        try {
            XTCEParameter ppp = ss.getTelemetryParameter( "PUS_Data_Field_Header.Subservice" );
            String parameterXml     = ppp.toXml();
            String parameterTypeXml = ppp.typeToXml();
            System.out.println( parameterXml );
            System.out.println( parameterTypeXml );
        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Got exception for parameter XML generation on " +
                         "'PUS_Data_Field_Header.Subservice' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

    }

    private void loadDocument() throws XTCEDatabaseException {

        System.out.println( "Loading the BogusSAT-2.xml demo database" );

        String file = "src/org/xtce/toolkit/database/examples/BogusSAT-2.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase db_ = null;

}
