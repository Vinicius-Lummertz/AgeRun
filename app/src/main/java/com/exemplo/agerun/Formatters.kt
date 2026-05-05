package com.exemplo.agerun

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val brazilianPortuguese = Locale.forLanguageTag("pt-BR")
private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", brazilianPortuguese)
private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'as' HH:mm", brazilianPortuguese)

fun formatDateTime(value: String): String {
    return runCatching {
        OffsetDateTime.parse(value).format(dateTimeFormatter)
    }.getOrElse { value }
}

fun formatDate(value: String): String {
    return runCatching {
        OffsetDateTime.parse(value).format(dateFormatter)
    }.getOrElse { value }
}
