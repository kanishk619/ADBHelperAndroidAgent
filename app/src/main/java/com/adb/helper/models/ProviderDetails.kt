package com.adb.helper.models

import android.content.pm.ProviderInfo

data class ProviderDetails(@Transient private val providerInfo: ProviderInfo) {
    val name: String = providerInfo.name
    val authority: String? = providerInfo.authority
    val readPermission: String? = providerInfo.readPermission
    val writePermission: String? = providerInfo.writePermission
    val isExported: Boolean = providerInfo.exported
}