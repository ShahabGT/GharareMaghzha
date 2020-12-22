package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName

data class ContactsModel(
        @SerializedName("user_id") val id: String,
        @SerializedName("user_number") val number: String,
        @SerializedName("user_code") val code: String,
        @SerializedName("user_name") val name: String,
        @SerializedName("user_avatar") val avatar: String,
        @SerializedName("user_reg_date") val regDate: String,
)
