package com.github.oycii.avro.validator

import com.github.oycii.avro.validator.service.StreamingAvroValidatorService
import com.github.oycii.common.config.{AppConfig, AppConfigParser}
import com.typesafe.scalalogging.LazyLogging
import commons.mapper.Mappers
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

object StreamingAvroValidator extends LazyLogging {

  def getAppConfig(args: Array[String]): AppConfig = {
    val argsParser = AppConfigParser.parser()
    val appConfig = argsParser.parse(args, AppConfig())
    appConfig match {
      case Some(value) => value
      case None =>
        throw new Exception("Error arguments")
    }
  }

  def main(args: Array[String]): Unit = {
    logger.info("Start app " + this.getClass.getName)
    try {
      logger.info(s"args: " + args.toSet)
      val appConfig = getAppConfig(args)
      val env = StreamExecutionEnvironment.getExecutionEnvironment
      env.setParallelism(appConfig.parallelism)
      env.enableCheckpointing(appConfig.checkpointingTimeout)

      val config: collection.Map[String, Any] = Mappers.beanToMap(appConfig)
      val componentStream = StreamingAvroValidatorService
      val messageInputStream = componentStream.initDataStream(env, config)
      val processStream = componentStream.process(messageInputStream, config)
      componentStream.setSink(processStream, config)

      env.execute(appConfig.appName)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        logger.error("Error:" + e.getMessage)
        System.exit(1)
    }
  }
}

