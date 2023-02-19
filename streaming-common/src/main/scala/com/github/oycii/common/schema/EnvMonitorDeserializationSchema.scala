package com.github.oycii.common.schema

import com.github.oycii.common.dto.EnvMonitor
import com.github.oycii.common.utils.Serializer
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor.getForClass
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema
import org.apache.flink.util.Collector
import org.apache.kafka.clients.consumer.ConsumerRecord

class EnvMonitorDeserializationSchema extends KafkaRecordDeserializationSchema[EnvMonitor] {

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]], out: Collector[EnvMonitor]): Unit = {
    val envMonitor = Serializer.deserialize[EnvMonitor](record.value())
    out.collect(envMonitor)
  }

  override def getProducedType: TypeInformation[EnvMonitor] = getForClass(classOf[EnvMonitor])
}