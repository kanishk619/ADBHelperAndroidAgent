package com.adb.helper


import android.content.pm.PackageManager
import android.util.Log
import com.adb.helper.factory.PackageFactory
import com.adb.helper.factory.PackageFactory.Companion.getPackageDetails
import com.adb.helper.factory.PackageFactory.Companion.getPackageThumbnail
import com.google.gson.GsonBuilder
import fi.iki.elonen.NanoHTTPD
import java.util.*
import java.util.regex.Pattern


class ApiServer: NanoHTTPD(HOST, PORT) {
    companion object {
        const val HOST = "127.0.0.1"
        const val PORT = 8765
        private val TAG = this::class.java.name.toString()
        private val PACKAGE_DETAIL_PATTERN: Pattern =
            Pattern.compile("^(/package/)(?<packageName>[\\w._]+)($|/thumbnail$)")
        private val nonExistentPath: String = UUID.randomUUID().toString()
    }

    override fun serve(session: IHTTPSession): Response {
        return try {
            handleRequest(session)
        } catch (e: PackageManager.NameNotFoundException) {
            jsonResponse(mapOf("error" to "PackageNotFound"), Response.Status.NOT_FOUND)
        } catch (e: Exception) {
            jsonResponse(mapOf("error" to e.stackTraceToString()), Response.Status.SERVICE_UNAVAILABLE)
        }
    }

    private fun handleRequest(session: IHTTPSession): Response {
        Log.i(TAG, "received request -> ${session.uri}")
        val packageDetailRequest = PACKAGE_DETAIL_PATTERN.matcher(session.uri)

        return when (session.uri) {
            "/packages/detail/basic" ->
                jsonResponse(PackageFactory.getAllPackages(PackageFactory.APP_DETAILS_BASIC))

            "/packages/detail/full" ->
                jsonResponse(PackageFactory.getAllPackages(PackageFactory.APP_DETAILS_FULL))

            "/status" ->
                jsonResponse(
                    mapOf(
                        "status" to "ok",
                        "app" to BuildConfig.APPLICATION_ID,
                        "debug" to BuildConfig.DEBUG,
                        "version" to BuildConfig.VERSION_NAME
                    )
                )

            /*
             * Return single package detailed info or icon thumbnail if the package exists.
             * If the package doesn't exist, return 404 with error PackageNotFound
             */
            packageDetailRequest.find().let { match ->
                if (!match)
                    nonExistentPath     // return a random string to that when case match fails
                else
                    session.uri
            } -> {
                val packageName = packageDetailRequest.group("packageName")!!

                // check what request type is this
                if (session.uri.endsWith("thumbnail")) {
                    jsonResponse(mapOf("thumbnail" to getPackageThumbnail(packageName)))
                } else {
                    jsonResponse(getPackageDetails(packageName))
                }
            }
            else -> jsonResponse(mapOf("error" to "invalid path"), Response.Status.NOT_FOUND)
        }
    }


    private fun jsonResponse(obj: Any?,
                             status: Response.Status = Response.Status.OK,
                             pretty: Boolean = false
    ): Response {
        val gsonBuilder = GsonBuilder().serializeNulls()
        if (pretty) gsonBuilder.setPrettyPrinting()
        val dataToSend = gsonBuilder.create().toJson(obj)
        //        response.addHeader("content-length", dataToSend.length.toString())
        return newChunkedResponse(
            status,
            "application/json",
            dataToSend.byteInputStream()
        )
    }

}
