package nl.ngti.androscope.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle

class AndroscopeMetadata private constructor(
        private val internal: Bundle
) {

    val httpPort: Int
        get() = internal.getInt(KEY_HTTP_PORT, HTTP_PORT)

    val databaseName: String?
        get() = internal.getString(KEY_DATABASE_NAME)

    companion object {

        private const val KEY_AUTO_START = "nl.ngti.androscope.AUTO_START"

        private const val KEY_HTTP_PORT = "nl.ngti.androscope.HTTP_PORT"
        private const val HTTP_PORT = 8787

        private const val KEY_DATABASE_NAME = "nl.ngti.androscope.DATABASE_NAME"

        @JvmStatic
        fun fromContext(context: Context): AndroscopeMetadata {
            return AndroscopeMetadata(getManifestMetadata(context)!!)
        }

        fun isAutoStartEnabled(context: Context): Boolean =
                getManifestMetadata(context)?.getBoolean(KEY_AUTO_START) ?: false

        private fun getManifestMetadata(context: Context): Bundle? {
            return try {
                context.packageManager.getApplicationInfo(
                        context.packageName, PackageManager.GET_META_DATA
                ).metaData
            } catch (e: PackageManager.NameNotFoundException) {
                throw RuntimeException(e)
            }
        }
    }
}
