package project.mobile

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.impl.ImageCaptureConfig
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_camera.*
import project.mobile.databinding.ActivityCameraBinding
import project.mobile.databinding.ActivityChangePhotoProfileBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ChangePhotoProfile : AppCompatActivity() {

    //Binding to the Camera Activity XML
    private lateinit var binding: ActivityChangePhotoProfileBinding

    //Define the output directory
    private lateinit var outputDirectory: File

    //Define camera variables
    private var imageCapture: ImageCapture? = null
    private var imageCaptureConfig: ImageCaptureConfig?= null
    private lateinit var cameraExecutor: ExecutorService

    public lateinit var yesButton: ImageButton

    var context = this

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setTitle("                         Death Planes")     //Define the name of the application

        //Inflate the camera binding
        binding = ActivityChangePhotoProfileBinding.inflate(layoutInflater)

        //Set the XML vire
        setContentView(binding.root)

        //Camera button listener
        var photoButton = findViewById(R.id.PhotoButton) as ImageButton
        photoButton.setOnClickListener {
            takePhoto()
        }

        //Back button listener
        var backButton = findViewById(R.id.BackButton) as ImageButton
        backButton.setOnClickListener {
            finish()
        }

        //Listener for No button
        var noButton = findViewById(R.id.NoButton) as ImageButton
        noButton.setOnClickListener{
            //Hide Yes Button
            var yesButton = findViewById(R.id.YesButton) as ImageButton
            yesButton.visibility = View.INVISIBLE
            yesButton.isClickable = false

            //Hide No Button
            var noButton = findViewById(R.id.NoButton) as ImageButton
            noButton.visibility = View.INVISIBLE
            noButton.isClickable = false

            //Show Gallery Button
            var galleryButton = findViewById(R.id.GalleryButton) as Button
            galleryButton.visibility = View.VISIBLE
            galleryButton.isClickable = true

            //Show Back Button
            var backButton = findViewById(R.id.BackButton) as ImageButton
            backButton.visibility = View.VISIBLE
            backButton.isClickable = true

            //Show preview
            var previewView = findViewById(R.id.viewFinder) as PreviewView
            previewView.visibility = View.VISIBLE

            //Show Photo Button
            var photoButton = findViewById(R.id.PhotoButton) as ImageButton
            photoButton.visibility = View.VISIBLE
            photoButton.isClickable = true
        }

        ///******************** GALLERY INTENT MANAGEMENT *******************///////////////////
        // result launcher to get the result of the intent of take image from gallery
        var resultLauncherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                imageUri = data?.data!!

                var imageView = findViewById(R.id.iv_capture) as ImageView
                imageView.visibility = View.VISIBLE
                imageView.setImageURI(imageUri)

                var previewView = findViewById(R.id.viewFinder) as PreviewView
                previewView.visibility = View.INVISIBLE

                //Show Yes Button
                var yesButton = findViewById(R.id.YesButton) as ImageButton
                yesButton.visibility = View.VISIBLE
                yesButton.isClickable = true

                //Show No Button
                var noButton = findViewById(R.id.NoButton) as ImageButton
                noButton.visibility = View.VISIBLE
                noButton.isClickable = true

                //Hide Photo Button
                var photoButton = findViewById(R.id.PhotoButton) as ImageButton
                photoButton.visibility = View.INVISIBLE
                photoButton.isClickable = false

                //Hide Gallery Button
                var galleryButton = findViewById(R.id.GalleryButton) as Button
                galleryButton.visibility = View.INVISIBLE
                galleryButton.isClickable = false

                //Hide Back Button
                var backButton = findViewById(R.id.BackButton) as ImageButton
                backButton.visibility = View.INVISIBLE
                backButton.isClickable = false

                //Listener for Yes Button
                yesButton = findViewById(R.id.YesButton) as ImageButton
                yesButton.setOnClickListener{
                    //Intent to Sign Up activity
                    intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("Image", imageUri.toString())
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
        //gallery button listener
        var galleryButton = findViewById(R.id.GalleryButton) as Button
        galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncherGallery.launch(intent)
        }
        ///******************** END GALLERY MANAGEMENT *******************///////////////////

        //Get the output directory
        outputDirectory = getOutputDirectory()


        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this,
                ChangePhotoProfile.REQUIRED_PERMISSIONS,
                ChangePhotoProfile.REQUEST_CODE_PERMISSIONS
            )
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    //Function that takes the photo
    private fun takePhoto() {

        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Death Planes Images")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .setMetadata(ImageCapture.Metadata().also {
                it.isReversedHorizontal = true
            })
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo saved in gallery"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)

                    var imageView = findViewById(R.id.iv_capture) as ImageView
                    imageView.visibility = View.VISIBLE
                    imageView.setImageURI(output.savedUri)

                    var previewView = findViewById(R.id.viewFinder) as PreviewView
                    previewView.visibility = View.INVISIBLE

                    //Show Yes Button
                    var yesButton = findViewById(R.id.YesButton) as ImageButton
                    yesButton.visibility = View.VISIBLE
                    yesButton.isClickable = true

                    //Show No Button
                    var noButton = findViewById(R.id.NoButton) as ImageButton
                    noButton.visibility = View.VISIBLE
                    noButton.isClickable = true

                    //Hide Photo Button
                    var photoButton = findViewById(R.id.PhotoButton) as ImageButton
                    photoButton.visibility = View.INVISIBLE
                    photoButton.isClickable = false

                    //Hide Gallery Button
                    var galleryButton = findViewById(R.id.GalleryButton) as Button
                    galleryButton.visibility = View.INVISIBLE
                    galleryButton.isClickable = false

                    //Hide Back Button
                    var backButton = findViewById(R.id.BackButton) as ImageButton
                    backButton.visibility = View.INVISIBLE
                    backButton.isClickable = false

                    //Listener for Yes Button
                    yesButton = findViewById(R.id.YesButton) as ImageButton
                    yesButton.setOnClickListener{
                        //Intent to Sign Up activity
                        intent = Intent(context, SignUpActivity::class.java)
                        intent.putExtra("Image", output.savedUri.toString())
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }
            }
        )
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
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 123
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // creates a folder inside internal storage
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {mFile ->
            File(mFile, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        //return if (mediaDir != null && mediaDir.exists())
        //    mediaDir else filesDir
        return filesDir
    }
}