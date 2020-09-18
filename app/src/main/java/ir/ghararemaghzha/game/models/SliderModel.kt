package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName

data class SliderModel(
        @SerializedName("slider_id") val sliderId:String,
        @SerializedName("slider_pic") val sliderPic:String,
        @SerializedName("slider_des") val sliderDes:String,
        @SerializedName("slider_link") val sliderLink:String,
)

