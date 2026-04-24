package com.mototrack.wit.di;

import android.content.Context;
import com.mototrack.wit.data.db.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_DbFactory implements Factory<AppDatabase> {
  private final Provider<Context> ctxProvider;

  public AppModule_DbFactory(Provider<Context> ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  @Override
  public AppDatabase get() {
    return db(ctxProvider.get());
  }

  public static AppModule_DbFactory create(Provider<Context> ctxProvider) {
    return new AppModule_DbFactory(ctxProvider);
  }

  public static AppDatabase db(Context ctx) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.db(ctx));
  }
}
