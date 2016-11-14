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
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.omg.space.xtce.ArrayParameterRefEntryType;
import org.omg.space.xtce.ArrayParameterRefEntryType.DimensionList;
import org.omg.space.xtce.CalibratorType;
import org.omg.space.xtce.ComparisonType;
import org.omg.space.xtce.ContextCalibratorType;
import org.omg.space.xtce.IntegerValueType;
import org.omg.space.xtce.MatchCriteriaType;
import org.omg.space.xtce.RepeatType;
import org.omg.space.xtce.SequenceEntryType;
import org.xtce.toolkit.XTCEContainerContentEntry.FieldType;

/** The base class for the XTCE Container Content models captures the common
 * elements when processing both TM and TC containers.
 *
 * @author David Overeem
 *
 */

public abstract class XTCEContainerContentModelBase {

    /** Constructor
     *
     * This base constructor creates the container content model object with
     * the common content for both TM and TC container processing.
     *
     * @param container XTCETMContainer from the database object that contains
     * all the needed entry list items.
     *
     * @param spaceSystems List of XTCESpaceSystem objects to search for
     * entries on the entry list.
     *
     * @param userValues List of XTCEContainerEntryValue objects for TM
     * Parameters that are within the container.
     *
     * @param binaryData BitSet containing a map of the binary data that makes
     * up a binary instance of the container.  The first bit in the set should
     * be the zeroth bit of the container binary (start bit 0) and may be short
     * in the event that there are trailing zeros.
     *
     * @param name String containing the name of the container that the model
     * represents.
     *
     * @param desc String containing a description of the container that the
     * model represents.
     *
     * @param showAllConditions boolean indicating if unsatisfied conditional
     * includes should be pursued at depth.  This can be a performance hit if
     * there are a large number of conditionals nested.
     *
     * @throws XTCEDatabaseException in the event that the container cannot
     * be completely processed.
     *
     */

    XTCEContainerContentModelBase( List<XTCESpaceSystem>         spaceSystems,
                                   List<XTCEContainerEntryValue> userValues,
                                   BitSet                        binaryValues,
                                   String                        name,
                                   String                        desc,
                                   boolean                       showAllConditions ) {

        spaceSystems_         = spaceSystems;
        showAllConditions_    = showAllConditions;
        containerName_        = name;
        containerDescription_ = desc;

        // the user values arraylist is never null in this object
        if ( userValues != null ) {
            userValues_ = userValues;
        }

        // the binary data object can be null in this object
        if ( binaryValues != null ) {
            binaryValues_ = binaryValues;
        }

        // create a fast lookup table for Space System paths
        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            spaceSystemsHashTable_.put( spaceSystem.getFullPath(),
                                        spaceSystem );
        }

    }

    /** Accessor to retrieve the name of the container that this model
     * represents.
     *
     * @return String containing a name or an empty string if no name is used.
     *
     */

    public String getName() {
        return containerName_;
    }

    /** Accessor to retrieve a description of the container that this model
     * represents.
     *
     * @return String containing a description or an empty string if none used.
     *
     */

    public String getDescription() {
        return containerDescription_;
    }

    /** Accessor to retrieve the list of warnings when processing this
     * container.
     *
     * @return List of strings containing the warning messages.
     *
     */

    public List<String> getWarnings() {
        return warnings_;
    }

    /** Retrieve the container processing validity guess flag.
     *
     * In the event that this flag is false, there will always be a warning
     * message to retrieve from getWarnings().
     *
     * @return boolean indicating if there were issues encountered during the
     * processing that suggest that this data is not usable.
     *
     */

    public final boolean isValid() {
        return valid_;
    }

    /** Accessor to retrieve the container content in a pre-processed series
     * of rows that can be iterated through.
     *
     * @return List of XTCEContainerContentEntry objects.
     *
     */

    public List<XTCEContainerContentEntry> getContentList() {
        return contentList_;
    }

    /** Retrieve the current applied list of user values.
     *
     * @return List of XTCEContainerEntryValue objects.
     */

    public List<XTCEContainerEntryValue> getUserValues() {
        return userValues_;
    }

    /** Retrieve the total size/length of this container in bits.
     *
     * @return long containing the bit length of the container
     *
     */

    public final long getTotalSize() {
        return totalContainerSize_;
    }

    /** Sets the user preference for showing the depth into conditional
     * containers and aggregate members, including repeats, when the entry item
     * is not currently included by the include condition logic.
     *
     * @param flag boolean indicating if the depth should be shown even when
     * the conditional is evaluated to not being present in the container.
     *
     */

    public void setShowAllConditionals( boolean flag ) {
        showAllConditions_ = flag;
    }

    /** Retrieve the compatibility of this container with the raw bits provided
     * by the caller to determine if this is the right model object to match
     * the binary.
     *
     * @param rawBits BitSet containing the raw binary bits of a container.
     *
     * @return boolean indicating if this container matches the supplied binary
     * based on the inheritance model restrictions.
     *
     * @throws XTCEDatabaseException thrown in the event that the container
     * contents cannot be processed.  In the event that the container has
     * already been processed, which is usually the case, then this method
     * cannot throw.
     *
     */

    public final boolean isProcessingCompatible( BitSet rawBits )
        throws XTCEDatabaseException {

        // TODO: This method does not yet differentiate between Parameter and
        // Argument.

        List<XTCEContainerContentEntry> entries = getContentList();

        for ( XTCEContainerContentEntry entry : entries ) {

            XTCEContainerEntryValue valueObj = entry.getValue();

            if ( ( valueObj                                      != null  ) &&
                 ( valueObj.toStringWithoutParameter().isEmpty() == false ) ) {

                BitSet raw = valueObj.getRawValue();
                int    sb  = Integer.parseInt( entry.getStartBit() );
                int    nb  = Integer.parseInt( entry.getRawSizeInBits() );

                for ( int iii = 0; iii < nb; ++iii ) {
                    if ( rawBits.get( sb + nb - 1 - iii ) != raw.get( iii ) ) {
                        return false;
                    }
                }

            }

        }

        return true;

    }

    /** Encode a raw binary BitSet from the processed container content model
     * for potential file or wire transmission.
     *
     * The binary will be encoded first from the values processed with a
     * binary or user value set input provided when processing this container
     * or the default/initial values in the database for parameters that do not
     * have an assigned value during container processing.
     *
     * @return BitSet containing the raw bits representing this container and
     * the values of the parameters.
     *
     * @throws XTCEDatabaseException thrown in the event that the container
     * contents cannot be processed.  In the event that the container has
     * already been processed, which is usually the case, then this method
     * cannot throw.
     *
     */

    public final BitSet encodeContainer( ) throws XTCEDatabaseException {

        //warnings_.clear();

        BitSet rawBits = new BitSet( (int)getTotalSize() );

        List<XTCEContainerContentEntry> entries = getContentList();

        for ( XTCEContainerContentEntry entry : entries ) {

            XTCEContainerEntryValue valueObj = entry.getValue();

            if ( valueObj == null ) {
                String defaultValue = entry.getInitialValue();
                if ( defaultValue.isEmpty() == false ) {
                    if ( entry.getEntryType() == FieldType.PARAMETER ) {
                        valueObj = new XTCEContainerEntryValue( entry.getParameter(),
                                                                defaultValue,
                                                                "==",
                                                                "Calibrated" );
                    } else if ( entry.getEntryType() == FieldType.ARGUMENT ) {
                        valueObj = new XTCEContainerEntryValue( entry.getArgument(),
                                                                defaultValue,
                                                                "==",
                                                                "Calibrated" );
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }

            BitSet raw = valueObj.getRawValue();
            int    sb  = Integer.parseInt( entry.getStartBit() );
            int    nb  = Integer.parseInt( entry.getRawSizeInBits() );

            for ( int iii = 0; iii < nb; ++iii ) {
                rawBits.set( sb + nb - 1 - iii, raw.get( iii ) );
            }

            warnings_.addAll( valueObj.getWarnings() );

        }

        return rawBits;

    }

    /** Extracts the raw binary value from a container where the binary of the
     * container has been provided to this object.
     *
     * @param currentEntry XTCEContainerContentEntry representing the item
     * that is desired for extraction on this object.
     *
     * @return BitSet which is intended to represent the raw bits.
     *
     * @throws XTCEDatabaseException thrown in the event one of three things
     * occurs.  First, the binary is not provided to this object so nothing can
     * be extracted.  Second, in the event that the calculated starting bit
     * for this item exceeds the length of the binary provided.  Third if the
     * length of the item exceeds the length of the binary provided.
     *
     */

    public BitSet extractRawValue( XTCEContainerContentEntry currentEntry )
        throws XTCEDatabaseException {

        int bitLength;

        try {
            bitLength = Integer.parseInt( currentEntry.getRawSizeInBits() );
        } catch ( NumberFormatException ex ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_encdec_noraw_nosizeinbits" ) + // NOI18N
                " " + // NOI18N
                currentEntry.getName() +
                " (" + // NOI18N
                XTCEFunctions.getText( "general_numberexception" ) + // NOI18N
                " )" ); // NOI18N
        }

        if ( binaryValues_ == null ) {
            return new BitSet( bitLength );
        }

        int availBits          = binaryValues_.size();
        int availBitsLastIndex = availBits - 1;

        try {

            int startBit = Integer.parseInt( currentEntry.getStartBit() );
            if ( startBit > availBitsLastIndex ) {
                if ( exhaustedBinaryBitSupply_ == false ) {
                    exhaustedBinaryBitSupply_ = true;
                } else {
                    return new BitSet( bitLength );
                }
                throw new XTCEDatabaseException(
                    XTCEFunctions.getText( "error_encdec_binarytoosmall" ) + // NOI18N
                    " '" + // NOI18N
                    currentEntry.getName() +
                    "' (" + // NOI18N
                    XTCEFunctions.getText( "error_encdec_binarysize" ) + // NOI18N
                    " " + // NOI18N
                    Integer.toString( availBits ) +
                    " " + // NOI18N
                    XTCEFunctions.getText( "error_encdec_itemstart" ) + // NOI18N
                    " " + // NOI18N
                    Integer.toString( startBit ) +
                    " " + // NOI18N
                    XTCEFunctions.getText( "error_encdec_itemlength" ) + // NOI18N
                    " " + // NOI18N
                    Integer.toString( bitLength ) +
                    ")" ); // NOI18N
            }

            if ( ( startBit + bitLength ) > availBits ) {
                if ( exhaustedBinaryBitSupply_ == false ) {
                    exhaustedBinaryBitSupply_ = true;
                } else {
                    return new BitSet( bitLength );
                }
                throw new XTCEDatabaseException(
                    XTCEFunctions.getText( "error_encdec_binarytoosmall" ) + // NOI18N
                    " '" + // NOI18N
                    currentEntry.getName() +
                    "' (" + // NOI18N
                    XTCEFunctions.getText( "error_encdec_binarysize" ) + // NOI18N
                    " " + // NOI18N
                    Integer.toString( availBits ) +
                    " " + // NOI18N
                    XTCEFunctions.getText( "error_encdec_itemstart" ) + // NOI18N
                    " " + // NOI18N
                    Integer.toString( startBit ) +
                    " " + // NOI18N
                    XTCEFunctions.getText( "error_encdec_itemlength" ) + // NOI18N
                    " " + // NOI18N
                    Integer.toString( bitLength ) +
                    ")" ); // NOI18N
            }

            exhaustedBinaryBitSupply_ = false;

            BitSet result = new BitSet( bitLength );

            for ( int iii = 0; iii < bitLength; ++iii ) {
                result.set( iii, binaryValues_.get( startBit + bitLength - 1 - iii ) );
            }

            return result;

        } catch ( NumberFormatException ex ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_encdec_nostartbit" ) + // NOI18N
                " " + // NOI18N
                currentEntry.getName() );
        }

    }

    protected boolean isEntryNeedingStartBit( XTCEContainerContentEntry currentEntry ) {

        // no start bit if this has no size, such as an Aggregate type or a
        // container.

        if ( currentEntry.getRawSizeInBits().isEmpty() == true ) {
            return false;
        }

        // if there are no conditions, then this must be included for start
        // bit addition.

        return currentEntry.isCurrentlyInUse();
        //if ( currentEntry.getConditionList().isEmpty() == false ) {
        //    //return false;
        //    return isEntryConditionSatisfied( currentEntry );
        //}

        //return true;

    }

    protected boolean isEntryConditionSatisfied( XTCEContainerContentEntry entry ) {

        final List<XTCEContainerEntryValue> conditions =
            entry.getConditionList();

        // short circuit if there are no conditionals to evaluate, in which
        // case the conditions can be said to always be satisfied

        if ( conditions.isEmpty() == true ) {
            return true;
        }

        long satisfied = 0;

        //System.out.println( "Conditions: " +
        //                    Long.toString( conditions.size() ) +
        //                    " entry values: " +
        //                    Long.toString( contentValues_.size() ) );

        for ( final XTCEContainerEntryValue condition : conditions ) {

            // TODO Make this so constant parameters will evaluate even if they are
            // not on the table.

            for ( int iii = contentValues_.size() - 1; iii >= 0; --iii ) {
                if ( condition.getItemFullPath().equals( contentValues_.get( iii ).getItemFullPath() ) == true ) {
                    if ( contentValues_.get( iii ).isCompatibleWith( condition ) == true ) {
                        ++satisfied;
                    }
                    break;
                }
            }

        }

        //System.out.println( "Conditions Satisfied " +
        //                    Long.toString( satisfied ) +
        //                    " of " + Long.toString( conditions.size() ) );

        return ( satisfied == conditions.size() );

    }

    @SuppressWarnings("unchecked")
    protected void reorderItemsByStartBit() {

        ArrayList<XTCEContainerContentEntry> tempList = new ArrayList<>();
        String sbText = Long.toString( Long.MAX_VALUE );

        for ( int iii = ( contentList_.size() - 1 ); iii >= 0; --iii ) {
            XTCEContainerContentEntry item = contentList_.get( iii );
            if ( item.getStartBit().isEmpty() == true ) {
                if ( iii < ( contentList_.size() - 1 ) ) {
                    item.setStartBit( sbText );
                    tempList.add( item );
                }
            } else {
                sbText = item.getStartBit();
            }
        }

        // sort function makes an unchecked warning on the
        // XTCEContainerContentEntry of List<T>.  Not sure how to check this
        // yet.

        Collections.sort( contentList_ );

        for ( XTCEContainerContentEntry item : tempList ) {
            item.setStartBit( "" ); // NOI18N
        }

    }

    protected long addArrayEntryDescription( ArrayParameterRefEntryType refEntry,
                                             XTCEContainerContentEntry  contentEntry ) {

        DimensionList dimListElement = refEntry.getDimensionList();

        if ( dimListElement.getSize() == null ) {
            warnings_.add( "'DimensionList/Dimension' " + // NOI18N
                           XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                           " " + // NOI18N
                           contentEntry.getName() );
            return 1;
        }

        IntegerValueType dimSizeElement = dimListElement.getSize();

        if ( dimSizeElement.getFixedValue() != null ) {

            return Long.parseLong( dimSizeElement.getFixedValue() );

        } else if ( dimSizeElement.getDynamicValue() != null ) {

            try {

                String  parameterRef = dimSizeElement.
                                       getDynamicValue().
                                       getParameterInstanceRef().
                                       getParameterRef();

                boolean useCalValue  = dimSizeElement.
                                       getDynamicValue().
                                       getParameterInstanceRef().
                                       isUseCalibratedValue();

                XTCEParameter parameterInstance;

                // TODO: Telecommand container support will be needed here

                if ( contentEntry.getTelemetryContainer() != null ) {
                    parameterInstance = findParameter( parameterRef,
                                                       contentEntry.getTelemetryContainer() );
                } else if ( contentEntry.getHoldingContainer() != null ) {
                    parameterInstance = findParameter( parameterRef,
                                                       contentEntry.getHoldingContainer() );
                } else {
                    throw new XTCEDatabaseException(
                        XTCEFunctions.getText( "error_encdec_entrynotsupported" ) ); // NOI18N
                }

                //contentEntry.setRepeatparameterInfo( "==" +
                //                                     parameterInstance.getName() +
                //                                     ( useCalValue == true ? "{cal}" : "{uncal}" ) );

                return dynamicCountFromUserValue( parameterInstance,
                                                  ( useCalValue == true ? "Calibrated" : "Uncalibrated" ) ); // NOI18N

            } catch ( Exception ex ) {

                warnings_.add( "'DimensionList/Size/DynamicValue' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_error_evaluation" ) + // NOI18N
                               " " + // NOI18N
                               contentEntry.getName() +
                               " " + // NOI18N
                               XTCEFunctions.getText( "general_because" ) + // NOI18N
                               " " + // NOI18N
                               ex.getLocalizedMessage() );

            }

        } else {

            warnings_.add( "'DimensionList/Size/DiscreteLookupList' " + // NOI18N
                           XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                           " " + // NOI18N
                           contentEntry.getName() );

        }

        return 1;

    }

    protected long addRepeatEntryDescription( SequenceEntryType         refEntry,
                                              XTCEContainerContentEntry contentEntry ) {

        if ( refEntry.getRepeatEntry() == null ) {
            return 1;
        }

        RepeatType repeatElement = refEntry.getRepeatEntry();

        if ( repeatElement.getCount().getFixedValue() != null ) {

            return Long.parseLong( repeatElement.getCount().getFixedValue() );

        } else if ( repeatElement.getCount().getDynamicValue() != null ) {

            try {

                String  parameterRef = repeatElement.
                                       getCount().
                                       getDynamicValue().
                                       getParameterInstanceRef().
                                       getParameterRef();

                boolean useCalValue  = repeatElement.
                                       getCount().
                                       getDynamicValue().
                                       getParameterInstanceRef().
                                       isUseCalibratedValue();

                XTCEParameter parameterInstance;

                // TODO: Telecommand container support will be needed here

                if ( contentEntry.getTelemetryContainer() != null ) {
                    parameterInstance = findParameter( parameterRef,
                                                       contentEntry.getTelemetryContainer() );
                } else if ( contentEntry.getHoldingContainer() != null ) {
                    parameterInstance = findParameter( parameterRef,
                                                       contentEntry.getHoldingContainer() );
                } else {
                    throw new XTCEDatabaseException(
                        XTCEFunctions.getText( "error_encdec_entrynotsupported" ) ); // NOI18N
                }

                contentEntry.setRepeatparameterInfo( "==" + // NOI18N
                                                     parameterInstance.getName() +
                                                     ( useCalValue == true ? "{cal}" : "{uncal}" ) ); // NOI18N

                return dynamicCountFromUserValue( parameterInstance,
                                                  ( useCalValue == true ? "Calibrated" : "Uncalibrated" ) ); // NOI18N

            } catch ( Exception ex ) {

                warnings_.add( "'RepeatEntry/DynamicEntry' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_error_evaluation" ) + // NOI18N
                               " " + // NOI18N
                               contentEntry.getName() +
                               " " + // NOI18N
                               XTCEFunctions.getText( "general_because" ) + // NOI18N
                               " " + // NOI18N
                               ex.getLocalizedMessage() );

            }

        } else {

            warnings_.add( "'RepeatEntry/DiscreteLookupList' " + // NOI18N
                           XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                           " " + // NOI18N
                           contentEntry.getName() );

        }

        return 1;

    }

    protected long dynamicCountFromUserValue( XTCENamedObject item, String form ) {

        // this can probably work on contentValues_

        String paramFullPath = item.getFullPath();

        for ( XTCEContainerContentEntry entry : contentList_ ) {

            if ( ( entry.getEntryType() != FieldType.PARAMETER ) &&
                 ( entry.getEntryType() != FieldType.ARGUMENT  ) ) {
                continue;
            }

            XTCEContainerEntryValue valueObj = entry.getValue();

            if ( entry.getValue() == null ) {
                continue;
            }

            if ( entry.getItemFullPath().equals( paramFullPath ) == true ) {

                if ( valueObj.getOperator().equals( "==" ) == true ) { // NOI18N
                    try {
                        return Long.parseLong( valueObj.getCalibratedValue() );
                    } catch ( NumberFormatException ex ) {
                        warnings_.add(
                            XTCEFunctions.getText( "xml_dynamic_count_numeric_error" ) + // NOI18N
                            " " + // NOI18N
                            item.getName() +
                            " (" + // NOI18N
                            XTCEFunctions.getText( "general_value" ) + // NOI18N
                            " '" + // NOI18N
                            valueObj.getCalibratedValue() +
                            "')" ); // NOI18N
                        return 1;
                    }
                }

                warnings_.add(
                    XTCEFunctions.getText( "xml_dynamic_count_missing_error" ) + // NOI18N
                    " " + // NOI18N
                    item.getName() +
                    ", " + // NOI18N
                    XTCEFunctions.getText( "xml_dynamic_count_assume1" ) ); // NOI18N
                return 1;

            }

        }

        return 1;

    }

    protected void applyRestrictions( XTCENamedObject   container,
                                      MatchCriteriaType restrictions,
                                      String            parentSpaceSystemPath ) {

        if ( parentSpaceSystemPath == null ) {
            //System.out.println( "first one, returning" );
            return;
        }

        if ( restrictions == null ) {
            return;
        }

        if ( restrictions.getComparisonList() != null ) {

            List<ComparisonType> list =
                restrictions.getComparisonList().getComparison();

            for ( ComparisonType compare : list ) {
                applyRestriction( compare,
                                  container );
            }

        } else if ( restrictions.getComparison() != null ) {

            applyRestriction( restrictions.getComparison(),
                              container );

        } else if ( restrictions.getBooleanExpression() != null ) {

            warnings_.add( "'RestrictionCriteria/BooleanExpression' " + // NOI18N
                           XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                           " " + // NOI18N
                           container.getName() );

        } else if ( restrictions.getCustomAlgorithm() != null ) {

            warnings_.add( "'RestrictionCriteria/CustomAlgorithm' " + // NOI18N
                           XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                           " " + // NOI18N
                           container.getName() );

        } else {

            warnings_.add( "'RestrictionCriteria' " + // NOI18N
                           XTCEFunctions.getText( "xml_incomplete_element" ) + // NOI18N
                           " " + // NOI18N
                           container.getName() );

        }

    }

    protected void applyRestriction( ComparisonType  compare,
                                     XTCENamedObject container ) {

        try {

            XTCEParameter compareParameter =
                findParameter( compare.getParameterRef(), container );

            boolean found = false;

            for ( XTCEContainerContentEntry entry : contentList_ ) {
                if ( entry.getEntryType() == FieldType.PARAMETER ) {
                    if ( entry.getParameter().getFullPath().equals( compareParameter.getFullPath() ) == true ) {
                        XTCEContainerEntryValue valueObj =
                            new XTCEContainerEntryValue( entry.getParameter(),
                                                         compare.getValue(),
                                                         compare.getComparisonOperator(),
                                                         ( compare.isUseCalibratedValue() ? "Calibrated" : "Uncalibrated" ) ); // NOI18N
                        if ( entry.getValue() != null ) {
                            if ( entry.getValue().isCompatibleWith( valueObj ) == false ) {
                                warnings_.add( entry.getName() +
                                    ": " + // NOI18N
                                    XTCEFunctions.getText( "error_encdec_value_violation" ) + // NOI18N
                                    " (" + // NOI18N
                                    XTCEFunctions.getText( "error_encdec_value_request" ) + // NOI18N
                                    " '" + // NOI18N
                                    entry.getValue().getCalibratedValue() +
                                    "' " + // NOI18N
                                    XTCEFunctions.getText( "error_encdec_value_restriction" ) + // NOI18N
                                    " '" + // NOI18N
                                    valueObj.getCalibratedValue() +
                                    "')" ); // NOI18N
                                found = true;
                                valid_ = false;
                                continue;
                            }
                        }
                        entry.setValue( valueObj );
                        contentValues_.add( valueObj );
                        found = true;
                    }
                }
            }

            if ( found == false ) {
                throw new XTCEDatabaseException(
                    XTCEFunctions.getText( "error_encdec_restrict_param" ) + // NOI18N
                    " " + // NOI18N
                    compareParameter.getName() +
                    " " + // NOI18N
                    XTCEFunctions.getText( "error_encdec_restrict_cont" ) + // NOI18N
                    " " + // NOI18N
                    container.getName() );
            }

        } catch ( XTCEDatabaseException ex ) {

            warnings_.add(
                XTCEFunctions.getText( "error_encdec_restrict_failed" ) + // NOI18N
                " " + // NOI18N
                compare.getParameterRef() +
                ", " + // NOI18N
                XTCEFunctions.getText( "general_because" ) + // NOI18N
                " " + // NOI18N
                ex.getLocalizedMessage() );

        }

    }

    protected void addIncludeConditions( SequenceEntryType         entry,
                                         XTCENamedObject           container,
                                         XTCEContainerContentEntry content ) {

        if ( entry.getIncludeCondition() == null ) {
            return;
        }

        if ( entry.getIncludeCondition().getComparisonList() != null ) {

            List<ComparisonType> list = entry.getIncludeCondition().getComparisonList().getComparison();
            for ( ComparisonType compare : list ) {
                addIncludeCondition( compare, container, content );
            }

        } else if ( entry.getIncludeCondition().getComparison() != null ) {

            addIncludeCondition( entry.getIncludeCondition().getComparison(),
                                 container,
                                 content );

        } else if ( entry.getIncludeCondition().getBooleanExpression() != null ) {

            warnings_.add( "'IncludeCondition/BooleanExpression' " + // NOI18N
                           XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                           " " + // NOI18N
                           container.getName() );

        } else if ( entry.getIncludeCondition().getCustomAlgorithm() != null ) {

            warnings_.add( "'IncludeCondition/CustomAlgorithm' " + // NOI18N
                           XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                           " " + // NOI18N
                           container.getName() );

        } else {

            warnings_.add( "'IncludeCondition' " + // NOI18N
                           XTCEFunctions.getText( "xml_incomplete_element" ) + // NOI18N
                           " " + // NOI18N
                           container.getName() );

        }

    }

    protected void addIncludeCondition( ComparisonType            compare,
                                        XTCENamedObject           container,
                                        XTCEContainerContentEntry content ) {

        try {

            XTCEParameter compareParameter =
                findParameter( compare.getParameterRef(), container );

            content.setCondition( compareParameter, compare );

        } catch ( XTCEDatabaseException ex ) {

            warnings_.add(
                XTCEFunctions.getText( "error_encdec_include_failed" ) + // NOI18N
                " " + // NOI18N
                compare.getParameterRef() +
                ", " + // NOI18N
                XTCEFunctions.getText( "general_because" ) + // NOI18N
                " " + // NOI18N
                ex.getLocalizedMessage() );

        }

    }

    protected void evaluateIncludeConditions( XTCEContainerContentEntry content ) {
        content.setCurrentlyInUse( isEntryConditionSatisfied( content ) );
    }

    protected XTCEParameter findParameter( String          parameterRef,
                                           XTCENamedObject currentContainer )
        throws XTCEDatabaseException {

        String currentSpaceSystemPath = currentContainer.getSpaceSystemPath();
        String parameterPath = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                                   parameterRef );
        String parameterName = XTCEFunctions.getNameFromPathReferenceString( parameterPath );
        String spaceSystemPath = XTCEFunctions.getPathNameFromReferenceString( parameterPath );

        // TODO Problems with Aggregate inside Array Parameters not found!
        //parameterPath = parameterPath.replaceAll( "\\[[0-9]+\\]", "" );
        //System.out.println( "Looking for " + parameterPath );

        int idx;

        do {

            XTCESpaceSystem spaceSystem = spaceSystemsHashTable_.get( spaceSystemPath );
            if ( spaceSystem != null ) {
                if ( spaceSystem.isTelemetryParameter( parameterName ) == true ) {
                    return spaceSystem.getTelemetryParameter( parameterName );
                }
            }

            idx = parameterPath.lastIndexOf( '/' ); // NOI18N
            if ( idx > 0 ) {
                parameterPath = parameterPath.substring( 0, idx ) + "." + parameterPath.substring( idx + 1 ); // NOI18N
                //System.out.println( "New search path " + parameterPath );
                parameterPath = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                                    parameterPath );
                parameterName = XTCEFunctions.getNameFromPathReferenceString( parameterPath );
                spaceSystemPath = XTCEFunctions.getPathNameFromReferenceString( parameterPath );
            }

        } while ( idx > 0 );

        throw new XTCEDatabaseException(
            XTCEFunctions.getText( "error_encdec_cannot_find_parameter" ) + // NOI18N
            " " + // NOI18N
            parameterRef +
            " " + // NOI18N
            XTCEFunctions.getText( "error_encdec_in_container" ) + // NOI18N
            " " + // NOI18N
            currentContainer.getName() );

    }

    protected XTCETMContainer findContainer( String          containerRef,
                                             XTCENamedObject currentContainer )
        throws XTCEDatabaseException {

        String currentSpaceSystemPath = currentContainer.getSpaceSystemPath();
        String containerPath = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                                   containerRef );
        String containerName = XTCEFunctions.getNameFromPathReferenceString( containerPath );
        String spaceSystemPath = XTCEFunctions.getPathNameFromReferenceString( containerPath );

        XTCESpaceSystem spaceSystem = spaceSystemsHashTable_.get( spaceSystemPath );
        if ( spaceSystem != null ) {
            return spaceSystem.getContainer( containerName );
        }

        throw new XTCEDatabaseException(
            XTCEFunctions.getText( "ss_name_text" ) + // NOI18N
            " " + // NOI18N
            spaceSystemPath +
            " " + // NOI18N
            XTCEFunctions.getText( "error_encdec_looking_for_container" ) + // NOI18N
            " " + // NOI18N
            containerName );

    }

    protected XTCETelecommand findTelecommand( String          metaCommandRef,
                                               XTCENamedObject currentContainer )
        throws XTCEDatabaseException {

        String currentSpaceSystemPath = currentContainer.getSpaceSystemPath();
        String containerPath = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                                   metaCommandRef );
        String containerName = XTCEFunctions.getNameFromPathReferenceString( containerPath );
        String spaceSystemPath = XTCEFunctions.getPathNameFromReferenceString( containerPath );

        XTCESpaceSystem spaceSystem = spaceSystemsHashTable_.get( spaceSystemPath );
        if ( spaceSystem != null ) {
            return spaceSystem.getTelecommand( containerName );
        }

        throw new XTCEDatabaseException(
            XTCEFunctions.getText( "ss_name_text" ) + // NOI18N
            " " + // NOI18N
            spaceSystemPath +
            " " + // NOI18N
            XTCEFunctions.getText( "error_encdec_looking_for_telecommand" ) + // NOI18N
            " " + // NOI18N
            containerName );

    }

    protected void applyBinaryValue( XTCEContainerContentEntry entry ) {

        if ( ( binaryValues_                 == null ) ||
             ( entry.getStartBit().isEmpty() == true ) ) {
            return;
        }

        try {

            BitSet rawValue = extractRawValue( entry );

            if ( exhaustedBinaryBitSupply_ == true ) {
                return;
            }

            CalibratorType calibrator = getMatchingCalibrator( entry );

            // what to do in the case of an existing set value?

            if ( entry.getEntryType() == FieldType.PARAMETER ) {
                XTCEContainerEntryValue valueObj;
                if ( calibrator == null ) {
                    valueObj =
                        new XTCEContainerEntryValue( entry.getParameter(),
                                                     rawValue );
                } else {
                    valueObj =
                        new XTCEContainerEntryValue( entry.getParameter(),
                                                     rawValue,
                                                     calibrator );
                }
                if ( entry.getValue() != null ) {
                    if ( entry.getValue().isCompatibleWith( valueObj ) == false ) {
                        warnings_.add( entry.getName() +
                            ": " + // NOI18N
                            XTCEFunctions.getText( "error_encdec_value_violation" ) + // NOI18N
                            " (" + // NOI18N
                            XTCEFunctions.getText( "error_encdec_value_request" ) + // NOI18N
                            " '" + // NOI18N
                            entry.getValue().getCalibratedValue() +
                            "' " + // NOI18N
                            XTCEFunctions.getText( "error_encdec_value_restriction" ) + // NOI18N
                            " '" + // NOI18N
                            valueObj.getCalibratedValue() +
                            "')" ); // NOI18N
                        valid_ = false;
                        return;
                    }
                }
                entry.setValue( valueObj );
                contentValues_.add( valueObj );
            } else if ( entry.getEntryType() == FieldType.ARGUMENT ) {
                // TODO set the calibrator here if there is a matching context
                XTCEContainerEntryValue valueObj =
                    new XTCEContainerEntryValue( entry.getArgument(),
                                                 rawValue );
                if ( entry.getValue() != null ) {
                    if ( entry.getValue().isCompatibleWith( valueObj ) == false ) {
                        warnings_.add( entry.getName() +
                            ": " + // NOI18N
                            XTCEFunctions.getText( "error_encdec_value_violation" ) + // NOI18N
                            " (" + // NOI18N
                            XTCEFunctions.getText( "error_encdec_value_request" ) + // NOI18N
                            " '" + // NOI18N
                            entry.getValue().getCalibratedValue() +
                            "' " + // NOI18N
                            XTCEFunctions.getText( "error_encdec_value_restriction" ) + // NOI18N
                            " '" + // NOI18N
                            valueObj.getCalibratedValue() +
                            "')" ); // NOI18N
                        valid_ = false;
                        return;
                    }
                }
                entry.setValue( valueObj );
                contentValues_.add( valueObj );
            } else {
                // do any of the others make sense?

            }

        } catch ( XTCEDatabaseException ex ) {
            warnings_.add( ex.getLocalizedMessage() );
        }

    }

    protected void applyUserValue( XTCEContainerContentEntry entry ) {

        if ( userValues_.isEmpty() == true ) {
            return;
        }

        if ( entry.getEntryType() == FieldType.PARAMETER ) {
            for ( XTCEContainerEntryValue valueObj : userValues_ ) {
                if ( valueObj.getItemFullPath().equals( entry.getParameter().getFullPath() ) == true ) {
                    if ( entry.getValue() != null ) {
                        if ( entry.getValue().isCompatibleWith( valueObj ) == false ) {
                            warnings_.add( entry.getName() +
                                ": " + // NOI18N
                                XTCEFunctions.getText( "error_encdec_value_violation" ) + // NOI18N
                                " (" + // NOI18N
                                XTCEFunctions.getText( "error_encdec_value_request" ) + // NOI18N
                                " '" + // NOI18N
                                entry.getValue().getCalibratedValue() +
                                "' " + // NOI18N
                                XTCEFunctions.getText( "error_encdec_value_restriction" ) + // NOI18N
                                " '" + // NOI18N
                                valueObj.getCalibratedValue() +
                                "')" ); // NOI18N
                            valid_ = false;
                            return;
                        }
                    }
                    entry.setValue( valueObj );
                    contentValues_.add( valueObj );
                    return;
                }
            }
        } else if ( entry.getEntryType() == FieldType.ARGUMENT ) {
            for ( XTCEContainerEntryValue valueObj : userValues_ ) {
                if ( valueObj.getItemFullPath().equals( entry.getArgument().getFullPath() ) == true ) {
                    if ( entry.getValue() != null ) {
                        if ( entry.getValue().isCompatibleWith( valueObj ) == false ) {
                            warnings_.add( entry.getName() +
                                ": " + // NOI18N
                                XTCEFunctions.getText( "error_encdec_value_violation" ) + // NOI18N
                                " (" + // NOI18N
                                XTCEFunctions.getText( "error_encdec_value_request" ) + // NOI18N
                                " '" + // NOI18N
                                entry.getValue().getCalibratedValue() +
                                "' " + // NOI18N
                                XTCEFunctions.getText( "error_encdec_value_restriction" ) + // NOI18N
                                " '" + // NOI18N
                                valueObj.getCalibratedValue() +
                                "')" ); // NOI18N
                            valid_ = false;
                            return;
                        }
                    }
                    entry.setValue( valueObj );
                    contentValues_.add( valueObj );
                    return;
                }
            }
        }


    }

    protected void addStartBit( SequenceEntryType         pRefEntry,
                                XTCEContainerContentEntry contentEntry,
                                RunningStartBit           currentStartBit,
                                long                      containerStartBit ) {

        // not present means 0 from previous entry
        String refLocation  = "previousEntry"; // NOI18N
        long   offsetInBits = getReferenceLocationOffset( pRefEntry, contentEntry );

        if ( pRefEntry.getLocationInContainerInBits() != null ) {
            refLocation = pRefEntry.getLocationInContainerInBits().getReferenceLocation();
        }

        long rawSizeInBitsLong = 0;

        if ( contentEntry.getRawSizeInBits().isEmpty() == false ) {
            rawSizeInBitsLong = Long.parseLong( contentEntry.getRawSizeInBits() );
        }

        if ( refLocation.equals( "previousEntry" ) == true ) { // NOI18N
            long start = currentStartBit.get() + offsetInBits;
            //System.out.println( "previousEntry offset resolved to " + Long.toString( start ) );
            if ( isEntryNeedingStartBit( contentEntry ) == true ) {
                contentEntry.setStartBit( start );
            }
            currentStartBit.set( start + rawSizeInBitsLong );
        } else if ( refLocation.equals( "containerStart" ) == true ) { // NOI18N
            long start = containerStartBit + offsetInBits;
            //System.out.println( "containerStart offset resolved to " + Long.toString( start ) );
            if ( isEntryNeedingStartBit( contentEntry ) == true ) {
                contentEntry.setStartBit( start );
            }
            currentStartBit.set( start + rawSizeInBitsLong );
        } else if ( refLocation.equals( "containerEnd") == true ) { // NOI18N
            if ( isEntryNeedingStartBit( contentEntry ) == true ) {
                contentEntry.setStartBit( "E" + Long.toString( offsetInBits ) ); // NOI18N
            }
            // do not set the entry to follow here???
            // could be a problem if someone does previousEntry...
        } else if ( refLocation.equals( "nextEntry" ) == true ) { // NOI18N
            warnings_.add( "'LocationInContainerInBits/@nextEntry' " + // NOI18N
                           XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                           " " + // NOI18N
                           contentEntry.getName() );
        }

    }

    protected long evaluateContainerReferenceLocation( SequenceEntryType         cRefEntry,
                                                       XTCEContainerContentEntry contentEntry,
                                                       RunningStartBit           currentStartBit,
                                                       long                      containerStartBit ) {

        // not present means 0 from previous entry
        String refLocation  = "previousEntry"; // NOI18N
        long   offsetInBits = getReferenceLocationOffset( cRefEntry, contentEntry );

        if ( cRefEntry.getLocationInContainerInBits() != null ) {
            refLocation = cRefEntry.getLocationInContainerInBits().getReferenceLocation();
        }

        if ( refLocation.equals( "previousEntry" ) == true ) { // NOI18N
            currentStartBit.add( offsetInBits );
        } else if ( refLocation.equals( "containerStart" ) == true ) { // NOI18N
            currentStartBit.set( containerStartBit + offsetInBits );
        } else if ( refLocation.equals( "containerEnd") == true ) { // NOI18N
            warnings_.add( "'LocationInContainerInBits/@containerEnd' " + // NOI18N
                           XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                           " " + // NOI18N
                           contentEntry.getName() );
        } else if ( refLocation.equals( "nextEntry" ) == true ) { // NOI18N
            warnings_.add( "'LocationInContainerInBits/@nextEntry' " + // NOI18N
                           XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                           " " + // NOI18N
                           contentEntry.getName() );
        }

        return currentStartBit.get();

    }

    protected void processEndOfContainer( RunningStartBit currentStartBit,
                                          long            containerStartBit,
                                          long            containerStartIndex ) {

        //System.out.println( "entering process end cur start bit " +
        //                    Long.toString( currentStartBit.get() ) +
        //                    " cont start bit " +
        //                    Long.toString( containerStartBit ) );
        // correct the running start bit in case the container was out of order
        // in the containerStart reference locations.

        long containerEndBit = 0;

        for ( int iii = contentList_.size() - 1; iii > containerStartIndex; --iii ) {
            if ( ( contentList_.get( iii ).getStartBit().isEmpty()         == false ) &&
                 ( contentList_.get( iii ).getStartBit().startsWith( "E" ) == false ) ) { // NOI18N
                long startBit   = Long.parseLong( contentList_.get( iii ).getStartBit() );
                long sizeInBits = Long.parseLong( contentList_.get( iii ).getRawSizeInBits() );
                if ( containerEndBit < ( startBit + sizeInBits ) ) {
                    containerEndBit = startBit + sizeInBits;
                }
            }
        }

        if ( currentStartBit.get() < containerEndBit ) {
            currentStartBit.set( containerEndBit );
        }
        //System.out.println( "Set next start bit in end of container to " + Long.toString( currentStartBit.get() ) );
        ArrayList<XTCEContainerContentEntry> endList = new ArrayList<>();

        // walk backwards through the content removing entries that are for the
        // containerEnd and add those to a temporary list.

        //System.out.println( "Processing end from index " + Long.toString( containerStartIndex + 1 ) + " to " + Long.toString( contentList_.size() - 1 ) );

        for ( int iii = contentList_.size() - 1; iii > containerStartIndex; --iii ) {
            if ( contentList_.get( iii ).getStartBit().startsWith( "E" ) == true ) { // NOI18N
                endList.add( contentList_.get( iii ) );
                contentList_.remove( contentList_.get( iii ) );
            }
        }

        // now compute the start bit for the containerEnd temporaries and add
        // them back to the content list at the end.

        for ( XTCEContainerContentEntry entry : endList ) {
            String sb      = entry.getStartBit().replaceFirst( "E", "" ); // NOI18N
            long   offset  = Long.parseLong( sb );
            long   rawsize = Long.parseLong( entry.getRawSizeInBits() );
            entry.setStartBit( containerEndBit - offset );
            if ( currentStartBit.get() < ( containerEndBit - offset + rawsize ) ) {
                currentStartBit.set( containerEndBit - offset + rawsize );
            }
            //System.out.println( "Set next start bit for end item " + Long.toString( currentStartBit.get() ) );
            contentList_.add( entry );
        }

    }

    protected void checkForOverlaps( long totalSizeInBits ) {

        final String separator =
            System.getProperty( "line.separator" ); // NOI18N

        StringBuilder sb = new StringBuilder();

        String[] usageMap = new String[ (int)totalSizeInBits ];

        try {

            for ( XTCEContainerContentEntry entry : contentList_ ) {

                if ( ( entry.getStartBit().isEmpty()      == false ) &&
                     ( entry.getRawSizeInBits().isEmpty() == false ) ) {

                    int startBit   = Integer.parseInt( entry.getStartBit() );
                    int sizeInBits = Integer.parseInt( entry.getRawSizeInBits() );

                    for ( int iii = startBit; iii < ( startBit + sizeInBits ); ++iii ) {
                        if ( usageMap[iii] != null ) {
                            sb.append( XTCEFunctions.getText( "warning_encdec_containeritem" ) ); // NOI18N
                            sb.append( " " ); // NOI18N
                            sb.append( entry.getName() ); // NOI18N
                            sb.append( " " ); // NOI18N
                            sb.append( XTCEFunctions.getText( "warning_encdec_overlapsitem" ) ); // NOI18N
                            sb.append( " " ); // NOI18N
                            sb.append( usageMap[iii] ); // NOI18N
                            sb.append( " " ); // NOI18N
                            sb.append( XTCEFunctions.getText( "warning_encdec_atbitpos" ) ); // NOI18N
                            sb.append( " " ); // NOI18N
                            sb.append( Integer.toString( iii ) ); // NOI18N
                            sb.append( separator ); // NOI18N
                        } else {
                            usageMap[iii] = entry.getName();
                        }
                    }

                }

            }

        } catch ( ArrayIndexOutOfBoundsException ex ) {
            // ignore it for now
        }

        if ( sb.length() > 0 ) {
            warnings_.add( sb.deleteCharAt( sb.length() - 1 ).toString() );
        }

    }

    private long getReferenceLocationOffset( SequenceEntryType         refEntry,
                                             XTCEContainerContentEntry entry ) {

        if ( refEntry.getLocationInContainerInBits() != null ) {
            if ( refEntry.getLocationInContainerInBits().getFixedValue() != null ) {
                return Long.parseLong( refEntry.getLocationInContainerInBits().getFixedValue() );
            } else if ( refEntry.getLocationInContainerInBits().getDynamicValue() != null ) {
                warnings_.add( "'LocationInContainerInBits/DynamicValue' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                               " " + // NOI18N
                               entry.getName() );
            } else if ( refEntry.getLocationInContainerInBits().getDiscreteLookupList() != null ) {
                warnings_.add( "'LocationInContainerInBits/DiscreteLookupList' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                               " " + // NOI18N
                               entry.getName() );
            }
        }

        return 0;

    }

    /** Method to resolve the matching context for a series of context
     * calibrators and return the appropriate match, or the default if there is
     * no match.
     *
     * @return CalibratorType containing the calibrator to use or a null
     * pointer if there is no calibrator.
     *
     */

    private CalibratorType getMatchingCalibrator( XTCEContainerContentEntry entry )
        throws XTCEDatabaseException {

        // first check that this is a parameter

        if ( entry.getEntryType() != FieldType.PARAMETER ) {
            return null;
        }

        // get the typed object

        XTCETypedObject parameter = entry.getParameter();

        // next check to see if we have any values to compare to, otherwise
        // this can only be the default calibrator

        if ( contentValues_.isEmpty() == true ) {
            return parameter.getDefaultCalibrator();
        }

        List<ContextCalibratorType> contextCalibrators =
            parameter.getContextCalibrators();

        // if there are no context calibrators, then the default or null must
        // be the case

        if ( contextCalibrators == null ) {
            return parameter.getDefaultCalibrator();
        }

        // check each context calibrator for matching context and return the
        // first one that matches

        for ( ContextCalibratorType pair : contextCalibrators ) {

            MatchCriteriaType criteria = pair.getContextMatch();

            // retrieve the comparisons

            List<ComparisonType> compares = new ArrayList<>();
            if ( criteria.getComparison() != null ) {
                compares.add( criteria.getComparison() );
            } else if ( criteria.getComparisonList() != null ) {
                compares.addAll( criteria.getComparisonList()
                                         .getComparison() );
            }

            // loop through the various comparisons

            int matches = 0;

            for ( ComparisonType compare : compares ) {

                XTCETypedObject compareParameter;

                // TODO: Telecommand container support will be needed here

                if ( entry.getTelemetryContainer() != null ) {
                    compareParameter = findParameter( compare.getParameterRef(),
                                                      entry.getTelemetryContainer() );
                } else if ( entry.getHoldingContainer() != null ) {
                    compareParameter = findParameter( compare.getParameterRef(),
                                                      entry.getHoldingContainer() );
                } else {
                    throw new XTCEDatabaseException(
                        XTCEFunctions.getText( "error_encdec_entrynotsupported" ) ); // NOI18N
                }

                XTCEContainerEntryValue compareValue =
                    new XTCEContainerEntryValue( compareParameter,
                                                 compare.getValue(),
                                                 compare.getComparisonOperator(),
                                                 ( compare.isUseCalibratedValue() ? "Calibrated" : "Uncalibrated" ) ); // NOI18N

                // read backwards through the content values so that we act on
                // the most recent potential matching parameter report

                for ( int iii = contentValues_.size() - 1; iii >= 0; --iii ) {
                    if ( contentValues_.get( iii ).getItemFullPath().equals( compareParameter.getFullPath() ) == true ) {
                        if ( contentValues_.get( iii ).isCompatibleWith( compareValue ) == true ) {
                            ++matches;
                        }
                        break;
                    }
                }

            }

            // return the context calibrator if it matches the data in the
            // container

            if ( matches == compares.size() ) {
                return pair.getCalibrator();
            }

        }

        // this will be null if there is no default calibrator

        return parameter.getDefaultCalibrator();

    }

    /// Flag to indicate that processing has run out of available bits

    protected boolean exhaustedBinaryBitSupply_ = false;

    /// Validity flag for container processing attempts

    private boolean valid_ = true;

    /// List of warning messages collected when processing this container.

    protected ArrayList<String> warnings_ = new ArrayList<>();

    /// If a binary container was provided then it is captured here

    private BitSet binaryValues_ = null;

    /// The list of userChosenValues to apply to the model built from the
    /// provided container.

    private List<XTCEContainerEntryValue> userValues_ = new ArrayList<>();

    /// The list of container contents.  This can be container entries,
    /// Parameter entries, Argument entries, and Fixed Value entries.

    protected List<XTCEContainerContentEntry> contentList_ = new ArrayList<>();

    /// The list of container content entry values when a value is specified
    /// for an entry.

    protected List<XTCEContainerEntryValue> contentValues_ = new ArrayList<>();

    /// A list of references to each of the XTCESpaceSystem objects that are a
    /// part of the data for this XTCE file.

    protected List<XTCESpaceSystem> spaceSystems_ = null;

    /// A hashed version of the Space System list to speed up searching for
    /// Parameter Members at depth.

    private HashMap<String, XTCESpaceSystem> spaceSystemsHashTable_ =
        new HashMap<>();

    /// Total Size of this container in bits

    protected long totalContainerSize_ = 0;

    /// Flag indicating if false conditional containers should be drilled into
    /// when processing, saves a lot of time if not done for certain packets.

    protected boolean showAllConditions_ = false;

    protected String containerName_ = "";
    protected String containerDescription_ = "";

    /** This class is a holder for the running start bit so that it can be
     * updated in a reference that is passed down several functions deep.
     *
     */

    protected final class RunningStartBit {

        public final long get() {
            return currentStartBit;
        }

        public final void set( long value ) {
            currentStartBit = value;
        }

        public final void add( long sizeValue ) {
            currentStartBit += sizeValue;
        }

        private long currentStartBit = 0;

    }

}
