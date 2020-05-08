package nl.ngti.androscope.responses.files

import android.content.Context
import fi.iki.elonen.NanoHTTPD
import nl.ngti.androscope.responses.common.RequestResult
import nl.ngti.androscope.responses.common.ResponseDataCache
import nl.ngti.androscope.responses.common.mimeType
import nl.ngti.androscope.responses.common.toDownloadResponse
import nl.ngti.androscope.server.SessionParams
import java.io.FileInputStream

internal class FileSystemResponse(
        private val context: Context
) {

    private val fileSystemResponseCache = ResponseDataCache(
            paramsSupplier = ::FileSystemParams,
            dataSupplier = {
                FileSystemData(context, it.getFile(context))
            }
    )

    fun getFileCount(session: SessionParams) = fileSystemResponseCache[session].getFileSystemCount()

    fun getFileList(session: SessionParams) = fileSystemResponseCache[session].getFileSystemList(
            session
    )

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

    fun delete(session: SessionParams): RequestResult {
        val params = FileSystemParams(session)
        val file = params.getFile(context)
        if (file.isDirectory && file.listFiles()?.isEmpty() == false) {
            return RequestResult.error("Cannot delete non-empty directories")
        }
        if (file.delete()) {
            return RequestResult.success()
        }
        return RequestResult.error("Cannot delete \"$file\"")
    }

    fun getFileToView(sessionParams: SessionParams): NanoHTTPD.Response? {
        val file = FileSystemParams(sessionParams).getFile(context)

        val mime = file.mimeType ?: "text/plain"
        return NanoHTTPD.newChunkedResponse(
                NanoHTTPD.Response.Status.OK,
                mime,
                FileInputStream(file)
        ).apply {
            addHeader("Content-Disposition", "filename=\"${file.name}\"")
        }
    }

    fun getFileToDownload(sessionParams: SessionParams): NanoHTTPD.Response? {
        val file = FileSystemParams(sessionParams).getFile(context)
        return file.toDownloadResponse()
    }
}
