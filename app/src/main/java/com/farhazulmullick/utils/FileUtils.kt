package com.farhazulmullick.utils

import android.database.Cursor

fun Cursor.getStringValue(columnName: String): String {
    val columnIdx = getColumnIndex(columnName)
    return if (columnIdx >=0) getString(columnIdx).value
    else String.EMPTY
}

fun Cursor.getStringValue(columnIdx: Int): String =
    if (columnIdx >=0) getString(columnIdx).value else String.EMPTY


fun Cursor.getIntValue(columnName: String): Int {
    val columnIdx = getColumnIndex(columnName)
    return if (columnIdx >=0) getInt(columnIdx)
    else 0
}

fun Cursor.getIntValue(columnIdx: Int) =
    if (columnIdx >=0) this.getInt(columnIdx) else 0

fun Cursor.getLongValue(columnName: String): Long {
    val columnIdx = getColumnIndex(columnName)
    return if (columnIdx >=0) getLong(columnIdx)
    else 0L
}

fun Cursor.getLongValue(columnIdx: Int) =
    if (columnIdx >=0) this.getLong(columnIdx) else 0L

fun Cursor.getFloat(columnName: String): Float {
    val columnIdx = getColumnIndex(columnName)
    return if (columnIdx >=0) getFloat(columnIdx)
    else 0f
}

fun Cursor.getFloatValue(columnIdx: Int) =
    if (columnIdx >=0) this.getFloat(columnIdx) else 0f