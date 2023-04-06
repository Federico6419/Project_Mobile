package project.mobile

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
import android.media.MediaPlayer


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
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import kotlin.math.atan2
import kotlin.properties.Delegates

class GameActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable

    @SuppressLint("WrongThread")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setTitle("                     Death Planes")     //Define the name of the application

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        val color = intent.getStringExtra("Color")
        val bul = intent.getStringExtra("Bullet")

        //Music management
        music.stopSound()
        music.releasePlayer()
        music = MusicManager()       //Music variable(I've restarted music to avoid crashes
        music.playSoundGame(this)
        //Mute management
        if (muted) {
            music.setVolume(0.0f, 0.0f)
        } else {
            music.setVolume(1.0f, 1.0f)
        }


        // get the wheater to set the right layout
        Log.i("USER", current_score.toString())
        Log.i("USER", weather)

        //
        val uid = firebaseAuth.currentUser?.uid
        var logged = false

        uid?.let {
            logged = true
        } ?: run {
            logged = false
        }

        setContentView(MyView(this, weather, color, bul, logged, packageName))
    }
}
