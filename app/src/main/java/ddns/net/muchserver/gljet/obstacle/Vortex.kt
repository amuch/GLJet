package ddns.net.muchserver.gljet.obstacle

import android.content.Context
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.collider.Collider
import ddns.net.muchserver.gljet.collider.ColliderType
import ddns.net.muchserver.gljet.jet.ID_JET
import ddns.net.muchserver.gljet.model.Model
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.loadRawResourceText
import kotlin.random.Random

class Vortex(val context: Context, val position: FloatArray) {
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

    fun initGL() {
        model.initGL()
    }

    fun update() {
        if(!isActive) {
            return
        }
        val timeCurrent = System.currentTimeMillis()
        val timeDelta = (timeCurrent - timeLastUpdate) / 1000f

        rotation[Y] += ((deltaY * timeDelta) % 360f)

        timeLastUpdate = timeCurrent
    }

    fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        if(!isActive) {
            return
        }
        model.draw(matrixView, matrixProjection)
        collider.draw(matrixView, matrixProjection)
    }

}