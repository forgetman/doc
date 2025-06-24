package reader.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import reader.App
import reader.datastore.Settings
import reader.def.FontType
import reader.def.LineSpacingType
import reader.def.ReadTheme
import reader.model.ReadableLines
import sugar.ext.split
import vector.datastore.preference.asEnumFlow
import vector.datastore.preference.defaultEnum
import vector.util.TimeFormatter

/**
 * @author yuansui
 * @since 2019/11/6
 */
object PageDrawer {

    private val BATTERY_HEIGHT: Int = 8.dp.toPx(Mode.FULL_SCREEN)
    private val BATTERY_WIDTH = 20.dp.toPx(Mode.FULL_SCREEN)
    private val BATTERY_INNER_FRAME_MARGIN = 1.dp.toPx(Mode.FULL_SCREEN)
    private val BATTERY_FRAME_STROKE_WIDTH = 1.dp.toPx(Mode.FULL_SCREEN)
    private val BATTERY_MARGIN_BOTTOM = 5.dp.toPx(Mode.FULL_SCREEN)

    private val MARGIN_BOTTOM =
        BATTERY_HEIGHT + BATTERY_FRAME_STROKE_WIDTH + BATTERY_MARGIN_BOTTOM + 5.dp.toPx(Mode.FULL_SCREEN)

    class Config : CoroutineScope by MainScope() {
        val fontSize: StateFlow<Float> = Settings.fontSize.asEnumFlow<FontSize>()
            .filterNotNull()
            .map {
                it.toDimension().toPx(Mode.FULL_SCREEN).toFloat()
            }.stateIn(this, SharingStarted.WhileSubscribed(), 0f)

        val lineSpace: StateFlow<Float> = Settings.lineSpacingType.asEnumFlow<LineSpacingType>()
            .filterNotNull()
            .map {
                it.toDimension().toPx(Mode.FULL_SCREEN).toFloat()
            }.stateIn(this, SharingStarted.WhileSubscribed(), 0f)

        val textColor = Settings.readTheme.asEnumFlow<ReadTheme>()
            .filterNotNull()
            .map {
                it.textColor()
            }.stateIn(
                this,
                SharingStarted.WhileSubscribed(),
                Settings.readTheme.defaultEnum<ReadTheme>()?.textColor() ?: Color.TRANSPARENT
            )

        val backgroundColor = Settings.readTheme.asEnumFlow<ReadTheme>()
            .filterNotNull()
            .map {
                it.backgroundColor()
            }.stateIn(
                this,
                SharingStarted.WhileSubscribed(),
                Settings.readTheme.defaultEnum<ReadTheme>()?.backgroundColor() ?: Color.TRANSPARENT
            )

        val fontTypePath = Settings.fontType.asEnumFlow<FontType>()
            .map {
                it?.path
            }.stateIn(
                this,
                SharingStarted.WhileSubscribed(),
                Settings.fontType.defaultEnum<FontType>()?.path
            )

        val textPaint = combine(fontSize, textColor, fontTypePath) { size, color, path ->
            TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
                textAlign = Paint.Align.LEFT
                textSize = size
                this.color = color
                typeface = getTypeface(path)
                isSubpixelText = true
            }
        }.stateIn(this, SharingStarted.WhileSubscribed(), TextPaint())

        val otherPaint = textPaint.map {
            TextPaint(it).apply {
                textSize = 10.dp.toPx(Mode.FULL_SCREEN).toFloat()
            }
        }.stateIn(this, SharingStarted.WhileSubscribed(), TextPaint())
    }

    private val config: Config = Config()

    private var visibleWidth: Int = 0 // 正文显示的区域宽度
    private var visibleHeight: Int = 0 // 正文显示的区域高度
    private var initialized: Boolean = false

    // 每页可以显示的行数
    private var maxLineCount: Int = 0

    fun initSize(w: Int, h: Int) {
        visibleWidth = w
        visibleHeight = h
        initialized = true
    }

    fun drawText(canvas: Canvas, texts: List<String>?) {
        if (texts == null) return

        var y = 0f
        val lineHeight = config.fontSize.value + config.lineSpace.value
        texts.forEach {
            y += lineHeight
            canvas.drawText(it, 0f, y, config.textPaint.value)
        }
    }

    fun drawBackground(canvas: Canvas) {
        canvas.drawColor(config.backgroundColor.value)
    }

    fun drawBattery(canvas: Canvas, battery: Int) {
        // 画外框
        val top = visibleHeight - (
            BATTERY_HEIGHT
                + BATTERY_FRAME_STROKE_WIDTH
                + BATTERY_MARGIN_BOTTOM
            ).toFloat()
        val rect = RectF(0f, top, BATTERY_WIDTH.toFloat(), top + BATTERY_HEIGHT)
        val paint = Paint(config.textPaint.value).apply {
            style = Paint.Style.STROKE
            strokeWidth = BATTERY_FRAME_STROKE_WIDTH.toFloat()
        }
        canvas.drawRect(rect, paint)

        // 画电量
        rect.left += BATTERY_INNER_FRAME_MARGIN
        rect.top += BATTERY_INNER_FRAME_MARGIN

        val totalRight = BATTERY_WIDTH - BATTERY_INNER_FRAME_MARGIN
        val levelRight = totalRight * (battery / 100f)
        rect.right = levelRight

        rect.bottom -= BATTERY_INNER_FRAME_MARGIN
        paint.style = Paint.Style.FILL
        canvas.drawRect(rect, paint)

        // 画时间
        val timeDesc = TimeFormatter.convert(
            System.currentTimeMillis(),
            TimeFormatter.FormatStyle.TIME_24H_HOUR_TO_MINUTE
        )

        canvas.drawTextInXAlign(
            timeDesc,
            BATTERY_WIDTH + 10f,
            top - BATTERY_HEIGHT / 2,
            config.otherPaint.value,
            Paint.Align.LEFT
        )
    }

    fun drawPageNumber(canvas: Canvas, desc: String?) {
        if (desc == null) return

        val top = visibleHeight - (
            BATTERY_HEIGHT
                + BATTERY_FRAME_STROKE_WIDTH
                + BATTERY_MARGIN_BOTTOM
            ).toFloat()

        canvas.drawTextInXAlign(
            desc,
            visibleWidth.toFloat(),
            top - BATTERY_HEIGHT / 2,
            config.otherPaint.value,
            Paint.Align.RIGHT
        )
    }

    fun prepare(text: String?): List<ReadableLines>? {
        if (!initialized) return null
        if (text == null) return emptyList()

        val layout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val length = text.length
            StaticLayout.Builder.obtain(text, 0, length, config.textPaint.value, visibleWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(config.lineSpace.value, 1f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                text,
                config.textPaint.value,
                visibleWidth,
                Layout.Alignment.ALIGN_NORMAL,
                1f,
                config.lineSpace.value,
                false
            )
        }

        // 计算内容能显示的高度
        val height = visibleHeight - MARGIN_BOTTOM
        val eachLineHeight = config.fontSize.value + config.lineSpace.value
        maxLineCount = (height / eachLineHeight).toInt()// 可显示的行数

        var lineCount = 0
        val lines = mutableListOf<String>()
        for (i in 0 until layout.lineCount) {
            val start = layout.getLineStart(i)
            val end = layout.getLineEnd(i)
            val sub = text.substring(start, end)

            if (lineCount == 0 && sub.startsWith("\n")) {
                // 如果是第一行且是起始为换行符, 略过
                continue
            } else {
                lines.add(sub)
            }
            lineCount++
        }

        return lines.split(maxLineCount).map {
            ReadableLines(it)
        }
    }

    private fun getTypeface(path: String?): Typeface {
        return if (path == null) {
            Typeface.DEFAULT
        } else {
            Typeface.createFromAsset(App.context.assets, path)
        }
    }
}