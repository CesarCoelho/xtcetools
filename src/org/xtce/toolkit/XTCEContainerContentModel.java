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

import java.util.BitSet;
import java.util.List;
import org.omg.space.xtce.AggregateDataType;
import org.omg.space.xtce.AggregateDataType.MemberList.Member;
import org.omg.space.xtce.ArrayDataTypeType;
import org.omg.space.xtce.ArrayParameterRefEntryType;
import org.omg.space.xtce.ContainerRefEntryType;
import org.omg.space.xtce.ContainerSegmentRefEntryType;
import org.omg.space.xtce.IndirectParameterRefEntryType;
import org.omg.space.xtce.MatchCriteriaType;
import org.omg.space.xtce.NameDescriptionType;
import org.omg.space.xtce.ParameterRefEntryType;
import org.omg.space.xtce.ParameterSegmentRefEntryType;
import org.omg.space.xtce.SequenceContainerType.BaseContainer;
import org.omg.space.xtce.SequenceEntryType;
import org.omg.space.xtce.StreamSegmentEntryType;

/** The container content model class encapsulates the pre-processing of an
 * XTCE SequenceContainer into a series of rows suitable for a simple iterator.
 *
 * @author David Overeem
 *
 */

public class XTCEContainerContentModel extends XTCEContainerContentModelBase {

    /** Constructor
     *
     * This constructor creates the container content model object with some
     * optional user values provided in a list.
     *
     * Major problems processing the container will result in an exception,
     * but many problems encountered do not inhibit continuation.  These can be
     * inspected by calling getWarnings() after this method to retrieve any
     * non-blocking issue descriptions.
     *
     * @param container XTCETMContainer from the database object that contains
     * all the needed entry list items.
     *
     * @param spaceSystems List of XTCESpaceSystem objects to search for
     * entries on the entry list.
     *
     * @param userValues List of XTCEContainerEntryValue objects for TM
     * Parameters that are within the container.  This can be null if no values
     * are needed to be passed into conditional processing.
     *
     * @param showAllConditions boolean indicating if unsatisfied conditional
     * includes should be pursued at depth.  This can be a performance hit if
     * there are a large number of conditionals nested.
     *
     * @throws XTCEDatabaseException in the event that the container cannot
     * be completely processed.
     *
     */

    public XTCEContainerContentModel( XTCETMContainer               container,
                                      List<XTCESpaceSystem>         spaceSystems,
                                      List<XTCEContainerEntryValue> userValues,
                                      boolean                       showAllConditions )
        throws XTCEDatabaseException {

        super( spaceSystems, userValues, null, showAllConditions );

        container_ = container;

        processContainer();

    }

    /** Constructor
     *
     * This constructor creates the container content model object with TM
     * parameter values evaluated from the binary provided in a BitSet.
     *
     * Major problems processing the container will result in an exception,
     * but many problems encountered do not inhibit continuation.  These can be
     * inspected by calling getWarnings() after this method to retrieve any
     * non-blocking issue descriptions.
     *
     * @param container XTCETMContainer from the database object that contains
     * all the needed entry list items.
     *
     * @param spaceSystems List of XTCESpaceSystem objects to search for
     * entries on the entry list.
     *
     * @param binaryData BitSet containing a map of the binary data that makes
     * up a binary instance of the container.  The first bit in the set should
     * be the zeroth bit of the container binary (start bit 0) and may be short
     * in the event that there are trailing zeros.
     *
     * @throws XTCEDatabaseException in the event that the container cannot
     * be completely processed.
     *
     */

    public XTCEContainerContentModel( XTCETMContainer       container,
                                      List<XTCESpaceSystem> spaceSystems,
                                      BitSet                binaryData )
        throws XTCEDatabaseException {

        super( spaceSystems, null, binaryData, false );

        container_ = container;

        processContainer();

    }

    /** Accessor to get the reference to the XTCETMContainer object that this
     * model represents.
     *
     * @return XTCETMContainer that is represented by this model object.
     *
     */

    public final XTCETMContainer getContainerReference() {
        return container_;
    }

    /** This method processes the contents of the entire container into
     * an ArrayList of XTCEContainerContentEntry objects.
     *
     * Major problems processing the container will result in an exception,
     * but many problems encountered do not inhibit continuation.  These can be
     * inspected by calling getWarnings() after this method to retrieve any
     * non-blocking issue descriptions.
     *
     * @return long containing the total length of this content/container
     * entry.
     *
     * @throws XTCEDatabaseException thrown in the event that this container
     * cannot be processed.
     *
     */

    public final long processContainer() throws XTCEDatabaseException {

        contentList_.clear();
        contentValues_.clear();
        warnings_.clear();

        //long startTime = System.currentTimeMillis();

        RunningStartBit totalSize = new RunningStartBit();

        applyCompleteContainer( container_, totalSize, 0, null, null, null );

        reorderItemsByStartBit();

        checkForOverlaps( totalSize.get() );

        //long estimatedTime = System.currentTimeMillis() - startTime;

        //System.out.println( "Processed Container in approximately " +
        //    Long.toString( estimatedTime / 1000 ) + " seconds" );

        totalContainerSize_ = totalSize.get();

        return getTotalSize();

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

        if ( contentList_.isEmpty() == true ) {
            processContainer();
        }

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

        if ( contentList_.isEmpty() == true ) {
            processContainer();
        }

        BitSet rawBits = new BitSet( (int)getTotalSize() );

        List<XTCEContainerContentEntry> entries = getContentList();

        for ( XTCEContainerContentEntry entry : entries ) {

            XTCEContainerEntryValue valueObj = entry.getValue();

            if ( valueObj == null ) {
                String defaultValue = entry.getInitialValue();
                if ( defaultValue.isEmpty() == false ) {
                    valueObj = new XTCEContainerEntryValue( entry.getParameter(),
                                                            defaultValue,
                                                            "==",
                                                            "Calibrated" );
                } else {
                    continue;
                }
            }

            BitSet rawValue = valueObj.getRawValue();
            int    startBit = Integer.parseInt( entry.getStartBit() );
            int    numBits  = Integer.parseInt( entry.getRawSizeInBits() );

            for ( int iii = 0; iii < numBits; ++iii ) {

                rawBits.set( startBit + numBits - 1 - iii,
                             rawValue.get( iii ) );

            }

        }

        return rawBits;

    }

    private void applyCompleteContainer(  XTCETMContainer           currentContainer,
                                          RunningStartBit           currentStartBit,
                                          long                      containerStartBit,
                                          MatchCriteriaType         parentRestrictions,
                                          String                    parentSpaceSystem,
                                          XTCEContainerContentEntry includedContainer )
        throws XTCEDatabaseException {

        long containerStartIndex = contentList_.size() - 1;

        applyBaseContainer( currentContainer,
                            currentStartBit,
                            containerStartBit,
                            null,
                            null,
                            includedContainer );

        processEndOfContainer( currentStartBit, containerStartBit, containerStartIndex );

    }

    private void applyBaseContainer( XTCETMContainer           currentContainer,
                                     RunningStartBit           currentStartBit,
                                     long                      containerStartBit,
                                     MatchCriteriaType         parentRestrictions,
                                     String                    parentSpaceSystem,
                                     XTCEContainerContentEntry includedContainer )
        throws XTCEDatabaseException {

        BaseContainer baseContainerRef =
            currentContainer.getSequenceContainerReference().getBaseContainer();
        String currentSpaceSystemPath = currentContainer.getSpaceSystemPath();

        // first crawl back to the deepest base container before proceeding

        if ( baseContainerRef != null ) {
            String containerPath = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                                       baseContainerRef.getContainerRef() );
            MatchCriteriaType restrictions =
                baseContainerRef.getRestrictionCriteria();
            String spaceSystemPath =
                XTCEFunctions.getPathNameFromReferenceString( containerPath );
            for ( XTCESpaceSystem spaceSystem : spaceSystems_ ) {
                if ( spaceSystemPath.equals( spaceSystem.getFullPath() ) == true ) {
                    XTCETMContainer baseContainer = spaceSystem.getContainer( containerPath );
                    applyBaseContainer( baseContainer,
                                        currentStartBit,
                                        containerStartBit,
                                        restrictions,
                                        currentSpaceSystemPath,
                                        includedContainer );
                    break;
                }
            }

        }

        //System.out.println( "Applying Current Container " +
        //                    currentContainer.getName() +
        //                    " with starting bit position " +
        //                    Long.toString( containerStartBit ) );

        applyCurrentContainer( currentContainer,
                               currentStartBit,
                               containerStartBit,
                               includedContainer );

        applyRestrictions( currentContainer,
                           parentRestrictions,
                           parentSpaceSystem );

    }

    private void applyCurrentContainer( XTCETMContainer           container,
                                        RunningStartBit           currentStartBit,
                                        long                      containerStartBit,
                                        XTCEContainerContentEntry includedContainer )
        throws XTCEDatabaseException {

        if ( includedContainer == null ) {
            contentList_.add( new XTCEContainerContentEntry( container, null ) );
        } else if ( includedContainer.getTelemetryContainer().getFullPath().equals( container.getFullPath() ) == false ) {
            contentList_.add( new XTCEContainerContentEntry( container, null ) );
        }

        List<SequenceEntryType> entryList = container.getSequenceContainerReference().getEntryList().getParameterRefEntryOrParameterSegmentRefEntryOrContainerRefEntry();

        for ( SequenceEntryType entry : entryList ) {

            if ( entry.getClass() == ParameterRefEntryType.class ) {

                if ( ( includedContainer                              != null  ) && 
                     ( includedContainer.getConditionList().isEmpty() == false ) ) {
                    addParameter( (ParameterRefEntryType)entry,
                                  currentStartBit,
                                  containerStartBit,
                                  container,
                                  includedContainer.getConditionList() );
                } else {
                    addParameter( (ParameterRefEntryType)entry,
                                  currentStartBit,
                                  containerStartBit,
                                  container,
                                  null );
                }

            } else if ( entry.getClass() == ContainerRefEntryType.class ) {

                if ( ( includedContainer                              != null  ) && 
                     ( includedContainer.getConditionList().isEmpty() == false ) ) {
                    addContainer( (ContainerRefEntryType)entry,
                                  currentStartBit,
                                  //currentStartBit.get(),
                                  containerStartBit,
                                  container,
                                  includedContainer.getConditionList() );
                } else {
                    addContainer( (ContainerRefEntryType)entry,
                                  currentStartBit,
                                  //currentStartBit.get(),
                                  containerStartBit,
                                  container,
                                  null );
                }

            } else if ( entry.getClass() == ArrayParameterRefEntryType.class ) {

                if ( ( includedContainer                              != null  ) && 
                     ( includedContainer.getConditionList().isEmpty() == false ) ) {
                    addArrayParameter( (ArrayParameterRefEntryType)entry,
                                  currentStartBit,
                                  containerStartBit,
                                  container,
                                  includedContainer.getConditionList() );
                } else {
                    addArrayParameter( (ArrayParameterRefEntryType)entry,
                                  currentStartBit,
                                  containerStartBit,
                                  container,
                                  null );
                }

            } else if ( entry.getClass() == ParameterSegmentRefEntryType.class ) {

                String nameRef =
                    ((ParameterSegmentRefEntryType)entry).getParameterRef();
                warnings_.add( "'ParameterSegmentRefEntry' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                               " " + // NOI18N
                               nameRef );

            } else if ( entry.getClass() == ContainerSegmentRefEntryType.class ) {

                String nameRef =
                    ((ContainerSegmentRefEntryType)entry).getContainerRef();
                warnings_.add( "'ContainerSegmentRefEntry' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                               " " + // NOI18N
                               nameRef );

            } else if ( entry.getClass() == StreamSegmentEntryType.class ) {

                String nameRef =
                    ((StreamSegmentEntryType)entry).getStreamRef();
                warnings_.add( "'StreamSegmentEntry' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                               " " + // NOI18N
                               nameRef );

            } else if ( entry.getClass() == IndirectParameterRefEntryType.class ) {

                String nameRef =
                    ((IndirectParameterRefEntryType)entry).getParameterInstance().getParameterRef();
                warnings_.add( "'IndirectParameterRefEntry' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                               " " + // NOI18N
                               nameRef );

            }

        }

    }

    private void addContainer( ContainerRefEntryType         entry,
                               RunningStartBit               currentStartBit,
                               long                          containerStartBit,
                               XTCETMContainer               holdingContainer,
                               List<XTCEContainerEntryValue> includedConditionsList )
        throws XTCEDatabaseException {

        String nameRef = entry.getContainerRef();

        //System.out.println( "Identified Container " +
        //                    nameRef +
        //                    " cur start bit " +
        //                    Long.toString( currentStartBit.get() ) +
        //                    " cont start bit " +
        //                    Long.toString( containerStartBit ) );

        XTCETMContainer nextIncludedContainer =
            findContainer( nameRef, holdingContainer );

        XTCEContainerContentEntry nextIncludedContent =
            new XTCEContainerContentEntry( nextIncludedContainer,
                                           holdingContainer );

        nextIncludedContent.setConditionList( includedConditionsList, false );

        // second argument was nextIncludedContainer

        addIncludeConditions( entry,
                              holdingContainer,
                              nextIncludedContent );

        evaluateIncludeConditions( nextIncludedContent );

        if ( nextIncludedContent.isCurrentlyInUse() == true ) {
            containerStartBit = evaluateContainerReferenceLocation( entry,
                                                                    nextIncludedContent,
                                                                    currentStartBit,
                                                                    containerStartBit );
        }

        long repeatCount =
            addRepeatEntryDescription( entry, nextIncludedContent );

        for ( int iii = 0; iii < repeatCount; ++iii ) {

            if ( repeatCount != 1 ) {
                nextIncludedContent.setRepeatparameterInfo(
                    XTCEFunctions.makeRepeatString( iii + 1, repeatCount ) );
            }

            //System.out.println( "Adding Identified Container " +
            //                    nameRef +
            //                    " cur start bit " +
            //                    Long.toString( currentStartBit.get() ) +
            //                    " cont start bit " +
            //                    Long.toString( containerStartBit ) +
            //                    " instance " + Integer.toString( iii ) );

            contentList_.add( nextIncludedContent );

            // short circuit the application of this container when the option
            // to show depth even when it is not included is false

            if ( ( showAllConditions_                     == false ) &&
                 ( nextIncludedContent.isCurrentlyInUse() == false ) ) {
                return;
            }

            applyCompleteContainer( nextIncludedContainer,
                                    currentStartBit,
                                    containerStartBit,
                                    null,
                                    null,
                                    nextIncludedContent );

            // need a deep copy of the content if this is NOT the last
            if ( iii < ( repeatCount - 1 ) ) {
                containerStartBit = currentStartBit.get();
                try {
                    nextIncludedContent = (XTCEContainerContentEntry)nextIncludedContent.clone();
                } catch ( CloneNotSupportedException ex ) {
                    // do nothing, it will not happen
                }
            }

        }

    }

    private void addArrayParameter( ArrayParameterRefEntryType    pRefEntry,
                                    RunningStartBit               currentStartBit,
                                    long                          containerStartBit,
                                    XTCETMContainer               container,
                                    List<XTCEContainerEntryValue> includedConditionsList )
        throws XTCEDatabaseException {

        String nameRef = pRefEntry.getParameterRef();

        //System.out.println( "Identified Parameter " +
        //                    nameRef +
        //                    " cur start bit " +
        //                    Long.toString( currentStartBit.get() ) +
        //                    " cont start bit " +
        //                    Long.toString( containerStartBit ) );

        XTCEParameter pObj = findParameter( nameRef, container );
        XTCEContainerContentEntry content =
            new XTCEContainerContentEntry( pObj, container );

        if ( includedConditionsList != null ) {
            content.setConditionList( includedConditionsList, false );
        }
        addIncludeConditions( pRefEntry, container, content );
        // NOT FOR ARRAYS
        //applyUserValue( content );
        evaluateIncludeConditions( content );
        //if ( content.isCurrentlyInUse() == true ) {
        //    addStartBit( pRefEntry, content, currentStartBit, containerStartBit );
            //applyBinaryValue( content );
        //}

        long repeatCount = addRepeatEntryDescription( pRefEntry, content );

        for ( int iii = 0; iii < repeatCount; ++iii ) {

            if ( repeatCount != 1 ) {
                content.setRepeatparameterInfo(
                    XTCEFunctions.makeRepeatString( iii + 1, repeatCount ) );
            }

            contentList_.add( content );

            // short circuit the depth into members when the parameter is not
            // currently applied

            if ( ( showAllConditions_         == false ) &&
                 ( content.isCurrentlyInUse() == false ) ) {
                return;
            }

            String aptFullPath = pObj.getTypeReferenceFullPath();
            String aptPath     = XTCEFunctions.getPathNameFromReferenceString( aptFullPath );
            String aptName     = XTCEFunctions.getNameFromPathReferenceString( aptFullPath );

            NameDescriptionType aptRef = null;

            for ( XTCESpaceSystem ss : spaceSystems_ ) {
                if ( ss.getFullPath().equals( aptPath ) == true ) {
                    String arrayTypeRef  = ((ArrayDataTypeType)ss.getTMParameterTypeReference( aptName )).getArrayTypeRef();
                    String arrayFullPath = XTCEFunctions.resolvePathReference( aptPath, arrayTypeRef );
                    String arrayTypeName = XTCEFunctions.getNameFromPathReferenceString( arrayFullPath );
                    String arrayTypePath = XTCEFunctions.getPathNameFromReferenceString( arrayFullPath );
                    for ( XTCESpaceSystem ss2 : spaceSystems_ ) {
                        if ( ss2.getFullPath().equals( arrayTypePath ) == true ) {
                            aptRef = ss2.getTMParameterTypeReference( arrayTypeName );
                            break;
                        }
                    }
                    break;
                }
            }

            if ( aptRef == null ) {
                warnings_.add( XTCEFunctions.getText( "xml_array_type_not_found" ) + // NOI18N
                               " " + // NOI18N
                               pObj.getName() );
                return;
            }

            long arrayLength = addArrayEntryDescription( pRefEntry, content );

            for ( int jjj = 0; jjj < arrayLength; ++jjj ) {

                XTCEParameter arrayParameterObject;

                if ( pObj.isParameter() == true ) {
                    arrayParameterObject =
                        new XTCEParameter( pObj.getName() +
                                           "[" + // NOI18N
                                           Long.toString( jjj ) +
                                           "]", // NOI18N
                                           pObj.getSpaceSystemPath(),
                                           pObj.getParameterReference(),
                                           aptRef );
                } else {
                    arrayParameterObject =
                        new XTCEParameter( pObj.getName() +
                                           "[" + // NOI18N
                                           Long.toString( jjj ) +
                                           "]", // NOI18N
                                           pObj.getSpaceSystemPath(),
                                           pObj.getMemberReference(),
                                           aptRef );
                }

                XTCEContainerContentEntry arrayContent =
                    new XTCEContainerContentEntry( arrayParameterObject, container );

                contentList_.add( arrayContent );

                if ( includedConditionsList != null ) {
                    arrayContent.setConditionList( includedConditionsList, false );
                }
                addIncludeConditions( pRefEntry, container, arrayContent );
                //applyUserValue( arrayContent ); // NOT YET FOR ARRAY CONTENTS
                evaluateIncludeConditions( arrayContent );
                // when doing a repeat entry, the leading distance from the
                // reference location only applies to the first one, additional
                // distances are covered by the Offset element - not supported
                if ( arrayContent.isCurrentlyInUse() == true ) {
                    if ( ( jjj == 0 ) && ( iii == 0 ) ) {
                        addStartBit( pRefEntry, arrayContent, currentStartBit, containerStartBit );
                    } else {
                        // to support RepeatEntry/Offset, add function here
                        arrayContent.setStartBit( currentStartBit.get() );
                        currentStartBit.add( Long.parseLong( arrayContent.getRawSizeInBits() ) );
                    }
                    applyBinaryValue( arrayContent );
                }

                if ( arrayParameterObject.getTypeReference().getClass() == AggregateDataType.class ) {
                   // doesnt need container start bit because they are always previousEntry & 0
                   addMembers( arrayParameterObject, currentStartBit, container, arrayContent );
                }

                // need a deep copy of the content if this is NOT the last
                //if ( jjj < ( arrayLength - 1 ) ) {
                //    arrayContent = arrayContent.deepCopy();
                //    // deep copy include is previousEntry 0 right now, but we need
                //    // to eventually consider repeat offset
                //    if ( isEntryNeedingStartBit( arrayContent ) == true ) {
                //        arrayContent.setStartBit( currentStartBit.get() );
                //        currentStartBit.add( Long.parseLong( arrayContent.getRawSizeInBits() ) );
                //        arrayContent.setValue( (XTCEContainerEntryValue)null );
                //        // applyBinaryValue( arrayContent ); // NOT YET FOR ARRAY CONTENTS
                //    }
                //}

            }

            // need a deep copy of the content if this is NOT the last
            if ( iii < ( repeatCount - 1 ) ) {
                try {
                    content = (XTCEContainerContentEntry)content.clone();
                } catch ( CloneNotSupportedException ex ) {
                    // do nothing, it will not happen
                }
                // deep copy include is previousEntry 0 right now, but we need
                // to eventually consider repeat offset - this case of arrays
                // probably will never call this
                //if ( isEntryNeedingStartBit( content ) == true ) {
                    //content.setStartBit( currentStartBit.get() );
                    //currentStartBit.add( Long.parseLong( content.getRawSizeInBits() ) );
                    //content.setValue( (XTCEContainerEntryValue)null );
                    //applyBinaryValue( content );
                //}
            }

        }

    }

    private void addParameter( ParameterRefEntryType         pRefEntry,
                               RunningStartBit               currentStartBit,
                               long                          containerStartBit,
                               XTCETMContainer               container,
                               List<XTCEContainerEntryValue> includedConditionsList )
        throws XTCEDatabaseException {

        String nameRef = pRefEntry.getParameterRef();

        //System.out.println( "Identified Parameter " +
        //                    nameRef +
        //                    " cur start bit " +
        //                    Long.toString( currentStartBit.get() ) +
        //                    " cont start bit " +
        //                    Long.toString( containerStartBit ) );

        XTCEParameter pObj = findParameter( nameRef, container );
        XTCEContainerContentEntry content =
            new XTCEContainerContentEntry( pObj, container );

        if ( includedConditionsList != null ) {
            content.setConditionList( includedConditionsList, false );
        }
        addIncludeConditions( pRefEntry, container, content );
        applyUserValue( content );
        evaluateIncludeConditions( content );
        if ( content.isCurrentlyInUse() == true ) {
            addStartBit( pRefEntry, content, currentStartBit, containerStartBit );
            applyBinaryValue( content );
        }

        long repeatCount = addRepeatEntryDescription( pRefEntry, content );

        for ( int iii = 0; iii < repeatCount; ++iii ) {

            if ( repeatCount != 1 ) {
                content.setRepeatparameterInfo(
                    XTCEFunctions.makeRepeatString( iii + 1, repeatCount ) );
            }

            contentList_.add( content );

            // short circuit the depth into members when the parameter is not
            // currently applied

            if ( ( showAllConditions_         == false ) &&
                 ( content.isCurrentlyInUse() == false ) ) {
                return;
            }

            if ( pObj.getTypeReference().getClass() == AggregateDataType.class ) {
                // doesnt need container start bit because they are always previousEntry & 0
                addMembers( pObj, currentStartBit, container, content );
            }

            // need a deep copy of the content if this is NOT the last
            if ( iii < ( repeatCount - 1 ) ) {
                try {
                    content = (XTCEContainerContentEntry)content.clone();
                } catch ( CloneNotSupportedException ex ) {
                    // do nothing, it will not happen
                }
                // deep copy include is previousEntry 0 right now, but we need
                // to eventually consider repeat offset
                if ( isEntryNeedingStartBit( content ) == true ) {
                    // to support RepeatEntry/Offset, add function here
                    content.setStartBit( currentStartBit.get() );
                    currentStartBit.add( Long.parseLong( content.getRawSizeInBits() ) );
                    content.setValue( (XTCEContainerEntryValue)null );
                    applyBinaryValue( content );
                }
            }

        }

    }

    private void addMembers( XTCEParameter             parameter,
                             RunningStartBit           currentStartBit,
                             XTCETMContainer           container,
                             XTCEContainerContentEntry parentContentEntry )
        throws XTCEDatabaseException {

        List<AggregateDataType.MemberList.Member> members =
            ((AggregateDataType)parameter.getTypeReference()).getMemberList().getMember();

        for ( Member member : members ) {

            String newPath = parameter.getFullPath() + "." + member.getName(); // NOI18N
            //System.out.println( "Identified Member " + newPath );

            XTCEParameter mObj = findParameter( newPath, container );
            XTCEContainerContentEntry mcontent =
                new XTCEContainerContentEntry( mObj, container );

            mcontent.setConditionList( parentContentEntry.getConditionList(),
                                       parentContentEntry.isCurrentlyInUse() );

            applyUserValue( mcontent );

            if ( parentContentEntry.getRepeatParameterInfo().isEmpty() == false ) {
                mcontent.setRepeatparameterInfo( parentContentEntry.getRepeatParameterInfo() );
            }

            // compute start bit for Members is easy because they are always
            // previousEntry and 0.

            if ( isEntryNeedingStartBit( mcontent ) == true ) {
                mcontent.setStartBit( currentStartBit.get() );
                currentStartBit.add( Long.parseLong( mcontent.getRawSizeInBits() ) );
                applyBinaryValue( mcontent );
            }

            contentList_.add( mcontent );

            if ( mObj.getTypeReference().getClass() == AggregateDataType.class ) {
                addMembers( mObj, currentStartBit, container, mcontent );
            }

        }

    }

    /// A reference to the XTCETMContainer object that this model is built from.

    private XTCETMContainer container_ = null;

}
