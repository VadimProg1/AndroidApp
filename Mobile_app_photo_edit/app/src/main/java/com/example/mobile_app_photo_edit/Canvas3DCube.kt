package com.example.mobile_app_photo_edit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class Canvas3DCube : View {
    private val mPath: Path = Path()
    private var mWidth = 0
    private var mHeight = 0
    val paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        strokeWidth = 12f
    }

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attribs: AttributeSet?) : super(
        context,
        attribs
    )

    private fun initSquare(){
        s.add(
            Square(
                Vertex(-400f, -400f, 400f),
                Vertex(400f, -400f, 400f),
                Vertex(400f, 400f, 400f),
                Vertex(-400f, 400f, 400f),
                Vertex(0f, 0f, 400f)
            )
        )
        s.add(
            Square(
                Vertex(-400f, -400f, -400f),
                Vertex(400f, -400f, -400f),
                Vertex(400f, -400f, 400f),
                Vertex(-400f, -400f, 400f),
                Vertex(0f, -400f, 0f)
            )
        )
        s.add(
            Square(
                Vertex(-400f, 400f, 400f),
                Vertex(-400f, 400f, -400f),
                Vertex(400f, 400f, -400f),
                Vertex(400f, 400f, 400f),
                Vertex(0f, 500f, 0f)
            )
        )
        s.add(
            Square(
                Vertex(400f, -400f, 400f),
                Vertex(400f, -400f, -400f),
                Vertex(400f, 400f, -400f),
                Vertex(400f, 400f, 400f),
                Vertex(400f, 0f, 0f)
            )
        )
        s.add(
            Square(
                Vertex(-400f, -400f, -400f),
                Vertex(-400f, -400f, 400f),
                Vertex(-400f, 400f, 400f),
                Vertex(-400f, 400f, -400f),
                Vertex(-400f, 0f, 0f)
            )
        )
        s.add(
            Square(
                Vertex(-400f, -400f, -400f),
                Vertex(400f, -400f, -400f),
                Vertex(400f, 400f, -400f),
                Vertex(-400f, 400f, -400f),
                Vertex(0f, 0f, -400f)
            )
        )
    }

    private fun initNums(){
        nums.add(
            //four
            mutableListOf<Vertex>(
                Vertex(-300f, -250f, 400f),
                Vertex(-150f, 250f, 400f),
                Vertex(0f, -250f, 400f),
                Vertex(200f, 250f, 400f),
                Vertex(200f, -250f, 400f),
                Vertex(100f, 250f, 400f),
                Vertex(300f, 250f, 400f),
                Vertex(100f, -250f, 400f),
                Vertex(300f, -250f, 400f)
            )
        )
        //two
        nums.add(
            mutableListOf<Vertex>(
                Vertex(-300f, -400f, -250f),
                Vertex(-100f, -400f, -250f),
                Vertex(-200f, -400f, -250f),
                Vertex(-200f, -400f, 250f),
                Vertex(-300f, -400f, 250f),
                Vertex(-100f, -400f, 250f),

                Vertex(300f, -400f, -250f),
                Vertex(100f, -400f, -250f),
                Vertex(200f, -400f, -250f),
                Vertex(200f, -400f, 250f),
                Vertex(300f, -400f, 250f),
                Vertex(100f, -400f, 250f)
            )
        )
        //three
        nums.add(
            mutableListOf<Vertex>(
                Vertex(-300f, 400f, -250f),
                Vertex(-200f, 400f, -250f),
                Vertex(-250f, 400f, -250f),
                Vertex(-250f, 400f, 250f),
                Vertex(-300f, 400f, 250f),
                Vertex(-200f, 400f, 250f),

                Vertex(-50f, 400f, -250f),
                Vertex(50f, 400f, -250f),
                Vertex(0f, 400f, -250f),
                Vertex(0f, 400f, 250f),
                Vertex(-50f, 400f, 250f),
                Vertex(50f, 400f, 250f),

                Vertex(300f, 400f, -250f),
                Vertex(200f, 400f, -250f),
                Vertex(250f, 400f, -250f),
                Vertex(250f, 400f, 250f),
                Vertex(300f, 400f, 250f),
                Vertex(200f, 400f, 250f)

            )
        )
        //five
        nums.add(
            mutableListOf<Vertex>(
                Vertex(400f, -250f, -200f),
                Vertex(400f, 250f, 0f),
                Vertex(400f, -250f, 200f)
            )
        )
        //six
        nums.add(
            mutableListOf<Vertex>(
                Vertex(-400f, -250f, -300f),
                Vertex(-400f, -250f, -100f),
                Vertex(-400f, -250f, -200f),
                Vertex(-400f, 250f, -200f),
                Vertex(-400f, 250f, -300f),
                Vertex(-400f, 250f, -100f),

                Vertex(-400f, -250f, 0f),
                Vertex(-400f, 250f, 150f),
                Vertex(-400f, -250f, 300f)

            )
        )
        //one
        nums.add(
            mutableListOf<Vertex>(
                Vertex(0f, 250f, -400f),
                Vertex(0f, -250f, -400f),
                Vertex(-100f, 250f, -400f),
                Vertex(100f, 250f, -400f),
                Vertex(100f, -250f, -400f),
                Vertex(-100f, -250f, -400f)
            )
        )
    }

    override fun onDraw(canvas: Canvas) {
        if(s.size == 0){
            initSquare()
            initNums()
        }

        var d = mWidth
        canvas.scale(0.5f, 0.5f)

        for(i in 0 until s.size) {
            if(s[i].centre.z < 0) {
                canvas.drawLine(s[i].v1.x + d, s[i].v1.y + d, s[i].v2.x + d, s[i].v2.y + d, paint)
                canvas.drawLine(s[i].v2.x + d, s[i].v2.y + d, s[i].v3.x + d, s[i].v3.y + d, paint)
                canvas.drawLine(s[i].v3.x + d, s[i].v3.y + d, s[i].v4.x + d, s[i].v4.y + d, paint)
                canvas.drawLine(s[i].v4.x + d, s[i].v4.y + d, s[i].v1.x + d, s[i].v1.y + d, paint)
                if(i == 0){
                    canvas.drawLine(nums[0][0].x + d, nums[0][0].y + d, nums[0][1].x + d, nums[0][1].y + d, paint)
                    canvas.drawLine(nums[0][1].x + d, nums[0][1].y + d, nums[0][2].x + d, nums[0][2].y + d, paint)
                    canvas.drawLine(nums[0][3].x + d, nums[0][3].y + d, nums[0][4].x + d, nums[0][4].y + d, paint)
                    canvas.drawLine(nums[0][5].x + d, nums[0][5].y + d, nums[0][6].x + d, nums[0][6].y + d, paint)
                    canvas.drawLine(nums[0][7].x + d, nums[0][7].y + d, nums[0][8].x + d, nums[0][8].y + d, paint)
                }
                if(i == 1){
                    canvas.drawLine(nums[1][0].x + d, nums[1][0].y + d, nums[1][1].x + d, nums[1][1].y + d, paint)
                    canvas.drawLine(nums[1][2].x + d, nums[1][2].y + d, nums[1][3].x + d, nums[1][3].y + d, paint)
                    canvas.drawLine(nums[1][4].x + d, nums[1][4].y + d, nums[1][5].x + d, nums[1][5].y + d, paint)
                    canvas.drawLine(nums[1][6].x + d, nums[1][6].y + d, nums[1][7].x + d, nums[1][7].y + d, paint)
                    canvas.drawLine(nums[1][8].x + d, nums[1][8].y + d, nums[1][9].x + d, nums[1][9].y + d, paint)
                    canvas.drawLine(nums[1][10].x + d, nums[1][10].y + d, nums[1][11].x + d, nums[1][11].y + d, paint)
                }
                if(i == 2){
                    canvas.drawLine(nums[2][0].x + d, nums[2][0].y + d, nums[2][1].x + d, nums[2][1].y + d, paint)
                    canvas.drawLine(nums[2][2].x + d, nums[2][2].y + d, nums[2][3].x + d, nums[2][3].y + d, paint)
                    canvas.drawLine(nums[2][4].x + d, nums[2][4].y + d, nums[2][5].x + d, nums[2][5].y + d, paint)

                    canvas.drawLine(nums[2][6].x + d, nums[2][6].y + d, nums[2][7].x + d, nums[2][7].y + d, paint)
                    canvas.drawLine(nums[2][8].x + d, nums[2][8].y + d, nums[2][9].x + d, nums[2][9].y + d, paint)
                    canvas.drawLine(nums[2][10].x + d, nums[2][10].y + d, nums[2][11].x + d, nums[2][11].y + d, paint)

                    canvas.drawLine(nums[2][12].x + d, nums[2][12].y + d, nums[2][13].x + d, nums[2][13].y + d, paint)
                    canvas.drawLine(nums[2][14].x + d, nums[2][14].y + d, nums[2][15].x + d, nums[2][15].y + d, paint)
                    canvas.drawLine(nums[2][16].x + d, nums[2][16].y + d, nums[2][17].x + d, nums[2][17].y + d, paint)
                }
                if(i == 3){
                    canvas.drawLine(nums[3][0].x + d, nums[3][0].y + d, nums[3][1].x + d, nums[3][1].y + d, paint)
                    canvas.drawLine(nums[3][1].x + d, nums[3][1].y + d, nums[3][2].x + d, nums[3][2].y + d, paint)
                }
                if(i == 4){
                    canvas.drawLine(nums[4][0].x + d, nums[4][0].y + d, nums[4][1].x + d, nums[4][1].y + d, paint)
                    canvas.drawLine(nums[4][2].x + d, nums[4][2].y + d, nums[4][3].x + d, nums[4][3].y + d, paint)
                    canvas.drawLine(nums[4][4].x + d, nums[4][4].y + d, nums[4][5].x + d, nums[4][5].y + d, paint)

                    canvas.drawLine(nums[4][6].x + d, nums[4][6].y + d, nums[4][7].x + d, nums[4][7].y + d, paint)
                    canvas.drawLine(nums[4][7].x + d, nums[4][7].y + d, nums[4][8].x + d, nums[4][8].y + d, paint)
                }
                if(i == 5){
                    canvas.drawLine(nums[5][0].x + d, nums[5][0].y + d, nums[5][1].x + d, nums[5][1].y + d, paint)
                    canvas.drawLine(nums[5][2].x + d, nums[5][2].y + d, nums[5][3].x + d, nums[5][3].y + d, paint)
                    canvas.drawLine(nums[5][4].x + d, nums[5][4].y + d, nums[5][5].x + d, nums[5][5].y + d, paint)
                }
            }
        }
        canvas.save()
        super.onDraw(canvas)
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