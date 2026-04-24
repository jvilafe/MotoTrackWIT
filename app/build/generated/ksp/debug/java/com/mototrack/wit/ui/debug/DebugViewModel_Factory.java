package com.mototrack.wit.ui.debug;

import com.mototrack.wit.ble.WitBleManager;
import com.mototrack.wit.fusion.SampleFusionEngine;
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
public final class DebugViewModel_Factory implements Factory<DebugViewModel> {
  private final Provider<WitBleManager> bleProvider;

  private final Provider<SampleFusionEngine> fusionProvider;

  public DebugViewModel_Factory(Provider<WitBleManager> bleProvider,
      Provider<SampleFusionEngine> fusionProvider) {
    this.bleProvider = bleProvider;
    this.fusionProvider = fusionProvider;
  }

  @Override
  public DebugViewModel get() {
    return newInstance(bleProvider.get(), fusionProvider.get());
  }

  public static DebugViewModel_Factory create(Provider<WitBleManager> bleProvider,
      Provider<SampleFusionEngine> fusionProvider) {
    return new DebugViewModel_Factory(bleProvider, fusionProvider);
  }

  public static DebugViewModel newInstance(WitBleManager ble, SampleFusionEngine fusion) {
    return new DebugViewModel(ble, fusion);
  }
}
