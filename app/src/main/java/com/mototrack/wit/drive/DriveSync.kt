package com.mototrack.wit.drive

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.FileContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DriveSync @Inject constructor(@ApplicationContext private val ctx: Context) {

    private val folderName = "MotoTrackWIT"

    private fun drive(): Drive? {
        val account = GoogleSignIn.getLastSignedInAccount(ctx) ?: return null
        val credential = GoogleAccountCredential.usingOAuth2(ctx, listOf(DriveScopes.DRIVE_FILE))
        credential.selectedAccount = account.account
        return Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory.getDefaultInstance(), credential)
            .setApplicationName("MotoTrackWIT").build()
    }

    private fun ensureFolder(d: Drive): String {
        val q = "mimeType='application/vnd.google-apps.folder' and name='$folderName' and trashed=false"
        val res = d.files().list().setQ(q).setFields("files(id,name)").execute()
        if (res.files.isNotEmpty()) return res.files[0].id
        val meta = com.google.api.services.drive.model.File()
            .setName(folderName).setMimeType("application/vnd.google-apps.folder")
        return d.files().create(meta).setFields("id").execute().id
    }

    /** Sube el .mtw y devuelve el fileId de Drive. */
    fun upload(local: File, displayName: String): String? {
        val d = drive() ?: return null
        val folderId = ensureFolder(d)
        val meta = com.google.api.services.drive.model.File()
            .setName(displayName).setParents(listOf(folderId))
        val media = FileContent("application/octet-stream", local)
        return d.files().create(meta, media).setFields("id").execute().id
    }

    /** Lista nombres+ids del folder MotoTrackWIT */
    fun list(): List<Pair<String, String>> {
        val d = drive() ?: return emptyList()
        val folderId = ensureFolder(d)
        val q = "'$folderId' in parents and trashed=false"
        return d.files().list().setQ(q).setFields("files(id,name)").execute()
            .files.map { it.name to it.id }
    }

    fun download(fileId: String, dest: File): Boolean {
        val d = drive() ?: return false
        FileOutputStream(dest).use { out -> d.files().get(fileId).executeMediaAndDownloadTo(out) }
        return true
    }
}
