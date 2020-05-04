package com.example.nabroski

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    var check: Boolean = false
    var image_uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Не забыть добавить иконки https://www.youtube.com/watch?v=ncHjCsoj0Ws
        fun_button.setOnClickListener{
            if(image_uri != null) {
                check = true
                val popupMenu = PopupMenu(this, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.image_rotation -> {
                            var intent = Intent(MainActivity@this, RotationActivity::class.java)
                            intent.putExtra(MainActivity.MY_MESSAGE_KEY,image_uri)
                            startActivityForResult(intent, 222)
                            true
                        }
                        R.id.image_broken_lines -> {
                            Toast.makeText(applicationContext, "Work in progress!", Toast.LENGTH_SHORT)
                                .show()
                            true
                        }
                        R.id.image_color_correction -> {
                            Toast.makeText(applicationContext, "Work in progress!", Toast.LENGTH_SHORT)
                                .show()
                            true
                        }
                        R.id.image_filtres -> {
                            Toast.makeText(applicationContext, "Work in progress!", Toast.LENGTH_SHORT).
                            show()
                            true
                        }
                        R.id.image_retouching -> {
                            Toast.makeText(applicationContext, "Work in progress!", Toast.LENGTH_SHORT)
                                .show()
                            true
                        }
                        R.id.image_scaling -> {
                            Toast.makeText(applicationContext, "Work in progress!", Toast.LENGTH_SHORT).
                            show()
                            true
                        }
                        R.id.image_segmentation -> {
                            Toast.makeText(applicationContext, "Work in progress!", Toast.LENGTH_SHORT)
                                .show()
                            true
                        }
                        R.id.image_unsharp_masking -> {
                            Toast.makeText(applicationContext, "Work in progress!", Toast.LENGTH_SHORT
                            ).show()
                            true
                        }
                        else
                        -> false
                    }
                }
                popupMenu.inflate(R.menu.menu_functions)
                popupMenu.show()
            }
            else {
                val toast = Toast.makeText(applicationContext, "Set Image!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }

        img_pick_btn.setOnClickListener{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else {
                    pickImageFromGallery()
                }
            }
            else{
                pickImageFromGallery()
            }
        }
        btn_cam.setOnClickListener{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if (checkCallingOrSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ){
                    val permissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else {
                    openCam()
                }
            }
            else{
                openCam()
            }
        }

    }

    private fun openCam() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(intent, PERMISSION_CODE)
    }


    private fun pickImageFromGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 111)
    }


    companion object{
        private val IMAGE_CAPTURE_CODE: Int = 1002
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
        val MY_MESSAGE_KEY = "Lol"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if(grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery()
                }
                else{
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 111){
            image_vieww.setImageURI(data?.data)
            image_uri = data?.data
        }
        else if(resultCode == Activity.RESULT_OK && requestCode == PERMISSION_CODE){
        image_vieww.setImageURI(image_uri)
        }
        else if(resultCode == Activity.RESULT_OK){
            val temp  = data!!.getStringExtra("uri")
            image_uri = Uri.parse(temp)
            image_vieww.setImageURI(image_uri)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemview = item.itemId
        val toast = Toast.makeText(applicationContext, "Saved!", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        when(itemview){
            R.id.image_save -> saveImageToInternalStorage()
        }
        return false
    }

    private fun saveImageToInternalStorage(){
        // Get the image from drawable resource as drawable object
        val drawable = image_vieww.drawable as BitmapDrawable
        var bitmap = drawable.bitmap

        // Get the context wrapper instance
        val wrapper = ContextWrapper(applicationContext)

        // Initializing a new file
        // The bellow line return a directory in internal storage
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)


        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
        }
    }
}

