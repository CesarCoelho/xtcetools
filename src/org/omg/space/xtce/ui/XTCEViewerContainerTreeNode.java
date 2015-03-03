/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.ui;

import javax.swing.tree.DefaultMutableTreeNode;
import org.omg.space.xtce.toolkit.XTCETMContainer;
import org.omg.space.xtce.toolkit.XTCEContainerContentModel;
import org.omg.space.xtce.toolkit.XTCESpaceSystem;

/** This class assists the graphical interface application by providing a
 * path and reference to the JTree objects that keep a list of the XTCE
 * SequenceContainer element that represents this node on the tree.
 *
 * @author Melanie Laub
 */

public class XTCEViewerContainerTreeNode extends DefaultMutableTreeNode {

    /** Constructor
     * 
     * @param label The text to display on the specific tree node that this
     * object gets attached to.
     *
     * @param containerObject A reference to the XTCETMContainer object that
 this tree node is linked to in the internal data model.
     *
     */

    XTCEViewerContainerTreeNode( String label, XTCETMContainer containerObject ) {
        super( label );
        containerObject_ = containerObject;
    }

    /** Accessor to retrieve the name of the XTCE SequenceContainer that is
     * represented by this node.
     *
     * @return String containing the name of the container.
     */

    public String getName() {
        return getContainerReference().getName();
    }

    /** Accessor to retrieve the fully qualified name and path to this
     * container in the XTCE Space System hierarchy.
     *
     * @return String containing the UNIX style path reference and name of the
     * container that is represented by this node.
     *
     */

    public String getContainerPath() {
        return getContainerReference().getFullPath();
    }

    /** Accessor to retrieve the fully qualified name and path to this
     * container in the inheritance tree of SequenceContainers within the XTCE
     * data model.
     *
     * @return String containing the UNIX style path reference and name of the
     * container as it is represented in the container inheritance model.
     *
     */

    public String getInheritancePath() {
        return getContainerReference().getInheritancePath();
    }

    /** Accessor to retrieve a reference to the XTCETMContainer object that is
 represented by this node in the tree of containers.
     *
     * @return XTCETMContainer pointed to by this tree node.
     *
     */

    public XTCETMContainer getContainerReference() {
        return containerObject_;
    }

    /** Accessor to retrieve a reference to the evaluated container content for
     * this tree node, or null if it has not been evaluated before.
     *
     * @return XTCEContainerContentModel reference to the container contents.
     *
     */

    public XTCEContainerContentModel getContentModel() {
        return contentModel_;
    }

    /** Setter to update the internal container model object when the container
     * for this tree node is evaluated.
     *
     * @param contentModel The XTCEContainerContentModel object that was
     * created for this container tree node.
     *
     */

    public void setContentModel( XTCEContainerContentModel contentModel ) {
        contentModel_ = contentModel;
    }

    /// The reference object held by the instance of this class

    private XTCETMContainer containerObject_ = null;

    /// The container model object that was evaluated for this container so
    /// that it can be retrieved on click events without re-evaluating.  This
    /// is only loaded when a container is selected and evaluated.

    private XTCEContainerContentModel contentModel_ = null;

}
