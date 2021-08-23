package com.example.tmdbapplication.util

import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date.roll(field: Int, amount: Int): Date {
    Calendar.getInstance().apply {
        time = this@roll
        roll(field, amount)
        return time
    }
}

fun Date.rollDownTwoWeeks(): Date {
    return roll(Calendar.DAY_OF_YEAR, -14)
}

fun Date.rollUpFourWeeks(): Date {
    return roll(Calendar.DAY_OF_YEAR, 56)
}

fun Date.formatted(): String {
    return DateFormat.format("yyyy-MM-dd", this).toString()
}

fun String.formatDate(): String {
    return if (this.isBlank()) {
        "No release date"
    } else {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .parse(this)
        DateFormat.format("MMM d, yyyy", date).toString()
    }
}