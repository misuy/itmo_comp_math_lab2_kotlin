package entities

class EquationSolvingResult(val root: Double, val iterationsCount: Int) {
    override fun toString(): String {
        return "Корень: $root; Число итераций: $iterationsCount";
    }
}