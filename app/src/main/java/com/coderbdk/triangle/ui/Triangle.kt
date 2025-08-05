package com.coderbdk.triangle.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.lang.Math.toDegrees
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class TrianglePoint(
    val position: Offset,
    val length: Float,
    val directionAngle: Float, // degree
    val color: Color
)

@Composable
fun Triangle(modifier: Modifier) {
    val density = LocalDensity.current
    val length = with(density) { 108.dp.toPx() }
    val radius = with(density) { 24.dp.toPx() }
    val interiorAngles by remember { mutableStateOf(arrayOf(60f, 60f, 60f)) }

    val trianglePoints = remember {
        arrayOf(
            TrianglePoint(
                position = Offset(length / 2, 0f),
                length = length,
                directionAngle = 90f,
                color = Color.Red
            ),
            TrianglePoint(
                position = Offset(0f, length),
                length = length,
                directionAngle = 210f,
                color = Color.Green
            ),
            TrianglePoint(
                position = Offset(length, length),
                length = length,
                directionAngle = 330f,
                color = Color.Magenta
            )
        )
    }

    val textMeasurer = rememberTextMeasurer()
    var rotationAngle by remember { mutableFloatStateOf(90.0f) }
    var selectedPointIndex by remember { mutableIntStateOf(-1) }

    var dragStartOffset by remember { mutableStateOf(Offset.Zero) }
    var currentDragOffset by remember { mutableStateOf(Offset.Zero) }

    Column(
        modifier = modifier.padding(16.dp)
    ) {

        Text(
            text = buildString {
                appendLine("Selected Point Index: $selectedPointIndex")
                appendLine("Rotation Angle: ${"%.1f".format(rotationAngle)}°")
                append("Interior Angles: (")
                append(interiorAngles.joinToString(" + ") { angle -> "%.1f".format(angle) })
                append(") = ${"%.1f".format(interiorAngles.sum())}°")
            }
        )

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures { tapOffset ->
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val relativeTap = tapOffset - center

                        trianglePoints.forEachIndexed { index, point ->
                            val rad = Math.toRadians((point.directionAngle).toDouble()).toFloat()
                            val dx = cos(rad) * length
                            val dy = -sin(rad) * length
                            val pointPos = Offset(dx, dy)

                            if ((pointPos - relativeTap).getDistance() < radius * 1.5f) {
                                rotationAngle = point.directionAngle
                                selectedPointIndex = if (selectedPointIndex == index) -1 else index
                                return@detectTapGestures
                            }
                        }
                    }
                }

                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            dragStartOffset = it
                            currentDragOffset = it
                        },
                        onDragEnd = {},
                        onDragCancel = {},
                        onDrag = { _, dragAmount ->
                            currentDragOffset += dragAmount

                            val center = Offset(size.width / 2f, size.height / 2f)
                            val vectorStart = dragStartOffset - center
                            val vectorCurrent = currentDragOffset - center


                            val angleStart = atan2(-vectorStart.y, vectorStart.x)
                            val angleCurrent = atan2(-vectorCurrent.y, vectorCurrent.x)

                            val deltaAngle =
                                toDegrees((angleCurrent - angleStart).toDouble()).toFloat()

                            rotationAngle += deltaAngle
                            dragStartOffset = currentDragOffset
                        }
                    )
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)

            translate(center.x, center.y) {

                trianglePoints.forEachIndexed { index, point ->
                    val newAngle =
                        (if (selectedPointIndex == index) rotationAngle else point.directionAngle)
                    val radian =
                        Math.toRadians(newAngle.toDouble())
                            .toFloat()
                    val dx = cos(radian) * length
                    val dy = -sin(radian) * length
                    val newPosition = Offset(dx, dy)
                    trianglePoints[index] = point.copy(
                        position = newPosition,
                        directionAngle = newAngle
                    )
                }
                trianglePoints.forEachIndexed { index, point ->
                    drawCircle(
                        color = if (selectedPointIndex == index) Color.Cyan else point.color,
                        radius = radius,
                        center = point.position
                    )
                }

                /* for (i in trianglePoints.indices) {
                     val startPoint = trianglePoints[i]
                     val endPoint = trianglePoints[(i + 1) % trianglePoints.size]

                     drawLine(
                         color = if (selectedPointIndex == i) Color.Cyan else Color.Black,
                         start = startPoint.position,
                         end = endPoint.position,
                         strokeWidth = 8f
                     )
                 }*/

                for (i in trianglePoints.indices) {
                    val prev =
                        trianglePoints[(i - 1 + trianglePoints.size) % trianglePoints.size].position
                    val curr = trianglePoints[i].position
                    val next = trianglePoints[(i + 1) % trianglePoints.size].position

                    // Create vectors from current point to previous and next
                    val v1 = Offset(prev.x - curr.x, prev.y - curr.y)
                    val v2 = Offset(next.x - curr.x, next.y - curr.y)

                    // Dot product and magnitude
                    val dot = v1.x * v2.x + v1.y * v2.y
                    val mag1 = hypot(v1.x, v1.y)
                    val mag2 = hypot(v2.x, v2.y)

                    drawLine(
                        color = if (selectedPointIndex == i || (i + 1) % trianglePoints.size == selectedPointIndex) Color.Cyan else Color.Black,
                        start = prev,
                        end = curr,
                        strokeWidth = 8f
                    )

                    // Avoid division by zero
                    if (mag1 != 0f && mag2 != 0f) {
                        val angleRad = acos((dot / (mag1 * mag2)).coerceIn(-1f, 1f))
                        val angleDeg = toDegrees(angleRad.toDouble()).toFloat()
                        interiorAngles[i] = angleDeg
                        drawText(
                            textMeasurer,
                            text = "➤ ${"%.1f".format(trianglePoints[i].directionAngle)}°, ∠ ${
                                "%.1f".format(
                                    angleDeg
                                )
                            }°",
                            topLeft = curr,
                            style = TextStyle(
                                Color.Blue,
                                shadow = Shadow(color = Color.Black, offset = Offset(1f, 2f))
                            )
                        )
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun TrianglePreview() {
    Triangle(
        modifier = Modifier.fillMaxSize()
    )
}