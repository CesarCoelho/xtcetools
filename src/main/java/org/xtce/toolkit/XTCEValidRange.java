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

package org.xtce.toolkit;

import org.omg.space.xtce.BooleanDataType;
import org.omg.space.xtce.FloatDataType;
import org.omg.space.xtce.IntegerDataType;
import org.omg.space.xtce.NameDescriptionType;

/** This class provides a simple accessor to the ValidRange element on an
 * XTCEParameter or XTCEArgument while abstracting the user away from the
 * type differences.
 *
 * @author David Overeem
 *
 */

public class XTCEValidRange {

    /** Constructor
     *
     * @param item NameDescriptionType base class reference for typed objects
     * in the XTCE data model.
     *
     */

    XTCEValidRange( final NameDescriptionType item ) {
        setValidRangeAttributes( item );
    }

    /** Retrieve the flag indicating if there is currently a ValidRange element
     * applied to the type represented in this object.
     *
     * @return boolean indicating if a ValidRange was processed.
     *
     */

    public final boolean isValidRangeApplied() {
        return rangeApplied_;
    }

    /** Retrieve the low valid valid in the allowable range.
     *
     * @return String containing the value.
     *
     */

    public final String getLowValue() {
        return lowValidValue_;
    }

    /** Retrieve the high valid valid in the allowable range.
     *
     * @return String containing the value.
     *
     */

    public final String getHighValue() {
        return highValidValue_;
    }

    /** Retrieve the flag indicating if the low value should be applied as
     * inclusive or exclusive.
     *
     * @return boolean indicating if the low value is inclusive.
     *
     */

    public final boolean isLowValueInclusive() {
        return lowValidValueInclusive_;
    }

    /** Retrieve the flag indicating if the high value should be applied as
     * inclusive or exclusive.
     *
     * @return boolean indicating if the high value is inclusive.
     *
     */

    public final boolean isHighValueInclusive() {
        return highValidValueInclusive_;
    }

    /** Retrieve the flag indicating if the low value should be applied to the
     * calibrated or the raw value.
     *
     * @return boolean indicating if the value should be applied to the
     * calibrated form.
     *
     */

    public final boolean isLowValueCalibrated() {
        return lowAppliesToCalibrated_;
    }

    /** Retrieve the flag indicating if the high value should be applied to the
     * calibrated or the raw value.
     *
     * @return boolean indicating if the value should be applied to the
     * calibrated form.
     *
     */

    public final boolean isHighValueCalibrated() {
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

    private void setValidRange( final String  lowValidValue,
                                final String  highValidValue,
                                final boolean lowValidValueInclusive,
                                final boolean highValidValueInclusive,
                                final boolean lowAppliesToCalibrated,
                                final boolean highAppliesToCalibrated ) {

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

    private void setValidRangeAttributes( final NameDescriptionType typeObj ) {

        if ( typeObj instanceof IntegerDataType ) {
            IntegerDataType.ValidRange rangeElement = ((IntegerDataType)typeObj).getValidRange();
            if ( rangeElement != null ) {
                setValidRange( rangeElement.getMinInclusive(),
                               rangeElement.getMaxInclusive(),
                               true,
                               true,
                               rangeElement.isValidRangeAppliesToCalibrated(),
                               rangeElement.isValidRangeAppliesToCalibrated() );
            }
        } else if ( typeObj instanceof FloatDataType ) {
            FloatDataType.ValidRange rangeElement = ((FloatDataType)typeObj).getValidRange();
            if ( rangeElement != null ) {
                setFloatValidRange( rangeElement );
            }
        } else if ( typeObj instanceof BooleanDataType ) {
            setValidRange( "0", "1", true, true, false, false ); // NOI18N
        }

    }

    /** Sets the Valid Range attributes of this object based on those that are
     * provided with a Floating Point Encoding value.
     *
     * @param rangeElement A FloatDataType.ValidRange element from the XTCE
     * data model.
     *
     */

    private void setFloatValidRange( final FloatDataType.ValidRange rangeElement ) {

        // first evaluate the minimum value, if it exists, and determine if
        // it is inclusive or exclusive.  We leave it an empty string if it is
        // not applicable to this item.
        boolean minInclusive = true;
        String  minValue     = ""; // NOI18N
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
        String  maxValue     = ""; // NOI18N
        Double  max          = rangeElement.getMaxInclusive();
        if ( max == null ) {
            max = rangeElement.getMaxExclusive();
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

    private boolean             rangeApplied_            = false;
    private String              lowValidValue_           = ""; // NOI18N
    private String              highValidValue_          = ""; // NOI18N
    private boolean             lowValidValueInclusive_  = true;
    private boolean             highValidValueInclusive_ = true;
    private boolean             lowAppliesToCalibrated_  = false;
    private boolean             highAppliesToCalibrated_ = false;

}
