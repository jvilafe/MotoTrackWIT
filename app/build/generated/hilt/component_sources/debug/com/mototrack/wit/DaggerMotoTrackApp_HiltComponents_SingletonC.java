package com.mototrack.wit;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.mototrack.wit.ble.WitBleManager;
import com.mototrack.wit.data.RouteRepository;
import com.mototrack.wit.data.db.AppDatabase;
import com.mototrack.wit.data.db.RouteDao;
import com.mototrack.wit.data.db.SampleDao;
import com.mototrack.wit.di.AppModule_DbFactory;
import com.mototrack.wit.di.AppModule_RouteDaoFactory;
import com.mototrack.wit.di.AppModule_SampleDaoFactory;
import com.mototrack.wit.fusion.SampleFusionEngine;
import com.mototrack.wit.gps.GpsLocationSource;
import com.mototrack.wit.recording.RecordingController;
import com.mototrack.wit.service.RecordingForegroundService;
import com.mototrack.wit.service.RecordingForegroundService_MembersInjector;
import com.mototrack.wit.ui.debug.DebugViewModel;
import com.mototrack.wit.ui.debug.DebugViewModel_HiltModules;
import com.mototrack.wit.ui.detail.RouteDetailViewModel;
import com.mototrack.wit.ui.detail.RouteDetailViewModel_HiltModules;
import com.mototrack.wit.ui.record.RecordViewModel;
import com.mototrack.wit.ui.record.RecordViewModel_HiltModules;
import com.mototrack.wit.ui.routes.RoutesViewModel;
import com.mototrack.wit.ui.routes.RoutesViewModel_HiltModules;
import com.mototrack.wit.ui.sensors.SensorsViewModel;
import com.mototrack.wit.ui.sensors.SensorsViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerMotoTrackApp_HiltComponents_SingletonC {
  private DaggerMotoTrackApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MotoTrackApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MotoTrackApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MotoTrackApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MotoTrackApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MotoTrackApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MotoTrackApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MotoTrackApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MotoTrackApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MotoTrackApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MotoTrackApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MotoTrackApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MotoTrackApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MotoTrackApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MotoTrackApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MotoTrackApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MotoTrackApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MotoTrackApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MotoTrackApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MotoTrackApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(5).put(LazyClassKeyProvider.com_mototrack_wit_ui_debug_DebugViewModel, DebugViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_mototrack_wit_ui_record_RecordViewModel, RecordViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_mototrack_wit_ui_detail_RouteDetailViewModel, RouteDetailViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_mototrack_wit_ui_routes_RoutesViewModel, RoutesViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_mototrack_wit_ui_sensors_SensorsViewModel, SensorsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_mototrack_wit_ui_routes_RoutesViewModel = "com.mototrack.wit.ui.routes.RoutesViewModel";

      static String com_mototrack_wit_ui_sensors_SensorsViewModel = "com.mototrack.wit.ui.sensors.SensorsViewModel";

      static String com_mototrack_wit_ui_debug_DebugViewModel = "com.mototrack.wit.ui.debug.DebugViewModel";

      static String com_mototrack_wit_ui_record_RecordViewModel = "com.mototrack.wit.ui.record.RecordViewModel";

      static String com_mototrack_wit_ui_detail_RouteDetailViewModel = "com.mototrack.wit.ui.detail.RouteDetailViewModel";

      @KeepFieldType
      RoutesViewModel com_mototrack_wit_ui_routes_RoutesViewModel2;

      @KeepFieldType
      SensorsViewModel com_mototrack_wit_ui_sensors_SensorsViewModel2;

      @KeepFieldType
      DebugViewModel com_mototrack_wit_ui_debug_DebugViewModel2;

      @KeepFieldType
      RecordViewModel com_mototrack_wit_ui_record_RecordViewModel2;

      @KeepFieldType
      RouteDetailViewModel com_mototrack_wit_ui_detail_RouteDetailViewModel2;
    }
  }

  private static final class ViewModelCImpl extends MotoTrackApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<DebugViewModel> debugViewModelProvider;

    private Provider<RecordViewModel> recordViewModelProvider;

    private Provider<RouteDetailViewModel> routeDetailViewModelProvider;

    private Provider<RoutesViewModel> routesViewModelProvider;

    private Provider<SensorsViewModel> sensorsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.debugViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.recordViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.routeDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.routesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.sensorsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(5).put(LazyClassKeyProvider.com_mototrack_wit_ui_debug_DebugViewModel, ((Provider) debugViewModelProvider)).put(LazyClassKeyProvider.com_mototrack_wit_ui_record_RecordViewModel, ((Provider) recordViewModelProvider)).put(LazyClassKeyProvider.com_mototrack_wit_ui_detail_RouteDetailViewModel, ((Provider) routeDetailViewModelProvider)).put(LazyClassKeyProvider.com_mototrack_wit_ui_routes_RoutesViewModel, ((Provider) routesViewModelProvider)).put(LazyClassKeyProvider.com_mototrack_wit_ui_sensors_SensorsViewModel, ((Provider) sensorsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_mototrack_wit_ui_routes_RoutesViewModel = "com.mototrack.wit.ui.routes.RoutesViewModel";

      static String com_mototrack_wit_ui_debug_DebugViewModel = "com.mototrack.wit.ui.debug.DebugViewModel";

      static String com_mototrack_wit_ui_record_RecordViewModel = "com.mototrack.wit.ui.record.RecordViewModel";

      static String com_mototrack_wit_ui_detail_RouteDetailViewModel = "com.mototrack.wit.ui.detail.RouteDetailViewModel";

      static String com_mototrack_wit_ui_sensors_SensorsViewModel = "com.mototrack.wit.ui.sensors.SensorsViewModel";

      @KeepFieldType
      RoutesViewModel com_mototrack_wit_ui_routes_RoutesViewModel2;

      @KeepFieldType
      DebugViewModel com_mototrack_wit_ui_debug_DebugViewModel2;

      @KeepFieldType
      RecordViewModel com_mototrack_wit_ui_record_RecordViewModel2;

      @KeepFieldType
      RouteDetailViewModel com_mototrack_wit_ui_detail_RouteDetailViewModel2;

      @KeepFieldType
      SensorsViewModel com_mototrack_wit_ui_sensors_SensorsViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.mototrack.wit.ui.debug.DebugViewModel 
          return (T) new DebugViewModel(singletonCImpl.witBleManagerProvider.get(), singletonCImpl.sampleFusionEngineProvider.get());

          case 1: // com.mototrack.wit.ui.record.RecordViewModel 
          return (T) new RecordViewModel(singletonCImpl.witBleManagerProvider.get(), singletonCImpl.gpsLocationSourceProvider.get(), singletonCImpl.recordingControllerProvider.get());

          case 2: // com.mototrack.wit.ui.detail.RouteDetailViewModel 
          return (T) new RouteDetailViewModel(singletonCImpl.routeRepositoryProvider.get());

          case 3: // com.mototrack.wit.ui.routes.RoutesViewModel 
          return (T) new RoutesViewModel(singletonCImpl.routeRepositoryProvider.get());

          case 4: // com.mototrack.wit.ui.sensors.SensorsViewModel 
          return (T) new SensorsViewModel(singletonCImpl.witBleManagerProvider.get(), singletonCImpl.gpsLocationSourceProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MotoTrackApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MotoTrackApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectRecordingForegroundService(
        RecordingForegroundService recordingForegroundService) {
      injectRecordingForegroundService2(recordingForegroundService);
    }

    private RecordingForegroundService injectRecordingForegroundService2(
        RecordingForegroundService instance) {
      RecordingForegroundService_MembersInjector.injectFusion(instance, singletonCImpl.sampleFusionEngineProvider.get());
      RecordingForegroundService_MembersInjector.injectRepo(instance, singletonCImpl.routeRepositoryProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends MotoTrackApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<WitBleManager> witBleManagerProvider;

    private Provider<GpsLocationSource> gpsLocationSourceProvider;

    private Provider<SampleFusionEngine> sampleFusionEngineProvider;

    private Provider<AppDatabase> dbProvider;

    private Provider<RouteRepository> routeRepositoryProvider;

    private Provider<RecordingController> recordingControllerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private RouteDao routeDao() {
      return AppModule_RouteDaoFactory.routeDao(dbProvider.get());
    }

    private SampleDao sampleDao() {
      return AppModule_SampleDaoFactory.sampleDao(dbProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.witBleManagerProvider = DoubleCheck.provider(new SwitchingProvider<WitBleManager>(singletonCImpl, 0));
      this.gpsLocationSourceProvider = DoubleCheck.provider(new SwitchingProvider<GpsLocationSource>(singletonCImpl, 2));
      this.sampleFusionEngineProvider = DoubleCheck.provider(new SwitchingProvider<SampleFusionEngine>(singletonCImpl, 1));
      this.dbProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 5));
      this.routeRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RouteRepository>(singletonCImpl, 4));
      this.recordingControllerProvider = DoubleCheck.provider(new SwitchingProvider<RecordingController>(singletonCImpl, 3));
    }

    @Override
    public void injectMotoTrackApp(MotoTrackApp motoTrackApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.mototrack.wit.ble.WitBleManager 
          return (T) new WitBleManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 1: // com.mototrack.wit.fusion.SampleFusionEngine 
          return (T) new SampleFusionEngine(singletonCImpl.witBleManagerProvider.get(), singletonCImpl.gpsLocationSourceProvider.get());

          case 2: // com.mototrack.wit.gps.GpsLocationSource 
          return (T) new GpsLocationSource(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.mototrack.wit.recording.RecordingController 
          return (T) new RecordingController(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.gpsLocationSourceProvider.get(), singletonCImpl.sampleFusionEngineProvider.get(), singletonCImpl.routeRepositoryProvider.get());

          case 4: // com.mototrack.wit.data.RouteRepository 
          return (T) new RouteRepository(singletonCImpl.routeDao(), singletonCImpl.sampleDao());

          case 5: // com.mototrack.wit.data.db.AppDatabase 
          return (T) AppModule_DbFactory.db(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
