package com.mototrack.wit.data.db

import androidx.room.*

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startedAt: Long,
    val endedAt: Long?,
    val distanceM: Double = 0.0,
    val maxSpeed: Float = 0f,
    val avgSpeed: Float = 0f,
    val maxAccel: Float = 0f,
    val maxBrake: Float = 0f,
    val maxRollLeft: Float = 0f,    // roll > 0
    val maxRollRight: Float = 0f,   // roll < 0 (valor absoluto)
    val maxG: Float = 0f,
    val driveFileId: String? = null,
)

@Entity(tableName = "samples",
    foreignKeys = [ForeignKey(entity = RouteEntity::class,
        parentColumns = ["id"], childColumns = ["routeId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["routeId", "t"])])
data class SampleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routeId: Long,
    val t: Long,
    val lat: Double, val lon: Double, val alt: Double,
    val vGps: Float, val bearing: Float, val hAcc: Float,
    val ax: Float, val ay: Float, val az: Float,
    val gx: Float, val gy: Float, val gz: Float,
    val roll: Float, val pitch: Float, val yaw: Float,
    val gMag: Float,
)
