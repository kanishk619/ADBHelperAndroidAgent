package com.adb.helper.models

import android.content.pm.ActivityInfo
import com.adb.helper.utils.Utils

data class ActivityDetails constructor(@Transient private var activity: ActivityInfo) {
    val name: String = activity.name
    val packageName: String = activity.packageName
    val label: String? = activity.loadLabel(Utils.PackageManager).toString()
    val targetActivity: String? = activity.targetActivity
    val permission: String? = activity.permission
    val parentName: String? = activity.parentActivityName
    val isExported: Boolean = activity.exported
}

