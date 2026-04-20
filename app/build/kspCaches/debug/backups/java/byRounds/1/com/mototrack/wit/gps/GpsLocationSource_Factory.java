package com.mototrack.wit.gps;

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
public final class GpsLocationSource_Factory implements Factory<GpsLocationSource> {
  private final Provider<Context> ctxProvider;

  public GpsLocationSource_Factory(Provider<Context> ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  @Override
  public GpsLocationSource get() {
    return newInstance(ctxProvider.get());
  }

  public static GpsLocationSource_Factory create(Provider<Context> ctxProvider) {
    return new GpsLocationSource_Factory(ctxProvider);
  }

  public static GpsLocationSource newInstance(Context ctx) {
    return new GpsLocationSource(ctx);
  }
}
