package gd.not.lightshow

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.graphics.ColorUtils
import android.support.v7.app.AppCompatActivity
import android.util.Log
import gd.not.lightshow.fragments.BounceFragment
import gd.not.lightshow.fragments.ColorFragment
import gd.not.lightshow.fragments.LightshowFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.coroutines.experimental.bg


class MainActivity : AppCompatActivity(), AnkoLogger {

  private val TAG = "MainActivity"
  var selectedFragment: LightshowFragment? = null

  private val mServiceConn = object : ServiceConnection {
    override fun onServiceDisconnected(p0: ComponentName?) {
      service = null
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
      service = (p1 as LightShowService.LocalBinder).getService()
      Log.d(TAG, "bound to lameoid service or whatevs")

      selectedFragment?.onServiceConnected(service!!)
    }
  }

  var service: LightShowService? = null

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

  fun setFragment(frag: Fragment) {
    val trans = supportFragmentManager.beginTransaction()
    trans.replace(R.id.container, frag)
    trans.commit()
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
      bg {
        service?.setColor(color)
      }
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
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
