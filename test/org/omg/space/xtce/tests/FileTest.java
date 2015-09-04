/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omg.space.xtce.tests;

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
import static org.junit.Assert.*;

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

        File fileObj = new File( "src/overview.html" );

        String absPath = fileObj.getAbsolutePath();

        if ( absPath.startsWith( "/" ) == false ) {
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

        URL fileUrl =
            ClassLoader.getSystemResource( "junit/runner/Version.class" );

        String absPath = fileUrl.getPath();
        int    idx     = absPath.lastIndexOf( "!" );
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

        try {
        Enumeration<URL> fileUrls =
            ClassLoader.getSystemResources( "junit/runner/*" );

        while ( fileUrls.hasMoreElements() == true ) {

            URL    fileUrl = fileUrls.nextElement();
            String absPath = fileUrl.getPath();
            int    idx     = absPath.lastIndexOf( "!" );
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
