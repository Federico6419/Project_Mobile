package project.mobile

import android.util.Log
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.IOException

interface FlaskInterface {
    @GET("/leaderboard")
    suspend fun getLeaderboard(): Response<Flask_Json>
}

class RequestLeaderboard{

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