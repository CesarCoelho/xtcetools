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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.TimeZone;
import org.omg.space.xtce.AbsoluteTimeDataType;
import org.omg.space.xtce.IntegerDataEncodingType;

/** This class implements CCSDS CUC time to support AbsoluteTimeDataType
 * elements in an XTCE document.
 *
 * <p>CCSDS CUC time format is composed of three identification fields.  These
 * are the P-FIELD, the T-FIELD, and an optional second P-FIELD providing
 * additional information to the first.  This implementation supports the
 * capability of the first P-FIELD and the T-FIELD only.  The default number
 * of octets is 7 where 4 octets are used for the seconds and 3 octets are
 * used for the seconds fraction.  This corresponds to bits 4,5,6,7 of the
 * P-FIELD definition.  The default epoch is TAI, although any epoch can be
 * specified.</p>
 *
 * <p>This class is applicable to AbsoluteTimeType XTCE specifications when
 * three conditions are met by the XML document:</p>
 *
 * <ul>
 * <li>Encoding size in bits of the type element is equal to the sum of the
 * octet sizes for the seconds and fraction seconds (default 56=7).</li>
 * <li>Encoding is Unsigned Integer</li>
 * <li>Epoch of the type element is equal to "TAI", "1958-01-01", or the
 * custom epoch specified.  All midnight GMT assumed.</li>
 * </ul>
 *
 * <p>Although the CCSDS specification carries as low as 2^-24 second counts,
 * the basic demo Java implementation here uses the basic libraries and will
 * only act as far as the milliseconds.  A production grade version of this
 * would need a better underlying time library.</p>
 *
 * @author dovereem
 *
 */

public class XTCECcsdsCucTimeHandler implements XTCEAbsoluteTimeType {

    /** Default constructor to initialize a handler with the default time
     * format and time zone.
     *
     * <p>The default time output format is UTC ISO8601 text.  This is in the
     * form of YYYY-MM-dd HH:MM:ss.SSS</p>
     *
     */

    public XTCECcsdsCucTimeHandler() {
        isoTimeFmt_   = "yyyy-MM-dd HH:mm:ss.SSS"; // NOI18N
        timeZone_     = "GMT"; // NOI18N
        epoch_        = "1958-01-01"; // NOI18N
        numSecBytes_  = 4;
        numFracBytes_ = 3;
    }

    /** Constructor that permits changing the default time format and the
     * time zone information.
     *
     * @param timeFormat String containing the time format string suitable
     * for the Java SimpleDateFormat formatter.
     *
     * @param timeZone String containing the time zone identifier string,
     * suitable for the TimeZone.getTimeZone method in Java.
     *
     * @param epoch String containing the epoch date in the form "yyyy-MM-dd"
     * or the string "TAI".
     *
     * @param numSecBytes integer containing the number of bytes to use for the
     * seconds field in the CCSDS CUC T-FIELD high bytes.
     *
     * @param numFracBytes integer containing the number of bytes to use for
     * the fractional seconds field in the CCSDS CUC T-FIELD low bytes.
     *
     */

    public XTCECcsdsCucTimeHandler( String timeFormat,
                                    String timeZone,
                                    String epoch,
                                    int    numSecBytes,
                                    int    numFracBytes ) {

        isoTimeFmt_   = timeFormat;
        timeZone_     = timeZone;
        numSecBytes_  = numSecBytes;
        numFracBytes_ = numFracBytes;

        if ( ( epoch == null ) || ( epoch.equals( "TAI" ) == true ) ) { // NOI18N
            epoch_ = "1958-01-01"; // NOI18N
        } else {
            epoch_ = epoch;
        }

        if ( epoch_.matches( "^[0-9]{4}-[0-9]{2}-[0-9]{2}$" ) == false ) { // NOI18N
            throw new RuntimeException(
                XTCEFunctions.getText( "error_time_badepoch" ) + // NOI18N
                ": '" + epoch_ + "'" ); // NOI18N
        }

    }

    /** Method to determine if an XTCE AbsoluteTimeType, either Parameter or
     * Argument, is handled by this implementation of the interface.
     *
     * <p>This method returns true when three conditions are met by the XML
     * that is represented by the JAXB AbsoluteTimeDataType object instance
     * passed by the caller:</p>
     *
     * <ul>
     * <li>Encoding size in bits of the type element is equal to the sum of the
     * octet sizes for the seconds and fraction seconds (default 56=7).</li>
     * <li>Encoding is Unsigned Integer</li>
     * <li>Epoch of the type element is equal to "TAI", "1958-01-01", or the
     * custom epoch specified.  All midnight GMT assumed.</li>
     * </ul>
     * 
     * @param instance AbsoluteTimeDataType object from the JAXB representation
     * of the XTCE XML document.
     *
     * @return boolean indicating if this handler supports/implements handling
     * for the user provided Absolute Time Type, as specified in the XML.
     *
     */

    @Override
    public boolean isApplicable( AbsoluteTimeDataType instance ) {

        try {

            long bits = ( numSecBytes_ + numFracBytes_ ) * 8L;

            IntegerDataEncodingType enc =
                instance.getEncoding().getIntegerDataEncoding();

            // The Epoch element in XTCE 1.1 returns a java.lang.String and
            // in the proposed 1.2 it returns a ReferenceTimeType.Epoch class
            // where we need to call getValue().  This reflection allows this
            // example to support both methods, although a program specific
            // function could just reduce this to the version of XTCE that is
            // needed for their local implementation.

            Object epochElement = instance.getReferenceTime().getEpoch();
            String docEpoch;

            if ( epochElement instanceof String == true ) {

                docEpoch = (String)epochElement;

            } else {

                Method epochGetter =
                    epochElement.getClass().getDeclaredMethod( "getValue" );

                docEpoch = (String)epochGetter.invoke( epochElement );

            }

            if ( docEpoch.equals( "TAI" ) == true ) { // NOI18N
                docEpoch = "1958-01-01"; // NOI18N
            }

            return ( ( enc.getSizeInBits().longValue()        == bits ) &&
                     ( enc.getEncoding().equals( "unsigned" ) == true ) && // NOI18N
                     ( docEpoch.equals( epoch_ )              == true ) );

        } catch ( Exception ex ) {
            // no match if a null pointer is caught or there is trouble with
            // the getDeclaredMethod/invoke.  the net effect of the throw means
            // that isApplicable() is false.
        }

        return false;

    }

    /** Retrieve the uncalibrated value of this time item, which can be a
     * Parameter or Argument, when given the raw binary value.
     *
     * <p>This conversion uses only system time capabilities, and as a result
     * is not leap second corrected.</p>
     *
     * @param rawValue BitSet containing the raw binary value that would
     * be encoded on the wire or bitfield.  The raw binary is always expected
     * to be in the order read from the stream.
     *
     * @return String containing the integral microseconds since epoch.
     *
     */

    @Override
    public String getUncalibratedFromRaw( BitSet rawValue ) {

        String hex = XTCEFunctions.bitSetToHex( rawValue,
                                                numSecBytes_ + numFracBytes_ );
        hex = hex.replaceFirst( "0x", "" ); // NOI18N
        while ( hex.length() > ( 2 * ( numSecBytes_ + numFracBytes_ ) ) ) {
            hex = hex.substring( 1 );
        }

        int sPos = numSecBytes_  * 8 / 4;
        int fPos = numFracBytes_ * 8 / 4;
        int tPos = sPos + fPos;

        BigInteger secs = new BigInteger( hex.substring( 0,    sPos ), 16 );
        BigInteger fsec = new BigInteger( hex.substring( sPos, tPos ), 16 );

        BigDecimal exp  = new BigDecimal( "2" ).pow( numFracBytes_ * 8 ); // NOI18N
        BigDecimal frac = new BigDecimal( fsec ).divide( exp );

        frac = frac.multiply( oneMillion_ );

        DateFormat format = new SimpleDateFormat( "yyyy-MM-dd" ); // NOI18N
        format.setTimeZone( TimeZone.getTimeZone( timeZone_ ) );

        secs = secs.multiply( oneThousand_ );

        try {

            Date thisEpoch = format.parse( epoch_ );

            secs = secs.add( new BigInteger( Long.toString( thisEpoch.getTime(), 16), 16 ) );

        } catch ( ParseException ex ) {
            System.err.println( ex.getLocalizedMessage() );
        }

        secs = secs.multiply( oneThousand_ );

        return "0x" + secs.add( frac.toBigInteger() ).toString( 16 ); // NOI18N

    }

    /** Retrieve the EU calibrated value of this time item, which can be a
     * Parameter or Argument, when given the uncalibrated value.
     *
     * <p>The uncalibrated input string contains an integral number of
     * microseconds since the UNIX epoch.  The output string is the UTC string
     * in the form of ISO8601 (YYYY-MM-DD HH:MM:SS.sssss).  The timezone is
     * always UTC/GMT.</p>
     *
     * @param uncalValue String containing the uncalibrated value that is
     * derived from the encoded value on the wire or bitfield.
     *
     * @return String containing the proper EU calibrated representation of the
     * uncalibrated value provided by the caller.
     *
     * @throws NumberFormatException in the event that the uncalibrated value
     * cannot be converted to an integral number.
     *
     */

    @Override
    public String getCalibratedFromUncalibrated( String uncalValue ) {

        BigInteger usecs;
        if ( uncalValue.startsWith( "0x" ) == true ) { // NOI18N
            usecs = new BigInteger( uncalValue.replaceFirst( "0x", "" ), 16 ); // NOI18N
        } else {
            usecs = new BigInteger( uncalValue );
        }

        BigInteger msecs = usecs.divideAndRemainder( oneThousand_ )[0];

        Date javadate = new Date( msecs.longValue() );
        DateFormat format = new SimpleDateFormat( isoTimeFmt_ );
        format.setTimeZone( TimeZone.getTimeZone( timeZone_ ) );

        return format.format( javadate );

    }

    /** Retrieve the raw binary bits for encoding of this time item, which can
     * be a Parameter or Argument, when given the uncalibrated value.
     *
     * <p>This conversion uses only system time capabilities, and as a result
     * is not leap second corrected.</p>
     *
     * @param uncalValue String containing the uncalibrated representation of
     * the value.
     *
     * @return BitSet containing the raw bits.
     *
     * @throws NumberFormatException in the event that the uncalibrated value
     * cannot be converted to an integral number.
     *
     */

    @Override
    public BitSet getRawFromUncalibrated( String uncalValue ) {

        BigInteger usecs;
        if ( uncalValue.startsWith( "0x" ) == true ) { // NOI18N
            usecs = new BigInteger( uncalValue.replaceFirst( "0x", "" ), 16 ); // NOI18N
        } else {
            usecs = new BigInteger( uncalValue );
        }

        BigInteger secs = usecs.divide( oneThousand_ ).divide( oneThousand_ );

        usecs = usecs.subtract( secs.multiply( oneThousand_ )
                                    .multiply( oneThousand_ ) );

        DateFormat format = new SimpleDateFormat( "yyyy-MM-dd" ); // NOI18N
        format.setTimeZone( TimeZone.getTimeZone( timeZone_ ) );

        try {

            Date thisEpoch = format.parse( epoch_ );

            BigInteger epochms = BigInteger.valueOf( thisEpoch.getTime() );
            secs = secs.subtract( epochms.divide( oneThousand_ ) );

        } catch ( ParseException ex ) {
            System.err.println( ex.getLocalizedMessage() );
        }

        //System.out.println( "Remaining microseconds: " + usecs.toString() );
        //System.out.println( "Remaining microseconds: 0x" + usecs.toString( 16 ) );

        BigDecimal frac = new BigDecimal( usecs ).divide( oneMillion_ );
        frac = frac.multiply( new BigDecimal( "2.0" ).pow( numFracBytes_ * 8 ) ); // NOI18N

        //System.out.println( "Fraction: " + frac.toString() );
        //System.out.println( "HexFraction 0x" + frac.toBigInteger().toString( 16 ) );

        String hexSeconds = secs.toString( 16 );
        while ( hexSeconds.length() < ( numSecBytes_ * 2 ) ) {
            hexSeconds = "0" + hexSeconds; // NOI18N
        }

        
        String hexUsecs = frac.toBigInteger().toString( 16 );
        while ( hexUsecs.length() < ( numFracBytes_ * 2 ) ) {
            hexUsecs = "0" + hexUsecs; // NOI18N
        }

        //System.out.println( "Temp: 0x" + hexSeconds + hexUsecs );

        BigInteger rawInteger = new BigInteger( hexSeconds + hexUsecs, 16 );

        BitSet rawBits = new BitSet( 64 );

        for ( int iii = 63; iii >= 0; --iii ) {
            rawBits.set( iii, rawInteger.testBit( iii ) );
        }

        return rawBits;

    }

    /** Retrieve the uncalibrated value for this time item, which can be a
     * Parameter or Argument, when given the EU calibrated value.
     *
     * <p>The uncalibrated output string contains an integral number of
     * microseconds since the UNIX epoch.  The input string is the UTC string
     * in the form of ISO8601 (YYYY-MM-DD HH:MM:SS.sssss).</p>
     *
     * @param euValue String containing a value of this item represented in
     * EU/calibrated form.
     *
     * @return String containing the uncalibrated value.
     *
     * @throws RuntimeException in the event that a checked exception is caught
     * by this function, with the parsing error text copied.
     *
     */

    @Override
    public String getUncalibratedFromCalibrated( String euValue ) {

        DateFormat format = new SimpleDateFormat( isoTimeFmt_ );
        format.setTimeZone( TimeZone.getTimeZone( timeZone_ ) );

        try {
            Date       javadate = format.parse( euValue );
            BigInteger msec     = BigInteger.valueOf( javadate.getTime() );
            return "0x" + msec.multiply( oneThousand_ ).toString( 16 );
        } catch ( ParseException ex ) {
            throw new RuntimeException( ex.getLocalizedMessage() );
        }

    }

    // Private Data Members

    private static final BigInteger oneThousand_ = new BigInteger( "1000" ); // NOI18N
    private static final BigDecimal oneMillion_  = new BigDecimal( "1000000" ); // NOI18N

    private final String isoTimeFmt_;
    private final String timeZone_;
    private final String epoch_;
    private final int    numSecBytes_;
    private final int    numFracBytes_;

}
