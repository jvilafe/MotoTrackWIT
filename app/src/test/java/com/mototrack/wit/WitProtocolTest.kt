package com.mototrack.wit

import com.mototrack.wit.ble.WitProtocol
import org.junit.Assert.*
import org.junit.Test

class WitProtocolTest {
    @Test fun parseFrame_zerosWhenAllZeros() {
        val buf = ByteArray(20).also { it[0]=0x55; it[1]=0x61.toByte() }
        val s = WitProtocol.parseFrame(buf)!!
        assertEquals(0f, s.ax, 1e-3f); assertEquals(0f, s.roll, 1e-3f)
    }
    @Test fun parseFrame_oneG_onZAxis() {
        // Az = 1 g => raw = 32768/16 = 2048 = 0x0800 little-endian
        val buf = ByteArray(20).also { it[0]=0x55; it[1]=0x61.toByte(); it[6]=0x00; it[7]=0x08 }
        val s = WitProtocol.parseFrame(buf)!!
        assertEquals(1f, s.az, 0.01f)
    }
    @Test fun parseBuffer_twoFrames() {
        val one = ByteArray(20).also { it[0]=0x55; it[1]=0x61.toByte() }
        val two = one + one
        assertEquals(2, WitProtocol.parseBuffer(two).size)
    }
    @Test fun parseFrame_rejectsWrongHeader() {
        val buf = ByteArray(20).also { it[0]=0x55; it[1]=0x50 }
        assertNull(WitProtocol.parseFrame(buf))
    }
}
