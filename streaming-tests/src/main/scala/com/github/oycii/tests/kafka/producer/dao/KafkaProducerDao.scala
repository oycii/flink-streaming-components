package com.github.oycii.tests.kafka.producer.dao

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.ByteArraySerializer
import java.util.Properties

object KafkaProducerDao {

  def send(servers: String, topic: String, count: Long, generator: () => (Array[Byte], Array[Byte])): Unit = {
    val props = new Properties()
    props.put("bootstrap.servers", servers)
    val producer: KafkaProducer[Array[Byte], Array[Byte]] = new KafkaProducer(props, new ByteArraySerializer, new ByteArraySerializer)
    try {
      for (n: Long <- 1L to count) {
        val (key, value) = generator.apply()
        println("№ " + n + ", key: " + key + ", json: " + value)
        val rec = new ProducerRecord(topic, key, value)
        producer.send(rec)
      }
      producer.flush()
    } finally {
      producer.close()
    }
  }

}
