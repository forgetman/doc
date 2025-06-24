package reader.pattern.activity

import android.view.LayoutInflater
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import coroutine.flow.launchIn
import coroutine.flow.state.inverse
import coroutine.flow.state.isFalse
import coroutine.flow.state.isTrue
import coroutine.flow.state.toFalse
import dagger.hilt.android.AndroidEntryPoint
import inject.annotation.creator.Creator
import inject.annotation.creator.Extra
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import live.Live
import live.ext.inverse
import live.ext.toFalse
import reader.Bus
import reader.DownloadEvent
import reader.EventId
import reader.R
import reader.databinding.ActivityReadBinding
import reader.datastore.Settings
import reader.def.FontSize
import reader.def.FontType
import reader.def.LineSpacingType
import reader.def.ReadTheme
import reader.ext.addBackIcon
import reader.ext.withViewState
import reader.model.Book
import reader.model.Chapter
import reader.pattern.frag.CatalogFrag
import reader.pattern.viewModel.ReadViewModel
import reader.serv.CacheServCreator
import reader.serv.CacheType
import reader.widget.FontAdjustView
import sugar.ext.SdkInt
import sugar.ext.isSdkAtLeast
import vector.app.appbar.AppBar
import vector.app.databinding.activity.DBActivityEx
import vector.app.databinding.annotation.LayoutBindingClass
import vector.app.ext.bind.bindView
import vector.app.ext.enterFullScreen
import vector.app.ext.quitFullScreen
import vector.app.ext.setOnFullScreenGestureChangedListener
import vector.app.fitter.FitStrategy
import vector.app.fitter.Mode
import vector.app.util.toColor
import vector.bindingadapter.CurrentItem
import vector.bindingadapter.bind.Bind
import vector.compat.notch.DisplayCutoutMode
import vector.compat.notch.NotchInScreenCompat
import vector.datastore.preference.asEnumFlow
import vector.datastore.preference.getEnumOrNull
import vector.datastore.preference.putEnum
import vector.ext.onChanged
import vector.ext.startActivity
import vector.ext.toast
import vector.widget.databinding.viewpager.ViewPagerBind
import vector.util.Brightness.System as SysBrightness
import vector.util.Brightness.Window as WindowBrightness

/**
 * @author yuansui
 * @since 2018/9/7
 */
@FitStrategy(Mode.FULL_SCREEN)
@Creator
@AndroidEntryPoint
@LayoutBindingClass<ActivityReadBinding>
class ReadActivity : DBActivityEx<ReadViewModel>() {

    @Extra
    lateinit var book: Book

    private val theme: StateFlow<ReadTheme> = Settings.readTheme.asEnumFlow<ReadTheme>()
        .filterNotNull()
        .stateIn(lifecycleScope, SharingStarted.WhileSubscribed(), ReadTheme.THEME1)

    val title = MutableStateFlow<String?>(null)
    val titleTextColor = theme.map { it.titleColor() }
        .stateIn(lifecycleScope, SharingStarted.WhileSubscribed(), 0)

    val titleMarginTop = Live<Int?>()

    val systemBarVisible = MutableStateFlow(false)
    val navigationBarMarginBottom = Live<Int>()

    /**
     * 只是为了取消 data set changed 的时候导致的淡入淡出渐变效果
     * @see [ViewPager2.setPageTransformer]
     */
    val pageTransformer = ViewPager2.PageTransformer { _, _ -> }
    val onPageIntent = ViewPagerBind.OnPageIntent {
        title.value = viewModel.data.value.getOrNull(it)?.title
        viewModel.onReadableIndexChanged(it)
    }

    private val innerAppBar by bindView<AppBar>(R.id.layout_app_bar)
    private val darkBackground by bindView<View>(R.id.layout_dark_background)

    // view holders
    val optionCacheViewHolder = OptionCacheViewHolder()
    val optionThemeViewHolder = OptionThemeViewHolder()
    val optionSetupViewHolder = OptionSetupViewHolder()
    val optionChapterViewHolder = OptionChapterViewHolder()
    val chapterDetailViewHolder = CatalogViewHolder()
    private val viewHolders = listOf(
        optionCacheViewHolder,
        optionThemeViewHolder,
        optionSetupViewHolder,
        optionChapterViewHolder,
        chapterDetailViewHolder
    )

    override fun createBinding(inflater: LayoutInflater): ViewDataBinding {
        val binding = ActivityReadBinding.inflate(inflater)
        binding.owner = this
        binding.viewModel = viewModel
        return binding
    }

    override fun initializeSystemBar() {
        innerAppBar.addBackIcon(this)
        innerAppBar.mid.addText(book.name)
        innerAppBar.setBackgroundColor(R.color.bg_primary.toColor(this))
    }

    override fun initializeData() {
        viewModel.bookId = book.id
        viewModel.currItem.value = CurrentItem(book.index, false)
    }

    override fun initializeContentView() {
        theme.onEach {
            setBackgroundColor(it.backgroundColor())
            reloadTheme()
        }.launchIn(this)

        viewHolders.forEach(ViewHolder::initialize)

        notchSetup()

        systemBarVisible.onEach { visible ->
            setSystemBarStatus(visible)

            optionChapterViewHolder.visible.value = visible

            if (!visible) {
                // 隐藏所有设置界面
                optionCacheViewHolder.visible.value = false
                optionThemeViewHolder.visible.value = false
                optionSetupViewHolder.visible.value = false

                chapterDetailViewHolder.visible.value = false
            }
        }.launchIn(this)

        setOnFullScreenGestureChangedListener { _, insetBottom ->
            navigationBarMarginBottom.value = insetBottom
        }

        with(viewModel) {
            chapters.onEach {
                chapterDetailViewHolder.refresh(it)
            }.launchIn(this@ReadActivity)

            currChapterIndex.filterNotNull().onEach {
                chapterDetailViewHolder.updateSelectedIndex(it)
            }.launchIn(this@ReadActivity)

            fetchChapters(book.id).withViewState(this@ReadActivity).launchIn(this@ReadActivity)
        }

        busSetup()

        chapterDetailViewHolder.setup()

        Settings.fontType.asEnumFlow<FontType>().filterNotNull().distinctUntilChanged().onEach {
            reloadConfig()
        }.launchIn(this)
    }

    private fun notchSetup() {
        NotchInScreenCompat.setDisplayCutout(this, DisplayCutoutMode.SHORT_EDGES)
        NotchInScreenCompat.applyListener(this) {
            titleMarginTop.value = it?.bottom
        }
    }

    private fun busSetup() {
        Bus.getInstance().with(this).onValue<String>(EventId.CHAPTER_DOWNLOAD_FINISH) {
            viewModel.onChapterDownload(it)
        }

        Bus.getInstance().with(this).onMessage(EventId.TOUCH_AREA_LEFT) {
            if (optionSetupViewHolder.clickLeftChecked.isTrue()) {
                viewModel.switchPageToNext()
            } else {
                viewModel.switchPageToPrevious()
            }
        }

        Bus.getInstance().with(this).onMessage(EventId.TOUCH_AREA_RIGHT) {
            viewModel.switchPageToNext()
        }

        Bus.getInstance().with(this).onMessage(EventId.TOUCH_AREA_CENTER) {
            // 看是否要读取readableFrag里面的viewState来调整 无网读取不到时, 点击retry的行为
            systemBarVisible.inverse()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (hasFocus && isSdkAtLeast(SdkInt.R_30)) {
            setSystemBarStatus(systemBarVisible.value)
        }
    }

    override fun onResume() {
        super.onResume()

        // 部分机型切换到桌面后, 再切换回来. status bar会被改变显示的状态, 需要重新设置一下
        if (systemBarVisible.isFalse()) {
            enterFullScreen()
        }

        viewHolders.forEach(ViewHolder::onResume)
    }

    override fun onPause() {
        super.onPause()

        viewHolders.forEach(ViewHolder::onPause)
    }

    private fun setSystemBarStatus(visible: Boolean) {
        if (visible) {
            quitFullScreen()
        } else {
            enterFullScreen()
        }
    }

    private fun reloadConfig() {
        viewModel.reloadDataFromConfig()
    }

    private fun reloadTheme() {
        Bus.getInstance().send(EventId.RELOAD_BY_THEME_CHANGED)
    }

    val onChapterClick = Bind.OnClick {
        chapterDetailViewHolder.visible.inverse()

        optionSetupViewHolder.visible.toFalse()
        optionThemeViewHolder.visible.toFalse()
        optionCacheViewHolder.visible.toFalse()
    }

    val onCacheClick = Bind.OnClick {
        optionCacheViewHolder.visible.inverse()

        optionSetupViewHolder.visible.toFalse()
        optionThemeViewHolder.visible.toFalse()
        chapterDetailViewHolder.visible.toFalse()
    }

    val onThemeClick = Bind.OnClick {
        optionThemeViewHolder.visible.inverse()

        optionSetupViewHolder.visible.toFalse()
        optionCacheViewHolder.visible.toFalse()
        chapterDetailViewHolder.visible.toFalse()
    }

    val onSetupClick = Bind.OnClick {
        optionSetupViewHolder.visible.inverse()

        optionCacheViewHolder.visible.toFalse()
        optionThemeViewHolder.visible.toFalse()
        chapterDetailViewHolder.visible.toFalse()
    }

    override fun onRetryClick() {
        viewModel.fetchChapters(book.id).withViewState(this).launchIn(this)
    }

    internal interface ViewHolder {
        fun initialize()
        fun onResume() {}
        fun onPause() {}
    }

    inner class OptionCacheViewHolder : ViewHolder {
        val visible = Live(false)
        val caching = Live<String>()

        override fun initialize() {
            Bus.getInstance().with(this@ReadActivity)
                .onEvent<DownloadEvent>(EventId.CACHE_DOWNLOAD_PROGRESS) {
                    if (it.bookId == book.id) caching.value = it.any
                }

            Bus.getInstance().with(this@ReadActivity)
                .onEvent<DownloadEvent>(EventId.CACHE_DOWNLOAD_FINISH) {
                    if (it.bookId == book.id) caching.value = it.any
                }
        }

        private fun startCaching(type: CacheType) {
            CacheServCreator.create(book, type).start(this@ReadActivity)
            caching.value = "开始缓存..."
        }

        val on50Click = Bind.OnClick {
            startCaching(CacheType.ONLY_50)
        }

        val onLastAllClick = Bind.OnClick {
            startCaching(CacheType.ALL_FROM_CURRENT)
        }

        val onAllClick = Bind.OnClick {
            startCaching(CacheType.ALL)
        }
    }

    inner class OptionThemeViewHolder : ViewHolder {
        val visible = Live(false)
        val brightness = MutableStateFlow<Int>(0)
        val adjustable = MutableStateFlow(false) // 是否可以自由调节亮度

        override fun initialize() {
            // 初始化获取一次系统亮度
            brightness.value = SysBrightness.getPercent()

            Settings.brightnessAdjustable.asFlow().filterNotNull().onEach {
                adjustable.value = it
            }.launchIn(this@ReadActivity)

            SysBrightness.onChanged(this@ReadActivity) { progress ->
                if (adjustable.isFalse()) brightness.value = progress
            }

            brightness.onEach { progress ->
                WindowBrightness.setPercent(window, progress)
            }.launchIn(this@ReadActivity)

            adjustable.onEach { value ->
                if (!value) {
                    WindowBrightness.reset(window)
                }
                lifecycleScope.launch {
                    Settings.brightnessAdjustable.put(value)
                }
            }.launchIn(this@ReadActivity)
        }

        override fun onResume() {
            // 切换回来的时候需要回复一次屏幕亮度的设置
            if (adjustable.isFalse()) {
                WindowBrightness.reset(window)
            }
        }

        private fun onThemeChanged(theme: ReadTheme) {
            lifecycleScope.launch {
                Settings.readTheme.putEnum(theme)
            }
        }

        val onTheme1Click = Bind.OnClick {
            onThemeChanged(ReadTheme.THEME1)
        }

        val onTheme2Click = Bind.OnClick {
            onThemeChanged(ReadTheme.THEME2)
        }

        val onTheme3Click = Bind.OnClick {
            onThemeChanged(ReadTheme.THEME3)
        }

        val onTheme4Click = Bind.OnClick {
            onThemeChanged(ReadTheme.THEME4)
        }
    }

    inner class OptionSetupViewHolder : ViewHolder {
        val visible = MutableStateFlow(false)
        val clickLeftChecked = MutableStateFlow(false)

        private val fontView: FontAdjustView by lazy { findViewById(R.id.layout_font_adjust) }

        override fun initialize() {
            Settings.fontSize.asEnumFlow<FontSize>().filterNotNull()
                .distinctUntilChanged()
                .map { it.ordinal }.onEach {
                    fontView.focusIndex = it
                    reloadConfig()
                    fontView.invalidate()
                }.launchIn(this@ReadActivity)

            Settings.lineSpacingType.asEnumFlow<LineSpacingType>().filterNotNull()
                .distinctUntilChanged()
                .onEach {
                    reloadConfig()
                }.launchIn(this@ReadActivity)

            Settings.clickLeftToNextPage.asFlow().filterNotNull().onEach {
                clickLeftChecked.value = it
            }.launchIn(this@ReadActivity)

            lifecycleScope.launch {
                fontView.listener = {
                    lifecycleScope.launch {
                        Settings.fontSize.putEnum(FontSize.entries.toTypedArray()[it])
                    }
                }
            }

            clickLeftChecked.onEach {
                Settings.clickLeftToNextPage.put(it)
            }.launchIn(this@ReadActivity)
        }

        private fun onLineSpacingChanged(type: LineSpacingType) {
            lifecycleScope.launch {
                Settings.lineSpacingType.putEnum(type)
            }
        }

        val onLineSpacing3Click = Bind.OnClick {
            onLineSpacingChanged(LineSpacingType.LEVEL3)
        }

        val onLineSpacing2Click = Bind.OnClick {
            onLineSpacingChanged(LineSpacingType.LEVEL2)
        }

        val onLineSpacing1Click = Bind.OnClick {
            onLineSpacingChanged(LineSpacingType.LEVEL1)
        }

        val onFontSizeMinusClick = Bind.OnClick {
            lifecycleScope.launch {
                val oldOrdinal = Settings.fontSize.getEnumOrNull<FontSize>()?.ordinal ?: 0
                val newOrdinal = oldOrdinal - 1
                if (newOrdinal < 0) {
                    toast("字已经最小了")
                    return@launch
                }

                val newSize = FontSize.entries.toTypedArray()[newOrdinal]
                Settings.fontSize.putEnum(newSize)
            }
        }

        val onFontSizePlusClick = Bind.OnClick {
            lifecycleScope.launch {
                val oldOrdinal = Settings.fontSize.getEnumOrNull<FontSize>()?.ordinal ?: 0
                val newOrdinal = oldOrdinal + 1
                val fontSizeArray = FontSize.entries.toTypedArray()
                if (newOrdinal > fontSizeArray.lastIndex) {
                    toast("字已经最大了")
                    return@launch
                }

                val newSize = fontSizeArray[newOrdinal]
                Settings.fontSize.putEnum(newSize)
            }
        }

        val onFontStyleClick = Bind.OnClick {
            startActivity<FontDisplayActivity>()
        }
    }

    inner class OptionChapterViewHolder : ViewHolder {
        val visible = Live(false)

        override fun initialize() {
        }

        val onLastChapterClick = Bind.OnClick {
            viewModel.switchToPreviousChapter()
        }

        val onNextChapterClick = Bind.OnClick {
            viewModel.switchToNextChapter()
        }
    }

    /**
     * 弹出的目录
     */
    inner class CatalogViewHolder : ViewHolder {
        val visible = Live<Boolean>()

        private val frag by lazy {
            supportFragmentManager.findFragmentById(R.id.frag_container_catalog) as? CatalogFrag
        }

        override fun initialize() {
            visible.observe(this@ReadActivity) {
                val f = frag ?: return@observe
                if (it) {
                    supportFragmentManager.beginTransaction()
                        .show(f)
                        .commitAllowingStateLoss()

                    darkBackground.visibility = View.VISIBLE
                } else {
                    supportFragmentManager.beginTransaction()
                        .hide(f)
                        .commitAllowingStateLoss()

                    darkBackground.visibility = View.GONE
                }
            }
        }

        fun setup() {
            frag?.bookId = book.id

            frag?.onSelected = {
                viewModel.switchChapterTo(it)
                systemBarVisible.toFalse()
            }
        }

        fun refresh(data: List<Chapter>) {
            frag?.refresh(data)
        }

        fun updateSelectedIndex(index: Int) {
            frag?.updateSelectedIndex(index, true)
        }
    }
}


