package de.bitb.pantryplaner.core.misc

import android.content.Context
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import kotlin.system.measureTimeMillis

suspend fun <T> atLeast(duration: Long, func: suspend () -> T): T {
    var result: T
    val runTime = measureTimeMillis { result = func() }
    val remainingTime = duration - runTime
    if (remainingTime > 0) {
        delay(remainingTime)
    }
    return result
}

fun readTextFromAsset(context: Context, fileName: String): String =
    context.assets.open(fileName).bufferedReader().use { it.readText() }.replace("\\n", "\n")

val Double.formatted: String
    get() {
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(this)
    }

fun String.capitalizeFirstCharacter(): String {
    if (isEmpty()) {
        return this
    }
    return this[0].uppercaseChar() + this.substring(1)
}

fun <K, V> Map<K, List<V>>.removeDuplicatesFromLists(): Map<K, List<V>> {
    val uniqueEntrys = mutableSetOf<V>()
    val map = mutableMapOf<K, List<V>>()
    for ((key, list) in this) {
        val newList = list.filter { item ->
            val isNewEntry = !uniqueEntrys.contains(item)
            if (isNewEntry) uniqueEntrys.add(item)
            isNewEntry
        }
        map[key] = newList
    }
    return map
}
