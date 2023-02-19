package com.github.oycii.common.schema

import com.github.oycii.common.dto.EnvMonitor
import com.github.oycii.common.utils.Serializer
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord

import java.lang
import java.nio.charset.StandardCharsets

class EnvMonitorSerializationSchema(topicName: String) extends KafkaRecordSerializationSchema[EnvMonitor] {

  override def serialize(envMonitor: EnvMonitor, context: KafkaRecordSerializationSchema.KafkaSinkContext,
                         timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    new ProducerRecord(topicName, envMonitor.region.toString.getBytes(StandardCharsets.UTF_8),
      Serializer.serialize[EnvMonitor](envMonitor))
  }

}
