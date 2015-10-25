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
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry.FieldType;

/**
 *
 * @author dovereem
 */
public class EnumerationsTest {
    
    public EnumerationsTest() {
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
    public void checkFieldType() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        FieldType ft;

        ft = FieldType.valueOf( "PARAMETER" );

        Assert.assertTrue( "String 'PARAMETER' should work",
                           ft == FieldType.PARAMETER );

        try {
            ft = FieldType.valueOf( "FOOBAR" );
            Assert.fail( "String 'FOOBAR' should not have worked" );
        } catch ( Exception ex ) {
            // expected an exception
        }

        FieldType[] fts = FieldType.values();

        int count = fts.length;

        Assert.assertTrue( "Should have been 4 field types",
                           count == 4 );
    }

}
