package am.com.amanmeena.promotionia.utils

import java.text.SimpleDateFormat
import java.util.*

fun formatMemberSince(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return format.format(date)
}