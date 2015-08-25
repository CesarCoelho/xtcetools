/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

        String expected = "org.omg.space.xtce.database";
        Assert.assertTrue( "Expected '" +
                           expected +
                           "' but got '" +
                           XTCEConstants.XTCE_PACKAGE +
                           "'",
                           XTCEConstants.XTCE_PACKAGE.equals( expected ) == true );

    }

}
