package com.mototrack.wit.drive

import android.content.Context
import android.net.Uri
import com.google.api.client.googleapis.javanet.NetHttpTransport
import com.google.api.client.http.ByteArrayContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.mototrack.wit.db.RouteEntity
import com.mototrack.wit.db.SampleEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class DriveSync(context: Context) {
    private val appContext = context.applicationContext
    private val jsonFactory = GsonFactory.getDefaultInstance()
    private val httpTransport = NetHttpTransport()

    private fun getDriveService(accountToken: String?): Drive? {
        if (accountToken == null) return null
        val credentials = GoogleCredentials.create(com.google.auth.oauth2.AccessToken(accountToken, null))
            .createScoped(listOf(DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE_FILE))
        return Drive.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName("MotoTrack WIT")
            .build()
    }

    suspend fun uploadRouteToAppFolder(
        drive: Drive,
        route: RouteEntity,
        samples: List<SampleEntity>
    ): String? = withContext(Dispatchers.IO) {
        val folderId = getOrCreateAppFolder(drive) ?: return@withContext null

        val fileMetadata = File().apply {
            name = "${route.name}_${route.startTime}.mtw"
            parents = listOf(folderId)
        }

        val content = buildMtwContent(route, samples)
        val mediaContent = ByteArrayContent("application/octet-stream", content.toByteArray())

        val uploaded = drive.files().create(fileMetadata, mediaContent)
            .setFields("id, name, size")
            .execute()

        uploaded.id
    }

    private fun getOrCreateAppFolder(drive: Drive): String? {
        val query = "mimeType='application/vnd.google-apps.folder' and name='MotoTrackWIT' and 'appDataFolder' in parents"
        val existing = drive.files().list()
            .setSpaces("appDataFolder")
            .setQ(query)
            .setFields("files(id, name)")
            .execute()
            .files

        if (existing.isNotEmpty()) return existing.first().id

        val folderMetadata = File().apply {
            name = "MotoTrackWIT"
            mimeType = "application/vnd.google-apps.folder"
            parents = listOf("appDataFolder")
        }
        return drive.files().create(folderMetadata)
            .setFields("id")
            .execute()
            .id
    }

    private fun buildMtwContent(route: RouteEntity, samples: List<SampleEntity>): String {
        val header = "MTWv1|${route.name}|${route.startTime}|${route.endTime ?: 0}\n"
        val body = samples.joinToString("\n") { s ->
            "${s.timestamp},${s.lat},${s.lon},${s.alt},${s.speed},${s.ax},${s.ay},${s.az},${s.gx},${s.gy},${s.gz},${s.roll},${s.pitch},${s.yaw}"
        }
        return header + body
    }

    suspend fun listRoutes(drive: Drive): List<File> = withContext(Dispatchers.IO) {
        drive.files().list()
            .setSpaces("appDataFolder")
            .setQ("name contains '.mtw'")
            .setFields("files(id, name, size, modifiedTime)")
            .execute()
            .files ?: emptyList()
    }

    suspend fun downloadRoute(drive: Drive, fileId: String): Pair<RouteEntity, List<SampleEntity>>? = withContext(Dispatchers.IO) {
        try {
            val stream: InputStream = drive.files().get(fileId).executeMediaAsInputStream()
            val content = stream.bufferedReader().use { it.readText() }
            parseMtwContent(content, fileId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseMtwContent(content: String, driveFileId: String): Pair<RouteEntity, List<SampleEntity>>? {
        val lines = content.lines()
        if (lines.isEmpty()) return null
        val headerParts = lines.first().split("|")
        if (headerParts.size < 4) return null

        val route = RouteEntity(
            id = 0,
            name = headerParts.getOrNull(1) ?: "Imported",
            startTime = headerParts.getOrNull(2)?.toLongOrNull() ?: System.currentTimeMillis(),
            endTime = headerParts.getOrNull(3)?.toLongOrNull(),
            driveFileId = driveFileId
        )

        val samples = lines.drop(1).mapNotNull { line ->
            val p = line.split(",")
            if (p.size >= 13) {
                SampleEntity(
                    id = 0,
                    routeId = 0,
                    timestamp = p[0].toLongOrNull() ?: 0L,
                    lat = p[1].toDoubleOrNull() ?: 0.0,
                    lon = p[2].toDoubleOrNull() ?: 0.0,
                    alt = p[3].toDoubleOrNull() ?: 0.0,
                    speed = p[4].toFloatOrNull() ?: 0f,
                    ax = p[5].toFloatOrNull() ?: 0f,
                    ay = p[6].toFloatOrNull() ?: 0f,
                    az = p[7].toFloatOrNull() ?: 0f,
                    gx = p[8].toFloatOrNull() ?: 0f,
                    gy = p[9].toFloatOrNull() ?: 0f,
                    gz = p[10].toFloatOrNull() ?: 0f,
                    roll = p.getOrNull(11)?.toFloatOrNull(),
                    pitch = p.getOrNull(12)?.toFloatOrNull(),
                    yaw = p.getOrNull(13)?.toFloatOrNull()
                )
            } else null
        }
        return route to samples
    }
}
