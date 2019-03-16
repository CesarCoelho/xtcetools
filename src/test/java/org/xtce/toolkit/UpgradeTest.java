/* Copyright 2017 David Overeem (dovereem@cox.net)
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

public class UpgradeTest {
    
    public UpgradeTest() {
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
    public void testUpgradeWithNamespace() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        File xmlFile =
            new File( "src/test/resources/org/xtce/toolkit/test/UnitTest-Convert-NS.xml" );

        File newFile = new File( "UnitTest-Convert-NS-TEST-1.2.xml" );

        newFile.deleteOnExit();

        System.out.println( "Converting the UnitTest-Convert-NS.xml demo database" );

        try {

            XTCEDatabaseConverter converter =
                new XTCEDatabaseConverter( xmlFile, false );

            long expected = 78;
            long count    = converter.upgrade();

            converter.normalizeDocument();

            boolean success = converter.save( newFile );

            for ( String message : converter.getMessages() ) {
                System.out.println( message );
            }

            Assert.assertTrue( "Should have seen " +
                               Long.toString( expected ) +
                               " items converted, instead it was " +
                               Long.toString( count ),
                               count == expected );

            Assert.assertTrue( "Failed to save file", success );

        } catch ( Exception ex ) {
            Assert.fail( "Unexpected exception: " + ex.getLocalizedMessage() );
        }

    }

    @Test
    public void testUpgradeWithDefaultNamespace() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        File xmlFile =
            new File( "src/test/resources/org/xtce/toolkit/test/UnitTest-Convert-DefNS.xml" );

        File newFile = new File( "UnitTest-Convert-DefNS-TEST-1.2.xml" );

        newFile.deleteOnExit();

        System.out.println( "Converting the UnitTest-Convert-DefNS.xml demo database" );

        try {

            XTCEDatabaseConverter converter =
                new XTCEDatabaseConverter( xmlFile, false );

            long expected = 78;
            long count    = converter.upgrade();

            converter.normalizeDocument();

            boolean success = converter.save( newFile );

            for ( String message : converter.getMessages() ) {
                System.out.println( message );
            }

            Assert.assertTrue( "Should have seen " +
                               Long.toString( expected ) +
                               " items converted, instead it was " +
                               Long.toString( count ),
                               count == expected );

            Assert.assertTrue( "Failed to save file", success );

        } catch ( Exception ex ) {
            Assert.fail( "Unexpected exception: " + ex.getLocalizedMessage() );
        }

    }

}
