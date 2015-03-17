/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry;
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry.FieldType;

/**
 *
 * @author b1053583
 */

public class XTCEViewerContainerTableCellRenderer extends DefaultTableCellRenderer {

    XTCEViewerContainerTableCellRenderer( ArrayList<XTCEContainerContentEntry> entries ) {
        entries_ = entries;
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

        if ( ( entries_ != null ) && ( entries_.size() > row ) ) {
            if ( entries_.get( row ).getEntryType() == FieldType.PARAMETER ) {
                if ( entries_.get( row ).getParameter().isValid() == false ) {
                    ccc.setForeground( Color.RED );
                    return ccc;
                }
            }
            if ( entries_.get( row ).isCurrentlyInUse() == false ) {
                ccc.setForeground( Color.ORANGE );
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

    private ArrayList<XTCEContainerContentEntry> entries_ = null;

    private DefaultTableCellRenderer defaultRenderer =
        new DefaultTableCellRenderer();

}
