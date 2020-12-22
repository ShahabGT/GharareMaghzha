package ir.ghararemaghzha.game.divider

import ir.ghararemaghzha.game.models.ContactsModel

class EventItem : ListItem() {

    private var model: ContactsModel=ContactsModel()
        get() = field
        set(value) { field = value }

    override fun getType(): Int {
        return TYPE_EVENT
    }

}