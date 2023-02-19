organization  := "oycii"
name := "streaming-env-monitoring"
version := "0.1"

lazy val scalaVersion = "2.12.12"
lazy val flinkVersion = "1.16.1"
lazy val kafkaVersion = "3.3.2"
val circeVersion = "0.14.3"
val json4sVersion = "3.6.6"
val scoptVersion = "4.1.0"

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "2.1.1")

libraryDependencies ++= Seq(
  "org.apache.flink" % "flink-streaming-scala_2.12" % flinkVersion,
  "org.apache.flink" % "flink-connector-kafka" % flinkVersion,
  "org.apache.flink" % "flink-clients" % flinkVersion,
  "org.apache.flink" % "flink-connector-files" % flinkVersion,

  "org.apache.flink" % "flink-runtime-web" % flinkVersion % Test,

  "com.github.tototoshi" %% "scala-csv" % "1.3.10",
  "org.apache.commons" % "commons-csv" % "1.9.0",
  "com.github.scopt" %% "scopt" % "4.1.0",
  "io.scalaland" %% "chimney" % "0.6.2",
  "ch.qos.logback" % "logback-classic" % "1.3.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  "com.typesafe" % "config" % "1.4.2",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.10.0",
  "com.sksamuel.avro4s" % "avro4s-core_2.12" % "4.1.0",
  "common4s" % "common4s_2.12" % "0.1",
  "oycii" % "streaming-common_2.12" % "0.1"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}