package funny.buildapp.pygerdownload.util

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import funny.buildapp.clauncher.util.toast
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * APK 下载管理器
 * 封装了下载、进度查询、安装等功能
 */
class ApkDownloadManager(private val context: Context) {

    companion object {
        private const val APK_MIME_TYPE = "application/vnd.android.package-archive"
        private const val FILE_PROVIDER_SUFFIX = ".fileprovider"
    }

    private val downloadManager: DownloadManager =
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    // ==================== 下载相关 ====================

    /**
     * 开始下载 APK 文件
     * @param url 下载地址
     * @param fileName 保存的文件名
     * @return 下载任务 ID
     */
    fun download(url: String, fileName: String): Long {
        val request = DownloadManager.Request(url.toUri()).apply {
            setTitle(fileName)
            setDescription("正在下载")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }
        return downloadManager.enqueue(request)
    }

    /**
     * 查询下载进度
     * @param downloadId 下载任务 ID
     * @return Pair<进度(0-100), 状态>
     *         状态值: STATUS_PENDING, STATUS_RUNNING, STATUS_PAUSED, STATUS_SUCCESSFUL, STATUS_FAILED
     *         查询失败返回 Pair(0, -1)
     */
    fun queryProgress(downloadId: Long): Pair<Int, Int> {
        val query = DownloadManager.Query().setFilterById(downloadId)
        return downloadManager.query(query).use { cursor ->
            if (cursor?.moveToFirst() == true) {
                val status = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                )
                val downloaded = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                )
                val total = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                )
                val progress = if (total > 0) ((downloaded * 100) / total).toInt() else 0
                Pair(progress, status)
            } else {
                Pair(0, -1)
            }
        }
    }

    /**
     * 获取已下载文件的 URI
     */
    fun getDownloadedUri(downloadId: Long): Uri? {
        return downloadManager.getUriForDownloadedFile(downloadId)
    }

    // ==================== 安装相关 ====================

    /**
     * 安装已下载的 APK
     * @param context 上下文
     * @param downloadId 下载任务 ID
     */
    fun installApk(context: Context, downloadId: Long) {
        // Android 10+ 直接使用 DownloadManager 的 content URI，更稳定
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            downloadManager.getUriForDownloadedFile(downloadId)?.let { uri ->
                launchInstallIntent(context, uri)
                return
            }
        }

        // Android 9 及以下：复制到缓存目录后使用 FileProvider
        val apkFile = copyToCache(downloadId)
        when {
            apkFile == null -> {
                // 复制失败，尝试使用 DownloadManager 的 URI
                downloadManager.getUriForDownloadedFile(downloadId)?.let { uri ->
                    launchInstallIntent(context, uri)
                } ?: "安装失败".toast(context)
            }
            !isApkValid(context, apkFile) -> {
                "安装包已损坏,请重新下载".toast(context)
                apkFile.delete()
            }
            else -> {
                launchInstallIntentWithFileProvider(context, apkFile)
            }
        }
    }

    /**
     * 使用 URI 安装 APK
     * @param context 上下文
     * @param apkUri APK 文件的 URI
     */
    fun installApk(context: Context, apkUri: Uri?) {
        if (apkUri == null) {
            "安装失败".toast(context)
            return
        }

        // 文件 URI 需要转换为 FileProvider URI
        if (apkUri.scheme == "file") {
            val file = File(apkUri.path ?: return)
            if (!isApkValid(context, file)) {
                "安装包已损坏,请重新下载".toast(context)
                file.delete()
                return
            }
            launchInstallIntentWithFileProvider(context, file)
            return
        }

        // Content URI 直接使用
        launchInstallIntent(context, apkUri)
    }

    // ==================== 清理相关 ====================

    /**
     * 清除所有已下载的 APK 文件
     * @return 删除的文件数量
     */
    fun clearAllDownloadedApks(): Int {
        val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadDir.exists() || !downloadDir.isDirectory) return 0

        return downloadDir.listFiles { file ->
            file.isFile && file.name.endsWith(".apk", ignoreCase = true)
        }?.count { it.delete() } ?: 0
    }

    // ==================== 私有方法 ====================

    /**
     * 将下载的文件复制到缓存目录
     */
    private fun copyToCache(downloadId: Long): File? {
        return try {
            val pfd = downloadManager.openDownloadedFile(downloadId)
            val cacheDir = context.externalCacheDir ?: context.cacheDir
            val tempFile = File(cacheDir, "installer_$downloadId.apk")

            // 删除旧文件
            if (tempFile.exists()) tempFile.delete()
            tempFile.parentFile?.mkdirs()

            // 复制文件
            FileInputStream(pfd.fileDescriptor).use { input ->
                FileOutputStream(tempFile).use { output ->
                    input.copyTo(output)
                    output.flush()
                    output.fd.sync()
                }
            }
            pfd.close()

            // 设置可读权限
            tempFile.setReadable(true, false)
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 验证 APK 文件是否有效
     */
    private fun isApkValid(context: Context, file: File): Boolean {
        return try {
            context.packageManager.getPackageArchiveInfo(
                file.absolutePath,
                android.content.pm.PackageManager.GET_ACTIVITIES
            ) != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 使用 FileProvider 启动安装 Intent
     */
    private fun launchInstallIntentWithFileProvider(context: Context, apkFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}$FILE_PROVIDER_SUFFIX",
                apkFile
            )
            launchInstallIntent(context, uri)
        } catch (e: Exception) {
            e.printStackTrace()
            "启动安装失败".toast(context)
        }
    }

    /**
     * 启动安装 Intent
     */
    private fun launchInstallIntent(context: Context, uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, APK_MIME_TYPE)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            "无法启动安装程序".toast(context)
        }
    }
}