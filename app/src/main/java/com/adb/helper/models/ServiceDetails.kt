package com.adb.helper.models

import android.content.pm.ServiceInfo


data class ServiceDetails constructor(@Transient private var service: ServiceInfo) {
    private val name: String = service.name
    private var label: String = name.split(".").last().replaceFirstChar { it.titlecase() }
    private val permission: String? = service.permission
    private val isExported: Boolean = service.exported
    private var isStopWithTask: Boolean = false
    private var isSingleUser: Boolean = false
    private var isIsolatedProcess: Boolean = false
    private var isExternalService: Boolean = false

    init {
        isStopWithTask = service.flags and ServiceInfo.FLAG_STOP_WITH_TASK > 0
        isSingleUser = service.flags and ServiceInfo.FLAG_SINGLE_USER > 0
        isIsolatedProcess = service.flags and ServiceInfo.FLAG_ISOLATED_PROCESS > 0
        isExternalService = service.flags and ServiceInfo.FLAG_EXTERNAL_SERVICE > 0
    }
}

