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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.bind.helpers.ValidationEventLocatorImpl;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.space.xtce.toolkit.XTCEFunctions;
import org.omg.space.xtce.toolkit.XTCESchemaErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author dovereem
 */
public class SchemaErrorHandlerTest {
    
    public SchemaErrorHandlerTest() {
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

    /**
     * Test of error method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testError() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        SAXParseException ex = new SAXParseException( "error message",
                                                      "publicid",
                                                      "systemid",
                                                      2,
                                                      4 );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        instance.error( ex );

        Assert.assertTrue( "expected error count 1",
                            instance.getErrorCount() == 1 );

        Assert.assertTrue( "expected warning count 0",
                            instance.getWarningCount() == 0 );

        try {

            ex = null;
            instance.error( ex );

            Assert.assertTrue( "expected error count 2",
                               instance.getErrorCount() == 2 );

        } catch ( NullPointerException nullex ) {
            Assert.fail( "Did not catch null pointer exception" );
        }

        
    }

    /**
     * Test of warning method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testWarning() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        SAXParseException ex = new SAXParseException( "warning message",
                                                      "publicid",
                                                      "systemid",
                                                      2,
                                                      4 );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        instance.warning( ex );

        Assert.assertTrue( "expected error count 0",
                            instance.getErrorCount() == 0 );

        Assert.assertTrue( "expected warning count 1",
                            instance.getWarningCount() == 1 );

        try {

            ex = null;
            instance.warning( ex );

            Assert.assertTrue( "expected warning count 2",
                               instance.getWarningCount() == 2 );

        } catch ( NullPointerException nullex ) {
            Assert.fail( "Did not catch null pointer exception" );
        }

    }

    /**
     * Test of warning method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testSkippedWarning() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String skipMsg =
            XTCEFunctions.getText( "xml_fallback_ignore" ); // NOI18N

        SAXParseException ex = new SAXParseException( skipMsg + ": warning message",
                                                      "publicid",
                                                      "systemid",
                                                      2,
                                                      4 );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        instance.warning( ex );

        Assert.assertTrue( "expected error count 0",
                            instance.getErrorCount() == 0 );

        Assert.assertTrue( "expected warning count 0",
                            instance.getWarningCount() == 0 );

        try {

            ex = null;
            instance.warning( ex );

            Assert.assertTrue( "expected warning count 1",
                               instance.getWarningCount() == 1 );

        } catch ( NullPointerException nullex ) {
            Assert.fail( "Did not catch null pointer exception" );
        }

    }

    /**
     * Test of fatalError method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testFatalError_SAXParseException() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        SAXParseException ex = new SAXParseException( "fatal message",
                                                      "publicid",
                                                      "systemid",
                                                      2,
                                                      4 );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        instance.fatalError( ex );

        Assert.assertTrue( "expected error count 1",
                            instance.getErrorCount() == 1 );

        Assert.assertTrue( "expected warning count 0",
                            instance.getWarningCount() == 0 );

        try {

            ex = null;
            instance.fatalError( ex );

            Assert.assertTrue( "expected error count 2",
                               instance.getErrorCount() == 2 );

        } catch ( NullPointerException nullex ) {
            Assert.fail( "Did not catch null pointer exception" );
        }

    }

    /**
     * Test of fatalError method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testFatalError_SAXException() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        SAXException ex = new SAXException( "fatal message" );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        instance.fatalError( ex );

        Assert.assertTrue( "expected error count 1",
                            instance.getErrorCount() == 1 );

        Assert.assertTrue( "expected warning count 0",
                            instance.getWarningCount() == 0 );

        try {

            ex = null;
            instance.fatalError( ex );

            Assert.assertTrue( "expected error count 2",
                               instance.getErrorCount() == 2 );

        } catch ( NullPointerException nullex ) {
            Assert.fail( "Did not catch null pointer exception" );
        }

    }

    /**
     * Test of fatalError method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testFatalError_IOException() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        IOException ex = new IOException( "fatal message" );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        instance.fatalError( ex );

        Assert.assertTrue( "expected error count 1",
                            instance.getErrorCount() == 1 );

        Assert.assertTrue( "expected warning count 0",
                            instance.getWarningCount() == 0 );

        try {

            ex = null;
            instance.fatalError( ex );

            Assert.assertTrue( "expected error count 2",
                               instance.getErrorCount() == 2 );

        } catch ( NullPointerException nullex ) {
            Assert.fail( "Did not catch null pointer exception" );
        }

    }

    /**
     * Test of handleEvent method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testHandleEventWarning() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        ValidationEventLocator locator = new ValidationEventLocatorImpl();

        ValidationEvent event = new ValidationEventImpl( ValidationEvent.WARNING,
                                                         "warning message",
                                                         locator );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        boolean expResult = true;
        boolean result = instance.handleEvent( event );
        assertEquals( expResult, result );

        Assert.assertTrue( "expected error count 0",
                            instance.getErrorCount() == 0 );

        Assert.assertTrue( "expected warning count 1",
                            instance.getWarningCount() == 1 );

        try {

            assertEquals( false, instance.handleEvent( null ) );

            Assert.assertTrue( "expected error count 1",
                               instance.getErrorCount() == 1 );

        } catch ( NullPointerException nullex ) {
            Assert.fail( "Did not catch null pointer exception" );
        }

    }

    /**
     * Test of handleEvent method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testSkippedHandleEventWarning() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        final String skipMsg =
            XTCEFunctions.getText( "xml_fallback_ignore" ); // NOI18N

        ValidationEventLocator locator = new ValidationEventLocatorImpl();

        ValidationEvent event = new ValidationEventImpl( ValidationEvent.WARNING,
                                                         skipMsg + ": warning message",
                                                         locator );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        boolean expResult = true;
        boolean result = instance.handleEvent( event );
        assertEquals( expResult, result );

        Assert.assertTrue( "expected error count 0",
                            instance.getErrorCount() == 0 );

        Assert.assertTrue( "expected warning count 0",
                            instance.getWarningCount() == 0 );

        try {

            assertEquals( false, instance.handleEvent( null ) );

            Assert.assertTrue( "expected error count 1",
                               instance.getErrorCount() == 1 );

        } catch ( NullPointerException nullex ) {
            Assert.fail( "Did not catch null pointer exception" );
        }

    }

    /**
     * Test of handleEvent method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testHandleEventError() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        ValidationEventLocator locator = new ValidationEventLocatorImpl();

        ValidationEvent event = new ValidationEventImpl( ValidationEvent.ERROR,
                                                         "error message",
                                                         locator );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        boolean expResult = true;
        boolean result = instance.handleEvent( event );
        assertEquals( expResult, result );

        Assert.assertTrue( "expected error count 1",
                            instance.getErrorCount() == 1 );

        Assert.assertTrue( "expected warning count 0",
                            instance.getWarningCount() == 0 );

        try {

            assertEquals( false, instance.handleEvent( null ) );

            Assert.assertTrue( "expected error count 2",
                               instance.getErrorCount() == 2 );

        } catch ( NullPointerException nullex ) {
            Assert.fail( "Did not catch null pointer exception" );
        }

    }

    /**
     * Test of handleEvent method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testHandleEventFatal() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        ValidationEventLocator locator = new ValidationEventLocatorImpl();

        ValidationEvent event = new ValidationEventImpl( ValidationEvent.FATAL_ERROR,
                                                         "fatal message",
                                                         locator );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        boolean expResult = true;
        boolean result = instance.handleEvent( event );
        assertEquals( expResult, result );

        Assert.assertTrue( "expected error count 1",
                            instance.getErrorCount() == 1 );

        Assert.assertTrue( "expected warning count 0",
                            instance.getWarningCount() == 0 );

        try {

            assertEquals( false, instance.handleEvent( null ) );

            Assert.assertTrue( "expected error count 2",
                               instance.getErrorCount() == 2 );

        } catch ( NullPointerException nullex ) {
            Assert.fail( "Did not catch null pointer exception" );
        }

    }

    /**
     * Test of getMessages method, of class XTCESchemaErrorHandler.
     */
    @Test
    public void testGetMessages() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        List<String> expResult = new ArrayList<>();
        List<String> result = instance.getMessages();
        assertEquals(expResult.size(), result.size());

    }

    /**
     * Test of getWarningCount method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testGetWarningCount() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        long expResult = 0L;
        long result = instance.getWarningCount();
        assertEquals(expResult, result);

    }

    /**
     * Test of getErrorCount method, of class XTCESchemaErrorHandler.
     */

    @Test
    public void testGetErrorCount() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCESchemaErrorHandler instance = new XTCESchemaErrorHandler();
        long expResult = 0L;
        long result = instance.getErrorCount();
        assertEquals(expResult, result);

    }
    
}
