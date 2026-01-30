package funny.buildapp.pygerdownload.common

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import funny.buildapp.clauncher.util.log

/**
 * 下载完成广播接收器
 * 注意：安装逻辑已在 HomeScreen 和 DetailScreen 中通过 ApkDownloadManager 处理
 * 此接收器仅用于记录日志，避免重复安装导致的问题
 */
class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent?.action) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
            "下载完成，downloadId: $id".log()
            // 不在此处触发安装，安装逻辑由 ApkDownloadManager 统一处理
            // 避免在 Android 10+ 上因权限问题导致的"解析软件包时出现问题"错误
        }
    }
}