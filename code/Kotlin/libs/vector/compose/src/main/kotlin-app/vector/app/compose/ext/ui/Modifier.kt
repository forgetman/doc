package vector.app.compose.ext.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.safeContentTopPadding(): Modifier {
    return this.padding(top = WindowInsets.safeContentTop.toDp())
}

@Composable
fun Modifier.safeContentBottomPadding(): Modifier {
    return this.padding(bottom = WindowInsets.safeContentBottom.toDp())
}

@Composable
fun Modifier.statusBarTopPadding(): Modifier {
    return this.padding(top = WindowInsets.statusBarsTop.toDp())
}

@Composable
fun Modifier.statusBarBottomPadding(): Modifier {
    return this.padding(bottom = WindowInsets.statusBarsBottom.toDp())
}

@Composable
fun Modifier.safeContentTopValuePadding(): Modifier {
    return this.padding(top = WindowInsets.safeContentTopValue.toDp())
}

@Composable
fun Modifier.safeContentBottomValuePadding(): Modifier {
    return this.padding(bottom = WindowInsets.safeContentBottomValue.toDp())
}

fun Modifier.nestedPagerScroll(
    pagerState: PagerState,
    listState: LazyListState,
    reverseLayout: Boolean = false,
    flingThreshold: Float = 2000f
): Modifier = this.then(
    Modifier.nestedScroll(
        object : NestedScrollConnection {

            // 判断是否在“逻辑顶部”
            fun atLogicalTop(): Boolean {
                return if (reverseLayout) !listState.canScrollForward else !listState.canScrollBackward
            }

            // 判断是否在“逻辑底部”
            fun atLogicalBottom(): Boolean {
                return if (reverseLayout) !listState.canScrollBackward else !listState.canScrollForward
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (available.y < 0f && atLogicalBottom()) {
                    return Offset.Zero
                }
                if (available.y > 0f && atLogicalTop()) {
                    return Offset.Zero
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (available.y < -flingThreshold && atLogicalBottom()) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    return Velocity.Zero
                }
                if (available.y > flingThreshold && atLogicalTop()) {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    return Velocity.Zero
                }
                return super.onPreFling(available)
            }
        }
    )
)

fun Modifier.dashedBackground(
    color: Color,
    dashWidth: Dp = 5.dp,
    dashGap: Dp = 5.dp,
    strokeWidth: Dp = 1.dp,
    shape: Shape = RectangleShape
) = drawWithContent {
    drawContent()
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth.toPx(), dashGap.toPx()))
    )
    val path = Path()
    when (val outline = shape.createOutline(size, layoutDirection, this)) {
        is Outline.Rectangle -> {
            path.addRect(outline.rect)
        }

        is Outline.Rounded -> {
            path.addRoundRect(outline.roundRect)
        }

        is Outline.Generic -> {
            path.addPath(outline.path)
        }
    }
    drawPath(
        path = path,
        color = color,
        style = stroke
    )
}