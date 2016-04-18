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

package org.xtce.apps.editor.ui;

import org.xtce.toolkit.XTCESpaceSystem;
import javax.swing.tree.DefaultMutableTreeNode;

/** This class assists the graphical interface application by providing a
 * path and reference to the JTree objects that keep a list of the XTCE Space
 * Systems.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerSpaceSystemTreeNode extends DefaultMutableTreeNode {

    /** Constructor
     *
     * @param label The text to display on the specific tree node that this
     * object gets attached to.
     *
     * @param referenceObject A reference to the XTCESpaceSystem object that
     * this tree node is linked to in the internal data model.
     *
     */
    
    XTCEViewerSpaceSystemTreeNode( String label, XTCESpaceSystem referenceObject ) {
        super( label );
        referenceObject_ = referenceObject;
    }

    /** Accessor to retrieve the fully qualified name with path to the Space
     * System that is represented by the object of this class.
     *
     * @return String containing the fully qualified path.
     *
     */

    public String getFullPath() {
        return getSpaceSystemReference().getFullPath();
    }

    /** Accessor to retrieve the reference to the XTCESpaceSystem object that
     * is associated to the tree node that is represented by the object of this
     * class.
     *
     * @return XTCESpaceSystem containing the reference.  The way this class is
     * used will not result in this return value ever being null.
     *
     */

    public XTCESpaceSystem getSpaceSystemReference() {
        return referenceObject_;
    }

    /// The reference object held by the instance of this class

    private XTCESpaceSystem referenceObject_ = null;
    
}
