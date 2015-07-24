/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.omg.space.xtce.toolkit.XTCEParameter;

/**
 *
 * @author David Overeem
 *
 */

public class XTCEViewerParameterTableCellRenderer extends DefaultTableCellRenderer {

    XTCEViewerParameterTableCellRenderer( ArrayList<XTCEParameter> parameters ) {
        parameters_ = parameters;
    }

    @Override
    public Component getTableCellRendererComponent( JTable  table,
                                                    Object  notused,
                                                    boolean isSelected,
                                                    boolean hasFocus,
                                                    int     row,
                                                    int     column ) {

        Component ccc = super.getTableCellRendererComponent( table,
                                                             notused,
                                                             isSelected,
                                                             hasFocus,
                                                             row,
                                                             column );

        if ( ( parameters_ != null ) && ( parameters_.size() > row ) ) {
            if ( parameters_.get( row ).isValid() == false ) {
                if ( ccc instanceof JComponent ) {
                    ((JComponent)ccc).setToolTipText( "Invalid type definition" );
                }
                ccc.setForeground( Color.RED );
                return ccc;
            }
        }

        return defaultRenderer.getTableCellRendererComponent( table,
                                                              notused,
                                                              isSelected,
                                                              hasFocus,
                                                              row,
                                                              column );

    }

    private ArrayList<XTCEParameter> parameters_ = null;

    private final DefaultTableCellRenderer defaultRenderer =
        new DefaultTableCellRenderer();

}
