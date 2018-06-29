package com.mrgueritte.atmostonce.service

import com.mrgueritte.atmostonce.dao.TweetDAO
import com.mrgueritte.atmostonce.model.Tweet
import io.circe.parser.decode
import org.apache.kafka.clients.consumer.ConsumerRecord

import scala.concurrent.{ExecutionContext, Future}

class CounterServiceImpl(dao: TweetDAO)(implicit val ec: ExecutionContext) extends CounterService {

  override def decodeTweetRecord(record: ConsumerRecord[String, String]): Future[Either[String, Tweet]] = Future {
    decode[Tweet](record.value()) match {
      case Right(t) => Right(t)
      case Left(error) => Left(s"Unable to parse record => ${record.value()}")
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
