package ddns.net.muchserver.gljet.scene

import android.content.Context
import android.opengl.GLES32
import android.opengl.Matrix
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.render.GLRenderer
import ddns.net.muchserver.gljet.utility.BYTES_PER_FLOAT
import ddns.net.muchserver.gljet.utility.MAT4_SIZE
import ddns.net.muchserver.gljet.utility.TextureUtils
import ddns.net.muchserver.gljet.utility.compileShader
import ddns.net.muchserver.gljet.utility.loadRawResourceText
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

val verticesSkyBox = floatArrayOf(
    -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
    1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f,
    1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
    1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f,
    1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
    -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f,
    -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
    -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
    -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f,
    1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f,
    -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f,
    1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f
)

val textureCoordinatesSkyBox = floatArrayOf(
    .25f, 0.6666667f, .25f, 0.33333334f, .5f, 0.33333334f,  // front face lower left
    .5f, 0.33333334f, .5f, 0.6666667f, .25f, 0.6666667f,  // front face upper right
    .5f, 0.33333334f, .75f, 0.33333334f, .5f, 0.6666667f,  // right face lower left
    .75f, 0.33333334f, .75f, 0.6666667f, .5f, 0.6666667f,  // right face upper right
    .75f, 0.33333334f, 1.0f, 0.33333334f, .75f, 0.6666667f,  // back face lower
    1.0f, 0.33333334f, 1.0f, 0.6666667f, .75f, 0.6666667f,  // back face upper
    0.0f, 0.33333334f, .25f, 0.33333334f, 0.0f, 0.6666667f,  // left face lower
    .25f, 0.33333334f, .25f, 0.6666667f, 0.0f, 0.6666667f,  // left face upper
    .25f, 0.0f, .5f, 0.0f, .5f, 0.33333334f,  // bottom face front
    .5f, 0.33333334f, .25f, 0.33333334f, .25f, 0.0f,  // bottom face back
    .25f, 0.6666667f, .5f, 0.6666667f, .5f, 1.0f,  // top face back
    .5f, 1.0f, .25f, 1.0f, .25f, 0.6666667f // top face front
)

val flippedTextureCoordinates = textureCoordinatesSkyBox.mapIndexed { i, v ->
    if (i % 2 == 1) 1.0f - v else v
}.toFloatArray()

class SkyBox(val context: Context): GLRenderer {
    var glProgram: Int = -1
    var handlePosition: Int = -1
    val vertexCount = verticesSkyBox.size / 3
    val idTexture = TextureUtils.loadTexture(context, R.drawable.skybox_space_3)
    val shaderVertex: Int
    val shaderFragment: Int
    private var isInitialized = false
    lateinit var bufferVertex: FloatBuffer
    lateinit var bufferTexture: FloatBuffer

    val matrixModel = FloatArray(MAT4_SIZE)

    init {
        val vertexShaderCode = loadRawResourceText(context, R.raw.shader_vertex_sky_box)
        val fragmentShaderCode = loadRawResourceText(context, R.raw.shader_fragment_sky_box)
        shaderVertex = compileShader(GLES32.GL_VERTEX_SHADER, vertexShaderCode)
        shaderFragment = compileShader(GLES32.GL_FRAGMENT_SHADER, fragmentShaderCode)
    }
    override fun initGL() {
        glProgram = GLES32.glCreateProgram().also {
            GLES32.glAttachShader(it, shaderVertex)
            GLES32.glAttachShader(it, shaderFragment)
            GLES32.glLinkProgram(it)
        }

        bufferVertex = ByteBuffer.allocateDirect(verticesSkyBox.size * BYTES_PER_FLOAT).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(verticesSkyBox)
                position(0)
            }
        }

        bufferTexture = ByteBuffer.allocateDirect(textureCoordinatesSkyBox.size * BYTES_PER_FLOAT).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(textureCoordinatesSkyBox)
                position(0)
            }
        }

        isInitialized = true
    }

    override fun update() {}

    override fun draw(matrixView: FloatArray, matrixProjection: FloatArray) {
        if(!isInitialized) {
            initGL()
        }

        GLES32.glUseProgram(this.glProgram)

        val viewNoTranslation = FloatArray(MAT4_SIZE)
        System.arraycopy(matrixView, 0, viewNoTranslation, 0, MAT4_SIZE)
        viewNoTranslation[12] = 0f
        viewNoTranslation[13] = 0f
        viewNoTranslation[14] = 0f

        Matrix.setIdentityM(matrixModel, 0)
        Matrix.scaleM(matrixModel, 0, 50f, 50f, 50f)

        val matrixModelView = FloatArray(MAT4_SIZE)
        Matrix.multiplyMM(matrixModelView, 0, viewNoTranslation, 0, matrixModel, 0)

        val matrixMVP = FloatArray(MAT4_SIZE)
        Matrix.multiplyMM(matrixMVP, 0, matrixProjection, 0, matrixModelView, 0)

        handlePosition = GLES32.glGetAttribLocation(glProgram, "vPosition")
        GLES32.glEnableVertexAttribArray(handlePosition)
        GLES32.glVertexAttribPointer(handlePosition, 3, GLES32.GL_FLOAT, false, 3 * BYTES_PER_FLOAT, bufferVertex)

        val handleTextureCoordinates = GLES32.glGetAttribLocation(glProgram, "aTexCoord")
        GLES32.glEnableVertexAttribArray(handleTextureCoordinates)
        GLES32.glVertexAttribPointer(handleTextureCoordinates, 2, GLES32.GL_FLOAT, false, 2 * BYTES_PER_FLOAT, bufferTexture)

        val handleMatrixMVP = GLES32.glGetUniformLocation(glProgram, "uMVPMatrix")
        GLES32.glUniformMatrix4fv(handleMatrixMVP, 1, false, matrixMVP, 0)

        GLES32.glActiveTexture(GLES32.GL_TEXTURE0)
        GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, idTexture)
        val textureUniformHandle = GLES32.glGetUniformLocation(glProgram, "uTexture")
        GLES32.glUniform1i(textureUniformHandle, 0)

        GLES32.glFrontFace(GLES32.GL_CCW)
        GLES32.glDisable(GLES32.GL_DEPTH_TEST)
        GLES32.glDisable(GLES32.GL_CULL_FACE)
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)

        GLES32.glDisableVertexAttribArray(handlePosition)
        GLES32.glDisableVertexAttribArray(handleTextureCoordinates)

//        drawModel(
//            glProgram,
//            bufferVertex,
//            bufferTexture,
//            vertexCount,
//            matrixModelView,
//            matrixProjection,
//            idTexture
//        )
    }

}