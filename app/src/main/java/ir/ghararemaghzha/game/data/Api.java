package ir.ghararemaghzha.game.data;

import ir.ghararemaghzha.game.models.GeneralResponse;
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
            @Header("Authorization") String Token
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

    @GET("time")
    Call<GeneralResponse> getServerTime();


}


