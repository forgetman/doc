package dsb.design.ui.frag

import android.graphics.Color
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.umeng.socialize.bean.SHARE_MEDIA
import dagger.hilt.android.AndroidEntryPoint
import dsb.Bus
import dsb.Caching
import dsb.EventId
import dsb.R
import dsb.databinding.FragMeBinding
import dsb.design.ui.activity.AlbumActivity
import dsb.design.ui.activity.SetupActivity
import dsb.design.ui.activity.SignInActivity
import dsb.design.ui.activity.WebViewActivityCreator
import dsb.design.ui.itembinder.*
import dsb.design.ui.itembinder.me.FormMe
import dsb.design.viewModel.MeViewModel
import dsb.ext.*
import image.ImageTransformation
import lib.base.Sp
import lib.base.design.dialog.SourcePickDialog
import lib.base.design.frag.BaseDBFrag
import lib.base.model.Form
import lib.um.share.UMShare
import live.ext.get
import logger.L
import vector.MimeType
import vector.annotation.LayoutBindingClass
import vector.app.databinding.frag.DBFragEx
import vector.bindingadapter.bind.Bind
import vector.bindingadapter.trigger.BindTrigger
import vector.ext.startActivity
import vector.ext.startForResult
import vector.ext.toast
import vector.app.os.dp
import vector.app.os.drawableRes
import vector.util.DangerousPerm
import vector.util.EasyPermissions
import vector.util.PhotoUtil
import vector.util.intent.IntentAction

/**
 * @author yuansui
 * @since 2019/1/18
 */
@AndroidEntryPoint
@LayoutBindingClass<FragMeBinding>
class MeFrag : BaseDBFrag<MeViewModel>() {

    val itemBinders = listOf(
        FormMe.ItemBinder.Header(object : FormMe.ItemBinder.Header.Listener {
            override fun onAvatarClick() {
                if (Sp.isSignIn()) {
                    showSourcePicDialog()
                } else {
                    startActivity<SignInActivity>()
                }
            }

            override fun onNameClick() {
                startActivity<SignInActivity>()
            }
        }),
        FormMe.ItemBinder.Footer(object : FormMe.ItemBinder.Footer.Listener {
            override fun onShareClick() {
            }

            override fun onGoodClick() {
            }
        }),
        Form41ItemBinder()
    )
    val transformation = ImageTransformation.Shape.Circle(2.dp.toPx(this), Color.WHITE)
    val refreshTrigger = SwipeRefreshBindTrigger.refresh()
    val toTopTrigger = ScrollableBindTrigger.toTop()

    override val lazyLoadMode: LazyLoadMode
        get() = LazyLoadMode.RESUME

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = FragMeBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeSystemBar() {
        appBar.right.addIcon(R.drawable.nav_bar_ic_setup.drawableRes) {
            startActivity<SetupActivity>()
        }
    }

    override fun flowOfSetup() {
        viewModel.fetchData().withViewState(this)

        viewModel.path.observe {
            viewModel.upload(it)
                .observe {
                    toast("上传成功")
                }.onError {
                    toast("上传失败")
                }.withLoading(this)
        }

        with(viewModel.header) {
            sets.observe(this@MeFrag) {
                visibility.value = it?.isEmpty() ?: true
            }
        }

        Bus.get().with(this).onMessage(EventId.SIGN_IN) {
            refreshTrigger.trig()
            toTopTrigger.trig(false)
            viewModel.header.signIn.value = true
        }

        Bus.get().with(this).onMessage(EventId.SIGN_OUT) {
            viewModel.header.signIn.value = false
        }

        Bus.get().with(this).onMessage(EventId.LOGOUT) {
            viewModel.header.signIn.value = false
        }
    }

    val onSwipe = RefreshBind.OnSwipe {
        viewModel.fetchData().withSwipe(this, it)
    }

    val onItemClick = ScrollableBind.List.OnItemClick { position ->
        val item = viewModel.itemData[position] as? Form ?: return@OnItemClick
        if (item.needLogin && !checkSignIn()) return@OnItemClick
        WebViewActivityCreator.create().url(item.url?.withWebParams()).start(context)
    }

    val onAboutClick = Bind.OnClick {
        startActivity<SetupActivity>()
    }

    val onShareClick = Bind.OnClick {
        UMShare.web {
            host = activity
            title = "大社宝app"
            desc = "大社宝-个人社保公积金代缴平台"
            drawableRes = R.mipmap.ic_launcher
            url = "http://www.dashebao.com/soft/index.html"
            medias = arrayOf(
                SHARE_MEDIA.WEIXIN,
                SHARE_MEDIA.WEIXIN_CIRCLE,
                SHARE_MEDIA.QQ,
                SHARE_MEDIA.QZONE
            )
            onShareResult = { toast("分享成功") }
            onShareError = { _, throwable -> L.e(throwable) }
        }.share()
    }

    val onGoodClick = Bind.OnClick {
        IntentAction.market().alert("没找到").launch()
    }

    private fun showSourcePicDialog() {
        val dialog = SourcePickDialog(requireContext())
        dialog.onAlbumClick = {
            EasyPermissions.request(this, DangerousPerm.STORAGE {
                when (it) {
                    EasyPermissions.Result.GRANT -> goAlbum()
                    else -> toast("请打开相册权限")
                }
            })
        }
        dialog.onCameraClick = {
            EasyPermissions.request(this, DangerousPerm.Camera {
                when (it) {
                    EasyPermissions.Result.GRANT -> goCamera()
                    else -> toast("请打开摄像头权限")
                }
            })
        }
        dialog.show()
    }

    private fun goCamera() {
        val path =
            "${Caching.imageCacheDir}photo${System.currentTimeMillis()}${MimeType.Image.Jpeg.suffix}"
        viewModel.cameraPhotoPath = path
        PhotoUtil.fromCamera(this, path) { resultCode, data ->
            viewModel.onRequestCamera(resultCode, data)
        }
    }

    private fun goAlbum() {
        startForResult<AlbumActivity> { resultCode, data ->
            viewModel.onRequestAlbum(resultCode, data)
        }
    }

    override fun onRetryClick() {
        viewModel.fetchData().withViewState(this).withToast()
    }
}