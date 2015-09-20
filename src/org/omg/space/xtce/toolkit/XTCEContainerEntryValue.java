/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.util.BitSet;

/** This class represents the value of an entry in the container model when
 * processing a specific Container in the XTCE data model.
 *
 * @author David Overeem
 *
 */

public class XTCEContainerEntryValue {

    /** Constructor
     *
     * This constructs an instance of the class to represent an assigned or
     * chosen value for an XTCEParameter and whether or not it is Calibrated.
     *
     * @param item The parameter or argument to keep this value for.
     *
     * @param value The value that should be used for this Parameter.
     *
     * @param operator The comparison operator that is used for assigning this
     * value, which can be "==", "!=", and others - refer to the XTCE Schema.
     *
     * @param form The form of the value, either "Calibrated" or "Uncalibrated".
     *
     */

    public XTCEContainerEntryValue( XTCETypedObject item,
                                    String          value,
                                    String          operator,
                                    String          form ) {

        name_                     = item.getFullPath();
        value_                    = value;
        operator_                 = operator;
        form_                     = form;
        item_                     = item;
        toStringWithoutParameter_ = operator_ + value_ +
            ( form_.equals( "Calibrated" ) == true ? "{cal}" : "{uncal}" );

    }

    /** Constructor
     *
     * This constructs an instance of the class to represent an assigned or
     * chosen value for an FixedValue field, which is always calibrated.
     *
     * @param value The value that should be used for this Parameter.
     *
     */

    public XTCEContainerEntryValue( String value ) {

        name_                     = "";
        value_                    = value;
        operator_                 = "==";
        form_                     = "Calibrated";
        toStringWithoutParameter_ = operator_ + value_ + "{cal}";

    }

    /** Constructor
     *
     * This constructs an instance of the class to represent a binary decoded
     * value from a binary stream BitSet, which is always a raw value.
     *
     * @param item XTCETypedObject representing the parameter or argument in
     * the XTCE document.
     *
     * @param rawValue BitSet containing the raw encoded bits for this item.
     *
     */

    public XTCEContainerEntryValue( XTCETypedObject item,
                                    BitSet          rawValue ) {

        item_                     = item;
        name_                     = item.getFullPath();
        itemValueObj_             = new XTCEItemValue( item );
        rawValue_                 = rawValue;
        value_                    = itemValueObj_.decode( rawValue );
        operator_                 = "==";
        form_                     = "Calibrated";
        toStringWithoutParameter_ = operator_ + value_ + "{cal}";

    }

    /** Retrieve the Name of the item for which this value is associated, which
     * can be an Argument or Parameter name.
     *
     * @return String containing the name.
     *
     */

    public String getName() {
        return XTCEFunctions.getNameFromPathReferenceString( name_ );
    }

    /** Retrieve the fully qualified name of the Parameter/Argument that is
     * attached to this value.
     *
     * At present, the argument will not be a path because it is local to the
     * telecommand that defines it.  A fixed value entry field will return an
     * empty string.
     *
     * @return String containing the fully qualified path to the name.
     *
     */

    public String getItemFullPath() {
        return name_;
    }

    /** Retrieve the value that is associated with this Parameter/Argument
     * entry.
     *
     * @return String containing the value, which can be either in engineering
     * or raw form.
     *
     */

    public String getAssignedValue() {
        return value_;
    }

    /** Retrieve the assigning/comparison operator that associates the value to
     * the Parameter/Argument that is represented by this value object.
     *
     * @return String containing the operator, which can be ==, !=, &lt;=,
     * &lt;, &gt;=, &gt;.
     *
     */

    public String getOperator() {
        return operator_;
    }

    /** Retrieve the form of the value that is associated with this entry.
     *
     * @return String containing with "Calibrated" or "Uncalibrated".
     *
     */

    public String getComparisonForm() {
        return form_;
    }

    /** Retrieve the EU/Calibrated value of the entry, regardless of how it
     * was assigned.
     *
     * @return String containing the EU/Calibrated value.
     *
     */

    public String getCalibratedValue() {

        if ( form_.equals( "Calibrated" ) == true ) {
            return value_;
        } else {
            if ( itemValueObj_ == null ) {
                itemValueObj_ = new XTCEItemValue( item_ );
            }
            return itemValueObj_.getCalibratedFromUncalibrated( value_ );
        }

    }

    /** Retrieve the Uncalibrated value of the entry, regardless of how it
     * was assigned.
     *
     * @return String containing the Uncalibrated value.
     *
     */

    public String getUncalibratedValue() {

        if ( form_.equals( "Uncalibrated" ) == true ) {
            return value_;
        } else {
            if ( itemValueObj_ == null ) {
                itemValueObj_ = new XTCEItemValue( item_ );
            }
            return itemValueObj_.getUncalibratedFromCalibrated( value_ );
        }
    }

    /** Retrieve the Raw value of the entry, regardless of how it was assigned.
     *
     * @return BitSet containing the raw value.
     *
     */

    public BitSet getRawValue() {

        if ( rawValue_ != null ) {
            return rawValue_;
        }

        String uncalValue = getUncalibratedValue();
        if ( itemValueObj_ == null ) {
            itemValueObj_ = new XTCEItemValue( item_ );
        }

        return itemValueObj_.getRawFromUncalibrated( uncalValue );

    }

/* Right now these are not used but were prototyped for the repeat set case

    public void setValue( String value ) {
        value_ = value;
    }

    public void setOperator( String operator ) {
        operator_ = operator;
    }

    public void setComparisonForm( String form ) {
        form_ = form;
    }
*/

    /** Compatibility Operator
     *
     * This method compares this entry value with another entry value to see if
     * they are "compatible".  They are compatible if equal, taking into
     * account that they may be in different forms (uncalibrated versus
     * calibrated).  They may also have different operators, so in the case
     * where I have this object being equal to a value, it could be compatible
     * with another object which is not equal to any value except this one.
     *
     * @param that XTCEContainerEntryValue to compare to.
     *
     * @return boolean indicating if there is compatibility between these two
     * entry values in a container.
     *
     */

    public boolean isCompatibleWith( XTCEContainerEntryValue that ) {

        // if these are exactly the same object on the Java heap then they must
        // be compatible

        if ( this == that ) {
            return true;
        }

        // next check if the parameter/argument represented is actually the
        // same, otherwise these are never compatible

        if ( this.name_.equals( that.name_) == false ) {
            return false;
        }

        // first check if the internal representation is the same, in which
        // case the test is simpler by far

        if ( this.form_.equals( that.form_ ) == true ) {
            return checkValuesCompatible( that.value_, that.operator_ );

        // otherwise we calibrated whichever of the two is uncalibrated and do
        // the comparison on the new temporary

        } else {
            if ( this.form_.equals( "Uncalibrated" ) == true ) {
                if ( itemValueObj_ == null ) {
                    itemValueObj_ = new XTCEItemValue( item_ );
                }
                String correctedValue =
                    itemValueObj_.getCalibratedFromUncalibrated( value_ );
                XTCEContainerEntryValue newObject =
                    new XTCEContainerEntryValue( item_,
                                                 correctedValue,
                                                 operator_,
                                                 "Calibrated" );
                return newObject.checkValuesCompatible( that.value_,
                                                        that.operator_ );
            } else {
                if ( itemValueObj_ == null ) {
                    itemValueObj_ = new XTCEItemValue( item_ );
                }
                String correctedValue =
                    itemValueObj_.getCalibratedFromUncalibrated( that.value_ );
                XTCEContainerEntryValue newObject =
                    new XTCEContainerEntryValue( that.item_,
                                                 correctedValue,
                                                 that.operator_,
                                                 "Calibrated" );
                return checkValuesCompatible( newObject.value_,
                                              newObject.operator_ );
            }
        }

    }

    /** Equality Operator
     *
     * Function to compare all the attributes of the entry value to determine
     * if it is equivalent to another entry value.
     *
     * @param rhs XTCEContainerEntryValue object to compare to.
     *
     * @return boolean indicating if the two value objects are functionally
     * the same.
     *
     */

    @Override
    public boolean equals( Object rhs ) {

        if ( this == rhs ) {
            return true;
        } else if ( rhs.getClass() != XTCEContainerEntryValue.class ) {
            return false;
        }

        XTCEContainerEntryValue that = (XTCEContainerEntryValue)rhs;

        return ( ( this.name_.equals( that.name_ )         == true ) &&
                 ( this.operator_.equals( that.operator_ ) == true ) &&
                 ( this.form_.equals( that.form_ )         == true ) &&
                 ( this.value_.equals( that.value_ )       == true ) );

    }

    /** Hashing Function
     *
     * Hashes the string representation of this entry value for used in certain
     * types of collections.
     *
     * @return integer containing the hash value.
     *
     */

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    /** Retrieve a string representation of the entry value that this object
     * contains.
     *
     * @return String representing the entry value, which is in the form of
     * [PARAM][OPERATOR][VALUE]{form}.
     *
     */

    @Override
    public String toString() {
        return XTCEFunctions.getNameFromPathReferenceString( name_ ) +
               toStringWithoutParameter_;
    }

    /** Retrieve a string representation of the entry value that this object
     * contains without the Parameter/Argument in the beginning.
     *
     * @return String representing the entry value, which is in the form of
     * [OPERATOR][VALUE]{form}.
     *
     */

    public String toStringWithoutParameter() {
        return toStringWithoutParameter_;
    }

    private boolean checkValuesCompatible( String otherValue,
                                           String otherOperator ) {

        if ( operator_.equals( "==" ) == true ) {
            if ( otherOperator.equals( "==" ) == true ) {
                return value_.equals( otherValue );
            } else if ( otherOperator.equals( "!=" ) == true ) {
                return ! value_.equals( otherValue );
            }
        } else if ( operator_.equals( "!=" ) == true ) {
            if ( otherOperator.equals( "==" ) == true ) {
                return ! value_.equals( otherValue );
            } else if ( otherOperator.equals( "!=" ) == true ) {
                return value_.equals( otherValue );
            }
        }

        // operators outside of == and != aren't yet supported

        return false;

    }

    // Private Data Members

    /// The fully qualified name of the XTCE Parameter or Argument that should
    /// be assigned this value during processing of Containers.

    private String name_ = null;

    /// The value for this XTCE Parameter or Argument that should be assigned
    /// during processing of Containers.

    private String value_ = null;

    /// The operator for setting this value, which generally is just "==".

    private String operator_ = null;

    /// Indicator for determining if this value for this Parameter or Argument
    /// is in Calibrated or Uncalibrated form.

    private String form_ = null;

    /// Prebuilt string for the toStringWithoutParameter method

    private String toStringWithoutParameter_ = null;

    /// The typed object reference

    private XTCETypedObject item_ = null;

    /// The XTCEItemValue object that gets made if needed

    private XTCEItemValue itemValueObj_ = null;

    /// The raw value if needed

    private BitSet rawValue_ = null;

}
