/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.ui;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/** This class supports rendering the Telecommand Tree nodes by making the
 * abstract telecommands in italic font, based on whichever font is currently
 * being used.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerTelecommandTreeCellRenderer extends DefaultTreeCellRenderer {

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

        XTCEViewerTelecommandTreeNode node =
            (XTCEViewerTelecommandTreeNode)value;

        if ( ( node == null ) || ( node.getTelecommandReference() == null ) ) {
            // fall down to the default
        } else {
            if ( node.getTelecommandReference().isAbstract() == true ) {
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

    private DefaultTreeCellRenderer defaultRenderer =
        new DefaultTreeCellRenderer();

}
