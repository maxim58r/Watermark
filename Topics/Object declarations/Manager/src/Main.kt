class Task(val name: String)

object Manager {
    var solvedTask = 0
    fun solveTask(task: Task): Task {
        println("Task ${Task(task.name).name} solved!")
        ++solvedTask
        return task
    }
}