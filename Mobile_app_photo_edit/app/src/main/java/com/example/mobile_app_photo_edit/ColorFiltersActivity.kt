package com.example.mobile_app_photo_edit

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mobile_app_photo_edit.MainActivity
import com.example.mobile_app_photo_edit.R
import kotlinx.android.synthetic.main.activity_main.*

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
    }
}
