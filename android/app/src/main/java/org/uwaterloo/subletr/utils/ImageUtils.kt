package org.uwaterloo.subletr.utils

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream


/**
 * Decodes base64 string representation of image.
 *
 * @receiver Base64 string encoding image.
 * @return Bitmap of decoded image.
 */
fun String.base64ToBitmap(): Bitmap {
	val imageBytes = Base64.decode(this, Base64.DEFAULT)
	return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun Bitmap.toBase64String(): String {
	val byteArrayOutputStream = ByteArrayOutputStream()
	this.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
	val byteArray = byteArrayOutputStream.toByteArray()
	return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
