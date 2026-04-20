package com.mototrack.wit;

import androidx.hilt.work.HiltWorkerFactory;
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
public final class MotoTrackApp_MembersInjector implements MembersInjector<MotoTrackApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public MotoTrackApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<MotoTrackApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new MotoTrackApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(MotoTrackApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.mototrack.wit.MotoTrackApp.workerFactory")
  public static void injectWorkerFactory(MotoTrackApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
