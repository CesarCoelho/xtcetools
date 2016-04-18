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

        if ( metrics.getNumberOfParameters() != 42 ) {
            Assert.fail( "Should have found 42 total parameters" );
        }
        if ( metrics.getNumberOfTelemetryParameters() != 41 ) {
            Assert.fail( "Should have found 41 total telemetry parameters" );
        }
        if ( metrics.getNumberOfTelecommandParameters() != 1 ) {
            Assert.fail( "Should have found 1 total telecommand parameters" );
        }
        if ( metrics.getNumberOfParameterTypes() != 40 ) {
            Assert.fail( "Should have found 40 total parameter types" );
        }
        if ( metrics.getNumberOfTelemetryParameterTypes() != 39 ) {
            Assert.fail( "Should have found 39 total telemetry parameter types" );
        }
        if ( metrics.getNumberOfTelecommandParameterTypes() != 1 ) {
            Assert.fail( "Should have found 1 total telecommand parameter types" );
        }

        if ( metrics.getNumberOfContainers() != 5 ) {
            Assert.fail( "Should have found 5 total containers" );
        }
        if ( metrics.getNumberOfTelemetryContainers() != 5 ) {
            Assert.fail( "Should have found 5 total telemetry containers" );
        }
        if ( metrics.getNumberOfTelecommandContainers() != 0 ) {
            Assert.fail( "Should have found 0 total telecommand containers" );
        }

        if ( metrics.getNumberOfChildSpaceSystems() != 4 ) {
            Assert.fail( "Should have found 4 child SpaceSystems" );
        }

        if ( metrics.getNumberOfTelecommands() != 2 ) {
            Assert.fail( "Should have found 2 total telecommands" );
        }
        if ( metrics.getNumberOfTelecommandArguments() != 0 ) {
            Assert.fail( "Should have found 0 total telecommand arguments" );
        }
        if ( metrics.getNumberOfTelecommandArgumentTypes() != 0 ) {
            Assert.fail( "Should have found 0 total telecommand argument types" );
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

    private XTCEDatabase  db_  = null;

}
