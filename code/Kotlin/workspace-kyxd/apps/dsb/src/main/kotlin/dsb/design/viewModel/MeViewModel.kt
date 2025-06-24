package dsb.design.viewModel

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dsb.design.repo.MeRepo
import dsb.design.ui.itembinder.me.FormMe
import dsb.model.Me
import eth.ext.bind
import eth.model.Nive
import lib.base.model.User
import live.Live
import vector.EMPTY
import vector.app.viewmodel.ViewModelEx
import javax.inject.Inject

/**
 * @author yuansui
 * @since 2019/1/29
 */
@HiltViewModel
class MeViewModel @Inject constructor(private val repo: MeRepo) : ViewModelEx() {

    companion object {
        private const val CLICK_TO_SIGN_IN = "立即登录"
    }

    val header = FormMe.Header()
    private val origin = Nive<Me>()
    val itemData = MediatorLiveData<List<Any>>().apply {
        addSource(origin) {
            val list= mutableListOf<Any>()

            header.desc.value = it.title
            header.sets.value = it.sets
            list.add(header)

            list.addAll(it.others)
            list.add(FormMe.Footer())

            value = list
        }
    }

    val path = Live<String>()

    var cameraPhotoPath = ""

    override fun onCreate() {
        header.signIn.observe {
            if (it) onSignIn() else onSignOut()
        }

        header.avatar.observe {
            User.get().avatar = it
            User.archive()
        }
    }

    fun fetchData() = repo.fetchData().bind(origin).launch(viewModelScope)

    fun upload(path: String) =
        repo.uploadAvatar(path).bind(header.avatar).launch(viewModelScope)

    private fun onSignIn() {
        header.name.value = User.get().mobile
        header.avatar.value = User.get().avatar
    }

    private fun onSignOut() {
        header.name.value = CLICK_TO_SIGN_IN
        header.avatar.value = EMPTY
    }

    fun onRequestAlbum(resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        data?.let {
            path.value = it.getStringExtra("EXTRA_PATH")
        }
    }

    fun onRequestCamera(resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) return
        path.value = cameraPhotoPath
    }
}