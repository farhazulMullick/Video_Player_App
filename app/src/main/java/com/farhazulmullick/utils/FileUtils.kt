package com.farhazulmullick.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

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
    if (columnIdx >= 0) this.getLong(columnIdx) else 0L

fun Cursor.getFloat(columnName: String): Float {
    val columnIdx = getColumnIndex(columnName)
    return if (columnIdx >= 0) getFloat(columnIdx)
    else 0f
}

fun Cursor.getFloatValue(columnIdx: Int) =
    if (columnIdx >= 0) this.getFloat(columnIdx) else 0f

/**
 * Check if a File is present based upon scheme [Content] [File].
 * @param context Context
 * @param filePath path of file
 * @return boolean value
 */
suspend fun Context.checkFileExist(filePath: String?): Boolean {
    return withContext(Dispatchers.IO) {
        val uri = Uri.parse(filePath)
        val isFileExist: Boolean =
            if (uri.scheme === "content") checkFileExist(this@checkFileExist, uri)
            else if (uri.scheme === "file") {
                val file = uri.toFile()
                file.exists() && file.length() > 0
            } else {
                val file = File(uri.path.toString())
                file.exists() && file.length() > 0
            }
        isFileExist
    }
}

/**
 * Check if content uri path of file valid by opening inputstream. Catching IOException since
 * FileNotFoundException is child of IOException.
 * @param context Context
 * @param mUri content scheme uri of file
 * @return boolean value
 */
suspend fun checkFileExist(context: Context, mUri: Uri?): Boolean {
    return withContext(Dispatchers.IO) {
        var isFileExist = false
        try {
            // FileNotFoundException is thrown if couldn't open inputStream.
            val inputStream = context.contentResolver.openInputStream(mUri!!)
            isFileExist = true
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        isFileExist
    }
}