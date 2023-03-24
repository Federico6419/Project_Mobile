package project.mobile

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import android.Manifest
import android.content.Context
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.net.toUri
import com.google.firebase.storage.ktx.storage
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
public var userimage: Uri? = null

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity(), LocationListener{
    lateinit var locationManager : LocationManager          //Define Location Manager
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable
    val storage = Firebase.storage      //Firebase Storage variable

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication


        //Music management
        music.playSoundMenu(this)

        //Location Permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {

        }

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        supportActionBar?.setTitle("                     Death Planes")     //Define the name of the application

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5f,this)

        //Get the uid of the current user
        val uid = firebaseAuth.uid

        //Check if the user id is null or not
        uid?.let {               //If user id is not null, manage the listeners of the signed homepage
            setContentView(R.layout.activity_main_signed)

            var imageView = findViewById(R.id.iv_capture) as ImageView

            // Create a storage reference from our app
            var storageRef = storage.reference

            // Create a reference with an initial file path and name
            val userImageRef = storageRef.child("Images/"+ current_username)
            //val userImageRef = storageRef.child("Images/166395")

            val localFile = File.createTempFile("images", "jpg")

            userImageRef.getFile(localFile).addOnSuccessListener {
                // Local temp file has been created
                userimage = localFile.toUri()
                imageView.setImageURI(userimage)
                Log.i("prova",it.toString())
            }.addOnFailureListener {
                // Handle any errors
                imageView.setImageResource(R.drawable.profileimage)
                Log.i("error",it.toString())
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
            intent = Intent(this, GameSettingsActivity::class.java)
            //intent.putExtra("Weather",weather)
            startActivity(intent)
        }

        //Leaderboard button listener
        val leaderboardButton = findViewById(R.id.LeaderboardButton) as ImageButton
        leaderboardButton.setOnClickListener() {
            intent = Intent(this, LeaderboardActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()

        //Music management when resuming the homepage
        music.playSoundMenu(this)
    }

    //Ask current location
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) === PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    //Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    /////// function used to get the weather and so set the layout of the game ////////////////////////
    suspend fun getW() {
        //var retrofit = Request_Api.getInstance()
        val weatherApi = Request_Api().retrofit.create(ApiInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {

            // Do the GET request and get response
            var response = weatherApi.getWeather("5a8f72b7d96a46caba6120024222612", city,"no")

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    val items = response.body()
                    if (items != null) {
                        //Log.i("weather", items.current?.condition?.text.toString())
                        weather = items.current?.condition?.text.toString()
                        Log.i("weat", weather)
                    }

                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        val location = Request_Api_Location().retrofit2.create(ApiInterface_Location::class.java)
        CoroutineScope(Dispatchers.IO).launch {

            // Do the GET request and get response
            var response = location.getLocation(p0.latitude.toString(),p0.longitude.toString(),"f5167fb93df14a64ad6e6e3f28d05443")

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    val items = response.body()
                    if (items != null) {
                        Log.i("city", items.features?.get(0)?.properties?.city.toString())
                        city = items.features?.get(0)?.properties?.city.toString()
                        GlobalScope.launch {
                            getW()
                        }
                    }

                } else {

                    Log.e("RETROFIT_ERROR OF LOCATION", response.code().toString())

                }
            }
        }

    }

}