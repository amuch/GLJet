package ddns.net.muchserver.gljet.render

import android.content.Context
import android.media.MediaPlayer
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.collider.CollisionManager
import ddns.net.muchserver.gljet.collider.X_MAX
import ddns.net.muchserver.gljet.collider.X_MIN
import ddns.net.muchserver.gljet.collider.Y_MAX
import ddns.net.muchserver.gljet.collider.Y_MIN
import ddns.net.muchserver.gljet.collider.Z_MAX
import ddns.net.muchserver.gljet.collider.Z_MIN
import ddns.net.muchserver.gljet.scene.Scene
import ddns.net.muchserver.gljet.time.GameLoop
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RenderManager(val context: Context, val gameLoop: GameLoop): GLSurfaceView.Renderer {
    val scene: Scene

    init {
        scene = Scene(context, gameLoop)
    }

    companion object {
        var widthScreen = 0f
        var heightScreen = 0f
        var ratio = 0f
        var shouldRenderColliders = false
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES32.glClearColor(0.1f, 0.2f, 0.4f, 1.0f)
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glEnable(GLES32.GL_CULL_FACE)
        GLES32.glCullFace(GLES32.GL_BACK)

        scene.initGL()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        scene.update()
        scene.drawScene()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
        widthScreen = width.toFloat()
        heightScreen = height.toFloat()
        ratio = widthScreen / heightScreen

        scene.updateSurface(width, height)
    }

    fun moveLeft() {
        scene.moveLeft()
    }

    fun moveRight() {
        scene.moveRight()
    }

    fun moveUp() {
        scene.moveUp()
    }

    fun moveDown() {
        scene.moveDown()
    }

    fun setIdle() {
        scene.easeIntoIdle()
    }

    fun fire() {
        scene.fire()
    }

    fun setFiring() {
        scene.startFiring()
    }

    fun unsetFiring() {
        scene.isFiringBullets = false
    }

    fun positionText(): String {
        if(!scene.jet.model.isInitialized) {
            return ""
        }
        return "X: ${scene.jet.position[X]} Y: ${scene.jet.position[Y]} Z: ${scene.jet.position[Z]}"
    }

    fun scoreText(): String {
        return "Score ${scene.score}"
    }

    fun colliderText(): String {
        if(!scene.jet.model.isInitialized) {
            return ""
        }
        val collider = CollisionManager.generateMaxMin(scene.jet.collider)
        return "X: ${collider[X_MIN]} - ${collider[X_MAX]} Y: ${collider[Y_MIN]} - ${collider[Y_MAX]} Z: ${collider[Z_MIN]} - ${collider[Z_MAX]}"
    }

    fun setFollowRear() {
        scene.setFollowRear()
    }

    fun setFollowSide() {
        scene.setFollowSide()
    }

    fun setFollowTop() {
        scene.setFollowTop()
    }

    fun reset() {
        scene.reset()
    }

    fun toggleColliderRender() {
        shouldRenderColliders = !shouldRenderColliders
    }
}