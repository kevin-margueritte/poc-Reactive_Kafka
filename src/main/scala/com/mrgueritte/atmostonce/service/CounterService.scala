package com.mrgueritte.atmostonce.service

import com.mrgueritte.atmostonce.model.Tweet
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.concurrent.Future

trait CounterService {

  def decodeTweetRecord(record: ConsumerRecord[String, String]): Future[Either[String, Tweet]]

  def sinkTweetCounters(tweet: Tweet): Unit

}
