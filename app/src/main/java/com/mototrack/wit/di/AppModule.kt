package com.mototrack.wit.di

import android.content.Context
import androidx.room.Room
import com.mototrack.wit.data.db.AppDatabase
import com.mototrack.wit.data.db.RouteDao
import com.mototrack.wit.data.db.SampleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun db(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "mototrack.db").build()

    @Provides fun routeDao(db: AppDatabase): RouteDao = db.routeDao()
    @Provides fun sampleDao(db: AppDatabase): SampleDao = db.sampleDao()
}
