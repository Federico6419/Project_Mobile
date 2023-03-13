package project.mobile

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException
import project.mobile.Nested_model_location as model_location

interface ApiInterface_Location {
    @GET("reverse?")
    suspend fun getLocation(@Query("lat") lat:String,@Query("lon") lon:String,@Query("apiKey") key:String): Response<model_location>
}
class Request_Api_Location {

    lateinit var retrofit2 : Retrofit

    init {
        val baseUrl = "https://api.geoapify.com/v1/geocode/"

        try {
            retrofit2 =
                Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create()) // JSON converter to Kotlin object
                    .build()
        }
        catch (e: IOException){

        }
    }


}