package project.mobile

import android.util.Log
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

interface DifferenceInterface {
    @GET("/difference")
    suspend fun getDifference(@Query("username1") username1: String?, @Query("username2") username2: String?): Response<Difference_Json>
}

class Request_Difference {
    lateinit var retrofit : Retrofit

    init {

        val baseUrl = "https://Federico110098.pythonanywhere.com/"
        try {

            retrofit =
                Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create()) // JSON converter to Kotlin object
                    .build()
        }
        catch (e: IOException){
            Log.i("RETROFIT", "ERRORE")
        }
    }
}