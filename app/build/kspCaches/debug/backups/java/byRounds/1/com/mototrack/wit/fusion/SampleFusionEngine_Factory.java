package com.mototrack.wit.fusion;

import com.mototrack.wit.ble.WitBleManager;
import com.mototrack.wit.gps.GpsLocationSource;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class SampleFusionEngine_Factory implements Factory<SampleFusionEngine> {
  private final Provider<WitBleManager> bleProvider;

  private final Provider<GpsLocationSource> gpsProvider;

  public SampleFusionEngine_Factory(Provider<WitBleManager> bleProvider,
      Provider<GpsLocationSource> gpsProvider) {
    this.bleProvider = bleProvider;
    this.gpsProvider = gpsProvider;
  }

  @Override
  public SampleFusionEngine get() {
    return newInstance(bleProvider.get(), gpsProvider.get());
  }

  public static SampleFusionEngine_Factory create(Provider<WitBleManager> bleProvider,
      Provider<GpsLocationSource> gpsProvider) {
    return new SampleFusionEngine_Factory(bleProvider, gpsProvider);
  }

  public static SampleFusionEngine newInstance(WitBleManager ble, GpsLocationSource gps) {
    return new SampleFusionEngine(ble, gps);
  }
}
