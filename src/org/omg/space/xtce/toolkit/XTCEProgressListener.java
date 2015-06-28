/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omg.space.xtce.toolkit;

/**
 *
 * @author David Overeem
 *
 */

public interface XTCEProgressListener {
    
    void updateProgress( int percentComplete, String currentStep );

}
