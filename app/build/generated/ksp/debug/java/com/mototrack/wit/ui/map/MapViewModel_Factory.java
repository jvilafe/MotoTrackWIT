package com.mototrack.wit.ui.map;

import com.mototrack.wit.fusion.SampleFusionEngine;
import com.mototrack.wit.gps.GpsLocationSource;
import com.mototrack.wit.recording.RecordingController;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class MapViewModel_Factory implements Factory<MapViewModel> {
  private final Provider<GpsLocationSource> gpsProvider;

  private final Provider<SampleFusionEngine> fusionProvider;

  private final Provider<RecordingController> recordingControllerProvider;

  public MapViewModel_Factory(Provider<GpsLocationSource> gpsProvider,
      Provider<SampleFusionEngine> fusionProvider,
      Provider<RecordingController> recordingControllerProvider) {
    this.gpsProvider = gpsProvider;
    this.fusionProvider = fusionProvider;
    this.recordingControllerProvider = recordingControllerProvider;
  }

  @Override
  public MapViewModel get() {
    return newInstance(gpsProvider.get(), fusionProvider.get(), recordingControllerProvider.get());
  }

  public static MapViewModel_Factory create(Provider<GpsLocationSource> gpsProvider,
      Provider<SampleFusionEngine> fusionProvider,
      Provider<RecordingController> recordingControllerProvider) {
    return new MapViewModel_Factory(gpsProvider, fusionProvider, recordingControllerProvider);
  }

  public static MapViewModel newInstance(GpsLocationSource gps, SampleFusionEngine fusion,
      RecordingController recordingController) {
    return new MapViewModel(gps, fusion, recordingController);
  }
}
