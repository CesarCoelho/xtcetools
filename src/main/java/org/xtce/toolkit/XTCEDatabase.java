/* Copyright 2015 David Overeem (dovereem@startmail.com)
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.omg.space.xtce.NameDescriptionType;
import org.omg.space.xtce.SpaceSystemType;

/** The XTCEDatabase class is the first core object to be used by a client
 * that is working with an XTCE Database File.
 *
 *
 * @author David Overeem
 *
 */

public final class XTCEDatabase extends XTCEDatabaseParser {

    /** Constructor for use with an XTCE database file on the filesystem.
     *
     * Successfully constructing this object means that the XTCE database file
     * was successfully loaded and methods can be called on the contents.
     *
     * @param dbLocation URL object containing the location of the XTCE
     * document to load.
     *
     * @param validateOnLoad boolean indicating if the XSD validation should be
     * performed during the loading.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     * @param readOnly boolean indicating if the document should be opened in
     * a read-only context, which is faster because only the JAXB structure is
     * created, avoiding the need to build the Document Object Model that is
     * needed for round trip processing.
     *
     * @throws XTCEDatabaseException in the event that the file could not be
     * successfully loaded in a valid state.  This can be partly bypassed by
     * not enabling the XSD validation, which is not recommended because it
     * may de-stabilize the application using this data file.
     *
     */

    public XTCEDatabase( URL     dbLocation,
                         boolean validateOnLoad,
                         boolean applyXIncludes,
                         boolean readOnly ) throws XTCEDatabaseException {

        topLevelSpaceSystem = loadDatabase( dbLocation,
                                            validateOnLoad,
                                            applyXIncludes,
                                            readOnly );

        setFilename( new File( dbLocation.getPath() ) );
        cacheParameterTypes();
        cacheArgumentTypes();

    }

    /** Constructor for use with an XTCE database file on the filesystem.
     *
     * Successfully constructing this object means that the XTCE database file
     * was successfully loaded and methods can be called on the contents.
     *
     * @param dbFile File object containing the name and path to the XTCE
     * database file to load and optionally validate.
     *
     * @param validateOnLoad boolean indicating if the XSD validation should be
     * performed during the loading.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     * @param readOnly boolean indicating if the document should be opened in
     * a read-only context, which is faster because only the JAXB structure is
     * created, avoiding the need to build the Document Object Model that is
     * needed for round trip processing.
     *
     * @throws XTCEDatabaseException in the event that the file could not be
     * successfully loaded in a valid state.  This can be partly bypassed by
     * not enabling the XSD validation, which is not recommended because it
     * may de-stabilize the application using this data file.
     *
     */

    public XTCEDatabase( File    dbFile,
                         boolean validateOnLoad,
                         boolean applyXIncludes,
                         boolean readOnly ) throws XTCEDatabaseException {

        topLevelSpaceSystem = loadDatabase( dbFile,
                                            validateOnLoad,
                                            applyXIncludes,
                                            readOnly );

        setFilename( dbFile );
        cacheParameterTypes();
        cacheArgumentTypes();

    }
    /** Constructor for use with an XTCE database file from a stream.
     *
     * Successfully constructing this object means that the XTCE database file
     * was successfully loaded and methods can be called on the contents.
     *
     * @param istream InputStream containing the stream to read the data from.
     *
     * @param dbFile File containing the name and path to the XTCE
     * database file to load and optionally validate.
     *
     * @param validateOnLoad boolean indicating if the XSD validation should be
     * performed during the loading.
     *
     * @param applyXIncludes boolean indicating if the XInclude processing for
     * the loaded file should be applied or ignored.
     *
     * @param readOnly boolean indicating if the document should be opened in
     * a read-only context, which is faster because only the JAXB structure is
     * created, avoiding the need to build the Document Object Model that is
     * needed for round trip processing.
     *
     * @throws XTCEDatabaseException in the event that the file could not be
     * successfully loaded in a valid state.  This can be partly bypassed by
     * not enabling the XSD validation, which is not recommended because it
     * may de-stabilize the application using this data file.
     *
     */

    public XTCEDatabase( InputStream istream,
                         File        dbFile,
                         boolean     validateOnLoad,
                         boolean     applyXIncludes,
                         boolean     readOnly ) throws XTCEDatabaseException {

        topLevelSpaceSystem = loadDatabase( istream,
                                            dbFile.getParent(),
                                            validateOnLoad,
                                            applyXIncludes,
                                            readOnly );

        setFilename( dbFile );
        cacheParameterTypes();
        cacheArgumentTypes();

    }

    /** Constructor for creating a new XTCE database object based on a top
     * level SpaceSystem element name.
     *
     * @param topLevelSpaceSystemName String containing the name of the top
     * level SpaceSystem element to create a new and empty XTCE database.
     *
     * @throws XTCEDatabaseException in the event that the name being used
     * cannot be a name for the top level SpaceSystem element.  Examine the
     * exception message for more details on the cause.
     *
     */

    public XTCEDatabase ( String topLevelSpaceSystemName ) throws XTCEDatabaseException {

        topLevelSpaceSystem = newDatabase( topLevelSpaceSystemName );

        setFilename( new File( "" ) );
        setChanged( true );

    }

    /** Retrieve the metrics for the XTCE document represented by this object.
     *
     * The metrics returned are inclusive and recursive to the top level Space
     * System in the XTCE data model.  Metrics for a singular Space System
     * element can be obtained using the getMetrics() method on the
     * XTCESpaceSystem class.
     *
     * @return XTCESpaceSystemMetrics object containing a variety of counts.
     *
     */

    public XTCESpaceSystemMetrics getMetrics( ) {
        return new XTCESpaceSystemMetrics( this );
    }

    /** Function to save the currently loaded database file.
     *
     * @param dbFile File object containing the file and path for which to save
     * the file containing the XTCE document.
     *
     * @throws XTCEDatabaseException thrown in the event that the file cannot
     * be saved.  The caller should inspect the message inside the exception
     * for more specific details on the cause of this error.
     *
     */

    public void save( File dbFile ) throws XTCEDatabaseException {

        if ( getChanged() == false ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_save_nochange" ) ); // NOI18N
        }

        saveDatabase( dbFile );
        setFilename( dbFile );
        setChanged( false );

    }

    /** Retrieve an arbitrary SpaceSystem wrapped element from the document.
     *
     * @param fullPath String containing the full path to the XTCE SpaceSystem
     * element using the fully qualified UNIX style path rules of an XTCE
     * reference.
     *
     * @return XTCESpaceSystem object containing the SpaceSystem and also some
     * helper functions.  If not found, the return can be null.
     *
     */

    public XTCESpaceSystem getSpaceSystem( String fullPath ) {

        for ( XTCESpaceSystem spaceSystem : getSpaceSystemTree() ) {
            if ( spaceSystem.getFullPath().equals( fullPath ) == true ) {
                return spaceSystem;
            }
        }

        return null;

    }

    /** Retrieve an XTCESpaceSystem object that represents the root SpaceSystem
     * element of this XTCE document.
     *
     * @return XTCESpaceSystem representing the root SpaceSystem, which for any
     * valid document can never be null.
     *
     */

    public XTCESpaceSystem getRootSpaceSystem( ) {

        return new XTCESpaceSystem( "/" + topLevelSpaceSystem.getName(), // NOI18N
                                    topLevelSpaceSystem,
                                    this );

    }

    /** Retrieve a list of all the SpaceSystem elements in this XTCE document,
     * wrapped inside XTCESpaceSystem objects.
     *
     * @return List of XTCESpaceSystem objects that are created from the
     * structure of the XTCE document.
     *
     */

    public List<XTCESpaceSystem> getSpaceSystemTree( ) {

        if ( spaceSystemCache != null ) {
            return spaceSystemCache;
        }

        spaceSystemCache = new ArrayList<>();

        XTCESpaceSystem rootSpaceSystem =
            new XTCESpaceSystem( "/" + topLevelSpaceSystem.getName(), // NOI18N
                                 topLevelSpaceSystem,
                                 this );
        spaceSystemCache.add( rootSpaceSystem );
        
        recurseSpaceSystems( rootSpaceSystem, spaceSystemCache );

        return spaceSystemCache;
        
    }

    /** Function to add a new Space System element to the XTCE document
     * structure.
     *
     * @param name String containing the name of the Space System to add.
     *
     * @param path String containing the fully qualified XTCE UNIX style path
     * to the new Space System in the hierarchy.
     *
     * @throws XTCEDatabaseException thrown in the event that this add method
     * is called for a root SpaceSystem element or the desired name conflicts
     * with another SpaceSystem element that already exists.
     *
     */

    public void addSpaceSystem( String name,
                                String path ) throws XTCEDatabaseException {

        List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

        if ( path == null || path.isEmpty() == true ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_addrootss" ) ); // NOI18N
        }

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            if ( spaceSystems.get( iii ).getFullPath().equals( path ) == true ) {
                XTCESpaceSystem parentSpaceSystem = spaceSystems.get( iii );
                List<SpaceSystemType> list = parentSpaceSystem.getReference().getSpaceSystem();
                for ( int jjj = 0; jjj < list.size(); ++jjj ) {
                    if ( list.get( jjj ).getName().equals( name ) == true ) {
                        throw new XTCEDatabaseException(
                            XTCEFunctions.getText( "ss_name_text" ) + // NOI18N
                            " " + // NOI18N
                            name +
                            " " + // NOI18N
                            XTCEFunctions.getText( "error_ssexists" ) + // NOI18N
                            " " + // NOI18N
                            path );
                    }
                }
                SpaceSystemType newSpaceSystem = new SpaceSystemType();
                newSpaceSystem.setName( name );
                list.add( newSpaceSystem );
            }
        }

        setChanged( true );
        spaceSystemCache = null;

    }

    /** Deletes a SpaceSystem element from the current XTCE document.
     *
     * @param ssPath String containing the fully qualified path to the XTCE
     * SpaceSystem element that should be removed from the data model
     * structure.
     *
     * @throws XTCEDatabaseException thrown in the event that this method is
     * called to remove the root SpaceSystem or the SpaceSystem element to be
     * removed cannot be located (as in, it does not exist).
     *
     */

    public void deleteSpaceSystem( String ssPath ) throws XTCEDatabaseException {

        List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

        int lastSlashIndex = ssPath.lastIndexOf('/');
        String pathOnly = ssPath.substring( 0, lastSlashIndex );
        String nameOnly = ssPath.substring( lastSlashIndex + 1 );

        if ( pathOnly == null || pathOnly.isEmpty() == true ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_rootssnodelete" ) ); // NOI18N
        }

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            if ( spaceSystems.get( iii ).getFullPath().equals( pathOnly ) == true ) {
                XTCESpaceSystem parentSpaceSystem = spaceSystems.get( iii );
                List<SpaceSystemType> list = parentSpaceSystem.getReference().getSpaceSystem();
                for ( int jjj = 0; jjj < list.size(); ++jjj ) {
                    if ( list.get( jjj ).getName().equals( nameOnly ) == true ) {
                        list.remove( jjj );
                        setChanged( true );
                        spaceSystemCache = null;
                        return;
                    }
                }
            }
        }

        throw new XTCEDatabaseException( XTCEFunctions.getText( "error_ssnotfound" ) + ": " + ssPath ); // NOI18N

    }

    /** Function to retrieve all of the Telemetry Parameters that are defined
     * in the XTCE document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCEParameter objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no telemetry
     * parameters, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCEParameter> getTelemetryParameters( ) {

        List<XTCESpaceSystem>    spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter> list         = new ArrayList<>();

        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            list.addAll( spaceSystem.getTelemetryParameters() );
        }

        return list;

    }

    /** Function to retrieve all of the Telemetry Parameters that are defined
     * in the XTCE document that match a glob style name pattern.
     *
     * Since the parameter name in XTCE is unique by Space System, it is
     * possible for this method to return multiple results even for a name that
     * is exact.
     *
     * TODO: This function can be optimized for searches that do not include
     * glob matching.  Not sure if this is needed though.
     *
     * @param nameGlob String containing a precise name or a glob of potential
     * names.
     *
     * @return List of XTCEParameter objects found, which can be empty.
     *
     */

    public List<XTCEParameter> getTelemetryParameters( String nameGlob ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            List<XTCEParameter> parameters =
                spaceSystems.get( iii ).getTelemetryParameters();
            for ( XTCEParameter parameter : parameters ) {
                if ( XTCEFunctions.matchesUsingGlob( parameter.getName(), nameGlob ) == true ) {
                    list.add( parameter );
                }
            }
        }

        return list;

    }

    /** Function to retrieve all of the Telemetry Parameters that are defined
     * in the XTCE document that match a glob style alias pattern in a
     * specified namespace.
     *
     * @param aliasGlob String containing a precise alias or a glob of
     * potential alias strings.
     *
     * @param aliasNameSpace String containing the namespace of the alias in
     * the XTCE data model.
     *
     * @return List of XTCEParameter objects found, which can be empty.
     *
     */

    public List<XTCEParameter> getTelemetryParameters( String aliasGlob,
                                                       String aliasNameSpace ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            List<XTCEParameter> parameters =
                spaceSystems.get( iii ).getTelemetryParameters();
            for ( XTCEParameter parameter : parameters ) {
                String alias = parameter.getAlias( aliasNameSpace );
                if ( ( alias.isEmpty()                                    == false ) &&
                     ( XTCEFunctions.matchesUsingGlob( alias, aliasGlob ) == true  ) ) {
                    list.add( parameter );
                }
            }
        }

        return list;

    }
    
    /** Function to retrieve all of the Telecommand Parameters that are defined
     * in the XTCE document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCEParameter objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no telecommand
     * parameters, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCEParameter> getTelecommandParameters( ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            list.addAll( spaceSystem.getTelecommandParameters() );
        }

        return list;

    }

    /** Function to retrieve all of the Telecommand Parameters that are defined
     * in the XTCE document that match a glob style name pattern.
     *
     * Since the parameter name in XTCE is unique by Space System, it is
     * possible for this method to return multiple results even for a name that
     * is exact.
     *
     * TODO: This function can be optimized for searches that do not include
     * glob matching.  Not sure if this is needed though.
     *
     * @param nameGlob String containing a precise name or a glob of potential
     * names.
     *
     * @return List of XTCEParameter objects found, which can be empty.
     *
     */

    public List<XTCEParameter> getTelecommandParameters( String nameGlob ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            List<XTCEParameter> parameters =
                spaceSystems.get( iii ).getTelecommandParameters();
            for ( XTCEParameter parameter : parameters ) {
                if ( XTCEFunctions.matchesUsingGlob( parameter.getName(), nameGlob) == true ) {
                    list.add( parameter );
                }
            }
        }

        return list;

    }

    /** Function to retrieve all of the Telecommand Parameters that are defined
     * in the XTCE document that match a glob style alias pattern in a
     * specified namespace.
     *
     * @param aliasGlob String containing a precise alias or a glob of
     * potential alias strings.
     *
     * @param aliasNameSpace String containing the namespace of the alias in
     * the XTCE data model.
     *
     * @return List of XTCEParameter objects found, which can be empty.
     *
     */

    public List<XTCEParameter> getTelecommandParameters( String aliasGlob,
                                                         String aliasNameSpace ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {
            List<XTCEParameter> parameters =
                spaceSystems.get( iii ).getTelecommandParameters();
            for ( XTCEParameter parameter : parameters ) {
                String alias = parameter.getAlias( aliasNameSpace );
                if ( ( alias.isEmpty()                                    == false ) &&
                     ( XTCEFunctions.matchesUsingGlob( alias, aliasGlob ) == true  ) ) {
                    list.add( parameter );
                }
            }
        }

        return list;

    }

    /** Function to retrieve all of the Parameters that are defined in the XTCE
     * document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCEParameter objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no
     * parameters, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCEParameter> getParameters( ) {

        List<XTCESpaceSystem>      spaceSystems = getSpaceSystemTree();
        ArrayList<XTCEParameter>   list         = new ArrayList<>();

        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            list.addAll( spaceSystem.getParameters() );
        }

        return list;

    }

    /** Function to retrieve all of the Telemetry Containers that are defined
     * in the XTCE document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCETMContainer objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no
     * containers, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCETMContainer> getContainers( ) {

       List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
       List<XTCETMContainer> containers   = new ArrayList<>();

       for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
           containers.addAll( spaceSystem.getContainers() );
       }
       
       return containers;

    }

    /** Retrieve a specific container in the XTCE database by the fully
     * qualified path name to the container, using XTCE document path rules.
     *
     * @param contFullPath String containing the fully qualified path to the
     * container desired.
     *
     * @return XTCETMContainer representing the SequenceContainer element in
     * the XTCE data model.
     *
     * @throws XTCEDatabaseException thrown in the event that the container
     * cannot be located using the provided path.
     *
     */

    public XTCETMContainer getContainer( String contFullPath )
        throws XTCEDatabaseException {

        String contPath =
            XTCEFunctions.getPathNameFromReferenceString( contFullPath );
        String contName =
            XTCEFunctions.getNameFromPathReferenceString( contFullPath );

        try {

            XTCESpaceSystem spaceSystem = getSpaceSystem( contPath );

            return spaceSystem.getContainer( contName );

        } catch ( NullPointerException ex ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "dialog_nolocatespacesystem_text" ) + // NOI18N
                " '" + contPath + "'" ); // NOI18N
        }

    }

    /** Retrieve a List of SequenceContainers that match a user provided string
     * glob, modeled as XTCETMContainer objects.
     *
     * @param nameGlob String containing a glob style matching pattern to match
     * against the container names.
     *
     * @return List of XTCETMContainer objects representing the containers
     * that match the provided glob or an empty list if there are no matches.
     *
     */

    public List<XTCETMContainer> getContainers( String nameGlob ) {

        List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
        List<XTCETMContainer> list         = new ArrayList<>();

        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            list.addAll( spaceSystem.getContainers( nameGlob ) );
        }

        return list;

    }

    /** Function to retrieve all of the Telecommands that are defined
     * in the XTCE document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCETelecommand objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no
     * containers, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCETelecommand> getTelecommands( ) {

       List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
       List<XTCETelecommand> containers   = new ArrayList<>();

       for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
           containers.addAll( spaceSystem.getTelecommands() );
       }

       return containers;

    }

    /** Retrieve a List of Telecommands that match a user provided string
     * glob, modeled as XTCETelecommand objects.
     *
     * @param nameGlob String containing a glob style matching pattern to match
     * against the telecommand names.
     *
     * @return List of XTCETelecommand objects representing the telecommands
     * that match the provided glob or an empty list if there are no matches.
     *
     */

    public List<XTCETelecommand> getTelecommands( String nameGlob ) {

        List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();
        List<XTCETelecommand> list         = new ArrayList<>();

        for ( XTCESpaceSystem spaceSystem : spaceSystems ) {
            list.addAll( spaceSystem.getTelecommands( nameGlob ) );
        }

        return list;

    }

    /** Retrieve a specific telecommand in the XTCE database by the fully
     * qualified path name to the telecommand, using XTCE document path rules.
     *
     * @param contFullPath String containing the fully qualified path to the
     * telecommand desired.
     *
     * @return XTCETelecommand representing the MetaCommand element in
     * the XTCE data model.
     *
     * @throws XTCEDatabaseException thrown in the event that the telecommand
     * cannot be located using the provided path.
     *
     */

    public XTCETelecommand getTelecommand( String contFullPath )
        throws XTCEDatabaseException {

        String contPath =
            XTCEFunctions.getPathNameFromReferenceString( contFullPath );
        String contName =
            XTCEFunctions.getNameFromPathReferenceString( contFullPath );

        try {

            XTCESpaceSystem spaceSystem = getSpaceSystem( contPath );

            return spaceSystem.getTelecommand( contName );

        } catch ( NullPointerException ex ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "dialog_nolocatespacesystem_text" ) + // NOI18N
                " '" + contPath + "'" ); // NOI18N
        }

    }

    /** Function to retrieve all of the Telemetry Streams that are defined
     * in the XTCE document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to return the entire contents of the XTCE database file.
     *
     * @return List of XTCETMStream objects that exist in the entirety
     * of the file.  The list can possibly be empty if there are no
     * containers, which is likely only to happen on a newly created database
     * file.
     *
     */

    public List<XTCETMStream> getStreams( ) {

       ArrayList<XTCETMStream> streams = new ArrayList<>();

       for ( XTCESpaceSystem spaceSystem : getSpaceSystemTree() ) {
           streams.addAll( spaceSystem.getStreams() );
       }
       
       return streams;

    }

    /** Function to retrieve a the Telemetry Streams that is defined in the
     * XTCE document.
     *
     * Similar functions exist on the XTCESpaceSystem objects.  This one is
     * intended to search the entire contents of the XTCE database file.
     *
     * @param name String containing a specific stream name to locate.
     *
     * @return XTCETMStream object if it exists in the document
     *
     * @throws XTCEDatabaseException in the event that the stream does not
     * exist or does not process correctly.  Interrogate the reason in the
     * exception for more information.
     *
     */

    public XTCETMStream getStream( String name ) throws XTCEDatabaseException {

       for ( XTCESpaceSystem spaceSystem : getSpaceSystemTree() ) {
           for ( XTCETMStream stream : spaceSystem.getStreams() ) {
               if ( stream.getName().equals( name ) == true ) {
                   return stream;
               }
           }

       }
       
       throw new XTCEDatabaseException(
            XTCEFunctions.getText( "error_stream_notfound" ) + // NOI18N
            ": '" + name + "'" ); // NOI18N

    }

    /** Function to decompose an XTCETMContainer object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * @param container XTCETMContainer object containing the container/packet
     * that the caller wishes to decompose.
     *
     * @param userValues List of XTCEContainerEntryValue objects that
     * represent desired setpoints for parameters in the container.  This
     * permits the caller to decompose a specific packet instance from a
     * container by specifying values for parameters that satisfy include
     * conditions for variable content.  Restriction values for Base Container
     * portions are automatically applied and do not need to be supplied by
     * the caller.
     *
     * @param showAllConditions boolean indicating if the returned content
     * model should provide an array of entry results that include information
     * only rows.  These information only rows consist of rows to announce the
     * start of a new Container or a new Aggregate.  If false, only those rows
     * will be returned for which a concrete start bit and length exist.
     *
     * @return XTCEContainerContentModel representing this XTCETMContainer.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the container completely due to bad references in
     * the XTCE document.
     *
     */

    public XTCEContainerContentModel processContainer( XTCETMContainer               container,
                                                       List<XTCEContainerEntryValue> userValues,
                                                       boolean                       showAllConditions )
        throws XTCEDatabaseException {

        return new XTCEContainerContentModel( container,
                                              getSpaceSystemTree(),
                                              userValues,
                                              showAllConditions );

    }

    /** Function to decompose an XTCETMContainer object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * @param container XTCETMContainer object containing the container/packet
     * that the caller wishes to decompose.
     *
     * @param binaryData BitSet containing the container binary encoded data
     * so that the output object contains entries with actual values from a
     * real binary image.
     *
     * @return XTCEContainerContentModel representing this XTCETMContainer.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the container completely due to bad references in
     * the XTCE document.
     *
     */

    public XTCEContainerContentModel processContainer( XTCETMContainer container,
                                                       BitSet          binaryData )
        throws XTCEDatabaseException {

        return new XTCEContainerContentModel( container,
                                              getSpaceSystemTree(),
                                              binaryData );

    }

    /** Function to decompose an XTCETMContainer object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * This function is intended to accept the byte array that is read from
     * a ByteArrayOutputStream.toByteArray() that is easily obtained when
     * reading a binary file using a Java FileInputStream.
     *
     * @param container XTCETMContainer object containing the container/packet
     * that the caller wishes to decompose.
     *
     * @param bytes byte[] containing the container binary encoded data
     * so that the output object contains entries with actual values from a
     * real binary image.
     *
     * @return XTCEContainerContentModel representing this XTCETMContainer.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the container completely due to bad references in
     * the XTCE document.
     *
     */

    public XTCEContainerContentModel processContainer( XTCETMContainer container,
                                                       byte[]          bytes )
        throws XTCEDatabaseException {

        BitSet bits = XTCEFunctions.getBitSetFromStreamByteArray( bytes );

        return processContainer( container, bits );

    }

    /** Function to decompose an XTCETMContainer object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * This function is intended to accept a Java InputStream containing the
     * bytes to use for the binary portion of the container.
     *
     * @param container XTCETMContainer object containing the container/packet
     * that the caller wishes to decompose.
     *
     * @param stream InputStream containing the container binary encoded data
     * so that the output object contains entries with actual values from a
     * real binary image.
     *
     * @return XTCEContainerContentModel representing this XTCETMContainer.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the container completely due to bad references in
     * the XTCE document, or if the stream throws an IOException.
     *
     */

    public XTCEContainerContentModel processContainer( XTCETMContainer container,
                                                       InputStream     stream )
        throws XTCEDatabaseException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int byteValue;

        try {
            while ( ( byteValue = stream.read() ) != -1 ) {
                buffer.write( byteValue );
            }
        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( ex.getLocalizedMessage() );
        }

        return processContainer( container, buffer.toByteArray() );

    }

    /** Function to decompose an XTCETelecommand object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * @param tcObject XTCETelecommand object containing the telecommand
     * that the caller wishes to decompose.
     *
     * @param userValues List of XTCEContainerEntryValue objects that
     * represent desired setpoints for arguments and/or parameters in the
     * telecommand container.  This permits the caller to decompose a specific
     * telecommand instance from a more general telecommand by specifying
     * values for parameters that satisfy include conditions for variable
     * content.  Restriction values for Base MetaComamand portions are
     * automatically applied and do not need to be supplied by the caller.
     *
     * @param showAllConditions boolean indicating if the returned content
     * model should provide an array of entry results that include information
     * only rows.  These information only rows consist of rows to announce the
     * start of a new Container or a new Aggregate.  If false, only those rows
     * will be returned for which a concrete start bit and length exist.
     *
     * @return XTCEContainerContentModel representing this XTCETelecommand.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the container completely due to bad references in
     * the XTCE document.
     *
     */

    public XTCETelecommandContentModel processTelecommand( XTCETelecommand               tcObject,
                                                           List<XTCEContainerEntryValue> userValues,
                                                           boolean                       showAllConditions )
        throws XTCEDatabaseException {

        return new XTCETelecommandContentModel( tcObject,
                                                getSpaceSystemTree(),
                                                userValues,
                                                showAllConditions );

    }

    /** Function to decompose an XTCETelecommand object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * @param telecommand XTCETelecommand object containing the telecommand
     * that the caller wishes to decompose.
     *
     * @param binaryData BitSet containing the telecommand binary encoded data
     * so that the output object contains entries with actual values from a
     * real binary image.
     *
     * @return XTCETelecommandContentModel representing this XTCETelecommand.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the telecommand completely due to bad references
     * in the XTCE document.
     *
     */

    public XTCETelecommandContentModel processTelecommand( XTCETelecommand telecommand,
                                                           BitSet          binaryData )
        throws XTCEDatabaseException {

        return new XTCETelecommandContentModel( telecommand,
                                                getSpaceSystemTree(),
                                                binaryData );

    }

    /** Function to decompose an XTCETelecommand object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * This function is intended to accept the byte array that is read from
     * a ByteArrayOutputStream.toByteArray() that is easily obtained when
     * reading a binary file using a Java FileInputStream.
     *
     * @param telecommand XTCETelecommand object containing the telecommand
     * that the caller wishes to decompose.
     *
     * @param bytes byte[] containing the container binary encoded data
     * so that the output object contains entries with actual values from a
     * real binary image.
     *
     * @return XTCETelecommandContentModel representing this XTCETelecommand.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the container completely due to bad references in
     * the XTCE document.
     *
     */

    public XTCETelecommandContentModel processTelecommand( XTCETelecommand telecommand,
                                                           byte[]          bytes )
        throws XTCEDatabaseException {

        BitSet bits = XTCEFunctions.getBitSetFromStreamByteArray( bytes );

        return processTelecommand( telecommand, bits );

    }

    /** Function to decompose an XTCETelecommand object into a simple array of
     * entries that an application can iterate over without the need to
     * resolve XTCE data model references, included additional containers,
     * base containers, and conditional processing.
     *
     * This function is intended to accept a Java InputStream containing the
     * bytes to use for the binary portion of the container.
     *
     * @param telecommand XTCETelecommand object containing the telecommand
     * that the caller wishes to decompose.
     *
     * @param stream InputStream containing the container binary encoded data
     * so that the output object contains entries with actual values from a
     * real binary image.
     *
     * @return XTCETelecommandContentModel representing this XTCETelecommand.
     *
     * @throws XTCEDatabaseException thrown in the event that it is not
     * possible to decompose the container completely due to bad references in
     * the XTCE document, or if the stream throws an IOException.
     *
     */

    public XTCETelecommandContentModel processTelecommand( XTCETelecommand telecommand,
                                                           InputStream     stream )
        throws XTCEDatabaseException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int byteValue;

        try {
            while ( ( byteValue = stream.read() ) != -1 ) {
                buffer.write( byteValue );
            }
        } catch ( Exception ex ) {
            throw new XTCEDatabaseException( ex.getLocalizedMessage() );
        }

        return processTelecommand( telecommand, buffer.toByteArray() );

    }

    /** Retrieve the containers in the XTCE document that directly reference
     * an entry in their manifest that includes the provided Parameter.
     *
     * @param parameter XTCEParameter object to find in the containers defined
     * in this XTCE database document.
     *
     * @return List of XTCETMContainer objects found, or an empty list if
     * the no container references the parameter.
     *
     */

    public List<XTCETMContainer> findContainers( XTCEParameter parameter ) {

        List<XTCETMContainer>      allContainers = getContainers();
        ArrayList<XTCETMContainer> containers    = new ArrayList<>();

        for ( XTCETMContainer container : allContainers ) {
            if ( container.contains( parameter ) == true ) {
                containers.add( container );
            }
        }

        return containers;

    }

    /** Retrieve the type reference from the JAXB generated objects for a
     * particular TM Parameter fully qualified type object path in the XTCE
     * data model.
     *
     * @param typePath String containing the UNIX style fully qualified path
     * to the TM Parameter type object.
     *
     * @return NameDescriptionType base class for the type object found, or a
     * null object reference in the event that it is not found.
     *
     */

    public NameDescriptionType getParameterTypeReference( String typePath ) {
        
        if ( parameterTypes.containsKey( typePath ) == true ) {
            return parameterTypes.get( typePath );
        } else {
            return null;
        }

    }

    /** Retrieve the type reference from the JAXB generated objects for a
     * particular TC Argument fully qualified type object path in the XTCE
     * data model.
     *
     * @param typePath String containing the UNIX style fully qualified path
     * to the TC Argument type object.
     *
     * @return NameDescriptionType base class for the type object found, or a
     * null object reference in the event that it is not found.
     *
     */

    public NameDescriptionType getArgumentTypeReference( String typePath ) {

        if ( argumentTypes.containsKey( typePath ) == true ) {
            return argumentTypes.get( typePath );
        } else {
            return null;
        }

    }

    /** Private function to recursively locate SpaceSystem elements from the
     * XTCE data model.
     *
     * @param currentSpaceSystem XTCESpaceSystem object to start the recursion
     * from.
     *
     * @param spaceSystemList List of XTCESpaceSystem objects that will
     * be populated for the caller.
     *
     */

    private void recurseSpaceSystems( XTCESpaceSystem       currentSpaceSystem,
                                      List<XTCESpaceSystem> spaceSystemList ) {
        
        List<SpaceSystemType> ssTree =
            currentSpaceSystem.getReference().getSpaceSystem();

        for ( int iii = 0; iii < ssTree.size(); ++iii ) {
            if ( ssTree.get( iii ) == null ) {
               //System.out.println( "not sure why null" );
            } else {
               String newCurrent =
                    currentSpaceSystem.getFullPath() + "/" + ssTree.get( iii ).getName(); // NOI18N
               XTCESpaceSystem childSpaceSystem =
                    new XTCESpaceSystem( newCurrent, ssTree.get( iii ), this );
               spaceSystemList.add( childSpaceSystem );
               recurseSpaceSystems( childSpaceSystem, spaceSystemList );
            }
        }
    }

    /** Private method to capture the Parameter Type paths with mapping to a
     * reference to the Argument Type itself, as generically represented by a
     * NameDescriptionType in the inheritance tree.
     *
     * The mapping is kept in a HashMap, which a private data member of this
     * object.
     *
     */

    private void cacheParameterTypes() {

        parameterTypes = new HashMap<>();

        List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {

            try {
                List<NameDescriptionType> tmtypes =
                    spaceSystems.get( iii ).
                                 getReference().
                                 getTelemetryMetaData().
                                 getParameterTypeSet().
                                 getStringParameterTypeOrEnumeratedParameterTypeOrIntegerParameterType();
                for ( int jjj = 0; jjj < tmtypes.size(); ++jjj ) {
                    parameterTypes.put( spaceSystems.get( iii ).getFullPath() +
                                        "/" + // NOI18N
                                        tmtypes.get( jjj ).getName(),
                                        tmtypes.get( jjj ) );
                }
            } catch ( NullPointerException ex ) {
                // this is okay, skip this SpaceSystem since it does not have types
            }

            try {
                List<NameDescriptionType> tctypes =
                    spaceSystems.get( iii ).
                                 getReference().
                                 getCommandMetaData().
                                 getParameterTypeSet().
                                 getStringParameterTypeOrEnumeratedParameterTypeOrIntegerParameterType();
                for ( int jjj = 0; jjj < tctypes.size(); ++jjj ) {
                    parameterTypes.put( spaceSystems.get( iii ).getFullPath() +
                                        "/" + // NOI18N
                                        tctypes.get( jjj ).getName(),
                                        tctypes.get( jjj ) );
                }
            } catch ( NullPointerException ex ) {
                // this is okay, skip this SpaceSystem since it does not have types
            }

        }

    }

    /** Private method to capture the Argument Type paths with mapping to a
     * reference to the Argument Type itself, as generically represented by a
     * NameDescriptionType in the inheritance tree.
     *
     * The mapping is kept in a HashMap, which a private data member of this
     * object.
     *
     */

    private void cacheArgumentTypes() {

        argumentTypes = new HashMap<>();

        List<XTCESpaceSystem> spaceSystems = getSpaceSystemTree();

        for ( int iii = 0; iii < spaceSystems.size(); ++iii ) {

            try {
               List<NameDescriptionType> argtypes =
                    spaceSystems.get( iii ).
                                 getReference().
                                 getCommandMetaData().
                                 getArgumentTypeSet().
                                 getStringArgumentTypeOrEnumeratedArgumentTypeOrIntegerArgumentType();
               for ( int jjj = 0; jjj < argtypes.size(); ++jjj ) {
                  argumentTypes.put( spaceSystems.get( iii ).getFullPath() + "/" + argtypes.get( jjj ).getName(), argtypes.get( jjj ) ); // NOI18N
               }
            } catch ( NullPointerException ex ) {
                // this is okay, skip this SpaceSystem since it does not have types
            }

        }

    }

    // Private Data Members

    private SpaceSystemType                  topLevelSpaceSystem = null;
    private Map<String, NameDescriptionType> parameterTypes      = null;
    private Map<String, NameDescriptionType> argumentTypes       = null;
    private List<XTCESpaceSystem>            spaceSystemCache    = null;

}
