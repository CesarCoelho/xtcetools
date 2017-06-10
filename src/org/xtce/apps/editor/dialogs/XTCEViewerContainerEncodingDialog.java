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

import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import org.xtce.apps.editor.ui.XTCEViewerFunctions;
import org.xtce.apps.editor.ui.XTCEViewerPreferences;
import org.xtce.toolkit.XTCEContainerContentEntry;
import org.xtce.toolkit.XTCEContainerContentModel;
import org.xtce.toolkit.XTCEContainerContentModelBase;
import org.xtce.toolkit.XTCEContainerEntryValue;
import org.xtce.toolkit.XTCEDatabase;
import org.xtce.toolkit.XTCEFunctions;
import org.xtce.toolkit.XTCETMContainer;
import org.xtce.toolkit.XTCETelecommand;
import org.xtce.toolkit.XTCETelecommandContentModel;
import org.xtce.toolkit.XTCETypedObject.EngineeringType;

/** XTCE Viewer/Browser dialog window for displaying content of a container
 * and providing the user an opportunity to encode a binary stream from values
 * set into the parameters of the container.
 *
 * @author dovereem
 *
 */

public class XTCEViewerContainerEncodingDialog extends javax.swing.JDialog {

    /** Constructs a new dialog window for a selected database container
     * from the container tree to permit the user to encode an arbitrary binary
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

    public XTCEViewerContainerEncodingDialog( java.awt.Frame        parent,
                                              boolean               modal,
                                              XTCETMContainer       container,
                                              XTCEDatabase          db,
                                              XTCEViewerPreferences prefs ) {

        super( parent, modal );
        initComponents();

        setTitle( XTCEFunctions.getText( "dialog_encodecontainer_title1" ) + // NOI18N
                  ": " + //NOI18N
                  container.getInheritancePath().replaceFirst( "/", "" ) ); // NOI18N

        db_          = db;
        prefs_       = prefs;
        tmContainer_ = container;
        tcObject_    = null;
        values_      = new ArrayList<>();

        populate();

        setLocationRelativeTo( parent );

    }

    /** Constructs a new dialog window for a selected database telecommand
     * from the telecommand tree to permit the user to encode an arbitrary
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

    public XTCEViewerContainerEncodingDialog( java.awt.Frame        parent,
                                              boolean               modal,
                                              XTCETelecommand       telecommand,
                                              XTCEDatabase          db,
                                              XTCEViewerPreferences prefs ) {

        super( parent, modal );
        initComponents();

        setTitle( XTCEFunctions.getText( "dialog_encodetelecommand_title1" ) + // NOI18N
                  ": " + //NOI18N
                  telecommand.getInheritancePath().replaceFirst( "/", "" ) ); // NOI18N

        db_          = db;
        prefs_       = prefs;
        tmContainer_ = null;
        tcObject_    = telecommand;
        values_      = new ArrayList<>();

        populate();

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
        binaryTextFieldScrollPane = new javax.swing.JScrollPane();
        binaryTextField = new javax.swing.JTextArea();
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
        binaryLabel.setText(bundle.getString("dialog_encodecontainer_hex1")); // NOI18N

        binaryTextField.setColumns(20);
        binaryTextField.setRows(8);
        binaryTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                binaryTextFieldKeyPressed(evt);
            }
        });
        binaryTextFieldScrollPane.setViewportView(binaryTextField);

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
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(dismissButton)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(binaryTextFieldScrollPane, javax.swing.GroupLayout.Alignment.LEADING)
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
                .addComponent(binaryTextFieldScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentsSupplementLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contentScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(warningsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dismissButton)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dismissButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dismissButtonActionPerformed

        dispatchEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );

    }//GEN-LAST:event_dismissButtonActionPerformed

    private void binaryTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_binaryTextFieldKeyPressed

        XTCEViewerFunctions.copyPasteTextArea( evt, binaryTextField );

    }//GEN-LAST:event_binaryTextFieldKeyPressed

    /** Method to repopulate the existing dialog when a value changes.
     *
     *
     */

    public void repopulate() {
        populate();
    }

    private void populate() {

        warnings.setText( "" );
        binaryTextField.setText( "" );

        try {

            if ( tmContainer_ != null ) {

                XTCEContainerContentModel model =
                    db_.processContainer( tmContainer_, values_, false );

                addContainerEntries( model.getContentList() );
                addContainerWarnings( model );

                BitSet rawBits    = model.encodeContainer();
                long   sizeInBits = model.getTotalSize();

                binaryTextField.setText( makeHexString( sizeInBits, rawBits ) );

            } else if ( tcObject_ != null ) {

                XTCETelecommandContentModel model =
                    db_.processTelecommand( tcObject_, values_, false );

                addContainerEntries( model.getContentList() );
                addContainerWarnings( model );

                BitSet rawBits    = model.encodeContainer();
                long   sizeInBits = model.getTotalSize();

                binaryTextField.setText( makeHexString( sizeInBits, rawBits ) );

            } else {

                warnings.append( XTCEFunctions.getText( "dialog_encodecontainer_error_notype" ) ); // NOI18N

            }

        } catch ( Exception ex ) {
            warnings.append( ex.getLocalizedMessage() );
            ex.printStackTrace();
        }

        warnings.setCaretPosition( 0 );
        binaryTextField.setCaretPosition( 0 );

    }

    private String makeHexString( final long sizeInBits, final BitSet rawBits ) {

        long sizeInBytes = sizeInBits / 8L;
        long rawBitSize  = rawBits.size();

        if ( sizeInBits % 8L != 0 ) {
            sizeInBytes += 1;
        }

        StringBuilder hex = new StringBuilder( "0x" ); // NOI18N

        for ( int iii = 0; iii < sizeInBytes; ++iii ) {

            int sb  = iii * 8;
            int val = 0;

            for ( int jjj = 0; jjj < 8; ++jjj ) {
                if ( sb + jjj >= rawBitSize ) {
                    continue;
                }
                if ( rawBits.get( sb + jjj ) == true ) {
                    val += 1 << ( 7 - jjj );
                }
            }

            hex.append( String.format( "%02x", val ) ); // NOI18N

        }

        return hex.toString();

    }

    private void addContainerWarnings( final XTCEContainerContentModelBase model ) {

        for ( String warning : model.getWarnings() ) {
            warnings.append( warning );
            warnings.append( System.getProperty( "line.separator" ) ); // NOI18N
        }

        for ( XTCEContainerContentEntry entry : model.getContentList() ) {
            if ( entry.getValue() != null ) {
                List<String> itemWarns = entry.getValue().getWarnings();
                for ( String itemWarn : itemWarns ) {
                    warnings.append( itemWarn );
                    warnings.append( System.getProperty( "line.separator" ) ); // NOI18N
                }
            }
        }

    }

    private void addContainerEntries( final List<XTCEContainerContentEntry> entries ) {

        boolean showAllNamespaces  = prefs_.getShowAllAliasNamespacesOption();
        boolean showNamespaces     = prefs_.getShowAliasNamespacesOption();
        String  preferredNamespace = prefs_.getPreferredAliasNamespaceOption();

        Point location = contentScrollPane.getViewport().getViewPosition();

        JPanel entryPanel = new JPanel();
        entryPanel.setLayout( new BoxLayout( entryPanel, BoxLayout.Y_AXIS ) );

        for ( XTCEContainerContentEntry entry : entries ) {

            switch ( entry.getEntryType() ) {

                case PARAMETER:
                    if ( ( entry.getParameter().getEngineeringType() != EngineeringType.STRUCTURE ) &&
                         ( entry.getParameter().getEngineeringType() != EngineeringType.ARRAY     ) &&
                         ( entry.isCurrentlyInUse()                  == true                      ) ) {
                        entryPanel.add( new XTCEViewerContainerContentRow( entry,
                                                                           showAllNamespaces,
                                                                           showNamespaces,
                                                                           preferredNamespace,
                                                                           this,
                                                                           values_ ) );
                    }
                    break;

                case ARGUMENT:
                    if ( ( entry.getArgument().getEngineeringType() != EngineeringType.STRUCTURE ) &&
                         ( entry.getArgument().getEngineeringType() != EngineeringType.ARRAY     ) &&
                         ( entry.isCurrentlyInUse()                 == true                      ) ) {
                        entryPanel.add( new XTCEViewerContainerContentRow( entry,
                                                                           showAllNamespaces,
                                                                           showNamespaces,
                                                                           preferredNamespace,
                                                                           this,
                                                                           values_ ) );
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
        contentScrollPane.getViewport().setViewPosition( location );
        contentScrollPane.revalidate();
        contentScrollPane.repaint();

    }

    // Private Data Members

    private final XTCEDatabase                  db_;
    private final XTCEViewerPreferences         prefs_;
    private final XTCETMContainer               tmContainer_;
    private final XTCETelecommand               tcObject_;
    private final List<XTCEContainerEntryValue> values_;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel binaryLabel;
    private javax.swing.JTextArea binaryTextField;
    private javax.swing.JScrollPane binaryTextFieldScrollPane;
    private javax.swing.JScrollPane contentScrollPane;
    private javax.swing.JLabel contentsLabel;
    private javax.swing.JLabel contentsSupplementLabel;
    private javax.swing.JButton dismissButton;
    private javax.swing.JTextArea warnings;
    private javax.swing.JLabel warningsLabel;
    private javax.swing.JScrollPane warningsScrollPane;
    // End of variables declaration//GEN-END:variables

}
