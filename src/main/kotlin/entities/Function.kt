package entities

import util.Node
import kotlin.math.pow

const val DEFAULT_DERIVATIVE_STEP: Double = 0.001;

class Function(val compTree: Node, val variableNames: List<String>) {
    fun getValue(variables: Variables): Double {
        return this.compTree.compute(variables);
    }

    fun getFirstDerivative(variables: Variables, variableName: String): Double {
        val variable: Double = variables.get(variableName);
        var derivative: Double = 0.0;
        variables.set(variableName, variable + DEFAULT_DERIVATIVE_STEP);
        derivative += this.getValue(variables);
        variables.set(variableName, variable - DEFAULT_DERIVATIVE_STEP);
        derivative -= this.getValue(variables);
        return derivative / (2 * DEFAULT_DERIVATIVE_STEP);
    }

    fun getSecondDerivative(variables: Variables, variableName: String): Double {
        val variable: Double = variables.get(variableName);
        var derivative: Double = -2 * this.getValue(variables);
        variables.set(variableName, variable + DEFAULT_DERIVATIVE_STEP);
        derivative += this.getValue(variables);
        variables.set(variableName, variable - DEFAULT_DERIVATIVE_STEP);
        derivative += this.getValue(variables);
        return derivative / DEFAULT_DERIVATIVE_STEP.pow(2);
    }

    fun getMinAndMaxValuesOnSegment(segment: Segment, step: Double): Pair<Double, Double> {
        var minValue: Double = Double.POSITIVE_INFINITY;
        var maxValue: Double = Double.NEGATIVE_INFINITY;
        if (this.variableNames.size != 1) throw IllegalArgumentException();

        val variables: Variables = Variables();
        val variableName: String = variableNames[0];
        var position: Double = segment.leftBorder;
        var value: Double;
        while (position <= segment.rightBorder) {
            variables.set(variableName, position);
            value = this.getValue(variables);
            if (value < minValue) minValue = value;
            if (value > maxValue) maxValue = value;
            position += step;
        }

        return Pair(minValue, maxValue);
    }
}