package ir.ghararemaghzha.game.models

data class BuyHistoryResponse(
        val result:String,
        val message:String,
        val data: List<BuyHistoryModel>
)
