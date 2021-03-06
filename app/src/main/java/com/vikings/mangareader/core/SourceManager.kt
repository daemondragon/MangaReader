package com.vikings.mangareader.core

import android.arch.lifecycle.LiveData
import com.vikings.mangareader.source.NullSource

/**
 * Manage all source so that a source
 * can easily be retrieved when needed.
 */
object SourceManager: LiveData<List<Source>>() {
    /**
     * Contains all sources. Source count and source access
     * is supposed to be low, so using only a list is fine.
     */
    private val sources: MutableList<Source> = mutableListOf()

    /**
     * Add the source to the sources list.
     * throw an exception if the Id is already present in the list.
     * Must be called from the main thread.
     */
    fun add(source: Source) {
        sources.find { it.id == source.id }?.apply {
            throw Exception("Could not add ${source.name}, id ${this.id} already used by ${this.name}.")
        }
        sources.add(source)
        value = sources
    }

    fun get(sourceId: Int): Source {
        return sources.find { it.id == sourceId } ?: NullSource(sourceId)
    }

    /**
     * Remove the source from the source list.
     * Must be called from the main thread.
     */
    fun remove(source: Source) {
        sources.remove(source)
        value = sources
    }
}