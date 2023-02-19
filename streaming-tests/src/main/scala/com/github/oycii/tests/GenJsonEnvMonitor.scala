package com.github.oycii.tests

import io.circe.syntax._
import io.circe.generic.auto._
import com.github.oycii.tests.gen.Generator
import com.github.oycii.tests.kafka.producer.dao.KafkaProducerDao

import java.nio.charset.StandardCharsets

object GenJsonEnvMonitor extends App {
  val servers = "localhost:29092"
  val topic = "input-json"

  val generator: () => (Array[Byte], Array[Byte]) = ()  => {
    val envMonitor = Generator.getEnvMonitor
    val json = envMonitor.asJson
    (envMonitor.region.toString.getBytes(StandardCharsets.UTF_8), json.toString().getBytes(StandardCharsets.UTF_8))
  }

  KafkaProducerDao.send(servers, topic, 100000, generator)

}
