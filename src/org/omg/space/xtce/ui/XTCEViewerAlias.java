/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omg.space.xtce.ui;

/**
 *
 * @author David Overeem
 *
 */

public class XTCEViewerAlias extends javax.swing.JPanel {

    /**
     * Creates new form XTCEViewerAlias
     */
    public XTCEViewerAlias( XTCEViewerAliasSet aliasSet, String ns, String text, int idx ) {
        initComponents();
        namespaceText.setText( ns );
        aliasText.setText( text );
        aliasSet_ = aliasSet;
        idx_      = idx;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        namespaceLabel = new javax.swing.JLabel();
        namespaceText = new javax.swing.JTextField();
        aliasLabel = new javax.swing.JLabel();
        aliasText = new javax.swing.JTextField();
        removeButton = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/omg/space/xtce/toolkit/MessagesBundle"); // NOI18N
        namespaceLabel.setText(bundle.getString("tab_ssdetail_nslabel_text")); // NOI18N

        namespaceText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namespaceTextActionPerformed(evt);
            }
        });

        aliasLabel.setText(bundle.getString("tab_ssdetail_aliaslabel_text")); // NOI18N

        aliasText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aliasTextActionPerformed(evt);
            }
        });

        removeButton.setText(bundle.getString("tab_ssdetail_remove_text")); // NOI18N
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(namespaceLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(namespaceText, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aliasLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(aliasText, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeButton)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(namespaceLabel)
                    .addComponent(namespaceText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aliasLabel)
                    .addComponent(aliasText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        aliasSet_.removeAlias( idx_ );
    }//GEN-LAST:event_removeButtonActionPerformed

    private void aliasTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aliasTextActionPerformed
        if ( isVisible() == true ) {
            aliasSet_.editAlias( namespaceText.getText(), aliasText.getText(), idx_ );
        }
    }//GEN-LAST:event_aliasTextActionPerformed

    private void namespaceTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namespaceTextActionPerformed
        if ( isVisible() == true ) {
            aliasSet_.editAlias( namespaceText.getText(), aliasText.getText(), idx_ );
        }
    }//GEN-LAST:event_namespaceTextActionPerformed

    public void setEditable( boolean editEnabled ) {
        removeButton.setEnabled( editEnabled );
        aliasText.setEditable( editEnabled );
        namespaceText.setEditable( editEnabled );
    }

    // Private Data Members

    private XTCEViewerAliasSet aliasSet_ = null;
    private int                idx_      = 0;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel aliasLabel;
    private javax.swing.JTextField aliasText;
    private javax.swing.JLabel namespaceLabel;
    private javax.swing.JTextField namespaceText;
    private javax.swing.JButton removeButton;
    // End of variables declaration//GEN-END:variables
}
