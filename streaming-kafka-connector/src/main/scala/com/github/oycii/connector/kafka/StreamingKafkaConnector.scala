package com.github.oycii.connector.kafka

import com.github.oycii.common.config.AppConfigParser.getAppConfig
import com.github.oycii.common.config.{AppConfig, AppConfigParser}
import com.github.oycii.connector.kafka.service.StreamingKafkaConnectorService
import com.typesafe.scalalogging.LazyLogging
import commons.mapper.Mappers
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object StreamingKafkaConnector extends LazyLogging {

  def main(args: Array[String]): Unit = {
    logger.info("Start app " + this.getClass.getName)
    try {
      logger.info(s"args: " + args.toSet)
      val appConfig = getAppConfig(args)
      val env = StreamExecutionEnvironment.getExecutionEnvironment
      env.setParallelism(appConfig.parallelism)
      env.enableCheckpointing(appConfig.checkpointingTimeout)

      val config: collection.Map[String, Any] = Mappers.beanToMap(appConfig)
      val componentStream = StreamingKafkaConnectorService
      val messageInputStream = componentStream.initDataStream(env, config)
      componentStream.setSink(messageInputStream, config)

      env.execute(appConfig.appName)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        logger.error("Error:" + e.getMessage)
        System.exit(1)
    }
  }
}

