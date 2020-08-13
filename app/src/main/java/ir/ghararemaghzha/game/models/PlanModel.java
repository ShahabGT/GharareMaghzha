package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

public class PlanModel {
//    "plan_id": "0",
//            "plan_count": "100",
//            "plan_price": "0"

    @SerializedName("planId")
    private String plan_id;

    @SerializedName("planCount")
    private String plan_count;

    @SerializedName("plan_price")
    private String planPrice;


    public String getPlan_id() {
        return plan_id;
    }

    public void setPlan_id(String plan_id) {
        this.plan_id = plan_id;
    }

    public String getPlan_count() {
        return plan_count;
    }

    public void setPlan_count(String plan_count) {
        this.plan_count = plan_count;
    }

    public String getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(String planPrice) {
        this.planPrice = planPrice;
    }
}
