package com.mototrack.wit.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RouteDao {
    @Insert suspend fun insert(r: RouteEntity): Long
    @Update suspend fun update(r: RouteEntity)
    @Query("DELETE FROM routes WHERE id=:id") suspend fun delete(id: Long)
    @Query("SELECT * FROM routes ORDER BY startedAt DESC") fun observeAll(): Flow<List<RouteEntity>>
    @Query("SELECT * FROM routes WHERE id=:id") suspend fun get(id: Long): RouteEntity?
}

@Dao
interface SampleDao {
    @Insert suspend fun insertAll(items: List<SampleEntity>)
    @Query("SELECT * FROM samples WHERE routeId=:rid ORDER BY t ASC") suspend fun getAll(rid: Long): List<SampleEntity>
    @Query("SELECT COUNT(*) FROM samples WHERE routeId=:rid") suspend fun count(rid: Long): Int
}
