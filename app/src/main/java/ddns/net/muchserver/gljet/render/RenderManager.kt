package ddns.net.muchserver.gljet.render

import android.content.Context
import android.opengl.GLES32
import android.opengl.GLSurfaceView
import ddns.net.muchserver.gljet.collider.CollisionManager
import ddns.net.muchserver.gljet.collider.X_MAX
import ddns.net.muchserver.gljet.collider.X_MIN
import ddns.net.muchserver.gljet.collider.Y_MAX
import ddns.net.muchserver.gljet.collider.Y_MIN
import ddns.net.muchserver.gljet.collider.Z_MAX
import ddns.net.muchserver.gljet.collider.Z_MIN
import ddns.net.muchserver.gljet.scene.Scene
import ddns.net.muchserver.gljet.scene.SceneManager
import ddns.net.muchserver.gljet.scene.SceneView
import ddns.net.muchserver.gljet.time.GameLoop
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class RenderManager(val context: Context, val gameLoop: GameLoop): GLSurfaceView.Renderer {
    val sceneManager: SceneManager
//    val scene: Scene

    init {
        sceneManager = SceneManager(context, gameLoop)
//        scene = sceneManager.scene!!
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

        sceneManager.initGL()
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)

        sceneManager.update()
        sceneManager.drawScene()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES32.glViewport(0, 0, width, height)
        widthScreen = width.toFloat()
        heightScreen = height.toFloat()
        ratio = widthScreen / heightScreen

        sceneManager.updateSurface(width, height)
    }

    fun moveLeft() {
        sceneManager.scene!!.moveLeft()
    }

    fun moveRight() {
        sceneManager.scene!!.moveRight()
    }

    fun moveUp() {
        sceneManager.scene!!.moveUp()
    }

    fun moveDown() {
        sceneManager.scene!!.moveDown()
    }

    fun setIdle() {
        sceneManager.scene!!.easeIntoIdle()
    }

    fun fire() {
        sceneManager.scene!!.fire()
    }

    fun setFiring() {
        sceneManager.scene!!.startFiring()
    }

    fun unsetFiring() {
        sceneManager.scene!!.isFiringBullets = false
    }

    fun positionText(): String {
        if(!sceneManager.scene!!.jet.model.isInitialized) {
            return ""
        }
        return "X: ${sceneManager.scene!!.jet.position[X]} Y: ${sceneManager.scene!!.jet.position[Y]} Z: ${sceneManager.scene!!.jet.position[Z]}"
    }

    fun scoreText(): String {
        return "Score ${sceneManager.scene?.score}"
    }

    fun accuracyText(): String {
        val accuracy = sceneManager.scene?.accuracy()
        return "Hit: ${sceneManager.scene?.hits} Miss: ${sceneManager.scene?.misses} Acc: $accuracy"
    }

    fun colliderText(): String {
        if(!sceneManager.scene?.jet!!.model.isInitialized) {
            return ""
        }
        val collider = CollisionManager.generateMaxMin(sceneManager.scene?.jet!!.collider)
        return "X: ${collider[X_MIN]} - ${collider[X_MAX]} Y: ${collider[Y_MIN]} - ${collider[Y_MAX]} Z: ${collider[Z_MIN]} - ${collider[Z_MAX]}"
    }

    fun setFollowRear() {
        if(sceneManager.scene == null) {
            return
        }
        sceneManager.resetScene(SceneView.REAR)
        sceneManager.updateSurface(widthScreen.toInt(), heightScreen.toInt())
        sceneManager.update()
        sceneManager.drawScene()
    }

    fun setFollowSide() {
        if(sceneManager.scene == null) {
            return
        }
        sceneManager.resetScene(SceneView.SIDE)
        sceneManager.updateSurface(widthScreen.toInt(), heightScreen.toInt())
        sceneManager.update()
        sceneManager.drawScene()
    }

    fun setFollowTop() {
        if(sceneManager.scene == null) {
            return
        }
        sceneManager.resetScene(SceneView.TOP)
        sceneManager.updateSurface(widthScreen.toInt(), heightScreen.toInt())
        sceneManager.update()
        sceneManager.drawScene()
    }

    fun toggleColliderRender() {
        shouldRenderColliders = !shouldRenderColliders
    }
}