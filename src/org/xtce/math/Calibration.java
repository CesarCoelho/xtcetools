/* Copyright 2017 David Overeem (dovereem@cox.net)
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

import org.xtce.toolkit.XTCEDatabaseException;

/** The Calibration interface provides the XTCE calibration types with a
 * common Java interface for encoding and decoding support.
 *
 * @author David Overeem
 *
 */

public abstract interface Calibration {

    /** Method to convert an uncalibrated value to a calibrated value by
     * applying the Calibrator in the XTCE document.
     *
     * @param xValue Number containing the uncalibrated value.
     *
     * @return Number containing the calibrated value.
     *
     * @throws XTCEDatabaseException thrown in the event that the operation
     * cannot be completed.  The message will contain the reason, which is
     * already internationalized.
     *
     */

    public Number calibrate( final Number xValue ) throws XTCEDatabaseException;

    /** Method to convert a calibrated value to an uncalibrated value by
     * reversing the Calibrator in the XTCE document.
     *
     * @param yValue Number containing the calibrated value.
     *
     * @return Number containing the uncalibrated value, which can be a value
     * selected as "best" representing the uncalibration when there are
     * multiple possible uncalibrated values.
     *
     * @throws XTCEDatabaseException thrown in the event that the operation
     * cannot be completed.  The message will contain the reason, which is
     * already internationalized.
     *
     */

    public Number uncalibrate( final Number yValue ) throws XTCEDatabaseException;

}
