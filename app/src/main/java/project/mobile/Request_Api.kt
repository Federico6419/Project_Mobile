package project.mobile

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import project.mobile.Nested_model as model
import project.mobile.Nested_model_location as model_location

interface ApiInterface {
    @GET("current.json?")
    suspend fun getWeather(@Query("key") key:String,@Query("q") q:String,@Query("aqi") aqi:String): Response<model>
}
class Request_Api {

    lateinit var retrofit : Retrofit

    init {
        val baseUrl = "https://api.weatherapi.com/v1/"

        try {
            retrofit =
                Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create()) // JSON converter to Kotlin object
                    .build()
        }
        catch (e: IOException){

        }
    }

}
