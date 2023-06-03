package project.mobile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import java.io.File


//Public variables for current weather and city services
public var weather = ""        //Current weather variable
public var city = ""           //Current city variable

//Public variables for music management
public var music = MusicManager()       //Music variable
public var muted = false                //Mute boolean variable

//Public variables of the current user
public var current_username = ""        //Username of the current user
public var current_id = ""              //ID of the current user
public var current_score = 0           //Score of the current user
public var userimage: Uri? = null       //Variable that will contain the URI of the image

//Variable that says if the application is executed in this moment
public var justExecuted = true

var hasphoto = false

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity(), LifecycleObserver {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable
    val storage = Firebase.storage      //Firebase Storage variable

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        Log.i("WEATHER", weather)

        //Music management
        music.playSoundMenu(this)

        //Manage music when app goes in background
        var lifecycleEventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    //Pause music when app goes in background
                    music.pauseSound()
                }
                Lifecycle.Event.ON_START -> {
                    //Resume music when app goes in foreground
                    music.playSoundMenu(this)
                }
                else -> {}
            }
        }
        ProcessLifecycleOwner.get().getLifecycle().addObserver(lifecycleEventObserver)

        supportActionBar?.setTitle("                     Death Planes")     //Define the name of the application

        //If the previous user is logged, sign him out
        if((firebaseAuth.currentUser?.email != null) and justExecuted){
            firebaseAuth.signOut()          //Sign out from Firebase Authentication
        }

        //Get the uid of the current user
        val uid = firebaseAuth.uid
        justExecuted = false

        //Check if the user id is null or not
        uid?.let {               //If user id is not null, manage the listeners of the signed homepage
            setContentView(R.layout.activity_main_signed)

            val changephotoButton = findViewById(R.id.iv_capture_button) as ImageButton

            if(hasphoto){
                changephotoButton.setImageURI(userimage)
            }else {
                //////////download the image of the user from storage and set to the userimage view


                // Create a storage reference from our app
                var storageRef = storage.reference

                // Create a reference with an initial file path and name
                val userImageRef = storageRef.child("Images/" + current_username)
                //val userImageRef = storageRef.child("Images/166395")

                val localFile = File.createTempFile("images", "jpg")

                userImageRef.getFile(localFile).addOnSuccessListener {
                    // Local temp file has been created
                    userimage = localFile.toUri()
                    changephotoButton.setImageURI(userimage)
                    hasphoto = true
                    Log.i("prova", it.toString())
                }.addOnFailureListener {
                    // Handle any errors
                    //imageView.setImageResource(R.drawable.profileimage)
                    changephotoButton.setImageResource(R.drawable.profileimage)
                    changephotoButton.getLayoutParams().height = 230
                    changephotoButton.getLayoutParams().width = 230
                    Log.i("error", it.toString())
                }
            }


            ////modify photo profile intent
            changephotoButton.setOnClickListener() {
                intent = Intent(this, ModifyPhoto::class.java)
                startActivity(intent)
            }


            val usernameText: TextView = findViewById(R.id.Username) as TextView
            usernameText.text = current_username

            val settingsButton = findViewById(R.id.SettingsButton) as ImageButton
            settingsButton.setOnClickListener() {
                intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            val logoutButton = findViewById(R.id.LogOutButton) as ImageButton
            logoutButton.setOnClickListener() {
                firebaseAuth.signOut()          //Sign out from Firebase Authentication

                //Reset the variables of the current user
                current_username = ""        //Username of the current user
                current_id = ""              //ID of the current user
                current_score = 0           //Score of the current user

                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            val leaderboardButton = findViewById(R.id.LeaderboardButton) as ImageButton
            leaderboardButton.setOnClickListener() {
                intent = Intent(this, LeaderboardActivity::class.java)
                startActivity(intent)
            }

        } ?: run {              //If user id is null, manage the listeners of the not signed homepage
            setContentView(R.layout.activity_main)

            //Sign Up button listener
            val signUpButton = findViewById(R.id.SignUpButton) as ImageButton
            signUpButton.setOnClickListener(){
                intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            //Sign In button listener
            val signInButton = findViewById(R.id.SignInButton) as ImageButton
            signInButton.setOnClickListener(){
                intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }

        }

        //Listeners for the buttons that are both in signed and not signed homepage

        //Mute management
        val audioButton = findViewById(R.id.AudioButton) as ImageButton
        if(muted) {
            music.setVolume(0.0f, 0.0f)
            audioButton.setImageResource(R.drawable.audiooff)
        } else{
            music.setVolume(1.0f, 1.0f)
            audioButton.setImageResource(R.drawable.audioon)
        }

        //Audio button listener
        audioButton.setOnClickListener(){
            if(muted) {
                music.setVolume(1.0f, 1.0f)
                audioButton.setImageResource(R.drawable.audioon)
                muted = false
            } else{
                music.setVolume(0.0f, 0.0f)
                audioButton.setImageResource(R.drawable.audiooff)
                muted = true
            }
        }

        //Start button listener
        val startButton = findViewById(R.id.StartButton) as ImageButton
        startButton.setOnClickListener() {
            //runFadeInAnimation()      //ANIMATION
            intent = Intent(this, GameSettingsActivity::class.java)
            startActivity(intent)
        }

        //Leaderboard button listener
        val leaderboardButton = findViewById(R.id.LeaderboardButton) as ImageButton
        leaderboardButton.setOnClickListener() {
            intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        //Manage logout when exit from application
        firebaseAuth.signOut()          //Sign out from Firebase Authentication
    }

    ///////////ANIMATION
    /*fun runFadeInAnimation() {
        var a = AnimationUtils.loadAnimation(this, R.anim.fadein)
        a.reset()
        var ll = findViewById(R.id.Layout) as ConstraintLayout
        ll.clearAnimation()
        ll.startAnimation(a)
    }*/
}