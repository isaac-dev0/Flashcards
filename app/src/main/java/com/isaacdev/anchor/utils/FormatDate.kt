package com.isaacdev.anchor.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Formats a date string into "MMM dd, yyyy" format.
 *
 * This function attempts to parse the input `dateString` into a [LocalDateTime] object.
 * If successful, it formats the date into the pattern "MMM dd, yyyy" (e.g., "Jan 01, 2023").
 * If the parsing fails for any reason (e.g., the input string is not a valid date format),
 * the original `dateString` is returned.
 *
 * @param dateString The string representation of the date to format.
 *                   It is expected to be in a format parseable by [LocalDateTime.parse].
 * @return The formatted date string in "MMM dd, yyyy" format, or the original `dateString`
 *         if parsing fails.
 */
fun formatDate(dateString: String): String {
    return try {
        val date = LocalDateTime.parse(dateString)
        date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
    } catch (e: Exception) {
        dateString
    }
}