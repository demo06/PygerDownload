package funny.buildapp.pygerdownload.ui.screen.mini

data class MiniDetailUiState(
    val isLoading: Boolean = false,
    val appName: String? = null,
    val appIcon: String? = null,
    val appDescription: String? = "中钢网APP是北京中钢网信息股份有限公司（股票代码831727）的产品，这里汇聚了国内最全的现货资源信息，与中钢网www.zgw.com实现无缝连接，第一时间为您推送各种优质资源，让您实时掌控钢市商机。\n" +
            "通过中钢网APP，您可以随时随地查看钢材报价，这里有特卖场，钢厂专场等意想不到的特惠资源；通过使用“在线下单”功能，可以免费优先锁货；您还可以委托找货，数百位行业精英提供全方位采购服务，轻松解决找货难题。\n" +
            "中钢网最懂您的“钢需”。",
    val miniApps: List<MiniAppInfo>? = null
)

sealed class MiniDetailUiAction {
    object GoBack : MiniDetailUiAction()
    object Download : MiniDetailUiAction()
    object FetchData : MiniDetailUiAction()
    data class DownloadMiniApp(val appKey: String, val password: String, val appName: String) : MiniDetailUiAction()
}

interface MiniDetailUiEffect {
    data class ShowToast(val msg: String) : MiniDetailUiEffect
    data object GoBack : MiniDetailUiEffect
}

data class MiniAppInfo(
    val name: String,
    val description: String,
    val qrCodeUrl: String,
    val appKey: String,
    val password: String,
    val versionName: String? = null,
    val versionCode: String? = null
)