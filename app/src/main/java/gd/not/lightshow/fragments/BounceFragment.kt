package gd.not.lightshow.fragments

import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import gd.not.lightshow.LightShowService
import gd.not.lightshow.R
import gd.not.lightshow.views.BounceView
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

class BounceFragment : LightshowFragment(), BounceView.OnBounceListener {
  private val TAG = "BounceFragment"

  var seekBar: SeekBar? = null
  var seekbarSpeed: TextView? = null

  override fun getLayoutId(): Int = R.layout.fragment_bounce

  override fun createView(view: View) {
    val bounceView = view.find<BounceView>(R.id.bouncer)
    bounceView.listener = this
    seekBar = view.find(R.id.speed)
    seekbarSpeed = view.find(R.id.seekbarSpeed)

    view.find<ImageButton>(R.id.button_reset).onClick {
      service?.clearDots()
    }

    view.find<ImageButton>(R.id.button_start).onClick {
      service?.bounce(true)
    }

    seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onStartTrackingTouch(p0: SeekBar?) {
      }

      override fun onStopTrackingTouch(p0: SeekBar?) {
      }

      override fun onProgressChanged(p0: SeekBar?, p1: Int, fromUser: Boolean) {
        if (fromUser) {
          val speed = p1 * 0.01F
          onSpeedSet(speed, false)
          service?.speedFactor = speed
        }
      }
    })
  }

  override fun onStateAvailable(service: LightShowService, color: Int, brightness: Int, speedFactor: Float) {
    onSpeedSet(speedFactor, true)
  }

  override fun onDotPlaced(color: Int, size: Int, position: Int) {
//    mainActivity?.setColor(color, false)
    seekBar?.progressDrawable?.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    service?.dot(color, position, size)
  }

  override fun onBounce(color: Int, size: Int, position: Int, velocity: Float) {
    service?.addDot(color, position, velocity, size)
  }

  fun onSpeedSet(speed: Float, setProgress: Boolean = false) {
    seekbarSpeed!!.text = getString(R.string.speed, speed)
    if (setProgress) {
      seekBar!!.setProgress((speed * 100).toInt(), false)
    }
  }
}