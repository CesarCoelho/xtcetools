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

import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;
import org.xtce.toolkit.XTCEContainerContentEntry;
import org.xtce.toolkit.XTCEContainerContentEntry.FieldType;
import org.xtce.toolkit.XTCEContainerEntryValue;
import org.xtce.toolkit.XTCEFunctions;
import org.xtce.toolkit.XTCEItemValue;

/** This is the JPanel that is used inside the scroll region in the
 * XTCEViewerContainerContentDialog class and also the variation class
 * XTCEViewerContainerEncodingDialog.
 *
 * Two constructors are used with this.  The decode class uses this in a much
 * simpler way and does not need the extra two arguments on the second larger
 * constructor.
 *
 * @author dovereem
 *
 */

public class XTCEViewerContainerContentRow extends javax.swing.JPanel {

    /** Constructor to create a new JPanel to display information about a
     * container content entry.
     *
     * @param entry XTCEContainerContentEntry from the processed container
     * content list.
     *
     * @param showAllNamespaces boolean indicating if the user preference to
     * show all alias namespace is set.  This is used for the first column that
     * displays the name of the item.
     *
     * @param showNamespaces boolean indicating if namespace names should be
     * shown per the user preference.  This is used for the first column that
     * displays the name of the item.
     *
     * @param preferredNamespace String containing the user preferred namespace
     * name, if there is one, based on the user preference.  This is used for
     * the first column that displays the name of the item.
     *
     */

    public XTCEViewerContainerContentRow( XTCEContainerContentEntry entry,
                                          boolean                   showAllNamespaces,
                                          boolean                   showNamespaces,
                                          String                    preferredNamespace ) {

        initComponents();

        dialog_ = null;
        entry_  = entry;
        values_ = null;

        drawRow( showAllNamespaces, showNamespaces, preferredNamespace );

    }

    /** Constructor to create a new JPanel to display information about a
     * container content entry where the row is editable.
     *
     * @param entry XTCEContainerContentEntry from the processed container
     * content list.
     *
     * @param showAllNamespaces boolean indicating if the user preference to
     * show all alias namespace is set.  This is used for the first column that
     * displays the name of the item.
     *
     * @param showNamespaces boolean indicating if namespace names should be
     * shown per the user preference.  This is used for the first column that
     * displays the name of the item.
     *
     * @param preferredNamespace String containing the user preferred namespace
     * name, if there is one, based on the user preference.  This is used for
     * the first column that displays the name of the item.
     *
     * @param dialog XTCEViewerContainerEncodingDialog parent used for callback
     * on value change.
     *
     * @param values List of XTCEContainerEntryValue containing the values that
     * are being updated for this container content.
     *
     */

    public XTCEViewerContainerContentRow( XTCEContainerContentEntry         entry,
                                          boolean                           showAllNamespaces,
                                          boolean                           showNamespaces,
                                          String                            preferredNamespace,
                                          XTCEViewerContainerEncodingDialog dialog,
                                          List<XTCEContainerEntryValue>     values ) {

        initComponents();

        dialog_ = dialog;
        entry_  = entry;
        values_ = values;

        drawRow( showAllNamespaces, showNamespaces, preferredNamespace );

    }

    private void drawRow( final boolean showAllNamespaces,
                          final boolean showNamespaces,
                          final String  preferredNamespace ) {

        XTCEContainerEntryValue valueObj = entry_.getValue();

        if ( entry_.getEntryType() == FieldType.CONSTANT ) {

            calValueField.setText( valueObj.getCalibratedValue() );

        } else if ( valueObj != null ) {

            updateFromValueObj( valueObj );

        } else if ( entry_.getEntryType() == FieldType.PARAMETER ) {

            String initialValue = entry_.getParameter().getInitialValue();

            if ( initialValue.isEmpty() == false ) {
                XTCEContainerEntryValue value =
                    new XTCEContainerEntryValue( entry_.getParameter(),
                                                 initialValue,
                                                 "==", // NOI18N
                                                 "Calibrated" ); // NOI18N
                updateFromValueObj( value );
            }

        } else if ( entry_.getEntryType() == FieldType.ARGUMENT ) {

            String initialValue = entry_.getArgument().getInitialValue();

            if ( initialValue.isEmpty() == false ) {
                XTCEContainerEntryValue value =
                    new XTCEContainerEntryValue( entry_.getArgument(),
                                                 initialValue,
                                                 "==", // NOI18N
                                                 "Calibrated" ); // NOI18N
                updateFromValueObj( value );
            }

        }

        if ( dialog_ == null ) {
            calValueField.setEditable( false );
            uncalValueField.setEditable( false );
            rawValueField.setEditable( false );
        } else {
            if ( entry_.getEntryType() != FieldType.CONSTANT ) {
                calValueField.setEditable( true );
                uncalValueField.setEditable( true );
                rawValueField.setEditable( true );
            } else {
                calValueField.setEditable( false );
                uncalValueField.setEditable( false );
                rawValueField.setEditable( false );
                uncalValueField.setEnabled( false );
                rawValueField.setEnabled( false );
            }
        }

        populateDisplayName( showAllNamespaces,
                             showNamespaces,
                             preferredNamespace );

        itemNameField.setCaretPosition( 0 );
        calValueField.setCaretPosition( 0 );
        uncalValueField.setCaretPosition( 0 );
        rawValueField.setCaretPosition( 0 );

    }

    private int getSizeInBytes( XTCEContainerContentEntry entry ) {

        String bitSize  = entry.getRawSizeInBits();
        int    byteSize = 0;

        if ( bitSize.isEmpty() == false ) {
            try {
                int bits = Integer.parseInt( bitSize );
                byteSize = bits / 8;
                if ( bits % 8 != 0 ) {
                    byteSize += 1;
                }
            } catch ( Exception ex ) {
                // do nothing
            }
        }

        return byteSize;

    }

    private void updateFromValueObj( final XTCEContainerEntryValue valueObj ) {

        int byteSize = getSizeInBytes( entry_ );

        calValueField.setText( valueObj.getCalibratedValue() );
        uncalValueField.setText( valueObj.getUncalibratedValue() );

        if ( byteSize != 0 ) {
            BitSet rawBits = valueObj.getRawValue();
            rawValueField.setText(
                XTCEFunctions.bitSetToHex( rawBits, byteSize ) );
        }

    }

    private void populateDisplayName( final boolean showAllNamespaces,
                                      final boolean showNamespaces,
                                      final String  preferredNamespace ) {

        String displayName = entry_.getName();
        String aliasString = "";

        if ( entry_.getEntryType() == FieldType.PARAMETER ) {
            aliasString =
                XTCEFunctions.makeAliasDisplayString( entry_.getParameter(),
                                                      showAllNamespaces,
                                                      showNamespaces,
                                                      preferredNamespace );
        } else if ( entry_.getEntryType() == FieldType.ARGUMENT ) {
            aliasString =
                XTCEFunctions.makeAliasDisplayString( entry_.getArgument(),
                                                      showAllNamespaces,
                                                      showNamespaces,
                                                      preferredNamespace );
        } else if ( entry_.getEntryType() == FieldType.CONSTANT ) {
            displayName = "Fixed Value"; // NOI18N
        }

        if ( aliasString.isEmpty() == true ) {
            itemNameField.setText( displayName );
        } else {
            itemNameField.setText( displayName + " (" + aliasString + ")" ); // NOI18N
        }

        itemNameField.setEditable( false );

    }

    private void updateValueList( final XTCEContainerEntryValue value ) {

        boolean found = false;

        for ( int iii = 0; iii < values_.size(); ++iii ) {
            if ( values_.get( iii ).getItemFullPath().equals( entry_.getItemFullPath() ) == true ) {
                values_.set( iii, value );
                found = true;
                break;
            }
        }

        if ( found == false ) {
            values_.add( value );
        }

        dialog_.repopulate();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        itemNameField = new javax.swing.JTextField();
        calValueField = new javax.swing.JTextField();
        uncalValueField = new javax.swing.JTextField();
        rawValueField = new javax.swing.JTextField();

        itemNameField.setEditable(false);

        calValueField.setEditable(false);
        calValueField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calValueFieldActionPerformed(evt);
            }
        });
        calValueField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                calValueFieldFocusLost(evt);
            }
        });

        uncalValueField.setEditable(false);
        uncalValueField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uncalValueFieldActionPerformed(evt);
            }
        });
        uncalValueField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                uncalValueFieldFocusLost(evt);
            }
        });

        rawValueField.setEditable(false);
        rawValueField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rawValueFieldActionPerformed(evt);
            }
        });
        rawValueField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                rawValueFieldFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(itemNameField, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calValueField, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(uncalValueField, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rawValueField, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(itemNameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(calValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(uncalValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(rawValueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void calValueFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_calValueFieldFocusLost

        if ( evt.isTemporary() == false ) {
            calValueFieldActionPerformed( null );
        }

    }//GEN-LAST:event_calValueFieldFocusLost

    private void uncalValueFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_uncalValueFieldFocusLost

        if ( evt.isTemporary() == false ) {
            uncalValueFieldActionPerformed( null );
        }

    }//GEN-LAST:event_uncalValueFieldFocusLost

    private void rawValueFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_rawValueFieldFocusLost

        if ( evt.isTemporary() == false ) {
            rawValueFieldActionPerformed( null );
        }

    }//GEN-LAST:event_rawValueFieldFocusLost

    private void calValueFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calValueFieldActionPerformed

        if ( dialog_ == null ) {
            return;
        }

        XTCEContainerEntryValue value;

        if ( entry_.getEntryType() == FieldType.PARAMETER ) {
            value = new XTCEContainerEntryValue( entry_.getParameter(),
                                                 calValueField.getText(),
                                                 "==", // NOI18N
                                                 "Calibrated" ); // NOI18N
        } else if ( entry_.getEntryType() == FieldType.ARGUMENT ) {
            value = new XTCEContainerEntryValue( entry_.getArgument(),
                                                 calValueField.getText(),
                                                 "==", // NOI18N
                                                 "Calibrated" ); // NOI18N
        } else {
            return;
        }

        updateValueList( value );

    }//GEN-LAST:event_calValueFieldActionPerformed

    private void uncalValueFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uncalValueFieldActionPerformed

        if ( dialog_ == null ) {
            return;
        }

        XTCEContainerEntryValue value;

        if ( entry_.getEntryType() == FieldType.PARAMETER ) {
            value = new XTCEContainerEntryValue( entry_.getParameter(),
                                                 uncalValueField.getText(),
                                                 "==", // NOI18N
                                                 "Uncalibrated" ); // NOI18N
        } else if ( entry_.getEntryType() == FieldType.ARGUMENT ) {
            value = new XTCEContainerEntryValue( entry_.getArgument(),
                                                 uncalValueField.getText(),
                                                 "==", // NOI18N
                                                 "Uncalibrated" ); // NOI18N
        } else {
            return;
        }

        updateValueList( value );

    }//GEN-LAST:event_uncalValueFieldActionPerformed

    private void rawValueFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rawValueFieldActionPerformed

        if ( dialog_ == null ) {
            return;
        }

        String rawValue = rawValueField.getText();

        XTCEContainerEntryValue value;

        if ( entry_.getEntryType() == FieldType.PARAMETER ) {
            XTCEItemValue valueObj = new XTCEItemValue( entry_.getParameter() );
            BigInteger rawInteger = valueObj.integerStringToBigInteger( rawValue );
            BitSet     bits       = valueObj.encodeRawBits( rawInteger );
            String     uncalValue = valueObj.getUncalibratedFromRaw( bits );
            value = new XTCEContainerEntryValue( entry_.getParameter(),
                                                 uncalValue,
                                                 "==", // NOI18N
                                                 "Uncalibrated" ); // NOI18N
        } else if ( entry_.getEntryType() == FieldType.ARGUMENT ) {
            XTCEItemValue valueObj = new XTCEItemValue( entry_.getArgument() );
            BigInteger rawInteger = valueObj.integerStringToBigInteger( rawValue );
            BitSet     bits       = valueObj.encodeRawBits( rawInteger );
            String     uncalValue = valueObj.getUncalibratedFromRaw( bits );
            value = new XTCEContainerEntryValue( entry_.getArgument(),
                                                 uncalValue,
                                                 "==", // NOI18N
                                                 "Uncalibrated" ); // NOI18N
        } else {
            return;
        }

        updateValueList( value );

    }//GEN-LAST:event_rawValueFieldActionPerformed

    // Private Data Members

    private final XTCEViewerContainerEncodingDialog dialog_;
    private final XTCEContainerContentEntry         entry_;
    private final List<XTCEContainerEntryValue>     values_;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField calValueField;
    private javax.swing.JTextField itemNameField;
    private javax.swing.JTextField rawValueField;
    private javax.swing.JTextField uncalValueField;
    // End of variables declaration//GEN-END:variables

}
