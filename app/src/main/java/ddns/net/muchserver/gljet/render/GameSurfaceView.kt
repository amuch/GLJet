package ddns.net.muchserver.gljet.render

import android.content.Context
import android.opengl.GLSurfaceView
import ddns.net.muchserver.gljet.time.GameLoop

const val EGL_VERSION = 3
class GameSurfaceView(context: Context): GLSurfaceView(context) {
    val renderManager: RenderManager
    val gameLoop: GameLoop

    init {
        setEGLContextClientVersion(EGL_VERSION)
        gameLoop = GameLoop(this)

        renderManager = RenderManager(context, gameLoop)
        setRenderer(renderManager)
        renderMode = RENDERMODE_WHEN_DIRTY // RENDERMODE_CONTINUOUSLY


    }

    override fun onResume() {
        super.onResume()
        gameLoop.start()
    }

    override fun onPause() {
        super.onPause()
        gameLoop.stop()
    }

    fun moveLeft() {
        renderManager.moveLeft()
    }

    fun moveRight() {
        renderManager.moveRight()
    }

    fun moveUp() {
        renderManager.moveUp()
    }

    fun moveDown() {
        renderManager.moveDown()
    }

    fun fire() {
        renderManager.fire()
    }

    fun setFiring() {
        renderManager.setFiring()
    }

    fun unsetFiring() {
        renderManager.unsetFiring()
    }

    fun setIdle() {
        renderManager.setIdle()
    }

    fun scoreText(): String {
        return renderManager.scoreText()
    }
    fun positionText(): String {
        return renderManager.positionText()
    }

    fun colliderText(): String {
        return renderManager.colliderText()
    }

    fun setFollowRear() {
        renderManager.setFollowRear()
    }

    fun setFollowSide() {
        renderManager.setFollowSide()
    }

    fun setFollowTop() {
        renderManager.setFollowTop()
    }

    fun reset() {
        renderManager.reset()
    }

    fun toggleColliderRender() {
        renderManager.toggleColliderRender()
    }

    fun pauseGame() {
        gameLoop.stop()
    }
}