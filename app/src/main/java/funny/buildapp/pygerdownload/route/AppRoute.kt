package funny.buildapp.pygerdownload.route

import androidx.navigation3.runtime.NavKey

interface AppRoute : NavKey {
    data object AppHome : NavKey
    data class Detail(val id: Int) : NavKey
    data class MiniDetail(val id: Int) : NavKey
}
