package org.uwaterloo.subletr.services

interface IIoService {
	fun readStringFromInternalFile(fileName: String): String
	fun writeStringToInternalFile(fileName: String, input: String)
	fun internalFileExists(fileName: String): Boolean
	fun deleteInternalFile(fileName: String)
}
