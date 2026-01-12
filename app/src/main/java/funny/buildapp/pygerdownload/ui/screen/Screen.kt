package funny.buildapp.pygerdownload.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import funny.buildapp.pygerdownload.ui.component.LoadingDialog

/**
 * 通用页面结构组件，支持：
 * - 顶部标题栏
 * - 加载状态显示
 * - 底部弹出面板
 *
 * @param showLoading 是否显示加载对话框
 * @param titleBar 标题栏内容
 * @param bottomSheetLayout 底部弹出内容布局
 * @param content 主体内容
 * @param modifier 布局修饰符
 * @param contentAlignment 内容对齐方式（默认居中）
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Screen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    contentAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    titleBar: @Composable () -> Unit = {},
    background: @Composable () -> Unit = {},
    dialog: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier)
    ) {
        background()
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = contentAlignment
        ) {
            titleBar()
            Box {
                content()
            }
        }
        LoadingDialog(isLoading = isLoading)
        dialog()
    }
}