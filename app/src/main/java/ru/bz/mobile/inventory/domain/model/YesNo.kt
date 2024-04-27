package ru.bz.mobile.inventory.domain.model

enum class YesNo(val code: Long, val flag: Boolean) {
    YES(1, flag = true),
    NO(2, flag = false);

    companion object {
        fun getYesNo(code: Long): YesNo =
            YesNo.values().find { it.code == code } ?: NO
        fun getYesNo(flag: Boolean): YesNo =
            YesNo.values().find { it.flag == flag } ?: NO
    }
}