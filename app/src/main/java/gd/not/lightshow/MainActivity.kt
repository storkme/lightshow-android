package gd.not.lightshow

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.graphics.ColorUtils
import android.support.v7.app.AppCompatActivity
import android.util.Log
import gd.not.lightshow.databinding.ActivityMainBinding
import gd.not.lightshow.fragments.BounceFragment
import gd.not.lightshow.fragments.ColorFragment
import gd.not.lightshow.fragments.LightshowFragment


class MainActivity : AppCompatActivity(), LightShowService.OnStateAvailableCallback {
  private val TAG = "MainActivity"

  var selectedFragment: LightshowFragment? = null
  var service: LightShowService? = null

  private lateinit var binding: ActivityMainBinding

  private val mServiceConn = object : ServiceConnection {
    override fun onServiceDisconnected(p0: ComponentName?) {
      service = null
      Log.d(TAG, "Service disconnected")
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
      service = (p1 as LightShowService.LocalBinder).getService()
      service!!.stateAvailableCallback = this@MainActivity
      selectedFragment?.service = service
      Log.d(TAG, "Service connected, selectedFragment=$selectedFragment")
    }
  }

  private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
    when (item.itemId) {
      R.id.navigation_home -> {
        setFragment(ColorFragment())
        return@OnNavigationItemSelectedListener true
      }
      R.id.navigation_bounce -> {
        setFragment(BounceFragment())
        return@OnNavigationItemSelectedListener true
      }
    }
    false
  }

  override fun onStateAvailable(service: LightShowService, color: Int, brightness: Int, speedFactor: Float) {
    Log.d(TAG, "State available, delegating to $selectedFragment")
    selectedFragment?.onStateAvailable(service, color, brightness, speedFactor)
  }

  fun setFragment(frag: LightshowFragment) {
    Log.d(TAG, "setting fragment to $frag")

    supportFragmentManager.beginTransaction()
      .replace(R.id.container, frag)
      .commit()
  }

  fun setColor(color: Int, submit: Boolean? = true) {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(color, hsl)

    // reduce lightness by 50%
    hsl[2] = hsl[2] * 0.5F
    val darkColor = ColorUtils.HSLToColor(hsl)

    window.statusBarColor = darkColor
    window.navigationBarColor = darkColor
    Log.d(TAG, "setting color to $color")
    if (submit == true) {
      service?.color = color
    }
  }

  fun querySpeed() {

  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

    binding = ActivityMainBinding.inflate(layoutInflater)

    setContentView(binding.root)
    binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    setFragment(ColorFragment())
  }

  override fun onStart() {
    super.onStart()

    bindService(Intent(this@MainActivity, LightShowService::class.java), mServiceConn, Context.BIND_AUTO_CREATE)
  }

  override fun onStop() {
    super.onStop()

    unbindService(mServiceConn)
  }
}
