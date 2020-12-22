package ir.ghararemaghzha.game.divider

abstract class ListItem {

    val TYPE_HEADER = 0
    val TYPE_EVENT = 1

    abstract fun getType(): Int
}