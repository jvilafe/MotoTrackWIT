package com.mototrack.wit.service;

import com.mototrack.wit.data.RouteRepository;
import com.mototrack.wit.fusion.SampleFusionEngine;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class RecordingForegroundService_MembersInjector implements MembersInjector<RecordingForegroundService> {
  private final Provider<SampleFusionEngine> fusionProvider;

  private final Provider<RouteRepository> repoProvider;

  public RecordingForegroundService_MembersInjector(Provider<SampleFusionEngine> fusionProvider,
      Provider<RouteRepository> repoProvider) {
    this.fusionProvider = fusionProvider;
    this.repoProvider = repoProvider;
  }

  public static MembersInjector<RecordingForegroundService> create(
      Provider<SampleFusionEngine> fusionProvider, Provider<RouteRepository> repoProvider) {
    return new RecordingForegroundService_MembersInjector(fusionProvider, repoProvider);
  }

  @Override
  public void injectMembers(RecordingForegroundService instance) {
    injectFusion(instance, fusionProvider.get());
    injectRepo(instance, repoProvider.get());
  }

  @InjectedFieldSignature("com.mototrack.wit.service.RecordingForegroundService.fusion")
  public static void injectFusion(RecordingForegroundService instance, SampleFusionEngine fusion) {
    instance.fusion = fusion;
  }

  @InjectedFieldSignature("com.mototrack.wit.service.RecordingForegroundService.repo")
  public static void injectRepo(RecordingForegroundService instance, RouteRepository repo) {
    instance.repo = repo;
  }
}
