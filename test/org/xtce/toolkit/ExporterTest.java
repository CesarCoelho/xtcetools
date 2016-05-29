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
import java.util.List;
import java.util.Properties;
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

public class ExporterTest {
    
    public ExporterTest() {
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

    private class NullExporter extends XTCEDatabaseExporter {

        NullExporter( XTCEDatabase db, Properties properties )
            throws XTCEDatabaseException {

            super( db, properties );

        }

    }

    @Test
    public void testEmptyImplementation() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {
            XTCEDatabaseExporter temp = new NullExporter( null, null );
            Assert.fail( "Should have thrown exception on null database" );
        } catch ( Exception ex ) {
            // expect a null database to throw an exception
        }

        String file = "src/org/xtce/toolkit/database/examples/BogusSAT-2.xml";

        try {

            File csvfile = new File( "tempfile.csv" );

            XTCEDatabase db =
                new XTCEDatabase( new File( file ), false, false, true );

            XTCEDatabaseExporter e1 = new NullExporter( db, null );

            Properties configProperties = new Properties();
            configProperties.setProperty( "use_header_row", "true" ); // NOI18N
            configProperties.setProperty( "use_namespaces", "true" ); // NOI18N
            configProperties.setProperty( "show_all_alias_namespaces", "true" ); // NOI18N
            configProperties.setProperty( "show_alias_namespaces", "true" ); // NOI18N
            configProperties.setProperty( "preferred_alias_namespace", "" ); // NOI18N
            configProperties.setProperty( "show_all_conditions", "false" ); // NOI18N

            XTCEDatabaseExporter e2 = new NullExporter( db, configProperties );

            List<String> msgs;

            msgs = e2.exportParameters( csvfile );

            Assert.assertTrue( "Should have 1 string response for parameters",
                               msgs.size() == 1 );

            Assert.assertEquals( XTCEFunctions.getText( "dialog_export_notyetimplemented_text" ),
                                 msgs.get( 0 ) );

            msgs = e2.exportContainers( csvfile );

            Assert.assertTrue( "Should have 1 string response for containers",
                               msgs.size() == 1 );

            Assert.assertEquals( XTCEFunctions.getText( "dialog_export_notyetimplemented_text" ),
                                 msgs.get( 0 ) );

            msgs = e2.exportTelecommands( csvfile );

            Assert.assertTrue( "Should have 1 string response for telecommands",
                               msgs.size() == 1 );

            Assert.assertEquals( XTCEFunctions.getText( "dialog_export_notyetimplemented_text" ),
                                 msgs.get( 0 ) );

        } catch ( Exception ex ) {
            Assert.fail( "Unexpected exception: " + ex.getLocalizedMessage() );
        }

    }

}
