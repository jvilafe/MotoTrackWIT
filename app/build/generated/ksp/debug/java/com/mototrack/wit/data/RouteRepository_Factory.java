package com.mototrack.wit.data;

import com.mototrack.wit.data.db.RouteDao;
import com.mototrack.wit.data.db.SampleDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class RouteRepository_Factory implements Factory<RouteRepository> {
  private final Provider<RouteDao> routeDaoProvider;

  private final Provider<SampleDao> sampleDaoProvider;

  public RouteRepository_Factory(Provider<RouteDao> routeDaoProvider,
      Provider<SampleDao> sampleDaoProvider) {
    this.routeDaoProvider = routeDaoProvider;
    this.sampleDaoProvider = sampleDaoProvider;
  }

  @Override
  public RouteRepository get() {
    return newInstance(routeDaoProvider.get(), sampleDaoProvider.get());
  }

  public static RouteRepository_Factory create(Provider<RouteDao> routeDaoProvider,
      Provider<SampleDao> sampleDaoProvider) {
    return new RouteRepository_Factory(routeDaoProvider, sampleDaoProvider);
  }

  public static RouteRepository newInstance(RouteDao routeDao, SampleDao sampleDao) {
    return new RouteRepository(routeDao, sampleDao);
  }
}
