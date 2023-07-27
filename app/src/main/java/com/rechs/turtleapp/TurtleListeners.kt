package com.rechs.turtleapp

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.util.Log
import com.rechs.turtleapp.TurtleListeners.Companion.toHexString
import com.rechs.turtleapp.ble.ConnectionEventListener
import com.rechs.turtleapp.ble.ConnectionManager

import org.jetbrains.anko.alert
import org.jetbrains.anko.runOnUiThread
import java.lang.StringBuilder
import java.util.*

/**
 * This class is so I can use Kotlin functions I used in last project.
 * I don't know how to convert these into Java, so I used this as a Wrapper-like class
 * Lazy fix, but it works
 */
class TurtleListeners {
    companion object {
        /**
         * Sets up connection listener for the Robot BLE
         */
        fun robotListener(context: Context, device: BluetoothDevice): ConnectionEventListener {
            val charList = getCharacteristicsList(device)

            val connectionEventListener by lazy {
                ConnectionEventListener().apply {
                    onDisconnect = {
                        context.runOnUiThread {
                            alert {
                                title = "Disconnected"
                                message = "Disconnected from device."
                            }.show()
                        }
                    }

                }
            }

            return connectionEventListener
        }

        /**
         * Sets up connection listener for the Dome BLE
         * Accepts an int[] to put in values for different reads
         * Works since its call by reference and method can change array passed
         */
        fun domeListener(context: Context, device: BluetoothDevice, itemsArr: Array<Int>, averageArr: Array<Int>): ConnectionEventListener {
            val charList = getCharacteristicsList(device)

            val connectionEventListener by lazy {
                ConnectionEventListener().apply {
                    onDisconnect = {
                        context.runOnUiThread {
                            alert {
                                title = "Disconnected"
                                message = "Disconnected from device."
                            }.show()
                        }
                    }

                    onCharacteristicRead = { _, characteristic->
                        /*
                        Chars
                         -1: Temp
                         -2: Humidity
                         -3: Vibration
                         */
                        var size = charList.size

                        // Write to sensor data array
                        if (itemsArr.size >= 3) {
                            if (characteristic == charList[size - 1]) {
                                itemsArr[0] = Integer.decode(removeEmptyBytes(characteristic.value.toHexString().filter { !it.isWhitespace() }))
                            }

                            if (characteristic == charList[size - 2]) {
                                itemsArr[1] = Integer.decode(removeEmptyBytes(characteristic.value.toHexString().filter { !it.isWhitespace() }))
                            }

                            if (characteristic == charList[size - 3]) {
                                itemsArr[2] = Integer.decode(removeEmptyBytes(characteristic.value.toHexString().filter { !it.isWhitespace() }))
                            }
                        }


                        // Write to average sensor data array
                        if (averageArr.size >= 2) {
                            if (characteristic == charList[size - 4]) {
                                averageArr[0] = Integer.decode(removeEmptyBytes(characteristic.value.toHexString().filter { !it.isWhitespace() }))
                            }

                            if (characteristic == charList[size - 5]) {
                                averageArr[1] = Integer.decode(removeEmptyBytes(characteristic.value.toHexString().filter { !it.isWhitespace() }))
                            }
                        }
                    }
                }
            }

            return connectionEventListener
        }

        /**
         * Gets charList of any BLE Device
         */
        fun getCharacteristicsList(device: BluetoothDevice): List<BluetoothGattCharacteristic> {
            val characteristicsList by lazy {
                ConnectionManager.servicesOnDevice(device)?.flatMap { service ->
                    service.characteristics ?: listOf()
                } ?: listOf()
            }

            return characteristicsList
        }

        /**
         * Removes Empty bytes of a hex string
         * @param str, ex. 0XA800
         * The way data is read through char is reversed, so we have to reverse it using this
         * So if 04 02 is being written, it shows up as 0204, so this function also reversed it
         */
        fun removeEmptyBytes(str : String): String {
            Log.e("Hex String", str)
            var newStr = ""
            if (str[4].toString() == "0" && str[5].toString() == "0") {
                newStr = str.slice(0..3)
                return newStr
            }
            val byte1 = str.slice(2..3)
            val byte2 = str.slice(4..5)
            newStr = StringBuilder("0X").append(byte2).append(byte1).toString()

            return newStr
        }

        /**
         * Converts hex string into bytes
         * @param str, hex string, ex. AA7B
         */
        fun hexToBytes(str: String) : ByteArray {
            return str.chunked(2).map { it.toUpperCase(Locale.US).toInt(16).toByte() }.toByteArray()
        }

        /**
         * Converts a byte array (being read) into a hex string with 0x prefix
         */
        fun ByteArray.toHexString(): String =
            joinToString(separator = " ", prefix = "0x") { String.format("%02X", it) }
    }
}