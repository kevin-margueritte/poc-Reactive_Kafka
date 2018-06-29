package com.mrgueritte.atmostonce.model

import io.circe.{Decoder, HCursor}

case class HashTag(tag: String) extends AnyVal
case class Hashtags(tags: Seq[HashTag]) extends AnyVal

object Hashtags {
  implicit val hashtagDecoder: Decoder[HashTag] = Decoder.instance(_.get[String]("text")).map(HashTag.apply)

  implicit val hashtagsDecoder: Decoder[Hashtags] = new Decoder[Hashtags] {
    final def apply(c: HCursor): Decoder.Result[Hashtags] = {
      val hashtagsCursor = c.downField("entities").downField("hashtags")
      for {
        hashtags <- hashtagsCursor.as[List[HashTag]]
      } yield {
        Hashtags(hashtags)
      }
    }
  }
}