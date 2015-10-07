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

package org.omg.space.xtce.toolkit;

import java.util.ArrayList;
import java.util.List;
import org.omg.space.xtce.database.ComparisonType;
import org.omg.space.xtce.database.MetaCommandType.BaseMetaCommand.ArgumentAssignmentList.ArgumentAssignment;

/** This class represents a single data entry resolved from an XTCETMContainer,
 * which represents a SequenceContainer in the XTCE data model.
 *
 * @author David Overeem
 *
 */

public class XTCEContainerContentEntry implements Comparable {

    /** Private Default Constructor
     *
     * This is for the deepCopy() method.
     *
     */

    private XTCEContainerContentEntry() {

    }

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

    XTCEContainerContentEntry( XTCEParameter   parameter,
                               XTCETMContainer holdingContainer ) {

        itemName   = parameter.getName(); // not used yet??
        fieldType  = FieldType.PARAMETER;
        pReference = parameter;
        hContainer = holdingContainer;

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

    XTCEContainerContentEntry( XTCETMContainer container,
                               XTCETMContainer holdingContainer ) {

        itemName        = container.getName(); // not used yet??
        fieldType       = FieldType.CONTAINER;
        pReference      = null;
        tmContReference = container;
        hContainer      = holdingContainer;

    }

    /** Constructor
     *
     * This constructor is for making an entry that is based on the existence
     * of an XTCEParameter which is contained in an XTCETelecommand
     * within the container being processed as a whole.
     *
     * @param parameter XTCEParameter object containing the parameter info.
     *
     * @param tcContainer XTCETelecommand object that holds this container
     * within the container being processed.
     *
     */

    XTCEContainerContentEntry( XTCEParameter   parameter,
                               XTCETelecommand tcContainer ) {

        itemName    = parameter.getName(); // not used yet??
        fieldType   = FieldType.PARAMETER;
        pReference  = parameter;
        telecommand = tcContainer;

    }

    /** Constructor
     *
     * This constructor is for making an entry that is based on the existence
     * of an XTCEArgument which is contained in an XTCETelecommand
     * within the container being processed as a whole.
     *
     * @param argument XTCEArgument object containing the argument info.
     *
     * @param tcContainer XTCETelecommand object that holds this container
     * within the container being processed.
     *
     */

    XTCEContainerContentEntry( XTCEArgument    argument,
                               XTCETelecommand tcContainer ) {

        itemName    = argument.getName(); // not used yet??
        fieldType   = FieldType.ARGUMENT;
        aReference  = argument;
        telecommand = tcContainer;

    }

    /** Constructor
     *
     * This constructor is for making an entry that is based on the existence
     * of an XTCETCContainer which is contained in an XTCETelecommand
     * within the container being processed as a whole.
     *
     * @param container XTCETCContainer object containing the container info.
     *
     * @param tcContainer XTCETelecommand object that holds this container
     * within the container being processed.
     *
     */

    XTCEContainerContentEntry( XTCETCContainer container,
                               XTCETelecommand tcContainer ) {

        itemName         = container.getName(); // not used yet??
        fieldType        = FieldType.CONTAINER;
        tcContReference  = container;
        telecommand      = tcContainer;

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
     * @param tcContainer XTCETelecommand object that holds this container
     * within the container being processed.
     *
     */

    XTCEContainerContentEntry( String          size,
                               String          value,
                               XTCETelecommand tcContainer ) {

        itemName    = ""; // not used yet??
        fieldType   = FieldType.CONSTANT;
        telecommand = tcContainer;
        fixedSize   = size;
        long tempValueLong = Long.parseLong( value, 16 );
        entryValue  = new XTCEContainerEntryValue( Long.toString( tempValueLong ) );

    }

    /** Retrieve the Name of the item for which this entry is associated, which
     * can be an Argument, Parameter, or Container name.
     *
     * @return String containing the name.
     *
     */

    public String getName() {
        return
            XTCEFunctions.getNameFromPathReferenceString( getItemFullPath() );
    }

    /** Retrieve the Fully Qualified Name of the item for which this entry is
     * associated, which can be an Argument, Parameter, or Container name.
     *
     * @return String containing the name.
     *
     */

    public String getItemFullPath() {
        if ( fieldType == FieldType.PARAMETER ) {
            return pReference.getFullPath();
        } else if ( fieldType == FieldType.ARGUMENT ) {
            return aReference.getFullPath();
        } else if ( fieldType == FieldType.CONTAINER ) {
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

    public XTCETMContainer getTelemetryContainer() {
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

    public XTCETCContainer getTelecommandContainer() {
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

    public XTCETMContainer getHoldingContainer() {
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

    public XTCEParameter getParameter() {
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

    public XTCEArgument getArgument() {
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

    public XTCETelecommand getTelecommand() {
        return telecommand;
    }

    /** Retrieve the type of entry that this object represents in the overall
     * container as a string.
     *
     * @return String containing the text instead of the enumeration value.
     *
     */

    public String getEntryTypeString() {
        switch ( fieldType ) {
            case PARAMETER:
                return "Parameter";
            case ARGUMENT:
                return "Argument";
            case CONTAINER:
                return "Container";
            case CONSTANT:
                return "Constant";
        }
        return "";
    }

    /** Retrieve the type of entry that this object represents in the overall
     * container as the native enumeration type.
     *
     * @return FieldType containing the enumeration value.
     *
     */

    public FieldType getEntryType() {
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

    public String getRawSizeInBits() {
        if ( fieldType == FieldType.PARAMETER ) {
            return pReference.getRawSizeInBits();
        } else if ( fieldType == FieldType.ARGUMENT ) {
            return aReference.getRawSizeInBits();
        } else if ( fieldType == FieldType.CONSTANT ) {
            return fixedSize;
        }
        return "";
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

    public String getInitialValue() {
        if ( fieldType == FieldType.PARAMETER ) {
            return pReference.getInitialValue();
        } else if ( fieldType == FieldType.ARGUMENT ) {
            return aReference.getInitialValue();
        } else if ( fieldType == FieldType.CONSTANT ) {
            return entryValue.toStringWithoutParameter();
        }
        return "";
    }

    /** Retrieve the start bit that this entry occupies in the container
     * stream, or an empty string if this entry is "information only", such as
     * another container or a telecommand entry.
     *
     * @return String containing the start bit, or an empty string when it
     * is not applicable to this entry or has not yet been set.
     *
     */

    public String getStartBit() {
        return startBit;
    }

    /** Sets the start bit for this entry in the container stream.
     *
     * @param startBitValue long containing the start bit position.
     *
     */

    public void setStartBit( long startBitValue ) {
        startBit = Long.toString( startBitValue );
    }

    /** Sets the start bit for this entry in the container stream.
     *
     * @param startBitValue String containing the start bit position.
     *
     */

    public void setStartBit( String startBitValue ) {
        startBit = startBitValue;
    }

    /** Retrieves the value that is set or restricted on this entry based on
     * ArgumentAssignment, RestrictionCriteria, or user chosen set values.
     *
     * @return XTCEContainerEntryValue containing the value information for
     * this entry or a null if there is none.
     *
     */

    public XTCEContainerEntryValue getValue() {
        return entryValue;
        //if ( entryValue != null ) {
        //    return entryValue.toStringWithoutParameter();
        //}
        //return "";
    }

    /** Sets a value for this entry based on a RestrictionCriteria by passing
     * the ComparisonType from the XTCE data model.
     *
     * @param compare ComparisonType element from the XTCE data model that
     * contains the value, the operator, and the comparison flag.
     *
     */

    public void setValue( ComparisonType compare ) {

        String valueForm = ( compare.isUseCalibratedValue() ? "Calibrated" :
                                                              "Uncalibrated" );

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

    public void setValue ( ArgumentAssignment argAssign ) {

        entryValue = new XTCEContainerEntryValue( aReference,
                                                  argAssign.getArgumentValue(),
                                                  "==",
                                                  "Calibrated" );

    }

    /** Sets a value for this entry based on a XTCEContainerEntryValue from
     * this toolkit.
     *
     * @param userValue XTCEContainerEntryValue object that is typically
     * created when the user manually desires to set values on the container
     * entries.
     *
     */

    public void setValue ( XTCEContainerEntryValue userValue ) {
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

    public String getConditions() {

        if ( ( conditions == null ) || ( conditions.isEmpty() ) ) {
            return "";
        }

        StringBuilder string = new StringBuilder();

        for ( XTCEContainerEntryValue condition : conditions ) {
            string.append( condition.toString() );
            string.append( "," );
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

    public List<XTCEContainerEntryValue> getConditionList() {

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

    public void setConditionList( List<XTCEContainerEntryValue> allConditions,
                                  boolean                       entryInUse ) {

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

    public void setCondition( XTCEParameter parameter, ComparisonType compare ) {

        if ( conditions == null ) {
            conditions = new ArrayList<>();
        }

        String operator  = compare.getComparisonOperator();
        String value     = compare.getValue();
        String valueForm = ( compare.isUseCalibratedValue() ? "Calibrated" :
                                                              "Uncalibrated" );

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

    public String getRepeatParameterInfo() {
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

    public void setRepeatparameterInfo( String repeatString ) {
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
     */

    public XTCEContainerContentEntry deepCopy() {
        XTCEContainerContentEntry newOne = new XTCEContainerContentEntry();
        newOne.itemName           = itemName;
        newOne.fieldType          = fieldType;
        newOne.startBit           = startBit;
        newOne.fixedSize          = fixedSize;
        newOne.entryValue         = entryValue;
        newOne.conditions         = conditions;
        newOne.repeatParameter    = repeatParameter;
        newOne.pReference         = pReference;
        newOne.tmContReference    = tmContReference;
        newOne.hContainer         = hContainer;
        newOne.aReference         = aReference;
        newOne.tcContReference    = tcContReference;
        newOne.telecommand        = telecommand;
        newOne.isCurrentlyApplied = isCurrentlyApplied;
        return newOne;
        
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

    public boolean isCurrentlyInUse() {
        return isCurrentlyApplied;
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

    /// This attribute is the name of the Parameter or Argument or an empty
    /// string if this field is an immutable constant in the container.

    String itemName = "";

    /// This attribute indicates which kind of field this entry refers to, with
    /// valid values of Parameter, Argument, Container, or Constant.

    FieldType fieldType;

    /// The start bit of this entry in the content of the container, or an
    /// empty string if the start bit is not applicable for this entry.

    String startBit = "";

    /// Size of a fixed/constant field in the telecommand encoding

    String fixedSize = null;

    /// This is the container to hold the value for this entry in the content
    /// when the value is assigned by a RestrictionCriteria or by the user to
    /// perhaps enable an IncludeCondition to be true.

    XTCEContainerEntryValue entryValue = null;

    /// This is the container to hold the conditions for this entry, which is
    /// a list of entries varying other Parameters and values.

    List<XTCEContainerEntryValue> conditions = null;

    /// This is the repeat entry parameter count condition

    String repeatParameter = "";

    /// In the event that the fieldType = "Parameter", then this reference will
    /// contain the XTCEParameter object representing the Parameter.

    XTCEParameter pReference = null;

    /// In the event that the fieldType = "Container", then this reference will
    /// contain the XTCETMContainer object representing the Container.

    XTCETMContainer tmContReference = null;

    /// This is the immediate parent XTCETMContainer that defines this entry.

    XTCETMContainer hContainer = null;

    /// In the event that the fieldType = "Argument", then this reference will
    /// contain the XTCEArgument object representing the Argument.

    XTCEArgument aReference = null;

    /// In the event that the fieldType = "Container", then this reference will
    /// contain the XTCETMContainer object representing the Container.

    XTCETCContainer tcContReference = null;

    /// This is the telecommand parent container.

    XTCETelecommand telecommand = null;

    /// This flag indicates if the entry is currently applicable on the output,
    /// which is false if there is an include condition and that condition is
    /// not currently true.

    boolean isCurrentlyApplied = true;

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
