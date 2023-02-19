package com.github.oycii.tests

import com.github.oycii.common.dto.EnvMonitor
import com.github.oycii.common.utils. Serializer
import com.github.oycii.tests.gen.Generator
import com.github.oycii.tests.kafka.producer.dao.KafkaProducerDao

import java.nio.charset.StandardCharsets

object GenCaseClassEnvMonitor extends App {
  val servers = "localhost:29092"
  val topic = "output-case-class"

  val generator: () => (Array[Byte], Array[Byte]) = ()  => {
    val envMonitor = Generator.getEnvMonitor
    val bytes = Serializer.serialize[EnvMonitor](envMonitor)
    val des = Serializer.deserialize[EnvMonitor](bytes)
    (envMonitor.region.toString.getBytes(StandardCharsets.UTF_8), bytes)
  }

  KafkaProducerDao.send(servers, topic, 1000, generator)

}
