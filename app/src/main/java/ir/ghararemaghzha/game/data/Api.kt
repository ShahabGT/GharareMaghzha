package ir.ghararemaghzha.game.data

import ir.ghararemaghzha.game.models.*
import retrofit2.Call
import retrofit2.http.*

interface Api {


    @FormUrlEncoded
    @POST("validate")
    fun validate(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): Call<VerifyResponse>

    @FormUrlEncoded
    @POST("appopen")
    fun appOpen(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): Call<AppOpenResponse>


    @FormUrlEncoded
    @POST("register")
    fun registerUser(
            @Field("name") name: String,
            @Field("number") number: String
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("verification")
    fun verification(
            @Field("number") number: String,
            @Field("code") code: String,
            @Field("fb_token") fbToken: String
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
            @Field("number") number: String
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("resend")
    fun resend(
            @Field("number") number: String
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("time")
    fun getServerTime(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): Call<TimeResponse>

    @FormUrlEncoded
    @POST("updatetime")
    fun updateLastUpdate(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("last_update") lastUpdate: String
    ): Call<GeneralResponse>


    @FormUrlEncoded
    @POST("chat")
    fun sendMessage(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("message") message: String
    ): Call<TimeResponse>

    @FormUrlEncoded
    @POST("messages")
    fun getMessages(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("date") lastUpdate: String
    ): Call<ChatResponse>


    @FormUrlEncoded
    @POST("questions")
    fun getQuestions(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("start") start: String,
            @Field("size") size: String
    ): Call<QuestionResponse>

    @FormUrlEncoded
    @POST("highscore")
    fun getHighscoreList(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): Call<HighscoreResponse>

    @FormUrlEncoded
    @POST("buyhistory")
    fun getBuyHistory(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): Call<BuyHistoryResponse>

    @FormUrlEncoded
    @POST("buy/initbuy")
    fun initBuy(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("plan") plan: String,
            @Field("influencer_id") influencerId: String,
            @Field("influencer_amount") influencerAmount: String,
            @Field("amount") amount: String
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("influencer")
    fun searchInfluencer(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("influencer_code") code: String
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("answer")
    fun answerQuestion(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("question_id") questionId: String,
            @Field("user_answer") userAnswer: String,
            @Field("user_answer_booster") booster: String,
            @Field("season") season: Int,
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("score")
    fun sendScore(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("score") score: String,
            @Field("season") season: Int,
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("serverquestions")
    fun sendQuestionCount(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("count") count: String
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("avatar")
    fun alterAvatar(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("option") option: String,
            @Field("image") image: String,
            @Field("avatar") avatar: String
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("profile")
    fun updateProfile(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("user_name") userName: String,
            @Field("user_email") userEmail: String,
            @Field("user_bday") userBday: String,
            @Field("user_sex") userSex: String,
            @Field("invite") invite: String
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("plans")
    fun getPlans(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): Call<PlanResponse>


    @FormUrlEncoded
    @POST("invites")
    fun getInvites(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): Call<GeneralResponse>

    @GET("ad")
    fun getSlider(): Call<SliderResponse>

    @FormUrlEncoded
    @POST("userdetails")
    fun getUserDetails(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("user_id") userId: String
    ): Call<UserDetailsResponse>

    @FormUrlEncoded
    @POST("report")
    fun report(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("question_id") questionId: String,
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("info")
    fun info(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("info") info: String,
    ): Call<GeneralResponse>

    @FormUrlEncoded
    @POST("scorebooster")
    fun scoreBooster(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("scorebooster") scoreBooster: Int,
    ): Call<GeneralResponse>

}


