package com.mrgueritte.atmostonce.configurations

import akka.actor.ActorSystem
import akka.kafka.ConsumerSettings
import akka.stream.ActorMaterializer
import com.mrgueritte.atmostonce.dao.{TweetDAO, TweetDAOImpl}
import com.mrgueritte.atmostonce.service.{CounterService, CounterServiceImpl}
import com.redis.RedisClient
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer

trait ActorConfig {
  implicit val system = ActorSystem("atmostonce")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
}

trait KafkaConfig {
  self : ActorConfig =>

  val config = system.settings.config.getConfig("akka.kafka.consumer")

  val consumerSettings =
    ConsumerSettings(config, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("2")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
}

trait RedisConfig {
  val r = new RedisClient("localhost", 6379)
}

trait Config extends ActorConfig with KafkaConfig with RedisConfig {

  import com.softwaremill.macwire._
  val log = system.log

  val dao: TweetDAO = wire[TweetDAOImpl]
  val service: CounterService = wire[CounterServiceImpl]

}
