package com.imaginea.kodebeagle

import akka.actor.Actor
import kafka.producer.KeyedMessage
import org.eclipse.jgit.diff.DiffEntry

class Worker extends Actor with Logger with KafkaUtils {

  override def receive: Receive = {
    case Work(repoPaths) =>
      doWork(repoPaths)
      sender() ! Result
  }

  private def doWork(repoPaths: List[String]): Unit = {
    val diffHelper = DiffHelperFactory.getInstance
    val producer = getKafkaProducer
    for (repoPath <- repoPaths; mayBeDiffs = diffHelper.getDiffs(repoPath)) {
      if (mayBeDiffs.isEmpty) {
        log.error(s"$repoPath was not updated due to an error!")
      } else {
        log.info(s"$repoPath was updated successfully!")
        producer.send(new KeyedMessage[String, List[DiffEntry]]("diff", repoPath, mayBeDiffs.get))
      }
    }
    producer.close
  }
}
