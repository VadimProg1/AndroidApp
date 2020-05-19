package com.example.mobile_app_photo_edit

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_affine_transformations.*
import kotlinx.android.synthetic.main.activity_affine_transformations.image_view
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

class AffineTransformationsActivity : AppCompatActivity() {

    var image_uri: Uri? = null
    var bitmap: Bitmap? = null
    var bmp_Copy: Bitmap? = null
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    private var bitmapTouchEventX = 0f
    private var bitmapTouchEventY = 0f
    private var counterOfClicks = 0
    var dotsFirst: MutableList<dot> = ArrayList()
    var dotsSecond: MutableList<dot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_affine_transformations)

        image_uri = intent.getParcelableExtra(MainActivity.MY_MESSAGE_KEY)
        image_view.setImageURI(image_uri)
        val drawable = image_view.drawable as BitmapDrawable
        bitmap = drawable.bitmap
        var bmp_Copy = Bitmap.createBitmap(
            bitmap!!.width, bitmap!!.height,
            Bitmap.Config.ARGB_8888
        )

        if(counterOfClicks == 0){
            Toast.makeText(this, "Set first 3 dots", Toast.LENGTH_SHORT).show()
        }

        canvasAffine.setOnTouchListener(View.OnTouchListener { v, event ->
            val action = event.action
            motionTouchEventX = event.x
            motionTouchEventY = event.y
            motionTouchEventY *= image_view.height.toFloat() / canvasAffine.mHeight.toFloat()
            motionTouchEventX *= image_view.width.toFloat() / canvasAffine.mWidth.toFloat()

            bitmapTouchEventX = event.x
            bitmapTouchEventY = event.y
            bitmapTouchEventY *= image_view.height.toFloat() / bitmap!!.height.toFloat()
            bitmapTouchEventX *= image_view.width.toFloat() / bitmap!!.width.toFloat()
            when (action) {
                MotionEvent.ACTION_DOWN -> {

                }
                MotionEvent.ACTION_MOVE -> {

                }
                MotionEvent.ACTION_UP -> {
                    counterOfClicks++
                    if(counterOfClicks == 3){
                        Toast.makeText(this, "Set last 3 dots", Toast.LENGTH_SHORT).show()
                    }
                    if(counterOfClicks == 6){
                        Toast.makeText(this, "Click magic button!", Toast.LENGTH_SHORT).show()
                    }
                    if(counterOfClicks <= 6){
                        canvasAffine.drawLine(motionTouchEventX, motionTouchEventY)
                        if(counterOfClicks <= 3) {
                            dotsFirst.add(dot(bitmapTouchEventX, bitmapTouchEventY))
                        }
                        else if(counterOfClicks <= 6){
                            dotsSecond.add(dot(bitmapTouchEventX, bitmapTouchEventY))
                        }
                    }
                    if(counterOfClicks >= 7){
                        Toast.makeText(this, "Click magic button!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            true
        })

        btn_magic.setOnClickListener{
            if(counterOfClicks >= 6) {
                canvasAffine.drawLine(0f, 0f)
                //shiftVol2()
                rotate()
                //shift()
                //scale()
                counterOfClicks = 0
            }
        }

        btn_save.setOnClickListener{
            image_uri = bitmapToFile(bitmap!!)
            onBackPressed()
        }
    }

    private fun shiftVol2(){
        var centerFirstX = 0f; var centerSecondX = 0f; var centerFirstY = 0f; var centerSecondY = 0f
        var skewX = 0f; var skewY = 0f
        centerFirstX = (dotsFirst[0].x + dotsFirst[1].x + dotsFirst[2].x) / 3
        centerFirstY = (dotsFirst[0].y + dotsFirst[1].y + dotsFirst[2].y) / 3
        centerSecondX = (dotsSecond[0].x + dotsSecond[1].x + dotsSecond[2].x) / 3
        centerSecondY = (dotsSecond[0].y + dotsSecond[1].y + dotsSecond[2].y) / 3
        skewX = (centerFirstX - centerSecondX) / abs(centerFirstX + centerSecondX)
        skewY = (centerFirstY - centerSecondY) / abs(centerFirstY + centerSecondY)
        var matrix = Matrix()
        matrix.postSkew(skewX, skewY)
        bitmap = Bitmap.createBitmap(
            bitmap!!,
            0,
            0,
            bitmap!!.width,
            bitmap!!.height,
            matrix,
            false
        )
        image_view.setImageBitmap(bitmap)

    }

    private fun shift(){
        var shiftX: Int = 0
        var shiftY: Int = 0
        var pixelColor: Int
        var pixelAlpha: Int
        var pixelRed: Int
        var pixelBlue: Int
        var pixelGreen: Int
        var newPixel :Int
        for(i in 0..2){
            shiftX += (dotsSecond[i].x - dotsFirst[i].x).toInt()
            shiftY += (dotsSecond[i].y - dotsFirst[i].y).toInt()
        }
        shiftX/= 3
        shiftY/= 3

        bmp_Copy = Bitmap.createBitmap(
            bitmap!!.width - abs(shiftX) , bitmap!!.height - abs(shiftY),
            Bitmap.Config.ARGB_8888
        )

        var xNew = 0
        var yNew = 0

        for(y in 0 until bitmap!!.height){
            for(x in 0 until bitmap!!.width){
                pixelColor = bitmap!!.getPixel(x, y)
                pixelAlpha = Color.alpha(pixelColor)
                pixelRed = Color.red(pixelColor)
                pixelBlue = Color.blue(pixelColor)
                pixelGreen = Color.green(pixelColor)
                if(x + shiftX >= 0 && x + shiftX < bitmap!!.width && y + shiftY >= 0 && y + shiftY < bitmap!!.height){
                    newPixel = Color.argb(pixelAlpha, pixelRed, pixelGreen, pixelBlue)
                    bmp_Copy!!.setPixel(xNew, yNew, newPixel)
                    xNew++
                }
                if(xNew >= bmp_Copy!!.width){
                    xNew = 0
                    yNew++
                }
            }
        }
        image_view.setImageBitmap(bmp_Copy)
        bitmap = bmp_Copy
    }

    private fun scale(){
        var lenghtFirst = 0f
        var lenghtSecond = 0f
        for(i in 0..1){
            lenghtFirst += sqrt((dotsFirst[i].x - dotsFirst[i + 1].x) * (dotsFirst[i].x - dotsFirst[i + 1].x) +
                    (dotsFirst[i].y - dotsFirst[i + 1].y) * (dotsFirst[i].y - dotsFirst[i + 1].y))
            lenghtSecond += sqrt((dotsSecond[i].x - dotsSecond[i + 1].x) * (dotsSecond[i].x - dotsSecond[i + 1].x) +
                    (dotsSecond[i].y - dotsSecond[i + 1].y) * (dotsSecond[i].y - dotsSecond[i + 1].y))
        }
        lenghtFirst += sqrt((dotsFirst[0].x - dotsFirst[2].x) * (dotsFirst[0].x - dotsFirst[2].x) +
                (dotsFirst[0].y - dotsFirst[2].y) * (dotsFirst[0].y - dotsFirst[2].y))
        lenghtSecond += sqrt((dotsSecond[0].x - dotsSecond[2].x) * (dotsSecond[0].x - dotsSecond[2].x) +
                (dotsSecond[0].y - dotsSecond[2].y) * (dotsSecond[0].y - dotsSecond[2].y))
        var coof = lenghtSecond / lenghtFirst
        if(coof > 1.9){
            coof = 1.9f
        }

        if(coof > 1){
            bilinearFiltration(coof)
        }
        image_view.setImageBitmap(bmp_Copy)
        bitmap = bmp_Copy
    }

    private fun bilinearFiltration(coof: Float){
        bmp_Copy = Bitmap.createBitmap((bitmap!!.width * coof).toInt(),
            (bitmap!!.height * coof).toInt(), Bitmap.Config.ARGB_8888)

        var c00: Int
        var c10: Int
        var c01: Int
        var c11: Int

        var c00r: Int; var c10r: Int; var c01r: Int; var c11r: Int
        var c00g: Int; var c10g: Int; var c01g: Int; var c11g: Int
        var c00b: Int;  var c10b: Int; var c01b: Int;  var c11b: Int

        var newPixelColorR: Int
        var newPixelColorG: Int
        var newPixelColorB: Int

        var tx: Float
        var ty:Float
        for(y in 1 until bmp_Copy!!.height - 1){
            for(x in 1 until bmp_Copy!!.width - 1){
                var gx = x / bmp_Copy!!.width.toFloat() * bitmap!!.width.toFloat()
                var gy = y / bmp_Copy!!.height.toFloat() * bitmap!!.height.toFloat()
                var gxi = gx.toInt()
                var gyi = gy.toInt()
                c00 = bitmap!!.getPixel(gxi, gyi)
                c10 = bitmap!!.getPixel(gxi + 1, gyi)
                c01 = bitmap!!.getPixel(gxi, gyi + 1)
                c11 = bitmap!!.getPixel(gxi + 1, gyi + 1)
                tx = gx - gxi
                ty = gy - gyi

                c00r = Color.red(c00)
                c01r = Color.red(c01)
                c10r = Color.red(c10)
                c11r = Color.red(c11)

                c00g = Color.green(c00)
                c01g = Color.green(c01)
                c10g = Color.green(c10)
                c11g = Color.green(c11)

                c00b = Color.blue(c00)
                c01b = Color.blue(c01)
                c10b = Color.blue(c10)
                c11b = Color.blue(c11)

                newPixelColorR = bilinearFiltrationWasMistake(c00r, c01r, c10r, c11r, tx, ty)
                newPixelColorG = bilinearFiltrationWasMistake(c00g, c01g, c10g, c11g, tx, ty)
                newPixelColorB = bilinearFiltrationWasMistake(c00b, c01b, c10b, c11b, tx, ty)

                val newPixel = Color.argb(
                    255,
                    (newPixelColorR),
                    (newPixelColorG),
                    (newPixelColorB)
                )

                bmp_Copy!!.setPixel(x, y, newPixel)
            }
        }
        image_view.setImageBitmap(bmp_Copy)
        bitmap = bmp_Copy
    }

    private fun bilinearFiltrationWasMistake(c00: Int, c01: Int, c10: Int, c11: Int, tx: Float, ty:Float): Int{
        var a = c00 * (1 - tx) + c10 * tx
        var b = c01 * (1 - tx) + c11 * tx
        return (a * (1 - ty) + b * ty).toInt()
    }

    private fun rotate(){
        var a = 0f; var b = 0f; var c = 0f
        a = sqrt((dotsFirst[0].x - dotsFirst[2].x) * (dotsFirst[0].x - dotsFirst[2].x) +
                (dotsFirst[0].y - dotsFirst[2].y) * (dotsFirst[0].y - dotsFirst[2].y))
        b = sqrt((dotsSecond[0].x - dotsSecond[2].x) * (dotsSecond[0].x - dotsSecond[2].x) +
                (dotsSecond[0].y - dotsSecond[2].y) * (dotsSecond[0].y - dotsSecond[2].y))
        c = sqrt((dotsFirst[2].x - dotsSecond[2].x) * (dotsFirst[2].x - dotsSecond[2].x) +
                (dotsFirst[2].y - dotsSecond[2].y) * (dotsSecond[2].y - dotsSecond[2].y))
        var check = ((a * a + b * b - c * c) / (2 * a * b)).toDouble()
        var rotate: Double
        if(check > 1){
            rotate = 0.0
        }
        else{
            rotate = Math.acos(check)
        }
        rotate = Math.toDegrees(rotate)
        if(dotsSecond[2].y < dotsFirst[2].y){
            rotate = 360 - rotate
        }
        var matrix = Matrix()
        matrix.postRotate(rotate.toFloat())
        bitmap = Bitmap.createBitmap(
            bitmap!!,
            0,
            0,
            bitmap!!.width,
            bitmap!!.height,
            matrix,
            true
        )
        image_view.setImageBitmap(bitmap!!)
    }

    private fun bitmapToFile(bitmap: Bitmap): Uri {
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

class dot(var x: Float, var y: Float)
