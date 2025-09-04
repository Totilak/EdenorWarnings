package ru.edenor.edenorWarnings.data

data class Warning(
  val name: String,
  val description: String?,
  val title: String?,
  val body: String,
  val permission: String?
)


/*
*
* /command Totilak kissa
* Привет кисса, тебя беспокоит ${sender}!
* Templates:
*   Kissa:
*     title:
*     sub-tittle:
*     body:
*     permission:
*
*
*
* */