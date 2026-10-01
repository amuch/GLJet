package ddns.net.muchserver.gljet.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.MutableLiveData

@Composable
fun Menu(
    modifier: Modifier,
    setIsMenuVisible: (Boolean) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        Button(
            onClick = {
                setIsMenuVisible(false)
            }
        ) {
            Text("X")
        }
    }
}