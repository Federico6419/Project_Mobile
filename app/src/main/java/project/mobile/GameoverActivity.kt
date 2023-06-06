package project.mobile//

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.LayoutManager
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

    var opponent = ""   //Opponent's name
    var isOpponentSet = false

    var opponentResult = 0
    var difference = 0

    var isFinished = false
    var isFinishedOpponent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        music.playGameoverMusic(this)

        //Mute management
        if (muted) {
            music.setVolume(0.0f, 0.0f)
        } else {
            music.setVolume(1.0f, 1.0f)
        }

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        conxt = this

        supportActionBar?.setTitle("                         Death Planes")

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
                    opponent = it.value.toString()
                    isOpponentSet = true
                    setContentView(R.layout.activity_gameover_opponent)

                    GlobalScope.launch {
                        getDifference(current_username, opponent)
                    }

                    var usernameText = findViewById(R.id.UsernameText) as TextView
                    usernameText.text = current_username

                    //Set your record text
                    val scoreReference = database.getReference("Users/$current_id/Score")    //Take the number of users

                    //Get the record
                    scoreReference.get().addOnSuccessListener {
                        var recordView = findViewById(R.id.RecordText) as TextView
                        recordView.text = "Record: " + it.value.toString()
                    }
                }
                else{
                    setContentView(R.layout.activity_gameover_signed)

                    GlobalScope.launch {
                        getLeaderB()
                    }

                    var usernameText = findViewById(R.id.UsernameText) as TextView
                    usernameText.text = current_username

                    //Set your record text
                    val scoreReference = database.getReference("Users/$current_id/Score")

                    //Get the record
                    scoreReference.get().addOnSuccessListener {
                        var recordView = findViewById(R.id.RecordText) as TextView
                        recordView.text = "Record: " + it.value.toString()
                    }
                }

                val newRecord = intent.getStringExtra("NewRecord")
                if(newRecord == "true") {
                    var newRecordView = findViewById(R.id.NewRecord) as ImageView
                    newRecordView.visibility = View.VISIBLE
                }

                //Set your score text
                var scoreView = findViewById(R.id.ScoreText) as TextView
                Log.i("SCORE", score.toString())
                scoreView.text = score

                val playAgainButton = findViewById(R.id.PlayAgainButton) as ImageButton
                playAgainButton.setOnClickListener() {
                    //Change all the views to invisible
                    scoreView.visibility = View.INVISIBLE
                    var recordView = findViewById(R.id.RecordText) as TextView
                    recordView.visibility = View.INVISIBLE
                    var rankingView = findViewById(R.id.RankingText) as TextView
                    rankingView.visibility = View.INVISIBLE
                    //Change all the views to invisible
                    scoreView.visibility = View.INVISIBLE
                    playAgainButton.visibility = View.INVISIBLE
                    var firstRowView = findViewById(R.id.FirstRow) as TableLayout
                    firstRowView.visibility = View.INVISIBLE
                    var scrollView = findViewById(R.id.scroll) as ScrollView
                    scrollView.visibility = View.INVISIBLE
                    val returnMenuButton = findViewById(R.id.ReturnButton) as ImageButton
                    returnMenuButton.visibility = View.INVISIBLE
                    val homeTextView = findViewById(R.id.HomeText) as TextView
                    homeTextView.visibility = View.INVISIBLE
                    var progressBar = findViewById(R.id.progress_loader) as ProgressBar
                    var loading = findViewById(R.id.LoadingText) as TextView
                    progressBar.visibility = View.INVISIBLE
                    loading.visibility = View.INVISIBLE
                    val gameoverView = findViewById(R.id.Gameover) as ImageView
                    gameoverView.visibility = View.INVISIBLE
                    var profileImageView = findViewById(R.id.ProfileImage) as ImageView
                    profileImageView.visibility = View.INVISIBLE
                    var usernameText = findViewById(R.id.UsernameText) as TextView
                    usernameText.visibility = View.INVISIBLE
                    var newRecordView = findViewById(R.id.NewRecord) as ImageView
                    newRecordView.visibility = View.INVISIBLE

                    if(isOpponentSet){
                        var rankingTextOpponent = findViewById(R.id.RankingTextOpponent) as TextView
                        rankingTextOpponent.visibility = View.INVISIBLE
                        var opponentText = findViewById(R.id.UsernameTextOpponent) as TextView
                        opponentText.visibility = View.INVISIBLE
                        var recordOpponent = findViewById(R.id.RecordTextOpponent) as TextView
                        recordOpponent.visibility = View.INVISIBLE
                        var profileImageOpponent = findViewById(R.id.ProfileImageOpponent) as ImageView
                        profileImageOpponent.visibility = View.INVISIBLE
                        var winnerView = findViewById(R.id.WinnerText) as TextView
                        winnerView.visibility = View.INVISIBLE
                    }


                    //Change loading game views to visible
                    var progressView = findViewById(R.id.progress_loader_game) as ProgressBar
                    progressView.visibility = View.VISIBLE
                    var loadingGameView = findViewById(R.id.LoadingGame) as TextView
                    loadingGameView.visibility = View.VISIBLE
                    var view = findViewById(R.id.View) as ConstraintLayout
                    view.setBackgroundColor(Color.BLUE)

                    music.stopSound()
                    intent = Intent(this, GameActivity::class.java)
                    intent.putExtra("Color", color)
                    intent.putExtra("Bullet", bul)
                    startActivity(intent)
                    finish()
                }

                val returnMenuButton = findViewById(R.id.ReturnButton) as ImageButton
                returnMenuButton.setOnClickListener() {
                    music.stopSound()
                    uid?.let {
                        intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } ?: run {
                        intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
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
            scoreView.text = score

            val signUpButton = findViewById(R.id.SignUpButton) as Button
            signUpButton.setOnClickListener() {
                music.stopSound()
                intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                finish()
            }

            val signInButton = findViewById(R.id.SignInButton) as Button
            signInButton.setOnClickListener() {
                music.stopSound()
                intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()
            }

            val playAgainButton = findViewById(R.id.PlayAgainButton) as ImageButton
            playAgainButton.setOnClickListener() {
                //Change all the views to invisible
                scoreView.visibility = View.INVISIBLE
                var logTextView = findViewById(R.id.LogText) as TextView
                logTextView.visibility = View.INVISIBLE
                signInButton.visibility = View.INVISIBLE
                signUpButton.visibility = View.INVISIBLE
                playAgainButton.visibility = View.INVISIBLE
                var firstRowView = findViewById(R.id.FirstRow) as TableLayout
                firstRowView.visibility = View.INVISIBLE
                var scrollView = findViewById(R.id.scroll) as ScrollView
                scrollView.visibility = View.INVISIBLE
                val returnMenuButton = findViewById(R.id.ReturnButton) as ImageButton
                returnMenuButton.visibility = View.INVISIBLE
                val homeTextView = findViewById(R.id.HomeText) as TextView
                homeTextView.visibility = View.INVISIBLE
                var progressBar = findViewById(R.id.progress_loader) as ProgressBar
                var loading = findViewById(R.id.LoadingText) as TextView
                progressBar.visibility = View.INVISIBLE
                loading.visibility = View.INVISIBLE
                val gameoverView = findViewById(R.id.Gameover) as ImageView
                gameoverView.visibility = View.INVISIBLE

                //Change loading game views to visible
                var progressView = findViewById(R.id.progress_loader_game) as ProgressBar
                progressView.visibility = View.VISIBLE
                var loadingGameView = findViewById(R.id.LoadingGame) as TextView
                loadingGameView.visibility = View.VISIBLE
                var view = findViewById(R.id.View) as ConstraintLayout
                view.setBackgroundColor(Color.BLUE)

                music.stopSound()
                intent = Intent(this, GameActivity::class.java)
                intent.putExtra("Color", color)
                intent.putExtra("Bullet", bul)
                startActivity(intent)
                finish()
            }

            val returnMenuButton = findViewById(R.id.ReturnButton) as ImageButton
            returnMenuButton.setOnClickListener() {
                music.stopSound()
                uid?.let {
                    intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } ?: run {
                    intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    suspend fun getLeaderB() {
        var progressBar = findViewById(R.id.progress_loader) as ProgressBar
        progressBar.visibility = View.VISIBLE

        //Manage points after loading text
        var loading = findViewById(R.id.LoadingText) as TextView

        if(!isOpponentSet) {
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
        }

        var ranking = 0

        firebaseAuth.uid?.let {
            if(!isOpponentSet){
                var rankingText = findViewById(R.id.RankingText) as TextView
                var numPointsRanking = 1
                val timerRanking = Timer()
                timerRanking.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        Handler(Looper.getMainLooper()).post(java.lang.Runnable {
                            if(!isFinished) {
                                if (numPointsRanking == 4) numPointsRanking = 1
                                rankingText.text = "Ranking: " + ".".repeat(numPointsRanking)
                                numPointsRanking += 1
                            }
                        })
                    }
                }, 10, 600)
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

                        while (tot < items.numberOfUsers!!) {
                            var cur_user = items.users?.get(tot)?.username.toString()
                            if(cur_user == opponent){
                                var opponentText = findViewById(R.id.UsernameTextOpponent) as TextView
                                opponentText.text = cur_user
                            }

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
                            var drawableGreen = getDrawable(R.drawable.bordergreen)
                            var drawableRed = getDrawable(R.drawable.borderred)
                            firebaseAuth.uid?.let {
                                if(current_username == items.users?.get(tot)?.username.toString()) {
                                    tr.setBackground(drawableGreen)
                                    ranking = tot + 1
                                } else if(cur_user == opponent){
                                    tr.setBackground(drawableRed)
                                } else{
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
                                if (current_username == cur_user) {
                                    var profileImageView = findViewById(R.id.ProfileImage) as ImageView
                                    profileImageView.setImageURI(localFile.toUri())
                                } else if (isOpponentSet and (cur_user == opponent)){
                                    var profileImageOpponent = findViewById(R.id.ProfileImageOpponent) as ImageView
                                    profileImageOpponent.setImageURI(localFile.toUri())
                                }
                            }.addOnFailureListener {
                                // Handle any errors
                                iv.setImageResource(R.drawable.profileimage)
                                firebaseAuth.uid?.let {
                                    if (current_username == cur_user) {
                                        var profileImageView = findViewById(R.id.ProfileImage) as ImageView
                                        profileImageView.setImageResource(R.drawable.profileimage)
                                        if(isOpponentSet) {
                                            profileImageView.getLayoutParams().height = 120
                                            profileImageView.getLayoutParams().width = 120
                                        } else{
                                            profileImageView.getLayoutParams().height = 250
                                            profileImageView.getLayoutParams().width = 250
                                        }
                                    } else if (isOpponentSet and (cur_user == opponent)){
                                        var profileImageOpponent = findViewById(R.id.ProfileImageOpponent) as ImageView
                                        profileImageOpponent.setImageResource(R.drawable.profileimage)
                                        profileImageOpponent.getLayoutParams().height = 120
                                        profileImageOpponent.getLayoutParams().width = 120
                                    }
                                }
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

                            if(isOpponentSet and (cur_user == opponent)){
                                var recordOpponent = findViewById(R.id.RecordTextOpponent) as TextView
                                recordOpponent.text = "Record: " + (items.users?.get(tot)?.score!!).toString()
                                var rankingOpponent = findViewById(R.id.RankingTextOpponent) as TextView
                                rankingOpponent.text = "Ranking: " + (tot + 1).toString() + "°"
                            }

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

                if(isOpponentSet) {
                    var winnerView = findViewById(R.id.WinnerText) as TextView
                    if (opponentResult == 0) {
                        winnerView.text =
                            "You're beating " + opponent + " by " + difference + " points!"
                        winnerView.setTextColor(Color.GREEN)
                    } else if (opponentResult == 1) {
                        winnerView.text =
                            "You need " + difference + " points more to beat " + opponent + "!"
                        winnerView.setTextColor(Color.RED)
                    } else {
                        winnerView.text = "You have the same record of " + opponent + "!"
                        winnerView.setTextColor(Color.YELLOW)
                    }
                }

                firebaseAuth.uid?.let {
                    var rankingText = findViewById(R.id.RankingText) as TextView
                    isFinished = true
                    isFinishedOpponent = true
                    rankingText.text = "Ranking: " + ranking.toString() + "°"
                }
            }
        }
    }

    suspend fun getDifference(username1: String, username2: String) {
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

        var rankingText = findViewById(R.id.RankingText) as TextView
        var numPointsRanking = 1
        val timerRanking = Timer()
        timerRanking.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                Handler(Looper.getMainLooper()).post(java.lang.Runnable {
                    if(!isFinished) {
                        if (numPointsRanking == 4) numPointsRanking = 1
                        rankingText.text = "Ranking: " + ".".repeat(numPointsRanking)
                        numPointsRanking += 1
                    }
                })
            }
        }, 10, 600)

        if(isOpponentSet){
            var rankingTextOpponent = findViewById(R.id.RankingTextOpponent) as TextView

            var numPointsRankingOpponent  = 1
            val timerRankingOpponent  = Timer()
            timerRankingOpponent .scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    Handler(Looper.getMainLooper()).post(java.lang.Runnable {
                        if(!isFinishedOpponent) {
                            if (numPointsRankingOpponent  == 4) numPointsRankingOpponent  = 1
                            rankingTextOpponent .text = "Ranking: " + ".".repeat(numPointsRankingOpponent )
                            numPointsRankingOpponent  += 1
                        }
                    })
                }
            }, 10, 600)
        }

        val differenceApi = Request_Difference().retrofit.create(DifferenceInterface::class.java)
        CoroutineScope(Dispatchers.IO).launch {

            // Do the GET request and get response
            var response = differenceApi.getDifference(username1, username2)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    val items = response.body()
                    if (items != null) {
                        var winner = items.winner
                        difference = items.difference!!
                        Log.i("DIFFERENCE", items.toString())

                        if(winner == current_username) {
                            opponentResult = 0
                        } else if(winner == opponent) {
                            opponentResult = 1
                        } else {
                            opponentResult = 2
                        }

                        GlobalScope.launch {
                            getLeaderB()
                        }
                    }
                }
            }
        }
    }
}
