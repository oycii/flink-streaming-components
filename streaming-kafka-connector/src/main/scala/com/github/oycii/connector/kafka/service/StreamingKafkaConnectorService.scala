package com.github.oycii.connector.kafka.service

import com.github.oycii.common.config.AppConfig
import com.github.oycii.common.dao.{KafkaConsumerDao, KafkaProducerDao, MetricsMapDao}
import com.github.oycii.common.dto.InputMsg
import com.github.oycii.common.interfaces.ComponentStream
import com.github.oycii.common.schema.{RecordDeserializationSchema, RecordSerializationSchema}
import commons.mapper.Mappers
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.connector.base.DeliveryGuarantee
import org.apache.flink.connector.kafka.sink.KafkaSink
import org.apache.flink.streaming.api.{datastream, environment}
import org.apache.flink.streaming.api.datastream.DataStreamSink

object StreamingKafkaConnectorService extends ComponentStream[InputMsg, InputMsg, InputMsg] {

  override def initDataStream(env: environment.StreamExecutionEnvironment, config: collection.Map[String, Any]):
    datastream.DataStream[InputMsg] =
  {
    val appConfig = Mappers.mapToBean[AppConfig](config)
    val schemaDeserialization = new RecordDeserializationSchema()
    val source = KafkaConsumerDao.getKafkaSource(appConfig, schemaDeserialization)
    env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
  }

  override def process(messageInputStream: datastream.DataStream[InputMsg], config: collection.Map[String, Any]):
    datastream.DataStream[InputMsg] =  messageInputStream

  override def setSink(messageInputStream: datastream.DataStream[InputMsg], config: collection.Map[String, Any]):
    DataStreamSink[InputMsg] =
  {
    val appConfig = Mappers.mapToBean[AppConfig](config)
    val deliveryGuarantee: DeliveryGuarantee = DeliveryGuarantee.EXACTLY_ONCE
    val sink: KafkaSink[InputMsg] = KafkaProducerDao.getKafkaSink[InputMsg](appConfig,
      new RecordSerializationSchema(appConfig.kafkaProducerTopic), deliveryGuarantee)

    messageInputStream
      .map(new MetricsMapDao[InputMsg](appConfig.appName))
      .sinkTo(sink)
  }

}
