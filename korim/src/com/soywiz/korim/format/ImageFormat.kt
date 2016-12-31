package com.soywiz.korim.format

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korio.stream.MemorySyncStreamToByteArray
import com.soywiz.korio.stream.SyncStream
import com.soywiz.korio.stream.openSync
import com.soywiz.korio.stream.slice
import java.io.File

open class ImageFormat {
	open fun check(s: SyncStream): Boolean = TODO()
	open fun read(s: SyncStream): Bitmap = TODO()
	fun read(file: File) = this.read(file.openSync())
	fun read(s: ByteArray): Bitmap = read(s.openSync())
	open fun write(bitmap: Bitmap, s: SyncStream): Unit = TODO()

	fun decode(s: SyncStream) = this.read(s)
	fun decode(file: File) = this.read(file.openSync("r"))
	fun decode(s: ByteArray): Bitmap = read(s.openSync())

	fun encode(bitmap: Bitmap): ByteArray = MemorySyncStreamToByteArray { write(bitmap, this) }
}

object ImageFormats : ImageFormat() {
	private val formats = listOf(PNG, JPEG, BMP, TGA)

	override fun check(s: SyncStream): Boolean {
		for (format in formats) if (format.check(s.slice())) return true
		return false
	}

	override fun read(s: SyncStream): Bitmap {
		for (format in formats) {
			if (format.check(s.slice())) {
				return format.read(s.slice())
			}
		}
		throw UnsupportedOperationException("Not suitable image format")
	}
}
