package gd.not.lightshow.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import gd.not.lightshow.LightShowService
import gd.not.lightshow.MainActivity
import gd.not.lightshow.R

abstract class LightshowFragment : Fragment(), LightShowService.OnStateAvailableCallback {
  private val TAG = "LightshowFragment"
  var service: LightShowService? = null

  abstract fun getLayoutId(): Int

  abstract fun createView(view: View)

  override fun onStart() {
    super.onStart()

    Log.d(TAG, "onStart() called")

    setState()
  }

  /**
   * override oncreateview and
   */
  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater!!.inflate(getLayoutId(), container, false)

    createView(view)

    Log.d(TAG, "onCreateView() called")

//    setState()

    return view
  }

  override fun onStop() {
    super.onStop()

    val mainActivity = activity as MainActivity
    mainActivity.selectedFragment = null
    service = null
  }

  private fun setState() {
    val mainActivity = activity as MainActivity
    mainActivity.selectedFragment = this
    service = mainActivity.service


    service?.let { service ->
      Log.d(TAG, "service available, state is available?${service.stateAvailable}")
      if (service.stateAvailable) {
        onStateAvailable(service, service.color, service.brightness, service.speedFactor)
      }
    }
  }
}