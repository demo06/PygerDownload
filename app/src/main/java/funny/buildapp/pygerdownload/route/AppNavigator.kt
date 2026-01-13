package funny.buildapp.pygerdownload.route

import androidx.compose.runtime.staticCompositionLocalOf


class AppNavigator(
    private val backStack: MutableList<Any>
) {
    fun push(route: Any) {
        backStack.add(route)
    }

    fun pop() {
        backStack.removeLastOrNull()
    }
}


val LocalNavigator = staticCompositionLocalOf { AppNavigator(mutableListOf()) }