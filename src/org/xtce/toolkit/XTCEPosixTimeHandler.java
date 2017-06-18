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

package org.xtce.toolkit;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.TimeZone;
import org.omg.space.xtce.AbsoluteTimeDataType;
import org.omg.space.xtce.IntegerDataEncodingType;

/** This class implements UNIX POSIX time to support AbsoluteTimeDataType
 * elements in an XTCE document.
 *
 * <p>This class is applicable to AbsoluteTimeType XTCE specifications when
 * three conditions are met by the XML document:</p>
 *
 * <ul>
 * <li>Encoding size in bits of the type element is equal to 64.</li>
 * <li>Encoding is Unsigned Integer</li>
 * <li>Epoch of the type element is equal to "1970-01-01" with no specific
 * time of the day specified (midnight assumed).</li>
 * </ul>
 *
 * <p>The EU/Calibrated value is implemented to be a UTC string, with no leap
 * second correction.  It is based on the internal system clock of this host.
 * The Uncalibrated value is the microseconds since midnight 1970-01-01 date.
 * The raw value is two POSIX unsigned integers, 32 bits of seconds, followed
 * by 32 bits of microseconds.  The BitSet of the raw encoded value will be
 * equal to that of a "struct timeval" in the C programming language foundation
 * of many computing platforms.</p>
 *
 * <p>Although the POSIX specification carries microseconds, the Java
 * implementation here uses the basic libraries and will only act as far as
 * the milliseconds.</p>
 *
 * @author dovereem
 *
 */

public class XTCEPosixTimeHandler implements XTCEAbsoluteTimeType {

    /** Default constructor to initialize a handler with the default time
     * format and time zone.
     *
     * <p>The default time output format is UTC ISO8601 text.  This is in the
     * form of YYYY-MM-dd HH:MM:ss.SSS</p>
     *
     */

    public XTCEPosixTimeHandler() {
        isoTimeFmt_  = "yyyy-MM-dd HH:mm:ss.SSS"; // NOI18N
        timeZone_    = "GMT"; // NOI18N
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
     */

    public XTCEPosixTimeHandler( String timeFormat, String timeZone ) {
        isoTimeFmt_  = timeFormat;
        timeZone_    = timeZone;
    }

    /** Method to determine if an XTCE AbsoluteTimeType, either Parameter or
     * Argument, is handled by this implementation of the interface.
     *
     * <p>This method returns true when three conditions are met by the XML
     * that is represented by the JAXB AbsoluteTimeDataType object instance
     * passed by the caller:</p>
     *
     * <ul>
     * <li>Encoding size in bits of the type element is equal to 64.</li>
     * <li>Encoding is Unsigned Integer</li>
     * <li>Epoch of the type element is equal to "1970-01-01" with no specific
     * time of the day specified (midnight assumed).</li>
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

            return ( ( enc.getSizeInBits().longValue()        == 64   ) &&
                     ( enc.getEncoding().equals( "unsigned" ) == true ) && // NOI18N
                     ( docEpoch.equals( "1970-01-01" )        == true ) ); // NOI18N

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
     * <p>The raw input value is a BitSet of 64 bits in the form of two
     * 32 bit unsigned integers.  The first is the number of seconds since
     * the UNIX epoch of 1970-01-01 and the seconds in the number of
     * microseconds within the second.  The output String will be an integral
     * representation of the number of microseconds since 1970-01-01.</p>
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

        String hex = XTCEFunctions.bitSetToHex( rawValue, 8 );

        hex = hex.replaceFirst( "0x", "" ); // NOI18N

        BigInteger secs = new BigInteger( hex.substring( 0,  8 ), 16 );
        BigInteger usec = new BigInteger( hex.substring( 8, 16 ), 16 );

        secs = secs.multiply( oneThousand_ ).multiply( oneThousand_ );

        return "0x" + secs.add( usec ).toString( 16 ); // NOI18N

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

        BigInteger usecs = null;
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
     * <p>The raw output value is a BitSet of 64 bits in the form of two
     * 32 bit unsigned integers.  The first is the number of seconds since
     * the UNIX epoch of 1970-01-01 and the seconds in the number of
     * microseconds within the second.  The input String will be an integral
     * representation of the number of microseconds since 1970-01-01.</p>
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

        BigInteger usecs = null;
        if ( uncalValue.startsWith( "0x" ) == true ) { // NOI18N
            usecs = new BigInteger( uncalValue.replaceFirst( "0x", "" ), 16 ); // NOI18N
        } else {
            usecs = new BigInteger( uncalValue );
        }

        BigInteger secs = usecs.divide( oneThousand_ ).divide( oneThousand_ );
        usecs = usecs.subtract( secs.multiply( oneThousand_ ).multiply( oneThousand_ ) );

        String hexSeconds = String.format( "%8s", secs.toString( 16 ) ); // NOI18N
        String hexUsecs   = String.format( "%8s", usecs.toString( 16 ) ); // NOI18N

        hexSeconds = hexSeconds.replaceAll( " ", "0" ); // NOI18N
        hexUsecs   = hexUsecs.replaceAll( " ", "0" ); // NOI18N

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
            return "0x" + msec.multiply( oneThousand_ ).toString( 16 ); // NOI18N
        } catch ( ParseException ex ) {
            throw new RuntimeException( ex.getLocalizedMessage() );
        }

    }

    // Private Data Members

    private static final BigInteger oneThousand_ = new BigInteger( "1000" ); // NOI18N

    private final String isoTimeFmt_;
    private final String timeZone_;

}
