package com.example.coffeeshopapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getCurrentDateTime(): String {
    val calendar = Calendar.getInstance().time
    val formatter = SimpleDateFormat("'Ngày' dd 'tháng' MM, yyyy 'lúc' hh:mm a", Locale("vi", "VN"))
    return formatter.format(calendar).replace("AM", "SA").replace("PM", "CH")
}