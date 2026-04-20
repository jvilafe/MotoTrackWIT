package com.mototrack.wit.drive

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mototrack.wit.data.RouteRepository
import com.mototrack.wit.data.export.MtwExporter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class UploadWorker @AssistedInject constructor(
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
    private val repo: RouteRepository,
    private val drive: DriveSync,
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val routeId = inputData.getLong("routeId", -1L)
        if (routeId <= 0) return Result.failure()
        val route = repo.get(routeId) ?: return Result.failure()
        val samples = repo.samples(routeId)
        val tmp = File(applicationContext.cacheDir, "${route.name}.mtw")
        MtwExporter.write(tmp, route, samples)
        val df = SimpleDateFormat("yyyy-MM-dd_HH-mm", Locale.US).format(Date(route.startedAt))
        val name = "${route.name}_${df}.mtw"
        return try {
            drive.upload(tmp, name)?.let { Result.success() } ?: Result.retry()
        } catch (e: Exception) { Result.retry() }
        finally { tmp.delete() }
    }
}
