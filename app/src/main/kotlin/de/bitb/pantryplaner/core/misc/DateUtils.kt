package de.bitb.pantryplaner.core.misc

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.concurrent.TimeUnit


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
