class Vehicle(val name: String) {
    inner class Engine(val horsePower: Int) {
        fun start() {
            println("RRRrrrrrrr....")
        }

        fun printHorsePower() {
            println("The ${this@Vehicle.name} vehicle has $horsePower horsepower.")
        }
    }
}