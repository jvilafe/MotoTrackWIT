package com.mototrack.wit.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.mototrack.wit.MainActivity
import com.mototrack.wit.R
import com.mototrack.wit.data.RouteRepository
import com.mototrack.wit.fusion.FusedSample
import com.mototrack.wit.fusion.SampleFusionEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordingForegroundService : LifecycleService() {

    @Inject lateinit var fusion: SampleFusionEngine
    @Inject lateinit var repo: RouteRepository

    enum class Status { IDLE, RECORDING, PAUSED }

    companion object {
        const val ACTION_START = "start"
        const val ACTION_PAUSE = "pause"
        const val ACTION_RESUME = "resume"
        const val ACTION_STOP = "stop"
        const val EXTRA_ROUTE_NAME = "name"

        private val _status = MutableStateFlow(Status.IDLE)
        val status: StateFlow<Status> = _status.asStateFlow()
        private val _routeId = MutableStateFlow<Long?>(null)
        val activeRouteId: StateFlow<Long?> = _routeId.asStateFlow()
        private val _count = MutableStateFlow(0)
        val sampleCount: StateFlow<Int> = _count.asStateFlow()
    }

    private var collectorJob: Job? = null
    private val buffer = ArrayList<FusedSample>(64)

    override fun onBind(intent: Intent): IBinder? { super.onBind(intent); return null }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            ACTION_START -> startRecording(intent.getStringExtra(EXTRA_ROUTE_NAME) ?: "Ruta")
            ACTION_PAUSE -> pauseRecording()
            ACTION_RESUME -> resumeRecording()
            ACTION_STOP -> stopRecording()
        }
        return START_STICKY
    }

    private fun startRecording(name: String) {
        startInForeground("Grabando: $name")
        _status.value = Status.RECORDING
        _count.value = 0
        lifecycleScope.launch {
            val id = repo.startRoute(name)
            _routeId.value = id
            fusion.start(40L)
            collectorJob = launch {
                fusion.flow.collect { s ->
                    buffer.add(s)
                    if (buffer.size >= 8) { // ~250 ms a 25 Hz
                        val batch = ArrayList(buffer); buffer.clear()
                        repo.appendBatch(id, batch)
                        _count.value = _count.value + batch.size
                    }
                }
            }
        }
    }

    private fun pauseRecording() {
        if (_status.value != Status.RECORDING) return
        fusion.stop()
        _status.value = Status.PAUSED
    }

    private fun resumeRecording() {
        if (_status.value != Status.PAUSED) return
        fusion.start(40L)
        _status.value = Status.RECORDING
    }

    private fun stopRecording() {
        val id = _routeId.value
        fusion.stop()
        collectorJob?.cancel()
        lifecycleScope.launch {
            if (id != null) {
                if (buffer.isNotEmpty()) { repo.appendBatch(id, buffer); buffer.clear() }
                repo.finalizeRoute(id)
            }
            _routeId.value = null
            _status.value = Status.IDLE
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun startInForeground(text: String) {
        val pi = PendingIntent.getActivity(this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val notif: Notification = NotificationCompat.Builder(this, "recording")
            .setSmallIcon(R.drawable.ic_stat_record)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(text)
            .setOngoing(true)
            .setContentIntent(pi)
            .build()
        val type = if (Build.VERSION.SDK_INT >= 34)
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION or
            ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE else 0
        if (Build.VERSION.SDK_INT >= 34) startForeground(1, notif, type)
        else startForeground(1, notif)
    }
}
