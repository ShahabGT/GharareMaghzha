package ir.ghararemaghzha.game.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class QuestionModel(
        @PrimaryKey
        @SerializedName("question_id") var questionId: String="",
        @SerializedName("sort_id") var sortId: Int=0,
        @SerializedName("question_text") var questionText: String="",
        @SerializedName("question_a1") var questionA1: String="",
        @SerializedName("question_a2") var questionA2: String="",
        @SerializedName("question_a3") var questionA3: String="",
        @SerializedName("question_a4") var questionA4: String="",
        @SerializedName("question_correct") var questionCorrect: String="",
        @SerializedName("question_points") var questionPoints: String="",
        @SerializedName("user_answer") var userAnswer: String="",
        @SerializedName("user_answer_booster") var userBooster: String="",

        var visible: Boolean=false,
        var uploaded: Boolean=false,
        var bought: Boolean=false

) : RealmObject()
