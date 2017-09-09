package gd.not.lightshow.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import gd.not.lightshow.MainActivity
import gd.not.lightshow.R
import gd.not.lightshow.views.BounceView
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.find
import org.jetbrains.anko.sdk25.coroutines.onClick

class BounceFragment : Fragment(), BounceView.OnBounceListener {
  private val TAG = "BounceFragment"

  var mainActivity: MainActivity? = null

  override fun onStart() {
    super.onStart()
    mainActivity = activity as MainActivity
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val view = inflater?.inflate(R.layout.fragment_bounce, container, false)
    val bounceView = view?.find<BounceView>(R.id.bouncer)
    bounceView?.listener = this

    view?.find<Button>(R.id.button_reset)?.onClick {
      bg { mainActivity?.service?.clearDots() }
    }

    return view
  }

  override fun onDotPlaced(color: Int, size: Int, position: Int) {
    Log.d(TAG, "onDotPlaced at $position (size: $size)")
    bg {
      mainActivity?.service?.dot(color, position, size)
    }
  }

  override fun onBounce(color: Int, size: Int, position: Int, velocity: Float) {
    Log.d(TAG, "onBounce at $position (size: $size) with velocity $velocity")
    bg {
      mainActivity?.service?.addDot(color, position, velocity, size)
    }
  }
}