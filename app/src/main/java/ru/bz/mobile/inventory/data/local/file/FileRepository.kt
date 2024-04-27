package ru.bz.mobile.inventory.data.local.file

import ru.bz.mobile.inventory.domain.model.IOP
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.nio.charset.Charset


private const val FRACTION_SEPARATOR_OLD = ','
private const val FRACTION_SEPARATOR_NEW = '.'

class FileRepository {

    fun importCsv(
        inputStream: InputStream, charset: Charset = Charsets.UTF_8, delimiter: Char
    ): List<IOP.Dto> {
        return InputStreamReader(inputStream, charset).use { stream ->
            stream.readLines().asSequence().drop(1).filter { line -> line.isNotEmpty() }
                .map { it.split(delimiter) }.map { list ->
                    IOP.Dto(
                        orno = list[0],
                        pono = list[1].trim().toLong(),
                        cwar = list[2],
                        loca = list[3],
                        clot = list[4],
                        item = list[5],
                        qstr = list[6].trim().replace(FRACTION_SEPARATOR_OLD, FRACTION_SEPARATOR_NEW).toDouble(),
                        unit = list[7],
                        porn = list[8],
                    )
                }.toList()
        }
    }

    fun exportCsv(
        outputStream: OutputStream,
        content: List<IOP.Dto>,
        charset: Charset = Charsets.UTF_8,
        delimiter: Char = ';'
    ) {
        outputStream.bufferedWriter().use { out ->
            out.write("orno${delimiter}" +
                    "pono${delimiter}" +
                    "cwar${delimiter}" +
                    "loca${delimiter}" +
                    "clot${delimiter}" +
                    "item${delimiter}" +
                    "qnty${delimiter}" +
                    "date${delimiter}" +
                    "stkr${delimiter}" +
                    "lock${delimiter}" +
                    "ilqd${delimiter}" +
                    "slca\n")
            content.forEach { line -> out.write(line.toString(delimiter)) }
        }
    }
}