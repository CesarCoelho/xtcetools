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
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author dovereem
 */
public class FileTest {
    
    public FileTest() {
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
    public void testFileLocation() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        File fileObj = new File( "src/overview.html" );

        String absPath = fileObj.getAbsolutePath();

        if ( ( absPath.startsWith( "/" ) == false ) &&
             ( absPath.charAt( 2 )       != '\\'   ) ) {
            Assert.fail( "Expected absolute path to overview.html, got " +
                         absPath );
        }
        System.out.println( "File Path: " + absPath );

        String fileName = fileObj.getName();

        if ( fileName.equals( "overview.html") == false ) {
            Assert.fail( "Expected name of overview.html, got " +
                         fileName );
        }
        System.out.println( "File Name: " + fileName );

    }

    @Test
    public void testUriLocation() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        File fileObj = new File( "src/overview.html" );

        URI fileUri = fileObj.toURI();

        String absPath = fileUri.getPath();

        if ( absPath.startsWith( "/" ) == false ) {
            Assert.fail( "Expected absolute path to overview.html, got " +
                         absPath );
        }
        System.out.println( "File Path: " + absPath );

        String fileName = fileUri.getPath();
        int    idx      = fileName.lastIndexOf( '/' );
        if ( idx != -1 ) {
            fileName = fileName.substring( idx + 1 );
        }

        if ( fileName.equals( "overview.html") == false ) {
            Assert.fail( "Expected name of overview.html, got " +
                         fileName );
        }
        System.out.println( "File Name: " + fileName );

    }

    @Test
    public void testFileInJar() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        URL fileUrl =
            ClassLoader.getSystemResource( "junit/runner/Version.class" );

        String absPath = fileUrl.getPath();
        int    idx     = absPath.lastIndexOf( '!' );
        if ( idx != -1 ) {
            absPath = absPath.substring( idx + 1 );
        }

        if ( absPath.startsWith( "/" ) == false ) {
            Assert.fail( "Expected absolute path to Version.class, got " +
                         absPath );
        }
        System.out.println( "File Path From URL: " + absPath );

        try {
            System.out.println( "URI path from URL: " + fileUrl.toURI().toString() );
        } catch ( URISyntaxException ex ) {
            System.out.println( "Cannot convert JAR URL to a URI: " + ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testDirectoryInJar() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {
        Enumeration<URL> fileUrls =
            ClassLoader.getSystemResources( "junit/runner/*" );

        while ( fileUrls.hasMoreElements() == true ) {

            URL    fileUrl = fileUrls.nextElement();
            String absPath = fileUrl.getPath();
            int    idx     = absPath.lastIndexOf( '!' );
            if ( idx != -1 ) {
                absPath = absPath.substring( idx + 1 );
            }

            if ( absPath.startsWith( "/" ) == false ) {
                Assert.fail( "Expected absolute path to Version.class, got " +
                             absPath );
            }
            System.out.println( "File Path From URL: " + absPath );

        }

        } catch ( IOException ex ) {
            Assert.fail( "Exception: " + ex.getLocalizedMessage() );
        }
    }

}
