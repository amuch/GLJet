package ddns.net.muchserver.gljet.time

import ddns.net.muchserver.gljet.render.GameSurfaceView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val FPS_TARGET = 60
class GameLoop(private val glSurfaceView: GameSurfaceView) {
    private val timeFrame = 1000L / FPS_TARGET
    private var job: Job? = null

    companion object {
        var isRunning = false
    }

    fun start() {
        if(isRunning) {
            return
        }

        isRunning = true
        job = CoroutineScope(Dispatchers.Default).launch {
            while(isRunning) {
                val timeStart = System.currentTimeMillis()

                withContext(Dispatchers.Main) {
                    glSurfaceView.requestRender()
                }

                val timeElapsed = System.currentTimeMillis() - timeStart
                val timeDelay = timeFrame - timeElapsed
                if(timeDelay > 0) {
                    delay(timeDelay)
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        job?.cancel()
    }
}