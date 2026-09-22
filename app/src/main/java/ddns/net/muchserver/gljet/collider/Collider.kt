package ddns.net.muchserver.gljet.collider

import android.content.Context
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.model.Model
import ddns.net.muchserver.gljet.render.GLRenderer
import ddns.net.muchserver.gljet.render.RenderManager
import ddns.net.muchserver.gljet.utility.loadRawResourceText

class Collider(
    val context: Context,
    val position: FloatArray,
    val id: Int,
    val scale: FloatArray,
    val colliderType: ColliderType
//    onCollision: Map<Int, (() -> Unit)>
): GLRenderer {
    val rotation = floatArrayOf(0f, 0f, 0f)
    val vertex = loadRawResourceText(context, R.raw.shader_vertex_model)
    val fragment = loadRawResourceText(context, R.raw.shader_fragment_model)
    val idResource = when(colliderType) {
        ColliderType.SPHERE -> R.raw.sphere_obj
        ColliderType.BOX -> R.raw.box_obj
    }

    val model = Model(
        context,
        idResource,
        position,
        rotation,
        vertex,
        fragment,
        scale
    )

    override fun initGL() {
        model.initGL()
    }

    override fun update() {

    }

    override fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        if(RenderManager.shouldRenderColliders) {
            model.draw(matrixView, matrixProjection)
        }
    }
}