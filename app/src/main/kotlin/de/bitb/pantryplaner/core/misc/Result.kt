package de.bitb.pantryplaner.core.misc

import androidx.annotation.StringRes
import de.bitb.pantryplaner.BuildConfig
import de.bitb.pantryplaner.core.misc.Logger.logCrashlytics
import de.bitb.pantryplaner.ui.base.comps.ResString
import kotlinx.coroutines.flow.MutableStateFlow

fun <T> Int.asError(): Result.Error<T> = Result.Error(this)
fun <T> String.asError(): Result.Error<T> = Result.Error(this)
fun <T> Boolean.asError(): Result.Error<T> = Result.Error(this.toString())
fun <T> Throwable.asError(data: T? = null): Result.Error<T> = Result.Error(this, data)

sealed class Result<T>(val data: T? = null, val message: ResString? = null) {

    class Success<T>(data: T?) : Result<T>(data) {
        constructor() : this(null)
    }

    class Error<T>(message: ResString, data: T? = null) : Result<T>(data, message) {
        constructor(e: Throwable, data: T? = null) : this(e.message ?: e.toString(), data)
        constructor(message: String, data: T? = null) : this(ResString.DynamicString(message), data)
        constructor(@StringRes stringId: Int, data: T? = null)
                : this(ResString.ResourceString(stringId), data)

        fun <E> castTo(data: E? = null): Error<E> {
            return Error(message!!, data)
        }
    }

    fun <E> asFlow() = MutableStateFlow(this)

    val hasData: Boolean
        get() = data != null
}

suspend fun <T> tryIt(
    errorValue: T? = null,
    onError: suspend (Exception) -> Result<T> = { e -> e.asError(errorValue) },
    onTry: suspend () -> Result<T>,
): Result<T> = try {
    onTry()
} catch (e: Exception) {
    if (BuildConfig.DEBUG) e.printStackTrace()
    logCrashlytics(e)
    onError(e)
}

suspend fun <T, E> castOnError(
    resp: Result<E>,
    func: suspend () -> Result<T>,
): Result<T> =
    if (resp is Result.Error) resp.castTo()
    else tryIt { func() }