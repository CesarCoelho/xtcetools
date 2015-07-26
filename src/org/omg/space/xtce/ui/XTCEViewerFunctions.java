/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.ui;

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.omg.space.xtce.toolkit.XTCESpaceSystem;

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

    /** Helper function to implement copying contents of a table cell to the
     * System Clipboard for pasting in other applications.
     *
     * @param table JTable object to query for the cell contents and copy
     *
     * @param mouseLocation Point from Java AWT that contains the location of
     * the mouse when the copy was selected.
     *
     */

    public static void copyCell( JTable table, Point mouseLocation ) {

        int row = table.rowAtPoint( mouseLocation );
        int col = table.columnAtPoint( mouseLocation );

        if ( ( row == -1 ) || ( col == -1 ) ) {
            return;
        }

        TableModel tm = table.getModel();
        String text = (String)tm.getValueAt( row, col );

        if ( text == null ) {
            return;
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents( new StringSelection( text ), null );

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

    public static void buildSpaceSystemTree( JTree                      tree,
                                             ArrayList<XTCESpaceSystem> spaceSystems ) {
        
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
