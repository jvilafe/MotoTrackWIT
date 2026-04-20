package com.mototrack.wit.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile RouteDao _routeDao;

  private volatile SampleDao _sampleDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `routes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `startedAt` INTEGER NOT NULL, `endedAt` INTEGER, `distanceM` REAL NOT NULL, `maxSpeed` REAL NOT NULL, `avgSpeed` REAL NOT NULL, `maxAccel` REAL NOT NULL, `maxBrake` REAL NOT NULL, `maxRollLeft` REAL NOT NULL, `maxRollRight` REAL NOT NULL, `maxG` REAL NOT NULL, `driveFileId` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `samples` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `routeId` INTEGER NOT NULL, `t` INTEGER NOT NULL, `lat` REAL NOT NULL, `lon` REAL NOT NULL, `alt` REAL NOT NULL, `vGps` REAL NOT NULL, `bearing` REAL NOT NULL, `hAcc` REAL NOT NULL, `ax` REAL NOT NULL, `ay` REAL NOT NULL, `az` REAL NOT NULL, `gx` REAL NOT NULL, `gy` REAL NOT NULL, `gz` REAL NOT NULL, `roll` REAL NOT NULL, `pitch` REAL NOT NULL, `yaw` REAL NOT NULL, `gMag` REAL NOT NULL, FOREIGN KEY(`routeId`) REFERENCES `routes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_samples_routeId_t` ON `samples` (`routeId`, `t`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '4cea4239f48c7285546621441f21944c')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `routes`");
        db.execSQL("DROP TABLE IF EXISTS `samples`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRoutes = new HashMap<String, TableInfo.Column>(13);
        _columnsRoutes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("endedAt", new TableInfo.Column("endedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("distanceM", new TableInfo.Column("distanceM", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("maxSpeed", new TableInfo.Column("maxSpeed", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("avgSpeed", new TableInfo.Column("avgSpeed", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("maxAccel", new TableInfo.Column("maxAccel", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("maxBrake", new TableInfo.Column("maxBrake", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("maxRollLeft", new TableInfo.Column("maxRollLeft", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("maxRollRight", new TableInfo.Column("maxRollRight", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("maxG", new TableInfo.Column("maxG", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRoutes.put("driveFileId", new TableInfo.Column("driveFileId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRoutes = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRoutes = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRoutes = new TableInfo("routes", _columnsRoutes, _foreignKeysRoutes, _indicesRoutes);
        final TableInfo _existingRoutes = TableInfo.read(db, "routes");
        if (!_infoRoutes.equals(_existingRoutes)) {
          return new RoomOpenHelper.ValidationResult(false, "routes(com.mototrack.wit.data.db.RouteEntity).\n"
                  + " Expected:\n" + _infoRoutes + "\n"
                  + " Found:\n" + _existingRoutes);
        }
        final HashMap<String, TableInfo.Column> _columnsSamples = new HashMap<String, TableInfo.Column>(19);
        _columnsSamples.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("routeId", new TableInfo.Column("routeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("t", new TableInfo.Column("t", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("lat", new TableInfo.Column("lat", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("lon", new TableInfo.Column("lon", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("alt", new TableInfo.Column("alt", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("vGps", new TableInfo.Column("vGps", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("bearing", new TableInfo.Column("bearing", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("hAcc", new TableInfo.Column("hAcc", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("ax", new TableInfo.Column("ax", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("ay", new TableInfo.Column("ay", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("az", new TableInfo.Column("az", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("gx", new TableInfo.Column("gx", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("gy", new TableInfo.Column("gy", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("gz", new TableInfo.Column("gz", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("roll", new TableInfo.Column("roll", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("pitch", new TableInfo.Column("pitch", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("yaw", new TableInfo.Column("yaw", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSamples.put("gMag", new TableInfo.Column("gMag", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSamples = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysSamples.add(new TableInfo.ForeignKey("routes", "CASCADE", "NO ACTION", Arrays.asList("routeId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesSamples = new HashSet<TableInfo.Index>(1);
        _indicesSamples.add(new TableInfo.Index("index_samples_routeId_t", false, Arrays.asList("routeId", "t"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoSamples = new TableInfo("samples", _columnsSamples, _foreignKeysSamples, _indicesSamples);
        final TableInfo _existingSamples = TableInfo.read(db, "samples");
        if (!_infoSamples.equals(_existingSamples)) {
          return new RoomOpenHelper.ValidationResult(false, "samples(com.mototrack.wit.data.db.SampleEntity).\n"
                  + " Expected:\n" + _infoSamples + "\n"
                  + " Found:\n" + _existingSamples);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "4cea4239f48c7285546621441f21944c", "f2d4b21d5829dc48885159902e8d000b");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "routes","samples");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `routes`");
      _db.execSQL("DELETE FROM `samples`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(RouteDao.class, RouteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SampleDao.class, SampleDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public RouteDao routeDao() {
    if (_routeDao != null) {
      return _routeDao;
    } else {
      synchronized(this) {
        if(_routeDao == null) {
          _routeDao = new RouteDao_Impl(this);
        }
        return _routeDao;
      }
    }
  }

  @Override
  public SampleDao sampleDao() {
    if (_sampleDao != null) {
      return _sampleDao;
    } else {
      synchronized(this) {
        if(_sampleDao == null) {
          _sampleDao = new SampleDao_Impl(this);
        }
        return _sampleDao;
      }
    }
  }
}
