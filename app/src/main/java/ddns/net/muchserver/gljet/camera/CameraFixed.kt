package ddns.net.muchserver.gljet.camera

import android.opengl.Matrix
import ddns.net.muchserver.gljet.scene.SceneView
import ddns.net.muchserver.gljet.utility.MAT4_SIZE
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z

class CameraFixed(
    val sceneView: SceneView,
    private val fieldOfViewY: Float = FIELD_OF_VIEW_DEFAULT,
    private val near: Float = 1f,
    private val far: Float = 100f,
) {
    val matrixProjection = FloatArray(MAT4_SIZE)
    val matrixView = FloatArray(MAT4_SIZE)

    var position = floatArrayOf(0f, 0f, 0f)
    var target = floatArrayOf(0f, 0f, 0f)
    var up = floatArrayOf(0f, 0f, 0f)

    init {
        when(sceneView) {
            SceneView.REAR -> {
                position[Z] = 25f
                up[Y] = 1f
            }
            SceneView.SIDE -> {
                position[X] = 50f
                position[Z] = -20f
                target[Z] = -30f
                up[Y] = 1f
            }
            SceneView.TOP -> {
                position[Y] = 65f
                position[Z] = 0f
                target[Z] = -20f
                up[Z] = -1f
            }
        }
        updateViewMatrix()
    }

    fun update() {


    }

    fun updateViewMatrix() {
        Matrix.setLookAtM(
            matrixView, 0,
            position[X], position[Y], position[Z],
            target[X], target[Y], target[Z],
            up[X], up[Y], up[Z]
        )
    }

    fun updateProjectionMatrix(width: Int, height: Int) {
        val ratio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(matrixProjection, 0, fieldOfViewY, ratio, near, far)
    }

}