package fr.ferfoui.nt2u.serial

fun resetLedsCommand() = "reset"

fun setLedStateCommand(ledId: Int, state: Int) = "set $ledId $state"

fun obtainLedStateCommand(ledId: Int) = "get $ledId"

