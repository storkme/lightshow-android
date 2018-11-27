package gd.not.lightshow

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutput
import java.io.DataOutputStream
import java.net.*

/**
 * Created by alex on 26/08/2017.
 */


class LightShowService : Service() {
  private val TAG = "LightShowService"
  private val ID_BRIGHTNESS: Byte = 100
  private val ID_QUERY_STATE: Byte = 120
  private val ID_QUERY_SPEED: Byte = 121
  private val ID_FLAT_COLOR: Byte = 101
  private val ID_DOT: Byte = 102
  private val ID_BOUNCE_START: Byte = 103
  private val ID_BOUNCE_STOP: Byte = 104
  private val ID_BOUNCE_ADD: Byte = 105
  private val ID_BOUNCE_CLEAR: Byte = 106
  private val ID_SET_SPEED: Byte = 107

  private val dest = InetSocketAddress("192.168.1.3", 43594)
  private val binder = LocalBinder()
  private var socket: DatagramSocket? = null
  var stateAvailableCallback: OnStateAvailableCallback? = null
  var stateAvailable = false

  private var _brightness = 0
  var brightness: Int
    get() {
      return _brightness
    }
    set(level) {
      bg {
        writeData(byteArrayOf(ID_BRIGHTNESS, level.toByte()))
        _brightness = level
      }
    }

  private var _color = 0
  var color: Int
    get() {
      return _color
    }
    set(color) {
      bg {
        writeData(byteArrayOf(ID_FLAT_COLOR, 0x00, (color shr 16).toByte(), (color shr 8).toByte(), color.toByte()))
        _color = color
      }
    }

  private var _speedFactor = 0F
  var speedFactor: Float
    get() {
      return _speedFactor
    }
    set(speed) {
      bg {
        val out = ByteArrayOutputStream(5)
        val strim = DataOutputStream(out)
        strim.writeByte(ID_SET_SPEED.toInt())
        strim.writeFloat(speed)
        writeData(out.toByteArray())
        _speedFactor = speed
      }
    }

  override fun onBind(p0: Intent?): IBinder = binder

  override fun onCreate() {
    super.onCreate()
    socket = DatagramSocket(43594)

    bg {
      query()
      stateAvailable = true
      stateAvailableCallback?.onStateAvailable(this, _color, _brightness, _speedFactor)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    socket?.close()
    socket?.disconnect()
  }

  fun query() {
    writeData(byteArrayOf(ID_QUERY_STATE))
    val buf = ByteArray(10)
    val result = DatagramPacket(buf, 10)

    socket?.receive(result)

    _brightness = buf[1].toInt() and 0xff
    val strim = DataInputStream(buf.inputStream(2, 8))
    _color = strim.readInt()
    _speedFactor = strim.readFloat()

    strim.close()
  }

  fun dot(color: Int, position: Int, size: Int) {
    writeData(byteArrayOf(ID_DOT, (color shr 24).toByte(), (color shr 16).toByte(), (color shr 8).toByte(), color.toByte(), (position shr 8).toByte(), position.toByte(), size.toByte()))
  }

  fun bounce(start: Boolean = true) {
    writeData(byteArrayOf(if (start) ID_BOUNCE_START else ID_BOUNCE_STOP))
  }

  fun clearDots() {
    writeData(byteArrayOf(ID_BOUNCE_CLEAR))
  }

  fun addDot(color: Int, position: Int, velocity: Float, size: Int) {
    val baos = ByteArrayOutputStream(12)
    val strim = DataOutputStream(baos)
    strim.writeByte(ID_BOUNCE_ADD.toInt())
    strim.writeInt(color) //color
    strim.writeShort(position) //start position
    strim.writeFloat(velocity) //velocity
    strim.writeByte(size)

    writeData(baos.toByteArray())
  }

  private fun writeData(buf: ByteArray) {
    bg { socket!!.send(DatagramPacket(buf, buf.size, dest)) }
  }

  inner class LocalBinder : Binder() {
    fun getService(): LightShowService = this@LightShowService
  }

  interface OnStateAvailableCallback {

    fun onStateAvailable(service: LightShowService, color: Int, brightness: Int, speedFactor: Float)
  }
}