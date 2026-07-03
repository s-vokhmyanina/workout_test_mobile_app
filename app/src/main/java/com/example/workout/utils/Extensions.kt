package com.example.workout.utils

import android.util.DisplayMetrics
import kotlin.math.roundToInt

fun String?.addMinutes(): String {
    return this?.toIntOrNull()?.let { "$it минут" } ?: "--"
}

fun Int.dpToPx(displayMetrics: DisplayMetrics): Int =
    (this * (displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
