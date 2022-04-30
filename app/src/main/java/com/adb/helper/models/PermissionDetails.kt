package com.adb.helper.models

import android.content.pm.PackageInfo
import android.content.pm.PermissionInfo
import android.content.pm.PermissionInfo.PROTECTION_DANGEROUS
import android.content.res.Resources
import com.adb.helper.utils.Utils


data class PermissionDetails(@Transient private var permission: PermissionInfo) {
    var name: String? = permission.name
    var label: String? = if (!name.isNullOrEmpty()) label() else null
    var description: String? = description()
    var group: String? = permission.group
    var isRuntime: Boolean = permission.protection == PROTECTION_DANGEROUS
    var isGranted: Boolean = false
    var protection: Map<String, Any> =
        mapOf("flags" to permission.protection, "description" to protectionToString())

    /*
    This will handle the situation where a permission name is not found (mostly for runtime permissions)
    Will create an empty object with everything else set to null
     */
    constructor(permissionName: String) : this(PermissionInfo()) {
        name = permissionName
        label = camelCase(permissionName.split(".").last(), "_")
    }

    private fun camelCase(string: String, delimiter: String = " ", separator: String = " "): String {
        return string.split(delimiter).joinToString(separator = separator) {
            it.lowercase().replaceFirstChar { char -> char.titlecase() }
        }
    }

    private fun label(): String? {
        return try {
            permission.loadLabel(Utils.PackageManager).toString()  // ignore IDE suggestion about replacing dotcall
        } catch (e: Resources.NotFoundException) {
            null
        }
    }

    private fun description(): String? {
        return try {
            permission.loadDescription(Utils.PackageManager)?.toString()
        } catch (e: Resources.NotFoundException) {
            null
        }
    }

    private fun protectionToString(): String {
        return Utils.getKMemberIfExists(
            permission,
            "protectionToString",
            permission.protection
        ) as String
    }

    override fun equals(other: Any?): Boolean {
        if (other is String) return other == name
        if (this === other) return true
        if (other !is PermissionDetails) return false
        if (name != other.name) return false
        if (label != other.label) return false
        if (group != other.group) return false
        return true
    }

    override fun toString(): String {
        return "Permission(name=$name, isRuntime=$isRuntime, group=$group, protection=$protection)"
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}



