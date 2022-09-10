class Intern(val weeklyWorkload: Int) {
    val baseWorkload = 20

    class Salary {
        val basePay = 50.0
        val extraHoursPay = 2.8
    }

    fun num(): Double {
        return if (weeklyWorkload > baseWorkload) {
            Salary().basePay + (weeklyWorkload - baseWorkload) * Salary().extraHoursPay
        } else if (weeklyWorkload == baseWorkload) {
            Salary().basePay
        } else {
            val oneDayPay = Salary().basePay / baseWorkload
            (baseWorkload - weeklyWorkload) * oneDayPay
        }
    }

    val weeklySalary: Double = num()
}