import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import reader.App
import reader.datastore.Settings
import reader.def.FontType
import reader.def.LineSpacingType
import reader.def.ReadTheme
import reader.model.ReadableLines
import sugar.ext.split
import vector.datastore.preference.asEnumFlow
import vector.datastore.preference.defaultEnum
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 阅读器页面绘制组件
 */
@Composable
fun ReaderPage(
    text: String,
    batteryLevel: Int,
    currentPage: Int,
    totalPages: Int,
    modifier: Modifier = Modifier
) {
    val config = rememberReaderConfig()
    val pageContent = remember(text, config.fontSize, config.lineSpacing) {
        prepareTextPages(text, config.fontSize, config.lineSpacing)
    }

    Box(modifier = modifier.fillMaxSize()) {
        // 绘制背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(config.backgroundColor)
        )

        // 绘制文本内容
        if (currentPage < pageContent.size) {
            Text(
                text = pageContent[currentPage].content,
                style = TextStyle(
                    color = config.textColor,
                    fontSize = config.fontSize.sp,
                    fontFamily = config.fontFamily,
                    lineHeight = (config.fontSize + config.lineSpacing).sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp)
                    .align(Alignment.TopStart)
            )
        }

        // 绘制底部信息栏
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // 电量和时间显示
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 电量显示
                BatteryIndicator(
                    batteryLevel = batteryLevel,
                    color = config.textColor,
                    modifier = Modifier.height(16.dp)
                )

                // 时间显示
                Text(
                    text = formatCurrentTime(),
                    style = TextStyle(
                        color = config.textColor,
                        fontSize = 12.sp
                    )
                )
            }

            // 页码显示
            Text(
                text = "第 ${currentPage + 1} 页 / 共 ${pageContent.size} 页",
                style = TextStyle(
                    color = config.textColor,
                    fontSize = 12.sp
                ),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

/**
 * 电量指示器组件
 */
@Composable
fun BatteryIndicator(
    batteryLevel: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedLevel = animateFloatAsState(
        targetValue = batteryLevel.coerceIn(0, 100) / 100f,
        label = "BatteryLevelAnimation"
    )

    Canvas(modifier = modifier.width(24.dp)) {
        val height = size.height
        val width = size.width * 0.8f
        val cornerRadius = 2f

        // 电池外框
        drawRoundRect(
            color = color,
            topLeft = Offset((size.width - width) / 2, 0f),
            size = Size(width, height),
            cornerRadius = cornerRadius,
            style = Stroke(width = 1.5f)
        )

        // 电池正极
        drawRect(
            color = color,
            topLeft = Offset(width + (size.width - width) / 2 - 1.5f, height * 0.3f),
            size = Size(3f, height * 0.4f)
        )

        // 电量填充
        if (animatedLevel.value > 0) {
            val padding = 2f
            val levelWidth = (width - padding * 2) * animatedLevel.value

            drawRoundRect(
                color = when {
                    animatedLevel.value < 0.2 -> Color.Red
                    animatedLevel.value < 0.5 -> Color.Yellow
                    else -> color
                },
                topLeft = Offset((size.width - width) / 2 + padding, padding),
                size = Size(levelWidth, height - padding * 2),
                cornerRadius = cornerRadius - 1
            )
        }
    }
}

/**
 * 记住阅读器配置
 */
@Composable
fun rememberReaderConfig(): ReaderConfig {
    val context = LocalContext.current
    val density = LocalDensity.current

    // 从设置中获取配置流
    val fontSizeFlow = Settings.fontSize.asEnumFlow<FontSize>()
        .filterNotNull()
        .map { it.toSp().value }

    val lineSpacingFlow = Settings.lineSpacingType.asEnumFlow<LineSpacingType>()
        .filterNotNull()
        .map { it.toSp().value }

    val textColorFlow = Settings.readTheme.asEnumFlow<ReadTheme>()
        .filterNotNull()
        .map { it.textColor() }

    val backgroundColorFlow = Settings.readTheme.asEnumFlow<ReadTheme>()
        .filterNotNull()
        .map { it.backgroundColor() }

    val fontTypeFlow = Settings.fontType.asEnumFlow<FontType>()
        .map { it?.path }

    // 组合配置流
    val configFlow = combine(
        fontSizeFlow,
        lineSpacingFlow,
        textColorFlow,
        backgroundColorFlow,
        fontTypeFlow
    ) { fontSize, lineSpacing, textColor, backgroundColor, fontPath ->
        ReaderConfig(
            fontSize = fontSize,
            lineSpacing = lineSpacing,
            textColor = Color(textColor),
            backgroundColor = Color(backgroundColor),
            fontFamily = loadFontFamily(fontPath)
        )
    }

    // 将Flow转换为State
    var config by remember { mutableStateOf(ReaderConfig()) }

    LaunchedEffect(Unit) {
        configFlow.collect {
            config = it
        }
    }

    return config
}

/**
 * 加载字体家族
 */
@Composable
private fun loadFontFamily(fontPath: String?): FontFamily {
    if (fontPath == null) return FontFamily.Default

    val context = LocalContext.current
    return remember(fontPath) {
        try {
            val font = Font(context.assets.open(fontPath))
            FontFamily(font)
        } catch (e: Exception) {
            FontFamily.Default
        }
    }
}

/**
 * 准备文本分页
 */
private fun prepareTextPages(text: String, fontSize: Float, lineSpacing: Float): List<PageContent> {
    // 这里简化处理，实际应该根据屏幕尺寸和字体计算
    val linesPerPage = 30 // 假设每页30行
    val lineHeight = fontSize + lineSpacing

    // 分割文本为行
    val lines = text.split('\n')

    // 分页
    val pages = mutableListOf<PageContent>()
    var currentPageLines = mutableListOf<String>()

    for (line in lines) {
        currentPageLines.add(line)

        if (currentPageLines.size >= linesPerPage) {
            pages.add(PageContent(currentPageLines.joinToString("\n")))
            currentPageLines = mutableListOf()
        }
    }

    // 添加最后一页
    if (currentPageLines.isNotEmpty()) {
        pages.add(PageContent(currentPageLines.joinToString("\n")))
    }

    return pages
}

/**
 * 格式化当前时间
 */
private fun formatCurrentTime(): String {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
}

/**
 * 阅读器配置数据类
 */
data class ReaderConfig(
    val fontSize: Float = 16f,
    val lineSpacing: Float = 4f,
    val textColor: Color = Color.Black,
    val backgroundColor: Color = Color.White,
    val fontFamily: FontFamily = FontFamily.Default
)

/**
 * 页面内容数据类
 */
data class PageContent(val content: String)