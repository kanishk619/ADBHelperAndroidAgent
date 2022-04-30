package com.adb.helper

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.util.Log
import java.util.*


open class HelperService: Service() {
    companion object {
        private const val TAG = "HelperService"
        private val CHANNEL_ID: String = "HelperService_${UUID.randomUUID()}"
        private val NOTIFICATION_ID = Random().nextInt(999)
        private val API: ApiServer = ApiServer()

        lateinit var service: HelperService
        fun isApiAlive(): Boolean = API.isAlive
        fun getContext(): Context = service.applicationContext
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        service = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!API.isAlive) {
            API.start()
        }

        if (MainActivity.isInitialized()) MainActivity.getInstance().setStatus()

        val mChannel = NotificationChannel(CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_NONE)
        mChannel.description = "$TAG Notification Channel"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }

        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("ADB Helper Service")
            .setContentText(
                if(API.isAlive)
                    getString(R.string.started, ApiServer.HOST, ApiServer.PORT)
                else
                    getString(R.string.stopped)
            )
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.notification_icon))
            .setSmallIcon(R.drawable.notification_icon)
            .setContentIntent(pendingIntent)
            .setChannelId(CHANNEL_ID)
            .build()

        // Notification ID cannot be 0.
        startForeground(NOTIFICATION_ID, notification)
        Log.i(TAG, "Service started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        API.stop()
        Log.i(TAG, "Service stopped")
        MainActivity.getInstance().setStatus()
    }
}