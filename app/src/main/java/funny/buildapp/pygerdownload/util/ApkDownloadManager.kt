package funny.buildapp.pygerdownload.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import funny.buildapp.clauncher.util.toast
import java.io.File

class ApkDownloadManager(private val context: Context) {

    private val downloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun download(
        url: String,
        fileName: String
    ): Long {
        val request = DownloadManager.Request(url.toUri())
            .setTitle(fileName)
            .setDescription("正在下载")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )

        return downloadManager.enqueue(request)
    }

    /**
     * 查询下载进度
     * @return Pair<Int, Int> 第一个值是进度(0-100)，第二个值是下载状态
     * 状态值: DownloadManager.STATUS_PENDING, STATUS_RUNNING, STATUS_PAUSED, STATUS_SUCCESSFUL, STATUS_FAILED
     * 如果查询失败返回 Pair(0, -1)
     */
    fun queryProgress(downloadId: Long): Pair<Int, Int> {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager.query(query)

        cursor.use {
            if (it != null && it.moveToFirst()) {
                val status = it.getInt(it.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                val downloaded =
                    it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val total =
                    it.getLong(it.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

                val progress = if (total > 0) {
                    ((downloaded * 100) / total).toInt()
                } else {
                    0
                }
                return Pair(progress, status)
            }
        }
        return Pair(0, -1)
    }

    fun getDownloadedUri(downloadId: Long): Uri? {
        return downloadManager.getUriForDownloadedFile(downloadId)
    }


    fun installApk(context: Context, apkUri: Uri?) {
        if (apkUri == null) {
            "安装失败".toast(context)
            return
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // 给安装程序权限读取这个 Uri
        }
        context.startActivity(intent)
    }

    private fun launchInstallIntent(context: Context, apkFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    /**
     * 清除所有已下载的 APK 文件
     * @return 删除的文件数量
     */
    fun clearAllDownloadedApks(): Int {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        var deletedCount = 0

        if (downloadDir.exists() && downloadDir.isDirectory) {
            val apkFiles = downloadDir.listFiles { file ->
                file.isFile && file.name.endsWith(".apk", ignoreCase = true)
            }

            apkFiles?.forEach { file ->
                if (file.delete()) {
                    deletedCount++
                }
            }
        }

        return deletedCount
    }
}