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
import org.xtce.toolkit.XTCETMStream;

/** This class assists the graphical interface application by providing a
 * path and reference to JTree objects so that the tree node can easily
 * reference a stream.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerStreamTreeNode extends DefaultMutableTreeNode {

    /** Constructor
     * 
     * @param label The text to display on the specific tree node that this
     * object gets attached to.
     *
     * @param streamObject A reference to the XTCETMStream object that
     * this tree node is linked to in the internal data model.
     *
     */

    XTCEViewerStreamTreeNode( String label, XTCETMStream streamObject ) {
        super( label );
        streamObject_ = streamObject;
    }

    /** Accessor to retrieve the name of the XTCE Stream that is
     * represented by this node.
     *
     * @return String containing the name of the container.
     */

    public String getName() {
        return getStreamReference().getName();
    }

    /** Accessor to retrieve a reference to the XTCETMStream object that is
     * represented by this node in the tree of streams.
     *
     * @return XTCETMStream pointed to by this tree node.
     *
     */

    public XTCETMStream getStreamReference() {
        return streamObject_;
    }

    /// The reference object held by the instance of this class

    private XTCETMStream streamObject_ = null;

}
