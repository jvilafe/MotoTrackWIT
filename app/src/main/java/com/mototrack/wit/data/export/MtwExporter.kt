package com.mototrack.wit.data.export

import com.mototrack.wit.data.db.RouteEntity
import com.mototrack.wit.data.db.SampleEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.io.DataOutputStream
import java.util.zip.GZIPOutputStream

@Serializable
data class MtwHeader(
    val version: Int = 1,
    val name: String,
    val startedAt: Long,
    val endedAt: Long?,
    val sampleCount: Int,
    val fields: List<String> = listOf(
        "t","lat","lon","alt","vGps","bearing","hAcc",
        "ax","ay","az","gx","gy","gz","roll","pitch","yaw","gMag")
)

object MtwExporter {
    fun write(file: File, route: RouteEntity, samples: List<SampleEntity>) {
        val header = MtwHeader(name = route.name, startedAt = route.startedAt,
            endedAt = route.endedAt, sampleCount = samples.size)
        val headerBytes = Json.encodeToString(MtwHeader.serializer(), header).toByteArray()
        DataOutputStream(GZIPOutputStream(file.outputStream())).use { out ->
            out.writeBytes("MTW1")
            out.writeInt(headerBytes.size)
            out.write(headerBytes)
            out.writeInt(samples.size)
            for (s in samples) {
                out.writeLong(s.t)
                out.writeDouble(s.lat); out.writeDouble(s.lon); out.writeDouble(s.alt)
                out.writeFloat(s.vGps); out.writeFloat(s.bearing); out.writeFloat(s.hAcc)
                out.writeFloat(s.ax); out.writeFloat(s.ay); out.writeFloat(s.az)
                out.writeFloat(s.gx); out.writeFloat(s.gy); out.writeFloat(s.gz)
                out.writeFloat(s.roll); out.writeFloat(s.pitch); out.writeFloat(s.yaw)
                out.writeFloat(s.gMag)
            }
        }
    }

    fun writeCsv(file: File, samples: List<SampleEntity>) {
        file.bufferedWriter().use { w ->
            w.write("t,lat,lon,alt,vGps,bearing,hAcc,ax,ay,az,gx,gy,gz,roll,pitch,yaw,gMag\n")
            for (s in samples) {
                w.write("${s.t},${s.lat},${s.lon},${s.alt},${s.vGps},${s.bearing},${s.hAcc}," +
                    "${s.ax},${s.ay},${s.az},${s.gx},${s.gy},${s.gz},${s.roll},${s.pitch},${s.yaw},${s.gMag}\n")
            }
        }
    }

    fun writeGpx(file: File, route: RouteEntity, samples: List<SampleEntity>) {
        file.bufferedWriter().use { w ->
            w.write("""<?xml version="1.0" encoding="UTF-8"?>""" + "\n")
            w.write("""<gpx version="1.1" creator="MotoTrackWIT" xmlns="http://www.topografix.com/GPX/1/1">""" + "\n")
            w.write("<trk><name>${route.name}</name><trkseg>\n")
            for (s in samples) {
                w.write("""<trkpt lat="${s.lat}" lon="${s.lon}"><ele>${s.alt}</ele></trkpt>""" + "\n")
            }
            w.write("</trkseg></trk></gpx>\n")
        }
    }
}
