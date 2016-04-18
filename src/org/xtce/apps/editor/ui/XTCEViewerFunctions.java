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

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.xtce.toolkit.XTCESpaceSystem;

/** This class is a container to capture some common static functions that are
 * applicable throughout the XTCEViewer application.
 *
 * This class is not meant to be constructed, since the functions are static.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerFunctions {

    /** Private Constructor
     *
     * This is to prevent a class of this type from being instantiated.
     *
     */

    private XTCEViewerFunctions() { }

    /** Helper function to expand all the nodes of a JTree object.
     *
     * @param tree JTree to expand all nodes.
     *
     */

    public static void expandAllTreeNodes( JTree tree ) {

        if ( tree != null ) {
            for ( int iii = 0; iii < tree.getRowCount(); iii++ ) {
                tree.expandRow( iii );
            }
        }

    }

    /** Show the right click menu associated with a table in the viewer.
     *
     * @param evt MouseEvent associated with the click.
     *
     * @param table JTable that the click event occurred on.
     *
     * @param menu JPopupMenu from the viewer that is associated with this
     * particular table.
     *
     */

    public static void showRightClickTableMenu( MouseEvent evt,
                                                JTable     table,
                                                JPopupMenu menu ) {

        if ( SwingUtilities.isRightMouseButton( evt ) == true ) {
            Point mouseLocation = evt.getPoint();
            int row = table.rowAtPoint( mouseLocation );
            int col = table.columnAtPoint( mouseLocation );
            table.setRowSelectionInterval( row, row );
            table.setColumnSelectionInterval( col, col );
            menu.show( table, evt.getX(), evt.getY() );
        }

    }

    /** Helper function to implement copying contents of a table cell to the
     * System Clipboard for pasting in other applications.
     *
     * @param table JTable object to query for the cell contents and copy
     *
     */

    public static void copyCell( JTable table ) {

        int row = table.getSelectedRow();
        int col = table.getSelectedColumn();

        if ( ( row == -1 ) || ( col == -1 ) ) {
            return;
        }

        String text = (String)table.getValueAt( row, col );

        if ( text == null ) {
            return;
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( new StringSelection( text ), null );

    }

    /** Helper function to implement copying contents of a table row to the
     * System Clipboard for pasting in other applications.
     *
     * @param table JTable object to query for the row contents and copy
     *
     */

    public static void copyRow( JTable table ) {

        int row         = table.getSelectedRow();
        int columnCount = table.getColumnCount();

        if ( row == -1 ) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        for ( int ccc = 0; ccc < columnCount; ++ccc ) {
            sb.append( '"' );
            if ( table.getValueAt( row, ccc ) instanceof Boolean ) {
                if ( (Boolean)table.getValueAt( row, ccc ) == true ) {
                    sb.append( "true" );
                } else {
                    sb.append( "false" );
                }
            } else {
                sb.append( (String)table.getValueAt( row, ccc ) );
            }
            sb.append( '"' );
            if ( ccc < ( columnCount - 1 ) ) {
                sb.append( ',' );
            }
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( new StringSelection( sb.toString() ), null );

    }

    /** Helper function to implement copying contents of a table column to the
     * System Clipboard for pasting in other applications.
     *
     * @param table JTable object to query for the column contents and copy
     *
     */

    public static void copyColumn( JTable table ) {

        int column   = table.getSelectedColumn();
        int rowCount = table.getRowCount();

        if ( column == -1 ) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        for ( int rrr = 0; rrr < rowCount; ++rrr ) {
            sb.append( '"' );
            if ( table.getValueAt( rrr, column ) instanceof Boolean ) {
                if ( (Boolean)table.getValueAt( rrr, column ) == true ) {
                    sb.append( "true" );
                } else {
                    sb.append( "false" );
                }
            } else {
                sb.append( (String)table.getValueAt( rrr, column ) );
            }
            sb.append( '"' );
            sb.append( System.getProperty( "line.separator" ) ); // NOI18N
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( new StringSelection( sb.toString() ), null );

    }

    /** Helper function to implement copying contents of a table to the
     * System Clipboard for pasting in other applications.
     *
     * @param table JTable object to query for the contents and copy
     *
     */

    public static void copyTable( JTable table ) {

        int rowCount    = table.getRowCount();
        int columnCount = table.getColumnCount();

        StringBuilder sb = new StringBuilder();

        for ( int rrr = 0; rrr < rowCount; ++rrr ) {
            for ( int ccc = 0; ccc < columnCount; ++ccc ) {
                sb.append( '"' );
                if ( table.getValueAt( rrr, ccc ) instanceof Boolean ) {
                    if ( (Boolean)table.getValueAt( rrr, ccc ) == true ) {
                        sb.append( "true" );
                    } else {
                        sb.append( "false" );
                    }
                } else {
                    sb.append( (String)table.getValueAt( rrr, ccc ) );
                }
                sb.append( '"' );
                if ( ccc < ( columnCount - 1 ) ) {
                    sb.append( ',' );
                }
            }
            sb.append( System.getProperty( "line.separator" ) );
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( new StringSelection( sb.toString() ), null );

    }

    /** Helper function to clear/erase the contents of a JTree Swing widget.
     *
     * This is apparently complex enough to justify having its own function and
     * is used throughout the GUI code.
     *
     * @param tree JTree to clear/erase.
     *
     */

    public static void clearTree( JTree tree ) {

        DefaultTreeModel tmodel = new DefaultTreeModel( null );
        tree.setModel( tmodel );
        tree.setPreferredSize( null ); 
        tmodel.reload();
        
    }

    /** Helper function to add the XTCE SpaceSystem array list to a JTree.
     *
     * @param tree JTree to add the XTCEViewerSpaceSystemTreeNode objects to.
     *
     * @param spaceSystems ArrayList of XTCESpaceSystem objects to add.
     *
     */

    public static void buildSpaceSystemTree( JTree                 tree,
                                             List<XTCESpaceSystem> spaceSystems ) {
        
        DefaultTreeModel tmodel = (DefaultTreeModel)tree.getModel();
        
        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            String[] fields = spaceSystems.get( iii ).getFullPath().split( "/" );
            //System.out.println( spaceSystems.get( iii ).getFullPath() );
            if ( tmodel.getRoot() == null ) {
                XTCEViewerSpaceSystemTreeNode obj = new XTCEViewerSpaceSystemTreeNode( fields[1], spaceSystems.get( iii ) );
                tmodel.setRoot( obj );
            }
            XTCEViewerSpaceSystemTreeNode obj = (XTCEViewerSpaceSystemTreeNode)tmodel.getRoot();
            for ( int jjj = 2; jjj < fields.length; ++jjj ) {
                obj = setSpaceSystemTreeNode( obj, spaceSystems.get( iii ) );
            }
        }

        tree.setRootVisible( true );
        tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
        tmodel.reload();

    }

    /** Helper function to determine if a provided SpaceSystem by name is a
     * node in a JTree list of Space Systems.
     *
     * @param tree JTree containing the tree for which to search for nodes.
     *
     * @param searchString String containing the Space System name or path to
     * search for.
     *
     * @param basenameOnly Boolean indicating of the search string argument is
     * a basename or a full path.
     *
     * @return Boolean indicating of a Space System node was found that matches
     * the search string described in the arguments.
     *
     */

    public static boolean selectSpaceSystemFromTree( JTree   tree,
                                                     String  searchString,
                                                     boolean basenameOnly ) {

        DefaultTreeModel tmodel = (DefaultTreeModel)tree.getModel();

        XTCEViewerSpaceSystemTreeNode obj =
            (XTCEViewerSpaceSystemTreeNode)tmodel.getRoot();

        Enumeration eee = obj.breadthFirstEnumeration();

        while ( eee.hasMoreElements() == true ) {

            XTCEViewerSpaceSystemTreeNode node =
                (XTCEViewerSpaceSystemTreeNode)eee.nextElement();

            boolean match = false;

            if ( basenameOnly == true ) {
                match = node.getSpaceSystemReference()
                            .getName()
                            .equals( searchString );
            } else {
                match = node.getFullPath().equals( searchString );
            }

            if ( match == true ) {
                TreeNode[] nodes = tmodel.getPathToRoot( node );
                TreePath path = new TreePath(nodes);
                tree.setSelectionPath( path );
                tree.scrollPathToVisible( path );
                tmodel.nodeChanged( node );
                return true;
            }

        }

        return false;

    }

    /** Private helper function to setup a XTCEViewerSpaceSystemTreeNode where
     * the node could already exist.
     *
     * @param obj XTCEViewerSpaceSystemTreeNode that represents the parent node
     * for the SpaceSystem that is being added to a JTree.
     *
     * @param spaceSystem XTCESpaceSystem object to add to the nodes of a
     * JTree.
     *
     * @return XTCEViewerSpaceSystemTreeNode that already exists or a new one
     * if the path to the Space System hasn't been created yet.
     *
     */

    private static XTCEViewerSpaceSystemTreeNode setSpaceSystemTreeNode( XTCEViewerSpaceSystemTreeNode obj,
                                                                         XTCESpaceSystem               spaceSystem ) {

        for ( int iii = 0; iii < obj.getChildCount(); ++iii ) {
            XTCEViewerSpaceSystemTreeNode child =
                (XTCEViewerSpaceSystemTreeNode)(obj.getChildAt( iii ));
            String existingPath = child.getFullPath();
            if ( existingPath.equals( spaceSystem.getFullPath() ) == true ) {
                //System.out.println( "matched " + spaceSystem.getFullPath() );
                return child;
            } else {
                existingPath += "/"; // NOI18N
                if ( spaceSystem.getFullPath().startsWith( existingPath ) == true ) {
                    //System.out.println( "matched starts " + spaceSystem.getFullPath() );
                    return child;
                }
            }
        }

        XTCEViewerSpaceSystemTreeNode newchild =
            new XTCEViewerSpaceSystemTreeNode( spaceSystem.getName(),
                                               spaceSystem );
        obj.add( newchild );

        return newchild;

    }

}
