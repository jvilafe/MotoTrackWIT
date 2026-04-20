package com.mototrack.wit.di;

import com.mototrack.wit.data.db.AppDatabase;
import com.mototrack.wit.data.db.SampleDao;
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
public final class AppModule_SampleDaoFactory implements Factory<SampleDao> {
  private final Provider<AppDatabase> dbProvider;

  public AppModule_SampleDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SampleDao get() {
    return sampleDao(dbProvider.get());
  }

  public static AppModule_SampleDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new AppModule_SampleDaoFactory(dbProvider);
  }

  public static SampleDao sampleDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.sampleDao(db));
  }
}
