package repositories

import scala.concurrent.Future

import models.Tree

import reactivemongo.api.commands.WriteResult
import reactivemongo.api.commands.MultiBulkWriteResult

/**
  * A pure non-blocking interface for the TreeRepository.
  */
trait TreeRepository {
  def findAll: Future[List[Tree]]
  def insert(tree: Tree): Future[WriteResult]
  def update(tree: Tree): Future[WriteResult]
  def delete(id: Seq[Int]): Future[WriteResult]
  def deleteAll: Future[MultiBulkWriteResult]
}

