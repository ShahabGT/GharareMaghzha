package ir.ghararemaghzha.game.models


import com.google.gson.annotations.SerializedName

data class NitroResponse(
    val message: String,
    @SerializedName("plan_price")
    val planPrice: String,
    @SerializedName("plan_value")
    val planValue: String,
    val result: String,
    @SerializedName("user_plan")
    val userPlan: String
)