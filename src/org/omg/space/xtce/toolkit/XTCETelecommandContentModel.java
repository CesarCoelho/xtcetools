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
import org.omg.space.xtce.database.ArrayParameterRefEntryType;
import org.omg.space.xtce.database.CommandContainerType.BaseContainer;
import org.omg.space.xtce.database.ContainerRefEntryType;
import org.omg.space.xtce.database.ContainerSegmentRefEntryType;
import org.omg.space.xtce.database.IndirectParameterRefEntryType;
import org.omg.space.xtce.database.MatchCriteriaType;
import org.omg.space.xtce.database.MetaCommandType.BaseMetaCommand;
import org.omg.space.xtce.database.MetaCommandType.BaseMetaCommand.ArgumentAssignmentList;
import org.omg.space.xtce.database.MetaCommandType.BaseMetaCommand.ArgumentAssignmentList.ArgumentAssignment;
import org.omg.space.xtce.database.ParameterRefEntryType;
import org.omg.space.xtce.database.ParameterSegmentRefEntryType;
import org.omg.space.xtce.database.SequenceEntryType;
import org.omg.space.xtce.database.StreamSegmentEntryType;
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry.FieldType;

/** The telecommand content model class encapsulates the pre-processing of an
 * XTCE MetaCommand/BlockMetaCommand into a series of rows suitable for a
 * simple iterator.
 *
 * @author David Overeem
 *
 */

public class XTCETelecommandContentModel extends XTCEContainerContentModelBase {

    XTCETelecommandContentModel( XTCETelecommand               tcObject,
                                 List<XTCESpaceSystem>         spaceSystems,
                                 List<XTCEContainerEntryValue> userValues,
                                 boolean                       showAllConditions ) throws XTCEDatabaseException {

        super( spaceSystems, userValues, null, showAllConditions );
        telecommand_        = tcObject;
        totalContainerSize_ = processTelecommand();

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
     * @throws XTCEDatabaseException 
     *
     */

    public final long processTelecommand() throws XTCEDatabaseException {

        long startTime = System.currentTimeMillis();

        warnings_ = new ArrayList<String>();

        RunningStartBit totalSize = new RunningStartBit();

        applyCompleteContainer( getTelecommandReference(),
                                totalSize,
                                0,
                                null,
                                null,
                                null );

        reorderItemsByStartBit();

        checkForOverlaps();

        long estimatedTime = System.currentTimeMillis() - startTime;

        //System.out.println( "Processed Telecommand in approximately " +
        //    Long.toString( estimatedTime / 1000 ) + " seconds" );

        return totalSize.get();

    }

    private void applyCompleteContainer(  XTCETelecommand           currentTelecommand,
                                          RunningStartBit           currentStartBit,
                                          long                      containerStartBit,
                                          MatchCriteriaType         parentRestrictions,
                                          String                    parentSpaceSystem,
                                          XTCEContainerContentEntry includedContainer ) throws XTCEDatabaseException {

        long containerStartIndex = contentList_.size() - 1;

        applyBaseTelecommand( currentTelecommand,
                              currentStartBit,
                              containerStartBit,
                              null,
                              null,
                              null,
                              includedContainer );

        processEndOfContainer( currentStartBit, containerStartBit, containerStartIndex );

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
                currentTelecommand.getCommandContainer().getCommandContainerReference().getBaseContainer();

            MatchCriteriaType restrictions = null;
            if ( baseContainerRef != null ) {
                restrictions = baseContainerRef.getRestrictionCriteria();
            }

            ArgumentAssignmentList assignments = baseTelecommandRef.getArgumentAssignmentList();

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

        List<SequenceEntryType> entryList = telecommand.getCommandContainer().getCommandContainerReference().getEntryList().getParameterRefEntryOrParameterSegmentRefEntryOrContainerRefEntry();
        for ( SequenceEntryType entry : entryList ) {

            if ( entry.getClass() == ParameterRefEntryType.class ) {
                //// pass the include condition?
                //if ( includedContainer != null ) {
                //    if ( includedContainer.getConditionList().size() > 0 ) {
                //        addParameter( (ParameterRefEntryType)entry,
                //                      currentStartBit,
                //                      containerStartBit,
                //                      container,
                //                      includedContainer.getConditionList() );
                //    } else {
                //        addParameter( (ParameterRefEntryType)entry,
                //                      currentStartBit,
                //                      containerStartBit,
                //                      container,
                //                      null );
                //    }
                //} else {
                //    addParameter( (ParameterRefEntryType)entry,
                //                  currentStartBit,
                //                  containerStartBit,
                //                  container,
                //                  null );
                //}
            } else if ( entry.getClass() == ContainerRefEntryType.class ) {
                System.out.println( "ContainerRefEntryType not yet implemented" );
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
            } else if ( entry.getClass() == ArrayParameterRefEntryType.class ) {
                String nameRef = ((ArrayParameterRefEntryType)entry).getParameterRef();
                warnings_.add( "Element ArrayParameterRefEntryType not yet supported for: " + nameRef );
            } else if ( entry.getClass() == ParameterSegmentRefEntryType.class ) {
                String nameRef = ((ParameterSegmentRefEntryType)entry).getParameterRef();
                warnings_.add( "Element ParameterSegmentRefEntryType not yet supported for: " + nameRef );
            } else if ( entry.getClass() == ContainerSegmentRefEntryType.class ) {
                String nameRef = ((ContainerSegmentRefEntryType)entry).getContainerRef();
                warnings_.add( "Element ContainerSegmentRefEntryType not yet supported for: " + nameRef );
            } else if ( entry.getClass() == StreamSegmentEntryType.class ) {
                String nameRef = ((StreamSegmentEntryType)entry).getStreamRef();
                warnings_.add( "Element StreamSegmentEntryType not yet supported for: " + nameRef );
            } else if ( entry.getClass() == IndirectParameterRefEntryType.class ) {
                warnings_.add( "Element IndirectParameterRefEntryType not yet supported" );
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

        throw new XTCEDatabaseException( "Unable to locate BaseMetaCommand Reference " +
                                         metaCommandRef +
                                         " in MetaCommand " +
                                         currentTelecommand.getName() );

    }

    private void applyAssignments( XTCETelecommand        telecommand,
                                   ArgumentAssignmentList argAssignments ) {

        if ( argAssignments == null ) {
            return;
        }

        List<ArgumentAssignment> assigns = argAssignments.getArgumentAssignment();

        for ( ArgumentAssignment assign : assigns ) {
            for ( XTCEContainerContentEntry entry : contentList_ ) {
                if ( entry.getEntryType() == FieldType.ARGUMENT ) {
                    if ( entry.getArgument().getName().equals( assign.getArgumentName() ) == true ) {
                        entry.setValue( assign );
                    }
                }
            }
        }

    }

    private XTCETelecommand telecommand_ = null;

}
