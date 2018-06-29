package com.mrgueritte.atmostonce.model

import cats.syntax.apply._
import io.circe.Decoder

case class Tweet(id: Long, user: User, hashtags: Hashtags, mentions: Mentions, like: Long)

object Tweet {
  import User.userDecoder
  import Hashtags.hashtagsDecoder
  import Mentions.mentionsDecoder

  implicit val tweetDecoder: Decoder[Tweet] = {
    val like = Decoder.instance(_.get[Long]("favorite_count"))
    val tweetId = Decoder.instance(_.get[Long]("id"))

    (tweetId, userDecoder, hashtagsDecoder, mentionsDecoder, like).mapN(Tweet.apply)
  }
}