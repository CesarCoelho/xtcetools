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

public class TelecommandTest {
    
    public TelecommandTest() {

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
    public void testDbTelecommandRetrieval() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            List<XTCETelecommand> telecommands = db_.getTelecommands();

            // this could will definitely be changing

            int expect = 11;
            int count  = telecommands.size();

            Assert.assertTrue( "Should have located " +
                               Integer.toString( expect ) +
                               " telecommands, but got " +
                               Integer.toString( count ),
                               expect == count );

            XTCETelecommand telecommand =
                db_.getTelecommand( "/BogusSAT/SC001/Payload1/Adjust_Payload_1_Config" );

            Assert.assertEquals( "Adjust_Payload_1_Config",
                                 telecommand.getName() );

            try {
                telecommand = db_.getTelecommand( "DoesNotExist" );
                Assert.fail( "Should have thrown exception on telecommand 'DoesNotExist'" );
            } catch ( Exception ex ) {
                // this is expected
            }

            telecommands = db_.getTelecommands( "Reaction_Wheel*" );

            Assert.assertTrue( "Should have located 5 'Reaction_Wheel*' telecommands",
                               telecommands.size() == 5 );

            telecommands = db_.getTelecommands( "foo*" );

            Assert.assertTrue( "Should have located 0 'foo*' telecommands",
                               telecommands.size() == 0 );

        } catch ( Exception ex ) {
            Assert.fail( "Unexpected exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testSpaceSystemTelecommandRetrieval() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" );

            List<XTCETelecommand> telecommands = ss.getTelecommands();

            Assert.assertTrue( "Should have located 6 telecommands",
                               telecommands.size() == 6 );

            XTCETelecommand telecommand = ss.getTelecommand( "Set_Mission_Phase" );

            Assert.assertEquals( "Set_Mission_Phase",
                                 telecommand.getName() );

            try {
                telecommand = ss.getTelecommand( "DoesNotExist" );
                Assert.fail( "Should have thrown exception on telecommand 'DoesNotExist'" );
            } catch ( Exception ex ) {
                // this is expected
            }

            telecommands = ss.getTelecommands( "Reaction_Wheel*" );

            Assert.assertTrue( "Should have located 5 'Reaction_Wheel*' telecommands",
                               telecommands.size() == 5 );

            telecommands = ss.getTelecommands( "foo*" );

            Assert.assertTrue( "Should have located 0 'foo*' telecommands",
                               telecommands.size() == 0 );

        } catch ( Exception ex ) {
            Assert.fail( "Unexpected exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testTelecommandAttributes() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCETelecommand tc = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" )
                                    .getTelecommand( "Reaction_Wheel_1_On" );

            Assert.assertEquals( "/CCSDSTelecommand/CCSDSDirectTelecommand/Reaction_Wheel_Control/Reaction_Wheel_1_On",
                                 tc.getInheritancePath() );

            Assert.assertTrue( "Reaction_Wheel_1_On should be a MetaCommand",
                               tc.isMetaCommand() );

            Assert.assertFalse( "Reaction_Wheel_1_On should NOT be a BlockMetaCommand",
                                tc.isBlockMetaCommand() );

            Assert.assertTrue( "Reaction_Wheel_1_On should have a MetaCommand Reference",
                               tc.getMetaCommandReference() != null );

            Assert.assertTrue( "Reaction_Wheel_1_On should NOT have a BlockMetaCommand Reference",
                               tc.getBlockMetaCommandReference() == null );

            Assert.assertTrue( "Reaction_Wheel_1_On Command Container is named Reaction_Wheel_1_On_Container",
                               tc.getCommandContainer().getName().equals( "Reaction_Wheel_1_On_Container" ) );

            Assert.assertFalse( "Reaction_Wheel_1_On should NOT be abstract",
                                tc.isAbstract() );

            Assert.assertEquals( "Turn power on for reaction wheel 1",
                                 tc.getShortDescription() );

            Assert.assertEquals( "This hardware telecommand is a derived form of 'Reaction_Wheel_Control' that turns ON reaction wheel 1",
                                 tc.getLongDescription() );

            Assert.assertEquals( "Turn power on for reaction wheel 1",
                                 tc.getDescription() );

        } catch ( Exception ex ) {
            Assert.fail( "Unable to retrieve TC 'Reaction_Wheel_1_On' from BusElectronics: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testXmlOutput() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/Payload1" );

        try {

            XTCETelecommand tc  = ss.getTelecommand( "Payload_1_Control" );

            String tcXml = tc.toXml();

            System.out.println( tcXml );

        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Got exception for telecommand XML generation on " +
                         "'Payload_1_Control' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

    }

    private void loadDocument() throws XTCEDatabaseException {

        System.out.println( "Loading the BoguSAT-2.xml demo database" );

        String file = "src/org/xtce/toolkit/database/examples/BogusSAT-2.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;

}
