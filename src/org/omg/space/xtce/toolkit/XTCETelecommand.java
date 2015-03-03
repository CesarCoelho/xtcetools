/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import org.omg.space.xtce.database.MetaCommandType;
import org.omg.space.xtce.database.MetaCommandType.ArgumentList.Argument;
import org.omg.space.xtce.database.NameDescriptionType;

/**
 *
 * @author Melanie Laub
 */

public class XTCETelecommand extends XTCENamedObject {

    XTCETelecommand( String       path,
                     String       iPath,
                     Object       metaCommandObj,
                     XTCEDatabase dbReference ) throws XTCEDatabaseException {

        super( getNameFromProperElement( metaCommandObj ),
               path,
               getAliasSetFromProperElement( metaCommandObj ) );
        iPath_ = iPath;
        if ( metaCommandObj.getClass() == MetaCommandType.class ) {
            metaCommand_ = (MetaCommandType)metaCommandObj;
            populateMetaCommandArguments( dbReference );
            if ( metaCommand_.getCommandContainer() != null ) {
                container_ = new XTCETCContainer( path,
                                                  makeContainerInheritanceString( metaCommand_.getCommandContainer(), dbReference ),
                                                  metaCommand_.getCommandContainer() );
            }
            System.out.println( "Made MetaCommand: " + getName() + " IPath: " + iPath_ + " Path: " + getSpaceSystemPath() );
            for ( XTCEArgument arg : argumentList_ ) {
                System.out.println( "Argument " + arg.getName() + " type " + arg.getTypeReferenceFullPath() );
            }
        } else if ( metaCommandObj.getClass() == BlockMetaCommand.class ) {
            blockMetaCommand_ = (BlockMetaCommand)metaCommandObj;
            argumentList_ = new ArrayList<XTCEArgument>();
            System.out.println( "Made MetaCommand: " + getName() + " IPath: " + iPath_ + " Path: " + getSpaceSystemPath() );
        }

    }

    public String getInheritancePath() {
        return iPath_;
    }

    public boolean isMetaCommand() {
        return ( metaCommand_ != null );
    }

    public boolean isBlockMetaCommand() {
        return ( metaCommand_ == null );
    }

    public MetaCommandType getMetaCommandReference() {
        return metaCommand_;
    }

    public BlockMetaCommand getBlockMetaCommandReference() {
        return blockMetaCommand_;
    }

    public XTCETCContainer getCommandContainer() {
        return container_;
    }

    public boolean isAbstract() {
        return ( metaCommand_ == null ? false : metaCommand_.isAbstract() );
    }

    public String getShortDescription() {
        if ( isMetaCommand() == true ) {
            return getPrimaryShortDescription( metaCommand_ );
        } else {
            return getPrimaryShortDescription( blockMetaCommand_ );
        }
    }

    public void setShortDescription( String description ) {
        if ( isMetaCommand() == true ) {
            setPrimaryShortDescription( metaCommand_, description );
        } else {
            setPrimaryShortDescription( blockMetaCommand_, description );
        }
    }

    public String getLongDescription() {
        if ( isMetaCommand() == true ) {
            return getPrimaryLongDescription( metaCommand_ );
        } else {
            return getPrimaryLongDescription( blockMetaCommand_ );
        }
    }

    public void setLongDescription( String description ) {
        if ( isMetaCommand() == true ) {
            setPrimaryLongDescription( metaCommand_, description );
        } else {
            setPrimaryLongDescription( blockMetaCommand_, description );
        }
    }

    /** Retrieves the preferred effective description of this Telecommand in the
     * XTCE data model.
     *
     * This method prefers the shortDescription attribute if it exists.  If it
     * does not, then the LongDescription element will be used if present.
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

            return mmm.marshalToXml( xmlElement );

        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( "Failed to create XML from Parameter Object: " + ex.getCause() );
        }

    }

    private static String getNameFromProperElement( Object element ) throws XTCEDatabaseException {
        if ( element.getClass() == MetaCommandType.class ) {
            return ((MetaCommandType)element).getName();
        } else if ( element.getClass() == BlockMetaCommand.class ) {
            return ((BlockMetaCommand)element).getName();
        } else {
            throw new XTCEDatabaseException( "Unknown telecommand object type" );
        }
    }

    private static AliasSetType getAliasSetFromProperElement( Object element ) {
        if ( element.getClass() == MetaCommandType.class ) {
            return ((MetaCommandType)element).getAliasSet();
        } else if ( element.getClass() == BlockMetaCommand.class ) {
            return ((BlockMetaCommand)element).getAliasSet();
        }
        return null;
    }

    private void populateMetaCommandArguments( XTCEDatabase dbReference ) {

        argumentList_ = new ArrayList<XTCEArgument>();

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

    private void addArgumentMembers( String basename, AggregateDataType type, ArrayList<XTCEArgument> list, XTCEDatabase dbReference ) {
        
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

    private String makeContainerInheritanceString( CommandContainerType container, XTCEDatabase dbReference ) throws XTCEDatabaseException {

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

    private CommandContainerType getCommandContainerElement( String ssPath, XTCEDatabase dbReference ) throws XTCEDatabaseException {

        String name = XTCEFunctions.getNameFromPathReferenceString( ssPath );
        String path = XTCEFunctions.getPathNameFromReferenceString( ssPath );
        ArrayList<XTCESpaceSystem> spaceSystems = dbReference.getSpaceSystemTree();
        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            if ( spaceSystem.getFullPath().equals( path ) == true ) {
                try {
                    List<Object> metacommands = spaceSystem.getReference().getCommandMetaData().getMetaCommandSet().getMetaCommandOrMetaCommandRefOrBlockMetaCommand();
                    for ( Object metacommand : metacommands ) {
                        if ( metacommand.getClass() == MetaCommandType.class ) {
                            if ( ((MetaCommandType)metacommand).getCommandContainer().getName().equals( name ) == true ) {
                                return (CommandContainerType)(((MetaCommandType)metacommand).getCommandContainer());
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

    private String                  iPath_            = null;
    private MetaCommandType         metaCommand_      = null;
    private BlockMetaCommand        blockMetaCommand_ = null;
    private ArrayList<XTCEAlias>    aliasList_        = null;
    private ArrayList<XTCEArgument> argumentList_     = null;
    private XTCETCContainer         container_        = null;

}
