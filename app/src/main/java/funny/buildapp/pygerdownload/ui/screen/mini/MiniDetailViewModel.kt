package funny.buildapp.pygerdownload.ui.screen.mini

import funny.buildapp.pygerdownload.util.BaseMviViewModel

class MiniDetailViewModel :
    BaseMviViewModel<MiniDetailUiState, MiniDetailUiAction, MiniDetailUiEffect>(MiniDetailUiState()) {

    override fun handleAction(action: MiniDetailUiAction) {
        when (action) {
            is MiniDetailUiAction.GoBack -> sendEffect { MiniDetailUiEffect.GoBack }
            is MiniDetailUiAction.FetchData -> setState { copy(item = action.item) }
            is MiniDetailUiAction.GoPreview -> sendEffect { MiniDetailUiEffect.GoMini(true) }
            is MiniDetailUiAction.GoRelease -> sendEffect { MiniDetailUiEffect.GoMini(false) }
        }
    }

    override fun updateLoading() {
    }
}