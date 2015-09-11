/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableModel;
import org.omg.space.xtce.database.CalibratorType;
import org.omg.space.xtce.database.CalibratorType.SplineCalibrator;
import org.omg.space.xtce.database.ParameterTypeSetType.BooleanParameterType;
import org.omg.space.xtce.database.PolynomialType;
import org.omg.space.xtce.database.SplinePointType;
import org.omg.space.xtce.database.ValueEnumerationType;
import org.omg.space.xtce.toolkit.XTCEFunctions;
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
            readOnlyCheckboxField.setSelected( parameter.isSettable() );
        }
        shortDescriptionField.setText( parameter.getShortDescription() );
        shortDescriptionField.setCaretPosition( 0 );
        longDescriptionField.setText( parameter.getLongDescription() );
        longDescriptionField.setCaretPosition( 0 );
        engineeringTypeComboField.setSelectedItem( parameter.getEngineeringType() );
        if ( parameter.getRawType().isEmpty() == true ) {
            encodingTypeComboField.setSelectedItem( "" );
            encodingTypeComboField.setEnabled( false );
            rawSizeTextField.setEnabled( false );
            bitOrderComboField.setSelectedItem( "" );
            bitOrderComboField.setEnabled( false );
            
        } else {
            encodingTypeComboField.setSelectedItem( parameter.getRawType() );
            rawSizeTextField.setText( parameter.getRawSizeInBits() );
            bitOrderComboField.setSelectedItem( parameter.getRawBitOrder() );
        }
        defaultValueTextField.setText( parameter.getInitialValue() );
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
        parameterTypeReferenceText.setCaretPosition( 0 );
        xtceTypeNameTextField.setCaretPosition( 0 );
        pack();
        setLocationRelativeTo( parent );
    }

    private void writeEnumerationTable( boolean hex ) {

        if ( parameter_.getEngineeringType().equals( "ENUMERATED" ) == true ) {
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
        } else if ( parameter_.getEngineeringType().equals( "BOOLEAN" ) == true ) {
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
            String rawEncoding = parameter_.getRawType();
            if ( ( candidate.intValue() < 0 ) && ( rawEncoding.equals( "unsigned" ) == true ) ) {
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
                String rawEncoding = parameter_.getRawType();
                if ( ( candidate.intValue() < 0 ) && ( rawEncoding.equals( "unsigned" ) == true ) ) {
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
            Object rowData[] = { new Double( point.getRaw() ),
                                 new Double( point.getCalibrated() ) };
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
        jPanel3 = new javax.swing.JPanel();
        editButton = new javax.swing.JButton();
        dismissButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        aliasLabel = new javax.swing.JLabel();
        unitsLabel = new javax.swing.JLabel();
        sourceLabel = new javax.swing.JLabel();
        systemNameLabel = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        aliasTextField = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        unitsTextField = new javax.swing.JTextArea();
        sourceComboField = new javax.swing.JComboBox();
        readOnlyCheckboxField = new javax.swing.JCheckBox();
        parameterDescriptionLabel = new javax.swing.JLabel();
        encodingInformationLabel = new javax.swing.JLabel();
        shortDescriptionLabel = new javax.swing.JLabel();
        shortDescriptionField = new javax.swing.JTextField();
        longDescriptionLabel = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        longDescriptionField = new javax.swing.JTextArea();
        systemNameTextField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
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
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        extraDetailsTabbedPane = new javax.swing.JTabbedPane();
        engioneeringConversionTab = new javax.swing.JPanel();
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
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        coefficient0Text = new javax.swing.JTextField();
        coefficient1Text = new javax.swing.JTextField();
        coefficient2Text = new javax.swing.JTextField();
        coefficient3Text = new javax.swing.JTextField();
        coefficient4Text = new javax.swing.JTextField();
        coefficient5Text = new javax.swing.JTextField();
        coefficient6Text = new javax.swing.JTextField();
        coefficient7Text = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        latexDrawingPanel = new javax.swing.JPanel();
        splineCalibratorsTab = new javax.swing.JPanel();
        splineCalibratorsPanel = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jScrollPane4 = new javax.swing.JScrollPane();
        splineTable = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        alarmDefinitionsTab = new javax.swing.JPanel();
        ancillaryDataTab = new javax.swing.JPanel();
        rangeLowInclusiveCheckbox = new javax.swing.JCheckBox();
        rangeHighInclusiveCheckbox = new javax.swing.JCheckBox();
        jLabel20 = new javax.swing.JLabel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Parameter Detail Display");

        editButton.setText("Edit");
        editButton.setMaximumSize(new java.awt.Dimension(75, 25));
        editButton.setMinimumSize(new java.awt.Dimension(75, 25));
        editButton.setPreferredSize(new java.awt.Dimension(75, 25));
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        dismissButton.setText("Dismiss");
        dismissButton.setMaximumSize(new java.awt.Dimension(75, 25));
        dismissButton.setMinimumSize(new java.awt.Dimension(75, 25));
        dismissButton.setPreferredSize(new java.awt.Dimension(75, 25));
        dismissButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dismissButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dismissButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dismissButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        nameLabel.setText("Name");

        aliasLabel.setText("Alias(s)");

        unitsLabel.setText("Units");

        sourceLabel.setText("Source");

        systemNameLabel.setText("Subsystem");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        aliasTextField.setColumns(20);
        aliasTextField.setRows(5);
        jScrollPane1.setViewportView(aliasTextField);

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        unitsTextField.setColumns(20);
        unitsTextField.setRows(5);
        jScrollPane2.setViewportView(unitsTextField);

        sourceComboField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "telemetered", "derived", "local", "constant" }));
        sourceComboField.setMinimumSize(new java.awt.Dimension(150, 27));
        sourceComboField.setPreferredSize(new java.awt.Dimension(150, 27));

        readOnlyCheckboxField.setText("Read Only");

        parameterDescriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        parameterDescriptionLabel.setText("Parameter Description");
        parameterDescriptionLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        encodingInformationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        encodingInformationLabel.setText("Encoding Information");
        encodingInformationLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        shortDescriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        shortDescriptionLabel.setText("Short Description");
        shortDescriptionLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        longDescriptionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        longDescriptionLabel.setText("Long Description");
        longDescriptionLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        longDescriptionField.setColumns(20);
        longDescriptionField.setLineWrap(true);
        longDescriptionField.setRows(5);
        longDescriptionField.setWrapStyleWord(true);
        jScrollPane3.setViewportView(longDescriptionField);

        jLabel1.setText("Engineering Type");

        jLabel2.setText("Encoding Type");

        jLabel3.setText("Encoding Size");

        jLabel4.setText("Type Name");

        jLabel5.setText("Bit Order");

        jLabel6.setText("Change Threshold");

        jLabel7.setText("Default Value");

        engineeringTypeComboField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ENUMERATED", "SIGNED", "UNSIGNED", "FLOAT32", "FLOAT64", "FLOAT128", "STRUCTURE", "BINARY", "BOOLEAN", "STRING", "TIME", "DURATION", "ARRAY" }));

        encodingTypeComboField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "binary", "unsigned", "twosComplement", "onesComplement", "signMagnitude", "IEEE754_1985", "MILSTD_1750A", "BCD", "packedBCD", "UTF-8", "UTF-16", "" }));

        bitOrderComboField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "mostSignificantBitFirst", "leastSignificantBitFirst", "" }));

        rangeAppliesToComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Uncalibrated", "Calibrated", "" }));

        jLabel17.setText("Range Low");

        jLabel18.setText("Range High");

        jLabel19.setText("Range Scope");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(parameterDescriptionLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shortDescriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(encodingInformationLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(shortDescriptionField)
                    .addComponent(longDescriptionLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(aliasLabel)
                            .addComponent(unitsLabel)
                            .addComponent(nameLabel)
                            .addComponent(systemNameLabel)
                            .addComponent(sourceLabel))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(sourceComboField, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(readOnlyCheckboxField))
                            .addComponent(systemNameTextField)
                            .addComponent(nameTextField)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane1)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(parameterDescriptionLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(aliasLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(unitsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(systemNameLabel)
                    .addComponent(systemNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(encodingInformationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(engineeringTypeComboField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(encodingTypeComboField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(rawSizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(xtceTypeNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(bitOrderComboField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(changeThresholdTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(defaultValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rangeLowTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rangeHighTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rangeAppliesToComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        enumerationLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        enumerationLabel.setText("Enumeration/Multistate Table");
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
        enumerationScrollPane.setViewportView(enumerationTable);

        removeEnumerationButton.setText("Remove");
        removeEnumerationButton.setMaximumSize(new java.awt.Dimension(75, 25));
        removeEnumerationButton.setMinimumSize(new java.awt.Dimension(75, 25));
        removeEnumerationButton.setPreferredSize(new java.awt.Dimension(75, 25));
        removeEnumerationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeEnumerationButtonActionPerformed(evt);
            }
        });

        editEnumerationButton.setText("Edit");
        editEnumerationButton.setMaximumSize(new java.awt.Dimension(75, 25));
        editEnumerationButton.setMinimumSize(new java.awt.Dimension(75, 25));
        editEnumerationButton.setPreferredSize(new java.awt.Dimension(75, 25));
        editEnumerationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editEnumerationButtonActionPerformed(evt);
            }
        });

        addEnumerationButton.setText("Add");
        addEnumerationButton.setMaximumSize(new java.awt.Dimension(75, 25));
        addEnumerationButton.setMinimumSize(new java.awt.Dimension(75, 25));
        addEnumerationButton.setPreferredSize(new java.awt.Dimension(75, 25));
        addEnumerationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEnumerationButtonActionPerformed(evt);
            }
        });

        hexCheckbox.setText("Show Values in Hex");
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

        javax.swing.GroupLayout engioneeringConversionTabLayout = new javax.swing.GroupLayout(engioneeringConversionTab);
        engioneeringConversionTab.setLayout(engioneeringConversionTabLayout);
        engioneeringConversionTabLayout.setHorizontalGroup(
            engioneeringConversionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(engioneeringConversionTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(enumerationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        engioneeringConversionTabLayout.setVerticalGroup(
            engioneeringConversionTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(engioneeringConversionTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(enumerationPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        extraDetailsTabbedPane.addTab("Enumerations", engioneeringConversionTab);

        polynomialCalibratorsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        polynomialCalibratorsLabel.setText("Polynomial Calibrator");
        polynomialCalibratorsLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel8.setText("X0 Coefficient");

        jLabel9.setText("X1 Coefficient");

        jLabel10.setText("X2 Coefficient");

        jLabel11.setText("X3 Coefficient");

        jLabel12.setText("X4 Coefficient");

        jLabel13.setText("X5 Coefficient");

        jLabel14.setText("X6 Coefficient");

        jLabel15.setText("X7 Coefficient");

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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
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
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(coefficient0Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient1Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient2Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient3Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(8, 8, 8)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient4Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient5Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient6Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(coefficient7Text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(latexDrawingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Default", jPanel4);

        javax.swing.GroupLayout polynomialCalibratorsPanelLayout = new javax.swing.GroupLayout(polynomialCalibratorsPanel);
        polynomialCalibratorsPanel.setLayout(polynomialCalibratorsPanelLayout);
        polynomialCalibratorsPanelLayout.setHorizontalGroup(
            polynomialCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(polynomialCalibratorsLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(polynomialCalibratorsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        polynomialCalibratorsPanelLayout.setVerticalGroup(
            polynomialCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(polynomialCalibratorsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(polynomialCalibratorsLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );

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

        extraDetailsTabbedPane.addTab("Polynomial Calibrators", polynomialCalibratorsTab);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Spline Calibrator / Piecewise Functions");
        jLabel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

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
        jScrollPane4.setViewportView(splineTable);

        jTabbedPane2.addTab("Default", jScrollPane4);

        jButton1.setText("Remove");
        jButton1.setMaximumSize(new java.awt.Dimension(75, 25));
        jButton1.setMinimumSize(new java.awt.Dimension(75, 25));
        jButton1.setPreferredSize(new java.awt.Dimension(75, 25));

        jButton2.setText("Edit");
        jButton2.setMaximumSize(new java.awt.Dimension(75, 25));
        jButton2.setMinimumSize(new java.awt.Dimension(75, 25));
        jButton2.setPreferredSize(new java.awt.Dimension(75, 25));

        jButton3.setText("Add");
        jButton3.setMaximumSize(new java.awt.Dimension(75, 25));
        jButton3.setMinimumSize(new java.awt.Dimension(75, 25));
        jButton3.setPreferredSize(new java.awt.Dimension(75, 25));

        javax.swing.GroupLayout splineCalibratorsPanelLayout = new javax.swing.GroupLayout(splineCalibratorsPanel);
        splineCalibratorsPanel.setLayout(splineCalibratorsPanelLayout);
        splineCalibratorsPanelLayout.setHorizontalGroup(
            splineCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(splineCalibratorsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(splineCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, splineCalibratorsPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        splineCalibratorsPanelLayout.setVerticalGroup(
            splineCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(splineCalibratorsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(splineCalibratorsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

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

        extraDetailsTabbedPane.addTab("Spline Calibrators", splineCalibratorsTab);

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

        extraDetailsTabbedPane.addTab("Alarm Definitions", alarmDefinitionsTab);

        javax.swing.GroupLayout ancillaryDataTabLayout = new javax.swing.GroupLayout(ancillaryDataTab);
        ancillaryDataTab.setLayout(ancillaryDataTabLayout);
        ancillaryDataTabLayout.setHorizontalGroup(
            ancillaryDataTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 578, Short.MAX_VALUE)
        );
        ancillaryDataTabLayout.setVerticalGroup(
            ancillaryDataTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 647, Short.MAX_VALUE)
        );

        extraDetailsTabbedPane.addTab("Ancillary Data", ancillaryDataTab);

        rangeLowInclusiveCheckbox.setText("Inclusive");

        rangeHighInclusiveCheckbox.setText("Inclusive");

        jLabel20.setText("Parameter Type Reference");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(extraDetailsTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(parameterTypeReferenceText)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rangeLowInclusiveCheckbox)
                    .addComponent(rangeHighInclusiveCheckbox))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(extraDetailsTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 693, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(rangeLowInclusiveCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rangeHighInclusiveCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
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
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void dismissButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dismissButtonActionPerformed
        this.dispatchEvent( new WindowEvent(this, WindowEvent.WINDOW_CLOSING) );
    }//GEN-LAST:event_dismissButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // TODO add your handling code here:
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
    private javax.swing.JButton addEnumerationButton;
    private javax.swing.JPanel alarmDefinitionsTab;
    private javax.swing.JLabel aliasLabel;
    private javax.swing.JTextArea aliasTextField;
    private javax.swing.JPanel ancillaryDataTab;
    private javax.swing.JComboBox bitOrderComboField;
    private javax.swing.JTextField changeThresholdTextField;
    private javax.swing.JTextField coefficient0Text;
    private javax.swing.JTextField coefficient1Text;
    private javax.swing.JTextField coefficient2Text;
    private javax.swing.JTextField coefficient3Text;
    private javax.swing.JTextField coefficient4Text;
    private javax.swing.JTextField coefficient5Text;
    private javax.swing.JTextField coefficient6Text;
    private javax.swing.JTextField coefficient7Text;
    private javax.swing.JTextField defaultValueTextField;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JTextArea descriptionText;
    private javax.swing.JScrollPane descriptionTextScrollPane;
    private javax.swing.JButton dismissButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton editEnumerationButton;
    private javax.swing.JLabel encodingInformationLabel;
    private javax.swing.JComboBox encodingTypeComboField;
    private javax.swing.JComboBox engineeringTypeComboField;
    private javax.swing.JPanel engioneeringConversionTab;
    private javax.swing.JLabel enumLabelLabel;
    private javax.swing.JLabel enumMaxValueLabel;
    private javax.swing.JLabel enumValueLabel;
    private javax.swing.JPanel enumerationEditorPanel;
    private javax.swing.JLabel enumerationLabel;
    private javax.swing.JPanel enumerationPanel;
    private javax.swing.JScrollPane enumerationScrollPane;
    private javax.swing.JTable enumerationTable;
    private javax.swing.JTabbedPane extraDetailsTabbedPane;
    private javax.swing.JCheckBox hexCheckbox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTextField labelText;
    private javax.swing.JPanel latexDrawingPanel;
    private javax.swing.JTextArea longDescriptionField;
    private javax.swing.JLabel longDescriptionLabel;
    private javax.swing.JTextField maxValueText;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel parameterDescriptionLabel;
    private javax.swing.JTextField parameterTypeReferenceText;
    private javax.swing.JLabel polynomialCalibratorsLabel;
    private javax.swing.JPanel polynomialCalibratorsPanel;
    private javax.swing.JPanel polynomialCalibratorsTab;
    private javax.swing.JComboBox rangeAppliesToComboBox;
    private javax.swing.JCheckBox rangeHighInclusiveCheckbox;
    private javax.swing.JTextField rangeHighTextField;
    private javax.swing.JCheckBox rangeLowInclusiveCheckbox;
    private javax.swing.JTextField rangeLowTextField;
    private javax.swing.JTextField rawSizeTextField;
    private javax.swing.JCheckBox readOnlyCheckboxField;
    private javax.swing.JButton removeEnumerationButton;
    private javax.swing.JTextField shortDescriptionField;
    private javax.swing.JLabel shortDescriptionLabel;
    private javax.swing.JComboBox sourceComboField;
    private javax.swing.JLabel sourceLabel;
    private javax.swing.JPanel splineCalibratorsPanel;
    private javax.swing.JPanel splineCalibratorsTab;
    private javax.swing.JTable splineTable;
    private javax.swing.JLabel systemNameLabel;
    private javax.swing.JTextField systemNameTextField;
    private javax.swing.JLabel unitsLabel;
    private javax.swing.JTextArea unitsTextField;
    private javax.swing.JTextField valueText;
    private javax.swing.JTextField xtceTypeNameTextField;
    // End of variables declaration//GEN-END:variables
}
