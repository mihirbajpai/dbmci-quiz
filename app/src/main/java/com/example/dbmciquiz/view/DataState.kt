package com.example.dbmciquiz.view

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Loading / Success / Failure for an async fetch. Use [update] to map the result.
 */
sealed interface DataState<out T> {
    class Loading<T> : DataState<T>
    data class Success<out T>(val value: T) : DataState<T>
    data class Failure(val error: Throwable) : DataState<Nothing>
}

/** Runs [fetchData] behind a Loading state, then stores Success or Failure. */
suspend fun <T> MutableStateFlow<DataState<T>>.update(fetchData: suspend () -> T) {
    value = DataState.Loading()
    value = try {
        DataState.Success(fetchData())
    } catch (e: Throwable) {
        DataState.Failure(e)
    }
}
