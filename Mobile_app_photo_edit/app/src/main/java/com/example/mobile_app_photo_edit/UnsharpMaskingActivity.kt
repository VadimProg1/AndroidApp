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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import kotlinx.android.synthetic.main.activity_color_filtres.*
import kotlinx.android.synthetic.main.activity_main.image_view
import kotlinx.android.synthetic.main.activity_spline_interpolation.view.*
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
    private var bitmapBlur: Bitmap? = null
    private var bmpCopy: Bitmap? = null
    val sharpenForce = 1f
    var coofContrast = 1.6f
    var coofThreshold = 30f
    var coofBlur = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unsharp_masking)

        image_uri = intent.getParcelableExtra(MainActivity.ACTIVITIES_MESSAGE_KEY)
        image_view.setImageURI(image_uri)
        val drawable = image_view.drawable as BitmapDrawable
        bitmap = drawable.bitmap

        val bitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        val blurBitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        bitmap!!.getPixels(bitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)

        seekBarBlur.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                coofBlur = seekBarBlur.progress
            }
        })

        seekBarContrast.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                coofContrast = seekBarContrast.progress.toFloat() + 1f
            }
        })

        seekBarSomething.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                coofThreshold = seekBarSomething.progress.toFloat() + 10f
            }
        })

        btn_coof.setOnClickListener{
            btn_done.visibility = VISIBLE
            btn_unsharpMasking.visibility = INVISIBLE
            btn_coof.visibility = INVISIBLE
            seekBarBlur.visibility = VISIBLE
            seekBarContrast.visibility = VISIBLE
            seekBarSomething.visibility = VISIBLE
            textBlur.visibility = VISIBLE
            textContrast.visibility = VISIBLE
            textSomething.visibility = VISIBLE
        }
        btn_done.setOnClickListener{
            seekBarBlur.visibility = INVISIBLE
            seekBarContrast.visibility = INVISIBLE
            seekBarSomething.visibility = INVISIBLE
            btn_unsharpMasking.visibility = VISIBLE
            btn_coof.visibility = VISIBLE
            btn_done.visibility = INVISIBLE
            textBlur.visibility = INVISIBLE
            textContrast.visibility = INVISIBLE
            textSomething.visibility = INVISIBLE
        }

        btn_unsharpMasking.setOnClickListener{
            contrastFilter(bitmapArray)
            bitmap!!.getPixels(bitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)
            blur(bitmapArray)
            bitmap!!.getPixels(bitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)
            unsharpMaskingFilter(bitmapArray)

        }
    }

    private fun blur(bitmapArray: IntArray){
        val kernel = arrayOf(
            floatArrayOf(1f, 4f, 6f, 4f, 1f),
            floatArrayOf(4f, 16f, 24f, 16f, 4f),
            floatArrayOf(6f, 24f, 36f, 24f, 6f),
            floatArrayOf(4f, 16f, 24f, 16f, 4f),
            floatArrayOf(1f, 4f, 6f, 4f, 1f)
        )
        var blurBitmapArray = bitmapArray
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
        for(i in 0..coofBlur) {
            for (Y in 3 until bitmap!!.height - 3) {
                for (X in 3 until bitmap!!.width - 3) {
                    newPixelValueA = 0f
                    newPixelValueR = 0f
                    newPixelValueG = 0f
                    newPixelValueB = 0f
                    for (YK in -1..3) {
                        for (XK in -1..3) {
                            pixelColor = blurBitmapArray[(Y + YK) * bitmap!!.width + (X + XK)]
                            pixelValueA = (Color.alpha(pixelColor)).toFloat()
                            pixelValueR = (Color.red(pixelColor)).toFloat()
                            pixelValueG = (Color.green(pixelColor)).toFloat()
                            pixelValueB = (Color.blue(pixelColor)).toFloat()
                            newPixelValueA += kernel[YK + 1][XK + 1] * pixelValueA
                            newPixelValueR += kernel[YK + 1][XK + 1] * pixelValueR
                            newPixelValueG += kernel[YK + 1][XK + 1] * pixelValueG
                            newPixelValueB += kernel[YK + 1][XK + 1] * pixelValueB
                        }
                    }
                    newPixelValueR /= 256
                    newPixelValueG /= 256
                    newPixelValueB /= 256
                    newPixel = Color.argb(
                        255,
                        (newPixelValueR).toInt(),
                        (newPixelValueG).toInt(),
                        (newPixelValueB).toInt()
                    )
                    blurBitmapArray[Y * bitmap!!.width + X] = newPixel
                }
            }
        }
        bitmapBlur = Bitmap.createBitmap(blurBitmapArray, bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888)
    }

    private fun unsharpMaskingFilter(bitmapArray: IntArray) {

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

        var blurBitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        bitmapBlur!!.getPixels(blurBitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)
        var contrastBitmapArray = IntArray(bitmap!!.height * bitmap!!.width)
        bitmapContrast!!.getPixels(contrastBitmapArray, 0, bitmap!!.width, 0, 0, bitmap!!.width, bitmap!!.height)
        var newBitmapArrayy = bitmapArray
        coofThreshold = 70 - coofThreshold

        var oldPixelR = 0f
        var oldPixelG = 0f
        var oldPixelB = 0f
        var contrastPixelColor = 0
        for(Y in 3 until bitmap!!.height - 3){
            for(X in 3 until bitmap!!.width - 3){
                pixelColor = bitmapArray[Y * bitmap!!.width + X]
                oldPixelR = (Color.red(pixelColor)).toFloat()
                oldPixelG = (Color.green(pixelColor)).toFloat()
                oldPixelB = (Color.blue(pixelColor)).toFloat()
                pixelColor = blurBitmapArray[Y * bitmap!!.width + X]
                newPixelValueR = (Color.red(pixelColor)).toFloat()
                newPixelValueG = (Color.green(pixelColor)).toFloat()
                newPixelValueB = (Color.blue(pixelColor)).toFloat()
                newPixelValueR = kotlin.math.abs(newPixelValueR - oldPixelR)
                newPixelValueG = kotlin.math.abs(newPixelValueG - oldPixelG)
                newPixelValueB = kotlin.math.abs(newPixelValueB - oldPixelB)
                if((newPixelValueR + newPixelValueG + newPixelValueB) >= coofThreshold){
                    contrastPixelColor = contrastBitmapArray[Y * bitmap!!.width + X]
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

        bitmap = Bitmap.createBitmap(newBitmapArrayy, bitmap!!.width, bitmap!!.height, Bitmap.Config.ARGB_8888)
        image_view.setImageBitmap(bitmap)
    }



    private fun contrastFilter(bitmapArray: IntArray){
        var newBitmapArray = bitmapArray
        val lightUp = 10
        var pixelColor = 0
        var pixelA = 0
        var pixelRed = 0
        var pixelBlue = 0
        var pixelGreen = 0
        for (Y in 0 until bitmap!!.height - 1) {
            for (X in 0 until bitmap!!.width - 1) {
                pixelColor = bitmapArray[Y * bitmap!!.width + X]
                pixelA = Color.alpha(pixelColor)
                pixelRed = Color.red(pixelColor)
                pixelBlue = Color.blue(pixelColor)
                pixelGreen = Color.green(pixelColor)
                pixelRed = (coofContrast * (pixelRed - 128) + 128 + lightUp).toInt()
                pixelGreen = (coofContrast * (pixelGreen - 128) + 128 + lightUp).toInt()
                pixelBlue = (coofContrast * (pixelBlue - 128) + 128 + lightUp).toInt()
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
