package ir.ghararemaghzha.game.models


data class PlanResponse(
        val result: String,
        val message: String,
        val data: List<PlanModel>
)

