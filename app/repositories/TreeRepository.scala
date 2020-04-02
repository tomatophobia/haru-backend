package repositories

import scala.concurrent.Future

import models.Tree

import reactivemongo.api.commands.WriteResult

/**
  * A pure non-blocking interface for the TreeRepository.
  */
trait TreeRepository {
  def findAll: Future[List[Tree]]
  def insert(tree: Tree): Future[WriteResult]
  def update(tree: Tree): Future[Unit]
  def delete(id: Seq[Int]): Future[Unit]
  def deleteAll: Future[Unit]
}

