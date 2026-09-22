package ddns.net.muchserver.gljet.entity

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import ddns.net.muchserver.gljet.collider.Collider
import ddns.net.muchserver.gljet.collider.ColliderType
import ddns.net.muchserver.gljet.jet.ID_JET
import ddns.net.muchserver.gljet.utility.COORDINATES_PER_VERTEX
import ddns.net.muchserver.gljet.utility.MAT4_SIZE
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y


val cubeVertices = floatArrayOf(
    // Back face (-Z)
    -1f, -1f, -1f, 1f, -1f, -1f, 1f, 1f, -1f,
    -1f, -1f, -1f, 1f, 1f, -1f, -1f, 1f, -1f,

    // Front face (+Z)
    -1f, -1f, 1f, 1f, 1f, 1f, 1f, -1f, 1f,
    -1f, -1f, 1f, -1f, 1f, 1f, 1f, 1f, 1f,

    // Left face (-X)
    -1f, -1f, -1f, -1f, 1f, -1f, -1f, 1f, 1f,
    -1f, -1f, -1f, -1f, 1f, 1f, -1f, -1f, 1f,

    // Right face (+X)
    1f, -1f, -1f, 1f, -1f, 1f, 1f, 1f, 1f,
    1f, -1f, -1f, 1f, 1f, 1f, 1f, 1f, -1f,

    // Bottom face (-Y)
    -1f, -1f, -1f, -1f, -1f, 1f, 1f, -1f, 1f,
    -1f, -1f, -1f, 1f, -1f, 1f, 1f, -1f, -1f,

    // Top face (+Y)
    -1f, 1f, -1f, 1f, 1f, -1f, 1f, 1f, 1f,
    -1f, 1f, -1f, 1f, 1f, 1f, -1f, 1f, 1f
)
open class Cube(
    val context: Context,
    position: FloatArray,
    vertexShaderCode: String,
    fragmentShaderCode: String
): Entity(
    cubeVertices,
    position,
    vertexShaderCode,
    fragmentShaderCode
) {
    protected var handlePosition = 0
    protected var handleMatrixVP = 0
    var timeLastUpdate = 0L
    val deltaY = 15.0f
    val deltaX = 10.0f
    var isActive = true
    val scale = floatArrayOf(1f, 1f, 1f)
    val collider = Collider(context, position, 0, scale, ColliderType.BOX)


    override fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        if(!isActive) {
            return
        }
        super.draw(matrixView, matrixProjection)

        val matrixModelView = FloatArray(MAT4_SIZE)
        Matrix.multiplyMM(matrixModelView, 0 , matrixView, 0,matrixModel, 0)
        Matrix.multiplyMM(matrixMVP, 0, matrixProjection, 0,matrixModelView, 0)

        GLES32.glUseProgram(this.glProgram)

        handlePosition = GLES32.glGetAttribLocation(this.glProgram, "vPosition").also {
            GLES32.glEnableVertexAttribArray(it)
            GLES32.glVertexAttribPointer(it, COORDINATES_PER_VERTEX, GLES32.GL_FLOAT, false, vertexStride, vertexBuffer)

            handleMatrixVP = GLES32.glGetUniformLocation(this.glProgram, "uMVPMatrix")
            GLES32.glUniformMatrix4fv(handleMatrixVP, 1, false, matrixMVP, 0)

            GLES32.glCullFace(GLES32.GL_BACK)
            GLES32.glFrontFace(GLES32.GL_CCW)
            GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)
            GLES32.glDisableVertexAttribArray(it)
        }
        collider.draw(matrixView, matrixProjection)
    }

    override fun update() {
        if(!isActive) {
            return
        }
        val timeCurrent = System.currentTimeMillis()
        val timeDelta = (timeCurrent - timeLastUpdate) / 1000f

        rotation[X] += ((deltaX * timeDelta) % 360f)
//        rotation[Y] += ((deltaY * timeDelta) % 360f)


        timeLastUpdate = timeCurrent
    }
}