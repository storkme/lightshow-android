package gd.not.lightshow.fragments

import gd.not.lightshow.LightShowService

interface LightshowFragment {
  fun onServiceConnected(service: LightShowService)
}