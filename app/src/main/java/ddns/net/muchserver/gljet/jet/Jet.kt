package ddns.net.muchserver.gljet.jet

import android.content.Context
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.collider.Collider
import ddns.net.muchserver.gljet.collider.ColliderType
import ddns.net.muchserver.gljet.model.Model
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import ddns.net.muchserver.gljet.utility.loadRawResourceText

val incRotation = 0.35f
val incMovement = 0.2f
val Y_POSITION_MIN = -10f
val Y_POSITION_MAX = 10f
val Y_ROTATION_BASE = 0f
val Y_ROTATION_MAX = 15f
val X_POSITION_MIN = -15f
val X_POSITION_MAX = 15f
val X_ROTATION_BASE = 90f
val X_ROTATION_MAX = 15f
val speed = 0.05f


val X_POSITION_INITIAL = 0f
val Y_POSITION_INITIAL = 0f
val Z_POSITION_INITIAL = -5f

val X_ROTATION_INITIAL = 90f
val Y_ROTATION_INITIAL = 0f
val Z_ROTATION_INITIAL = 0f
const val ID_JET = 1

class Jet(val context: Context) {
    val position = floatArrayOf(X_POSITION_INITIAL, Y_POSITION_INITIAL, Z_POSITION_INITIAL)

    val rotation = floatArrayOf(X_ROTATION_INITIAL, Y_ROTATION_INITIAL, Z_ROTATION_INITIAL)
    val vertex = loadRawResourceText(context, R.raw.shader_vertex_model)
    val fragment = loadRawResourceText(context, R.raw.shader_fragment_model)
    val model = Model(context, R.raw.jet_copper_obj, position, rotation,vertex, fragment)
    var movement = JetMovement.NONE
    var isUpdating = true

    val scale = floatArrayOf(1f, 1.3f, 5f)
    val collider = Collider(context, position, ID_JET, scale, ColliderType.BOX)


    init {
        setIdle()
    }

    fun initGL() {
        model.initGL()
    }

    fun update() {
        if(isUpdating) {
            when (movement) {
                JetMovement.LEFT -> {
                    if (position[X] > X_POSITION_MIN) {
                        if (rotation[Y] < Y_ROTATION_MAX) {
                            rotation[Y] += incRotation
                        }
                        val x = position[X] - incMovement
                        position[X] = x
                    } else {
                        easeYRotation()
                    }
                    easeXRotation()
                }

                JetMovement.RIGHT -> {
                    if (position[X] < X_POSITION_MAX) {
                        if (rotation[Y] > -Y_ROTATION_MAX) {
                            rotation[Y] -= incRotation
                        }
                        val x = position[X] + incMovement
                        position[X] = x
                    } else {
                        easeYRotation()
                    }
                    easeXRotation()
                }

                JetMovement.UP -> {
                    if (position[Y] < Y_POSITION_MAX) {
                        if (rotation[X] < X_ROTATION_BASE + X_ROTATION_MAX) {
                            rotation[X] += incRotation
                        }
                        val y = position[Y] + incMovement
                        position[Y] = y
                    } else {
                        easeXRotation()
                    }
                    easeYRotation()
                }

                JetMovement.DOWN -> {
                    if (position[Y] > Y_POSITION_MIN) {
                        if (rotation[X] > X_ROTATION_BASE - X_ROTATION_MAX) {
                            rotation[X] -= incRotation
                        }
                        val y = position[Y] - incMovement
                        position[Y] = y
                    } else {
                        easeXRotation()
                    }
                    easeYRotation()
                }

                else -> {
                    easeIntoIdle()
                }
            }

//            val z = position[Z] - speed
//            position[Z] = z
        }
    }

    fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        model.draw(matrixView, matrixProjection)
        collider.draw(matrixView, matrixProjection)
    }

    fun moveLeft() {
        movement = JetMovement.LEFT
    }

    fun moveRight() {
        movement = JetMovement.RIGHT
    }

    fun moveUp() {
        movement = JetMovement.UP
    }

    fun moveDown() {
        movement = JetMovement.DOWN
    }

    fun setIdle() {
        movement = JetMovement.NONE
        rotation[X] = X_ROTATION_INITIAL
        rotation[Y] = Y_ROTATION_INITIAL
        rotation[Z] = Z_ROTATION_INITIAL
    }

    fun easeYRotation() {
        if(rotation[Y] < 0) {
            rotation[Y] += incRotation
        }
        if(rotation[Y] > 0) {
            rotation[Y] -= incRotation
        }
    }

    fun easeXRotation() {
        if(rotation[X] > 90f) {
            rotation[X] -= incRotation
        }
        if(rotation[X] < 90f) {
            rotation[X] += incRotation
        }
    }

    fun easeIntoIdle() {
        movement = JetMovement.NONE
        easeXRotation()
        easeYRotation()
    }

    fun resetPosition() {
        position[X] = X_POSITION_INITIAL
        position[Y] = Y_POSITION_INITIAL
        position[Z] = Z_POSITION_INITIAL
        movement = JetMovement.NONE
    }
}