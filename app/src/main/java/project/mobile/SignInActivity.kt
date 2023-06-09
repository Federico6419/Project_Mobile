package project.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class SignInActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable

    //Email, username and password variables
    private var email : String = ""
    private var password : String = ""
    private var username : String = ""
    private var score : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)   //Display the sign in XML

        firebaseAuth = FirebaseAuth.getInstance()

        supportActionBar?.setTitle("                         Death Planes")

        //Music management
        music.playSoundMenu(this)

        //Getting the references to the Views
        var emailView = findViewById(R.id.Email) as EditText
        var passwordView = findViewById(R.id.Password) as EditText
        var resetbutton = findViewById(R.id.ResetButton) as Button
        var submitbutton = findViewById(R.id.SubmitButton) as Button

        //Manage the reset button
        resetbutton.setOnClickListener {
            emailView.setText("")
            passwordView.setText("")
        }

        //Setting the listener of the onClick event of the submit button
        submitbutton.setOnClickListener {
            email = emailView.text.toString()
            password = passwordView.text.toString()

            //Check if the email is not empty
            if(email.isEmpty()){
                Toast.makeText(this, "Email not inserted", Toast.LENGTH_SHORT).show()
            }
            //Check if the password is not empty
            else if(password.isEmpty()){
                Toast.makeText(this, "Password not inserted", Toast.LENGTH_SHORT).show()
            }
            else {
                //Sign in
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            //Take the user id
                            var uid = firebaseAuth.uid

                            //Connect to Firebase Database
                            val database =
                                Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

                            val referenceDB =
                                database.getReference("NumberOfUsers")    //Take the number of users

                            var id = "1"

                            //Get the number of users
                            referenceDB.get().addOnSuccessListener {
                                var numberOfUsers = it.value.toString().toInt()
                                var referenceUid: DatabaseReference

                                for (i in 1..numberOfUsers) {
                                    referenceUid =
                                        database.getReference("Users/$i/UID")    //Reference to username in the Database

                                    referenceUid.get().addOnSuccessListener {
                                        if (it.value.toString() == uid) {
                                            id = i.toString()

                                            //Get reference to Score
                                            val referenceScore =
                                                database.getReference("Users/$id/Score")

                                            //Get the score
                                            referenceScore.get().addOnSuccessListener {
                                                score = it.value.toString()

                                                //Get reference to Username
                                                val referenceUsername =
                                                    database.getReference("Users/$id/Username")

                                                //Get the username
                                                referenceUsername.get().addOnSuccessListener {
                                                    username = it.value.toString()

                                                    //Intent to the home page
                                                    var signInGameSettings = intent.getStringExtra("SignInGameSettings")
                                                    if(signInGameSettings == "1"){
                                                        val intent = Intent(this, GameSettingsActivity::class.java)
                                                        current_username = username
                                                        current_id = id
                                                        current_score = score.toInt()
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        val intent =
                                                            Intent(this, MainActivity::class.java)
                                                        current_username = username
                                                        current_id = id
                                                        current_score = score.toInt()
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        val houseButton = findViewById(R.id.HouseButton) as ImageButton
        houseButton.setOnClickListener() {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}