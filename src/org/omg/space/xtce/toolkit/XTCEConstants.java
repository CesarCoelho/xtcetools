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

package org.omg.space.xtce.toolkit;

/** This class contains several static constants for use throughout the XTCE
 * Toolkit and User Interface.
 *
 * @author David Overeem
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

    public static final String DEFAULT_SCHEMA_FILE =
        "SpaceSystemV1.2-27Feb2014-mods.xsd";

    /** The URL representing the XTCE XML namespace from the Object Management
     * Group (OMG).
     *
     * This constant should not be changed.
     *
     */

    public static final String XTCE_NAMESPACE =
        "http://www.omg.org/space/xtce";

    /** The Java Package declaration for the generated classes from the XTCE
     * schema.
     *
     * This constant is later used for marshaling to and from XML.  It should
     * not be changed and must match the package of the toolkit.
     *
     */

    public static final String XTCE_PACKAGE =
        "org.omg.space.xtce.database";

}
