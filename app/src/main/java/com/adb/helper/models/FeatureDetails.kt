package com.adb.helper.models

import android.content.pm.FeatureInfo

data class FeatureDetails constructor(@Transient private var feature: FeatureInfo) {
    private var name: String = feature.name ?: "glEsVersion"
    private var label: String? = name.split(".").last().replaceFirstChar { it.titlecase() }
    private var version: Int =
        if (feature.name != null) feature.version else (feature.glEsVersion.toFloat()).toInt()
    private var isRequired: Boolean = (feature.flags and FeatureInfo.FLAG_REQUIRED) > 0
}
