package com.example.mobile_app_photo_edit

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_color_filtres.*
import kotlinx.android.synthetic.main.activity_main.image_view
import java.io.*
import java.util.*

class ColorFiltersActivity : AppCompatActivity() {

    var image_uri: Uri? = null
    var bitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_filtres)

        image_uri = intent.getParcelableExtra(MainActivity.MY_MESSAGE_KEY)
        image_view.setImageURI(image_uri)
        val drawable = image_view.drawable as BitmapDrawable
        bitmap = drawable.bitmap

        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val byteArray = stream.toByteArray()
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        image_view.setImageBitmap(bitmap)

        btn_blackwhite.setOnClickListener{
            BlackWhite()
        }
        btn_save.setOnClickListener{
           image_uri = bitmapToFile(bitmap!!)
            onBackPressed()
        }

    }

    private fun BlackWhite(){
        val photoWidth = bitmap!!.width
        val photoHeight = bitmap!!.height
        val bmp_Copy: Bitmap = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        for(i in 0..photoWidth - 1){
            for(j in 0..photoHeight - 1){
                var pixelColor = bitmap!!.getPixel(i, j)
                var pixelAlpha = Color.alpha(pixelColor)
                var pixelRed = Color.red(pixelColor)
                var pixelBlue = Color.blue(pixelColor)
                var pixelGreen = Color.green(pixelColor)
                var pixelAverage = (pixelRed + pixelBlue + pixelGreen) / 3
                val newPixel = Color.argb(pixelAlpha, pixelAverage, pixelAverage, pixelAverage)
                bmp_Copy.setPixel(i, j, newPixel)
            }
        }
        bitmap = bmp_Copy
        image_view.setImageBitmap(bitmap)
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
