/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.ui;

import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.omg.space.xtce.toolkit.XTCEFunctions;

/**
 *
 * @author dovereem
 */

public class XTCEViewerOpenFileChooser extends JFileChooser {

    /** This dialog creates the File Chooser for XTCE documents to support the
     * XTCEViewer application.
     *
     * @param prefs XTCEViewerPreferences object containing the current user
     * preferences.
     *
     */

    XTCEViewerOpenFileChooser( XTCEViewerPreferences prefs ) {

        super( "." );

        xincludeCheckBox.setSelected( prefs.getUseXIncludeOption() );
        validateCheckBox.setSelected( prefs.getValidateOnLoadOption() );
        readOnlyCheckBox.setSelected( true );

        JPanel accessory = new JPanel();
        accessory.setLayout( new BoxLayout( accessory, BoxLayout.PAGE_AXIS ) );
        accessory.add( xincludeCheckBox );
        accessory.add( validateCheckBox );
        accessory.add( readOnlyCheckBox );
        setAccessory( accessory );

        FileFilter fileFilter =
            new FileNameExtensionFilter( XTCEFunctions.getText( "file_chooser_xtcexml_text" ), // NOI18N
                                         "xml" ); // NOI18N
        addChoosableFileFilter( fileFilter );
        setFileFilter( fileFilter ); // Initial filter setting

        if ( prefs.getCurrentWorkingDirectory().isEmpty() == false ) {
            setCurrentDirectory( new File( prefs.getCurrentWorkingDirectory() ) );
        }

    }

    /** Retrieve the selection state of the option to use XInclude when opening
     * the requested file.
     *
     * @return Boolean indicating of XInclude should be used.
     *
     */

    public boolean isXIncludeSelected() {
        return xincludeCheckBox.isSelected();
    }

    /** Retrieve the selection state of the option to validate the XTCE
     * document when loading it using the XTCE schema description (XSD).
     *
     * @return Boolean indicating if XSD validation should be performed.
     *
     */

    public boolean isValidateSelected() {
        return validateCheckBox.isSelected();
    }

    /** Retrieve the selection state of the option to open the XTCE document in
     * read-only mode, which is much faster.
     *
     * @return boolean indicating if the document is read-only
     *
     */

    public boolean isReadOnlySelected() {
        return readOnlyCheckBox.isSelected();
    }

    // Private Data Members

    private JCheckBox xincludeCheckBox =
        new JCheckBox( XTCEFunctions.getText( "file_chooser_xinclude_text" ) ); // NOI18N

    private JCheckBox validateCheckBox =
        new JCheckBox( XTCEFunctions.getText( "file_chooser_validate_text" ) ); // NOI18N

    private JCheckBox readOnlyCheckBox =
        new JCheckBox( XTCEFunctions.getText( "file_chooser_readonly_text" ) ); // NOI18N

}
