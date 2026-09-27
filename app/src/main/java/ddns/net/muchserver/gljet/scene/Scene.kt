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
import ddns.net.muchserver.gljet.obstacle.HEALTH_VORTEX_DEFAULT
import ddns.net.muchserver.gljet.obstacle.Vortex
import ddns.net.muchserver.gljet.obstacle.Vortex.Companion.randomizeX
import ddns.net.muchserver.gljet.obstacle.Vortex.Companion.randomizeY
import ddns.net.muchserver.gljet.render.GLRenderer
import ddns.net.muchserver.gljet.sound.Sound
import ddns.net.muchserver.gljet.time.GameLoop
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import ddns.net.muchserver.gljet.utility.loadRawResourceText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.collections.get
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid.Companion.random

const val MAX_VORTEX_COUNT = 9
const val BULLET_MAX_COUNT = 13
const val Z_MAX_SCENE = 5
const val Z_SPAWN = -60f
const val BULLET_MIN_Z = -105

class Scene(val context: Context, val gameLoop: GameLoop): GLRenderer {
    var score = 0
    var hits = 0
    var misses = 0
    val sound = Sound(context)
    val camera = Camera()
    lateinit var jet: Jet
    lateinit var skyBox: SkyBox
    val vortices = ArrayList<Vortex>()
    val bullets = ArrayList<Bullet>()
    var isFiringBullets = false


    override fun initGL() {
        jet = Jet(context)
        jet.initGL()

        skyBox = SkyBox(context)
        skyBox.initGL()

        vorticesInit()

        bulletsInit()

        setFollowRear()
    }

    override fun update() {
            camera.position[Z] = camera.position[Z] - speed
            camera.updateViewMatrix()

            camera.update()
            jet.update()

            for(bullet in bullets) {
                bullet.update()
                if(bullet.position[Z] < BULLET_MIN_Z) {
                    if(bullet.isActive) {
                        misses += 1
                    }
                    bullet.reset()
                }
            }

            for(vortex in vortices) {
                vortex.update()
                if(vortex.position[Z] > Z_MAX_SCENE) {
                    if(vortex.isActive) {
                        score -= 1
                    }
                    vortex.position[X] = randomizeX()
                    vortex.position[Y] = randomizeY()
                    vortex.position[Z] = Z_SPAWN
                    vortex.isActive = true
                    vortex.health = HEALTH_VORTEX_DEFAULT
                }

                resolveJetVortexCollision(vortex)
                if(!vortex.isActive) {
                    continue
                }

                for(bullet in bullets) {
                    if(!bullet.isActive) {
                        continue
                    }

                    if(CollisionManager.isCollision(bullet.collider, vortex.collider)) {
                        bullet.reset()
                        vortex.health -= 1
                        hits += 1
                        println("Vortex Health: ${vortex.health}")

                        if(vortex.health < 1) {
                            sound.playExplosion()
                            score += 3
                            vortex.isActive = false
                        }
                        break
                    }

                }


            }
    }

    override fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        skyBox.draw(matrixView, matrixProjection)

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

    fun reset() {
        if(jet.isUpdating) {
            jet.isUpdating = false
            jet.resetPosition()
            diableBullets()
            enableVortices()
            randomizeVorticesPosition()
            score = 0
            hits = 0
            misses = 0
        }
        else {
            jet.isUpdating = true
        }
    }

    fun vorticesInit() {
        for(i in 0 until MAX_VORTEX_COUNT) {
            val position = floatArrayOf(0f, 0f, 0f)
            val vortex = Vortex(context, position)
            vortices.add(vortex)
            vortex.initGL()
        }
        randomizeVorticesPosition()
    }

    fun enableVortices() {
        for(vortex in vortices) {
            vortex.isActive = true
        }
    }

    fun randomizeVorticesPosition() {
        val incFactor = -7.0f
        for(i in 0 until MAX_VORTEX_COUNT) {
            vortices[i].position[X] = Vortex.randomizeX()
            vortices[i].position[Y] = Vortex.randomizeY()
            vortices[i].position[Z] = Z_SPAWN + (i * incFactor)
        }
    }

    fun bulletsInit() {
        for(i in 0 until BULLET_MAX_COUNT) {
            val position = floatArrayOf(0f, 0f, 0f)
            val direction = floatArrayOf(0f, 0f, 1f)
            val bullet = Bullet(context, position, direction)
            bullet.initGL()
            bullets.add(bullet)
        }
    }
    fun fire() {
        for(bullet in bullets) {
            if(!bullet.isActive) {
                bullet.position[X] = jet.position[X]
                bullet.position[Y] = jet.position[Y]
                bullet.position[Z] = jet.position[Z] - 3f
                bullet.isActive = true
                sound.playShoot()
                break
            }
        }
    }

    fun diableBullets() {
        for(bullet in bullets) {
            bullet.reset()
        }
    }

    fun startFiring() {
        if(isFiringBullets) {
            return
        }
        isFiringBullets = true
        resolveIsFiringBullets()
    }

    fun resolveIsFiringBullets() {
        CoroutineScope(Dispatchers.Default).launch {
            while(isFiringBullets) {
                fire()
                delay(150.milliseconds)
            }
        }
    }

    fun resolveJetVortexCollision(vortex: Vortex) {
        CoroutineScope(Dispatchers.Default).launch {
            if(!vortex.isActive) {
                return@launch
            }
            if(CollisionManager.isCollision(jet.collider, vortex.collider)) {
                sound.playCollision()
                score -= 4
                vortex.isActive = false
            }
        }
    }

    fun resolveBulletsVortexCollision(vortex: Vortex) {
        CoroutineScope(Dispatchers.Default).launch {
            for(bullet in bullets) {
                if(!bullet.isActive) {
                    continue
                }
                if(vortex.isActive) {
                    if(CollisionManager.isCollision(bullet.collider, vortex.collider)) {
                        bullet.reset()
                        vortex.health -= 1
                        hits += 1
                        println("Vortex Health: ${vortex.health}")

                        if(vortex.health < 1) {
                            sound.playExplosion()
                            score += 3
                            vortex.isActive = false
                        }
                        break
                    }
                }
            }
        }
    }

    fun accuracy(): Float {
        if(hits == 0) {
            return 0.0f
        }
        val hitsFloat = hits.toFloat()
        val missesFloat = misses.toFloat()
        val sum = hitsFloat + missesFloat
        return (hitsFloat / sum)
    }
}