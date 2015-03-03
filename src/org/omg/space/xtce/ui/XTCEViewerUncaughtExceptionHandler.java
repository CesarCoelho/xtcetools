/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.ui;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import org.omg.space.xtce.toolkit.XTCEFunctions;

/** This class implements an exception handler of last resort to prevent the
 * graphical user interface from crashing or dropping unintelligible text to
 * STDOUT.
 *
 * @author Melanie Laub
 *
 */

public final class XTCEViewerUncaughtExceptionHandler
       implements  Thread.UncaughtExceptionHandler {

    /** Constructor
     *
     * The constructor attaches this handler to the JFrame of the XTCE Viewer
     * user interface.
     *
     * @param parent JFrame containing the main window of the application.
     *
     */

    XTCEViewerUncaughtExceptionHandler( JFrame parent ) {

        frame = parent;

    }

    /** Method that is called by Java when no exception handler is defined.
     *
     * This methods changes the behavior such that the uncaught exception will
     * be caught and the localized message displayed to a JOptionPane dialog
     * box with ERROR_MESSAGE specialization.  The message can be acknowledged
     * and processing will continue.
     *
     * @param id Thread that generated the exception, not otherwise used here.
     *
     * @param ex Throwable base class for all Java exceptions, which is used
     * to extract any kind of useful error message that might be there.
     *
     */

    @Override
    public void uncaughtException( Thread id, Throwable ex ) {

        final Writer result = new StringWriter();
        final PrintWriter writer = new PrintWriter( result );
        ex.printStackTrace( writer );

        String st = new String();
        writer.write( st );

        // TODO remove this stack trace someday
        ex.printStackTrace();

        JOptionPane.showMessageDialog( frame,
                                       XTCEFunctions.generalErrorPrefix() + ex.toString() + "\n\n" + st,
                                       XTCEFunctions.getText( "uncaught_exception_error_title" ),
                                       JOptionPane.ERROR_MESSAGE );

    }

    // Private Data Members

    private JFrame frame = null;

}
