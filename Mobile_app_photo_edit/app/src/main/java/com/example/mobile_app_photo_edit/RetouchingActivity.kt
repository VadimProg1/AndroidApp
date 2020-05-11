package com.example.mobile_app_photo_edit

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scale
import kotlinx.android.synthetic.main.activity_color_filtres.*
import kotlinx.android.synthetic.main.activity_color_filtres.btn_save
import kotlinx.android.synthetic.main.activity_main.image_view
import kotlinx.android.synthetic.main.activity_retouching.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.math.roundToInt


class RetouchingActivity : AppCompatActivity() {

    var image_uri: Uri? = null
    var bitmap: Bitmap? = null
    var bmp_Copy: Bitmap? = null
    private var motionTouchEventX = 0
    private var motionTouchEventY = 0
    private var currentX = 0f
    private var currentY = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

        image_uri = intent.getParcelableExtra(MainActivity.MY_MESSAGE_KEY)
        image_view.setImageURI(image_uri)
        val drawable = image_view.drawable as BitmapDrawable
        bitmap = drawable.bitmap

        resizeBitmap()
       // bitmap = Bitmap.createScaledBitmap(bitmap!!,  1800,
       //    1200, true);
        bmp_Copy = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        image_view.layoutParams.height = bitmap!!.height
        image_view.layoutParams.width = bitmap!!.width
        //bitmap = Bitmap.createScaledBitmap(bitmap!!, image_view.layoutParams.width, image_view.layoutParams.height, true)
        btn_save.setOnClickListener{
            image_uri = bitmapToFile(bitmap!!)
            onBackPressed()
        }

        image_view.setOnTouchListener(OnTouchListener { v, event ->
            val action = event.action
            motionTouchEventX  = event.x.toInt()
            motionTouchEventY = event.y.toInt()
            motionTouchEventY *= bitmap!!.height / image_view.height
            motionTouchEventX *= bitmap!!.width / image_view.width
            when (action) {
                MotionEvent.ACTION_DOWN -> {

                }
                MotionEvent.ACTION_MOVE -> {
                    if(motionTouchEventX >= 10 && motionTouchEventX <= bitmap!!.width - 10
                        && motionTouchEventY >= 10 && motionTouchEventY <= bitmap!!.height - 10) {
                        retouching()
                    }
                }
                MotionEvent.ACTION_UP -> {

                }
            }

            true
        })
    }

    private fun resizeBitmap()
    {
        var width: Float = (bitmap!!.width).toFloat()
        var height: Float = (bitmap!!.height).toFloat()
        var scale: Float = 1f
        var coof:Float = (width / height).toFloat()
        var reverseCoof:Float = (height / width).toFloat()
        if(bitmap!!.height > bitmap!!.width){
            height = bitmap!!.height * scale
            width = bitmap!!.width * scale
        }
        else{
            height = bitmap!!.height * scale
            width = bitmap!!.width * scale
        }
        bitmap = Bitmap.createScaledBitmap(bitmap!!,  width.toInt(),
            height.toInt(), false)
    }
    private fun retouching(){

        var yUP = (motionTouchEventY + 10).toInt()
        var yDOWN = (motionTouchEventY - 10).toInt()
        var xLeft = (motionTouchEventX - 10).toInt()
        var xRight = (motionTouchEventX + 10).toInt()
        var pixelColor = 0
        var pixelAlpha = 0
        var pixelRed = 0
        var pixelBlue = 0
        var pixelGreen = 0
        var counter = 0
        for(i in yDOWN.. yUP - 1){
            for(j in xLeft..xRight - 1){
                 pixelColor = bitmap!!.getPixel(j, i)
                 pixelRed += Color.red(pixelColor)
                 pixelBlue += Color.blue(pixelColor)
                 pixelGreen += Color.green(pixelColor)
                counter++
            }
        }

        var pixelRedAverage = pixelRed / counter
        var pixelBlueAverage = pixelBlue / counter
        var pixelGreenAverage = pixelGreen / counter

        yUP = (motionTouchEventY + 10).toInt()
        yDOWN = (motionTouchEventY - 10).toInt()
        xLeft = (motionTouchEventX - 10).toInt()
        xRight = (motionTouchEventX + 10).toInt()


        for (i in yDOWN..yUP - 1) {
            for (j in xLeft..xRight - 1) {
                pixelColor = bitmap!!.getPixel(j, i)
                pixelAlpha = Color.alpha(pixelColor)
                pixelRed = Color.red(pixelColor)
                pixelBlue = Color.blue(pixelColor)
                pixelGreen = Color.green(pixelColor)
                pixelRed += ((pixelRedAverage - pixelRed) * 0.18).toInt()
                pixelBlue += ((pixelBlueAverage - pixelBlue) * 0.18).toInt()
                pixelGreen += ((pixelGreenAverage - pixelGreen) * 0.18).toInt()

                val newPixel = Color.argb(pixelAlpha, pixelRed, pixelGreen, pixelBlue)
                bmp_Copy!!.setPixel(j, i, newPixel)
            }
        }

        bitmap = bmp_Copy
        image_view.setImageBitmap(bmp_Copy)
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
