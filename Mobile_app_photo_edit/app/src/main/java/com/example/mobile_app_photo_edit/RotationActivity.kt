package com.example.mobile_app_photo_edit

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.image_view
import kotlinx.android.synthetic.main.activity_rotation.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class RotationActivity : AppCompatActivity() {

    var bitmap: Bitmap? = null
    var rotate: Float = 0f
    var image_uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation)

        image_uri = intent.getParcelableExtra(MainActivity.MY_MESSAGE_KEY)
        image_vieww.setImageURI(image_uri)
        val drawable = image_vieww.drawable as BitmapDrawable
        var bitmap = drawable.bitmap
        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                image_vieww.animate().rotation((progress.toFloat() - 45 + rotate))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                   // rotate+= (seekBar.progress.toFloat() - 45)
                }
            }

        })
        btn_rotate.setOnClickListener{
            rotate+= 90
            image_vieww.animate().rotation(rotate)
            seekBar.progress = 45

        }
        btn_save.setOnClickListener{
            var matrix = Matrix()
            matrix.postRotate(rotate)
            bitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )
            image_uri = bitmapToFile(bitmap!!)
            onBackPressed()
        }
    }

    private fun bitmapToFile(bitmap:Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(applicationContext)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }


    override fun onBackPressed() {
        var intent = Intent().apply {
            putExtra("uri", image_uri.toString())
        }
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
