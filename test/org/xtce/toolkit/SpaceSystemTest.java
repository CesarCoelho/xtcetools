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
import org.omg.space.xtce.NameDescriptionType;

/**
 *
 * @author dovereem
 */

public class SpaceSystemTest {
    
    public SpaceSystemTest() {

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
    public void checkDescriptionsThatExist() {

        XTCESpaceSystem ss = db_.getRootSpaceSystem();

        String shortDesc = ss.getShortDescription();

        Assert.assertTrue( "Short Description should be 'This is a bogus satellite telemetry and telecommand database.'",
                           shortDesc.equals( "This is a bogus satellite telemetry and telecommand database." ) == true );

        String longDesc = ss.getLongDescription();

        Assert.assertTrue( "Long Description should be 'This is a bogus satellite telemetry and telecommand database.  The purpose of BogusSAT is to exercise the capabilities of XTCE and the XTCE View tool.'",
                           longDesc.equals( "This is a bogus satellite telemetry and telecommand database.  The purpose of BogusSAT is to exercise the capabilities of XTCE and the XTCE View tool." ) == true );

        String opStatus = ss.getOperationalStatus();

        Assert.assertTrue( "Operational Status should be 'unittest'",
                           opStatus.equals( "unittest" ) == true );

        List<String> warnings = ss.getWarningsFromLastOperation();

        Assert.assertTrue( "There should not be any warnings yet",
                           warnings.isEmpty() == true );

    }

    @Test
    public void changeDescriptions() {

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001" );

        String shortDesc = ss.getShortDescription();

        Assert.assertTrue( "Short Description should be 'This is the first spacecraft in the BogusSAT system'",
                           shortDesc.equals( "This is the first spacecraft in the BogusSAT system" ) == true );

        String newShortDesc = "Blah Blah Blah";
        ss.setShortDescription( newShortDesc );

        Assert.assertTrue( "NEW Short Description should be '" + newShortDesc + "'",
                           ss.getShortDescription().equals( newShortDesc ) == true );

        String longDesc = ss.getLongDescription();

        Assert.assertTrue( "Long Description should be 'This space system contains the telemetry packets that comprise TM parameters from the various subsystems on the satellite.'",
                           longDesc.equals( "This space system contains the telemetry packets that comprise TM parameters from the various subsystems on the satellite." ) == true );

        String newLongDesc = "Foo Foo Foo";
        ss.setLongDescription( newLongDesc );

        Assert.assertTrue( "NEW Long Description should be '" + newLongDesc + "'",
                           ss.getLongDescription().equals( newLongDesc ) == true );

        String opStatus = ss.getOperationalStatus();

        Assert.assertTrue( "Operational Status should be ''",
                           opStatus.isEmpty() == true );

        ss.setOperationalStatus( "new" );

        Assert.assertTrue( "Operational Status should be 'new'",
                           ss.getOperationalStatus().equals( "new" ) == true );

        ss.setOperationalStatus( "" );

        Assert.assertTrue( "Operational Status should be '' after clearing",
                           ss.getOperationalStatus().isEmpty() == true );

        List<String> warnings = ss.getWarningsFromLastOperation();

        Assert.assertTrue( "There should not be any warnings yet",
                           warnings.isEmpty() == true );

    }

    @Test
    public void checkEmptySpaceSystem() {

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/Payload2" );

        String shortDesc = ss.getShortDescription();

        Assert.assertTrue( "Short Description should be ''",
                           shortDesc.isEmpty() == true );

        String longDesc = ss.getLongDescription();

        Assert.assertTrue( "Long Description should be ''",
                           longDesc.isEmpty() == true );

        String opStatus = ss.getOperationalStatus();

        Assert.assertTrue( "Operational Status should be ''",
                           opStatus.isEmpty() == true );

    }

    @Test
    public void checkInvalidSpaceSystem() {

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/PayloadX" );

        if ( ss != null ) {
            Assert.fail( "Should have gotten a null for SpaceSystem /BogusSAT/SC001/PayloadX" );
        }

    }

    @Test
    public void checkEmptySpaceSystemForParameterChecks() {

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/Payload2" );

        boolean isparam;

        isparam = ss.isTelemetryParameter( "foobar" );

        Assert.assertTrue( "Parameter 'foobar' should not be a TM parameter (before hash)",
                           isparam == false );

        isparam = ss.isTelecommandParameter( "foobar" );

        Assert.assertTrue( "Parameter 'foobar' should not be a TC parameter (before hash)",
                           isparam == false );

        // repeat it once the hash is built

        isparam = ss.isTelemetryParameter( "foobar" );

        Assert.assertTrue( "Parameter 'foobar' should not be a TM parameter (after hash)",
                           isparam == false );

        isparam = ss.isTelecommandParameter( "foobar" );

        Assert.assertTrue( "Parameter 'foobar' should not be a TC parameter (after hash)",
                           isparam == false );

    }

    @Test
    public void checkEmptySpaceSystemForParameterGetters() {

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/Payload2" );

        XTCEParameter parameter;

        try {
            parameter = ss.getTelemetryParameter( "foobar" );
            Assert.fail( "Parameter 'foobar' should not be a TM parameter (before hash)" );
        } catch ( Exception ex ) {
            System.out.println( "Expected Exception: " + ex.getLocalizedMessage() );
        }

        try {
            parameter = ss.getTelecommandParameter( "foobar" );
            Assert.fail( "Parameter 'foobar' should not be a TC parameter (before hash)" );
        } catch ( Exception ex ) {
            System.out.println( "Expected Exception: " + ex.getLocalizedMessage() );
        }

        // repeat it once the hash is built

        try {
            parameter = ss.getTelemetryParameter( "foobar" );
            Assert.fail( "Parameter 'foobar' should not be a TM parameter (after hash)" );
        } catch ( Exception ex ) {
            System.out.println( "Expected Exception: " + ex.getLocalizedMessage() );
        }

        try {
            parameter = ss.getTelecommandParameter( "foobar" );
            Assert.fail( "Parameter 'foobar' should not be a TC parameter (after hash)" );
        } catch ( Exception ex ) {
            System.out.println( "Expected Exception: " + ex.getLocalizedMessage() );
        }

    }

    @Test
    public void checkEmptySpaceSystemForContainers() {

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/Payload2" );

        try {
            XTCETMContainer container = ss.getContainer( "foorbar" );
            Assert.fail( "Should have gotten an exception for no container named 'foobar'" );
        } catch ( Exception ex ) {
            System.out.println( "Expected Exception: " + ex.getLocalizedMessage() );
        }

        List<XTCETMContainer> containers = ss.getContainers();

        Assert.assertTrue( "Should be 0 containers in SpaceSystem",
                           containers.isEmpty() == true );

    }

    @Test
    public void checkEmptySpaceSystemForTelecommands() {

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/Payload2" );

        try {
            XTCETelecommand tc = ss.getTelecommand( "foorbar" );
            Assert.fail( "Should have gotten an exception for no telecommand named 'foobar'" );
        } catch ( Exception ex ) {
            System.out.println( "Expected Exception: " + ex.getLocalizedMessage() );
        }


        List<XTCETelecommand> tclist = ss.getTelecommands();

        Assert.assertTrue( "Should be 0 telecommands in SpaceSystem",
                           tclist.isEmpty() == true );

    }

    @Test
    public void checkEmptySpaceSystemForReferences() {

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/Payload2" );

        NameDescriptionType type;

        type = ss.getTMParameterTypeReference( "foobar" );
        Assert.assertTrue( "TM Parameter Type for 'foobar' should be null",
                           type == null );

        type = ss.getTCParameterTypeReference( "foobar" );
        Assert.assertTrue( "TC Parameter Type for 'foobar' should be null",
                           type == null );

        type = ss.getArgumentTypeReference( "foobar" );
        Assert.assertTrue( "Argument Type for 'foobar' should be null",
                           type == null );

    }

    @Test
    public void checkGetAllParameters() {

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT" );

        List<XTCEParameter> parameters = ss.getParameters();

        long expected = 29;

        if ( parameters.size() != expected ) {
            Assert.fail( "Expected " +
                         Long.toString( expected ) +
                         " parameters but got " +
                         Long.toString( parameters.size() ) +
                         " instead" );
        }

    }

    @Test
    public void checkGetTypeReferences() {

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" );

        NameDescriptionType typeRef;

        typeRef = ss.getTMParameterTypeReference( "Battery_Current_Type" );
        if ( typeRef == null ) {
            Assert.fail( "Should have found type named 'Battery_Current_Type'" );
        }

        typeRef = ss.getTMParameterTypeReference( "FOOBAR_Type" );
        if ( typeRef != null ) {
            Assert.fail( "Should not have found type named 'FOOBAR_Type'" );
        }

        ss = db_.getSpaceSystem( "/BogusSAT" );

        typeRef = ss.getTCParameterTypeReference( "TC_CHECKSUMType" );
        if ( typeRef == null ) {
            Assert.fail( "Should have found type named 'TC_CHECKSUMType'" );
        }

        typeRef = ss.getTCParameterTypeReference( "FOOBAR_Type" );
        if ( typeRef != null ) {
            Assert.fail( "Should not have found type named 'FOOBAR_Type'" );
        }

        ss = db_.getSpaceSystem( "/BogusSAT/SC001" );

        typeRef = ss.getTMParameterTypeReference( "FOOBAR_Type" );
        if ( typeRef != null ) {
            Assert.fail( "Should not have found type named 'FOOBAR_Type' in SpaceSystem with no types" );
        }

        typeRef = ss.getTCParameterTypeReference( "FOOBAR_Type" );
        if ( typeRef != null ) {
            Assert.fail( "Should not have found type named 'FOOBAR_Type' in SpaceSystem with no types" );
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
