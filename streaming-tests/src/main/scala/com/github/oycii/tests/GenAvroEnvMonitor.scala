package com.github.oycii.tests

import com.github.oycii.common.dto.EnvMonitor
import com.github.oycii.tests.gen.Generator
import com.github.oycii.tests.kafka.producer.dao.KafkaProducerDao
import com.sksamuel.avro4s.AvroOutputStream

import java.io.ByteArrayOutputStream

object GenAvroEnvMonitor extends App {
  val servers = "localhost:29092"
  val topic = "input-avro"

  def serialize(envMonitor: EnvMonitor): (Array[Byte], Array[Byte]) = {
    var baos: ByteArrayOutputStream = null
    var output: AvroOutputStream[EnvMonitor] = null
    try {
      baos = new ByteArrayOutputStream()
      val avroOutputStream = AvroOutputStream.binary[EnvMonitor]
      output = avroOutputStream.to(baos).build()
      output.write(envMonitor)
    } finally {
      if (output != null) {
        output.close()
      }

      if (baos != null) {
        baos.close()
      }
    }

    (envMonitor.region.toString.getBytes(), baos.toByteArray)
  }

  val generator: () => (Array[Byte], Array[Byte]) = ()  => {
    val envMonitor = Generator.getEnvMonitor
    serialize(envMonitor)
  }

  KafkaProducerDao.send(servers, topic, 100000, generator)

}
