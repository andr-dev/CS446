package org.uwaterloo.subletr.services

import android.content.Context
import java.io.File
import java.nio.file.Files

class IoService(private val context: Context): IIoService {
	override fun internalFileExists(fileName: String): Boolean {
		val targetFile = File(context.filesDir, fileName)
		return targetFile.exists()
	}

	override fun writeStringToInternalFile(fileName: String, input: String) {
		val targetFile = File(context.filesDir, fileName)
		targetFile.bufferedWriter().use {
			it.write(input)
		}
	}

	override fun readStringFromInternalFile(fileName: String): String {
		var outputString: String
		val targetFile = File(context.filesDir, fileName)
		targetFile.bufferedReader().use {
			outputString = targetFile.readLines().joinToString("\n")
		}
		return outputString
	}

	override fun deleteInternalFile(fileName: String) {
		val targetFile = File(context.filesDir, fileName)
		Files.deleteIfExists(targetFile.toPath())
	}
}
