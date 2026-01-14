package funny.buildapp.pygerdownload.route

import androidx.navigation3.runtime.NavKey
import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.model.MiniInfo

interface AppRoute : NavKey {
    data object AppHome : NavKey
    data class Detail(val item: AppInfo) : NavKey
    data class MiniDetail(val item: MiniInfo) : NavKey
}
