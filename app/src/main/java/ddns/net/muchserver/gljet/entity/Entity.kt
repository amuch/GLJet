package ddns.net.muchserver.gljet.entity

import android.opengl.GLES32
import android.opengl.Matrix
import ddns.net.muchserver.gljet.render.GLRenderer
import ddns.net.muchserver.gljet.utility.BYTES_PER_FLOAT
import ddns.net.muchserver.gljet.utility.COORDINATES_PER_VERTEX
import ddns.net.muchserver.gljet.utility.MAT4_SIZE
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import ddns.net.muchserver.gljet.utility.compileShader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

open class Entity(
    val vertices: FloatArray,
    protected var position: FloatArray,
    vertexShaderCode: String,
    fragmentShaderCode: String
): GLRenderer {
    val vertexCount = vertices.size / COORDINATES_PER_VERTEX
    val vertexStride = COORDINATES_PER_VERTEX * BYTES_PER_FLOAT

    protected var glProgram: Int = -1
    protected var matrixModel = FloatArray(MAT4_SIZE)
    protected var matrixMVP = FloatArray(MAT4_SIZE)
    protected var rotation = floatArrayOf(0f, 0f, 0f)

    val vertexShader: Int
    val fragmentShader: Int
    val vertexBuffer: FloatBuffer

    var isInitialized = false

    init {
        this.vertexShader = compileShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        this.fragmentShader = compileShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * BYTES_PER_FLOAT).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }
    }

    override fun initGL() {
        this.glProgram = GLES32.glCreateProgram().also {
            GLES32.glAttachShader(it, vertexShader)
            GLES32.glAttachShader(it, fragmentShader)
            GLES32.glLinkProgram(it)
        }
        isInitialized = true
    }

    override fun update() {}

    override fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        if(!isInitialized) {
            initGL()
        }
        Matrix.setIdentityM(matrixModel, 0)
        Matrix.translateM(matrixModel, 0, position[X], position[Y], position[Z])

        if(rotation[X] != 0.0f) {
            Matrix.rotateM(matrixModel, 0, rotation[X], 1f, 0f, 0f)
        }
        if(rotation[Y] != 0.0f) {
            Matrix.rotateM(matrixModel, 0, rotation[Y], 0f, 1f, 0f)
        }
        if(rotation[Z] != 0.0f) {
            Matrix.rotateM(matrixModel, 0, rotation[Z], 0f, 0f, 1f)
        }
    }
}