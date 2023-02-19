package com.github.oycii.common.dao

import org.apache.flink.api.common.serialization.SimpleStringEncoder
import org.apache.flink.connector.file.sink.FileSink
import org.apache.flink.core.fs.Path

object FileProducerDao {

  def getSink(path: String): FileSink[String] = {
    FileSink.forRowFormat[String](new Path(path), new SimpleStringEncoder[String]()).build()
  }

}
