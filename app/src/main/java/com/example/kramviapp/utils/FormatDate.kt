package com.example.kramviapp.utils

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter

fun formatDate(dateString: String): String {
    val instant: Instant = Instant.parse(dateString)
    val systemZone: ZoneId = ZoneId.systemDefault() // my timezone
    val currentOffsetForMyZone: ZoneOffset = systemZone.rules.getOffset(instant)
    val ldtPST: LocalDateTime = LocalDateTime.ofInstant(instant, currentOffsetForMyZone)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return ldtPST.format(formatter)
}
fun formatTime(dateString: String): String {
    val instant: Instant = Instant.parse(dateString)
    val systemZone: ZoneId = ZoneId.systemDefault() // my timezone
    val currentOffsetForMyZone: ZoneOffset = systemZone.rules.getOffset(instant)
    val ldtPST: LocalDateTime = LocalDateTime.ofInstant(instant, currentOffsetForMyZone)
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
    return ldtPST.format(formatter)
}