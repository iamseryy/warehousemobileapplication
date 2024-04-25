package ru.bz.mobile.inventory.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import ru.bz.mobile.inventory.util.GsonSerializer
import ru.bz.mobile.inventory.util.YesNo

data class IOP(
    var cwar: String,
    var loca: String,
    var item: String,
    var clot: String,
    var qstr: Double,
    var qnty: Double,
    var utcDate: Long = 0,
    var unit: String = "",
    var stkr: YesNo = YesNo.NO,
    var lock: YesNo = YesNo.NO,
    var illiquid: YesNo = YesNo.NO,
    var sloca: String = ""

) {
    companion object {
        fun getEmpty(): IOP = IOP(
            cwar = "",
            loca = "",
            item = "",
            clot = "",
            qstr = 0.0,
            qnty = 0.0,
            utcDate = 0,
            unit = "",
            stkr = YesNo.NO,
            lock = YesNo.NO,
            illiquid = YesNo.NO,
        )
    }
    val isEmpty:Boolean
        get() = cwar.isEmpty() and loca.isEmpty() and item.isEmpty() and clot.isEmpty()

    fun toDTO(): Dto {
        return Dto(
            orno = "",
            pono = 0,
            porn = "",

            unit = unit,
            cwar = cwar,
            loca = loca,
            item = item,
            clot = clot,
            qstr = qstr,
            qnty = qnty,
            utcDate = utcDate,
            stkr = stkr.code,
            lock = lock.code,
            illiquid = illiquid.code,
            sloca = sloca,
        )
    }

    fun clear() {
        cwar = ""
        loca = ""
        item = ""
        clot = ""
        qstr = 0.0
        qnty = 0.0
        utcDate = 0
        stkr = YesNo.NO
        unit = ""
        lock = YesNo.NO
        illiquid = YesNo.NO
        sloca = ""
    }
    fun copy(iop: IOP):IOP {
        return this.apply {
            cwar = iop.cwar
            loca = iop.loca
            item = iop.item
            clot = iop.clot
            qstr = iop.qstr
            qnty = iop.qnty
            utcDate = iop.utcDate
            stkr = iop.stkr
            unit = iop.unit
            lock = iop.lock
            illiquid = iop.illiquid
            sloca = iop.sloca
        }
    }

    //dto
    @Entity(
        tableName = "whinh501",
        primaryKeys = ["cwar", "loca", "clot", "item"],
        indices = [Index(value = ["cwar", "loca", "clot", "item"], name = "clci")]
    )
    data class Dto(
        @ColumnInfo(name = "cwar") var cwar: String,
        @ColumnInfo(name = "loca") var loca: String,
        @ColumnInfo(name = "item") var item: String,
        @ColumnInfo(name = "clot") var clot: String,

        @ColumnInfo(name = "orno") var orno: String,
        @ColumnInfo(name = "pono") var pono: Long,
        @ColumnInfo(name = "qstr") var qstr: Double,
        @ColumnInfo(name = "qnty") var qnty: Double = 0.0,
        @ColumnInfo(name = "utcDate") var utcDate: Long = 0,
        @ColumnInfo(name = "stkr") var stkr: Long = 2,
        @ColumnInfo(name = "unit") var unit: String = "",
        @ColumnInfo(name = "lock") var lock: Long = 2,
        @ColumnInfo(name = "illiquid") var illiquid: Long = 2,
        @ColumnInfo(name = "porn") var porn: String = "",
        @ColumnInfo(name = "sloca") var sloca: String = ""
    ) {
        fun toIOP(): IOP {
            return IOP(
                cwar = cwar,
                loca = loca,
                item = item,
                clot = clot,
                qstr = qstr,
                qnty = qnty,
                sloca = sloca,
                illiquid = YesNo.getYesNo(illiquid),
                lock = YesNo.getYesNo(lock),
                stkr = YesNo.getYesNo(stkr)
            )
        }
        fun toString(delimiter: Char): String {
            return (
                    "$orno${delimiter}" +
                    "$pono${delimiter}" +
                    "$cwar${delimiter}" +
                    "$loca${delimiter}" +
                    "$clot${delimiter}" +
                    "$item${delimiter}" +
                    "$qnty${delimiter}" +
                    "$utcDate${delimiter}" +
                    "$stkr${delimiter}" +
                    "$lock${delimiter}" +
                    "$illiquid${delimiter}" +
                    "$sloca" +
                    "\n"
                    )
        }
        fun copy(dto: Dto):Dto {
            return this.apply {
                cwar = dto.cwar
                loca = dto.loca
                item = dto.item
                clot = dto.clot
                orno = dto.orno
                pono = dto.pono
                qstr = dto.qstr
                qnty = dto.qnty
                utcDate = dto.utcDate
                stkr = dto.stkr
                unit = dto.unit
                lock = dto.lock
                illiquid = dto.illiquid
                porn = dto.porn
                sloca = dto.sloca
            }
        }
        fun copyPartial(dto: Dto?):Dto {
            if (dto == null)
                return this
            return this.apply {
                orno = dto.orno
                pono = dto.pono
                qstr = dto.qstr
                qnty = dto.qnty
                utcDate = dto.utcDate
                unit = dto.unit
                porn = dto.porn
                sloca = dto.sloca
            }
        }
    }
    fun serialized() :String = GsonSerializer.serializeObject(this)
}