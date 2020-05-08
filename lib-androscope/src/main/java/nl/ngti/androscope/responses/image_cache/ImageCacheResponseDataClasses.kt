package nl.ngti.androscope.responses.image_cache

internal class ImageCache(
        val type: String,
        val title: String
)

internal class ImageCacheInfo(
        val totalEntries: Int = 0,
        val error: String? = null
)

internal class ImageCacheEntry(
        val fileName: String,
        val size: Long
)
