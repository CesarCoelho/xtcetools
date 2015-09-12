/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
                //cwd + "/build/classes/org/omg/space/xtce/examples:" +
                //cwd + "/build/classes/org/omg/space/xtce/toolkit:" +
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

        int procResult = procHandle.waitFor();

        String line;
        while ( ( line = buf.readLine() ) != null ) {
            System.out.println( line );
        }

        Assert.assertTrue( "Process should return 0 but value is " +
                           Integer.toString( procResult ),
                           procResult == 0 );

    }

}
