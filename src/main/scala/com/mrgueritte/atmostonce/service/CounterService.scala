package com.mrgueritte.atmostonce.service

import akka.NotUsed
import akka.stream.scaladsl.Flow
import com.mrgueritte.atmostonce.model.Tweet
import org.apache.kafka.clients.consumer.ConsumerRecord

trait CounterService {

  def decodeTweetRecord(record: ConsumerRecord[Array[Byte], Array[Byte]]): Either[String, Tweet]

  def sinkTweetCounters(tweet: Tweet): Unit

}
