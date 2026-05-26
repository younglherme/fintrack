package dev.guilhermesilva.fintrack.application.common

import java.math.BigDecimal
import java.time.LocalDate

fun String.normalizedText(): String =
    trim().replace(Regex("\\s+"), " ")

fun String.isValidTextLength(max: Int): Boolean =
    normalizedText().length <= max

fun BigDecimal.isPositiveAmount(): Boolean =
    this > BigDecimal.ZERO

fun LocalDate.isNotInFarFuture(): Boolean =
    !isAfter(LocalDate.now().plusYears(1))