package com.imaginea.kodebeagle

import akka.actor.{Props, Actor}
import akka.routing.RoundRobinPool
import org.eclipse.jgit.diff.DiffEntry

class Master(nrOfWorkers: Int, nrOfReposPerWorker: Int) extends Actor with Logger {
  var nrOfResults: Int = _
  val start: Long = System.currentTimeMillis
  val repoUpdater = context.actorOf(RoundRobinPool(nrOfWorkers).
    props(Props[Worker]), "repoUpdaterRouter")
  val mapOfAllPathVsDiffs = new scala.collection.mutable.HashMap[String, List[DiffEntry]]

  override def receive: Receive = {
    case Work(repoPaths) =>
      repoPaths.grouped(nrOfReposPerWorker).foreach(repoUpdater ! Work(_))

    case Result  =>
      nrOfResults += 1
      if (nrOfResults == nrOfWorkers) {
        log.info(
          s"""Time taken to update all repositories
              | ${(System.currentTimeMillis() - start) / 1000}""".stripMargin)
        context.system.terminate()
      }
  }
}