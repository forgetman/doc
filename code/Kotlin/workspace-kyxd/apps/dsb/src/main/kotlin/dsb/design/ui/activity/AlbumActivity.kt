package dsb.design.ui.activity

import dagger.hilt.android.AndroidEntryPoint
import dsb.ext.addBackIcon
import dsb.ext.withViewState
import lib.base.design.ui.activity.BaseAlbumActivity

/**
 * @author yuansui
 * @since 2019/1/30
 */
@AndroidEntryPoint
class AlbumActivity : BaseAlbumActivity() {

    override fun initializeSystemBar() {
        appBar.addBackIcon(this)
        appBar.mid.addText("相册")
    }

    override fun flowOfSetup() {
        viewModel.fetchPhotos().withViewState(this)
    }
}