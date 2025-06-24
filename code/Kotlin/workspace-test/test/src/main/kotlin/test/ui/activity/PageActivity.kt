package test.ui.activity

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import test.databinding.ActivityBookBinding
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.app.fitter.FitStrategy
import vector.app.fitter.Mode

/**
 * @author yuansui
 * @since 2019-06-19
 */
@FitStrategy(Mode.FULL_SCREEN)
class PageActivity : SimpleDBActivityEx() {

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        return ActivityBookBinding.inflate(inflater)
    }
}