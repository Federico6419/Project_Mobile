package project.mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import com.google.firebase.auth.FirebaseAuth

class GameoverActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gameover)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        supportActionBar?.setTitle("                     Death Planes")

        val uid = firebaseAuth.uid


        val playAgainButton = findViewById(R.id.PlayAgainButton) as ImageButton
        playAgainButton.setOnClickListener() {
            uid?.let {
                intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            } ?: run {
                intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            }
        }

        val returnMenuButton = findViewById(R.id.ReturnButton) as ImageButton
        returnMenuButton.setOnClickListener() {
            uid?.let {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } ?: run {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
