package pl.quizpszczelarski.app.platform

/**
 * Returns true when [currentVersion] is strictly below [minVersion].
 * Compares each dot-separated segment numerically.
 */
fun isVersionOutdated(currentVersion: String, minVersion: String): Boolean {
    val current = currentVersion.split(".").map { it.toIntOrNull() ?: 0 }
    val min = minVersion.split(".").map { it.toIntOrNull() ?: 0 }
    val size = maxOf(current.size, min.size)
    for (i in 0 until size) {
        val c = current.getOrElse(i) { 0 }
        val m = min.getOrElse(i) { 0 }
        if (c < m) return true
        if (c > m) return false
    }
    return false
}
