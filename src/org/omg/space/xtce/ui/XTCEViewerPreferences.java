/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omg.space.xtce.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.omg.space.xtce.toolkit.XTCEFunctions;

/** This singleton class provides a convenient accessor for user preferences
 * related to using the XTCEViewer application.
 *
 * The preferences are stored using the Java Preferences API, so the manner and
 * location of storage is dependent on the platform being used and is not
 * managed within the application directly.
 *
 * The design pattern for this singleton resembles the recommendation by
 * "Bill Pugh", of the Java community.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerPreferences {

    /** Constructor
     *
     * Private so that it is not usable outside of this class.
     *
     */

    private XTCEViewerPreferences() {
        prefs = Preferences.userNodeForPackage( XTCEViewer.class );
    }

    /** This private inner class "holds" the constructed reference to the
     * singleton of XTCEViewerPreferences.
     *
     */

    private static class LazyHolder {

        /// Static data member to contain the reference to the instance of
        /// XTCEViewerPreferences.

        private static final XTCEViewerPreferences instance = new XTCEViewerPreferences();
    }

    /** The instance accessor to retrieve the singleton reference to the
     * preferences object.
     *
     * @return XTCEViewerPreferences object, which can never be null.
     *
     */

    public static XTCEViewerPreferences getInstance() {
        return LazyHolder.instance;
    }

    /** Retrieves the user preference for exercising XSD validation on any
     * XTCE XML document at time of load.
     *
     * Having this option be false improves load performance but risks a
     * de-stablization of the application in the event that the XML document is
     * not valid against the XTCE data model.
     *
     * @return boolean indicating if schema validation will be enabled on load.
     *
     */

    public boolean getValidateOnLoadOption() {
        return prefs.getBoolean( "ValidateOnLoad", true );
    }

    /** Sets the user preference for exercising the XSD validation on any
     * XTCE XML document at time of load.
     *
     * Having this option be false improves load performance but risks a
     * de-stablization of the application in the event that the XML document is
     * not valid against the XTCE data model.
     * 
     * @param flag Boolean indicating if the XSD validation should be enabled
     * on the next document load and for any other document load events until
     * otherwise changed again by the user.
     *
     */

    public void setValidateOnLoadOption( boolean flag ) {
        prefs.putBoolean( "ValidateOnLoad", flag );
        save();
    }

    /** Retrieve the user preference for showing the alias namespace on the
     * XTCEViewer displays in the form of NS::ALIAS, which is generally
     * recommended if more than one namespace is to be shown.
     *
     * @return boolean indicating if the user interface should show the Alias
     * namespaces in the form NS::ALIAS or just the ALIAS name.
     *
     */

    public boolean getShowAliasNamespacesOption() {
        return prefs.getBoolean( "ShowAliasNamespaces", true );
    }

    /** Sets the user preference for showing the alias namespace on the
     * XTCEViewer displays in the form of NS::ALIAS, which is generally
     * recommended if more than one namespace is to be shown.
     *
     * @param flag Boolean indicating if the user interface should show the
     * alias namespaces in the form NS::ALIAS or just the ALIAS name.
     *
     */

    public void setShowAliasNamespacesOption( boolean flag ) {
        prefs.putBoolean( "ShowAliasNamespaces", flag );
        save();
    }

    /** Retrieve the user preference for showing all available Aliases on the
     * XTCEViewer displays.
     *
     * This preference is independent of the getShowAliasNamespacesOption().
     * That options controls whether or not the namespaces for each alias are
     * displayed.  It is generally recommended that the other option be true
     * when this option is also true, but it is not required.  The drawback is
     * that the user will not be able to differentiate between the aliases.
     *
     * When a preferred namespace is set with setPreferredAliasNamespace(),
     * then this flag will automatically become false.  When setting this flag
     * true, the setPreferredAliasNamespace() will automatically be set to
     * an empty string, meaning no preferred namespace.
     *
     * @return boolean indicating if all aliases of all namespaces will be
     * displayed on the user interface.
     *
     */

    public boolean getShowAllAliasNamespacesOption() {
        return prefs.getBoolean( "ShowAllAliasNamespaces", true );
    }

    /** Sets the user preference for showing all available Aliases on the
     * XTCEViewer displays.
     *
     * This preference is independent of the setShowAliasNamespacesOption().
     * That options controls whether or not the namespaces for each alias are
     * displayed.  It is generally recommended that the other option be true
     * when this option is also true, but it is not required.  The drawback is
     * that the user will not be able to differentiate between the aliases.
     *
     * When a preferred namespace is set with setPreferredAliasNamespace(),
     * then this flag will automatically become false.  When setting this flag
     * true, the setPreferredAliasNamespace() will automatically be set to
     * an empty string, meaning no preferred namespace.
     *
     * @param flag Boolean indicating if all aliases of all namespaces will be
     * displayed on the user interface.
     *
     */

    public void setShowAllAliasNamespacesOption( boolean flag ) {
        prefs.putBoolean( "ShowAllAliasNamespaces", flag );
        if ( flag == true ) {
            prefs.put( "PreferredAliasNamespace", "" );
        }
        save();
    }

    /** Retrieve the user preference for using the XInclude XML feature when
     * loading a database document.
     *
     * @return boolean indicating if new documents loaded will have XInclude
     * applied when it exists in the document.
     *
     */

    public boolean getUseXIncludeOption() {
        return prefs.getBoolean( "UseXInclude", true );
    }

    /** Sets the user preference for using the XInclude XML feature when
     * loading a database document.
     *
     * @param flag boolean indicating if future documents should have the XML
     * XInclude feature observed.
     *
     */

    public void setUseXIncludeOption( boolean flag ) {
        prefs.putBoolean( "UseXInclude", flag );
        save();
    }

    /** Retrieve the user preference for which namespace to show Aliases for on
     * XTCEViewer displays.
     *
     * This option is coupled with the getShowAllAliasNamespacesOption()
     * method.  A non-empty string return here will imply that the other option
     * will return false.  If this option returns an empty string, then the
     * other option will return true.
     *
     * @return String containing the preferred namespace to show the user on
     * the user interface.
     *
     */

    public String getPreferredAliasNamespaceOption() {
        return prefs.get( "PreferredAliasNamespace", "" );
    }

    /** Sets the user preference for which namespace to show Aliases for on
     * XTCEViewer displays.
     *
     * This option is coupled with the setShowAllAliasNamespacesOption()
     * method.  A non-empty string setting here will set that the other option
     * will return false.
     *
     * @param ns String containing the preferred namespace to show the user on
     * the user interface.
     *
     */

    public void setPreferredAliasNamespaceOption( String ns ) {
        prefs.put( "PreferredAliasNamespace", ns );
        if ( ns.isEmpty() == true ) {
            prefs.putBoolean( "ShowAllAliasNamespaces", true );
        }
        save();
    }

    /** Retrieve the user preference for the number of files to store in the
     * recently loaded list for quicker access.
     *
     * @return Integer count of the number of files to keep on the list.  The
     * oldest files are dropped first when the count reaches the maximum size.
     *
     */

    public int getRecentFilesMaxCountOption() {
        return prefs.getInt( "RecentFilesMaxCount", 10 );
    }

    /** Sets the user preference for the number of files to store in the
     * recently loaded list for quicker access.
     *
     * @param maxNum Integer value containing the maximum number of files to
     * store on the recent files loaded list, with the oldest ones being
     * dropped when the count is exceeded.
     *
     */

    public void setRecentFilesMaxCountOption( int maxNum ) {
        prefs.putInt( "RecentFilesMaxCount", maxNum );
        save();
    }

    /** Retrieve the user preference for showing all available conditions and
     * their children on containers within the XTCEViewer displays.
     *
     * @return boolean indicating if all aliases of all conditions will be
     * displayed on the user interface.
     *
     */

    public boolean getShowAllContainerConditionalsOption() {
        return prefs.getBoolean( "ShowAllContainerConditionals", true );
    }

    /** Sets the user preference for showing all available conditions and
     * their children on containers within the XTCEViewer displays.
     *
     * @param flag Boolean indicating if all conditionals for containers will
     * be displayed on the user interface.
     *
     */

    public void setShowAllContainerConditionalsOption( boolean flag ) {
        prefs.putBoolean( "ShowAllContainerConditionals", flag );
        save();
    }

    /** Retrieve the user preference for drawing containers and telecommands
     * as to their orientation on the drawing.
     *
     * @return String containing "LEFT_TO_RIGHT" or "TOP_TO_BOTTOM".
     *
     */

    public String getContainerOrientationOption() {
        return prefs.get( "ContainerDrawingOrientation", "LEFT_TO_RIGHT" );
    }

    /** Sets the user preference for drawing containers and telecommands
     * as to their orientation on the drawing.
     *
     * @param orientDrawingAs String containing either "LEFT_TO_RIGHT" or
     * "TOP_TO_BOTTOM".  Anything else will be no change.
     *
     */

    public void setContainerOrientationOption( String orientDrawingAs ) {
        if ( ( orientDrawingAs.equals( "LEFT_TO_RIGHT" ) == true ) ||
             ( orientDrawingAs.equals( "TOP_TO_BOTTOM" ) == true ) ) {
            prefs.put( "ContainerDrawingOrientation", orientDrawingAs );
            save();
        }
    }

    /** Method to add the most recently loaded file to the recent items menu
     * list, store it in the preferences, and re-sort the menu items.
     *
     * @param recentItemsMenu The JMenu object to update the list with the
     * latest loaded file.
     *
     * @param dbFile The File object of the just loaded XTCE database or null
     * if the list need not be updated, rather just the menu (such as startup).
     *
     */

    public void updateRecentFilesList( JMenu recentItemsMenu, File dbFile ) {

        ArrayList<String> files = getObject( "RecentFilesList" );
        if ( dbFile != null ) {
            if ( files.contains( dbFile.getAbsolutePath() ) == true ) {
                files.remove( dbFile.getAbsolutePath() );
            }
            files.add( dbFile.getAbsolutePath() );
        }

        int maxItems = getRecentFilesMaxCountOption();
        while ( files.size() > maxItems ) {
            files.remove( 0 );
        }

        recentItemsMenu.removeAll();
        for ( int iii = ( files.size() - 1 ); iii >= 0; --iii ) {
            JMenuItem item = new JMenuItem( files.get( iii ) );
            final File actionFile = new File ( files.get( iii ) );
            item.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed( ActionEvent evt ) {
                    viewer.openFile( actionFile, getUseXIncludeOption() );
                }
            });
            recentItemsMenu.add( item );
        }

        putObject( "RecentFilesList", files );
        save();

    }

    /** Method to initialize/update the example databases list from the
     * internal database package directory included with the toolkit.
     *
     * Note that these files are loaded using the system resource classpath,
     * which may be inside the distributed jar file and not directly visible
     * on the filesystem.  Also, this method presently only selects the files
     * in the org.omg.space.xtce.database package/folder with names of "*.xml".
     *
     * @param exampleItemsMenu JMenu item on the File Menu that contains the
     * list of example databases.
     *
     */

    public void updateExampleFilesList( JMenu exampleItemsMenu ) {


        ArrayList<File> files = new ArrayList<>();

        URL dbFilesDirectoryUrl =
            ClassLoader.getSystemResource( "org/omg/space/xtce/database" );
        if ( dbFilesDirectoryUrl != null ) {
            try {
                File dir = new File( dbFilesDirectoryUrl.toURI() );
                for ( File nextFile : dir.listFiles() ) {
                    if ( ( nextFile.isFile()                     == true ) &&
                         ( nextFile.getName().endsWith( ".xml" ) == true ) ) {
                        files.add( nextFile );
                    }
                }
            } catch ( URISyntaxException ex ) {
                // skip it, do nothing for now
            } catch ( IllegalArgumentException ex ) {
                // skip it, do nothing for now
            }
        }

        exampleItemsMenu.removeAll();
        for ( int iii = ( files.size() - 1 ); iii >= 0; --iii ) {
            JMenuItem item = new JMenuItem( files.get( iii ).getName() );
            final File actionFile = files.get( iii );
            item.addActionListener( new ActionListener() {
                @Override
                public void actionPerformed( ActionEvent evt ) {
                    viewer.openFile( actionFile, true );
                }
            });
            exampleItemsMenu.add( item );
        }

    }

    /** Method to erase the recent files list.
     *
     * @param recentItemsMenu The JMenu object to clear the items from.
     *
     */

    public void clearRecentFilesList( JMenu recentItemsMenu ) {
        putObject( "RecentFilesList", null );
        recentItemsMenu.removeAll();
        save();
    }

    /** Retrieve the list of stored XPath Query items to display in the combo
     * box for user convenience.
     *
     * @return ArrayList of Strings in order from most recent to least recent
     * with a limit of up to 25 previous searches.
     *
     */

    public ArrayList<String> getSavedXPathQueries() {
        return getObject( "XPathQueries" );
    }

    /** Adds another XPath Query item to the saved cache with a limit of 25
     * non-duplicate items, with the most recent being the first on the list.
     *
     * @param searchItem String containing the user text string to add.
     *
     */

    public void addSavedXPathQuerySearch( String searchItem ) {
        addFindSearch( searchItem,
                       "XPathQueries",
                       getSavedXPathQueries() );
    }

    /** Removes an XPath Query item from the saved cache.
     *
     * @param searchItem String containing the user text string to remove.
     *
     */

    public void removeSavedXPathQuerySearch( String searchItem ) {
        removeFindSearch( searchItem,
                          "XPathQueries",
                          getSavedXPathQueries() );
    }

    /** Retrieve the list of recent Find Parameter Search Text items to display
     * in the combo box for user convenience.
     *
     * @return ArrayList of Strings in order from most recent to least recent
     * with a limit of up to 25 previous searches.
     *
     */

    public ArrayList<String> getRecentFindParameterSearches() {
        return getObject( "FindParameterSearches" );
    }

    /** Adds another Find Parameter Search Text item to the history with a
     * limit of 25 non-duplicate items, with the most recent being the first
     * on the list.
     *
     * @param searchItem String containing the user text string to add.
     *
     */

    public void addFindParameterSearch( String searchItem ) {
        addFindSearch( searchItem,
                       "FindParameterSearches",
                       getRecentFindParameterSearches() );
    }

    /** Retrieve the list of recent Find Container Search Text items to display
     * in the combo box for user convenience.
     *
     * @return ArrayList of Strings in order from most recent to least recent
     * with a limit of up to 25 previous searches.
     *
     */

    public ArrayList<String> getRecentFindContainerSearches() {
        return getObject( "FindContainerSearches" );
    }

    /** Adds another Find Container Search Text item to the history with a
     * limit of 25 non-duplicate items, with the most recent being the first
     * on the list.
     *
     * @param searchItem String containing the user text string to add.
     *
     */

    public void addFindContainerSearch( String searchItem ) {
        addFindSearch( searchItem,
                       "FindContainerSearches",
                       getRecentFindContainerSearches() );
    }

    /** Retrieve the list of recent Find Telecommand Search Text items to
     * display in the combo box for user convenience.
     *
     * @return ArrayList of Strings in order from most recent to least recent
     * with a limit of up to 25 previous searches.
     *
     */

    public ArrayList<String> getRecentFindTelecommandSearches() {
        return getObject( "FindTelecommandSearches" );
    }

    /** Adds another Find Telecommand Search Text item to the history with a
     * limit of 25 non-duplicate items, with the most recent being the first
     * on the list.
     *
     * @param searchItem String containing the user text string to add.
     *
     */

    public void addFindTelecommandSearch( String searchItem ) {
        addFindSearch( searchItem,
                       "FindTelecommandSearches",
                       getRecentFindTelecommandSearches() );
    }

    /** Retrieve the last working directory that was used for a load or save
     * operation, considering that this is not retained in the Preferences.
     *
     * @return String containing the last current working directory or an
     * empty string if none has yet been used.
     *
     */

    public String getCurrentWorkingDirectory() {
        if ( currentWorkingDirectory == null ) {
            return "";
        }
        return currentWorkingDirectory;
    }

    /** Sets the last working directory that was used for a load or save
     * operation, considering that this is not retained in the Preferences.
     *
     * @param dirName String containing the last working directory that was
     * used for a load or save operation.
     *
     */

    public void setCurrentWorkingDirectory( String dirName ) {
        currentWorkingDirectory = dirName;
    }

    /** Retrieve the user last selected language and country preference or the
     * default of English/US if it has not been explicitly set.
     *
     * @return Locale containing the I18N and L10N preferences.
     *
     */

    public Locale getLanguagePreference() {
        String language = prefs.get( "Language", "en" );
        String country  = prefs.get( "Country",  "US" );
        return new Locale( language, country );
    }

    /** Sets the user preferred language and country preference.
     *
     * @param locale containing the language and country preference.
     *
     */

    public void setLanguagePreference( Locale locale ) {
        prefs.put( "Language", locale.getLanguage() );
        prefs.put( "Country",  locale.getCountry() );
        save();
    }

    /** This permits the XTCEViewer application to set a reference to itself
     * for displaying the warning dialog in the event that the preferences
     * cannot be saved.
     *
     * If this is not set, then the warning will be displayed to STDOUT.
     *
     * @param viewerTool The XTCEViewer application that instantiated this
     * preferences object.
     *
     */

    public void setParentWindow( XTCEViewer viewerTool ) {
        viewer = viewerTool;
    }

    /** Method to save the preferences to the persistent store defined within
     * the Java Preferenes API class.
     *
     * A means is provided to warn the user in the event that there is an error
     * saving the preferences information, although it is treated only as a
     * warning in this case.
     *
     */

    private void save() {

        try {
            prefs.flush();
        } catch ( BackingStoreException ex ) {
            if ( ( viewer != null ) && ( preferenceSaveWarnings == true ) ) {
                String message =
                    XTCEFunctions.getText( "preference_save_error_message" ) +
                    "\n\n" +
                    ex.getLocalizedMessage();
                String[] options = { XTCEFunctions.getText( "general_dismiss_text" ),
                                     XTCEFunctions.getText( "general_no_more_warning" ) };
                int nnn = JOptionPane.showOptionDialog( viewer,
                                                        message,
                                                        XTCEFunctions.getText( "general_warning" ),
                                                        JOptionPane.YES_NO_OPTION,
                                                        JOptionPane.WARNING_MESSAGE,
                                                        null,
                                                        options,
                                                        options[1] );
                if ( nnn == 1 ) {
                    preferenceSaveWarnings = false;
                }
            } else if ( preferenceSaveWarnings == true ) {
                System.out.println( XTCEFunctions.generalWarningPrefix() +
                    XTCEFunctions.getText( "preference_save_log_message" ) +
                    " " +
                    ex.getLocalizedMessage() );
                preferenceSaveWarnings = false;
            }
        }

    }

    /** Method to handle adding a new search text item to the list of saved
     * search text items.
     *
     * @param searchItem String containing the user search text to add.
     *
     * @param keyName String containing the key in the preferences store that
     * contains the search list.
     *
     * @param recentSearches ArrayList of Strings containing the recent
     * searches retrieved for the keyName list.
     *
     */

    private void addFindSearch( String            searchItem,
                                String            keyName,
                                ArrayList<String> recentSearches ) {

        ArrayList<String> searches = new ArrayList<String>();
        searches.add( searchItem );
        searches.addAll( recentSearches );

        for ( int iii = searches.size() - 1; iii >= 1; --iii ) {
            if ( ( searches.get( iii )                      == null ) ||
                 ( searches.get( iii ).equals( searchItem ) == true ) ||
                 ( searches.get( iii ).isEmpty()            == true ) ) {
                searches.remove( iii );
            }
        }

        while ( searches.size() > 25 ) {
            searches.remove( searches.size() - 1 );
        }

        putObject( keyName, searches );
        save();

    }

    /** Method to handle removing a search text item to the list of saved
     * search text items.
     *
     * @param searchItem String containing the user search text to add.
     *
     * @param keyName String containing the key in the preferences store that
     * contains the search list.
     *
     * @param recentSearches ArrayList of Strings containing the recent
     * searches retrieved for the keyName list.
     *
     */

    private void removeFindSearch( String            searchItem,
                                   String            keyName,
                                   ArrayList<String> recentSearches ) {

        for ( int iii = recentSearches.size() - 1; iii >= 0; --iii ) {
            if ( recentSearches.get( iii ).equals( searchItem ) == true ) {
                recentSearches.remove( iii );
            }
        }

        putObject( keyName, recentSearches );
        save();

    }

    /** Method to serialize an Object that implements Java Serializable
     * interface to a byte array.
     *
     * @param obj The object to serialize into bytes.
     *
     * @return byte array
     *
     * @throws IOException 
     *
     */

    private static byte[] objectToBytes( ArrayList<String> obj ) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream    oos  = new ObjectOutputStream( baos );

        oos.writeObject( obj );

        return baos.toByteArray();

    }

    /** Method to un-serialize a byte array back into an Object that implements
     * Java Serializable interface.
     *
     * @param raw Byte array that contains the data for the Object contents.
     *
     * @return Object reconstructed from the byte array.
     *
     * @throws IOException
     * @throws ClassNotFoundException 
     *
     */

    private static ArrayList<String> bytesToObject( byte raw[] ) throws IOException, ClassNotFoundException {

        ByteArrayInputStream bais = new ByteArrayInputStream( raw );
        ObjectInputStream    ois  = new ObjectInputStream( bais );

        ArrayList<String> obj = (ArrayList<String>)ois.readObject();

        return obj;

    }

    /** Method to chunk the raw serialized binary from an Object into pieces
     * that are compatible with the Java Preferences API.
     *
     * @param raw Object serialized binary bytes.
     *
     * @return Array of byte arrays that are limited individually in length to
     * half the Java Preferences.MAX_VALUE_LENGTH.
     *
     */

    private static byte[][] breakIntoPieces( byte raw[] ) {

        int pieceLength = Preferences.MAX_VALUE_LENGTH / 2;
        int numPieces   = ( raw.length + pieceLength - 1 ) / pieceLength;
        byte pieces[][] = new byte[numPieces][];
        for ( int iii = 0; iii < numPieces; ++iii ) {
            int startByte = iii * pieceLength;
            int endByte   = startByte + pieceLength;
            if ( endByte > raw.length ) {
                endByte = raw.length;
            }
            int length = endByte - startByte;
            pieces[iii] = new byte[length];
            System.arraycopy( raw, startByte, pieces[iii], 0, length );
        }
        return pieces;

    }

    /** Method to consolidate the fractional byte arrays that are read from the
     * Java Preferences API back into a single byte array for serialization
     * back into an Object.
     *
     * @param pieces Byte Array of byte arrays that are limited individually in
     * length to half the Java Preferences.MAX_VALUE_LENGTH.
     *
     * @return Object serialized binary bytes.
     *
     */

    private static byte[] combinePieces( byte pieces[][] ) {

        int length = 0;
        for ( int iii = 0; iii < pieces.length; ++iii ) {
            length += pieces[iii].length;
        }
        byte raw[] = new byte[length];
        int cursor = 0;
        for ( int iii = 0; iii < pieces.length; ++iii ) {
            System.arraycopy( pieces[iii], 0, raw, cursor, pieces[iii].length );
            cursor += pieces[iii].length;
        }
        return raw;

    }

    /** Get an object, which in this case is an ArrayList of Strings that is
     * any size from the Java Preferences API.
     *
     * @param key String containing the name of the child node to use to store
     * the segments that make up the serialized object.
     *
     * @return ArrayList of Strings, which can be 0 size, but will never be
     * null.
     *
     */

    private ArrayList<String> getObject( String key ) {

        try {

            Preferences child  = prefs.node( key );
            String[]    keys   = child.keys();
            ArrayList<String> keyList = new ArrayList<String>();
            for ( String value : keys ) {
                keyList.add( value );
            }
            int iii = 0;
            while ( keyList.contains( new Integer( iii ).toString() ) == true ) {
                ++iii;
            }
            if ( iii == 0 ) {
                return new ArrayList<String>();
            }

            byte pieces[][] = new byte[iii][];

            iii = 0;
            while ( keyList.contains( ""+iii ) == true ) {
                pieces[iii] = child.getByteArray( ""+iii, null );
                ++iii;
            }
            byte              raw[] = combinePieces( pieces );
            ArrayList<String> obj   = bytesToObject( raw );

            return obj;

        } catch ( IOException ex ) {
            System.out.println( "IOException: " + ex.getLocalizedMessage() );
        } catch ( BackingStoreException ex ) {
            System.out.println( "BackingStoreException: " + ex.getLocalizedMessage() );
        } catch ( ClassNotFoundException ex ) {
            System.out.println( "ClassNotFoundException: " + ex.getLocalizedMessage() );
        }

        return new ArrayList<String>();

    }

    /** Put an object, which in this case is an ArrayList of Strings that is
     * any size to the Java Preferences API.
     *
     * @param key String containing the name of the child node to use to get
     * the segments that make up the serialized object.
     *
     * @param list An ArrayList of Strings to write to the Java Preferences
     * API as serialized bytes in a child node defined by the "key" parameter.
     *
     */

    private void putObject( String key, ArrayList<String> list ) {

        try {

            Preferences child = prefs.node( key );
            child.clear();

            if ( ( list == null ) || ( list.size() == 0 ) ) {
                return;
            }

            byte raw[]      = objectToBytes( list );
            byte pieces[][] = breakIntoPieces( raw );

            for ( int iii = 0; iii < pieces.length; ++iii ) {
                child.putByteArray( ""+iii, pieces[iii] );
            }

        } catch ( IOException ex ) {
            System.out.println( "IOException: " + ex.getLocalizedMessage() );
        } catch ( BackingStoreException ex ) {
            System.out.println( "BackingStoreException: " + ex.getLocalizedMessage() );
        }

    }

    /// Private copy of the XTCEView object so that the warning message can be
    /// sent back to the messages window in the event that writes fail.

    private XTCEViewer viewer = null;

    /// Private copy of the Java Preferences API singleton.

    private Preferences prefs = null;

    /// The last used directory for a load or save operation, used to have the
    /// dialogs jump to the last directory.  This is not saved in the Java
    /// Preferences API.

    private String currentWorkingDirectory = null;

    /// Flag to indicate if we should keep showing preference save warnings.

    private boolean preferenceSaveWarnings = true;

}
