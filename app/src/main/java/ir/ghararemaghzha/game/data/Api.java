package ir.ghararemaghzha.game.data;

import ir.ghararemaghzha.game.models.BuyHistoryResponse;
import ir.ghararemaghzha.game.models.ChatResponse;
import ir.ghararemaghzha.game.models.GeneralResponse;
import ir.ghararemaghzha.game.models.HighscoreResponse;
import ir.ghararemaghzha.game.models.MessageModel;
import ir.ghararemaghzha.game.models.PlanResponse;
import ir.ghararemaghzha.game.models.QuestionResponse;
import ir.ghararemaghzha.game.models.TimeResponse;
import ir.ghararemaghzha.game.models.VerifyResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Api {


    @FormUrlEncoded
    @POST("verify")
    Call<VerifyResponse> verify(
            @Header("Authorization") String Token,
            @Field("number") String number
    );


    @FormUrlEncoded
    @POST("register")
    Call<GeneralResponse> registerUser(
            @Field("name") String name,
            @Field("number") String number
    );

    @FormUrlEncoded
    @POST("verification")
    Call<GeneralResponse> verification(
            @Field("number") String number,
            @Field("code") String code,
            @Field("fb_token") String fbToken
    );

    @FormUrlEncoded
    @POST("login")
    Call<GeneralResponse> login(
            @Field("number") String number
    );
    @FormUrlEncoded
    @POST("resend")
    Call<GeneralResponse> resend(
            @Field("number") String number
    );

    @FormUrlEncoded
    @POST("time")
    Call<TimeResponse> getServerTime(
            @Header("Authorization") String Token,
            @Field("number") String number
    );
    @FormUrlEncoded
    @POST("updatetime")
    Call<GeneralResponse> updateLastUpdate(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("last_update") String lastUpdate
    );
    @FormUrlEncoded
    @POST("chat")
    Call<TimeResponse> sendMessage(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("message") String message
    );

    @FormUrlEncoded
    @POST("messages")
    Call<ChatResponse> getMessages(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("date") String lastUpdate);

    @FormUrlEncoded
    @POST("messages")
    Call<ChatResponse> getMessages(
            @Header("Authorization") String Token,
            @Field("number") String number);

    @FormUrlEncoded
    @POST("questions")
    Call<QuestionResponse> getQuestions(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("start") String start,
            @Field("size") String size
    );

    @FormUrlEncoded
    @POST("highscore")
    Call<HighscoreResponse> getHighscoreList(
            @Header("Authorization") String Token,
            @Field("number") String number
    );

    @FormUrlEncoded
    @POST("buyhistory")
    Call<BuyHistoryResponse> getBuyHistory(
            @Header("Authorization") String Token,
            @Field("number") String number
    );

    @FormUrlEncoded
    @POST("buy/initbuy")
    Call<GeneralResponse> initBuy(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("plan") String plan,
            @Field("influencer_id") String influencerId,
            @Field("influencer_amount") String influencerAmount,
            @Field("amount") String amount
    );

    @FormUrlEncoded
    @POST("influencer")
    Call<GeneralResponse> searchInfluencer(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("influencer_code") String code
    );

    @FormUrlEncoded
    @POST("answer")
    Call<GeneralResponse> answerQuestion(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("question_id") String questionId,
            @Field("user_answer") String userAnswer
    );

    @FormUrlEncoded
    @POST("score")
    Call<GeneralResponse> sendScore(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("score") String score);

    @FormUrlEncoded
    @POST("serverquestions")
    Call<GeneralResponse> sendQuestionCount(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("count") String count);

    @FormUrlEncoded
    @POST("avatar")
    Call<GeneralResponse> alterAvatar(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("option") String option,
            @Field("image") String image);

    @FormUrlEncoded
    @POST("profile")
    Call<GeneralResponse> updateProfile(
            @Header("Authorization") String Token,
            @Field("number") String number,
            @Field("user_name") String userName,
            @Field("user_email") String userEmail,
            @Field("user_bday") String userBday,
            @Field("user_sex") String userSex,
            @Field("invite") String invite);

    @FormUrlEncoded
    @POST("plans")
    Call<PlanResponse> getPlans(
            @Header("Authorization") String Token,
            @Field("number") String number);


    @FormUrlEncoded
    @POST("invites")
    Call<GeneralResponse> getInvites(
            @Header("Authorization") String Token,
            @Field("number") String number);

}


