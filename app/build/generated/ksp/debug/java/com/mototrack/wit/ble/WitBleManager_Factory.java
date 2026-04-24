package com.mototrack.wit.ble;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class WitBleManager_Factory implements Factory<WitBleManager> {
  private final Provider<Context> ctxProvider;

  public WitBleManager_Factory(Provider<Context> ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  @Override
  public WitBleManager get() {
    return newInstance(ctxProvider.get());
  }

  public static WitBleManager_Factory create(Provider<Context> ctxProvider) {
    return new WitBleManager_Factory(ctxProvider);
  }

  public static WitBleManager newInstance(Context ctx) {
    return new WitBleManager(ctx);
  }
}
