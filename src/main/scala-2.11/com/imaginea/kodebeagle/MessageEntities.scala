package com.imaginea.kodebeagle

sealed trait Message

case class Work(repoPaths: List[String]) extends Message

case object Result extends Message
