package com.example.mobile_app_photo_edit

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import kotlinx.android.synthetic.main.activity_color_filtres.*
import kotlinx.android.synthetic.main.activity_color_filtres.btn_save
import kotlinx.android.synthetic.main.activity_main.image_view
import kotlinx.android.synthetic.main.activity_unsharp_masking.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class UnsharpMaskingActivity : AppCompatActivity() {

    private var image_uri: Uri? = null
    var bitmap: Bitmap? = null
    private var bitmapUnsharpMask: Bitmap? = null
    private var bitmapContrast: Bitmap? = null
    private var bmpCopy: Bitmap? = null
    val sharpenForce = 1f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsharp_masking)

        image_uri = intent.getParcelableExtra(MainActivity.MY_MESSAGE_KEY)
        image_view.setImageURI(image_uri)
        val drawable = image_view.drawable as BitmapDrawable
        bitmap = drawable.bitmap

        val bitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        bitmap!!.getPixels(bitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)

        btn_unsharpMasking.setOnClickListener{
            progressBar1.visibility = View.VISIBLE
            val future = doAsync {
                contrastFilter(bitmapArray)
                bitmap!!.getPixels(bitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)
                unsharpMaskingFilter(bitmapArray)
                uiThread {
                    // use result here if you want to update ui
                    progressBar1.visibility = View.INVISIBLE
                }
            }
        }
        btn_save.setOnClickListener{
            image_uri = bitmapToFile(bitmap!!)
            onBackPressed()
        }
    }

    private fun unsharpMaskingFilter(bitmapArray: IntArray) {

        val kernel = arrayOf(
            floatArrayOf(1f, 4f, 6f, 4f, 1f),
            floatArrayOf(4f, 16f, 24f, 16f, 4f),
            floatArrayOf(6f, 24f, 36f, 24f, 6f),
            floatArrayOf(4f, 16f, 24f, 16f, 4f),
            floatArrayOf(1f, 4f, 6f, 4f, 1f)
        )
        var newBitmapArrayy = bitmapArray
        var contrastBitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        bitmapContrast!!.getPixels(contrastBitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)
        var newPixelValueA = 0f
        var newPixelValueR = 0f
        var newPixelValueG = 0f
        var newPixelValueB = 0f
        var pixelColor: Int
        var pixelValueA: Float
        var pixelValueR: Float
        var pixelValueG: Float
        var pixelValueB: Float
        var newPixel: Int
        for(i in 0..1) {
            for (Y in 3 until bitmap!!.height - 3) {
                for (X in 3 until bitmap!!.width - 3) {
                    newPixelValueA = 0f
                    newPixelValueR = 0f
                    newPixelValueG = 0f
                    newPixelValueB = 0f
                    for (YK in -1..3) {
                        for (XK in -1..3) {
                            pixelColor = bitmapArray[(Y + YK) * bitmap!!.width + (X + XK)]
                            pixelValueA = (Color.alpha(pixelColor)).toFloat()
                            pixelValueR = (Color.red(pixelColor)).toFloat()
                            pixelValueG = (Color.green(pixelColor)).toFloat()
                            pixelValueB= (Color.blue(pixelColor)).toFloat()
                            newPixelValueA += kernel[YK + 1][XK + 1] * pixelValueA
                            newPixelValueR += kernel[YK + 1][XK + 1] * pixelValueR
                            newPixelValueG += kernel[YK + 1][XK + 1] * pixelValueG
                            newPixelValueB += kernel[YK + 1][XK + 1] * pixelValueB
                        }
                    }
                    newPixelValueR /= 256
                    newPixelValueG /= 256
                    newPixelValueB /= 256

                    pixelColor = bitmapArray[Y * bitmap!!.width + X]
                    var oldPixelR = (Color.red(pixelColor)).toFloat()
                    var oldPixelG = (Color.green(pixelColor)).toFloat()
                    var oldPixelB = (Color.blue(pixelColor)).toFloat()
                    newPixelValueR = kotlin.math.abs(newPixelValueR - oldPixelR)
                    newPixelValueG = kotlin.math.abs(newPixelValueG - oldPixelG)
                    newPixelValueB = kotlin.math.abs(newPixelValueB - oldPixelB)
                    if((newPixelValueR + newPixelValueG + newPixelValueB) >= 30){
                        var contrastPixelColor = contrastBitmapArray[Y * bitmap!!.width + X]
                        newPixelValueR = Color.red(contrastPixelColor).toFloat()
                        newPixelValueB = Color.blue(contrastPixelColor).toFloat()
                        newPixelValueG = Color.green(contrastPixelColor).toFloat()
                        newPixel = Color.argb(
                            255,
                            (newPixelValueR).toInt(),
                            (newPixelValueG).toInt(),
                            (newPixelValueB).toInt()
                        )
                        newBitmapArrayy[Y * bitmap!!.width + X] = newPixel
                    }
                }
            }
        }
        bitmap = Bitmap.createBitmap(newBitmapArrayy, bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888)
        image_view.setImageBitmap(bitmap)
    }

    private fun contrastFilter(bitmapArray: IntArray){
        var newBitmapArray = bitmapArray
        val coof = 1.6f
        val bee = 10
        for (Y in 0 until bitmap!!.height - 1) {
            for (X in 0 until bitmap!!.width - 1) {
                var pixelColor = bitmapArray[Y * bitmap!!.width + X]
                var pixelA = Color.alpha(pixelColor)
                var pixelRed = Color.red(pixelColor)
                var pixelBlue = Color.blue(pixelColor)
                var pixelGreen = Color.green(pixelColor)
                pixelRed = (coof * (pixelRed - 128) + 128 + bee).toInt()
                pixelGreen = (coof * (pixelGreen - 128) + 128 + bee).toInt()
                pixelBlue = (coof * (pixelBlue - 128) + 128 + bee).toInt()
                if(pixelRed < 0){
                    pixelRed = 0
                }
                if(pixelGreen < 0){
                    pixelGreen = 0
                }
                if(pixelBlue < 0){
                    pixelBlue = 0
                }
                if(pixelRed > 255){
                    pixelRed = 255
                }
                if(pixelGreen > 255){
                    pixelGreen = 255
                }
                if(pixelBlue > 255){
                    pixelBlue = 255
                }
                val newPixel = Color.argb(pixelA, pixelRed, pixelGreen, pixelBlue)
                newBitmapArray[Y * bitmap!!.width + X] = newPixel
            }
        }
        bitmapContrast = Bitmap.createBitmap(newBitmapArray, bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888)
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
