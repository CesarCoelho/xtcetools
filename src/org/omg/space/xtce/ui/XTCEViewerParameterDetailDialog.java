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

package org.omg.space.xtce.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import org.omg.space.xtce.toolkit.XTCEDatabaseException;
import org.omg.space.xtce.toolkit.XTCEParameter;
import org.omg.space.xtce.toolkit.XTCEAlias;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.math.BigInteger;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.omg.space.xtce.database.CalibratorType;
import org.omg.space.xtce.database.CalibratorType.SplineCalibrator;
import org.omg.space.xtce.database.DescriptionType;
import org.omg.space.xtce.database.DescriptionType.AncillaryDataSet.AncillaryData;
import org.omg.space.xtce.database.ParameterTypeSetType.BooleanParameterType;
import org.omg.space.xtce.database.PolynomialType;
import org.omg.space.xtce.database.SplinePointType;
import org.omg.space.xtce.database.ValueEnumerationType;
import org.omg.space.xtce.toolkit.XTCEFunctions;
import org.omg.space.xtce.toolkit.XTCETypedObject;
import org.omg.space.xtce.toolkit.XTCETypedObject.EngineeringType;
import org.omg.space.xtce.toolkit.XTCEValidRange;
//import org.scilab.forge.jlatexmath.TeXConstants;
//import org.scilab.forge.jlatexmath.TeXFormula;
//import org.scilab.forge.jlatexmath.TeXIcon;

/**
 *
 * @author David Overeem
 *
 */

public class XTCEViewerParameterDetailDialog extends javax.swing.JDialog {

    /**
     * Creates new form XTCEViewerParameterDetailDialog
     */
    public XTCEViewerParameterDetailDialog(java.awt.Frame parent, boolean modal, XTCEParameter parameter ) throws XTCEDatabaseException {
        super(parent, modal);
        initComponents();
        if ( parameter == null ) {
            throw new XTCEDatabaseException( "test" ); // NOI18N
        }
        parameter_ = parameter;
        nameTextField.setText( parameter.getName() );
        nameTextField.setCaretPosition( 0 );
        List<XTCEAlias> aliases = parameter.getAliasSet();
        for ( int iii = 0; iii < aliases.size(); ++iii ) {
            aliasTextField.append( aliases.get( iii ).getFullAliasName() );
            if ( iii < ( aliases.size() - 1 ) ) {
                aliasTextField.append( "\n" ); // NOI18N
            }
        }
        aliasTextField.setCaretPosition( 0 );
        unitsTextField.setText( parameter.getUnits() );
        unitsTextField.setCaretPosition( 0 );
        systemNameTextField.setText( parameter.getSystemName() );
        sourceComboField.setSelectedItem( parameter.getDataSource() );
        if ( parameter.getDataSource().equals( "constant" ) == true ) { // NOI18N
            readOnlyCheckboxField.setSelected( true );
            readOnlyCheckboxField.setEnabled( false );
        } else {
            readOnlyCheckboxField.setSelected( parameter.isReadOnly() );
        }
        shortDescriptionField.setText( parameter.getShortDescription() );
        shortDescriptionField.setCaretPosition( 0 );
        longDescriptionField.setText( parameter.getLongDescription() );
        longDescriptionField.setCaretPosition( 0 );
        engineeringTypeComboField.setSelectedItem( parameter.getEngineeringTypeString() );
        if ( parameter.getRawTypeString().isEmpty() == true ) {
            encodingTypeComboField.setSelectedItem( "" );
            encodingTypeComboField.setEnabled( false );
            rawSizeTextField.setEnabled( false );
            bitOrderComboField.setSelectedItem( "" );
            bitOrderComboField.setEnabled( false );
            changeThresholdTextField.setEnabled( false );
        } else {
            encodingTypeComboField.setSelectedItem( parameter.getRawTypeString() );
            rawSizeTextField.setText( parameter.getRawSizeInBits() );
            bitOrderComboField.setSelectedItem( parameter.getRawBitOrder() );
            changeThresholdTextField.setText( parameter.getChangeThreshold() );
        }
        if ( ( parameter.getEngineeringType() == EngineeringType.ARRAY     ) ||
             ( parameter.getEngineeringType() == EngineeringType.STRUCTURE ) ) {
            defaultValueTextField.setEnabled( false );
        } else {
            defaultValueTextField.setText( parameter.getInitialValue() );
        }
        if ( parameter.getTypeReference() != null ) {
            parameterTypeReferenceText.setText( parameter.getTypeReferenceFullPath() );
            xtceTypeNameTextField.setText( parameter.getTypeReference().getName() );
            writeEnumerationTable( hexCheckbox.isSelected() );
            writePolynomials();
            writeSplines();
            writeValidRange();
        } else {
            parameterTypeReferenceText.setText( "Parameter ERROR: Broken Reference: " + parameter.getTypeReferenceFullPath() );
            xtceTypeNameTextField.setText( "INVALID" );
        }
        writeAncillaryData();
        parameterTypeReferenceText.setCaretPosition( 0 );
        xtceTypeNameTextField.setCaretPosition( 0 );
        pack();
        setLocationRelativeTo( parent );
    }

    private void writeEnumerationTable( boolean hex ) {

        if ( parameter_.getEngineeringType() == EngineeringType.ENUMERATED ) {
            DefaultTableModel tableModel = (DefaultTableModel)enumerationTable.getModel();
            tableModel.setRowCount( 0 );
            List<ValueEnumerationType> enumerations = parameter_.getEnumerations();
            for ( ValueEnumerationType valueEnum : enumerations ) {
                String value = null;
                if ( hex == true ) {
                    value = "0x" + valueEnum.getValue().toString( 16 );
                } else {
                    value = valueEnum.getValue().toString();
                }
                if ( valueEnum.getMaxValue() != null ) {
                    if ( hex == true ) {
                        value = value + " - 0x" + valueEnum.getMaxValue().toString( 16 );
                    } else {
                        value = value + " - " + valueEnum.getMaxValue().toString();
                    }
                }
                Object rowData[] = { valueEnum.getLabel(),
                                     value,
                                     valueEnum.getShortDescription() };
                tableModel.addRow( rowData );
            }
        } else if ( parameter_.getEngineeringType() == EngineeringType.BOOLEAN ) {
            DefaultTableModel tableModel = (DefaultTableModel)enumerationTable.getModel();
            tableModel.setRowCount( 0 );
            String values[] = { ((BooleanParameterType)parameter_.getTypeReference()).getZeroStringValue(),
                                ((BooleanParameterType)parameter_.getTypeReference()).getOneStringValue() };
            for ( int iii = 0; iii < 2; ++iii ) {
                String value = "";
                if ( hex == true ) {
                    value = "0x" + Integer.toString( iii, 16 );
                } else {
                    value = Integer.toString( iii );
                }
                Object rowData[] = { values[iii],
                                     value,
                                     "Boolean" };
                tableModel.addRow( rowData );
            }
        } else {
            discardNamedTab( "Enumerations" );
        }

    }

    private boolean validateEnumeration( List<ValueEnumerationType> enumerations,
                                         int                        indexToOmit,
                                         String                     label,
                                         String                     value,
                                         String                     maxValue ) {

        if ( ( label == null ) || ( label.isEmpty() == true ) ) {
            JOptionPane.showMessageDialog( this,
                                           "Cannot have an empty enumeration label",
                                           XTCEFunctions.getText( "general_error" ),
                                           JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if ( ( value == null ) || ( value.isEmpty() == true ) ) {
            JOptionPane.showMessageDialog( this,
                                           "Cannot have an empty enumeration value",
                                           XTCEFunctions.getText( "general_error" ),
                                           JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Integer candidate = Integer.parseInt( value );
            String rawEncoding = parameter_.getRawTypeString();
            if ( ( candidate < 0 ) && ( rawEncoding.equals( "unsigned" ) == true ) ) {
                JOptionPane.showMessageDialog( this,
                                               "Negative value for enumeration specified on an unsigned parameter",
                                               XTCEFunctions.getText( "general_error" ),
                                               JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch ( NumberFormatException ex ) {
            JOptionPane.showMessageDialog( this,
                                           "Invalid numeric value of " + value,
                                           XTCEFunctions.getText( "general_error" ),
                                           JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if ( ( maxValue != null ) && ( maxValue.isEmpty() == false ) ) {
            try {
                Integer candidate = Integer.parseInt( value );
                String rawEncoding = parameter_.getRawTypeString();
                if ( ( candidate < 0 ) && ( rawEncoding.equals( "unsigned" ) == true ) ) {
                    JOptionPane.showMessageDialog( this,
                                                   "Negative maximum value for enumeration specified on an unsigned parameter",
                                                   XTCEFunctions.getText( "general_error" ),
                                                   JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch ( NumberFormatException ex ) {
                JOptionPane.showMessageDialog( this,
                                               "Invalid numeric maximum value of " + value,
                                               XTCEFunctions.getText( "general_error" ),
                                               JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        for ( int iii = 0; iii < enumerations.size(); ++iii ) {
            if ( indexToOmit != iii ) {
                if ( enumerations.get( iii ).getLabel().equals( label ) == true ) {
                    JOptionPane.showMessageDialog( this,
                                                   "Cannot edit an enumeration to cause a duplicate label",
                                                   XTCEFunctions.getText( "general_error" ),
                                                   JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                /// @todo test for numeric range conflict
            }
        }

        return true;

    }

    private void writePolynomials() {

        CalibratorType defaultCalibrator = parameter_.getDefaultCalibrator();
        PolynomialType polyCal           = null;

        if ( defaultCalibrator == null ) {
            polyCal = new PolynomialType();
        } else {
            polyCal = defaultCalibrator.getPolynomialCalibrator();
        }

        if ( polyCal == null ) {
            polyCal = new PolynomialType();
        }

        List<PolynomialType.Term> terms = polyCal.getTerm();
        if ( terms.isEmpty() ) {
            coefficient0Text.setText( "0" );
            coefficient1Text.setText( "1" );
            coefficient2Text.setText( "" );
            coefficient3Text.setText( "" );
            coefficient4Text.setText( "" );
            coefficient5Text.setText( "" );
            coefficient6Text.setText( "" );
            coefficient7Text.setText( "" );
            //return;
        }

        double[] exponentArray = { 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

        for ( PolynomialType.Term polyTerm : terms ) {
            if ( polyTerm.getExponent().intValue() == 0 ) {
                coefficient0Text.setText( Double.toString( polyTerm.getCoefficient() ) );
                exponentArray[0] = polyTerm.getCoefficient();
            } else if ( polyTerm.getExponent().intValue() == 1 ) {
                coefficient1Text.setText( Double.toString( polyTerm.getCoefficient() ) );
                exponentArray[1] = polyTerm.getCoefficient();
            } else if ( polyTerm.getExponent().intValue() == 2 ) {
                coefficient2Text.setText( Double.toString( polyTerm.getCoefficient() ) );
                exponentArray[2] = polyTerm.getCoefficient();
            } else if ( polyTerm.getExponent().intValue() == 3 ) {
                coefficient3Text.setText( Double.toString( polyTerm.getCoefficient() ) );
                exponentArray[3] = polyTerm.getCoefficient();
            } else if ( polyTerm.getExponent().intValue() == 4 ) {
                coefficient4Text.setText( Double.toString( polyTerm.getCoefficient() ) );
                exponentArray[4] = polyTerm.getCoefficient();
            } else if ( polyTerm.getExponent().intValue() == 5 ) {
                coefficient5Text.setText( Double.toString( polyTerm.getCoefficient() ) );
                exponentArray[5] = polyTerm.getCoefficient();
            } else if ( polyTerm.getExponent().intValue() == 6 ) {
                coefficient6Text.setText( Double.toString( polyTerm.getCoefficient() ) );
                exponentArray[6] = polyTerm.getCoefficient();
            } else if ( polyTerm.getExponent().intValue() == 7 ) {
                coefficient7Text.setText( Double.toString( polyTerm.getCoefficient() ) );
                exponentArray[7] = polyTerm.getCoefficient();
            } else {
                JOptionPane.showMessageDialog( this,
                                               "Exponent of " + polyTerm.getExponent().toString() + " too big for the current display",
                                               XTCEFunctions.getText( "general_warning" ),
                                               JOptionPane.WARNING_MESSAGE);
            }
        }

        StringBuilder latexExpressionBuilder = new StringBuilder( "y={" );
        for ( int iii = 0; iii < exponentArray.length; ++iii ) {
            if ( ( iii == 0 ) && ( exponentArray[iii] != 0.0 ) ) {
                latexExpressionBuilder.append( Double.toString( exponentArray[iii] ) );
                latexExpressionBuilder.append( "+" );
            } else if ( ( iii == 1 ) && ( exponentArray[iii] != 0.0 ) ) {
                latexExpressionBuilder.append( Double.toString( exponentArray[iii] ) );
                latexExpressionBuilder.append( "x" );
                latexExpressionBuilder.append( "+" );
            } else if ( exponentArray[iii] != 0.0 ) {
                latexExpressionBuilder.append( Double.toString( exponentArray[iii] ) );
                latexExpressionBuilder.append( "x^" );
                latexExpressionBuilder.append( Integer.toString( iii ) );
                latexExpressionBuilder.append( "+" );
            }
        }
        /*
        latexExpressionBuilder = latexExpressionBuilder.deleteCharAt( latexExpressionBuilder.length() - 1 );
        latexExpressionBuilder.append( "}" );

        System.out.println( latexExpressionBuilder.toString() );

        TeXFormula formula = new TeXFormula( latexExpressionBuilder.toString() );
        TeXIcon icon = formula.createTeXIcon( TeXConstants.STYLE_DISPLAY, 20 );
        icon.setInsets( new Insets( 5, 5, 5, 5 ) );
        BufferedImage image = new BufferedImage( icon.getIconWidth(),
                                                 icon.getIconHeight(),
                                                 BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = image.createGraphics();
        g2.setColor( Color.white );
        g2.fillRect( 0, 0, icon.getIconWidth(), icon.getIconHeight() );
        JLabel jl = new JLabel();
        jl.setForeground( new Color(0, 0, 0) );
        icon.paintIcon( jl, g2, 0, 0 );
        latexDrawingPanel.add( jl );
        Graphics ggg = latexDrawingPanel.getGraphics();
        ggg.drawImage( image, 0, 0, null );
        latexDrawingPanel.revalidate();
        latexDrawingPanel.repaint();
        */

    }

    private void writeSplines() {

        DefaultTableModel tableModel = (DefaultTableModel)splineTable.getModel();
        tableModel.setRowCount( 0 );

        CalibratorType   defaultCalibrator = parameter_.getDefaultCalibrator();
        SplineCalibrator splineCal         = null;

        if ( defaultCalibrator == null ) {
            splineCal = new SplineCalibrator();
        } else {
            splineCal = defaultCalibrator.getSplineCalibrator();
        }

        if ( splineCal == null ) {
            splineCal = new SplineCalibrator();
        }

        List<SplinePointType> points = splineCal.getSplinePoint();

        for ( SplinePointType point : points ) {
            Object rowData[] = { point.getRaw(),
                                 point.getCalibrated() };
            tableModel.addRow( rowData );
        }

        //System.out.println( "Spline Order: " + splineCal.getOrder().toString() );
        //System.out.println( "Spline Extrapolate: " + ( splineCal.isExtrapolate() ? "true" : "false" ) );

    }

    private void writeValidRange() {

        XTCEValidRange rangeObj = parameter_.getValidRange();
        if ( rangeObj.isValidRangeApplied() == true ) {
            rangeLowTextField.setText( rangeObj.getLowValue() );
            rangeHighTextField.setText( rangeObj.getHighValue() );
            rangeLowInclusiveCheckbox.setSelected( rangeObj.isLowValueInclusive() );
            rangeHighInclusiveCheckbox.setSelected( rangeObj.isHighValueInclusive() );
            if ( rangeObj.isLowValueCalibrated() == true ) {
                rangeAppliesToComboBox.setSelectedItem( "Calibrated" );
            } else {
                rangeAppliesToComboBox.setSelectedItem( "Uncalibrated" );
            }
        } else {
            rangeLowTextField.setEnabled( false );
            rangeHighTextField.setEnabled( false );
            rangeLowInclusiveCheckbox.setEnabled( false );
            rangeHighInclusiveCheckbox.setEnabled( false );
            rangeAppliesToComboBox.setSelectedItem( "" );
            rangeAppliesToComboBox.setEnabled( false );
        }

    }

    private void writeAncillaryData() {

        List<AncillaryData> ancDataList = parameter_.getAncillaryData();

        JPanel entryPanel = new JPanel();
        entryPanel.setLayout( new BoxLayout( entryPanel, BoxLayout.Y_AXIS ) );

        for ( AncillaryData ancData : ancDataList ) {
            entryPanel.add( new XTCEViewerAncillaryDataRow( ancData ) );
        }

        ancillaryDataScrollPane.setViewportView( entryPanel );
        ancillaryDataScrollPane.revalidate();
        ancillaryDataScrollPane.repaint();

    }

    private void discardNamedTab( String tabName ) {
        
        int tabIndex = extraDetailsTabbedPane.indexOfTab( tabName );
        if ( tabIndex != -1 ) {
            extraDetailsTabbedPane.remove( tabIndex );
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        enumerationEditorPanel = new javax.swing.JPanel();
        enumLabelLabel = new javax.swing.JLabel();
        enumValueLabel = new javax.swing.JLabel();
        enumMaxValueLabel = new javax.swing.JLabel();
        descriptionLabel = new javax.swing.JLabel();
        labelText = new javax.swing.JTextField();
        maxValueText = new javax.swing.JTextField();
        valueText = new javax.swing.JTextField();
        descriptionTextScrollPane = new javax.swing.JScrollPane();
        descriptionText = new javax.swing.JTextArea();
        enumerationTablePopupMenu = new javax.swing.JPopupMenu();
        copyEnumerationCellMenuItem = new javax.swing.JMenuItem();
        copyEnumerationColumnMenuItem = new javax.swing.JMenuItem();
        copyEnumerationRowMenuItem = new javax.swing.JMenuItem();
        copyEnumerationTableMenuItem = new javax.swing.JMenuItem();
        splineCalibratorTablePopupMenu = new javax.swing.JPopupMenu();
        copySplineCellMenuItem = new javax.swing.JMenuItem();
        copySplineColumnMenuItem = new javax.swing.JMenuItem();
        copySplineRowMenuItem = new javax.swing.JMenuItem();
        copySplineTableMenuItem = new javax.swing.JMenuItem();
        editButtonsPanel = new javax.swing.JPanel();
        editButton = new javax.swing.JButton();
        dismissButton = new javax.swing.JButton();
        leftPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        aliasLabel = new javax.swing.JLabel();
        unitsLabel = new javax.swing.JLabel();
        sourceLabel = new javax.swing.JLabel();
        systemNameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        aliasScrollPane = new javax.swing.JScrollPane();
        aliasTextField = new javax.swing.JTextArea();
        unitsScrollPane = new javax.swing.JScrollPane();
        unitsTextField = new javax.swing.JTextArea();
        sourceComboField = new javax.swing.JComboBox();
        readOnlyCheckboxField = new javax.swing.JCheckBox();
        parameterDescriptionLabel = new javax.swing.JLabel();
        encodingInformationLabel = new javax.swing.JLabel();
        shortDescriptionLabel = new javax.swing.JLabel();
        shortDescriptionField = new javax.swing.JTextField();
        longDescriptionLabel = new javax.swing.JLabel();
        leftScrollPane = new javax.swing.JScrollPane();
        longDescriptionField = new javax.swing.JTextArea();
        systemNameTextField = new javax.swing.JTextField();
        engineeringTypeLabel = new javax.swing.JLabel();
        encodingTypeLabel = new javax.swing.JLabel();
        encodingSizeLabel = new javax.swing.JLabel();
        typeNameLabel = new javax.swing.JLabel();
        bitOrderLabel = new javax.swing.JLabel();
        changeThresholdLabel = new javax.swing.JLabel();
        defaultValueLabel = new javax.swing.JLabel();
        lowRangeLabel = new javax.swing.JLabel();
        highRangeLabel = new javax.swing.JLabel();
        rangeScopeLabel = new javax.swing.JLabel();
        engineeringTypeComboField = new javax.swing.JComboBox();
        encodingTypeComboField = new javax.swing.JComboBox();
        rawSizeTextField = new javax.swing.JTextField();
        xtceTypeNameTextField = new javax.swing.JTextField();
        bitOrderComboField = new javax.swing.JComboBox();
        changeThresholdTextField = new javax.swing.JTextField();
        defaultValueTextField = new javax.swing.JTextField();
        rangeLowTextField = new javax.swing.JTextField();
        rangeHighTextField = new javax.swing.JTextField();
        rangeAppliesToComboBox = new javax.swing.JComboBox();
        rightPanel = new javax.swing.JPanel();
        extraDetailsTabbedPane = new javax.swing.JTabbedPane();
        enumerationConversionTab = new javax.swing.JPanel();
        enumerationPanel = new javax.swing.JPanel();
        enumerationLabel = new javax.swing.JLabel();
        enumerationScrollPane = new javax.swing.JScrollPane();
        enumerationTable = new javax.swing.JTable();
        removeEnumerationButton = new javax.swing.JButton();
        editEnumerationButton = new javax.swing.JButton();
        addEnumerationButton = new javax.swing.JButton();
        hexCheckbox = new javax.swing.JCheckBox();
        polynomialCalibratorsTab = new javax.swing.JPanel();
        polynomialCalibratorsPanel = new javax.swing.JPanel();
        polynomialCalibratorsLabel = new javax.swing.JLabel();
        polynomialCalibratorsTabbedPane = new javax.swing.JTabbedPane();
        polynomialCalibratorParentDetailPanel = new javax.swing.JPanel();
        polynomialCalibratorDetailPanel = new javax.swing.JPanel();
        coefficient0Text = new javax.swing.JTextField();
        coefficient1Text = new javax.swing.JTextField();
        coefficient2Text = new javax.swing.JTextField();
        coefficient3Text = new javax.swing.JTextField();
        coefficient4Text = new javax.swing.JTextField();
        coefficient5Text = new javax.swing.JTextField();
        coefficient6Text = new javax.swing.JTextField();
        coefficient7Text = new javax.swing.JTextField();
        coefficient0Label = new javax.swing.JLabel();
        coefficient1Label = new javax.swing.JLabel();
        coefficient2Label = new javax.swing.JLabel();
        coefficient3Label = new javax.swing.JLabel();
        coefficient4Label = new javax.swing.JLabel();
        coefficient5Label = new javax.swing.JLabel();
        coefficient6Label = new javax.swing.JLabel();
        coefficient7Label = new javax.swing.JLabel();
        latexDrawingPanel = new javax.swing.JPanel();
        splineCalibratorsTab = new javax.swing.JPanel();
        splineCalibratorsPanel = new javax.swing.JPanel();
        splineLabel = new javax.swing.JLabel();
        splineCalibratorsTabbedPane = new javax.swing.JTabbedPane();
        splineTableScrollPane = new javax.swing.JScrollPane();
        splineTable = new javax.swing.JTable();
        removeSplinePointButton = new javax.swing.JButton();
        editSplinePointButton = new javax.swing.JButton();
        addSplinePointButton = new javax.swing.JButton();
        alarmDefinitionsTab = new javax.swing.JPanel();
        ancillaryDataTab = new javax.swing.JPanel();
        ancillaryDataPanel = new javax.swing.JPanel();
        ancillaryDataLabel = new javax.swing.JLabel();
        ancillaryDataScrollPane = new javax.swing.JScrollPane();
        addAncillaryDataButton = new javax.swing.JButton();
        rangeLowInclusiveCheckbox = new javax.swing.JCheckBox();
        rangeHighInclusiveCheckbox = new javax.swing.JCheckBox();
        typeReferenceNameLabel = new javax.swing.JLabel();
        parameterTypeReferenceText = new javax.swing.JTextField();

        enumLabelLabel.setText("Label");

        enumValueLabel.setText("Value");

        enumMaxValueLabel.setText("to");

        descriptionLabel.setText("Optional Description");

        descriptionText.setColumns(20);
        descriptionText.setRows(5);
        descriptionTextScrollPane.setViewportView(descriptionText);

        javax.swing.GroupLayout enumerationEditorPanelLayout = new javax.swing.GroupLayout(enumerationEditorPanel);
        enumerationEditorPanel.setLayout(enumerationEditorPanelLayout);
        enumerationEditorPanelLayout.setHorizontalGroup(
            enumerationEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enumerationEditorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(enumerationEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(descriptionTextScrollPane)
                    .addGroup(enumerationEditorPanelLayout.createSequentialGroup()
                        .addComponent(enumLabelLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(labelText))
                    .addGroup(enumerationEditorPanelLayout.createSequentialGroup()
                        .addComponent(enumValueLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(valueText, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(enumMaxValueLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addComponent(maxValueText, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(enumerationEditorPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(descriptionLabel)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        enumerationEditorPanelLayout.setVerticalGroup(
            enumerationEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enumerationEditorPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(enumerationEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(enumLabelLabel)
                    .addComponent(labelText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(enumerationEditorPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxValueText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enumValueLabel)
                    .addComponent(valueText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(enumMaxValueLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(descriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(descriptionTextScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/omg/space/xtce/toolkit/MessagesBundle"); // NOI18N
        copyEnumerationCellMenuItem.setText(bundle.getString("general_copy_cell")); // NOI18N
        copyEnumerationCellMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyEnumerationCellMenuItemActionPerformed(evt);
            }
        });
        enumerationTablePopupMenu.add(copyEnumerationCellMenuItem);

        copyEnumerationColumnMenuItem.setText(bundle.getString("general_copy_column")); // NOI18N
        copyEnumerationColumnMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyEnumerationColumnMenuItemActionPerformed(evt);
            }
        });
        enumerationTablePopupMenu.add(copyEnumerationColumnMenuItem);

        copyEnumerationRowMenuItem.setText(bundle.getString("general_copy_row")); // NOI18N
        copyEnumerationRowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyEnumerationRowMenuItemActionPerformed(evt);
            }
        });
        enumerationTablePopupMenu.add(copyEnumerationRowMenuItem);

        copyEnumerationTableMenuItem.setText(bundle.getString("general_copy_table")); // NOI18N
        copyEnumerationTableMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyEnumerationTableMenuItemActionPerformed(evt);
            }
        });
        enumerationTablePopupMenu.add(copyEnumerationTableMenuItem);

        copySplineCellMenuItem.setText(bundle.getString("general_copy_cell")); // NOI18N
        copySplineCellMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copySplineCellMenuItemActionPerformed(evt);
            }
        });
        splineCalibratorTablePopupMenu.add(copySplineCellMenuItem);

        copySplineColumnMenuItem.setText(bundle.getString("general_copy_column")); // NOI18N
        copySplineColumnMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copySplineColumnMenuItemActionPerformed(evt);
            }
        });
        splineCalibratorTablePopupMenu.add(copySplineColumnMenuItem);

        copySplineRowMenuItem.setText(bundle.getString("general_copy_row")); // NOI18N
        copySplineRowMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copySplineRowMenuItemActionPerformed(evt);
            }
        });
        splineCalibratorTablePopupMenu.add(copySplineRowMenuItem);

        copySplineTableMenuItem.setText(bundle.getString("general_copy_table")); // NOI18N
        copySplineTableMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copySplineTableMenuItemActionPerformed(evt);
            }
        });
        splineCalibratorTablePopupMenu.add(copySplineTableMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("dialog_paramdetail_title")); // NOI18N

        editButton.setText(bundle.getString("general_edit")); // NOI18N
        editButton.setMaximumSize(new java.awt.Dimension(75, 25));
        editButton.setMinimumSize(new java.awt.Dimension(75, 25));
        editButton.setPreferredSize(new java.awt.Dimension(75, 25));
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        dismissButton.setText(bundle.getString("general_dismiss_text")); // NOI18N
        dismissButton.setMaximumSize(new java.awt.Dimension(75, 25));
        dismissButton.setMinimumSize(new java.awt.Dimension(75, 25));
        dismissButton.setPreferredSize(new java.awt.Dimension(75, 25));
        dismissButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dismissButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout editButtonsPanelLayout = new javax.swing.GroupLayout(editButtonsPanel);
        editButtonsPanel.setLayout(editButtonsPanelLayout);
        editButtonsPanelLayout.setHorizontalGroup(
            editButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editButtonsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dismissButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        editButtonsPanelLayout.setVerticalGroup(
            editButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(editButtonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(editButtonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dismissButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        nameLabel.setText(bundle.getString("dialog_paramdetail_name")); // NOI18N

        aliasLabel.setText(bundle.getString("dialog_paramdetail_aliases")); // NOI18N

        unitsLabel.setText(bundle.getString("dialog_paramdetail_units")); // NOI18N

        sourceLabel.setText(bundle.getString("dialog_paramdetail_source")); // NOI18N

        systemNameLabel.setText(bundle.getString("dialog_paramdetail_subsys")); // NOI18N

        aliasScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        aliasTextField.setColumns(20);
        aliasTextField.setRows(5);
        aliasScrollPane.setViewportView(aliasTextField);

        unitsScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        unitsTextField.setColumns(20);
        unitsTextField.setRows(5);
        unitsScrollPane.setViewportView(unitsTextField);

        sourceComboField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "telemetered", "derived", "local", "constant" }));
        sourceComboField.setMinimumSize(new java.awt.Dimension(150, 27));
        sourceComboField.setPreferredSize(new java.awt.Dimension(150, 27));

        readOnlyCheckboxField.setText(bundle.getString("dialog_paramdetail_readonly")); // NOI18N

        parameterDescriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        parameterDescriptionLabel.setText(bundle.getString("dialog_paramdetail_pdesc")); // NOI18N
        parameterDescriptionLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        encodingInformationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        encodingInformationLabel.setText(bundle.getString("dialog_paramdetail_enc")); // NOI18N
        encodingInformationLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        shortDescriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        shortDescriptionLabel.setText(bundle.getString("dialog_paramdetail_sdesc")); // NOI18N
        shortDescriptionLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        longDescriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        longDescriptionLabel.setText(bundle.getString("dialog_paramdetail_ldesc")); // NOI18N
        longDescriptionLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        longDescriptionField.setColumns(20);
        longDescriptionField.setLineWrap(true);
        longDescriptionField.setRows(5);
        longDescriptionField.setWrapStyleWord(true);
        leftScrollPane.setViewportView(longDescriptionField);

        engineeringTypeLabel.setText(bundle.getString("dialog_paramdetail_etype")); // NOI18N

        encodingTypeLabel.setText(bundle.getString("dialog_paramdetail_rtype")); // NOI18N

        encodingSizeLabel.setText(bundle.getString("dialog_paramdetail_rsize")); // NOI18N

        typeNameLabel.setText(bundle.getString("dialog_paramdetail_ptypename")); // NOI18N

        bitOrderLabel.setText(bundle.getString("dialog_paramdetail_bitorder")); // NOI18N

        changeThresholdLabel.setText(bundle.getString("dialog_paramdetail_cthreshold")); // NOI18N

        defaultValueLabel.setText(bundle.getString("dialog_paramdetail_ivalue")); // NOI18N

        lowRangeLabel.setText(bundle.getString("dialog_paramdetail_rangelow")); // NOI18N

        highRangeLabel.setText(bundle.getString("dialog_paramdetail_rangehigh")); // NOI18N

        rangeScopeLabel.setText(bundle.getString("dialog_paramdetail_rangescope")); // NOI18N

        engineeringTypeComboField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ENUMERATED", "SIGNED", "UNSIGNED", "FLOAT32", "FLOAT64", "FLOAT128", "STRUCTURE", "BINARY", "BOOLEAN", "STRING", "TIME", "DURATION", "ARRAY" }));

        encodingTypeComboField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "binary", "unsigned", "twosComplement", "onesComplement", "signMagnitude", "IEEE754_1985", "MILSTD_1750A", "BCD", "packedBCD", "UTF-8", "UTF-16", "" }));

        bitOrderComboField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "mostSignificantBitFirst", "leastSignificantBitFirst", "" }));

        rangeAppliesToComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Uncalibrated", "Calibrated", "" }));

        javax.swing.GroupLayout leftPanelLayout = new javax.swing.GroupLayout(leftPanel);
        leftPanel.setLayout(leftPanelLayout);
        leftPanelLayout.setHorizontalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(parameterDescriptionLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shortDescriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(encodingInformationLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shortDescriptionField)
                    .addComponent(longDescriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(leftScrollPane, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(aliasLabel)
                            .addComponent(unitsLabel)
                            .addComponent(nameLabel)
                            .addComponent(systemNameLabel)
                            .addComponent(sourceLabel))
                        .addGap(3, 3, 3)
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(leftPanelLayout.createSequentialGroup()
                                .addComponent(sourceComboField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(readOnlyCheckboxField))
                            .addComponent(systemNameTextField)
                            .addComponent(nameTextField)
                            .addComponent(unitsScrollPane)
                            .addComponent(aliasScrollPane)))
                    .addGroup(leftPanelLayout.createSequentialGroup()
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(engineeringTypeLabel)
                            .addComponent(encodingTypeLabel)
                            .addComponent(encodingSizeLabel)
                            .addComponent(typeNameLabel)
                            .addComponent(bitOrderLabel)
                            .addComponent(changeThresholdLabel)
                            .addComponent(defaultValueLabel)
                            .addComponent(lowRangeLabel)
                            .addComponent(highRangeLabel)
                            .addComponent(rangeScopeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(engineeringTypeComboField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(encodingTypeComboField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rawSizeTextField)
                            .addComponent(xtceTypeNameTextField)
                            .addComponent(bitOrderComboField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(changeThresholdTextField)
                            .addComponent(defaultValueTextField)
                            .addComponent(rangeLowTextField)
                            .addComponent(rangeHighTextField)
                            .addComponent(rangeAppliesToComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        leftPanelLayout.setVerticalGroup(
            leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(leftPanelLayout.createSequentialGroup()
                .addComponent(parameterDescriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(aliasScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aliasLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(unitsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(systemNameLabel)
                    .addComponent(systemNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(sourceComboField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(readOnlyCheckboxField))
                    .addComponent(sourceLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shortDescriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shortDescriptionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(longDescriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(leftScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(encodingInformationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(engineeringTypeLabel)
                    .addComponent(engineeringTypeComboField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(encodingTypeLabel)
                    .addComponent(encodingTypeComboField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(encodingSizeLabel)
                    .addComponent(rawSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeNameLabel)
                    .addComponent(xtceTypeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bitOrderLabel)
                    .addComponent(bitOrderComboField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(changeThresholdLabel)
                    .addComponent(changeThresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(defaultValueLabel)
                    .addComponent(defaultValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rangeLowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lowRangeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rangeHighTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(highRangeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(leftPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rangeAppliesToComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rangeScopeLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        enumerationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        enumerationLabel.setText(bundle.getString("dialog_paramdetail_tabenumstitle")); // NOI18N
        enumerationLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        enumerationTable.setAutoCreateRowSorter(true);
        enumerationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Label", "Value", "Description"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        enumerationTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                enumerationTableMousePressed(evt);
            }
        });
        enumerationScrollPane.setViewportView(enumerationTable);
        if (enumerationTable.getColumnModel().getColumnCount() > 0) {
            enumerationTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("dialog_paramdetail_enumslabel")); // NOI18N
            enumerationTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("dialog_paramdetail_enumsvalue")); // NOI18N
            enumerationTable.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("dialog_paramdetail_enumsdesc")); // NOI18N
        }

        removeEnumerationButton.setText(bundle.getString("general_remove")); // NOI18N
        removeEnumerationButton.setMaximumSize(new java.awt.Dimension(75, 25));
        removeEnumerationButton.setMinimumSize(new java.awt.Dimension(75, 25));
        removeEnumerationButton.setPreferredSize(new java.awt.Dimension(75, 25));
        removeEnumerationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeEnumerationButtonActionPerformed(evt);
            }
        });

        editEnumerationButton.setText(bundle.getString("general_edit")); // NOI18N
        editEnumerationButton.setMaximumSize(new java.awt.Dimension(75, 25));
        editEnumerationButton.setMinimumSize(new java.awt.Dimension(75, 25));
        editEnumerationButton.setPreferredSize(new java.awt.Dimension(75, 25));
        editEnumerationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editEnumerationButtonActionPerformed(evt);
            }
        });

        addEnumerationButton.setText(bundle.getString("general_add")); // NOI18N
        addEnumerationButton.setMaximumSize(new java.awt.Dimension(75, 25));
        addEnumerationButton.setMinimumSize(new java.awt.Dimension(75, 25));
        addEnumerationButton.setPreferredSize(new java.awt.Dimension(75, 25));
        addEnumerationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEnumerationButtonActionPerformed(evt);
            }
        });

        hexCheckbox.setText(bundle.getString("dialog_paramdetail_enumshex")); // NOI18N
        hexCheckbox.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                hexCheckboxStateChanged(evt);
            }
        });

        javax.swing.GroupLayout enumerationPanelLayout = new javax.swing.GroupLayout(enumerationPanel);
        enumerationPanel.setLayout(enumerationPanelLayout);
        enumerationPanelLayout.setHorizontalGroup(
            enumerationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, enumerationPanelLayout.createSequentialGroup()
                .addGroup(enumerationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(enumerationPanelLayout.createSequentialGroup()
                        .addComponent(hexCheckbox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addEnumerationButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editEnumerationButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeEnumerationButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(enumerationLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(enumerationScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );
        enumerationPanelLayout.setVerticalGroup(
            enumerationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enumerationPanelLayout.createSequentialGroup()
                .addComponent(enumerationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(enumerationScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                .addGap(14, 14, 14)
                .addGroup(enumerationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeEnumerationButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editEnumerationButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addEnumerationButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hexCheckbox))
                .addContainerGap())
        );

        javax.swing.GroupLayout enumerationConversionTabLayout = new javax.swing.GroupLayout(enumerationConversionTab);
        enumerationConversionTab.setLayout(enumerationConversionTabLayout);
        enumerationConversionTabLayout.setHorizontalGroup(
            enumerationConversionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enumerationConversionTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(enumerationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        enumerationConversionTabLayout.setVerticalGroup(
            enumerationConversionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(enumerationConversionTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(enumerationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        extraDetailsTabbedPane.addTab(bundle.getString("dialog_paramdetail_tabenums"), enumerationConversionTab); // NOI18N

        polynomialCalibratorsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        polynomialCalibratorsLabel.setText(bundle.getString("dialog_paramdetail_polycal")); // NOI18N
        polynomialCalibratorsLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        coefficient0Label.setText("X0 Coefficient");

        coefficient1Label.setText("X1 Coefficient");

        coefficient2Label.setText("X2 Coefficient");

        coefficient3Label.setText("X3 Coefficient");

        coefficient4Label.setText("X4 Coefficient");

        coefficient5Label.setText("X5 Coefficient");

        coefficient6Label.setText("X6 Coefficient");

        coefficient7Label.setText("X7 Coefficient");

        javax.swing.GroupLayout latexDrawingPanelLayout = new javax.swing.GroupLayout(latexDrawingPanel);
        latexDrawingPanel.setLayout(latexDrawingPanelLayout);
        latexDrawingPanelLayout.setHorizontalGroup(
            latexDrawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        latexDrawingPanelLayout.setVerticalGroup(
            latexDrawingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 267, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout polynomialCalibratorDetailPanelLayout = new javax.swing.GroupLayout(polynomialCalibratorDetailPanel);
        polynomialCalibratorDetailPanel.setLayout(polynomialCalibratorDetailPanelLayout);
        polynomialCalibratorDetailPanelLayout.setHorizontalGroup(
            polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(polynomialCalibratorDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(polynomialCalibratorDetailPanelLayout.createSequentialGroup()
                        .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(coefficient0Label, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(coefficient1Label)
                            .addComponent(coefficient2Label)
                            .addComponent(coefficient3Label)
                            .addComponent(coefficient4Label)
                            .addComponent(coefficient5Label)
                            .addComponent(coefficient6Label)
                            .addComponent(coefficient7Label))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(coefficient7Text, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(coefficient6Text)
                            .addComponent(coefficient5Text)
                            .addComponent(coefficient4Text)
                            .addComponent(coefficient3Text)
                            .addComponent(coefficient2Text)
                            .addComponent(coefficient1Text)
                            .addComponent(coefficient0Text))
                        .addGap(0, 315, Short.MAX_VALUE))
                    .addComponent(latexDrawingPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        polynomialCalibratorDetailPanelLayout.setVerticalGroup(
            polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(polynomialCalibratorDetailPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient0Label)
                    .addComponent(coefficient0Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient1Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coefficient1Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient2Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coefficient2Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient3Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coefficient3Label))
                .addGap(8, 8, 8)
                .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient4Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coefficient4Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient5Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coefficient5Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient6Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coefficient6Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(polynomialCalibratorDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient7Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(coefficient7Label))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(latexDrawingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout polynomialCalibratorParentDetailPanelLayout = new javax.swing.GroupLayout(polynomialCalibratorParentDetailPanel);
        polynomialCalibratorParentDetailPanel.setLayout(polynomialCalibratorParentDetailPanelLayout);
        polynomialCalibratorParentDetailPanelLayout.setHorizontalGroup(
            polynomialCalibratorParentDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(polynomialCalibratorDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        polynomialCalibratorParentDetailPanelLayout.setVerticalGroup(
            polynomialCalibratorParentDetailPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(polynomialCalibratorDetailPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        polynomialCalibratorsTabbedPane.addTab("Default", polynomialCalibratorParentDetailPanel);

        javax.swing.GroupLayout polynomialCalibratorsPanelLayout = new javax.swing.GroupLayout(polynomialCalibratorsPanel);
        polynomialCalibratorsPanel.setLayout(polynomialCalibratorsPanelLayout);
        polynomialCalibratorsPanelLayout.setHorizontalGroup(
            polynomialCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(polynomialCalibratorsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(polynomialCalibratorsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(polynomialCalibratorsTabbedPane)
                .addContainerGap())
        );
        polynomialCalibratorsPanelLayout.setVerticalGroup(
            polynomialCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(polynomialCalibratorsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(polynomialCalibratorsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(polynomialCalibratorsTabbedPane)
                .addContainerGap())
        );

        polynomialCalibratorsTabbedPane.getAccessibleContext().setAccessibleName(bundle.getString("dialog_paramdetail_defaulttab")); // NOI18N

        javax.swing.GroupLayout polynomialCalibratorsTabLayout = new javax.swing.GroupLayout(polynomialCalibratorsTab);
        polynomialCalibratorsTab.setLayout(polynomialCalibratorsTabLayout);
        polynomialCalibratorsTabLayout.setHorizontalGroup(
            polynomialCalibratorsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
            .addGroup(polynomialCalibratorsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(polynomialCalibratorsTabLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(polynomialCalibratorsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        polynomialCalibratorsTabLayout.setVerticalGroup(
            polynomialCalibratorsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 647, Short.MAX_VALUE)
            .addGroup(polynomialCalibratorsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(polynomialCalibratorsTabLayout.createSequentialGroup()
                    .addComponent(polynomialCalibratorsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        extraDetailsTabbedPane.addTab(bundle.getString("dialog_paramdetail_tabpolycal"), polynomialCalibratorsTab); // NOI18N

        splineLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        splineLabel.setText(bundle.getString("dialog_paramdetail_splinecaltitle")); // NOI18N
        splineLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        splineTable.setAutoCreateRowSorter(true);
        splineTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Raw Value", "Calibrated Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        splineTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                splineTableMousePressed(evt);
            }
        });
        splineTableScrollPane.setViewportView(splineTable);
        if (splineTable.getColumnModel().getColumnCount() > 0) {
            splineTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("dialog_paramdetail_splineraw")); // NOI18N
            splineTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("dialog_paramdetail_splinecal")); // NOI18N
        }

        splineCalibratorsTabbedPane.addTab("Default", splineTableScrollPane);

        removeSplinePointButton.setText(bundle.getString("general_remove")); // NOI18N
        removeSplinePointButton.setMaximumSize(new java.awt.Dimension(75, 25));
        removeSplinePointButton.setMinimumSize(new java.awt.Dimension(75, 25));
        removeSplinePointButton.setPreferredSize(new java.awt.Dimension(75, 25));
        removeSplinePointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeSplinePointButtonActionPerformed(evt);
            }
        });

        editSplinePointButton.setText(bundle.getString("general_edit")); // NOI18N
        editSplinePointButton.setMaximumSize(new java.awt.Dimension(75, 25));
        editSplinePointButton.setMinimumSize(new java.awt.Dimension(75, 25));
        editSplinePointButton.setPreferredSize(new java.awt.Dimension(75, 25));
        editSplinePointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSplinePointButtonActionPerformed(evt);
            }
        });

        addSplinePointButton.setText(bundle.getString("general_add")); // NOI18N
        addSplinePointButton.setMaximumSize(new java.awt.Dimension(75, 25));
        addSplinePointButton.setMinimumSize(new java.awt.Dimension(75, 25));
        addSplinePointButton.setPreferredSize(new java.awt.Dimension(75, 25));
        addSplinePointButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSplinePointButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout splineCalibratorsPanelLayout = new javax.swing.GroupLayout(splineCalibratorsPanel);
        splineCalibratorsPanel.setLayout(splineCalibratorsPanelLayout);
        splineCalibratorsPanelLayout.setHorizontalGroup(
            splineCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(splineLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(splineCalibratorsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(splineCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(splineCalibratorsTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, splineCalibratorsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addSplinePointButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editSplinePointButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeSplinePointButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        splineCalibratorsPanelLayout.setVerticalGroup(
            splineCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(splineCalibratorsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(splineLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(splineCalibratorsTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(splineCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeSplinePointButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editSplinePointButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addSplinePointButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        splineCalibratorsTabbedPane.getAccessibleContext().setAccessibleName(bundle.getString("dialog_paramdetail_defaulttab")); // NOI18N

        javax.swing.GroupLayout splineCalibratorsTabLayout = new javax.swing.GroupLayout(splineCalibratorsTab);
        splineCalibratorsTab.setLayout(splineCalibratorsTabLayout);
        splineCalibratorsTabLayout.setHorizontalGroup(
            splineCalibratorsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
            .addGroup(splineCalibratorsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(splineCalibratorsTabLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(splineCalibratorsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        splineCalibratorsTabLayout.setVerticalGroup(
            splineCalibratorsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 647, Short.MAX_VALUE)
            .addGroup(splineCalibratorsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(splineCalibratorsTabLayout.createSequentialGroup()
                    .addComponent(splineCalibratorsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        extraDetailsTabbedPane.addTab(bundle.getString("dialog_paramdetail_tabslinecal"), splineCalibratorsTab); // NOI18N

        javax.swing.GroupLayout alarmDefinitionsTabLayout = new javax.swing.GroupLayout(alarmDefinitionsTab);
        alarmDefinitionsTab.setLayout(alarmDefinitionsTabLayout);
        alarmDefinitionsTabLayout.setHorizontalGroup(
            alarmDefinitionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
        );
        alarmDefinitionsTabLayout.setVerticalGroup(
            alarmDefinitionsTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 647, Short.MAX_VALUE)
        );

        extraDetailsTabbedPane.addTab(bundle.getString("dialog_paramdetail_tabalarm"), alarmDefinitionsTab); // NOI18N

        ancillaryDataLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        ancillaryDataLabel.setText("Ancillary Data Elements");
        ancillaryDataLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        addAncillaryDataButton.setText(bundle.getString("general_add")); // NOI18N
        addAncillaryDataButton.setMaximumSize(new java.awt.Dimension(75, 25));
        addAncillaryDataButton.setMinimumSize(new java.awt.Dimension(75, 25));
        addAncillaryDataButton.setPreferredSize(new java.awt.Dimension(75, 25));
        addAncillaryDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAncillaryDataButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ancillaryDataPanelLayout = new javax.swing.GroupLayout(ancillaryDataPanel);
        ancillaryDataPanel.setLayout(ancillaryDataPanelLayout);
        ancillaryDataPanelLayout.setHorizontalGroup(
            ancillaryDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ancillaryDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ancillaryDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ancillaryDataLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
                    .addComponent(ancillaryDataScrollPane)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ancillaryDataPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(addAncillaryDataButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        ancillaryDataPanelLayout.setVerticalGroup(
            ancillaryDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ancillaryDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ancillaryDataLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ancillaryDataScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 574, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addAncillaryDataButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout ancillaryDataTabLayout = new javax.swing.GroupLayout(ancillaryDataTab);
        ancillaryDataTab.setLayout(ancillaryDataTabLayout);
        ancillaryDataTabLayout.setHorizontalGroup(
            ancillaryDataTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ancillaryDataTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ancillaryDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        ancillaryDataTabLayout.setVerticalGroup(
            ancillaryDataTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ancillaryDataTabLayout.createSequentialGroup()
                .addComponent(ancillaryDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        extraDetailsTabbedPane.addTab(bundle.getString("dialog_paramdetail_tabancdata"), ancillaryDataTab); // NOI18N

        rangeLowInclusiveCheckbox.setText(bundle.getString("dialog_paramdetail_inclusive")); // NOI18N

        rangeHighInclusiveCheckbox.setText(bundle.getString("dialog_paramdetail_inclusive")); // NOI18N

        typeReferenceNameLabel.setText(bundle.getString("dialog_paramdetail_ptyperef")); // NOI18N

        javax.swing.GroupLayout rightPanelLayout = new javax.swing.GroupLayout(rightPanel);
        rightPanel.setLayout(rightPanelLayout);
        rightPanelLayout.setHorizontalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(extraDetailsTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(typeReferenceNameLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameterTypeReferenceText)
                .addContainerGap())
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rangeLowInclusiveCheckbox)
                    .addComponent(rangeHighInclusiveCheckbox))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        rightPanelLayout.setVerticalGroup(
            rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rightPanelLayout.createSequentialGroup()
                .addComponent(extraDetailsTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(rangeLowInclusiveCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rangeHighInclusiveCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(rightPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeReferenceNameLabel)
                    .addComponent(parameterTypeReferenceText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(editButtonsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(leftPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rightPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editButtonsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dismissButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dismissButtonActionPerformed
        this.dispatchEvent( new WindowEvent(this, WindowEvent.WINDOW_CLOSING) );
    }//GEN-LAST:event_dismissButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed

        JOptionPane.showMessageDialog( this,
                                       XTCEFunctions.getText( "not_implemented_edit_feature" ), // NOI18N
                                       XTCEFunctions.getText( "general_warning" ), // NOI18N
                                       JOptionPane.INFORMATION_MESSAGE );

    }//GEN-LAST:event_editButtonActionPerformed

    private void hexCheckboxStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_hexCheckboxStateChanged
        writeEnumerationTable( hexCheckbox.isSelected() );
    }//GEN-LAST:event_hexCheckboxStateChanged

    private void addEnumerationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEnumerationButtonActionPerformed

        List<ValueEnumerationType> enumerations = parameter_.getEnumerations();
        ValueEnumerationType newEnumeration = new ValueEnumerationType();
        labelText.setText( "NEWLABEL" );
        valueText.setText( "" );
        maxValueText.setText( "" );
        descriptionText.setText( "" );
        int response = JOptionPane.showConfirmDialog( this,
                                                      enumerationEditorPanel,
                                                      "Add Enumeration",
                                                      JOptionPane.OK_CANCEL_OPTION );
        if ( response == JOptionPane.CANCEL_OPTION ) {
            return;
        }
        String label    = labelText.getText();
        String value    = valueText.getText();
        String maxValue = maxValueText.getText();
        String desc     = descriptionText.getText();
        if ( validateEnumeration( enumerations, -1, label, value, maxValue ) == false ) {
            return;
        }
        newEnumeration.setLabel( label );
        newEnumeration.setValue( new BigInteger( value ) );
        if ( ( maxValue != null ) && ( maxValue.isEmpty() == false ) ) {
            newEnumeration.setMaxValue( new BigInteger( maxValue ) );
        }
        if ( ( desc != null ) && ( desc.isEmpty() == false ) ) {
            newEnumeration.setShortDescription( desc );
        }
        enumerations.add( newEnumeration );
        //parameter_.setEnumerations( enumerations );
        //setChanged( true ); // needs to be implemented
        writeEnumerationTable( hexCheckbox.isSelected() );

    }//GEN-LAST:event_addEnumerationButtonActionPerformed

    private void editEnumerationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editEnumerationButtonActionPerformed

        DefaultTableModel tableModel = (DefaultTableModel)enumerationTable.getModel();
        int row = enumerationTable.getSelectedRow();
        if ( row == -1 ) {
            JOptionPane.showMessageDialog( this,
                                           "No Enumeration Selected",
                                           XTCEFunctions.getText( "general_error" ),
                                           JOptionPane.ERROR_MESSAGE);
            return;
        }
        String label = (String)enumerationTable.getValueAt( row, 0 );
        List<ValueEnumerationType> enumerations = parameter_.getEnumerations();
        int enumIndex = -1;
        for ( int iii = 0; iii < enumerations.size(); ++iii ) {
            if ( enumerations.get( iii ).getLabel().equals( label ) == true ) {
                enumIndex = iii;
                iii = enumerations.size();
            }
        }
        if ( enumIndex == -1 ) {
            return;  // will not happen
        }
        labelText.setText( enumerations.get( enumIndex ).getLabel() );
        valueText.setText( enumerations.get( enumIndex ).getValue().toString() );
        if ( enumerations.get( enumIndex ).getMaxValue() != null ) {
            maxValueText.setText( enumerations.get( enumIndex ).getMaxValue().toString() );
        } else {
            maxValueText.setText( "" );
        }
        if ( enumerations.get( enumIndex ).getShortDescription() != null ) {
            descriptionText.setText( enumerations.get( enumIndex ).getShortDescription() );
        } else {
            descriptionText.setText( "" );
        }
        int response = JOptionPane.showConfirmDialog( this,
                                                      enumerationEditorPanel,
                                                      "Edit Enumeration",
                                                      JOptionPane.OK_CANCEL_OPTION );
        if ( response == JOptionPane.CANCEL_OPTION ) {
            return;
        }
        label           = labelText.getText();
        String value    = valueText.getText();
        String maxValue = maxValueText.getText();
        String desc     = descriptionText.getText();
        if ( validateEnumeration( enumerations, enumIndex, label, value, maxValue ) == false ) {
            return;
        }
        enumerations.get( enumIndex ).setLabel( label );
        enumerations.get( enumIndex ).setValue( new BigInteger( value ) );
        if ( ( maxValue != null ) && ( maxValue.isEmpty() == false ) ) {
            enumerations.get( enumIndex ).setMaxValue( new BigInteger( maxValue ) );
        } else {
            enumerations.get( enumIndex ).setMaxValue( null );
        }
        if ( ( desc != null ) && ( desc.isEmpty() == false ) ) {
            enumerations.get( enumIndex ).setShortDescription( desc );
        } else {
            enumerations.get( enumIndex ).setShortDescription( null );
        }
        //parameter_.setEnumerations( enumerations );
        //setChanged( true ); // needs to be implemented
        writeEnumerationTable( hexCheckbox.isSelected() );

    }//GEN-LAST:event_editEnumerationButtonActionPerformed

    private void removeEnumerationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEnumerationButtonActionPerformed

        DefaultTableModel tableModel = (DefaultTableModel)enumerationTable.getModel();
        int row = enumerationTable.getSelectedRow();
        if ( row == -1 ) {
            JOptionPane.showMessageDialog( this,
                                           "No Enumeration Selected",
                                           XTCEFunctions.getText( "general_error" ),
                                           JOptionPane.ERROR_MESSAGE);
            return;
        }
        String label = (String)enumerationTable.getValueAt( row, 0 );
        int confirmed = JOptionPane.showConfirmDialog( this,
                                                       "Are you aure you want to delete " + label + "?",
                                                       XTCEFunctions.getText( "general_warning" ),
                                                       JOptionPane.YES_NO_OPTION,
                                                       JOptionPane.WARNING_MESSAGE );
        if ( confirmed == JOptionPane.NO_OPTION ) {
            return;
        }
        List<ValueEnumerationType> enumerations = parameter_.getEnumerations();
        for ( int iii = 0; iii < enumerations.size(); ++iii ) {
            if ( enumerations.get( iii ).getLabel().equals( label ) == true ) {
                enumerations.remove( iii );
                iii = enumerations.size();
            }
        }
        //parameter_.setEnumerations( enumerations );
        //setChanged( true ); // needs to be implemented
        writeEnumerationTable( hexCheckbox.isSelected() );

    }//GEN-LAST:event_removeEnumerationButtonActionPerformed

    private void addAncillaryDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAncillaryDataButtonActionPerformed

        JOptionPane.showMessageDialog( this,
                                       XTCEFunctions.getText( "not_implemented_edit_feature" ), // NOI18N
                                       XTCEFunctions.getText( "general_warning" ), // NOI18N
                                       JOptionPane.INFORMATION_MESSAGE );

    }//GEN-LAST:event_addAncillaryDataButtonActionPerformed

    private void addSplinePointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSplinePointButtonActionPerformed

        JOptionPane.showMessageDialog( this,
                                       XTCEFunctions.getText( "not_implemented_edit_feature" ), // NOI18N
                                       XTCEFunctions.getText( "general_warning" ), // NOI18N
                                       JOptionPane.INFORMATION_MESSAGE );

    }//GEN-LAST:event_addSplinePointButtonActionPerformed

    private void editSplinePointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSplinePointButtonActionPerformed

        JOptionPane.showMessageDialog( this,
                                       XTCEFunctions.getText( "not_implemented_edit_feature" ), // NOI18N
                                       XTCEFunctions.getText( "general_warning" ), // NOI18N
                                       JOptionPane.INFORMATION_MESSAGE );

    }//GEN-LAST:event_editSplinePointButtonActionPerformed

    private void removeSplinePointButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeSplinePointButtonActionPerformed

        JOptionPane.showMessageDialog( this,
                                       XTCEFunctions.getText( "not_implemented_edit_feature" ), // NOI18N
                                       XTCEFunctions.getText( "general_warning" ), // NOI18N
                                       JOptionPane.INFORMATION_MESSAGE );

    }//GEN-LAST:event_removeSplinePointButtonActionPerformed

    private void enumerationTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_enumerationTableMousePressed

        XTCEViewerFunctions.showRightClickTableMenu( evt,
                                                     enumerationTable,
                                                     enumerationTablePopupMenu );

    }//GEN-LAST:event_enumerationTableMousePressed

    private void splineTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_splineTableMousePressed

        XTCEViewerFunctions.showRightClickTableMenu( evt,
                                                     splineTable,
                                                     splineCalibratorTablePopupMenu );

    }//GEN-LAST:event_splineTableMousePressed

    private void copyEnumerationCellMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyEnumerationCellMenuItemActionPerformed
        XTCEViewerFunctions.copyCell( enumerationTable );
    }//GEN-LAST:event_copyEnumerationCellMenuItemActionPerformed

    private void copyEnumerationColumnMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyEnumerationColumnMenuItemActionPerformed
        XTCEViewerFunctions.copyColumn( enumerationTable );
    }//GEN-LAST:event_copyEnumerationColumnMenuItemActionPerformed

    private void copyEnumerationRowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyEnumerationRowMenuItemActionPerformed
        XTCEViewerFunctions.copyRow( enumerationTable );
    }//GEN-LAST:event_copyEnumerationRowMenuItemActionPerformed

    private void copyEnumerationTableMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyEnumerationTableMenuItemActionPerformed
        XTCEViewerFunctions.copyTable( enumerationTable );
    }//GEN-LAST:event_copyEnumerationTableMenuItemActionPerformed

    private void copySplineCellMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copySplineCellMenuItemActionPerformed
        XTCEViewerFunctions.copyCell( splineTable );
    }//GEN-LAST:event_copySplineCellMenuItemActionPerformed

    private void copySplineColumnMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copySplineColumnMenuItemActionPerformed
        XTCEViewerFunctions.copyColumn( splineTable );
    }//GEN-LAST:event_copySplineColumnMenuItemActionPerformed

    private void copySplineRowMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copySplineRowMenuItemActionPerformed
        XTCEViewerFunctions.copyRow( splineTable );
    }//GEN-LAST:event_copySplineRowMenuItemActionPerformed

    private void copySplineTableMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copySplineTableMenuItemActionPerformed
        XTCEViewerFunctions.copyTable( splineTable );
    }//GEN-LAST:event_copySplineTableMenuItemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(XTCEViewerParameterDetailDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(XTCEViewerParameterDetailDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(XTCEViewerParameterDetailDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(XTCEViewerParameterDetailDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    XTCEViewerParameterDetailDialog dialog = new XTCEViewerParameterDetailDialog(new javax.swing.JFrame(), true, null);
                    dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                        @Override
                        public void windowClosing(java.awt.event.WindowEvent e) {
                            System.exit(0);
                        }
                    });
                    dialog.setVisible(true);
                } catch ( XTCEDatabaseException ex ) {
                    System.out.println( "Exception: " + ex.getLocalizedMessage() );
                }
            }
        });
    }

    private XTCEParameter parameter_ = null;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAncillaryDataButton;
    private javax.swing.JButton addEnumerationButton;
    private javax.swing.JButton addSplinePointButton;
    private javax.swing.JPanel alarmDefinitionsTab;
    private javax.swing.JLabel aliasLabel;
    private javax.swing.JScrollPane aliasScrollPane;
    private javax.swing.JTextArea aliasTextField;
    private javax.swing.JLabel ancillaryDataLabel;
    private javax.swing.JPanel ancillaryDataPanel;
    private javax.swing.JScrollPane ancillaryDataScrollPane;
    private javax.swing.JPanel ancillaryDataTab;
    private javax.swing.JComboBox bitOrderComboField;
    private javax.swing.JLabel bitOrderLabel;
    private javax.swing.JLabel changeThresholdLabel;
    private javax.swing.JTextField changeThresholdTextField;
    private javax.swing.JLabel coefficient0Label;
    private javax.swing.JTextField coefficient0Text;
    private javax.swing.JLabel coefficient1Label;
    private javax.swing.JTextField coefficient1Text;
    private javax.swing.JLabel coefficient2Label;
    private javax.swing.JTextField coefficient2Text;
    private javax.swing.JLabel coefficient3Label;
    private javax.swing.JTextField coefficient3Text;
    private javax.swing.JLabel coefficient4Label;
    private javax.swing.JTextField coefficient4Text;
    private javax.swing.JLabel coefficient5Label;
    private javax.swing.JTextField coefficient5Text;
    private javax.swing.JLabel coefficient6Label;
    private javax.swing.JTextField coefficient6Text;
    private javax.swing.JLabel coefficient7Label;
    private javax.swing.JTextField coefficient7Text;
    private javax.swing.JMenuItem copyEnumerationCellMenuItem;
    private javax.swing.JMenuItem copyEnumerationColumnMenuItem;
    private javax.swing.JMenuItem copyEnumerationRowMenuItem;
    private javax.swing.JMenuItem copyEnumerationTableMenuItem;
    private javax.swing.JMenuItem copySplineCellMenuItem;
    private javax.swing.JMenuItem copySplineColumnMenuItem;
    private javax.swing.JMenuItem copySplineRowMenuItem;
    private javax.swing.JMenuItem copySplineTableMenuItem;
    private javax.swing.JLabel defaultValueLabel;
    private javax.swing.JTextField defaultValueTextField;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JTextArea descriptionText;
    private javax.swing.JScrollPane descriptionTextScrollPane;
    private javax.swing.JButton dismissButton;
    private javax.swing.JButton editButton;
    private javax.swing.JPanel editButtonsPanel;
    private javax.swing.JButton editEnumerationButton;
    private javax.swing.JButton editSplinePointButton;
    private javax.swing.JLabel encodingInformationLabel;
    private javax.swing.JLabel encodingSizeLabel;
    private javax.swing.JComboBox encodingTypeComboField;
    private javax.swing.JLabel encodingTypeLabel;
    private javax.swing.JComboBox engineeringTypeComboField;
    private javax.swing.JLabel engineeringTypeLabel;
    private javax.swing.JLabel enumLabelLabel;
    private javax.swing.JLabel enumMaxValueLabel;
    private javax.swing.JLabel enumValueLabel;
    private javax.swing.JPanel enumerationConversionTab;
    private javax.swing.JPanel enumerationEditorPanel;
    private javax.swing.JLabel enumerationLabel;
    private javax.swing.JPanel enumerationPanel;
    private javax.swing.JScrollPane enumerationScrollPane;
    private javax.swing.JTable enumerationTable;
    private javax.swing.JPopupMenu enumerationTablePopupMenu;
    private javax.swing.JTabbedPane extraDetailsTabbedPane;
    private javax.swing.JCheckBox hexCheckbox;
    private javax.swing.JLabel highRangeLabel;
    private javax.swing.JTextField labelText;
    private javax.swing.JPanel latexDrawingPanel;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JScrollPane leftScrollPane;
    private javax.swing.JTextArea longDescriptionField;
    private javax.swing.JLabel longDescriptionLabel;
    private javax.swing.JLabel lowRangeLabel;
    private javax.swing.JTextField maxValueText;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel parameterDescriptionLabel;
    private javax.swing.JTextField parameterTypeReferenceText;
    private javax.swing.JPanel polynomialCalibratorDetailPanel;
    private javax.swing.JPanel polynomialCalibratorParentDetailPanel;
    private javax.swing.JLabel polynomialCalibratorsLabel;
    private javax.swing.JPanel polynomialCalibratorsPanel;
    private javax.swing.JPanel polynomialCalibratorsTab;
    private javax.swing.JTabbedPane polynomialCalibratorsTabbedPane;
    private javax.swing.JComboBox rangeAppliesToComboBox;
    private javax.swing.JCheckBox rangeHighInclusiveCheckbox;
    private javax.swing.JTextField rangeHighTextField;
    private javax.swing.JCheckBox rangeLowInclusiveCheckbox;
    private javax.swing.JTextField rangeLowTextField;
    private javax.swing.JLabel rangeScopeLabel;
    private javax.swing.JTextField rawSizeTextField;
    private javax.swing.JCheckBox readOnlyCheckboxField;
    private javax.swing.JButton removeEnumerationButton;
    private javax.swing.JButton removeSplinePointButton;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JTextField shortDescriptionField;
    private javax.swing.JLabel shortDescriptionLabel;
    private javax.swing.JComboBox sourceComboField;
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JPopupMenu splineCalibratorTablePopupMenu;
    private javax.swing.JPanel splineCalibratorsPanel;
    private javax.swing.JPanel splineCalibratorsTab;
    private javax.swing.JTabbedPane splineCalibratorsTabbedPane;
    private javax.swing.JLabel splineLabel;
    private javax.swing.JTable splineTable;
    private javax.swing.JScrollPane splineTableScrollPane;
    private javax.swing.JLabel systemNameLabel;
    private javax.swing.JTextField systemNameTextField;
    private javax.swing.JLabel typeNameLabel;
    private javax.swing.JLabel typeReferenceNameLabel;
    private javax.swing.JLabel unitsLabel;
    private javax.swing.JScrollPane unitsScrollPane;
    private javax.swing.JTextArea unitsTextField;
    private javax.swing.JTextField valueText;
    private javax.swing.JTextField xtceTypeNameTextField;
    // End of variables declaration//GEN-END:variables
}
