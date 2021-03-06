package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName

data class HighscoreModel(
        val color: Int,
        @SerializedName("score_count") val scoreCount: String,
        @SerializedName("user_id") val userId: String,
        @SerializedName("user_name") val userName: String,
        @SerializedName("user_number") val userNumber: String,
        @SerializedName("user_code") val userCode: String,
        @SerializedName("user_avatar") val userAvatar: String,
)



