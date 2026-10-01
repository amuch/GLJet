package ddns.net.muchserver.gljet.scene

import android.content.Context
import android.os.Build
import android.os.Vibrator
import androidx.core.content.ContextCompat.getSystemService
import ddns.net.muchserver.gljet.jet.Bullet
import ddns.net.muchserver.gljet.jet.Jet
import ddns.net.muchserver.gljet.obstacle.Vortex
import ddns.net.muchserver.gljet.render.GLRenderer
import ddns.net.muchserver.gljet.sound.Sound
import ddns.net.muchserver.gljet.time.GameLoop
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z

class SceneManager(val context: Context, val gameLoop: GameLoop) {
    var scene: Scene? = null
    val sound = Sound(context)
    lateinit var jet: Jet
    lateinit var skyBox: SkyBox
    val vortices = ArrayList<Vortex>()
    val bullets = ArrayList<Bullet>()

    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE)


    fun initGL() {
        jet = Jet(context)
        jet.initGL()

        skyBox = SkyBox(context)
        skyBox.initGL()

        vorticesInit()

        bulletsInit()

        scene = Scene(
            SceneView.REAR,
            sound,
            jet,
            skyBox,
            vortices,
            bullets,
            vibrator as Vibrator
        )
    }

    fun update() {
        scene!!.update()
    }

    fun drawScene() {
        scene!!.drawScene()
    }

    fun updateSurface(width : Int, height: Int) {
        scene!!.updateSurface(width, height)
    }

    fun vorticesInit() {
        for(i in 0 until MAX_VORTEX_COUNT) {
            val position = floatArrayOf(0f, 0f, 0f)
            val vortex = Vortex(context, position)
            vortices.add(vortex)
            vortex.initGL()
        }
    }

    fun bulletsInit() {
        for(i in 0 until BULLET_MAX_COUNT) {
            val position = floatArrayOf(0f, 0f, 0f)
            val direction = floatArrayOf(0f, 0f, 1f)
            val bullet = Bullet(context, position, direction)
            bullet.initGL()
            bullets.add(bullet)
        }
    }

    fun resetScene(sceneView: SceneView) {
        scene = Scene(
            sceneView,
            sound,
            jet,
            skyBox,
            vortices,
            bullets,
            vibrator as Vibrator
        )
        scene!!.reset()
        scene!!.jet.isUpdating = true
        scene!!.drawScene()
    }
}