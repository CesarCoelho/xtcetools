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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/** This test verifies that the example code runs and does something, but it
 * requires that the distribution JAR file has been built before running.
 *
 * @author dovereem
 */

public class ExampleCodeTest {
    
    public ExampleCodeTest() {
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
    public void testDumpParameterList() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String cwd = System.getProperty( "user.dir" );
        System.out.println( "CWD: " + cwd );

        try {

            ProcessBuilder proc = new ProcessBuilder( 
                "java",
                "-classpath",
                "dist/XTCETools.jar",
                "org.omg.space.xtce.examples.DumpParameterListExample",
                "src/org/omg/space/xtce/database/BogusSAT-2.xml" );

            run( proc );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testProcessContainerExample() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String cwd = System.getProperty( "user.dir" );
        System.out.println( "CWD: " + cwd );

        try {

            ProcessBuilder proc = new ProcessBuilder( 
                "java",
                "-classpath",
                "dist/XTCETools.jar",
                "org.omg.space.xtce.examples.ProcessContainerExample",
                "src/org/omg/space/xtce/database/BogusSAT-2.xml" );

            run( proc );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    private void run( ProcessBuilder proc ) throws IOException, InterruptedException {

        proc.redirectErrorStream( true );

        Process procHandle = proc.start();

        BufferedReader buf =
            new BufferedReader( new InputStreamReader( procHandle.getInputStream() ) );

        String line;
        while ( ( line = buf.readLine() ) != null ) {
            System.out.println( line );
        }

        int procResult = procHandle.waitFor();

        Assert.assertTrue( "Process should return 0 but value is " +
                           Integer.toString( procResult ),
                           procResult == 0 );

    }

}
