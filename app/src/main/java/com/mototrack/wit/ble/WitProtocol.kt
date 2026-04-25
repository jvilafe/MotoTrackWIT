package com.mototrack.wit.ble

import android.os.SystemClock
import java.util.UUID

object WitProtocol {

    val SERVICE_UUID:   UUID = UUID.fromString("0000ffe5-0000-1000-8000-00805f9a34fb")
    val CHAR_NOTIFY:    UUID = UUID.fromString("0000ffe4-0000-1000-8000-00805f9a34fb")
    val CHAR_WRITE:     UUID = UUID.fromString("0000ffe9-0000-1000-8000-00805f9a34fb")
    val CCC_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private const val ACC_RANGE_G = 16f
    private const val GYRO_RANGE  = 2000f

    data class Sample(
        val tMono: Long = SystemClock.elapsedRealtime(),
        val ax: Float = 0f, val ay: Float = 0f, val az: Float = 0f,
        val wx: Float = 0f, val wy: Float = 0f, val wz: Float = 0f,
        val roll: Float = 0f, val pitch: Float = 0f, val yaw: Float = 0f,
        val hx: Float = 0f, val hy: Float = 0f, val hz: Float = 0f,
        val temp: Float = 0f,
        val pressurePa: Int? = null,
        val altitudeM: Float? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val altitude: Float? = null,
        val groundSpeedKmh: Float? = null,
        val batteryPct: Int? = null
    )

    private val queue = ArrayDeque<Byte>()
    private var current = Sample()

    private fun s16(lo: Int, hi: Int): Int =
        (((hi and 0xff) shl 8) or (lo and 0xff)).toShort().toInt()

    private fun s32(b0: Int, b1: Int, b2: Int, b3: Int): Int =
        ((b3 and 0xff) shl 24) or ((b2 and 0xff) shl 16) or ((b1 and 0xff) shl 8) or (b0 and 0xff)

    @Synchronized
    fun parseBuffer(data: ByteArray): List<Sample> {
        val out = mutableListOf<Sample>()
        for (b in data) queue.addLast(b)

        while (queue.size >= 2) {
            val first = queue.first()
            if (first != 0x55.toByte()) { queue.removeFirst(); continue }
            if (queue.size < 2) break
            val second = queue.elementAt(1)

            when {
                // ---- WT901BLE5.0 extendido: 0x55 0xEF, 54 bytes ----
                second == 0xEF.toByte() -> {
                    if (queue.size < 54) return out
                    val p = ByteArray(54)
                    for (i in 0 until 54) p[i] = queue.removeFirst()

                    fun i16(o: Int) = s16(p[o].toInt(), p[o + 1].toInt())
                    fun i32(o: Int) = s32(p[o].toInt(), p[o + 1].toInt(), p[o + 2].toInt(), p[o + 3].toInt())

                    val ax = i16(10) / 32768f * ACC_RANGE_G
                    val ay = i16(12) / 32768f * ACC_RANGE_G
                    val az = i16(14) / 32768f * ACC_RANGE_G
                    val wx = i16(16) / 32768f * GYRO_RANGE
                    val wy = i16(18) / 32768f * GYRO_RANGE
                    val wz = i16(20) / 32768f * GYRO_RANGE
                    val hx = i16(22).toFloat()
                    val hy = i16(24).toFloat()
                    val hz = i16(26).toFloat()
                    val rl = i16(28) / 32768f * 180f
                    val pt = i16(30) / 32768f * 180f
                    val yw = i16(32) / 32768f * 180f
                    val press = i32(34)
                    val altCm = i32(38)

                    current = current.copy(
                        ax = ax, ay = ay, az = az,
                        wx = wx, wy = wy, wz = wz,
                        hx = hx, hy = hy, hz = hz,
                        roll = rl, pitch = pt, yaw = yw,
                        pressurePa = press,
                        altitudeM = altCm / 100f,
                        tMono = SystemClock.elapsedRealtime()
                    )
                    out.add(current)
                }

                // ---- BLE 5.0 estándar: 0x55 0x61, 20 bytes ----
                second == 0x61.toByte() -> {
                    if (queue.size < 20) return out
                    val p = ByteArray(20)
                    for (i in 0 until 20) p[i] = queue.removeFirst()
                    fun i16(o: Int) = s16(p[o].toInt(), p[o + 1].toInt())
                    current = current.copy(
                        ax = i16(2)  / 32768f * ACC_RANGE_G,
                        ay = i16(4)  / 32768f * ACC_RANGE_G,
                        az = i16(6)  / 32768f * ACC_RANGE_G,
                        wx = i16(8)  / 32768f * GYRO_RANGE,
                        wy = i16(10) / 32768f * GYRO_RANGE,
                        wz = i16(12) / 32768f * GYRO_RANGE,
                        roll  = i16(14) / 32768f * 180f,
                        pitch = i16(16) / 32768f * 180f,
                        yaw   = i16(18) / 32768f * 180f,
                        tMono = SystemClock.elapsedRealtime()
                    )
                    out.add(current)
                }

                // ---- Legacy 0x55 0x5X, 11 bytes con checksum ----
                (second.toInt() and 0xF0) == 0x50 -> {
                    if (queue.size < 11) return out
                    val p = ByteArray(11)
                    for (i in 0 until 11) p[i] = queue.removeFirst()
                    var sum = 0
                    for (i in 0..9) sum = (sum + (p[i].toInt() and 0xff)) and 0xff
                    if (sum.toByte() != p[10]) continue
                    val f = FloatArray(4)
                    for (i in 0..3) f[i] = s16(p[2 + i*2].toInt(), p[3 + i*2].toInt()).toFloat()
                    when (p[1]) {
                        0x51.toByte() -> current = current.copy(
                            ax = f[0]/32768f*ACC_RANGE_G, ay = f[1]/32768f*ACC_RANGE_G,
                            az = f[2]/32768f*ACC_RANGE_G, temp = f[3]/100f
                        )
                        0x52.toByte() -> current = current.copy(
                            wx = f[0]/32768f*GYRO_RANGE, wy = f[1]/32768f*GYRO_RANGE, wz = f[2]/32768f*GYRO_RANGE
                        )
                        0x53.toByte() -> {
                            current = current.copy(
                                roll = f[0]/32768f*180f, pitch = f[1]/32768f*180f, yaw = f[2]/32768f*180f,
                                tMono = SystemClock.elapsedRealtime()
                            )
                            out.add(current)
                        }
                        0x54.toByte() -> current = current.copy(hx = f[0], hy = f[1], hz = f[2])
                    }
                }

                else -> { queue.removeFirst() }
            }
        }
        return out
    }

    // ---------- Comandos ----------
    fun cmdUnlock(): ByteArray =
        byteArrayOf(0xFF.toByte(), 0xAA.toByte(), 0x69, 0x88.toByte(), 0xB5.toByte())

    fun cmdEnableOutputs(): ByteArray = cmdWriteReg(0x02, 0x001E)
    fun cmdSetRate50Hz():  ByteArray = cmdWriteReg(0x03, 0x0008)
    fun cmdSetRate100Hz(): ByteArray = cmdWriteReg(0x03, 0x0009)
    fun cmdReadBattery(): ByteArray =
        byteArrayOf(0xFF.toByte(), 0xAA.toByte(), 0x27, 0x64, 0x00)
    fun cmdSave(): ByteArray = cmdWriteReg(0x00, 0x0000)

    /** Cambia entre 6-ejes (solo acelerómetro/giroscopio) y 9-ejes (incluye magnetómetro).
     * Para motos se recomienda 6-ejes (0x01) para evitar interferencias magnéticas. */
    fun cmdSetAlgorithm(sixAxis: Boolean): ByteArray = cmdWriteReg(0x24, if (sixAxis) 0x01 else 0x00)

    /** Calibración de acelerómetro (Z-axis). El sensor debe estar quieto y nivelado. */
    fun cmdCalibrateAccel(): ByteArray = cmdWriteReg(0x01, 0x0001)

    /** Calibración de magnetómetro. Inicia el proceso (girar el sensor en todos los ejes). */
    fun cmdCalibrateMag(): ByteArray = cmdWriteReg(0x01, 0x0002)

    private fun cmdWriteReg(addr: Int, value: Int): ByteArray = byteArrayOf(
        0xFF.toByte(), 0xAA.toByte(),
        (addr and 0xFF).toByte(),
        (value and 0xFF).toByte(),
        ((value shr 8) and 0xFF).toByte()
    )
}
