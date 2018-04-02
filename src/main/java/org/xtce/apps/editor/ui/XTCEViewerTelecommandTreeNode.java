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

import javax.swing.tree.DefaultMutableTreeNode;
import org.xtce.toolkit.XTCETelecommand;
import org.xtce.toolkit.XTCETelecommandContentModel;

/** This class assists the graphical interface application by providing a
 * path and reference to the JTree objects that keep a list of the XTCE
 * MetaCommand element that represents this node on the tree.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerTelecommandTreeNode extends DefaultMutableTreeNode {

    /** Constructor
     * 
     * @param label The text to display on the specific tree node that this
     * object gets attached to.
     *
     * @param containerObject A reference to the XTCETelemcommand object that
     * this tree node is linked to in the internal data model.
     *
     */

    XTCEViewerTelecommandTreeNode( String label, XTCETelecommand tcObject ) {
        super( label );
        telcommandObject_ = tcObject;
    }

    /** Accessor to retrieve the name of the XTCE MetaCommand element that is
     * represented by this node.
     *
     * @return String containing the name of the telecommand.
     */

    public String getName() {
        return getTelecommandReference().getName();
    }

    /** Accessor to retrieve the fully qualified name and path to this
     * telecommand in the XTCE Space System hierarchy.
     *
     * @return String containing the UNIX style path reference and name of the
     * telecommand that is represented by this node.
     *
     */

    public String getTelecommandPath() {
        return getTelecommandReference().getFullPath();
    }

    /** Accessor to retrieve the fully qualified name and path to this
     * telecommand in the inheritance tree of MetaCommand elements within the
     * XTCE data model.
     *
     * @return String containing the UNIX style path reference and name of the
     * container as it is represented in the telecommand inheritance model.
     *
     */

    public String getInheritancePath() {
        return getTelecommandReference().getInheritancePath();
    }

    /** Accessor to retrieve a reference to the XTCETelecommand object that is
     * represented by this node in the tree of containers.
     *
     * @return XTCETelecommand pointed to by this tree node.
     *
     */

    public XTCETelecommand getTelecommandReference() {
        return telcommandObject_;
    }

    /** Accessor to retrieve a reference to the evaluated container content for
     * this tree node, or null if it has not been evaluated before.
     *
     * @return XTCETelecommandContentModel reference to the container contents.
     *
     */

    public XTCETelecommandContentModel getContentModel() {
        return contentModel_;
    }

    /** Setter to update the internal container model object when the container
     * for this tree node is evaluated.
     *
     * @param contentModel The XTCETelecommandContentModel object that was
     * created for this container tree node.
     *
     */

    public void setContentModel( XTCETelecommandContentModel contentModel ) {
        contentModel_ = contentModel;
    }

    /// The reference object held by the instance of this class

    private XTCETelecommand telcommandObject_ = null;

    /// The container model object that was evaluated for this container so
    /// that it can be retrieved on click events without re-evaluating.  This
    /// is only loaded when a container is selected and evaluated.

    private XTCETelecommandContentModel contentModel_ = null;
    
}
