# MotoTracker — Android nativo

App Android nativa (Kotlin + Jetpack Compose) para registrar rutas en moto fusionando:
- Sensor BLE **WitMotion WT901BLECL5.0** (50 Hz: aceleración, giroscopio, ángulos roll/pitch/yaw).
- GPS interno (10 Hz vía FusedLocationProviderClient).
- Salida combinada a **25 Hz** persistida en Room.
- Visualización en mapa **MapLibre Native + OpenStreetMap** (gratis, sin API key, sin tarjeta).
- Sincronización opcional a **Google Drive** (carpeta `MotoTracker/`).

> Probado contra: Xiaomi Poco X5 (22111317PG), Android 14.

## Estructura

```
app/src/main/java/com/mototrack/wit/
├── MotoTrackApp.kt          # Application + canales notif + WorkManager
├── MainActivity.kt          # Compose + Navigation
├── ble/
│   ├── WitProtocol.kt       # UUIDs FFE5/FFE9/FFE4 + parser frame 0x55 0x61
│   └── WitBleManager.kt     # Scan, connect, notify, comandos config 50 Hz
├── gps/GpsLocationSource.kt # FusedLocation 10 Hz
├── fusion/
│   ├── FusedSample.kt
│   └── SampleFusionEngine.kt # 25 Hz, interpolación GPS lineal
├── data/
│   ├── RouteRepository.kt
│   ├── db/{AppDatabase,Daos,Entities}.kt
│   └── export/MtwExporter.kt # .mtw (binario gz) + CSV + GPX
├── drive/{DriveSync,UploadWorker}.kt
├── service/RecordingForegroundService.kt
├── ui/{record,routes,detail,debug,theme}/
└── di/AppModule.kt

app/src/main/res/raw/osm_style.json   # Estilo MapLibre con tiles OSM
```

## Pasos para abrirlo en Android Studio

1. Abre **Android Studio** → *Open* → selecciona la carpeta `MotoTracker/`.
2. Copia `local.properties.example` a `local.properties` y rellena solo `sdk.dir`.
   **No hace falta ningún token de mapas.**
3. (Solo si vas a usar Drive) Sigue `docs/google-drive-setup.md`, descarga `google-services.json` y colócalo en `app/`. Después descomenta `id("com.google.gms.google-services")` en `app/build.gradle.kts`.
4. Conecta el **Poco X5** por USB con depuración activada.
5. Pulsa ▶ Run. Acepta los permisos en el móvil:
   - Ubicación → "Permitir todo el tiempo".
   - Bluetooth cercano.
   - Notificaciones.
6. **MIUI/HyperOS — IMPORTANTE**: ve a Ajustes → Apps → MotoTracker → Ahorro de batería → **Sin restricciones**, y activa "Inicio automático". Sin esto, el servicio en background se mata.

## Mapas

Usamos **MapLibre Native** (fork open-source de Mapbox v1) con tiles raster de
**OpenStreetMap**. No requiere cuenta, ni tarjeta, ni API key. Para uso intensivo
o comercial puedes cambiar la URL de tiles en `res/raw/osm_style.json` (ver
`docs/maps-setup.md`).

## Uso

1. Pantalla **Grabación**: pulsa "Buscar", selecciona tu sensor `WT901BLE...`, "Conectar".
2. Escribe un nombre de ruta y pulsa **Iniciar**. La app activa el `ForegroundService`.
3. Puedes apagar la pantalla y usar el móvil para otras cosas.
4. Pausar/Reanudar/Finalizar cuando termines.
5. Pantalla **Rutas**: lista con resumen, abre detalle (mapa) o súbela a Drive.
6. Pantalla **Detalle**: trazado en MapLibre + estadísticas máx/medias.

## Validación

- `./gradlew test` ejecuta los tests del parser BLE.
- Pantalla `/debug` (icono bug) muestra Hz real.

## Limitaciones conocidas

- En el primer arranque, MIUI puede pedir confirmar permiso BLE dos veces.
- La sincronización Drive requiere Wi-Fi conectada al cargador (configurable en `RoutesViewModel`).
