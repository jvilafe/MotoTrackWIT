package com.mototrack.wit.ui.record;

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
public final class RecordViewModel_Factory implements Factory<RecordViewModel> {
  private final Provider<WitBleManager> bleProvider;

  private final Provider<SampleFusionEngine> fusionProvider;

  public RecordViewModel_Factory(Provider<WitBleManager> bleProvider,
      Provider<SampleFusionEngine> fusionProvider) {
    this.bleProvider = bleProvider;
    this.fusionProvider = fusionProvider;
  }

  @Override
  public RecordViewModel get() {
    return newInstance(bleProvider.get(), fusionProvider.get());
  }

  public static RecordViewModel_Factory create(Provider<WitBleManager> bleProvider,
      Provider<SampleFusionEngine> fusionProvider) {
    return new RecordViewModel_Factory(bleProvider, fusionProvider);
  }

  public static RecordViewModel newInstance(WitBleManager ble, SampleFusionEngine fusion) {
    return new RecordViewModel(ble, fusion);
  }
}
