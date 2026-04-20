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
  private final Provider<Context> contextProvider;

  public DriveSync_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DriveSync get() {
    return newInstance(contextProvider.get());
  }

  public static DriveSync_Factory create(Provider<Context> contextProvider) {
    return new DriveSync_Factory(contextProvider);
  }

  public static DriveSync newInstance(Context context) {
    return new DriveSync(context);
  }
}
