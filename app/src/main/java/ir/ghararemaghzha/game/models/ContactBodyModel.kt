package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName

data class ContactBodyModel(
        @SerializedName("user_number")
        val user_number:String)
