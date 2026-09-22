package ddns.net.muchserver.gljet.collider

import android.content.Context
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.model.Model
import ddns.net.muchserver.gljet.utility.loadRawResourceText

class Box(val context: Context, scale: FloatArray, position: FloatArray) {
    val rotation = floatArrayOf(0f, 0f, 0f)
    val vertex = loadRawResourceText(context, R.raw.shader_vertex_model)
    val fragment = loadRawResourceText(context, R.raw.shader_fragment_model)
    val model = Model(
        context,
        R.raw.box_obj,
        position,
        rotation,
        vertex,
        fragment,
        scale
    )

    fun initGL() {
        model.initGL()
    }

    fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        model.draw(matrixView, matrixProjection)
    }

}