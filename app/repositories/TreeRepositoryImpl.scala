package repositories

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import play.api.{Configuration, Logger}

import reactivemongo.api.Cursor
import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection

import helpers.DbHelper.getCollection

import models.Tree

class TreeRepositoryImpl @Inject() ()(implicit ec: ExecutionContext, config: Configuration)
    extends TreeRepository {
  private val logger = Logger(this.getClass)

  private val treesFuture: Future[BSONCollection] = getCollection(treeCollectionName)

  override def findAll(): Future[List[Tree]] = {
    logger.trace(s"findAll: ")
    treesFuture flatMap { coll =>
      coll
        .find(BSONDocument(), Option.empty[BSONDocument])
        .cursor[Tree]()
        .collect[List](-1, Cursor.FailOnError[List[Tree]]())
    }
  }

  override def update(update: Tree): Future[Unit] = {
    logger.trace(s"update: $update.id")
    val selector = BSONDocument("id" -> update.id)

    val modifier = BSONDocument(
        "$set" -> update
      )

    treesFuture.map(_.update.one(selector, modifier, false, false))
  }
}
