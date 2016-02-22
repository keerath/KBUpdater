package com.imaginea.kodebeagle

import org.eclipse.jgit.diff.DiffEntry

trait DiffHelper extends Logger {
  def getDiffs(repoPath: String): Option[List[DiffEntry]]
}

