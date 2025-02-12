package fr.ferfoui.nt2u.serial

fun resetLedsCommand() = "reset"

fun setLedStateCommand(ledId: Int, state: Boolean) = "set $ledId ${if (state) 1 else 0}"

fun obtainLedStateCommand(ledId: Int) = "get $ledId"

