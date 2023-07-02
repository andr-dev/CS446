package org.uwaterloo.subletr.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import org.uwaterloo.subletr.R
import java.io.File

class ComposeFileProvider: FileProvider(R.xml.filepaths) {
	companion object {
		fun getImageUri(context: Context): Uri {
			val directory = File(context.cacheDir, "images")
			directory.mkdirs()

			val file = File.createTempFile(
				"selected_image_",
				".jpg",
				directory
			)

			return getUriForFile(
				context,
				context.packageName + ".fileprovider",
				file,
			)
		}
	}
}
