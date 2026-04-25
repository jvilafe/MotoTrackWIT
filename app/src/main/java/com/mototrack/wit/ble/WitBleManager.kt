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
import kotlinx.coroutines.*
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

data class BleConnState(
    val connected: Boolean = false,
    val name: String? = null,
    val address: String? = null,
    val rssi: Int? = null
)

@Singleton
@SuppressLint("MissingPermission")
class WitBleManager @Inject constructor(@ApplicationContext private val ctx: Context) {

    private val tag = "WitBLE"
    private val adapter: BluetoothAdapter? =
        (ctx.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _state = MutableStateFlow<BleState>(BleState.Idle)
    val state: StateFlow<BleState> = _state.asStateFlow()

    private val _samples = MutableSharedFlow<WitProtocol.Sample>(extraBufferCapacity = 256)
    val samples: SharedFlow<WitProtocol.Sample> = _samples.asSharedFlow()

    private val _rssi = MutableStateFlow<Int?>(null)
    val rssi: StateFlow<Int?> = _rssi.asStateFlow()

    private val _connectionState = MutableStateFlow(BleConnState())
    val connectionState: StateFlow<BleConnState> = _connectionState.asStateFlow()

    private val _sampleHz = MutableStateFlow(0f)
    val sampleHz: StateFlow<Float> = _sampleHz.asStateFlow()

    private var sampleCounter = 0

    private var gatt: BluetoothGatt? = null
    private var writeChar: BluetoothGattCharacteristic? = null
    private var rssiPollerJob: Job? = null

    private var autoReconnect: Boolean = false
    private var lastConnectedAddress: String? = null

    init {
        scope.launch {
            while (isActive) {
                delay(1000)
                _sampleHz.value = sampleCounter.toFloat()
                sampleCounter = 0
            }
        }
    }

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

    suspend fun scanAndConnect(timeoutMs: Long = 15_000) {
        val found = withTimeoutOrNull(timeoutMs) { scan().first() } ?: run {
            _state.value = BleState.Error("No se encontró ningún sensor WitMotion")
            return
        }
        connect(found.address)
    }

    fun connect(address: String) {
        val dev = adapter?.getRemoteDevice(address) ?: return
        lastConnectedAddress = address
        autoReconnect = true
        _state.value = BleState.Connecting(address)
        gatt = dev.connectGatt(ctx, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    fun disconnect() {
        autoReconnect = false
        try { gatt?.disconnect(); gatt?.close() } catch (_: Exception) {}
        gatt = null; writeChar = null
        rssiPollerJob?.cancel(); rssiPollerJob = null
        _state.value = BleState.Idle
        _connectionState.value = BleConnState(connected = false)
    }

    /** Envía un comando arbitrario al sensor. Asegúrate de llamar a unlock si es necesario. */
    fun sendCommand(bytes: ByteArray) {
        writeCmd(bytes)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                g.requestMtu(247)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _state.value = BleState.Idle
                _connectionState.value = BleConnState(connected = false)
                rssiPollerJob?.cancel(); rssiPollerJob = null
                try { g.close() } catch (_: Exception) {}
                gatt = null; writeChar = null
                if (autoReconnect) {
                    scope.launch {
                        delay(2000)
                        lastConnectedAddress?.let {
                            Log.d(tag, "Reintentando conexión a $it")
                            connect(it)
                        }
                    }
                }
            }
        }

        override fun onMtuChanged(g: BluetoothGatt, mtu: Int, status: Int) {
            Log.d(tag, "MTU=$mtu status=$status")
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
            if (ccc != null) {
                if (Build.VERSION.SDK_INT >= 33) {
                    g.writeDescriptor(ccc, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                } else {
                    @Suppress("DEPRECATION")
                    ccc.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    @Suppress("DEPRECATION")
                    g.writeDescriptor(ccc)
                }
                Log.d(tag, "Notify habilitado en FFE4")
            } else {
                Log.w(tag, "CCCD no encontrado en FFE4")
            }

            _state.value = BleState.Connected(g.device.address)
            _connectionState.value = BleConnState(
                connected = true,
                name = g.device.name,
                address = g.device.address,
                rssi = _rssi.value
            )

            // Configuración: unlock + outputs + 50 Hz + save
            scope.launch {
                delay(300)
                writeCmd(WitProtocol.cmdUnlock());        delay(100)
                writeCmd(WitProtocol.cmdEnableOutputs()); delay(100)
                writeCmd(WitProtocol.cmdUnlock());        delay(100)
                writeCmd(WitProtocol.cmdSetRate50Hz());   delay(100)
                writeCmd(WitProtocol.cmdUnlock());        delay(100)
                writeCmd(WitProtocol.cmdSave())
            }

            rssiPollerJob?.cancel()
            rssiPollerJob = scope.launch {
                while (isActive) {
                    try { gatt?.readRemoteRssi() } catch (_: Exception) {}
                    delay(2000)
                }
            }
        }

        @Deprecated("API 33-")
        override fun onCharacteristicChanged(g: BluetoothGatt, c: BluetoothGattCharacteristic) {
            val data = c.value ?: return
            Log.d(tag, "rx(legacy) ${data.size}B: ${data.joinToString(" "){ "%02X".format(it) }}")
            val parsed = WitProtocol.parseBuffer(data)
            Log.d(tag, "parsed=${parsed.size}")
            for (s in parsed) { _samples.tryEmit(s); sampleCounter++ }
        }

        override fun onCharacteristicChanged(g: BluetoothGatt, c: BluetoothGattCharacteristic, value: ByteArray) {
            Log.d(tag, "rx ${value.size}B: ${value.joinToString(" "){ "%02X".format(it) }}")
            val parsed = WitProtocol.parseBuffer(value)
            Log.d(tag, "parsed=${parsed.size}")
            for (s in parsed) { _samples.tryEmit(s); sampleCounter++ }
        }

        override fun onReadRemoteRssi(g: BluetoothGatt, rssi: Int, status: Int) {
            _rssi.value = rssi
            _connectionState.update { it.copy(rssi = rssi) }
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
