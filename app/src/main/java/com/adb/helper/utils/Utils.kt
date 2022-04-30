package com.adb.helper.utils


import android.app.AppOpsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import com.adb.helper.BuildConfig
import com.adb.helper.HelperService
import com.adb.helper.models.CertificateDetails
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import kotlin.reflect.KClass


class Utils {
    companion object {
        /**
        Return kotlin class of java object, for use with objection.
        Can be done directly with: obj::class.memberProperties or obj.javaClass.kotlin.members
         */
        fun getContext() = HelperService.getContext()
        val PackageManager: PackageManager = getContext().packageManager

        private fun getKClass(o: Any): KClass<Any> = o.javaClass.kotlin

        fun getKMemberIfExists(obj: Any, member: String, vararg args: Any?): Any? {
            return getKClass(obj).members.firstOrNull { it.name == member }?.call(*args)
        }

        private fun drawableToBitmap(drawable: Drawable): Bitmap? {
            if (drawable is BitmapDrawable) {
                if (drawable.bitmap != null) {
                    return drawable.bitmap
                }
            }
            val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
                Bitmap.createBitmap(
                    1,
                    1,
                    Bitmap.Config.ARGB_8888
                ) // Single color bitmap will be created of 1x1 pixel
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }

        @RequiresApi(Build.VERSION_CODES.Q)
        fun isAccessGranted(packageName: String = BuildConfig.APPLICATION_ID): Boolean {
            return try {
                val applicationInfo = PackageManager.getApplicationInfo(packageName, 0)
                val appOpsManager = getContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                val mode: Int = appOpsManager.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName
                )
                mode == AppOpsManager.MODE_ALLOWED
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
        }


        fun iconAsBase64(packageName: String, thumbnail: Boolean = false, compress: Boolean = false): String? {
            return iconAsBase64(PackageManager.getApplicationInfo(packageName, 0), thumbnail, compress)
        }

        fun iconAsBase64(app: ApplicationInfo, thumbnail: Boolean = false, compress: Boolean = false): String? {
            PackageManager.getDrawable(app.packageName, app.icon, app)?.let { drawable ->
                val bitmap: Bitmap? = if (thumbnail)
                    drawableToBitmap(drawable)?.let {
                        Bitmap.createScaledBitmap(it, 100, 100, false)
                    }
                else
                    drawableToBitmap(drawable)
                val outputStream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.PNG, if (compress) 70 else 100, outputStream)
                return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            }
            return null
        }


        /*
        * https://github.com/MartinStyk/AndroidApkAnalyzer/blob/master/app/src/main/java/sk/styk/martin/apkanalyzer/manager/appanalysis/CertificateManager.kt
        * Replaced deprecated field/method calls
         */
        fun getCertificates(packageInfo: PackageInfo): MutableList<CertificateDetails> {
            val signatures = if (packageInfo.signingInfo.hasMultipleSigners()) {
                packageInfo.signingInfo.apkContentsSigners
            } else {
                packageInfo.signingInfo.signingCertificateHistory
            } ?: throw IllegalStateException("No signature")

            val certificates = mutableListOf<CertificateDetails>()
            for (signature in signatures) {
                certificates.add(ByteArrayInputStream(signature.toByteArray()).use {
                    val certFactory = CertificateFactory.getInstance("X509")
                    val certificate = certFactory.generateCertificate(it) as X509Certificate
                    CertificateDetails(certificate)
                })
            }
            return certificates
        }
    }
}