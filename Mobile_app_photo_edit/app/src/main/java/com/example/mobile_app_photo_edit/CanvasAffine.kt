package com.example.mobile_app_photo_edit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class CanvasAffine : View {

    var mWidth = 0
    var mHeight = 0
    var dotX = 0f
    var dotY = 0f
    var firstTriDotX = 0f
    var firstTriDotY = 0f
    var secondTriDotX = 0f
    var secondTriDotY = 0f
    var firstPath = Path()
    var secondPath = Path()

    private var counterOfDots: Int = 0

    val paintFirst = Paint().apply {
        color = Color.RED
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = 20f // default: Hairline-width (really thin)
    }


    val paintSecond = Paint().apply {
        color = Color.BLUE
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true
        style = Paint.Style.STROKE // default: FILL
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT
        strokeWidth = 20f // default: Hairline-width (really thin)
    }


    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attribs: AttributeSet?) : super(
        context,
        attribs
    )


    override fun onDraw(canvas: Canvas) {
        if(counterOfDots == 1) {
            canvas.drawPoint(dotX, dotY, paintFirst)
            firstPath.moveTo(dotX, dotY)
        }
        if(counterOfDots == 2){
            firstPath.lineTo(dotX, dotY)
            canvas.drawPath(firstPath, paintFirst)
        }
        if(counterOfDots == 3){
            firstPath.lineTo(dotX, dotY)
            firstPath.lineTo(firstTriDotX, firstTriDotY)
            canvas.drawPath(firstPath, paintFirst)
        }
        if(counterOfDots == 4){
            canvas.drawPoint(dotX, dotY, paintSecond)
            secondPath.moveTo(dotX, dotY)
            canvas.drawPath(firstPath, paintFirst)
        }
        if(counterOfDots == 5){
            secondPath.lineTo(dotX, dotY)
            canvas.drawPath(firstPath, paintFirst)
            canvas.drawPath(secondPath, paintSecond)
        }
        if(counterOfDots == 6){
            secondPath.lineTo(dotX, dotY)
            secondPath.lineTo(secondTriDotX, secondTriDotY)
            canvas.drawPath(firstPath, paintFirst)
            canvas.drawPath(secondPath, paintSecond)
        }
    }

    fun drawLine(x: Float, y: Float){
        dotX = x
        dotY = y
        counterOfDots++
        if(counterOfDots == 1){
            firstTriDotX = x
            firstTriDotY = y
        }
        if(counterOfDots == 4){
            secondTriDotX = x
            secondTriDotY = y
        }
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