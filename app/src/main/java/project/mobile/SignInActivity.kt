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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)   //Display the sign in XML

        supportActionBar?.setTitle("                     Death Planes")

        //Getting the references to the Views
        var username = findViewById(R.id.Username) as EditText
        var password = findViewById(R.id.Password) as EditText
        var resetbutton = findViewById(R.id.ResetButton) as Button
        var submitbutton = findViewById(R.id.SubmitButton) as Button

        //Manage the reset button
        resetbutton.setOnClickListener {
            username.setText("")
            password.setText("")
        }

        //Setting the listener of the onClick event of the submit button
        submitbutton.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()

            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

            val referenceDB = database.getReference("NumberOfUsers")

            var res = ""
            var numberOfUsers = 0
            referenceDB.get().addOnSuccessListener {
                res = it.value.toString()
                numberOfUsers = res.toInt()
                var existsUser = false

                for(i in 1..numberOfUsers){
                    var id = ""
                    for(i in 1..5 - i.toString().length){
                        id += "0"
                    }
                    id += i

                    val referenceUsername = database.getReference("Users/$id/Username")
                    var res = ""

                    referenceUsername.get().addOnSuccessListener {
                        //Se lo trova adesso fa la verifica della password, altrimenti continua il for
                        res = it.value.toString()
                        if(user == res) {
                            val referencePassword = database.getReference("Users/$id/Password")

                            res = ""

                            referencePassword.get().addOnSuccessListener {
                                res = it.value.toString()
                                Log.i("firebase", "Got value ${it.value}")
                                if (pass == res) {
                                    intent = Intent(this, MainActivity::class.java)
                                    intent.putExtra("Username", user)
                                    intent.putExtra("Password", pass)
                                    intent.putExtra("ID",id)
                                    startActivity(intent)
                                }
                            }.addOnFailureListener {
                                Log.e("firebase", "Error getting data", it)
                            }
                        }
                    }
                }
            }.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
}