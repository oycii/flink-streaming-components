package com.github.oycii.common.dao

import com.github.oycii.common.config.AppConfig
import org.apache.flink.connector.base.DeliveryGuarantee
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}

import java.util.Properties

object KafkaProducerDao {
  def getKafkaSink[T](appConfig: AppConfig, recordSerializationSchema: KafkaRecordSerializationSchema[T],
                      deliveryGuarantee: DeliveryGuarantee): KafkaSink[T] = {
    val props = new Properties
    appConfig.kafkaProducerOptions.foreach { case (key, value) => props.setProperty(key, value.toString) }

    KafkaSink.builder[T]()
      .setBootstrapServers(appConfig.kafkaProducerBootstrapServers)
      .setRecordSerializer(recordSerializationSchema)
      .setDeliveryGuarantee(deliveryGuarantee)
      .setTransactionalIdPrefix(appConfig.kafkaProducerTransactionalIdPrefix)
      .setKafkaProducerConfig(props)
      .build()

  }
}
