package com.mototrack.wit.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

data class WitDevice(val name: String?, val address: String, val rssi: Int)

sealed class BleState {
    data object Idle : BleState()
    data object Scanning : BleState()
    data class Connecting(val address: String) : BleState()
    data class Connected(val address: String) : BleState()
    data class Error(val msg: String) : BleState()
}

@Singleton
@SuppressLint("MissingPermission")
class WitBleManager @Inject constructor(@ApplicationContext private val ctx: Context) {

    private val tag = "WitBLE"
    private val adapter: BluetoothAdapter? =
        (ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private val _state = MutableStateFlow<BleState>(BleState.Idle)
    val state: StateFlow<BleState> = _state.asStateFlow()

    private val _samples = MutableSharedFlow<WitProtocol.Sample>(extraBufferCapacity = 256)
    val samples: SharedFlow<WitProtocol.Sample> = _samples.asSharedFlow()

    private val _rssi = MutableStateFlow<Int?>(null)
    val rssi: StateFlow<Int?> = _rssi.asStateFlow()

    private var gatt: BluetoothGatt? = null
    private var writeChar: BluetoothGattCharacteristic? = null

    fun scan(): Flow<WitDevice> = callbackFlow {
        val scanner = adapter?.bluetoothLeScanner ?: run {
            close(IllegalStateException("Bluetooth no disponible")); return@callbackFlow
        }
        _state.value = BleState.Scanning
        val cb = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val name = result.device.name ?: result.scanRecord?.deviceName
                if (name?.startsWith("WT", ignoreCase = true) == true ||
                    name?.contains("BWT", ignoreCase = true) == true) {
                    trySend(WitDevice(name, result.device.address, result.rssi))
                }
            }
            override fun onScanFailed(errorCode: Int) {
                _state.value = BleState.Error("scan failed $errorCode"); close()
            }
        }
        val settings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
        scanner.startScan(null, settings, cb)
        awaitClose {
            try { scanner.stopScan(cb) } catch (_: Exception) {}
            if (_state.value is BleState.Scanning) _state.value = BleState.Idle
        }
    }

    fun connect(address: String) {
        val dev = adapter?.getRemoteDevice(address) ?: return
        _state.value = BleState.Connecting(address)
        gatt = dev.connectGatt(ctx, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    fun disconnect() {
        try { gatt?.disconnect(); gatt?.close() } catch (_: Exception) {}
        gatt = null; writeChar = null
        _state.value = BleState.Idle
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                g.requestMtu(247)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _state.value = BleState.Idle
            }
        }
        override fun onMtuChanged(g: BluetoothGatt, mtu: Int, status: Int) {
            g.discoverServices()
        }
        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            val service = g.getService(WitProtocol.SERVICE_UUID) ?: run {
                _state.value = BleState.Error("Servicio FFE5 no encontrado"); return
            }
            val notifyChar = service.getCharacteristic(WitProtocol.CHAR_NOTIFY)
            writeChar = service.getCharacteristic(WitProtocol.CHAR_WRITE)
            g.setCharacteristicNotification(notifyChar, true)
            val ccc = notifyChar.getDescriptor(WitProtocol.CCC_DESCRIPTOR)
            ccc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            g.writeDescriptor(ccc)
            _state.value = BleState.Connected(g.device.address)
            // Configurar 50 Hz + outputs
            writeCmd(WitProtocol.cmdEnableOutputs())
            writeCmd(WitProtocol.cmdSetRate50Hz())
            writeCmd(WitProtocol.cmdSave())
            g.readRemoteRssi()
        }
        @Deprecated("API 33-")
        override fun onCharacteristicChanged(g: BluetoothGatt, c: BluetoothGattCharacteristic) {
            val data = c.value ?: return
            for (s in WitProtocol.parseBuffer(data)) _samples.tryEmit(s)
        }
        override fun onCharacteristicChanged(g: BluetoothGatt, c: BluetoothGattCharacteristic, value: ByteArray) {
            for (s in WitProtocol.parseBuffer(value)) _samples.tryEmit(s)
        }
        override fun onReadRemoteRssi(g: BluetoothGatt, rssi: Int, status: Int) {
            _rssi.value = rssi
        }
    }

    private fun writeCmd(bytes: ByteArray) {
        val g = gatt ?: return
        val c = writeChar ?: return
        if (Build.VERSION.SDK_INT >= 33) {
            g.writeCharacteristic(c, bytes, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
        } else {
            @Suppress("DEPRECATION")
            c.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            @Suppress("DEPRECATION")
            c.value = bytes
            @Suppress("DEPRECATION")
            g.writeCharacteristic(c)
        }
        Log.d(tag, "cmd ${bytes.joinToString(" ") { "%02X".format(it) }}")
    }
}
