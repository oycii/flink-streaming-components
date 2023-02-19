package com.github.oycii.common.dao

import com.github.oycii.common.config.AppConfig
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema

import java.util.Properties

object KafkaConsumerDao {

  def getKafkaSource[T](appConfig: AppConfig, recordDeserializationSchema: KafkaRecordDeserializationSchema[T]): KafkaSource[T] = {
    val props = new Properties
    appConfig.kafkaConsumerOptions.foreach { case (key, value) => props.setProperty(key, value.toString) }

    KafkaSource.builder[T]()
      .setBootstrapServers(appConfig.kafkaConsumerBootstrapServers)
      .setTopics(appConfig.kafkaConsumerTopics)
      .setGroupId(appConfig.kafkaConsumerGroupId)
      .setStartingOffsets(OffsetsInitializer.latest())
      .setDeserializer(recordDeserializationSchema)
      .setClientIdPrefix(appConfig.kafkaConsumerClientIdPrefix)
      .setProperties(props)
      .build()
  }

}
