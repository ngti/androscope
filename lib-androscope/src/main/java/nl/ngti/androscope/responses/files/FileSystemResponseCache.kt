package nl.ngti.androscope.responses.files

import android.content.Context
import nl.ngti.androscope.server.FileSystemData
import nl.ngti.androscope.server.SessionParams

class FileSystemResponseCache(
        private val context: Context
) {

    private var lastParams: FileSystemParams? = null
    private var lastCachedResult: FileSystemData? = null

    operator fun get(session: SessionParams): FileSystemData {
        val params = FileSystemParams(session)
        return synchronized(this) {
            if (lastParams == params) {
                lastCachedResult!!
            } else {
                lastParams = params
                FileSystemData(context, params.getRootFile(context)).apply {
                    lastCachedResult = this
                }
            }
        }
    }
}
