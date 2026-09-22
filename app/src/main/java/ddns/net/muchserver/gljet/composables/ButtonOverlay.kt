package ddns.net.muchserver.gljet.composables

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ButtonOverlay(modifier: Modifier, text: String, onInteraction: (Interaction) -> Unit) {
    val buttonBackgroundColor = Color(0x80507B9C)
    val buttonTextColor = Color(0xFF000000)

    val interactionSource = remember { MutableInteractionSource() }
    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            onInteraction(interaction)
        }
    }
    Button(
        modifier = modifier,
        onClick = {},
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            contentColor = buttonTextColor,
            containerColor = buttonBackgroundColor
        )
    ) {
        Text(text)
    }
}