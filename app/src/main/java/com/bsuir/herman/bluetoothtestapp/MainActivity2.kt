package com.bsuir.herman.bluetoothtestapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.IOException
import java.util.*
import javax.security.auth.callback.Callback

class MainActivity2 : AppCompatActivity() {

    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var bluetoothDevices: Array<BluetoothDevice>

    val STATE_LISTENING: Int = 1
    val STATE_CONNECTING: Int = 2
    val STATE_CONNECTED: Int = 3
    val STATE_CONNECTION_FAILED: Int = 4
    val STATE_MESSAGE_RECIEVED: Int = 5

    val REQUEST_ENABLE_BLUETOOTH: Int = 1

    private val APP_NAME = "BTChat"
    private val MY_UUID: UUID = UUID
        .fromString("8ce255c0-223a-11e0-ac64-0803450c9a66")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (!bluetoothAdapter.isEnabled) {
            val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH)
        }

        implementListeners()
    }

    private fun implementListeners() {
        list_view.setOnClickListener {
            var bt: Set<BluetoothDevice> = bluetoothAdapter.bondedDevices
            var strings: Array<String> = emptyArray()
            var index = 0

            if (bt.size > 0) {
                bt.forEach {
                    bluetoothDevices[index] = it
                    strings[index] = it.name
                    index++
                }
                var arrayAdapter: ArrayAdapter<String> =
                    ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, strings)
                list_view.adapter = arrayAdapter
            }
        }

        btn_listen.setOnClickListener {
            val serverClass: ServerClass = ServerClass()
            serverClass.start()
        }
        list_view.setOnItemClickListener { parent, view, position, id ->
            val clientClass = ClientClass(bluetoothDevices[position])
            clientClass.start()

            tv_status.text = "Connecting"
        }
    }

    val handler: Handler = Handler {
        when (it.what) {
            STATE_LISTENING -> {
                tv_status.text = "Listening"
            }
            STATE_CONNECTING -> {
                tv_status.text = "Connecting"
            }
            STATE_CONNECTED -> {
                tv_status.text = "Connected"
            }
            STATE_CONNECTION_FAILED -> {
                tv_status.text = "Connection failed"
            }
            STATE_MESSAGE_RECIEVED -> {
                TODO("write later")
            }
        }
        return@Handler true
    }

    inner class ServerClass : Thread() {
        private lateinit var bluetoothServerSocket: BluetoothServerSocket

        init {
            try {
                bluetoothServerSocket = bluetoothAdapter
                    .listenUsingRfcommWithServiceRecord(APP_NAME, MY_UUID)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun run() {
            var bluetoothSocket: BluetoothSocket? = null
            while (bluetoothSocket == null) {
                try {
                    val message: Message = Message.obtain()
                    message.what = STATE_CONNECTING
                    handler.sendMessage(message)

                    bluetoothSocket = bluetoothServerSocket.accept()
                } catch (e: IOException) {
                    e.printStackTrace()

                    val message: Message = Message.obtain()
                    message.what = STATE_CONNECTION_FAILED
                    handler.sendMessage(message)
                }

                if (bluetoothSocket != null) {
                    val message: Message = Message.obtain()
                    message.what = STATE_CONNECTED
                    handler.sendMessage(message)

                    TODO("here is gonna be code for sending/receiving msg")
                    break
                }

            }
        }
    }

    inner class ClientClass(device: BluetoothDevice) : Thread() {
        //        private lateinit var device: BluetoothDevice
        private lateinit var socket: BluetoothSocket

        init {
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun run() {
            try {
                socket.connect()
                val message = Message.obtain()
                message.what = STATE_CONNECTED
                handler.sendMessage(message)
            } catch (e: IOException) {
                e.printStackTrace()
                val message = Message.obtain()
                message.what = STATE_CONNECTION_FAILED
                handler.sendMessage(message)
            }
        }
    }
}