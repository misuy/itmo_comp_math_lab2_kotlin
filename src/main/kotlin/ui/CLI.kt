package ui

import entities.Function
import entities.Segment
import methods.*
import util.*
import java.io.InputStream
import java.io.OutputStream
import java.io.PrintStream
import java.util.Scanner
import kotlin.properties.Delegates


abstract class Reader(val scanner: Scanner, val outputStream: PrintStream, val message: String) {
    fun printMessage() {
        outputStream.print(message);
    }


    abstract fun read();
}

class NothingReader(scanner: Scanner, outputStream: PrintStream, message: String) : Reader(scanner, outputStream, message) {
    override fun read() {
        this.printMessage();
    }
}

class IntReader(scanner: Scanner, outputStream: PrintStream, message: String) : Reader(scanner, outputStream, message) {
    var int by Delegates.notNull<Int>();

    override fun read() {
        this.printMessage();
        this.int = this.scanner.nextInt();
    }
}

class DoubleReader(scanner: Scanner, outputStream: PrintStream, message: String) : Reader(scanner, outputStream, message) {
    var double by Delegates.notNull<Double>();

    override fun read() {
        this.printMessage();
        this.double = this.scanner.nextDouble();
    }
}

class SegmentReader(scanner: Scanner, outputStream: PrintStream, message: String) : Reader(scanner, outputStream, message) {
    lateinit var segment: Segment;

    override fun read() {
        this.printMessage();
        val leftBorder: Double = this.scanner.nextDouble();
        val rightBorder: Double = this.scanner.nextDouble();
        this.segment = Segment(leftBorder, rightBorder);
    }

}

class FunctionReader(scanner: Scanner, outputStream: PrintStream, message: String) : Reader(scanner, outputStream, message) {
    lateinit var function: Function;

    override fun read() {
        this.printMessage();
        val expression: String = this.scanner.nextLine();
        val tokens: List<Token> = parseTokens(expression, constants, openingBrackets, closingBrackets, unaryPreOperations, unaryPostOperations, binaryOperations);
        this.function = Function(buildCompTree(tokens), getVariableNamesFromTokens(tokens))
    }
}

class EquationsMethodReader(scanner: Scanner, outputStream: PrintStream, message: String) : Reader(scanner, outputStream, message) {
    lateinit var method: EquationSolvingMethod;

    override fun read() {
        this.printMessage();
        when (this.scanner.nextInt()) {
            1 -> this.method = chordMethod;
            2 -> this.method = newtonMethod;
            3 -> this.method = simpleIterationMethod;
            else -> throw IllegalArgumentException();
        }
    }
}

class SystemsOfEquationsMethodReader(scanner: Scanner, outputStream: PrintStream, message: String) : Reader(scanner, outputStream, message) {
    lateinit var method: SystemOfTwoEquationsSolvingMethod;

    override fun read() {
        this.printMessage();
        when (this.scanner.nextInt()) {
            1 -> this.method = simpleIterationForSystemOfTwoEquationsMethod;
            else -> throw IllegalArgumentException();
        }
    }
}