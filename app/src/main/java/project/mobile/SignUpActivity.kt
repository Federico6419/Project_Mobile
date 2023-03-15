package project.mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_sign_up.*
import project.mobile.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SignUpActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable

    //Email, username and password variables
    private var username : String = ""
    private var email : String = ""
    private var password : String = ""

    //CAMERA////////////
    private lateinit var binding:ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    ////////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)   //Display the sign up XML

        firebaseAuth = FirebaseAuth.getInstance()   //Get instance from Firebase Authentication

        supportActionBar?.setTitle("                     Death Planes")     //Decide the title of the application

        //CAMERA////////////
        // Check camera permissions if all permission granted
        // start camera else ask for the permission
        /*if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // set on click listener for the button of capture photo
        // it calls a method which is implemented below
        findViewById<Button>(R.id.camera_capture_button).setOnClickListener {
            takePhoto()
        }
        outputDirectory = getOutputDirectory()
        //outputDirectory = ""
        cameraExecutor = Executors.newSingleThreadExecutor()*/
        /////////////////

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
        }

        //Setting the listener of the onClick event of the submit button
        submitbutton.setOnClickListener {
            username = usernameView.text.toString()
            email = emailView.text.toString()
            password = passwordView.text.toString()


            //Creation of the user

            //Connecting to Firebase Database
            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

            val referenceDB = database.getReference("NumberOfUsers")    //Take the number of users


            //Get the number of users
            referenceDB.get().addOnSuccessListener {
                var numberOfUsers = it.value.toString().toInt()

                var found = false
                var referenceUsername : DatabaseReference

                for (i in 1 .. numberOfUsers){
                    referenceUsername = database.getReference("Users/$i/Username")    //Reference to username in the Database

                    referenceUsername.get().addOnSuccessListener {
                        if(it.value.toString() == username){
                            found = true
                        }
                    }
                }


                //If username does not exist
                //DA METTERE L'EMPTY, CIOE: if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() && user == "null") {
                if (! found) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {      //If the user is created, create its informations on Firebase Database
                            //Increment the number of users
                            numberOfUsers += 1
                            referenceDB.setValue(numberOfUsers)

                            //Save Username
                            val referenceUser = database.getReference("Users/$numberOfUsers/Username")
                            referenceUser.setValue(username)

                            //Save Email
                            val referenceEmail = database.getReference("Users/$numberOfUsers/Email")
                            referenceEmail.setValue(email)

                            //Save Password
                            val referencePassword =
                                database.getReference("Users/$numberOfUsers/Password")
                            referencePassword.setValue(password)

                            //Save Score
                            val referenceScore = database.getReference("Users/$numberOfUsers/Score")
                            referenceScore.setValue("0")

                            //Take and save the user id
                            var uid = firebaseAuth.uid
                            val referenceUid = database.getReference("Users/$numberOfUsers/UID")
                            referenceUid.setValue(uid)

                            //Execute the log in
                            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        intent = Intent(this, MainActivity::class.java)
                                        intent.putExtra("Username", username)
                                        intent.putExtra("Score", "0")
                                        intent.putExtra("ID", numberOfUsers.toString())
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Email already used", Toast.LENGTH_SHORT).show()
                    }
                }
                else{       //If username already exists
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //CAMERA
    /*
    private fun takePhoto() {
        // Get a stable reference of the
        // modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener,
        // which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)

                    // set the saved uri to the image view
                    findViewById<ImageView>(R.id.iv_capture).visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.iv_capture).setImageURI(savedUri)

                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                    Log.d(TAG, msg)
                }
            })
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({

            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all{
            ContextCompat.checkSelfPermission(
                baseContext, it
            ) == PackageManager.PERMISSION_GRANTED
        }

    // creates a folder inside internal storage
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            //File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
            File(it, "download").apply {
                mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    // checks the camera permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // If all permissions granted , then start Camera
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                // If permissions are not granted,
                // present a toast to notify the user that
                // the permissions were not granted.
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    companion object {
        private const val TAG = "CameraX"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 123
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }*///////////////////
}