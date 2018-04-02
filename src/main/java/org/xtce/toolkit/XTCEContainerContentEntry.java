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

import java.util.ArrayList;
import java.util.List;
import org.omg.space.xtce.ComparisonType;
import org.omg.space.xtce.MetaCommandType.BaseMetaCommand.ArgumentAssignmentList.ArgumentAssignment;

/** This class represents a single data entry resolved from an XTCETMContainer,
 * which represents a SequenceContainer in the XTCE data model.
 *
 * @author David Overeem
 *
 */

public class XTCEContainerContentEntry implements Comparable, Cloneable {

    /** Constructor
     *
     * This constructor is for making an entry that is based on the existence
     * of an XTCEParameter which is contained in the XTCETMContainer argument
     * within the container being processed as a whole.
     *
     * @param parameter XTCEParameter object containing the parameter info.
     *
     * @param holdingContainer XTCETMContainer object that holds this parameter
     * within the container being processed.
     *
     */

    XTCEContainerContentEntry( final XTCEParameter   parameter,
                               final XTCETMContainer holdingContainer ) {

        fieldType       = FieldType.PARAMETER;
        pReference      = parameter;
        hContainer      = holdingContainer;
        tmContReference = null;
        tcContReference = null;
        aReference      = null;
        telecommand     = null;
        fixedSize       = "";

    }

    /** Constructor
     *
     * This constructor is for making an entry that is based on the existence
     * of an XTCETMContainer which is contained in another XTCETMContainer
     * within the container being processed as a whole.
     *
     * @param container XTCETMContainer object containing the container info.
     *
     * @param holdingContainer XTCETMContainer object that holds this container
     * within the container being processed.
     *
     */

    XTCEContainerContentEntry( final XTCETMContainer container,
                               final XTCETMContainer holdingContainer ) {

        fieldType       = FieldType.CONTAINER;
        pReference      = null;
        tmContReference = container;
        hContainer      = holdingContainer;
        tcContReference = null;
        aReference      = null;
        telecommand     = null;
        fixedSize       = "";

    }

    /** Constructor
     *
     * This constructor is for making an entry that is based on the existence
     * of an XTCEParameter which is contained in an XTCETelecommand
     * within the container being processed as a whole.
     *
     * @param parameter XTCEParameter object containing the parameter info.
     *
     * @param tc XTCETelecommand object that holds this container
     * within the container being processed.
     *
     */

    XTCEContainerContentEntry( final XTCEParameter   parameter,
                               final XTCETelecommand tc ) {

        fieldType       = FieldType.PARAMETER;
        pReference      = parameter;
        hContainer      = null;  // should I be doing this?
        tmContReference = null;
        tcContReference = tc.getCommandContainer();
        aReference      = null;
        telecommand     = tc;
        fixedSize       = "";

    }

    /** Constructor
     *
     * This constructor is for making an entry that is based on the existence
     * of an XTCEArgument which is contained in an XTCETelecommand
     * within the container being processed as a whole.
     *
     * @param argument XTCEArgument object containing the argument info.
     *
     * @param tc XTCETelecommand object that holds this container
     * within the container being processed.
     *
     */

    XTCEContainerContentEntry( final XTCEArgument    argument,
                               final XTCETelecommand tc ) {

        fieldType       = FieldType.ARGUMENT;
        pReference      = null;
        hContainer      = null;  // should I be doing this?
        tmContReference = null;
        tcContReference = tc.getCommandContainer();
        aReference      = argument;
        telecommand     = tc;
        fixedSize       = "";

    }

    /** Constructor
     *
     * This constructor is for making an entry that is based on the existence
     * of an XTCETCContainer which is contained in an XTCETelecommand
     * within the container being processed as a whole.
     *
     * @param container XTCETCContainer object containing the container info.
     *
     * @param tc XTCETelecommand object that holds this container
     * within the container being processed.
     *
     */

    XTCEContainerContentEntry( final XTCETCContainer container,
                               final XTCETelecommand tc ) {

        fieldType        = FieldType.CONTAINER;
        pReference       = null;
        hContainer       = null;  // should I be doing this?
        tmContReference  = null;
        aReference       = null;
        tcContReference  = container;
        telecommand      = tc;
        fixedSize        = "";

    }

    /** Constructor
     *
     * This constructor is for making an entry that is based on the existence
     * of a FixedValueEntry which is contained in an XTCETelecommand
     * within the container being processed as a whole.
     *
     * @param size String containing the size in bits of this fixed entry.
     *
     * @param value String containing the value of this fixed entry field,
     * which is assumed to be in xs:hexBinary format.
     *
     * @param tc XTCETelecommand object that holds this container
     * within the container being processed.
     *
     */

    XTCEContainerContentEntry( final String          size,
                               final String          value,
                               final XTCETelecommand tc ) {

        fieldType       = FieldType.CONSTANT;
        pReference      = null;
        hContainer      = null;  // should I be doing this?
        tmContReference = null;
        tcContReference = tc.getCommandContainer();
        aReference      = null;
        telecommand     = tc;
        fixedSize       = size;
        entryValue      = new XTCEContainerEntryValue( value );

    }

    /** Retrieve the Name of the item for which this entry is associated, which
     * can be an Argument, Parameter, or Container name.
     *
     * @return String containing the name.
     *
     */

    public final String getName() {

        switch ( fieldType ) {
            case PARAMETER:
                return pReference.getName();
            case ARGUMENT:
                return aReference.getName();
            case CONTAINER:
                if ( tmContReference != null ) {
                    return tmContReference.getName();
                } else if ( tcContReference != null ) {
                    return tcContReference.getName();
                }
        }

        return "";

    }

    /** Retrieve the Fully Qualified Name of the item for which this entry is
     * associated, which can be an Argument, Parameter, or Container name.
     *
     * @return String containing the name.
     *
     */

    public final String getItemFullPath() {

        switch ( fieldType ) {
            case PARAMETER:
                return pReference.getFullPath();
            case ARGUMENT:
                return aReference.getFullPath();
            case CONTAINER:
                if ( tmContReference != null ) {
                    return tmContReference.getFullPath();
                } else if ( tcContReference != null ) {
                    return tcContReference.getFullPath();
                }
        }

        return "";

    }

    /** Retrieve the XTCETMContainer that is represented by this entry, or a
     * null pointer if this entry is not based on an XTCETMContainer.
     *
     * It is recommended that users check the type of this entry using the
     * provided methods prior to using the getters to retrieve underlying
     * objects.
     *
     * @see #getEntryType
     *
     * @return XTCETMContainer reference to the container represented by this
     * entry or a null pointer.
     *
     */

    public final XTCETMContainer getTelemetryContainer() {
        return tmContReference;
    }

    /** Retrieve the XTCETCContainer that is represented by this entry, or a
     * null pointer if this entry is not based on an XTCETCContainer.
     *
     * It is recommended that users check the type of this entry using the
     * provided methods prior to using the getters to retrieve underlying
     * objects.
     *
     * @see #getEntryType
     *
     * @return XTCETCContainer reference to the container represented by this
     * entry or a null pointer.
     *
     */

    public final XTCETCContainer getTelecommandContainer() {
        return tcContReference;
    }

    /** Retrieve the XTCETMContainer that is represented by this entry, or a
     * null pointer if this entry is not a XTCETMContainer within another
     * XTCETMContainer.
     *
     * It is recommended that users check the type of this entry using the
     * provided methods prior to using the getters to retrieve underlying
     * objects.
     *
     * @see #getEntryType
     *
     * @return XTCETMContainer reference to the container represented by this
     * entry or a null pointer.
     *
     */

    public final XTCETMContainer getHoldingContainer() {
        return hContainer;
    }

    /** Retrieve the XTCEParameter that is represented by this entry, or a
     * null pointer if this entry is not based on an XTCEParameter.
     *
     * It is recommended that users check the type of this entry using the
     * provided methods prior to using the getters to retrieve underlying
     * objects.
     *
     * @see #getEntryType
     *
     * @return XTCEParameter reference to the parameter represented by this
     * entry or a null pointer.
     *
     */

    public final XTCEParameter getParameter() {
        return pReference;
    }

    /** Retrieve the XTCEArgument that is represented by this entry, or a
     * null pointer if this entry is not based on an XTCEArgument.
     *
     * It is recommended that users check the type of this entry using the
     * provided methods prior to using the getters to retrieve underlying
     * objects.
     *
     * @see #getEntryType
     *
     * @return XTCEArgument reference to the argument represented by this
     * entry or a null pointer.
     *
     */

    public final XTCEArgument getArgument() {
        return aReference;
    }

    /** Retrieve the XTCETelecommand that is represented by this entry, or a
     * null pointer if this entry is not based on an XTCETelecommand.
     *
     * It is recommended that users check the type of this entry using the
     * provided methods prior to using the getters to retrieve underlying
     * objects.
     *
     * @see #getEntryType
     *
     * @return XTCETelecommand reference to the telecommand represented by this
     * entry or a null pointer.
     *
     */

    public final XTCETelecommand getTelecommand() {
        return telecommand;
    }

    /** Retrieve the type of entry that this object represents in the overall
     * container as a string.
     *
     * @return String containing the text instead of the enumeration value.
     *
     */

    public final String getEntryTypeString() {

        switch ( fieldType ) {
            case PARAMETER:
                return "Parameter"; // NOI18N
            case ARGUMENT:
                return "Argument"; // NOI18N
            case CONTAINER:
                return "Container"; // NOI18N
            case CONSTANT:
                return "Constant"; // NOI18N
        }

        return ""; // NOI18N

    }

    /** Retrieve the type of entry that this object represents in the overall
     * container as the native enumeration type.
     *
     * @return FieldType containing the enumeration value.
     *
     */

    public final FieldType getEntryType() {
        return fieldType;
    }

    /** Retrieve the size in bits that this entry occupies in the container
     * stream, or an empty string if this entry is "information only", such as
     * another container or a telecommand entry.
     *
     * @return String containing the size in bits, or an empty string when it
     * is not applicable to this entry.
     *
     */

    public final String getRawSizeInBits() {

        switch ( fieldType ) {
            case PARAMETER:
                return pReference.getRawSizeInBits();
            case ARGUMENT:
                return aReference.getRawSizeInBits();
            case CONSTANT:
                return fixedSize;
        }

        return ""; // NOI18N

    }

    /** Retrieve the initial/default value for this entry in the container
     * stream, or an empty string if none is defined.
     *
     * Note that container and telecommand entries will never have a non-empty
     * return value from this function.
     *
     * @return String containing the initial/default value, or an empty string
     * when does not exist for this entry.
     *
     */

    public final String getInitialValue() {

        switch ( fieldType ) {
            case PARAMETER:
                return pReference.getInitialValue();
            case ARGUMENT:
                return aReference.getInitialValue();
            case CONSTANT:
                return entryValue.toStringWithoutParameter();
        }

        return ""; // NOI18N
    }

    /** Retrieve the start bit that this entry occupies in the container
     * stream, or an empty string if this entry is "information only", such as
     * another container or a telecommand entry.
     *
     * @return String containing the start bit, or an empty string when it
     * is not applicable to this entry or has not yet been set.
     *
     */

    public final String getStartBit() {
        return startBit;
    }

    /** Sets the start bit for this entry in the container stream.
     *
     * @param startBitValue long containing the start bit position.
     *
     */

    public final void setStartBit( final long startBitValue ) {
        startBit = Long.toString( startBitValue );
    }

    /** Sets the start bit for this entry in the container stream.
     *
     * @param startBitValue String containing the start bit position.
     *
     */

    public final void setStartBit( final String startBitValue ) {
        startBit = startBitValue;
    }

    /** Retrieves the value that is set or restricted on this entry based on
     * ArgumentAssignment, RestrictionCriteria, or user chosen set values.
     *
     * @return XTCEContainerEntryValue containing the value information for
     * this entry or a null if there is none.
     *
     */

    public final XTCEContainerEntryValue getValue() {
        return entryValue;
    }

    /** Sets a value for this entry based on a RestrictionCriteria by passing
     * the ComparisonType from the XTCE data model.
     *
     * @param compare ComparisonType element from the XTCE data model that
     * contains the value, the operator, and the comparison flag.
     *
     */

    public final void setValue( final ComparisonType compare ) {

        String valueForm = ( compare.isUseCalibratedValue() ? "Calibrated" : // NOI18N
                                                              "Uncalibrated" ); // NOI18N

        entryValue = new XTCEContainerEntryValue( pReference,
                                                  compare.getValue(),
                                                  compare.getComparisonOperator(),
                                                  valueForm );

    }

    /** Sets a value for this entry based on a ArgumentAssignment by passing
     * the ArgumentAssignment from the XTCE data model.
     *
     * @param argAssign ArgumentAssignment element from the XTCE data model
     * that contains the value, the operator, and the comparison flag.
     *
     */

    public final void setValue ( final ArgumentAssignment argAssign ) {

        entryValue = new XTCEContainerEntryValue( aReference,
                                                  argAssign.getArgumentValue(),
                                                  "==", // NOI18N
                                                  "Calibrated" ); // NOI18N

    }

    /** Sets a value for this entry based on a XTCEContainerEntryValue from
     * this toolkit.
     *
     * @param userValue XTCEContainerEntryValue object that is typically
     * created when the user manually desires to set values on the container
     * entries.
     *
     */

    public final void setValue ( final XTCEContainerEntryValue userValue ) {
        entryValue = userValue;
    }

    /** Retrieve a string representation of the list of include conditions
     * that are applied to this entry in the container model.
     *
     * @return String containing a comma separated list of the conditions in
     * textual form, [PARAMETER][OPERATOR][VALUE}{form}, ...
     * (e.g. PARAM1=4{uncal},PARAM2=HELLO{cal}).
     *
     */

    public final String getConditions() {

        if ( ( conditions == null ) || ( conditions.isEmpty() ) ) {
            return ""; // NOI18N
        }

        StringBuilder string = new StringBuilder();

        for ( XTCEContainerEntryValue condition : conditions ) {
            string.append( condition.toString() );
            string.append( "," ); // NOI18N
        }

        return string.deleteCharAt( string.length() - 1).toString();

    }

    /** Retrieve the list of include conditions that are applied to this entry
     * in the container model.
     *
     * @return List of XTCEContainerEntryValue conditions objects.  The
     * list will never be null, but is often empty.
     *
     */

    public final List<XTCEContainerEntryValue> getConditionList() {

        if ( conditions == null ) {
            return new ArrayList<>();
        }

        return conditions;

    }

    /** Sets the include condition list based on it already being evaluated.
     *
     * This is effectively setting all the conditions at once, which is useful
     * for Members of an Aggregate or repeating entries where the list need not
     * be evaluated again.  The second argument is the "in use" flag.  This is
     * explained further.
     *
     * @see #isCurrentlyInUse()
     *
     * @param allConditions List of XTCEContainerEntryValue conditions to
     * set the list.
     *
     * @param entryInUse boolean indicating if this entry passes the include
     * conditions and should be set to be in use.
     *
     */

    public final void setConditionList( final List<XTCEContainerEntryValue> allConditions,
                                        final boolean                       entryInUse ) {

        if ( allConditions == null ) {
            return;
        }

        if ( conditions == null ) {
            conditions = new ArrayList<>();
        }

        conditions.addAll( allConditions );

        isCurrentlyApplied = entryInUse;

    }

    /** Sets the include condition for this entry, which is always based on a
     * Parameter in a ComparisonType element from the XTCE data model.
     *
     * @param parameter XTCEParameter that is being compared in the condition.
     *
     * @param compare ComparisonType element that contains the operator, value,
     * and form of comparison.
     *
     */

    public final void setCondition( final XTCEParameter  parameter,
                                    final ComparisonType compare ) {

        if ( conditions == null ) {
            conditions = new ArrayList<>();
        }

        String operator  = compare.getComparisonOperator();
        String value     = compare.getValue();
        String valueForm = ( compare.isUseCalibratedValue() ? "Calibrated" : // NOI18N
                                                              "Uncalibrated" ); // NOI18N

        conditions.add( new XTCEContainerEntryValue( parameter,
                                                     value,
                                                     operator,
                                                     valueForm ) );

    }

    /** Retrieve the repeating condition for this entry.
     *
     * The repeating condition is only set when it is not a fixed repeat count.
     * When it is a fixed repeat count, this entry will just be repeated that
     * number of times in the content model.
     *
     * @return String containing the repeating entry condition, which will be
     * in the form of [operator][parameter][form] (e.g. ==PARM1{cal}).
     *
     */

    public final String getRepeatParameterInfo() {
        return repeatParameter;
    }

    /** Sets the repeating condition for this entry.
     *
     * The repeating condition is only set when it is not a fixed repeat count.
     * When it is a fixed repeat count, this entry will just be repeated that
     * number of times in the content model.
     *
     * @param repeatString String containing the repeating entry condition,
     * which must be in the form of [operator][parameter][form]
     * (e.g. ==PARM1{cal}).
     *
     */

    public final void setRepeatparameterInfo( final String repeatString ) {
        repeatParameter = repeatString;
    }

    /** Make a copy of this entry by copying all the references included.
     *
     * This function is useful for making high speed copies of this entry
     * without performing all the initialization logic again.  It is used when
     * parameters/arguments/containers are repeating.  Note that saying this is
     * a "deep copy" is not actually the case.
     *
     * @return XTCEContainerContentEntry copy of this entry.
     *
     * @throws CloneNotSupportedException in the event that this object cannot
     * be cloned.
     *
     */

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /** Retrieve the "in use" attribute of this entry.
     *
     * The "in use" attribute tells the caller if this entry is currently an
     * active participant in the container.  An entry is not in use when it has
     * a conditional that is NOT satisfied.
     *
     * @return boolean indicating if the entry is currently in use.
     *
     */

    public final boolean isCurrentlyInUse() {
        return isCurrentlyApplied;
    }

    /** Sets the "in use" attribute of this entry.
     *
     * The "in use" attribute tells the caller if this entry is currently an
     * active participant in the container.  An entry is not in use when it has
     * a conditional that is NOT satisfied.
     *
     * @param useFlag boolean indicating that this entry is currently used in
     * the container definition for which it is included.
     *
     */

    public final void setCurrentlyInUse( final boolean useFlag ) {
        isCurrentlyApplied = useFlag;
    }

    /** Comparison Operator
     *
     * This function is used for sorting XTCEContainerContentEntry by their
     * start bit while safely ignoring those that do not have a start bit set.
     *
     * @see Comparable
     *
     * @param obj XTCEContainerContentEntry to compare to.
     *
     * @return integer containing -1, 0, or 1 for less than, equal, or greater
     * than, respectively.
     *
     */

    @Override
    public int compareTo( Object obj ) {

        if ( (Object)this == obj ) {
            return 0;
        }

        XTCEContainerContentEntry that = (XTCEContainerContentEntry)obj;
        boolean thisEmpty = this.getStartBit().isEmpty();
        boolean thatEmpty = that.getStartBit().isEmpty();

        if ( ( thisEmpty == true ) && ( thatEmpty == true ) ) {
            return 0;
        } else if ( thisEmpty == true ) {
            return 0;
        } else if ( thatEmpty == true ) {
            return 0;
        } else {
            long lhsStartBit = Long.parseLong( this.getStartBit() );
            long rhsStartBit = Long.parseLong( that.getStartBit() );
            if ( lhsStartBit < rhsStartBit ) {
                return -1;
            } else if ( lhsStartBit > rhsStartBit ) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    /// This attribute indicates which kind of field this entry refers to, with
    /// valid values of Parameter, Argument, Container, or Constant.

    private final FieldType fieldType;

    /// Size of a fixed/constant field in the telecommand encoding

    private final String fixedSize;

    /// In the event that the fieldType = "Parameter", then this reference will
    /// contain the XTCEParameter object representing the Parameter.

    private final XTCEParameter pReference;

    /// In the event that the fieldType = "Container", then this reference will
    /// contain the XTCETMContainer object representing the Container.

    private final XTCETMContainer tmContReference;

    /// This is the immediate parent XTCETMContainer that defines this entry.

    private final XTCETMContainer hContainer;

    /// In the event that the fieldType = "Argument", then this reference will
    /// contain the XTCEArgument object representing the Argument.

    private final XTCEArgument aReference;

    /// In the event that the fieldType = "Container", then this reference will
    /// contain the XTCETMContainer object representing the Container.

    private final XTCETCContainer tcContReference;

    /// This is the telecommand parent container.

    private final XTCETelecommand telecommand;

    /// This flag indicates if the entry is currently applicable on the output,
    /// which is false if there is an include condition and that condition is
    /// not currently true.

    private boolean isCurrentlyApplied = true;

    /// The start bit of this entry in the content of the container, or an
    /// empty string if the start bit is not applicable for this entry.

    private String startBit = ""; // NOI18N

    /// This is the container to hold the value for this entry in the content
    /// when the value is assigned by a RestrictionCriteria or by the user to
    /// perhaps enable an IncludeCondition to be true.

    private XTCEContainerEntryValue entryValue = null;

    /// This is the container to hold the conditions for this entry, which is
    /// a list of entries varying other Parameters and values.

    private List<XTCEContainerEntryValue> conditions = null;

    /// This is the repeat entry parameter count condition

    private String repeatParameter = ""; // NOI18N

    /** Enumeration type to describe the nature of an entry item on a container
     * or telecommand table and/or drawing.
     *
     *
     */

    public enum FieldType {

        /** The item represented came from a Parameter in the XTCE data model.
         *
         *
         */

        PARAMETER,

        /** The item represented came from an Argument in the XTCE data model.
         *
         *
         */

        ARGUMENT,

        /** The item represented came from a FixedValue constant in the XTCE
         * data model.
         *
         *
         */

        CONSTANT,

        /** The item represented came from a SequenceContainer or a
         * CommandContainer in the XTCE data model.
         *
         *
         */

        CONTAINER

    }

}
