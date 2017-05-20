#!/usr/bin/perl -w
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
#
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

use strict;

open( FILE1, ">Container-Calibration_Offsets.bin" );
binmode( FILE1 );

print FILE1 pack( "N", 0x40600000 ); # Battery_Voltage_Offset = 3.5
print FILE1 pack( "n", 0x00 ); # gap of 2 bytes
print FILE1 pack( "N", 0x00000009 ); # Solar_Array_Voltage_1_Offset = 9
print FILE1 pack( "N", 0xfffffffb ); # Solar_Array_Voltage_2_Offset = -5
print FILE1 pack( "N2", 0x402c8000, 0x00000000 ); # Battery_Current_Offset = 14.25
print FILE1 pack( "C", 0x02 ); # Default_CPU_Start_Mode = SAFEHOLD (2)

close( FILE1 );

open( FILE9, ">Container-SensorHistoryBuffer.bin" );
binmode( FILE9 );

print FILE9 pack( "N2", 0x3c31abf5, 0x00000000 ); # SensorTime
print FILE9 pack( "C2", 0x02, 0x02 ); # SunSensorMode = HIGH and EarthSensorMode = HIGH
print FILE9 pack( "n", 0x0003 ); # SunSensorLevel = 45.0
print FILE9 pack( "n", 0x0005 ); # EarthSensorLevel = 10.0
print FILE9 pack( "N2", 0x3c310a10, 0x00000000 ); # SensorTime
print FILE9 pack( "C2", 0x01, 0x01 ); # SunSensorMode = LOW and EarthSensorMode = LOW
print FILE9 pack( "n", 0x0003 ); # SunSensorLevel = 15.0
print FILE9 pack( "n", 0x0005 ); # EarthSensorLevel = 7.5
print FILE9 pack( "N2", 0x3c310d94, 0x00000000 ); # SensorTime
print FILE9 pack( "C2", 0x00, 0x00 ); # SunSensorMode = OFF and EarthSensorMode = OFF
print FILE9 pack( "n", 0x0003 ); # SunSensorLevel = 5.0
print FILE9 pack( "n", 0x0014 ); # EarthSensorLevel = 10.0
print FILE9 pack( "N2", 0x3c311118, 0x00000000 ); # SensorTime = 2002-01-01 01:30:00.000
print FILE9 pack( "C2", 0x02, 0x02 ); # SunSensorMode = HIGH and EarthSensorMode = HIGH
print FILE9 pack( "n", 0x0000 ); # SunSensorLevel = 0.0
print FILE9 pack( "n", 0x0000 ); # EarthSensorLevel = 10.0

close( FILE9 );

open( FILE2, ">Container-CCSDS_SpacePacket1.bin" );
binmode( FILE2 );

print FILE2 pack( "n", 0x0001 ); # CCSDS_Packet_ID = TM, NotPresent, APID 1
print FILE2 pack( "n", 0xcec7 ); # CCSDS_Packet_Sequence with standalone
print FILE2 pack( "n", 0x000b ); # CCSDS_Packet_Length = 11 (payload - 1)
print FILE2 pack( "N", 0x4144cccd ); # Battery_Voltage = 12.3
print FILE2 pack( "N", 0x3f000000 ); # Battery_Current = 0.5
print FILE2 pack( "C", 0x85 ); # Battery_Charge_Mode = CHARGE, SomeParameter = 5
print FILE2 pack( "C3", 0x4c, 0xe4, 0xce ); # Solar_Array_Voltage_1 = 23.0, 2 = 23.0

close( FILE2 );

open( FILE7, ">Container-CCSDS_SpacePacket3.bin" );
binmode( FILE7 );

print FILE7 pack( "n", 0x0003 ); # CCSDS_Packet_ID = TM, NotPresent, APID 1
print FILE7 pack( "n", 0xceca ); # CCSDS_Packet_Sequence with standalone
print FILE7 pack( "n", 0x0013 ); # CCSDS_Packet_Length = 19 (payload - 1)
print FILE7 pack( "N", 0x00000001 ); # Basic_string_uint32 = 1
print FILE7 pack( "N", 0x00000000 ); # Basic_string_uint32 = 0
print FILE7 pack( "N", 0x0000ffff ); # Basic_string_uint32 = 65535
print FILE7 pack( "N", 0x00005555 ); # Basic_string_uint32 = 21845
print FILE7 pack( "N", 0x00001000 ); # Basic_string_uint32 = 4096

close( FILE7 );

open( FILE4, ">Container-CCSDS_SpacePacket1-Bad.bin" );
binmode( FILE4 );

print FILE4 pack( "n", 0x0801 ); # CCSDS_Packet_ID = TM, Present, APID 1
print FILE4 pack( "n", 0xcec7 ); # CCSDS_Packet_Sequence with standalone
print FILE4 pack( "n", 0x000b ); # CCSDS_Packet_Length = 11 (payload - 1)
print FILE4 pack( "N", 0x4144cccd ); # Battery_Voltage = 12.3
print FILE4 pack( "N", 0x3f000000 ); # Battery_Current = 0.5
print FILE4 pack( "C", 0x85 ); # Battery_Charge_Mode = CHARGE, SomeParameter = 5
print FILE4 pack( "C3", 0x4c, 0xe4, 0xce ); # Solar_Array_Voltage_1 = 23.0, 2 = 23.0

close( FILE4 );

open( FILE3, ">Container-ECSS_Service_1_Subservice_1.bin" );
binmode( FILE3 );

print FILE3 pack( "n", 0x0864 ); # CCSDS_Packet_ID = TM, Present, APID 100
print FILE3 pack( "n", 0xcec8 ); # CCSDS_Packet_Sequence with standalone
print FILE3 pack( "n", 0x0008 ); # CCSDS_Packet_Length = 8 (payload - 1)
print FILE3 pack( "C5", 0x10, 0x01, 0x01, 0xf0, 0x02 ); # PUS_Data_Field_Header (1,1)
print FILE3 pack( "n", 0x00ee ); # TC_Packet_ID
print FILE3 pack( "n", 0x5555 ); # TC_Packet_Sequence_Control

close( FILE3 );

open( FILE8, ">Container-ECSS_Service_1_Subservice_2.bin" );
binmode( FILE8 );

print FILE8 pack( "n", 0x0864 ); # CCSDS_Packet_ID = TM, Present, APID 100
print FILE8 pack( "n", 0xcec9 ); # CCSDS_Packet_Sequence with standalone
print FILE8 pack( "n", 0x001b ); # CCSDS_Packet_Length = 27 (payload - 1)
print FILE8 pack( "C5", 0x10, 0x01, 0x02, 0xfe, 0x02 ); # PUS_Data_Field_Header (1,1)
print FILE8 pack( "n", 0x00aa ); # TC_Packet_ID
print FILE8 pack( "n", 0x0101 ); # TC_Packet_Sequence_Control
print FILE8 pack( "n", 0x0005 ); # TC_Accept_Failure_Code = INCONSISTENT_APPL_DATA
print FILE8 pack( "C", 0x0010 ); # TC_Parameter_Count = 16
for ( my $iii = 0; $iii < 16; ++$iii ) {
   print FILE8 pack( "C", $iii ); # TC_Parameter_Data
}

close( FILE8 );

open( FILE5, ">Container-ECSS_3_25_HK-ECSS_SpacePacket2-NoInc.bin" );
binmode( FILE5 );

print FILE5 pack( "n", 0x0864 ); # CCSDS_Packet_ID = TM, Present, APID 100
print FILE5 pack( "n", 0xcec8 ); # CCSDS_Packet_Sequence with standalone
print FILE5 pack( "n", 0x0011 ); # CCSDS_Packet_Length = 17 (payload - 1)
print FILE5 pack( "C5", 0x10, 0x03, 0x19, 0xf1, 0x03 ); # PUS_Data_Field_Header (3,25)
print FILE5 pack( "C", 0x02 ); # PUS_Structure_ID = ECSS_SpacePacket2 (2)
print FILE5 pack( "N", 0x4144cccd ); # Battery_Voltage = 12.3
print FILE5 pack( "N", 0x3f000000 ); # Battery_Current = 0.5
print FILE5 pack( "C", 0x05 ); # Flag_Parameter
print FILE5 pack( "C3", 0x41, 0xa4, 0xce ); # Solar_Array_Voltage_1 = 5.0, 2 = 23.0

close( FILE5 );

open( FILE6, ">Container-ECSS_3_25_HK-ECSS_SpacePacket2-Inc.bin" );
binmode( FILE6 );

print FILE6 pack( "n", 0x0864 ); # CCSDS_Packet_ID = TM, Present, APID 100
print FILE6 pack( "n", 0xcec8 ); # CCSDS_Packet_Sequence with standalone
print FILE6 pack( "n", 0x0013 ); # CCSDS_Packet_Length = 19 (payload - 1)
print FILE6 pack( "C5", 0x10, 0x03, 0x19, 0xf1, 0x03 ); # PUS_Data_Field_Header (3,25)
print FILE6 pack( "C", 0x02 ); # PUS_Structure_ID = ECSS_SpacePacket2 (2)
print FILE6 pack( "N", 0x4144cccd ); # Battery_Voltage = 12.3
print FILE6 pack( "N", 0x3f000000 ); # Battery_Current = 0.5
print FILE6 pack( "C", 0x01 ); # Flag_Parameter
print FILE6 pack( "n", 0xf00f ); # Optional_Parameter
print FILE6 pack( "C3", 0x41, 0xa4, 0xce ); # Solar_Array_Voltage_1 = 5.0, 2 = 23.0

close( FILE6 );

open( FILE10, ">Container-Proc_1_Cfg_Table.bin" );
binmode( FILE10 );

# watch out here for the non-byte aligned values!
print FILE10 pack( "C", 0x01 ); # Table_Processor_ID = PROCESSOR1
print FILE10 pack( "N", 0x10123210 ); # Config_Log_Levels
print FILE10 pack( "C", 0x00 ); # empty space in container
print FILE10 pack( "n", 0x01e0 ); # Config_Watchdog = 30
print FILE10 pack( "n", 0x3e80 ); # Config_PROM_Access = 1000
print FILE10 pack( "n", 0x0630 ); # Config_PROM_Access = 2
print FILE10 pack( "n", 0x0fa7 ); # Config_PROM_Access = 250
print FILE10 pack( "n", 0x9183 ); # Config_PROM_Access = 31000
print FILE10 pack( "C", 0x30 ); # Config_Self_Test = on,on,off,off,on,on
print FILE10 pack( "C", 0x00 ); # Config_Clock_ID = PRIMARY
print FILE10 pack( "n", 0x0061 ); # Config_Ant_Power_Level = 5.8
print FILE10 pack( "C", 0x2a ); # Config_Payload_IF_ID = LINK1, Config_Payload_Perf = NORMAL
print FILE10 pack( "n", 0xbcd0 ); # Table_CRC_Value

close( FILE10 );

open( FILE11, ">Container-Proc_2_Cfg_Table.bin" );
binmode( FILE11 );

# watch out here for the non-byte aligned values!
print FILE11 pack( "C", 0x02 ); # Table_Processor_ID = PROCESSOR2
print FILE11 pack( "N", 0x10123210 ); # Config_Log_Levels
print FILE11 pack( "C", 0x00 ); # empty space in container
print FILE11 pack( "n", 0x01e0 ); # Config_Watchdog = 30
print FILE11 pack( "n", 0x3e80 ); # Config_PROM_Access = 1000
print FILE11 pack( "n", 0x0630 ); # Config_PROM_Access = 2
print FILE11 pack( "n", 0x0fa7 ); # Config_PROM_Access = 250
print FILE11 pack( "n", 0x9183 ); # Config_PROM_Access = 31000
print FILE11 pack( "C", 0x30 ); # Config_Self_Test = on,on,off,off,on,on
print FILE11 pack( "C", 0x00 ); # Config_Clock_ID = PRIMARY
print FILE11 pack( "n", 0x006a ); # Config_Ant_Power_Level = 5.8
print FILE11 pack( "n", 0xbcd0 ); # Table_CRC_Value

close( FILE11 );

if ( -f "Container-10000Packets.bin" ) {
   system( "rm", "-f", "Container-10000Packets.bin" );
}

if ( -f "Conatainer-UniquePackets.bin" ) {
   system( "rm", "-f", "Container-UniquePackets.bin" );
}

system( "touch", "Container-10000Packets.bin" );

for ( my $iii = 0; $iii < 10000; ++$iii ) {

   system( "cat Container-CCSDS_SpacePacket1.bin >> Container-10000Packets.bin" );
   system( "cat Container-CCSDS_SpacePacket3.bin >> Container-10000Packets.bin" );
   system( "cat Container-ECSS_Service_1_Subservice_1.bin >> Container-10000Packets.bin" );
   system( "cat Container-ECSS_Service_1_Subservice_2.bin >> Container-10000Packets.bin" );
   system( "cat Container-ECSS_3_25_HK-ECSS_SpacePacket2-NoInc.bin >> Container-10000Packets.bin" );
   system( "cat Container-ECSS_3_25_HK-ECSS_SpacePacket2-Inc.bin >> Container-10000Packets.bin" );

   if ( $iii == 0 ) {
      system( "cp", "-f", "Container-10000Packets.bin", "Container-UniquePackets.bin" );
   }

}

exit( 0 );
