package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;


data class GeneralResponse(
        val result:String,
        val message:String,
        val token:String,
        val time:String,
        val count:String,
        val value:String,
        @SerializedName("user_id") val userId:String,
        @SerializedName("user_name") val userName:String,
        @SerializedName("user_code") val userCode:String,
        @SerializedName("user_score") val userScore:String,
        @SerializedName("user_plan") val userPlan:String,
        @SerializedName("influencer_id") val influencerId:String,
        @SerializedName("influencer_amount") val influencerAmount:String,
        @SerializedName("merchant_id") val merchantId:String,
        @SerializedName("user_sex") val userSex:String?,
        @SerializedName("user_bday") val userBday:String?,
        @SerializedName("user_email") val userEmail:String?,
        @SerializedName("user_invite") val userInvite:String?,
        @SerializedName("user_avatar") val userAvatar:String?,
)


