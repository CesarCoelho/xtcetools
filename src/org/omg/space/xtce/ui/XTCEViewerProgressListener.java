/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omg.space.xtce.ui;

import org.omg.space.xtce.toolkit.XTCEProgressListener;

/**
 *
 * @author David Overeem
 *
 */

public class XTCEViewerProgressListener implements XTCEProgressListener {
    
    XTCEViewerProgressListener( XTCEViewerProgressMonitor progressDialog ) {
        userInterface_ = progressDialog;
    }

    public void updateProgress( int percentComplete, String currentStep ) {
        userInterface_.updateProgress( percentComplete, currentStep );
    }

    private XTCEViewerProgressMonitor userInterface_ = null;

}
