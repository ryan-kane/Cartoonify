package com.example.cartoonify.ExtractForeground

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import kotlin.math.abs


private const val TAG = "ExtractForeGroundView"
class ExtractForeGroundView(context: Context) : View(context)
{

    private lateinit var extractForegroundViewListener: ExtractForegroundListener

    private var imBitmap: Bitmap? = null
    private var mCanvas: Canvas? = null
    private var mBitmap: Bitmap? = null
    private val mPath: Path = Path()
    private val mBitmapPaint: Paint = Paint(Paint.DITHER_FLAG)
    private val paint = getPaint()

    private fun getPaint(): Paint {
        val paintColor = Color.BLACK
        val paint = Paint()
        paint.isAntiAlias = true
        paint.isDither = true
        paint.color = paintColor
        paint.style = Paint.Style.STROKE
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = 12F
        return paint
    }

    fun setImBitmap(imBitmap: Bitmap) {
        this.imBitmap = imBitmap
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap!!)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawColor(0x0000000)
        canvas?.drawBitmap(imBitmap!!, 0F, 0F, mBitmapPaint)
        canvas?.drawBitmap(mBitmap!!, 0F, 0F, mBitmapPaint)
        canvas?.drawPath(mPath, paint)
    }

    private var mX: Float = 0F
    private var mY: Float = 0F


    fun touchStart(x: Float, y: Float){
        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
        mY = y
    }

    fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if(dx > TOUCH_TOLERANCE || dy > TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2)
            mX = x
            mY = y
        }
    }

    private fun touch_up() {
        mPath.lineTo(mX, mY)
        // commit the path to our offscreen
        mCanvas!!.drawPath(mPath, paint)
        val bmp = Bitmap.createBitmap(
            imBitmap!!.width,
            imBitmap!!.height,
            Bitmap.Config.ARGB_8888,
            true)
        extractForegroundViewListener.markBackground(mBitmap!!)
        // kill this so we don't double draw
        mPath.reset()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y
        if(x == null || y == null){
            return false
        }
        if (!this.isEnabled) {
            return false
        }
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                touch_up()
                invalidate()
            }
        }
        return true
    }

    override fun setEnabled(enabled: Boolean) {
        Log.d(TAG, "setEnabled: $enabled")
        if (!enabled) {
            mCanvas?.drawARGB(100,255,255,255)
        }
        super.setEnabled(enabled)

    }

    fun setExtractForegroundListener(extractForegroundViewListener: ExtractForegroundListener){
        this.extractForegroundViewListener = extractForegroundViewListener
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
    }


}
