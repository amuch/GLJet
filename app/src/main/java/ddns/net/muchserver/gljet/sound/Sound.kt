package ddns.net.muchserver.gljet.sound

import android.content.Context
import android.media.MediaPlayer
import ddns.net.muchserver.gljet.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Sound(val context: Context) {

    val shoot = MediaPlayer.create(context, R.raw.shoot)
    val explosion = MediaPlayer.create(context, R.raw.explosion)
    val collision = MediaPlayer.create(context, R.raw.collision)

    fun playShoot() {
        CoroutineScope(Dispatchers.Default).launch {
            if(shoot.isPlaying) {
                return@launch
            }
            shoot.start()
        }
    }

    fun playExplosion() {
        CoroutineScope(Dispatchers.Default).launch {
            if(explosion.isPlaying) {
                return@launch
            }
            explosion.start()
        }
    }

    fun playCollision() {
        CoroutineScope(Dispatchers.Default).launch {
            if(collision.isPlaying) {
                return@launch
            }
            collision.start()
        }
    }

}