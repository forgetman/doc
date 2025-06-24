package fund.design.viewModel

import fund.design.repo.MeRepo
import fund.model.Me
import fund.model.MeLayoutStyle
import fund.model.MeType
import lib.base.Sp
import lib.base.model.User
import live.Live
import live.Live

<Boolean>
import live.LiveList
import live.Live

<String>
import vector.EMPTY
import vector.design.viewModel.ViewModelEx

/**
 * @author yuansui
 * @since 2018/8/1
 */
class MeViewModel : ViewModelEx() {

    companion object {
        private const val WAIT_LOGIN = "点击登录"
    }

    private val repo = MeRepo()

    val path = Live<String>()
    var cameraPhotoPath = ""

    val name = Live<String>().apply { value = WAIT_LOGIN }
    val avatar = Live<String>().apply { value = User.get().avatar }
    val login = Live<Boolean>().apply { value = Sp.isLogin() }

    val data = LiveList<Me>()

    init {
        if (Sp.isLogin()) {
            onLogin()
        } else {
            onLogout()
        }
    }

    private fun createDataWithLogin() =
        mutableListOf<Me>().apply {
            add(Me(MeLayoutStyle.TEXT, MeType.ORDER))
            add(Me(MeLayoutStyle.TEXT, MeType.DIFF))
            add(Me(MeLayoutStyle.TEXT, MeType.PERSON))
            add(Me(MeLayoutStyle.TEXT, MeType.UPLOAD))

            add(Me(MeLayoutStyle.DIVIDER, MeType.DIVIDER))

            add(Me(MeLayoutStyle.TEXT, MeType.STORE))

            add(Me(MeLayoutStyle.DIVIDER, MeType.DIVIDER))

            add(Me(MeLayoutStyle.TEXT, MeType.ABOUT))

            add(Me(MeLayoutStyle.LOGOUT, MeType.LOGOUT))
        }


    private fun createDataWithLogout() =
        mutableListOf<Me>().apply {
            add(Me(MeLayoutStyle.TEXT, MeType.ORDER))
            add(Me(MeLayoutStyle.TEXT, MeType.DIFF))
            add(Me(MeLayoutStyle.TEXT, MeType.PERSON))
            add(Me(MeLayoutStyle.TEXT, MeType.UPLOAD))

            add(Me(MeLayoutStyle.DIVIDER, MeType.DIVIDER))

            add(Me(MeLayoutStyle.TEXT, MeType.STORE))

            add(Me(MeLayoutStyle.DIVIDER, MeType.DIVIDER))

            add(Me(MeLayoutStyle.TEXT, MeType.ABOUT))
        }


    fun onLogin() {
        data.value = createDataWithLogin()
        login.value = false
        name.value = User.get().mobile
        avatar.value = User.get().avatar
    }

    fun onLogout() {
        data.value = createDataWithLogout()
        login.value = false
        name.value = WAIT_LOGIN
        avatar.value = EMPTY
    }

    //    fun upload(path: String) = repo.uploadAvatar(path)
//            .observe {
//                liveAvatar.value = it
//                User.get().avatar = it
//                User.archive()
//
//                toast("上传成功")
//            }.error {
//
//                toast("上传失败")
//            }

    fun getOrderUrl() = repo.orderUrl()

    fun getPeopleUrl() = repo.peopleUrl()

    fun getFundUrl() = repo.fundUrl()

    fun getAboutUrl() = repo.aboutUrl()

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode != Activity.RESULT_OK) return
//
//        when (requestCode) {
//            MeFrag.REQUEST_ALBUM -> {
//                data?.let {
//                    val path = it.getStringExtra(Consts.EXTRA_PATH)
//                    livePath.value = path
//                }
//            }
//            MeFrag.REQUEST_CAMERA -> {
//                livePath.value = cameraPhotoPath
//            }
//        }
//    }
}