package ddns.net.muchserver.gljet.jet

import android.content.Context
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.collider.Collider
import ddns.net.muchserver.gljet.collider.ColliderType
import ddns.net.muchserver.gljet.model.Model
import ddns.net.muchserver.gljet.utility.Z
import ddns.net.muchserver.gljet.utility.loadRawResourceText

const val BULLET_MAX_UPDATE_COUNT = 301
class Bullet(val context: Context, val position: FloatArray, val direction: FloatArray) {
    val rotation = floatArrayOf(-90f, 0f, 0f)
    val vertex = loadRawResourceText(context, R.raw.shader_vertex_model)
    val fragment = loadRawResourceText(context, R.raw.shader_fragment_model)
    val scale = floatArrayOf(6f, 6f, 6f)
    val model = Model(context, R.raw.bullet_obj, position, rotation,vertex, fragment, scale)
    val scaleCollider = floatArrayOf(0.4f, 0.4f, 0.8f)
    val collider = Collider(context, position, ID_JET, scaleCollider, ColliderType.BOX)
    var updateCount = 0
    var isActive = false

    fun initGL() {
        model.initGL()
    }

    fun update() {
        if(isActive) {
            updateCount++
            val z = position[Z] - speed * 10f
            position[Z] = z
            if(updateCount > BULLET_MAX_UPDATE_COUNT) {
                isActive = false
                updateCount = 0
                println("update count expired")
            }
        }
    }

    fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        if(isActive) {
            model.draw(matrixView, matrixProjection)
            collider.draw(matrixView, matrixProjection)
        }
    }
}