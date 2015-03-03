/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import java.util.ArrayList;

/** A class for instantiating an error handler to catch the warnings and errors
 * that are returned by the SAX XML parser.
 *
 * @author Melanie Laub
 */

public class XTCESchemaErrorHandler implements ErrorHandler {

    /** Constructor
     *
     *
     */

    XTCESchemaErrorHandler() {
        messages = new ArrayList<String>();
        errors = 0;
        warnings = 0;
    }

    /** Method to capture errors from the SAX parser.
     *
     * @param ex Exception received from SAX to interrogate.
     *
     */

    public void error( SAXParseException ex ) {
        messages.add( "XML Schema Compliance Error: " + ex.getLocalizedMessage() );
        ++errors;
    }

    /** Method to capture warnings from the SAX parser.
     *
     * @param ex Exception received from SAX to interrogate.
     *
     */

    public void warning( SAXParseException ex ) {
        String msg = "XML Schema Compliance Warning: " + ex.getLocalizedMessage();
        if ( messages.contains( msg ) == false ) {
            messages.add( "XML Schema Compliance Warning: " + ex.getLocalizedMessage() );
            ++warnings;
        }
    }

    /** Method to capture fatal errors from the SAX parser.
     *
     * @param ex Exception received from SAX to interrogate.
     *
     */

    public void fatalError( SAXParseException ex ) {
        messages.add( "XML Schema Compliance Fatal: " + ex.getLocalizedMessage() );
        ++errors;
    }

    /// Count of the number of errors

    public long errors = 0;

    /// Count of the number of warnings

    public long warnings = 0;

    /// List of the message strings that have been gathered

    public ArrayList<String> messages;

}
