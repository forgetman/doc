package lib.base.design.adapter

import lib.base.databinding.LayoutItemAlbumBinding
import lib.base.databinding.LayoutItemTestBinding
import lib.base.model.Test
import vector.app.databinding.adapter.binder.DBItemBinder
import vector.compat.media.MediaData

class TestItemBinder : DBItemBinder<Test, LayoutItemTestBinding>() {
    override fun onBindBinding(item: Test, binding: LayoutItemTestBinding, position: Int) {
        binding.item = item
    }
}

class AlbumItemBinder : DBItemBinder<MediaData, LayoutItemAlbumBinding>() {
    override fun onBindBinding(item: MediaData, binding: LayoutItemAlbumBinding, position: Int) {
        binding.item = item
    }
}