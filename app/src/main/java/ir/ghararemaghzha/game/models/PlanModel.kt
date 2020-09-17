package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

data class PlanModel(
        val value: String,
        @SerializedName("plan_id") val planId: String,
        @SerializedName("plan_count") val planCount: String,
        @SerializedName("plan_price") val planPrice: String,
)



