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
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import org.omg.space.xtce.CalibratorType.MathOperationCalibrator;
import org.omg.space.xtce.ParameterInstanceRefType;
import org.omg.space.xtce.ParameterRefType;
import org.xtce.toolkit.XTCEDatabaseException;
import org.xtce.toolkit.XTCEFunctions;

/** Class to apply a calibration specified in an XTCE document in the form of
 * a Math Operation Calibrator.
 *
 * @author David Overeem
 *
 */

public class MathOperationCalibration implements Calibration {

    /** Constructs a Calibration object based on the MathOperationCalibrator
     * element in the XTCE document.
     *
     * @param element MathOperationCalibrator containing the data.
     *
     */

    public MathOperationCalibration( final MathOperationCalibrator element ) {

        mathElement_ = element;

    }

    /** Calibration of values using MathOperationCalibrator has some limited
     * support for common numeric values and operators.
     *
     * At present, the ParameterInstanceRefOperand is not yet supported.  Some
     * operators are not yet implemented, but likely will be supported in the
     * future.  The following list contains the unsupported operators that are
     * specified in XTCE 1.1:
     *
     * <ul>
     * <li>case "&": // really for integer values only?</li>
     * <li>case "|": // really for integer values only?</li>
     * <li>case "<<": // really for integer values only?</li>
     * <li>case ">>": // really for integer values only?</li>
     * <li>case ">>>": // really for integer values only?</li>
     * <li>case "x!": // not in the basic java.lang.Math</li>
     * <li>case "atanh": // not in the basic java.lang.Math</li>
     * <li>case "asinh": // not in the basic java.lang.Math</li>
     * <li>case "acosh": // not in the basic java.lang.Math</li>
     * </ul>
     *
     * Future implementations will utilize an open source library that
     * implements all the needed functions specifically for BigDecimal, to
     * avoid conversion and precision issues.
     *
     * @param xValue Number containing the uncalibrated value.
     *
     * @return Number containing the calibrated value after the calibration.
     *
     * @throws XTCEDatabaseException thrown in the event that the math operator
     * is not yet supported, the stack is exhausted, which would occur for an
     * invalid expression, or for an arithmetic error, such as a division by
     * zero.
     *
     */

    @Override
    public Number calibrate( final Number xValue ) throws XTCEDatabaseException {

        List<Object> terms =
            mathElement_.getValueOperandOrThisParameterOperandOrParameterInstanceRefOperand();

        Stack<BigDecimal> stack = new Stack<>();

        // general stack math loop/algorithm.  pretty easy when compared to
        // doing it with the familiar infix expressions.

        try {

            for ( Object term : terms ) {

                // XTCE element ValueOperand
                if ( term instanceof Double ) {

                    stack.push( new BigDecimal( (Double)term ) );

                // XTCE element Operator
                } else if ( term instanceof String ) {

                    applyOperator( (String)term, stack );

                // XTCE element ParameterInstanceRefOperand (add later)
                } else if ( term instanceof ParameterInstanceRefType ) {

                    throw new XTCEDatabaseException(
                        XTCEFunctions.getText( "error_encdec_unsupported_math_element" ) + // NOI18N
                        " " + // NOI18N
                        term.getClass().getSimpleName() );

                // XTCE element ThisParameterOperand
                } else if ( term instanceof Object ) {

                    stack.push( new BigDecimal( xValue.toString() ) );

                //} else { // invalid element - unreachable because of Object

                //    throw new XTCEDatabaseException(
                //        XTCEFunctions.getText( "error_encdec_unsupported_math_element" ) + // NOI18N
                //        " " + // NOI18N
                //        term.getClass().getSimpleName() );

                }

            }

        } catch ( EmptyStackException ex ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_encdec_invalid_postfix_expression" ) + // NOI18N
                " 0" ); // NOI18N
        } catch ( ArithmeticException ex ) {
            throw new XTCEDatabaseException( ex.getLocalizedMessage() );
        }

        if ( stack.size() != 1 ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_encdec_invalid_postfix_expression" ) + // NOI18N
                " " + // NOI18N
                Integer.toString( stack.size() ) );
        }

        return stack.pop();

    }

    /** Reverse calibration of values is not yet supported for
     * MathOperationCalibrator.
     *
     * @param yValue Number containing the calibrated value.
     *
     * @return Number containing the uncalibrated value after the reverse
     * calibration operation.
     *
     * @throws XTCEDatabaseException any time this is called.
     *
     */

    @Override
    public Number uncalibrate( final Number yValue ) throws XTCEDatabaseException {

        throw new XTCEDatabaseException(
            XTCEFunctions.getText( "error_encdec_unsupported_calibrator" ) ); // NOI18N

    }

    /** Method to create a simple space delimited string that represents the
     * postfix expression from the XTCE MathOperationCalibrator that this
     * object instance represents.
     *
     * @return String containing a space delimited expression of operands and
     * operators in postfix notation.
     *
     */

    @Override
    public String toString() {

        List<Object> terms =
            mathElement_.getValueOperandOrThisParameterOperandOrParameterInstanceRefOperand();

        StringBuilder sb = new StringBuilder();

        for ( Object term : terms ) {

            // XTCE element ValueOperand
            if ( term instanceof Double ) {

                sb.append( term.toString() );
                sb.append( " " ); // NOI18N

            // XTCE element Operator
            } else if ( term instanceof String ) {

                sb.append( term.toString() );
                sb.append( " " ); // NOI18N

            // XTCE element ParameterInstanceRefOperand (add later)
            } else if ( term instanceof ParameterInstanceRefType ) {

                String pref  = ((ParameterInstanceRefType)term).getParameterRef();
                String pname = XTCEFunctions.getNameFromPathReferenceString( pref );
                sb.append( pname );
                if ( ((ParameterInstanceRefType)term).isUseCalibratedValue() == true ) {
                    sb.append( ".cal " ); // NOI18N
                } else {
                    sb.append( ".uncal " ); // NOI18N
                }

            // XTCE element ThisParameterOperand
            } else if ( term instanceof Object ) {

                sb.append( "uncal " ); // NOI18N

            //} else { // invalid element - unreachable because of Object

            //    throw new XTCEDatabaseException(
            //        XTCEFunctions.getText( "error_encdec_unsupported_math_element" ) + // NOI18N
            //        " " + // NOI18N
            //        term.getClass().getSimpleName() );

            }

        }

        if ( sb.length() > 0 ) {
            sb.deleteCharAt( sb.length() - 1 );
        }

        return sb.toString();

    }

    /** Method to create a simple space delimited string that represents the
     * infix expression from the XTCE MathOperationCalibrator that this
     * object instance represents.
     *
     * @return String containing a space delimited expression of operands and
     * operators in infix notation.
     *
     * @throws XTCEDatabaseException thrown in the event that the math operator
     * is not yet supported or the stack is exhausted, which would occur for an
     * invalid expression.
     *
     */

    public String toInfixString() throws XTCEDatabaseException {

        List<Object> terms =
            mathElement_.getValueOperandOrThisParameterOperandOrParameterInstanceRefOperand();

        Stack<String> stack = new Stack<>();

        // general stack math loop/algorithm.  pretty easy when compared to
        // doing it with the familiar infix expressions.

        try {

            for ( Object term : terms ) {

                // XTCE element ValueOperand
                if ( term instanceof Double ) {

                    stack.push( term.toString() );

                // XTCE element ParameterInstanceRefOperand (add later)
                } else if ( term instanceof ParameterInstanceRefType ) {

                    String pref  = ((ParameterRefType)term).getParameterRef();
                    String pname = XTCEFunctions.getNameFromPathReferenceString( pref );

                    if ( ((ParameterInstanceRefType)term).isUseCalibratedValue() == true ) {
                        stack.push( pname + ".cal" ); // NOI18N
                    } else {
                        stack.push( pname + ".uncal" ); // NOI18N
                    }

                // XTCE element Operator
                } else if ( term instanceof String ) {

                    applyToInfixString( (String)term, stack );

                // XTCE element ThisParameterOperand
                } else if ( term instanceof Object ) {

                    stack.push( "uncal" ); // NOI18N

                }

            }

        } catch ( EmptyStackException ex ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_encdec_invalid_postfix_expression" ) + // NOI18N
                " 0" ); // NOI18N
        }

        if ( stack.size() != 1 ) {
            throw new XTCEDatabaseException(
                XTCEFunctions.getText( "error_encdec_invalid_postfix_expression" ) + // NOI18N
                " " + // NOI18N
                Integer.toString( stack.size() ) );
        }

        return stack.pop().trim();

    }

    /** Apply one of the operators from the MathOperation/Operator element
     * where some operations use two values from the stack and some operations
     * that resemble functions use a single value from the stack.
     *
     * @param op String containing the operator character
     *
     * @param stack Stack object of BigDecimals being used in the calculation
     *
     * @throws XTCEDatabaseException thrown in the event that the math operator
     * is not yet supported.
     *
     * @throws EmptyStackException in the event that the stack does not have
     * sufficient values for the operator that is to be applied.
     *
     * @throws ArithmeticException in the event that division by zero occurs.
     *
     */

    private void applyOperator( final String            op,
                                final Stack<BigDecimal> stack )
        throws XTCEDatabaseException, EmptyStackException, ArithmeticException {

        BigDecimal aaa;
        BigDecimal bbb;

        switch ( op ) {

            case "*": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                stack.push( bbb.multiply( aaa ) );
                break;

            case "+": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                stack.push( bbb.add( aaa ) );
                break;

            case "-": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                stack.push( bbb.subtract( aaa ) );
                break;

            case "/": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                if ( aaa.doubleValue() == 0.0 ) {
                    throw new ArithmeticException( XTCEFunctions.getText( "error_encdec_divide_zero" ) ); // NOI18N
                }
                stack.push( new BigDecimal( bbb.doubleValue() / aaa.doubleValue() ) );
                break;

            case "%": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                stack.push( bbb.remainder( aaa ) ); // can be negative?!
                break;

            case "^": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                stack.push( new BigDecimal( Math.pow( bbb.doubleValue(),
                                                      aaa.doubleValue() ) ) );
                break;
            case "y^x": // inverse of the power operator above? // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                stack.push( new BigDecimal( Math.pow( aaa.doubleValue(),
                                                      bbb.doubleValue() ) ) );
                break;

            case "ln": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.log( aaa.doubleValue() ) ) );
                break;

            case "log": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.log10( aaa.doubleValue() ) ) );
                break;

            case "e^x": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.exp( aaa.doubleValue() ) ) );
                break;

            case "1/x": // NOI18N
                aaa = stack.pop();
                stack.push( BigDecimal.ONE.divide( aaa ) );
                break;

            case "swap": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                stack.push( aaa );
                stack.push( bbb );
                break;

            case "abs": // NOI18N
                aaa = stack.pop();
                stack.push( aaa.abs() );
                break;

            case "==": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                if ( bbb.compareTo( aaa ) == 0 ) {
                    stack.push( BigDecimal.ONE );
                } else {
                    stack.push( BigDecimal.ZERO );
                }
                break;

            case "!=": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                if ( bbb.compareTo( aaa ) == 0 ) {
                    stack.push( BigDecimal.ZERO );
                } else {
                    stack.push( BigDecimal.ONE );
                }
                break;

            case "<": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                if ( bbb.compareTo( aaa ) < 0 ) {
                    stack.push( BigDecimal.ONE );
                } else {
                    stack.push( BigDecimal.ZERO );
                }
                break;

            case ">": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                if ( bbb.compareTo( aaa ) > 0 ) {
                    stack.push( BigDecimal.ONE );
                } else {
                    stack.push( BigDecimal.ZERO );
                }
                break;

            case "<=": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                if ( bbb.compareTo( aaa ) <= 0 ) {
                    stack.push( BigDecimal.ONE );
                } else {
                    stack.push( BigDecimal.ZERO );
                }
                break;

            case ">=": // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                if ( bbb.compareTo( aaa ) >= 0 ) {
                    stack.push( BigDecimal.ONE );
                } else {
                    stack.push( BigDecimal.ZERO );
                }
                break;

            case "tan": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.tan( Math.toRadians( aaa.doubleValue() ) ) ) );
                break;

            case "sin": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.sin( Math.toRadians( aaa.doubleValue() ) ) ) );
                break;

            case "cos": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.cos( Math.toRadians( aaa.doubleValue() ) ) ) );
                break;

            case "atan": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.toDegrees( Math.atan( aaa.doubleValue() ) ) ) );
                break;

            case "asin": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.toDegrees( Math.asin( aaa.doubleValue() ) ) ) );
                break;

            case "acos": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.toDegrees( Math.acos( aaa.doubleValue() ) ) ) );
                break;

            case "tanh": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.tanh( aaa.doubleValue() ) ) );
                break;

            case "sinh": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.sinh( aaa.doubleValue() ) ) );
                break;

            case "cosh": // NOI18N
                aaa = stack.pop();
                stack.push( new BigDecimal( Math.cosh( aaa.doubleValue() ) ) );
                break;

            //case "&": // really for integer values only?
            //case "|": // really for integer values only?
            //case "<<": // really for integer values only?
            //case ">>": // really for integer values only?
            //case ">>>": // really for integer values only?
            //case "x!":
            //case "atanh":
            //case "asinh":
            //case "acosh":
            default:
                throw new XTCEDatabaseException(
                    XTCEFunctions.getText( "error_encdec_unsupported_math_operator" ) + // NOI18N
                    " '" + // NOI18N
                    op +
                    "'" ); // NOI18N

        }

    }
    /** Apply one of the operators from the MathOperation/Operator element
     * where some operations use two values from the stack and some operations
     * that resemble functions use a single value from the stack.
     *
     * @param op String containing the operator character
     *
     * @param stack Stack object of Strings being used in the calculation
     *
     * @throws XTCEDatabaseException thrown in the event that the math operator
     * is not yet supported.
     *
     * @throws EmptyStackException in the event that the stack does not have
     * sufficient values for the operator that is to be applied.
     *
     */

    private void applyToInfixString( final String        op,
                                     final Stack<String> stack )
        throws XTCEDatabaseException, EmptyStackException {

        String aaa;
        String bbb;

        switch ( op ) {

            case "*": // NOI18N
            case "+": // NOI18N
            case "-": // NOI18N
            case "/": // NOI18N
            case "%": // NOI18N
            case "^": // NOI18N
            case "==": // NOI18N
            case "!=": // NOI18N
            case "<": // NOI18N
            case ">": // NOI18N
            case "<=": // NOI18N
            case ">=": // NOI18N
            case "&": // really for integer values only?
            case "|": // really for integer values only?
            case "<<": // really for integer values only?
            case ">>": // really for integer values only?
            case ">>>": // really for integer values only?
                aaa = stack.pop();
                bbb = stack.pop();
                pushTwoTermOperatorToStack( bbb, op, aaa, stack );
                break;

            case "y^x": // inverse of the power operator above? // NOI18N
                aaa = stack.pop();
                bbb = stack.pop();
                pushTwoTermOperatorToStack( aaa, "^", bbb, stack );
                break;

            case "ln": // NOI18N
            case "log": // NOI18N
            case "abs": // NOI18N
            case "tan": // NOI18N
            case "sin": // NOI18N
            case "cos": // NOI18N
            case "atan": // NOI18N
            case "asin": // NOI18N
            case "acos": // NOI18N
            case "tanh": // NOI18N
            case "sinh": // NOI18N
            case "cosh": // NOI18N
            case "atanh": // NOI18N
            case "asinh": // NOI18N
            case "acosh": // NOI18N
                aaa = stack.pop();
                pushOneTermOperatorToStack( aaa, op, stack );
                break;

            case "e^x": // NOI18N
                aaa = stack.pop();
                pushTwoTermOperatorToStack( "e", "^", aaa.trim(), stack );
                break;

            case "1/x": // NOI18N
                aaa = stack.pop();
                stack.push( "(1.0 / " + aaa.trim() + ") " );
                break;

            case "swap": // NOI18N
                // not sure what to do with this one
                aaa = stack.pop();
                bbb = stack.pop();
                stack.push( bbb );
                stack.push( aaa );
                break;

            case "x!": // NOI18N
                aaa = stack.pop();
                stack.push( aaa.trim() + " ! " );
                break;

            default:
                throw new XTCEDatabaseException(
                    XTCEFunctions.getText( "error_encdec_unsupported_math_operator" ) + // NOI18N
                    " '" + // NOI18N
                    op +
                    "'" ); // NOI18N

        }

    }

    /** Helper method to print a two term operator to the result string in
     * construction for infix output.
     *
     * "Two Term Operators" are the most common basic primitives
     *
     * @param aaa String containing the right hand side value
     *
     * @param bbb String containing the left hand side value
     *
     * @param opr String containing the operator (function) name
     *
     * @param stack Stack of strings containing the running string being
     * constructed for an infix expression.
     *
     */

    private void pushTwoTermOperatorToStack( final String        bbb,
                                             final String        opr,
                                             final String        aaa,
                                             final Stack<String> stack ) {

        final StringBuilder sb = new StringBuilder();

        sb.append( "(" );
        sb.append( bbb.trim() );
        sb.append( " " );
        sb.append( opr );
        sb.append( " " );
        sb.append( aaa );
        sb.append( ")" );
        sb.append( " " );

        stack.push( sb.toString() );

    }

    /** Helper method to print a one term operator to the result string in
     * construction for infix output.
     *
     * "One Term Operators" are treated like function call syntax.
     *
     * @param aaa String containing the value
     *
     * @param opr String containing the operator (function) name
     *
     * @param stack Stack of strings containing the running string being
     * constructed for an infix expression.
     *
     */

    private void pushOneTermOperatorToStack( final String        aaa,
                                             final String        opr,
                                             final Stack<String> stack ) {

        final StringBuilder sb = new StringBuilder();

        sb.append( opr );
        sb.append( "[" );
        sb.append( aaa.trim() );
        sb.append( "]" );
        sb.append( " " );

        stack.push( sb.toString() );

    }

    // Private Data Members

    private final MathOperationCalibrator mathElement_;

}
