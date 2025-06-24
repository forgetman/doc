package fund.design.ui.activity

import fund.ext.addNavBack
import fund.ext.withViewState
import lib.base.design.ui.activity.BaseAlbumActivity

/**
 * @author yuansui
 * @since 2019/1/30
 */
class AlbumActivity : BaseAlbumActivity() {

    override fun flowOfNavBar() {
        addNavBack()
        navBar.mid.addText("相册")
    }

    override fun flowOfSetup() {
        viewModel.getPhotos().withViewState(this)
    }
}