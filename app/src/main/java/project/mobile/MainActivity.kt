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

var weather = ""        //Current weather variable
var city = ""           //Current city variable

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class MainActivity : AppCompatActivity(), LocationListener{
    lateinit var locationManager : LocationManager          //Define Location Manager
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

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

        //Get values from the intent
        val username = intent.getStringExtra("Username")
        val score = intent.getStringExtra("Score")
        val id = intent.getStringExtra("ID")
        val uid = firebaseAuth.uid

        Log.i("FIRE", id.toString())

        //Check if the user id is null or not
        uid?.let {               //If user id is not null
            setContentView(R.layout.activity_main_signed)

            val usernameText: TextView = findViewById(R.id.Username) as TextView
            usernameText.text = username

            val settingsButton = findViewById(R.id.SettingsButton) as ImageButton
            settingsButton.setOnClickListener() {
                intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra("activity", "MainActivity")
                intent.putExtra("Username", username)
                intent.putExtra("ID", id)
                intent.putExtra("Weather",weather)
                intent.putExtra("Score", score)
                startActivity(intent)
            }

            val logoutButton = findViewById(R.id.LogOutButton) as ImageButton
            logoutButton.setOnClickListener() {
                firebaseAuth.signOut()
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            val startButton = findViewById(R.id.StartButton) as ImageButton
            startButton.setOnClickListener() {
                intent = Intent(this, GameSettingsActivity::class.java)
                intent.putExtra("activity", "MainActivity")
                intent.putExtra("Username",username)
                intent.putExtra("ID",id)
                intent.putExtra("Weather",weather)
                intent.putExtra("Score",score)
                startActivity(intent)
            }
            val leaderboardButton = findViewById(R.id.LeaderboardButton) as ImageButton
            leaderboardButton.setOnClickListener() {
                intent = Intent(this, LeaderboardActivity::class.java)
                intent.putExtra("activity", "MainActivity")
                intent.putExtra("Username", username)
                intent.putExtra("ID", id)
                intent.putExtra("Score",score)
                startActivity(intent)
            }
            val houseButton = findViewById(R.id.HouseButton) as ImageButton
            houseButton.setOnClickListener() {
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("activity", "MainActivity")
                intent.putExtra("Username",username)
                intent.putExtra("ID",id)
                intent.putExtra("Weather",weather)
                intent.putExtra("Score",score)
                startActivity(intent)
            }
            val backButton = findViewById(R.id.BackButton) as ImageButton
            backButton.setOnClickListener() {
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("activity", "MainActivity")
                intent.putExtra("Username",username)
                intent.putExtra("ID",id)
                intent.putExtra("Weather",weather)
                intent.putExtra("Score",score)
                startActivity(intent)
            }
        } ?: run {
            setContentView(R.layout.activity_main)

            val startButton = findViewById(R.id.StartButton) as ImageButton
            startButton.setOnClickListener() {
                intent = Intent(this, GameSettingsActivity::class.java)
                intent.putExtra("Weather",weather)
                startActivity(intent)
            }
            val signUpButton = findViewById(R.id.SignUpButton) as ImageButton
            signUpButton.setOnClickListener(){
                intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }
            val signInButton = findViewById(R.id.SignInButton) as ImageButton
            signInButton.setOnClickListener(){
                intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
            val leaderboardButton = findViewById(R.id.LeaderboardButton) as ImageButton
            leaderboardButton.setOnClickListener() {
                intent = Intent(this, LeaderboardActivity::class.java)
                startActivity(intent)
            }
            val houseButton = findViewById(R.id.HouseButton) as ImageButton
            houseButton.setOnClickListener() {
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("activity", "MainActivity")
                startActivity(intent)
            }
            val backButton = findViewById(R.id.BackButton) as ImageButton
            backButton.setOnClickListener() {
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("activity", "MainActivity")
                startActivity(intent)
            }
        }

    }

    //Ask Location
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
                        Log.i("weather", items.current?.condition?.text.toString())
                        weather = items.current?.condition?.text.toString()
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