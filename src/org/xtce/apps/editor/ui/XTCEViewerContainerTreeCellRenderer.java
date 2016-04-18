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

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/** This class supports rendering the Container Tree nodes by making the
 * abstract containers in italic font, based on whichever font is currently
 * being used.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerContainerTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent( JTree   tree,
                                                   Object  value,
                                                   boolean selected,
                                                   boolean expanded,
                                                   boolean leaf,
                                                   int     row,
                                                   boolean hasFocus ) {

        Component ccc = super.getTreeCellRendererComponent( tree,
                                                            value,
                                                            selected,
                                                            expanded,
                                                            leaf,
                                                            row,
                                                            hasFocus );

        XTCEViewerContainerTreeNode node =
            (XTCEViewerContainerTreeNode)value;

        if ( ( node == null ) || ( node.getContainerReference() == null ) ) {
            // fall down to the default
        } else {
            if ( node.getContainerReference().isAbstract() == true ) {
                ccc.setFont( getFont().deriveFont( Font.ITALIC ) );
                return this;
            }
        }

        return defaultRenderer.getTreeCellRendererComponent( tree,
                                                             value,
                                                             selected,
                                                             expanded,
                                                             leaf,
                                                             row,
                                                             hasFocus );

    }

    private final DefaultTreeCellRenderer defaultRenderer =
        new DefaultTreeCellRenderer();

}
