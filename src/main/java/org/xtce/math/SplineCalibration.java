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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.omg.space.xtce.CalibratorType.SplineCalibrator;
import org.omg.space.xtce.SplinePointType;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCEFunctions;

/** Class to apply a calibration specified in an XTCE document in the form of
 * a Spline Calibrator.
 *
 * @author David Overeem
 *
 */

public class SplineCalibration implements Calibration {

    /** Constructs a Calibration object based on the SplineCalibrator
     * element in the XTCE document.
     *
     * @param splineElement SplineCalibrator containing the data.
     *
     */

    public SplineCalibration( final SplineCalibrator splineElement ) {

        splineElement_ = splineElement;

    }

    /** Apply this Spline Calibrator, otherwise known as a
     * "piecewise function", to an uncalibrated value.
     *
     * The Spline Point pairs in XTCE are expected to be in sequential order
     * from lowest to highest raw value.  The first two points are mandatory
     * and subsequent points are made by adding one new point and dropping the
     * previous low point.  This assures that the evaluation is continuous.  It
     * is also assumed that they are in order from lowest raw value to highest
     * raw value.
     *
     * @param xValue Number containing the uncalibrated value.
     *
     * @return Number containing the calibrated value after the calibration.
     *
     * @throws XTCEDatabaseException when the Spline Calibrator has unsupported
     * features, the calibrated value is unbounded, or has infinite slope
     * between points.
     *
     */

    @Override
    public Number calibrate( final Number xValue ) throws XTCEDatabaseException {

        final BigInteger            order       = splineElement_.getOrder();
        final boolean               extrapolate = splineElement_.isExtrapolate();
        final List<SplinePointType> points      = splineElement_.getSplinePoint();

        // TODO: Support quadratics because I did it on the other side

        if ( ( order.intValue() > 1 ) || ( order.intValue() < 0 ) ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_encdec_orderapprox" ) + // NOI18N
                " '" + // NOI18N
                order.toString() +
                "', " + // NOI18N
                XTCEFunctions.getText( "error_encdec_onlylinear" ) ); // NOI18N
        }

        if ( points.size() < 2 ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_encdec_minpoints" ) ); // NOI18N
        }

        double rawLow  = points.get( 0 ).getRaw();
        double calLow  = points.get( 0 ).getCalibrated();
        double rawHigh = rawLow;
        double calHigh = calLow;

        for ( int iii = 1; iii < points.size(); ++iii ) {

            rawHigh = points.get( iii ).getRaw();
            calHigh = points.get( iii ).getCalibrated();

            if ( ( xValue.doubleValue() >= rawLow  ) &&
                 ( xValue.doubleValue() <= rawHigh ) ) {

                if ( order.intValue() == 0 ) {

                    // if it equals rawHigh, then take the next one as there is
                    // a discontinuity and this is how I handled it
                    if ( xValue.doubleValue() < rawHigh ) {
                        return calLow;
                    }

                } else if ( order.intValue() == 1 ) {

                    if ( rawHigh - rawLow == 0.0 ) {
                        throw new XTCEDatabaseException(
                              XTCEFunctions.getText( "error_encdec_infslope" ) +
                              " " +
                              Double.toString( calLow ) +
                              " " +
                              XTCEFunctions.getText( "general_and" ) +
                              " " +
                              Double.toString( calHigh ) );
                    }

                    double slope = ( calHigh - calLow ) / ( rawHigh - rawLow );
                    double intercept = calLow - ( slope * rawLow );
                    //double slope = ( rawHigh - rawLow ) / ( calHigh - calLow );
                    //System.out.println( "xvalue = " + new Double( xValue ).toString() +
                    //    " slope = " + new Double( slope ).toString() +
                    //    " calLow = " + new Double( calLow ).toString() +
                    //    " calHigh = " + new Double( calHigh ).toString() );
                    return ( slope * xValue.doubleValue() ) + intercept;

                }

            }

            // prepare for next iteration of spline point loop

            rawLow = rawHigh;
            calLow = calHigh;

        } // end of spline point loop

        if ( extrapolate == true ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_encdec_noextrapolate" ) ); // NOI18N
        }

        // TODO out of bounds case, add extrapolate support

        return calHigh;

    }

    /** Apply this Spline Calibrator to an already calibrated value.
     *
     * The Spline Point pairs in XTCE are expected to be in sequential order
     * from lowest to highest raw value.  The first two points are mandatory
     * and subsequent points are made by adding one new point and dropping the
     * previous low point.  This assures that the evaluation is continuous.  It
     * is also assumed that they are in order from lowest raw value to highest
     * raw value.
     *
     * @param yValue Number containing a calibrated value.
     *
     * @return Number containing the uncalibrated value after the reverse
     * calibration operation.
     *
     * @throws XTCEDatabaseException when the Spline Calibrator has unsupported
     * features, the calibrated value is unbounded, or has infinite slope
     * between points.
     *
     */

    @Override
    public Number uncalibrate( final Number yValue ) throws XTCEDatabaseException {

        final long    interpolateOrder     = splineElement_.getOrder().longValue();
        final boolean extrapolate          = splineElement_.isExtrapolate();
        final List<SplinePointType> points = splineElement_.getSplinePoint();

        if ( points.size() < 2 ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_encdec_minpoints" ) ); // NOI18N
        }

        final List<BigDecimal> calList = new ArrayList<>();
        final List<BigDecimal> rawList = new ArrayList<>();

        for ( SplinePointType point : points ) {
            calList.add( new BigDecimal( point.getCalibrated() ) );
            rawList.add( new BigDecimal( point.getRaw() ) );
        }

        BigDecimal minCalValue = calList.get( 0 );
        BigDecimal maxCalValue = calList.get( calList.size() - 1 );

        for ( BigDecimal cal : calList ) {
            if ( cal.min( minCalValue ) == cal ) {
                minCalValue = cal;
            }
            if ( cal.max( maxCalValue ) == cal ) {
                maxCalValue = cal;
            }
        }

        BigDecimal calValue = new BigDecimal( yValue.toString() );

        if ( extrapolate == false ) {

            if ( ( calValue.compareTo( minCalValue ) < 0 ) ||
                 ( calValue.compareTo( maxCalValue ) > 0 ) ) {
                throw new XTCEDatabaseException(
                    XTCEFunctions.getText( "error_encdec_noboundval" ) +
                    " '" +
                    yValue.toString() +
                    "'" );
            }

        }

        // shema requires two spline points minimum, so this should never
        // hit a null case where a spline point is not found.
        BigDecimal rawValue1 = null;
        BigDecimal rawValue2 = null;
        BigDecimal calValue1 = null;
        BigDecimal calValue2 = null;

        final Iterator<BigDecimal> calitr = calList.iterator();
        final Iterator<BigDecimal> rawitr = rawList.iterator();

        if ( calitr.hasNext() == true ) {
            calValue1 = calitr.next();
            rawValue1 = rawitr.next();
        }

        while ( calitr.hasNext() == true ) {

            if ( calValue2 != null ) {
                calValue1 = calValue2;
                rawValue1 = rawValue2;
            }

            calValue2 = calitr.next();
            rawValue2 = rawitr.next();
            //System.out.println( "Cals: cal1 = " + calValue1.toString() +
            //                    " cal2 = " + calValue2.toString() );

            if ( ( calValue1.compareTo( calValue ) <= 0 ) &&
                 ( calValue2.compareTo( calValue ) >= 0 ) ) {
                if ( yValue.equals( calValue1 ) == true ) {
                    return rawValue1;
                } else if ( yValue.equals( calValue2 ) == true ) {
                    return rawValue2;
                }
                break;
            }
        }

        if ( rawValue1 == null || rawValue2 == null ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_encdec_noextrapolate" ) +
                ", " +
                XTCEFunctions.getText( "error_encdec_neededforval" ) +
                " " +
                yValue.toString() );
        }

        //System.out.println( calValue.toString() +
        //                    " Order = " + Long.toString( interpolateOrder ) +
        //                    " y2 = " + calValue2.toString() +
        //                    " y1 = " + calValue1.toString() +
        //                    " x2 = " + rawValue2.toString() +
        //                    " x1 = " + rawValue2.toString() );

        final double y2 = calValue2.doubleValue();
        final double y1 = calValue1.doubleValue();
        final double x2 = rawValue2.doubleValue();
        final double x1 = rawValue1.doubleValue();

        if ( interpolateOrder == 0 ) {

            return new BigDecimal( ( x1 + x2 ) / 2.0 );

        } else if ( interpolateOrder == 1 ) {

            if ( x2 - x1 == 0.0 ) {
                throw new XTCEDatabaseException(
                      XTCEFunctions.getText( "error_encdec_infslope" ) +
                      " " +
                      Double.toString( y1 ) +
                      " " +
                      XTCEFunctions.getText( "general_and" ) +
                      " " +
                      Double.toString( y2 ) );
            }
            double slope = ( y2 - y1 ) / ( x2 - x1 );
            //System.out.println( "Slope = " + Double.toString( slope ) );
            if ( slope == 0.0 ) {
                // does not matter which since slope is 0
                return new BigDecimal( x1 );
            }
            double rawValue = ( calValue.doubleValue() - y1 ) / slope + x1;
            //System.out.println( "Raw = " + Double.toString( rawValue ) );
            return new BigDecimal( rawValue );

        } else { // interpolation order is not 0 or 1

            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_encdec_orderapprox" ) + // NOI18N
                " '" + // NOI18N
                Long.toString( interpolateOrder ) +
                "', " + // NOI18N
                XTCEFunctions.getText( "error_encdec_onlylinear" ) ); // NOI18N

        }

    }

    // Private Data Members

    private final SplineCalibrator splineElement_;

}

