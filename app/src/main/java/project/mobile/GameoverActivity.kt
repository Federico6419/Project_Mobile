package project.mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton

class GameoverActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("GAMEOVER", "OK")
        setContentView(R.layout.activity_gameover)

        supportActionBar?.setTitle("                     Death Planes")

        val username = intent.getStringExtra("Username")
        val password = intent.getStringExtra("Password")
        val id = intent.getStringExtra("ID")
        val weather= intent.getStringExtra("Weather")
        val score = intent.getStringExtra("Score")


        val playAgainButton = findViewById(R.id.PlayAgainButton) as ImageButton
        playAgainButton.setOnClickListener() {
            username?.let {
                intent = Intent(this, GameActivity::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Password",password)
                intent.putExtra("ID",id)
                intent.putExtra("Weather",weather)
                intent.putExtra("Score",score)
                startActivity(intent)
            } ?: run {
                intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            }
        }

        val returnMenuButton = findViewById(R.id.ReturnButton) as ImageButton
        returnMenuButton.setOnClickListener() {
            username?.let {
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("Username",username)
                intent.putExtra("Password",password)
                intent.putExtra("ID",id)
                intent.putExtra("Weather",weather)
                intent.putExtra("Score",score)
                startActivity(intent)
            } ?: run {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}