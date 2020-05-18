package com.example.proj3

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_control.*
import java.io.IOException
import java.util.*

class ControlActivity : AppCompatActivity() {
    var myHandler: Handler = Handler {
        when(it.what) {
            0-> {
            }
        }
        true
    }

    companion object {
        var m_myUUID:UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket:BluetoothSocket?=null
        lateinit var m_progress:ProgressDialog
        lateinit var m_bluetoothAdapter:BluetoothAdapter
        var m_isConnected:Boolean = false
        lateinit var m_address:String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        m_address = intent.getStringExtra(SelectDevice.EXTRA_ADDRESS)

        ConnectToDevice(this).execute()

        control_led_on.setOnClickListener {
            sendCommand("a")
            var s = getCommand()
            Log.i("data1",s)
        }
        control_led_off.setOnClickListener {
            sendCommand("b")
            Log.i("data1","Noting1212")
        }
        control_led_disconnect.setOnClickListener {
            disconnect()
        }

    }
    private fun getCommand():String {
        var numByte:Int =0
        val mmBuffer:ByteArray = ByteArray(1024)
        if(m_bluetoothSocket!= null) {
            try {
                numByte = m_bluetoothSocket!!.inputStream.read(mmBuffer)
            } catch (e:IOException) {
                e.printStackTrace()
            }
            val readMsg = myHandler.obtainMessage(0,numByte,-1,mmBuffer)
            val a:String = String(mmBuffer,0,numByte)
            readMsg.sendToTarget()
            return a
        }
        return ""
    }

    private fun sendCommand(input:String) {
        if(m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            }catch (e:IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if(m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
            }catch (e:IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    private class ConnectToDevice(c: Context):AsyncTask<Void, Void, String>(){
        private var connectSuccess:Boolean = true
        private val context:Context
        init {
            this.context = c
        }
        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context,"Connecting...","please wait")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if(m_bluetoothSocket==null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device:BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }
            }catch (e:IOException) {
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess) {
                Log.i("data","couldn't connect")
            } else {
                m_isConnected = true
            }
            m_progress.dismiss()
        }

    }
}
