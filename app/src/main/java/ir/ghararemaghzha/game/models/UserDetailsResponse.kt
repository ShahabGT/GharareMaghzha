package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

data class UserDetailsResponse(
        val result: String,
        val message: String,
        val correct: Int,
        val incorrect: Int,
        val plan: String,
        @SerializedName("user_data") val userData: UserDataModel
)



