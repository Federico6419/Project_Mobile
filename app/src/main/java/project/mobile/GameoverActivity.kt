package project.mobile

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.*
import java.io.File
import java.util.*


class GameoverActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable
    val storage = Firebase.storage      //Firebase Storage variable

    lateinit var conxt : Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        conxt = this

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

            GlobalScope.launch {
                getLeaderB()
            }

            //Set your score text
            var scoreView = findViewById(R.id.ScoreText) as TextView
            Log.i("SCORE", score.toString())
            scoreView.text = "Your score: " + score

            val signUpButton = findViewById(R.id.SignUpButton) as Button
            signUpButton.setOnClickListener() {
                intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            }

            val signInButton = findViewById(R.id.SignInButton) as Button
            signInButton.setOnClickListener() {
                intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
            }

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

                        while (tot < items.numberOfUsers!!) {
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
                            var drawable = getDrawable(R.drawable.border)
                            tr.setBackground(drawable)
                            tr.setPadding(10, 0, 10, 0)

                            //Create a new column
                            val tv1 = TextView(conxt)
                            tv1.setLayoutParams(
                                TableRow.LayoutParams(
                                    TableRow.LayoutParams.MATCH_PARENT,
                                    TableRow.LayoutParams.WRAP_CONTENT
                                )
                            )
                            tv1.text = (tot + 1).toString() + "°"
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
                            val userImageRef =
                                storageRef.child("Images/" + items.users?.get(tot)?.username.toString())

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
                } else {

                    Log.e("RETROFIT_ERROR", response.code().toString())

                }
                progressBar.visibility = View.INVISIBLE
                loading.visibility = View.INVISIBLE
            }
        }
    }
}
