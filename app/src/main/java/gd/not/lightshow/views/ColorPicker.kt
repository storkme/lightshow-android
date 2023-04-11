package gd.not.lightshow.views

import android.content.Context
import android.graphics.*
import android.support.v4.graphics.ColorUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class ColorPicker : View {

  constructor(context: Context) : super(context)
  constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

  private val TAG = "ColorPicker"

  val paint = Paint()
  val paintShadow = Paint()
  val hsv = floatArrayOf(0F, 1F, 0.5F)
  var bitmap: Bitmap? = null
  var listener: OnColorPickedListener? = null

  init {
    this.isDrawingCacheEnabled = true

//    paint.isAntiAlias = true

    paintShadow.isAntiAlias = true
//    setLayerType(LAYER_TYPE_SOFTWARE, paintShadow)
  }

  fun setOnColorPickedListener(listener: OnColorPickedListener?) {
    this.listener = listener
  }

  fun drawWheel(canvas: Canvas?, lightness: Float, left: Float, top: Float, right: Float, bottom: Float) {
    val segWidth = 5f
    for (i in 0..71) {
      hsv[0] = i * segWidth
      hsv[2] = lightness
      paint.color = ColorUtils.HSLToColor(hsv)
      paintShadow.color = ColorUtils.HSLToColor(hsv)
//      paintShadow.setShadowLayer(10F, 0F, 0F, ColorUtils.HSLToColor(hsv))
      canvas?.drawArc(left, top, right, bottom, i * segWidth, segWidth, true, paintShadow)
      canvas?.drawArc(left, top, right, bottom, i * segWidth, segWidth, true, paint)
    }
  }

  override fun onDraw(canvas: Canvas?) {
    super.onDraw(canvas)

    val availableSpace = Math.min(
      (width - (paddingLeft + paddingRight)) / 2,
      (height - (paddingTop + paddingBottom)) / 2
    )
    val segRadius = availableSpace / 6F

//    val scales = floatArrayOf(0F, 3F, 2F, 1.5F, 1.25F, 1.125F)
//
//    for (i in 0..5) {
    for (i in 0..0) {
      val left = paddingLeft.toFloat() + (segRadius * i)
      val top = paddingTop.toFloat() + (segRadius * i)
      val right = width.toFloat() - (paddingRight + (segRadius * i))
      val bottom = height.toFloat() - (paddingBottom + (segRadius * i))
      if (i == 5) {
        paint.setARGB(255, 255, 255, 255)
        canvas?.drawOval(left, top, right, bottom, paint)
      } else {
        drawWheel(canvas, 0.5F + i * 0.1F, left, top, right, bottom)
      }
    }

    val left = paddingLeft.toFloat() + (segRadius * 5.5f)
    val top = paddingTop.toFloat() + (segRadius * 10f)
    val right = width.toFloat() - (paddingRight + (segRadius * 5.5f))
    val bottom = height.toFloat() - (paddingBottom + (segRadius * 10f))
    paint.setARGB(255, 255, 255, 255)
    canvas?.drawOval(left, top, right, bottom, paint)

    bitmap = getDrawingCache(true)
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    when (event?.actionMasked) {
      MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
        val x = event.x.toInt()
        val y = event.y.toInt()

        try {
          val pixel = bitmap?.getPixel(event.x.toInt(), event.y.toInt())

          if (pixel != null) {
            this.listener?.onColorPicked(pixel)
          }
        } catch (e: IllegalArgumentException) {
          // ignore
        }
      }
    }
    return true
  }

  interface OnColorPickedListener {
    fun onColorPicked(color: Int)
  }
}