/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import java.util.ArrayList;
import java.util.List;

/** A class for instantiating an error handler to catch the warnings and errors
 * that are returned by the SAX XML parser.
 *
 * @author David Overeem
 *
 */

public class XTCESchemaErrorHandler implements ErrorHandler {

    /** Constructor
     *
     *
     */

    XTCESchemaErrorHandler() {
        messages = new ArrayList<>();
        errors   = 0;
        warnings = 0;
    }

    /** Method to capture errors from the SAX parser.
     *
     * @param ex Exception received from SAX to interrogate.
     *
     */

    @Override
    public void error( SAXParseException ex ) {
        messages.add( "XML Schema Compliance Error: " + ex.getLocalizedMessage() );
        ++errors;
    }

    /** Method to capture warnings from the SAX parser.
     *
     * @param ex Exception received from SAX to interrogate.
     *
     */

    @Override
    public void warning( SAXParseException ex ) {
        if ( ex.getMessage().contains( "reverting to fallback" ) == true ) {
            return;
        }
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

    @Override
    public void fatalError( SAXParseException ex ) {
        messages.add( "XML Schema Compliance Fatal: " + ex.getLocalizedMessage() );
        ++errors;
    }

    /** Retrieve the list of messages collected by SAX during validation of the
     * XTCE document.
     *
     * @return List of Strings that were collected from the parser.
     *
     */

    public List<String> getMessages() {
        return messages;
    }

    /** Retrieve the count of the warnings that were logged by SAX during
     * validation of the XTCE document.
     *
     * @return long containing the count.
     *
     */

    public long getWarningCount() {
        return warnings;
    }

    /** Retrieve the count of the errors that were logged by SAX during
     * validation of the XTCE document.
     *
     * @return long containing the count.
     *
     */

    public long getErrorCount() {
        return errors;
    }

    /// Count of the number of errors

    private long errors;

    /// Count of the number of warnings

    private long warnings;

    /// List of the message strings that have been gathered

    private List<String> messages;

}
