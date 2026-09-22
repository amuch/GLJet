package ddns.net.muchserver.gljet.collider

import ddns.net.muchserver.gljet.utility.X
import ddns.net.muchserver.gljet.utility.Y
import ddns.net.muchserver.gljet.utility.Z
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt

const val X_MIN = 0
const val X_MAX = 1
const val Y_MIN = 2
const val Y_MAX = 3
const val Z_MIN = 4
const val Z_MAX = 5

class CollisionManager {

    companion object {
        fun generateMaxMin(collider: Collider): FloatArray {
            val xMax = collider.position[X] + collider.scale[X] * 0.5f
            val xMin = collider.position[X] - collider.scale[X] * 0.5f

            val yMax = collider.position[Y] + collider.scale[Y] * 0.5f
            val yMin = collider.position[Y] - collider.scale[Y] * 0.5f

            val zMax = collider.position[Z] + collider.scale[Z] * 0.5f
            val zMin = collider.position[Z] - collider.scale[Z] * 0.5f

            return floatArrayOf(xMin, xMax, yMin, yMax, zMin, zMax)
        }

        fun isCollision(colliderFirst: Collider, colliderSecond: Collider): Boolean {
//            val first = max(colliderFirst.scale[Z], max(colliderFirst.scale[X], colliderFirst.scale[Y]))
//            val second = max(colliderSecond.scale[Z], max(colliderSecond.scale[X], colliderSecond.scale[Y]))
//
//            return distance(colliderFirst, colliderSecond) < (first / 2 + second / 2)
            val first = generateMaxMin(colliderFirst)
            val second = generateMaxMin(colliderSecond)

            return first[X_MIN] <= second[X_MAX] && first[X_MAX] >= second[X_MIN] &&
                   first[Y_MIN] <= second[Y_MAX] && first[Y_MAX] >= second[Y_MIN] &&
                   first[Z_MIN] <= second[Z_MAX] && first[Z_MAX] >= second[Z_MIN]

//            if(first[X_MAX] > second[X_MIN]) {
//                return false
//            }
//            if(second[X_MIN] > first[X_MAX]) {
//                return false
//            }
//            if(first[Y_MIN] > second[Y_MAX]) {
//                return false
//            }
//            if(second[Y_MIN] > first[Y_MAX]) {
//                return false
//            }
//            if(first[Z_MAX] > second[Z_MIN]) {
//                return false
//            }
//            if(second[Z_MIN] > first[Z_MAX]) {
//                return false
//            }
//            return true
        }

        fun distance(colliderFirst: Collider, colliderSecond: Collider): Float {
            val first = colliderFirst.position
            val second = colliderSecond.position

            val x = (first[X] - second[X]).pow(2)
            val y = (first[Y] - second[Y]).pow(2)
            val z = (first[Z] - second[Z]).pow(2)

            return sqrt(x + y + z)
        }
     }
    val colliders = ArrayList<Collider>()

    fun registerCollider(collider: Collider) {
        colliders.add(collider)
    }

    fun resolveCollisions() {

    }
}