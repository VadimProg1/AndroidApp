package com.example.mobile_app_photo_edit

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_color_filtres.image_view
import kotlinx.android.synthetic.main.activity_rotation.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class ScalingActivity : AppCompatActivity() {

    var image_uri: Uri? = null
    var bitmap: Bitmap? = null
    var bmp_Copy: Bitmap? = null
    var scaleCoof = 1f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        image_uri = intent.getParcelableExtra(MainActivity.ACTIVITIES_MESSAGE_KEY)
        image_view.setImageURI(image_uri)
        val drawable = image_view.drawable as BitmapDrawable
        bitmap = drawable.bitmap

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = (progress.toFloat() / 10).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar != null) {
                    scaleCoof = (seekBar.progress.toFloat() / 10)
                }
            }

        })

    }

    private fun scaling(){

        if(scaleCoof < 0.6){
            scaleCoof = 0.6f
        }
        var newWidth = (bitmap!!.width / scaleCoof).toInt()
        var newHeight = (bitmap!!.height / scaleCoof).toInt()
        bmp_Copy = Bitmap.createBitmap(newWidth,
            newHeight, Bitmap.Config.ARGB_8888)

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
        for(y in 1 until newHeight - 1){
            for(x in 1 until newWidth - 1){
                var gx = (x / newWidth.toFloat()) * bitmap!!.width.toFloat()
                var gy = (y / newHeight.toFloat()) * bitmap!!.height.toFloat()
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

    private fun saveImage(){
        scaling()
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
