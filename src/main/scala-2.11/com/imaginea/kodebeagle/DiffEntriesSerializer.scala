package com.imaginea.kodebeagle

import java.io.{ObjectOutputStream, ByteArrayOutputStream}
import java.util.Properties

import kafka.javaapi.producer.Producer
import kafka.producer.ProducerConfig
import kafka.serializer.Encoder
import kafka.utils.VerifiableProperties
import org.eclipse.jgit.diff.DiffEntry

class DiffEntriesSerializer(verifiableProperties: VerifiableProperties)
  extends Encoder[List[DiffEntry]] {
  override def toBytes(t: List[DiffEntry]) = {
    val b = new ByteArrayOutputStream()
    val o = new ObjectOutputStream(b)
    o.writeObject(t)
    b.toByteArray
  }
}

trait KafkaUtils {
  def getKafkaProducer = {
    val props = new Properties()
    props.put("metadata.broker.list", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "com.imaginea.kodebeagle.DiffEntriesSerializer")
    val producerConfig = new ProducerConfig(props)
    new Producer[String, List[DiffEntry]](producerConfig)
  }
}
