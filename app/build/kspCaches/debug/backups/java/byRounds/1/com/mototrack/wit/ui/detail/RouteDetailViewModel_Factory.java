package com.mototrack.wit.ui.detail;

import com.mototrack.wit.data.RouteRepository;
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
public final class RouteDetailViewModel_Factory implements Factory<RouteDetailViewModel> {
  private final Provider<RouteRepository> repoProvider;

  public RouteDetailViewModel_Factory(Provider<RouteRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public RouteDetailViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static RouteDetailViewModel_Factory create(Provider<RouteRepository> repoProvider) {
    return new RouteDetailViewModel_Factory(repoProvider);
  }

  public static RouteDetailViewModel newInstance(RouteRepository repo) {
    return new RouteDetailViewModel(repo);
  }
}
