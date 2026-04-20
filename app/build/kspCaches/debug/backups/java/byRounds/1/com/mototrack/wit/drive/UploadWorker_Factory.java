package com.mototrack.wit.drive;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.mototrack.wit.data.RouteRepository;
import dagger.internal.DaggerGenerated;
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
public final class UploadWorker_Factory {
  private final Provider<RouteRepository> repoProvider;

  private final Provider<DriveSync> driveProvider;

  public UploadWorker_Factory(Provider<RouteRepository> repoProvider,
      Provider<DriveSync> driveProvider) {
    this.repoProvider = repoProvider;
    this.driveProvider = driveProvider;
  }

  public UploadWorker get(Context ctx, WorkerParameters params) {
    return newInstance(ctx, params, repoProvider.get(), driveProvider.get());
  }

  public static UploadWorker_Factory create(Provider<RouteRepository> repoProvider,
      Provider<DriveSync> driveProvider) {
    return new UploadWorker_Factory(repoProvider, driveProvider);
  }

  public static UploadWorker newInstance(Context ctx, WorkerParameters params, RouteRepository repo,
      DriveSync drive) {
    return new UploadWorker(ctx, params, repo, drive);
  }
}
