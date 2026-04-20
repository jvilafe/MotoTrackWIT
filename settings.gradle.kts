pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google()
        mavenCentral()
        // MapLibre Native (gratis, sin token, sin tarjeta)
        maven { url = uri("https://maven.maplibre.org") }
    }
}
rootProject.name = "MotoTrackWIT"
include(":app")
