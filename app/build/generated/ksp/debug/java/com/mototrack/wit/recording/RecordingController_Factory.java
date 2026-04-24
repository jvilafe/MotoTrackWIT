package com.mototrack.wit.recording;

import android.content.Context;
import com.mototrack.wit.data.RouteRepository;
import com.mototrack.wit.fusion.SampleFusionEngine;
import com.mototrack.wit.gps.GpsLocationSource;
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
public final class RecordingController_Factory implements Factory<RecordingController> {
  private final Provider<Context> ctxProvider;

  private final Provider<GpsLocationSource> gpsProvider;

  private final Provider<SampleFusionEngine> fusionProvider;

  private final Provider<RouteRepository> repoProvider;

  public RecordingController_Factory(Provider<Context> ctxProvider,
      Provider<GpsLocationSource> gpsProvider, Provider<SampleFusionEngine> fusionProvider,
      Provider<RouteRepository> repoProvider) {
    this.ctxProvider = ctxProvider;
    this.gpsProvider = gpsProvider;
    this.fusionProvider = fusionProvider;
    this.repoProvider = repoProvider;
  }

  @Override
  public RecordingController get() {
    return newInstance(ctxProvider.get(), gpsProvider.get(), fusionProvider.get(), repoProvider.get());
  }

  public static RecordingController_Factory create(Provider<Context> ctxProvider,
      Provider<GpsLocationSource> gpsProvider, Provider<SampleFusionEngine> fusionProvider,
      Provider<RouteRepository> repoProvider) {
    return new RecordingController_Factory(ctxProvider, gpsProvider, fusionProvider, repoProvider);
  }

  public static RecordingController newInstance(Context ctx, GpsLocationSource gps,
      SampleFusionEngine fusion, RouteRepository repo) {
    return new RecordingController(ctx, gps, fusion, repo);
  }
}
