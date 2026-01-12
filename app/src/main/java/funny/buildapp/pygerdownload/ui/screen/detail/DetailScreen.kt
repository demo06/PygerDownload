package funny.buildapp.pygerdownload.ui.screen.detail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import funny.buildapp.pygerdownload.ui.component.Screen

@Composable
fun DetailScreen(onBack: () -> Unit = {}) {
    Screen {
        Text(text = "DetailScreen")

    }
}