package com.adb.helper.factory

import android.content.pm.PackageManager.*
import androidx.annotation.IntDef
import com.adb.helper.models.PackageAllDetails
import com.adb.helper.models.PackageBasicDetails
import com.adb.helper.utils.Utils.Companion.PackageManager
import com.adb.helper.utils.Utils.Companion.iconAsBase64


class PackageFactory {
    @IntDef(APP_DETAILS_BASIC, APP_DETAILS_FULL)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class DetailsType

    companion object {
        const val APP_DETAILS_BASIC = 1
        const val APP_DETAILS_FULL = 2

        private const val flags = GET_ACTIVITIES or
                GET_CONFIGURATIONS or
                GET_META_DATA or
                GET_PERMISSIONS or
                GET_PROVIDERS or
                GET_RECEIVERS or
                GET_SERVICES or
                GET_SHARED_LIBRARY_FILES or
                GET_SIGNING_CERTIFICATES

        fun getAllPackages(@DetailsType detailsType: Int): MutableList<Any> {
            val allPackages: MutableList<Any> = mutableListOf()

            when (detailsType) {
                APP_DETAILS_BASIC -> PackageManager.getInstalledPackages(0).forEach {
                    allPackages.add(PackageBasicDetails(it))
                }
                APP_DETAILS_FULL -> PackageManager.getInstalledPackages(flags).forEach {
                    allPackages.add(PackageAllDetails(it))
                }
            }
            return allPackages
        }

        fun getPackageDetails(packageName: String): PackageAllDetails {
            return PackageAllDetails(PackageManager.getPackageInfo(packageName, flags))
        }

        fun getPackageThumbnail(packageName: String): String? {
            return iconAsBase64(packageName, thumbnail = true, compress = false)
        }
    }

}