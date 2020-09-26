package ir.ghararemaghzha.game.models

data class HighscoreResponse(
        val result: String,
        val message: String,
        val data: List<HighscoreModel>,
        val user: UserRankModel
)


