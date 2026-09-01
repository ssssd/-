package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import com.example.core.audio.WhiteNoiseType
import com.example.core.localization.LocalStrings
import com.example.ui.theme.GlassCardBackgroundDark
import com.example.ui.theme.GlassCardBorderDark
import com.example.ui.theme.PriorityHighColor
import com.example.ui.theme.PriorityLowColor
import com.example.ui.theme.PriorityNormalColor
import com.example.ui.theme.PriorityUrgentColor

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 24.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val baseModifier = modifier
        .clip(RoundedCornerShape(cornerRadius))
        .background(
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.08f),
                    Color.White.copy(alpha = 0.02f)
                ),
                start = Offset(0f, 0f),
                end = Offset(400f, 400f)
            )
        )
        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.65f))
        .border(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.1f),
            shape = RoundedCornerShape(cornerRadius)
        )

    val finalModifier = if (onClick != null) {
        baseModifier.clickable(onClick = onClick)
    } else {
        baseModifier
    }

    Box(modifier = finalModifier) {
        content()
    }
}

@Composable
fun ImmersiveHeroProgressRing(
    progress: Float,
    timeText: String,
    labelText: String,
    primaryColor: Color,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 190.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 900),
        label = "hero_progress"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(sizeDp)
    ) {
        // Ambient glow background
        Box(
            modifier = Modifier
                .size(sizeDp - 10.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                )
        )

        val trackColor = Color(0xFF2A2A2A)
        Canvas(modifier = Modifier.size(sizeDp)) {
            val strokeWidth = 10.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(diameter, diameter)

            // Background ring
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )

            // Dynamic progress ring
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(
                        primaryColor,
                        Color(0xFF818CF8),
                        primaryColor
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Inner solid circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(sizeDp - 30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = labelText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MetricRingCard(
    title: String,
    valueText: String,
    subtitle: String,
    progress: Float,
    ringColor: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    deltaText: String? = null,
    isDeltaPositive: Boolean = false
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 800),
        label = "ring_progress"
    )

    GlassCard(
        modifier = modifier,
        cornerRadius = 24.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = ringColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (deltaText != null) {
                    Text(
                        text = deltaText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = if (isDeltaPositive) PriorityLowColor else PriorityUrgentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = valueText,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(38.dp)
                ) {
                    val surfaceBg = Color(0xFF2A2A2A)
                    Canvas(modifier = Modifier.size(34.dp)) {
                        val strokeWidth = 4.dp.toPx()
                        drawCircle(
                            color = surfaceBg,
                            radius = (size.minDimension - strokeWidth) / 2,
                            style = Stroke(width = strokeWidth)
                        )
                        drawArc(
                            color = ringColor,
                            startAngle = -90f,
                            sweepAngle = 360f * animatedProgress,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityBadge(priority: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (priority.uppercase()) {
        "URGENT" -> Triple(PriorityUrgentColor.copy(alpha = 0.15f), PriorityUrgentColor, "Urgent / 紧急")
        "HIGH" -> Triple(PriorityHighColor.copy(alpha = 0.15f), PriorityHighColor, "High / 高")
        "NORMAL" -> Triple(PriorityNormalColor.copy(alpha = 0.15f), PriorityNormalColor, "Medium / 中")
        else -> Triple(PriorityLowColor.copy(alpha = 0.15f), PriorityLowColor, "Low / 低")
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Flag,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun FocusTimerGauge(
    progress: Float,
    formattedTime: String,
    modeTitle: String,
    isStrict: Boolean,
    primaryColor: Color,
    secondaryColor: Color,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 260.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600),
        label = "timer_gauge"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(sizeDp)
    ) {
        val trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        Canvas(modifier = Modifier.size(sizeDp)) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
            val arcSize = Size(diameter, diameter)

            // Background circle
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )

            // Progress Arc with Gradient
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(primaryColor, secondaryColor, primaryColor)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = primaryColor.copy(alpha = 0.12f),
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Text(
                    text = modeTitle,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = primaryColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Text(
                text = formattedTime,
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 44.sp,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (isStrict) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PriorityUrgentColor.copy(alpha = 0.12f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🔒 严格模式",
                        style = MaterialTheme.typography.labelSmall,
                        color = PriorityUrgentColor
                    )
                }
            }
        }
    }
}

@Composable
fun AmbientSoundBar(
    selectedNoise: WhiteNoiseType?,
    volume: Float,
    isPlaying: Boolean,
    onSelectNoise: (WhiteNoiseType) -> Unit,
    onVolumeChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.ambientNoise,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (isPlaying && selectedNoise != null) {
                    Text(
                        text = "播放中 · ${(volume * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                WhiteNoiseType.entries.forEach { type ->
                    val isSelected = selectedNoise == type && isPlaying
                    val (label, icon) = when (type) {
                        WhiteNoiseType.RAIN -> Pair(strings.noiseRain, Icons.Default.WaterDrop)
                        WhiteNoiseType.FOREST -> Pair(strings.noiseForest, Icons.Default.Park)
                        WhiteNoiseType.CAFE -> Pair(strings.noiseCafe, Icons.Default.Headphones)
                        WhiteNoiseType.OCEAN_WAVES -> Pair(strings.noiseWaves, Icons.Default.Waves)
                        WhiteNoiseType.FIREPLACE -> Pair(strings.noiseFireplace, Icons.Default.LocalFireDepartment)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSelectNoise(type) }
                            .testTag("noise_chip_${type.name}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            if (isPlaying) {
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = volume,
                    onValueChange = onVolumeChange,
                    valueRange = 0.05f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.height(24.dp)
                )
            }
        }
    }
}

@Composable
fun FocusTrendCurveChart(
    dataPoints: List<Pair<String, Float>>, // Label (e.g. "周一", "09:00", "第1周") to Value (minutes)
    modifier: Modifier = Modifier,
    curveColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = Color(0xFF818CF8),
    unitLabel: String = "分钟"
) {
    val maxMinutes = dataPoints.maxOfOrNull { it.second } ?: 0f
    val ceilingValue = max(maxMinutes * 1.25f, 60f)
    val totalMinutes = dataPoints.sumOf { it.second.toDouble() }.toFloat()
    val averageMinutes = if (dataPoints.isNotEmpty()) totalMinutes / dataPoints.size else 0f
    val peakIndex = dataPoints.indices.maxByOrNull { dataPoints[it].second }

    Column(modifier = modifier.fillMaxWidth()) {
        // Stats summary row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text(
                        text = "周期总专注",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(totalMinutes / 60).toInt()}h ${(totalMinutes % 60).toInt()}m",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = curveColor
                    )
                }

                Column {
                    Text(
                        text = "均值 / 节点",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${averageMinutes.toInt()} $unitLabel",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (maxMinutes > 0f) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = curveColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "最高峰值: ${maxMinutes.toInt()}m",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = curveColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Curve Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val bottomPadding = 20.dp.toPx()
                val topPadding = 16.dp.toPx()
                val leftPadding = 12.dp.toPx()
                val rightPadding = 12.dp.toPx()

                val chartWidth = width - leftPadding - rightPadding
                val chartHeight = height - topPadding - bottomPadding
                val bottomY = height - bottomPadding

                // Draw horizontal subtle dashed guide lines
                val steps = 3
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                for (i in 0..steps) {
                    val y = bottomY - (chartHeight / steps) * i
                    drawLine(
                        color = Color.White.copy(alpha = 0.08f),
                        start = Offset(leftPadding, y),
                        end = Offset(width - rightPadding, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = if (i > 0) dashEffect else null
                    )
                }

                if (dataPoints.isNotEmpty()) {
                    val stepX = if (dataPoints.size > 1) chartWidth / (dataPoints.size - 1) else 0f
                    val points = dataPoints.mapIndexed { index, pair ->
                        val x = leftPadding + index * stepX
                        val normalizedY = (pair.second / ceilingValue).coerceIn(0f, 1f)
                        val y = bottomY - (normalizedY * chartHeight)
                        Offset(x, y)
                    }

                    // Build smooth Cubic Bézier curve Path
                    val curvePath = Path()
                    val fillPath = Path()

                    curvePath.moveTo(points.first().x, points.first().y)
                    fillPath.moveTo(points.first().x, bottomY)
                    fillPath.lineTo(points.first().x, points.first().y)

                    for (i in 0 until points.size - 1) {
                        val current = points[i]
                        val next = points[i + 1]
                        val cx1 = current.x + (next.x - current.x) / 2f
                        val cy1 = current.y
                        val cx2 = current.x + (next.x - current.x) / 2f
                        val cy2 = next.y

                        curvePath.cubicTo(cx1, cy1, cx2, cy2, next.x, next.y)
                        fillPath.cubicTo(cx1, cy1, cx2, cy2, next.x, next.y)
                    }

                    fillPath.lineTo(points.last().x, bottomY)
                    fillPath.close()

                    // 1. Draw Gradient Area Fill under the curve
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                curveColor.copy(alpha = 0.35f),
                                curveColor.copy(alpha = 0.06f),
                                Color.Transparent
                            ),
                            startY = topPadding,
                            endY = bottomY
                        )
                    )

                    // 2. Draw Glow Stroke beneath the curve
                    drawPath(
                        path = curvePath,
                        color = curveColor.copy(alpha = 0.35f),
                        style = Stroke(
                            width = 6.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // 3. Draw Crisp Main Smooth Curve Line
                    drawPath(
                        path = curvePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(secondaryColor, curveColor, secondaryColor)
                        ),
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // 4. Draw Data Point Dots & Highlights
                    points.forEachIndexed { index, point ->
                        val isPeak = peakIndex == index && dataPoints[index].second > 0f

                        if (isPeak) {
                            drawCircle(
                                color = curveColor.copy(alpha = 0.35f),
                                radius = 8.dp.toPx(),
                                center = point
                            )
                        }

                        drawCircle(
                            color = if (isPeak) curveColor else curveColor.copy(alpha = 0.8f),
                            radius = if (isPeak) 4.5.dp.toPx() else 3.5.dp.toPx(),
                            center = point
                        )

                        drawCircle(
                            color = Color.White,
                            radius = 1.8.dp.toPx(),
                            center = point
                        )
                    }
                }
            }

            if (totalMinutes == 0f) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                    ) {
                        Text(
                            text = "暂无专注时长 · 开启专注后将在此绘制连续趋势曲线",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // X-Axis Labels Row
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 4.dp)
        ) {
            dataPoints.forEach { point ->
                Text(
                    text = point.first,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun EmptyStatePlaceholder(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
