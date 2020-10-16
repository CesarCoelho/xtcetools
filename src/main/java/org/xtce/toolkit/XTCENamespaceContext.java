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

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

/** The XTCE Namespace Context class is used to support XPath Queries by
 * mapping the "xtce" prefix to the URI in the XTCEConstants class.
 *
 * @author David Overeem
 *
 */

public class XTCENamespaceContext implements NamespaceContext {

    /** Retrieve the URI associated with the XML node name prefix.
     *
     * @param prefix String containing the prefix.
     *
     * @return String containing the URI.
     *
     */

    @Override
    public String getNamespaceURI( String prefix ) {

        if ( prefix.equals( "xtce" ) == true ) { // NOI18N
            return XTCEConstants.XTCE_NAMESPACE;
        }

        return null;

    }

    /** Retrieve the declared prefixes for this Node context
     *
     * @param val String containing something I don't know what is and is not
     * used in this application.
     *
     * @return Iterator over the list of known prefixes.
     *
     */

    @Override
    public Iterator<String> getPrefixes( String val ) {

        throw new IllegalAccessError( XTCEFunctions.getText( "general_not_implemented" ) );

    }

    /** Retrieve the prefix associated with a URI.
     *
     * @param uri String containing the URI declared in the XML.
     *
     * @return String containing the prefix.
     *
     */

    @Override
    public String getPrefix( String uri ) {

        if ( uri.equals( XTCEConstants.XTCE_NAMESPACE ) == true ) {
            return "xtce"; // NOI18N
        }

        throw new IllegalAccessError( XTCEFunctions.getText( "general_not_implemented" ) );

    }

}
