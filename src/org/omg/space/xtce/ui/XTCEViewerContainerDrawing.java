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

package org.omg.space.xtce.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry;
import org.omg.space.xtce.toolkit.XTCEContainerContentEntry.FieldType;
import org.omg.space.xtce.toolkit.XTCEContainerContentModel;
import org.omg.space.xtce.toolkit.XTCEDatabaseException;
import org.omg.space.xtce.toolkit.XTCEFunctions;

/** The Container/Telecommand drawing class that serves as a JPanel, which
 * extends JComponent in Java Swing.
 *
 * @author David Overeem
 *
 */

public class XTCEViewerContainerDrawing extends JPanel {

    /** Private Constructor
     *
     * This is used for the deepCopy() method that clones this object for the
     * purpose of the "Clone Display" menu item.  Without the cloning, the
     * JScrollPane Viewport on the new display will "take over" the object and
     * it will disappear from the main display window.
     *
     */

    private XTCEViewerContainerDrawing() {
        
    }

    /** Constructor
     *
     * This is the constructor that initializes and sets up all the internal
     * data needed to paint the drawing when requested by the Java Swing event
     * dispatch thread.  The drawing is made when the event dispatch thread
     * calls "paintComponent( Graphics )".
     *
     * @param contentModel XTCEContainerContentModel with the processed
     * container or telecommand contents from XTCEDatabase.processContainer()
     * or XTCEDatabase.processTelecommand().
     *
     * @param orientDrawingAs Orientation enumeration, defined within this
     * class, to instruct the paintComponent( Graphics ) method to draw the
     * contents either from LEFT_TO_RIGHT or TOP_TO_BOTTOM.
     *
     * @param showAllNamespaces boolean indicating the preference to show all
     * of the Aliases for each item (an XTCENamedObject in the toolkit).  This
     * argument affects the argument preferredNamespace in that if this is
     * true, then the later will have no effect.
     *
     * @param showAliasNamespaces boolean indicating the preference to show the
     * namespace names in addition to the Aliases in the form NS:ALIAS.
     *
     * @param preferredNamespace String containing the preferred Alias
     * namespace to use when an Alias exists for any item to display.  This
     * has the effect of limiting the Aliases shown to just the ones that are
     * a part of this named namespace.  It has no effect in the event that the
     * showAllNamespaces boolean argument is true.
     *
     */

    XTCEViewerContainerDrawing( XTCEContainerContentModel contentModel,
                                Orientation               orientDrawingAs,
                                boolean                   showAllNamespaces,
                                boolean                   showAliasNamespaces,
                                String                    preferredNamespace ) {

        contentModel_        = contentModel;
        orientDrawingAs_     = orientDrawingAs;
        entriesInUse_        = new ArrayList<>();
        showAllNamespaces_   = showAllNamespaces;
        showAliasNamespaces_ = showAliasNamespaces;
        preferredNamespace_  = preferredNamespace;

        initializeDrawingEntryList();

        setDefaultSizing();

    }

    /** Method to report the needed size of the drawing, which is often
     * requested during painting operations of the JScrollPane that contains
     * this custom drawing component.
     *
     * @return Dimension object containing the calculated dimensions needed to
     * see the entire drawing.
     *
     */

    @Override
    public Dimension getPreferredSize() {
        return new Dimension( totalSizeX_, totalSizeY_ );
    }

    /** Method to draw the container/telecommand contents picture onto the
     * Java Swing supplied Graphics object when requested by the event dispatch
     * thread, which occurs quite often as it checks for updates.
     *
     * This function contains a short circuit to prevent drawing this very
     * frequently.  The flag is set to false when doing a re-orientation of the
     * picture using the reOrient() method.  Other methods can also potentially
     * cause a change to the drawing assumptions and should implement something
     * similar to reOrient().  After calling such a function, the graphical
     * interface caller should then call repaint(), which is provided by the
     * Java Swing interface on this class through inheritance.  This will have
     * the new drawing ready on the next screen update.
     *
     * @param ggg Graphics object to use to make the drawing, which must be
     * supplied by Java Swing for screen drawings, but it also suppled in this
     * case when the image is being written to a file using a BufferedImage
     * object instead of the Swing display.
     *
     */

    @Override
    public void paintComponent( Graphics ggg ) {
        super.paintComponent( ggg );

        // short circuit this if there are no applicable entries
        if ( entriesInUse_.isEmpty() == true ) {
            return;
        }

        // create the drawing if it has not already been drawn since the
        // content last updated on the table
        if ( drawingDone_ == false ) {
            createDrawing( ggg );
        } else {
            drawingDone_ = true;
        }

    }

    /** Method to change the orientation flag to a new setting, either
     * LEFT_TO_RIGHT or TOP_TO_BOTTOM and sets the drawing flag to cause a
     * redraw at the next opportunity.
     *
     * After browsing this class, the reader might wonder why there is not a
     * corresponding set of methods for the alias preferences.  Since changes
     * to those also need to repaint the table on the graphical user interface,
     * it instead generated a new version of this object.
     *
     * @param newOrientation Orientation enumerations, which is local to this
     * class, and can be either LEFT_TO_RIGHT or TOP_TO_BOTTOM.
     *
     */

    public void reOrient( Orientation newOrientation ) {
        orientDrawingAs_ = newOrientation;
        drawingDone_     = false;
    }

    /** Method to save the drawing to an image file on the filesystem.
     *
     * This method supports several file formats.  In includes those that are
     * supported by the Java ImageIO class.
     *
     * @param fileName File containing the name and path to store the output
     * image to.  It must contain the file extension for the image type or this
     * function will throw with an error.
     *
     * @throws XTCEDatabaseException thrown in the event that the image could
     * not be written.  The target file and the cause will be reported.
     *
     */

    public void save( File fileName ) throws XTCEDatabaseException {

        BufferedImage image = new BufferedImage( totalSizeX_,
                                                 totalSizeY_,
                                                 BufferedImage.TYPE_INT_ARGB);

        // setup a local Graphics2D object to use as the rendering engine
        // for the drawing.

        Graphics2D ggg = image.createGraphics();
        ggg.setBackground( Color.WHITE );
        ggg.setColor( Color.BLACK );

        drawingDone_ = false;

        // use the paintComponent function that Java Swing would otherwise use
        // for the display with our local Graphics object.

        paintComponent( ggg );

        try {

            // cheesy - but make sure we have a file extension so that we can
            // pass the correct type to the write method.

            int lastPeriodIndex = fileName.getName().lastIndexOf('.'); // NOI18N
            if ( lastPeriodIndex == -1 ) {
                throw new XTCEDatabaseException( XTCEFunctions.getText( "error_missing_ext" ) ); // NOI18N
            }

            // write the imagem, this could send back an IOException.

            String fileType = fileName.getName().substring( lastPeriodIndex + 1 );
            ImageIO.write( image, fileType, fileName );

        } catch ( Exception ex ) {

            String msg = XTCEFunctions.getText( "error_cannot_save_image" ) + // NOI18N
                         " '" + fileName + "', " + // NOI18N
                         XTCEFunctions.getText( "general_because" ) + // NOI18N
                         ": "; // NOI18N
            throw new XTCEDatabaseException( msg + ex.getLocalizedMessage() );

        }

    }

    /** This function is called by the paintComponet function to dispatch the
     * drawing work to other functions based on the orientation preference.
     *
     * @param ggg Graphics object to use as the rendering engine.
     *
     */

    private void createDrawing( Graphics ggg ) {

        int maxEntriesInUse = 256;

        // momentarily restore the default sizes so that the drawing functions
        // can grow them as needed, otherwise the sizes will leak larger and
        // larger.

        setDefaultSizing();

        // short circuit crazy large drawings that are unreadable anyway

        long drawingEntryCount = 0;

        for ( XTCEContainerContentEntry entry : contentModel_.getContentList() ) {
            if ( entry.isCurrentlyInUse() == true ) {
                drawingEntryCount++;
            }
        }

        if ( drawingEntryCount > maxEntriesInUse ) {
            totalSizeX_ = 250;
            totalSizeY_ = 100;
            ggg.drawString( XTCEFunctions.getText( "warning_drawing_too_wide" ) + // NOI18N
                            " " + // NOI18N
                            Integer.toString( maxEntriesInUse ),
                            25,
                            25 );
            return;
        }

        // make the drawing

        if ( orientDrawingAs_ == Orientation.LEFT_TO_RIGHT ) {
            drawParameterNames( ggg );
            drawParameters( ggg );
            drawByteLineLeftToRight( ggg );
        } else if ( orientDrawingAs_ == Orientation.TOP_TO_BOTTOM ) {
            drawTopToBottomRectangles( ggg );
            drawByteLineTopToBottom( ggg );
        }

    }

    private void drawParameterNames( Graphics ggg ) {

        int numberOfItems = entriesInUse_.size();
        int upOffsetEach  = scale( 3 );
        int currentOffset = upOffsetEach * numberOfItems;

        totalSizeY_ = originY_ + currentOffset + scale( 40 );
        rectBaseY_  = originY_ + currentOffset;

        for ( DrawingEntry entry : entriesInUse_ ) {

            String itemValue = "";
            if ( entry.itemEntryObj.getValue() != null ) {
                itemValue = entry.itemEntryObj.getValue().toStringWithoutParameter();
            }

            String containerName   = entry.containerName;
            String itemName        = entry.itemName;
            String itemAliases     = entry.itemAliases;
            String itemSize        = entry.itemEntryObj.getRawSizeInBits();
            String itemStartBit    = entry.itemEntryObj.getStartBit();
            int    itemSizeInt     = Integer.parseInt( itemSize );
            int    itemStartBitInt = Integer.parseInt( itemStartBit );

            int lineBottomX = rectBaseX_ + scale( itemStartBitInt ) + ( scale( itemSizeInt ) / 2 );
            int lineBottomY = rectBaseY_;
            int lineTopX    = lineBottomX;
            int lineTopY    = lineBottomY - currentOffset;
            drawDashedLine( ggg, lineBottomX, lineBottomY, lineTopX, lineTopY );

            int aLineLeftX = lineTopX;
            int aLineLeftY = lineTopY;
            int aLineRightX = aLineLeftX + scale( 2 );
            int aLineRightY = aLineLeftY;
            drawDashedLine( ggg, aLineLeftX, aLineLeftY, aLineRightX, aLineRightY );

            String parameterDesc = itemName;
            if ( itemAliases.isEmpty() == false ) {
                parameterDesc += " (" + itemAliases + ")"; // NOI18N
            }
            if ( itemValue.isEmpty() == false ) {
                parameterDesc += " " + itemValue; // NOI18N
            }
            int textPosX = aLineRightX + scale( 1 );
            int textPosY = aLineRightY;
            drawYCenteredString( ggg, parameterDesc, textPosX, textPosY );

            int textWidth = getStringWidth( ggg, parameterDesc ) * 2;
            if ( ( textPosX + textWidth + 200 ) > totalSizeX_ ) {
                totalSizeX_ = textPosX + textWidth + 200;
            }

            currentOffset -= upOffsetEach;

        }

    }

    private void drawByteLineLeftToRight( Graphics ggg ) {

        int linex1 = rectBaseX_;
        int liney1 = rectBaseY_ + scale( 20 );
        int linex2 = rectBaseX_ + scale( (int)contentModel_.getTotalSize() );
        int liney2 = liney1;
        ggg.drawLine( linex1, liney1, linex2, liney2 );
        
        int textxpos = rectBaseX_ - 75;
        int textypos = liney1;
        ggg.drawString( XTCEFunctions.getText( "general_bytes" ), // NOI18N
                        textxpos,
                        textypos );

        int bitCountInBytes = (int)contentModel_.getTotalSize() +
                              ( (int)contentModel_.getTotalSize() % 8 );

        for ( int iii = 0; iii <= bitCountInBytes; iii += 8 ) {
            int x1 = rectBaseX_ + scale( iii );
            int y1 = rectBaseY_ + scale( 19 );
            int x2 = x1;
            int y2 = rectBaseY_ + scale( 21 );
            ggg.drawLine( x1, y1, x2, y2 );
            int posx1 = x1;
            int posy1 = rectBaseY_ + scale( 22 );
            drawCenteredString( ggg, Integer.toString( iii / 8 ), posx1, posy1 );
        }

        int xpostotal = rectBaseX_ - 75;
        int ypostotal = rectBaseY_ + scale( 35 );
        String totalsMessage = XTCEFunctions.getText( "general_totalbytes" ) + // NOI18N
            ": " + // NOI18N
            Integer.toString( bitCountInBytes / 8 ) +
            " " + // NOI18N
            XTCEFunctions.getText( "general_bits_cap" ) + // NOI18N
            ": " + // NOI18N
            Long.toString( contentModel_.getTotalSize() );
        ggg.drawString( totalsMessage, xpostotal, ypostotal);
        ggg.drawString( XTCEFunctions.getText( "general_bits_cap" ), // NOI18N
                        xpostotal,
                        rectBaseY_ + ( scale( 15 ) / 2 ) );

        totalSizeX_ = rectBaseX_ + linex2 + 25;

    }

    private void drawByteLineTopToBottom( Graphics ggg ) {

        int linex1 = rectBaseX_ + scale( 50 ) + scale( 50 ) + scale( 6 );
        int liney1 = rectBaseY_;
        int linex2 = linex1;
        int liney2 = rectBaseY_ + ( (int)contentModel_.getTotalSize() * 15 );
        ggg.drawLine( linex1, liney1, linex2, liney2 );

        int textxpos = linex2;
        int textypos = liney2 + 25;
        drawCenteredString( ggg,
                            XTCEFunctions.getText( "general_bytes" ), // NOI18N
                            textxpos,
                            textypos );

        int bitCountInBytes = (int)contentModel_.getTotalSize() +
                              ( (int)contentModel_.getTotalSize() % 8 );

        for ( int iii = 0; iii <= bitCountInBytes; iii += 8 ) {
            int x1 = linex1 - scale( 2 );
            int y1 = liney1 + ( iii * 15 );
            int x2 = linex1 + scale( 2 );
            int y2 = y1;
            ggg.drawLine( x1, y1, x2, y2 );
            int posx1 = x2 + scale( 2 );
            int posy1 = y2;
            drawCenteredString( ggg, Integer.toString( iii / 8 ), posx1, posy1 );
        }

        //int xpostotal = rectBaseX_ - 75;
        //int ypostotal = rectBaseY_ + scale( 35 );
        //String totalsMessage = "Total Bytes: " +
        //    Integer.toString( bitCountInBytes / 8 ) + " Bits: " +
        //    Long.toString( contentModel_.getTotalSize() );
        //ggg.drawString( totalsMessage, xpostotal, ypostotal);
        //ggg.drawString( "Bits", xpostotal, rectBaseY_ + ( scale( 15 ) / 2 ) );

        if ( totalSizeX_ < ( rectBaseX_ + linex2 + 25 ) ) {
            totalSizeX_ = rectBaseX_ + linex2 + 25;
        }

    }

    private void drawParameters( Graphics ggg ) {

        String previousContainerName = ""; // NOI18N
        int    previousContainerEnd  = 0;

        DrawingEntry lastEntry  = entriesInUse_.get( entriesInUse_.size() - 1 );
        DrawingEntry firstEntry = entriesInUse_.get( 0 );

        for ( DrawingEntry entry : entriesInUse_ ) {

            String containerName = entry.containerName;
            String itemName      = entry.itemName;
            String itemSize      = entry.itemEntryObj.getRawSizeInBits();
            String itemStartBit  = entry.itemEntryObj.getStartBit();

            //System.out.println( "Drawing " +
            //                    itemName +
            //                    " in " +
            //                    containerName +
            //                    " starting at bit " +
            //                    itemStartBit +
            //                    " size in bits " +
            //                    itemSize );

            int itemSizeInt     = Integer.parseInt( itemSize );
            int itemStartBitInt = Integer.parseInt( itemStartBit );

            drawLeftToRightRectangles( ggg, itemSizeInt, itemStartBitInt );

            if ( ( previousContainerName.equals( containerName ) == false ) ||
                 ( lastEntry                                     == entry ) ) {

                if ( previousContainerName.equals( containerName ) == false ) {
                    int x1 = rectBaseX_ + scale( itemStartBitInt );
                    int y1 = rectBaseY_ + scale( 28 );
                    int x2 = x1;
                    int y2 = rectBaseY_ + scale( 30 );
                    ggg.drawLine( x1, y1, x2, y2 );
                    int containerLength = itemStartBitInt - previousContainerEnd;
                    if ( ( firstEntry != entry ) && ( containerLength != 0 ) ) {
                        int xpos1 = rectBaseX_ + scale( previousContainerEnd ) + ( scale( containerLength ) / 2 );
                        int ypos1 = rectBaseY_ + scale( 28 ) - 2;
                        drawCenteredString( ggg, Integer.toString( containerLength ), xpos1, ypos1 );
                    }
                    previousContainerName = containerName;
                    previousContainerEnd  = itemStartBitInt;
                }

                if ( lastEntry == entry ) {
                    int containerLength = itemStartBitInt - previousContainerEnd + itemSizeInt;
                    int x1 = rectBaseX_ + scale( itemStartBitInt ) + scale( itemSizeInt );
                    int y1 = rectBaseY_ + scale( 28 );
                    int x2 = x1;
                    int y2 = rectBaseY_ + scale( 30 );
                    ggg.drawLine( x1, y1, x2, y2 );
                    int xpos1 = rectBaseX_ + scale( previousContainerEnd ) + ( scale( containerLength ) / 2 );
                    int ypos1 = rectBaseY_ + scale( 28 ) - 2;
                    drawCenteredString( ggg, Integer.toString( containerLength ), xpos1, ypos1 );
                }

            }

        }

        drawContainersLine( ggg );

    }

    private void drawContainersLine( Graphics ggg ) {

        int clinex1 = rectBaseX_;
        int cliney1 = rectBaseY_ + scale( 29 );
        int clinex2 = clinex1 + scale( (int)contentModel_.getTotalSize() );
        int cliney2 = cliney1;
        ggg.drawLine( clinex1, cliney1, clinex2, cliney2 );

        int textxpos = rectBaseX_ - 75;
        int textypos = cliney1;
        ggg.drawString( XTCEFunctions.getText( "general_containers" ), // NOI18N
                        textxpos,
                        textypos );

    }

    private void drawLeftToRightRectangles( Graphics ggg,
                                            int      itemSize,
                                            int      itemStartBit ) {

        int leftTopX = rectBaseX_ + scale( itemStartBit );
        int leftTopY = rectBaseY_;
        int width  = scale( itemSize );
        int height = scale( 15 );
        ggg.drawRect( leftTopX, leftTopY, width, height );
        int posX = rectBaseX_ + scale( itemStartBit ) + ( scale( itemSize ) / 2 );
        int posY = rectBaseY_ + ( height / 2 );
        drawCenteredString( ggg, Integer.toString( itemSize ), posX, posY );

    }

    private void drawTopToBottomRectangles( Graphics ggg ) {

        String previousContainerName = ""; // NOI18N
        int    previousContainerEnd  = 0;

        DrawingEntry lastEntry  = entriesInUse_.get( entriesInUse_.size() - 1 );
        DrawingEntry firstEntry = entriesInUse_.get( 0 );

        for ( DrawingEntry entry : entriesInUse_ ) {

            String itemValue = ""; // NOI18N
            if ( entry.itemEntryObj.getValue() != null ) {
                itemValue = entry.itemEntryObj
                                 .getValue()
                                 .toStringWithoutParameter();
            }

            String containerName   = entry.containerName;
            String itemName        = entry.itemName;
            String itemAliases     = entry.itemAliases;
            String itemSize        = entry.itemEntryObj.getRawSizeInBits();
            String itemStartBit    = entry.itemEntryObj.getStartBit();
            int    itemSizeInt     = Integer.parseInt( itemSize );
            int    itemStartBitInt = Integer.parseInt( itemStartBit );

            int leftTopX = rectBaseX_ + scale( 50 );
            int leftTopY = rectBaseY_ + ( itemStartBitInt * 15 );
            int width  = scale( 50 );
            int height = itemSizeInt * 15;
            ggg.drawRect( leftTopX, leftTopY, width, height );

            String bits = itemSize + " bit"; // NOI18N
            if ( itemSizeInt > 1 ) {
                bits += "s"; // NOI18N
            }
            int posX = leftTopX - scale( 5 );
            int posY = leftTopY + ( height / 2 );
            drawCenteredString( ggg, bits, posX, posY );

            String parameterDesc = itemName;
            if ( itemAliases.isEmpty() == false ) {
                parameterDesc += " (" + itemAliases + ")"; // NOI18N
            }

            int textPosX = leftTopX + ( width / 2 );
            int textPosY = leftTopY + ( height / 2 );

            if ( itemValue.isEmpty() == false ) {
                parameterDesc += " " + itemValue; // NOI18N
            }

            drawCenteredString( ggg, parameterDesc, textPosX, textPosY );

            if ( ( leftTopY + height + 50 ) > totalSizeY_ ) {
                totalSizeY_ = leftTopY + height + 50;
            }

            if ( ( previousContainerName.equals( containerName ) == false ) ||
                 ( lastEntry                                     == entry ) ) {

                if ( previousContainerName.equals( containerName ) == false ) {
                    int x1 = rectBaseX_ + scale( 2 );
                    int y1 = leftTopY;
                    int x2 = leftTopX - scale( 2 );
                    int y2 = y1;
                    drawDashedLine( ggg, x1, y1, x2, y2 );
                    int containerLength = itemStartBitInt - previousContainerEnd;
                    if ( ( firstEntry != entry ) && ( containerLength != 0 ) ) {
                        int xpos1 = x1;
                        int ypos1 = rectBaseY_ + ( previousContainerEnd * 15 ) + ( ( containerLength * 15 ) / 2 );// needs 15?
                        drawYCenteredString( ggg, previousContainerName, xpos1, ypos1 );
                    }
                    previousContainerName = containerName;
                    previousContainerEnd  = itemStartBitInt;
                }

                if ( lastEntry == entry ) {
                    int containerLength = itemStartBitInt - previousContainerEnd + itemSizeInt;
                    int x1 = rectBaseX_ + scale( 2 );
                    int y1 = leftTopY + ( itemSizeInt * 15 );
                    int x2 = leftTopX - scale( 2 );
                    int y2 = y1;
                    drawDashedLine( ggg, x1, y1, x2, y2 );
                    int xpos1 = x1;
                    int ypos1 = rectBaseY_ + ( previousContainerEnd * 15 ) + ( ( containerLength * 15 ) / 2 );
                    drawYCenteredString( ggg, containerName, xpos1, ypos1 );
                }

            }

        }

    }

    /** Function to scale a value or distance in pixels consistently.
     *
     * @param value integer containing pixel value relative to the base X or
     * Y value observed by the drawing area.
     *
     * @return integer containing the value scaled by the constant integer in
     * the private data members.
     *
     */

    private int scale( int value ) {
        return value * scaleFactor_;
    }

    /** Function to render a string with the Graphics object where the provided
     * Y anchor point is approximately the center of the string instead
     * of the left baseline corner of the text.
     *
     * This is an annoying function to have to write because this is so easy in
     * Perl/Tk.  The X and Y points are adjusted based on the anticipated
     * length and height of the string, in the native pixel measurement of the
     * Graphics object.
     *
     * @param ggg Graphics object that is being used as the rendering engine.
     *
     * @param text String containing the text to calculate the center from and
     * draw.
     *
     * @param xxx integer containing the X coordinate point, which is left
     * alone, meaning that it is not adjusted to the center of the string.
     *
     * @param yyy integer containing the Y coordinate point intended to be the
     * center of the string.
     *
     */

    private void drawYCenteredString( Graphics ggg, String text, int xxx, int yyy ) {

        FontMetrics metrics = ggg.getFontMetrics();

        int ypos = yyy + ( metrics.getHeight() / 2 );

        ggg.drawString( text, xxx, ypos );

    }

    /** Function to render a string with the Graphics object where the provided
     * X and Y anchor points are approximately the center of the string instead
     * of the left baseline corner of the text.
     *
     * This is an annoying function to have to write because this is so easy in
     * Perl/Tk.  The X and Y points are adjusted based on the anticipated
     * length and height of the string, in the native pixel measurement of the
     * Graphics object.
     *
     * @param ggg Graphics object that is being used as the rendering engine.
     *
     * @param text String containing the text to calculate the center from and
     * draw.
     *
     * @param xxx integer containing the X coordinate point intended to be the
     * center of the string.
     *
     * @param yyy integer containing the Y coordinate point intended to be the
     * center of the string.
     *
     */

    private void drawCenteredString( Graphics ggg, String text, int xxx, int yyy ) {
        
        FontMetrics metrics = ggg.getFontMetrics();

        int xpos = xxx - ( metrics.stringWidth( text ) / 2 );
        int ypos = yyy + ( metrics.getHeight() / 2);

        ggg.drawString( text, xpos, ypos );

    }

    /** Function to draw a dashed line instead of the default drawLine()
     * function.
     *
     * This method is needed to set a one-time preference for the kind of line
     * to be drawn, which Swing requires us to create a temporary Graphics
     * object and use that to adjust the line characteristics, draw the line,
     * and then dispose of the temporary Graphics object.  Seems a bit verbose
     * for what was a pretty simple task in Perl/Tk.
     *
     * @param ggg Graphics object to use as a factory to create a temporary
     * new Graphics object.
     *
     * @param x1 integer containing the X value of the first point on the line
     * segment to be drawn.
     *
     * @param y1 integer containing the Y value of the first point on the line
     * segment to be drawn.
     *
     * @param x2 integer containing the X value of the second point on the line
     * segment to be drawn.
     *
     * @param y2 integer containing the Y value of the second point on the line
     * segment to be drawn.
     *
     */

    private void drawDashedLine( Graphics ggg, int x1, int y1, int x2, int y2 ) {

        Graphics2D g2d = (Graphics2D)ggg.create();

        Stroke dashed = new BasicStroke( 1,
                                         BasicStroke.CAP_BUTT,
                                         BasicStroke.JOIN_BEVEL,
                                         0,
                                         new float[]{2},
                                         0);

        g2d.setStroke( dashed );
        g2d.drawLine( x1, y1, x2, y2 );

        g2d.dispose();

    }

    /** Retrieve the approximate width of a string in the native measurement
     * of the Graphics object rendering engine.
     *
     * @param ggg Graphics object containing the rendering engine.
     *
     * @param text String containing the text to measure the graphical length.
     *
     * @return integer containing the approximate number of pixels, which is
     * the native measurement of the Graphics object in Java Swing.
     *
     */

    private int getStringWidth( Graphics ggg, String text ) {

        FontMetrics metrics = ggg.getFontMetrics();
        return metrics.stringWidth( text );

    }

    /** This method sets the base values for the rectangles and the total size
     * counts to the default values.
     *
     * This is called in between orientation changes so that the sizes will not
     * be retained.  The two orientations make much different X and Y
     * dimensions.
     *
     */

    private void setDefaultSizing() {
        rectBaseX_  = originX_;
        rectBaseY_  = originY_;
        totalSizeX_ = 800;
        totalSizeY_ = 600;
    }

    /** Function to copy this XTCEViewerContainerDrawing to another one so that
     * it can be used in another JScrollPane.
     *
     * Without the cloning, the JScrollPane Viewport on the new display will
     * "take over" the object and it will disappear from the main display
     * window.  To say that this is a deep copy is not really true, but good
     * enough for the purposes of this class in the graphical user interface.
     *
     * @return XTCEViewerContainerDrawing copied from this one.  Note that the
     * big references, like the content model, are still references to the
     * original data.
     *
     */

    public XTCEViewerContainerDrawing deepCopy() {

        XTCEViewerContainerDrawing that = new XTCEViewerContainerDrawing();
        that.drawingDone_         = false;
        that.contentModel_        = this.contentModel_;
        that.orientDrawingAs_     = this.orientDrawingAs_;
        that.entriesInUse_        = this.entriesInUse_;
        that.rectBaseX_           = this.rectBaseX_;
        that.rectBaseY_           = this.rectBaseY_;
        that.totalSizeX_          = this.totalSizeX_;
        that.totalSizeY_          = this.totalSizeY_;
        that.showAllNamespaces_   = this.showAliasNamespaces_;
        that.showAliasNamespaces_ = this.showAliasNamespaces_;
        that.preferredNamespace_  = this.preferredNamespace_;
        return that;

    }

    /** This initializer function pre-processes the entry list from the model
     * object that is passed to the constructor.
     *
     * The entry list is pre-processed to create another list of references to
     * the entries in the DrawingObject class, which is local to this drawing
     * class.  The DrawingObjects abstract away the name of the field, whether
     * Parameter or Argument, into an Item Name.  It also does the same thing
     * for the Container or Telecommand name, into just a Container Name.
     * In addition, it filters all "information only" rows, including
     * Aggregates, Containers, and Telecommands that have no length or start
     * bit in the entry list.  This makes it more convenient for the drawing
     * functions because they do not use these entries.  It then ensures that
     * the entries kept are sorted by start bit, which is most convenient for
     * the LEFT_TO_RIGHT orientation, but has no effect on the TOP_TO_BOTTOM
     * orientation.
     *
     */

    private void initializeDrawingEntryList() {

        for ( XTCEContainerContentEntry entry : contentModel_.getContentList() ) {

            String containerName = ""; // NOI18N
            String itemName      = ""; // NOI18N
            String itemAliases   = ""; // NOI18N
            if ( entry.getEntryType() == FieldType.PARAMETER ) {
                itemName      = entry.getParameter().getName();
                containerName = entry.getHoldingContainer().getName();
                itemAliases   =
                    XTCEFunctions.makeAliasDisplayString( entry.getParameter(),
                                                          showAllNamespaces_,
                                                          showAliasNamespaces_,
                                                          preferredNamespace_ );
            } else if ( entry.getEntryType() == FieldType.ARGUMENT ) {
                itemName      = entry.getArgument().getName();
                containerName = entry.getTelecommand().getName();
                itemAliases   =
                    XTCEFunctions.makeAliasDisplayString( entry.getArgument(),
                                                          showAllNamespaces_,
                                                          showAliasNamespaces_,
                                                          preferredNamespace_ );
            }

            if ( ( itemName.isEmpty()                 == true ) ||
                 ( entry.getRawSizeInBits().isEmpty() == true ) ||
                 ( entry.getStartBit().isEmpty()      == true ) ) {
                continue;
            }

            if ( entry.isCurrentlyInUse() == false ) {
                continue;
            }

            DrawingEntry localEntryItem  = new DrawingEntry();
            localEntryItem.containerName = containerName;
            localEntryItem.itemName      = itemName;
            localEntryItem.itemEntryObj  = entry;
            localEntryItem.itemAliases   = itemAliases;
            entriesInUse_.add( localEntryItem );

        }

        Collections.sort( entriesInUse_ );

    }

    // Private Data Members

    private boolean                   drawingDone_     = false;
    private XTCEContainerContentModel contentModel_    = null;
    private Orientation               orientDrawingAs_ = null;
    private ArrayList<DrawingEntry>   entriesInUse_    = null;
    private final int                 originX_         = 100;
    private final int                 originY_         = 20;
    private int                       rectBaseX_;
    private int                       rectBaseY_;
    private final int                 scaleFactor_     = 8;
    private int                       totalSizeX_;
    private int                       totalSizeY_;
    private boolean                   showAllNamespaces_;
    private boolean                   showAliasNamespaces_;
    private String                    preferredNamespace_;

    /** Enumeration to describe the orientation of the container/telecommand
     * drawing.
     *
     *
     */

    public enum Orientation {

        /// Draw the contents from left to right with start bit 0 on the left.

        LEFT_TO_RIGHT,

        /// Draw the contents from top to bottom with start bit 0 on the top.

        TOP_TO_BOTTOM

    }

    /** Private class to cache the table entry information needed to make the
     * drawings.
     *
     */

    private class DrawingEntry implements Comparable {

        /// The name of the container that holds this entry item.
        public String containerName;

        /// The name of the item for the display, which could be a Parameter
        /// or Argument name.
        public String itemName;

        /// The item aliases as built by the alias preferences passed by the
        /// constructor.
        public String itemAliases;

        /// The XTCEContainerContentEntry object item from the container
        /// content model object passed to the constructor.
        public XTCEContainerContentEntry itemEntryObj;

        /** Method to implement the Java Comparable interface so that this
         * set of DrawingEntry objects can be sorted by the start bit.
         *
         * @param rhs DrawingEntry object to compare to.
         *
         * @return integer of -1, 0, or 1 representing less than greater than,
         * or equal, respectively.
         *
         */

        @Override
        public int compareTo( Object rhs ) {
            if ( this == rhs ) {
                return 0;
            }
            DrawingEntry that = (DrawingEntry)rhs;
            return new Integer( this.itemEntryObj.getStartBit() ).
                   compareTo( new Integer( that.itemEntryObj.getStartBit() ) );
        }

    }

}
