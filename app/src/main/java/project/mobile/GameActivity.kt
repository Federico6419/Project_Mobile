package project.mobile//
//
import android.annotation.SuppressLint
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import android.widget.Toast
import kotlinx.coroutines.*


import android.graphics.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.util.Log
import androidx.core.graphics.withTranslation
import java.util.*
import kotlin.math.atan2


class GameActivity : AppCompatActivity() {

    var w = ""
    @SuppressLint("WrongThread")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val username=intent.getStringExtra("Username")
        val password=intent.getStringExtra("Password")
        val w = intent.getStringExtra("Weather")
        val old_score = intent.getStringExtra("Score")?.toInt()
        val id = intent.getStringExtra("ID")
        val opponent = intent.getStringExtra("Opponent")
        val color = intent.getStringExtra("Color")
        val bul = intent.getStringExtra("Bullet")


        GlobalScope.launch {
            getDifference(username, opponent)
        }
        //Log.i("prova1",w.toString())
        // get the wheater to set the right layout
        setContentView(MyView(this,w,old_score,id,username,password,color,bul))
    }

    override fun onPause() {
        super.onPause()

    }

    override fun onResume() {
        super.onResume()
    }


}

suspend fun getDifference(username1: String?, username2: String?) {
    val differenceApi = Request_Difference().retrofit.create(DifferenceInterface::class.java)
    CoroutineScope(Dispatchers.IO).launch {

        // Do the GET request and get response
        var response = differenceApi.getDifference(username1, username2)

        Log.i("opponent", username2.toString())

        var winner = ""
        var difference = 0

        withContext(Dispatchers.Main) {
            if (response.isSuccessful) {

                val items = response.body()
                if (items != null) {
                    winner = items.winner.toString()
                    difference = items.difference!!
                }
                Log.i("opponent", winner)
                Log.i("opponent", difference.toString())

            } else {

                Log.e("RETROFIT_ERROR", response.code().toString())

            }
        }
    }
}
