package project.mobile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.annotations.SerializedName


data class Difference_Json(
    @SerializedName("winner")
    var winner: String?,
    @SerializedName("difference")
    var difference: Int?
)

class GameSettingsActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        supportActionBar?.setTitle("                     Death Planes")     //Define the name of the application

        var color = "red"
        var bullet = "normal"

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


        //Opponent management
        //Get the uid of the current user
        val uid = firebaseAuth.uid
        //Check if the user id is null or not
        uid?.let {             //If user id is not null, display the choose of the opponent
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
            //Change Set Opponent button in Sign Up button
            var opponentNameView = findViewById(R.id.Opponent) as TextView
            opponentNameView.text = "You have to be logged to choose your opponent"
            val user = findViewById(R.id.Username) as EditText
            user.isVisible = false
            user.isClickable = false
            val opponentButton = findViewById(R.id.SubmitButton) as Button
            opponentButton.text = "Sign Up"
            opponentButton.setOnClickListener{
                intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            //Change Remove Opponent button in Sign In button
            val removeButton = findViewById(R.id.RemoveButton) as Button
            removeButton.text = "Sign In"
            removeButton.setOnClickListener() {
                intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }
        }


        //Start button management
        val startButton = findViewById(R.id.StartButton) as ImageButton
        startButton.setOnClickListener() {
            setContentView(R.layout.activity_game_settings_loading)

            intent = Intent(this, GameActivity::class.java)
            intent.putExtra("Color", color)
            intent.putExtra("Bullet", bullet)
            startActivity(intent)
        }
        val houseButton = findViewById(R.id.HouseButton) as ImageButton
        houseButton.setOnClickListener() {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
