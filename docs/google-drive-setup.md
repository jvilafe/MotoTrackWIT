# Configuración de Google Drive (OAuth)

## 1. Crea proyecto Google Cloud
1. https://console.cloud.google.com/ → "New project" → nombre `MotoTrackWIT`.
2. APIs & Services → **Enable APIs** → habilita **Google Drive API**.

## 2. Pantalla de consentimiento OAuth
1. APIs & Services → OAuth consent screen → External → rellena nombre, email, scope `.../auth/drive.file`.
2. Add test users → añade tu cuenta Google.

## 3. OAuth Client ID Android
1. APIs & Services → Credentials → "Create credentials" → OAuth client ID → Android.
2. Package name: `com.mototrack.wit`
3. SHA-1 fingerprint de tu keystore debug:
   ```
   keytool -list -v -alias androiddebugkey -keystore ~/.android/debug.keystore -storepass android -keypass android
   ```
   Copia el SHA-1 y pégalo en la consola.
4. Descarga `google-services.json` y guárdalo en `app/google-services.json`.
5. En `app/build.gradle.kts` descomenta:
   ```kotlin
   id("com.google.gms.google-services")
   ```

## 4. Inicio de sesión en la app
- En la pantalla "Rutas", al pulsar el icono ☁️ por primera vez se abrirá el selector de cuenta Google.
- Concede permiso al scope `drive.file`. La app **solo verá los archivos que ella misma cree** (carpeta `MotoTrackWIT/`).
