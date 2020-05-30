package com.example.mobile_app_photo_edit

import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.INVISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_spline_interpolation.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sqrt

var dott: MutableList<dot> = ArrayList()

class SplineInterpolationActivity : AppCompatActivity() {

    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f
    var drawSpline: Boolean = false
    var changingSpline = false
    lateinit var a: FloatArray
    lateinit var b: FloatArray
    lateinit var c: FloatArray
    lateinit var d: FloatArray
    var indexNewDot: Int = 0
    var check = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spline_interpolation)

        canvasSpline.setOnTouchListener(View.OnTouchListener { v, event ->
            val action = event.action
            motionTouchEventX = event.x
            motionTouchEventY = event.y
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    if(!changingSpline) {
                        canvasSpline.drawDot(motionTouchEventX, motionTouchEventY, drawSpline)
                        dott.add(dot(motionTouchEventX, motionTouchEventY))
                    }
                    if(changingSpline){
                        check = checkNewDot(motionTouchEventX, motionTouchEventY)
                        if(check){
                            indexNewDot = findIndexNewDot(motionTouchEventX, motionTouchEventY)
                        }
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if(changingSpline){
                        if(check) {
                            dott[indexNewDot].x = motionTouchEventX
                            dott[indexNewDot].y = motionTouchEventY
                            if(indexNewDot == dott.size - 1) {
                                if (dott[indexNewDot].x < dott[indexNewDot - 1].x) {
                                    indexNewDot--
                                }
                            }
                            else if(indexNewDot == 0) {
                                if (dott[indexNewDot].x > dott[indexNewDot + 1].x) {
                                    indexNewDot++
                                }
                            }
                            else{
                                if (dott[indexNewDot].x < dott[indexNewDot - 1].x) {
                                    indexNewDot--
                                }
                                else if (dott[indexNewDot].x > dott[indexNewDot + 1].x) {
                                    indexNewDot++
                                }
                            }
                            dott.sortBy { it.x }
                            canvasSpline.pathN.reset()
                            canvasSpline.pathDot.reset()
                            interpolation()
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {}
            }
            true
        })

        btn_interpolation.setOnClickListener{
            if(dott.size > 1) {
                btn_interpolation.visibility = INVISIBLE
                dott.sortBy { it.x }
                interpolation()
            }
            else{
                val toast = Toast.makeText(applicationContext, "Set dots", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
    }

    private fun checkNewDot(x: Float, y: Float): Boolean{
        for(i in 0 until dott.size) {
            var startX = dott[i].x
            var startY = dott[i].y
            if(sqrt((x - startX) * (x - startX) + (y - startY) * (y - startY)) < 80){
                return true
            }
        }
        return false
    }

    private fun findIndexNewDot(x: Float, y: Float): Int{
        var indexDot: Int = 0
        for(i in 0 until dott.size) {
            var startX = dott[i].x
            var startY = dott[i].y
            if(sqrt((x - startX) * (x - startX) + (y - startY) * (y - startY)) < 80){
                indexDot = i
                break
            }
        }
        return indexDot
    }

    private fun interpolation(){
        changingSpline = true
        a = FloatArray(dott.size)
        b = FloatArray(dott.size - 1)
        d = FloatArray(dott.size - 1)
        val h = FloatArray(dott.size - 1)
        val alpha = FloatArray(dott.size - 1)
        c = FloatArray(dott.size)
        val l = FloatArray(dott.size)
        val u = FloatArray(dott.size)
        val z = FloatArray(dott.size)
        for(i in 0..a.size - 1){
            a[i] = dott[i].y
        }
        for(i in 0..h.size - 1){
            h[i] = dott[i + 1].x - dott[i].x
        }
        for(i in 1..alpha.size - 1) {
            alpha[i] = (3 / h[i]) * (a[i + 1] - a[i]) - (3 / h[i - 1]) * (a[i] - a[i - 1])
        }
        l[0] = 1f
        u[0] = 0f
        z[0] = 0f
        for(i in 1.. dott.size - 2){
            l[i] = 2 * (dott[i + 1].x - dott[i - 1].x) - h[i - 1] * u[i - 1]
            u[i] = h[i] / l[i]
            z[i] = (alpha[i] - h[i - 1] * z[i - 1]) / l[i]
        }
        l[dott.size - 1] = 1f
        z[dott.size - 1] = 0f
        c[dott.size - 1] = 0f
        for(j  in dott.size - 2 downTo 0){
            c[j] = z[j] - u[j] * c[j + 1]
            b[j] = (a[j + 1] - a[j]) / h[j] - (h[j] * (c[j + 1] + 2 * c[j])) / 3
            d[j] = (c[j + 1] - c[j]) / (3 * h[j])
        }
        canvasSpline.drawSpline(a, b, c, d)
    }

    override fun onBackPressed() {
        dott.clear()
        drawSpline = false
        canvasSpline.drawSpline = false
        super.onBackPressed()
       overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}


class dot(var x: Float, var y: Float)
