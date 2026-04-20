package com.mototrack.wit.ui.routes;

import android.content.Context;
import com.mototrack.wit.data.RouteRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class RoutesViewModel_Factory implements Factory<RoutesViewModel> {
  private final Provider<RouteRepository> repoProvider;

  private final Provider<Context> ctxProvider;

  public RoutesViewModel_Factory(Provider<RouteRepository> repoProvider,
      Provider<Context> ctxProvider) {
    this.repoProvider = repoProvider;
    this.ctxProvider = ctxProvider;
  }

  @Override
  public RoutesViewModel get() {
    return newInstance(repoProvider.get(), ctxProvider.get());
  }

  public static RoutesViewModel_Factory create(Provider<RouteRepository> repoProvider,
      Provider<Context> ctxProvider) {
    return new RoutesViewModel_Factory(repoProvider, ctxProvider);
  }

  public static RoutesViewModel newInstance(RouteRepository repo, Context ctx) {
    return new RoutesViewModel(repo, ctx);
  }
}
