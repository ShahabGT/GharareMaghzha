package ir.ghararemaghzha.game.models;


data class QuestionResponse(
        val result: String,
        val message: String,
        val data: List<QuestionModel>
)

