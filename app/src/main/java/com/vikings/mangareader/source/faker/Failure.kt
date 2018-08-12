package com.vikings.mangareader.source.faker

/**
 * This object is made so that [Faker] Source
 * can randomly fail, to test fail path.
 * Success rate can't be set, but is approximately 50%
 */
object FakerFailure {
    private var probabilities: MutableList<Boolean> = mutableListOf(false, true)
    private var index = 0

    fun isSuccess(): Boolean {
        val result = probabilities[index]
        ++index

        if (probabilities.size <= index) {
            index = 0
            probabilities.shuffle()
        }
        return result
    }
}