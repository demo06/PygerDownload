package funny.buildapp.pygerdownload.common

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import funny.buildapp.clauncher.util.log
import java.io.File
import java.net.URI

class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent?.action) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0)
            val uri = downloadManager.getUriForDownloadedFile(id)
            uri?.let {
                install(context, it)
            }
        }
    }

    private fun install(context: Context?, path: Uri) {
        val intent = Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(
            path,
            "application/vnd.android.package-archive"
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent);
    }
}