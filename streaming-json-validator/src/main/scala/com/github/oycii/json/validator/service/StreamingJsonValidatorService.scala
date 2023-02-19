package com.github.oycii.json.validator.service

import com.github.oycii.common.config.AppConfig
import com.github.oycii.common.dao.{KafkaConsumerDao, KafkaProducerDao, MetricsMapDao}
import com.github.oycii.common.dto.{EnvMonitor, InputMsg}
import com.github.oycii.common.interfaces.ComponentStream
import com.github.oycii.common.schema.RecordDeserializationSchema
import commons.mapper.Mappers
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.connector.base.DeliveryGuarantee
import org.apache.flink.connector.kafka.sink.KafkaSink
import org.apache.flink.streaming.api.datastream.{DataStream, DataStreamSink}
import org.apache.flink.streaming.api.{datastream, environment}
import com.github.oycii.common.schema.EnvMonitorSerializationSchema
import io.circe
import io.circe.generic.auto._
import io.circe.parser.decode

import java.nio.charset.StandardCharsets

object StreamingJsonValidatorService extends ComponentStream[InputMsg, EnvMonitor, EnvMonitor] {

  override def initDataStream(env: environment.StreamExecutionEnvironment, config: collection.Map[String, Any]):
    datastream.DataStream[InputMsg] =
  {
    val appConfig = Mappers.mapToBean[AppConfig](config)
    val schemaDeserialization = new RecordDeserializationSchema()
    val source = KafkaConsumerDao.getKafkaSource(appConfig, schemaDeserialization)
    env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
  }

  override def process(messageInputStream: DataStream[InputMsg], config: collection.Map[String, Any]): DataStream[EnvMonitor] = {
    messageInputStream.map(inputMsg => {
       val str = new String(inputMsg.message, StandardCharsets.UTF_8)
       decode[EnvMonitor](str) match {
        case Left(error) => throw new Exception(error)
        case Right(value) =>
          println(value)
          value
      }
    })
  }

  override def setSink(messageInputStream: DataStream[EnvMonitor], config: collection.Map[String, Any]): DataStreamSink[EnvMonitor] =
  {
    val appConfig = Mappers.mapToBean[AppConfig](config)
    val deliveryGuarantee: DeliveryGuarantee = DeliveryGuarantee.EXACTLY_ONCE
    val sink: KafkaSink[EnvMonitor] = KafkaProducerDao.getKafkaSink[EnvMonitor](appConfig,
      new EnvMonitorSerializationSchema(appConfig.kafkaProducerTopic), deliveryGuarantee)

    messageInputStream
      .map(new MetricsMapDao[EnvMonitor](appConfig.appName))
      .sinkTo(sink)
    }

}
