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

public class ArgumentTest {
    
    public ArgumentTest() {

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
    public void testBasicArgumentRetrieval() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCETelecommand tc = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" )
                                    .getTelecommand( "Reaction_Wheel_Control" );

            List<XTCEArgument> args = tc.getArguments();

            Assert.assertTrue( "Should have located 2 arguments on TC 'Reaction_Wheel_Control'",
                               args.size() == 2 );

            Assert.assertEquals( "RW_UNIT_ID", args.get( 0 ).getName() );
            Assert.assertEquals( "RW_PWR_STATE", args.get( 1 ).getName() );

        } catch ( Exception ex ) {
            Assert.fail( "Unable to retrieve TC 'Reaction_Wheel_Control' from BusElectronics: " +
                         ex.getLocalizedMessage() );
        }

        try {

            XTCETelecommand tc = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" )
                                    .getTelecommand( "Set_Mission_Phase" );

            List<XTCEArgument> args = tc.getArguments();

            Assert.assertTrue( "Should have located 1 arguments on TC 'Set_Mission_Phase'",
                               args.size() == 1 );

            Assert.assertEquals( "PHASE", args.get( 0 ).getName() );

        } catch ( Exception ex ) {
            Assert.fail( "Unable to retrieve TC 'Set_Mission_Phase' from BusElectronics: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testBasicArgumentRetrievalFailure() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCETelecommand tc = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" )
                                    .getTelecommand( "Reaction_Wheel_Control" );

            XTCEArgument arg;

            arg = tc.getArgument( "RW_PWR_STATE" );

            Assert.assertTrue( "Should have located 'RW_PWR_STATE' in Reaction_Wheel_Control",
                               arg.getName().equals( "RW_PWR_STATE" ) );

            try {
                arg = tc.getArgument( "ARG_NOT_THERE" );
                Assert.fail( "Should not have located 'ARG_NOT_THERE' in Reaction_Wheel_Control" );
            } catch ( Exception ex ) {
                // expected to throw when looking for ARG_NOT_THERE
            }

        } catch ( Exception ex ) {
            Assert.fail( "Unable to retrieve TC 'Reaction_Wheel_Control' from BusElectronics: " +
                         ex.getLocalizedMessage() );
        }

        try {

            XTCETelecommand tc = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" )
                                    .getTelecommand( "Set_Mission_Phase" );

            XTCEArgument arg;

            arg = tc.getArgument( "PHASE" );

            Assert.assertTrue( "Should have located 'PHASE' in Set_Mission_Phase",
                               arg.getName().equals( "PHASE" ) );

            try {
                arg = tc.getArgument( "ARG_NOT_THERE" );
                Assert.fail( "Should not have located 'ARG_NOT_THERE' in Set_Mission_Phase" );
            } catch ( Exception ex ) {
                // expected to throw when looking for ARG_NOT_THERE
            }

        } catch ( Exception ex ) {
            Assert.fail( "Unable to retrieve TC 'Set_Mission_Phase' from BusElectronics: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testArgumentAttributes() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCETelecommand tc = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" )
                                    .getTelecommand( "Reaction_Wheel_1_On" );

            List<XTCEArgument> args = tc.getArguments();

            Assert.assertTrue( "Should have located 2 arguments on TC 'Reaction_Wheel_Control'",
                               args.size() == 2 );

            XTCEArgument arg = args.get( 0 );

            Assert.assertEquals( "/BogusSAT/SC001/BusElectronics/RW_UNIT_ID_Type",
                                 arg.getTypeReferenceFullPath() );

            Assert.assertEquals( "ID number of the reaction wheel",
                                 arg.getShortDescription() );

            Assert.assertEquals( "",
                                 arg.getLongDescription() );

            Assert.assertEquals( "ID number of the reaction wheel",
                                 arg.getDescription() );

            Assert.assertEquals( "",
                                 arg.getInitialValue() );

            Assert.assertEquals( false, arg.isMember() );


            Assert.assertEquals( true, arg.isArgument() );

        } catch ( Exception ex ) {
            Assert.fail( "Unable to retrieve TC 'Reaction_Wheel_1_On' from BusElectronics: " +
                         ex.getLocalizedMessage() );
        }

        try {

            XTCETelecommand tc = db_.getSpaceSystem( "/BogusSAT/SC001/Payload1" )
                                    .getTelecommand( "Payload_1_Control" );

            List<XTCEArgument> args = tc.getArguments();

            Assert.assertTrue( "Should have located 2 arguments on TC 'Payload_1_Control'",
                               args.size() == 2 );

            XTCEArgument arg = args.get( 1 );

            Assert.assertEquals( "/BogusSAT/SC001/Payload1/Output_Power_Type",
                                 arg.getTypeReferenceFullPath() );

            Assert.assertEquals( "Payload output power",
                                 arg.getShortDescription() );

            Assert.assertEquals( "Long Description of the output power argument",
                                 arg.getLongDescription() );

            Assert.assertEquals( "Long Description of the output power argument",
                                 arg.getDescription() );

            Assert.assertEquals( "",
                                 arg.getInitialValue() );

            Assert.assertEquals( false, arg.isMember() );

            Assert.assertEquals( true, arg.isArgument() );

        } catch ( Exception ex ) {
            Assert.fail( "Unable to retrieve TC 'Payload_1_Control' from Payload1: " +
                         ex.getLocalizedMessage() );
        }

        try {

            XTCETelecommand tc = db_.getSpaceSystem( "/BogusSAT/SC001/Payload1" )
                                    .getTelecommand( "Adjust_Payload_1_Config" );

            List<XTCEArgument> args = tc.getArguments();

            Assert.assertTrue( "Should have located 3 arguments on TC 'Adjust_Payload_1_Config'",
                               args.size() == 3 );

            XTCEArgument arg = args.get( 2 );

            Assert.assertEquals( "/BogusSAT/SC001/Payload1/Value32_Type",
                                 arg.getTypeReferenceFullPath() );

            Assert.assertEquals( "New value to set to 32 bit integer item",
                                 arg.getShortDescription() );

            Assert.assertEquals( "",
                                 arg.getLongDescription() );

            Assert.assertEquals( "New value to set to 32 bit integer item",
                                 arg.getDescription() );

            Assert.assertEquals( "",
                                 arg.getInitialValue() );

            Assert.assertEquals( true, arg.isMember() );

            Assert.assertEquals( false, arg.isArgument() );

        } catch ( Exception ex ) {
            Assert.fail( "Unable to retrieve TC 'Adjust_Payload_1_Config' from Payload1: " +
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
            XTCEArgument    arg = tc.getArgument( "STATE" );

            String argumentXml     = arg.toXml();
            String argumentTypeXml = arg.typeToXml();

            System.out.println( argumentXml );
            System.out.println( argumentTypeXml );

        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Got exception for argument XML generation on " +
                         "'STATE' of 'Payload_1_Control' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

        try {

            XTCETelecommand tc  = ss.getTelecommand( "Adjust_Payload_1_Config" );
            XTCEArgument    arg = tc.getArgument( "CFGITEM.ADDRESS" );

            String argumentXml     = arg.toXml();
            String argumentTypeXml = arg.typeToXml();

            System.out.println( argumentXml );
            System.out.println( argumentTypeXml );

        } catch ( XTCEDatabaseException ex ) {
            Assert.fail( "Got exception for argument XML generation on " +
                         "'CFGITEM.ADDRESS' of 'Adjust_Payload_1_Config' with '" +
                         ex.getLocalizedMessage() +
                         "'" );
        }

    }

    private void loadDocument() throws XTCEDatabaseException {

        System.out.println( "Loading the BoguSAT-2.xml demo database" );

        String file = "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;

}
