/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

/** This class contains several static constants for use throughout the XTCE
 * Toolkit and User Interface.
 *
 * @author Melanie Laub
 *
 */

public class XTCEConstants {

    /** Private Constructor
     *
     * This is to prevent a class of this type from being instantiated.
     *
     */

    private XTCEConstants() { }

    /** The schema file to use by default for document validation.
     *
     * This file is the default if no other file is specified in the XTCE
     * document or otherwise overridden.  This file is the one that the JAXB
     * bindings were created from for this toolkit and is strongly recommended
     * because of that.  It contains some additions and improvements that are
     * on the docket for inclusion in XTCE 1.2 release from the OMG.
     *
     */

    public static final String DEFAULT_SCHEMA_FILE = "SpaceSystemV1.2-27Feb2014-mods.xsd";

    /** The URL representing the XTCE XML namespace from the Object Management
     * Group (OMG).
     *
     * This constant should not be changed.
     *
     */

    public static final String XTCE_NAMESPACE      = "http://www.omg.org/space/xtce";

    /** The Java Package declaration for the generated classes from the XTCE
     * schema.
     *
     * This constant is later used for marshaling to and from XML.  It should
     * not be changed and must match the package of the toolkit.
     *
     */

    public static final String XTCE_PACKAGE        = "org.omg.space.xtce.database";

}
