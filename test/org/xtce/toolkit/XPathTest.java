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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.NodeList;

/**
 *
 * @author dovereem
 */

public class XPathTest {
    
    public XPathTest() {
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
    public void verifyNamespaceContext() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        XTCENamespaceContext nsc = new XTCENamespaceContext();

        Assert.assertTrue( "getNamespaceURI for prefix 'xctce' should be " +
                           XTCEConstants.XTCE_NAMESPACE,
                           nsc.getNamespaceURI( "xtce" ).equals( XTCEConstants.XTCE_NAMESPACE ) );

        Assert.assertTrue( "getNamespaceURI for prefix 'xxx' should be null",
                           nsc.getNamespaceURI( "xxx" ) == null );

        try {
            Iterator it = nsc.getPrefixes( "" );
            Assert.fail( "getPrefixes should have thrown exception" );
        } catch ( IllegalAccessError ex ) {
            System.out.println( "getPrefixes got exception as expected: " +
                                ex.getLocalizedMessage() );
        }

        Assert.assertTrue( "getPrefix for URI " +
                           XTCEConstants.XTCE_NAMESPACE +
                           "should be 'xtce'",
                           nsc.getPrefix( XTCEConstants.XTCE_NAMESPACE ).equals( "xtce" ) );

        try {
            String prefix = nsc.getPrefix( "http://foo.com/bar" );
            Assert.fail( "getPrefix should have thrown exception" );
        } catch ( IllegalAccessError ex ) {
            System.out.println( "getPrefix got exception as expected: " +
                                ex.getLocalizedMessage() );
        }

    }

    @Test
    public void verifyReadOnlyCannotUseXPath() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {
            loadDocument( true );
            NodeList results = db_.evaluateXPathQuery( "//xtce:testquery" );
            Assert.fail( "Expected exception on a query when read-only load" );
        } catch ( Exception ex ) {
            System.out.println( "Expected exception: " +
                                ex.getLocalizedMessage() );
        }

    }

    @Test
    public void verifyXPathException() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String query = "//xtce:SystemName[";

        try {
            loadDocument( false );
            NodeList results = db_.evaluateXPathQuery( query );
        } catch ( Exception ex ) {
            System.out.println( "Expected Exception: " +
                                ex.getLocalizedMessage() );
            return;
        }

        Assert.fail( "Should have thrown exception for query '" + query + "'" );

    }

    @Test
    public void verifyXPathOnElementName() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String query = "//xtce:SystemName";

        try {
            loadDocument( false );
            NodeList results = db_.evaluateXPathQuery( query );
            Assert.assertTrue( query + " should have results",
                               results.getLength() > 0 );
        } catch ( Exception ex ) {
            Assert.fail( "Query for " + query + " should not have thrown: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void verifyXPathOnElementWithSpecificAttribute() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String query = "//xtce:Parameter[@name = 'Payload_1_State']";

        try {
            loadDocument( false );
            NodeList results = db_.evaluateXPathQuery( query );
            Assert.assertTrue( query + " should have 1 result",
                               results.getLength() == 1 );
        } catch ( Exception ex ) {
            Assert.fail( "Query for " + query + " should not have thrown: " +
                         ex.getLocalizedMessage() );
        }

    }

    @Test
    public void verifyXPathOnElementWithSpecificAttributeAndPrettyPrint() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String query = "//xtce:Parameter[@name = 'Payload_1_State']";

        try {
            loadDocument( false );
            NodeList results = db_.evaluateXPathQuery( query );
            Assert.assertTrue( query + " should have 1 result",
                               results.getLength() == 1 );
            System.out.println( XTCEFunctions.xmlPrettyPrint( results ) );
        } catch ( Exception ex ) {
            Assert.fail( "Query/PrettyPrint for " + query + " should not have thrown: " +
                         ex.getLocalizedMessage() );
        }

    }

    private void loadDocument( boolean readOnly ) throws XTCEDatabaseException {

        System.out.println( "Loading the BogusSAT-2.xml demo database" );

        String file = "src/org/xtce/toolkit/database/examples/BogusSAT-2.xml";

        try {
            URL dbFile = new File( file ).toURI().toURL();
            db_ = new XTCEDatabase( dbFile, false, false, readOnly );
        } catch ( MalformedURLException ex ) {
            throw new XTCEDatabaseException( ex.getLocalizedMessage() );
        }

    }

    // Private Data Members

    private XTCEDatabase db_ = null;

}
