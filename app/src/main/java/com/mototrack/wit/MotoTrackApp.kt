package com.mototrack.wit

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application de MotoTrack WIT.
 *
 * Cambios respecto a la versión anterior:
 *  - Eliminada la integración con `androidx.hilt:hilt-work` (HiltWorkerFactory
 *    + Configuration.Provider) porque ya no usamos Workers inyectados con Hilt
 *    (el único era `UploadWorker` de Google Drive, ya retirado).
 *  - Eliminado el canal de notificación "sync" (sincronización Drive).
 *  - Se conserva el canal "recording" usado por el ForegroundService que
 *    mantiene viva la grabación de la ruta con la pantalla bloqueada.
 *
 * Si en el futuro se reintroducen Workers con Hilt habrá que volver a añadir
 * `androidx.hilt:hilt-work` y restaurar `HiltWorkerFactory` aquí.
 */
@HiltAndroidApp
class MotoTrackApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_RECORDING,
                    "Grabación de ruta",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    companion object {
        const val CHANNEL_RECORDING = "recording"
    }
}