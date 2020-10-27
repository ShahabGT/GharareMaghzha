package ir.ghararemaghzha.game.data

import okhttp3.ConnectionSpec
import okhttp3.Dispatcher
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {

    companion object {
        private const val BASE_URL = "https://ghararehmaghzha.ir/api/"
    }


    fun <Api> getApi(
            api: Class<Api>
    ): Api {
        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 2

        val interceptor = Interceptor { chain: Interceptor.Chain -> chain.proceed(chain.request()) }
        val client = OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .addInterceptor(interceptor)
                .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
                .build()
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(api)
    }


}