package project.mobile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*

import com.google.gson.annotations.SerializedName

data class Flask_Json(
    @SerializedName("numberOfUsers")
    var numberOfUsers: Int?,
    @SerializedName("users")
    var users: List<Users>?
)
data class Users(
    @SerializedName("username")
    val username: String?,
    @SerializedName("score")
    val score: Int?
)


var users = arrayOf<String>("","","","","","","","","","")
var scores = arrayOf<Int>(0,0,0,0,0,0,0,0,0,0)
var numberOfUsers = 0
//var username: String? = null
//var id = ""
//var score = ""

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication


        GlobalScope.launch {
            getLeaderB()
        }

        firebaseAuth.uid?.let {
            val houseButton = findViewById(R.id.HouseButton) as ImageButton
            houseButton.setOnClickListener() {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

        } ?: run {
            val houseButton = findViewById(R.id.HouseButton) as ImageButton
            houseButton.setOnClickListener() {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    suspend fun getLeaderB() {
        //var retrofit = Request_Api.getInstance()
        val flaskApi = RequestLeaderboard().retrofit.create(FlaskInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {

            // Do the GET request and get response
            var response = flaskApi.getLeaderboard()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    val items = response.body()
                    if (items != null) {
                        var tot = 0
                        numberOfUsers = items.numberOfUsers!!
                        Log.i("leader", items.toString())
                        while((tot<10) and (tot< items.numberOfUsers!!)) {

                            users[tot] = items.users?.get(tot)?.username.toString()
                            scores[tot] = items.users?.get(tot)?.score!!
                            tot = tot+1
                        }
                    }
                    Log.i("leader", users.toString())

                    var player1 = findViewById(R.id.player1) as TextView
                    var player2 = findViewById(R.id.player2) as TextView
                    var player3 = findViewById(R.id.player3) as TextView
                    var player4 = findViewById(R.id.player4) as TextView
                    var player5 = findViewById(R.id.player5) as TextView
                    var player6 = findViewById(R.id.player6) as TextView
                    var player7 = findViewById(R.id.player7) as TextView
                    var player8 = findViewById(R.id.player8) as TextView
                    var player9 = findViewById(R.id.player9) as TextView
                    var player10 = findViewById(R.id.player10) as TextView

                    var score1 = findViewById(R.id.score1) as TextView
                    var score2 = findViewById(R.id.score2) as TextView
                    var score3 = findViewById(R.id.score3) as TextView
                    var score4 = findViewById(R.id.score4) as TextView
                    var score5 = findViewById(R.id.score5) as TextView
                    var score6 = findViewById(R.id.score6) as TextView
                    var score7 = findViewById(R.id.score7) as TextView
                    var score8 = findViewById(R.id.score8) as TextView
                    var score9 = findViewById(R.id.score9) as TextView
                    var score10 = findViewById(R.id.score10) as TextView

                    player1.text = users[0]
                    player2.text = users[1]
                    player3.text = users[2]
                    player4.text = users[3]
                    player5.text = users[4]
                    player6.text = users[5]
                    player7.text = users[6]
                    player8.text = users[7]
                    player9.text = users[8]
                    player10.text = users[9]

                    score1.text = scores[0].toString()
                    score2.text = scores[1].toString()
                    score3.text = scores[2].toString()
                    score4.text = scores[3].toString()
                    score5.text = scores[4].toString()
                    score6.text = scores[5].toString()
                    score7.text = scores[6].toString()
                    score8.text = scores[7].toString()
                    score9.text = scores[8].toString()
                    score10.text = scores[9].toString()

                    var tot = 0
                    var myScore = ""

                    firebaseAuth.uid?.let {
                        if (items != null) {
                            while((tot< items.numberOfUsers!!)) {
                                if(items.users?.get(tot)?.username.toString() == current_username.toString()) {
                                    myScore = items.users?.get(tot)?.score.toString()!!
                                    break
                                }
                                tot = tot+1
                            }
                            val rankingText: TextView = findViewById(R.id.YourRanking) as TextView
                            rankingText.text = "Your ranking: "+(tot+1).toString()+"°"

                            val scoreText: TextView = findViewById(R.id.YourScore) as TextView
                            scoreText.text = "Your score: "+myScore
                        }
                    }
                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
            }
        }
    }
}