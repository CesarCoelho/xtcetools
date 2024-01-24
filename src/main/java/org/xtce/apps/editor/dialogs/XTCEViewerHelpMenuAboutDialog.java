/* Copyright 2015 David Overeem (dovereem@startmail.com)
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

package org.xtce.apps.editor.dialogs;

import java.awt.event.WindowEvent;
import java.io.InputStream;
import javax.swing.JTextArea;
import org.xtce.toolkit.XTCEFunctions;

/** This class contains the Help Menu Dialog for the About/Version item.
 *
 * @author dovereem
 */

public class XTCEViewerHelpMenuAboutDialog extends javax.swing.JDialog {

    /** Constructor to instantiate the dialog box.
     *
     * @param parent Frame object containing the parent window.
     *
     * @param modal boolean indicating if this dialog should block interaction
     * with the parent window.
     *
     */

    public XTCEViewerHelpMenuAboutDialog(java.awt.Frame parent, boolean modal) {

        super(parent, modal);
        initComponents();
        addTextToTextArea( "README.txt", textDescription ); // NOI18N
        addTextToTextArea( "LICENSE.txt", textLicense ); // NOI18N
        setLocationRelativeTo( parent );

    }

    /** Private method to read a text data file from the Classpath because it
     * will be inside the deployed JAR file.
     *
     * @param file String containing the name of the file to load, relative to
     * the root of the JAR file.
     *
     * @param textField JTextArea from the dialog to place the loaded text.
     *
     */

    private void addTextToTextArea( String file, JTextArea textField ) {

        InputStream stream =
            ClassLoader.getSystemResourceAsStream( file );

        byte[] data = new byte[4096];

        try {
            while ( stream.read( data ) > 0 ) {
                textField.append( new String( data ) );
            }
        } catch ( Exception ex ) {
            textField.setText( XTCEFunctions.getText( "dialog_about_read_failed" ) + // NOI18N
                               " " + // NOI18N
                               file );
        }

        try {
            stream.close();
        } catch ( Exception ex ) {
            // not much that can be done here
        }

        textField.setCaretPosition( 0 );
        textField.setEditable( false );

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dismissButton = new javax.swing.JButton();
        labelDescription = new javax.swing.JLabel();
        scrollPaneDescription = new javax.swing.JScrollPane();
        textDescription = new javax.swing.JTextArea();
        labelLicense = new javax.swing.JLabel();
        scrollPaneLicense = new javax.swing.JScrollPane();
        textLicense = new javax.swing.JTextArea();
        versionLabel = new javax.swing.JLabel();
        scrollPaneVersion = new javax.swing.JScrollPane();
        textVersion = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/xtce/toolkit/MessagesBundle"); // NOI18N
        setTitle(bundle.getString("dialog_about_title")); // NOI18N
        setMinimumSize(new java.awt.Dimension(520, 0));

        dismissButton.setText(bundle.getString("general_dismiss_text")); // NOI18N
        dismissButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dismissButtonActionPerformed(evt);
            }
        });

        labelDescription.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelDescription.setText(bundle.getString("dialog_about_description")); // NOI18N
        labelDescription.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        textDescription.setEditable(false);
        textDescription.setColumns(20);
        textDescription.setRows(5);
        textDescription.setWrapStyleWord(true);
        scrollPaneDescription.setViewportView(textDescription);

        labelLicense.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        labelLicense.setText(bundle.getString("dialog_about_license")); // NOI18N
        labelLicense.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        textLicense.setEditable(false);
        textLicense.setColumns(20);
        textLicense.setRows(5);
        textLicense.setWrapStyleWord(true);
        scrollPaneLicense.setViewportView(textLicense);

        versionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        versionLabel.setText(bundle.getString("dialog_about_version")); // NOI18N
        versionLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        textVersion.setEditable(false);
        textVersion.setColumns(20);
        textVersion.setRows(3);
        textVersion.setWrapStyleWord(true);
        scrollPaneVersion.setViewportView(textVersion);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(versionLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPaneLicense, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 508, Short.MAX_VALUE)
                    .addComponent(scrollPaneDescription, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(dismissButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(labelDescription, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelLicense, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scrollPaneVersion))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneDescription)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelLicense)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneLicense)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(versionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPaneVersion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dismissButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dismissButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dismissButtonActionPerformed
        dispatchEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );
    }//GEN-LAST:event_dismissButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { // NOI18N
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(XTCEViewerHelpMenuAboutDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(XTCEViewerHelpMenuAboutDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(XTCEViewerHelpMenuAboutDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(XTCEViewerHelpMenuAboutDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                XTCEViewerHelpMenuAboutDialog dialog = new XTCEViewerHelpMenuAboutDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton dismissButton;
    private javax.swing.JLabel labelDescription;
    private javax.swing.JLabel labelLicense;
    private javax.swing.JScrollPane scrollPaneDescription;
    private javax.swing.JScrollPane scrollPaneLicense;
    private javax.swing.JScrollPane scrollPaneVersion;
    private javax.swing.JTextArea textDescription;
    private javax.swing.JTextArea textLicense;
    private javax.swing.JTextArea textVersion;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
}
