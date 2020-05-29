package com.example.mobile_app_photo_edit

import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_cube3_d.*

var s: MutableList<Square> = ArrayList()
var nums: MutableList<MutableList<Vertex>> = ArrayList()

class Cube3DActivity : AppCompatActivity() {

    var prevRZ = 0
    var prevRY = 0
    var prevRX = 0
    var motionTouchEventX = 0f
    var motionTouchEventY = 0f
    var startTouchEventX = 0f
    var startTouchEventY = 0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cube3_d)

        canv.setOnTouchListener(View.OnTouchListener { v, event ->
            val action = event.action
            motionTouchEventX = event.x
            motionTouchEventY = event.y
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    startTouchEventX = motionTouchEventX
                    startTouchEventY = motionTouchEventY
                }
                MotionEvent.ACTION_MOVE -> {
                    var diffX = (startTouchEventX.toDouble() - motionTouchEventX.toDouble()) / 4
                    var diffY = (motionTouchEventY.toDouble() - startTouchEventY.toDouble()) / 4
                    startTouchEventX = motionTouchEventX
                    startTouchEventY = motionTouchEventY
                    val headingY = Math.toRadians(diffX)
                    val headingX = Math.toRadians(diffY)
                    var transformX = Matrix3(
                        doubleArrayOf(
                            1.0, 0.0, 0.0,
                            0.0, Math.cos(headingX), Math.sin(headingX),
                            0.0, -Math.sin(headingX), Math.cos(headingX)
                        )
                    )
                    var transformY = Matrix3(
                        doubleArrayOf(
                            Math.cos(headingY), 0.0, -Math.sin(headingY),
                            0.0 , 1.0, 0.0,
                            Math.sin(headingY), 0.0, Math.cos(headingY)
                        )
                    )
                    transformX = transformX.multiply(transformY)!!
                    for(i in 0 until s.size) {
                        s[i].v1 = transformX.transform(s[i].v1)
                        s[i].v2 = transformX.transform(s[i].v2)
                        s[i].v3 = transformX.transform(s[i].v3)
                        s[i].v4 = transformX.transform(s[i].v4)
                        s[i].centre = transformX.transform(s[i].centre)
                        for(j in 0 until nums[i].size) {
                            nums[i][j] = transformX.transform(nums[i][j])
                        }
                    }
                    canv.draw()
                }
                MotionEvent.ACTION_UP -> {}
            }
            true
        })

        seekBarZ.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val heading = Math.toRadians(seekBar!!.progress.toDouble() - prevRX)
                val transform = Matrix3(
                    doubleArrayOf(
                        Math.cos(heading), -Math.sin(heading), 0.0,
                        Math.sin(heading), Math.cos(heading), 0.0,
                        0.0, 0.0, 1.0
                    )
                )
                for(i in 0 until s.size) {
                    s[i].v1 = transform.transform(s[i].v1)
                    s[i].v2 = transform.transform(s[i].v2)
                    s[i].v3 = transform.transform(s[i].v3)
                    s[i].v4 = transform.transform(s[i].v4)
                    s[i].centre = transform.transform(s[i].centre)
                    for(j in 0 until nums[i].size) {
                        nums[i][j] = transform.transform(nums[i][j])
                    }
                }
                canv.draw()
                prevRX = seekBar.progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

}

class Vertex(var x: Float, var y: Float, var z: Float)

class Square(
    var v1: Vertex,
    var v2: Vertex,
    var v3: Vertex,
    var v4: Vertex,
    var centre: Vertex
)

class Matrix3(var values: DoubleArray) {

    fun multiply(other: Matrix3): Matrix3? {
        val result = DoubleArray(9)
        for (row in 0..2) {
            for (col in 0..2) {
                for (i in 0..2) {
                    result[row * 3 + col] += values[row * 3 + i] * other.values[i * 3 + col]
                }
            }
        }
        return Matrix3(result)
    }
    fun transform(`in`: Vertex): Vertex {
        return Vertex(
            (`in`.x * values[0] + `in`.y * values[3] + `in`.z * values[6]).toFloat(),
            (`in`.x * values[1] + `in`.y * values[4] + `in`.z * values[7]).toFloat(),
            (`in`.x * values[2] + `in`.y * values[5] + `in`.z * values[8]).toFloat()
        )
    }

}