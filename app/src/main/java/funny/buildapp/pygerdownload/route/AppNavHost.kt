package funny.buildapp.pygerdownload.route

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import funny.buildapp.pygerdownload.ui.screen.home.HomeScreen
import funny.buildapp.pygerdownload.ui.screen.detail.DetailScreen
import funny.buildapp.pygerdownload.ui.screen.mini.MiniDetailScreen


@Composable
fun AppNavHost() {
    val backStack = remember { mutableStateListOf<Any>(AppRoute.AppHome) }
    val navigator = remember { AppNavigator(backStack) }

    CompositionLocalProvider(LocalNavigator provides navigator) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() }
        ) { key ->
            when (key) {
                is AppRoute.AppHome -> NavEntry(key) { HomeScreen() }
                is AppRoute.Detail -> NavEntry(key) { DetailScreen() }
                is AppRoute.MiniDetail -> NavEntry(key) { MiniDetailScreen() }
                else -> NavEntry(Unit) { HomeScreen() }
            }
        }
    }
}

