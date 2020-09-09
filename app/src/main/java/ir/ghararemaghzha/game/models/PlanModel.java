package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class PlanModel {
//    "plan_id": "0",
//            "plan_count": "100",
//            "plan_price": "0"

    @SerializedName("plan_id")
    private String planId;

    @SerializedName("plan_count")
    private String planCount;

    @SerializedName("plan_price")
    private String planPrice;

    private String value;


    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanCount() {
        return planCount;
    }

    public void setPlanCount(String planCount) {
        this.planCount = planCount;
    }

    public String getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(String planPrice) {
        this.planPrice = planPrice;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
