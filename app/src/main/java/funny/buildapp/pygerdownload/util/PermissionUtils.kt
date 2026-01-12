package funny.buildapp.pygerdownload.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri

open class PermissionUtils {

    companion object {
        fun haveInstallPermission(context: Context): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return context.packageManager.canRequestPackageInstalls()
            }
            return true
        }

        fun goSettings(context: Context) {
            val packageUri = "package:${context.packageName}".toUri()
            context.startActivity(
                Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    packageUri
                )
            )
        }
    }

}