package com.mrgueritte.atmostonce.service

import java.nio.ByteBuffer

import akka.stream.scaladsl.Flow
import com.mrgueritte.atmostonce.dao.TweetDAO
import com.mrgueritte.atmostonce.model.Tweet
import io.circe.jawn.JawnParser
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.concurrent.ExecutionContext

class CounterServiceImpl(dao: TweetDAO)(implicit val ec: ExecutionContext) extends CounterService {

  override def decodeTweetRecord(record: ConsumerRecord[Array[Byte], Array[Byte]]): Either[String, Tweet] = {
    val parser = new JawnParser
    parser.decodeByteBuffer[Tweet](ByteBuffer.wrap(record.value())) match {
      case Right(t) => Right(t)
      case Left(_)  => Left(s"Unable to parse record => ${record.value()}")
    }
  }

  override def sinkTweetCounters(tweet: Tweet): Unit = {
    val userId = tweet.user.id

    dao.setMentionCount(userId, tweet.mentions.list.size)
    dao.incrTweetCount(userId)
    dao.setHashTagsCount(userId, tweet.hashtags.tags.size)
    dao.setScreenName(userId, tweet.user.screenName)
    dao.setTweetLikeCount(userId, tweet.like)
  }

}
