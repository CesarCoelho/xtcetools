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
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.omg.space.xtce.database.AggregateDataType;
import org.omg.space.xtce.database.AggregateDataType.MemberList.Member;
import org.omg.space.xtce.database.AliasSetType;
import org.omg.space.xtce.database.CommandContainerType;
import org.omg.space.xtce.database.CommandMetaDataType.MetaCommandSet.BlockMetaCommand;
import org.omg.space.xtce.database.DescriptionType.AncillaryDataSet;
import org.omg.space.xtce.database.MetaCommandType;
import org.omg.space.xtce.database.MetaCommandType.ArgumentList.Argument;
import org.omg.space.xtce.database.MetaCommandType.BaseMetaCommand;
import org.omg.space.xtce.database.NameDescriptionType;

/** This class serves as a convenient container for representing Telecommands
 * from the XTCE data model by abstracting the mechanics of assembling the
 * Argument and Parameter attributes that are derived from XTCE related rules
 * of processing, which can include processing of Parameter, Member, and Type
 * elements.
 *
 * This class endeavors to generate a minimum of additional data, storing
 * almost entirely references to objects already created in other parts of the
 * data model.  The two key exceptions to this are the Aliases and the TC
 * Arguments.  These are preprocessed during construction of the object for
 * later convenience.
 *
 * At present, Aliases do not apply through the inheritance chain.  The author
 * is not sure if they should or not.
 *
 * @author David Overeem
 *
 */

public class XTCETelecommand extends XTCENamedObject {

    /** Constructor
     *
     * This is a constructor for an XTCETelecommand object.
     *
     * @param path String containing the Space System path leading to this
     * object in the XTCE data model, but without the object name.
     *
     * @param iPath String containing the inheritance path of this telecommand
     * in the XTCE hierarchy.  This is not the same as a SpaceSystem location
     * path as the inheritance path depends on BaseMetaCommand elements
     * instead.
     *
     * @param metaCommandObj The object reference to the MetaCommand or the
     * BlockMetaCommand element from the XTCE data model as modeled by the
     * Java JAXB generated classes.
     *
     * @param dbReference An object reference to the XTCEDatabase that owns
     * this XTCE document in the internal data model.  This is used in the
     * staging of an XTCETelecommand object to assemble the Argument List while
     * taking into account the inheritance.
     *
     * @throws XTCEDatabaseException in the event that the arguments could not
     * be resolved, making this XTCETelecommand object of limited usefulness.
     *
     */

    XTCETelecommand( String       path,
                     String       iPath,
                     Object       metaCommandObj,
                     XTCEDatabase dbReference ) throws XTCEDatabaseException {

        super( getNameFromProperElement( metaCommandObj ),
               path,
               getAliasSetFromProperElement( metaCommandObj ),
               getAncillaryDataFromProperElement( metaCommandObj) );

        iPath_ = iPath;
        if ( metaCommandObj.getClass() == MetaCommandType.class ) {
            metaCommand_ = (MetaCommandType)metaCommandObj;
            populateArguments( dbReference );
            if ( metaCommand_.getCommandContainer() != null ) {
                container_ = new XTCETCContainer( path,
                                                  makeContainerInheritanceString( metaCommand_.getCommandContainer(), dbReference ),
                                                  metaCommand_.getCommandContainer() );
            }
            //System.out.println( "Made MetaCommand: " + getName() + " IPath: " + iPath_ + " Path: " + getSpaceSystemPath() );
            for ( XTCEArgument arg : argumentList_ ) {
                //System.out.println( "Argument " + arg.getName() + " type " + arg.getTypeReferenceFullPath() );
            }
        } else if ( metaCommandObj.getClass() == BlockMetaCommand.class ) {
            blockMetaCommand_ = (BlockMetaCommand)metaCommandObj;
            argumentList_ = new ArrayList<XTCEArgument>();
            //System.out.println( "Made MetaCommand: " + getName() + " IPath: " + iPath_ + " Path: " + getSpaceSystemPath() );
        }

    }

    /** Retrieve the inheritance path for this MetaCommand element.
     *
     * The "inheritance path" is different from the SpaceSystem locating path
     * in that it represents the parent MetaCommand elements leading to this
     * element through the BaseContainer element in the XTCE data model.  This
     * does not apply to BlockMetaCommand elements.
     *
     * @see #isMetaCommand()
     * @see #isBlockMetaCommand()
     *
     * @return String containing the inheritance path to this telecommand.
     *
     */

    public String getInheritancePath() {
        return iPath_;
    }

    /** Retrieve whether or not this XTCETelecommand represents a MetaCommand
     * element in the underlying XTCE data model.
     *
     * @return boolean true if this object represents a MetaCommand element or
     * false if it represents a BlockMetaCommand element.
     *
     */

    public boolean isMetaCommand() {
        return ( metaCommand_ != null );
    }

    /** Retrieve whether or not this XTCETelecommand represents a
     * BlockMetaCommand element in the underlying XTCE data model.
     *
     * @return boolean true if this object represents a BlockMetaCommand
     * element or false if it represents a MetaCommand element.
     *
     */

    public boolean isBlockMetaCommand() {
        return ( metaCommand_ == null );
    }

    /** Retrieve a reference to the underlying MetaCommand element in the
     * XTCE data model.
     *
     * @see #isMetaCommand()
     * @see #isBlockMetaCommand()
     *
     * @return MetaCommandType object if this XTCETelecommand object represents
     * a MetaCommand element in the XTCE data model, or null if it represents
     * the other case of a BlockMetaCommand element.
     *
     */

    public MetaCommandType getMetaCommandReference() {
        return metaCommand_;
    }

    /** Retrieve a reference to the underlying BlockMetaCommand element in the
     * XTCE data model.
     *
     * @see #isMetaCommand()
     * @see #isBlockMetaCommand()
     *
     * @return BlockMetaCommand object if this XTCETelecommand object
     * represents a BlockMetaCommand element in the XTCE data model, or null
     * if it represents the other case of a MetaCommand element.
     *
     */

    public BlockMetaCommand getBlockMetaCommandReference() {
        return blockMetaCommand_;
    }

    /** Retrieve a reference to the underlying CommandContainer element within
     * a MetaCommand element.
     *
     * @see #isMetaCommand()
     * @see #isBlockMetaCommand()
     *
     * @return XTCETCContainer object or null if there is no CommandContainer
     * element for this XTCETelecommand, which will always be the case if the
     * object represents a BlockMetaCommand element.
     *
     */

    public XTCETCContainer getCommandContainer() {
        return container_;
    }

    /** Retrieve the list of XTCEArgument objects that were constructed for
     * this XTCETelecommand, which includes all inherited Argument elements,
     * in addition to those defined with this XTCETelecommand.
     *
     * @return List of XTCEArgument objects, or an empty list is there are
     * no Arguments.  This list will never be null.
     *
     */

    public List<XTCEArgument> getArguments() {
        return argumentList_;
    }

    /** Retrieve the abstract attribute flag for this XTCETelecommand.
     *
     * The abstract flag indicates that this XTCETelecommand is based on a
     * representation of a MetaCommand element in the XTCE data model and that
     * it is not intended to be instantiated for transmission, rather used as
     * a BaseMetaCommand for a specific telecommand.
     *
     * @return boolean indicating if this XTCETelecommand object represents an
     * abstract MetaCommand element in the XTCE data model.
     *
     */

    public boolean isAbstract() {
        return ( metaCommand_ == null ? false : metaCommand_.isAbstract() );
    }

    /** Retrieve the short description attribute.
     *
     * @return String containing the short description or an empty string.  The
     * return value will never be null.
     *
     */

    public String getShortDescription() {
        if ( isMetaCommand() == true ) {
            return getPrimaryShortDescription( metaCommand_ );
        } else {
            return getPrimaryShortDescription( blockMetaCommand_ );
        }
    }

    /** Set the short description attribute.
     *
     * @param description String containing the new description, which can be
     * empty or null, which in either case will remove the short description
     * attribute for brevity on the XML document.
     *
     */

    public void setShortDescription( String description ) {
        if ( isMetaCommand() == true ) {
            setPrimaryShortDescription( metaCommand_, description );
        } else {
            setPrimaryShortDescription( blockMetaCommand_, description );
        }
    }

    /** Retrieve the Long Description element text content.
     *
     * @return String containing the long description or an empty string.  The
     * return value will never be null.
     *
     */

    public String getLongDescription() {
        if ( isMetaCommand() == true ) {
            return getPrimaryLongDescription( metaCommand_ );
        } else {
            return getPrimaryLongDescription( blockMetaCommand_ );
        }
    }

    /** Set the long description element text content.
     *
     * @param description String containing the new description, which can be
     * empty or null, which in either case will remove the long description
     * element for brevity on the XML document.
     *
     */

    public void setLongDescription( String description ) {
        if ( isMetaCommand() == true ) {
            setPrimaryLongDescription( metaCommand_, description );
        } else {
            setPrimaryLongDescription( blockMetaCommand_, description );
        }
    }

    /** Retrieves the preferred effective description of this Telecommand in
     * the XTCE data model.
     *
     * This method prefers the shortDescription attribute if it exists.  If it
     * does not, then the LongDescription element will be returned if present.
     *
     * @return String containing a single description text item for this
     * Container, generally suitable for display tables.  The String will never
     * be null.
     *
     */

    public String getDescription() {

        String parameterDescription = getShortDescription();
        if ( parameterDescription.isEmpty() == true ) {
            parameterDescription = getLongDescription();
        }

        return parameterDescription;

    }

    /** Retrieve an XML string that represents this MetaCommand element.
     *
     * @return String containing the XML fragment.
     *
     * @throws XTCEDatabaseException in the event that the elements being
     * marshaled from the JAXB internal classes cannot make a valid document.
     * Check the exception message for causality information.
     *
     */

    public String toXml() throws XTCEDatabaseException {

        try {

            JAXBElement            xmlElement = null;
            XTCEDocumentMarshaller mmm        = null;

            if ( isMetaCommand() == true ) {

                xmlElement = new JAXBElement( new QName(MetaCommandType.class.getSimpleName()),
                                                        MetaCommandType.class,
                                                        metaCommand_ );

                mmm = new XTCEDocumentMarshaller( MetaCommandType.class, true );

            } else if ( isBlockMetaCommand() == true ) {

                xmlElement = new JAXBElement( new QName(BlockMetaCommand.class.getSimpleName()),
                                                        BlockMetaCommand.class,
                                                        blockMetaCommand_ );

                mmm = new XTCEDocumentMarshaller( BlockMetaCommand.class, true );

            }

            return XTCEFunctions.xmlPrettyPrint( mmm.marshalToXml( xmlElement ) );

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException(
                getName() +
                ": " + // NOI18N
                XTCEFunctions.getText( "xml_marshal_error_telecommand" ) + // NOI18N
                " '" + // NOI18N
                ex.getCause() +
                "'" ); // NOI18N
        }

    }

    /** Private method to extract the name attribute based on whether this is
     * a MetaCommand or a BlockMetaCommand.
     *
     * @param element Object containing the XTCE data model element.
     *
     * @return String containing the name attribute.
     *
     * @throws XTCEDatabaseException thrown in the event that the Object passed
     * is neither of the expected objects, which is unlikely to ever occur.
     *
     */

    private static String getNameFromProperElement( Object element ) throws XTCEDatabaseException {
        if ( element.getClass() == MetaCommandType.class ) {
            return ((NameDescriptionType)element).getName();
        } else if ( element.getClass() == BlockMetaCommand.class ) {
            return ((NameDescriptionType)element).getName();
        } else {
            throw new XTCEDatabaseException( "Unknown telecommand object type" );
        }
    }

    /** Private method to extract the AliasSetType element from the XTCE data
     * model for the Object that this XTCETelecommand represents.
     *
     * @param element Object containing the XTCE data model element.
     *
     * @return AliasSetType from the appropriate MetaCommand or
     * BlockMetaCommand, which can be null.
     *
     */

    private static AliasSetType getAliasSetFromProperElement( Object element ) {
        if ( element.getClass() == MetaCommandType.class ) {
            return ((MetaCommandType)element).getAliasSet();
        } else if ( element.getClass() == BlockMetaCommand.class ) {
            return ((BlockMetaCommand)element).getAliasSet();
        }
        return null;
    }

    /** Private method to extract the AncillaryDataSet element from the XTCE
     * data model for the Object that this XTCETelecommand represents.
     *
     * @param element Object containing the XTCE data model element.
     *
     * @return AncillaryDataSet from the appropriate MetaCommand or
     * BlockMetaCommand, which can be null.
     *
     */

    private static AncillaryDataSet getAncillaryDataFromProperElement( Object element ) {
        if ( element.getClass() == MetaCommandType.class ) {
            return ((MetaCommandType)element).getAncillaryDataSet();
        } else if ( element.getClass() == BlockMetaCommand.class ) {
            return ((BlockMetaCommand)element).getAncillaryDataSet();
        }
        return null;
    }

    /** Private method to populate the List of XTCEArguments that are
     * located for this XTCETelecommand.
     *
     * This method only looks 1 deep for the BaseMetaCommand.  The recursion
     * happens automatically when grabbing the XTCETelecommand object from the
     * XTCESpaceSystem object.
     *
     * @param dbReference XTCEDatabase object used to retrieve the
     * XTCESpaceSystem object to construct an XTCETelecommand object that
     * represents the BaseMetaCommand for this object, if it exists.
     *
     * @throws XTCEDatabaseException thrown when the BaseMetaCommand cannot be
     * located.
     *
     */

    private void populateArguments( XTCEDatabase dbReference ) throws XTCEDatabaseException {

        argumentList_ = new ArrayList<XTCEArgument>();

        BaseMetaCommand baseTelecommandElement =
            getMetaCommandReference().getBaseMetaCommand();

        if ( baseTelecommandElement != null ) {
            String bmcRefPath = baseTelecommandElement.getMetaCommandRef();
            String bmcPath = XTCEFunctions.resolvePathReference( getFullPath(),
                                                                 bmcRefPath );
            String bmcName =
                XTCEFunctions.getNameFromPathReferenceString( bmcPath );
            String ssPath =
                XTCEFunctions.getPathNameFromReferenceString( bmcPath );
            XTCESpaceSystem ss = dbReference.getSpaceSystem( ssPath );
            XTCETelecommand telecommand = ss.getTelecommand( bmcName );
            argumentList_.addAll( telecommand.getArguments() );

        }

        populateMetaCommandArguments( dbReference );

    }

    /** Private method to retrieve and walk the Argument List of this
     * XTCETelecommand and stored the XTCEArgument objects in a list for later
     * retreival convenience.
     *
     * @param dbReference XTCEDatabase object used to locate the Argument Type
     * elements and also resolve recursive potential Aggregate Member
     * Arguments.
     *
     */

    private void populateMetaCommandArguments( XTCEDatabase dbReference ) {

        if ( metaCommand_.getArgumentList() == null ) {
            return;
        }

        List<Argument> argList = metaCommand_.getArgumentList().getArgument();

        for ( Argument arg : argList ) {

            String path = XTCEFunctions.resolvePathReference( getFullPath(),
                                                              arg.getArgumentTypeRef() );
            NameDescriptionType argType =
                dbReference.getArgumentTypeReference( path );

            argumentList_.add( new XTCEArgument( metaCommand_.getName(),
                                                 getSpaceSystemPath(),
                                                 arg,
                                                 argType ) );

            if ( ( argType != null ) && ( argType.getClass() == AggregateDataType.class ) ) {
                addArgumentMembers( metaCommand_.getName(), (AggregateDataType)argType, argumentList_, dbReference );
            }

        }

    }

    /** Private method to gather the Argument Members of an Aggregate Argument
     * Type and append those to the list.
     *
     * This method needs to be recursive since it is possible for the Aggregate
     * Argument to contain other Aggregate Arguments.
     *
     * @param basename String containing the Argument name that should be
     * prepended to all the Member Argument names.
     *
     * @param type AggregateDataType object from the XTCE data model that
     * contains the Argument Member list and their associated types.
     *
     * @param list List of XTCEArguments that represents the current
     * running list.  This will eventually become the private data member of
     * this object that contains all the XTCEArgument objects.
     *
     * @param dbReference XTCEDatabase object used to locate the Argument Type
     * elements and also resolve recursive potential Aggregate Member
     * Arguments.
     *
     */

    private void addArgumentMembers( String             basename,
                                     AggregateDataType  type,
                                     List<XTCEArgument> list,
                                     XTCEDatabase       dbReference ) {
        
        List<Member> members = type.getMemberList().getMember();

        for ( Member member : members ) {

            String mpath              = XTCEFunctions.resolvePathReference( getFullPath(), member.getTypeRef() );
            String newbasename        = basename + "." + member.getName();
            NameDescriptionType mtype = dbReference.getParameterTypeReference( mpath );
            
            list.add( new XTCEArgument( newbasename,
                                        getSpaceSystemPath(),
                                        member,
                                        mtype ) );

            if ( ( mtype != null ) && ( mtype.getClass() == AggregateDataType.class ) ) {
                addArgumentMembers( newbasename, (AggregateDataType)mtype, list, dbReference );
            }

        }

    }

    /** Private method to manufacture the CommandContainer inheritance string
     * path for the XTCETCContainer that is constructed to represent the
     * CommandContainer element for this XTCETelecommand.
     *
     * @param container The XTCE CommandContainerType element from the JAXB
     * generated classes.
     *
     * @param dbReference The XTCEDatabase object used to resolve the path
     * references to the concrete containers.
     *
     * @return String containing the inheritance path.
     *
     * @throws XTCEDatabaseException thrown in the event that the element for
     * the CommandContainer cannot be located, which would make this object of
     * limited usefulness.
     *
     */

    private String makeContainerInheritanceString( CommandContainerType container,
                                                   XTCEDatabase         dbReference ) throws XTCEDatabaseException {

        LinkedList<String> cpathList = new LinkedList<String>();
        CommandContainerType currentContainer = container;
        cpathList.addFirst( container.getName() );
        String currentSpaceSystemPath = getFullPath();

        while ( currentContainer.getBaseContainer() != null ) {
            String path = XTCEFunctions.resolvePathReference( currentSpaceSystemPath,
                                                              currentContainer.getBaseContainer().getContainerRef() );
            currentContainer = getCommandContainerElement( path, dbReference );
            currentSpaceSystemPath = XTCEFunctions.getPathNameFromReferenceString( path );
            cpathList.addFirst( currentContainer.getName() );
        }

        StringBuilder inheritancePathBuilder = new StringBuilder();
        for ( String containerName : cpathList ) {
            inheritancePathBuilder.append( "/" );
            inheritancePathBuilder.append( containerName );
        }

        return inheritancePathBuilder.toString();

    }

    /** Private method to retrieve the CommandContainer element for this
     * telecommand object represented.
     *
     * @param ssPath String containing the SpaceSystem path to this
     * telecommand object, which is used to anchor the CommandContainer to the
     * hierarchy of the XTCE document.
     *
     * @param dbReference The XTCEDatabase object used to resolve the path
     * references to the concrete containers.
     *
     * @return CommandContainerType object from the JAXB generated classed of
     * the XTCE data model.
     *
     * @throws XTCEDatabaseException thrown in the event that the element for
     * the CommandContainer cannot be located, which would make this object of
     * limited usefulness.
     *
     */

    private CommandContainerType getCommandContainerElement( String       ssPath,
                                                             XTCEDatabase dbReference ) throws XTCEDatabaseException {

        String name = XTCEFunctions.getNameFromPathReferenceString( ssPath );
        String path = XTCEFunctions.getPathNameFromReferenceString( ssPath );
        List<XTCESpaceSystem> spaceSystems = dbReference.getSpaceSystemTree();
        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            if ( spaceSystem.getFullPath().equals( path ) == true ) {
                try {
                    List<Object> metacommands = spaceSystem.getReference().getCommandMetaData().getMetaCommandSet().getMetaCommandOrMetaCommandRefOrBlockMetaCommand();
                    for ( Object metacommand : metacommands ) {
                        if ( metacommand.getClass() == MetaCommandType.class ) {
                            if ( ((MetaCommandType)metacommand).getCommandContainer().getName().equals( name ) == true ) {
                                return (((MetaCommandType)metacommand).getCommandContainer());
                            }
                        }
                    }
                } catch ( NullPointerException ex ) {
                    // maybe this space system has no commanding
                }
                try {
                    List<CommandContainerType> containers = spaceSystem.getReference().getCommandMetaData().getCommandContainerSet().getCommandContainer();
                    for ( CommandContainerType container : containers ) {
                        if ( container.getName().equals( name ) == true ) {
                            return container;
                        }
                    }
                } catch ( NullPointerException ex ) {
                    // maybe this space system has no command container set
                }
            }
        }

        throw new XTCEDatabaseException( "Container named " + name + " not found in Space System " + path );

    }


    // Private Data Members

    private String                  iPath_            = null;
    private MetaCommandType         metaCommand_      = null;
    private BlockMetaCommand        blockMetaCommand_ = null;
    private ArrayList<XTCEAlias>    aliasList_        = null;
    private ArrayList<XTCEArgument> argumentList_     = null;
    private XTCETCContainer         container_        = null;

}
