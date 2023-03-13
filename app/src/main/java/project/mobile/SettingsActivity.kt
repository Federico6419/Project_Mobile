package project.mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setTitle("                     Death Planes")

        val message=intent.getStringExtra("msg")
        val id=intent.getStringExtra("ID")

        var username = findViewById(R.id.Username) as EditText
        var password = findViewById(R.id.Password) as EditText
        var userButton = findViewById(R.id.ChangeUsernameButton) as Button
        var passButton = findViewById(R.id.ChangePasswordButton) as Button
        var deleteButton = findViewById(R.id.DeleteButton) as Button

        val usernameText: TextView = findViewById(R.id.Username) as TextView
        usernameText.text = intent.getStringExtra("Username")

        val passwordText: TextView = findViewById(R.id.Password) as TextView
        passwordText.text = intent.getStringExtra("Password")

        message?.let {
            val MessageText: TextView = findViewById(R.id.MessageText) as TextView
            MessageText.text = ""
        } ?: run {
            val MessageText: TextView = findViewById(R.id.MessageText) as TextView
            MessageText.text = message
        }

        // set on-click listener
        userButton.setOnClickListener {
            val user = username.text.toString()

            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")
            val referenceUsername = database.getReference("Users/$id/Username")

            referenceUsername.setValue(user)

            intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("msg", "Username changed successfully")
            intent.putExtra("Username",user)
            intent.putExtra("Password",password.text.toString())
            intent.putExtra("ID",id)
            startActivity(intent)
        }

        // set on-click listener
        passButton.setOnClickListener {
            val pass = password.text.toString()

            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")
            val referencePassword = database.getReference("Users/$id/Password")

            referencePassword.setValue(pass)

            intent = Intent(this, SettingsActivity::class.java)
            intent.putExtra("msg", "Password changed successfully")
            intent.putExtra("Username",username.text.toString())
            intent.putExtra("Password",pass)
            intent.putExtra("ID",id)
            startActivity(intent)
        }

        // set on-click listener
        deleteButton.setOnClickListener {
            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")
            val referenceID = database.getReference("Users/$id")

            /*val intent = Intent(this, PopUp::class.java)
            intent.putExtra("popuptitle", "Error")
            intent.putExtra("popuptext", "Sorry, that email address is already used!")
            intent.putExtra("popupbtn", "OK")
            intent.putExtra("darkstatusbar", false)
            startActivity(intent)*/

            /*referenceID.removeValue()
            intent = Intent(this, MainActivity::class.java)
            intent.putExtra("msg", "Account deleted successfully")
            startActivity(intent)*/
        }
    }
}