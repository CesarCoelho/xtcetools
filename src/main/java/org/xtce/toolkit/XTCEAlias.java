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

    XTCEAlias( final String alias, final String nameSpace ) {
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
        return nameSpace_ + "::" + alias_; // NOI18N
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

        if ( rhs == null ) {
            return false;
        } else if ( this == rhs ) {
            return true;
        } else if ( rhs instanceof XTCEAlias == false ) {
            return false;
        }

        XTCEAlias that = (XTCEAlias)rhs;

        return ( ( this.getAliasName().equals( that.getAliasName() ) == true ) &&
                 ( this.getNameSpace().equals( that.getNameSpace() ) == true ) );

    }

    /** Hash Code Operator
     *
     * @return int hash code based on the combination of the namespace and the
     * alias names.
     *
     */

    @Override
    public int hashCode() {
        return getFullAliasName().hashCode();
    }

    // Private Data Members

    private final String alias_;
    private final String nameSpace_;

}
