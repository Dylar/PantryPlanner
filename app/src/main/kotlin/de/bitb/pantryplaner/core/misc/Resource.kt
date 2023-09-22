package de.bitb.pantryplaner.core.misc

import androidx.annotation.StringRes
import de.bitb.pantryplaner.core.misc.Logger.logCrashlytics
import de.bitb.pantryplaner.ui.base.comps.ResString
import kotlinx.coroutines.flow.MutableStateFlow

sealed class Resource<T>(val data: T? = null, val message: ResString? = null) {

    class Success<T>(data: T?) : Resource<T>(data) {
        constructor() : this(null)
    }

    class Error<T>(message: ResString, data: T? = null) : Resource<T>(data, message) {
        constructor(e: Throwable, data: T? = null) : this(e.message ?: e.toString(), data)
        constructor(message: String, data: T? = null)
                : this(ResString.DynamicString(message), data)

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

fun <T> Int.asResourceError(): Resource.Error<T> = Resource.Error(this)
fun <T> String.asResourceError(): Resource.Error<T> = Resource.Error(this)
fun <T> Throwable.asResourceError(data: T? = null): Resource.Error<T> = Resource.Error(this, data)

suspend fun <T> tryIt(
    errorValue: T? = null,
    onError: suspend (Exception) -> Resource<T>? = { _ -> null },
    onTry: suspend () -> Resource<T>,
): Resource<T> = try {
    onTry()
} catch (e: Exception) {
    e.printStackTrace()
    logCrashlytics(e)
    onError(e) ?: e.asResourceError(errorValue)
//    throw e
}

suspend fun <T, E> castOnError(
    resp: Resource<E>,
    func: suspend () -> Resource<T>,
): Resource<T> =
    if (resp is Resource.Error) resp.castTo()
    else tryIt { func() }