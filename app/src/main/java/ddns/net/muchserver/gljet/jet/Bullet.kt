package ddns.net.muchserver.gljet.jet

import android.content.Context
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.collider.Collider
import ddns.net.muchserver.gljet.collider.ColliderType
import ddns.net.muchserver.gljet.model.Model
import ddns.net.muchserver.gljet.scene.Scene
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import ddns.net.muchserver.gljet.utility.loadRawResourceText

const val X_POSITION_INITIAL_BULLET = 0f
const val Y_POSITION_INITIAL_BULLET = 0f
const val Z_POSITION_INITIAL_BULLET = -2f
class Bullet(val context: Context, val position: FloatArray, val direction: FloatArray) {
    val speed = 0.5f
    val rotation = floatArrayOf(-90f, 0f, 0f)
    val vertex = loadRawResourceText(context, R.raw.shader_vertex_model)
    val fragment = loadRawResourceText(context, R.raw.shader_fragment_model)
    val scale = floatArrayOf(6f, 6f, 6f)
    val model = Model(context, R.raw.bullet_obj, position, rotation,vertex, fragment, scale)
    val scaleCollider = floatArrayOf(0.4f, 0.4f, 0.8f)
    val collider = Collider(context, position, ID_JET, scaleCollider, ColliderType.BOX)

    var isActive = false

    fun initGL() {
        model.initGL()
    }

    fun update() {
        if(isActive) {
            val z = position[Z] - speed
            position[Z] = z
        }
    }

    fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        if(isActive) {
            model.draw(matrixView, matrixProjection)
            collider.draw(matrixView, matrixProjection)
        }
    }

    fun reset() {
        position[X] = X_POSITION_INITIAL_BULLET
        position[Y] = Y_POSITION_INITIAL_BULLET
        position[Z] = Z_POSITION_INITIAL_BULLET
        isActive = false
        println("Bullet Reset")
    }
}