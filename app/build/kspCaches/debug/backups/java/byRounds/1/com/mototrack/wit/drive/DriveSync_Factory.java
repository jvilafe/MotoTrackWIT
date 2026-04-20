package com.mototrack.wit.drive;

import android.content.Context;
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
public final class DriveSync_Factory implements Factory<DriveSync> {
  private final Provider<Context> ctxProvider;

  public DriveSync_Factory(Provider<Context> ctxProvider) {
    this.ctxProvider = ctxProvider;
  }

  @Override
  public DriveSync get() {
    return newInstance(ctxProvider.get());
  }

  public static DriveSync_Factory create(Provider<Context> ctxProvider) {
    return new DriveSync_Factory(ctxProvider);
  }

  public static DriveSync newInstance(Context ctx) {
    return new DriveSync(ctx);
  }
}
