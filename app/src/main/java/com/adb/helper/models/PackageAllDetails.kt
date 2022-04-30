package com.adb.helper.models

import android.content.pm.PackageInfo
import com.adb.helper.utils.Utils
import com.adb.helper.utils.Utils.Companion.getKMemberIfExists
import com.adb.helper.utils.Utils.Companion.iconAsBase64


/*
    Marking a field @Transient makes Gson ignore the field during serialization
*/

data class PackageAllDetails constructor(@Transient private val packageInfo: PackageInfo) :
    PackageBasicDetails(packageInfo) {

    companion object {
        private val TAG = this::class.java.name.toString()
    }

    private var icon: String? = iconAsBase64(packageInfo.applicationInfo)
    private val certificates = Utils.getCertificates(packageInfo)
    private var permissions: MutableList<Any>? = mutableListOf()
    private var sharedLibraries: MutableList<String>? = mutableListOf()
    private var activities: MutableList<ActivityDetails>? = mutableListOf()
    private var metaData: MutableMap<String, Any?>? = mutableMapOf()
    private var providers: MutableList<ProviderDetails>? = mutableListOf()
    private var services: MutableList<ServiceDetails>? = mutableListOf()
    private var broadcastReceivers: MutableList<BroadcastReceiverDetails>? = mutableListOf()
    private var features: MutableList<FeatureDetails>? = mutableListOf()
    private var isOem: Boolean = false
    @Transient
    var credentialProtectedDataDir: String? = null


    init {
        // check oem flag
        // TODO: check whether this returns correct results
        val oemFlag = 1 shl 17
        val privateFlags = getKMemberIfExists(
            packageInfo.applicationInfo,
            "privateFlags",
            packageInfo.applicationInfo
        ) as Int
        isOem = privateFlags.and(oemFlag) != 0

        credentialProtectedDataDir = getKMemberIfExists(
            packageInfo.applicationInfo,
            "credentialProtectedDataDir", packageInfo.applicationInfo
        ) as String?


        // populate all permissions
        packageInfo.permissions?.forEach {
            if (!permissions!!.contains(it)) permissions!!.add(PermissionDetails(it))
        }
        packageInfo.requestedPermissions?.forEachIndexed { i, permissionName ->
            val p = PermissionDetails(permissionName)
            if (packageInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED != 0) {
                p.isGranted = true
            }
            if (!permissions!!.contains(permissionName))
                permissions!!.add(p)
        }
        if (permissions.isNullOrEmpty()) permissions = null


        // get all shared libraries
        packageInfo.applicationInfo.sharedLibraryFiles?.forEach { sharedLibraries!!.add(it) }
        if (sharedLibraries.isNullOrEmpty()) sharedLibraries = null

        // get all activities
        packageInfo.activities?.forEach { activities!!.add(ActivityDetails(it)) }
        if (activities.isNullOrEmpty()) activities = null

        // get metadata
        packageInfo.applicationInfo.metaData?.keySet()?.forEach { key ->
            metaData!![key] = packageInfo.applicationInfo.metaData.get(key)
        }
        if (metaData.isNullOrEmpty()) metaData = null

        // get all providers
        packageInfo.providers?.forEach { providers!!.add(ProviderDetails(it)) }
        if (providers.isNullOrEmpty()) providers = null

        // get all services
        packageInfo.services?.forEach { services!!.add(ServiceDetails(it)) }
        if (services.isNullOrEmpty()) services = null

        // get all broadcast receivers
        packageInfo.receivers?.forEach { broadcastReceivers!!.add(BroadcastReceiverDetails(it)) }
        if (broadcastReceivers.isNullOrEmpty()) broadcastReceivers = null

        // get all features
        packageInfo.reqFeatures?.forEach { features!!.add(FeatureDetails(it)) }
        if (features.isNullOrEmpty()) features = null

    }

    override fun toString(): String {
        return "PackageAllDetails(" +
                "Name=$name, " +
                "Enabled=$isEnabled, " +
                "Flags=$flags, " +
                "MinSdk=$minSdk, " +
                "TargetSdk=$targetSdk, " +
                "Version=$versionName, " +
                "DataDir=$dataDir, " +
                "SharedLibs=${sharedLibraries?.size}, " +
                "Permissions=${permissions?.size})"
    }
}