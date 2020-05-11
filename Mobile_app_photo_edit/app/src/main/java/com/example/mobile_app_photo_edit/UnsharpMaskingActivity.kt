package com.example.mobile_app_photo_edit

import android.R.color
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_color_filtres.*
import kotlinx.android.synthetic.main.activity_color_filtres.btn_save
import kotlinx.android.synthetic.main.activity_main.image_view
import kotlinx.android.synthetic.main.activity_unsharp_masking.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Math.abs
import java.util.*


class UnsharpMaskingActivity : AppCompatActivity() {

    var image_uri: Uri? = null
    var bitmap: Bitmap? = null
    val sharpenForce = 1f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsharp_masking)

        image_uri = intent.getParcelableExtra(MainActivity.MY_MESSAGE_KEY)
        image_view.setImageURI(image_uri)
        val drawable = image_view.drawable as BitmapDrawable
        bitmap = drawable.bitmap

        btn_unsharpMasking.setOnClickListener{
            unsharpMaskingFilter()
        }
        btn_save.setOnClickListener{
            image_uri = bitmapToFile(bitmap!!)
            onBackPressed()
        }
    }

    private fun unsharpMaskingFilter() {
        val kernel = arrayOf(
            floatArrayOf(1f, 4f, 6f, 4f, 1f),
            floatArrayOf(4f, 16f, 24f, 16f, 4f),
            floatArrayOf(6f, 24f, 36f, 24f, 6f),
            floatArrayOf(4f, 16f, 24f, 16f, 4f),
            floatArrayOf(1f, 4f, 6f, 4f, 1f)
        )
        val bmp_Copy: Bitmap = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        for(i in 0..2) {
            for (Y in 3 until bitmap!!.height - 3) {
                for (X in 3 until bitmap!!.width - 3) {
                    var newPixelValueA = 0f
                    var newPixelValueR = 0f
                    var newPixelValueG = 0f
                    var newPixelValueB = 0f
                    for (YK in -1..3) {
                        for (XK in -1..3) {
                            var pixelColor = bmp_Copy.getPixel((X + XK), (Y + YK))
                            //val PixelPosition: Int = (Y + YK) * bitmap!!.width + (X + XK)
                            val pixelValueA: Float = (Color.alpha(pixelColor)).toFloat()
                            val pixelValueR: Float = (Color.red(pixelColor)).toFloat()
                            val pixelValueG: Float = (Color.green(pixelColor)).toFloat()
                            val pixelValueB: Float = (Color.blue(pixelColor)).toFloat()
                            newPixelValueA += kernel[YK + 1][XK + 1] * pixelValueA
                            newPixelValueR += kernel[YK + 1][XK + 1] * pixelValueR
                            newPixelValueG += kernel[YK + 1][XK + 1] * pixelValueG
                            newPixelValueB += kernel[YK + 1][XK + 1] * pixelValueB
                        }
                    }
                    var pixelColor = bmp_Copy.getPixel(X, Y)
                    var oldPixelR = (Color.red(pixelColor)).toFloat()
                    var oldPixelG = (Color.green(pixelColor)).toFloat()
                    var oldPixelB = (Color.blue(pixelColor)).toFloat()
                    newPixelValueR /= 256
                    newPixelValueG /= 256
                    newPixelValueB /= 256
                    //newPixelValueR = kotlin.math.abs(newPixelValueR - oldPixelR)
                     //newPixelValueG = kotlin.math.abs(newPixelValueG - oldPixelG)
                     //newPixelValueB = kotlin.math.abs(newPixelValueB - oldPixelB)
                    val newPixel = Color.argb(
                        255,
                        (newPixelValueR).toInt(),
                        (newPixelValueG).toInt(),
                        (newPixelValueB).toInt()
                    )
                    bmp_Copy.setPixel(X, Y, newPixel)
                }
            }
        }
        for (Y in 1 until bitmap!!.height - 1) {
            for (X in 1 until bitmap!!.width - 1) {
                var newPixelColor = bmp_Copy.getPixel(X, Y)
                var newPixelValueR = (Color.red(newPixelColor)).toFloat()
                var newPixelValueG =(Color.green(newPixelColor)).toFloat()
                var newPixelValueB = (Color.blue(newPixelColor)).toFloat()
                var pixelColor = bitmap!!.getPixel(X, Y)
                var oldPixelR = (Color.red(pixelColor)).toFloat()
                var oldPixelG = (Color.green(pixelColor)).toFloat()
                var oldPixelB = (Color.blue(pixelColor)).toFloat()
                newPixelValueR = kotlin.math.abs(newPixelValueR - oldPixelR)
                newPixelValueG = kotlin.math.abs(newPixelValueG - oldPixelG)
                newPixelValueB = kotlin.math.abs(newPixelValueB - oldPixelB)
                val newPixel = Color.argb(255, (newPixelValueR).toInt(), (newPixelValueG).toInt(), (newPixelValueB).toInt())
                bmp_Copy.setPixel(X, Y, newPixel)
            }
        }
        image_view.setImageBitmap(bmp_Copy)
        bitmap = bmp_Copy
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
