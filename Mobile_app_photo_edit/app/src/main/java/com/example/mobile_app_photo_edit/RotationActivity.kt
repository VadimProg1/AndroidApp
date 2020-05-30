package com.example.mobile_app_photo_edit

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.scale
import kotlinx.android.synthetic.main.activity_main.image_view
import kotlinx.android.synthetic.main.activity_rotation.*
import java.io.*
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt

class RotationActivity : AppCompatActivity() {

    var bitmap: Bitmap? = null
    var rotate: Float = 0f
    var image_uri: Uri? = null
    var tempRotate: Float = 0f
    var scaleCoof: Float = 1f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rotation)

        image_uri = intent.getParcelableExtra(MainActivity.ACTIVITIES_MESSAGE_KEY)
        image_view.setImageURI(image_uri)
        val drawable = image_view.drawable as BitmapDrawable
        bitmap = drawable.bitmap

        var diag: Float = sqrt((bitmap!!.height * bitmap!!.height + bitmap!!.width * bitmap!!.width).toFloat())
        var coof = (diag / (bitmap!!.height + bitmap!!.width)) / 34

        seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                image_view.animate().rotation((progress.toFloat() - 45 + rotate))
                image_view.animate().scaleX(abs(progress.toFloat() - 45) * coof + 1)
                image_view.animate().scaleY(abs(progress.toFloat() - 45) * coof + 1)
                textView.text = (progress - 45).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    tempRotate = (seekBar.progress.toFloat() - 45)
                    scaleCoof = (abs(seekBar.progress.toFloat() - 45)) * coof + 1
                }
            }

        })
        btn_rotate.setOnClickListener{
            var matrix = Matrix()
            matrix.postRotate(90f)
            bitmap = Bitmap.createBitmap(
                bitmap!!,
                0,
                0,
                bitmap!!.width,
                bitmap!!.height,
                matrix,
                true
            )
            tempRotate = 0f
            seekBar.progress = 45
            image_view.setImageBitmap(bitmap)
            image_view.animate().scaleX(1f)
            image_view.animate().scaleY(1f)
            scaleCoof = 1f
        }
    }

    private fun scaling(bmp_Copy: Bitmap, oldHeight: Int, oldWidth: Int): Bitmap{
        var newHeight: Int = (oldHeight / scaleCoof).roundToInt()
        var newWidth: Int = (oldWidth / scaleCoof).roundToInt()
        var bitmapArray = IntArray(bmp_Copy.height * bmp_Copy.width)
        var newBitmapArray: MutableList<Int> = ArrayList()
        bmp_Copy.getPixels(bitmapArray, 0, bmp_Copy.width, 0, 0, bmp_Copy.width, bmp_Copy.height)
        if(scaleCoof > 1){
            var indentX = (bmp_Copy.width - newWidth) / 2
            var indentY = (bmp_Copy.height - newHeight) / 2
            newHeight = bmp_Copy.height - (indentY * 2)
            newWidth = bmp_Copy.width - (indentX * 2)
            var indexCounter = 0
            for(Y in indentY..(bmp_Copy.height - indentY) - 1){
                for(X in indentX..(bmp_Copy.width - indentX) - 1){
                    newBitmapArray.add(bitmapArray[Y * bmp_Copy.width + X])
                    indexCounter++
                }
            }
        }
        return Bitmap.createBitmap(newBitmapArray.toIntArray(),newWidth, newHeight, Bitmap.Config.ARGB_8888)
    }

    private fun saveImage(){
        rotate+= tempRotate
        var matrix = Matrix()
        var oldHeight = bitmap!!.height
        var oldWidth = bitmap!!.width
        matrix.postRotate(rotate)
        bitmap = Bitmap.createBitmap(
            bitmap!!,
            0,
            0,
            bitmap!!.width,
            bitmap!!.height,
            matrix,
            true
        )
        if(rotate != 0f){
            var bmp_Copy = bitmap
            bitmap = scaling(bmp_Copy!!, oldHeight, oldWidth)
        }
        image_uri = bitmapToFile(bitmap!!)
        rotate = 0f
        scaleCoof = 1f
        tempRotate = 0f
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
