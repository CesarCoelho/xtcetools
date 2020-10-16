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

import java.util.BitSet;
import org.omg.space.xtce.AbsoluteTimeDataType;

/** This interface permits the user/program to implement the handling of a
 * specific form of an AbsoluteTimeType in the toolkit.
 *
 * <p>An example of a class that implements this interface can be found in the
 * toolkit as XTCEPosixTimeHandler.  Another example is the class
 * XTCECcsdsCucTimeHandler.  These two types are automatically included,
 * although user registed time handlers are always examined first, so they can
 * be overridden.</p>
 *
 * <p>This interface requires at least 5 implementation methods.  These are
 * described in the 5 public methods that follow.</p>
 *
 * <p>At least one constructor will be required in the implementing classes
 * because they get registered on the XTCEFunctions class for use throughout
 * the toolkit as needed.</p>
 *
 * @author dovereem
 *
 */

public interface XTCEAbsoluteTimeType {

    /** Method to determine if an XTCE AbsoluteTimeType, either Parameter or
     * Argument, is handled by this implementation of the interface.
     *
     * <p>The implementer should endeavor to have this function not throw an
     * exception.  If one is thrown, an error message will be logged by the
     * encode/decode functions of the XTCEItemValue object where handlers that
     * implement this class are used.  A throw will short-circuit tests for
     * other applicable handlers.</p>
     *
     * @param instance AbsoluteTimeDataType object from the JAXB representation
     * of the XTCE XML document.
     *
     * @return boolean indicating if this handler supports/implements handling
     * for the user provided Absolute Time Type, as specified in the XML.
     *
     */

    public boolean isApplicable( AbsoluteTimeDataType instance );

    /** Retrieve the uncalibrated value of this time item, which can be a
     * Parameter or Argument, when given the raw binary value.
     *
     * <p>The raw value is provided as a Java BitSet to account for an
     * arbitrary size of the raw value binary.  The output of this function
     * takes into account the encoding type to interpret the raw binary in the
     * proper type and alignment.</p>
     *
     * <p>A common implementation for this function would be to return the
     * microseconds since the UNIX epoch, although that is not specifically
     * required.</p>
     *
     * <p>This method should endeavor to not throw an exception, which is not
     * hard because the input BitSet is a well constrained value.</p>
     *
     * @param rawValue BitSet containing the raw binary value that would
     * be encoded on the wire or bitfield.  The raw binary is always expected
     * to be in the order read from the stream.
     *
     * @return String containing the proper uncalibrated representation of the
     * raw encoded value provided by the caller.
     *
     */

    public String getUncalibratedFromRaw( BitSet rawValue );

    /** Retrieve the EU calibrated value of this time item, which can be a
     * Parameter or Argument, when given the uncalibrated value.
     *
     * <p>A common implementation for this function would be to return the
     * UTC or GPS ISO8601 string, although that is not specifically required.
     * </p>
     *
     * <p>This function may throw unchecked exceptions that inherit from
     * java.lang.Exception.  Exceptions will be caught by the XTCEItemValue
     * class that uses this implementation.  In the event that occurs, then
     * the calling code will be provided with an error message regarding an
     * invalid value for the specific parameter that was attempted.</p>
     *
     * @param uncalValue String containing the uncalibrated value that is
     * derived from the encoded value on the wire or bitfield.
     *
     * @return String containing the proper EU calibrated representation of the
     * uncalibrated value provided by the caller.
     *
     */

    public String getCalibratedFromUncalibrated( String uncalValue );

    /** Retrieve the raw binary bits for encoding of this time item, which can
     * be a Parameter or Argument, when given the uncalibrated value.
     *
     * <p>TIP: An implementer may be interested to use the BitSet conversion
     * function in the static helper XTCEFunctions class.</p>
     *
     * <p>This function may throw unchecked exceptions that inherit from
     * java.lang.Exception.  Exceptions will be caught by the XTCEItemValue
     * class that uses this implementation.  In the event that occurs, then
     * the calling code will be provided with an error message regarding an
     * invalid value for the specific parameter that was attempted.</p>
     *
     * @param uncalValue String containing the uncalibrated representation of
     * the value.
     *
     * @return BitSet containing the raw bits.
     *
     */

    public BitSet getRawFromUncalibrated( String uncalValue );

    /** Retrieve the uncalibrated value for this time item, which can be a
     * Parameter or Argument, when given the EU calibrated value.
     *
     * <p>This function may throw unchecked exceptions that inherit from
     * java.lang.Exception.  Exceptions will be caught by the XTCEItemValue
     * class that uses this implementation.  In the event that occurs, then
     * the calling code will be provided with an error message regarding an
     * invalid value for the specific parameter that was attempted.</p>
     *
     * @param euValue String containing a value of this item represented in
     * EU/calibrated form.
     *
     * @return String containing the uncalibrated value.
     *
     */

    public String getUncalibratedFromCalibrated( String euValue );

}
