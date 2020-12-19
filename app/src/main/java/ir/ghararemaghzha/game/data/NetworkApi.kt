package ir.ghararemaghzha.game.data

import ir.ghararemaghzha.game.models.*
import retrofit2.Call
import retrofit2.http.*

interface NetworkApi {


    @FormUrlEncoded
    @POST("validate")
    suspend fun validate(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): VerifyResponse

    @FormUrlEncoded
    @POST("appopen")
    suspend fun appOpen(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): AppOpenResponse


    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
            @Field("name") name: String,
            @Field("number") number: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("verification")
    suspend fun verification(
            @Field("number") number: String,
            @Field("code") code: String,
            @Field("fb_token") fbToken: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
            @Field("number") number: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("resend")
    suspend fun resend(
            @Field("number") number: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("time")
    suspend fun getServerTime(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): TimeResponse


    @FormUrlEncoded
    @POST("chat")
    suspend fun sendMessage(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("message") message: String
    ): TimeResponse

    @FormUrlEncoded
    @POST("messages")
    suspend fun getMessages(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("date") lastUpdate: String
    ): ChatResponse


    @FormUrlEncoded
    @POST("questions")
    suspend fun getQuestions(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("start") start: Int,
            @Field("size") size: Int
    ): QuestionResponse

    @FormUrlEncoded
    @POST("highscore")
    suspend fun getHighscoreList(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): HighscoreResponse

    @FormUrlEncoded
    @POST("buyhistory")
    suspend fun getBuyHistory(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): BuyHistoryResponse

    @FormUrlEncoded
    @POST("buy/initbuy")
    suspend fun initBuy(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("plan") plan: String,
            @Field("influencer_id") influencerId: String,
            @Field("influencer_amount") influencerAmount: String,
            @Field("amount") amount: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("influencer")
    suspend fun searchInfluencer(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("influencer_code") code: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("answer")
    suspend fun answerQuestion(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("question_id") questionId: String,
            @Field("user_answer") userAnswer: String,
            @Field("user_answer_booster") booster: String,
            @Field("season") season: Int
    ): GeneralResponse

    @FormUrlEncoded
    @POST("score")
    suspend fun sendScore(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("score") score: String,
            @Field("season") season: Int
    ): GeneralResponse

    @FormUrlEncoded
    @POST("serverquestions")
    suspend fun sendQuestionCount(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("count") count: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("avatar")
    suspend fun alterAvatar(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("option") option: String,
            @Field("image") image: String,
            @Field("avatar") avatar: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("profile")
    suspend fun updateProfile(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("user_name") userName: String,
            @Field("user_email") userEmail: String,
            @Field("user_bday") userBday: String,
            @Field("user_sex") userSex: String,
            @Field("invite") invite: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("plans")
    suspend fun getPlans(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): PlanResponse

    @FormUrlEncoded
    @POST("nitro")
    suspend fun getNitro(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): NitroResponse

    @FormUrlEncoded
    @POST("invites")
    suspend fun getInvites(
            @Header("Authorization") Token: String,
            @Field("number") number: String
    ): GeneralResponse

    @GET("ad")
    suspend fun getSlider(): SliderResponse

    @FormUrlEncoded
    @POST("userdetails")
    suspend fun getUserDetails(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("user_id") userId: String
    ): UserDetailsResponse

    @FormUrlEncoded
    @POST("report")
    suspend fun report(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("question_id") questionId: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("info")
    suspend fun info(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("info") info: String
    ): GeneralResponse

    @FormUrlEncoded
    @POST("scorebooster")
    suspend fun scoreBooster(
            @Header("Authorization") Token: String,
            @Field("number") number: String,
            @Field("scorebooster") scoreBooster: String,
    ): GeneralResponse
}


