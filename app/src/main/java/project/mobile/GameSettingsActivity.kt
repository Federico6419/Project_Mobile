package project.mobile

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.*
import java.util.*

class GameSettingsActivity : AppCompatActivity(), LocationListener {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable
    lateinit var locationManager : LocationManager          //Define Location Manager

    var isExecuted = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        supportActionBar?.setTitle("                     Death Planes")     //Define the name of the application


        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(this@GameSettingsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION) !==
            PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@GameSettingsActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@GameSettingsActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                Log.i("ELSE", "OK")
                ActivityCompat.requestPermissions(this@GameSettingsActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        var color = "red"
        var bullet = "normal"

        //Get the uid of the current user
        val uid = firebaseAuth.uid

        uid?.let {
            setContentView(R.layout.activity_game_settings_opponent)

            //Connecting to Firebase Database
            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

            //Set the opponent's name
            var opponentNameView = findViewById(R.id.Opponent) as TextView
            val opponentNameReference = database.getReference("Users/$current_id/Opponent")

            //Get the opponent, if there is one set it
            opponentNameReference.get().addOnSuccessListener {
                if(it.value != null) {
                    opponentNameView.text = "Current opponent: " + it.value.toString()
                    var usernameText = findViewById(R.id.Username) as EditText
                    usernameText.setText("")    //NON FUNZIONA
                }
            }

            //Change opponent
            val opponentButton = findViewById(R.id.SubmitButton) as Button
            opponentButton.setOnClickListener() {
                val user = findViewById(R.id.Username) as EditText

                val numUsersReference = database.getReference("NumberOfUsers")
                numUsersReference.get().addOnSuccessListener {
                    var numberOfUsers = it.value.toString().toInt()

                    var found = false
                    var referenceUsername: DatabaseReference

                    for (i in 1..numberOfUsers) {
                        referenceUsername =
                            database.getReference("Users/$i/Username")    //Reference to username in the Database

                        referenceUsername.get().addOnSuccessListener {
                            if ((it.value.toString() == user.text.toString()) and (!found)) {
                                found = true
                                //If username already exists
                                opponentNameReference.setValue(user.text.toString())
                                opponentNameView.text = "Current opponent: " + user.text
                            } else if ((i == numberOfUsers) and !found) {
                                //If username does not exist
                                Toast.makeText(this, "This username doesn't exists", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }

            //Remove opponent
            val removeButton = findViewById(R.id.RemoveButton) as Button
            removeButton.setOnClickListener() {
                val opponentNameReference = database.getReference("Users/$current_id/Opponent")
                opponentNameReference.removeValue()
                var opponentNameView = findViewById(R.id.Opponent) as TextView
                opponentNameView.text = "Current opponent: None"
            }

        } ?: run {
            setContentView(R.layout.activity_game_settings)

            val signUpButton = findViewById(R.id.SignUpButton) as Button
            signUpButton.setOnClickListener {
                intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            //Change Remove Opponent button in Sign In button
            val signInButton = findViewById(R.id.SignInButton) as Button
            signInButton.setOnClickListener() {
                intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
        }

        //Airplane selection management
        val airplane1: ImageButton = findViewById(R.id.Airplane1) as ImageButton
        airplane1.setImageResource(R.drawable.airplaneredsigned)

        val airplane2: ImageButton = findViewById(R.id.Airplane2) as ImageButton
        airplane2.setImageResource(R.drawable.airplaneblue)

        val airplane3: ImageButton = findViewById(R.id.Airplane3) as ImageButton
        airplane3.setImageResource(R.drawable.airplanegreen)

        airplane1.setOnClickListener() {
            airplane1.setImageResource(R.drawable.airplaneredsigned)
            airplane2.setImageResource(R.drawable.airplaneblue)
            airplane3.setImageResource(R.drawable.airplanegreen)
            color = "red"
            music.playChooseSound(this)
        }

        airplane2.setOnClickListener() {
            airplane1.setImageResource(R.drawable.airplane)
            airplane2.setImageResource(R.drawable.airplanebluesigned)
            airplane3.setImageResource(R.drawable.airplanegreen)
            color = "blue"
            music.playChooseSound(this)
        }

        airplane3.setOnClickListener() {
            airplane1.setImageResource(R.drawable.airplane)
            airplane2.setImageResource(R.drawable.airplaneblue)
            airplane3.setImageResource(R.drawable.airplanegreensigned)
            color = "green"
            music.playChooseSound(this)
        }


        //Bullet selection management
        val bullet1: ImageButton = findViewById(R.id.Bullet1) as ImageButton

        val bullet2: ImageButton = findViewById(R.id.Bullet2) as ImageButton

        //val bullet3: ImageButton = findViewById(R.id.Bullet3) as ImageButton

        bullet1.setOnClickListener() {
            bullet1.setImageResource(R.drawable.bulletsigned)
            bullet2.setImageResource(R.drawable.lasersettings)
            //bullet3.setImageResource(R.drawable.bullet)
            bullet = "normal"
            music.playChooseSound(this)
        }

        bullet2.setOnClickListener() {
            bullet1.setImageResource(R.drawable.bulletsettings)
            bullet2.setImageResource(R.drawable.lasersigned)
            //bullet3.setImageResource(R.drawable.bullet)
            bullet = "laser"
            music.playChooseSound(this)
        }

        val weatherButton = findViewById(R.id.WeatherButton) as Button
        weatherButton.setOnClickListener {
            weatherButton.visibility = View.INVISIBLE
            var processBar = findViewById(R.id.progress_loader) as ProgressBar
            var loadingText = findViewById(R.id.LoadingText) as TextView
            processBar.visibility = View.VISIBLE
            loadingText.visibility = View.VISIBLE

            //Manage points after loading text
            var loading = findViewById(R.id.LoadingText) as TextView
            var numPoints = 1
            val timer = Timer()
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    Handler(Looper.getMainLooper()).post(java.lang.Runnable {
                        if (numPoints == 4) numPoints = 1
                        loading.text = "Downloading weather" + ".".repeat(numPoints)
                        numPoints += 1
                    })
                }
            }, 10, 600)

            GlobalScope.launch{
                getW()
            }
        }

        val removeWeatherButton = findViewById(R.id.RemoveWeatherButton) as Button
        removeWeatherButton.setOnClickListener {
            weather = ""
            removeWeatherButton.visibility = View.INVISIBLE
            weatherButton.visibility = View.VISIBLE
            val weatherText = findViewById(R.id.WeatherText) as TextView
            weatherText.text = "Weather: Default"
        }

        if(weather != ""){
            removeWeatherButton.visibility = View.VISIBLE
            val weatherText = findViewById(R.id.WeatherText) as TextView
            weatherText.text = "Weather: " + weather
        }

        //Start button management
        val startButton = findViewById(R.id.StartButton) as ImageButton
        startButton.setOnClickListener() {
            var processBar = findViewById(R.id.progress_loader) as ProgressBar
            if (processBar.visibility == View.INVISIBLE) {
                setContentView(R.layout.activity_game_settings_loading)

                intent = Intent(this, GameActivity::class.java)
                intent.putExtra("Color", color)
                intent.putExtra("Bullet", bullet)
                startActivity(intent)
            } else{
                Toast.makeText(this@GameSettingsActivity, "Wait, downloading weather", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        val backButton = findViewById(R.id.BackButton) as ImageButton
        backButton.setOnClickListener() {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    /////// function used to get the weather and so set the layout of the game ////////////////////////
    suspend fun getW() {

        while(!isExecuted){}

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
                    Toast.makeText(this@GameSettingsActivity, "Retrofit error", Toast.LENGTH_SHORT)
                        .show()
                }
                var processBar = findViewById(R.id.progress_loader) as ProgressBar
                var loadingText = findViewById(R.id.LoadingText) as TextView
                processBar.visibility = View.INVISIBLE
                loadingText.visibility = View.INVISIBLE
                var weatherText = findViewById(R.id.WeatherText) as TextView
                weatherText.text = "Weather: " + weather
                var removeWeatherButton = findViewById(R.id.RemoveWeatherButton) as Button
                removeWeatherButton.visibility = View.VISIBLE
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
                        isExecuted = true
                    }

                } else {

                    Log.e("RETROFIT_ERROR OF LOCATION", response.code().toString())

                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((ContextCompat.checkSelfPermission(this@GameSettingsActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION) ===
                                PackageManager.PERMISSION_GRANTED)) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5f, this)
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

}
