package com.imaginea.kodebeagle

import akka.actor.{ActorSystem, Props}

object Main {

  def main(args: Array[String]): Unit = {
    while (true) {
      val actorSystem = ActorSystem("Repository-Updater")
      val master = actorSystem.actorOf(Props(new Master(1, 1)), name = "MasterUpdater")
      master ! Work(List("/home/keerathj/Desktop/commons-io"))
    }
  }
}
