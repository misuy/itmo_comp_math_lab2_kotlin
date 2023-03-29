package ui

import entities.*
import entities.Function
import util.*
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel
import kotlin.math.abs

class ChartPanel : JPanel() {
    private lateinit var horizontalSegment: Segment;
    private lateinit var verticalSegment: Segment;

    private val functions: MutableList<FunctionGraph> = mutableListOf();
    private val filledAreas: MutableList<FilledAreaUnderFunctionGraph> = mutableListOf();

    init {
        this.addHorizontalAxis();
        this.addVerticalAxis();
    }

    private fun addHorizontalAxis() {
        val expression: String = "x+0*y";
        val tokens: List<Token> = parseTokens(expression, constants, openingBrackets, closingBrackets, unaryPreOperations, unaryPostOperations, binaryOperations);
        val variableNames: List<String> = getVariableNamesFromTokens(tokens);
        this.functions.add(FunctionGraph(Function(buildCompTree(tokens), variableNames), Color.BLACK));
    }

    private fun addVerticalAxis() {
        val expression: String = "0*x+y";
        val tokens: List<Token> = parseTokens(expression, constants, openingBrackets, closingBrackets, unaryPreOperations, unaryPostOperations, binaryOperations);
        val variableNames: List<String> = getVariableNamesFromTokens(tokens);
        this.functions.add(FunctionGraph(Function(buildCompTree(tokens), variableNames), Color.BLACK));
    }

    fun setChartSegments(horizontalSegment: Segment, verticalSegment: Segment) {
        this.horizontalSegment = horizontalSegment;
        this.verticalSegment = verticalSegment;
    }

    fun addFunction(function: FunctionGraph) {
        this.functions.add(function);
    }

    fun addFilledArea(filledArea: FilledAreaUnderFunctionGraph) {
        this.filledAreas.add(filledArea);
    }

    override fun paintComponent(g: Graphics?) {
        val graphics: Graphics2D = g as? Graphics2D ?: throw IllegalArgumentException();
        super.paintComponent(graphics);

        val widthPerPixel: Double = (this.horizontalSegment.rightBorder - this.horizontalSegment.leftBorder) / this.width;
        val heightPerPixel: Double = (this.verticalSegment.rightBorder - this.verticalSegment.leftBorder) / this.height;

        for (filledArea: FilledAreaUnderFunctionGraph in this.filledAreas) {
            this.paintFilledArea(graphics, filledArea, widthPerPixel, heightPerPixel);
        }

        for (function: FunctionGraph in this.functions) {
            when (function.function.variableNames.size) {
                1 -> this.plotFunctionByOneVariable(graphics, function, widthPerPixel, heightPerPixel);
                2 -> this.plotFunctionByTwoVariables(graphics, function, widthPerPixel, heightPerPixel);
                else -> throw IllegalArgumentException();
            }
        }
    }

    private fun plotFunctionByOneVariable(graphics: Graphics2D, function: FunctionGraph, widthPerPixel: Double, heightPerPixel: Double) {
        graphics.stroke = BasicStroke(4F);
        graphics.color = function.color;
        val variables: Variables = Variables();
        val variableName: String = function.function.variableNames[0];
        for (horizontalPixel: Int in 0 until this.width) {
            variables.set(variableName, horizontalPixelToCoordinate(horizontalPixel, widthPerPixel));
            val value: Double = function.function.getValue(variables);
            for (verticalPixel: Int in 0 until this.height) {
                if (abs(value - verticalPixelToCoordinate(verticalPixel, heightPerPixel)) < heightPerPixel) graphics.drawLine(horizontalPixel, verticalPixel, horizontalPixel, verticalPixel);
            }
        }
    }

    private fun plotFunctionByTwoVariables(graphics: Graphics2D, function: FunctionGraph, widthPerPixel: Double, heightPerPixel: Double) {
        graphics.stroke = BasicStroke(4F);
        graphics.color = function.color;
        val variables: Variables = Variables();
        var prevValue: Double;
        var value: Double
        val horizontalVariableName: String = function.function.variableNames[0];
        val verticalVariableName: String = function.function.variableNames[1];
        for (horizontalPixel: Int in 0 until this.width) {
            variables.set(horizontalVariableName, horizontalPixelToCoordinate(horizontalPixel, widthPerPixel));
            variables.set(verticalVariableName, verticalPixelToCoordinate(0, heightPerPixel));
            value = function.function.getValue(variables);
            for (verticalPixel: Int in 0 until this.height) {
                variables.set(verticalVariableName, verticalPixelToCoordinate(verticalPixel, heightPerPixel));
                prevValue = value;
                value = function.function.getValue(variables);
                if (value * prevValue <= 0) graphics.drawLine(horizontalPixel, verticalPixel, horizontalPixel, verticalPixel);
            }
        }
        for (verticalPixel: Int in 0 until this.height) {
            variables.set(horizontalVariableName, horizontalPixelToCoordinate(0, widthPerPixel));
            variables.set(verticalVariableName, verticalPixelToCoordinate(verticalPixel, heightPerPixel));
            value = function.function.getValue(variables);
            for (horizontalPixel: Int in 0 until this.width) {
                variables.set(horizontalVariableName, horizontalPixelToCoordinate(horizontalPixel, widthPerPixel));
                prevValue = value;
                value = function.function.getValue(variables);
                if (value * prevValue <= 0) graphics.drawLine(horizontalPixel, verticalPixel, horizontalPixel, verticalPixel);
            }
        }
    }

    private fun paintFilledArea(graphics: Graphics2D, filledArea: FilledAreaUnderFunctionGraph, widthPerPixel: Double, heightPerPixel: Double) {
        graphics.stroke = BasicStroke(1F);
        graphics.color = filledArea.color;
        if (filledArea.function.variableNames.size != 1) throw IllegalArgumentException();
        val variableName: String = filledArea.function.variableNames[0];
        val variables: Variables = Variables();
        var value: Double;
        var verticalCoordinate: Double;
        for (horizontalPixel: Int in 0 until this.width) {
            variables.set(variableName, horizontalPixelToCoordinate(horizontalPixel, widthPerPixel));
            if ((variables.get(variableName) >= filledArea.segment.leftBorder) && (variables.get(variableName) <= filledArea.segment.rightBorder)) {
                value = filledArea.function.getValue(variables);
                for (verticalPixel: Int in 0 until this.height) {
                    verticalCoordinate = verticalPixelToCoordinate(verticalPixel, heightPerPixel);
                    if (((verticalCoordinate <= 0) && (verticalCoordinate > value)) || ((verticalCoordinate >= 0) && (verticalCoordinate < value))) graphics.drawLine(horizontalPixel, verticalPixel, horizontalPixel, verticalPixel);
                }
            }
        }
    }

    private fun horizontalPixelToCoordinate(horizontalPixel: Int, widthPerPixel: Double): Double = horizontalSegment.leftBorder + horizontalPixel * widthPerPixel;

    private fun verticalPixelToCoordinate(verticalPixel: Int, heightPerPixel: Double): Double = verticalSegment.leftBorder + (this.height - verticalPixel) * heightPerPixel;
}