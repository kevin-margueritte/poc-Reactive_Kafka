package com.mrgueritte.atleastonce.model

import io.circe.derivation.deriveEncoder
import io.circe.{Decoder, HCursor}

case class User(id: Long, screenName: String)

object User {
  implicit val userEncoder = deriveEncoder[User]
  implicit val userDecoder: Decoder[User] = new Decoder[User] {
    final def apply(c: HCursor): Decoder.Result[User] = {
      val userCursor = c.downField("user")
      for {
        id <- userCursor.downField("id").as[Long]
        screenName <- userCursor.downField("screen_name").as[String]
      } yield {
        User(id, screenName)
      }
    }
  }
}
