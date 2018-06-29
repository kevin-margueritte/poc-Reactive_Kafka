package com.mrgueritte.atmostonce

import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.javadsl.Sink
import com.mrgueritte.atmostonce.configurations.Config
import com.mrgueritte.atmostonce.model.Tweet

object Main extends App with Config {

  Consumer
      .atMostOnceSource(consumerSettings, Subscriptions.topics("tweets"))
      .mapAsync(5)(service.decodeTweetRecord)
      .to(
        Sink.foreach {
          case Right(tw: Tweet) => service.sinkTweetCounters(tw)
          case Left(err: String) => log.error(err)
        }
      )
      .run()
}
