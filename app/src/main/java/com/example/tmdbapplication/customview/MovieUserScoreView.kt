package com.example.tmdbapplication.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.tmdbapplication.R
import java.lang.Exception
import kotlin.math.min

class MovieUserScoreView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        context.obtainStyledAttributes(attrs, R.styleable.MovieUserScoreView).apply {
            try {
                backColor = getColor(R.styleable.MovieUserScoreView_musv_backColor, Color.DKGRAY)
                defStrokeColor =
                    getColor(R.styleable.MovieUserScoreView_musv_strokeColor, Color.GRAY)
                highScoreColor =
                    getColor(R.styleable.MovieUserScoreView_musv_highScoreColor, Color.GREEN)
                normalScoreColor =
                    getColor(R.styleable.MovieUserScoreView_musv_normalScoreColor, Color.YELLOW)
                lowScoreColor =
                    getColor(R.styleable.MovieUserScoreView_musv_lowScoreColor, Color.RED)
                textColor = getColor(R.styleable.MovieUserScoreView_musv_textColor, Color.WHITE)
            } catch (e: Exception) {
                Log.e(TAG, "obtainStyledAttributes:", e)
            }
            recycle()
        }
    }

    var backColor: Int
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var defStrokeColor: Int
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var highScoreColor: Int
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var normalScoreColor: Int
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var lowScoreColor: Int
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }
    var textColor: Int
        set(value) {
            if (field != value) {
                field = value
                invalidate()
            }
        }

    var userScore: Float? = null
        set(value) {
            field = value
            invalidate()
        }

    private val backPaint: Paint
        get() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = backColor
        }
    private val strokePaint: Paint
        get() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            color = defStrokeColor
            strokeWidth = min(width / STROKE_WIDTH_COEFFICIENT, height / STROKE_WIDTH_COEFFICIENT)
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
        }
    private val scorePaint: Paint
        get() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = min(width / STROKE_WIDTH_COEFFICIENT, height / STROKE_WIDTH_COEFFICIENT)
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            userScore?.let {
                color = when (it) {
                    in 7F..10F -> highScoreColor
                    in 4F..7F -> normalScoreColor
                    else -> lowScoreColor
                }
            }
        }
    private val textPaint: Paint
        get() = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = textColor
            textSize = min(width / TEXT_SIZE_COEFFICIENT, height / TEXT_SIZE_COEFFICIENT)
            textAlign = Paint.Align.CENTER
        }
    private val rectF = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.d(TAG, "onMeasure: ${MeasureSpec.toString(widthMeasureSpec)}")
        Log.d(TAG, "onMeasure: ${MeasureSpec.toString(heightMeasureSpec)}")
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val measuredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val measuredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        val resultWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> width
            MeasureSpec.AT_MOST -> min(width, measuredWidth)
            else -> measuredWidth
        }
        val resultHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> height
            MeasureSpec.AT_MOST -> min(height, measuredHeight)
            else -> measuredHeight
        }
        if (resultWidth < measuredWidth || resultHeight < measuredHeight) {
            Log.e(
                TAG, "View's dimension are smaller then required. " +
                        "The view might be cropped"
            )
        }
        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let { drawUserScore(it) }
        super.onDraw(canvas)
    }

    private fun drawUserScore(canvas: Canvas) {
        val cX = width / RADIUS_COEFFICIENT
        val cY = height / RADIUS_COEFFICIENT
        val radius = min(cX, cY)
        val sweepAngle = userScore?.let { (360F * it * 10) / 100 } ?: START_ANGLE
        val text = userScore?.let {
            if (it == 0F) NO_RATING else (it * 10).toInt().toString() + "%"
        } ?: NO_RATING
        val start = scorePaint.strokeWidth / 2
        val end = (radius * 2) - start
        rectF.set(start, start, end, end)
        with(canvas) {
            drawCircle(cX, cY, radius, backPaint)
            drawCircle(cX, cY, radius - strokePaint.strokeWidth / 2, strokePaint)
            drawArc(rectF, START_ANGLE, sweepAngle, false, scorePaint)
            drawText(text, cX, cY + STROKE_WIDTH_COEFFICIENT, textPaint)
        }
    }

    companion object {
        private const val TAG = "UserScoreView"
        private const val NO_RATING = "NR"
        private const val STROKE_WIDTH_COEFFICIENT = 8.5F
        private const val TEXT_SIZE_COEFFICIENT = 3.5F
        private const val RADIUS_COEFFICIENT = 2F
        private const val START_ANGLE = 270F
    }
}