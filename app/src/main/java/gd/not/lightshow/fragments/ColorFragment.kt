package gd.not.lightshow.fragments

import android.view.View
import android.widget.SeekBar
import gd.not.lightshow.R
import gd.not.lightshow.views.ColorPicker
import org.jetbrains.anko.find
import android.graphics.PorterDuff
import android.util.Log
import gd.not.lightshow.LightShowService


/**
 * Created by alex on 26/08/2017.
 */
class ColorFragment : LightshowFragment() {
  private val TAG = "ColorFragment"

  var seekBar: SeekBar? = null

  override fun getLayoutId(): Int = R.layout.fragment_color

  override fun createView(view: View) {
    val colorPicker = view.find<ColorPicker>(R.id.colorPicker)
    seekBar = view.find(R.id.seekBar)

    colorPicker.setOnColorPickedListener(object : ColorPicker.OnColorPickedListener {
      override fun onColorPicked(color: Int) {
        seekBar?.progressDrawable?.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        service?.color = color
//        mainActivity?.setColor(color)
      }
    })

    seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onStartTrackingTouch(p0: SeekBar?) {
      }

      override fun onStopTrackingTouch(p0: SeekBar?) {
      }

      override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
          service?.brightness = progress
        }
      }
    })
  }

  override fun onStateAvailable(service: LightShowService, color: Int, brightness: Int, speedFactor: Float) {
    Log.d(TAG, "state available! brightness=$brightness")
    seekBar?.setProgress(brightness, false)
  }
}