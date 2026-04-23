package com.mototrack.wit.ui.routes;

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
public final class RoutesViewModel_Factory implements Factory<RoutesViewModel> {
  private final Provider<RouteRepository> repoProvider;

  public RoutesViewModel_Factory(Provider<RouteRepository> repoProvider) {
    this.repoProvider = repoProvider;
  }

  @Override
  public RoutesViewModel get() {
    return newInstance(repoProvider.get());
  }

  public static RoutesViewModel_Factory create(Provider<RouteRepository> repoProvider) {
    return new RoutesViewModel_Factory(repoProvider);
  }

  public static RoutesViewModel newInstance(RouteRepository repo) {
    return new RoutesViewModel(repo);
  }
}
