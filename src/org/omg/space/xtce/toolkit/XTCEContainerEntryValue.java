/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

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
     * @param parameter The parameter to keep this value for.
     *
     * @param value The value that should be used for this Parameter.
     *
     * @param operator The comparison operator that is used for assigning this
     * value, which can be "==", "!=", and others - refer to the XTCE Schema.
     *
     * @param form The form of the value, either "Calibrated" or "Uncalibrated".
     *
     */

    public XTCEContainerEntryValue( XTCEParameter parameter,
                                    String        value,
                                    String        operator,
                                    String        form ) {

        name_                     = parameter.getFullPath();
        value_                    = value;
        operator_                 = operator;
        form_                     = form;
        toStringWithoutParameter_ = operator_ + value_ +
            ( form_.equals( "Calibrated" ) == true ? "{cal}" : "{uncal}" );

    }

    /** Constructor
     *
     * This constructs an instance of the class to represent an assigned or
     * chosen value for an XTCEArgument and whether or not it is Calibrated.
     *
     * @param argument The argument to keep this value for.
     *
     * @param value The value that should be used for this Parameter.
     *
     * @param operator The comparison operator that is used for assigning this
     * value, which can be "==", "!=", and others - refer to the XTCE Schema.
     *
     * @param form The form of the value, either "Calibrated" or "Uncalibrated".
     *
     */

    public XTCEContainerEntryValue( XTCEArgument argument,
                                    String       value,
                                    String       operator,
                                    String       form ) {

        name_                     = argument.getName();
        value_                    = value;
        operator_                 = operator;
        form_                     = form;
        toStringWithoutParameter_ = operator_ + value_ +
            ( form_.equals( "Calibrated" ) == true ? "{cal}" : "{uncal}" );

    }

    /** Constructor
     *
     * This constructs an instance of the class to represent an assigned or
     * chosen value for an FixedValue field, which is always uncalibrated.
     *
     * @param value The value that should be used for this Parameter.
     *
     * @param operator The comparison operator that is used for assigning this
     * value, which can be "==", "!=", and others - refer to the XTCE Schema.
     *
     */

    public XTCEContainerEntryValue( String value ) {

        name_                     = "";
        value_                    = value;
        operator_                 = "==";
        form_                     = "Uncalibrated";
        toStringWithoutParameter_ = operator_ + value_ + "{uncal}";

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

    public String getValue() {
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
     * @return Strig containing with "Calibrated" or "Uncalibrated".
     *
     */

    public String getComparisonForm() {
        return form_;
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

}
