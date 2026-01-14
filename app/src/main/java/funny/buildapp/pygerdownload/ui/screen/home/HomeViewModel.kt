package funny.buildapp.pygerdownload.ui.screen.home

import funny.buildapp.clauncher.util.downloadUrl
import funny.buildapp.pygerdownload.BuildConfig
import funny.buildapp.pygerdownload.R
import funny.buildapp.pygerdownload.model.AppInfo
import funny.buildapp.pygerdownload.model.MiniInfo
import funny.buildapp.pygerdownload.net.ApiService
import funny.buildapp.pygerdownload.ui.component.DownloadState
import funny.buildapp.pygerdownload.util.BaseMviViewModel
import funny.buildapp.pygerdownload.util.Constants

/**
 * @author WenBin
 * @version 1.0
 * @description ViewModel for MainActivity
 * @email wenbin@buildapp.fun
 * @date 2022/6/16
 */
class HomeViewModel : BaseMviViewModel<HomeUiState, HomeUiAction, HomeUiEffect>(HomeUiState()) {

    init {
        checkUpdate()
        assembleMiniList()
    }

    override fun handleAction(action: HomeUiAction) {
        when (action) {
            is HomeUiAction.FetchData -> fetchData()
            is HomeUiAction.ShowUpdate -> changeDownLoadDialogState()
            is HomeUiAction.GoDetail -> sendEffect { HomeUiEffect.GoDetail(action.item) }
            is HomeUiAction.Download -> downloadApp(action.appKey, action.password)
            is HomeUiAction.GoMINIDetail -> sendEffect { HomeUiEffect.GoMINIDetail(action.item) }
            is HomeUiAction.UpdateDownloadProgress -> setState { copy(updateProcess = action.progress) }
            is HomeUiAction.Update -> {
                setState { copy(isDownloading = true) }
                downloadApp(Constants.PGYER_KEY, "")
            }
            // 新增：开始下载应用
            is HomeUiAction.StartAppDownload -> startAppDownload(action.appKey, action.password)
            // 新增：更新应用下载进度
            is HomeUiAction.UpdateAppDownloadProgress -> updateAppDownloadProgress(action.appKey, action.progress)
            // 新增：应用下载完成
            is HomeUiAction.AppDownloadCompleted -> appDownloadCompleted(action.appKey, action.downloadUri)
            // 新增：应用下载失败
            is HomeUiAction.AppDownloadFailed -> appDownloadFailed(action.appKey)
            // 新增：安装应用
            is HomeUiAction.InstallApp -> installApp(action.appKey)
        }
    }

    override fun updateLoading() {
    }

    /**
     * 获取最新数据
     */
    private fun fetchData() {
        getAppGroup()
    }

    private fun getAppGroup() {
        // 清除下载记录
        setState { copy(isRefreshing = true, appDownloadStates = emptyMap()) }
        // 发送清除已下载APK的 Effect
        sendEffect { HomeUiEffect.ClearDownloadedApks }
        request(
            api = { ApiService.instance().appGroup(Constants.API_KEY, Constants.GROUP_KEY) },
            onFailed = { setState { copy(isRefreshing = false) } },
            onSuccess = {
                setState { copy(items = it.apps ?: emptyList(), isRefreshing = false) }
            }
        )
    }


    private fun assembleMiniList() {
        setState {
            copy(
                miniItems = arrayListOf(
                    MiniInfo(
                        appName = "钢小秘",
                        appPath = Constants.MINI_GXM_PATH,
                        appDescription = "获客更加简单。",
                        appFullDescription = "钢小秘致力于为钢贸商提供更高效、更智能的业务推广与获客工具，全面降低找客户、找资源的成本。通过智能推荐与精准匹配能力，帮助钢贸商快速触达真实有效的采购需求，让优质资源主动找上门。平台汇聚海量钢厂、贸易商及终端客户资源，覆盖多品类、多区域钢材供需信息，支持一键查询、快速对接，大幅提升成交效率。同时，钢小秘将复杂的业务流程进行智能化整合，让推广更简单、获客更直接、资源更集中，助力钢贸商在激烈的市场竞争中抢占先机，实现业务增长与效率提升。",
                        appIcon = R.mipmap.icon_gxm,
                        appOriginalId = Constants.MINI_GXM_ORIGINAL_ID,
                    ),
                    MiniInfo(
                        appName = "卖卖钢",
                        appPath = Constants.MINI_MMG_PATH,
                        appDescription = "全国钢材商机平台领导者！",
                        appFullDescription = "卖卖钢，中钢网旗下全国钢材商机平台领导者，深耕钢铁行业多年，依托中钢网强大的行业资源与数据能力，致力于为中国钢材产业链上下游客户提供更完整、更专业、更高效的精准拓客解决方案。平台聚合海量真实钢材采购与供应信息，覆盖建材、板材、型材、管材等全品类资源，帮助钢厂、贸易商与终端客户快速对接商机。通过智能匹配、精准推荐与多渠道曝光能力，卖卖钢有效降低获客成本，提升成交转化效率，让找客户更简单、谈生意更高效，持续助力钢贸企业实现业务增长与数字化升级。",
                        appIcon = R.mipmap.icon_mmg,
                        appOriginalId = Constants.MINI_MMG_ORIGINAL_ID,
                    ),
                    MiniInfo(
                        appName = "中钢网物流宝",
                        appPath = Constants.MINI_WLB_PATH,
                        appDescription = "一站式解决物流运输问题。",
                        appFullDescription = "中钢网旗下物流服务平台，聚焦钢材交易后的运输需求，创新采用“互联网 + 物流”的服务模式，整合优质运力与行业资源，为用户提供从下单到交付的全程一站式物流解决方案。平台以透明合理的价格体系、可靠的运输安全保障和高效便捷的服务流程，帮助钢贸企业解决找车难、价格不透明、运输风险高等问题，让钢材运输更省心、更放心、更高效，全面提升钢材交易的履约体验与运营效率。",
                        appIcon = R.mipmap.icon_wlb,
                        appOriginalId = Constants.MINI_WLB_ORIGINAL_ID,
                    ),
                    MiniInfo(
                        appName = "中钢网现货商城",
                        appPath = Constants.MINI_XHT_PATH,
                        appDescription = "查询实时现货资源。",
                        appFullDescription = "中钢钢现货通是一款专注于实时现货资源查询的专业服务平台，依托中钢网强大的行业数据与资源整合能力，汇聚全国各地钢厂及贸易商的真实现货信息。平台覆盖多品类、多规格钢材资源，支持按地区、品种、规格等条件精准筛选，帮助用户快速掌握市场供需动态。通过实时更新与智能查询能力，中钢钢现货通让找货更高效、信息更透明，助力钢贸商与终端用户第一时间锁定优质现货资源，把握交易机会。",
                        appIcon = R.mipmap.icon_xht,
                        appOriginalId = Constants.MINI_XHT_ORIGINAL_ID,
                    ),
                    MiniInfo(
                        appName = "钢小秘个人版",
                        appPath = Constants.MINI_GXM_PERSONAL_PATH,
                        appDescription = "中智数据的一款产品。",
                        appFullDescription = "该产品是 中智数据 旗下打造的一款专业数据服务产品，依托中智数据在钢铁行业长期积累的数据资源与技术能力，深度整合多维度行业信息。通过数据分析与智能处理，为用户提供更准确、更及时、更有价值的业务参考，帮助企业洞察市场趋势、提升决策效率，实现钢铁产业链相关业务的数字化与精细化运营。",
                        appIcon = R.mipmap.icon_gxm_personal,
                        appOriginalId = Constants.MINI_GXM_PERSONAL_ORIGINAL_ID,
                    ),
                    MiniInfo(
                        appName = "钢小秘企业版",
                        appPath = Constants.MINI_GXM_ENTERPRISE_PATH,
                        appDescription = "优秀钢材供应商名录。",
                        appFullDescription = "优秀钢材供应商名录是一个面向钢铁行业的权威资源集合，汇聚全国范围内优质钢厂与实力钢贸企业信息。通过严格筛选与持续更新，帮助用户快速识别信誉良好、供货稳定的合作伙伴。名录覆盖多品类钢材资源，支持按地区、品种等维度查找，为采购方降低选商成本、提升合作效率，也为优质供应商提供精准展示与对接机会，促进钢材供需双方高效合作。",
                        appIcon = R.mipmap.icon_gxm_enterprise,
                        appOriginalId = Constants.MINI_GXM_ENTERPRISE_ORIGINAL_ID,
                    ),
                    MiniInfo(
                        appName = "采购通-10分钟报价",
                        appPath = Constants.MINI_CGT_PATH,
                        appDescription = "钢材采购全网最快！",
                        appFullDescription = "采购通依托中关村在线在科技产业的深厚资源，旨在为 IT 数码行业的经销商、零售商及企业采购方提供高效的供应链解决方案。作为产业互联网工具，它连接了上游优质供应商与下游渠道商，致力于解决传统 IT 采购中信息不对称、效率低等痛点。",
                        appIcon = R.mipmap.icon_cgt,
                        appOriginalId = Constants.MINI_CGT_ORIGINAL_ID,
                    ),
                )
            )
        }
    }


    /**
     * 检查更新
     */
    private fun checkUpdate() {
        request(
            api = { ApiService.instance().check(Constants.API_KEY, Constants.PGYER_KEY) },
            onSuccess = {
                setState {
                    copy(
                        hasUpdate = (it.buildVersionNo?.toInt() ?: 0) > BuildConfig.VERSION_CODE,
                        isForceUpdate = it.buildUpdateDescription?.split("===")[1]?.contains("isForceUpdate=true")
                            ?: false,
                        updateContent = it.buildUpdateDescription?.split("===")[0] ?: ""
                    )
                }
            }
        )
    }


    private fun downloadApp(appKey: String, password: String) {
        val url = downloadUrl(appKey, password)
        sendEffect { HomeUiEffect.DownLoadApp(url) }
    }


    private fun changeDownLoadDialogState() {
        setState { copy(hasUpdate = !hasUpdate) }
    }

    /**
     * 开始下载应用
     */
    private fun startAppDownload(appKey: String, password: String) {
        // 设置下载中状态
        setState {
            val newStates = appDownloadStates.toMutableMap()
            newStates[appKey] = AppDownloadInfo(
                appKey = appKey,
                state = DownloadState.DOWNLOADING,
                progress = 0
            )
            copy(appDownloadStates = newStates)
        }
        // 发送下载 Effect
        val url = downloadUrl(appKey, password)
        sendEffect { HomeUiEffect.StartDownloadApp(appKey, password, url) }
    }

    /**
     * 更新应用下载进度
     */
    private fun updateAppDownloadProgress(appKey: String, progress: Int) {
        setState {
            val newStates = appDownloadStates.toMutableMap()
            val currentInfo = newStates[appKey] ?: AppDownloadInfo(appKey = appKey)
            newStates[appKey] = currentInfo.copy(
                state = DownloadState.DOWNLOADING,
                progress = progress
            )
            copy(appDownloadStates = newStates)
        }
    }

    /**
     * 应用下载完成
     */
    private fun appDownloadCompleted(appKey: String, downloadUri: android.net.Uri?) {
        setState {
            val newStates = appDownloadStates.toMutableMap()
            newStates[appKey] = AppDownloadInfo(
                appKey = appKey,
                state = DownloadState.COMPLETED,
                progress = 100,
                downloadUri = downloadUri
            )
            copy(appDownloadStates = newStates)
        }
    }

    /**
     * 应用下载失败
     */
    private fun appDownloadFailed(appKey: String) {
        setState {
            val newStates = appDownloadStates.toMutableMap()
            newStates[appKey] = AppDownloadInfo(
                appKey = appKey,
                state = DownloadState.IDLE,
                progress = 0
            )
            copy(appDownloadStates = newStates)
        }
        sendEffect { HomeUiEffect.ShowToast("下载失败") }
    }

    /**
     * 安装应用
     */
    private fun installApp(appKey: String) {
        val downloadInfo = _uiState.value.appDownloadStates[appKey]
        val downloadUri = downloadInfo?.downloadUri
        if (downloadUri != null) {
            sendEffect { HomeUiEffect.InstallApp(downloadUri) }
        } else {
            sendEffect { HomeUiEffect.ShowToast("安装文件不存在") }
        }
    }

}