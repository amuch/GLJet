package ddns.net.muchserver.gljet.model

import android.content.Context
import ddns.net.muchserver.gljet.R
import ddns.net.muchserver.gljet.material.Material
import ddns.net.muchserver.gljet.utility.TextureUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class ModelLoader {
    private val vertexValues = ArrayList<Float>()
    private val vertexTriangles = ArrayList<Float>()
    private val textureCoordinates = ArrayList<Float>()
    private val stValues = ArrayList<Float>()
    private val normals = ArrayList<Float>()
    private val normalValues = ArrayList<Float>()
    private val mapMaterial = mutableMapOf<String, Material>()
    private val materialsFace = mutableListOf<String>()
    private var materialCurrent = ""
    private var idResourceTexture: Int = 0

    @Throws(IOException::class)
    fun parseObject(context: Context, idResource: Int) {
        val input = context.resources.openRawResource(idResource)
        val bufferedReader = BufferedReader(InputStreamReader(input))
        var line: String?
        while(true) {
            line = bufferedReader.readLine() ?: break
            when {
                line.startsWith("v ") -> {
                    vertexValues.addAll(
                        line.substring(2).split("\\s+".toRegex()).map{ it.toFloat() }
                    )
                }
                line.startsWith("vt ") -> {
                    stValues.addAll(
                        line.substring(3).split("\\s+".toRegex()).map{ it.toFloat() }
                    )
                }
                line.startsWith("vn ") -> {
                    normalValues.addAll(
                        line.substring(3).split("\\s+".toRegex()).map{ it.toFloat() }
                    )
                }
                line.startsWith("f ") -> {
                    val tokens = line.substring(2).split("\\s+".toRegex())
                    for(token in tokens) {
                        val (vertex, textureCoordinate, normal) = token.split("/")
                        val refVertex = (vertex.toInt() - 1) * 3
                        val refTexture = (textureCoordinate.toInt() - 1) * 2
                        val refNormal = (normal.toInt() - 1) * 3

                        vertexTriangles.addAll(
                            listOf(
                                vertexValues[refVertex],
                                vertexValues[refVertex + 1],
                                vertexValues[refVertex + 2]
                            )
                        )
                        textureCoordinates.addAll(
                            listOf(
                                stValues[refTexture],
                                stValues[refTexture + 1]
                            )
                        )
                        normals.addAll(
                            listOf(
                                normalValues[refNormal],
                                normalValues[refNormal + 1],
                                normalValues[refNormal + 2]
                            )
                        )
                        materialsFace.add(materialCurrent)
                    }
                }
                line.startsWith("mtllib ") -> {
                    val fileMaterial = line.substring(7).trim()
                    val idResourceMaterial = context.resources.getIdentifier(
                        fileMaterial.substringBefore("."),
                            "raw",
                            context.packageName
                        )
                    parseMaterial(context, idResourceMaterial)
                }
                line.startsWith("usemtl ") -> {
                    materialCurrent = line.substring(7).trim()
                }
            }
        }
        input.close()
    }

    @Throws(IOException::class)
    fun parseMaterial(context: Context, idResource: Int) {
        val input = context.resources.openRawResource(idResource)
        val bufferedReader = BufferedReader(InputStreamReader(input))
        var line: String?
        var nameMaterialCurrent = ""
        var material = Material()
        while(true) {
            line = bufferedReader.readLine() ?: break
            when {
                line.startsWith("newmtl ") -> {
                    if(nameMaterialCurrent.isNotEmpty()) {
                        mapMaterial[nameMaterialCurrent] = material
                    }
                    nameMaterialCurrent = line.substring(7).trim()
                    material = Material()
                }
                line.startsWith("Kd ") -> {
                    val tokens = line.split(" ")
                    material.diffuse = floatArrayOf(
                        tokens[1].toFloat(),
                        tokens[2].toFloat(),
                        tokens[3].toFloat()
                    )
                }
                line.startsWith("map_Kd ") -> {
                    val nameTexture = line.substring(7).trim().substringBeforeLast(".")
                    material.idResourceTexture = context.resources.getIdentifier(
                        nameTexture,
                        "drawable",
                        context.packageName
                    )
                    idResourceTexture = TextureUtils.loadTexture(context, material.idResourceTexture)
                }
            }
        }
        if(nameMaterialCurrent.isNotEmpty()) {
            mapMaterial[nameMaterialCurrent] = material
        }
        input.close()
    }

    fun getVertexCount(): Int {
        return (this.vertexTriangles.size / 3)
    }

    fun getVertices(): FloatArray {
        val vertices = FloatArray(this.vertexTriangles.size)
        for(i in vertexTriangles.indices) {
            vertices[i] = vertexTriangles[i]
        }
        return vertices
    }

    fun getTextureCoordinates(): FloatArray {
        val coordinates = FloatArray(textureCoordinates.size)
        for(i in textureCoordinates.indices) {
            coordinates[i] = textureCoordinates[i]
        }
        return coordinates
    }

    fun getNormals(): FloatArray {
        val normal = FloatArray(normals.size)
        for(i in normals.indices) {
            normal[i] = normals[i]
        }
        return normal
    }

    fun getidResourceTexture(): Int {
        return this.idResourceTexture
    }
}