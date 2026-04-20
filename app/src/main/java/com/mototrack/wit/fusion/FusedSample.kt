package com.mototrack.wit.fusion

data class FusedSample(
    val t: Long,            // epoch ms
    val lat: Double,
    val lon: Double,
    val alt: Double,
    val vGps: Float,        // m/s
    val bearing: Float,     // °
    val hAcc: Float,        // m
    val ax: Float, val ay: Float, val az: Float,    // g
    val gx: Float, val gy: Float, val gz: Float,    // °/s
    val roll: Float, val pitch: Float, val yaw: Float, // °
    val gMag: Float         // |a| en g
)
