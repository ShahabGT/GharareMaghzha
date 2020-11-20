package ir.ghararemaghzha.game.data;

import java.util.Arrays;
import okhttp3.ConnectionSpec;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {

    private static final String BASE_URL = "https://ghararehmaghzha.ir/api/";
    private static RetrofitClient mInstance;
    private final Retrofit retrofit;

    private RetrofitClient(){
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(3);

        Interceptor interceptor = chain -> chain.proceed(chain.request());
        OkHttpClient client = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addInterceptor(interceptor)
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
                .build();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(){
        if(mInstance == null)
            mInstance = new RetrofitClient();

        return mInstance;
    }

    public Api getApi(){
        return retrofit.create(Api.class);
    }
}
