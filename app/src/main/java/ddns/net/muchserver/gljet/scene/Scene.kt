package ddns.net.muchserver.gljet.scene

import android.content.Context
import android.media.MediaPlayer
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.camera.Camera
import ddns.net.muchserver.gljet.collider.CollisionManager
import ddns.net.muchserver.gljet.collider.X_MAX
import ddns.net.muchserver.gljet.collider.X_MIN
import ddns.net.muchserver.gljet.collider.Y_MAX
import ddns.net.muchserver.gljet.collider.Y_MIN
import ddns.net.muchserver.gljet.collider.Z_MAX
import ddns.net.muchserver.gljet.collider.Z_MIN
import ddns.net.muchserver.gljet.entity.Cube
import ddns.net.muchserver.gljet.jet.Bullet
import ddns.net.muchserver.gljet.jet.Jet
import ddns.net.muchserver.gljet.jet.speed
import ddns.net.muchserver.gljet.obstacle.Vortex
import ddns.net.muchserver.gljet.render.GLRenderer
import ddns.net.muchserver.gljet.time.GameLoop
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import ddns.net.muchserver.gljet.utility.loadRawResourceText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.get
import kotlin.random.Random
import kotlin.uuid.Uuid.Companion.random

val positionsCube = arrayOf(
    floatArrayOf(-4f, 0f, -18f),
    floatArrayOf(4f, 0f, -18f),
    floatArrayOf(-4f, 0f, -22f),
    floatArrayOf(4f, 0f, -22f),
    floatArrayOf(-4f, 4f, -28f),
    floatArrayOf(4f, 0f, -28f),
    floatArrayOf(-4f, 4f, -34f),
    floatArrayOf(4f, -4f, -34f),
    floatArrayOf(-4f, -4f, -40f),
    floatArrayOf(4f, 6f, -40f),
    floatArrayOf(-4f, -6f, -46f),
    floatArrayOf(-4f, 4f, -58f),
    floatArrayOf(4f, -4f, -66f),
    floatArrayOf(-4f, -4f, -75f),
    floatArrayOf(4f, 6f, -88f),
    floatArrayOf(-4f, -6f, -98f),
    floatArrayOf(-4f, 0f, -106f),
    floatArrayOf(4f, 0f, -128f),
    floatArrayOf(-4f, 0f, -122f),
    floatArrayOf(4f, 0f, -122f),
    floatArrayOf(-4f, 4f, -128f),
    floatArrayOf(4f, 0f, -128f),
    floatArrayOf(-4f, 4f, -134f),
    floatArrayOf(4f, -4f, -134f),
)

const val MAX_VORTEX_COUNT = 42
const val X_MIN_VORTEX = -4.0
const val X_MAX_VORTEX = 4.0
const val Y_MIN_VORTEX = -6.0
const val Y_MAX_VORTEX = 6.0

const val BULLET_MAX_COUNT = 9
class Scene(val context: Context, val gameLoop: GameLoop): GLRenderer {
    var score = 0

    val shoot = MediaPlayer.create(context, R.raw.shoot)
    val explosion = MediaPlayer.create(context, R.raw.explosion)
    val collision = MediaPlayer.create(context, R.raw.collision)
    val camera = Camera()
    lateinit var jet: Jet
    lateinit var skyBox: SkyBox
//    val cubes = ArrayList<Cube>()
    val vortices = ArrayList<Vortex>()
    val bullets = ArrayList<Bullet>()

    val vertex = loadRawResourceText(context, R.raw.shader_vertex_cube)
    val fragment = loadRawResourceText(context, R.raw.shader_fragment_cube)


    override fun initGL() {
        jet = Jet(context)
        jet.initGL()

        skyBox = SkyBox(context)
        skyBox.initGL()

//        for(position in positionsCube) {
//            val cube = Cube(context, position, vertex, fragment)
//            cubes.add(cube)

//        }
//        for(cube in cubes) {
//            cube.initGL()
//        }
        var z = -30.0
        val incFactor = -16.0
        for(i in 1 until MAX_VORTEX_COUNT) {
            val x = Random.nextDouble(X_MIN_VORTEX, X_MAX_VORTEX).toFloat()
            val y = Random.nextDouble(Y_MIN_VORTEX, Y_MAX_VORTEX).toFloat()
            val zed = Random.nextDouble(z + incFactor / 2, incFactor).toFloat()
            val position = floatArrayOf(x, y, zed)
            val vortex = Vortex(context, position)
            vortices.add(vortex)
            z += incFactor
        }
        for(vortex in vortices) {
            vortex.initGL()
        }
        for(i in 0 until BULLET_MAX_COUNT) {
            val position = floatArrayOf(0f, 0f, 0f)
            val direction = floatArrayOf(0f, 0f, 1f)
            val bullet = Bullet(context, position, direction)

            bullets.add(bullet)
        }
        setFollowRear()
    }

    override fun update() {
//        CoroutineScope(Dispatchers.Default).launch {
            camera.position[Z] = camera.position[Z] - speed
            camera.updateViewMatrix()

            camera.update()
            jet.update()

            for (bullet in bullets) {
                bullet.update()
            }
            for(vortex in vortices) {
                if(!vortex.isActive) {
                    continue
                }
                for (bullet in bullets) {
                    if (!bullet.isActive) {
                        continue
                    }
                    if (CollisionManager.isCollision(bullet.collider, vortex.collider)) {
                        playExplosion()
                        score += 3
                        bullet.isActive = false
                        vortex.isActive = false
                        break
                    }
                }
                if (CollisionManager.isCollision(jet.collider, vortex.collider)) {
                    playCollision()
                    score -= 4
                    vortex.isActive = false
                }
                vortex.update()
            }
//            for (cube in cubes) {
//                if(!cube.isActive) {
//                    continue
//                }
//                for (bullet in bullets) {
//                    if (!bullet.isActive) {
//                        continue
//                    }
//                    if (CollisionManager.isCollision(bullet.collider, cube.collider)) {
//                        playExplosion()
//                        score += 3
//                        bullet.isActive = false
//                        cube.isActive = false
//                        break
//                    }
//                }
//                if (CollisionManager.isCollision(jet.collider, cube.collider)) {
//                    playCollision()
//                    score -= 4
//                    cube.isActive = false
//                }
//                cube.update()
//            }
//        }
    }

    override fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        skyBox.draw(matrixView, matrixProjection)
//        for(cube in cubes) {
//            cube.draw(matrixView, matrixProjection)
//        }
        for(vortex in vortices) {
            vortex.draw(matrixView, matrixProjection)
        }

        for(bullet in bullets) {
            bullet.draw(matrixView, matrixProjection)
        }

        jet.draw(matrixView, matrixProjection)
    }

    fun drawScene() {
        draw(camera.matrixView, camera.matrixProjection)
    }

    fun updateSurface(width : Int, height: Int) {
        camera.updateProjectionMatrix(width, height)
        camera.updateViewMatrix()
    }

    fun moveLeft() {
        jet.moveLeft()
    }

    fun moveRight() {
        jet.moveRight()
    }

    fun moveUp() {
        jet.moveUp()
    }

    fun moveDown() {
        jet.moveDown()
    }

    fun easeIntoIdle() {
        jet.easeIntoIdle()
    }

    fun setFollowRear() {
        camera.setFollowRear(jet.position)
    }

    fun setFollowSide() {
        camera.setFollowSide(jet.position)
    }

    fun setFollowTop() {
        camera.setFollowTop(jet.position)
    }

    fun resetPositionJet() {
        if(jet.isUpdating) {
            jet.isUpdating = false
            jet.resetPosition()
            diableBullets()
            score = 0
        }
        else {
            jet.isUpdating = true
        }
    }
//    fun enableCubes() {
//        for(cube in cubes) {
//            cube.isActive = true
//        }
//    }
    fun enableVortices() {
        for(vortex in vortices) {
            vortex.isActive = true
        }
    }

    fun fire() {
        for(bullet in bullets) {
            if(!bullet.isActive) {
                bullet.position[X] = jet.position[X]
                bullet.position[Y] = jet.position[Y]
                bullet.position[Z] = jet.position[Z] - 5f
                bullet.isActive = true
                playShoot()
                break
            }
        }
    }

    fun diableBullets() {
        for(bullet in bullets) {
            bullet.isActive = false
        }
    }

    fun playShoot() {
        CoroutineScope(Dispatchers.Default).launch {
            if(shoot.isPlaying) {
                return@launch
            }
            shoot.start()
        }
    }

    fun playExplosion() {
        CoroutineScope(Dispatchers.Default).launch {
            if(explosion.isPlaying) {
                return@launch
            }
            explosion.start()
        }
    }

    fun playCollision() {
        CoroutineScope(Dispatchers.Default).launch {
            if(collision.isPlaying) {
                return@launch
            }
            collision.start()
        }
    }
}