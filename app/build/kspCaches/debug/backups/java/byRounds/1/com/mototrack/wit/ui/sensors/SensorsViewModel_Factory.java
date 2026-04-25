package com.mototrack.wit.ui.sensors;

import com.mototrack.wit.ble.WitBleManager;
import com.mototrack.wit.gps.GpsLocationSource;
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
public final class SensorsViewModel_Factory implements Factory<SensorsViewModel> {
  private final Provider<WitBleManager> bleProvider;

  private final Provider<GpsLocationSource> gpsProvider;

  public SensorsViewModel_Factory(Provider<WitBleManager> bleProvider,
      Provider<GpsLocationSource> gpsProvider) {
    this.bleProvider = bleProvider;
    this.gpsProvider = gpsProvider;
  }

  @Override
  public SensorsViewModel get() {
    return newInstance(bleProvider.get(), gpsProvider.get());
  }

  public static SensorsViewModel_Factory create(Provider<WitBleManager> bleProvider,
      Provider<GpsLocationSource> gpsProvider) {
    return new SensorsViewModel_Factory(bleProvider, gpsProvider);
  }

  public static SensorsViewModel newInstance(WitBleManager ble, GpsLocationSource gps) {
    return new SensorsViewModel(ble, gps);
  }
}
