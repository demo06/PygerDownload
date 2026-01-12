package funny.buildapp.pygerdownload.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import funny.buildapp.clauncher.util.loge
import funny.buildapp.pygerdownload.model.BaseBean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext


/**
 * MVI 基础 ViewModel
 *
 * STATE  : 页面可持续展示的 UI 状态（会被 Compose 收集）
 * ACTION : UI 触发的用户行为（点击、刷新、加载更多等）
 * EFFECT : 一次性事件（Toast、Dialog、Navigation 等）
 */
abstract class BaseMviViewModel<STATE : Any, ACTION : Any, EFFECT : Any>(
    initialState: STATE
) : ViewModel() {
    private val requestMutex = Mutex()

    /**
     * 内部可变 State
     * - 只能在 ViewModel 内部修改
     * - 使用 MutableStateFlow 以支持 Compose 的状态订阅
     */
    protected val _uiState: MutableStateFlow<STATE> = MutableStateFlow(initialState)

    /**
     * 对外暴露的只读 State
     * - UI 层只能 collect，不能修改
     */
    val uiState: StateFlow<STATE> = _uiState.asStateFlow()

    /**
     * 一次性事件通道（Effect）
     *
     * Channel.BUFFERED：
     * - 避免在短时间内发送多个 Effect 时丢失
     * - 适合导航、Toast 等不需要重放的事件
     */
    private val _effect: Channel<EFFECT> =
        Channel(Channel.BUFFERED)

    /**
     * 对外暴露为 Flow，供 UI 层 collect
     */
    val effect: Flow<EFFECT> = _effect.receiveAsFlow()


    /**
     * UI 行为的唯一入口（MVI 核心约束）
     *
     * Screen 中只能调用 dispatch(action)
     * 不允许直接调用 ViewModel 内部方法
     */
    fun dispatch(action: ACTION) {
        handleAction(action)
    }

    /**
     * 处理 UI Action
     *
     * 子类在这里：
     * - 根据不同 Action 执行业务逻辑
     * - 调用 setState / sendEffect
     */
    protected abstract fun handleAction(action: ACTION)

    /**
     * 原子化更新 UI State
     *
     * 使用 reducer 形式，确保：
     * - 状态不可变
     * - 多线程安全
     *
     * 示例：
     * setState { copy(loading = true) }
     */
    protected fun setState(reducer: STATE.() -> STATE) {
        _uiState.update { currentState ->
            currentState.reducer()
        }
    }

    /**
     * 发送一次性事件（Effect）
     *
     * 示例：
     * sendEffect { Effect.ShowToast("成功") }
     */
    protected fun sendEffect(effect: () -> EFFECT) {
        viewModelScope.launch {
            _effect.send(effect())
        }
    }


    /**
     *  统一控制loading显示隐藏
     */
    var loadingCount = 0

    protected fun startLoading() {
        loadingCount++
        updateLoading()
    }

    protected fun endLoading() {
        loadingCount--
        if (loadingCount < 0) loadingCount = 0
        updateLoading()
    }

    protected abstract fun updateLoading()

    /**
     *
     */
    fun <T> request(
        api: suspend () -> BaseBean<T>,
        onFailed: (msg: String) -> Unit = { },
        onSuccess: (T) -> Unit = {}
    ) {
        startLoading()
        viewModelScope.launch {
            requestMutex.withLock {
                try {
                    val result = withContext(Dispatchers.IO) { api() }
                    withContext(Dispatchers.Main) {
                        if (result.code == 0 && result.data != null) {
                            onSuccess(result.data)
                            endLoading()
                        } else {
                            endLoading()
                            onFailed(result.message?:"")
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        e.message.loge()
                        onFailed("网络加载失败")
                        endLoading()
                    }
                }
            }

        }
    }


}