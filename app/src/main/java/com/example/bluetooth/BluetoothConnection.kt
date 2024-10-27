package com.example.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.SocketTimeoutException
import java.util.UUID

class BluetoothConnection(private val deviceAddress: String) {
    private val timeoutMillis: Long = 5000

    private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00802F9B74FB")
    private val DEF_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val bluetoothAdapter: BluetoothAdapter by lazy { BluetoothAdapter.getDefaultAdapter() }
    private val device: BluetoothDevice by lazy { bluetoothAdapter.getRemoteDevice(deviceAddress) }

    private var bluetoothSocket: BluetoothSocket? = null

    private var inputStream: InputStream? = null
    private var outputStream: OutputStream? = null

    private val _receivedData = MutableLiveData<String>()
    val receivedData: LiveData<String> get() = _receivedData

    suspend fun connectToDevice() {

        // Инициируем подключение
        try {
            // Отменяем поиск устройств, чтобы не замедлять подключение
            bluetoothAdapter.cancelDiscovery()

            device.createBond()

            bluetoothSocket = device.createRfcommSocketToServiceRecord(DEF_UUID)

            // Подключаемся к устройству
            bluetoothSocket?.connect()

            // Соединение успешно установлено
            Log.d("MyTag", "Успешное подключение к ${device.name}")

            // Теперь можно использовать bluetoothSocket для обмена данными

        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("MyTag", "Ошибка подключения: ${e.message}")
            try {
                bluetoothSocket?.close()
            } catch (closeException: IOException) {
                closeException.printStackTrace()
            }
        } catch (e: SocketTimeoutException) {
            Log.e("MyTag", "Время ожидания истекло")
        } catch (e: SecurityException) {
            Log.e("MyTag", "Нет разрешения на работу с Bluetooth")
        }
    }

    suspend fun sendData(data: String) {
        try {
            withTimeout(timeoutMillis) {
                while (!bluetoothSocket?.isConnected!!) {
                }
            }

            if (outputStream == null) {
                outputStream = bluetoothSocket?.outputStream
            }

            outputStream?.write(data.toByteArray())
            Log.d("MyTag", "Данные отправлены: $data")

        } catch (e: IOException) {
            Log.e("MyTag", "Ошибка при отправке данных: ${e.message}")
        } catch (e: SocketTimeoutException) {
            Log.e("MyTag", "Время ожидания истекло")
        } catch (e: SecurityException) {
            Log.e("MyTag", "Нет разрешения на работу с Bluetooth")
        } catch (e: IllegalStateException) {
            Log.e("MyTag", "Сокет закрыт или соединение завершено")
        } catch (e: TimeoutCancellationException) {
            Log.e("MyTag", "Время ожидания подключения истекло")
        }
    }

    suspend fun startReceiveData() {
        try {
            withTimeout(timeoutMillis) {
                while (bluetoothSocket == null || bluetoothSocket?.isConnected == false) {
                    delay(1)
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.e("MyTag", "Время ожидания подключения истекло")
        }

        inputStream = bluetoothSocket?.inputStream
        val buffer = ByteArray(1024)

        try {
            while (true) {
                val bytesRead = inputStream?.read(buffer) ?: 0
                val rData = String(buffer, 0, bytesRead)
                Log.d("MyTag", "Данные получены: $rData")
                _receivedData.postValue(rData)
            }

        } catch (e: IOException) {
            Log.e("MyTag", "Ошибка при получении данных: ${e.message}")
        } catch (e: SocketTimeoutException) {
            Log.e("MyTag", "Время ожидания истекло")
        } catch (e: SecurityException) {
            Log.e("MyTag", "Нет разрешения на работу с Bluetooth")
        } catch (e: IllegalStateException) {
            Log.e("MyTag", "Сокет закрыт или соединение завершено")
        }
    }

    fun closeConnection() {
        try {
            inputStream?.close()
            outputStream?.close()
            bluetoothSocket!!.close()
            Log.d("MyTag", "Соединение закрыто")
        } catch (e: IOException) {
            Log.e("MyTag", "Ошибка при закрытии соединения: ${e.message}")
        }
    }
}