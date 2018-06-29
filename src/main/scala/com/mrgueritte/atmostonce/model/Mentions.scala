package com.mrgueritte.atmostonce.model

import io.circe.{Decoder, HCursor}

case class Mention(id: Long) extends AnyVal
case class Mentions(list: Seq[Mention]) extends AnyVal

object Mentions {
  implicit val metionDecoder: Decoder[Mention] = new Decoder[Mention] {
    final def apply(c: HCursor): Decoder.Result[Mention] = {
      for {
        id <- c.downField("id").as[Long]
      } yield {
        Mention(id)
      }
    }
  }

  implicit val mentionsDecoder: Decoder[Mentions] = new Decoder[Mentions] {
    final def apply(c: HCursor): Decoder.Result[Mentions] = {
      val mentionsCursor = c.downField("entities").downField("user_mentions")
      for {
        mentions <- mentionsCursor.as[List[Mention]]
      } yield {
        Mentions(mentions)
      }
    }
  }
}