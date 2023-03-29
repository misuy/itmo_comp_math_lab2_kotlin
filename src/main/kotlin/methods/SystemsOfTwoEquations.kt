package methods

import entities.*
import entities.Function
import kotlin.math.abs
import kotlin.math.max

fun interface SystemOfTwoEquationsSolvingMethod {
    fun solve(function1: Function, function2: Function, segment1: Segment, segment2: Segment, accuracy: Double): SystemOfTwoEquationsSolvingResult;
}

fun checkConvergenceConditionForSimpleIterationForSystemsOfTwoEquationsMethod(function1: Function, function2: Function, segment1: Segment, segment2: Segment, variableNames: List<String>): Boolean {
    val variables: Variables = Variables();
    val variableName1: String = variableNames[0];
    val variableName2: String = variableNames[1];
    var result: Boolean = true;

    var x: Double = segment1.leftBorder;
    var y: Double;
    while (x <= segment1.rightBorder) {
        variables.set(variableName1, x);
        y = segment2.leftBorder;
        while (y <= segment2.rightBorder) {
            variables.set(variableName2, y);
            if ((abs(function1.getFirstDerivative(variables, variableName1) + abs(function1.getFirstDerivative(variables, variableName2)))) >= 1) result = false;
            if ((abs(function2.getFirstDerivative(variables, variableName1) + abs(function2.getFirstDerivative(variables, variableName2)))) >= 1) result = false;
            y += DEFAULT_STEP;
        }
        x += DEFAULT_STEP;
    }

    return result;
}

val simpleIterationForSystemOfTwoEquationsMethod: SystemOfTwoEquationsSolvingMethod = SystemOfTwoEquationsSolvingMethod { function1, function2, segment1, segment2, accuracy ->
    var iterationsCount: Int = 0;
    if ((function1.variableNames.size != 2) || (function2.variableNames.size != 2) || (function1.variableNames[0] != function2.variableNames[0]) || (function1.variableNames[1] != function2.variableNames[1])) throw IllegalArgumentException("Метод должен использоваться только для функций от двух пременных")
    val variables: Variables = Variables();
    val variableNames: List<String> = function1.variableNames;

    if (!checkConvergenceConditionForSimpleIterationForSystemsOfTwoEquationsMethod(function1, function2, segment1, segment2, variableNames)) throw IllegalArgumentException("Не выполнено достаточное условие сходимости");

    var x: Double = segment1.leftBorder;
    var y: Double = segment2.leftBorder;
    var prevX: Double = x - 2*accuracy;
    var prevY: Double = y - 2*accuracy;

    while (max(abs(x - prevX), abs(y - prevY)) >= accuracy) {
        iterationsCount++;
        prevX = x;
        prevY = y;
        variables.set(variableNames[0], prevX);
        variables.set(variableNames[1], prevY);
        x = function1.getValue(variables);
        y = function2.getValue(variables);
    }
    SystemOfTwoEquationsSolvingResult(x, y, iterationsCount);
}