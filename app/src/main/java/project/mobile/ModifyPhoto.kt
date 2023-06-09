package project.mobile

import android.content.Intent
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
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_modify_photo.*

var TAKE_FROM_GALLERY = false
class ModifyPhoto : AppCompatActivity() {

    //private lateinit var firebaseAuth: FirebaseAuth         //Firebase Authenticatoin variable
    val storage = Firebase.storage      //Firebase Storage variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setTitle("                         Death Planes")     //Define the name of the application

        setContentView(R.layout.activity_modify_photo)


        val houseButton = findViewById(R.id.HouseButton) as ImageButton
        houseButton.setOnClickListener() {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val ChangePhotoButton = findViewById(R.id.ChangeButton) as Button

        val PhotoButton = findViewById(R.id.PhotoButton) as Button

        var showimage = findViewById(R.id.ProfileImage) as ImageView

        val galleryButton = findViewById(R.id.GalleryButton) as Button

        // show remove button
        val RemoveButton = findViewById(R.id.RemoveButton) as Button
        ////remove photo button setted to true and remove from storage the photo if clicked
        RemoveButton.setOnClickListener() {

            RemoveButton.visibility = View.INVISIBLE
            RemoveButton.isClickable = false

            /// hide change photo button
            ChangePhotoButton.visibility = View.INVISIBLE
            ChangePhotoButton.isClickable = false

            /// hide submit photo button
            SubmitButton.visibility = View.INVISIBLE
            SubmitButton.isClickable = false

            /// show photo button
            PhotoButton.visibility = View.VISIBLE
            PhotoButton.isClickable = true

            //show Gallery Button
            galleryButton.visibility = View.VISIBLE
            galleryButton.isClickable = true

            /// show photoprofile generica
            showimage.setImageResource(R.drawable.profileimage)
            showimage.getLayoutParams().height = 200
            showimage.getLayoutParams().width = 200

            // Create a storage reference from our app
            val storageRefDelete = storage.reference

            // Create a reference to the file to delete
            val userImageRef = storageRefDelete.child("Images/" + current_username)

            // Delete the file
            userImageRef.delete().addOnSuccessListener {
                // File deleted successfully
                Toast.makeText(this, "Photo deleted successfully", Toast.LENGTH_SHORT)
                userimage = null
                hasphoto = false
            }.addOnFailureListener {
                // Uh-oh, an error occurred!
                Toast.makeText(this, "Something goes wrong", Toast.LENGTH_SHORT)
            }

            userimage = null
            hasphoto = false
        }

        ////submit intent
        val SubmitButton = findViewById(R.id.SubmitButton) as Button
        SubmitButton.setOnClickListener() {
            //save image on storage
            var storageRef = storage.reference

            var file = userimage
            hasphoto = true

            val imageRef = storageRef.child("Images/$current_username")
            var uploadTask = file?.let { imageRef.putFile(it) }

            // Register observers to listen for when the download is done or if it fails
            if (uploadTask != null) {
                uploadTask.addOnFailureListener {
                    Log.i("STORAGE", "Failed")
                }.addOnSuccessListener { taskSnapshot ->
                    Log.i("STORAGE", "Success")
                    intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }


        /// intent with new method to return photo after finish changePhotoProfile activity
        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    userimage = result.data!!.getStringExtra("Image")?.toUri()!!

                    /// hide take photo button
                    val PhotoButton = findViewById(R.id.PhotoButton) as Button
                    PhotoButton.visibility = View.INVISIBLE
                    PhotoButton.isClickable = false

                    //Show image
                    showimage.setImageURI(userimage)
                    showimage.getLayoutParams().height = 300
                    showimage.getLayoutParams().width = 240

                    //// show submit button
                    SubmitButton.visibility = View.VISIBLE
                    SubmitButton.isClickable = true

                    // show change photo button
                    ChangePhotoButton.visibility = View.VISIBLE
                    ChangePhotoButton.isClickable = true

                    //// hide gallery button
                    galleryButton.visibility = View.INVISIBLE
                    galleryButton.isClickable = false


                    /// show remove button
                    RemoveButton.visibility = View.VISIBLE
                    RemoveButton.isClickable = true

                    TAKE_FROM_GALLERY = false


                }
            }

        ////take photo profile intent
        PhotoButton.setOnClickListener() {
            intent = Intent(this, ChangePhotoProfile::class.java)
            resultLauncher.launch(intent)
        }
        ChangePhotoButton.setOnClickListener() {
            intent = Intent(this, ChangePhotoProfile::class.java)
            resultLauncher.launch(intent)
        }

        ///////////////////GALLERY RESULT LAUNCHER ///////////////////
        // result launcher to get the result of the intent of take image from gallery
        var resultLauncherGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                userimage = data?.data!!

                // hide photo button
                PhotoButton.visibility = View.INVISIBLE
                PhotoButton.isClickable = false

                //Hide Gallery Button
                galleryButton.visibility = View.INVISIBLE
                galleryButton.isClickable = false

                //Show image
                showimage.setImageURI(userimage)
                showimage.getLayoutParams().height = 300
                showimage.getLayoutParams().width = 240

                // show change photo button
                ChangePhotoButton.visibility = View.VISIBLE
                ChangePhotoButton.isClickable = true

                // show remove button
                RemoveButton.visibility = View.VISIBLE
                RemoveButton.isClickable = true

                /// show submit photo button
                SubmitButton.visibility = View.VISIBLE
                SubmitButton.isClickable = true

                TAKE_FROM_GALLERY = true

            }
        }
        //gallery button listener
        galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            resultLauncherGallery.launch(intent)
        }
        /////////////////////////////**************************//////////////////////////

        //// if user has already a photo stored
        if(userimage!=null){
            //Show image
            showimage.setImageURI(userimage)
            showimage.getLayoutParams().height = 300
            showimage.getLayoutParams().width = 240

            /// hide take photo button
            PhotoButton.visibility = View.INVISIBLE
            PhotoButton.isClickable = false

            // show remove button
            RemoveButton.visibility = View.VISIBLE
            RemoveButton.isClickable = true

            //Hide Gallery Button
            galleryButton.visibility = View.INVISIBLE
            galleryButton.isClickable = false

            ////change photo profile intent
            ChangePhotoButton.visibility = View.VISIBLE
            ChangePhotoButton.isClickable = true

        }else{
            ///hide submit button
            SubmitButton.visibility = View.INVISIBLE
            SubmitButton.isClickable = false
            /// hide change photo button
            ChangePhotoButton.visibility = View.INVISIBLE
            ChangePhotoButton.isClickable = false
            ////take photo profile intent
            PhotoButton.setOnClickListener() {
                intent = Intent(this, ChangePhotoProfile::class.java)
                resultLauncher.launch(intent)
            }
        }
    }
}