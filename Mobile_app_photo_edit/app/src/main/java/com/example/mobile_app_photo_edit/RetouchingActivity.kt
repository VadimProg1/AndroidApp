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
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scale
import kotlinx.android.synthetic.main.activity_color_filtres.*
import kotlinx.android.synthetic.main.activity_main.image_view
import kotlinx.android.synthetic.main.activity_retouching.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.sqrt


class RetouchingActivity : AppCompatActivity() {

    var image_uri: Uri? = null
    var bitmap: Bitmap? = null
    var bmp_Copy: Bitmap? = null
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

        image_uri = intent.getParcelableExtra(MainActivity.ACTIVITIES_MESSAGE_KEY)
        image_view.setImageURI(image_uri)
        val drawable = image_view.drawable as BitmapDrawable
        bitmap = drawable.bitmap
        bmp_Copy = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)
        /*
        btn_save.setOnClickListener{
            image_uri = bitmapToFile(bitmap!!)
            onBackPressed()
        }

         */

        image_view.setOnTouchListener(OnTouchListener { v, event ->
            val action = event.action
            motionTouchEventX  = event.x
            motionTouchEventY = event.y
            motionTouchEventY *= bitmap!!.height.toFloat() / image_view.height.toFloat()
            motionTouchEventX *= bitmap!!.width.toFloat() / image_view.width.toFloat()
            when (action) {
                MotionEvent.ACTION_DOWN -> {}
                MotionEvent.ACTION_MOVE -> {
                    if (motionTouchEventX >= 31 && motionTouchEventX <= bitmap!!.width - 31
                        && motionTouchEventY >= 31 && motionTouchEventY <= bitmap!!.height - 31
                    ) {
                        retouching()
                    }
                }
                MotionEvent.ACTION_UP -> {}
            }
            true
        })
    }


    private fun retouching(){

        var yUP = (motionTouchEventY + 31).toInt()
        var yDOWN = (motionTouchEventY - 31).toInt()
        var xLeft = (motionTouchEventX - 31).toInt()
        var xRight = (motionTouchEventX + 31).toInt()
        var xCenter = motionTouchEventX
        var yCenter = motionTouchEventY

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
        var dist: Float
        var coof = 0.26f
        var localCoof = coof
        for (i in yDOWN..yUP - 1) {
            for (j in xLeft..xRight - 1) {
                localCoof = coof
                dist = sqrt((xCenter - j) * (xCenter - j) + (yCenter - i) * (yCenter - i))
                if(localCoof - (dist * 0.007f) > 0) {
                    localCoof -= dist * 0.007f
                }
                else
                    localCoof = 0f
                pixelColor = bitmap!!.getPixel(j, i)
                pixelAlpha = Color.alpha(pixelColor)
                pixelRed = Color.red(pixelColor)
                pixelBlue = Color.blue(pixelColor)
                pixelGreen = Color.green(pixelColor)
                pixelRed += ((pixelRedAverage - pixelRed) * localCoof).toInt()
                pixelBlue += ((pixelBlueAverage - pixelBlue) * localCoof).toInt()
                pixelGreen += ((pixelGreenAverage - pixelGreen) * localCoof).toInt()
                val newPixel = Color.argb(pixelAlpha, pixelRed, pixelGreen, pixelBlue)
                bmp_Copy!!.setPixel(j, i, newPixel)
            }
        }

        bitmap = bmp_Copy
        image_view.setImageBitmap(bmp_Copy)
    }

    private fun saveImage(){
        image_uri = bitmapToFile(bitmap!!)
        onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save_activity_result, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemview = item.itemId
        when (itemview) {
            R.id.btn_image_save_activity_result -> saveImage()
        }
        return false
    }

    private fun bitmapToFile(bitmap:Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)

        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

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
