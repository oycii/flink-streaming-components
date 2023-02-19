package com.github.oycii.env.monitoring.service

import com.github.oycii.common.config.AppConfig
import com.github.oycii.common.dao.{KafkaConsumerDao, KafkaProducerDao, MetricsMapDao}
import com.github.oycii.common.dto.EnvMonitor
import com.github.oycii.common.interfaces.ComponentStream
import com.github.oycii.common.schema.EnvMonitorDeserializationSchema
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.connector.base.DeliveryGuarantee
import org.apache.flink.streaming.api.datastream.{DataStream, DataStreamSink}
import org.apache.flink.streaming.api.{datastream, environment}
import com.github.oycii.env.monitoring.dto.EnvMonitorAlarm
import com.github.oycii.env.monitoring.schema.EnvMonitorAlarmSerializationSchema
import commons.mapper.Mappers
import org.apache.flink.api.java.functions.KeySelector


object StreamingEnvMonitoringService extends ComponentStream[EnvMonitor, EnvMonitorAlarm, EnvMonitorAlarm] {

  override def initDataStream(env: environment.StreamExecutionEnvironment, config: collection.Map[String, Any]):
    datastream.DataStream[EnvMonitor] =
  {
    val appConfig = Mappers.mapToBean[AppConfig](config)
    val schemaDeserialization = new EnvMonitorDeserializationSchema()
    val source = KafkaConsumerDao.getKafkaSource(appConfig, schemaDeserialization)
    env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
  }

  override def process(messageInputStream: DataStream[EnvMonitor], config: collection.Map[String, Any]): DataStream[EnvMonitorAlarm] = {
    val keySelector: KeySelector[EnvMonitor, Long] = (in: EnvMonitor) => in.region
    val alarmMapService = new AlarmMapService

    messageInputStream.keyBy(keySelector)
      .map(alarmMapService)
  }

  override def setSink(messageInputStream: DataStream[EnvMonitorAlarm], config: collection.Map[String, Any]): DataStreamSink[EnvMonitorAlarm] = {
    val appConfig = Mappers.mapToBean[AppConfig](config)
    val deliveryGuarantee: DeliveryGuarantee = DeliveryGuarantee.EXACTLY_ONCE
    val schema = new EnvMonitorAlarmSerializationSchema(appConfig.kafkaProducerTopic)
    val sink = KafkaProducerDao.getKafkaSink[EnvMonitorAlarm](appConfig, schema, deliveryGuarantee)

    messageInputStream
      .map(new MetricsMapDao[EnvMonitorAlarm](appConfig.appName))
      .sinkTo(sink)
    }

}
