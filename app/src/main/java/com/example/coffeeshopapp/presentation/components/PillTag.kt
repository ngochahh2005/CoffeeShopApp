package com.example.coffeeshopapp.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.coffeeshopapp.data.model.entity.CartItemTopping
import kotlin.collections.forEach

@Composable
fun PillTag(
    text: String,
    isSize: Boolean = false
) {
    val backgroundColor = if (isSize) Color(0xFFEEEDFE) else Color(0xFFF1F0EF)
    val textColor = if (isSize) Color(0xFF3C3489) else Color(0xFF5F5E5A)

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = textColor,
            fontWeight = if (isSize) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun FlowTagRow(
    size: String?,
    toppings: List<CartItemTopping>,
    maxLines: Int = 1
) {
    val allTags = buildList {
        size?.let { add(Pair(it, true)) }
        toppings.forEach { add(Pair("+ ${it.name}", false)) }
    }

    if (allTags.isEmpty()) return

    SubcomposeLayout { constraints ->
        val maxWidth = constraints.maxWidth
        val gap = 6.dp.roundToPx()
        val rowGap = 6.dp.roundToPx()

        val tagPlaceables = subcompose("tags") {
            allTags.forEach { (text, isSize) -> PillTag(text, isSize) }
        }.map { it.measure(constraints.copy(minWidth = 0)) }

        // ✅ Guard: nếu không đo được gì thì không render
        if (tagPlaceables.isEmpty()) {
            return@SubcomposeLayout layout(0, 0) {}
        }

        data class Line(val items: MutableList<Int> = mutableListOf(), var width: Int = 0)
        val lines = mutableListOf(Line())

        for (i in tagPlaceables.indices) {
            val p = tagPlaceables[i]
            val currentLine = lines.last()
            // ✅ Nếu maxWidth = 0 (Preview chưa đo xong), cứ add hết vào 1 dòng
            val addedWidth = if (currentLine.items.isEmpty()) p.width else gap + p.width
            if (maxWidth == 0 || currentLine.width + addedWidth <= maxWidth) {
                currentLine.items.add(i)
                currentLine.width += addedWidth
            } else {
                if (lines.size >= maxLines) break
                lines.add(Line(mutableListOf(i), p.width))
            }
        }

        val totalShown = lines.sumOf { it.items.size }
        val overflowCount = allTags.size - totalShown
        val lineHeight = tagPlaceables.maxOfOrNull { it.height } ?: 0  // ✅ maxOfOrNull thay vì maxOf
        val totalHeight = if (lines.isEmpty()) 0
        else lines.size * lineHeight + (lines.size - 1) * rowGap

        if (overflowCount > 0) {
            val overflowPlaceable = subcompose("overflow") {
                PillTag("+$overflowCount", isSize = false)
            }.firstOrNull()?.measure(constraints.copy(minWidth = 0))  // ✅ firstOrNull

            if (overflowPlaceable != null) {
                val lastLine = lines.last()
                while (lastLine.items.size > 1) {
                    val lastIdx = lastLine.items.last()
                    val lastP = tagPlaceables[lastIdx]
                    val fits = lastLine.width - gap - lastP.width + gap + overflowPlaceable.width <= maxWidth
                    if (fits) break
                    lastLine.items.removeLast()
                    lastLine.width -= gap + lastP.width
                }
            }

            val finalOverflow = allTags.size - lines.sumOf { it.items.size }
            val finalOverflowPlaceable = subcompose("overflow_final") {
                PillTag("+$finalOverflow", isSize = false)
            }.firstOrNull()?.measure(constraints.copy(minWidth = 0))  // ✅ firstOrNull

            layout(maxWidth.coerceAtLeast(0), totalHeight.coerceAtLeast(0)) {
                lines.forEachIndexed { lineIdx, line ->
                    val y = lineIdx * (lineHeight + rowGap)
                    var x = 0
                    line.items.forEachIndexed { itemIdx, tagIdx ->
                        tagPlaceables[tagIdx].placeRelative(x, y + (lineHeight - tagPlaceables[tagIdx].height) / 2)
                        x += tagPlaceables[tagIdx].width + gap
                        val isLastItem = lineIdx == lines.lastIndex && itemIdx == line.items.lastIndex
                        if (isLastItem && finalOverflowPlaceable != null) {
                            finalOverflowPlaceable.placeRelative(x, y + (lineHeight - finalOverflowPlaceable.height) / 2)
                        }
                    }
                }
            }
        } else {
            layout(maxWidth.coerceAtLeast(0), totalHeight.coerceAtLeast(0)) {
                lines.forEachIndexed { lineIdx, line ->
                    val y = lineIdx * (lineHeight + rowGap)
                    var x = 0
                    line.items.forEach { tagIdx ->
                        tagPlaceables[tagIdx].placeRelative(x, y + (lineHeight - tagPlaceables[tagIdx].height) / 2)
                        x += tagPlaceables[tagIdx].width + gap
                    }
                }
            }
        }
    }
}