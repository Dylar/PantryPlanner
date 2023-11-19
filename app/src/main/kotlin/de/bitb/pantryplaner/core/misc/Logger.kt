package de.bitb.pantryplaner.core.misc

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val PACKAGE_NAME = "de.bitb.pantryplaner."
const val LOG_BORDER_BOT: String = "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"
const val LOG_BORDER_TOP: String = "---------------------------------------------------------------"

enum class PrintLevel { INFO, WARNING, ERROR, SYSTEM }

@Suppress("unused")
object Logger {
    val loggingActive: Boolean = true

    private var time: Long = 0

    @Suppress("unused")
    fun startTimer() {
        time = System.currentTimeMillis()
    }

    @Suppress("unused")
    fun printTimer(msg: String) {
        val inMillis = (System.currentTimeMillis() - time).toDouble()
        log("Start Timer" to "$msg (TIME: $inMillis)")
    }

    @SuppressWarnings("FunctionCouldBePrivate")
    fun log(vararg params: Pair<String, *>, level: PrintLevel = PrintLevel.SYSTEM) {
        val log = createLog(*params)
        printMessage(
            "\n${LOG_BORDER_TOP}" +
                    "\nTime:${log.timeStamp}" +
                    "\nParams:\n${log.params}",
            "\nThread:${log.thread}" +
                    "\nStack:${log.stack}" +
                    "\n$LOG_BORDER_BOT",
            level
        )
    }

    fun print(message: String, level: PrintLevel = PrintLevel.SYSTEM) {
        val log = createLog("" to message)
        val tag = "\n${LOG_BORDER_TOP}" +
                "\nTime:${log.timeStamp}"
        val msg = "\nParams:${log.params}" +
                "\n$LOG_BORDER_BOT"
        printMessage(tag, msg, level)
    }

    private fun printMessage(tag: String, message: String, level: PrintLevel = PrintLevel.SYSTEM) {
        if (loggingActive) {
            when (level) {
                PrintLevel.INFO -> Log.i(tag, message)
                PrintLevel.WARNING -> Log.w(tag, message)
                PrintLevel.ERROR -> Log.e(tag, message)
                PrintLevel.SYSTEM -> println(tag + message)
            }
        }
    }

    private fun createLog(vararg params: Pair<String, *>): LogData {
        return Thread.currentThread().let { thread ->
            val xParams = params
                .map { if (it.first.isBlank()) it.second.toString() else "   ${it.first}: ${it.second}" }
                .reduce { first, second -> first + "\n" + second }

            val xThread = thread.name
                .removeArrayBrackets()
                .removePackage()

            val xStack = thread
                .stackTrace
                .filter(appClass())
                .asSequence()
                .drop(3)
                .map { "\n$it".removePackage() }
                .toList()
                .toTypedArray()
                .contentDeepToString()
                .removePackage()
                .removeArrayBrackets()

            LogData(xThread, xParams, xStack)
        }
    }

    fun logCrashlytics(e: Exception) {
        FirebaseCrashlytics.getInstance().recordException(e)
    }
}

data class LogData(
    val thread: String,
    val params: String,
    val stack: String,
    val timeStamp: String = LocalDateTime.now()
        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
)

private fun appClass(): (StackTraceElement) -> Boolean = { it.className.contains(PACKAGE_NAME) }
private fun String.removePackage(): String = replace(PACKAGE_NAME, "")
private fun String.removeArrayBrackets(): String = replace("[", "").replace("]", "")
