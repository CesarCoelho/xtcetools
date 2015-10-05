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
import org.omg.space.xtce.database.AliasSetType;
import org.omg.space.xtce.database.DescriptionType;
import org.omg.space.xtce.database.DescriptionType.AncillaryDataSet;
import org.omg.space.xtce.database.DescriptionType.AncillaryDataSet.AncillaryData;

/** The XTCENamedObject class serves to capture the common elements to XTCE
 * Parameters, Arguments, Containers, and Streams.
 *
 * @author David Overeem
 *
 */

public abstract class XTCENamedObject implements Comparable {

    /** Constructor
     * 
     * Constructs a new instance of a named object from the XTCE data model.
     * These include Parameters, Arguments, Container, Telecommands, and
     * Streams.
     *
     * A casual reader might wonder why the obvious thing was not done here in
     * that the arguments passed include AliasSet, AncillaryDataSet, etc when
     * it would be much cleaner to pass NameDescriptionType.  There is one
     * element in XTCE that needs to behave in this model but does not inherit
     * from NameDescriptionType and that is the Member element on Aggregate
     * Parameters and Arguments.  A goal of this abstraction is to make Member
     * elements look to the end user application like bonafide Parameters or
     * Arguments.  Hence the reason this was implemented the way it was.
     *
     * @param name String containing the object name as referenced from the
     * getName() method on the JAXB generated object.
     *
     * @param path String containing the Space System path leading to this
     * object in the XTCE data model, but without the object name.
     *
     * @param aliasSet AliasSetType object from the JAXB generated classes for
     * the named object, not the typed object.
     *
     * @param ancDataSet AncillaryDataSet object from the JAXB generated
     * classes for the named object, not the typed object.
     *
     */

    XTCENamedObject( String           name,
                     String           path,
                     AliasSetType     aliasSet,
                     AncillaryDataSet ancDataSet ) {
        name_      = name;
        path_      = path;
        aliasList_ = new ArrayList<>();

        // Member Elements do not have Aliases
        if ( aliasSet != null ) {
            populateAliasListFromReference( aliasSet );
        }

        // Member Elements do not have Ancillary Data
        if ( ancDataSet != null ) {
            populateAncillaryListFromReference( ancDataSet );
        }

    }

    /** Retrieve the name of this object in the XTCE data model
     *
     * @return String containing the name attribute.
     *
     */

    public String getName() {
        return name_;
    }

    /** Retrieve the fully qualified Space System path to this object in the
     * XTCE data model.
     *
     * @return String containing the fully qualified path to this Parameter,
     * Argument, Container, or Stream in the XTCE data model.
     *
     */

    public String getFullPath() {
        return path_ + "/" + getName();
    }

    /** Retrieve the Space System path that contains this object in the XTCE
     * data model.
     *
     * @return String containing Space System path, without the name of this
     * object, for the Space System that contains this object.
     *
     */

    public String getSpaceSystemPath() {
        return path_;
    }

    /** Retrieve the Space System name that contains this object in the XTCE
     * data model.
     *
     * @return String containing just the name of the Space System that
     * contains this named object without the path hierarchy that is otherwise
     * returned from getSpaceSystemPath().
     *
     */

    public String getSpaceSystemName() {
        int idx = path_.lastIndexOf( '/' );
        return path_.substring( idx + 1 );
    }

    /** Accessor to retrieve the complete list of applicable Aliases for this
     * named object.
     *
     * The list is provided in a form where each entry in the List return
     * contains a string that is in the form NS::ALIAS, which NS is the
     * nameSpace attribute of the discovered Alias and the ALIAS is the alias
     * attribute of the Alias element.
     *
     * Aliases are evaluated first to consider those on the primary element,
     * such as a Parameter, Container, Argument, or Stream.  They cannot appear
     * on primary Member elements.  Aliases are then checked on the Type
     * element that is associated to this object, when it is a Parameter,
     * Member, or Argument.  Any alias on the type element is secondary or
     * superceded by the primary Alias is it has the same nameSpace attribute
     * as on the higher priority element.  Aliases that appear on the type
     * elements which do not have a corresponding nameSpace attribute match on
     * the primary element are added to the list of valid aliases.
     *
     * @return List of XTCEAlias objects that were gathered.
     *
     */

    public List<XTCEAlias> getAliasSet() {
        return aliasList_;
    }

    /** Accessor to retrieve the Alias for this named object where the
     * desired nameSpace attribute for the Alias is provided to search the list
     * of potential Aliases.
     *
     * This function is convenient because it permits the caller to know in
     * advance the desired Alias nameSpace and retrieve a String object that
     * does not contain the nameSpace prefix with the double colons.  As a
     * result, there is no need to split the resulting string.
     *
     * @see #getAliasSet()
     *
     * @param nameSpace The namespace as defined by the nameSpace attribute in
     * XTCE for the Alias element.
     *
     * @return The alias as contained in the alias attribute of the Alias
     * element located by the caller provided nameSpace attribute of that
     * element.
     *
     */

    public String getAlias( String nameSpace ) {

        for ( XTCEAlias aliasEntry : aliasList_ ) {
            if ( aliasEntry.getNameSpace().equals( nameSpace ) == true ) {
                return aliasEntry.getAliasName();
            }
        }
        return "";

    }

    /** Accessor to retrieve the List of AncillaryData elements from the XTCE
     * data for this named object.
     *
     * AncillaryData is not managed like Aliases are in the object.  While
     * aliases are pre-populated and cached, AncillaryData is only retrieved
     * when actually requested since it is expected to not be all that
     * popular.
     *
     * @return List of AncillaryData elements which can be empty but it will
     * never be null.
     *
     */

    public List<AncillaryData> getAncillaryData( ) {

        if ( ancDataList_ == null ) {
            return new ArrayList<>();
        } else {
            return ancDataList_;
        }

    }

    /** Accessor to retrieve only a partial list of AncillaryData elements from
     * the XTCE data for this named object where the name attribute of the
     * AncillaryData element must either exactly match a string or a glob style
     * pattern.
     *
     * Since the name attribute of an AncillaryData element does not qualify
     * uniqueness, multiple results can still be retrieved when doing an exact
     * match.
     *
     * @param nameGlob String containing a precise name or a glob style pattern
     * to match against the name attribute of the AncillaryData elements.
     *
     * @return List of AncillaryData elements that match.  The list can be
     * empty but it will never be null.
     *
     */

    public List<AncillaryData> getAncillaryData( String nameGlob ) {

        ArrayList<AncillaryData> list = new ArrayList<>();

        if ( ancDataList_ == null ) {
            return list;
        }

        for ( AncillaryData element : ancDataList_ ) {
            if ( XTCEFunctions.matchesUsingGlob( element.getName(), nameGlob) == true ) {
                list.add( element );
            }
        }

        return list;

    }

    /** Retrieves the shortDescription attribute of the primary element for
     * a named object in the XTCE data model.
     *
     * @param obj DescriptionType base class from the JAXB generated classes
     * for this named object.
     *
     * @return String containing the short description or an empty string.  The
     * return value will never be null.
     *
     */

    protected String getPrimaryShortDescription( DescriptionType obj ) {
        if ( obj == null || obj.getShortDescription() == null ) {
            return "";
        }
        return obj.getShortDescription();
    }

    /** Sets the shortDescription attribute of the primary element for a
     * named object in the XTCE data model.
     *
     * @param obj DescriptionType base class from the JAXB generated classes
     * for this named object.
     *
     * @param description String containing the text to set.
     *
     * @throws NullPointerException in the event that the DescriptionType is
     * null.  This method should not be used on an invalid object.
     *
     */

    protected void setPrimaryShortDescription( DescriptionType obj, String description ) {
        if ( description.isEmpty() == false ) {
            obj.setShortDescription( description );
        } else {
            obj.setShortDescription( null );
        }
    }

    /** Retrieves the LongDescription element text of the primary element for
     * a named object in the XTCE data model.
     *
     * @param obj DescriptionType base class from the JAXB generated classes
     * for this named object.
     *
     * @return String containing the long description or an empty string.  The
     * return value will never be null.
     *
     */

    protected String getPrimaryLongDescription( DescriptionType obj ) {
        if ( obj == null || obj.getLongDescription() == null ) {
            return "";
        }
        return obj.getLongDescription();
    }

    /** Sets the shortDescription attribute of the primary element for a
     * named object in the XTCE data model.
     *
     * @param obj DescriptionType base class from the JAXB generated classes
     * for this named object.
     *
     * @param description String containing the text to set.
     *
     * @throws NullPointerException in the event that the DescriptionType is
     * null.  This method should not be used on an invalid object.
     *
     */

    protected void setPrimaryLongDescription( DescriptionType obj, String description ) {
        if ( description.isEmpty() == false ) {
            obj.setLongDescription( description );
        } else {
            obj.setLongDescription( null );
        }
    }

    /** Private method to gather the aliases from this named object, which is
     * performed once for Containers, Streams, and Member objects, but appended
     * to with the type element for Parameters and Arguments.
     *
     * @param set DescriptionType object from the JAXB generated classes
     * that represents this named object.
     *
     */

    protected void populateAliasListFromReference( AliasSetType set ) {

        ArrayList<XTCEAlias> result = new ArrayList<>();

        if ( set != null ) {

            List<AliasSetType.Alias> list = set.getAlias();

            // loop through all the alias elements that were found

            for ( AliasSetType.Alias entry : list ) {

                // gather the alias info and add it to a temporary list
                String name = ( entry.getAlias()     == null ? "" : entry.getAlias() );
                String ns   = ( entry.getNameSpace() == null ? "" : entry.getNameSpace() );
                XTCEAlias aliasObj = new XTCEAlias( name, ns );
                boolean foundInCurrentElement = false;
                for ( XTCEAlias existingObj : result ) {
                    if ( existingObj.getNameSpace().equals( aliasObj.getNameSpace() ) == true ) {
                        foundInCurrentElement = true;
                        // TODO Warning of duplicate namespace in same element - ignoring
                    }
                }

                // add them to the main list if they do not already exist, so
                // this can support adding the lower priority type element for
                // cases of typed objects

                boolean foundInHigherScopeElement = false;
                for ( XTCEAlias existingObj : aliasList_ ) {
                    if ( existingObj.getNameSpace().equals( aliasObj.getNameSpace() ) == true ) {
                        foundInHigherScopeElement = true;
                    }
                }
                if ( ( foundInHigherScopeElement == false ) &&
                     ( foundInCurrentElement     == false ) ) {
                    result.add( aliasObj );
                }

            } // end of loop

        }

        aliasList_.addAll( result );

    }

    protected void populateAncillaryListFromReference( AncillaryDataSet set ) {
        ancDataList_ = set.getAncillaryData();
    }

    /** Comparison Interface
     *
     * Comparison of this named object in the XTCE data model is based on the
     * fully qualified path to this object in the Space System hierarchy.
     *
     * @see Comparable
     *
     * @param that XTCENamedObject or derived class to compare the path.
     *
     * @return int containing either -1, 0, or 1.
     *
     */

    @Override
    public int compareTo( Object that ) {
        return this.getFullPath().compareTo( ((XTCENamedObject)that).getFullPath() );
    }

    /** Equality Operator
     *
     * @param rhs String or XTCENamedObject to compare to this one, with
     * equality being defined as the fully qualified path in the XTCE data
     * model being equal to the path for this object.
     *
     * @return boolean indicating if the two objects are the same in the XTCE
     * data model.
     */

    @Override
    public boolean equals( Object rhs ) {

        if ( rhs instanceof String ) {
            return ( rhs.equals( this.path_ ) );
        } else if ( rhs instanceof XTCENamedObject ) {
            XTCENamedObject that = (XTCENamedObject)rhs;
            return ( this.path_.equals( that.path_ ) );
        } else {
            return false;
        }

    }

    /** Hash function for Java Collections
     *
     * Key rule here is that if this object is equal to another object via the
     * equals() function, then the hashCode() must also be the same.
     *
     * @return int containing the hash code
     *
     */

    @Override
    public int hashCode() {
        return this.path_.hashCode();
    }

    // Private Data Members, which are all references

    private String              name_        = null;
    private String              path_        = null;
    private List<XTCEAlias>     aliasList_   = null;
    private List<AncillaryData> ancDataList_ = null;

}
