package com.mototrack.wit.ui.record;

import com.mototrack.wit.ble.WitBleManager;
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
public final class RecordViewModel_Factory implements Factory<RecordViewModel> {
  private final Provider<WitBleManager> bleProvider;

  private final Provider<GpsLocationSource> gpsProvider;

  private final Provider<RecordingController> controllerProvider;

  public RecordViewModel_Factory(Provider<WitBleManager> bleProvider,
      Provider<GpsLocationSource> gpsProvider, Provider<RecordingController> controllerProvider) {
    this.bleProvider = bleProvider;
    this.gpsProvider = gpsProvider;
    this.controllerProvider = controllerProvider;
  }

  @Override
  public RecordViewModel get() {
    return newInstance(bleProvider.get(), gpsProvider.get(), controllerProvider.get());
  }

  public static RecordViewModel_Factory create(Provider<WitBleManager> bleProvider,
      Provider<GpsLocationSource> gpsProvider, Provider<RecordingController> controllerProvider) {
    return new RecordViewModel_Factory(bleProvider, gpsProvider, controllerProvider);
  }

  public static RecordViewModel newInstance(WitBleManager ble, GpsLocationSource gps,
      RecordingController controller) {
    return new RecordViewModel(ble, gps, controller);
  }
}
