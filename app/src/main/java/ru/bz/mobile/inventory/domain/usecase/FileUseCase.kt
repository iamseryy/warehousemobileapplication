package ru.bz.mobile.inventory.domain.usecase


import ru.bz.mobile.inventory.data.local.file.FileRepository
import ru.bz.mobile.inventory.domain.model.IOP
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import javax.inject.Inject

private const val DELIMITER = ';'

class FileUseCase @Inject constructor(private val repository: FileRepository){
    fun importCsv(
        inputStream: InputStream,
        charset: Charset,
        delimiter: Char = DELIMITER
    ) = repository.importCsv(inputStream, charset, delimiter)

    fun exportCsv(
        outputStream: OutputStream,
        content: List<IOP.Dto>,
        charset: Charset = Charsets.UTF_8,
        delimiter: Char = ';'
    ) = repository.exportCsv(outputStream, content, charset, delimiter)
}