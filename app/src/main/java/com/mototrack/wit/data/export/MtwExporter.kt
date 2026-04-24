package com.mototrack.wit.data.export

import com.mototrack.wit.data.db.RouteEntity
import com.mototrack.wit.data.db.SampleEntity
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class MtwHeader(
    val version: Int = 1,
    val name: String,
    val createdAt: String,
    val device: String = "",
    val sensor: String = "WT901BLECL5.0",
    val sampleRateHz: Int = 25,
    val numPoints: Int,
    val fields: List<String> = DEFAULT_FIELDS
) {
    companion object {
        val DEFAULT_FIELDS = listOf(
            "t", "lat", "lon", "alt", "speed", "heading",
            "ax", "ay", "az", "roll", "pitch", "yaw"
        )
    }
}

object MtwExporter {

    private const val MAGIC = "MTW1"
    private const val FLOATS_PER_POINT = 12

    private val isoUtc = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun write(
        file: File,
        route: RouteEntity,
        samples: List<SampleEntity>,
        device: String = android.os.Build.MODEL ?: "",
        sampleRateHz: Int = 25,
    ) {
        val header = MtwHeader(
            name = route.name,
            createdAt = isoUtc.format(Date(route.startedAt)),
            device = device,
            sampleRateHz = sampleRateHz,
            numPoints = samples.size,
        )

        val headerBytes = buildHeaderJson(header).toByteArray(Charsets.UTF_8)
        val payloadSize = samples.size * FLOATS_PER_POINT * 4
        val totalSize = 8 + headerBytes.size + payloadSize

        val buffer = ByteBuffer.allocate(totalSize).order(ByteOrder.LITTLE_ENDIAN)
        buffer.put(MAGIC.toByteArray(Charsets.US_ASCII))
        buffer.putInt(headerBytes.size)
        buffer.put(headerBytes)

        val t0 = route.startedAt
        for (s in samples) {
            val tSec = (s.t - t0) / 1000f
            buffer.putFloat(tSec)
            buffer.putFloat(s.lat.toFloat())
            buffer.putFloat(s.lon.toFloat())
            buffer.putFloat(s.alt.toFloat())
            buffer.putFloat(s.vGps * 3.6f) // m/s -> km/h
            buffer.putFloat(s.bearing)
            buffer.putFloat(s.ax)
            buffer.putFloat(s.ay)
            buffer.putFloat(s.az)
            buffer.putFloat(s.roll)
            buffer.putFloat(s.pitch)
            buffer.putFloat(s.yaw)
        }

        RandomAccessFile(file, "rw").use { raf ->
            raf.setLength(0)
            raf.write(buffer.array(), 0, buffer.position())
        }
    }

    fun writeGpx(file: File, route: RouteEntity, samples: List<SampleEntity>) {
        file.bufferedWriter().use { w ->
            w.write("""<?xml version="1.0" encoding="UTF-8"?>""" + "\n")
            w.write("""<gpx version="1.1" creator="MotoTrackWIT" xmlns="http://www.topografix.com/GPX/1/1">""" + "\n")
            w.write("<trk><name>${escapeXml(route.name)}</name><trkseg>\n")
            for (s in samples) {
                val time = isoUtc.format(Date(s.t))
                w.write(
                    """<trkpt lat="${s.lat}" lon="${s.lon}"><ele>${s.alt}</ele><time>$time</time><speed>${s.vGps}</speed></trkpt>""" + "\n"
                )
            }
            w.write("</trkseg></trk></gpx>\n")
        }
    }

    private fun buildHeaderJson(header: MtwHeader): String {
        val fieldsJson = header.fields.joinToString(separator = ",") { "\"${escapeJson(it)}\"" }
        return buildString {
            append("{")
            append("\"version\":").append(header.version).append(",")
            append("\"name\":\"").append(escapeJson(header.name)).append("\",")
            append("\"createdAt\":\"").append(escapeJson(header.createdAt)).append("\",")
            append("\"device\":\"").append(escapeJson(header.device)).append("\",")
            append("\"sensor\":\"").append(escapeJson(header.sensor)).append("\",")
            append("\"sampleRateHz\":").append(header.sampleRateHz).append(",")
            append("\"numPoints\":").append(header.numPoints).append(",")
            append("\"fields\":[").append(fieldsJson).append("]")
            append("}")
        }
    }

    private fun escapeJson(value: String): String {
        val out = StringBuilder(value.length + 16)
        for (ch in value) {
            when (ch) {
                '\\' -> out.append("\\\\")
                '"' -> out.append("\\\"")
                '\b' -> out.append("\\b")
                '\u000C' -> out.append("\\f")
                '\n' -> out.append("\\n")
                '\r' -> out.append("\\r")
                '\t' -> out.append("\\t")
                else -> {
                    if (ch.code < 0x20) {
                        out.append("\\u")
                        out.append(ch.code.toString(16).padStart(4, '0'))
                    } else {
                        out.append(ch)
                    }
                }
            }
        }
        return out.toString()
    }

    private fun escapeXml(value: String): String =
        value.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
}
