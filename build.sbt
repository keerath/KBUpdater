name := "KBUpdater"
version := "1.0"
scalaVersion := "2.11.6"

ivyScala := ivyScala.value map {
  _.copy(overrideScalaVersion = true)
}
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.2" //akka
libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit" % "4.2.0.201601211800-r" //jgit
libraryDependencies +=
  "org.apache.kafka" % "kafka_2.11" % "0.8.2.0" excludeAll(
    ExclusionRule(organization = "com.sun.jmx", artifact = "jmxri"),
    ExclusionRule(organization = "com.sun.jmx", artifact = "jms"),
    ExclusionRule(organization = "com.sun.jdmk", artifact = "jmxtools")
    )



    