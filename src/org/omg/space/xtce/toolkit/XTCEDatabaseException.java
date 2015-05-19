/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import javax.xml.bind.MarshalException;
import java.util.ArrayList;

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
 * @author Melanie Laub
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

    /** Constructor to initialize an XTCEDatabaseException from a list of
     * collected messages.
     *
     * @param messages ArrayList of Strings containing feedback during a long
     * operation that may collect many interesting errors, but can proceed
     * until completion.
     *
     */

    public XTCEDatabaseException( ArrayList<String> messages ) {
        super( XTCEDatabaseException.concatenate( messages ) );
    }

    /** Private method to concatenate a series of messages in an ArrayList of
     * Strings into a single string delimited by new lines.
     *
     * @param messages ArrayList of String messages.
     *
     * @return String containing the concatenated messages.
     *
     */

    private static String concatenate( ArrayList<String> messages ) {
        StringBuilder sb = new StringBuilder();
        for ( int iii = 0; iii < messages.size(); ++iii ) {
           sb.append( messages.get( iii ) );
           if ( iii < ( messages.size() - 1 ) ) {
               sb.append( "\n" );
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
                return XTCEFunctions.getText( "general_lastresort_exception" ); // NOI18N
            } else {
                return ex.getMessage();
            }
        } else {
            return ex.getLocalizedMessage();
        }
    }

}
