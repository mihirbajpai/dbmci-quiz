package com.example.dbmciquiz.data

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Loading / Success / Failure wrapper for an async data fetch.
 *
 * Created in the ViewModel (presentation layer); the data/domain layers stay unaware of it.
 * Use [update] on a state flow to run a fetch and map the result automatically.
 */
sealed interface DataState<out T> {
    class Loading<T> : DataState<T>
    data class Success<out T>(val value: T) : DataState<T>
    data class Failure(val error: Throwable) : DataState<Nothing>
}

/** Sets this flow to [DataState.Loading], runs [fetchData], then assigns Success/Failure. */
suspend fun <T> MutableStateFlow<DataState<T>>.update(fetchData: suspend () -> T) {
    value = DataState.Loading()
    value = try {
        DataState.Success(fetchData())
    } catch (e: Throwable) {
        DataState.Failure(e)
    }
}
