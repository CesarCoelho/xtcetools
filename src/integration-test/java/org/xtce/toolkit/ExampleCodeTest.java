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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xtce.toolkit.examples.ContainerDefaultValueDump;
import org.xtce.toolkit.examples.DecodeContainerExample;
import org.xtce.toolkit.examples.DumpParameterListExample;
import org.xtce.toolkit.examples.EncodeContainerExample;
import org.xtce.toolkit.examples.ParameterDefaultValueDump;
import org.xtce.toolkit.examples.ProcessContainerExample;

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
    public void testDumpParameterListExampleExecute() {

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
                "org.xtce.toolkit.examples.DumpParameterListExample",
                "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml" );

            run( proc );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testDumpParameterListExampleInstance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String[] args = { "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml" };

            DumpParameterListExample.main( args );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testProcessContainerExampleExecute() {

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
                "org.xtce.toolkit.examples.ProcessContainerExample",
                "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml" );

            run( proc );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testProcessContainerExampleInstance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String[] args = { "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml" };

            ProcessContainerExample.main( args );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testDecodeContainerExampleExecute() {

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
                "org.xtce.toolkit.examples.DecodeContainerExample",
                "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml",
                "/BogusSAT/SC001/ECSS_Service_1_Subservice_1",
                "src/test/resources/org/xtce/toolkit/test/Container-ECSS_Service_1_Subservice_1.bin" );

            run( proc );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testDecodeContainerExampleInstance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String[] args = { "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml",
                              "/BogusSAT/SC001/ECSS_Service_1_Subservice_1",
                              "src/test/resources/org/xtce/toolkit/test/Container-ECSS_Service_1_Subservice_1.bin" };

            DecodeContainerExample.main( args );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testEncodeContainerExampleExecute() {

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
                "org.xtce.toolkit.examples.EncodeContainerExample" );

            run( proc );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testEncodeContainerExampleInstance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String[] args = { };

            EncodeContainerExample.main( args );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterDefaultValueDumpWithNameExecute() {

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
                "org.xtce.toolkit.examples.ParameterDefaultValueDump",
                "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml",
                "Config*" );

            run( proc );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterDefaultValueDumpWithNameInstance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String[] args = { "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml",
                              "Config*" };

            ParameterDefaultValueDump.main( args );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterDefaultValueDumpWithAliasExecute() {

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
                "org.xtce.toolkit.examples.ParameterDefaultValueDump",
                "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml",
                "cfg_*",
                "--aliasns=FSW" );

            run( proc );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testParameterDefaultValueDumpWithAliasInstance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String[] args = { "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml",
                              "cfg_*",
                              "--aliasns=FSW" };

            ParameterDefaultValueDump.main( args );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testContainerDefaultValueDumpWithNameExecute() {

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
                "org.xtce.toolkit.examples.ContainerDefaultValueDump",
                "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml",
                "Processor_1_Config_Table" );

            run( proc );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testContainerDefaultValueDumpWithNameInstance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String[] args = { "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml",
                              "Processor_1_Config_Table" };

            ContainerDefaultValueDump.main( args );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testContainerDefaultValueDumpWithAliasExecute() {

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
                "org.xtce.toolkit.examples.ContainerDefaultValueDump",
                "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml",
                "Processor_2_Config_Table",
                "--aliasns=FSW" );

            run( proc );

        } catch ( Exception ex ) {
            Assert.fail( "Should not have gotten an exception: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testContainerDefaultValueDumpWithAliasInstance() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            String[] args = { "src/main/resources/org/xtce/toolkit/database/examples/BogusSAT-2.xml",
                              "Processor_2_Config_Table",
                              "--aliasns=FSW" };

            ContainerDefaultValueDump.main( args );

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
