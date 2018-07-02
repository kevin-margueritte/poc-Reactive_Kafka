package com.mrgueritte.atleastonce.model

import io.circe.Decoder.Result
import io.circe.derivation.deriveEncoder
import io.circe.{Decoder, HCursor}

case class Media(id: Long, url: String)
case class Medias(list: Seq[Media])

object Medias {
  implicit val mediaEncoder = deriveEncoder[Media]
  implicit val mediaDecoder: Decoder[Media] = new Decoder[Media] {
    override def apply(c: HCursor): Result[Media] = {
      for {
        id  <- c.get[Long]("id")
        url <- c.get[String]("media_url_https")
      } yield Media(id, url)
    }
  }

  implicit val mediasEncoder = deriveEncoder[Medias]
  implicit val mediasDecoder: Decoder[Medias] = new Decoder[Medias] {
    override def apply(c: HCursor): Result[Medias] = {
      val hashtagsCursor = c.downField("entities")
      for {
        media <- hashtagsCursor.getOrElse[List[Media]]("media")(Nil)
      } yield {
        Medias(media)
      }
    }
  }
}
