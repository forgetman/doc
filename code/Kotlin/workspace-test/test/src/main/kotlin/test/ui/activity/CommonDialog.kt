package test.ui.activity

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import logger.L
import sugar.ext.observeCreate
import sugar.ext.observeDestroy
import sugar.ext.observeStart
import test.databinding.DialogCommonBinding
import vector.app.databinding.dialog.DBDialogEx

/**
 * 通用的dialog
 * @author : GuoXuan
 * @since : 2018/8/9
 */
class CommonDialog constructor(context: Context) : DBDialogEx(context) {

    override fun createBinding(layoutInflater: LayoutInflater): ViewDataBinding {
        return DialogCommonBinding.inflate(layoutInflater)
    }

    init {
        observeCreate {
            L.d("CommonDialog onCreate")
        }

        observeStart {
            L.d("CommonDialog onStart")
        }

        observeDestroy {
            L.d("CommonDialog onDestroy")
        }
    }

    override fun initializeContentView() {
        super.initializeContentView()
    }
}