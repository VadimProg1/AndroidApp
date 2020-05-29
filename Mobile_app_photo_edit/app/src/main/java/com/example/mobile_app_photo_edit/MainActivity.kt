package com.example.mobile_app_photo_edit

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

    var image_uri: Uri? = null
    var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Не забыть добавить иконки https://www.youtube.com/watch?v=ncHjCsoj0Ws
        fun_button.setOnClickListener{
            if(image_uri != null) {
                val popupMenu = PopupMenu(this, it)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.image_rotation -> {
                            var intent = Intent(this, RotationActivity::class.java)
                            intent.putExtra(MainActivity.ACTIVITIES_MESSAGE_KEY,image_uri)
                            startActivityForResult(intent, ACTIVITIES_CODE)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            true
                        }
                        R.id.image_broken_lines -> {
                            var intent = Intent(this, SplineInterpolationActivity::class.java)
                            intent.putExtra(MainActivity.ACTIVITIES_MESSAGE_KEY,image_uri)
                            startActivityForResult(intent, ACTIVITIES_CODE)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            true
                        }
                        R.id.image_color_correction -> {
                            var intent = Intent(this, ColorFiltersActivity::class.java)
                            intent.putExtra(MainActivity.ACTIVITIES_MESSAGE_KEY,image_uri)
                            startActivityForResult(intent, ACTIVITIES_CODE)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            true
                        }
                        R.id.image_retouching -> {
                            var intent = Intent(this, RetouchingActivity::class.java)
                            intent.putExtra(MainActivity.ACTIVITIES_MESSAGE_KEY,image_uri)
                            startActivityForResult(intent, ACTIVITIES_CODE)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            true
                        }
                        R.id.image_scaling -> {

                            var intent = Intent(this, ScalingActivity::class.java)
                            intent.putExtra(MainActivity.ACTIVITIES_MESSAGE_KEY,image_uri)
                            startActivityForResult(intent, ACTIVITIES_CODE)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            true
                        }
                        R.id.image_unsharp_masking -> {
                            var intent = Intent(this, UnsharpMaskingActivity::class.java)
                            intent.putExtra(MainActivity.ACTIVITIES_MESSAGE_KEY,image_uri)
                            startActivityForResult(intent, ACTIVITIES_CODE)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            true
                        }
                        R.id.cube_3d -> {
                            var intent = Intent(this, Cube3DActivity::class.java)
                            intent.putExtra(MainActivity.ACTIVITIES_MESSAGE_KEY,image_uri)
                            startActivityForResult(intent, ACTIVITIES_CODE)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            true
                        }
                        R.id.shashlick -> {
                            var intent = Intent(this, ShashlickActivity::class.java)
                            intent.putExtra(MainActivity.ACTIVITIES_MESSAGE_KEY,image_uri)
                            startActivityForResult(intent, ACTIVITIES_CODE)
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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
                val toast = Toast.makeText(applicationContext, "Set Image", Toast.LENGTH_SHORT)
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
        private val ACTIVITIES_CODE = 1111
        val ACTIVITIES_MESSAGE_KEY = "Secret massage"
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
                    val toast = Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 111){
            image_view.setImageURI(data?.data)
            image_uri = data?.data
        }
        else if(resultCode == Activity.RESULT_OK && requestCode == PERMISSION_CODE){
            image_view.setImageURI(image_uri)
        }
        else if(resultCode == Activity.RESULT_OK && requestCode == ACTIVITIES_CODE){
            val temp  = data!!.getStringExtra("uri")
            image_uri = Uri.parse(temp)
            image_view.setImageURI(image_uri)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    public fun saveImageToStorage(){
        val toast = Toast.makeText(applicationContext, "Image saved!", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
        val drawable = image_view.drawable as BitmapDrawable
        var bitmap = drawable.bitmap
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, image_uri.toString() , "image")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemview = item.itemId
        if(image_uri != null) {
            when (itemview) {
                R.id.image_save -> saveImageToStorage()
            }
            return false
        }
        else {
            val toast = Toast.makeText(applicationContext, "Set image", Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
            return false
        }
    }

}

