package com.mototrack.wit.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
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
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RouteDao_Impl implements RouteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<RouteEntity> __insertionAdapterOfRouteEntity;

  private final EntityDeletionOrUpdateAdapter<RouteEntity> __updateAdapterOfRouteEntity;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  public RouteDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfRouteEntity = new EntityInsertionAdapter<RouteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `routes` (`id`,`name`,`startedAt`,`endedAt`,`distanceM`,`maxSpeed`,`avgSpeed`,`maxAccel`,`maxBrake`,`maxRollLeft`,`maxRollRight`,`maxG`,`driveFileId`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RouteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getStartedAt());
        if (entity.getEndedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndedAt());
        }
        statement.bindDouble(5, entity.getDistanceM());
        statement.bindDouble(6, entity.getMaxSpeed());
        statement.bindDouble(7, entity.getAvgSpeed());
        statement.bindDouble(8, entity.getMaxAccel());
        statement.bindDouble(9, entity.getMaxBrake());
        statement.bindDouble(10, entity.getMaxRollLeft());
        statement.bindDouble(11, entity.getMaxRollRight());
        statement.bindDouble(12, entity.getMaxG());
        if (entity.getDriveFileId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getDriveFileId());
        }
      }
    };
    this.__updateAdapterOfRouteEntity = new EntityDeletionOrUpdateAdapter<RouteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `routes` SET `id` = ?,`name` = ?,`startedAt` = ?,`endedAt` = ?,`distanceM` = ?,`maxSpeed` = ?,`avgSpeed` = ?,`maxAccel` = ?,`maxBrake` = ?,`maxRollLeft` = ?,`maxRollRight` = ?,`maxG` = ?,`driveFileId` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final RouteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getStartedAt());
        if (entity.getEndedAt() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getEndedAt());
        }
        statement.bindDouble(5, entity.getDistanceM());
        statement.bindDouble(6, entity.getMaxSpeed());
        statement.bindDouble(7, entity.getAvgSpeed());
        statement.bindDouble(8, entity.getMaxAccel());
        statement.bindDouble(9, entity.getMaxBrake());
        statement.bindDouble(10, entity.getMaxRollLeft());
        statement.bindDouble(11, entity.getMaxRollRight());
        statement.bindDouble(12, entity.getMaxG());
        if (entity.getDriveFileId() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getDriveFileId());
        }
        statement.bindLong(14, entity.getId());
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM routes WHERE id=?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final RouteEntity r, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRouteEntity.insertAndReturnId(r);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final RouteEntity r, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRouteEntity.handle(r);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<RouteEntity>> observeAll() {
    final String _sql = "SELECT * FROM routes ORDER BY startedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"routes"}, new Callable<List<RouteEntity>>() {
      @Override
      @NonNull
      public List<RouteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfDistanceM = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceM");
          final int _cursorIndexOfMaxSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "maxSpeed");
          final int _cursorIndexOfAvgSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeed");
          final int _cursorIndexOfMaxAccel = CursorUtil.getColumnIndexOrThrow(_cursor, "maxAccel");
          final int _cursorIndexOfMaxBrake = CursorUtil.getColumnIndexOrThrow(_cursor, "maxBrake");
          final int _cursorIndexOfMaxRollLeft = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRollLeft");
          final int _cursorIndexOfMaxRollRight = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRollRight");
          final int _cursorIndexOfMaxG = CursorUtil.getColumnIndexOrThrow(_cursor, "maxG");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final List<RouteEntity> _result = new ArrayList<RouteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RouteEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final double _tmpDistanceM;
            _tmpDistanceM = _cursor.getDouble(_cursorIndexOfDistanceM);
            final float _tmpMaxSpeed;
            _tmpMaxSpeed = _cursor.getFloat(_cursorIndexOfMaxSpeed);
            final float _tmpAvgSpeed;
            _tmpAvgSpeed = _cursor.getFloat(_cursorIndexOfAvgSpeed);
            final float _tmpMaxAccel;
            _tmpMaxAccel = _cursor.getFloat(_cursorIndexOfMaxAccel);
            final float _tmpMaxBrake;
            _tmpMaxBrake = _cursor.getFloat(_cursorIndexOfMaxBrake);
            final float _tmpMaxRollLeft;
            _tmpMaxRollLeft = _cursor.getFloat(_cursorIndexOfMaxRollLeft);
            final float _tmpMaxRollRight;
            _tmpMaxRollRight = _cursor.getFloat(_cursorIndexOfMaxRollRight);
            final float _tmpMaxG;
            _tmpMaxG = _cursor.getFloat(_cursorIndexOfMaxG);
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            _item = new RouteEntity(_tmpId,_tmpName,_tmpStartedAt,_tmpEndedAt,_tmpDistanceM,_tmpMaxSpeed,_tmpAvgSpeed,_tmpMaxAccel,_tmpMaxBrake,_tmpMaxRollLeft,_tmpMaxRollRight,_tmpMaxG,_tmpDriveFileId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object get(final long id, final Continuation<? super RouteEntity> $completion) {
    final String _sql = "SELECT * FROM routes WHERE id=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<RouteEntity>() {
      @Override
      @Nullable
      public RouteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfDistanceM = CursorUtil.getColumnIndexOrThrow(_cursor, "distanceM");
          final int _cursorIndexOfMaxSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "maxSpeed");
          final int _cursorIndexOfAvgSpeed = CursorUtil.getColumnIndexOrThrow(_cursor, "avgSpeed");
          final int _cursorIndexOfMaxAccel = CursorUtil.getColumnIndexOrThrow(_cursor, "maxAccel");
          final int _cursorIndexOfMaxBrake = CursorUtil.getColumnIndexOrThrow(_cursor, "maxBrake");
          final int _cursorIndexOfMaxRollLeft = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRollLeft");
          final int _cursorIndexOfMaxRollRight = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRollRight");
          final int _cursorIndexOfMaxG = CursorUtil.getColumnIndexOrThrow(_cursor, "maxG");
          final int _cursorIndexOfDriveFileId = CursorUtil.getColumnIndexOrThrow(_cursor, "driveFileId");
          final RouteEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final double _tmpDistanceM;
            _tmpDistanceM = _cursor.getDouble(_cursorIndexOfDistanceM);
            final float _tmpMaxSpeed;
            _tmpMaxSpeed = _cursor.getFloat(_cursorIndexOfMaxSpeed);
            final float _tmpAvgSpeed;
            _tmpAvgSpeed = _cursor.getFloat(_cursorIndexOfAvgSpeed);
            final float _tmpMaxAccel;
            _tmpMaxAccel = _cursor.getFloat(_cursorIndexOfMaxAccel);
            final float _tmpMaxBrake;
            _tmpMaxBrake = _cursor.getFloat(_cursorIndexOfMaxBrake);
            final float _tmpMaxRollLeft;
            _tmpMaxRollLeft = _cursor.getFloat(_cursorIndexOfMaxRollLeft);
            final float _tmpMaxRollRight;
            _tmpMaxRollRight = _cursor.getFloat(_cursorIndexOfMaxRollRight);
            final float _tmpMaxG;
            _tmpMaxG = _cursor.getFloat(_cursorIndexOfMaxG);
            final String _tmpDriveFileId;
            if (_cursor.isNull(_cursorIndexOfDriveFileId)) {
              _tmpDriveFileId = null;
            } else {
              _tmpDriveFileId = _cursor.getString(_cursorIndexOfDriveFileId);
            }
            _result = new RouteEntity(_tmpId,_tmpName,_tmpStartedAt,_tmpEndedAt,_tmpDistanceM,_tmpMaxSpeed,_tmpAvgSpeed,_tmpMaxAccel,_tmpMaxBrake,_tmpMaxRollLeft,_tmpMaxRollRight,_tmpMaxG,_tmpDriveFileId);
          } else {
            _result = null;
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
