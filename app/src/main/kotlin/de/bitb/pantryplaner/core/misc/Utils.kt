package de.bitb.pantryplaner.core.misc

import android.content.Context
import kotlinx.coroutines.delay
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit
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

fun timeExceeded(date1: Date, date2: Date, diff: Long): Boolean =
    getMillisecondsBetweenDates(date1, date2) > diff

fun getMillisecondsBetweenDates(date1: Date, date2: Date): Long = date2.time - date1.time

fun getPercentageDiff(milliseconds: Long, targetValue: Long): Float {
    val percentage = (milliseconds.toFloat() / targetValue.toFloat())
    return if (percentage > 1) 1f else if (percentage < 0) 0f else percentage
}

fun getHours(duration: Long): Int {
    return TimeUnit.MILLISECONDS.toHours(duration).toInt()
}

fun getMins(duration: Long): Int {
    return TimeUnit.MILLISECONDS.toMinutes(duration).toInt() % 60
}

fun formatDuration(duration: Long): String {
    val hours = getHours(duration)
    val minutes = getMins(duration)

    return String.format("%02dh:%02dm", hours, minutes)
}

fun calculateMilliseconds(hours: Int, minutes: Int): Long {
    val totalMinutes = hours * 60 + minutes
    return totalMinutes * 60 * 1000L
}

fun parseDateTimeString(date: String): LocalDateTime =
    LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

fun parseDateString(date: String): LocalDate =
    LocalDate.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy"))

fun formatDateTimeString(date: LocalDateTime): String =
    date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

fun formatDateString(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))

fun formatDateNow(): String = formatDateTimeString(LocalDateTime.now())

val Double.formatted: String
    get() {
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(this)
    }

fun readTextFromAsset(context: Context, fileName: String): String =
    context.assets.open(fileName).bufferedReader().use { it.readText() }.replace("\\n", "\n")

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