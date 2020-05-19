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
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_color_filtres.*
import kotlinx.android.synthetic.main.activity_main.image_view
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.*
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList as ArrayList1

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
            progressBar.visibility = VISIBLE
            ProgressBarListener("baw")
           // BlackWhite()
        }

        btn_filter1.setOnClickListener{
            progressBar.visibility = VISIBLE
            ProgressBarListener("emboss")
           // EmbossFilter()
        }
        btn_filter2.setOnClickListener{
            progressBar.visibility = VISIBLE
            ProgressBarListener("sharpen")
            //SharpenFilter()
        }

        btn_filter3.setOnClickListener{
            progressBar.visibility = VISIBLE
            ProgressBarListener("blur")
        }

        btn_filter4.setOnClickListener{
            progressBar.visibility = VISIBLE
            ProgressBarListener("contrast")
            //ContrastFilter()
        }
        btn_save.setOnClickListener{
           image_uri = bitmapToFile(bitmap!!)
            onBackPressed()
        }

    }

    private fun ProgressBarListener(code: String){
        val future = doAsync {
            if(code == "blur") {
                BlurFilter()
            }
            else if(code == "contrast"){
                ContrastFilter()
            }
            else if(code == "sharpen"){
                SharpenFilter()
            }
            else if(code == "emboss"){
                EmbossFilter()
            }
            else if(code == "baw"){
                BlackWhite()
            }
            uiThread {
                // use result here if you want to update ui
                progressBar.visibility = INVISIBLE
            }
        }
    }

    private fun ContrastFilter(){

        var bitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        bitmap!!.getPixels(bitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)

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
                bitmapArray[Y * bitmap!!.width + X] = newPixel
            }
        }
        bitmap = Bitmap.createBitmap(bitmapArray, bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888)
        image_view.setImageBitmap(bitmap!!)
    }

    private fun BlackWhite(){
        var bitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        bitmap!!.getPixels(bitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)

        for(Y in 0 until bitmap!!.height - 1){
            for(X in 1 until bitmap!!.width - 1){
                var pixelColor = bitmapArray[Y * bitmap!!.width + X]
                var pixelAlpha = Color.alpha(pixelColor)
                var pixelRed = Color.red(pixelColor)
                var pixelBlue = Color.blue(pixelColor)
                var pixelGreen = Color.green(pixelColor)
                var pixelAverage = (pixelRed + pixelBlue + pixelGreen) / 3
                val newPixel = Color.argb(pixelAlpha, pixelAverage, pixelAverage, pixelAverage)
                bitmapArray[Y * bitmap!!.width + X] = newPixel
            }
        }
        bitmap = Bitmap.createBitmap(bitmapArray, bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888)
        image_view.setImageBitmap(bitmap!!)
    }

    private fun EmbossFilter() {
        var bitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        var newBitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        bitmap!!.getPixels(bitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)
        bitmap!!.getPixels(newBitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)

        val kernel = arrayOf(
            intArrayOf(-2, -1, 0),
            intArrayOf(-1, 1, 1),
            intArrayOf(0, 1, 2)
        )
        for(i in 0..1) {
            for (Y in 1 until bitmap!!.height - 1) {
                for (X in 1 until bitmap!!.width - 1) {
                    var newPixelValueR = 0
                    var newPixelValueG = 0
                    var newPixelValueB = 0
                    for (YK in -1..1) {
                        for (XK in -1..1) {
                            var pixelColor = bitmapArray[(Y + YK) * bitmap!!.width + (X + XK)]
                            val pixelValueR = (Color.red(pixelColor))
                            val pixelValueG= (Color.green(pixelColor))
                            val pixelValueB= (Color.blue(pixelColor))
                            newPixelValueR += kernel[YK + 1][XK + 1] * pixelValueR
                            newPixelValueG += kernel[YK + 1][XK + 1] * pixelValueG
                            newPixelValueB += kernel[YK + 1][XK + 1] * pixelValueB
                        }
                    }

                    if(newPixelValueR < 0){
                        newPixelValueR = 0
                    }
                    if(newPixelValueG < 0){
                        newPixelValueG = 0
                    }
                    if(newPixelValueB < 0){
                        newPixelValueB = 0
                    }
                    if(newPixelValueR > 255){
                        newPixelValueR = 255
                    }
                    if(newPixelValueG > 255){
                        newPixelValueG = 255
                    }
                    if(newPixelValueB > 255){
                        newPixelValueB = 255
                    }

                    var pixelColor = bitmapArray[Y * bitmap!!.width + X]
                    val pixelValueA = Color.alpha(pixelColor)
                    val newPixel = Color.argb(
                        pixelValueA,
                        (newPixelValueR),
                        (newPixelValueG),
                        (newPixelValueB)
                    )
                    newBitmapArray[Y * bitmap!!.width + X] = newPixel
                }
            }
        }
        bitmap = Bitmap.createBitmap(newBitmapArray, bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888)
        image_view.setImageBitmap(bitmap!!)
    }

    private fun SharpenFilter() {
        val kernel = arrayOf(
            intArrayOf(-1, -1, -1, -1, -1),
            intArrayOf(-1,  2,  2,  2, -1),
            intArrayOf(-1,  2,  8,  2, -1),
            intArrayOf(-1,  2,  2,  2, -1),
            intArrayOf(-1, -1, -1, -1, -1)
        )

        var bitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        var newBitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        bitmap!!.getPixels(bitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)
        bitmap!!.getPixels(newBitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)

        for(i in 0..1) {
            for (Y in 2 until bitmap!!.height - 3) {
                for (X in 2 until bitmap!!.width - 3) {
                    var newPixelValueR = 0
                    var newPixelValueG = 0
                    var newPixelValueB = 0
                    for (YK in -1..3) {
                        for (XK in -1..3) {
                            var pixelColor = bitmapArray[(Y + YK) * bitmap!!.width + (X + XK)]
                            val pixelValueR = (Color.red(pixelColor))
                            val pixelValueG= (Color.green(pixelColor))
                            val pixelValueB= (Color.blue(pixelColor))
                            newPixelValueR += kernel[YK + 1][XK + 1] * pixelValueR
                            newPixelValueG += kernel[YK + 1][XK + 1] * pixelValueG
                            newPixelValueB += kernel[YK + 1][XK + 1] * pixelValueB
                        }
                    }

                    newPixelValueR /= 8
                    newPixelValueG /= 8
                    newPixelValueB /= 8

                    if(newPixelValueR < 0){
                        newPixelValueR = 0
                    }
                    if(newPixelValueG < 0){
                        newPixelValueG = 0
                    }
                    if(newPixelValueB < 0){
                        newPixelValueB = 0
                    }
                    if(newPixelValueR > 255){
                        newPixelValueR = 255
                    }
                    if(newPixelValueG > 255){
                        newPixelValueG = 255
                    }
                    if(newPixelValueB > 255){
                        newPixelValueB = 255
                    }

                    var pixelColor = bitmapArray[Y * bitmap!!.width + X]
                    val pixelValueA = Color.alpha(pixelColor)
                    val newPixel = Color.argb(
                        pixelValueA,
                        (newPixelValueR),
                        (newPixelValueG),
                        (newPixelValueB)
                    )
                    newBitmapArray[Y * bitmap!!.width + X] = newPixel
                }
            }
        }
        bitmap = Bitmap.createBitmap(newBitmapArray, bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888)
        image_view.setImageBitmap(bitmap!!)
    }

    private fun BlurFilter(){
        val kernel = arrayOf(
            floatArrayOf(1f, 4f, 6f, 4f, 1f),
            floatArrayOf(4f, 16f, 24f, 16f, 4f),
            floatArrayOf(6f, 24f, 36f, 24f, 6f),
            floatArrayOf(4f, 16f, 24f, 16f, 4f),
            floatArrayOf(1f, 4f, 6f, 4f, 1f)
        )
        var newPixelValueR: Float
        var newPixelValueG: Float
        var newPixelValueB: Float
        var pixelColor : Int
        var pixelValueR: Float
        var pixelValueG: Float
        var pixelValueB: Float

        var bitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        var newBitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        bitmap!!.getPixels(bitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)
        bitmap!!.getPixels(newBitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)

        for(i in 0..1) {
            for (Y in 2 until bitmap!!.height - 3) {
                for (X in 2 until bitmap!!.width - 3) {
                    newPixelValueR = 0f
                    newPixelValueG = 0f
                    newPixelValueB = 0f
                    for (YK in -1..3) {
                        for (XK in -1..3) {
                            pixelColor = bitmapArray[(Y + YK) * bitmap!!.width + (X + XK)]
                            pixelValueR = (Color.red(pixelColor)).toFloat()
                            pixelValueG = (Color.green(pixelColor)).toFloat()
                            pixelValueB = (Color.blue(pixelColor)).toFloat()
                            newPixelValueR += kernel[YK + 1][XK + 1] * pixelValueR
                            newPixelValueG += kernel[YK + 1][XK + 1] * pixelValueG
                            newPixelValueB += kernel[YK + 1][XK + 1] * pixelValueB
                        }
                    }
                    pixelColor = bitmapArray[Y * bitmap!!.width + X]
                    var pixelA = Color.alpha(pixelColor)
                    newPixelValueR /= 256
                    newPixelValueG /= 256
                    newPixelValueB /= 256
                    val newPixel = Color.argb(
                        pixelA,
                        (newPixelValueR).toInt(),
                        (newPixelValueG).toInt(),
                        (newPixelValueB).toInt()
                    )
                    newBitmapArray[Y * bitmap!!.width + X] = newPixel
                }
            }
        }
        bitmap = Bitmap.createBitmap(newBitmapArray, bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888)
        image_view.setImageBitmap(bitmap!!)
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

