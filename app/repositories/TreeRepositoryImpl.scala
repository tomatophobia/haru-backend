package repositories

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.immutable.Queue

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

  private def subTreeModifierSelectorString(length: Int): String = {
    assert(length >= 2)
    // child.$[i0].child.$[i1].child.$[i2].child. ... $[in].child
    def go(n: Int, res: String): String = {
      if (n == 0) "child" + res
      else {
        val k = n - 1
        go(k, ".$[i" + s"$k" +"].child" + res)
      }
    }
    go(length-2, "")
  }

  private def subTreeArrayFilter(id: Seq[Int]): Seq[BSONDocument] = {
    assert(id.length >= 2)
    // List(BSONDocument("i0.id" -> ...), BSONDocument("i1.id -> ..."), ...)
    val k = id.length - 2
    def go(n: Int, res: List[BSONDocument], remain: Seq[Int], cur: Seq[Int]): List[BSONDocument] = {
      if (n == 0) res
      else {
        val key = "i" + (k-n).toString + ".id"
        go(n-1, BSONDocument(key -> cur) :: res, remain.tail, cur :+ remain.head)
      }
    }
    go(k, List(), id.drop(2), id.take(2))
  }

  override def findAll: Future[List[Tree]] = {
    logger.debug("findAll")
    treesFuture flatMap { coll =>
      coll
        .find(BSONDocument(), Option.empty[BSONDocument])
        .cursor[Tree]()
        .collect[List](-1, Cursor.FailOnError[List[Tree]]())
    }
  }

  override def insert(tree: Tree): Future[Unit] = {
    logger.debug(s"insert: $tree.id")
    if (tree.id.length == 1)
      treesFuture.map(_.insert.one(tree))
    else {
      val selector = BSONDocument("id" -> List(tree.id.head))
      val modifier = BSONDocument("$push" -> BSONDocument(subTreeModifierSelectorString(tree.id.length) -> tree))
      val filter = subTreeArrayFilter(tree.id)
      treesFuture.map(_.update.one(selector, modifier, false, false, None, filter))
    }
  }

  override def update(tree: Tree): Future[Unit] = {
    logger.debug(s"update: $tree.id")
    val selector = BSONDocument("id" -> List(tree.id.head))
    if (tree.id.length == 1)
      treesFuture.map(_.update.one(selector, BSONDocument("$set" -> tree)))
    else {
      val k = tree.id.length - 2
      val modifier = BSONDocument("$set" -> BSONDocument(subTreeModifierSelectorString(tree.id.length) + ".$[i" + s"$k]" -> tree))
      val filter = BSONDocument(s"i$k.id" -> tree.id) +: subTreeArrayFilter(tree.id)
      treesFuture.map(_.update.one(selector, modifier, false, false, None, filter))
    }
  }

  override def delete(id: Seq[Int]): Future[Unit] = {
    logger.debug(s"delete: $id")
    if (id.length == 1) {
      val selector = BSONDocument("id" -> id)
      treesFuture.map(_.delete.one(selector))
    }
    else {
      val selector = BSONDocument("id" -> List(id.head))
      val modifier = BSONDocument("$pull" -> BSONDocument(subTreeModifierSelectorString(id.length) -> BSONDocument("id" -> id)))
      val filter = subTreeArrayFilter(id)
      treesFuture.map(_.update.one(selector, modifier, false, false, None, filter))
    }
      

    // TODO 후처리
  }

}
