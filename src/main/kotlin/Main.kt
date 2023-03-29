//^^

import entities.*
import entities.Function
import methods.*
import ui.*
import util.*
import java.awt.Color
import java.awt.Dimension
import java.io.FileInputStream
import java.io.InputStream
import java.io.PrintStream
import java.util.Scanner
import javax.swing.JFrame

const val DEFAULT_STEP: Double = 0.01;
val FRAME_SIZE: Dimension = Dimension(1000, 1000);

fun main(args: Array<String>) {
    val frame: JFrame = JFrame("chart");
    val chartPanel: ChartPanel = ChartPanel();
    frame.size = FRAME_SIZE;

    val scanner: Scanner = if (args.size == 1) Scanner(FileInputStream(args[0])) else Scanner(System.`in`);
    val outputStream: PrintStream = System.out;

    val greeting: NothingReader = NothingReader(scanner, outputStream, "Добро пожаловать в лаботаторую работу №2 \"Численное решение нелинейных уравнений и систем\"\n");
    val equationOrSystemReader: IntReader = IntReader(scanner, outputStream, "Что будем решать? (нужно ввести одно целоое число) {\n1 -- нелинейное уравнение\n2 -- система нелинейных уравнений\n}: ");

    greeting.read();
    equationOrSystemReader.read();

    when (equationOrSystemReader.int) {
        1 -> {
            val segmentReader: SegmentReader = SegmentReader(scanner, outputStream, "Введите границы интервала поиска корня (левая_граница правая_граница): ");
            val accuracyReader: DoubleReader = DoubleReader(scanner, outputStream, "Введите точность: ");
            val methodReader: EquationsMethodReader = EquationsMethodReader(scanner, outputStream, "Выберите метод решения (нужно ввести одно целое число) {\n1 -- метод хорд\n2 -- метод ньютона\n3 -- метод простой итерации\n}: ");

            methodReader.read();
            val method: EquationSolvingMethod = methodReader.method;

            val functionReader: FunctionReader =
                if (method == simpleIterationMethod) FunctionReader(scanner, outputStream, "Введите функцию f(x), для которой будет решено уравнение f(x)=x:\n");
                else FunctionReader(scanner, outputStream, "Введите функцию f(x), для которой будет решено уравнение f(x)=0:\n");

            scanner.nextLine();
            functionReader.read();
            val function: Function = functionReader.function;

            val functionToPlot: Function =
            if (method == simpleIterationMethod) Function(
                buildBinaryOperator(fun (leftValue: Double, rightValue: Double): Double = leftValue - rightValue, function.compTree, buildVariable("x")),
                function.variableNames
            )
            else function;

            segmentReader.read();
            accuracyReader.read();

            val segment: Segment = segmentReader.segment;
            val accuracy: Double = accuracyReader.double;

            chartPanel.addFunction(FunctionGraph(functionToPlot, Color.BLUE));
            val minAndMaxValuesOnSegment: Pair<Double, Double> = functionToPlot.getMinAndMaxValuesOnSegment(segment, DEFAULT_STEP);
            chartPanel.setChartSegments(Segment(segment.leftBorder - (segment.rightBorder - segment.leftBorder) / 4,
                segment.rightBorder + (segment.rightBorder - segment.leftBorder) / 4),
                Segment(minAndMaxValuesOnSegment.first - (minAndMaxValuesOnSegment.second - minAndMaxValuesOnSegment.first) / 4,
                    minAndMaxValuesOnSegment.second + (minAndMaxValuesOnSegment.second - minAndMaxValuesOnSegment.first) / 4));

            println();
            println(method.solve(function, segment, accuracy));
        }
        2 -> {
            val methodReader: SystemsOfEquationsMethodReader = SystemsOfEquationsMethodReader(scanner, outputStream, "Выберите метод решения (нужно ввести одно целое число) {\n1 -- метод простой итерации\n}: ");
            val function1Reader: FunctionReader = FunctionReader(scanner, outputStream, "Нужно ввести функции f(x, y) и g(x, y) для которых будет решена система f(x, y)=x and g(x, y)=y.\nВведите функцию f(x, y):\n");
            val function2Reader: FunctionReader = FunctionReader(scanner, outputStream, "Введите функцию g(x, y):\n");
            val segment1Reader: SegmentReader = SegmentReader(scanner, outputStream, "Введите границы интервала поиска корня по x (левая_граница правая_граница): ");
            val segment2Reader: SegmentReader = SegmentReader(scanner, outputStream, "Введите границы интервала поиска корня по y (левая_граница правая_граница): ");
            val accuracyReader: DoubleReader = DoubleReader(scanner, outputStream, "Введите точность: ");

            methodReader.read();
            scanner.nextLine();
            function1Reader.read();
            function2Reader.read();
            segment1Reader.read();
            segment2Reader.read();
            accuracyReader.read();

            val method: SystemOfTwoEquationsSolvingMethod = methodReader.method;
            val function1: Function = Function(function1Reader.function.compTree, listOf("x", "y"));
            val functionToPlot1: Function = Function(buildBinaryOperator(fun (leftValue: Double, rightValue: Double): Double = leftValue - rightValue, function1.compTree, buildVariable("x")), function1.variableNames);
            val function2: Function = Function(function2Reader.function.compTree, listOf("x", "y"));
            val functionToPlot2: Function = Function(buildBinaryOperator(fun (leftValue: Double, rightValue: Double): Double = leftValue - rightValue, function2.compTree, buildVariable("y")), function2.variableNames);
            val segment1: Segment = segment1Reader.segment;
            val segment2: Segment = segment2Reader.segment;
            val accuracy: Double = accuracyReader.double;

            chartPanel.addFunction(FunctionGraph(functionToPlot1, Color.BLUE));
            chartPanel.addFunction(FunctionGraph(functionToPlot2, Color.RED));
            chartPanel.setChartSegments(Segment(segment1.leftBorder - (segment1.rightBorder-segment1.leftBorder) / 4, segment1.rightBorder + (segment1.rightBorder-segment1.leftBorder) / 4), Segment(segment2.leftBorder - (segment2.rightBorder-segment2.leftBorder) / 4, segment2.rightBorder + (segment2.rightBorder-segment2.leftBorder) / 4));

            println();
            println(method.solve(function1, function2, segment1, segment2, accuracy));
        }
    }


    frame.add(chartPanel);
    frame.isVisible = true;
}
