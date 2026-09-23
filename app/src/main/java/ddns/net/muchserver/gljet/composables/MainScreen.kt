package ddns.net.muchserver.gljet.composables

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ddns.net.muchserver.gljet.render.GameSurfaceView
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MainScreen(
    modifier: Modifier
) {
    var glView: GameSurfaceView? by remember { mutableStateOf(null) }
//    val positionTextState = remember { mutableStateOf("X: 0.0, Y: 0.0 Z: 0.0") }
//    val colliderTextState = remember { mutableStateOf("X: 0.0, Y: 0.0 Z: 0.0") }
    val scoreTextState = remember { mutableStateOf("") }

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    Box(modifier) {
        OpenGLView(modifier = Modifier.fillMaxSize()) { view ->
            glView = view
        }
        LaunchedEffect(Unit) {
            while(true) {
                delay(25.milliseconds)
//                val position = glView?.positionText()
//                if(position != null) {
//                    positionTextState.value = position
//                }
//                val collider = glView?.colliderText()
//                if(collider != null) {
//                    colliderTextState.value = collider
//                }
                val score = glView?.scoreText()
                if(score != null) {
                    scoreTextState.value = score
                }
            }
        }
        Text(
            text = scoreTextState.value,
            modifier = Modifier.align(Alignment.TopStart).padding(15.dp).background(Color.White),
            fontSize = 20.sp
        )

//        Text(
//            text = positionTextState.value,
//            modifier = Modifier.align(Alignment.TopStart).padding(15.dp).background(Color.White),
//            fontSize = 20.sp
//        )
//        Text(
//            text = colliderTextState.value,
//            modifier = Modifier.align(Alignment.BottomCenter).padding(15.dp).background(Color.White),
//            fontSize = 20.sp
//        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.20f)
                .fillMaxHeight(0.45f)
                .offset(x = 30.dp)
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ButtonOverlay(
                    modifier = Modifier.padding(5.dp),
                    text = "^",
                    onInteraction = { interaction ->
                        when(interaction) {
                            is PressInteraction.Press -> glView?.moveUp()
                            is PressInteraction.Release -> glView?.setIdle()
                            is PressInteraction.Cancel -> glView?.setIdle()
                        }
                    }
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ButtonOverlay(
                    modifier = Modifier.padding(5.dp),
                    text = "<",
                    onInteraction = { interaction ->
                        when(interaction) {
                            is PressInteraction.Press -> glView?.moveLeft()
                            is PressInteraction.Release -> glView?.setIdle()
                            is PressInteraction.Cancel -> glView?.setIdle()
                        }
                    }
                )
                ButtonOverlay(
                    modifier = Modifier.padding(5.dp),
                    text = ">",
                    onInteraction = { interaction ->
                        when(interaction) {
                            is PressInteraction.Press -> glView?.moveRight()
                            is PressInteraction.Release -> glView?.setIdle()
                            is PressInteraction.Cancel -> glView?.setIdle()
                        }
                    }
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ButtonOverlay(
                    modifier = Modifier.padding(5.dp),
                    text = "v",
                    onInteraction = { interaction ->
                        when(interaction) {
                            is PressInteraction.Press -> glView?.moveDown()
                            is PressInteraction.Release -> glView?.setIdle()
                            is PressInteraction.Cancel -> glView?.setIdle()
                        }
                    }
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .fillMaxHeight(0.2f)
                .align(Alignment.TopEnd)
        ) {

            ButtonOverlay(
                modifier = Modifier.padding(5.dp),
                text = "C",
                onInteraction = { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> glView?.toggleColliderRender()
                    }
                }
            )
            ButtonOverlay(
                modifier = Modifier.padding(5.dp),
                text = "J",
                onInteraction = { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> glView?.reset()
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .fillMaxWidth(0.10f)
                .fillMaxHeight(0.45f)
                .offset(x = (-20).dp, y = (-40).dp),
            verticalArrangement = Arrangement.Bottom
        ) {

            ButtonOverlay(
                modifier = Modifier.padding(5.dp),
                text = "F",
                onInteraction = { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> glView?.fire()
                    }
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(0.30f)
                .fillMaxHeight(0.2f)
                .align(Alignment.TopCenter)
        ) {
            ButtonOverlay(
                modifier = Modifier.padding(5.dp),
                text = "R",
                onInteraction = { interaction ->
                    when(interaction) {
                        is PressInteraction.Press -> glView?.setFollowRear()
                    }
                }
            )
            ButtonOverlay(
                modifier = Modifier.padding(5.dp),
                text = "S",
                onInteraction = { interaction ->
                    when(interaction) {
                        is PressInteraction.Press -> glView?.setFollowSide()
                    }
                }
            )
            ButtonOverlay(
                modifier = Modifier.padding(5.dp),
                text = "T",
                onInteraction = { interaction ->
                    when(interaction) {
                        is PressInteraction.Press -> glView?.setFollowTop()
                    }
                }
            )
        }
    }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val activity = LocalContext.current as Activity
    DisposableEffect(orientation) {
        val activity = activity ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}