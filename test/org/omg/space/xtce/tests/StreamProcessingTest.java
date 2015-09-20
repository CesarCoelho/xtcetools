/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.tests;

import java.io.File;
import java.util.List;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEDatabaseException;
import org.omg.space.xtce.toolkit.XTCESpaceSystem;
import org.omg.space.xtce.toolkit.XTCETMContainer;
import org.omg.space.xtce.toolkit.XTCETMStream;

/**
 *
 * @author dovereem
 */

public class StreamProcessingTest {
    
    public StreamProcessingTest()  {

        try {
            loadDocument();
        } catch ( Throwable ex ) {
            Assert.fail( "Cannot start test: " + ex.getLocalizedMessage() );
        }

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
    public void lookupAllStreamsFromDatabase() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            List<XTCETMStream> streams = db_.getStreams();

            long expected = 2;

            Assert.assertTrue( "Should have found " +
                Long.toString( expected ) + " streams, but found instead " +
                Long.toString( streams.size() ),
                streams.size() == expected );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    @Test
    public void lookupAllStreamsFromSpaceSystem() {

        final String methodName =
            Thread.currentThread().getStackTrace()[1].getMethodName();

        System.out.println( "Test Case: " + methodName + "()" );

        try {

            XTCESpaceSystem ss = db_.getSpaceSystem( "/BogusSAT/SC001/Onboard_Tables" );

            List<XTCETMStream> streams = ss.getStreams();

            long expected = 1;

            Assert.assertTrue( "Should have found " +
                Long.toString( expected ) + " streams in " + ss.getName() +
                ", but found instead " +
                Long.toString( streams.size() ),
                streams.size() == expected );

            expected = 0;

            ss = db_.getSpaceSystem( "/BogusSAT/SC001/BusElectronics" );

            streams = ss.getStreams();

            Assert.assertTrue( "Should have found " +
                Long.toString( expected ) + " streams in " + ss.getName() +
                ", but found instead " +
                Long.toString( streams.size() ),
                streams.size() == expected );

        } catch ( Exception ex ) {
            //ex.printStackTrace();
            Assert.fail( ex.getLocalizedMessage() );
        }

    }

    private void loadDocument() throws XTCEDatabaseException {

        //System.out.println( "Loading the BogusSat-2.xml demo database" );

        String file = "src/org/omg/space/xtce/database/BogusSat-2.xml";

        db_ = new XTCEDatabase( new File( file ), false, false, true );

    }

    // Private Data Members

    private XTCEDatabase  db_  = null;

}
