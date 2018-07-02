package com.mrgueritte.atleastonce

import java.text.SimpleDateFormat
import java.util.Locale

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableOffsetBatch
import akka.kafka._
import akka.kafka.scaladsl.Consumer.DrainingControl
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink}
import cats.data.Kleisli
import cats.implicits._
import com.mrgueritte.atleastonce.model.Tweet
import io.circe.parser.decode
import io.circe.syntax._
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.concurrent.duration._
import scala.util.{Success, Try}

object Main extends App {

  implicit val system = ActorSystem("atmostonce")
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  val config = system.settings.config.getConfig("akka.kafka.consumer")

  val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
    .withBootstrapServers("localhost:9092")

  val consumerSettings =
    ConsumerSettings(config, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("2")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  val source = Consumer
    .committableSource(consumerSettings, Subscriptions.topics("tweets"))
    .map { msg =>
      business(msg.record.value) match {
          case Right(tw) =>
            ProducerMessage.Message(
              new ProducerRecord[String, String]("tweets-mario-fr", tw.id.toString, tw.asJson.noSpaces),
              msg.committableOffset
            )
          case _ => ProducerMessage.PassThroughMessage[String, String, ConsumerMessage.CommittableOffset](msg.committableOffset)
        }
    }
    .via(Producer.flexiFlow(producerSettings))
    .map(_.passThrough)
    .groupedWithin(10, 5.seconds)
    .map(CommittableOffsetBatch.apply)
    .mapAsync(3)(_.commitScaladsl())
    .toMat(Sink.ignore)(Keep.both)
    .mapMaterializedValue(DrainingControl.apply)
    .run()

  def business(msg: String) = {
    checkFrLanguage.compose(checkMario).compose(parsingCreationDate).compose(parseRecord).run(msg)
  }

  val checkFrLanguage: Kleisli[Either[String, ?], Tweet, Tweet] = Kleisli[Either[String, ?], Tweet, Tweet] { case tw =>
    tw.lang match {
      case "fr" => Right(tw)
      case _    => Left("Language doesn't match")
    }
  }

  val checkMario: Kleisli[Either[String, ?], Tweet, Tweet] = Kleisli[Either[String, ?], Tweet, Tweet] { case tw =>
    tw.text.matches("(?i:.*" + "mario" + ".*)") match {
      case true => Right(tw)
      case _    => Left("Word `mario` doesn't match with tweet")
    }
  }

  val parsingCreationDate: Kleisli[Either[String, ?], Tweet, Tweet] = Kleisli[Either[String, ?], Tweet, Tweet] { case tw =>
    val dateFormatFromRecord = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.ENGLISH)
    Try(dateFormatFromRecord.parse(tw.creationDate)) match {
      case Success(dateFormatted) =>
        val newFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH)
        Right(tw.copy(creationDate = newFormat.format(dateFormatted)))
      case _                      => Left("Error parsing date")
    }
  }

  val parseRecord: Kleisli[Either[String, ?], String, Tweet] = Kleisli[Either[String, ?], String, Tweet] { case msg =>
    decode[Tweet](msg) match {
      case Right(tw) => Right(tw)
      case Left(err) => Left(err.toString)
    }
  }

}
