package lib.base.design.ui.viewModel

import android.app.Application
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import eth.ext.bind
import eth.model.Nive
import lib.base.design.repo.AlbumRepo
import vector.app.viewmodel.ViewModelEx
import vector.compat.media.MediaData
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2018/8/12
 */
@HiltViewModel
class AlbumViewModel @Inject constructor(private val repo: AlbumRepo, app: Application) : ViewModelEx(app) {

    val data = Nive<List<MediaData>>()

    fun fetchPhotos() =
        repo.fetchPhotos().bind(data).launch(viewModelScope)
}