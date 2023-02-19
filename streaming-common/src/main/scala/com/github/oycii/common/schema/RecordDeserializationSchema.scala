package com.github.oycii.common.schema

import com.github.oycii.common.dto.InputMsg
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor.getForClass
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema
import org.apache.flink.util.Collector
import org.apache.kafka.clients.consumer.ConsumerRecord

import java.nio.charset.StandardCharsets

class RecordDeserializationSchema extends KafkaRecordDeserializationSchema[InputMsg] {

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]], out: Collector[InputMsg]): Unit = {
    val key = if (record.key() != null) record.key() else "".getBytes
    val headers: Map[String, String] =
      if (record.headers() != null) {
        record.headers().toArray.map{ header => header.key()->new String(header.value(), StandardCharsets.UTF_8)}.toMap
      } else Map.empty[String, String]

    out.collect(InputMsg(key, record.value(), headers, record.partition()))
  }

  override def getProducedType: TypeInformation[InputMsg] = getForClass(classOf[InputMsg])
}