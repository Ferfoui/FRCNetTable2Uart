package fr.ferfoui.nt2u.serial

fun resetLedsCommand() = newLine("reset")

fun setLedStateCommand(ledId: Int, state: Boolean) = newLine("set $ledId ${if (state) 1 else 0}")

fun obtainLedStateCommand(ledId: Int) = newLine("get $ledId")

private fun newLine(line: String) = "$line\n"
