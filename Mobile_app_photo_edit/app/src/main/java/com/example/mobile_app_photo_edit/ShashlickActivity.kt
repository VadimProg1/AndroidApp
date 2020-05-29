package com.example.mobile_app_photo_edit

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_shashlick.*

class ShashlickActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val male90upCoof = 0.6f
        val male90downCoof = 0.5f
        val woman70upCoof = 0.5f
        val woman70downCoof = 0.4f
        val kidsCoof = 0.3f
        var textMale90up = 0
        var textMale90down = 0
        var textWoman90up= 0
        var textWoman90down = 0
        var textKids = 0
        var result = 0f
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shashlick)
        btn_minusMale90up.setOnClickListener{
            if(textMale90up > 0){
                textMale90up--
                result-= male90upCoof
                textViewMale90up.text = textMale90up.toString()
            }
        }
        btn_plusMale90up.setOnClickListener{
            result+= male90upCoof
            textMale90up++
            textViewMale90up.text = textMale90up.toString()
        }
        btn_minusMale90down.setOnClickListener{
            if(textMale90down > 0){
                textMale90down--
                textViewMale90down.text = textMale90down.toString()
                result-= male90downCoof
            }
        }
        btn_plusMale90down.setOnClickListener{
            result+= male90downCoof
            textMale90down++
            textViewMale90down.text = textMale90down.toString()
        }
        btn_minusWoman70up.setOnClickListener{
            if(textWoman90up > 0){
                textWoman90up--
                result-= woman70upCoof
                textViewWomen70up.text = textWoman90up.toString()
            }
        }
        btn_plusWoman70up.setOnClickListener{
            result+= woman70upCoof
            textWoman90up++
            textViewWomen70up.text = textWoman90up.toString()
        }
        btn_minusWoman70down.setOnClickListener{
            if(textWoman90down > 0){
                textWoman90down--
                result-= woman70downCoof
                textViewWomen70down.text = textWoman90down.toString()
            }
        }
        btn_plusWoman70down.setOnClickListener{
            result+= woman70downCoof
            textWoman90down++
            textViewWomen70down.text = textWoman90down.toString()
        }
        btn_minusKids.setOnClickListener{
            if(textKids > 0){
                result-= kidsCoof
                textKids--
                textViewKids.text = textKids.toString()
            }
        }
        btn_plusKids.setOnClickListener{
            textKids++
            textViewKids.text = textKids.toString()
            result+= kidsCoof
        }
        btn_calculate.setOnClickListener{
            if(result > 0.1) {
                val toast = Toast.makeText(applicationContext, "$result kg", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
    }
}
