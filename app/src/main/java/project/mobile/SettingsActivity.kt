package project.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SettingsActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        supportActionBar?.setTitle("                     Death Planes")

        val id=intent.getStringExtra("ID")

        var usernameView = findViewById(R.id.Username) as EditText
        var passwordView = findViewById(R.id.Password) as EditText
        var emailView = findViewById(R.id.Email) as EditText
        var userButton = findViewById(R.id.ChangeUsernameButton) as Button
        var emailButton = findViewById(R.id.ChangeEmailButton) as Button
        var passButton = findViewById(R.id.ChangePasswordButton) as Button
        var deleteButton = findViewById(R.id.DeleteButton) as Button

        val usernameText: TextView = findViewById(R.id.Username) as TextView
        usernameText.text = intent.getStringExtra("Username")

        val emailText: TextView = findViewById(R.id.Email) as TextView
        emailText.text = firebaseAuth.currentUser?.email

        val passwordText: TextView = findViewById(R.id.Password) as TextView
        val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")
        val referencePassword = database.getReference("Users/$id/Password")
        referencePassword.get().addOnSuccessListener {
        passwordText.text = it.value.toString()
        }
        //firebaseAuth.currentUser?.updatePassword("ciao")

        // set on-click listener to change username
        userButton.setOnClickListener {
            val user = usernameView.text.toString()

            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")
            val referenceUsername = database.getReference("Users/$id/Username")

            referenceUsername.setValue(user)
            Toast.makeText(this, "USERNAME CHANGED CORRECTLY", Toast.LENGTH_SHORT).show()

            startActivity(intent)
        }

        // set on-click listener to change email
        emailButton.setOnClickListener {
            val email = emailView.text.toString()

            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")
            val referenceUsername = database.getReference("Users/$id/Email")

            referenceUsername.setValue(email) // change email on database realtime
            firebaseAuth.currentUser?.updateEmail(email) // change email on firebase authentication
            Toast.makeText(this, "EMAIL CHANGED CORRECTLY", Toast.LENGTH_SHORT).show()

            startActivity(intent)
        }

        // set on-click listener to the change password
        passButton.setOnClickListener {
            val pass = passwordView.text.toString()

            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")
            val referencePassword = database.getReference("Users/$id/Password")

            referencePassword.setValue(pass)

            //Alert of success
            Toast.makeText(this, "PASSWORD CHANGED CORRECTLY", Toast.LENGTH_SHORT).show()

            startActivity(intent)
        }

        //Set on-click listener for delete button
        deleteButton.setOnClickListener {
            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

            val referenceNumUsers = database.getReference("NumberOfUsers")

            //Get the number of users
            referenceNumUsers.get().addOnSuccessListener {
                var numberOfUsers = it.value.toString().toInt()

                val referenceID = database.getReference("Users/$id")

                //If this is the last id, delete it
                if (numberOfUsers == id?.toInt()) {
                    referenceID.removeValue()

                    numberOfUsers -= 1
                    referenceNumUsers.setValue(numberOfUsers)

                    //Alert of success
                    Toast.makeText(this, "ACCOUNT DELETED SUCCESSFULLY", Toast.LENGTH_SHORT).show()

                    //Delete account
                    firebaseAuth.currentUser?.delete()

                    firebaseAuth.signOut()

                    //Execute the intent
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {       //If this is not the last id
                    //Get Username
                    val referenceUser = database.getReference("Users/$numberOfUsers/Username")
                    referenceUser.get().addOnSuccessListener {
                        var newUsername = it.value.toString()

                        //Set Username
                        val referenceNewUser = database.getReference("Users/$id/Username")
                        referenceNewUser.setValue(newUsername)

                        //Get Email
                        val referenceEmail = database.getReference("Users/$numberOfUsers/Email")
                        referenceEmail.get().addOnSuccessListener {
                            var newEmail = it.value.toString()

                            //Set Email
                            val referenceNewEmail = database.getReference("Users/$id/Email")
                            referenceNewEmail.setValue(newEmail)

                            //Save Password
                            val referencePassword =
                                database.getReference("Users/$numberOfUsers/Password")
                            referencePassword.get().addOnSuccessListener {
                                var newPassword = it.value.toString()

                                //Set Password
                                val referenceNewPassword =
                                    database.getReference("Users/$id/Password")
                                referenceNewPassword.setValue(newPassword)

                                //Save Score
                                val referenceScore =
                                    database.getReference("Users/$numberOfUsers/Score")
                                referenceScore.get().addOnSuccessListener {
                                    var newScore = it.value.toString()

                                    //Set Score
                                    val referenceNewScore = database.getReference("Users/$id/Score")
                                    referenceNewScore.setValue(newScore)

                                    //Take and save the user id
                                    val referenceUid = database.getReference("Users/$numberOfUsers/UID")
                                    referenceUid.get().addOnSuccessListener {
                                        var newUID = it.value.toString()

                                        //Set UID
                                        val referenceNewUID = database.getReference("Users/$id/UID")
                                        referenceNewUID.setValue(newUID)

                                        Log.i("FIRE", numberOfUsers.toString())

                                        //Delete last account cause moved to new position
                                        val referenceOldID = database.getReference("Users/$numberOfUsers")
                                        referenceOldID.removeValue()

                                        //Decrement the number of users
                                        numberOfUsers -= 1
                                        referenceNumUsers.setValue(numberOfUsers)

                                        //Alert of success
                                        Toast.makeText(this, "ACCOUNT DELETED SUCCESSFULLY", Toast.LENGTH_SHORT).show()

                                        //Delete account
                                        firebaseAuth.currentUser?.delete()

                                        firebaseAuth.signOut()

                                        //Execute the intent
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}