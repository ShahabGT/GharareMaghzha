package ir.ghararemaghzha.game.data

import okhttp3.ConnectionSpec
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor(){
    private var retrofit:Retrofit

    init {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 4
        val client = OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addInterceptor { chain -> chain.proceed(chain.request()) }
                .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
                .build();
        retrofit=Retrofit.Builder().baseUrl("https://ghararehmaghzha.ir/api/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build()
    }

    companion object{
        @Volatile
        private var instance: RetrofitClient? = null

        fun getInstance(): RetrofitClient =
                instance ?: synchronized(this) {
                    instance ?: RetrofitClient().also { instance = it }
                }
    }

    fun getApi(): Api {
        return retrofit.create(Api::class.java)
    }
}
