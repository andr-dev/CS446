package org.uwaterloo.subletr.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64

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
