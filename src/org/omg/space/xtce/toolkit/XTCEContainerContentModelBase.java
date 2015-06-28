/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.omg.space.xtce.database.ComparisonType;
import org.omg.space.xtce.database.MatchCriteriaType;
import org.omg.space.xtce.database.RepeatType;
import org.omg.space.xtce.database.SequenceEntryType;
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry.FieldType;

/** The base class for the XTCE Container Content models captures the common
 * elements when processing both TM and TC containers.
 *
 * @author David Overeem
 *
 */

public class XTCEContainerContentModelBase {

    XTCEContainerContentModelBase( ArrayList<XTCESpaceSystem>         spaceSystems,
                                   ArrayList<XTCEContainerEntryValue> userValues,
                                   boolean                            showAllConditions ) {

        spaceSystems_      = spaceSystems;
        showAllConditions_ = showAllConditions;
        if ( userValues != null ) {
            userValues_ = userValues;
        }
        for ( XTCESpaceSystem spaceSystem : spaceSystems_ ) {
            spaceSystemsHashTable_.put( spaceSystem.getFullPath(),
                                        spaceSystem );
        }

    }

    /** Accessor to retrieve the list of warnings when processing this
     * container.
     *
     * @return ArrayList of strings containing the warning messages.
     *
     */

    public ArrayList<String> getWarnings() {
        return warnings_;
    }

    /** Accessor to retrieve the container content in a pre-processed series
     * of rows that can be iterated through.
     *
     * @return ArrayList of XTCEContainerContentEntry objects.
     *
     */

    public ArrayList<XTCEContainerContentEntry> getContentList() {
        return contentList_;
    }

    /** Retrieve the current applied list of user values.
     *
     * @return ArrayList of XTCEContainerEntryValue objects.
     */

    public ArrayList<XTCEContainerEntryValue> getUserValues() {
        return userValues_;
    }

    /** Retrieve the total size/length of this container in bits.
     *
     * @return long containing the bit length of the container
     *
     */

    public long getTotalSize() {
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

        final ArrayList<XTCEContainerEntryValue> conditions = entry.getConditionList();

        // short circuit if there are no conditionals to evaluate

        if ( conditions.isEmpty() == true ) {
            return true;
        }

        FieldType entryType = entry.getEntryType();
        long      satisfied = 0;

        for ( final XTCEContainerEntryValue condition : conditions ) {

            // TODO Make this so constant parameters will evaluate even if they are
            // not on the table.

            //String ref  = condition.getItemFullPath();
            //String name = XTCEFunctions.getNameFromPathReferenceString( ref );
            //String path = XTCEFunctions.getPathNameFromReferenceString( ref );

            //XTCESpaceSystem spaceSystem = spaceSystemsHashTable_.get( path );
            //if ( spaceSystem != null ) {
            //    if ( spaceSystem.isTelemetryParameter( name ) == true ) {
            //        try {
            //            XTCEParameter parameter = spaceSystem.getTelemetryParameter( name );
            //            if ( parameter.getDataSource().equals( "constant" ) == true ) {
            //                XTCEContainerEntryValue val =
            //                    new XTCEContainerEntryValue( parameter, parameter.getInitialValue(), "==", "Calibrated" );
            //                if ( val.toStringWithoutParameter().equals( condition.toStringWithoutParameter() ) == true ) {
            //                    ++satisfied;
            //                    continue;
            //                }
            //            }
            //        } catch ( XTCEDatabaseException ex ) {
            //            // this can't happen because if the true test above
            //        }
            //    }
            //}

            for ( final XTCEContainerContentEntry listEntry : contentList_ ) {
                final String entryValue = listEntry.getValue();
                if ( ( entryValue == null ) || ( entryValue.isEmpty() == true ) ) {
                    continue;
                }
                if ( entryValue.equals( condition.toStringWithoutParameter() ) == true ) {
                    ++satisfied;
                }
            }
        }

        //System.out.println( "Conditions Satisfied " + Long.toString( satisfied ) + " of " + Long.toString( conditions.size() ) );
        return ( satisfied == conditions.size() );

    }

    protected void reorderItemsByStartBit() {

        ArrayList<XTCEContainerContentEntry> tempHoldingList =
            new ArrayList<XTCEContainerContentEntry>();
        String sbText = Long.toString( Long.MAX_VALUE );

        for ( int iii = ( contentList_.size() - 1 ); iii >= 0; --iii ) {
            XTCEContainerContentEntry item = contentList_.get( iii );
            if ( item.getStartBit().isEmpty() == true ) {
                if ( iii < ( contentList_.size() - 1 ) ) {
                    item.setStartBit( sbText );
                    tempHoldingList.add( item );
                }
            } else {
                sbText = item.getStartBit();
            }
        }

        Collections.sort( contentList_ );

        for ( XTCEContainerContentEntry item : tempHoldingList ) {
            item.setStartBit( "" );
        }

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

                XTCEParameter parameterInstance = findParameter( parameterRef,
                                                                 contentEntry.getTelemetryContainer() );

                contentEntry.setRepeatparameterInfo( "==" +
                                                     parameterInstance.getName() +
                                                     ( useCalValue == true ? "{cal}" : "{uncal}" ) );

            } catch ( Exception ex ) {

                warnings_.add( "Element RepeatEntry/DynamicEntry could not be evaluated for: " +
                               contentEntry.itemName +
                               " because: " +
                               ex.getLocalizedMessage() );

            }

        } else {

            warnings_.add( "Element RepeatEntry/DiscreteLookupList not yet supported for: " +
                           contentEntry.itemName );

        }

        return 1;

    }

    protected void applyRestrictions( XTCENamedObject   container,
                                      MatchCriteriaType restrictions,
                                      String            parentSpaceSystemPath ) {

        //System.out.println( container.getName() );
        //System.out.println( container.getFullPath() );

        if ( parentSpaceSystemPath == null ) {
            //System.out.println( "first one, returning" );
            return;
        }

        //System.out.println( parentSpaceSystemPath );

        if ( restrictions == null ) {
            //System.out.println( "no restrictions, returning" );
            return;
        }

        if ( restrictions.getComparisonList() != null ) {

            List<ComparisonType> list = restrictions.getComparisonList().getComparison();
            for ( ComparisonType compare : list ) {
                applyRestriction( compare,
                                  container );
            }

        } else if ( restrictions.getComparison() != null ) {

            applyRestriction( restrictions.getComparison(),
                              container );

        } else if ( restrictions.getBooleanExpression() != null ) {

            warnings_.add( "Element RestrictionCriteria/BooleanExpression in container " +
                           container.getName() +
                           " is not yet supported" );

        } else if ( restrictions.getCustomAlgorithm() != null ) {

            warnings_.add( "Element RestrictionCriteria/CustomAlgorithm in container " +
                           container.getName() +
                           " is not yet supported" );

        } else {

            warnings_.add( "Element RestrictionCriteria in container " +
                           container.getName() +
                           " is incomplete" );

        }

    }

    protected void applyRestriction( ComparisonType  compare,
                                     XTCENamedObject container ) {

        try {

            XTCEParameter compareParameter = findParameter( compare.getParameterRef(),
                                                            container );

            boolean found = false;

            for ( XTCEContainerContentEntry entry : contentList_ ) {
                if ( entry.getEntryType() == FieldType.PARAMETER ) {
                    if ( entry.getParameter().getFullPath().equals( compareParameter.getFullPath() ) == true ) {
                        entry.setValue( compare );
                        found = true;
                    }
                }
            }

            if ( found == false ) {
                throw new XTCEDatabaseException( "Parameter does not appear in container" );
            }

        } catch ( XTCEDatabaseException ex ) {

            warnings_.add( "Adding Restriction failed for " +
                compare.getParameterRef() +
                " Reason: " +
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

            warnings_.add( "Element IncludeCondition/BooleanExpression in container " +
                           container.getName() +
                           " is not yet supported" );

        } else if ( entry.getIncludeCondition().getCustomAlgorithm() != null ) {

            warnings_.add( "Element IncludeCondition/CustomAlgorithm in container " +
                           container.getName() +
                           " is not yet supported" );

        } else {

            warnings_.add( "Element RestrictionCriteria in container " +
                           container.getName() +
                           " is incomplete" );

        }

    }

    protected void addIncludeCondition( ComparisonType            compare,
                                        XTCENamedObject           container,
                                        XTCEContainerContentEntry content ) {

        try {

            XTCEParameter compareParameter = findParameter( compare.getParameterRef(),
                                                            container );
            content.setCondition( compareParameter, compare );

        } catch ( XTCEDatabaseException ex ) {

            warnings_.add( "Adding Include Condition failed for " +
                compare.getParameterRef() +
                " Reason: " +
                ex.getLocalizedMessage() );

        }

    }

    protected void evaluateIncludeConditions( XTCEContainerContentEntry content ) {
        content.isCurrentlyApplied = isEntryConditionSatisfied( content );
    }

    protected XTCEParameter findParameter( String          parameterRef,
                                           XTCENamedObject currentContainer ) throws XTCEDatabaseException {

        String currentSpaceSystemPath = currentContainer.getSpaceSystemPath();
        String parameterPath = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                                   parameterRef );
        String parameterName = XTCEFunctions.getNameFromPathReferenceString( parameterPath );
        String spaceSystemPath = XTCEFunctions.getPathNameFromReferenceString( parameterPath );

        int idx = 1;

        do {

            XTCESpaceSystem spaceSystem = spaceSystemsHashTable_.get( spaceSystemPath );
            if ( spaceSystem != null ) {
                if ( spaceSystem.isTelemetryParameter( parameterName ) == true ) {
                    return spaceSystem.getTelemetryParameter( parameterName );
                }
            }

            idx = parameterPath.lastIndexOf( '/' );
            if ( idx > 0 ) {
                parameterPath = parameterPath.substring( 0, idx ) + "." + parameterPath.substring( idx + 1 );
                //System.out.println( "New search path " + parameterPath );
                parameterPath = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                                    parameterPath );
                parameterName = XTCEFunctions.getNameFromPathReferenceString( parameterPath );
                spaceSystemPath = XTCEFunctions.getPathNameFromReferenceString( parameterPath );
            }

        } while ( idx > 0 );

        throw new XTCEDatabaseException( "Cannot find Parameter Reference " +
                                         parameterRef +
                                         " in container " +
                                         currentContainer.getName() );

    }

    protected XTCETMContainer findContainer( String          containerRef,
                                             XTCENamedObject currentContainer ) throws XTCEDatabaseException {

        String currentSpaceSystemPath = currentContainer.getSpaceSystemPath();
        String containerPath = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                                   containerRef );
        String containerName = XTCEFunctions.getNameFromPathReferenceString( containerPath );
        String spaceSystemPath = XTCEFunctions.getPathNameFromReferenceString( containerPath );

        XTCESpaceSystem spaceSystem = spaceSystemsHashTable_.get( spaceSystemPath );
        if ( spaceSystem != null ) {
            return spaceSystem.getContainer( containerName );
        }

        throw new XTCEDatabaseException( "Space System " +
                                         spaceSystemPath +
                                         " not found when looking for container " +
                                         containerName );

    }

    protected XTCETelecommand findTelecommand( String          metaCommandRef,
                                               XTCENamedObject currentContainer ) throws XTCEDatabaseException {

        String currentSpaceSystemPath = currentContainer.getSpaceSystemPath();
        String containerPath = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                                   metaCommandRef );
        String containerName = XTCEFunctions.getNameFromPathReferenceString( containerPath );
        String spaceSystemPath = XTCEFunctions.getPathNameFromReferenceString( containerPath );

        XTCESpaceSystem spaceSystem = spaceSystemsHashTable_.get( spaceSystemPath );
        if ( spaceSystem != null ) {
            return spaceSystem.getTelecommand( containerName );
        }

        throw new XTCEDatabaseException( "Space System " +
                                         spaceSystemPath +
                                         " not found when looking for telecommand " +
                                         containerName );

    }

    protected void applyUserValues( XTCEContainerContentEntry entry ) {
        if ( userValues_.isEmpty() == true ) {
            return;
        }
        if ( entry.getEntryType() == FieldType.PARAMETER ) {
            for ( XTCEContainerEntryValue value : userValues_ ) {
                if ( value.getItemFullPath().equals( entry.getParameter().getFullPath() ) == true ) {
                    entry.setValue( value );
                    return;
                }
            }
        } else if ( entry.getEntryType() == FieldType.ARGUMENT ) {
            for ( XTCEContainerEntryValue value : userValues_ ) {
                if ( value.getItemFullPath().equals( entry.getArgument().getFullPath() ) == true ) {
                    entry.setValue( value );
                    return;
                }
            }
        }
        return;

    }

    protected void addStartBit( SequenceEntryType         pRefEntry,
                                XTCEContainerContentEntry contentEntry,
                                RunningStartBit           currentStartBit,
                                long                      containerStartBit ) {

        // not present means 0 from previous entry
        String refLocation = "previousEntry";
        String offsetInBits = "0";

        if ( pRefEntry.getLocationInContainerInBits() != null ) {
            refLocation = pRefEntry.getLocationInContainerInBits().getReferenceLocation();
            if ( pRefEntry.getLocationInContainerInBits().getFixedValue() != null ) {
                offsetInBits = pRefEntry.getLocationInContainerInBits().getFixedValue();
            } else if ( pRefEntry.getLocationInContainerInBits().getDynamicValue() != null ) {
                warnings_.add( "Element LocationInContainerInBits/DynamicValue not yet supported for: " + contentEntry.itemName );
            } else if ( pRefEntry.getLocationInContainerInBits().getDiscreteLookupList() != null ) {
                warnings_.add( "Element LocationInContainerInBits/DiscreteLookupList not yet supported for: " + contentEntry.itemName );
            }
        }

        long offsetInBitsLong  = Long.parseLong( offsetInBits );
        long rawSizeInBitsLong = 0;
        if ( contentEntry.getRawSizeInBits().isEmpty() == false ) {
            rawSizeInBitsLong = Long.parseLong( contentEntry.getRawSizeInBits() );
        }

        if ( refLocation.equals( "previousEntry" ) == true ) {
            long start = currentStartBit.get() + offsetInBitsLong;
            //System.out.println( "previousEntry offset resolved to " + Long.toString( start ) );
            if ( isEntryNeedingStartBit( contentEntry ) == true ) {
                contentEntry.setStartBit( start );
            }
            currentStartBit.set( start + rawSizeInBitsLong );
        } else if ( refLocation.equals( "containerStart" ) == true ) {
            long start = containerStartBit + offsetInBitsLong;
            //System.out.println( "containerStart offset resolved to " + Long.toString( start ) );
            if ( isEntryNeedingStartBit( contentEntry ) == true ) {
                contentEntry.setStartBit( start );
            }
            currentStartBit.set( start + rawSizeInBitsLong );
        } else if ( refLocation.equals( "containerEnd") == true ) {
            if ( isEntryNeedingStartBit( contentEntry ) == true ) {
                contentEntry.setStartBit( "E" + Long.toString( offsetInBitsLong ) );
            }
            // do not set the entry to follow here???
            // could be a problem if someone does previousEntry...
        } else if ( refLocation.equals( "nextEntry" ) == true ){
            warnings_.add( "Element LocationInContainerInBits/@nextEntry not yet supported for: " + contentEntry.itemName );
        }

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
                 ( contentList_.get( iii ).getStartBit().startsWith( "E" ) == false ) ) {
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
        ArrayList<XTCEContainerContentEntry> endList =
            new ArrayList<XTCEContainerContentEntry>();

        // walk backwards through the content removing entries that are for the
        // containerEnd and add those to a temporary list.

        //System.out.println( "Processing end from index " + Long.toString( containerStartIndex + 1 ) + " to " + Long.toString( contentList_.size() - 1 ) );

        for ( int iii = contentList_.size() - 1; iii > containerStartIndex; --iii ) {
            if ( contentList_.get( iii ).getStartBit().startsWith( "E" ) == true ) {
                endList.add( contentList_.get( iii ) );
                contentList_.remove( contentList_.get( iii ) );
            }
        }

        // now compute the start bit for the containerEnd temporaries and add
        // them back to the content list at the end.

        for ( XTCEContainerContentEntry entry : endList ) {
            String sb      = entry.getStartBit().replaceFirst( "E", "" );
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

    protected void checkForOverlaps() {

        HashMap<Long, String> usageMap = new HashMap<Long, String>();
        for ( XTCEContainerContentEntry entry : contentList_ ) {
            if ( ( entry.getStartBit().isEmpty()      == false ) &&
                 ( entry.getRawSizeInBits().isEmpty() == false ) ) {
                long startBit   = Long.parseLong( entry.getStartBit() );
                long sizeInBits = Long.parseLong( entry.getRawSizeInBits() );
                for ( long iii = startBit; iii < ( startBit + sizeInBits ); ++iii ) {
                    Long bit = Long.valueOf( iii );
                    if ( usageMap.containsKey( bit ) == true ) {
                        warnings_.add( "Container Item " +
                                       entry.itemName +
                                       " overlaps " +
                                       usageMap.get( bit ) +
                                       " at bit " +
                                       bit.toString() );
                    } else {
                        usageMap.put( bit, entry.itemName );
                    }
                }
            }
        }

    }

    /// List of warning messages collected when processing this container.

    protected ArrayList<String> warnings_ = new ArrayList<String>();

    /// The list of userChosenValues to apply to the model built from the
    /// provided container.

    private ArrayList<XTCEContainerEntryValue> userValues_ =
        new ArrayList<XTCEContainerEntryValue>();

    /// The list of container contents.  This can be container entries,
    /// Parameter entries, Argument entries, and Fixed Value entries.

    protected ArrayList<XTCEContainerContentEntry> contentList_ =
        new ArrayList<XTCEContainerContentEntry>();

    /// A list of references to each of the XTCESpaceSystem objects that are a
    /// part of the data for this XTCE file.

    protected ArrayList<XTCESpaceSystem> spaceSystems_ = null;

    /// A hashed version of the Space System list to speed up searching for
    /// Parameter Members at depth.

    protected HashMap<String, XTCESpaceSystem> spaceSystemsHashTable_ =
        new HashMap<String, XTCESpaceSystem>();

    /// Total Size of this container in bits

    protected long totalContainerSize_ = 0;

    /// Flag indicating if false conditional containers should be drilled into
    /// when processing, saves a lot of time if not done for certain packets.

    boolean showAllConditions_ = false;

    /** This class is a holder for the running start bit so that it can be
     * updated in a reference that is passed down several functions deep.
     *
     */

    protected class RunningStartBit {

        public long get() {
            return currentStartBit;
        }

        public void set( long value ) {
            currentStartBit = value;
        }

        public void add( long sizeValue ) {
            currentStartBit += sizeValue;
        }

        private long currentStartBit = 0;

    }

}
