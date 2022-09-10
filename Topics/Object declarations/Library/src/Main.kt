object Math {
    fun abs(x: Int): Int {
        return if (x < 0) -x else x
    }

    fun abs(x: Double): Double {
        return if (x <= 0.0) 0.0 - x else x
    }
}