package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName

data class AppOpenResponse(
    @SerializedName("booster_value")
    val boosterValue: String,
    val message: String,
    val passed: String,
    val result: String,
    @SerializedName("score_booster_count")
    val scoreBoosterCount: String,
    @SerializedName("score_count")
    val scoreCount: String,
    val time: String,
    @SerializedName("user_booster")
    val userBooster: String,
    @SerializedName("user_plan")
    val userPlan: String,
    val version: String,
    @SerializedName("version_essential")
    val versionEssential: String
)