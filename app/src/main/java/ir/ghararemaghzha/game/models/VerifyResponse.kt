package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName

data class VerifyResponse(
        val result: String,
        val version: String,
        val message: String,
        @SerializedName("version_essential") val versionEssential: String,
        @SerializedName("user_plan") val userPlan: String,
        @SerializedName("score_count") val scoreCount: String,
        @SerializedName("user_booster_expire") val userBoosterExpire: String,
        @SerializedName("user_booster") val userBooster: String,
        @SerializedName("booster_value") val boosterValue: String,
)

