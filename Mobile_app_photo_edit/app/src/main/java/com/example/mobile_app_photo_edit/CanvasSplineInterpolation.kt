package com.example.mobile_app_photo_edit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class CanvasSplineInterpolation : View {
    private var mWidth = 0
    private var mHeight = 0
    private var dotX = 0f
    private var dotY = 0f
    private var dotXN = 0f
    private var dotYN = 0f
    private var counterOfDots = 0
    var path = Path()
    var pathN = Path()
    var pathDot = Path()
    val paint = Paint().apply {
        color = Color.BLACK
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = 20f // default: Hairline-width (really thin)
    }
    val paintDot = Paint().apply {
        color = Color.BLACK
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = 40f // default: Hairline-width (really thin)
    }
    var drawSpline = false
    lateinit var a: FloatArray
    lateinit var b: FloatArray
    lateinit var c: FloatArray
    lateinit var d: FloatArray

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attribs: AttributeSet?) : super(
        context,
        attribs
    )

    override fun onDraw(canvas: Canvas) {
        if(counterOfDots == 1 && drawSpline == false){
            canvas.drawPoint(dotX, dotY, paint)
            path.moveTo(dotX, dotY)
        }
        else if(drawSpline == false){
            path.lineTo(dotX, dotY)
            canvas.drawPath(path, paint)
        }
        else if (drawSpline == true){
            for(i in 0.. dott.size - 2) {
                dotX = dott[i].x
                dotY = dott[i].y
                dotXN = dott[i + 1].x
                dotYN = dott[i + 1].y
                pathN.moveTo(dotX, dotY)
                var oldX = dotX
                pathDot.moveTo(dotX, dotY)
                pathDot.lineTo(dotX + 1, dotY + 1)
                canvas.drawPath(pathDot, paintDot)
                if(dotX < dotXN) {
                    while (dotX <= dotXN) {
                        pathN.lineTo(dotX, dotY)
                        dotY = a[i] + b[i] * (dotX - oldX) + c[i] * ((dotX - oldX) * (dotX - oldX)) + d[i] * ((dotX - oldX) * (dotX - oldX) * (dotX - oldX))
                        dotX += 1
                    }
                }
                else{
                    while (dotX >= dotXN) {
                        pathN.lineTo(dotX, dotY)
                        dotY = a[i] + b[i] * (dotX - oldX) + c[i] * ((dotX - oldX) * (dotX - oldX)) + d[i] * ((dotX - oldX) * (dotX - oldX) * (dotX - oldX))
                        dotX -= 1
                    }
                }
            }
            pathDot.moveTo(dott[dott.size - 1].x, dott[dott.size - 1].y)
            pathDot.lineTo(dott[dott.size - 1].x, dott[dott.size - 1].y)
            canvas.drawPath(pathDot, paintDot)
            canvas.drawPath(pathN, paint)
        }
    }

    fun drawSpline(A: FloatArray, B: FloatArray, C: FloatArray, D: FloatArray){
        a = A
        b = B
        c = C
        d = D
        drawSpline = true
        invalidate()
        requestLayout()
    }

    fun drawDot(x: Float, y: Float, spline: Boolean){
        drawSpline = spline
        dotX = x
        dotY = y
        counterOfDots++
        invalidate()
        requestLayout()
    }

    fun draw(){
        invalidate()
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(mWidth, mHeight)
    }
}