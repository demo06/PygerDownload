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


    fun installApk(context: Context, downloadId: Long) {
        var apkFile: File? = null
        try {
            // 尝试将 DownloadManager 的文件复制到私有目录，以解决兼容性问题
            val pfd = downloadManager.openDownloadedFile(downloadId)
            val inputStream = java.io.FileInputStream(pfd.fileDescriptor)
            val cacheDir = context.externalCacheDir ?: context.cacheDir
            
            // 使用 downloadId 作为文件名的一部分，防止冲突
            val tempFile = File(cacheDir, "installer_$downloadId.apk")
            
            if (tempFile.exists()) {
                tempFile.delete()
            }
            
            // 确保目录存在
            tempFile.parentFile?.mkdirs()
            
            val outputStream = java.io.FileOutputStream(tempFile)
            inputStream.copyTo(outputStream)
            
            inputStream.close()
            outputStream.close()
            pfd.close()
            
            // 设置文件为所有者可读写，其他人可读
            tempFile.setReadable(true, false)
            
            apkFile = tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            // 复制失败，apkFile 保持为 null
        }

        if (apkFile != null && apkFile!!.exists()) {
            if (isApkValid(context, apkFile!!)) {
                try {
                    launchInstallIntent(context, apkFile!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                    "启动安装失败".toast(context)
                }
            } else {
                "安装包已损坏,请重新下载".toast(context)
                try {
                    apkFile!!.delete()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            // Fallback: 如果复制失败或无法获取文件，尝试直接使用 DownloadManager 提供的 Uri
            val uri = downloadManager.getUriForDownloadedFile(downloadId)
            installApk(context, uri)
        }
    }

    fun installApk(context: Context, apkUri: Uri?) {
        if (apkUri == null) {
            "安装失败".toast(context)
            return
        }

        // 如果是文件 Uri，尝试使用 FileProvider
        if (apkUri.scheme == "file") {
            try {
                val file = File(apkUri.path!!)
                if (isApkValid(context, file)) {
                    launchInstallIntent(context, file)
                    return
                } else {
                    "安装包已损坏,请重新下载".toast(context)
                    try {
                        file.delete()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    // 文件已损坏，不再进一步尝试
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // 给安装程序权限读取这个 Uri
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            "无法启动安装程序".toast(context)
        }
    }

    private fun isApkValid(context: Context, file: File): Boolean {
        return try {
            val pm = context.packageManager
            val info = pm.getPackageArchiveInfo(
                file.absolutePath,
                android.content.pm.PackageManager.GET_ACTIVITIES
            )
            info != null
        } catch (e: Exception) {
            false
        }
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