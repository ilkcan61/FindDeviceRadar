package com.ilkcanyilmaz.finddeviceradar

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.SpannableString
import android.text.StaticLayout
import android.text.TextPaint
import android.text.style.RelativeSizeSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import kotlin.math.absoluteValue

class RadarView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    companion object {
        private val DEFAULT_CIRCLEBACKGORUNDCOLOR = Color.rgb(69, 123, 157)
        private const val DEFAULT_DEVICE_DISTANCE = 1000f
        private val DEFAULT_DEVICE_DRAWABLE = R.drawable.ic_device

    }

    var deviceDistance = 1000f
    var deviceDrawable: Drawable? = null
    var circleBackgroundColor: Int = Color.rgb(24, 78, 119)

    private val POINT_ARRAY_SIZE = 5
    private var size = 0
    private var bitmapDeviceWidth = 0
    var latestPoint: Array<Point?> = arrayOfNulls(POINT_ARRAY_SIZE)
    var latestPaint: Array<Paint?> = arrayOfNulls(POINT_ARRAY_SIZE)

    constructor(context: Context?) : this(context, null) {}
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {}

    var centerX = 0
    var centerY = 0

    private fun setupAttributes(attrs: AttributeSet?) {
        // Obtain a typed array of attribute
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.RadarView,
            0, 0
        )
        background
        circleBackgroundColor =
            typedArray.getColor(
                R.styleable.RadarView_circleBackgroundColor,
                DEFAULT_CIRCLEBACKGORUNDCOLOR
            )
        // Extract custom attributes into member variable
        deviceDistance =
            typedArray.getFloat(R.styleable.RadarView_distance, DEFAULT_DEVICE_DISTANCE)

        val drawableResId =
            typedArray.getResourceId(R.styleable.RadarView_deviceDrawable, DEFAULT_DEVICE_DRAWABLE)
        deviceDrawable = AppCompatResources.getDrawable(context, drawableResId)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val r = width.coerceAtMost(height)

        val i = r / 2f

        drawCircleBackground(canvas, width - (i), height - 200f)

        drawTriangle(canvas, width - i.toInt(), height - 200, width)

        val paintLine = Paint()
        paintLine.color = context.getColor(R.color.white)
        paintLine.alpha = 160
        paintLine.strokeWidth = 2f
        drawLine(canvas, paintLine, width - i.toInt(), height - 200, width)

        val debug = false
        if (debug) {
            val sb = StringBuilder(" >> ")
            for (p in latestPoint) {
                if (p != null) sb.append(" (" + p.x.toString() + "x" + p.y.toString() + ")")
            }
        }

        drawDevice(canvas, width / 2)

        drawDistanceText(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        size = measuredWidth.coerceAtMost(measuredHeight)

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        centerX = (measuredWidth / 2)
        centerY = (measuredHeight - 200f).toInt()
    }


    init {
        setupAttributes(attrs)
    }

    private fun drawCircleBackground(canvas: Canvas, x: Float, y: Float) {
        val localPaint = Paint()
        localPaint.color = circleBackgroundColor
        localPaint.isAntiAlias = true
        localPaint.style = Paint.Style.STROKE
        localPaint.strokeWidth = 2.0f
        localPaint.style = Paint.Style.FILL
        val colorList = arrayOf(100, 120, 150, 175, 255)
        for (i in latestPaint.indices) {
            latestPaint[i] = Paint(localPaint)
            if (i < 5) {
                latestPaint[i]!!.alpha = colorList[i]
            }
        }


        val a = (height - 200) / (width - x - 1f)
        val j = (width - x - 1f) * a / 1.2.toFloat()


        canvas.drawCircle(x, y, j * 1.25f, latestPaint[0]!!)
        canvas.drawCircle(x, y, j, latestPaint[1]!!)
        canvas.drawCircle(x, y, j * 3 / 4, latestPaint[2]!!)
        canvas.drawCircle(
            x,
            y,
            (j.toInt() shr 1).toFloat(),
            latestPaint[3]!!
        )
        canvas.drawCircle(
            x,
            y,
            (j.toInt() shr 2).toFloat(),
            latestPaint[4]!!
        )

        invalidate()
    }

    private fun drawTriangle(canvas: Canvas, x: Int, y: Int, width: Int) {
        val path = Path()
        val paintTriangle = Paint()
        paintTriangle.color = context.getColor(R.color.white)
        paintTriangle.alpha = 70

        path.moveTo(x.toFloat(), y.toFloat())
        path.lineTo(x.toFloat() - width, 0f)
        path.lineTo(x.toFloat() + width, 0f)
        path.lineTo(x.toFloat() + width, 0f)
        path.close()
        canvas.drawPath(path, paintTriangle)
    }

    private fun drawLine(canvas: Canvas, paint: Paint?, x: Int, y: Int, width: Int) {
        val r = width.coerceAtMost(height)

        val a = (height - 200) / (width - x - 1f)
        val j = (width - x - 1f) * a / 1.2.toFloat()

        canvas.drawLine(x.toFloat(), y.toFloat(), x.toFloat(), y - j, paint!!)
    }

    private fun drawDevice(canvas: Canvas, x: Int) {
        val paint = Paint()
        paint.alpha = 255
        val bitmap = (deviceDrawable as BitmapDrawable).bitmap
        bitmapDeviceWidth = bitmap.width
        canvas.drawBitmap(
            bitmap,
            x.toFloat() - bitmap.width / 2,
            deviceDistance,
            paint
        )
    }

    private fun drawDistanceText(canvas: Canvas) {
        val s = (deviceDistance.toInt() - height).absoluteValue.toString().plus("\nMetre")
        val ss1 = SpannableString(s)
        ss1.setSpan(RelativeSizeSpan(0.6f), s.length - 5, s.length, 0)


        val textSizeValue = 32

        val textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            textSizeValue.toFloat(), resources.displayMetrics
        ).toInt()

        val textPaint = TextPaint()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize.toFloat()
        textPaint.color = Color.WHITE
        val customTypeface = resources.getFont(R.font.poppins_medium)
        textPaint.typeface = customTypeface
        val mTextLayout =
            StaticLayout(
                ss1,
                textPaint,
                canvas.width,
                Layout.Alignment.ALIGN_NORMAL,
                0.8f,
                0.0f,
                false
            )

        canvas.save()

        val xPos = canvas.width / 2
        val yPos = deviceDistance + bitmapDeviceWidth

        canvas.translate(xPos.toFloat(), yPos.toFloat());
        mTextLayout.draw(canvas);
        canvas.restore();
    }

}