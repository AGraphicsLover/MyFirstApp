package com.example.myfirstapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.example.myfirstapp.R

class ArcView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

  private var mWidth: Int = 0
  private var mHeight: Int = 0
  private var mArcHeight: Int = 0
  private var mBgColor: Int
  private var mPaint: Paint = Paint()

  init {
    val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcView)
    mArcHeight = typedArray.getDimensionPixelSize(R.styleable.ArcView_arcHeight, 0)
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
    mBgColor = ContextCompat.getColor(context, typedValue.resourceId)
    typedArray.recycle()

    mPaint = Paint()
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)

    mPaint.style = Paint.Style.FILL
    mPaint.color = mBgColor
    mPaint.isAntiAlias = true

    val rect = Rect(0, 0, mWidth, mHeight)
    canvas.drawRect(rect, mPaint)

    mPaint.color = ContextCompat.getColor(context, R.color.bgPurple)

    val path = Path()
    path.reset()
    path.moveTo(0f, (mHeight).toFloat())
    path.quadTo(
      (mWidth / 2).toFloat(),
      (mHeight - mArcHeight).toFloat(),
      mWidth.toFloat(),
      (mHeight).toFloat()
    )
    canvas.drawPath(path, mPaint)
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    val widthMode = MeasureSpec.getMode(widthMeasureSpec)
    val heightSize = MeasureSpec.getSize(heightMeasureSpec)
    val heightMode = MeasureSpec.getMode(heightMeasureSpec)

    if (widthMode == MeasureSpec.EXACTLY) {
      mWidth = widthSize
    }
    if (heightMode == MeasureSpec.EXACTLY) {
      mHeight = heightSize
    }
    setMeasuredDimension(mWidth, mHeight)
  }
}