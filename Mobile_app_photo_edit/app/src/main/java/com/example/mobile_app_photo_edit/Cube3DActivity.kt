package com.example.mobile_app_photo_edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_cube3_d.*

var s: MutableList<Square> = ArrayList()
var nums: MutableList<MutableList<Vertex>> = ArrayList()

class Cube3DActivity : AppCompatActivity() {

    var prevRZ = 0
    var prevRY = 0
    var prevRX = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cube3_d)

        seekBarZ.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val heading = Math.toRadians(seekBar!!.progress.toDouble() - prevRZ)
                val transform = Matrix3(
                    doubleArrayOf(
                        Math.cos(heading), 0.0, -Math.sin(heading),
                        0.0 , 1.0, 0.0,
                        Math.sin(heading), 0.0, Math.cos(heading)
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
                prevRZ = seekBar.progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarY.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val heading = Math.toRadians(seekBar!!.progress.toDouble() - prevRY)
                val transform = Matrix3(
                    doubleArrayOf(
                        1.0, 0.0, 0.0,
                        0.0, Math.cos(heading), Math.sin(heading),
                        0.0, -Math.sin(heading), Math.cos(heading)
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
                prevRY = seekBar.progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        seekBarX.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
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
    fun transform(`in`: Vertex): Vertex {
        return Vertex(
            (`in`.x * values[0] + `in`.y * values[3] + `in`.z * values[6]).toFloat(),
            (`in`.x * values[1] + `in`.y * values[4] + `in`.z * values[7]).toFloat(),
            (`in`.x * values[2] + `in`.y * values[5] + `in`.z * values[8]).toFloat()
        )
    }

}