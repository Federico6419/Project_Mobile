package project.mobile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.*


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


var users = arrayOf<String>()
var scores = arrayOf<Int>()
var numberOfUsers = 0

lateinit var conxt : Context

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        conxt = this

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
        var progressBar = findViewById(R.id.progress_loader) as ProgressBar
        progressBar.visibility = View.VISIBLE

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

                        //Find the Table layout
                        val tl = findViewById(R.id.Table) as TableLayout

                        while(tot< items.numberOfUsers!!) {
                            users.plus((items.users?.get(tot)?.username.toString()))
                            scores.plus(items.users?.get(tot)?.score!!)

                            //Create a new row
                            var tr = TableRow(conxt)
                            tr.setLayoutParams(
                                TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                                )
                            )
                            tr.setBackgroundColor(Color.GRAY)
                            tr.setPadding(10, 10, 10, 10)

                            //Create a new column
                            val tv1 = TextView(conxt)
                            tv1.setLayoutParams(
                                TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                                )
                            )
                            tv1.text = (tot+1).toString() + "°"
                            tr.addView(tv1)

                            //Create a new column
                            val tv2 = TextView(conxt)
                            tv2.setLayoutParams(
                                TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                                )
                            )
                            tv2.text = items.users?.get(tot)?.username.toString()
                            tv2.setPadding(275, 0, 0, 0)
                            tr.addView(tv2)

                            //Create a new column
                            val tv3 = TextView(conxt)
                            tv3.setLayoutParams(
                                TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                                )
                            )
                            tv3.text = (items.users?.get(tot)?.score!!).toString()
                            tv3.setPadding(70, 0, 0, 0)
                            tr.addView(tv3)

                            //Add the row to the table
                            tl.addView(
                                tr,
                                TableLayout.LayoutParams(
                                    TableLayout.LayoutParams.MATCH_PARENT,
                                    TableLayout.LayoutParams.WRAP_CONTENT
                                )
                            )

                            tot += 1
                        }
                    }
                    Log.i("leader", users.toString())

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
                progressBar.visibility = View.INVISIBLE
            }
        }
    }
}