package com.adb.helper.models

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import com.adb.helper.utils.Utils


/*
   Marking a field @Transient makes Gson ignore the field during serialization
 */
open class PackageBasicDetails constructor(@Transient private val packageInfo: PackageInfo) {

    companion object {
        @Transient
        private var TAG = this::class.java.name
    }

    val name: String = packageInfo.packageName
    var label: String? = packageInfo.applicationInfo.loadLabel(Utils.PackageManager).toString()
    val versionName: String? = packageInfo.versionName
    val versionCode: Long = packageInfo.longVersionCode

    val processName: String = packageInfo.applicationInfo.processName
    val isEnabled: Boolean = packageInfo.applicationInfo.enabled
    val flags: Int = packageInfo.applicationInfo.flags
    val minSdk: Int = packageInfo.applicationInfo.minSdkVersion
    val targetSdk: Int = packageInfo.applicationInfo.targetSdkVersion
    val dataDir: String = packageInfo.applicationInfo.dataDir
    val path: String = packageInfo.applicationInfo.sourceDir
    val uid: Int = packageInfo.applicationInfo.uid
    val isSystemApp: Boolean = packageInfo.applicationInfo.flags.and(ApplicationInfo.FLAG_SYSTEM) != 0
    val isDebuggable: Boolean =
        packageInfo.applicationInfo.flags.and(ApplicationInfo.FLAG_DEBUGGABLE) != 0
    val hasCode: Boolean = packageInfo.applicationInfo.flags.and(ApplicationInfo.FLAG_HAS_CODE) != 0

    override fun toString(): String {
        return "PackageBasicDetails(Name=$name, Label=$label, Version=$versionName)"
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + versionCode.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PackageBasicDetails || other !is PackageAllDetails) return false
        if (name != other.name) return false
        if (label != other.label) return false
        if (versionCode != other.versionCode) return false

        return true
    }
}