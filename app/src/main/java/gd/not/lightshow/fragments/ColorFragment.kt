package gd.not.lightshow.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import gd.not.lightshow.MainActivity
import gd.not.lightshow.R
import gd.not.lightshow.views.ColorPicker
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.find
import android.graphics.PorterDuff
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import gd.not.lightshow.LightShowService
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async


/**
 * Created by alex on 26/08/2017.
 */
class ColorFragment : Fragment(), LightshowFragment {
  private val TAG = "ColorFragment"

  var mainActivity: MainActivity? = null
  var seekBar: SeekBar? = null


  override fun onStart() {
    super.onStart()
    mainActivity = activity as MainActivity
    mainActivity!!.selectedFragment = this
    if (mainActivity!!.service != null) {
      queryState(mainActivity!!.service!!)
    }
  }

  override fun onStop() {
    super.onStop()
    mainActivity!!.selectedFragment = null
  }

  override fun onServiceConnected(service: LightShowService) {
    queryState(service)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater?.inflate(R.layout.fragment_color, container, false)

    val colorPicker = view?.find<ColorPicker>(R.id.colorPicker)
    seekBar = view?.find(R.id.seekBar)

    colorPicker?.setOnColorPickedListener(object : ColorPicker.OnColorPickedListener {
      override fun onColorPicked(color: Int) {
        Log.d(TAG, "color picked! $color")

        seekBar?.progressDrawable?.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        mainActivity?.setColor(color)
      }
    })


    seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onStartTrackingTouch(p0: SeekBar?) {
      }

      override fun onStopTrackingTouch(p0: SeekBar?) {
      }

      override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        bg {
          mainActivity?.service?.setBrightness((p1.toFloat() * 2.55).toByte())
        }
      }
    })

    return view
  }

  fun queryState(service:LightShowService) {
    async(UI) {
      val data: Deferred<Pair<Int, Int>> = bg {
        service.queryState()
      }

      // This code is executed on the UI thread
      val result = data.await()
      Log.d(TAG, "read brightness as: ${result.first}, color as: ${result.second}")
      seekBar?.setProgress(((result.first / 255F) * 100F).toInt(), true)
      mainActivity?.setColor(result.second, false)
    }
  }
}