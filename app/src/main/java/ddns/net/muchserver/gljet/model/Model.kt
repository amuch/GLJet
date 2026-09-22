package ddns.net.muchserver.gljet.model

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import ddns.net.muchserver.gljet.render.GLRenderer
import ddns.net.muchserver.gljet.utility.BYTES_PER_FLOAT
import ddns.net.muchserver.gljet.utility.COORDINATES_PER_VERTEX
import ddns.net.muchserver.gljet.utility.MAT4_SIZE
import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import ddns.net.muchserver.gljet.utility.checkShaderCompile
import ddns.net.muchserver.gljet.utility.compileShader
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class Model(
    val context: Context,
    val idResource: Int,
    var position: FloatArray,
    var rotation: FloatArray,
    shaderCodeVertex: String,
    shaderCodeFragment: String,
    val scale: FloatArray = floatArrayOf(1f, 1f, 1f),
    val onUpdate: (() -> Unit)? = null
): GLRenderer {

    val vertexStride = COORDINATES_PER_VERTEX * BYTES_PER_FLOAT

    var glProgram: Int = -1
    val shaderVertex: Int
    val shaderFragment: Int
    var isInitialized = false

    var vertexCount = 0
    var idTexture = 0
    val matrixModel = FloatArray(MAT4_SIZE)

    lateinit var vertices: FloatArray
    lateinit var bufferVertex: FloatBuffer

    lateinit var textureCoordinates: FloatArray
    lateinit var bufferTexture: FloatBuffer

    lateinit var normals: FloatArray

    init {
        shaderVertex = compileShader(GLES32.GL_VERTEX_SHADER, shaderCodeVertex)
        shaderFragment = compileShader(GLES32.GL_FRAGMENT_SHADER, shaderCodeFragment)

        checkShaderCompile(shaderVertex)
        checkShaderCompile(shaderFragment)
    }


    override fun initGL() {
        val modelLoader = ModelLoader()
        try {
            modelLoader.parseObject(context, idResource)
            vertexCount = modelLoader.getVertexCount()
            vertices = modelLoader.getVertices()
            textureCoordinates = modelLoader.getTextureCoordinates()
            normals = modelLoader.getNormals()
            idTexture = modelLoader.getidResourceTexture()
        }
        catch(e: IOException) {
            e.printStackTrace()
        }

        this.glProgram = GLES32.glCreateProgram().also { program ->
            GLES32.glAttachShader(program,shaderVertex)
            GLES32.glAttachShader(program,shaderFragment)
            GLES32.glLinkProgram(program)
        }

        bufferVertex = ByteBuffer.allocateDirect(vertices.size * BYTES_PER_FLOAT).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

        bufferTexture = ByteBuffer.allocateDirect(textureCoordinates.size * BYTES_PER_FLOAT).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(textureCoordinates)
                position(0)
            }
        }
        isInitialized = true
    }

//    override fun update() {
//        onUpdate?.invoke()
//    }

    var timeLastUpdate = 0L
    val deltaY = 15.0f
    val deltaX = 10.0f
    override fun update() {
//        val timeCurrent = System.currentTimeMillis()
//        val timeDelta = (timeCurrent - timeLastUpdate) / 1000f
//
//        rotation[X] += ((deltaX * timeDelta) % 360f)
//        rotation[Y] += ((deltaY * timeDelta) % 360f)
//
//
//        timeLastUpdate = timeCurrent
    }

    override fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        if(!isInitialized) {
            initGL()
        }

        GLES32.glUseProgram(this.glProgram)

        val matrixModelView = FloatArray(MAT4_SIZE)
        Matrix.setIdentityM(matrixModel, 0)
        Matrix.translateM(matrixModel, 0, position[X], position[Y], position[Z])
        Matrix.scaleM(matrixModel, 0, scale[X], scale[Y], scale[Z])

        if(rotation[X] != 0.0f) {
            Matrix.rotateM(matrixModel, 0, rotation[X], 1f, 0f, 0f)
        }
        if(rotation[Y] != 0.0f) {
            Matrix.rotateM(matrixModel, 0, rotation[Y], 0f, 1f, 0f)
        }
        if(rotation[Z] != 0.0f) {
            Matrix.rotateM(matrixModel, 0, rotation[Z], 0f, 0f, 1f)
        }

        Matrix.multiplyMM(matrixModelView, 0, matrixView, 0, matrixModel, 0)

        drawModel(bufferVertex, bufferTexture, vertexCount, matrixModelView, matrixProjection, idTexture)
    }

    fun drawModel(
        bufferVertex: FloatBuffer,
        bufferTexture: FloatBuffer?,
        vertexCount: Int,
        matrixModelView: FloatArray,
        matrixProjection: FloatArray,
        idTexture: Int?
    ) {
        val matrixMVP = FloatArray(MAT4_SIZE)
        Matrix.multiplyMM(matrixMVP, 0 , matrixProjection, 0, matrixModelView, 0)

        GLES32.glUseProgram(glProgram)

        val handlePosition = GLES32.glGetAttribLocation(glProgram, "vPosition")
        val handleMatrixMVP = GLES32.glGetUniformLocation(glProgram, "uMVPMatrix")
        GLES32.glEnableVertexAttribArray(handlePosition)
        GLES32.glVertexAttribPointer(handlePosition, 3, GLES32.GL_FLOAT, false, 3 * BYTES_PER_FLOAT, bufferVertex)

        if(bufferTexture != null && idTexture != null) {
            val handleTexture = GLES32.glGetAttribLocation(glProgram, "aTexCoord")
            val handleTextureUniform = GLES32.glGetUniformLocation(glProgram, "uTexture")

            GLES32.glEnableVertexAttribArray(handleTexture)
            GLES32.glVertexAttribPointer(handleTexture, 2, GLES32.GL_FLOAT, false, 2 * BYTES_PER_FLOAT, bufferTexture)

            GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, idTexture)
            GLES32.glUniform1i(handleTextureUniform, 0)
        }

        GLES32.glUniformMatrix4fv(handleMatrixMVP, 1, false, matrixMVP, 0)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)

        if(bufferTexture != null) {
            val handleTexture = GLES32.glGetAttribLocation(glProgram, "aTexCoord")
            GLES32.glDisableVertexAttribArray(handleTexture)
        }
        GLES32.glDisableVertexAttribArray(handlePosition)
    }
}