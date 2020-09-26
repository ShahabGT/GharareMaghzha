package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName

data class UserDataModel(
        @SerializedName("user_rank") val userRank: String,
        @SerializedName("score_count") val scoreCount: String,
        @SerializedName("user_id") val userId: String,
        @SerializedName("user_name") val userName: String,
        @SerializedName("user_code") val userCode: String,
        @SerializedName("user_avatar") val userAvatar: String,
)



