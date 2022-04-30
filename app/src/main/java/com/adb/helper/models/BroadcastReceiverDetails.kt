package com.adb.helper.models

import android.content.pm.ActivityInfo

data class BroadcastReceiverDetails constructor(@Transient private var receiver: ActivityInfo) {
    private val name: String = receiver.name
    private val label: String = receiver.name.split(".").last()
    private val permission: String? = receiver.permission
    private val isExported: Boolean = receiver.exported
}