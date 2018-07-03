package com.mrgueritte.atmostonce

import java.util.concurrent.CompletionStage

import akka.kafka.Subscriptions
import akka.kafka.scaladsl.Consumer
import akka.stream.ClosedShape
import akka.stream.javadsl.Sink
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, RunnableGraph, Source, Unzip}
import akka.{Done, NotUsed}
import com.mrgueritte.atmostonce.configurations.Config
import com.mrgueritte.atmostonce.model.Tweet
import org.apache.kafka.clients.consumer.ConsumerRecord

object Main extends App with Config {

  RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

    val source: Source[ConsumerRecord[Array[Byte], Array[Byte]], Consumer.Control] = Consumer.atMostOnceSource(consumerSettings, Subscriptions.topics("tweets"))
    val sinkDatabase: Sink[Tweet, CompletionStage[Done]] = Sink.foreach[Tweet](service.sinkTweetCounters)
    val sinkLog = Sink.foreach[String](err => log.error(err))

    val decodeTweetFlow = Flow.fromFunction(service.decodeTweetRecord).async
    val bcast = builder.add(Broadcast[Either[String, Tweet]](outputPorts = 2))
    val leftFlow = builder.add(Flow[Either[String, Tweet]].collect { case Left(err) => err })
    val rightFlow = builder.add(Flow[Either[String, Tweet]].collect { case Right(tw) => tw })

    source ~> decodeTweetFlow ~> bcast ~> rightFlow ~> sinkDatabase
                                 bcast ~> leftFlow ~> sinkLog

    ClosedShape
  }).run()
}
