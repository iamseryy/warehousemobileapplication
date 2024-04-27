package ru.bz.mobile.inventory.domain.model.main

import ru.bz.mobile.inventory.domain.usecase.MainUseCase

fun interface ValidatorPredicate {
    fun validate(text: String): Boolean
}

class Validator(private val useCase: MainUseCase) {
    val itemIsValid: (String) -> Boolean = { item ->
        ValidatorPredicate { item -> item.length in listOf(9, 11, 15) }.validate(item)
    }
    val clotIsValid: (String) -> Boolean = { clot ->
        ValidatorPredicate { clot -> clot.length == 3 || clot.contains('-') }.validate(clot)
    }

    var cwarIsCwar: (String) -> Boolean = { cwar ->
        ValidatorPredicate { cwar ->
            useCase.findCwarSync(cwar.uppercase())
        }.validate(cwar)
    }

    var locaIsLoca: (String, String) -> Boolean = { cwar, loca ->
        ValidatorPredicate { loca ->
            useCase.findLocaByCwarSync(cwar = cwar, loca = loca.uppercase())
        }.validate(loca)
    }
}

