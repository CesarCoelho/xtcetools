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
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author dovereem
 */

public class MetricsTest {
    
    public MetricsTest() {

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
    public void testDatabaseMetrics() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESpaceSystemMetrics metrics = db_.getMetrics();

        if ( metrics.getNumberOfParameters() != 62 ) {
            Assert.fail( "Should have found 62 total parameters" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfParameters() ) );
        }

        if ( metrics.getNumberOfTelemetryParameters() != 61 ) {
            Assert.fail( "Should have found 61 total telemetry parameters" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfTelemetryParameters() ) );
        }

        if ( metrics.getNumberOfTelecommandParameters() != 1 ) {
            Assert.fail( "Should have found 1 total telecommand parameters" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfTelecommandParameters() ) );
        }

        if ( metrics.getNumberOfParameterTypes() != 60 ) {
            Assert.fail( "Should have found 60 total parameter types" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfParameterTypes() ) );
        }

        if ( metrics.getNumberOfTelemetryParameterTypes() != 59 ) {
            Assert.fail( "Should have found 59 total telemetry parameter types" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfTelemetryParameterTypes() ) );
        }

        if ( metrics.getNumberOfTelecommandParameterTypes() != 1 ) {
            Assert.fail( "Should have found 1 total telecommand parameter types" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfTelecommandParameterTypes() ) );
        }

        if ( metrics.getNumberOfContainers() != 5 ) {
            Assert.fail( "Should have found 5 total containers" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfContainers() ) );
        }

        if ( metrics.getNumberOfTelemetryContainers() != 5 ) {
            Assert.fail( "Should have found 5 total telemetry containers" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfTelemetryContainers() ) );
        }

        if ( metrics.getNumberOfTelecommandContainers() != 0 ) {
            Assert.fail( "Should have found 0 total telecommand containers" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfTelecommandContainers() ) );
        }

        if ( metrics.getNumberOfChildSpaceSystems() != 4 ) {
            Assert.fail( "Should have found 4 child SpaceSystems" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfChildSpaceSystems() ) );
        }

        if ( metrics.getNumberOfTelecommands() != 10 ) {
            Assert.fail( "Should have found 10 total telecommands" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfTelecommands() ) );
        }

        if ( metrics.getNumberOfTelecommandArguments() != 6 ) {
            Assert.fail( "Should have found 6 total telecommand arguments" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfTelecommandArguments() ) );
        }

        if ( metrics.getNumberOfTelecommandArgumentTypes() != 8 ) {
            Assert.fail( "Should have found 8 total telecommand argument types" +
                         ", found instead " +
                         Long.toString( metrics.getNumberOfTelecommandArgumentTypes() ) );
        }

        if ( metrics.isDeepCount() == false ) {
            Assert.fail( "Database metrics are supposed to be a deep count" );
        }

    }

    @Test
    public void testSpaceSystemMetrics() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001" );

        XTCESpaceSystemMetrics metrics = ss.getMetrics();

        if ( metrics.getNumberOfParameters() != 0 ) {
            Assert.fail( "Should have found 0 total parameters" );
        }
        if ( metrics.getNumberOfTelemetryParameters() != 0 ) {
            Assert.fail( "Should have found 0 total telemetry parameters" );
        }
        if ( metrics.getNumberOfTelecommandParameters() != 0 ) {
            Assert.fail( "Should have found 0 total telecommand parameters" );
        }
        if ( metrics.getNumberOfParameterTypes() != 0 ) {
            Assert.fail( "Should have found 0 total parameter types" );
        }
        if ( metrics.getNumberOfTelemetryParameterTypes() != 0 ) {
            Assert.fail( "Should have found 0 total telemetry parameter types" );
        }
        if ( metrics.getNumberOfTelecommandParameterTypes() != 0 ) {
            Assert.fail( "Should have found 0 total telecommand parameter types" );
        }

        if ( metrics.getNumberOfContainers() != 0 ) {
            Assert.fail( "Should have found 0 total containers" );
        }
        if ( metrics.getNumberOfTelemetryContainers() != 0 ) {
            Assert.fail( "Should have found 0 total telemetry containers" );
        }
        if ( metrics.getNumberOfTelecommandContainers() != 0 ) {
            Assert.fail( "Should have found 0 total telecommand containers" );
        }

        if ( metrics.getNumberOfChildSpaceSystems() != 3 ) {
            Assert.fail( "Should have found 3 child SpaceSystems" );
        }

        if ( metrics.getNumberOfTelecommands() != 0 ) {
            Assert.fail( "Should have found 0 total telecommands" );
        }
        if ( metrics.getNumberOfTelecommandArguments() != 0 ) {
            Assert.fail( "Should have found 0 total telecommand arguments" );
        }
        if ( metrics.getNumberOfTelecommandArgumentTypes() != 0 ) {
            Assert.fail( "Should have found 0 total telecommand argument types" );
        }

        if ( metrics.isDeepCount() == true ) {
            Assert.fail( "SpaceSystem metrics are not supposed to be a deep count" );
        }

    }

    private void loadDocument() throws XTCEDatabaseException {

        System.out.println( "Loading the BogusSAT-1.xml demo database" );

        String file = "test/org/xtce/toolkit/test/BogusSAT-1.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase db_  = null;

}
