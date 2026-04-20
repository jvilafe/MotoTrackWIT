package com.mototrack.wit.data

import com.mototrack.wit.data.db.*
import com.mototrack.wit.fusion.FusedSample
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlin.math.max

@Singleton
class RouteRepository @Inject constructor(
    private val routeDao: RouteDao,
    private val sampleDao: SampleDao,
) {
    fun observeAll(): Flow<List<RouteEntity>> = routeDao.observeAll()
    suspend fun get(id: Long) = routeDao.get(id)
    suspend fun samples(id: Long) = sampleDao.getAll(id)
    suspend fun delete(id: Long) = routeDao.delete(id)

    suspend fun startRoute(name: String): Long =
        routeDao.insert(RouteEntity(name = name, startedAt = System.currentTimeMillis(), endedAt = null))

    suspend fun appendBatch(routeId: Long, batch: List<FusedSample>) {
        sampleDao.insertAll(batch.map {
            SampleEntity(
                routeId = routeId, t = it.t, lat = it.lat, lon = it.lon, alt = it.alt,
                vGps = it.vGps, bearing = it.bearing, hAcc = it.hAcc,
                ax = it.ax, ay = it.ay, az = it.az,
                gx = it.gx, gy = it.gy, gz = it.gz,
                roll = it.roll, pitch = it.pitch, yaw = it.yaw, gMag = it.gMag,
            )
        })
    }

    suspend fun finalizeRoute(routeId: Long) {
        val r = routeDao.get(routeId) ?: return
        val all = sampleDao.getAll(routeId)
        var dist = 0.0; var sumV = 0f; var maxV = 0f
        var maxAccel = 0f; var maxBrake = 0f
        var maxRollL = 0f; var maxRollR = 0f; var maxG = 0f
        for (i in all.indices) {
            val s = all[i]
            if (i > 0) {
                val p = all[i-1]
                dist += haversine(p.lat, p.lon, s.lat, s.lon)
                val dt = (s.t - p.t) / 1000f
                if (dt > 0) {
                    val a = (s.vGps - p.vGps) / dt
                    if (a > maxAccel) maxAccel = a
                    if (-a > maxBrake) maxBrake = -a
                }
            }
            sumV += s.vGps
            maxV = max(maxV, s.vGps)
            if (s.roll > maxRollL) maxRollL = s.roll
            if (-s.roll > maxRollR) maxRollR = -s.roll
            maxG = max(maxG, s.gMag)
        }
        val avg = if (all.isNotEmpty()) sumV / all.size else 0f
        routeDao.update(r.copy(
            endedAt = System.currentTimeMillis(),
            distanceM = dist, maxSpeed = maxV, avgSpeed = avg,
            maxAccel = maxAccel, maxBrake = maxBrake,
            maxRollLeft = maxRollL, maxRollRight = maxRollR, maxG = maxG,
        ))
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat/2).let { it*it } +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon/2).let { it*it }
        return 2 * r * kotlin.math.asin(kotlin.math.sqrt(a))
    }
}
