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

    public XTCESchemaErrorHandler() {
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
        if ( ex != null ) {
            messages.add( errorMsg_ + ex.getLocalizedMessage() );
        } else {
            messages.add( errorMsg_ + XTCEFunctions.getText( "general_lastresort_exception" ) );
        }
        ++errors;
    }

    /** Method to capture warnings from the SAX parser.
     *
     * @param ex Exception received from SAX to interrogate.
     *
     */

    @Override
    public void warning( SAXParseException ex ) {

        if ( ex == null ) {
            messages.add( warningMsg_ + XTCEFunctions.getText( "general_lastresort_exception" ) );
            ++warnings;
            return;
        }

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
        if ( ex != null ) {
            messages.add( fatalMsg_ + ex.getLocalizedMessage() );
        } else {
            messages.add( fatalMsg_ + XTCEFunctions.getText( "general_lastresort_exception" ) );
        }
        ++errors;
    }

    /** Method to capture fatal errors from the SAX parser.
     *
     * @param ex Exception received from SAX to interrogate.
     *
     */

    public void fatalError( SAXException ex ) {
        if ( ex != null ) {
            messages.add( XTCEFunctions.getText( "xml_fatal_parse" ) + ": " + // NOI18N
                          ex.getLocalizedMessage() );
        } else {
            messages.add( XTCEFunctions.getText( "xml_fatal_parse" ) + ": " + // NOI18N
                          XTCEFunctions.getText( "general_lastresort_exception" ) );
        }
        ++errors;
    }

    /** Method to capture fatal errors from the SAX parser.
     *
     * @param ex Exception received from file reading to interrogate.
     *
     */

    public void fatalError( IOException ex ) {
        if ( ex != null ) {
            messages.add( XTCEFunctions.getText( "xml_io_fatal" ) + ": " + // NOI18N
                          ex.getLocalizedMessage() );
        } else {
            messages.add( XTCEFunctions.getText( "xml_io_fatal" ) + ": " + // NOI18N
                          XTCEFunctions.getText( "general_lastresort_exception" ) );
        }
        ++errors;
    }

    /** Method to capture schema validation events that occur during the JAXB
     * unmarshal operation.
     *
     * @param event ValidationEvent containing the message of interest.
     *
     * @return true unless the event is null, then it is false.
     *
     */

    @Override
    public boolean handleEvent( ValidationEvent event ) {

        if ( event == null ) {
            messages.add( errorMsg_ + XTCEFunctions.getText( "general_lastresort_exception" ) );
            ++errors;
            return false;
        }

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

    private final String skipMsg_    =
        XTCEFunctions.getText( "xml_fallback_ignore" ); // NOI18N
    private final String warningMsg_ =
        XTCEFunctions.getText( "xml_schema_warning" ) + ": "; // NOI18N
    private final String errorMsg_   =
        XTCEFunctions.getText( "xml_schema_error" ) + ": "; // NOI18N
    private final String fatalMsg_   =
        XTCEFunctions.getText( "xml_schema_fatal" ) + ": "; // NOI18N

}
