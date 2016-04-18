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

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.xtce.toolkit.XTCEParameter;

/**
 *
 * @author David Overeem
 *
 */

public class XTCEViewerParameterTableCellRenderer extends DefaultTableCellRenderer {

    XTCEViewerParameterTableCellRenderer( List<XTCEParameter> parameters ) {
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

    private List<XTCEParameter> parameters_ = null;

    private final DefaultTableCellRenderer defaultRenderer =
        new DefaultTableCellRenderer();

}
