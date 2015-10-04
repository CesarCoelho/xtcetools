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

package org.omg.space.xtce.ui;

import java.awt.event.WindowEvent;
import java.util.List;
import org.omg.space.xtce.toolkit.XTCEDatabase;
import org.omg.space.xtce.toolkit.XTCEDatabaseException;
import org.omg.space.xtce.toolkit.XTCEFunctions;
import org.w3c.dom.NodeList;

/** The XPath Query Dialog provides an interactive screen for the user to be
 * able to execute and view XML string results based on an arbitrary XPath
 * query statement against the loaded XTCE document.
 *
 * It is necessary to prefix element names with xtce: because of Namespace
 * Context awareness in Document Object Model (DOM).  This is common to both
 * Perl and Java implementations of the DOM and XPath standards.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerXpathQueryDialog extends javax.swing.JFrame {

    /** This dialog presents the user with an opportunity to interactively
     * query the XML document loaded using the XML XPath facility.
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

    XTCEViewerXpathQueryDialog( XTCEViewer            parent,
                                XTCEViewerPreferences prefs,
                                XTCEDatabase          dbFile ) {

        prefs_  = prefs;
        xtcedb_ = dbFile;
        initComponents();
        resultsText.setEditable( false );
        populatePreviousSearches( true );
        deleteButton.setEnabled( false );
        saveButton.setEnabled( false );
        setLocationRelativeTo( parent );
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

        jPanel1 = new javax.swing.JPanel();
        decoratorLabel = new javax.swing.JLabel();
        queryTextField = new javax.swing.JTextField();
        executeButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        queriesListBox = new javax.swing.JComboBox();
        saveButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        resultsScrollPane = new javax.swing.JScrollPane();
        resultsText = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        dismissButton = new javax.swing.JButton();
        resultsCountLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/omg/space/xtce/toolkit/MessagesBundle"); // NOI18N
        setTitle(bundle.getString("dialog_xpathquery_title")); // NOI18N

        decoratorLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        decoratorLabel.setText(bundle.getString("dialog_xpathquery_qline")); // NOI18N

        queryTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryTextFieldActionPerformed(evt);
            }
        });

        executeButton.setText(bundle.getString("dialog_xpathquery_execute")); // NOI18N
        executeButton.setMaximumSize(new java.awt.Dimension(100, 25));
        executeButton.setMinimumSize(new java.awt.Dimension(100, 25));
        executeButton.setPreferredSize(new java.awt.Dimension(100, 25));
        executeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                executeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(decoratorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(queryTextField)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(executeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(decoratorLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(queryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(executeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        queriesListBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queriesListBoxActionPerformed(evt);
            }
        });

        saveButton.setText(bundle.getString("dialog_xpathquery_save")); // NOI18N
        saveButton.setMaximumSize(new java.awt.Dimension(150, 25));
        saveButton.setMinimumSize(new java.awt.Dimension(150, 25));
        saveButton.setPreferredSize(new java.awt.Dimension(150, 25));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        deleteButton.setText(bundle.getString("dialog_xpathquery_delete")); // NOI18N
        deleteButton.setMaximumSize(new java.awt.Dimension(150, 25));
        deleteButton.setMinimumSize(new java.awt.Dimension(150, 25));
        deleteButton.setPreferredSize(new java.awt.Dimension(150, 25));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 154, Short.MAX_VALUE)
                        .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 155, Short.MAX_VALUE))
                    .addComponent(queriesListBox, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(queriesListBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        resultsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        resultsText.setColumns(20);
        resultsText.setRows(5);
        resultsScrollPane.setViewportView(resultsText);

        dismissButton.setLabel(bundle.getString("general_dismiss_text")); // NOI18N
        dismissButton.setMaximumSize(new java.awt.Dimension(150, 25));
        dismissButton.setMinimumSize(new java.awt.Dimension(150, 25));
        dismissButton.setPreferredSize(new java.awt.Dimension(150, 25));
        dismissButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dismissButtonActionPerformed(evt);
            }
        });

        resultsCountLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        resultsCountLabel.setText(bundle.getString("dialog_xpathquery_noresults")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(dismissButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(resultsCountLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(resultsCountLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dismissButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(resultsScrollPane)
                .addContainerGap())
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resultsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dismissButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dismissButtonActionPerformed

        dispatchEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );

    }//GEN-LAST:event_dismissButtonActionPerformed

    private void executeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_executeButtonActionPerformed

        try {

            long startTime = System.currentTimeMillis();

            NodeList nodes =
                xtcedb_.evaluateXPathQuery( queryTextField.getText() );

            long duration = System.currentTimeMillis() - startTime;

            resultsCountLabel.setText( Integer.toString( nodes.getLength() ) +
                                       " " +
                                       XTCEFunctions.getText( "dialog_xpathquery_results" ) +
                                       " ( " +
                                       Double.toString( duration / 1000.0 ) +
                                       " " +
                                       XTCEFunctions.getText( "file_chooser_load_time_unit_text" ) +
                                       " )" );

            resultsText.setText( XTCEFunctions.xmlPrettyPrint( nodes ) );
            lastQueryString_ = queryTextField.getText();
            saveButton.setEnabled( true );

        } catch ( XTCEDatabaseException ex ) {

            resultsText.setText( ex.getLocalizedMessage() );
            lastQueryString_ = "";
            saveButton.setEnabled( false );

        }

    }//GEN-LAST:event_executeButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed

        prefs_.addSavedXPathQuerySearch( lastQueryString_ );
        populatePreviousSearches( true );
        deleteButton.setEnabled( false );
        saveButton.setEnabled( false );

    }//GEN-LAST:event_saveButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed

        String item = (String)queriesListBox.getSelectedItem();
        prefs_.removeSavedXPathQuerySearch( item );
        populatePreviousSearches( true );
        deleteButton.setEnabled( false );
        saveButton.setEnabled( false );

    }//GEN-LAST:event_deleteButtonActionPerformed

    private void queriesListBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queriesListBoxActionPerformed

        String item = (String)queriesListBox.getSelectedItem();
        queryTextField.setText( item );
        deleteButton.setEnabled( true );
        saveButton.setEnabled( false );

    }//GEN-LAST:event_queriesListBoxActionPerformed

    private void queryTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_queryTextFieldActionPerformed

        // this is triggered when the user presses the Enter/Return key in the
        // Query Text Field.  Just invoke the button method.

        executeButtonActionPerformed( evt );

    }//GEN-LAST:event_queryTextFieldActionPerformed

    private void populatePreviousSearches( boolean setEmptyRow ) {

        queriesListBox.removeAllItems();
        List<String> itemList = prefs_.getSavedXPathQueries();
        if ( setEmptyRow == true ) {
            queriesListBox.addItem( "" );
        }
        for ( String searchItem : itemList ) {
            queriesListBox.addItem( searchItem );
        }
        queriesListBox.setSelectedIndex( 0 );

    }

    // Private Data Members

    private XTCEViewerPreferences prefs_           = null;
    private XTCEDatabase          xtcedb_          = null;
    private String                lastQueryString_ = "";


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel decoratorLabel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton dismissButton;
    private javax.swing.JButton executeButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JComboBox queriesListBox;
    private javax.swing.JTextField queryTextField;
    private javax.swing.JLabel resultsCountLabel;
    private javax.swing.JScrollPane resultsScrollPane;
    private javax.swing.JTextArea resultsText;
    private javax.swing.JButton saveButton;
    // End of variables declaration//GEN-END:variables
}
