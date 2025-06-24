package reader.pattern.activity

import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import dagger.hilt.android.AndroidEntryPoint
import live.Live
import reader.EventId
import reader.bus
import reader.databinding.ActivityMainBinding
import reader.db.Db
import reader.pattern.frag.main.BookCityFrag
import reader.pattern.frag.main.BookshelfFrag
import vector.app.adapter.pager.AdapterPager
import vector.app.adapter.pager.FragPager
import vector.app.adapter.pager.build
import vector.app.databinding.activity.SimpleDBActivityEx
import vector.app.databinding.annotation.LayoutBindingClass
import vector.ext.toast

@AndroidEntryPoint
@LayoutBindingClass<ActivityMainBinding>
class MainActivity : SimpleDBActivityEx() {

    companion object {
        const val TAB_BOOKSHELF = 0
        const val TAB_BOOK_CITY = 1
    }

    val pager = FragPager.build(
        creators = listOf(
            AdapterPager.PagerCreator { BookshelfFrag() },
            AdapterPager.PagerCreator { BookCityFrag() })
    )

    val currIndex = Live(TAB_BOOKSHELF)

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityMainBinding.inflate(inflater)
        binding.owner = this
        return binding
    }

    override fun initializeContentView() {
//        Settings.dayNightMode.asEnumFlow<DayNightMode>()
//            .filterNotNull()
//            .distinctUntilChanged()
//            .onEach {
//                setDayNightMode(it)
//            }.launchIn(this)

        bus.onMessage(EventId.SWITCH_TO_BOOK_CITY) {
            currIndex.value = TAB_BOOK_CITY
        }
    }

    private var lastExitTime = 0L
    private val enableExit: Boolean
        get() {
            val time = System.currentTimeMillis()
            return if (time - lastExitTime > 2000) {
                lastExitTime = time
                false
            } else {
                true
            }
        }

    override fun handleOnBackPressed() {
        if (enableExit) {
            super.handleOnBackPressed()
        } else {
            toast("再按一次退出")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        Db.close()
    }
}