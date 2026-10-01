package ddns.net.muchserver.gljet.scene

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import ddns.net.muchserver.gljet.camera.CameraFixed
import ddns.net.muchserver.gljet.collider.CollisionManager
import ddns.net.muchserver.gljet.jet.Bullet
import ddns.net.muchserver.gljet.jet.Jet
import ddns.net.muchserver.gljet.obstacle.HEALTH_VORTEX_DEFAULT
import ddns.net.muchserver.gljet.obstacle.Vortex
import ddns.net.muchserver.gljet.obstacle.Vortex.Companion.randomizeX
import ddns.net.muchserver.gljet.obstacle.Vortex.Companion.randomizeY
import ddns.net.muchserver.gljet.sound.Sound
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

const val MAX_VORTEX_COUNT = 9
const val BULLET_MAX_COUNT = 13
const val Z_MAX_SCENE = 10
const val Z_SPAWN = -60f
const val BULLET_MIN_Z = -105

class Scene(
    val sceneView: SceneView,
    val sound: Sound,
    val jet: Jet,
    val skyBox: SkyBox,
    val vortices: ArrayList<Vortex>,
    val bullets: ArrayList<Bullet>,
    val vibrator: Vibrator? = null
) {
    var score = 0
    var hits = 0
    var misses = 0
    val camera = CameraFixed(sceneView)

    var isFiringBullets = false

    val vibrationEffect: VibrationEffect? = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
    val fireEffect: VibrationEffect? = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.R) VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                                       else null

    fun update() {
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
                    vortex.position[X] = if(sceneView == SceneView.SIDE) 0f else randomizeX()
                    vortex.position[Y] = if(sceneView == SceneView.TOP) 0f else randomizeY()
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
                            if(vibrator != null && fireEffect != null) {
                                vibrator.cancel()
                                vibrator.vibrate(fireEffect)
                            }
                            score += 3
                            vortex.isActive = false
                        }
                        break
                    }

                }


            }
    }

    fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
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
        if(sceneView == SceneView.SIDE) {
            jet.moveBackward()
        }
        else {
            jet.moveLeft()
        }
    }

    fun moveRight() {
        if(sceneView == SceneView.SIDE) {
            jet.moveForward()
        }
        else {
            jet.moveRight()
        }
    }

    fun moveUp() {
        if(sceneView == SceneView.TOP) {
            jet.moveForward()
        }
        else {
            jet.moveUp()
        }
    }

    fun moveDown() {
        if(sceneView == SceneView.TOP) {
            jet.moveBackward()
        }
        else {
            jet.moveDown()
        }
    }

    fun easeIntoIdle() {
        jet.easeIntoIdle()
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
    fun enableVortices() {
        for(vortex in vortices) {
            vortex.isActive = true
        }
    }

    fun randomizeVorticesPosition() {
        val incFactor = -7.0f

        for(i in 0 until MAX_VORTEX_COUNT) {
            vortices[i].position[X] = if(sceneView == SceneView.SIDE) 0f else randomizeX()
            vortices[i].position[Y] = if(sceneView == SceneView.TOP) 0f else randomizeY()
            vortices[i].position[Z] = Z_SPAWN + (i * incFactor)
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
                if(vibrator != null) {
                    vibrator.cancel()
                    vibrator.vibrate(vibrationEffect)
                }
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