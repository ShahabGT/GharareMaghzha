package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

data class TimeResponse(
        val result: String,
        val message: String,
        val time: String,
        val passed: String,
        @SerializedName("last_update") val lastUpdate: String,
        @SerializedName("user_questions") val userQuestions: String,
)

