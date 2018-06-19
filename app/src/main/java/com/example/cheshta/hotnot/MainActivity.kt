package com.example.cheshta.hotnot

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.example.cheshta.hotnot.classifier.Classifier
import com.example.cheshta.hotnot.classifier.*
import com.example.cheshta.hotnot.classifier.tensorflow.ImageClassifierFactory
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var classifier: Classifier

    private val handler = Handler()
    private var photoFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
    }

    private fun checkPermissions(){
        if(permissionGranted())
            init()
        else
            requestPermission()
    }

    private fun permissionGranted() =
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1)
    }

    private fun takePhoto(){
        photoFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath +
                "/${System.currentTimeMillis()}.jpg"
        val currentPhotoUri = Uri.fromFile(File(photoFilePath))

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        takePictureIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, 2)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 1 && arePermissionGranted(grantResults))
            takePhoto()
        else
            requestPermission()
    }

    private fun arePermissionGranted(grantResults: IntArray) =
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val file = File(photoFilePath)
        if(requestCode == 2 && file.exists())
            classifyPhoto()
    }

    private fun init(){
        createClassifier()
        takePhoto()
    }

    private fun createClassifier(){
        classifier = ImageClassifierFactory.create(
                assets,
                GRAPH_FILE_PATH,
                LABELS_FILE_PATH,
                IMAGE_SIZE,
                GRAPH_INPUT_NAME,
                GRAPH_OUTPUT_NAME
        )
    }

    private fun classifyPhoto(file: File){
        val photoBitmap = BitmapFactory.decodeFile(file.absolutePath)
    }
}
