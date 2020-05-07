package nl.ngti.androscope.responses.image_cache

class ImageCache(
        val type: String,
        val title: String
)

class ImageCacheInfo(
        val totalEntries: Int = 0,
        val error: String? = null
)

class ImageCacheEntry(
        val fileName: String,
        val size: Long
)
