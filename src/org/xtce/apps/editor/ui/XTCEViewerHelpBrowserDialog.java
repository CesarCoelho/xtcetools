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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.net.URL;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.xtce.toolkit.XTCEFunctions;


/** This class is a dialog implementation that leverages an internal JFXPanel
 * so that it can use the Java FX Web Browser Component.
 *
 * The GUI design for this cannot be edited with NetBeans because it is setup
 * in Swing mode.  The basic layouts for this are copied from the class
 * XTCEViewerHelpDialog, which uses a basic Swing component for the rendering
 * of HTML.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerHelpBrowserDialog extends JDialog {

    /** Constructor for a new web browser based dialog to support opening
     * help pages within the XTCE Viewer.
     *
     * @param parent Frame containing the XTCE Viewer parent window frame.
     *
     * @param modal Boolean indicating if this dialog window should have modal
     * behavior with respect to the parent window.
     *
     * @param pageUrl URL containing the initial document in the dialog.
     *
     */

    XTCEViewerHelpBrowserDialog( java.awt.Frame parent,
                                 final boolean  modal,
                                 final URL      pageUrl ) {

        super(parent, modal);

        panel_         = new JFXPanel();
        dismissButton_ = new JButton();
        backButton_    = new JButton();
        forwardButton_ = new JButton();

        setTitle( XTCEFunctions.getText( "help_menu_label" ) );

        // For using JavaFX Panel here, we need to HIDE_ON_CLOSE otherwise it
        // will call Platform.exit and the app will not be able to open another
        // FX window.

        setDefaultCloseOperation( WindowConstants.HIDE_ON_CLOSE );
        setMinimumSize( new Dimension( 640, 480 ) );
        setPreferredSize( new Dimension( 1024, 768 ) );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/xtce/toolkit/MessagesBundle"); // NOI18N
        dismissButton_.setText(bundle.getString("general_dismiss_text")); // NOI18N
        dismissButton_.setMaximumSize(new java.awt.Dimension(90, 25));
        dismissButton_.setMinimumSize(new java.awt.Dimension(90, 25));
        dismissButton_.setPreferredSize(new java.awt.Dimension(90, 25));
        dismissButton_.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dismissButtonActionPerformed(evt);
            }
        });

        backButton_.setText(bundle.getString("general_back")); // NOI18N
        backButton_.setMaximumSize(new java.awt.Dimension(90, 25));
        backButton_.setMinimumSize(new java.awt.Dimension(90, 25));
        backButton_.setPreferredSize(new java.awt.Dimension(90, 25));
        backButton_.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });

        forwardButton_.setText(bundle.getString("general_forward")); // NOI18N
        forwardButton_.setMaximumSize(new java.awt.Dimension(90, 25));
        forwardButton_.setMinimumSize(new java.awt.Dimension(90, 25));
        forwardButton_.setPreferredSize(new java.awt.Dimension(90, 25));
        forwardButton_.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(57, Short.MAX_VALUE)
                .addComponent(backButton_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dismissButton_, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(forwardButton_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dismissButton_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(backButton_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(forwardButton_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        setLocationRelativeTo( parent );

        Platform.runLater( new Runnable() {
            @Override
            public void run() {
                initialiseJavaFXScene( pageUrl );
                SwingUtilities.invokeLater( new Runnable() {
                    @Override
                    public void run() {
                        pack();
                        setVisible( true );
                    }
                } );
            }
        } );

    }

    /** Method to dismiss the dialog
     *
     * @param evt ActionEvent containing the action performed, which is not
     * needed for this implementation.
     *
     */

    private void dismissButtonActionPerformed( ActionEvent evt ) {                                              
        dispatchEvent( new WindowEvent( this, WindowEvent.WINDOW_CLOSING ) );
    }

    /** Action performed when the "back" button is pressed
     *
     * @param evt ActionEvent containing the action performed, which is not
     * needed for this implementation.
     *
     */

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {                                           

        final WebHistory history = webEngine_.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();
        Platform.runLater( new Runnable() {
            @Override
            public void run() {
                try {
                    history.go( -1 );
                } catch ( IndexOutOfBoundsException ex ) {
                    // ignore it
                }
            }
        });

    }                                          

    /** Action performed when the "forward" button is pressed
     *
     * @param evt ActionEvent containing the action performed, which is not
     * needed for this implementation.
     *
     */

    private void forwardButtonActionPerformed(java.awt.event.ActionEvent evt) {                                              

        final WebHistory history = webEngine_.getHistory();
        ObservableList<WebHistory.Entry> entryList = history.getEntries();
        int currentIndex = history.getCurrentIndex();
        Platform.runLater( new Runnable() {
            @Override
            public void run() {
                try {
                    history.go( 1 );
                } catch ( IndexOutOfBoundsException ex ) {
                    // ignore it
                }
            }
        });

    }   

    /** Method to draw the web page URL.
     *
     * @param pageUrl URL to the page to open when the dialog starts.
     *
     */

    private void initialiseJavaFXScene( URL pageUrl ) {

        webView_ = new WebView();
        webEngine_ = webView_.getEngine();
        webEngine_.load( pageUrl.toString() );

        Scene scene = new Scene( webView_ );
        panel_.setScene( scene );

    }

    // Private Data Members

    private JButton        dismissButton_ = null;
    private JButton        backButton_    = null;
    private JButton        forwardButton_ = null;
    private JFXPanel       panel_         = null;
    private WebView        webView_       = null;
    private WebEngine      webEngine_     = null;

}

