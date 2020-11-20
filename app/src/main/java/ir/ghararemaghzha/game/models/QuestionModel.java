package ir.ghararemaghzha.game.models;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class QuestionModel extends RealmObject {
    //    {
//        "question_id": "1",
//            "question_text": "ساختار H2o مربوط به کدام ماده اس؟",
//            "question_a1": "آب",
//            "question_a2": "هوا",
//            "question_a3": "خاک",
//            "question_a4": "آتش",
//            "question_correct": "1",
//            "question_points": "10",
//            "user_answer": "-1"
//            "user_answer_booster": "0"
//    }


    @PrimaryKey
    @SerializedName("question_id")
    private String questionId;

    @SerializedName("sort_id")
    private int sortId;

    @SerializedName("question_text")
    private String questionText;

    @SerializedName("question_a1")
    private String questionA1;

    @SerializedName("question_a2")
    private String questionA2;

    @SerializedName("question_a3")
    private String questionA3;

    @SerializedName("question_a4")
    private String questionA4;

    @SerializedName("question_correct")
    private String questionCorrect;

    @SerializedName("question_points")
    private String questionPoints;

    @SerializedName("user_answer")
    private String userAnswer;

    @SerializedName("user_answer_booster")
    private String userBooster;

    private boolean visible;
    private boolean uploaded;
    private boolean bought;


    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isUploaded() {
        return uploaded;
    }

    public void setUploaded(boolean uploaded) {
        this.uploaded = uploaded;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionA1() {
        return questionA1;
    }

    public void setQuestionA1(String questionA1) {
        this.questionA1 = questionA1;
    }

    public String getQuestionA2() {
        return questionA2;
    }

    public void setQuestionA2(String questionA2) {
        this.questionA2 = questionA2;
    }

    public String getQuestionA3() {
        return questionA3;
    }

    public void setQuestionA3(String questionA3) {
        this.questionA3 = questionA3;
    }

    public String getQuestionA4() {
        return questionA4;
    }

    public void setQuestionA4(String questionA4) {
        this.questionA4 = questionA4;
    }

    public String getQuestionCorrect() {
        return questionCorrect;
    }

    public void setQuestionCorrect(String questionCorrect) {
        this.questionCorrect = questionCorrect;
    }

    public String getQuestionPoints() {
        return questionPoints;
    }

    public void setQuestionPoints(String questionPoints) {
        this.questionPoints = questionPoints;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }

    public String getUserBooster() {
        return userBooster;
    }

    public void setUserBooster(String userBooster) {
        this.userBooster = userBooster;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }
}
