package project.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SettingsActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        supportActionBar?.setTitle("                     Death Planes")

        //val id=intent.getStringExtra("ID")

        var usernameView = findViewById(R.id.Username) as EditText
        var passwordView = findViewById(R.id.Password) as EditText
        var emailView = findViewById(R.id.Email) as EditText
        var userButton = findViewById(R.id.ChangeUsernameButton) as Button
        var emailButton = findViewById(R.id.ChangeEmailButton) as Button
        var passButton = findViewById(R.id.ChangePasswordButton) as Button
        var deleteButton = findViewById(R.id.DeleteButton) as Button

        val usernameText: TextView = findViewById(R.id.Username) as TextView
        usernameText.text = current_username

        val emailText: TextView = findViewById(R.id.Email) as TextView
        val int = intent.getStringExtra("Email")
        if(int != null){
            emailText.text = intent.getStringExtra("Email")
        } else{
            emailText.text = firebaseAuth.currentUser?.email
        }

        // set on-click listener to change username
        userButton.setOnClickListener {
            /*val user = usernameView.text.toString()

            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")
            val referenceUsername = database.getReference("Users/$current_id/Username")

            referenceUsername.setValue(user)
            Toast.makeText(this, "USERNAME CHANGED CORRECTLY", Toast.LENGTH_SHORT).show()

            //Change the value of the public variable of the current user
            current_username = user
            startActivity(intent)*/

            val user = usernameView.text.toString()

            //Connecting to Firebase Database
            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

            val referenceDB = database.getReference("NumberOfUsers")    //Take the number of users

            //Get the number of users
            referenceDB.get().addOnSuccessListener {
                var numberOfUsers = it.value.toString().toInt()

                var found = false
                var referenceUsername: DatabaseReference

                for (i in 1..numberOfUsers) {
                    referenceUsername = database.getReference("Users/$i/Username")    //Reference to username in the Database

                    referenceUsername.get().addOnSuccessListener {
                        if ((it.value.toString() == user) and !found) {
                            found = true
                            //If username already exists
                            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                        } else if ((i == numberOfUsers) and !found) {
                            //Save Username
                            val referenceUser = database.getReference("Users/$current_id/Username")
                            referenceUser.setValue(user)
                            current_username = user
                            startActivity(intent)
                        }
                    }
                }
            }
        }

        // set on-click listener to change email
        emailButton.setOnClickListener {
            val email = emailView.text.toString()

            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")
            val referenceUsername = database.getReference("Users/$current_id/Email")

            referenceUsername.setValue(email) // change email on database realtime
            firebaseAuth.currentUser?.updateEmail(email) // change email on firebase authentication
            Toast.makeText(this, "EMAIL CHANGED CORRECTLY", Toast.LENGTH_SHORT).show()

            intent.putExtra("Email", email)
            startActivity(intent)
        }

        // set on-click listener to the change password
        passButton.setOnClickListener {
            val pass = passwordView.text.toString()

            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")
            val referencePassword = database.getReference("Users/$current_id/Password")

            var hash = PasswordHashManager()
            referencePassword.setValue(hash.encryptSHA256(pass))

            firebaseAuth.currentUser?.updatePassword(pass)?.addOnSuccessListener{
                //Alert of success
                Toast.makeText(this, "PASSWORD CHANGED CORRECTLY", Toast.LENGTH_SHORT).show()

                startActivity(intent)
            }?.addOnFailureListener{
                Toast.makeText(this, "PASSWORD TOO SHORT", Toast.LENGTH_SHORT).show()
            }
        }

        //Set on-click listener for delete button
        deleteButton.setOnClickListener {
            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

            val referenceNumUsers = database.getReference("NumberOfUsers")

            //Get the number of users
            referenceNumUsers.get().addOnSuccessListener {
                var numberOfUsers = it.value.toString().toInt()

                val referenceID = database.getReference("Users/$current_id")

                //If this is the last id, delete it
                if (numberOfUsers == current_id?.toInt()) {
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
                        val referenceNewUser = database.getReference("Users/$current_id/Username")
                        referenceNewUser.setValue(newUsername)

                        //Get Email
                        val referenceEmail = database.getReference("Users/$numberOfUsers/Email")
                        referenceEmail.get().addOnSuccessListener {
                            var newEmail = it.value.toString()

                            //Set Email
                            val referenceNewEmail = database.getReference("Users/$current_id/Email")
                            referenceNewEmail.setValue(newEmail)

                            //Save Password
                            val referencePassword =
                                database.getReference("Users/$numberOfUsers/Password")
                            referencePassword.get().addOnSuccessListener {
                                var newPassword = it.value.toString()

                                //Set Password
                                val referenceNewPassword =
                                    database.getReference("Users/$current_id/Password")
                                referenceNewPassword.setValue(newPassword)

                                //Save Score
                                val referenceScore =
                                    database.getReference("Users/$numberOfUsers/Score")
                                referenceScore.get().addOnSuccessListener {
                                    var newScore = it.value.toString()

                                    //Set Score
                                    val referenceNewScore = database.getReference("Users/$current_id/Score")
                                    referenceNewScore.setValue(newScore)

                                    //Take and save the user id
                                    val referenceUid = database.getReference("Users/$numberOfUsers/UID")
                                    referenceUid.get().addOnSuccessListener {
                                        var newUID = it.value.toString()

                                        //Set UID
                                        val referenceNewUID = database.getReference("Users/$current_id/UID")
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
        val houseButton = findViewById(R.id.HouseButton) as ImageButton
        houseButton.setOnClickListener() {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}