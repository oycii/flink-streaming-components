package com.github.oycii.common.schema

import com.github.oycii.common.dto.InputMsg
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader

import java.lang
import java.nio.charset.StandardCharsets
import scala.jdk.CollectionConverters.seqAsJavaListConverter


class RecordSerializationSchema(topicName: String) extends KafkaRecordSerializationSchema[InputMsg] {
  override def serialize(i: InputMsg, context: KafkaRecordSerializationSchema.KafkaSinkContext,
                         timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    val headers: List[Header] =  i.meta.map(h => new RecordHeader(new String(h._1), h._2.getBytes(StandardCharsets.UTF_8))).toList
    new ProducerRecord(topicName, i.partition, i.key,  i.message, headers.asJava)
  }
}
