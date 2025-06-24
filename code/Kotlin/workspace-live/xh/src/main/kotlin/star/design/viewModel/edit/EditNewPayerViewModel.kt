package star.design.viewModel.edit

import android.app.Application
import live.Live


/**
 * @author yuansui
 * @since 2020/4/14
 */
class EditNewPayerViewModel(app: Application) : BaseEditInfoViewModel(app) {

    val input: NewPayerInput = NewPayerInput()

}

class NewPayerInput {
    var zoneCount = Live<Int>()
    var platformCount = Live<Int>()
}