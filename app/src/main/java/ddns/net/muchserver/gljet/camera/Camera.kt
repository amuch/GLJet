package ddns.net.muchserver.gljet.camera

import android.opengl.Matrix
import ddns.net.muchserver.gljet.utility.MAT4_SIZE
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import kotlin.math.cos
import kotlin.math.sin


const val FIELD_OF_VIEW_DEFAULT = 45f
const val Z_CAMERA_POSITION = 20f
const val Y_CAMERA_POSITION = 0f
const val X_CAMERA_POSITION = 0f
const val INCREMENT_MOVEMENT = 0.2f
const val INCREMENT_ROTATION = 1.5f

const val OFFSET_FOLLOW = -8f
const val Y_FOLLOW = 2.0f
const val Z_OFFSET_FOLLOW_SIDE = -20f
const val Z_OFFSET_FOLLOW_TOP = -15f
const val Y_OFFSET_FOLLOW_REAR = 2.5f

open class Camera(
    private val fieldOfViewY: Float = FIELD_OF_VIEW_DEFAULT,
    private val near: Float = 1f,
    private val far: Float = 100f
) {
    val matrixProjection = FloatArray(MAT4_SIZE)
    val matrixView = FloatArray(MAT4_SIZE)

    var position = floatArrayOf(X_CAMERA_POSITION, Y_CAMERA_POSITION, Z_CAMERA_POSITION)
    var target = floatArrayOf(0f, 0f, 0f)
    var up = floatArrayOf(0f, 1f, 0f)
    var yaw = 0f // degrees
    var cameraFollow = CameraFollow.NONE

    var positionFollowedObject: (() -> FloatArray)? = null
    var offsetFollow: (() -> FloatArray)? = null

    fun update() {
        var changed = false

        positionFollowedObject?.let { positionJet ->
            val positionFollowed = positionJet()
            val offset = offsetFollow?.invoke() ?: floatArrayOf(0f, Y_FOLLOW, OFFSET_FOLLOW)

            position[X] = positionFollowed[X] + offset[X]
            position[Y] = positionFollowed[Y] + offset[Y]
            position[Z] = positionFollowed[Z] + offset[Z]

            setTargetFollow(positionFollowed)

            changed = true
        }

        if(changed) {
            updateViewMatrix()
        }
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

    fun rotateYaw(degrees: Float) {
        yaw += degrees
        yaw %= 360

        val radians = Math.toRadians(yaw.toDouble())
        target[X] = position[X] + cos(radians).toFloat()
        target[Y] = position[Y]
        target[Z] = position[Z] + sin(radians).toFloat()
    }

    fun setFollowRear(positionJet: FloatArray) {
        cameraFollow = CameraFollow.REAR
        setUpY()
        positionFollowedObject = { positionJet }
        offsetFollow = { floatArrayOf(0f, 0f, 25f) }
    }

    fun setFollowSide(positionJet: FloatArray) {
        cameraFollow = CameraFollow.SIDE
        setUpY()
        positionFollowedObject = { positionJet }
        offsetFollow = { floatArrayOf(40f, 0f, 0f) }
    }

    fun setFollowTop(positionJet: FloatArray) {
        cameraFollow = CameraFollow.TOP
        positionFollowedObject = { positionJet }
        offsetFollow = { floatArrayOf(0f, 50f, 0f) }
        setUpZNegative()
    }

    fun setUpY() {
        up[X] = 0f
        up[Y] = 1f
        up[Z] = 0f
    }

    fun setUpZNegative() {
        up[X] = 0f
        up[Y] = 0f
        up[Z] = -1f
    }

    fun setTargetNone(positionFollowed: FloatArray) {
        target[X] = positionFollowed[X]
        target[Y] = positionFollowed[Y]
        target[Z] = positionFollowed[Z]
    }

    fun setTargetRear(positionFollowed: FloatArray) {
        target[X] = positionFollowed[X]
        target[Y] = positionFollowed[Y] + Y_OFFSET_FOLLOW_REAR
        target[Z] = positionFollowed[Z]
    }

    fun setTargetSide(positionFollowed: FloatArray) {
        target[X] = positionFollowed[X]
        target[Y] = positionFollowed[Y]
        target[Z] = positionFollowed[Z] + Z_OFFSET_FOLLOW_SIDE
    }

    fun setTargetTop(positionFollowed: FloatArray) {
        target[X] = positionFollowed[X]
        target[Y] = positionFollowed[Y]
        target[Z] = positionFollowed[Z] + Z_OFFSET_FOLLOW_TOP
    }

    fun setTargetFollow(positionFollowed: FloatArray) {
        when(cameraFollow) {
            CameraFollow.NONE -> setTargetNone(positionFollowed)
            CameraFollow.REAR -> setTargetRear(positionFollowed)
            CameraFollow.SIDE -> setTargetSide(positionFollowed)
            CameraFollow.TOP -> setTargetTop(positionFollowed)
        }
    }
}