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

import org.xtce.toolkit.XTCEProgressListener;

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
