/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.net.URL;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

/** This class is a dialog implementation that leverages an internal JFXPanel
 * so that it can use the Java FX Web Browser Component.
 *
 * The GUI design for this cannot be edited with NetBeans because it is setup
 * in Swing mode.  The basic layouts for this are copied from the class
 * XTCEViewerHelpDialog, which uses a basic Swing component for the rendering
 * of HTML.
 *
 * @author b1053583
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

    public XTCEViewerHelpBrowserDialog( java.awt.Frame parent,
                                        final boolean  modal,
                                        final URL      pageUrl ) {

        super(parent, modal);

        panel_ = new JFXPanel();
        Platform.runLater( new Runnable() {
            @Override
            public void run() {
                initialiseJavaFXScene( pageUrl );
            }
        } );
        dismissButton_ = new JButton();

        setTitle( "Help" );

        setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        setMinimumSize( new Dimension( 640, 480 ) );
        setPreferredSize( new Dimension( 1024, 768 ) );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/omg/space/xtce/toolkit/MessagesBundle"); // NOI18N
        dismissButton_.setText(bundle.getString("general_dismiss_text")); // NOI18N
        dismissButton_.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dismissButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(154, Short.MAX_VALUE)
                .addComponent(dismissButton_, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(154, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_, javax.swing.GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dismissButton_))
        );

        pack();
        setLocationRelativeTo( parent );
        setVisible( true );

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

    /** Method to draw the web page URL.
     *
     * @param pageUrl URL to the page to open initially when the dialog starts.
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

    private JButton   dismissButton_ = null;
    private JFXPanel  panel_         = null;
    private WebView   webView_       = null;
    private WebEngine webEngine_     = null;

}

