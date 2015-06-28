/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

/** This class is a container for capturing the Alias information on an element
 * in the XTCE data model, which is represented as an XTCENamedObject in the
 * toolkit.
 *
 * @author David Overeem
 *
 */

public class XTCEAlias {

    /** Constructor
     *
     * @param alias String containing the alias name from the XTCE document.
     *
     * @param nameSpace String containing the alias namespace from the XTCE
     * document.
     *
     */

    XTCEAlias( String alias, String nameSpace ) {
        alias_     = alias;
        nameSpace_ = nameSpace;
    }

    /** Retrieve the alias name
     *
     * @return String containing the alias name.
     *
     */

    public String getAliasName() {
        return alias_;
    }

    /** Retrieve the alias namespace
     *
     * @return String containing the namespace name for this alias.
     *
     */

    public String getNameSpace() {
        return nameSpace_;
    }

    /** Retrieve the alias name and namespace name in the form of NS::ALIAS
     *
     * @return String containing the alias name and namespace name.
     *
     */

    public String getFullAliasName() {
        return nameSpace_ + "::" + alias_;
    }

    /** Equality Operator
     *
     * Function to compare one XTCEAlias object to another XTCEAlias object.
     * Equality is defined as both the alias string and the namespace name
     * being the same.
     *
     * @param rhs XTCEAlias object to compare to.
     *
     * @return boolean indicating if the two XTCEAlias objects are effectively
     * the same information.
     *
     */

    @Override
    public boolean equals( Object rhs ) {
        if ( this == rhs ) {
            return true;
        }
        if ( rhs.getClass() != XTCEAlias.class ) {
            return false;
        }
        XTCEAlias that = (XTCEAlias)rhs;
        return ( ( this.getAliasName().equals( that.getAliasName() ) == true ) &&
                 ( this.getNameSpace().equals( that.getNameSpace() ) == true ) );
    }

    // Private Data Members

    private String alias_     = null;
    private String nameSpace_ = null;

}
