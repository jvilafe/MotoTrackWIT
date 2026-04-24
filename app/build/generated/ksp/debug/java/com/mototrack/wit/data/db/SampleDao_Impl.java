package com.mototrack.wit.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SampleDao_Impl implements SampleDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SampleEntity> __insertionAdapterOfSampleEntity;

  public SampleDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSampleEntity = new EntityInsertionAdapter<SampleEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `samples` (`id`,`routeId`,`t`,`lat`,`lon`,`alt`,`vGps`,`bearing`,`hAcc`,`ax`,`ay`,`az`,`gx`,`gy`,`gz`,`roll`,`pitch`,`yaw`,`gMag`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SampleEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRouteId());
        statement.bindLong(3, entity.getT());
        statement.bindDouble(4, entity.getLat());
        statement.bindDouble(5, entity.getLon());
        statement.bindDouble(6, entity.getAlt());
        statement.bindDouble(7, entity.getVGps());
        statement.bindDouble(8, entity.getBearing());
        statement.bindDouble(9, entity.getHAcc());
        statement.bindDouble(10, entity.getAx());
        statement.bindDouble(11, entity.getAy());
        statement.bindDouble(12, entity.getAz());
        statement.bindDouble(13, entity.getGx());
        statement.bindDouble(14, entity.getGy());
        statement.bindDouble(15, entity.getGz());
        statement.bindDouble(16, entity.getRoll());
        statement.bindDouble(17, entity.getPitch());
        statement.bindDouble(18, entity.getYaw());
        statement.bindDouble(19, entity.getGMag());
      }
    };
  }

  @Override
  public Object insertAll(final List<SampleEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSampleEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final long rid, final Continuation<? super List<SampleEntity>> $completion) {
    final String _sql = "SELECT * FROM samples WHERE routeId=? ORDER BY t ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, rid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<SampleEntity>>() {
      @Override
      @NonNull
      public List<SampleEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRouteId = CursorUtil.getColumnIndexOrThrow(_cursor, "routeId");
          final int _cursorIndexOfT = CursorUtil.getColumnIndexOrThrow(_cursor, "t");
          final int _cursorIndexOfLat = CursorUtil.getColumnIndexOrThrow(_cursor, "lat");
          final int _cursorIndexOfLon = CursorUtil.getColumnIndexOrThrow(_cursor, "lon");
          final int _cursorIndexOfAlt = CursorUtil.getColumnIndexOrThrow(_cursor, "alt");
          final int _cursorIndexOfVGps = CursorUtil.getColumnIndexOrThrow(_cursor, "vGps");
          final int _cursorIndexOfBearing = CursorUtil.getColumnIndexOrThrow(_cursor, "bearing");
          final int _cursorIndexOfHAcc = CursorUtil.getColumnIndexOrThrow(_cursor, "hAcc");
          final int _cursorIndexOfAx = CursorUtil.getColumnIndexOrThrow(_cursor, "ax");
          final int _cursorIndexOfAy = CursorUtil.getColumnIndexOrThrow(_cursor, "ay");
          final int _cursorIndexOfAz = CursorUtil.getColumnIndexOrThrow(_cursor, "az");
          final int _cursorIndexOfGx = CursorUtil.getColumnIndexOrThrow(_cursor, "gx");
          final int _cursorIndexOfGy = CursorUtil.getColumnIndexOrThrow(_cursor, "gy");
          final int _cursorIndexOfGz = CursorUtil.getColumnIndexOrThrow(_cursor, "gz");
          final int _cursorIndexOfRoll = CursorUtil.getColumnIndexOrThrow(_cursor, "roll");
          final int _cursorIndexOfPitch = CursorUtil.getColumnIndexOrThrow(_cursor, "pitch");
          final int _cursorIndexOfYaw = CursorUtil.getColumnIndexOrThrow(_cursor, "yaw");
          final int _cursorIndexOfGMag = CursorUtil.getColumnIndexOrThrow(_cursor, "gMag");
          final List<SampleEntity> _result = new ArrayList<SampleEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SampleEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRouteId;
            _tmpRouteId = _cursor.getLong(_cursorIndexOfRouteId);
            final long _tmpT;
            _tmpT = _cursor.getLong(_cursorIndexOfT);
            final double _tmpLat;
            _tmpLat = _cursor.getDouble(_cursorIndexOfLat);
            final double _tmpLon;
            _tmpLon = _cursor.getDouble(_cursorIndexOfLon);
            final double _tmpAlt;
            _tmpAlt = _cursor.getDouble(_cursorIndexOfAlt);
            final float _tmpVGps;
            _tmpVGps = _cursor.getFloat(_cursorIndexOfVGps);
            final float _tmpBearing;
            _tmpBearing = _cursor.getFloat(_cursorIndexOfBearing);
            final float _tmpHAcc;
            _tmpHAcc = _cursor.getFloat(_cursorIndexOfHAcc);
            final float _tmpAx;
            _tmpAx = _cursor.getFloat(_cursorIndexOfAx);
            final float _tmpAy;
            _tmpAy = _cursor.getFloat(_cursorIndexOfAy);
            final float _tmpAz;
            _tmpAz = _cursor.getFloat(_cursorIndexOfAz);
            final float _tmpGx;
            _tmpGx = _cursor.getFloat(_cursorIndexOfGx);
            final float _tmpGy;
            _tmpGy = _cursor.getFloat(_cursorIndexOfGy);
            final float _tmpGz;
            _tmpGz = _cursor.getFloat(_cursorIndexOfGz);
            final float _tmpRoll;
            _tmpRoll = _cursor.getFloat(_cursorIndexOfRoll);
            final float _tmpPitch;
            _tmpPitch = _cursor.getFloat(_cursorIndexOfPitch);
            final float _tmpYaw;
            _tmpYaw = _cursor.getFloat(_cursorIndexOfYaw);
            final float _tmpGMag;
            _tmpGMag = _cursor.getFloat(_cursorIndexOfGMag);
            _item = new SampleEntity(_tmpId,_tmpRouteId,_tmpT,_tmpLat,_tmpLon,_tmpAlt,_tmpVGps,_tmpBearing,_tmpHAcc,_tmpAx,_tmpAy,_tmpAz,_tmpGx,_tmpGy,_tmpGz,_tmpRoll,_tmpPitch,_tmpYaw,_tmpGMag);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object count(final long rid, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM samples WHERE routeId=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, rid);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
