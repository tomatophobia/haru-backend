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

  private val treeList: List[Tree] = List(
    Tree(
      List(0),
      0,
      "하루 프로젝트 시작하기",
      false,
      List(
        Tree(List(0, 0), 1, "프론트앤드 완성", false, List()),
        Tree(List(0, 1), 1, "백앤드 완성", false, List())
      )
    )
  )

  override def findAll(): Future[List[Tree]] = {
    logger.trace(s"findAll: ")
    treesFuture flatMap { coll =>
      coll
        .find(BSONDocument(), Option.empty[BSONDocument])
        .cursor[Tree]()
        .collect[List](-1, Cursor.FailOnError[List[Tree]]())
    }
  }
}
