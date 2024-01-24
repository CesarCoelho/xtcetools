/* Copyright 2017 David Overeem (dovereem@startmail.com)
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
import java.util.HashMap;
import java.util.List;
import org.omg.space.xtce.PolynomialType;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCEFunctions;
import org.xtce.toolkit.XTCETypedObject.RawType;
import org.xtce.toolkit.XTCEValidRange;

/** Class to apply a calibration specified in an XTCE document in the form of
 * a Polynomial Calibrator.
 *
 * @author David Overeem
 *
 */

public class PolynomialCalibration implements Calibration {

    /** Constructs a Calibration object based on the PolynomialType
     * element in the XTCE document.
     *
     * @param polynomialElement PolynomialType containing the data.
     *
     * @param validRange XTCEValidRange object associated with the typed object
     * so that a "best" uncalibrated value can be deduced during the usage of
     * the uncalibrate() method.
     *
     * @param rawSizeInBits int containing the raw encoding size in bits to
     * help deduce a "best" uncalibrated value when using the uncalibrate()
     * method.
     *
     * @param rawTypeName RawType enumeration containing the raw encoding
     * type to help deduce a "best" uncalibrated value when using the
     * uncalibrate() method.
     *
     */

    public PolynomialCalibration( final PolynomialType polynomialElement,
                                  final XTCEValidRange validRange,
                                  final int            rawSizeInBits,
                                  final RawType        rawTypeName ) {

        polynomialElement_ = polynomialElement;
        validRange_        = validRange;
        rawSizeInBits_     = rawSizeInBits;
        rawTypeName_       = rawTypeName;

    }

    /** Apply this Polynomial Calibrator to an uncalibrated value.
     *
     * This method doesn't care which order the coefficient/exponent terms
     * appears because of the commutative property of addition, so any
     * sequence of terms may be specified in XTCE and this function will apply
     * them as they are specified.
     *
     * This function is not concerned with the encoding type (integer or float)
     * when performing the calculation.  The calculation is always done in the
     * floating point space and if the engineering type is integer, then it
     * will be later rounded back.  This could be a point of controversy.
     *
     * @param xValue Number containing the uncalibrated value.
     *
     * @return Number containing the calibrated value after the calibration.
     *
     * @throws XTCEDatabaseException in the event that the PolynomialCalibrator
     * does not have any terms, which would also be invalid in the XTCE
     * document when the schema is checked against the document.
     *
     */

    @Override
    public Number calibrate( final Number xValue ) throws XTCEDatabaseException {

        final List<PolynomialType.Term> terms = polynomialElement_.getTerm();

        if ( terms.isEmpty() == true ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_encdec_nopolyterms" ) ); // NOI18N
        }

        double yValue = 0.0;

        for ( PolynomialType.Term term : terms ) {

            final double coeff    = term.getCoefficient();
            final double exponent = term.getExponent().doubleValue();
            final double powTerm  = Math.pow( xValue.doubleValue(), exponent );

            yValue += coeff * powTerm;

        }

        return yValue;

    }

    /** Apply this Polynomial Calibrator to an already calibrated value.
     *
     * This method doesn't care which order the coefficient/exponent terms
     * appears because of the commutative property of addition, so any
     * sequence of terms may be specified in XTCE and this function will apply
     * them as they are specified.
     *
     * This function is not concerned with the encoding type (integer or float)
     * when performing the calculation.  The calculation is always done in the
     * floating point space and if the engineering type is integer, then it
     * will be later rounded back.  This could be a point of controversy.
     *
     * @param yValue Number containing a calibrated value.
     *
     * @return Number containing the uncalibrated value after the reverse
     * calibration operation.
     *
     * @throws XTCEDatabaseException in the event that the PolynomialCalibrator
     * does not have any terms, which would also be invalid in the XTCE
     * document when the schema is checked against the document.  The exception
     * can also be thrown in the event that there are no real roots for a
     * quadratic uncalibrate or the polynomial has a higher order than a
     * quadratic, which is not currently supported by this toolkit.
     *
     */

    @Override
    public Number uncalibrate( final Number yValue ) throws XTCEDatabaseException {

        final HashMap<BigInteger, BigDecimal> terms = new HashMap<>();

        final List<PolynomialType.Term> xtceTerms = polynomialElement_.getTerm();

        if ( xtceTerms.isEmpty() == true ) {
            throw new XTCEDatabaseException( XTCEFunctions.getText( "error_encdec_nopolyterms" ) ); // NOI18N
        }

        long maxExponent = 0;

        for ( PolynomialType.Term term : xtceTerms ) {
            if ( term.getCoefficient() != 0.0 ) {
                terms.put( term.getExponent(), new BigDecimal( term.getCoefficient() ) );
                if ( term.getExponent().longValue() > maxExponent ) {
                    maxExponent = term.getExponent().longValue();
                }
            }
        }

        if ( maxExponent <= 1 ) {

            double value = yValue.doubleValue();

            if ( terms.containsKey( BigInteger.ZERO ) == true ) {
                value -= terms.get( BigInteger.ZERO ).doubleValue();
            }

            if ( terms.containsKey( BigInteger.ONE ) == true ) {
                value /= terms.get( BigInteger.ONE ).doubleValue();
            }

            return value;

        } else if ( maxExponent == 2 ) {

            final BigInteger inttwo = new BigInteger( "2" ); // NOI18N
            // evaluate b^2 -4ac to determine if roots exist
            double aaa = 0.0;
            double bbb = 0.0;
            double ccc = -1.0 * yValue.doubleValue();

            if ( terms.containsKey( BigInteger.ZERO ) == true ) {
                ccc += terms.get( BigInteger.ZERO ).doubleValue();
            }

            if ( terms.containsKey( BigInteger.ONE ) == true ) {
                bbb = terms.get( BigInteger.ONE ).doubleValue();
            }

            if ( terms.containsKey( inttwo ) == true ) {
                aaa = terms.get( inttwo ).doubleValue();
            }

            double discriminant = Math.pow( bbb, 2 ) - ( 4.0 * aaa * ccc );

            if ( discriminant < 0 ) {

                throw new XTCEDatabaseException(
                    XTCEFunctions.getText( "error_encdec_norealroots" ) + // NOI18N
                    " '" + // NOI18N
                    yValue.toString() +
                    "'" ); // NOI18N

            } else {

                double posroot = Math.sqrt( discriminant );
                double root1   = ( ( bbb * -1.0 ) - posroot ) / ( 2.0 * aaa );
                double root2   = ( ( bbb * -1.0 ) + posroot ) / ( 2.0 * aaa );
                //System.out.println( "Root1 = " + Double.toString( root1 ) + " Root2 = " + Double.toString( root2 ) );

                return findBestRoot( root1, root2 );

            }

        } else {

            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_encdec_maxexponent" ) + // NOI18N
                " (" + // NOI18N
                Long.toString( maxExponent ) +
                ")" ); // NOI18N

        }

    }

    private double findBestRoot( double root1, double root2 )
        throws XTCEDatabaseException {

        // function is never called if BOTH are invalid since we checked the
        // discriminant earlier.

        if ( ( true  == Double.isNaN( root1 )    ) ||
             ( root1 == Double.POSITIVE_INFINITY ) ||
             ( root1 == Double.NEGATIVE_INFINITY ) ) {
            root1 = root2;
        }

        if ( ( true  == Double.isNaN( root2 )    ) ||
             ( root2 == Double.POSITIVE_INFINITY ) ||
             ( root2 == Double.NEGATIVE_INFINITY ) ) {
            root2 = root1;
        }

        if ( rawTypeName_ == RawType.unsigned ) {
            // TODO worry about inclusive versus exclusive
            long minValue = 0;
            long maxValue = (long)Math.pow( 2, rawSizeInBits_  ) - 1;
            if ( ( validRange_.isValidRangeApplied()  == true  ) &&
                 ( validRange_.isLowValueCalibrated() == false ) ) {
                minValue = Math.round( Double.parseDouble( validRange_.getLowValue() ) );
            }
            if ( ( validRange_.isValidRangeApplied()   == true  ) &&
                 ( validRange_.isHighValueCalibrated() == false ) ) {
                maxValue = Math.round( Double.parseDouble( validRange_.getHighValue() ) );
            }
            if ( ( root1 >= minValue ) && ( root1 <= maxValue ) ) {
                return root1;
            } else if ( ( root2 >= minValue ) && ( root2 <= maxValue ) ) {
                return root2;
            } else {
                throw new XTCEDatabaseException(
                      XTCEFunctions.getText( "error_encdec_polyroots" ) + // NOI18N
                      Double.toString( root1 ) +
                      " " + // NOI18N
                      XTCEFunctions.getText( "general_and" ) + // NOI18N
                      " " + // NOI18N
                      Double.toString( root2 ) +
                      ", " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_notinrange" ) + // NOI18N
                      " ( " + // NOI18N
                      Long.toString( minValue ) +
                      ", " + // NOI18N
                      Long.toString( maxValue ) +
                      " ) " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_forrawtype" ) ); // NOI18N
            }
        } else if ( ( rawTypeName_ == RawType.signMagnitude  ) ||
                    ( rawTypeName_ == RawType.twosComplement ) ||
                    ( rawTypeName_ == RawType.onesComplement ) ) {
            // TODO worry about inclusive versus exclusive
            long minValue = -1 * (long)Math.pow( 2, ( rawSizeInBits_ - 1 ) );
            long maxValue = (long)Math.pow( 2, ( rawSizeInBits_ - 1 ) ) - 1;
            if ( ( validRange_.isValidRangeApplied()  == true  ) &&
                 ( validRange_.isLowValueCalibrated() == false ) ) {
                minValue = Math.round( Double.parseDouble( validRange_.getLowValue() ) );
            }
            if ( ( validRange_.isValidRangeApplied()   == true  ) &&
                 ( validRange_.isHighValueCalibrated() == false ) ) {
                maxValue = Math.round( Double.parseDouble( validRange_.getHighValue() ) );
            }
            if ( ( root1 >= minValue ) && ( root1 <= maxValue ) ) {
                return root1;
            } else if ( ( root2 >= minValue ) && ( root2 <= maxValue ) ) {
                return root2;
            } else {
                throw new XTCEDatabaseException(
                      XTCEFunctions.getText( "error_encdec_polyroots" ) + // NOI18N
                      Double.toString( root1 ) +
                      " " + // NOI18N
                      XTCEFunctions.getText( "general_and" ) + // NOI18N
                      " " + // NOI18N
                      Double.toString( root2 ) +
                      ", " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_notinrange" ) + // NOI18N
                      " ( " + // NOI18N
                      Long.toString( minValue ) +
                      ", " + // NOI18N
                      Long.toString( maxValue ) +
                      " ) " + // NOI18N
                      XTCEFunctions.getText( "error_encdec_forrawtype" ) ); // NOI18N
            }
        }

        return root1;

    }

    // Private Data Members

    private final PolynomialType polynomialElement_;
    private final XTCEValidRange validRange_;
    private final int            rawSizeInBits_;
    private final RawType        rawTypeName_;

}
