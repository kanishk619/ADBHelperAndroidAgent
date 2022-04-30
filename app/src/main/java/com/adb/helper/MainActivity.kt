package com.adb.helper


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    companion object {
        private lateinit var instance: MainActivity     // Create singleton instance
        fun getInstance(): MainActivity = instance
        fun isInitialized() = ::instance.isInitialized
        private const val TAG = BuildConfig.APPLICATION_ID
    }

    fun setStatus() {
        findViewById<TextView>(R.id.statusTextView).apply {
            if (HelperService.isApiAlive()) {
                this.text = getString(R.string.started, ApiServer.HOST, ApiServer.PORT)
            }
            else
                this.text = getString(R.string.stopped)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        instance = this // set singleton instance

        setStatus()

        findViewById<Button>(R.id.startService).setOnClickListener {
            startForegroundService(Intent(this, HelperService::class.java))
        }

        findViewById<Button>(R.id.stopService).setOnClickListener {
            stopService(Intent(this, HelperService::class.java))
        }

//        if (!Utils.isAccessGranted()) {
//            val t = Toast.makeText(applicationContext, "Usage access permission is needed else the app won't work", Toast.LENGTH_LONG)
//            t.show()
//            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
//        }

    }
}

