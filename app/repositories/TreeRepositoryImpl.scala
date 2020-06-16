package repositories

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import scala.collection.immutable.Queue

import play.api.{Configuration, Logger}

import reactivemongo.api.bson._
import reactivemongo.api.bson.collection.BSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.Cursor

import helpers.DbHelper.getCollection

import models.Tree
import reactivemongo.api.commands.MultiBulkWriteResult

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
        go(k, ".$[i" + s"$k" + "].child" + res)
      }
    }
    go(length - 2, "")
  }

  private def subTreeArrayFilter(id: Seq[Int]): Seq[BSONDocument] = {
    assert(id.length >= 2)
    // List(BSONDocument("i0.id" -> ...), BSONDocument("i1.id -> ..."), ...)
    val k = id.length - 2
    def go(n: Int, res: List[BSONDocument], remain: Seq[Int], cur: Seq[Int]): List[BSONDocument] = {
      if (n == 0) res
      else {
        val key = "i" + (k - n).toString + ".id"
        go(n - 1, BSONDocument(key -> cur) :: res, remain.tail, cur :+ remain.head)
      }
    }
    go(k, List(), id.drop(2), id.take(2))
  }

  private def subTreeFinder(treeOpt: Option[Tree], id: Seq[Int]): Option[Tree] = {
    treeOpt.flatMap { tree =>
      if (tree.id.length == id.length) {
        Some(tree)
      }
      else {
        val sub = tree.child.find(c => c.id == id.take(c.id.length))
        subTreeFinder(sub, id)
      }
    }
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

  override def findOne(id: Seq[Int]): Future[Option[Tree]] = {
    logger.debug(s"find tree: $id")
    treesFuture flatMap { coll =>
      coll
        .find(BSONDocument("id" -> id.head), Option.empty[BSONDocument])
        .one[Tree]
        .map(tree => subTreeFinder(tree, id))
    }
  }

  override def insert(tree: Tree): Future[WriteResult] = {
    logger.debug(s"insert: $tree.id")
    if (tree.id.length == 1)
      treesFuture.flatMap(_.insert.one(tree))
    else {
      val selector = BSONDocument("id" -> List(tree.id.head))
      val modifier = BSONDocument(
        "$push" -> BSONDocument(subTreeModifierSelectorString(tree.id.length) -> tree)
      )
      val filter = subTreeArrayFilter(tree.id)
      treesFuture.flatMap(_.update.one(selector, modifier, false, false, None, filter))
    }
  }

  override def update(tree: Tree): Future[WriteResult] = {
    logger.debug(s"update: $tree.id")
    val selector = BSONDocument("id" -> List(tree.id.head))
    if (tree.id.length == 1)
      treesFuture.flatMap(_.update.one(selector, BSONDocument("$set" -> tree)))
    else {
      val k = tree.id.length - 2
      val modifier = BSONDocument(
        "$set" -> BSONDocument(
          subTreeModifierSelectorString(tree.id.length) + ".$[i" + s"$k]" -> tree
        )
      )
      val filter = BSONDocument(s"i$k.id" -> tree.id) +: subTreeArrayFilter(tree.id)
      treesFuture.flatMap(_.update.one(selector, modifier, false, false, None, filter))
    }
  }

  override def delete(id: Seq[Int]): Future[WriteResult] = {
    logger.debug(s"delete: $id")
    if (id.length == 1) {
      val selector = BSONDocument("id" -> id)
      treesFuture.flatMap(_.delete.one(selector))
    } else {
      val selector = BSONDocument("id" -> List(id.head))
      val modifier = BSONDocument(
        "$pull" -> BSONDocument(
          subTreeModifierSelectorString(id.length) -> BSONDocument("id" -> id)
        )
      )
      val filter = subTreeArrayFilter(id)
      treesFuture.flatMap(_.update.one(selector, modifier, false, false, None, filter))
    }
    // TODO 후처리
  }

  override def deleteAll: Future[MultiBulkWriteResult] = {
    logger.debug("delete all documents")
    treesFuture.flatMap(coll => {
      val deleteBuilder = coll.delete(ordered = false)

      val deletes = Future.sequence(
        Seq(deleteBuilder.element(q = BSONDocument(), limit = None, collation = None))
      )

      deletes.flatMap { ops =>
        deleteBuilder.many(ops)
      }
    })
  }

}
