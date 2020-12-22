package ir.ghararemaghzha.game.models

data class ContactsResponse(
        val result: String,
        val message: String,
        val data: List<ContactsModel>
)