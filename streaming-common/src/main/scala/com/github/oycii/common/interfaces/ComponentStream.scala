package com.github.oycii.common.interfaces

import org.apache.flink.streaming.api.datastream.{DataStream, DataStreamSink}
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

trait ComponentStream[A,B,C] {
  def initDataStream(env: StreamExecutionEnvironment, config: collection.Map[String, Any]): DataStream[A]
  def process(messageInputStream: DataStream[A], config: collection.Map[String, Any]): DataStream[B]
  def setSink(messageInputStream: DataStream[B], config: collection.Map[String, Any]): DataStreamSink[C]
}
