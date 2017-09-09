package gd.not.lightshow.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.support.v4.graphics.ColorUtils
import android.support.v4.view.GestureDetectorCompat
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

/**
 * Created by alex on 09/09/2017.
 */
class BounceView : View {

  private val TAG = "BounceView"

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  private val paintDot = Paint()
  private val paintBg = Paint()
  private var touching = false
  private var touchedX = 0F
  private var touchedY = 0F
  private var touchStart = 0L
  private var dotSize = 0
  var listener: OnBounceListener? = null

  val gestureDetector = GestureDetectorCompat(context, object : GestureDetector.OnGestureListener {
    override fun onShowPress(p0: MotionEvent?) {
    }

    override fun onSingleTapUp(p0: MotionEvent?): Boolean {
      return false
    }

    override fun onDown(p0: MotionEvent?): Boolean {
      return false
    }

    override fun onFling(p0: MotionEvent?, p1: MotionEvent?, vX: Float, vY: Float): Boolean {
      if (p0 != null && p1 != null) {
        val position = Math.floor(((p1.y / height) * 288).toDouble()).toInt()

        // vY is pixels per second along the y axis
        val velocity = (vY / height) / 5

        listener?.onBounce(paintDot.color, dotSize, withinBounds(position), velocity)
      }
      return true
    }

    override fun onScroll(p0: MotionEvent?, p1: MotionEvent?, p2: Float, p3: Float): Boolean {
      return false
    }

    override fun onLongPress(p0: MotionEvent?) {
    }
  })

  init {
    paintDot.isAntiAlias = true
    paintBg.isAntiAlias = true

    Log.d(TAG, "bounceview init")

  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    @Suppress("DUPLICATE_LABEL_IN_WHEN")
    when (event?.actionMasked) {
      MotionEvent.ACTION_DOWN -> {
        val hue = 360 * event.x / width
        Log.d(TAG, "pointer down with hue: $hue")
        paintDot.color = ColorUtils.HSLToColor(floatArrayOf(hue, 1F, 0.5F))
        touchStart = System.currentTimeMillis()
        dotSize = 0
      }
      MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
        touchedX = event.x
        touchedY = event.y
        touching = true
        dotSize = Math.min(20, 1 + ((System.currentTimeMillis() - touchStart) / 200)).toInt()
        listener?.onDotPlaced(paintDot.color, dotSize, withinBounds((touchedY / height * 288).toInt()))
        invalidate()
      }
      MotionEvent.ACTION_UP -> {
        touching = false
        invalidate()
      }
    }
    gestureDetector.onTouchEvent(event)
    return true
  }

  override fun onDraw(canvas: Canvas?) {
    if (canvas != null) {
      val color = floatArrayOf(0F, 1F, 0.5F)
      val w = canvas.width / 288F
      for (i in 0..288) {
        color[0] = (i / 288F) * 360F
        paintBg.color = ColorUtils.setAlphaComponent(ColorUtils.HSLToColor(color), 100)
        canvas.drawRect(i * w, 0F, (i * w) + w, canvas.height.toFloat(), paintBg)
      }

      if (touching) {
        canvas.drawCircle(touchedX, touchedY, dotSize * 20F, paintDot)
      }
    }
  }

  fun withinBounds(position: Int): Int {
    return Math.min(Math.max(position, 0), 288)
  }

  interface OnBounceListener {
    fun onDotPlaced(color: Int, size: Int, position: Int)
    fun onBounce(color: Int, size: Int, position: Int, velocity: Float)
  }
}