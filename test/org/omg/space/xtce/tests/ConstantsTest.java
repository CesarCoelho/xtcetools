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

import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.space.xtce.toolkit.XTCEConstants;

/**
 *
 * @author dovereem
 */

public class ConstantsTest {
    
    public ConstantsTest() {
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
    public void checkSchemaDefaultValue() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String expected = "SpaceSystemV1.2-27Feb2014-mods.xsd";
        Assert.assertTrue( "Expected '" +
                           expected +
                           "' but got '" +
                           XTCEConstants.DEFAULT_SCHEMA_FILE +
                           "'",
                           XTCEConstants.DEFAULT_SCHEMA_FILE.equals( expected ) == true );

    }

    @Test
    public void checkNamespaceDefaultValue() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String expected = "http://www.omg.org/space/xtce";
        Assert.assertTrue( "Expected '" +
                           expected +
                           "' but got '" +
                           XTCEConstants.XTCE_NAMESPACE +
                           "'",
                           XTCEConstants.XTCE_NAMESPACE.equals( expected ) == true );

    }

    @Test
    public void checkPackageDefaultValue() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        String expected = "org.omg.space.xtce.database";
        Assert.assertTrue( "Expected '" +
                           expected +
                           "' but got '" +
                           XTCEConstants.XTCE_PACKAGE +
                           "'",
                           XTCEConstants.XTCE_PACKAGE.equals( expected ) == true );

    }

}
