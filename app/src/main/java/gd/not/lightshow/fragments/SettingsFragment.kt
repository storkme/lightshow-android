package gd.not.lightshow.fragments

import android.os.Bundle
import android.preference.PreferenceFragment
import gd.not.lightshow.R

abstract class SettingsFragment : PreferenceFragment() {
  public val PREF_HOST = "pref_host"
  public val PREF_PORT = "pref_port"
  private val TAG = "SettingsFragment"

  override fun onCreate(savedState: Bundle) {
    super.onCreate(savedState)

    addPreferencesFromResource(R.xml.preferences)
  }
}