package com.example.nabroski

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var image_uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Не забыть добавить иконки https://www.youtube.com/watch?v=ncHjCsoj0Ws
        fun_button.setOnClickListener{
            val popupMenu = PopupMenu(this, it)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId){
                    R.id.image_rotation -> {
                        Toast.makeText(applicationContext, "rotate it!", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.image_broken_lines -> {
                        Toast.makeText(applicationContext, "broken lines", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.image_color_correction -> {
                        Toast.makeText(applicationContext, "correction", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.image_filtres -> {
                        Toast.makeText(applicationContext, "Filters", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.image_retouching -> {
                        Toast.makeText(applicationContext, "Retouching", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.image_scaling -> {
                        Toast.makeText(applicationContext, "Scaling", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.image_segmentation -> {
                        Toast.makeText(applicationContext, "Segmentation", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.image_unsharp_masking -> {
                        Toast.makeText(applicationContext, "Unsharp masking", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else
                        ->false
                }
            }
            popupMenu.inflate(R.menu.menu_functions)
            popupMenu.show()
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

    private fun openCam()
    {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(intent, PERMISSION_CODE)
    }

    private fun pickImageFromGallery()
    {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object{
        private val IMAGE_CAPTURE_CODE: Int = 1002
        private val IMAGE_PICK_CODE = 1000
        private val PERMISSION_CODE = 1001
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
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image_view.setImageURI(data?.data)
        }
        else if(resultCode == Activity.RESULT_OK){
            image_view.setImageURI(image_uri)
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
            R.id.image_save -> toast.show()
        }
        return false
    }

}