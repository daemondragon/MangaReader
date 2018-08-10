package com.vikings.mangareader.core

/**
 * Manage all source so that a source
 * can easily be retrieved when needed.
 */
object SourceManager {
    /**
     * Contains all sources. Source count and source access
     * is supposed to be low, so using only a list is fine.
     */
    private val sources: MutableList<Source> = mutableListOf()

    /**
     * Add the source to the sources list.
     * throw an exception if the Id is already present in the list.
     */
    fun add(source: Source) {
        sources.find { it.id == source.id }?.apply {
            throw Exception("Could not add ${source.name}, id ${this.id} already used by ${this.name}.")
        }
        sources.add(source)
    }

    fun get(sourceId: Int): Source? {
        return sources.find { it.id == sourceId }
    }

    fun remove(source: Source) {
        sources.remove(source)
    }

    fun all(): List<Source> {
        return sources
    }
}