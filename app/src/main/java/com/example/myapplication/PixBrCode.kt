package com.example.myapplication

private fun tlv(id: String, value: String): String {
    val length = value.length.toString().padStart(2, '0')
    return "$id$length$value"
}

fun buildPixBrCode(
    pixKey: String,
    merchantName: String = "AGEGO",
    merchantCity: String = "SAO PAULO",
    amount: String = "",
    txId: String = "***"
): String {
    val merchantAccountInfo = tlv("26", tlv("00", "BR.GOV.BCB.PIX") + tlv("01", pixKey))
    val merchantCategoryCode = tlv("52", "0000")
    val transactionCurrency = tlv("53", "986")
    val normalizedAmount = amount.replace(",", ".").toDoubleOrNull()?.takeIf { it > 0.0 }
    val transactionAmount = if (normalizedAmount != null) tlv("54", "%.2f".format(normalizedAmount)) else ""
    val countryCode = tlv("58", "BR")
    val name = tlv("59", merchantName.uppercase().take(25).ifBlank { "AGEGO" })
    val city = tlv("60", merchantCity.uppercase().take(15).ifBlank { "BRASIL" })
    val additionalDataField = tlv("62", tlv("05", txId.ifBlank { "***" }))

    val payload = buildString {
        append(tlv("00", "01"))
        append(tlv("01", "11"))
        append(merchantAccountInfo)
        append(merchantCategoryCode)
        append(transactionCurrency)
        append(transactionAmount)
        append(countryCode)
        append(name)
        append(city)
        append(additionalDataField)
        append("6304")
    }
    return payload + crc16Ccitt(payload)
}

private fun crc16Ccitt(data: String): String {
    var crc = 0xFFFF
    for (byte in data.toByteArray(Charsets.ISO_8859_1)) {
        crc = crc xor ((byte.toInt() and 0xFF) shl 8)
        repeat(8) {
            crc = if (crc and 0x8000 != 0) (crc shl 1) xor 0x1021 else crc shl 1
            crc = crc and 0xFFFF
        }
    }
    return crc.toString(16).uppercase().padStart(4, '0')
}
