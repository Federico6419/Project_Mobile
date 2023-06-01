package project.mobile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import java.io.File
import java.util.*


var users = arrayOf<String>()
var scores = arrayOf<Int>()
var numberOfUsers = 0

lateinit var conxt : Context

class LeaderboardActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable
    val storage = Firebase.storage      //Firebase Storage variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setTitle("                     Death Planes")     //Define the name of the application

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
                finish()
            }

        } ?: run {
            val houseButton = findViewById(R.id.HouseButton) as ImageButton
            houseButton.setOnClickListener() {
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    suspend fun getLeaderB() {
        var progressBar = findViewById(R.id.progress_loader) as ProgressBar
        progressBar.visibility = View.VISIBLE

        //Manage points after loading text
        var loading = findViewById(R.id.LoadingText) as TextView
        var numPoints = 1
        val timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post(java.lang.Runnable {
                    if (numPoints == 4) numPoints = 1
                    loading.text = "Downloading leaderboard" + ".".repeat(numPoints)
                    numPoints += 1
                })
            }
        }, 10, 600)

        //Connecting to Firebase Database
        val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

        //Take the opponent, if there is one
        var opponent = ""
        var isOpponentSet = false
        firebaseAuth.uid?.let {
            val opponentNameReference =
                database.getReference("Users/$current_id/Opponent")    //Take the number of users
            //Get the opponent, if there is one
            opponentNameReference.get().addOnSuccessListener {
                if (it.value != null) {
                    opponent = it.value.toString()
                    isOpponentSet = true
                }
            }
        }


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

                            var drawable = getDrawable(R.drawable.border)
                            var drawableGreen = getDrawable(R.drawable.bordergreen)
                            var drawableRed = getDrawable(R.drawable.borderred)
                            firebaseAuth.uid?.let {
                                if(current_username == items.users?.get(tot)?.username.toString()) {
                                    tr.setBackground(drawableGreen)
                                }else if(isOpponentSet and (opponent == items.users?.get(tot)?.username.toString())){
                                    tr.setBackground(drawableRed)
                                }
                                else{
                                    tr.setBackground(drawable)
                                }
                            } ?: run {
                                tr.setBackground(drawable)
                            }
                            tr.setPadding(10, 0, 10, 0)

                            //Create a new column
                            val tv1 = TextView(conxt)
                            tv1.setLayoutParams(
                                TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                                )
                            )
                            tv1.text = (tot+1).toString() + "°"
                            tv1.setPadding(0, 100, 0, 100)
                            tr.addView(tv1)

                            //Create a new column
                            val iv = ImageView(conxt)
                            iv.setLayoutParams(
                                TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                                )
                            )

                            // Create a storage reference from our app
                            var storageRef = storage.reference

                            // Create a reference with an initial file path and name
                            val userImageRef = storageRef.child("Images/"+ items.users?.get(tot)?.username.toString())

                            val localFile = File.createTempFile("images", "jpg")

                            userImageRef.getFile(localFile).addOnSuccessListener {
                                // Local temp file has been created
                                iv.setImageURI(localFile.toUri())
                            }.addOnFailureListener {
                                // Handle any errors
                                iv.setImageResource(R.drawable.profileimage)
                            }
                            iv.getLayoutParams().height = 250
                            iv.getLayoutParams().width = 250
                            iv.setPadding(160, 0, 0, 0)
                            tr.addView(iv)

                            //Create a new column
                            val tv2 = TextView(conxt)
                            tv2.setLayoutParams(
                                TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                                )
                            )
                            tv2.text = items.users?.get(tot)?.username.toString()
                            tv2.setPadding(10, 0, 0, 0)
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
                            tv3.setPadding(80, 0, 0, 0)
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
                loading.visibility = View.INVISIBLE
            }
        }
    }
}