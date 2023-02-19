package com.github.oycii.pipeline.config

case class PipelineConfig(
                           appName: String = null,
                           parallelism: Int = 1,
                           checkpointingTimeout: Long = 1000,
                           kafkaConsumerBootstrapServers: String = null,
                           kafkaConsumerTopics: String = null,
                           kafkaConsumerTopicAvro: String = null,
                           kafkaConsumerGroupId: String = null,
                           kafkaConsumerClientIdPrefix: String = null,
                           kafkaProducerBootstrapServers: String = null,
                           kafkaProducerTopic: String = null,
                           kafkaProducerTransactionalIdPrefix: String = null,
                           kafkaConsumerOptions: Map[String, String] = Map(),
                           kafkaProducerOptions: Map[String, String] = Map()
                         )

object PipelineConfigParser {

  def getPipelineConfig(args: Array[String]): PipelineConfig = {
    val argsParser = PipelineConfigParser.parser()
    val appConfig = argsParser.parse(args, PipelineConfig())
    appConfig match {
      case Some(value) => value
      case None =>
        throw new Exception("Error arguments")
    }
  }

  def parser(): scopt.OptionParser[PipelineConfig] = {
    new scopt.OptionParser[PipelineConfig]("streaming-pipeline") {

      opt[String]("appName")
        .valueName("<String>")
        .action((x, c) => c.copy(appName = x: String))
        .text("appName - Application name")

      opt[Int]("parallelism")
        .valueName("<Int>")
        .action((x, c) => c.copy(parallelism = x: Int))
        .text("parallelism - Flink job parallelism")

      opt[Long]("checkpointingTimeout")
        .valueName("<Int>")
        .action((x, c) => c.copy(checkpointingTimeout = x: Long))
        .text("parallelism - Flink job checkpointing timeout between save")

      opt[String]("kafkaConsumerBootstrapServers")
        .valueName("<String>")
        .action((x, c) => c.copy(kafkaConsumerBootstrapServers = x: String))
        .text("kafkaConsumerBootstrapServers - List of kafka servers of consumer with ports, example: example1:9092,example2:9092")

      opt[String]("kafkaConsumerTopics")
        .valueName("<String>")
        .action((x, c) => c.copy(kafkaConsumerTopics = x: String))
        .text("kafkaConsumerTopics - Consumer list of topics, example: test1,test2,test3")

      opt[String]("kafkaConsumerTopicAvro")
        .valueName("<String>")
        .action((x, c) => c.copy(kafkaConsumerTopicAvro = x: String))
        .text("kafkaConsumerTopicAvro - kafka Consumer Topic Avro")

      opt[String]("kafkaConsumerGroupId")
        .valueName("<String>")
        .action((x, c) => c.copy(kafkaConsumerGroupId = x: String))
        .text("kafkaConsumerGroupId - Kafka consumer group id")

      opt[String]("kafkaConsumerClientIdPrefix")
        .valueName("<String>")
        .action((x, c) => c.copy(kafkaConsumerClientIdPrefix = x: String))
        .text("kafkaClientIdPrefix - Kafka consumer client id prefix")

      opt[String]("kafkaProducerBootstrapServers")
        .valueName("<String>")
        .action((x, c) => c.copy(kafkaProducerBootstrapServers = x: String))
        .text("kafkaProducerBootstrapServers - List of kafka servers of provider with ports, example: example1:9092,example2:9092")

      opt[String]("kafkaProducerTopic")
        .valueName("<String>")
        .action((x, c) => c.copy(kafkaProducerTopic = x: String))
        .text("kafkaProducerTopic - Producer topic")

      opt[String]("kafkaProducerTransactionalIdPrefix")
        .valueName("<String>")
        .action((x, c) => c.copy(kafkaProducerTransactionalIdPrefix = x: String))
        .text("kafkaProducerTransactionalIdPrefix - Producer TransactionalIdPrefix")

      opt[Map[String, String]]("kafkaConsumerOptions")
        .valueName("<Map[String, String]>")
        .action((x, c) => c.copy(kafkaConsumerOptions = x: Map[String, String]))
        .text("kafkaConsumerOptions - kafka Consumer Native Options: k1=v1,k2=v2...")

      opt[Map[String, String]]("kafkaProducerOptions")
        .valueName("<Map[String, String]>")
        .action((x, c) => c.copy(kafkaProducerOptions = x: Map[String, String]))
        .text("kafkaProducerOptions - kafka Producer Native Options: k1=v1,k2=v2...")

    }
  }
}
