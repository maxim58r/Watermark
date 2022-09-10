fun String?.capitalize(): String? {
    println("Before: $this")
    return when {
        isNullOrBlank() -> {
            println("After: $this")
            this
        }
        length == 1 -> {
            println("After: ${uppercase()}")
            uppercase()
        }
        else -> {
            println("After: ${this[0].uppercase() + substring(1)}")
            this[0].uppercase() + substring(1)
        }
    }
}