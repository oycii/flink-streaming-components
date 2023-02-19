package com.github.oycii.env.monitoring.schema

import com.github.oycii.env.monitoring.dto.EnvMonitorAlarm
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord

import java.lang
import java.nio.charset.StandardCharsets

import io.circe.syntax._
import io.circe.generic.auto._

class EnvMonitorAlarmSerializationSchema(topicName: String) extends KafkaRecordSerializationSchema[EnvMonitorAlarm] {

  override def serialize(envMonitorAlarm: EnvMonitorAlarm, context: KafkaRecordSerializationSchema.KafkaSinkContext,
                         timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    val json = envMonitorAlarm.asJson
    new ProducerRecord(topicName, envMonitorAlarm.region.toString.getBytes(StandardCharsets.UTF_8),
      json.toString().getBytes(StandardCharsets.UTF_8))
  }

}