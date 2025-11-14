package am.com.amanmeena.promotionia.utils

import java.util.concurrent.TimeUnit

fun getRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    val months = days / 30
    val years = days / 365

    return when {
        seconds < 60 -> "Joined just now"
        minutes < 60 -> "Joined $minutes minute${if (minutes > 1) "s" else ""} ago"
        hours < 24 -> "Joined $hours hour${if (hours > 1) "s" else ""} ago"
        days < 30 -> "Joined $days day${if (days > 1) "s" else ""} ago"
        months < 12 -> "Joined $months month${if (months > 1) "s" else ""} ago"
        else -> "Joined $years year${if (years > 1) "s" else ""} ago"
    }
}