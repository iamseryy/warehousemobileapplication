package ru.bz.mobile.inventory.model.main

import ru.bz.mobile.inventory.data.room.MainRepository

fun interface ValidatorPredicate {
    fun validate(text: String): Boolean
}

class Validator(val repo: MainRepository) {
    val itemIsValid: (String) -> Boolean = { item ->
        ValidatorPredicate { item -> item.length in listOf(9, 11, 15) }.validate(item)
    }
    val clotIsValid: (String) -> Boolean = { clot ->
        ValidatorPredicate { clot -> clot.length == 3 || clot.contains('-') }.validate(clot)
    }

    var cwarIsCwar: (String) -> Boolean = { cwar ->
        ValidatorPredicate { cwar ->
            repo.findCwarSync(cwar.uppercase())
        }.validate(cwar)
    }

    var locaIsLoca: (String, String) -> Boolean = { cwar, loca ->
        ValidatorPredicate { loca ->
            repo.findLocaByCwarSync(cwar = cwar, loca = loca.uppercase())
        }.validate(loca)
    }
}

