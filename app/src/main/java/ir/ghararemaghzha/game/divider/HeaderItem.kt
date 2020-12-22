package ir.ghararemaghzha.game.divider

class HeaderItem : ListItem() {

    private var name: String = ""
        get() = field
        set(value) { field = value }

    override fun getType(): Int {
        return TYPE_HEADER
    }

}