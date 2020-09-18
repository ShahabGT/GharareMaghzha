package ir.ghararemaghzha.game.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {

    companion object {
        private const val BASE_URL = "https://ghararehmaghzha.ir/api/"
    }


    fun <Api> getApi(
            api: Class<Api>
    ): Api {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(api)
    }


}