package project.mobile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.annotations.SerializedName


data class Difference_Json(
    @SerializedName("winner")
    var winner: String?,
    @SerializedName("difference")
    var difference: Int?
)

class GameSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        val username = intent.getStringExtra("Username")
        val password = intent.getStringExtra("Password")
        val id = intent.getStringExtra("ID")

        var color = "red"
        var bullet = "normal"

        //Airplane selection management
        val airplane1: ImageButton = findViewById(R.id.Airplane1) as ImageButton
        airplane1.setImageResource(R.drawable.airplane)

        val airplane2: ImageButton = findViewById(R.id.Airplane2) as ImageButton

        val airplane3: ImageButton = findViewById(R.id.Airplane3) as ImageButton

        airplane1.setOnClickListener() {
            airplane1.setImageResource(R.drawable.airplane)
            airplane2.setImageResource(R.drawable.airplane)
            airplane3.setImageResource(R.drawable.airplane)
            color = "red"
        }

        airplane2.setOnClickListener() {
            airplane2.setImageResource(R.drawable.airplane)
            airplane1.setImageResource(R.drawable.airplane)
            airplane3.setImageResource(R.drawable.airplane)
            color = "blue"
        }

        airplane3.setOnClickListener() {
            airplane3.setImageResource(R.drawable.airplane)
            airplane1.setImageResource(R.drawable.airplane)
            airplane2.setImageResource(R.drawable.airplane)
            color = "green"
        }


        //Bullet selection management
        val bullet1: ImageButton = findViewById(R.id.Bullet1) as ImageButton
        bullet1.setImageResource(R.drawable.airplane)

        val bullet2: ImageButton = findViewById(R.id.Bullet2) as ImageButton

        //val bullet3: ImageButton = findViewById(R.id.Bullet3) as ImageButton

        bullet1.setOnClickListener() {
            bullet1.setImageResource(R.drawable.airplane)
            bullet2.setImageResource(R.drawable.bullet)
            //bullet3.setImageResource(R.drawable.bullet)
            bullet = "normal"
        }

        bullet2.setOnClickListener() {
            bullet2.setImageResource(R.drawable.airplane)
            bullet1.setImageResource(R.drawable.bullet)
            //bullet3.setImageResource(R.drawable.bullet)
            bullet = "laser"
        }

        /*bullet3.setOnClickListener() {
            bullet3.setImageResource(R.drawable.airplane)
            bullet1.setImageResource(R.drawable.bullet)
            bullet2.setImageResource(R.drawable.bullet)
        }*/


        //Opponent management
        val opponentButton = findViewById(R.id.SubmitButton) as Button
        var avversario = ""
        opponentButton.setOnClickListener() {
            val opponent = findViewById(R.id.Opponent) as TextView
            val user = findViewById(R.id.Username) as EditText
            opponent.text = "Current opponent: " + user.text
            avversario = user.text.toString()
        }


        //Start button management
        val startButton = findViewById(R.id.StartButton) as ImageButton
        startButton.setOnClickListener() {

            intent = Intent(this, GameActivity::class.java)
            intent.putExtra("Username", username)
            intent.putExtra("Password", password)
            intent.putExtra("ID", id)
            intent.putExtra("Opponent", avversario)
            intent.putExtra("Color", color)
            intent.putExtra("Bullet", bullet)
            startActivity(intent)
        }
    }
}