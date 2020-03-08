package models

import play.api.libs.json.{Json, OFormat}

import reactivemongo.api.bson._

case class Tree(id: Seq[Int], level: Int, text: String, checked: Boolean, child: Seq[Tree])

object Tree {
  implicit val jsonFormat: OFormat[Tree] = Json.format[Tree]
  implicit val bsonHandler: BSONDocumentHandler[Tree] = Macros.handler[Tree]
}
