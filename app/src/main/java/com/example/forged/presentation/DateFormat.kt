package com.example.forged.presentation

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val shortDate: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE d MMM", Locale.UK)
private val longDate: DateTimeFormatter =
    DateTimeFormatter.ofPattern("EEE d MMM yyyy", Locale.UK)

fun LocalDate.toHistoryLabel(): String = format(shortDate)
fun LocalDate.toFormLabel(): String = format(longDate)
