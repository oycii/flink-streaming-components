package com.github.oycii.avro.validator.service

import com.github.oycii.avro.validator.convert.AvroConverter
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

object StreamingAvroValidatorService extends ComponentStream[InputMsg, EnvMonitor, EnvMonitor] {

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
      AvroConverter.dec(inputMsg.message)
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
