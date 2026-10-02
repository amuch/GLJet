package ddns.net.muchserver.gljet.composables


import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import ddns.net.muchserver.gljet.render.GameSurfaceView
import kotlin.math.abs

@Composable
fun DPad(
    modifier: Modifier,
    glView: GameSurfaceView?
) {
    Canvas(
        modifier = modifier.then(
            Modifier.pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        setMovementJet(glView, it, size)
                    },
                    onDrag = { change, dragAmount ->
                        setMovementJet(glView, change.position, size)
                    },
                    onDragEnd = {
                        glView?.setIdle()
                    },
                    onDragCancel = {
                        glView?.setIdle()
                    }
                )
            }
        )
    ) {
        drawCircle(
            color = Color(0x80507B9C),
            radius = size.width / 2.0f,
        )
    }
}

fun calculateOffset(offsetTap: Offset, size: IntSize): Offset {
    val offsetX = (-(offsetTap.x - (size.width / 2f)) * 2f).coerceIn(-size.width / 2f, size.width / 2f)
    val offsetY = (-(offsetTap.y - (size.height / 2f)) * 2f).coerceIn(-size.height / 2f, size.height / 2f)
    return Offset(offsetX, offsetY)
}

fun setMovementJet(glView: GameSurfaceView?, offset: Offset, size: IntSize) {
    val offsetLocal = calculateOffset(offset, size)
    val x = offsetLocal.x
    val y = offsetLocal.y

    if(abs(x) > abs(y)) {
        if(x < 0) {
            glView?.moveRight()
        }
        else {
            glView?.moveLeft()
        }
    }
    else {
        if(y < 0) {
            glView?.moveDown()
        }
        else {
            glView?.moveUp()
        }
    }
}
