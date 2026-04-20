# Configuración de mapas (MapLibre + OpenStreetMap)

Esta app usa **MapLibre Native** (fork open-source de Mapbox v1) con tiles raster
de **OpenStreetMap**. Es 100% gratuito, no requiere cuenta, ni tarjeta de crédito,
ni API key.

## ¿Qué se ha configurado?

- Dependencia: `org.maplibre.gl:android-sdk:11.5.2` (Maven Central + maven.maplibre.org).
- Estilo de mapa definido inline en `app/src/main/res/raw/osm_style.json` apuntando
  a los tiles de tile.openstreetmap.org.

## Política de uso de OSM

OpenStreetMap permite uso personal/de bajo volumen siguiendo su Tile Usage Policy
(https://operations.osmfoundation.org/policies/tiles/):

- Cabecera User-Agent identificable (ya configurada como `MotoTrackWIT/0.1`).
- No descargas masivas (la app solo carga lo que ves en pantalla).

Para **uso intensivo o comercial** sustituye la URL de tiles por un proveedor
gratuito con cuenta sin tarjeta, p. ej.:

- **MapTiler** plan free (sin tarjeta): https://www.maptiler.com/cloud/ — sustituye
  el `tiles` en `osm_style.json` por tu URL con `?key=TU_API_KEY`.
- **Stadia Maps** free tier para dev: https://stadiamaps.com/.
- Tu propio servidor con `tileserver-gl` y datos OSM.

Solo cambias la URL del array `tiles`. El resto del código no se toca.
