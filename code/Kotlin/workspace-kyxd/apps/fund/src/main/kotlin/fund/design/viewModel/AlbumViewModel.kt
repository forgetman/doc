package fund.design.viewModel

/**
 * @author yuansui
 * @since 2018/8/12
 */
//class AlbumViewModel : ViewModelEx<Album, AlbumAdapter>() {
//
//    private val repo = AlbumRepo()
//    val selectLive = Live<String>()
//
//    fun getPhotos() = repo.getPhotos()
//
//    // TODO: 没调用
//    fun photosLive() = repo.photos.observe {
//        addAll(it)
//        invalidate()
//    }
//
//    override fun onItemClick(v: View?, position: Int) {
//        val item = getItem(position) ?: return
//        selectLive.value = item.path
//    }
//}