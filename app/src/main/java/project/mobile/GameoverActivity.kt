package project.mobile //

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GameoverActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        supportActionBar?.setTitle("                     Death Planes")

        val color = intent.getStringExtra("Color")
        val bul = intent.getStringExtra("Bullet")
        val score = intent.getStringExtra("Score")

        //Get the uid of the current user
        val uid = firebaseAuth.uid

        //Connecting to Firebase Database
        val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

        uid?.let {
            val opponentNameReference = database.getReference("Users/$current_id/Opponent")    //Take the number of users
            //Get the opponent, if there is one
            opponentNameReference.get().addOnSuccessListener {
                if (it.value != null) {
                    Log.i("BUG", it.value.toString())
                    setContentView(R.layout.activity_gameover_opponent)
                    var opponentView = findViewById(R.id.OpponentText) as TextView
                    opponentView.text = "Opponent's record: " + it.value.toString()

                    /*GlobalScope.launch {
                        getDifference(current_username, opponent)
                    }*/

                    //Set your record text
                    val scoreReference = database.getReference("Users/$current_id/Score")    //Take the number of users

                    //Get the record
                    scoreReference.get().addOnSuccessListener {
                        var recordView = findViewById(R.id.RecordText) as TextView
                        recordView.text = "Your record: " + it.value.toString()
                    }
                }
                else{
                    setContentView(R.layout.activity_gameover_signed)

                    //Set your record text
                    val scoreReference = database.getReference("Users/$current_id/Score")    //Take the number of users

                    //Get the record
                    scoreReference.get().addOnSuccessListener {
                        Log.i("BUG", it.value.toString())
                        var recordView = findViewById(R.id.RecordText) as TextView
                        recordView.text = "Your record: " + it.value.toString()
                    }
                }

                //Set your score text
                var scoreView = findViewById(R.id.ScoreText) as TextView
                Log.i("SCORE", score.toString())
                scoreView.text = "Your score: " + score

                val playAgainButton = findViewById(R.id.PlayAgainButton) as ImageButton
                playAgainButton.setOnClickListener() {
                    intent = Intent(this, GameActivity::class.java)
                    intent.putExtra("Color", color)
                    intent.putExtra("Bullet", bul)
                    startActivity(intent)
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
        } ?: run {
            setContentView(R.layout.activity_gameover)

            //Set your score text
            var scoreView = findViewById(R.id.ScoreText) as TextView
            Log.i("SCORE", score.toString())
            scoreView.text = "Your score: " + score

            val playAgainButton = findViewById(R.id.PlayAgainButton) as ImageButton
            playAgainButton.setOnClickListener() {
                intent = Intent(this, GameActivity::class.java)
                intent.putExtra("Color", color)
                intent.putExtra("Bullet", bul)
                startActivity(intent)
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
}
