package ddns.net.muchserver.gljet.utility

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES32
import android.opengl.GLUtils
import android.opengl.Matrix
import androidx.annotation.RawRes
import java.nio.FloatBuffer

const val BYTES_PER_FLOAT = 4
const val COORDINATES_PER_VERTEX = 3
const val MAT4_SIZE = 16

const val X = 0
const val Y = 1
const val Z = 2
fun compileShader(type: Int, source: String): Int {
    val shader = GLES32.glCreateShader(type)
    GLES32.glShaderSource(shader, source)
    GLES32.glCompileShader(shader)

    val compileStatus = IntArray(1)
    GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, compileStatus, 0)

    if(compileStatus[0] == 0) {
        val log = GLES32.glGetShaderInfoLog(shader)
        GLES32.glDeleteShader(shader)
        throw RuntimeException("Shader compilation failed: $log")
    }

    return shader
}

fun loadRawResourceText(context: Context, @RawRes resId: Int): String {
    return context.resources.openRawResource(resId).bufferedReader().use { it.readText() }
}

fun checkShaderCompile(shader: Int) {
    val compiled = IntArray(1)
    GLES32.glGetShaderiv(shader, GLES32.GL_COMPILE_STATUS, compiled, 0)
    if(compiled[0] == 0) {
        val log = GLES32.glGetShaderInfoLog(shader)
        throw RuntimeException("Shader compile failed: $log")
    }
}

fun loadTextureFromAssets(context: Context, fileName: String): Int {
    val textureHandle = IntArray(1)
    GLES32.glGenTextures(1, textureHandle, 0)

    val inputStream = context.assets.open(fileName)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureHandle[0])
    GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)
    GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR)
    GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)

    bitmap.recycle()
    return textureHandle[0]
}

object TextureUtils {
    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)
        GLES32.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = BitmapFactory.Options().apply {
                inScaled = false // No pre-scaling
            }

            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

            GLES32.glBindTexture(GLES32.GL_TEXTURE_2D, textureHandle[0])

            // Set texture parameters
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MIN_FILTER, GLES32.GL_LINEAR_MIPMAP_LINEAR)
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_MAG_FILTER, GLES32.GL_LINEAR)
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_S, GLES32.GL_REPEAT)
            GLES32.glTexParameteri(GLES32.GL_TEXTURE_2D, GLES32.GL_TEXTURE_WRAP_T, GLES32.GL_REPEAT)

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES32.GL_TEXTURE_2D, 0, bitmap, 0)

            // Generate mipmaps
            GLES32.glGenerateMipmap(GLES32.GL_TEXTURE_2D)

            bitmap.recycle()
        } else {
            throw RuntimeException("Error generating texture handle.")
        }

        return textureHandle[0]
    }
}

fun drawModel(
    glProgram: Int,
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