/* Copyright 2015 David Overeem (dovereem@startmail.com)
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

import javax.swing.tree.DefaultMutableTreeNode;
import org.xtce.toolkit.XTCETMContainer;
import org.xtce.toolkit.XTCEContainerContentModel;

/** This class assists the graphical interface application by providing a
 * path and reference to JTree objects so that the tree node can easily
 * reference a container.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerContainerTreeNode extends DefaultMutableTreeNode {

    /** Constructor
     * 
     * @param label The text to display on the specific tree node that this
     * object gets attached to.
     *
     * @param containerObject A reference to the XTCETMContainer object that
     * this tree node is linked to in the internal data model.
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
     * represented by this node in the tree of containers.
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
