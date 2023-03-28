package project.mobile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authentication variable

    //Email, username and password variables
    private var username : String = ""
    private var email : String = ""
    private var password : String = ""

    val storage = Firebase.storage      //Firebase Storage variable

    lateinit var imageUri: Uri          //Variable that will contain the URI of the image
    var imageChosen = false             //Variable that says if the user has chosen an image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)   //Display the sign up XML

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        supportActionBar?.setTitle("                     Death Planes")     //Decide the title of the application

        //Getting the references to the Views
        var usernameView = (findViewById(R.id.Username) as EditText)
        var emailView = (findViewById(R.id.Email) as EditText)
        var passwordView = (findViewById(R.id.Password) as EditText)
        var resetbutton = findViewById(R.id.ResetButton) as Button
        var submitbutton = findViewById(R.id.SubmitButton) as Button

        //Manage the reset button
        resetbutton.setOnClickListener {
            usernameView.setText("")
            emailView.setText("")
            passwordView.setText("")

            //Hide Change Button
            val changeButton = findViewById(R.id.ChangeButton) as Button
            changeButton.visibility = View.INVISIBLE
            changeButton.isClickable = false

            //Hide Remove Button
            val removeButton = findViewById(R.id.RemoveButton) as Button
            removeButton.visibility = View.INVISIBLE
            removeButton.isClickable = false

            //Show Photo Button
            val photoButton = findViewById(R.id.PhotoButton) as Button
            photoButton.visibility = View.VISIBLE
            photoButton.isClickable = true

            //Show Gallery Button
            val galleryButton = findViewById(R.id.GalleryButton) as Button
            galleryButton.visibility = View.VISIBLE
            galleryButton.isClickable = true

            //Set the standard profile image
            val profileImage = findViewById(R.id.ProfileImage) as ImageView
            profileImage.setImageResource(R.drawable.profileimage)

            //Reinitialize image uri variable
            imageChosen = false
        }

        /// intent with new method to return photo after finish camera activity
        var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data!!.getStringExtra("Image")?.toUri()!!
                imageChosen = true

                //Hide Photo Button
                val photoButton = findViewById(R.id.PhotoButton) as Button
                photoButton.visibility = View.INVISIBLE
                photoButton.isClickable = false

                //Hide Gallery Button
                val galleryButton = findViewById(R.id.GalleryButton) as Button
                galleryButton.visibility = View.INVISIBLE
                galleryButton.isClickable = false

                //Show image
                val profileImage = findViewById(R.id.ProfileImage) as ImageView
                profileImage.setImageURI(imageUri)

                //Show Change Button
                val changeButton = findViewById(R.id.ChangeButton) as Button
                changeButton.visibility = View.VISIBLE
                changeButton.isClickable = true

                //Show Remove Button
                val removeButton = findViewById(R.id.RemoveButton) as Button
                removeButton.visibility = View.VISIBLE
                removeButton.isClickable = true
                removeButton.setOnClickListener {
                    //Hide Change Button
                    changeButton.visibility = View.INVISIBLE
                    changeButton.isClickable = false

                    //Hide Remove Button
                    removeButton.visibility = View.INVISIBLE
                    removeButton.isClickable = false

                    //Show Photo Button
                    val photoButton = findViewById(R.id.PhotoButton) as Button
                    photoButton.visibility = View.VISIBLE
                    photoButton.isClickable = true

                    //Show Gallery Button
                    val galleryButton = findViewById(R.id.GalleryButton) as Button
                    galleryButton.visibility = View.VISIBLE
                    galleryButton.isClickable = true

                    //Set the standard profile image
                    profileImage.setImageResource(R.drawable.profileimage)

                    //Reinitialize image uri variable
                    imageChosen = false
                }
            }
        }

        //Listener for Photo Button
        val photoButton = findViewById(R.id.PhotoButton) as Button
        photoButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            resultLauncher.launch(intent)
        }

        //Listener for Change Button
        val changeButton = findViewById(R.id.ChangeButton) as Button
        changeButton.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            resultLauncher.launch(intent)
        }

        // result launcher to get the result of the intent of take image from gallery
        var resultLauncherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                imageUri = data?.data!!

                imageChosen = true

                //Hide Photo Button
                val photoButton = findViewById(R.id.PhotoButton) as Button
                photoButton.visibility = View.INVISIBLE
                photoButton.isClickable = false

                //Hide Gallery Button
                val galleryButton = findViewById(R.id.GalleryButton) as Button
                galleryButton.visibility = View.INVISIBLE
                galleryButton.isClickable = false

                //Show image
                val profileImage = findViewById(R.id.ProfileImage) as ImageView
                profileImage.setImageURI(imageUri)

                //Show Change Button
                val changeButton = findViewById(R.id.ChangeButton) as Button
                changeButton.visibility = View.VISIBLE
                changeButton.isClickable = true

                //Show Remove Button
                val removeButton = findViewById(R.id.RemoveButton) as Button
                removeButton.visibility = View.VISIBLE
                removeButton.isClickable = true
                removeButton.setOnClickListener {
                    //Hide Change Button
                    changeButton.visibility = View.INVISIBLE
                    changeButton.isClickable = false

                    //Hide Remove Button
                    removeButton.visibility = View.INVISIBLE
                    removeButton.isClickable = false

                    //Show Photo Button
                    val photoButton = findViewById(R.id.PhotoButton) as Button
                    photoButton.visibility = View.VISIBLE
                    photoButton.isClickable = true

                    //Show Gallery Button
                    val galleryButton = findViewById(R.id.GalleryButton) as Button
                    galleryButton.visibility = View.VISIBLE
                    galleryButton.isClickable = true

                    //Set the standard profile image
                    profileImage.setImageResource(R.drawable.profileimage)

                    //Reinitialize image uri variable
                    imageChosen = false
                }
            }
        }
        //gallery button listener
        var galleryButton = findViewById(R.id.GalleryButton) as Button
        galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncherGallery.launch(intent)
        }


        //Setting the listener of the onClick event of the submit button
        submitbutton.setOnClickListener {
            username = usernameView.text.toString()
            email = emailView.text.toString()
            password = passwordView.text.toString()

            //Check if the username is not empty
            if(username.isEmpty()){
                Toast.makeText(this, "Username not inserted", Toast.LENGTH_SHORT).show()
            }
            //Check if the email is not empty
            else if(email.isEmpty()){
                Toast.makeText(this, "Email not inserted", Toast.LENGTH_SHORT).show()
            }
            //Check if the password is not empty
            else if(password.isEmpty()){
                Toast.makeText(this, "Password not inserted", Toast.LENGTH_SHORT).show()
            }
            else if(password.length < 6){
                Toast.makeText(this, "Password should contain at least 6 characters", Toast.LENGTH_SHORT).show()
            }
            else if(! password.contains("[0-9]".toRegex())){
                Toast.makeText(this, "Password should contain at least a number", Toast.LENGTH_SHORT).show()
            }
            else if(! password.contains("[A-Z]".toRegex())){
                Toast.makeText(this, "Password should contain at least an uppercase character", Toast.LENGTH_SHORT).show()
            }
            else if(! password.contains("[a-z]".toRegex())){
                Toast.makeText(this, "Password should contain at least an lowercase character", Toast.LENGTH_SHORT).show()
            }
            else {

                //Creation of the user

                //Connecting to Firebase Database
                val database =
                    Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

                val referenceDB =
                    database.getReference("NumberOfUsers")    //Take the number of users


                //Get the number of users
                referenceDB.get().addOnSuccessListener {
                    var numberOfUsers = it.value.toString().toInt()

                    var found = false
                    var referenceUsername: DatabaseReference

                    for (i in 1..numberOfUsers) {
                        referenceUsername =
                            database.getReference("Users/$i/Username")    //Reference to username in the Database

                        referenceUsername.get().addOnSuccessListener {
                            if ((it.value.toString() == username) and !found) {
                                found = true
                                //If username already exists
                                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT)
                                    .show()
                            } else if ((i == numberOfUsers) and !found) {
                                //If username does not exist
                                firebaseAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener {
                                        //Increment the number of users
                                        numberOfUsers += 1
                                        referenceDB.setValue(numberOfUsers)

                                        //Save Username
                                        val referenceUser =
                                            database.getReference("Users/$numberOfUsers/Username")
                                        referenceUser.setValue(username)

                                        //Save Email
                                        val referenceEmail =
                                            database.getReference("Users/$numberOfUsers/Email")
                                        referenceEmail.setValue(email)

                                        //Save Password
                                        val referencePassword =
                                            database.getReference("Users/$numberOfUsers/Password")
                                        //Create the Password Hash Manager variable and save the hashed password with sha256
                                        var hash = PasswordHashManager()
                                        referencePassword.setValue(hash.encryptSHA256(password))

                                        //Save Score
                                        val referenceScore =
                                            database.getReference("Users/$numberOfUsers/Score")
                                        referenceScore.setValue("0")

                                        //Take and save the user id
                                        var uid = firebaseAuth.uid
                                        val referenceUid =
                                            database.getReference("Users/$numberOfUsers/UID")
                                        referenceUid.setValue(uid)

                                        //Save the photo in the Database
                                        if (imageChosen) {
                                            Log.i("PROVA", imageUri.toString())
                                            // Create a storage reference from our app
                                            var storageRef = storage.reference

                                            var file = imageUri

                                            val imageRef = storageRef.child("Images/$username")
                                            var uploadTask = file?.let { imageRef.putFile(it) }

                                            // Register observers to listen for when the download is done or if it fails
                                            if (uploadTask != null) {
                                                uploadTask.addOnFailureListener {
                                                    Log.i("STORAGE", "Failed")
                                                }.addOnSuccessListener { taskSnapshot ->
                                                    Log.i("STORAGE", "Success")

                                                    //Execute the log in after success
                                                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                                            .addOnCompleteListener {
                                                                if (it.isSuccessful) {
                                                                    intent =
                                                                        Intent(this, MainActivity::class.java)
                                                                    current_username = username
                                                                    current_id = numberOfUsers.toString()
                                                                    current_score = 0
                                                                    startActivity(intent)
                                                                } else {
                                                                    Toast.makeText(
                                                                        this,
                                                                        it.exception.toString(),
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                            }
                                                }
                                            }
                                        }
                                        else {
                                            //Execute the log in when the photo is not chosen
                                            firebaseAuth.signInWithEmailAndPassword(email, password)
                                                .addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        intent =
                                                            Intent(this, MainActivity::class.java)
                                                        current_username = username
                                                        current_id = numberOfUsers.toString()
                                                        current_score = 0
                                                        startActivity(intent)
                                                    } else {
                                                        Toast.makeText(
                                                            this,
                                                            it.exception.toString(),
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        }
                                    }.addOnFailureListener {
                                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
        val houseButton = findViewById(R.id.HouseButton) as ImageButton
        houseButton.setOnClickListener() {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}