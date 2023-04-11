package gd.not.lightshow.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import gd.not.lightshow.LightShowService
import gd.not.lightshow.R
import gd.not.lightshow.databinding.FragmentBounceBinding
import gd.not.lightshow.views.BounceView
import org.jetbrains.anko.find

class BounceFragment : LightshowFragment(), BounceView.OnBounceListener {
  private val TAG = "BounceFragment"
  private var _binding: FragmentBounceBinding? = null
  private val binding get() = _binding!!

  override fun getLayoutId(): Int = R.layout.fragment_bounce
  override fun createView(view: View) {
    TODO("Not yet implemented")
  }

  override fun onCreateView(
    inflater: LayoutInflater?,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentBounceBinding.inflate(inflater!!, container, false)

    binding.bouncer.listener = this

    binding.buttonReset.setOnClickListener {
      service?.clearDots()
    }

    binding.buttonStart.setOnClickListener {
      service?.bounce(true)
    }

    binding.speed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
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

    return binding.root
  }

  override fun onStateAvailable(service: LightShowService, color: Int, brightness: Int, speedFactor: Float) {
    onSpeedSet(speedFactor, true)
  }

  override fun onDotPlaced(color: Int, size: Int, position: Int) {
    Log.d(TAG, "dot placed bitch")
//    mainActivity?.setColor(color, false)
    binding.speed.progressDrawable?.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    service?.dot(color, position, size)
  }

  override fun onBounce(color: Int, size: Int, position: Int, velocity: Float) {
    service?.addDot(color, position, velocity, size)
  }

  fun onSpeedSet(speed: Float, setProgress: Boolean = false) {
    binding.seekbarSpeed.text = getString(R.string.speed, speed)
    if (setProgress) {
      binding.speed.setProgress((speed * 100).toInt(), false)
    }
  }
}