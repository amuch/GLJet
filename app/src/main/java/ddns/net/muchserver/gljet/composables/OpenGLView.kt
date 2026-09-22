package ddns.net.muchserver.gljet.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.LifecycleEventObserver
import ddns.net.muchserver.gljet.render.GameSurfaceView

@Composable
fun OpenGLView(
    modifier: Modifier,
    onSurfaceReady: (GameSurfaceView) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val surfaceView = remember {
        GameSurfaceView(context).apply {
            isClickable = true
            isFocusable = true
            isFocusableInTouchMode = true
        }
    }

    LaunchedEffect(Unit) {
        onSurfaceReady(surfaceView)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when(event) {
                Lifecycle.Event.ON_RESUME -> surfaceView.onResume()
                Lifecycle.Event.ON_PAUSE -> surfaceView.onPause()
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            surfaceView.onPause()
        }
    }
    AndroidView(
        factory = { surfaceView },
        modifier = modifier.pointerInput(Unit) {}
    )
}