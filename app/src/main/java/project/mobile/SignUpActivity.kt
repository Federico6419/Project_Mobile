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

    private lateinit var binding:ActivityMainBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)   //Display the sign up XML

        supportActionBar?.setTitle("                     Death Planes")

        // Check camera permissions if all permission granted
        // start camera else ask for the permission
        if (allPermissionsGranted()) {
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
        cameraExecutor = Executors.newSingleThreadExecutor()

        //Getting the references to the Views
        var username = findViewById(R.id.Username) as EditText
        var password = findViewById(R.id.Password) as EditText
        var resetbutton = findViewById(R.id.ResetButton) as Button
        var submitbutton = findViewById(R.id.SubmitButton) as Button

        //Manage the reset button
        resetbutton.setOnClickListener {
            username.setText("")
            password.setText("")
        }

        //Setting the listener of the onClick event of the submit button
        submitbutton.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()

            //Connecting to Firebase Database
            val database = Firebase.database("https://mobileproject2-50486-default-rtdb.europe-west1.firebasedatabase.app/")

            val referenceDB = database.getReference("NumberOfUsers")

            var res = ""
            var numberOfUsers = 0
            referenceDB.get().addOnSuccessListener {
                res = it.value.toString()
                numberOfUsers = res.toInt()
                var referenceUsername = ""

                res = (numberOfUsers + 1).toString()
                var id = ""
                for(i in 1..5 - res.length){
                    id += "0"
                }
                id += res

                for(i in 1..numberOfUsers) {
                    var sid = ""
                    for (i in 1..5 - i.toString().length) {
                        sid += "0"
                    }
                    sid += i

                    val referenceUsername = database.getReference("Users/$sid/Username")
                    var res = ""

                    referenceUsername.get().addOnSuccessListener {
                        res = it.value.toString()
                        Log.i("TEST", res+user)
                        if (res == user) {
                            ///CAMBIA CON GESTIONE ERRORE
                            intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                        }
                        if(i == numberOfUsers && res != user){
                            numberOfUsers += 1
                            referenceDB.setValue(numberOfUsers)
                            val refUsername = database.getReference("Users/$id/Username")
                            refUsername.setValue(user)

                            val referencePassword = database.getReference("Users/$id/Password")
                            referencePassword.setValue(pass)

                            val referenceScore = database.getReference("Users/$id/Score")
                            referenceScore.setValue("0")

                            intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("Username",user)
                            intent.putExtra("Password",pass)
                            intent.putExtra("ID",id)
                            intent.putExtra("Score","0")
                            startActivity(intent)
                        }
                    }
                }

            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }

        }
    }

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
    }


}