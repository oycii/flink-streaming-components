package com.github.oycii.avro.validator.convert

import com.github.oycii.common.dto.EnvMonitor
import com.sksamuel.avro4s.{AvroOutputStream, Decoder, Encoder}
import com.typesafe.scalalogging.LazyLogging
import org.apache.avro.generic.{GenericDatumReader, GenericDatumWriter, GenericRecord}
import org.apache.avro.io.DecoderFactory
import org.apache.avro.util.ByteBufferInputStream

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.Collections

object AvroConverter extends LazyLogging {

  def decode[T](bytes: ByteBuffer, decoder: Decoder[T], reader: GenericDatumReader[GenericRecord]): T = {
    var byteBuffer: ByteBufferInputStream = null
    try {
      byteBuffer = new ByteBufferInputStream(Collections.singletonList(bytes.duplicate))
      val dec = DecoderFactory.get().binaryDecoder(byteBuffer, null)
      val record = reader.read(null, dec)
      decoder.decode(record)
    }
    finally {
      if (byteBuffer != null) byteBuffer.close()
    }
  }

  def dec(bytes: Array[Byte]): EnvMonitor = {
    try {
      val decoder = Decoder[EnvMonitor]
      val reader = new GenericDatumReader[GenericRecord](decoder.schema)

      decode(ByteBuffer.wrap(bytes), decoder, reader)
    } catch {
      case e: Throwable => e match {
        case _ =>
          logger.error(e.getMessage)
          throw new RuntimeException(e)
      }
    }
  }


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
}
