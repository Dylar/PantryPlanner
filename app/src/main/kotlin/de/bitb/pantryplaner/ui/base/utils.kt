package de.bitb.pantryplaner.ui.base

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import de.bitb.pantryplaner.ui.base.styles.BaseColors

fun highlightedText(
    text: String,
    highlighted: String,
): AnnotatedString {
    val occurrences = findAllOccurrences(text, highlighted)

    val annotatedString = buildAnnotatedString {
        if (highlighted.isBlank() || occurrences.isEmpty()) {
            append(text)
        } else {
            var currentIndex = 0
            for (occurrence in occurrences) {
                val startIndex = occurrence.start
                val endIndex = occurrence.end

                withStyle(style = SpanStyle()) {
                    append(text.substring(currentIndex, startIndex))
                }
                withStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        background = BaseColors.DarkGreen,
                    )
                ) {
                    append(text.substring(startIndex, endIndex))
                }
                currentIndex = endIndex
            }
            if (currentIndex < text.length) {
                withStyle(style = SpanStyle()) {
                    append(text.substring(currentIndex, text.length))
                }
            }
        }
    }
    return annotatedString
}

private data class SubstringRange(val start: Int, val end: Int)

private fun findAllOccurrences(text: String, substring: String): List<SubstringRange> {
    val occurrences = mutableListOf<SubstringRange>()
    var index = text.indexOf(substring, ignoreCase = true)
    while (index >= 0) {
        occurrences.add(SubstringRange(index, index + substring.length))
        index = text.indexOf(substring, index + 1, ignoreCase = true)
    }
    return occurrences
}
