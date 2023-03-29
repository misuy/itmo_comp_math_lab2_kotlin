package entities

class SystemOfTwoEquationsSolvingResult(val root1: Double, val root2: Double, val iterationsCount: Int) {
    override fun toString(): String {
        return "Корень: ($root1, $root2); Число итераций: $iterationsCount";
    }
}