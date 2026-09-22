package ddns.net.muchserver.gljet.material

data class Material(
    var diffuse: FloatArray = floatArrayOf(1f, 1f, 1f),
    var fileTexture: String? = null,
    var idResourceTexture: Int = -1
) {
    override fun equals(other: Any?): Boolean {
        if(this == other) {
            return true
        }

        if(javaClass != other?.javaClass) {
            return false
        }

        other as Material
        if(!this.diffuse.contentEquals(other.diffuse)) {
            return false
        }

        if(this.fileTexture != other.fileTexture) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        var result = diffuse.contentHashCode()
        result = 31 * result + (fileTexture?.hashCode() ?: 0)
        return result
    }
}