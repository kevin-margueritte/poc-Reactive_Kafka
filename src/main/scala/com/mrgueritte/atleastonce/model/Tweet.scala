package com.mrgueritte.atleastonce.model

import io.circe.derivation.deriveEncoder
import io.circe.{Decoder, HCursor}

case class Tweet(id: Long, creationDate: String, text: String, lang: String, user: User, hashtags: Hashtags, medias: Medias)

object Tweet {
  import Hashtags._
  import Medias._
  import User._

  implicit val tweetEncoder = deriveEncoder[Tweet]
  implicit val tweetDecoder: Decoder[Tweet] = new Decoder[Tweet] {
    final def apply(c: HCursor): Decoder.Result[Tweet] = {

      val tweetIdDecoder      = Decoder.instance(_.get[Long]("id"))
      val creationDateDecoder = Decoder.instance(_.get[String]("created_at"))
      val textDecoder         = Decoder.instance(_.get[String]("text"))
      val langDecoder         = Decoder.instance(_.get[String]("lang"))

      for {
        user          <- c.as[User]
        hashtags      <- c.as[Hashtags]
        media         <- c.as[Medias]
        tweetId       <- c.as(tweetIdDecoder)
        creationDate  <- c.as(creationDateDecoder)
        text          <- c.as(textDecoder)
        lang          <- c.as(langDecoder)
      } yield Tweet(tweetId, creationDate, text, lang, user, hashtags, media)
    }
  }

}