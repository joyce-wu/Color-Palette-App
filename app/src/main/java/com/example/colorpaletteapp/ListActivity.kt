package com.example.colorpaletteapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.example.colorpaletteapp.adapter.PaletteAdapter
import com.example.colorpaletteapp.data.PalettePost
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_list.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URLEncoder
import java.util.*

class ListActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private const val CAMERA_REQUEST_CODE = 1002
        private const val LIGHT_VIBRANT = "LIGHT VIBRANT"
        private const val VIBRANT = "VIBRANT"
        private const val DARK_VIBRANT = "DARK VIBRANT"
        private const val LIGHT_MUTED = "LIGHT MUTED"
        private const val MUTED = "MUTED"
        private const val DARK_MUTED = "DARK MUTED"
        private const val IMG_URL = "IMG_URL"
    }

    var uploadBitmap: Bitmap? = null
    private lateinit var paletteAdapter: PaletteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            startActivityForResult(
                Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                CAMERA_REQUEST_CODE
            )
        }

        paletteAdapter = PaletteAdapter(this)
        recyclerPalettes.adapter = paletteAdapter
        initPalettes()
    }

    fun initPalettes() {
        val db = FirebaseFirestore.getInstance()
        val query = db.collection("palettes")
        query.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                if (e != null) {
                    Toast.makeText(this@ListActivity, "Error: ${e.message}", Toast.LENGTH_LONG)
                        .show()
                    return
                }

                for (docChange in querySnapshot?.getDocumentChanges()!!) {
                    if (docChange.type == DocumentChange.Type.ADDED) {
                        val post = docChange.document.toObject(PalettePost::class.java)
                        paletteAdapter.addPalette(post, docChange.document.id)
                    } else if (docChange.type == DocumentChange.Type.REMOVED) {
                        paletteAdapter.removePostByKey(docChange.document.id)
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uploadBitmap = data!!.extras!!.get("data") as Bitmap
            try {
                uploadBitmapToFirebase()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun requestNeededPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {
                Toast.makeText(this, "I need it for camera", Toast.LENGTH_SHORT).show()
            }

            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), PERMISSION_REQUEST_CODE)
        }
    }

    @Throws(Exception::class)
    private fun uploadBitmapToFirebase() {
        val baos = ByteArrayOutputStream()
        uploadBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInBytes = baos.toByteArray()
        val storageRef = FirebaseStorage.getInstance().getReference()
        val newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg"
        val newImagesRef = storageRef.child("images/$newImage")

        newImagesRef.putBytes(imageInBytes)
            .addOnFailureListener { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener {
                newImagesRef.downloadUrl.addOnCompleteListener(object: OnCompleteListener<Uri> {
                    override fun onComplete(task: Task<Uri>) {
                        uploadPalette(task.result.toString())
                    }
                })
            }
    }

    private fun uploadPalette(imgURL: String) {
        val imgPalette = Palette.from(uploadBitmap!!).generate()
        val pal = PalettePost(
            imgURL,
            imgPalette.getLightVibrantColor(255255255),
            imgPalette.getVibrantColor(255255255),
            imgPalette.getDarkVibrantColor(255255255),
            imgPalette.getLightMutedColor(255255255),
            imgPalette.getMutedColor(255255255),
            imgPalette.getDarkMutedColor(255255255)
        )

        var paletteCollection = FirebaseFirestore.getInstance().collection("palettes")
        paletteCollection.add(pal)
            .addOnSuccessListener {
                Toast.makeText(this, "Post SAVED", Toast.LENGTH_LONG).show()
                showPalettePage(pal, imgURL)
            }
            .addOnFailureListener{
                Toast.makeText(this, "Error ${it.message}", Toast.LENGTH_LONG).show()
            }

    }

    private fun showPalettePage(pal: PalettePost, imgURL: String) {
        var intentDetails = Intent()
        intentDetails.setClass(this, PaletteActivity::class.java)
        intentDetails.putExtra(LIGHT_VIBRANT, pal.lightVibrant)
        intentDetails.putExtra(VIBRANT, pal.vibrant)
        intentDetails.putExtra(DARK_VIBRANT, pal.darkVibrant)
        intentDetails.putExtra(LIGHT_MUTED, pal.lightMuted)
        intentDetails.putExtra(MUTED, pal.muted)
        intentDetails.putExtra(DARK_MUTED, pal.darkMuted)
        intentDetails.putExtra(IMG_URL, imgURL)
        startActivity(intentDetails)
    }

}
