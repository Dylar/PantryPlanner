package de.bitb.pantryplaner.core.misc

import android.content.Context
import kotlinx.coroutines.delay
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

fun readTextFromAsset(context: Context, fileName: String): String =
    context.assets.open(fileName).bufferedReader().use { it.readText() }.replace("\\n", "\n")

