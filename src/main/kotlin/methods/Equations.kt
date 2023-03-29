package methods

import entities.EquationSolvingResult
import entities.Function
import entities.Segment
import entities.Variables
import kotlin.math.abs

const val DEFAULT_STEP: Double = 0.01;


fun interface EquationSolvingMethod {
    fun solve(function: Function, segment: Segment, accuracy: Double): EquationSolvingResult;
}

val chordMethod: EquationSolvingMethod = EquationSolvingMethod { function, segment, accuracy ->
    if (function.variableNames.size != 1) throw IllegalArgumentException("Метод должен использоваться только для функций от одной пременной");
    val variableName: String = function.variableNames[0];
    val variables: Variables = Variables();
    var curSegment: Segment = segment;
    var iterationsCount: Int = 0;
    while ((curSegment.rightBorder - curSegment.leftBorder) >= accuracy) {
        iterationsCount++;

        variables.set(variableName, curSegment.leftBorder);
        val leftValue: Double = function.getValue(variables);
        variables.set(variableName, curSegment.rightBorder);
        val rightValue: Double = function.getValue(variables);

        val x: Double = curSegment.leftBorder - leftValue * ((curSegment.rightBorder - curSegment.leftBorder) / (rightValue - leftValue));
        variables.set(variableName, x);
        val xValue: Double = function.getValue(variables);

        if (leftValue * xValue <= 0) curSegment = Segment(curSegment.leftBorder, x);
        else curSegment = Segment(x, curSegment.rightBorder);
    }

    EquationSolvingResult(curSegment.leftBorder, iterationsCount);
}

val newtonMethod: EquationSolvingMethod = EquationSolvingMethod { function, segment, accuracy ->
    if (function.variableNames.size != 1) throw IllegalArgumentException("Метод должен использоваться только для функций от одной пременной");
    var iterationsCount: Int = 0;
    val variableName: String = function.variableNames[0];
    val variables: Variables = Variables();
    var x: Double;
    variables.set(variableName, segment.leftBorder);
    if (function.getValue(variables) * function.getSecondDerivative(variables, variableName) > 0) x = segment.leftBorder;
    else x = segment.rightBorder;
    var prevX: Double = x - 2*accuracy;
    while (abs(x - prevX) >= accuracy) {
        iterationsCount++;
        prevX = x;
        variables.set(variableName, prevX);
        x = prevX - function.getValue(variables) / function.getFirstDerivative(variables, variableName);
    }

    EquationSolvingResult(x, iterationsCount);
}

fun checkConvergenceConditionForSimpleIterationMethod(function: Function, segment: Segment, variableName: String): Boolean {
    val variables: Variables = Variables();

    var result: Boolean = true;
    var x: Double = segment.leftBorder;
    while (x <= segment.rightBorder) {
        variables.set(variableName, x);
        if (abs(function.getFirstDerivative(variables, variableName)) >= 1) result = false;
        x += DEFAULT_STEP;
    }

    return result;
}

val simpleIterationMethod: EquationSolvingMethod = EquationSolvingMethod { function, segment, accuracy ->
    var iterationsCount: Int = 0;
    if (function.variableNames.size != 1) throw IllegalArgumentException("Метод должен использоваться только для функций от одной пременной");
    val variables: Variables = Variables();
    val variableName: String = function.variableNames[0];

    if (!checkConvergenceConditionForSimpleIterationMethod(function, segment, variableName)) throw IllegalArgumentException("Не выполняется достаточное условие сходимости");

    var x: Double = segment.leftBorder;
    var prevX: Double = x - 2*accuracy;
    while (abs(x - prevX) >= accuracy) {
        iterationsCount++;
        prevX = x;
        variables.set(variableName, prevX);
        x = function.getValue(variables);
    }

    EquationSolvingResult(x, iterationsCount);
}
