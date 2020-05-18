package nl.ngti.androscope.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle

internal class AndroscopeMetadata private constructor(
        private val bundle: Bundle
) {

    val httpPortRange: IntRange
        get() {
            val port = bundle.getInt(KEY_HTTP_PORT, -1)
            return if (port == -1)
                START_HTTP_PORT..END_HTTP_PORT
            else port..port
        }

    val databaseName: String?
        get() = bundle.getString(KEY_DATABASE_NAME)

    val imageCacheConfig: ImageCacheConfig?
        get() {
            val cache = bundle.getString(KEY_IMAGE_CACHE)
            val filter = bundle.getString(KEY_IMAGE_CACHE_FILTER)
            if (cache?.isNotBlank() == true && filter?.isNotBlank() == true) {
                return ImageCacheConfig("Configured in manifest", cache, filter)
            }
            return null
        }

    companion object {

        private const val KEY_AUTO_START = "nl.ngti.androscope.AUTO_START"

        private const val KEY_HTTP_PORT = "nl.ngti.androscope.HTTP_PORT"
        private const val START_HTTP_PORT = 8787
        private const val END_HTTP_PORT = 10000

        private const val KEY_DATABASE_NAME = "nl.ngti.androscope.DATABASE_NAME"

        private const val KEY_IMAGE_CACHE = "nl.ngti.androscope.IMAGE_CACHE"
        private const val KEY_IMAGE_CACHE_FILTER = "nl.ngti.androscope.IMAGE_CACHE.filter"

        @JvmStatic
        fun fromContext(context: Context): AndroscopeMetadata {
            return AndroscopeMetadata(getManifestMetadata(context) ?: Bundle.EMPTY)
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
