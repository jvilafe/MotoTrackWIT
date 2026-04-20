package com.mototrack.wit.drive

import android.content.Context
import com.google.api.client.http.FileContent
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File as DriveFile
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriveSync @Inject constructor(
    @ApplicationContext @Suppress("unused") private val context: Context
) {
    private val httpTransport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()

    @Volatile
    private var currentToken: String? = null

    fun setAccessToken(token: String?) {
        currentToken = token
    }

    private fun service(): Drive? {
        val token = currentToken ?: return null
        val credentials = GoogleCredentials.create(AccessToken(token, null))
            .createScoped(listOf(DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE_FILE))
        return Drive.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(credentials))
            .setApplicationName("MotoTrack WIT")
            .build()
    }

    suspend fun upload(file: File, name: String): String? = withContext(Dispatchers.IO) {
        val drive = service() ?: return@withContext null
        try {
            val metadata = DriveFile().apply {
                this.name = name
                this.parents = listOf("appDataFolder")
            }
            val media = FileContent("application/octet-stream", file)
            drive.files().create(metadata, media)
                .setFields("id, name, size")
                .execute()
                .id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun listRoutes(): List<DriveFile> = withContext(Dispatchers.IO) {
        val drive = service() ?: return@withContext emptyList()
        try {
            drive.files().list()
                .setSpaces("appDataFolder")
                .setQ("name contains '.mtw'")
                .setFields("files(id, name, size, modifiedTime)")
                .execute()
                .files ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun download(fileId: String): InputStream? = withContext(Dispatchers.IO) {
        val drive = service() ?: return@withContext null
        try {
            drive.files().get(fileId).executeMediaAsInputStream()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
