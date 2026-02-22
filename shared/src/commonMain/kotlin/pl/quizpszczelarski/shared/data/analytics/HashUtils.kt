package pl.quizpszczelarski.shared.data.analytics

/**
 * Returns a hex prefix of the hashCode for the given input.
 *
 * Used to anonymize question IDs in analytics/crashlytics keys.
 * Full SHA-256 would require an extra library; hashCode-based hex
 * is sufficient for debugging without exposing actual IDs.
 *
 * @param input The string to hash (e.g. question ID).
 * @param length Number of hex characters to return (default 8).
 */
fun hashPrefix(input: String, length: Int = 8): String {
    return input.hashCode().toUInt().toString(16).padStart(8, '0').take(length)
}
