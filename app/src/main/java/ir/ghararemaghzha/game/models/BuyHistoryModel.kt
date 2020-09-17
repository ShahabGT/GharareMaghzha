package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;


data class BuyHistoryModel(
        val plan:String,
        val amount:String,
        val valid:String,
        val date:String,
        val description:String,
        @SerializedName("buy_plan_id") val buyPlanId:String,
        @SerializedName("merchant_id") val merchantId:String,
        @SerializedName("user_id") val userId:String,
        @SerializedName("influencer_id") val influencerId:String,
        @SerializedName("influencer_code") val influencerCode:String,
        @SerializedName("influencer_name") val influencerName:String,
        @SerializedName("influencer_amount") val influencerAmount:String,
        @SerializedName("orderid") val orderId:String,
        @SerializedName("rescode") val resCode:String,
)




