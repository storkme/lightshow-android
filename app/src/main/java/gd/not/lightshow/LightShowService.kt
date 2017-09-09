package gd.not.lightshow

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
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
  private val ID_FLAT_COLOR: Byte = 101
  private val ID_DOT: Byte = 102
  private val ID_BOUNCE_START: Byte = 103
  private val ID_BOUNCE_STOP: Byte = 104
  private val ID_BOUNCE_ADD: Byte = 105
  private val ID_BOUNCE_CLEAR: Byte = 106

  private val dest = InetSocketAddress("192.168.1.3", 43594)
  private val binder = LocalBinder()
  private var socket: DatagramSocket? = null

  override fun onBind(p0: Intent?): IBinder = binder

  override fun onCreate() {
    super.onCreate()
    socket = DatagramSocket(43594)
  }

  override fun onDestroy() {
    super.onDestroy()
    socket?.close()
    socket?.disconnect()
  }

  fun queryState(): Pair<Int, Int> {
    writeData(byteArrayOf(ID_QUERY_STATE))
    val buf = ByteArray(6)
    val result = DatagramPacket(buf, 6)

    socket?.receive(result)
    val brightness = buf[1].toInt() and 0xff
    val color = DataInputStream(buf.inputStream(2, 42)).readInt()

    Log.d(TAG, "well we got our color values: ${buf[2]} ${buf[3]} ${buf[4]} ${buf[5]}")

    return Pair(brightness, color.toInt())
  }


  fun setBrightness(level: Byte) {
//    Log.d(TAG, "starting request to " + ENDPOINT)
//    val urlConnection = URL(ENDPOINT).openConnection()
//    val result = urlConnection.getInputStream().bufferedReader().use { it.readText() }
//    Log.d(TAG, result)

    writeData(byteArrayOf(ID_BRIGHTNESS, level))
  }

  fun setColor(color: Int) {
    Log.d(TAG, "setting color to ${(color shr 24).toByte()},${(color shr 16).toByte()},${(color shr 8).toByte()}, ${color.toByte()}")
    writeData(byteArrayOf(ID_FLAT_COLOR, (color shr 24).toByte(), (color shr 16).toByte(), (color shr 8).toByte(), color.toByte()))
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
    if (socket == null) {
      Log.w(TAG, "tried to write data to a null socket :S :S :S: S:S:S :S :S ")
    }
    socket?.send(DatagramPacket(buf, buf.size, dest))
  }

  inner class LocalBinder : Binder() {
    fun getService(): LightShowService = this@LightShowService
  }
}