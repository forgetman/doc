package lib.base.design.ui.activity

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import lib.base.databinding.ActivityBaseAlbumBinding
import lib.base.design.adapter.AlbumItemBinder
import lib.base.design.ui.viewModel.AlbumViewModel
import vector.annotation.LayoutBindingClass
import vector.bindingadapter.bind.Bind
import vector.scrollable.widget.layoutmanager.LayoutManagers

/**
 * 相册
 */
@LayoutBindingClass<ActivityBaseAlbumBinding>
abstract class BaseAlbumActivity : BaseDBActivity<AlbumViewModel>() {

    val itemBinder = AlbumItemBinder()
    val layoutManager = LayoutManagers.grid(4)

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityBaseAlbumBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    val onItemClick = ScrollableBind.List.OnItemClick { _, position ->
        val item = viewModel.data.value?.get(position) ?: return@OnItemClick
        val intent = Intent()
        intent.putExtra("EXTRA_PATH", item.relativePath.plus(item.displayName))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
