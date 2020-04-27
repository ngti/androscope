package nl.ngti.androscope.responses.files

import android.content.Context
import nl.ngti.androscope.common.ResponseDataCache
import nl.ngti.androscope.server.FileSystemData
import nl.ngti.androscope.server.SessionParams

class FileSystemResponse(
        private val context: Context
) {

    private val fileSystemResponseCache = ResponseDataCache(
            paramsSupplier = ::FileSystemParams,
            dataSupplier = {
                FileSystemData(context, it.getRootFile(context))
            }
    )

    fun getFileCount(session: SessionParams) = fileSystemResponseCache[session].getFileSystemCount()

    fun getFileList(session: SessionParams) = fileSystemResponseCache[session].getFileSystemList(session)

    fun getBreadcrumbs(session: SessionParams): List<Breadcrumb> {
        val params = FileSystemParams(session)
        val root = params.resolveFileSystemType(context)
        val result = ArrayList<Breadcrumb>()
        result += Breadcrumb(root.absolutePath, "")
        params.path?.takeIf { it.isNotEmpty() }?.run {
            var relativePath = ""
            split('/').forEach {
                relativePath += it
                result += Breadcrumb(it, relativePath)
                relativePath += '/'
            }
        }
        return result
    }

    fun delete(session: SessionParams): FileDeleteResult {
        val params = FileSystemParams(session)
        val file = params.getRootFile(context)
        if (file.isDirectory && file.listFiles()?.isEmpty() == false) {
            return FileDeleteResult(false, "Cannot delete non-empty directories")
        }
        if (file.delete()) {
            return FileDeleteResult(true)
        }
        return FileDeleteResult(false, "Cannot delete \"$file\"")
    }
}
