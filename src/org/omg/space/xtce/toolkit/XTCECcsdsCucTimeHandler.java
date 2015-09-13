/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.omg.space.xtce.toolkit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Date;
import java.util.TimeZone;
import org.omg.space.xtce.database.AbsoluteTimeDataType;
import org.omg.space.xtce.database.IntegerDataEncodingType;
import org.omg.space.xtce.database.ReferenceTimeType;
import org.omg.space.xtce.database.ReferenceTimeType.Epoch;

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
        isoTimeFmt_   = "yyyy-MM-dd HH:mm:ss.SSS";
        timeZone_     = "GMT";
        epoch_        = "1958-01-01";
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
        if ( ( epoch == null ) || ( epoch.equals( "TAI" ) == true ) ) {
            epoch_ = "1958-01-01";
        } else {
            epoch_ = epoch;
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

            long bits = ( numSecBytes_ + numFracBytes_ ) * 8;

            IntegerDataEncodingType enc =
                instance.getEncoding().getIntegerDataEncoding();

            Epoch epc = instance.getReferenceTime().getEpoch();

            String docEpoch = epc.getValue();
            if ( docEpoch.equals( "TAI" ) == true ) {
                docEpoch = "1958-01-01";
            }

            return ( ( enc.getSizeInBits().longValue()        == bits ) &&
                     ( enc.getEncoding().equals( "unsigned" ) == true ) &&
                     ( docEpoch.equals( epoch_ )              == true ) );

        } catch ( NullPointerException ex ) {
            // no match if a null pointer is caught
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
        hex = hex.replaceFirst( "0x", "" );
        while ( hex.length() > ( 2 * ( numSecBytes_ + numFracBytes_ ) ) ) {
            hex = hex.substring( 1 );
        }

        System.out.println( "Temp: " + hex );
        int sPos = numSecBytes_  * 8 / 4;
        int fPos = numFracBytes_ * 8 / 4;
        int tPos = sPos + fPos;

        BigInteger secs = new BigInteger( hex.substring( 0,    sPos ), 16 );
        BigInteger fsec = new BigInteger( hex.substring( sPos, tPos ), 16 );

        BigDecimal exp  = new BigDecimal( "2" ).pow( numFracBytes_ * 8 );
        BigDecimal frac = new BigDecimal( fsec ).divide( exp );

        frac = frac.multiply( oneMillion_ );

        DateFormat format = new SimpleDateFormat( isoTimeFmt_ );
        format.setTimeZone( TimeZone.getTimeZone( timeZone_ ) );

        secs = secs.multiply( oneThousand_ );

        try {

            //Date unixEpoch = format.parse( "1970-01-01 00:00:00" );
            Date thisEpoch = format.parse( epoch_ + " 00:00:00.000" );

            System.out.println( "Offset ms 0x" + Long.toString( thisEpoch.getTime(), 16) );
            secs = secs.add( new BigInteger( Long.toString( thisEpoch.getTime(), 16), 16 ) );

        } catch ( ParseException ex ) {
            System.out.println( ex.getLocalizedMessage() );
        }

        secs = secs.multiply( oneThousand_ );

        return "0x" + secs.add( frac.toBigInteger() ).toString( 16 );

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
        if ( uncalValue.startsWith( "0x" ) == true ) {
            usecs = new BigInteger( uncalValue.replaceFirst( "0x", "" ), 16 );
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
     * <p>This conversions uses only system time capabilities, and as a result
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
        if ( uncalValue.startsWith( "0x" ) == true ) {
            usecs = new BigInteger( uncalValue.replaceFirst( "0x", "" ), 16 );
        } else {
            usecs = new BigInteger( uncalValue );
        }

        BigInteger secs = usecs.divide( oneThousand_ ).divide( oneThousand_ );
        usecs = usecs.subtract( secs.multiply( oneThousand_ ).multiply( oneThousand_ ) );

        String hexSeconds = String.format( "%8s", secs.toString( 16 ) );
        String hexUsecs   = String.format( "%8s", usecs.toString( 16 ) );

        System.out.println( "Temp: " + hexSeconds + hexUsecs );
        hexSeconds = hexSeconds.replaceAll( " ", "0" );
        hexUsecs   = hexUsecs.replaceAll( " ", "0" );

        System.out.println( "Temp: " + hexSeconds + hexUsecs );
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

    private static final BigInteger oneThousand_ = new BigInteger( "1000" );
    private static final BigDecimal oneMillion_  = new BigDecimal( "1000000" );

    private final String isoTimeFmt_;
    private final String timeZone_;
    private final String epoch_;
    private final int    numSecBytes_;
    private final int    numFracBytes_;

}
