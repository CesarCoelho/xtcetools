/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import org.omg.space.xtce.database.ArgumentTypeSetType;
import org.omg.space.xtce.database.BooleanDataType;
import org.omg.space.xtce.database.FloatDataType;
import org.omg.space.xtce.database.IntegerDataType;
import org.omg.space.xtce.database.NameDescriptionType;
import org.omg.space.xtce.database.ParameterTypeSetType;

/** This class provides a simple accessor to the ValidRange element on an
 * XTCEParameter or XTCEArgument while abstracting the user away from the
 * type differences.
 *
 * @author b1053583
 *
 */

public class XTCEValidRange {

    /** Constructor
     *
     * @param item NameDescriptionType base class reference for typed objects
     * in the XTCE data model.
     *
     */

    XTCEValidRange( NameDescriptionType item ) {
        setValidRangeAttributes( item );
    }

    /** Retrieve the flag indicating if there is currently a ValidRange element
     * applied to the type represented in this object.
     *
     * @return boolean indicating if a ValidRange was processed.
     *
     */

    public boolean isValidRangeApplied() {
        return rangeApplied_;
    }

    /** Retrieve the low valid valid in the allowable range.
     *
     * @return String containing the value.
     *
     */

    public String getLowValue() {
        return lowValidValue_;
    }

    /** Retrieve the high valid valid in the allowable range.
     *
     * @return String containing the value.
     *
     */

    public String getHighValue() {
        return highValidValue_;
    }

    /** Retrieve the flag indicating if the low value should be applied as
     * inclusive or exclusive.
     *
     * @return boolean indicating if the low value is inclusive.
     *
     */

    public boolean isLowValueInclusive() {
        return lowValidValueInclusive_;
    }

    /** Retrieve the flag indicating if the high value should be applied as
     * inclusive or exclusive.
     *
     * @return boolean indicating if the high value is inclusive.
     *
     */

    public boolean isHighValueInclusive() {
        return highValidValueInclusive_;
    }

    /** Retrieve the flag indicating if the low value should be applied to the
     * calibrated or the raw value.
     *
     * @return boolean indicating if the value should be applied to the
     * calibrated form.
     *
     */

    public boolean isLowValueCalibrated() {
        return lowAppliesToCalibrated_;
    }

    /** Retrieve the flag indicating if the high value should be applied to the
     * calibrated or the raw value.
     *
     * @return boolean indicating if the value should be applied to the
     * calibrated form.
     *
     */

    public boolean isHighValueCalibrated() {
        return highAppliesToCalibrated_;
    }

    /** Sets ranging information for item value validity.
     *
     * Ranging information is optional and will be ignored if it has not been
     * setup by this method.  This is generally invoked from the function
     * setValidRangeAttributes().  The reason it is separated is so that some
     * platform specific versions of this class can be generated, although they
     * will not be a part of this toolkit.
     *
     * @param lowValidValue String containing the minimum valid value.  See the
     * other arguments for modifiers of this.
     *
     * @param highValidValue String containing the maximum valid value.  See
     * the other arguments for modifiers of this.
     *
     * @param lowValidValueInclusive boolean indicating if the minimum valid
     * value should be inclusive (if true) or exclusive (if false).  This
     * changes the behavior to &gt; or equal to in the case of true and just
     * &gt; in the event of false.
     *
     * @param highValidValueInclusive boolean indicating if the minimum valid
     * value should be inclusive (if true) or exclusive (if false).  This
     * changes the behavior to &lt; or equal to in the case of true and just
     * &lt; in the event of false.
     *
     * @param lowAppliesToCalibrated boolean indicating of the valid values are
     * to be applied to the raw/uncalibrated or EU/calibrated value when doing
     * the encode/decode operations.
     *
     * @param highAppliesToCalibrated boolean indicating of the valid values are
     * to be applied to the raw/uncalibrated or EU/calibrated value when doing
     * the encode/decode operations.
     *
     */

    protected void setValidRange( String  lowValidValue,
                                  String  highValidValue,
                                  boolean lowValidValueInclusive,
                                  boolean highValidValueInclusive,
                                  boolean lowAppliesToCalibrated,
                                  boolean highAppliesToCalibrated ) {

        lowValidValue_           = lowValidValue;
        highValidValue_          = highValidValue;
        lowValidValueInclusive_  = lowValidValueInclusive;
        highValidValueInclusive_ = highValidValueInclusive;
        lowAppliesToCalibrated_  = lowAppliesToCalibrated;
        highAppliesToCalibrated_ = highAppliesToCalibrated;
        rangeApplied_            = true;

    }

    /** Sets the Valid Range attributes of this object based on a general
     * type specification from the XTCE data model, which all inherit from the
     * NameDescriptionType.
     *
     * @param typeObj NameDescriptionType containing the type information for
     * this named and typed Parameter/Argument.
     *
     */

    private void setValidRangeAttributes( NameDescriptionType typeObj ) {

        if ( typeObj.getClass() == ParameterTypeSetType.BooleanParameterType.class ) {
            setValidRange( "0", "1", true, true, false, false );
        } else if ( typeObj.getClass() == BooleanDataType.class ) {
            setValidRange( "0", "1", true, true, false, false );
        } else if ( typeObj.getClass() == ParameterTypeSetType.IntegerParameterType.class ) {
            IntegerDataType.ValidRange rangeElement = ((ParameterTypeSetType.IntegerParameterType)typeObj).getValidRange();
            if ( rangeElement != null ) {
                setValidRange( rangeElement.getMinInclusive(),
                               rangeElement.getMaxInclusive(),
                               true,
                               true,
                               rangeElement.isValidRangeAppliesToCalibrated(),
                               rangeElement.isValidRangeAppliesToCalibrated() );
            }
        } else if ( typeObj.getClass() == ArgumentTypeSetType.IntegerArgumentType.class ) {
            IntegerDataType.ValidRange rangeElement = ((ArgumentTypeSetType.IntegerArgumentType)typeObj).getValidRange();
            if ( rangeElement != null ) {
                setValidRange( rangeElement.getMinInclusive(),
                               rangeElement.getMaxInclusive(),
                               true,
                               true,
                               rangeElement.isValidRangeAppliesToCalibrated(),
                               rangeElement.isValidRangeAppliesToCalibrated() );
            }
        } else if ( typeObj.getClass() == ParameterTypeSetType.FloatParameterType.class ) {
            FloatDataType.ValidRange rangeElement = ((ParameterTypeSetType.FloatParameterType)typeObj).getValidRange();
            if ( rangeElement != null ) {
                setFloatValidRange( rangeElement );
            }
        } else if ( typeObj.getClass() == ArgumentTypeSetType.FloatArgumentType.class ) {
            FloatDataType.ValidRange rangeElement = ((ArgumentTypeSetType.FloatArgumentType)typeObj).getValidRange();
            if ( rangeElement != null ) {
                setFloatValidRange( rangeElement );
            }
        }

    }

    /** Sets the Valid Range attributes of this object based on those that are
     * provided with a Floating Point Encoding value.
     *
     * @param rangeElement A FloatDataType.ValidRange element from the XTCE
     * data model.
     *
     */

    private void setFloatValidRange( FloatDataType.ValidRange rangeElement ) {

        // first evaluate the minimum value, if it exists, and determine if
        // it is inclusive or exclusive.  We leave it an empty string if it is
        // not applicable to this item.
        boolean minInclusive = true;
        String  minValue     = "";
        Double  min          = rangeElement.getMinInclusive();
        if ( min == null ) {
            min = rangeElement.getMinExclusive();
            if ( min != null ) {
                minInclusive = false;
            }
        }
        if ( min != null ) {
            minValue = min.toString();
        }

        // next evaluated the maximum value, if it exists, and determine if
        // it is inclusive or exclusive.  We leave it an empty string if it is
        // not applicable to this item.
        boolean maxInclusive = true;
        String  maxValue     = "";
        Double  max          = rangeElement.getMaxInclusive();
        if ( max == null ) {
            max = rangeElement.getMinExclusive();
            if ( max != null ) {
                maxInclusive = false;
            }
        }
        if ( max != null ) {
            maxValue = max.toString();
        }

        // set the valid range information from the more generic setter method
        setValidRange( minValue,
                       maxValue,
                       minInclusive,
                       maxInclusive,
                       rangeElement.isValidRangeAppliesToCalibrated(),
                       rangeElement.isValidRangeAppliesToCalibrated() );

    }

    // Private Data Members

    private NameDescriptionType typeObj_;
    private boolean             rangeApplied_            = false;
    private String              lowValidValue_           = "";
    private String              highValidValue_          = "";
    private boolean             lowValidValueInclusive_  = true;
    private boolean             highValidValueInclusive_ = true;
    private boolean             lowAppliesToCalibrated_  = false;
    private boolean             highAppliesToCalibrated_ = false;

}
