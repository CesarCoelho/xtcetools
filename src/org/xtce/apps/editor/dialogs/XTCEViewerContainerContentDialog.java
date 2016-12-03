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

package org.xtce.apps.editor.dialogs;

import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.xtce.apps.editor.ui.XTCEViewerFunctions;
import org.xtce.apps.editor.ui.XTCEViewerPreferences;
import org.xtce.toolkit.XTCEContainerContentEntry;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEFunctions;
import org.xtce.toolkit.XTCETMContainer;
import org.xtce.toolkit.XTCETMStream;
import org.xtce.toolkit.XTCETelecommand;
import org.xtce.toolkit.XTCETelecommandContentModel;
import org.xtce.toolkit.XTCETypedObject.EngineeringType;

/** XTCE Viewer/Browser dialog window for displaying processed content of a
 * binary data stream.
 *
 * @author dovereem
 *
 */

public class XTCEViewerContainerContentDialog extends javax.swing.JDialog {

    /** Constructs a new dialog window for a selected database container
     * from the container tree to permit the user to decode an arbitrary binary
     * hex string.
     *
     * @param parent Frame object of the parent window, which is a part of the
     * JDialog interface to permit control of the dialog from the parent.
     *
     * @param modal boolean indicating if this window should block interaction
     * with the parent Frame.
     *
     * @param container XTCETMContainer object selected from the viewer
     * container tree.
     *
     * @param db XTCEDatabase object used to perform the processing of the
     * container object.
     *
     * @param prefs XTCEViewerPreferences object used for formatting the name
     * of the container content item in the entry rows.
     *
     */

    public XTCEViewerContainerContentDialog( java.awt.Frame        parent,
                                             boolean               modal,
                                             XTCETMContainer       container,
                                             XTCEDatabase          db,
                                             XTCEViewerPreferences prefs ) {

        super( parent, modal );
        initComponents();

        setTitle( XTCEFunctions.getText( "dialog_decodecontainer_title1" ) + // NOI18N
                  ": " + //NOI18N
                  container.getInheritancePath().replaceFirst( "/", "" ) ); // NOI18N

        db_          = db;
        prefs_       = prefs;
        tmContainer_ = container;
        tmStream_    = null;
        tcObject_    = null;

        setLocationRelativeTo( parent );

    }

    /** Constructs a new dialog window for a selected database stream
     * from the stream tree to permit the user to decode an arbitrary binary
     * hex string.
     *
     * @param parent Frame object of the parent window, which is a part of the
     * JDialog interface to permit control of the dialog from the parent.
     *
     * @param modal boolean indicating if this window should block interaction
     * with the parent Frame.
     *
     * @param stream XTCETMStream object selected from the viewer stream tree.
     *
     * @param db XTCEDatabase object used to perform the processing of the
     * stream object.
     *
     * @param prefs XTCEViewerPreferences object used for formatting the name
     * of the container content item in the entry rows.
     *
     */

    public XTCEViewerContainerContentDialog( java.awt.Frame        parent,
                                             boolean               modal,
                                             XTCETMStream          stream,
                                             XTCEDatabase          db,
                                             XTCEViewerPreferences prefs ) {

        super( parent, modal );
        initComponents();

        setTitle( XTCEFunctions.getText( "dialog_decodecontainer_title2" ) + // NOI18N
                  ": " + //NOI18N
                  stream.getName() ); // NOI18N

        db_          = db;
        prefs_       = prefs;
        tmContainer_ = null;
        tmStream_    = stream;
        tcObject_    = null;

        setLocationRelativeTo( parent );

    }

    /** Constructs a new dialog window for a selected database telecommand
     * from the telecommand tree to permit the user to decode an arbitrary
     * binary hex string.
     *
     * @param parent Frame object of the parent window, which is a part of the
     * JDialog interface to permit control of the dialog from the parent.
     *
     * @param modal boolean indicating if this window should block interaction
     * with the parent Frame.
     *
     * @param telecommand XTCETelecommand object selected from the viewer
     * telecommand tree.
     *
     * @param db XTCEDatabase object used to perform the processing of the
     * container object.
     *
     * @param prefs XTCEViewerPreferences object used for formatting the name
     * of the container content item in the entry rows.
     *
     */

    public XTCEViewerContainerContentDialog( java.awt.Frame        parent,
                                             boolean               modal,
                                             XTCETelecommand       telecommand,
                                             XTCEDatabase          db,
                                             XTCEViewerPreferences prefs ) {

        super( parent, modal );
        initComponents();

        setTitle( XTCEFunctions.getText( "dialog_decodetelecommand_title1" ) + // NOI18N
                  ": " + //NOI18N
                  telecommand.getInheritancePath().replaceFirst( "/", "" ) ); // NOI18N

        db_          = db;
        prefs_       = prefs;
        tmContainer_ = null;
        tmStream_    = null;
        tcObject_    = telecommand;

        setLocationRelativeTo( parent );

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        binaryLabel = new javax.swing.JLabel();
        binarySupplementLabel = new javax.swing.JLabel();
        binaryTextFieldScrollPane = new javax.swing.JScrollPane();
        binaryTextField = new javax.swing.JTextArea();
        decodeButton = new javax.swing.JButton();
        contentsLabel = new javax.swing.JLabel();
        contentsSupplementLabel = new javax.swing.JLabel();
        contentScrollPane = new javax.swing.JScrollPane();
        dismissButton = new javax.swing.JButton();
        warningsScrollPane = new javax.swing.JScrollPane();
        warnings = new javax.swing.JTextArea();
        warningsLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        binaryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/xtce/toolkit/MessagesBundle"); // NOI18N
        binaryLabel.setText(bundle.getString("dialog_decodecontainer_hex1")); // NOI18N

        binarySupplementLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        binarySupplementLabel.setText(bundle.getString("dialog_decodecontainer_hex2")); // NOI18N

        binaryTextField.setColumns(20);
        binaryTextField.setRows(8);
        binaryTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                binaryTextFieldKeyTyped(evt);
            }
        });
        binaryTextFieldScrollPane.setViewportView(binaryTextField);

        decodeButton.setText(bundle.getString("dialog_decodecontainer_decodebutton")); // NOI18N
        decodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                decodeButtonActionPerformed(evt);
            }
        });

        contentsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        contentsLabel.setText(bundle.getString("dialog_decodecontainer_cont1")); // NOI18N

        contentsSupplementLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        contentsSupplementLabel.setText(bundle.getString("dialog_decodecontainer_cont2")); // NOI18N

        contentScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        dismissButton.setText(bundle.getString("general_dismiss_text")); // NOI18N
        dismissButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dismissButtonActionPerformed(evt);
            }
        });

        warnings.setColumns(20);
        warnings.setRows(6);
        warningsScrollPane.setViewportView(warnings);

        warningsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        warningsLabel.setText(bundle.getString("general_warnings")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(binaryLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(binarySupplementLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(binaryTextFieldScrollPane)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(decodeButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(dismissButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(warningsScrollPane, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(warningsLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(contentsSupplementLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
                            .addComponent(contentsLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(contentScrollPane, javax.swing.GroupLayout.Alignment.LEADING))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(binaryLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(binarySupplementLabel)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(binaryTextFieldScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(decodeButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentsSupplementLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dismissButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void decodeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_decodeButtonActionPerformed

        warnings.setText( "" );

        String hex =
            XTCEFunctions.getCleanHexString( binaryTextField.getText() );

        try {

            byte[] bytes = XTCEFunctions.getBytesFromHexString( hex );

            if ( tcObject_ != null ) {

                XTCETelecommandContentModel model =
                    db_.processTelecommand( tcObject_, bytes );

                addContainerEntries( model.getContentList() );

                for ( String warning : model.getWarnings() ) {
                    warnings.append( warning );
                    warnings.append( System.getProperty( "line.separator" ) ); // NOI18N
                }

            } else {

                XTCEContainerContentModel model;

                if ( tmContainer_ != null ) {
                    model = db_.processContainer( tmContainer_, bytes );
                } else if ( tmStream_ != null ) {
                    model = tmStream_.processStream( bytes );
                } else {
                    warnings.append( XTCEFunctions.getText( "dialog_decodecontainer_error_notype" ) ); // NOI18N
                    return;
                }

                addContainerEntries( model.getContentList() );

                for ( String warning : model.getWarnings() ) {
                    warnings.append( warning );
                    warnings.append( System.getProperty( "line.separator" ) ); // NOI18N
                }

            }

        } catch ( NumberFormatException ex ) {
            warnings.append( XTCEFunctions.getText( "dialog_decodecontainer_error_hex" ) ); // NOI18N
        } catch ( Exception ex ) {
            warnings.append( ex.getLocalizedMessage() );
        }

        warnings.setCaretPosition( 0 );

    }//GEN-LAST:event_decodeButtonActionPerformed

    private void dismissButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dismissButtonActionPerformed

        dispatchEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );

    }//GEN-LAST:event_dismissButtonActionPerformed

    private void binaryTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_binaryTextFieldKeyTyped

        XTCEViewerFunctions.copyPasteTextArea( evt, binaryTextField );

    }//GEN-LAST:event_binaryTextFieldKeyTyped

    private void addContainerEntries( final List<XTCEContainerContentEntry> entries ) {

        boolean showAllNamespaces  = prefs_.getShowAllAliasNamespacesOption();
        boolean showNamespaces     = prefs_.getShowAliasNamespacesOption();
        String  preferredNamespace = prefs_.getPreferredAliasNamespaceOption();

        JPanel entryPanel = new JPanel();
        entryPanel.setLayout( new BoxLayout( entryPanel, BoxLayout.Y_AXIS ) );

        for ( XTCEContainerContentEntry entry : entries ) {

            if ( ( entry.isCurrentlyInUse() == false ) ||
                 ( entry.getValue()         == null  ) ) {
                continue;
            }

            switch ( entry.getEntryType() ) {

                case PARAMETER:
                    if ( ( entry.getParameter().getEngineeringType() != EngineeringType.STRUCTURE ) &&
                         ( entry.getParameter().getEngineeringType() != EngineeringType.ARRAY     ) ) {
                        entryPanel.add( new XTCEViewerContainerContentRow( entry,
                                                                           showAllNamespaces,
                                                                           showNamespaces,
                                                                           preferredNamespace ) );
                    }
                    break;

                case ARGUMENT:
                    if ( ( entry.getArgument().getEngineeringType() != EngineeringType.STRUCTURE ) &&
                         ( entry.getArgument().getEngineeringType() != EngineeringType.ARRAY     ) ) {
                        entryPanel.add( new XTCEViewerContainerContentRow( entry,
                                                                           showAllNamespaces,
                                                                           showNamespaces,
                                                                           preferredNamespace ) );
                    }
                    break;

                case CONSTANT:
                    entryPanel.add( new XTCEViewerContainerContentRow( entry,
                                                                       showAllNamespaces,
                                                                       showNamespaces,
                                                                       preferredNamespace ) );
                    break;

            }

        }

        contentScrollPane.setViewportView( entryPanel );
        contentScrollPane.revalidate();
        contentScrollPane.repaint();

    }

    // Private Data Members

    private final XTCEDatabase          db_;
    private final XTCEViewerPreferences prefs_;
    private final XTCETMContainer       tmContainer_;
    private final XTCETMStream          tmStream_;
    private final XTCETelecommand       tcObject_;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel binaryLabel;
    private javax.swing.JLabel binarySupplementLabel;
    private javax.swing.JTextArea binaryTextField;
    private javax.swing.JScrollPane binaryTextFieldScrollPane;
    private javax.swing.JScrollPane contentScrollPane;
    private javax.swing.JLabel contentsLabel;
    private javax.swing.JLabel contentsSupplementLabel;
    private javax.swing.JButton decodeButton;
    private javax.swing.JButton dismissButton;
    private javax.swing.JTextArea warnings;
    private javax.swing.JLabel warningsLabel;
    private javax.swing.JScrollPane warningsScrollPane;
    // End of variables declaration//GEN-END:variables
}
