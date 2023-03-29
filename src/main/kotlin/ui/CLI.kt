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


abstract class Reader(val inputStream: InputStream, val outputStream: PrintStream, val message: String) {
    val scanner: Scanner = Scanner(inputStream);

    fun printMessage() {
        outputStream.print(message);
    }


    abstract fun read();
}

class NothingReader(inputStream: InputStream, outputStream: PrintStream, message: String) : Reader(inputStream, outputStream, message) {
    override fun read() {
        this.printMessage();
    }
}

class IntReader(inputStream: InputStream, outputStream: PrintStream, message: String) : Reader(inputStream, outputStream, message) {
    var int by Delegates.notNull<Int>();

    override fun read() {
        this.printMessage();
        this.int = this.scanner.nextInt();
    }
}

class DoubleReader(inputStream: InputStream, outputStream: PrintStream, message: String) : Reader(inputStream, outputStream, message) {
    var double by Delegates.notNull<Double>();

    override fun read() {
        this.printMessage();
        this.double = this.scanner.nextDouble();
    }
}

class SegmentReader(inputStream: InputStream, outputStream: PrintStream, message: String) : Reader(inputStream, outputStream, message) {
    lateinit var segment: Segment;

    override fun read() {
        this.printMessage();
        val leftBorder: Double = this.scanner.nextDouble();
        val rightBorder: Double = this.scanner.nextDouble();
        this.segment = Segment(leftBorder, rightBorder);
    }

}

class FunctionReader(inputStream: InputStream, outputStream: PrintStream, message: String) : Reader(inputStream, outputStream, message) {
    lateinit var function: Function;

    override fun read() {
        this.printMessage();
        val expression: String = this.scanner.nextLine();
        val tokens: List<Token> = parseTokens(expression, constants, openingBrackets, closingBrackets, unaryPreOperations, unaryPostOperations, binaryOperations);
        this.function = Function(buildCompTree(tokens), getVariableNamesFromTokens(tokens))
    }
}

class EquationsMethodReader(inputStream: InputStream, outputStream: PrintStream, message: String) : Reader(inputStream, outputStream, message) {
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

class SystemsOfEquationsMethodReader(inputStream: InputStream, outputStream: PrintStream, message: String) : Reader(inputStream, outputStream, message) {
    lateinit var method: SystemOfTwoEquationsSolvingMethod;

    override fun read() {
        this.printMessage();
        when (this.scanner.nextInt()) {
            1 -> this.method = simpleIterationForSystemOfTwoEquationsMethod;
            else -> throw IllegalArgumentException();
        }
    }
}