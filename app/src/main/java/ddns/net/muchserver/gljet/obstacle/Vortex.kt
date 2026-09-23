package ddns.net.muchserver.gljet.obstacle

import android.content.Context
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.collider.Collider
import ddns.net.muchserver.gljet.collider.ColliderType
import ddns.net.muchserver.gljet.jet.ID_JET
import ddns.net.muchserver.gljet.model.Model
import ddns.net.muchserver.gljet.scene.Z_MAX_SCENE
import ddns.net.muchserver.gljet.scene.Z_SPAWN
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import ddns.net.muchserver.gljet.utility.loadRawResourceText
import kotlin.random.Random

const val X_MIN_VORTEX = -4.0
const val X_MAX_VORTEX = 4.0
const val Y_MIN_VORTEX = -6.0
const val Y_MAX_VORTEX = 6.0
class Vortex(val context: Context, val position: FloatArray) {
    val speed = 0.05f
    val rotation = floatArrayOf(0.35f, 0f, 0f)
    val vertex = loadRawResourceText(context, R.raw.shader_vertex_model)
    val fragment = loadRawResourceText(context, R.raw.shader_fragment_model)
    val scale = floatArrayOf(1f, 1f, 1f)
    val model = Model(context, R.raw.vortex1_obj, position, rotation,vertex, fragment, scale)

    val scaleCollider = floatArrayOf(1.8f, 2.8f, 1.8f)
    val collider = Collider(context, position, ID_JET, scaleCollider, ColliderType.BOX)
    var isActive = true
    var timeLastUpdate = 0L
    val deltaY = Random.nextDouble(-60.0, 60.0).toFloat()

    companion object {
        fun randomizeX(): Float {
            return Random.nextDouble(X_MIN_VORTEX, X_MAX_VORTEX).toFloat()
        }

        fun randomizeY(): Float {
            return Random.nextDouble(Y_MIN_VORTEX, Y_MAX_VORTEX).toFloat()
        }
    }

    fun initGL() {
        model.initGL()
    }

    fun update() {
        val timeCurrent = System.currentTimeMillis()
        val timeDelta = (timeCurrent - timeLastUpdate) / 1000f

        rotation[Y] += ((deltaY * timeDelta) % 360f)

        timeLastUpdate = timeCurrent

        val z = position[Z] + speed
        position[Z] = z
        if(z > Z_MAX_SCENE) {
            position[X] = randomizeX()
            position[Y] = randomizeY()
            position[Z] = Z_SPAWN
            isActive = true
        }
    }

    fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        if(!isActive) {
            return
        }
        model.draw(matrixView, matrixProjection)
        collider.draw(matrixView, matrixProjection)
    }

}