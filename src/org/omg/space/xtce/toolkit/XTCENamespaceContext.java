/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;

/** The XTCE Namespace Context class is used to support XPath Queries by
 * mapping the "xtce" prefix to the URI in the XTCEConstants class.
 *
 * @author b1053583
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

        if ( prefix.equals( "xtce" ) == true ) {
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
    public Iterator getPrefixes( String val ) {

        throw new IllegalAccessError( "Not implemented!" );

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
            return "xtce";
        }

        throw new IllegalAccessError( "Not implemented!" );

    }

}
