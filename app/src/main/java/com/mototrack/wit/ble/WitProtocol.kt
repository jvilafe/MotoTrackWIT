package com.mototrack.wit.ble

import java.util.UUID

/**
 * Protocolo BLE WitMotion WT901BLECL5.0
 * Doc oficial: https://wit-motion.gitbook.io/witmotion-sdk/ble-5.0-protocol/sdk
 *
 * UUIDs (BLE 5.0 series):
 *  - Service:        0000FFE5-0000-1000-8000-00805F9A34FB
 *  - Notify (read):  0000FFE4-0000-1000-8000-00805F9A34FB
 *  - Write:          0000FFE9-0000-1000-8000-00805F9A34FB
 *  - CCC descriptor: 00002902-0000-1000-8000-00805F9B34FB
 *
 * Frame combinado 0x55 0x61 (20 bytes): timestamp único con accel + gyro + ángulos.
 * Layout (little-endian Int16 cada par):
 *  [0]=0x55 [1]=0x61
 *  [2..3]=Ax  [4..5]=Ay  [6..7]=Az      (escala /32768 * 16 g)
 *  [8..9]=Wx  [10..11]=Wy [12..13]=Wz   (/32768 * 2000 °/s)
 *  [14..15]=Roll [16..17]=Pitch [18..19]=Yaw  (/32768 * 180 °)
 */
object WitProtocol {
    val SERVICE_UUID: UUID = UUID.fromString("0000ffe5-0000-1000-8000-00805f9a34fb")
    val CHAR_NOTIFY: UUID  = UUID.fromString("0000ffe4-0000-1000-8000-00805f9a34fb")
    val CHAR_WRITE:  UUID  = UUID.fromString("0000ffe9-0000-1000-8000-00805f9a34fb")
    val CCC_DESCRIPTOR: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private const val G = 16f
    private const val DPS = 2000f
    private const val ANG = 180f

    data class Sample(
        val ax: Float, val ay: Float, val az: Float,   // g
        val gx: Float, val gy: Float, val gz: Float,   // °/s
        val roll: Float, val pitch: Float, val yaw: Float // °
    )

    /** Devuelve null si el frame no es 0x55 0x61 o checksum/longitud inválida. */
    fun parseFrame(buf: ByteArray, offset: Int = 0): Sample? {
        if (buf.size - offset < 20) return null
        if (buf[offset].toInt() and 0xFF != 0x55) return null
        if (buf[offset + 1].toInt() and 0xFF != 0x61) return null
        fun s16(i: Int): Int {
            val lo = buf[offset + i].toInt() and 0xFF
            val hi = buf[offset + i + 1].toInt()
            return (hi shl 8) or lo
        }
        val ax = s16(2).toShort() / 32768f * G
        val ay = s16(4).toShort() / 32768f * G
        val az = s16(6).toShort() / 32768f * G
        val gx = s16(8).toShort() / 32768f * DPS
        val gy = s16(10).toShort() / 32768f * DPS
        val gz = s16(12).toShort() / 32768f * DPS
        val roll  = s16(14).toShort() / 32768f * ANG
        val pitch = s16(16).toShort() / 32768f * ANG
        val yaw   = s16(18).toShort() / 32768f * ANG
        return Sample(ax, ay, az, gx, gy, gz, roll, pitch, yaw)
    }

    /** Procesa un buffer que puede contener varios frames consecutivos. */
    fun parseBuffer(buf: ByteArray): List<Sample> {
        val out = ArrayList<Sample>(buf.size / 20 + 1)
        var i = 0
        while (i + 20 <= buf.size) {
            val s = parseFrame(buf, i)
            if (s != null) { out.add(s); i += 20 } else i++
        }
        return out
    }

    // ===== Comandos de configuración =====
    // Header de comando: 0xFF 0xAA REG DATA_L DATA_H
    private fun cmd(reg: Int, data: Int): ByteArray =
        byteArrayOf(0xFF.toByte(), 0xAA.toByte(), reg.toByte(),
            (data and 0xFF).toByte(), ((data shr 8) and 0xFF).toByte())

    /** Reg 0x03 RRATE; 0x08 = 50 Hz, 0x09 = 100 Hz, 0x0B = 200 Hz */
    fun cmdSetRate50Hz(): ByteArray = cmd(0x03, 0x08)
    /** Reg 0x02 RSW: bit2 (accel)+bit3 (ang vel)+bit4 (angle) → 0x1C  */
    fun cmdEnableOutputs(): ByteArray = cmd(0x02, 0x1C)
    /** Reg 0x00 SAVE: 0x00 guardar, 0x01 reset por defecto */
    fun cmdSave(): ByteArray = cmd(0x00, 0x00)
}
