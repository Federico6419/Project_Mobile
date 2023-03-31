package project.mobile

import android.content.Intent
import android.os.Bundle
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
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
            bullet2.setImageResource(R.drawable.laser_bullet2)
            //bullet3.setImageResource(R.drawable.bullet)
            bullet = "normal"
            music.playChooseSound(this)
        }

        bullet2.setOnClickListener() {
            bullet1.setImageResource(R.drawable.bulletsettings)
            bullet2.setImageResource(R.drawable.bulletsigned)
            //bullet3.setImageResource(R.drawable.bullet)
            bullet = "laser"
            music.playChooseSound(this)
        }


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
            setContentView(R.layout.activity_game_settings_loading)

            intent = Intent(this, GameActivity::class.java)
            intent.putExtra("Opponent", avversario)
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
