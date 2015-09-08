/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.io.IOException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import org.xml.sax.SAXException;

/** A class for instantiating an error handler to catch the warnings and errors
 * that are returned by the SAX XML parser.
 *
 * @author David Overeem
 *
 */

public class XTCESchemaErrorHandler implements ErrorHandler,
                                               ValidationEventHandler {

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
        messages.add( errorMsg_ + ex.getLocalizedMessage() );
        ++errors;
    }

    /** Method to capture warnings from the SAX parser.
     *
     * @param ex Exception received from SAX to interrogate.
     *
     */

    @Override
    public void warning( SAXParseException ex ) {

        if ( ex.getMessage().contains( skipMsg_ ) == true ) {
            return;
        }

        String msg = warningMsg_ + ex.getLocalizedMessage();
        if ( messages.contains( msg ) == false ) {
            messages.add( msg );
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
        messages.add( fatalMsg_ + ex.getLocalizedMessage() );
        ++errors;
    }

    /** Method to capture fatal errors from the SAX parser.
     *
     * @param ex Exception received from SAX to interrogate.
     *
     */

    public void fatalError( SAXException ex ) {
        messages.add( XTCEFunctions.getText( "xml_fatal_parse" ) + ": " + // NOI18N
                      ex.getLocalizedMessage() );
        ++errors;
    }

    /** Method to capture fatal errors from the SAX parser.
     *
     * @param ex Exception received from file reading to interrogate.
     *
     */

    public void fatalError( IOException ex ) {
        messages.add( XTCEFunctions.getText( "xml_io_fatal" ) + ": " + // NOI18N
                      ex.getLocalizedMessage() );
        ++errors;
    }

    /** Method to capture schema validation events that occur during the JAXB
     * unmarshal operation.
     *
     * @param event ValidationEvent containing the message of interest.
     *
     * @return true always.
     *
     */

    @Override
    public boolean handleEvent( ValidationEvent event ) {

        String msg = null;

        switch ( event.getSeverity() ) {

            case ValidationEvent.WARNING:
                if ( event.getMessage().contains( skipMsg_ ) == true ) {
                    return true;
                }
                msg = warningMsg_ + event.getMessage();
                if ( messages.contains( msg ) == false ) {
                    messages.add( msg );
                    ++warnings;
                }
                break;

            case ValidationEvent.ERROR:
                msg = errorMsg_ + event.getMessage();
                if ( messages.contains( msg ) == false ) {
                    messages.add( msg );
                    ++errors;
                }
                break;

            case ValidationEvent.FATAL_ERROR:
                msg = fatalMsg_ + event.getMessage();
                if ( messages.contains( msg ) == false ) {
                    messages.add( msg );
                    ++errors;
                }
                break;

        }

        return true;

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

    private final String skipMsg_    = "reverting to fallback";
    private final String warningMsg_ =
        XTCEFunctions.getText( "xml_schema_warning" ) + ": "; // NOI18N
    private final String errorMsg_   =
        XTCEFunctions.getText( "xml_schema_error" ) + ": "; // NOI18N
    private final String fatalMsg_   =
        XTCEFunctions.getText( "xml_schema_fatal" ) + ": "; // NOI18N

}
