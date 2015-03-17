/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.ui;

import org.omg.space.xtce.toolkit.XTCESpaceSystem;
import javax.swing.tree.DefaultMutableTreeNode;

/** This class assists the graphical interface application by providing a
 * path and reference to the JTree objects that keep a list of the XTCE Space
 * Systems.
 *
 * @author Melanie Laub
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
