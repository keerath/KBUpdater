package com.imaginea.kodebeagle

object DiffHelperFactory {
  def getInstance: DiffHelper = {
    new GitDiffHelper()
    // this return value will be based on the configuration in application.properties
  }
}
