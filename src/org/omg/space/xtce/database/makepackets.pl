#!/usr/bin/perl -w

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

if ( -f "Container-10000Packets.bin" ) {
   system( "rm", "-f", "Container-10000Packets.bin" );
}

system( "touch", "Container-10000Packets.bin" );

for ( my $iii = 0; $iii < 10000; ++$iii ) {

   system( "cat Container-CCSDS_SpacePacket1.bin >> Container-10000Packets.bin" );
   system( "cat Container-ECSS_Service_1_Subservice_1.bin >> Container-10000Packets.bin" );
   system( "cat Container-ECSS_3_25_HK-ECSS_SpacePacket2-NoInc.bin >> Container-10000Packets.bin" );
   system( "cat Container-ECSS_3_25_HK-ECSS_SpacePacket2-Inc.bin >> Container-10000Packets.bin" );

}

exit( 0 );

