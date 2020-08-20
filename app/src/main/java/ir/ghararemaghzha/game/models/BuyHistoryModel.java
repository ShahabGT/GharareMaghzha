package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class BuyHistoryModel {

//                "buy_plan_id": "3",
//                "merchant_id": "qwerf4234",
//                "user_id": "5",
//                "plan": "2",
//                "amount": "15000",
//                "influencer_id": "",
//                "influencer_amount": "",
//                "orderid": "15999848498",
//                "rescode": "8498498",
//                "valid": "1",
//                "date": "2020-07-29 22:03:10",
//                "description": "season1",
//                "influencer_name": ""

    @SerializedName("buy_plan_id")
    private String buyPlanId;

    @SerializedName("merchant_id")
    private String merchantId;

    @SerializedName("user_id")
    private String userId;

    private String plan;

    private String amount;

    @SerializedName("influencer_id")
    private String influencerId;

    @SerializedName("influencer_code")
    private String influencerCode;

    @SerializedName("influencer_name")
    private String influencerName;

    @SerializedName("influencer_amount")
    private String influencerAmount;

    @SerializedName("orderid")
    private String orderId;

    @SerializedName("rescode")
    private String resCode;

    private String valid;

    private String date;

    private String description;

    public String getBuyPlanId() {
        return buyPlanId;
    }

    public void setBuyPlanId(String buyPlanId) {
        this.buyPlanId = buyPlanId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getInfluencerId() {
        return influencerId;
    }

    public void setInfluencerId(String influencerId) {
        this.influencerId = influencerId;
    }

    public String getInfluencerName() {
        return influencerName;
    }

    public void setInfluencerName(String influencerName) {
        this.influencerName = influencerName;
    }

    public String getInfluencerAmount() {
        return influencerAmount;
    }

    public void setInfluencerAmount(String influencerAmount) {
        this.influencerAmount = influencerAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfluencerCode() {
        return influencerCode;
    }

    public void setInfluencerCode(String influencerCode) {
        this.influencerCode = influencerCode;
    }
}
