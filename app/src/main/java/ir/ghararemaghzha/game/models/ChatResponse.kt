package ir.ghararemaghzha.game.models;


data class ChatResponse(
        val result: String,
        val message: String,
        val data: List<MessageModel>
)
