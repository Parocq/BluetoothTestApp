package com.bsuir.herman.bluetoothtestapp

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //bluetooth adapter
    lateinit var bAdapter: BluetoothAdapter
    private val REQEST_CODE_ENABLE_BT: Int = 1
    private val REQUEST_CODE_DISCOVERABLE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init bluetooth adapter
        bAdapter = BluetoothAdapter.getDefaultAdapter()
        //check if blue is available or not
        if (bAdapter == null) {
            tv_bluetooth_status.text = "Bluetooth is not available"
        } else {
            tv_bluetooth_status.text = "Bluetooth is available"
        }
        // set image view according to it
        if (bAdapter.isEnabled) {
            //bluetooth is on
            iv_bluetooth.setImageResource(R.drawable.ic_baseline_bluetooth_24)
        } else {
            //Blue is off
            iv_bluetooth.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24)
        }

        //turn on bluetooth
        turnOn.setOnClickListener {
            if (bAdapter.isEnabled) {
                //already on
                Toast.makeText(this, "Already on", Toast.LENGTH_SHORT).show()
            } else {
                //turn on
                var intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(intent, REQEST_CODE_ENABLE_BT)
            }
        }
        //turn off
        turnOff.setOnClickListener {
            if (!bAdapter.isEnabled) {
                //already off
                Toast.makeText(this, "Already off", Toast.LENGTH_SHORT).show()
            } else {
                //turn off
                bAdapter.disable()
                iv_bluetooth.setImageResource(R.drawable.ic_baseline_bluetooth_disabled_24)
            }

        }
        //discoverable
        turnDiscoverable.setOnClickListener {
            if (bAdapter.isDiscovering) {
                Toast.makeText(this, "Making Tour device discoverable", Toast.LENGTH_SHORT).show()
                var intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
                startActivityForResult(intent, REQUEST_CODE_DISCOVERABLE)
            }
        }
        //get list of paired devices
        pairedDeviceButton.setOnClickListener {
            if (bAdapter.isEnabled) {
                tv_paired_devices.text = "Paired Devices"
                val devices = bAdapter.bondedDevices
                devices.forEach {
                    val devicesName = it.name
                    val devAddress = it.address
                    tv_paired_devices.append("\nDevice: $devicesName, $devAddress")
                }
            } else {
                Toast.makeText(this, "Turn on bluetooth first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQEST_CODE_ENABLE_BT -> {
                if (requestCode == Activity.RESULT_OK) {
                    iv_bluetooth.setImageResource(R.drawable.ic_baseline_bluetooth_24)
                } else {
                    Toast.makeText(this, "Could not turn on bluetooth", Toast.LENGTH_SHORT).show()
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}