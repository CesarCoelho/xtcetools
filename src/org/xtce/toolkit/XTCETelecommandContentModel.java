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

import java.math.BigInteger;
import java.util.List;
import org.omg.space.xtce.AggregateDataType;
import org.omg.space.xtce.ArrayParameterRefEntryType;
import org.omg.space.xtce.CmdContArrayArgumentRefEntryType;
import org.omg.space.xtce.CommandContainerEntryListType.ArgumentRefEntry;
import org.omg.space.xtce.CommandContainerEntryListType.FixedValueEntry;
import org.omg.space.xtce.CommandContainerType.BaseContainer;
import org.omg.space.xtce.ContainerRefEntryType;
import org.omg.space.xtce.ContainerSegmentRefEntryType;
import org.omg.space.xtce.IndirectParameterRefEntryType;
import org.omg.space.xtce.MatchCriteriaType;
import org.omg.space.xtce.MetaCommandType.BaseMetaCommand;
import org.omg.space.xtce.MetaCommandType.BaseMetaCommand.ArgumentAssignmentList;
import org.omg.space.xtce.MetaCommandType.BaseMetaCommand.ArgumentAssignmentList.ArgumentAssignment;
import org.omg.space.xtce.ParameterRefEntryType;
import org.omg.space.xtce.ParameterSegmentRefEntryType;
import org.omg.space.xtce.SequenceEntryType;
import org.omg.space.xtce.StreamSegmentEntryType;
import org.xtce.toolkit.XTCEContainerContentEntry.FieldType;

/** The telecommand content model class encapsulates the pre-processing of an
 * XTCE MetaCommand/BlockMetaCommand into a series of rows suitable for a
 * simple iterator.
 *
 * @author David Overeem
 *
 */

public class XTCETelecommandContentModel extends XTCEContainerContentModelBase {

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
     * @param tcObject XTCETelecommand from the database object that contains
     * all the needed entry list items.
     *
     * @param spaceSystems List of XTCESpaceSystem objects to search for
     * entries on the entry list.
     *
     * @param userValues List of XTCEContainerEntryValue objects for TC
     * Parameters and/or Arguments that are within the container.  This can be
     * null if no values are needed to be passed into conditional processing.
     *
     * @param showAllConditions boolean indicating if unsatisfied conditional
     * includes should be pursued at depth.  This can be a performance hit if
     * there are a large number of conditionals nested.
     *
     * @throws XTCEDatabaseException in the event that the container cannot
     * be completely processed.
     *
     */

    public XTCETelecommandContentModel( XTCETelecommand               tcObject,
                                        List<XTCESpaceSystem>         spaceSystems,
                                        List<XTCEContainerEntryValue> userValues,
                                        boolean                       showAllConditions )
        throws XTCEDatabaseException {

        super( spaceSystems,
               userValues,
               null,
               tcObject.getName(),
               tcObject.getDescription(),
               showAllConditions );

        telecommand_ = tcObject;

        processTelecommand();

    }

    /** Accessor to get the reference to the XTCETelecommand object that this
     * model represents.
     *
     * @return XTCETelecommand that is represented by this model object.
     *
     */

    public XTCETelecommand getTelecommandReference() {
        return telecommand_;
    }

    /** Accessor to get the reference to the XTCETCContainer object that this
     * model represents.
     *
     * @return XTCETCContainer that is represented by this model object.
     *
     */

    public XTCETCContainer getContainerReference() {
        return telecommand_.getCommandContainer();
    }

    /** This method processes the contents of the entire telecommand into
     * a List of XTCEContainerContentEntry objects.
     *
     * @return long containing the total length of this content/container
     * entry.
     *
     * @throws XTCEDatabaseException thrown in the event that this container
     * cannot be processed.
     *
     */

    public final long processTelecommand() throws XTCEDatabaseException {

        contentList_.clear();
        contentValues_.clear();
        warnings_.clear();

        //long startTime = System.currentTimeMillis();

        RunningStartBit totalSize = new RunningStartBit();

        applyCompleteContainer( getTelecommandReference(),
                                totalSize,
                                0,
                                null,
                                null,
                                null );

        reorderItemsByStartBit();

        checkForOverlaps( totalSize.get() );

        //long estimatedTime = System.currentTimeMillis() - startTime;

        //System.out.println( "Processed Telecommand in approximately " +
        //    Long.toString( estimatedTime / 1000 ) + " seconds" );

        totalContainerSize_ = totalSize.get();

        return getTotalSize();

    }

    private void applyCompleteContainer(  XTCETelecommand           currentTelecommand,
                                          RunningStartBit           currentStartBit,
                                          long                      containerStartBit,
                                          MatchCriteriaType         parentRestrictions,
                                          String                    parentSpaceSystem,
                                          XTCEContainerContentEntry includedContainer )
        throws XTCEDatabaseException {

        long containerStartIndex = contentList_.size() - 1;

        applyBaseTelecommand( currentTelecommand,
                              currentStartBit,
                              containerStartBit,
                              null,
                              null,
                              null,
                              includedContainer );

        processEndOfContainer( currentStartBit,
                               containerStartBit,
                               containerStartIndex );

    }

    private void applyBaseTelecommand( XTCETelecommand           currentTelecommand,
                                       RunningStartBit           currentStartBit,
                                       long                      containerStartBit,
                                       MatchCriteriaType         parentRestrictions,
                                       ArgumentAssignmentList    argAssignments,
                                       String                    parentSpaceSystem,
                                       XTCEContainerContentEntry includedContainer ) throws XTCEDatabaseException {

        BaseMetaCommand baseTelecommandRef =
            currentTelecommand.getMetaCommandReference().getBaseMetaCommand();

        String currentSpaceSystemPath = currentTelecommand.getSpaceSystemPath();

        // first crawl back to the deepest base container before proceeding

        if ( baseTelecommandRef != null ) {

            XTCETelecommand baseTelecommand =
                getBaseTelecommand( currentTelecommand );

            BaseContainer baseContainerRef =
                currentTelecommand.getCommandContainer()
                                  .getCommandContainerReference()
                                  .getBaseContainer();

            MatchCriteriaType restrictions = null;
            if ( baseContainerRef != null ) {
                restrictions = baseContainerRef.getRestrictionCriteria();
            }

            ArgumentAssignmentList assignments =
                baseTelecommandRef.getArgumentAssignmentList();

            applyBaseTelecommand( baseTelecommand,
                                  currentStartBit,
                                  containerStartBit,
                                  restrictions,
                                  assignments,
                                  currentSpaceSystemPath,
                                  includedContainer );

        }

        applyCurrentTelecommand( currentTelecommand,
                                 currentStartBit,
                                 containerStartBit,
                                 includedContainer );

        applyAssignments( currentTelecommand, argAssignments );

        applyRestrictions( currentTelecommand,
                           parentRestrictions,
                           parentSpaceSystem );

    }

    private void applyCurrentTelecommand( XTCETelecommand           telecommand,
                                          RunningStartBit           currentStartBit,
                                          long                      containerStartBit,
                                          XTCEContainerContentEntry includedContainer ) throws XTCEDatabaseException {

        //System.out.println( "Processing " + telecommand.getInheritancePath() );

        // do not make a second row for the included container
        //if ( ( includedContainer != null ) && ( includedContainer.getContainer().getFullPath().equals( container.getFullPath() ) == false ) ) {
        if ( includedContainer == null ) {
            contentList_.add( new XTCEContainerContentEntry( telecommand.getCommandContainer(), telecommand ) );
        } else if ( includedContainer.getTelecommandContainer().getFullPath().equals( telecommand.getCommandContainer().getFullPath() ) == false ) {
            contentList_.add( new XTCEContainerContentEntry( telecommand.getCommandContainer(), null ) );
        }

        List<SequenceEntryType> entryList =
            telecommand.getCommandContainer()
                       .getCommandContainerReference()
                       .getEntryList()
                       .getParameterRefEntryOrParameterSegmentRefEntryOrContainerRefEntry();

        for ( SequenceEntryType entry : entryList ) {

            if ( entry.getClass() == ParameterRefEntryType.class ) {

                if ( ( includedContainer                              != null  ) && 
                     ( includedContainer.getConditionList().isEmpty() == false ) ) {
                    addParameter( (ParameterRefEntryType)entry,
                                  currentStartBit,
                                  containerStartBit,
                                  telecommand,
                                  includedContainer.getConditionList() );
                } else {
                    addParameter( (ParameterRefEntryType)entry,
                                  currentStartBit,
                                  containerStartBit,
                                  telecommand,
                                  null );
                }

            } else if ( entry.getClass() == ContainerRefEntryType.class ) {

                String nameRef =
                    ((ContainerRefEntryType)entry).getContainerRef();
                warnings_.add( "'ContainerRefEntry' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                               " " + // NOI18N
                               nameRef );

                //String nameRef = ((ContainerRefEntryType)entry).getContainerRef();
                //XTCETCContainer nextIncludedContainer = findContainer( nameRef, telecommand );
                //XTCEContainerContentEntry nextIncludedContent =
                //    new XTCEContainerContentEntry( nextIncludedContainer, telecommand );
                //if ( includedContainer != null ) {
                //    if ( includedContainer.getConditionList().size() > 0 ) {
                //        nextIncludedContent.setConditionList( includedContainer.getConditionList() );
                //    }
                //}
                //addIncludeConditions( entry, nextIncludedContainer, nextIncludedContent );
                //long repeatCount = addRepeatEntryDescription( entry, nextIncludedContent );
                //for ( int iii = 0; iii < repeatCount; ++iii ) {
                //    if ( repeatCount != 1 ) {
                //        nextIncludedContent.setRepeatparameterInfo( "Repeat " +
                //                                                    Long.toString( iii + 1 ) +
                //                                                    " of " +
                //                                                    Long.toString( repeatCount ) );
                //    }
                //    contentList_.add( nextIncludedContent );
                //    applyCompleteContainer( nextIncludedContainer,
                //                            currentStartBit,
                //                            null,
                //                            null,
                //                            nextIncludedContent );
                //    // need a deep copy of the content if this is NOT the last
                //    if ( iii < ( repeatCount - 1 ) ) {
                //        nextIncludedContent = nextIncludedContent.deepCopy();
                //        // deep copy include is previousEntry 0 right now, but we need
                //        // to eventually consider repeat offset
                //    }
                //}
            } else if ( entry.getClass() == ArgumentRefEntry.class ) {

                if ( ( includedContainer                              != null  ) && 
                     ( includedContainer.getConditionList().isEmpty() == false ) ) {
                    addArgument( (ArgumentRefEntry)entry,
                                 currentStartBit,
                                 containerStartBit,
                                 telecommand,
                                 includedContainer.getConditionList() );
                } else {
                    addArgument( (ArgumentRefEntry)entry,
                                 currentStartBit,
                                 containerStartBit,
                                 telecommand,
                                 null );
                }

            } else if ( entry.getClass() == FixedValueEntry.class ) {

                if ( ( includedContainer                              != null  ) && 
                     ( includedContainer.getConditionList().isEmpty() == false ) ) {
                    addFixedValue( (FixedValueEntry)entry,
                                   currentStartBit,
                                   containerStartBit,
                                   telecommand,
                                   includedContainer.getConditionList() );
                } else {
                    addFixedValue( (FixedValueEntry)entry,
                                   currentStartBit,
                                   containerStartBit,
                                   telecommand,
                                   null );
                }

            } else if ( entry.getClass() == ArrayParameterRefEntryType.class ) {

                String nameRef =
                    ((ArrayParameterRefEntryType)entry).getParameterRef();
                warnings_.add( "'ArrayParameterRefEntry' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                               " " + // NOI18N
                               nameRef );

            } else if ( entry.getClass() == CmdContArrayArgumentRefEntryType.class ) {

                String nameRef =
                    ((CmdContArrayArgumentRefEntryType)entry).getArgumentRef();
                warnings_.add( "'ArrayArgumentRefEntry' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_not_yet_supported" ) + // NOI18N
                               " " + // NOI18N
                               nameRef );

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

    private XTCETelecommand getBaseTelecommand( XTCETelecommand currentTelecommand ) throws XTCEDatabaseException {

        BaseMetaCommand baseTelecommandRef =
            currentTelecommand.getMetaCommandReference().getBaseMetaCommand();

        if ( baseTelecommandRef == null ) {
            return null;
        }

        String metaCommandRef          = baseTelecommandRef.getMetaCommandRef();
        String currentSpaceSystemPath  = currentTelecommand.getSpaceSystemPath();
        String baseMetaCommandFullPath = XTCEFunctions.resolvePathReference( currentSpaceSystemPath, metaCommandRef );
        String baseMetaCommandPath     = XTCEFunctions.getPathNameFromReferenceString( baseMetaCommandFullPath );
        String baseMetaCommandName     = XTCEFunctions.getNameFromPathReferenceString( baseMetaCommandFullPath );

        for ( XTCESpaceSystem spaceSystem : spaceSystems_ ) {
            if ( baseMetaCommandPath.equals( spaceSystem.getFullPath() ) == true ) {
                return spaceSystem.getTelecommand( baseMetaCommandName );
            }
        }

        throw new XTCEDatabaseException( XTCEFunctions.getText( "error_no_base_tc" ) + // NOI18N
                                         " '" + // NOI18N
                                         metaCommandRef +
                                         "' " + // NOI18N
                                         XTCEFunctions.getText( "error_from_tc" ) + // NOI18N
                                         " '" + // NOI18N
                                         currentTelecommand.getName() +
                                         "'" ); // NOI18N

    }

    private void applyAssignments( XTCETelecommand        telecommand,
                                   ArgumentAssignmentList argAssignments ) {

        if ( argAssignments == null ) {
            return;
        }

        List<ArgumentAssignment> assigns = argAssignments.getArgumentAssignment();

        for ( ArgumentAssignment assign : assigns ) {

            boolean found = false;

            for ( XTCEContainerContentEntry entry : contentList_ ) {
                if ( entry.getEntryType() == FieldType.ARGUMENT ) {
                    if ( entry.getArgument().getName().equals( assign.getArgumentName() ) == true ) {
                        entry.setValue( assign );
                        found = true;
                        break;
                    }
                }
            }

            if ( found == false ) {
                warnings_.add( XTCEFunctions.getText( "xml_element_assignment_not_found1" ) + // NOI18N
                               " '" + // NOI18N
                               telecommand.getFullPath() +
                               "' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_assignment_not_found2" ) + // NOI18N
                               " '" + // NOI18N
                               assign.getArgumentName() +
                               "' " + // NOI18N
                               XTCEFunctions.getText( "xml_element_assignment_not_found3" ) ); // NOI18N
            }

        }

    }

    private void addFixedValue( FixedValueEntry               fixedEntry,
                                RunningStartBit               currentStartBit,
                                long                          containerStartBit,
                                XTCETelecommand               telecommand,
                                List<XTCEContainerEntryValue> includedConditionsList )
        throws XTCEDatabaseException {

        BigInteger value = new BigInteger( fixedEntry.getBinaryValue() );

        if ( value.compareTo( BigInteger.ZERO ) == -1 ) {
            value = value.negate();
        }

        XTCEContainerContentEntry content =
            new XTCEContainerContentEntry( fixedEntry.getSizeInBits().toString(),
                                           value.toString(),
                                           telecommand );

        if ( includedConditionsList != null ) {
            content.setConditionList( includedConditionsList, false );
        }
        addIncludeConditions( fixedEntry, telecommand, content );
        evaluateIncludeConditions( content );
        if ( content.isCurrentlyInUse() == true ) {
            addStartBit( fixedEntry, content, currentStartBit, containerStartBit );
        }

        long repeatCount = addRepeatEntryDescription( fixedEntry, content );

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
                }
            }

        }

    }

    private void addArgument( ArgumentRefEntry              aRefEntry,
                              RunningStartBit               currentStartBit,
                              long                          containerStartBit,
                              XTCETelecommand               telecommand,
                              List<XTCEContainerEntryValue> includedConditionsList )
        throws XTCEDatabaseException {

        String nameRef = aRefEntry.getArgumentRef();

        //System.out.println( "Identified Argument " +
        //                    nameRef +
        //                    " cur start bit " +
        //                    Long.toString( currentStartBit.get() ) +
        //                    " cont start bit " +
        //                    Long.toString( containerStartBit ) );

        XTCEArgument aObj = telecommand.getArgument( nameRef );

        XTCEContainerContentEntry content =
            new XTCEContainerContentEntry( aObj, telecommand );

        if ( includedConditionsList != null ) {
            content.setConditionList( includedConditionsList, false );
        }
        addIncludeConditions( aRefEntry, telecommand, content );
        applyUserValue( content );
        evaluateIncludeConditions( content );
        if ( content.isCurrentlyInUse() == true ) {
            addStartBit( aRefEntry, content, currentStartBit, containerStartBit );
            applyBinaryValue( content );
        }

        long repeatCount = addRepeatEntryDescription( aRefEntry, content );

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

            if ( aObj.getTypeReference().getClass() == AggregateDataType.class ) {
                // doesnt need container start bit because they are always previousEntry & 0
                addMembers( aObj, currentStartBit, telecommand, content );
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

    private void addParameter( ParameterRefEntryType         pRefEntry,
                               RunningStartBit               currentStartBit,
                               long                          containerStartBit,
                               XTCETelecommand               telecommand,
                               List<XTCEContainerEntryValue> includedConditionsList )
        throws XTCEDatabaseException {

        String nameRef = pRefEntry.getParameterRef();

        //System.out.println( "Identified Parameter " +
        //                    nameRef +
        //                    " cur start bit " +
        //                    Long.toString( currentStartBit.get() ) +
        //                    " cont start bit " +
        //                    Long.toString( containerStartBit ) );

        XTCEParameter pObj = findParameter( nameRef, telecommand );
        XTCEContainerContentEntry content =
            new XTCEContainerContentEntry( pObj, telecommand );

        if ( includedConditionsList != null ) {
            content.setConditionList( includedConditionsList, false );
        }
        addIncludeConditions( pRefEntry, telecommand, content );
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
                addMembers( pObj, currentStartBit, telecommand, content );
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
                             XTCETelecommand           telecommand,
                             XTCEContainerContentEntry parentContentEntry )
        throws XTCEDatabaseException {

        List<AggregateDataType.MemberList.Member> members =
            ((AggregateDataType)parameter.getTypeReference()).getMemberList()
                                                             .getMember();

        for ( AggregateDataType.MemberList.Member member : members ) {

            String newPath = parameter.getFullPath() + "." + member.getName(); // NOI18N
            //System.out.println( "Identified Parameter Member " + newPath );

            XTCEParameter mObj = findParameter( newPath, telecommand );

            XTCEContainerContentEntry mcontent =
                new XTCEContainerContentEntry( mObj, telecommand );

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
                addMembers( mObj, currentStartBit, telecommand, mcontent );
            }

        }

    }

    private void addMembers( XTCEArgument              argument,
                             RunningStartBit           currentStartBit,
                             XTCETelecommand           telecommand,
                             XTCEContainerContentEntry parentContentEntry )
        throws XTCEDatabaseException {

        List<AggregateDataType.MemberList.Member> members =
            ((AggregateDataType)argument.getTypeReference()).getMemberList()
                                                            .getMember();

        for ( AggregateDataType.MemberList.Member member : members ) {

            String newPath = argument.getName() + "." + member.getName(); // NOI18N
            //System.out.println( "Identified Argument Member " + newPath );

            XTCEArgument aObj = telecommand.getArgument( newPath );

            XTCEContainerContentEntry mcontent =
                new XTCEContainerContentEntry( aObj, telecommand );

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

            if ( aObj.getTypeReference() instanceof AggregateDataType ) {
                addMembers( aObj, currentStartBit, telecommand, mcontent );
            }

        }

    }

    private XTCETelecommand telecommand_ = null;

}
