package com.mototrack.wit.di;

import com.mototrack.wit.data.db.AppDatabase;
import com.mototrack.wit.data.db.RouteDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_RouteDaoFactory implements Factory<RouteDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_RouteDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public RouteDao get() {
    return routeDao(dbProvider.get());
  }

  public static AppModule_RouteDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_RouteDaoFactory(dbProvider);
  }

  public static RouteDao routeDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.routeDao(db));
  }
}
