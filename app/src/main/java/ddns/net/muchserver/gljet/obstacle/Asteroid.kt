package ddns.net.muchserver.gljet.obstacle

import android.content.Context
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.collider.Collider
import ddns.net.muchserver.gljet.collider.ColliderType
import ddns.net.muchserver.gljet.jet.ID_JET
import ddns.net.muchserver.gljet.model.Model
import ddns.net.muchserver.gljet.obstacle.Vortex.Companion.randomizeX
import ddns.net.muchserver.gljet.obstacle.Vortex.Companion.randomizeY
import ddns.net.muchserver.gljet.scene.Z_MAX_SCENE
import ddns.net.muchserver.gljet.scene.Z_SPAWN
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import ddns.net.muchserver.gljet.utility.loadRawResourceText
import kotlin.math.sqrt
import kotlin.random.Random

val X_POSITION_MIN_ASTEROID = -20f
val X_POSITION_MAX_ASTEROID = 20f
val Y_POSITION_MIN_ASTEROID = -15f
val Y_POSITION_MAX_ASTEROID = 15f
class Asteroid(val context: Context, val position: FloatArray) {
    val speed = 0.05f
    val rotation = floatArrayOf(0.35f, 0f, 0f)
    val direction = floatArrayOf(1f, 1f, 1f)
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
        fun randomizeXY(asteroid: Asteroid) {
            val index = Random.nextInt(0, 3)
            when(index) {
                0 -> {
                    asteroid.position[X] = X_POSITION_MAX_ASTEROID
                    asteroid.position[Y] = Y_POSITION_MAX_ASTEROID
                }
                1 -> {
                    asteroid.position[X] = X_POSITION_MIN_ASTEROID
                    asteroid.position[Y] = Y_POSITION_MAX_ASTEROID
                }
                2 -> {
                    asteroid.position[X] = X_POSITION_MIN_ASTEROID
                    asteroid.position[Y] = Y_POSITION_MIN_ASTEROID
                }
                else -> {
                    asteroid.position[X] = X_POSITION_MAX_ASTEROID
                    asteroid.position[Y] = Y_POSITION_MIN_ASTEROID
                }
            }
        }

        fun setDirection(asteroid: Asteroid, point: FloatArray) {
            val x = asteroid.position[X] - point[X]
            val y = asteroid.position[Y] - point[Y]
            val z = asteroid.position[Z] - point[Z]
            val length = sqrt(x * x + y * y + z * z)

            asteroid.direction[X] = x / length
            asteroid.direction[Y] = y / length
            asteroid.direction[Z] = z / length
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
        if(isOutOfBounds()) {
            randomizeXY(this)
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

    fun isOutOfBounds(): Boolean {
        if(position[X] < X_POSITION_MIN_ASTEROID) {
            return true
        }
        if(position[X] > X_POSITION_MAX_ASTEROID) {
            return true
        }
        if(position[Y] < Y_POSITION_MIN_ASTEROID) {
            return true
        }
        if(position[Y] > Y_POSITION_MAX_ASTEROID) {
            return true
        }
        if(position[Z] > Z_MAX_SCENE) {
            return true
        }

        return false
    }
}