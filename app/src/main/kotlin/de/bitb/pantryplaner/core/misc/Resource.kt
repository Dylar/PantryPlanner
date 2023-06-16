package de.bitb.pantryplaner.core.misc

import androidx.annotation.StringRes
import de.bitb.pantryplaner.core.misc.Logger.logCrashlytics
import de.bitb.pantryplaner.ui.base.composable.ResString

sealed class Resource<T>(val data: T? = null, val message: ResString? = null) {
    class Success<T>(data: T?) : Resource<T>(data) {
        constructor() : this(null)
    }

    class Error<T> constructor(message: ResString, data: T? = null) : Resource<T>(data, message) {
        constructor(e: Throwable, data: T? = null) : this(e.message ?: e.toString(), data)
        constructor(message: String, data: T? = null)
                : this(ResString.DynamicString(message), data)

        constructor(@StringRes stringId: Int, data: T? = null)
                : this(ResString.ResourceString(stringId), data)

        fun <E> castTo(): Error<E> {
            return Error(message!!)
        }
    }

    val hasData: Boolean
        get() = data != null
}

fun <T> Int.asResourceError(): Resource.Error<T> = Resource.Error(this)
fun <T> String.asResourceError(): Resource.Error<T> = Resource.Error(this)
fun <T> Throwable.asResourceError(data: T? = null): Resource.Error<T> = Resource.Error(this, data)

suspend fun <T> tryIt(
    onError: suspend (Exception) -> Resource<T>? = { _ -> null },
    onTry: suspend () -> Resource<T>,
): Resource<T> = try {
    onTry()
} catch (e: Exception) {
    e.printStackTrace()
    logCrashlytics(e)
    onError(e) ?: e.asResourceError()
}