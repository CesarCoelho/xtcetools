/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.ui;

import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import org.omg.space.xtce.toolkit.XTCEAlias;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEFunctions;
import org.omg.space.xtce.toolkit.XTCEParameter;
import org.omg.space.xtce.toolkit.XTCESpaceSystem;

/**
 *
 * @author David Overeem
 *
 */

public class XTCEViewerParameterFindDialog extends javax.swing.JFrame {

    /** This dialog presents the user with an opportunity to interactively
     * query the XML document Parameters.
     *
     * @param parent XTCEViewer application for setting the initial location
     * of the dialog box.
     *
     * @param prefs XTCEViewerPreferences object used for saving queries that
     * the user might like to repeat.
     *
     * @param dbFile XTCEDatabase object to perform the queries against.
     *
     */

    public XTCEViewerParameterFindDialog( XTCEViewer            parent,
                                          XTCEViewerPreferences prefs,
                                          XTCEDatabase          dbFile ) {
        initComponents();
        xtceViewer_ = parent;
        prefs_      = prefs;
        dbFile_     = dbFile;
        populatePreviousSearches( true );
        setLocationRelativeTo( xtceViewer_ );
        setVisible( true );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        findByButtonGroup = new javax.swing.ButtonGroup();
        parameterCategoryButtonGroup = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        parameterFindPanel = new javax.swing.JPanel();
        searchTextLabel = new javax.swing.JLabel();
        searchTextComboBox = new javax.swing.JComboBox();
        optionsPanel = new javax.swing.JPanel();
        parameterFindByAliasRadioButton = new javax.swing.JRadioButton();
        telemetryParameterRadioButton = new javax.swing.JRadioButton();
        parameterFindByNameRadioButton = new javax.swing.JRadioButton();
        telecommandParameterRadioButton = new javax.swing.JRadioButton();
        executeButton = new javax.swing.JButton();
        resultsScrollPane = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        goToParameterButton = new javax.swing.JButton();
        dismissButton = new javax.swing.JButton();
        resultsText = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/omg/space/xtce/toolkit/MessagesBundle"); // NOI18N
        setTitle(bundle.getString("dialog_findparameter_title")); // NOI18N

        searchTextLabel.setText(bundle.getString("dialog_findparameter_searchtext_label")); // NOI18N

        searchTextComboBox.setEditable(true);
        searchTextComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchTextComboBoxActionPerformed(evt);
            }
        });

        findByButtonGroup.add(parameterFindByAliasRadioButton);
        parameterFindByAliasRadioButton.setText(bundle.getString("dialog_findparameter_asalias_radiobuttom")); // NOI18N

        parameterCategoryButtonGroup.add(telemetryParameterRadioButton);
        telemetryParameterRadioButton.setSelected(true);
        telemetryParameterRadioButton.setText(bundle.getString("dialog_metrics_tmparameters_text")); // NOI18N

        findByButtonGroup.add(parameterFindByNameRadioButton);
        parameterFindByNameRadioButton.setSelected(true);
        parameterFindByNameRadioButton.setText(bundle.getString("dialog_findparameter_asname_radiobutton")); // NOI18N

        parameterCategoryButtonGroup.add(telecommandParameterRadioButton);
        telecommandParameterRadioButton.setText(bundle.getString("dialog_metrics_tcparameters_text")); // NOI18N

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(parameterFindByNameRadioButton)
                    .addComponent(parameterFindByAliasRadioButton))
                .addGap(18, 18, 18)
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(telemetryParameterRadioButton)
                    .addComponent(telecommandParameterRadioButton))
                .addContainerGap())
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(parameterFindByNameRadioButton)
                    .addComponent(telemetryParameterRadioButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(parameterFindByAliasRadioButton)
                    .addComponent(telecommandParameterRadioButton))
                .addContainerGap())
        );

        executeButton.setText(bundle.getString("dialog_findparameter_execute_button")); // NOI18N
        executeButton.setMaximumSize(new java.awt.Dimension(80, 25));
        executeButton.setMinimumSize(new java.awt.Dimension(80, 25));
        executeButton.setPreferredSize(new java.awt.Dimension(80, 25));
        executeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout parameterFindPanelLayout = new javax.swing.GroupLayout(parameterFindPanel);
        parameterFindPanel.setLayout(parameterFindPanelLayout);
        parameterFindPanelLayout.setHorizontalGroup(
            parameterFindPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parameterFindPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parameterFindPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(parameterFindPanelLayout.createSequentialGroup()
                        .addComponent(searchTextLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchTextComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(executeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(parameterFindPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(optionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        parameterFindPanelLayout.setVerticalGroup(
            parameterFindPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(parameterFindPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(parameterFindPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchTextLabel)
                    .addComponent(searchTextComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(executeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        resultsTable.setAutoCreateRowSorter(true);
        resultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Space System", "Parameter Name", "Parameter Alias(s)", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultsTableMouseClicked(evt);
            }
        });
        resultsScrollPane.setViewportView(resultsTable);
        if (resultsTable.getColumnModel().getColumnCount() > 0) {
            resultsTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("find_menu_spacesystem_label")); // NOI18N
            resultsTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("table_containers_paramname_label")); // NOI18N
            resultsTable.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("table_containers_paramaliases_label")); // NOI18N
            resultsTable.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("table_parameters_desc_col_label")); // NOI18N
        }

        goToParameterButton.setText(bundle.getString("dialog_findparameter_goto_button")); // NOI18N
        goToParameterButton.setMaximumSize(new java.awt.Dimension(125, 25));
        goToParameterButton.setMinimumSize(new java.awt.Dimension(125, 25));
        goToParameterButton.setPreferredSize(new java.awt.Dimension(125, 25));
        goToParameterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goToParameterButtonActionPerformed(evt);
            }
        });

        dismissButton.setText(bundle.getString("dialog_findparameter_dismiss_button")); // NOI18N
        dismissButton.setMaximumSize(new java.awt.Dimension(125, 25));
        dismissButton.setMinimumSize(new java.awt.Dimension(125, 25));
        dismissButton.setPreferredSize(new java.awt.Dimension(125, 25));
        dismissButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dismissButtonActionPerformed(evt);
            }
        });

        resultsText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        resultsText.setText(bundle.getString("dialog_findparameter_initialresults_text")); // NOI18N

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addContainerGap(156, Short.MAX_VALUE)
                .addComponent(goToParameterButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dismissButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(156, Short.MAX_VALUE))
            .addComponent(resultsText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addComponent(resultsText)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(goToParameterButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dismissButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(parameterFindPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(resultsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 580, Short.MAX_VALUE)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parameterFindPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dismissButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dismissButtonActionPerformed
        dispatchEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );
    }//GEN-LAST:event_dismissButtonActionPerformed

    private void executeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeButtonActionPerformed
        String searchText = updatePreviousSearches();
        populateParameterTable( searchText );
    }//GEN-LAST:event_executeButtonActionPerformed

    private void goToParameterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goToParameterButtonActionPerformed

        int row = resultsTable.getSelectedRow();
        if ( row == -1 ) {
            return;
        }

        String spaceSystemName = (String)resultsTable.getValueAt( row, 0 );
        String parameterName   = (String)resultsTable.getValueAt( row, 1 );

        if ( telemetryParameterRadioButton.isSelected() == true ) {
            xtceViewer_.goToParameter( parameterName, spaceSystemName, true );
        } else {
            xtceViewer_.goToParameter( parameterName, spaceSystemName, false );
        }

    }//GEN-LAST:event_goToParameterButtonActionPerformed

    private void searchTextComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchTextComboBoxActionPerformed
        if ( isVisible() == true ) {
            executeButtonActionPerformed( evt );
        }
    }//GEN-LAST:event_searchTextComboBoxActionPerformed

    private void resultsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultsTableMouseClicked
        if ( evt.getClickCount() == 2 ) {
            goToParameterButtonActionPerformed( null );
            dismissButtonActionPerformed( null );
        }
    }//GEN-LAST:event_resultsTableMouseClicked

    private void populatePreviousSearches( boolean setEmptyRow ) {
        searchTextComboBox.removeAllItems();
        ArrayList<String> itemList = prefs_.getRecentFindParameterSearches();
        if ( setEmptyRow == true ) {
            searchTextComboBox.addItem( "" );
        }
        for ( String searchItem : itemList ) {
            if ( setEmptyRow == false ) {
                if ( searchItem.isEmpty() == true ) {
                    continue;
                }
            }
            searchTextComboBox.addItem( searchItem );
        }
        searchTextComboBox.setSelectedIndex( 0 );
    }

    private String updatePreviousSearches() {
        String searchText = (String)searchTextComboBox.getSelectedItem();
        if ( searchText == null ) {
            searchText = "";
        }
        prefs_.addFindParameterSearch( searchText );
        populatePreviousSearches( false );
        return searchText;
    }

    private void populateParameterTable( String searchText ) {

        String preferredNamespace = prefs_.getPreferredAliasNamespaceOption();

        ArrayList<XTCESpaceSystem> spaceSystems = dbFile_.getSpaceSystemTree();
        ArrayList<XTCEParameter> results = new ArrayList<XTCEParameter>();

        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {

            ArrayList<XTCEParameter> parameters = null;
            if ( telemetryParameterRadioButton.isSelected() == true ) {
                parameters = spaceSystem.getTelemetryParameters();
            } else {
                parameters = spaceSystem.getTelecommandParameters();
            }

            for ( XTCEParameter parameter : parameters ) {
                if ( parameterFindByNameRadioButton.isSelected() == true ) {
                    if ( XTCEFunctions.matchesUsingGlob( parameter.getName(), searchText ) == true ) {
                        results.add( parameter );
                    }
                } else {
                    if ( preferredNamespace.isEmpty() == false ) {
                        String searchName = parameter.getAlias( preferredNamespace );
                        if ( searchName != null ) {
                            if ( XTCEFunctions.matchesUsingGlob( searchName, searchText ) == true ) {
                                results.add( parameter );
                            }
                        }
                    } else {
                        ArrayList<XTCEAlias> aliases = parameter.getAliasSet();
                        for ( XTCEAlias alias : aliases ) {
                            if ( XTCEFunctions.matchesUsingGlob( alias.getAliasName(), searchText) == true ) {
                                results.add( parameter );
                                break;
                            }
                        }
                    }
                }
            }

        }

        updateParameterTable( results, searchText );

    }

    private void updateParameterTable( ArrayList<XTCEParameter> results,
                                       String                   searchText ) {

        boolean showAllNamespaces   = prefs_.getShowAllAliasNamespacesOption();
        boolean showAliasNamespaces = prefs_.getShowAliasNamespacesOption();
        String  preferredNamespace  = prefs_.getPreferredAliasNamespaceOption();

        DefaultTableModel tableModel = (DefaultTableModel)resultsTable.getModel();
        tableModel.setRowCount( 0 );

        if ( results.size() > 0 ) {
            resultsText.setText( Long.toString( results.size() ) +
                                 " " +
                                 XTCEFunctions.getText( "dialog_findparameter_found" ) );
        } else {
            resultsText.setText( XTCEFunctions.getText( "dialog_findparameter_none" ) +
                                 " '" +
                                 searchText +
                                 "'" );
            return;
        }

        for ( XTCEParameter parameter : results ) {

            String aliasString =
                XTCEFunctions.makeAliasDisplayString( parameter,
                                                      showAllNamespaces,
                                                      showAliasNamespaces,
                                                      preferredNamespace );

            Object rowData[] = { parameter.getSpaceSystemPath(),
                                 parameter.getName(),
                                 aliasString,
                                 parameter.getDescription() };

            tableModel.addRow( rowData );

        }

    }

    // Private Data Members

    private XTCEViewer            xtceViewer_ = null;
    private XTCEViewerPreferences prefs_      = null;
    private XTCEDatabase          dbFile_     = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton dismissButton;
    private javax.swing.JButton executeButton;
    private javax.swing.ButtonGroup findByButtonGroup;
    private javax.swing.JButton goToParameterButton;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.ButtonGroup parameterCategoryButtonGroup;
    private javax.swing.JRadioButton parameterFindByAliasRadioButton;
    private javax.swing.JRadioButton parameterFindByNameRadioButton;
    private javax.swing.JPanel parameterFindPanel;
    private javax.swing.JScrollPane resultsScrollPane;
    private javax.swing.JTable resultsTable;
    private javax.swing.JLabel resultsText;
    private javax.swing.JComboBox searchTextComboBox;
    private javax.swing.JLabel searchTextLabel;
    private javax.swing.JRadioButton telecommandParameterRadioButton;
    private javax.swing.JRadioButton telemetryParameterRadioButton;
    // End of variables declaration//GEN-END:variables
}
