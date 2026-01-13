package funny.buildapp.pygerdownload.ui.screen.mini

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import funny.buildapp.clauncher.util.click
import funny.buildapp.clauncher.util.toast
import funny.buildapp.pygerdownload.R
import funny.buildapp.pygerdownload.route.LocalNavigator
import funny.buildapp.pygerdownload.ui.component.Screen
import funny.buildapp.pygerdownload.ui.component.TitleBar
import funny.buildapp.pygerdownload.ui.screen.home.Tag
import funny.buildapp.pygerdownload.ui.theme.black333
import funny.buildapp.pygerdownload.ui.theme.blue1890FF
import funny.buildapp.pygerdownload.ui.theme.gray999
import funny.buildapp.pygerdownload.ui.theme.green07C160
import funny.buildapp.pygerdownload.ui.theme.white
import funny.buildapp.pygerdownload.ui.theme.whiteF4F5FA

@Preview
@Composable
fun MiniDetailScreen(viewModel: MiniDetailViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val dispatch = viewModel::dispatch
    UIEffect(viewModel, dispatch)
    Screen(
        modifier = Modifier.background(whiteF4F5FA),
        titleBar = {
            TitleBar(title = "小程序", onBack = { dispatch(MiniDetailUiAction.GoBack) })
        }
    ) {
        Content(
            appInfo = {
                AppInfoCard(
                    appName = uiState.appName ?: "环球钢材",
                    iconUrl = uiState.appIcon ?: "",
                    description = uiState.appDescription ?: "",
                    onDownloadClick = { dispatch(MiniDetailUiAction.Download) }
                )
            },
        )
    }
}

@Composable
private fun UIEffect(viewModel: MiniDetailViewModel, dispatch: (MiniDetailUiAction) -> Unit) {
    val navigator = LocalNavigator.current
    val effect by viewModel.effect.collectAsState(initial = null)
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        dispatch(MiniDetailUiAction.FetchData)
    }

    LaunchedEffect(effect) {
        effect?.let {
            when (it) {
                is MiniDetailUiEffect.GoBack -> {
                    navigator.pop()
                }

                is MiniDetailUiEffect.ShowToast -> {
                    it.msg.toast(context)
                }
            }
        }
    }

}

@Composable
private fun Content(
    appInfo: @Composable () -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            appInfo()
        }
    }
}


@Composable
private fun AppInfoCard(
    appName: String = "环球钢材",
    iconUrl: String = "",
    description: String = "",
    onDownloadClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .background(white, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            AsyncImage(
                model = iconUrl,
                contentDescription = "icon",
                placeholder = painterResource(R.mipmap.zgw),
                error = painterResource(R.mipmap.zgw),
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    appName,
                    color = black333,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Tag("MINI", green07C160)
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(description, color = gray999)
        Spacer(modifier = Modifier.height(20.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(
                "打开体验版",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .background(blue1890FF, RoundedCornerShape(30.dp))
                    .click(onDownloadClick)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "打开正式版",
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .background(green07C160, RoundedCornerShape(30.dp))
                    .click(onDownloadClick)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        }
    }
}