package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName

data class UserDetailsResponse(
        val result: String,
        val message: String,
        val correct: Int,
        val incorrect: Int,
        val plan: String,
        val booster: String,
        val rank: String,
        val level1: String,
        val level2: String,
        val level3: String,
        val level4: String,
        @SerializedName("user_data") val userData: UserDataModel
)



