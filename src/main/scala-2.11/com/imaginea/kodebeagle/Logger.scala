package com.imaginea.kodebeagle

/**
  * Created by keerathj on 18/2/16.
  */
import org.slf4j.LoggerFactory

trait Logger {
  val log = LoggerFactory.getLogger(this.getClass.getName)
}
