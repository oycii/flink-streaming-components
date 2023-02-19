package com.github.oycii.pipeline

import com.github.oycii.avro.validator.service.StreamingAvroValidatorService
import com.github.oycii.common.mapper.NextMapper.Next
import com.github.oycii.connector.kafka.service.StreamingKafkaConnectorService
import com.github.oycii.env.monitoring.service.StreamingEnvMonitoringService
import com.github.oycii.json.validator.service.StreamingJsonValidatorService
import com.github.oycii.pipeline.config.PipelineConfigParser
import com.typesafe.scalalogging.LazyLogging
import commons.mapper.Mappers
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object StreamingPipeline extends App with LazyLogging  {

  logger.info("Start app " + this.getClass.getName)
  try {
    logger.info(s"args: " + args.toSet)
    val pipelineConfig = PipelineConfigParser.getPipelineConfig(args)
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(pipelineConfig.parallelism)
    env.enableCheckpointing(pipelineConfig.checkpointingTimeout)

    val config = Mappers.beanToMap(pipelineConfig)
    val avroConfig =  Mappers.beanToMap(pipelineConfig.copy(kafkaConsumerTopics = pipelineConfig.kafkaConsumerTopicAvro))
    val connectorStream = StreamingKafkaConnectorService
    val jsonValidator = StreamingJsonValidatorService
    val avroValidator = StreamingAvroValidatorService
    val envMonitor = StreamingEnvMonitoringService

    val jsonInputStream = connectorStream.initDataStream(env, config)
    val avroInputStream = connectorStream.initDataStream(env, avroConfig)
    val dataEnvMonitorFromAvro = avroInputStream.next(data => avroValidator.process(data, config))

    jsonInputStream
      .next(data => jsonValidator.process(data, config).union(dataEnvMonitorFromAvro).rebalance())
      .next(data => envMonitor.process(data, config))
      .next(dataEnvMonitorAlarm => envMonitor.setSink(dataEnvMonitorAlarm, config))

    env.execute(pipelineConfig.appName)
  } catch {
    case e: Exception =>
      e.printStackTrace()
      logger.error("Error:" + e.getMessage)
      System.exit(1)
  }
}
