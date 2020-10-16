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

import javax.xml.bind.MarshalException;
import java.util.List;
import javax.xml.xpath.XPathException;

/** The XTCEDatabaseException class serves to simplify management of exceptions
 * in the toolkit by distinguishing toolkit exceptions from unhandled system
 * exceptions.
 *
 * Toolkit users should generally not need several catch blocks in their client
 * code.  This exception is almost always what will be thrown.  The toolkit
 * attempts to convert other exceptions when it is known what will trigger
 * those exceptions, in order to add more descriptive information than
 * otherwise would be provided by the general system exceptions.
 *
 * @author David Overeem
 *
 */

public class XTCEDatabaseException extends Exception {

    /** Constructor for a String Message
     *
     * @param message String containing the exception message.
     *
     */

    public XTCEDatabaseException( String message ) {
        super( message );
    }

    /** Constructor to initialize an XTCEDatabaseException from a generally
     * uninformative Java Throwable.
     *
     * @param message String message to associate with the exception, where the
     * Throwable did not provide anything helpful, but the toolkit knows the
     * cause.
     *
     * @param ex Throwable to copy from.
     *
     */

    public XTCEDatabaseException( String message, Throwable ex ) {
        super( message, ex );
    }

    /** Constructor to initialize an XTCEDatabaseException from a generally
     * informative Java Exception.
     *
     * @param ex Exception base class that was instantiated with a decent and
     * descriptive error message.
     *
     */

    public XTCEDatabaseException( Exception ex ) {
        super( XTCEDatabaseException.extractMessage( ex ) );
    }

    /** Constructor to initialize an XTCEDatabaseException from a JAXB
     * MarshalException, which needs to be handled via the getCause() instead
     * of the more common getLocalizedMessage(), so this converts the text from
     * the cause to the localized message accessor.
     *
     * @param ex MarshalException thrown by JAXB.
     *
     */

    public XTCEDatabaseException( MarshalException ex ) {
        super( ex.getCause() );
    }

    /** Constructor to initialize an XTCEDatabaseException from an XPath
     * Exception, which needs to be handled via the getCause() instead
     * of the more common getLocalizedMessage(), so this converts the text from
     * the cause to the localized message accessor.
     *
     * @param ex XPathException thrown by the XPath evaluate function.
     *
     */

    public XTCEDatabaseException( XPathException ex ) {
        super( ex.getCause() );
    }

    /** Constructor to initialize an XTCEDatabaseException from a list of
     * collected messages.
     *
     * @param messages List of Strings containing feedback during a long
     * operation that may collect many interesting errors, but can proceed
     * until completion.
     *
     */

    public XTCEDatabaseException( List<String> messages ) {
        super( XTCEDatabaseException.concatenate( messages ) );
    }

    /** Private method to concatenate a series of messages in a List of
     * Strings into a single string delimited by new lines.
     *
     * @param messages List of String messages.
     *
     * @return String containing the concatenated messages.
     *
     */

    private static String concatenate( List<String> messages ) {
        StringBuilder sb = new StringBuilder();
        for ( int iii = 0; iii < messages.size(); ++iii ) {
           sb.append( messages.get( iii ) );
           if ( iii < ( messages.size() - 1 ) ) {
               sb.append( System.getProperty( "line.separator" ) ); // NOI18N
           }
        }
        return sb.toString();
    }

    /** Private method to support copying the message from another exception
     * where the author who threw that exception may or may not have localized
     * the message.
     *
     * @param ex Exception to be converted.
     *
     * @return String containing the best guess at a valid error message from
     * the candidate exception.
     *
     */

    private static String extractMessage( Exception ex ) {
        if ( ex.getLocalizedMessage() == null ) {
            if ( ex.getMessage() == null ) {
                String msg =
                    XTCEFunctions.getText( "general_lastresort_exception" ) + // NOI18N
                    " from " + // NOI18N
                    ex.getClass().toString();
                return msg;
            } else {
                return ex.getMessage();
            }
        } else {
            return ex.getLocalizedMessage();
        }
    }

}
