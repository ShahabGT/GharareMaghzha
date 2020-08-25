package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class SliderModel {

//            "slider_id": "1",
//            "slider_pic": "shahab",
//            "slider_des": "portfolio of shahab azimi",
//            "slider_link": "https://shahabazimi.ir"


    @SerializedName("slider_id")
    private String sliderId;

    @SerializedName("slider_pic")
    private String sliderPic;

    @SerializedName("slider_des")
    private String sliderDes;

    @SerializedName("slider_link")
    private String sliderLink;

    public String getSliderId() {
        return sliderId;
    }

    public void setSliderId(String sliderId) {
        this.sliderId = sliderId;
    }

    public String getSliderPic() {
        return sliderPic;
    }

    public void setSliderPic(String sliderPic) {
        this.sliderPic = sliderPic;
    }

    public String getSliderDes() {
        return sliderDes;
    }

    public void setSliderDes(String sliderDes) {
        this.sliderDes = sliderDes;
    }

    public String getSliderLink() {
        return sliderLink;
    }

    public void setSliderLink(String sliderLink) {
        this.sliderLink = sliderLink;
    }
}
