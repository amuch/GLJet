package ddns.net.muchserver.gljet.render

interface GLRenderer {
    fun initGL()
    fun update()
    fun draw(matrixView: FloatArray, matrixProjection: FloatArray)
}