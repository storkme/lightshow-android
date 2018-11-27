package gd.not.lightshow

import android.app.Application
import android.content.Context
import android.graphics.Bitmap

class LightShowApp : Application() {
  public val bitmapCache: LinkedHashMap<String, Bitmap> = LinkedHashMap()
  private val TAG = "LightShowApp"

  override fun onCreate() {
    super.onCreate()
  }

  fun from(c: Context): LightShowApp {
    return c.applicationContext as LightShowApp
  }
}