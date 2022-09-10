class Block(val color: String) {
    object BlockProperties {
        var length = 6
        var width = 4

        fun blocksInBox(length: Int, width: Int): Int {
            val l = length / this.length
            val w = width / this.width

            return l * w
        }
    }
}