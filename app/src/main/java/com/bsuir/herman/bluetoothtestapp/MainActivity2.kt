package com.bsuir.herman.bluetoothtestapp

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main2.*
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
    }

    val handler: Handler = Handler {
        when(it.what){
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
}