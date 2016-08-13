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

package org.xtce.math;

import java.math.BigInteger;

/** This class supports the MILSTD-1750A encoding and decoding by allowing for
 * conversion from 16, 32, and 48 bit floating point values, in MILSTD 1750A
 * representation to and from native IEEE754 representation for use in Java.
 *
 * It would have been preferred to use a freely available library for this,
 * but the author was unable to locate one at the time of need.  Note that the
 * integer 16 bit form is not supported by this class.  One document indicates
 * that it isn't formally part of the standard (although discussed) and it
 * appears that this is just "signMagnitude" of 16 bit integer encoding size.
 * As a result, it was already supported, but by a different name.
 *
 * The logic used to implement this class is based on the logic used in the
 * CPAN Convert::MIL1750A perl module by Jared Clarke, ASRC Aerospace.  Much
 * thanks for the help there.
 *
 * @author dovereem
 *
 */

public class MilStd1750A {

    /** Constructor for base 10 or base 16 representations of a raw MILSTD
     * 1750A floating point representation.
     *
     * @param value BigInteger containing the value as it would be raw
     * encoded.
     *
     * @param encodingSize int containing the number of bits represented.
     *
     * @throws IllegalArgumentException in the event that the encoding size is
     * not one of the allowable values of 16, 32, or 48.
     *
     */

    public MilStd1750A( final BigInteger value, final int encodingSize ) {

        encodingSize_ = encodingSize;
        milStdValue_  = value.longValue();

        final long mantissa;
        final long exponent;

        switch ( encodingSize ) {

            case 16:
                mantissa = convert2C2Native( ( milStdValue_ & 0xffc0L ) >> 6, 10 );
                exponent = convert2C2Native( ( milStdValue_ & 0x003fL ), 6 );
                nativeValue_ =
                    mantissa * Math.pow( 2.0, ( exponent - 9.0 ) );
                break;

            case 32:
                mantissa = convert2C2Native( ( milStdValue_ & 0xffffff00L ) >> 8, 24 );
                exponent = convert2C2Native( ( milStdValue_ & 0x000000ffL ), 8 );
                nativeValue_ =
                    (double)mantissa * Math.pow( 2.0, ( exponent - 23.0 ) );
                break;

            case 48:
                exponent = convert2C2Native( ( milStdValue_ & 0x000000ff0000L ) >> 16, 8 );
                mantissa =
                    convert2C2Native( ( ( milStdValue_ & 0x00000000ffffL ) ) +
                                      ( ( milStdValue_ & 0xffffff000000L ) >> 8 ), 40 );
                nativeValue_ =
                    (double)mantissa * Math.pow( 2.0, ( exponent - 39.0 ) );
                break;

            default: {
                String msg = "Invalid MILSTD-1750A encoding size of " +
                    Integer.toString( encodingSize_ );
                throw new IllegalArgumentException( msg );
            }

        }

    }

    /** Constructor for native IEEE754 representations of a raw MILSTD
     * 1750A floating point number.
     *
     * @param value double containing the IEEE754 native representation of a
     * value to be encoded in MILSTD 1750A.
     *
     * @param encodingSize int containing the number of bits to be represented.
     *
     * @throws IllegalArgumentException in the event that the encoding size is
     * not one of the allowable values of 16, 32, or 48.
     *
     */

    public MilStd1750A( final double value, final int encodingSize ) {

        encodingSize_ = encodingSize;
        nativeValue_  = value;

        long mantissa;
        long exponent;

        switch ( encodingSize_ ) {

            case 16:
                exponent = (long)( Math.ceil(
                           Math.log( Math.abs( value ) ) / Math.log( 2.0 ) ) );
                mantissa = (long)
                           ( value / ( Math.pow( 2.0, ( exponent - 9 ) ) ) );
                if ( mantissa == 0x200L ) { // 10th bit
                    mantissa = mantissa / 2;
                    exponent++;
                }
                milStdValue_ = ( exponent & 0x3f ) +
                               ( ( mantissa & 0x3ff ) << 6 );
                break;

            case 32:
                exponent = (long)( Math.ceil(
                           Math.log( Math.abs( value ) ) / Math.log( 2.0 ) ) );
                mantissa = (long)
                           ( value / ( Math.pow( 2.0, ( exponent - 23 ) ) ) );
                if ( mantissa == 0x800000L ) { // 24th bit
                    mantissa = mantissa / 2;
                    exponent++;
                }
                milStdValue_ = ( exponent & 0xff ) +
                               ( ( mantissa & 0xffffff ) << 8 );
                break;

            case 48:
                exponent = (long)( Math.ceil(
                           Math.log( Math.abs( value ) ) / Math.log( 2.0 ) ) );
                mantissa = (long)
                           ( value / ( Math.pow( 2.0, ( exponent - 39 ) ) ) );
                if ( mantissa == 0x8000000000L ) { // 40th bit
                    mantissa = mantissa / 2;
                    exponent++;
                }
                milStdValue_ = ( ( mantissa & 0xffffff0000L ) << 8  )  +
                               ( ( exponent & 0xffL ) << 16 ) +
                               ( mantissa & 0xffffL );
                break;

            default: {
                String msg = "Invalid MILSTD-1750A encoding size of " +
                    Integer.toString( encodingSize_ );
                throw new IllegalArgumentException( msg );
            }

        }

    }

    /** Retrieves this MILSTD modeled value as an IEEE754 double, which is the
     * native representation for Java.
     *
     * @return double containing the value represented.
     *
     */

    public double toIeeeDouble() {
        return nativeValue_;
    }

    /** Retrieves this MILSTD modeled value as a raw binary value, in the form
     * of a long integer representation for binary encoding.
     *
     * @return long containing the MILSTD value as encoded to raw bits.
     *
     */

    public long toRawBits() {
        return milStdValue_;
    }

    /** Retrieves this MILSTD modeled value as a string printable floating
     * point value.
     *
     * @return String containing the floating point value representation.
     *
     */

    @Override
    public String toString() {
        return Double.toString( nativeValue_ );
    }

    /** Private method to convert the twos complement value of the MILSTD
     * encoding into a native sign magnitude value for calculations.
     *
     * @param value long containing the binary twos complement value.
     *
     * @param bits int containing the number of bits.
     *
     * @return long containing the value as it would be native to basic Java
     * math in sign magnitude form.
     *
     */

    private long convert2C2Native( long value, int bits ) {

        final BigInteger halfValue = BigInteger.valueOf( 2 ).pow( bits - 1 );
        final BigInteger fullValue = BigInteger.valueOf( 2 ).pow( bits );

        if ( value >= halfValue.longValue() ) {
            return value - fullValue.longValue();
        } else {
            return value;
        }

    }

    /// Private Data Members

    private final double nativeValue_;
    private final int    encodingSize_;
    private final long   milStdValue_;

}
