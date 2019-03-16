/* Copyright 2017 David Overeem (dovereem@cox.net)
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

import java.awt.Cursor;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.JDialog;
import javax.swing.SwingWorker;
import org.xtce.toolkit.XTCEDatabaseCompressor;
import org.xtce.toolkit.XTCEFunctions;

/** Dialog and action class to compress the XML content in an XTCE document
 * using schema aware algorithms in support of the XTCE Viewer tool.
 *
 * @author dovereem
 *
 */

public class XTCEViewerFileCompressProgressDialog extends    JDialog
                                                  implements PropertyChangeListener {

    /** Constructor
     *
     * Initializes the dialog containing a JProgressBar to capture the progress
     * while compressing an XTCE database XML file.
     *
     * @param xmlFile File containing the XTCE XML document to be compressed.
     *
     * @param options Properties containing a key=value pair list of options
     * selected for the compression operation.
     *
     * @param parent Frame object containing the parent window Frame.
     *
     * @param modal boolean indicating if the parent window should be blocked
     * while this dialog is shown.
     *
     */

    public XTCEViewerFileCompressProgressDialog( final File       xmlFile,
                                                 final Properties options,
                                                 final Frame      parent,
                                                 final boolean    modal ) {

        super( parent, modal );

        initComponents();

        task = new CompressFileTask( xmlFile, options );
        task.addPropertyChangeListener( this );
        progressText.setText( task.step_ );

    }

    /** Method to start the database compression task.
     *
     *
     */

    public void compress() {

        setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
        pack();
        setLocationRelativeTo( getParent() );

        task.execute();

    }

    /** Retrieve the messages that were generated by the database XML file
     * compression task.
     *
     * @return List of Strings containing the messages.
     *
     */

    public List<String> getMessages() {
        return task.messages_;
    }

    /** Retrieve the File that was saved after the compression.
     *
     * @return File containing the saved file or null in the event that the
     * file has not yet been saved.
     *
     */

    public File getSavedFile() {
        return task.getSavedFile();
    }

    /** Method invoked by the SwingWorker Task object when the status of the
     * progress property changes.
     *
     * @param evt PropertyChangeEvent that contains the updated progress to use
     * to update the JProgressBar object.
     *
     */

    @Override
    public void propertyChange( PropertyChangeEvent evt ) {

        if ( evt.getPropertyName().equals( "progress" ) == true ) { // NOI18N

            int progress = (Integer)evt.getNewValue();

            progressBar.setValue( progress );
            progressText.setText( task.step_ );

            if ( progress == 100 ) {
                dispose();
            }

        }

    }

    /** Private inner class to run the compression task in a thread so that the
     * Graphical User Interface can update a JProgressBar.
     *
     */

    private class CompressFileTask extends SwingWorker<Void, Void> {

        /** Constructor
         *
         * Initializes the CompressFileTask object so that it can support
         * performing the update to the provided XTCE XML File.
         *
         * @param xmlFile File containing the location of the XML document to
         * load for the compression.
         *
         * @param options Properties containing a key=value pair list of options
         * selected for the compression operation.
         *
         */

        private CompressFileTask( final File       xmlFile,
                                  final Properties options ) {

            convObj_     = null;
            xmlFile_     = xmlFile;
            options_     = options;
            messages_    = new ArrayList<>();
            step_        = XTCEFunctions.getText( "file_upgrade_start" ); // NOI18N
            count_       = 0;

        }

        /** Method to start the database compression task and run it through to
         * completion.
         *
         */

        @Override
        protected Void doInBackground() {

            step_ = XTCEFunctions.getText( "file_upgrade_step01" ); // NOI18N
            setProgress( 1 );

            try {

                convObj_ = new XTCEDatabaseCompressor( xmlFile_, options_ );

                step_ = XTCEFunctions.getText( "file_compress_step02" ); // NOI18N
                setProgress( 20 );
                count_ += convObj_.compressEncodingAttributes();

                step_ = XTCEFunctions.getText( "file_compress_step03" ); // NOI18N
                setProgress( 25 );
                count_ += convObj_.compressCalibrationTestAttributes();

                step_ = XTCEFunctions.getText( "file_compress_step04" ); // NOI18N
                setProgress( 30 );
                count_ += convObj_.compressParameterPropertiesAttributes();

                step_ = XTCEFunctions.getText( "file_compress_step05" ); // NOI18N
                setProgress( 35 );
                count_ += convObj_.compressListsAndSets();

                step_ = XTCEFunctions.getText( "file_compress_step06" ); // NOI18N
                setProgress( 40 );
                count_ += convObj_.compressIdentityCalibrators();

                step_ = XTCEFunctions.getText( "file_compress_step07" ); // NOI18N
                setProgress( 45 );
                count_ += convObj_.compressSimpleEnumerations();

                step_ = XTCEFunctions.getText( "file_compress_step08" ); // NOI18N
                setProgress( 50 );
                count_ += convObj_.compressValidRangeTruisms();

                step_ = XTCEFunctions.getText( "file_compress_step09" ); // NOI18N
                setProgress( 60 );
                count_ += convObj_.compressDuplicateTypes();

                step_ = XTCEFunctions.getText( "file_compress_step10" ); // NOI18N
                setProgress( 70 );
                count_ += convObj_.compressContainerStartBits();

                step_ = XTCEFunctions.getText( "file_compress_step11" ); // NOI18N
                setProgress( 80 );
                count_ += convObj_.compressAdditionalAttributes();

                step_ = XTCEFunctions.getText( "file_upgrade_step11" ); // NOI18N
                setProgress( 90 );
                convObj_.normalizeDocument();

                step_ = XTCEFunctions.getText( "file_upgrade_step12" ); // NOI18N
                setProgress( 95 );
                convObj_.save();

                messages_.add( Long.toString( count_ ) +
                               " " + // NOI18N
                               XTCEFunctions.getText( "file_upgrade_count" ) ); // NOI18N

            } catch ( Exception ex ) {

                messages_.add( XTCEFunctions.getText( "general_error_caps" ) + // NOI18N
                               " " + // NOI18N
                               ex.getLocalizedMessage() );
                convObj_ = null;

            }

            step_ = XTCEFunctions.getText( "file_upgrade_finish" ); // NOI18N
            setProgress( 100 );

            return null;

        }

        /** Method called by the event dispatching thread when the task is
         * finished.
         *
         */

        @Override
        protected void done() {

            setCursor( null ); //turn off the wait cursor

            if ( convObj_ != null ) {
                messages_.addAll( convObj_.getMessages() );
                messages_.add( XTCEFunctions.getText( "file_upgrade_done" ) + // NOI18N
                               ": " + // NOI18N
                               convObj_.getFilename().getAbsolutePath() );
            }

        }

        /** Retrieve the File that was saved after the compression.
         *
         * @return File containing the saved file or null in the event that the
         * file has not yet been saved.
         *
         */

        private File getSavedFile() {

            if ( ( convObj_ != null ) && ( getProgress() == 100 ) ) {
                return convObj_.getFilename();
            } else {
                return null;
            }

        }

        // Private Data Members

        private       XTCEDatabaseCompressor convObj_;
        private final Properties             options_;
        private final File                   xmlFile_;
        private final List<String>           messages_;
        private       String                 step_;
        private       long                   count_;

    } // End of CompressFileTask class definition

    // Private Data Members

    private final CompressFileTask task;

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        progressBar = new javax.swing.JProgressBar();
        progressText = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Progress");
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(500, 100));
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        setName("dialogMonitorWindow"); // NOI18N
        setResizable(false);

        progressText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        progressText.setText("Initializing");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(progressText)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressText)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel progressText;
    // End of variables declaration//GEN-END:variables

}